<template>
  <div class="about-page">
    <div class="page-bg" aria-hidden="true">
      <div class="bg-grid"></div>
    </div>

    <div class="about-container">
      <!-- 个人档案面板 -->
      <section class="profile-panel">
        <!-- 顶部信息 -->
        <div class="profile-header">
          <button v-if="userStore.isAdmin" class="edit-profile-btn" @click="openEditor">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
            编辑
          </button>
          <div class="avatar-section">
            <div class="avatar-wrapper">
              <div class="avatar-glow-ring" aria-hidden="true"></div>
              <div class="avatar-core">
                <img
                  v-if="siteConfig.avatar"
                  :src="profile.avatar"
                  :alt="profile.name"
                  @error="onAvatarError"
                />
                <div v-else class="avatar-placeholder">
                  {{ profile.name?.charAt(0) || '?' }}
                </div>
              </div>
            </div>
            <div class="profile-name">
              <h1 class="name-text">{{ profile.name }}</h1>
              <span class="name-tag">{{ profile.tagline }}</span>
            </div>
          </div>

          <div class="profile-meta">
            <div class="meta-item">
              <span class="meta-icon"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/></svg></span>
              <span class="meta-text">{{ profile.location }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-icon"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg></span>
              <span class="meta-text">{{ currentTimezone }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-icon"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="4"/><line x1="12" y1="2" x2="12" y2="6"/><line x1="12" y1="18" x2="12" y2="22"/></svg></span>
              <span class="meta-text">{{ profile.status }}</span>
            </div>
          </div>
        </div>

        <!-- 描述区 -->
        <div class="profile-body" v-if="profile.bio">
          <p class="profile-desc">{{ profile.bio }}</p>
        </div>

        <!-- 技能展示 -->
        <div class="skills-section" v-if="skills.length">
          <div class="section-header">
            <span class="section-label">SKILLS</span>
            <span class="section-line"></span>
          </div>
          <div class="skills-grid">
            <div
              v-for="skill in skills"
              :key="skill.name"
              class="skill-node"
            >
              <div class="skill-header">
                <span class="skill-name">{{ skill.name }}</span>
                <span class="skill-level">{{ skill.level || '--' }}%</span>
              </div>
              <div class="skill-bar">
                <div
                  class="skill-fill"
                  :style="{ width: (skill.level || 50) + '%' }"
                ></div>
              </div>
            </div>
          </div>
        </div>

        <!-- 兴趣标签 -->
        <div class="interests-section" v-if="interests.length">
          <div class="section-header">
            <span class="section-label">INTERESTS</span>
            <span class="section-line"></span>
          </div>
          <div class="interest-tags">
            <span
              v-for="interest in interests"
              :key="interest"
              class="interest-tag"
            >{{ interest }}</span>
          </div>
        </div>

        <!-- 联系方式 -->
        <div class="contact-section">
          <div class="section-header">
            <span class="section-label">CONNECT</span>
            <span class="section-line"></span>
          </div>
          <div class="contact-grid">
            <template v-for="social in socials" :key="social.name">
              <!-- 普通链接 -->
              <a
                v-if="!social.isQRCode && !social.url?.startsWith('#')"
                :href="social.url"
                target="_blank"
                rel="noopener noreferrer"
                class="contact-node"
                :style="{ '--node-color': social.color || '#38f8ff' }"
              >
                <span class="contact-icon" v-html="getSocialIcon(social.icon)"></span>
                <span class="contact-copy">
                  <span class="contact-name">{{ social.name }}</span>
                  <span v-if="social.handle" class="contact-detail">{{ social.handle }}</span>
                </span>
                <span class="contact-arrow">
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M7 17l9.2-9.2M17 17V7H7"/>
                  </svg>
                </span>
              </a>
              <!-- 占位链接（未配置真实URL） -->
              <div
                v-else-if="!social.isQRCode && social.url?.startsWith('#')"
                class="contact-node placeholder-link"
                :style="{ '--node-color': social.color || '#38f8ff' }"
                title="链接待配置"
              >
                <span class="contact-icon" v-html="getSocialIcon(social.icon)"></span>
                <span class="contact-copy">
                  <span class="contact-name">{{ social.name }}</span>
                  <span v-if="social.handle" class="contact-detail">{{ social.handle }}</span>
                </span>
                <span class="contact-arrow">
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M7 17l9.2-9.2M17 17V7H7"/>
                  </svg>
                </span>
              </div>
              <!-- 二维码弹窗 -->
              <div
                v-else
                class="contact-node qr-trigger"
                :style="{ '--node-color': social.color || '#38f8ff' }"
                @click="showQRCode(social)"
              >
                <span class="contact-icon" v-html="getSocialIcon(social.icon)"></span>
                <span class="contact-copy">
                  <span class="contact-name">{{ social.name }}</span>
                  <span v-if="social.handle" class="contact-detail">{{ social.handle }}</span>
                </span>
                <span class="contact-arrow">
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M7 17l9.2-9.2M17 17V7H7"/>
                  </svg>
                </span>
              </div>
            </template>
          </div>
        </div>

        <!-- 底部装饰 -->
        <div class="profile-footer">
          <div class="footer-text">
            <span class="footer-bracket">[</span>
            <span class="footer-content">{{ profile.footer }}</span>
            <span class="footer-bracket">]</span>
          </div>
        </div>

        <!-- 二维码弹窗 -->
        <div v-if="showQR" class="qr-modal" @click="closeQRCode">
          <div class="qr-content" @click.stop>
            <button class="qr-close" @click="closeQRCode">×</button>
            <h4 class="qr-title">{{ currentQR?.name }}</h4>
            <img v-if="currentQR?.qrCodeUrl" :src="resolveAssetUrl(currentQR.qrCodeUrl)" :alt="currentQR?.name" class="qr-image" />
            <p class="qr-tip">扫描二维码关注我</p>
          </div>
        </div>

        <!-- 角标装饰 -->
        <div class="corner top-left" aria-hidden="true"></div>
        <div class="corner top-right" aria-hidden="true"></div>
        <div class="corner bottom-left" aria-hidden="true"></div>
        <div class="corner bottom-right" aria-hidden="true"></div>
      </section>

      <!-- 编辑资料弹窗 -->
      <div v-if="showEditor" class="editor-overlay" @click.self="showEditor = false">
        <div class="editor-panel">
          <h3>编辑个人资料</h3>
          <div class="editor-row"><label>姓名</label><input v-model="editForm.name" /></div>
          <div class="editor-row"><label>标签</label><input v-model="editForm.tagline" placeholder="如：AI & Web Developer" /></div>
          <div class="editor-row"><label>简介</label><textarea v-model="editForm.bio" rows="3" placeholder="一段话介绍自己" /></div>
          <div class="editor-row"><label>位置</label><input v-model="editForm.location" placeholder="如：Beijing, China" /></div>
          <div class="editor-row"><label>状态</label><input v-model="editForm.status" placeholder="如：Available / Busy" /></div>
          <div class="editor-actions">
            <button @click="showEditor = false" class="editor-cancel">取消</button>
            <button @click="submitProfile" :disabled="saving" class="editor-save">{{ saving ? '保存中...' : '保存' }}</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import siteConfig from '../config/site.config'
import { resolveAssetUrl } from '../config/site.config'
import { useUserStore } from '../stores/user'
import { getProfile, updateProfile } from '../api/profile'

const userStore = useUserStore()
const showQR = ref(false)
const currentQR = ref(null)
const profile = ref({})
const showEditor = ref(false)
const saving = ref(false)
const editForm = ref({})

// 解析技能（支持字符串和对象数组）
const parseSkills = (raw) => {
  if (!raw) return []
  const arr = typeof raw === 'string' ? JSON.parse(raw) : raw
  return arr.map(s => typeof s === 'string' ? { name: s, level: 70 + Math.floor(Math.random() * 25) } : s)
}

// 加载资料
const loadProfile = async () => {
  try {
    const res = await getProfile()
    if (res.code === 200 && res.data) {
      const p = res.data
      profile.value = {
        name: p.name || siteConfig.name,
        tagline: p.tagline || siteConfig.about?.tagline,
        bio: p.bio || siteConfig.about?.bio,
        location: p.location || siteConfig.about?.location,
        status: p.status || siteConfig.about?.status,
        avatar: p.avatar || siteConfig.avatar,
        footer: siteConfig.about?.footer || '保持热爱，奔赴山海'
      }
      profile.value._skills = parseSkills(p.skills || siteConfig.about?.skills)
      profile.value._socials = parseSocials(p.socials)
      return
    }
  } catch (e) { /* fallback to siteConfig */ }
  // 默认
  profile.value = {
    name: siteConfig.name,
    tagline: siteConfig.about?.tagline || 'Developer & Creator',
    bio: siteConfig.about?.bio || '',
    location: siteConfig.about?.location || 'Earth',
    status: siteConfig.about?.status || 'Available',
    avatar: siteConfig.avatar || '',
    footer: siteConfig.about?.footer || '保持热爱，奔赴山海',
    _skills: parseSkills(siteConfig.about?.skills),
    _socials: siteConfig.socials || []
  }
}

const parseSocials = (raw) => {
  if (!raw) return siteConfig.socials || []
  const arr = typeof raw === 'string' ? JSON.parse(raw) : raw
  return arr
}

const openEditor = () => {
  editForm.value = {
    name: profile.value.name || '',
    tagline: profile.value.tagline || '',
    bio: profile.value.bio || '',
    location: profile.value.location || '',
    status: profile.value.status || ''
  }
  showEditor.value = true
}

const submitProfile = async () => {
  saving.value = true
  try {
    const res = await updateProfile(editForm.value)
    if (res.code === 200) {
      showEditor.value = false
      await loadProfile()
    } else {
      alert(res.message || '保存失败')
    }
  } catch (e) {
    alert('保存失败')
  }
  saving.value = false
}

onMounted(loadProfile)

const skills = computed(() => profile.value._skills || [])
const interests = computed(() => siteConfig.about?.interests || [])
const socials = computed(() => profile.value._socials || [])

const showQRCode = (social) => { currentQR.value = social; showQR.value = true }
const closeQRCode = () => { showQR.value = false; currentQR.value = null }

const currentTimezone = computed(() => {
  const offset = -new Date().getTimezoneOffset() / 60
  const sign = offset >= 0 ? '+' : ''
  return `UTC${sign}${offset}`
})

const onAvatarError = (e) => {
  e.target.style.display = 'none'
}

const getSocialIcon = (icon) => {
  const icons = {
    github: '<svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor"><path d="M12 2A10 10 0 0 0 2 12c0 4.42 2.87 8.17 6.84 9.5.5.08.66-.23.66-.5v-1.69c-2.77.6-3.36-1.34-3.36-1.34-.46-1.16-1.11-1.47-1.11-1.47-.91-.62.07-.6.07-.6 1 .07 1.53 1.03 1.53 1.03.87 1.52 2.34 1.07 2.91.83.09-.65.35-1.09.63-1.34-2.22-.25-4.55-1.11-4.55-4.92 0-1.11.38-2 1.03-2.71-.1-.25-.45-1.29.1-2.64 0 0 .84-.27 2.75 1.02.79-.22 1.65-.33 2.5-.33s1.71.11 2.5.33c1.91-1.29 2.75-1.02 2.75-1.02.55 1.35.2 2.39.1 2.64.65.71 1.03 1.6 1.03 2.71 0 3.82-2.34 4.66-4.57 4.91.36.31.69.92.69 1.85V21c0 .27.16.59.67.5C19.14 20.16 22 16.42 22 12A10 10 0 0 0 12 2z"/></svg>',
    bilibili: '<svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor"><path d="M17.813 4.653h.854c1.51.054 2.769.578 3.773 1.574 1.004.995 1.524 2.249 1.56 3.76v7.36c-.036 1.51-.556 2.769-1.56 3.773s-2.262 1.524-3.773 1.56H5.333c-1.51-.036-2.769-.556-3.773-1.56S.036 18.858 0 17.347v-7.36c.036-1.511.556-2.765 1.56-3.76 1.004-.996 2.262-1.52 3.773-1.574h.774l-1.174-1.12a1.234 1.234 0 0 1-.373-.906c0-.356.124-.659.373-.907l.027-.027c.267-.249.573-.373.92-.373.347 0 .653.124.92.373L9.653 4.44c.071.071.134.142.187.213h4.267a.836.836 0 0 1 .16-.213l2.853-2.747c.267-.249.573-.373.92-.373.347 0 .662.151.929.4.267.249.391.551.391.907 0 .355-.124.657-.373.906zM5.333 7.24c-.746.018-1.373.276-1.88.773-.506.498-.769 1.13-.786 1.894v7.52c.017.764.28 1.395.786 1.893.507.498 1.134.756 1.88.773h13.334c.746-.017 1.373-.275 1.88-.773.506-.498.769-1.129.786-1.893v-7.52c-.017-.765-.28-1.396-.786-1.894-.507-.497-1.134-.755-1.88-.773zM8 11.107c.373 0 .684.124.933.373.25.249.383.569.4.96v1.173c-.017.391-.15.711-.4.96-.249.25-.56.374-.933.374s-.684-.125-.933-.374c-.25-.249-.383-.569-.4-.96V12.44c0-.373.129-.689.386-.947.258-.257.574-.386.947-.386zm8 0c.373 0 .684.124.933.373.25.249.383.569.4.96v1.173c-.017.391-.15.711-.4.96-.249.25-.56.374-.933.374s-.684-.125-.933-.374c-.25-.249-.383-.569-.4-.96V12.44c.017-.391.15-.711.4-.96.249-.249.56-.373.933-.373z"/></svg>',
    zhihu: '<svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor"><path d="M5.721 0C2.251 0 0 2.25 0 5.719V18.28C0 21.751 2.252 24 5.721 24h12.56C21.751 24 24 21.75 24 18.281V5.72C24 2.249 21.75 0 18.281 0zm1.964 4.078c-.271.73-.5 1.434-.68 2.11h4.587c.545-.006.445 1.168.445 1.171H9.384a58.104 58.104 0 0 1-.112 3.797h2.726c.388.023.393 1.251.393 1.251H9.183c.062 1.191.328 2.559.837 4.106.388.116.774-.078.774-.078 0-.078-.291-1.07-.291-1.07a5.597 5.597 0 0 1 2.831.078c.097.485.155.95.184 1.398a3.29 3.29 0 0 1-.753.252c-.078.272-.35.932-.35.932l-.078.155c-.252.66-.524 1.282-.815 1.863a6.43 6.43 0 0 1-1.126 1.67 5.243 5.243 0 0 1-1.553 1.126c-.582.291-1.204.436-1.864.436-.66 0-1.282-.145-1.863-.436a5.244 5.244 0 0 1-1.554-1.126 6.43 6.43 0 0 1-1.126-1.67 7.768 7.768 0 0 1-.815-1.863l-.078-.155s-.272-.66-.35-.932a3.29 3.29 0 0 1-.753-.252c.029-.448.087-.913.184-1.398.66-.155 1.864-.078 2.831-.078 0 0-.252.992-.291 1.07 0 0 .388.194.774.078.51-1.547.776-2.915.837-4.106H4.077s.006-1.228.393-1.251h2.726a58.104 58.104 0 0 1-.112-3.797H4.077s-.1-1.177.445-1.171h4.587c-.18-.676-.409-1.38-.68-2.11z"/></svg>',
    weibo: '<svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor"><path d="M10.098 20.323c-3.977.391-7.414-1.406-7.672-4.02-.259-2.609 2.759-5.047 6.74-5.441 3.979-.394 7.413 1.404 7.671 4.018.259 2.6-2.759 5.049-6.737 5.439l-.002.004zM9.05 17.219c-.384.616-1.208.884-1.829.602-.612-.279-.793-.991-.406-1.593.379-.595 1.176-.861 1.793-.601.622.263.82.972.442 1.592zm1.27-1.627c-.141.237-.449.353-.689.253-.236-.09-.313-.361-.177-.586.138-.227.436-.346.672-.24.239.09.315.36.18.573h.014zm.176-2.719c-1.893-.493-4.033.45-4.857 2.118-.836 1.704-.026 3.591 1.886 4.21 1.983.64 4.318-.341 5.132-2.179.8-1.793-.201-3.642-2.161-4.149zm7.563-1.224c-.346-.105-.579-.18-.402-.649.386-1.031.425-1.922.008-2.557-.781-1.192-2.924-1.128-5.354-.034 0 0-.767.334-.571-.271.378-1.207.32-2.217-.266-2.8-1.331-1.32-4.869.047-7.91 3.052C1.834 10.411.124 12.695.124 14.555c0 3.557 4.567 5.72 9.031 5.72 5.847 0 9.737-3.397 9.737-6.093 0-1.631-1.377-2.553-2.813-2.913l.002-.002zm1.627-3.885c-.752-.856-1.864-1.291-3.031-1.291-.184 0-.369.015-.553.044l-.134.022-.038.134c-.059.214.067.437.281.496l.133.038c.151.038.306.058.462.058.793 0 1.531.295 2.078.833.548.537.84 1.257.822 2.028l-.007.135.134.038c.213.059.437-.067.496-.281l.038-.134c.029-.185.044-.369.044-.554 0-1.166-.435-2.278-1.291-3.031l-.434-.415z"/></svg>',
    douyin: '<svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor"><path d="M12.525.02c1.31-.02 2.61-.01 3.91-.02.08 1.53.63 3.09 1.75 4.17 1.12 1.11 2.7 1.62 4.24 1.79v4.03c-1.44-.05-2.89-.35-4.2-.97-.57-.26-1.1-.59-1.62-.93-.01 2.92.01 5.84-.02 8.75-.08 1.4-.54 2.79-1.35 3.94-1.31 1.92-3.58 3.17-5.91 3.21-1.43.08-2.86-.31-4.08-1.03-2.02-1.19-3.44-3.37-3.65-5.71-.02-.5-.03-1-.01-1.49.18-1.9 1.12-3.72 2.58-4.96 1.66-1.44 3.98-2.13 6.15-1.72.02 1.48-.04 2.96-.04 4.44-.99-.32-2.15-.23-3.02.37-.63.41-1.11 1.04-1.36 1.75-.21.51-.15 1.07-.14 1.61.24 1.64 1.82 3.02 3.5 2.87 1.12-.01 2.19-.66 2.77-1.61.19-.33.4-.67.41-1.06.1-1.79.06-3.57.07-5.36.01-4.03-.01-8.05.02-12.07z"/></svg>',
    xiaohongshu: '<svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm4.64 6.8c-.15 1.58-.8 5.42-1.13 7.19-.14.75-.42 1-.68 1.03-.58.05-1.02-.38-1.58-.75-.88-.58-1.38-.94-2.23-1.5-.99-.65-.35-1.01.22-1.59.15-.15 2.71-2.48 2.76-2.69a.2.2 0 0 0-.05-.18c-.06-.05-.14-.03-.21-.02-.09.02-1.49.95-4.22 2.79-.4.27-.76.41-1.08.4-.36-.01-1.04-.2-1.55-.37-.63-.2-1.12-.31-1.08-.66.02-.18.27-.36.74-.55 2.92-1.27 4.86-2.11 5.83-2.51 2.78-1.16 3.35-1.36 3.73-1.36.08 0 .27.02.39.12.1.08.13.19.14.27-.01.06.01.24 0 .38z"/></svg>',
    telegram: '<svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor"><path d="M11.944 0A12 12 0 0 0 0 12a12 12 0 0 0 12 12 12 12 0 0 0 12-12A12 12 0 0 0 12 0a12 12 0 0 0-.056 0zm4.962 7.224c.1-.002.321.023.465.14a.506.506 0 0 1 .171.325c.016.093.036.306.02.472-.18 1.898-.962 6.502-1.36 8.627-.168.9-.499 1.201-.82 1.23-.696.065-1.225-.46-1.9-.902-1.056-.693-1.653-1.124-2.678-1.8-1.185-.78-.417-1.21.258-1.91.177-.184 3.247-2.977 3.307-3.23.007-.032.014-.15-.056-.212s-.174-.041-.249-.024c-.106.024-1.793 1.14-5.061 3.345-.48.33-.913.49-1.302.48-.428-.008-1.252-.241-1.865-.44-.752-.245-1.349-.374-1.297-.789.027-.216.325-.437.893-.663 3.498-1.524 5.83-2.529 6.998-3.014 3.332-1.386 4.025-1.627 4.476-1.635z"/></svg>',
    email: '<svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor"><path d="M20 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 4l-8 5-8-5V6l8 5 8-5v2z"/></svg>',
    twitter: '<svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor"><path d="M18.244 2.25h3.308l-7.227 8.26 8.502 11.24H16.17l-5.214-6.817L4.99 21.75H1.68l7.73-8.835L1.254 2.25H8.08l4.713 6.231zm-1.161 17.52h1.833L7.084 4.126H5.117z"/></svg>',
    juejin: '<span style="font-size:14px;font-weight:800">掘</span>',
    leetcode: '<span style="font-size:12px;font-weight:800">LC</span>',
    wechat: '<span style="font-size:14px;font-weight:800">微</span>',
    qq: '<span style="font-size:12px;font-weight:800">QQ</span>',
    gitlab: '<span style="font-size:14px;font-weight:700">GL</span>',
    gitee: '<span style="font-size:14px;font-weight:700">GE</span>',
    csdn: '<span style="font-size:14px;font-weight:700">CN</span>',
    yuque: '<svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1.5 15.5h-2v-7h2v7zm5 0h-2v-4h-2v-3h4v7z"/></svg>',
  }
  return icons[icon] || '<span style="font-size:14px">--</span>'
}
</script>

<style scoped>
.about-page {
  position: relative;
  min-height: 100vh;
  padding: 80px 24px 100px;
}

.page-bg {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 0;
}

.bg-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(56, 189, 248, 0.035) 1px, transparent 1px),
    linear-gradient(90deg, rgba(56, 189, 248, 0.035) 1px, transparent 1px);
  background-size: 58px 58px;
  mask-image: linear-gradient(to bottom, #000, transparent 82%);
}

.about-container {
  position: relative;
  z-index: 1;
  max-width: 800px;
  margin: 0 auto;
}

/* 档案面板 */
.profile-panel {
  position: relative;
  background: linear-gradient(135deg, rgba(12, 20, 35, 0.95), rgba(8, 12, 22, 0.98));
  border: 1px solid rgba(56, 248, 255, 0.2);
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.4);
}

/* 角标 */
.corner {
  position: absolute;
  width: 30px;
  height: 30px;
  pointer-events: none;
}

.corner::before, .corner::after {
  content: '';
  position: absolute;
  background: rgba(56, 248, 255, 0.4);
}

.corner.top-left { top: 12px; left: 12px; }
.corner.top-left::before { width: 15px; height: 2px; top: 0; left: 0; }
.corner.top-left::after { width: 2px; height: 15px; top: 0; left: 0; }

.corner.top-right { top: 12px; right: 12px; }
.corner.top-right::before { width: 15px; height: 2px; top: 0; right: 0; }
.corner.top-right::after { width: 2px; height: 15px; top: 0; right: 0; }

.corner.bottom-left { bottom: 12px; left: 12px; }
.corner.bottom-left::before { width: 15px; height: 2px; bottom: 0; left: 0; }
.corner.bottom-left::after { width: 2px; height: 15px; bottom: 0; left: 0; }

.corner.bottom-right { bottom: 12px; right: 12px; }
.corner.bottom-right::before { width: 15px; height: 2px; bottom: 0; right: 0; }
.corner.bottom-right::after { width: 2px; height: 15px; bottom: 0; right: 0; }

/* 顶部信息 */
.profile-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 40px 32px 32px;
  border-bottom: 1px solid rgba(56, 248, 255, 0.1);
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 24px;
}

.avatar-wrapper {
  position: relative;
  width: 80px;
  height: 80px;
  flex-shrink: 0;
}

.avatar-wrapper img,
.avatar-placeholder {
  width: 100%;
  height: 100%;
  border-radius: 16px;
  object-fit: cover;
  border: 2px solid rgba(56, 248, 255, 0.3);
}

.avatar-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  font-weight: 700;
  color: #38f8ff;
  background: rgba(56, 248, 255, 0.1);
}

.avatar-core {
  position: relative;
  z-index: 2;
  width: 100%;
  height: 100%;
  border-radius: 16px;
  overflow: hidden;
  animation: coreFloat 4s ease-in-out infinite;
}

.avatar-core img,
.avatar-core .avatar-placeholder {
  width: 100%;
  height: 100%;
  border-radius: 16px;
  object-fit: cover;
  border: 2px solid rgba(56, 248, 255, 0.3);
  background: rgba(8, 14, 27, 0.92);
}

.avatar-glow-ring {
  position: absolute;
  z-index: 1;
  inset: -5px;
  border-radius: 21px;
  background: conic-gradient(from 0deg, rgba(56, 248, 255, 0) 0%, rgba(56, 248, 255, 0.55) 25%, rgba(155, 92, 255, 0.55) 75%, rgba(56, 248, 255, 0) 100%);
  animation: ringRotate 6s linear infinite;
  opacity: 0.55;
}

.avatar-glow-ring::before {
  content: '';
  position: absolute;
  inset: 3px;
  background: rgba(8, 14, 27, 0.92);
  border-radius: 18px;
}

@keyframes ringRotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@keyframes coreFloat {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-3px); }
}

.profile-name {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.name-text {
  font-size: 28px;
  font-weight: 700;
  color: #fff;
  margin: 0;
  background: linear-gradient(135deg, #fff 0%, #38f8ff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.name-tag {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
  letter-spacing: 0.05em;
}

.profile-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: flex-end;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
}

.meta-icon {
  font-size: 14px;
}

/* 描述 */
.profile-body {
  padding: 24px 32px;
  border-bottom: 1px solid rgba(56, 248, 255, 0.1);
}

.profile-desc {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  line-height: 1.8;
  margin: 0;
}

/* 区块通用 */
.section-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.section-label {
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.2em;
  color: rgba(56, 248, 255, 0.6);
}

.section-line {
  flex: 1;
  height: 1px;
  background: linear-gradient(to right, rgba(56, 248, 255, 0.2), transparent);
}

/* 技能 */
.skills-section {
  padding: 24px 32px;
  border-bottom: 1px solid rgba(56, 248, 255, 0.1);
}

.skills-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.skill-node {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.skill-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.skill-name {
  font-size: 13px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.8);
}

.skill-level {
  font-size: 11px;
  font-family: 'SF Mono', monospace;
  color: #38f8ff;
}

.skill-bar {
  height: 6px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 3px;
  overflow: hidden;
}

.skill-fill {
  height: 100%;
  background: linear-gradient(90deg, #38f8ff 0%, #9b5cff 100%);
  border-radius: 3px;
  transition: width 0.5s ease;
}

/* 兴趣 */
.interests-section {
  padding: 24px 32px;
  border-bottom: 1px solid rgba(56, 248, 255, 0.1);
}

.interest-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.interest-tag {
  padding: 8px 16px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
  background: rgba(56, 248, 255, 0.08);
  border: 1px solid rgba(56, 248, 255, 0.15);
  border-radius: 8px;
  transition: all 0.2s ease;
}

.interest-tag:hover {
  background: rgba(56, 248, 255, 0.15);
  border-color: rgba(56, 248, 255, 0.3);
  color: #fff;
}

/* 联系方式 */
.contact-section {
  padding: 24px 32px;
  border-bottom: 1px solid rgba(56, 248, 255, 0.1);
}

.contact-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 12px;
}

.contact-node {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(56, 248, 255, 0.1);
  border-radius: 10px;
  text-decoration: none;
  color: inherit;
  transition: all 0.2s ease;
}

.contact-node:hover {
  background: rgba(255, 255, 255, 0.06);
  border-color: var(--node-color, #38f8ff);
  transform: translateY(-2px);
}

.contact-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(255, 255, 255, 0.6);
  transition: color 0.2s;
}

.contact-node:hover .contact-icon {
  color: var(--node-color, #38f8ff);
}

.contact-copy {
  flex: 1;
  min-width: 0;
}

.contact-name {
  display: block;
  font-size: 13px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.8);
}

.contact-detail {
  display: block;
  margin-top: 2px;
  color: rgba(255, 255, 255, 0.42);
  font-size: 11px;
  overflow-wrap: anywhere;
}

.contact-arrow {
  display: flex;
  align-items: center;
  color: rgba(255, 255, 255, 0.3);
  transition: color 0.2s;
}

.contact-node:hover .contact-arrow {
  color: var(--node-color, #38f8ff);
}

/* 底部 */
.profile-footer {
  padding: 24px 32px;
  text-align: center;
}

.footer-text {
  font-family: 'SF Mono', monospace;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.3);
}

.footer-bracket {
  color: rgba(56, 248, 255, 0.4);
}

.footer-content {
  margin: 0 8px;
}

/* 响应式 */
@media (max-width: 640px) {
  .about-page {
    padding: 72px 16px 80px;
  }
  
  .profile-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 24px;
    padding: 32px 24px 24px;
  }
  
  .profile-meta {
    flex-direction: row;
    flex-wrap: wrap;
    align-items: flex-start;
  }
  
  .skills-section,
  .interests-section,
  .contact-section,
  .profile-body,
  .profile-footer {
    padding: 20px 24px;
  }
  
  .skills-grid {
    grid-template-columns: 1fr;
  }
  
  .contact-grid {
    grid-template-columns: 1fr;
  }
}

/* 二维码弹窗 */
.qr-modal {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.qr-content {
  background: linear-gradient(135deg, rgba(17, 27, 49, 0.95), rgba(8, 14, 27, 0.95));
  border: 1px solid rgba(56, 248, 255, 0.3);
  border-radius: 16px;
  padding: 32px;
  text-align: center;
  position: relative;
  max-width: 320px;
  width: 100%;
  box-shadow: 0 0 40px rgba(56, 248, 255, 0.2);
}

.qr-close {
  position: absolute;
  top: 12px;
  right: 16px;
  background: none;
  border: none;
  color: rgba(255, 255, 255, 0.6);
  font-size: 24px;
  cursor: pointer;
  transition: color 0.2s;
}

.qr-close:hover {
  color: #fff;
}

.qr-title {
  font-size: 18px;
  font-weight: 600;
  color: #fff;
  margin: 0 0 20px;
}

.qr-image {
  width: 200px;
  height: 200px;
  border-radius: 8px;
  margin-bottom: 16px;
}

.qr-tip {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  margin: 0;
}

.qr-trigger {
  cursor: pointer;
}

.placeholder-link {
  opacity: 0.5;
  cursor: default;
}

.placeholder-link:hover {
  transform: none;
}

/* 编辑按钮 */
.edit-profile-btn {
  position: absolute;
  top: 16px;
  right: 16px;
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  background: rgba(56, 248, 255, 0.1);
  border: 1px solid rgba(56, 248, 255, 0.3);
  border-radius: 6px;
  color: #38f8ff;
  font-size: 12px;
  cursor: pointer;
  z-index: 10;
  transition: all 0.2s;
}
.edit-profile-btn:hover {
  background: rgba(56, 248, 255, 0.2);
}

/* 编辑弹窗 */
.editor-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.75);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(4px);
}
.editor-panel {
  background: rgba(12, 20, 35, 0.98);
  border: 1px solid rgba(56, 248, 255, 0.2);
  border-radius: 14px;
  padding: 24px;
  width: 90%;
  max-width: 440px;
}
.editor-panel h3 {
  font-size: 16px;
  color: #fff;
  margin: 0 0 16px;
}
.editor-row {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-bottom: 12px;
}
.editor-row label {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.5);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}
.editor-row input,
.editor-row textarea {
  padding: 8px 10px;
  background: rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 6px;
  color: #fff;
  font-size: 13px;
  outline: none;
  font-family: inherit;
  resize: vertical;
}
.editor-row input:focus,
.editor-row textarea:focus {
  border-color: rgba(56, 248, 255, 0.4);
}
.editor-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  margin-top: 4px;
}
.editor-cancel {
  padding: 6px 16px;
  background: none;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 6px;
  color: #888;
  font-size: 13px;
  cursor: pointer;
}
.editor-save {
  padding: 6px 20px;
  background: #38f8ff;
  border: none;
  border-radius: 6px;
  color: #0c1423;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
}
.editor-save:disabled {
  opacity: 0.5;
  cursor: default;
}
</style>
