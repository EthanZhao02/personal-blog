<template>
  <div class="about-page">
    <div class="page-bg" aria-hidden="true">
      <div class="bg-grid"></div>
    </div>

    <div class="about-container">
      <!-- 双栏简历面板 -->
      <section class="resume-panel">
        <!-- 编辑按钮 -->
        <button v-if="userStore.isAdmin" class="edit-resume-btn" @click="openEditor">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
          {{ ui.edit }}
        </button>

        <!-- 左侧边栏：头像 + 基本信息 -->
        <aside class="resume-sidebar">
          <div class="sidebar-avatar ai-core-avatar">
            <span class="neural-ring neural-a" aria-hidden="true"></span>
            <span class="neural-ring neural-b" aria-hidden="true"></span>
            <span class="neural-ring neural-c" aria-hidden="true"></span>
            <span class="avatar-laser laser-a" aria-hidden="true"></span>
            <span class="avatar-laser laser-b" aria-hidden="true"></span>
            <span class="avatar-laser laser-c" aria-hidden="true"></span>
            <span class="neural-node node-a" aria-hidden="true"></span>
            <span class="neural-node node-b" aria-hidden="true"></span>
            <span class="neural-node node-c" aria-hidden="true"></span>
            <div class="avatar-frame">
              <img v-if="profile.avatar" :src="profile.avatar" :alt="profile.name" @error="onAvatarError" />
              <div v-else class="avatar-placeholder">{{ profile.name?.charAt(0) || '?' }}</div>
            </div>
            <div class="avatar-glow"></div>
          </div>

          <h1 class="sidebar-name">{{ profile.name }}</h1>
          <p class="sidebar-tagline">{{ profile.tagline }}</p>

          <div class="sidebar-meta">
            <div class="meta-row">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/></svg>
              <span>{{ profile.location }}</span>
            </div>
            <div class="meta-row">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
              <span>{{ currentTimezone }}</span>
            </div>
            <div class="meta-row">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="4"/><line x1="12" y1="2" x2="12" y2="6"/><line x1="12" y1="18" x2="12" y2="22"/></svg>
              <span class="status-badge">{{ profile.status }}</span>
            </div>
          </div>

          <div class="sidebar-bio" v-if="profile.bio">
            <p>{{ profile.bio }}</p>
          </div>
        </aside>

        <!-- 右侧主体内容 -->
        <main class="resume-main">
          <!-- Skills -->
          <section class="resume-block" v-if="skills.length">
            <div class="block-head">
              <span class="block-tag">{{ ui.skills }}</span>
              <div class="block-line"></div>
            </div>
            <div class="block-body">
              <div class="skill-list">
                <div v-for="skill in skills" :key="skill.name" class="skill-item">
                  <span class="skill-label">{{ skill.name }}</span>
                  <div class="skill-track">
                    <div class="skill-bar" :style="{ width: (skill.level || 50) + '%' }"></div>
                  </div>
                  <span class="skill-pct">{{ skill.level || '--' }}%</span>
                </div>
              </div>
            </div>
          </section>

          <!-- Interests -->
          <section class="resume-block" v-if="interests.length">
            <div class="block-head">
              <span class="block-tag">{{ ui.interests }}</span>
              <div class="block-line"></div>
            </div>
            <div class="block-body">
              <div class="tag-cloud">
                <span v-for="item in interests" :key="item" class="tag-pill">{{ item }}</span>
              </div>
            </div>
          </section>

          <!-- Hobbies -->
          <section class="resume-block" v-if="hobbies.length">
            <div class="block-head">
              <span class="block-tag">{{ ui.hobbies }}</span>
              <div class="block-line"></div>
            </div>
            <div class="block-body">
              <div class="hobby-grid">
                <div v-for="item in hobbies" :key="item" class="hobby-card">
                  <span class="hobby-icon">{{ getHobbyIcon(item) }}</span>
                  <span class="hobby-name">{{ item }}</span>
                </div>
              </div>
            </div>
          </section>

          <!-- Tools -->
          <section class="resume-block" v-if="tools.length">
            <div class="block-head">
              <span class="block-tag">{{ ui.tools }}</span>
              <div class="block-line"></div>
            </div>
            <div class="block-body">
              <div class="tool-grid">
                <div v-for="tool in tools" :key="tool.name" class="tool-item">
                  <span class="tool-name">{{ tool.name }}</span>
                  <span v-if="tool.category" class="tool-cat">{{ tool.category }}</span>
                </div>
              </div>
            </div>
          </section>

          <!-- Blog Story -->
          <section class="resume-block" v-if="profile.blogStory">
            <div class="block-head">
              <span class="block-tag">{{ ui.blogStory }}</span>
              <div class="block-line"></div>
            </div>
            <div class="block-body">
              <p class="story-text">{{ profile.blogStory }}</p>
            </div>
          </section>

          <!-- Name Origin -->
          <section class="resume-block" v-if="profile.nameOrigin">
            <div class="block-head">
              <span class="block-tag">{{ ui.nameOrigin }}</span>
              <div class="block-line"></div>
            </div>
            <div class="block-body">
              <p class="story-text">{{ profile.nameOrigin }}</p>
            </div>
          </section>

          <!-- Connect -->
          <section class="resume-block" v-if="socials.length">
            <div class="block-head">
              <span class="block-tag">{{ ui.connect }}</span>
              <div class="block-line"></div>
            </div>
            <div class="block-body">
              <div class="connect-grid">
                <template v-for="social in socials" :key="social.name">
                  <a v-if="!social.isQRCode && social.url && !social.url.startsWith('#')"
                    :href="social.url" target="_blank" rel="noopener"
                    class="connect-card"
                    :style="{ '--card-color': normalizeSocialColor(social.color) }">
                    <span class="cc-icon" v-html="getSocialIcon(social.icon)"></span>
                    <div class="cc-info">
                      <span class="cc-name">{{ social.name }}</span>
                      <span v-if="social.handle" class="cc-handle">{{ social.handle }}</span>
                    </div>
                    <svg class="cc-arrow" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M7 17l9.2-9.2M17 17V7H7"/></svg>
                  </a>
                  <div v-else-if="social.isQRCode"
                    class="connect-card qr-card" @click="showQRCode(social)" :style="{ '--card-color': normalizeSocialColor(social.color) }">
                    <span class="cc-icon" v-html="getSocialIcon(social.icon)"></span>
                    <div class="cc-info">
                      <span class="cc-name">{{ social.name }}</span>
                      <span v-if="social.handle" class="cc-handle">{{ social.handle }}</span>
                    </div>
                    <svg class="cc-arrow" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M7 17l9.2-9.2M17 17V7H7"/></svg>
                  </div>
                  <div v-else class="connect-card placeholder-card" :style="{ '--card-color': normalizeSocialColor(social.color) }">
                    <span class="cc-icon" v-html="getSocialIcon(social.icon)"></span>
                    <div class="cc-info">
                      <span class="cc-name">{{ social.name }}</span>
                      <span v-if="social.handle" class="cc-handle">{{ social.handle }}</span>
                    </div>
                  </div>
                </template>
              </div>
            </div>
          </section>

          <!-- 底部签名 -->
          <div class="resume-footer">
            <span class="footer-bracket">[</span>
            {{ profile.footer }}
            <span class="footer-bracket">]</span>
          </div>
        </main>

        <!-- 角标 -->
        <div class="corner top-left"></div>
        <div class="corner top-right"></div>
        <div class="corner bottom-left"></div>
        <div class="corner bottom-right"></div>
      </section>

      <!-- 编辑弹窗 -->
      <div v-if="showEditor" class="editor-overlay" @click.self="showEditor = false">
        <div class="editor-panel">
          <h3>编辑个人资料</h3>
          <div class="editor-row"><label>姓名</label><input v-model="editForm.name" /></div>
          <div class="editor-row"><label>标签</label><input v-model="editForm.tagline" placeholder="如：AI & Web Developer" /></div>
          <div class="editor-row"><label>简介</label><textarea v-model="editForm.bio" rows="3" placeholder="一段话介绍自己" /></div>
          <div class="editor-row"><label>位置</label><input v-model="editForm.location" placeholder="如：Beijing, China" /></div>
          <div class="editor-row"><label>状态</label><input v-model="editForm.status" placeholder="如：Available / Busy" /></div>
          <div class="editor-row"><label>技能 Skills</label><textarea v-model="editForm.skillsText" rows="5" placeholder='[
  {"name":"Vue.js","level":85},
  {"name":"Java","level":80}
]' class="editor-json" /></div>
          <div class="editor-row"><label>兴趣 Interests</label><textarea v-model="editForm.interestsText" rows="3" placeholder='["AI", "Web Dev", "Open Source"]' class="editor-json" /></div>
          <div class="editor-row"><label>社交链接 Connect</label><textarea v-model="editForm.socialsText" rows="6" placeholder='[
  {"name":"GitHub","icon":"github","url":"https://...","handle":"@xxx"}
]' class="editor-json" /></div>
          <div class="editor-row"><label>爱好 Hobbies</label><textarea v-model="editForm.hobbiesText" rows="3" placeholder='["阅读", "摄影", "桌游"]' class="editor-json" /></div>
          <div class="editor-row"><label>常用工具 Tools</label><textarea v-model="editForm.toolsText" rows="5" placeholder='[
  {"name":"VS Code","category":"编辑器"},
  {"name":"Cursor","category":"AI编程"}
]' class="editor-json" /></div>
          <div class="editor-row"><label>博客历程 Blog Story</label><textarea v-model="editForm.blogStory" rows="4" placeholder="这个博客的故事..." /></div>
          <div class="editor-row"><label>名字由来 About the Name</label><textarea v-model="editForm.nameOrigin" rows="3" placeholder="名字的含义与由来..." /></div>
          <div class="editor-actions">
            <button @click="showEditor = false" class="editor-cancel">取消</button>
            <button @click="submitProfile" :disabled="saving" class="editor-save">{{ saving ? '保存中...' : '保存' }}</button>
          </div>
        </div>
      </div>

      <!-- 二维码弹窗 -->
      <div v-if="showQR" class="qr-modal" @click="closeQRCode">
        <div class="qr-content" @click.stop>
          <button class="qr-close" @click="closeQRCode">×</button>
          <h4 class="qr-title">{{ currentQR?.name }}</h4>
          <img v-if="currentQR?.qrCodeUrl" :src="resolveAssetUrl(currentQR.qrCodeUrl)" :alt="currentQR?.name" class="qr-image" />
          <p class="qr-tip">{{ ui.qrTip }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, inject, ref, onMounted } from 'vue'
import siteConfig, { resolveAssetUrl } from '../config/site.config'
import { useUserStore } from '../stores/user'
import { getProfile, updateProfile } from '../api/profile'

const userStore = useUserStore()
const showQR = ref(false)
const currentQR = ref(null)
const profile = ref({})
const showEditor = ref(false)
const saving = ref(false)
const editForm = ref({})
const siteLanguage = inject('siteLanguage', ref(localStorage.getItem('ethan-language') || 'zh'))

const ui = computed(() => siteLanguage.value === 'en'
  ? {
      edit: 'Edit',
      skills: 'SKILLS',
      interests: 'INTERESTS',
      hobbies: 'HOBBIES',
      tools: 'TOOLBELT',
      blogStory: 'BLOG STORY',
      nameOrigin: 'ABOUT THE NAME',
      connect: 'CONNECT',
      qrTip: 'Scan the QR code to follow me',
    }
  : {
      edit: '编辑',
      skills: '技能矩阵',
      interests: '兴趣方向',
      hobbies: '生活爱好',
      tools: '常用工具',
      blogStory: '博客故事',
      nameOrigin: '名字由来',
      connect: '连接我',
      qrTip: '扫描二维码关注我',
    })

const parseSkills = (raw) => {
  if (!raw) return []
  const arr = typeof raw === 'string' ? JSON.parse(raw) : raw
  return arr.map(s => typeof s === 'string' ? { name: s, level: 70 + Math.floor(Math.random() * 25) } : s)
}
const parseInterests = (raw) => {
  if (!raw) return siteConfig.about?.interests || []
  return typeof raw === 'string' ? JSON.parse(raw) : raw
}
const parseSocials = (raw) => {
  if (!raw) return siteConfig.socials || []
  return typeof raw === 'string' ? JSON.parse(raw) : raw
}
const parseHobbies = (raw) => {
  if (!raw) return []
  return typeof raw === 'string' ? JSON.parse(raw) : raw
}
const parseTools = (raw) => {
  if (!raw) return []
  return typeof raw === 'string' ? JSON.parse(raw) : raw
}

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
        footer: siteConfig.about?.footer || '保持热爱，奔赴山海',
        blogStory: p.blogStory || '',
        nameOrigin: p.nameOrigin || '',
        _skills: parseSkills(p.skills || siteConfig.about?.skills),
        _interests: parseInterests(p.interests || siteConfig.about?.interests),
        _socials: parseSocials(p.socials),
        _hobbies: parseHobbies(p.hobbies),
        _tools: parseTools(p.tools)
      }
      return
    }
  } catch {}
  profile.value = {
    name: siteConfig.name,
    tagline: siteConfig.about?.tagline || 'Developer & Creator',
    bio: siteConfig.about?.bio || '',
    location: siteConfig.about?.location || 'Earth',
    status: siteConfig.about?.status || 'Available',
    avatar: siteConfig.avatar || '',
    footer: siteConfig.about?.footer || '保持热爱，奔赴山海',
    blogStory: '',
    nameOrigin: '',
    _skills: parseSkills(siteConfig.about?.skills),
    _interests: parseInterests(siteConfig.about?.interests),
    _socials: siteConfig.socials || [],
    _hobbies: [],
    _tools: []
  }
}

onMounted(loadProfile)

const skills = computed(() => profile.value._skills || [])
const interests = computed(() => profile.value._interests || [])
const socials = computed(() => profile.value._socials || [])
const hobbies = computed(() => profile.value._hobbies || [])
const tools = computed(() => profile.value._tools || [])
const normalizeSocialColor = (color) => {
  const fallback = '#60a5fa'
  const normalized = String(color || fallback).trim().toLowerCase()
  const colorMap = {
    '#38f8ff': '#60a5fa',
    '#38bdf8': '#60a5fa',
    '#7dd3fc': '#93c5fd',
    '#00b96b': '#3b82f6',
    '#10b981': '#3b82f6',
    '#14b8a6': '#3b82f6',
    '#22c55e': '#60a5fa',
    '#34d399': '#60a5fa',
    '#8df8c7': '#8b5cf6',
  }
  return colorMap[normalized] || color || fallback
}

const openEditor = () => {
  const skillsRaw = profile.value._skills?.map(s => ({ name: s.name, level: s.level })) || []
  editForm.value = {
    name: profile.value.name || '',
    tagline: profile.value.tagline || '',
    bio: profile.value.bio || '',
    location: profile.value.location || '',
    status: profile.value.status || '',
    skillsText: JSON.stringify(skillsRaw, null, 2),
    interestsText: JSON.stringify(profile.value._interests || []),
    socialsText: JSON.stringify(profile.value._socials || [], null, 2),
    hobbiesText: JSON.stringify(profile.value._hobbies || [], null, 2),
    toolsText: JSON.stringify(profile.value._tools || [], null, 2),
    blogStory: profile.value.blogStory || '',
    nameOrigin: profile.value.nameOrigin || ''
  }
  showEditor.value = true
}

const submitProfile = async () => {
  saving.value = true
  try {
    const payload = { ...editForm.value }
    // 后端 skills/interests/socials/hobbies/tools 是 String 类型，需要 stringify
    try { payload.skills = JSON.stringify(JSON.parse(editForm.value.skillsText || '[]')) } catch { alert('Skills JSON 格式不正确'); saving.value = false; return }
    try { payload.interests = JSON.stringify(JSON.parse(editForm.value.interestsText || '[]')) } catch { alert('Interests JSON 格式不正确'); saving.value = false; return }
    try { payload.socials = JSON.stringify(JSON.parse(editForm.value.socialsText || '[]')) } catch { alert('Connect JSON 格式不正确'); saving.value = false; return }
    try { payload.hobbies = JSON.stringify(JSON.parse(editForm.value.hobbiesText || '[]')) } catch { alert('Hobbies JSON 格式不正确'); saving.value = false; return }
    try { payload.tools = JSON.stringify(JSON.parse(editForm.value.toolsText || '[]')) } catch { alert('Tools JSON 格式不正确'); saving.value = false; return }
    delete payload.skillsText; delete payload.interestsText; delete payload.socialsText; delete payload.hobbiesText; delete payload.toolsText
    const res = await updateProfile(payload)
    if (res.code === 200) { showEditor.value = false; await loadProfile() }
    else alert(res.message || '保存失败')
  } catch { alert('保存失败') }
  saving.value = false
}

const currentTimezone = computed(() => {
  const offset = -new Date().getTimezoneOffset() / 60
  return `UTC${offset >= 0 ? '+' : ''}${offset}`
})
const onAvatarError = (e) => { e.target.style.display = 'none' }
const getHobbyIcon = (name) => {
  const map = {
    '阅读': '📚', '读书': '📚', '科幻': '🚀', '小说': '📖',
    '摄影': '📷', '旅行': '✈️', '旅游': '✈️',
    '桌游': '🎲', '游戏': '🎮', '咖啡': '☕', '探店': '☕',
    '音乐': '🎵', '吉他': '🎸', '钢琴': '🎹',
    '电影': '🎬', '运动': '⚽', '跑步': '🏃', '健身': '💪',
    '烹饪': '🍳', '美食': '🍜', '画画': '🎨', '绘画': '🎨',
    '代码': '💻', '编程': '💻', '写作': '✍️',
    '茶': '🍵', '手工': '🔨', '回附': '🔧'
  }
  return map[name] || '01'
}
const showQRCode = (s) => { currentQR.value = s; showQR.value = true }
const closeQRCode = () => { showQR.value = false; currentQR.value = null }

const getSocialIcon = (icon) => {
  // 使用 SimpleIcons CDN 真实品牌图标
  const iconMap = {
    github: 'github',
    bilibili: 'bilibili',
    zhihu: 'zhihu',
    twitter: 'x',
    douyin: 'tiktok',
    telegram: 'telegram',
    email: 'gmail',
    xiaohongshu: 'xiaohongshu',
    weibo: 'sinaweibo',
    juejin: 'juejin',
    leetcode: 'leetcode',
    wechat: 'wechat',
    qq: 'tencentqq',
    gitlab: 'gitlab',
    gitee: 'gitee',
    csdn: 'csdn',
    yuque: 'yuque'
  }
  const slug = iconMap[icon]
  if (!slug) return '<span style="font-size:12px">--</span>'
  // 用 inline SVG 而非 CDN 图片，保证暗色模式下可见
  const svgPaths = {
    github: '<path d="M12 .297c-6.63 0-12 5.373-12 12 0 5.303 3.438 9.8 8.205 11.385.6.113.82-.258.82-.577 0-.285-.01-1.04-.015-2.04-3.338.724-4.042-1.61-4.042-1.61C4.422 18.07 3.633 17.7 3.633 17.7c-1.087-.744.084-.729.084-.729 1.205.084 1.838 1.236 1.838 1.236 1.07 1.835 2.809 1.305 3.495.998.108-.776.417-1.305.76-1.605-2.665-.3-5.466-1.332-5.466-5.93 0-1.31.465-2.38 1.235-3.22-.135-.303-.54-1.523.105-3.176 0 0 1.005-.322 3.3 1.23.96-.267 1.98-.399 3-.405 1.02.006 2.04.138 3 .405 2.28-1.552 3.285-1.23 3.285-1.23.645 1.653.24 2.873.12 3.176.765.84 1.23 1.91 1.23 3.22 0 4.61-2.805 5.625-5.475 5.92.42.36.81 1.096.81 2.22 0 1.606-.015 2.896-.015 3.286 0 .315.21.69.825.57C20.565 22.092 24 17.592 24 12.297c0-6.627-5.373-12-12-12"/>',
    bilibili: '<path d="M17.813 4.653h.854c1.51.054 2.769.578 3.773 1.574 1.004.995 1.524 2.249 1.56 3.76v7.36c-.036 1.51-.556 2.769-1.56 3.773s-2.262 1.524-3.773 1.56H5.333c-1.51-.036-2.769-.556-3.773-1.56S.036 18.858 0 17.347v-7.36c.036-1.511.556-2.765 1.56-3.76 1.004-.996 2.262-1.52 3.773-1.574h.774l-1.174-1.12a1.234 1.234 0 0 1-.373-.906c0-.356.124-.659.373-.907l.027-.027c.267-.249.573-.373.92-.373.347 0 .653.124.92.373L9.653 4.44c.071.071.134.142.187.213h4.267a.836.836 0 0 1 .16-.213l2.853-2.747c.267-.249.573-.373.92-.373.347 0 .662.151.929.4.267.249.391.551.391.907 0 .355-.124.657-.373.906zM5.333 7.24c-.746.018-1.373.276-1.88.773-.506.498-.769 1.13-.786 1.894v7.52c.017.764.28 1.395.786 1.893.507.498 1.134.756 1.88.773h13.334c.746-.017 1.373-.275 1.88-.773.506-.498.769-1.129.786-1.893v-7.52c-.017-.765-.28-1.396-.786-1.894-.507-.497-1.134-.755-1.88-.773zM8 11.107c.373 0 .684.124.933.373.25.249.383.569.4.96v1.173c-.017.391-.15.711-.4.96-.249.25-.56.374-.933.374s-.684-.125-.933-.374c-.25-.249-.383-.569-.4-.96V12.44c0-.373.129-.689.386-.947.258-.257.574-.386.947-.386zm8 0c.373 0 .684.124.933.373.25.249.383.569.4.96v1.173c-.017.391-.15.711-.4.96-.249.25-.56.374-.933.374s-.684-.125-.933-.374c-.25-.249-.383-.569-.4-.96V12.44c.017-.391.15-.711.4-.96.249-.249.56-.373.933-.373z"/>',
    zhihu: '<path d="M5.721 0C2.251 0 0 2.25 0 5.719V18.28C0 21.751 2.252 24 5.721 24h12.56C21.751 24 24 21.75 24 18.281V5.72C24 2.249 21.75 0 18.281 0zm1.964 4.078c-.271.73-.5 1.434-.68 2.11h4.587c.545-.006.445 1.168.445 1.171H9.384a58.104 58.104 0 0 1-.112 3.797h2.726c.388.023.393 1.251.393 1.251H9.183c.062 1.191.328 2.559.837 4.106.388.116.774-.078.774-.078 0-.078-.291-1.07-.291-1.07a5.597 5.597 0 0 1 2.831.078c.097.485.155.95.184 1.398a3.29 3.29 0 0 1-.753.252c-.078.272-.35.932-.35.932l-.078.155c-.252.66-.524 1.282-.815 1.863a6.43 6.43 0 0 1-1.126 1.67 5.243 5.243 0 0 1-1.553 1.126c-.582.291-1.204.436-1.864.436-.66 0-1.282-.145-1.863-.436a5.244 5.244 0 0 1-1.554-1.126 6.43 6.43 0 0 1-1.126-1.67 7.768 7.768 0 0 1-.815-1.863l-.078-.155s-.272-.66-.35-.932a3.29 3.29 0 0 1-.753-.252c.029-.448.087-.913.184-1.398.66-.155 1.864-.078 2.831-.078 0 0-.252.992-.291 1.07 0 0 .388.194.774.078.51-1.547.776-2.915.837-4.106H4.077s.006-1.228.393-1.251h2.726a58.104 58.104 0 0 1-.112-3.797H4.077s-.1-1.177.445-1.171h4.587c-.18-.676-.409-1.38-.68-2.11z"/>',
    x: '<path d="M18.244 2.25h3.308l-7.227 8.26 8.502 11.24H16.17l-5.214-6.817L4.99 21.75H1.68l7.73-8.835L1.254 2.25H8.08l4.713 6.231zm-1.161 17.52h1.833L7.084 4.126H5.117z"/>',
    tiktok: '<path d="M12.525.02c1.31-.02 2.61-.01 3.91-.02.08 1.53.63 3.09 1.75 4.17 1.12 1.11 2.7 1.62 4.24 1.79v4.03c-1.44-.05-2.89-.35-4.2-.97-.57-.26-1.1-.59-1.62-.93-.01 2.92.01 5.84-.02 8.75-.08 1.4-.54 2.79-1.35 3.94-1.31 1.92-3.58 3.17-5.91 3.21-1.43.08-2.86-.31-4.08-1.03-2.02-1.19-3.44-3.37-3.65-5.71-.02-.5-.03-1-.01-1.49.18-1.9 1.12-3.72 2.58-4.96 1.66-1.44 3.98-2.13 6.15-1.72.02 1.48-.04 2.96-.04 4.44-.99-.32-2.15-.23-3.02.37-.63.41-1.11 1.04-1.36 1.75-.21.51-.15 1.07-.14 1.61.24 1.64 1.82 3.02 3.5 2.87 1.12-.01 2.19-.66 2.77-1.61.19-.33.4-.67.41-1.06.1-1.79.06-3.57.07-5.36.01-4.03-.01-8.05.02-12.07z"/>',
    telegram: '<path d="M11.944 0A12 12 0 0 0 0 12a12 12 0 0 0 12 12 12 12 0 0 0 12-12A12 12 0 0 0 12 0a12 12 0 0 0-.056 0zm4.962 7.224c.1-.002.321.023.465.14a.506.506 0 0 1 .171.325c.016.093.036.306.02.472-.18 1.898-.962 6.502-1.36 8.627-.168.9-.499 1.201-.82 1.23-.696.065-1.225-.46-1.9-.902-1.056-.693-1.653-1.124-2.678-1.8-1.185-.78-.417-1.21.258-1.91.177-.184 3.247-2.977 3.307-3.23.007-.032.014-.15-.056-.212s-.174-.041-.249-.024c-.106.024-1.793 1.14-5.061 3.345-.48.33-.913.49-1.302.48-.428-.008-1.252-.241-1.865-.44-.752-.245-1.349-.374-1.297-.789.027-.216.325-.437.893-.663 3.498-1.524 5.83-2.529 6.998-3.014 3.332-1.386 4.025-1.627 4.476-1.635z"/>',
    gmail: '<path d="M24 5.457v13.909c0 1.094-.626 2.061-1.54 2.544l-1.855-1.484.013-.012L12 13.328l-8.618 6.786.013.012L1.54 21.91C.626 21.426 0 20.46 0 19.366V5.457c0-1.094.626-2.061 1.54-2.544L12 11.636l10.46-8.723C23.374 3.396 24 4.363 24 5.457z"/>',
    xiaohongshu: '<path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm4.64 6.8c-.15 1.58-.8 5.42-1.13 7.19-.14.75-.42 1-.68 1.03-.58.05-1.02-.38-1.58-.75-.88-.58-1.38-.94-2.23-1.5-.99-.65-.35-1.01.22-1.59.15-.15 2.71-2.48 2.76-2.69a.2.2 0 0 0-.05-.18c-.06-.05-.14-.03-.21-.02-.09.02-1.49.95-4.22 2.79-.4.27-.76.41-1.08.4-.36-.01-1.04-.2-1.55-.37-.63-.2-1.12-.31-1.08-.66.02-.18.27-.36.74-.55 2.92-1.27 4.86-2.11 5.83-2.51 2.78-1.16 3.35-1.36 3.73-1.36.08 0 .27.02.39.12.1.08.13.19.14.27-.01.06.01.24 0 .38z"/>',
    sinaweibo: '<path d="M10.098 20.323c-3.977.391-7.414-1.406-7.672-4.02-.259-2.609 2.759-5.047 6.74-5.441 3.979-.394 7.413 1.404 7.671 4.018.259 2.6-2.759 5.049-6.737 5.439l-.002.004zM9.05 17.219c-.384.616-1.208.884-1.829.602-.612-.279-.793-.991-.406-1.593.379-.595 1.176-.861 1.793-.601.622.263.82.972.442 1.592zm1.27-1.627c-.141.237-.449.353-.689.253-.236-.09-.313-.361-.177-.586.138-.227.436-.346.672-.24.239.09.315.36.18.573h.014zm.176-2.719c-1.893-.493-4.033.45-4.857 2.118-.836 1.704-.026 3.591 1.886 4.21 1.983.64 4.318-.341 5.132-2.179.8-1.793-.201-3.642-2.161-4.149zm7.563-1.224c-.346-.105-.579-.18-.402-.649.386-1.031.425-1.922.008-2.557-.781-1.192-2.924-1.128-5.354-.034 0 0-.767.334-.571-.271.378-1.207.32-2.217-.266-2.8-1.331-1.32-4.869.047-7.91 3.052C1.834 10.411.124 12.695.124 14.555c0 3.557 4.567 5.72 9.031 5.72 5.847 0 9.737-3.397 9.737-6.093 0-1.631-1.377-2.553-2.813-2.913l.002-.002zm1.627-3.885c-.752-.856-1.864-1.291-3.031-1.291-.184 0-.369.015-.553.044l-.134.022-.038.134c-.059.214.067.437.281.496l.133.038c.151.038.306.058.462.058.793 0 1.531.295 2.078.833.548.537.84 1.257.822 2.028l-.007.135.134.038c.213.059.437-.067.496-.281l.038-.134c.029-.185.044-.369.044-.554 0-1.166-.435-2.278-1.291-3.031l-.434-.415z"/>',
    juejin: '<path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1.5 15.5h-2v-7h2v7zm5 0h-2v-4h-2v-3h4v7z"/>',
    leetcode: '<path d="M13.483 0a1.374 1.374 0 0 0-.961.438L7.116 6.226l-3.854 4.126a5.266 5.266 0 0 0-1.209 2.104 5.35 5.35 0 0 0-.125.513 5.527 5.527 0 0 0 .062 2.362 5.83 5.83 0 0 0 .349 1.017 5.938 5.938 0 0 0 1.271 1.818l4.277 4.193.039.038c2.248 2.165 5.852 2.133 8.063-.074l2.386-2.379a2.245 2.245 0 0 0-.002-3.174 2.236 2.236 0 0 0-3.161.003l-2.386 2.379a2.236 2.236 0 0 1-3.161.003l-.032-.031 2.748-2.749a2.245 2.245 0 0 0-.002-3.172 2.237 2.237 0 0 0-3.162 0l-2.75 2.75-.031-.031a2.237 2.237 0 0 1 0-3.163l3.778-3.776a2.237 2.237 0 0 0 0-3.162 2.237 2.237 0 0 0-3.162 0L9.223 9.63a2.237 2.237 0 0 1-3.162 0 2.237 2.237 0 0 1 0-3.162l4.277-4.193a1.374 1.374 0 0 0 .438-.961V1.374A1.374 1.374 0 0 0 13.483 0z"/>',
    wechat: '<path d="M8.691 2.188C3.891 2.188 0 5.476 0 9.53c0 2.212 1.17 4.203 3.002 5.55a.59.59 0 0 1 .213.665l-.39 1.48c-.019.07-.048.141-.048.213 0 .163.13.295.29.295a.326.326 0 0 0 .167-.054l1.903-1.114a.864.864 0 0 1 .717-.098 10.16 10.16 0 0 0 2.837.403c.276 0 .543-.027.81-.05-.857-2.578.157-4.972 1.616-6.446 1.007-1.02 2.345-1.663 3.822-1.815-.276-3.435-3.954-6.155-8.448-6.155zM5.785 5.991c.642 0 1.162.529 1.162 1.18a1.17 1.17 0 0 1-1.162 1.178A1.17 1.17 0 0 1 4.623 7.17c0-.651.52-1.18 1.162-1.18zm5.813 0c.642 0 1.162.529 1.162 1.18a1.17 1.17 0 0 1-1.162 1.178 1.17 1.17 0 0 1-1.162-1.178c0-.651.52-1.18 1.162-1.18zm5.34 2.867c-1.797-.052-3.746.512-5.28 2.071-1.62 1.648-2.526 4.265-1.546 7.075.62 1.78 1.94 3.748 4.83 4.739.8.27 1.7.431 2.67.431.708 0 1.464-.103 2.2-.295a.671.671 0 0 1 .595.116l1.184.86a.264.264 0 0 0 .152.054c.131 0 .242-.106.242-.239 0-.061-.018-.116-.039-.17l-.314-1.026a.516.516 0 0 1 .18-.57c1.594-1.155 2.604-2.894 2.604-4.838 0-3.412-3.006-6.155-6.708-6.155-.144 0-.288.005-.43.014zm-2.876 3.155c.515 0 .932.424.932.946a.946.946 0 0 1-.932.946.946.946 0 0 1-.932-.946c0-.522.417-.946.932-.946zm4.726 0c.515 0 .932.424.932.946a.946.946 0 0 1-.932.946.946.946 0 0 1-.932-.946c0-.522.417-.946.932-.946z"/>',
    tencentqq: '<path d="M21.395 15.035a39.548 39.548 0 0 0-.803-2.264l-1.079-2.695c.001-.032.014-.562.014-.836C19.526 4.632 17.351 0 12 0S4.474 4.632 4.474 9.241c0 .274.013.804.014.836l-1.08 2.695a39.547 39.547 0 0 0-.802 2.264c-1.021 3.283-.69 4.643-.438 4.673.54.065 2.103-2.472 2.103-2.472 0 1.469.756 3.387 2.394 4.771-.612.188-1.363.479-1.845.835-.434.32-.379.646-.301.778.343.578 5.883.369 7.582.189 1.7.18 7.239.389 7.582-.189.078-.132.133-.458-.301-.778-.482-.356-1.233-.647-1.845-.835 1.637-1.384 2.393-3.302 2.393-4.771 0 0 1.564 2.537 2.105 2.472.251-.03.582-1.39-.439-4.673z"/>',
    gitee: '<path d="M11.984 0A12 12 0 0 0 0 12a12 12 0 0 0 12 12 12 12 0 0 0 12-12A12 12 0 0 0 12 0a12 12 0 0 0-.016 0zm4.69 8.785v.001c.083.004.161.01.238.02.404.052.79.197 1.103.448.32.257.566.608.711 1.012.145.404.186.846.124 1.28a2.49 2.49 0 0 1-.448 1.103 2.55 2.55 0 0 1-.912.757 2.56 2.56 0 0 1-1.178.227 2.58 2.58 0 0 1-.238-.02v.001l-6.755-.001a2.48 2.48 0 0 1-.238.02 2.56 2.56 0 0 1-1.178-.227 2.55 2.55 0 0 1-.912-.757 2.49 2.49 0 0 1-.448-1.103 2.47 2.47 0 0 1 .124-1.28c.145-.404.391-.755.711-1.012a2.47 2.47 0 0 1 1.103-.448c.077-.01.155-.016.238-.02v-.001h7.314z"/>',
    csdn: '<path d="M14.594 4.078c-.271.73-.5 1.434-.68 2.11h4.587c.545-.006.445 1.168.445 1.171h-4.083a58.104 58.104 0 0 1-.112 3.797h2.726c.388.023.393 1.251.393 1.251h-3.184c.062 1.191.328 2.559.837 4.106.388.116.774-.078.774-.078 0-.078-.291-1.07-.291-1.07a5.597 5.597 0 0 1 2.831.078c.097.485.155.95.184 1.398a3.29 3.29 0 0 1-.753.252c-.078.272-.35.932-.35.932l-.078.155c-.252.66-.524 1.282-.815 1.863a6.43 6.43 0 0 1-1.126 1.67 5.243 5.243 0 0 1-1.553 1.126c-.582.291-1.204.436-1.864.436-.66 0-1.282-.145-1.863-.436a5.244 5.244 0 0 1-1.554-1.126 6.43 6.43 0 0 1-1.126-1.67 7.768 7.768 0 0 1-.815-1.863l-.078-.155s-.272-.66-.35-.932a3.29 3.29 0 0 1-.753-.252c.029-.448.087-.913.184-1.398.66-.155 1.864-.078 2.831-.078 0 0-.252.992-.291 1.07 0 0 .388.194.774.078.51-1.547.776-2.915.837-4.106H9.183s.006-1.228.393-1.251h2.726a58.104 58.104 0 0 1-.112-3.797H9.183s-.1-1.177.445-1.171h4.587c-.18-.676-.409-1.38-.68-2.11z"/>',
    yuque: '<path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1.5 15.5h-2v-7h2v7zm5 0h-2v-4h-2v-3h4v7z"/>'
  }
  const path = svgPaths[slug]
  if (!path) return `<span style="font-size:12px;font-weight:700">${icon}</span>`
  return `<svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor">${path}</svg>`
}
</script>

<style scoped>
.about-page {
  position: relative;
  min-height: 100vh;
  padding: 80px 24px 100px;
}
.page-bg { position: fixed; inset: 0; pointer-events: none; z-index: 0; }
.bg-grid {
  position: absolute; inset: 0;
  background-image:
    linear-gradient(rgba(56, 189, 248, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(56, 189, 248, 0.03) 1px, transparent 1px);
  background-size: 60px 60px;
  mask-image: linear-gradient(to bottom, #000, transparent 80%);
}
.about-container { position: relative; z-index: 1; max-width: 1080px; margin: 0 auto; }

/* ======== 双栏简历面板 ======== */
.resume-panel {
  position: relative;
  display: grid;
  grid-template-columns: minmax(280px, 320px) minmax(0, 1fr);
  background:
    linear-gradient(135deg, rgba(12, 20, 35, 0.88), rgba(8, 12, 24, 0.9)),
    repeating-linear-gradient(90deg, rgba(147, 197, 253, 0.035) 0 1px, transparent 1px 96px);
  border: 1px solid rgba(96, 165, 250, 0.18);
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 24px 80px rgba(0,0,0,0.35), inset 0 0 90px rgba(96,165,250,0.035);
  animation: resumeIn 0.7s var(--ease-out) backwards;
}

@keyframes resumeIn {
  from { opacity: 0; transform: translateY(24px) scale(0.98); filter: blur(6px); }
  to { opacity: 1; transform: translateY(0) scale(1); filter: blur(0); }
}

/* 编辑按钮 */
.edit-resume-btn {
  position: absolute; top: 14px; right: 14px; z-index: 10;
  display: flex; align-items: center; gap: 4px;
  padding: 6px 12px;
  background: rgba(96, 165, 250, 0.1);
  border: 1px solid rgba(96, 165, 250, 0.25);
  border-radius: 6px; color: #60a5fa; font-size: 12px; cursor: pointer;
  transition: all 0.2s;
}
.edit-resume-btn:hover { background: rgba(96, 165, 250, 0.2); }

/* 左侧栏 */
.resume-sidebar {
  padding: 36px 28px 28px;
  border-right: 1px solid rgba(96, 165, 250, 0.1);
  text-align: center;
  display: flex; flex-direction: column; align-items: center; gap: 14px;
}
.sidebar-avatar {
  position: relative; width: 90px; height: 90px; flex-shrink: 0;
}
.ai-core-avatar {
  width: 154px;
  height: 154px;
  display: grid;
  place-items: center;
  margin: 10px 0 4px;
  animation: coreLevitate 5.8s ease-in-out infinite;
}
.ai-core-avatar::before,
.ai-core-avatar::after {
  display: none !important;
}
.avatar-frame {
  width: 100%; height: 100%; border-radius: 18px; overflow: hidden;
  border: 2px solid rgba(96, 165, 250, 0.3);
  position: relative; z-index: 2;
}
.ai-core-avatar .avatar-frame {
  width: 90px;
  height: 90px;
  border-radius: 50%;
  border-color: rgba(147, 197, 253, 0.52);
  box-shadow: 0 0 32px rgba(96, 165, 250, 0.16), inset 0 0 24px rgba(96, 165, 250, 0.08);
}
.avatar-frame img, .avatar-placeholder {
  width: 100%; height: 100%; object-fit: cover;
}
.avatar-placeholder {
  display: flex; align-items: center; justify-content: center;
  font-size: 36px; font-weight: 700; color: #60a5fa;
  background: rgba(96, 165, 250, 0.1);
}
.avatar-glow {
  position: absolute; z-index: 1; inset: -4px; border-radius: 22px;
  background: conic-gradient(from 0deg, rgba(96,165,250,0) 0%, rgba(96,165,250,0.45) 25%, rgba(155,92,255,0.45) 75%, rgba(96,165,250,0) 100%);
  animation: glowSpin 6s linear infinite; opacity: 0.5;
}
.ai-core-avatar .avatar-glow {
  inset: 23px;
  border-radius: 50%;
  opacity: 0.68;
}
.avatar-glow::before {
  content: ''; position: absolute; inset: 3px;
  background: rgba(8, 14, 27, 0.92); border-radius: 19px;
}
.ai-core-avatar .avatar-glow::before {
  border-radius: 50%;
}
.neural-ring {
  position: absolute;
  border-radius: 999px;
  pointer-events: none;
  border: 1px solid rgba(147, 197, 253, 0.28);
}
.neural-a {
  inset: 0;
  border-style: dashed;
  animation: neuralSpin 12s linear infinite;
}
.neural-b {
  inset: 14px;
  border-color: rgba(167, 139, 250, 0.36);
  animation: neuralSpin 8s linear infinite reverse;
}
.neural-c {
  inset: 34px;
  border-color: rgba(255, 255, 255, 0.14);
  box-shadow: inset 0 0 28px rgba(96, 165, 250, 0.08);
  animation: corePulse 2.6s ease-in-out infinite;
}
.avatar-laser {
  position: absolute;
  width: 118px;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(96, 165, 250, 0.86), rgba(167, 139, 250, 0.72), transparent);
  filter: drop-shadow(0 0 12px rgba(96, 165, 250, 0.52));
  pointer-events: none;
  transform-origin: center;
}
.laser-a { transform: rotate(15deg); animation: laserBreathe 3.8s ease-in-out infinite; }
.laser-b { transform: rotate(101deg); animation: laserBreathe 4.6s ease-in-out 0.4s infinite reverse; }
.laser-c { transform: rotate(-37deg); animation: laserBreathe 5.2s ease-in-out 0.8s infinite; }
.neural-node {
  position: absolute;
  width: 8px;
  height: 8px;
  border-radius: 999px;
  background: #dbeafe;
  box-shadow: 0 0 16px rgba(96, 165, 250, 0.78);
}
.node-a { transform: translate(58px, -44px); }
.node-b { transform: translate(-62px, 38px); background: #a78bfa; }
.node-c { transform: translate(14px, 67px); background: #ffbd66; }
@keyframes glowSpin { to { transform: rotate(360deg); } }
@keyframes neuralSpin { to { transform: rotate(360deg); } }
@keyframes corePulse {
  0%, 100% { opacity: 0.42; transform: scale(0.94); }
  50% { opacity: 0.86; transform: scale(1.04); }
}
@keyframes laserBreathe {
  0%, 100% { opacity: 0.2; scale: 0.8 1; }
  50% { opacity: 0.82; scale: 1.08 1; }
}
@keyframes coreLevitate {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-5px); }
}

.sidebar-name {
  font-size: 22px; font-weight: 700; color: #fff; margin: 0;
  background: linear-gradient(135deg, #fff 0%, #60a5fa 100%);
  -webkit-background-clip: text; -webkit-text-fill-color: transparent;
  background-clip: text;
}
.sidebar-tagline {
  font-size: 12px; color: rgba(255,255,255,0.45); margin: 0; letter-spacing: 0.04em;
}
.sidebar-meta { display: flex; flex-direction: column; gap: 6px; width: 100%; }
.meta-row {
  display: flex; align-items: center; gap: 8px; font-size: 12px;
  color: rgba(255,255,255,0.45); justify-content: center;
}
.status-badge { color: #60a5fa; }
.sidebar-bio {
  text-align: left; padding: 14px 0 0;
  border-top: 1px solid rgba(96,165,250,0.08);
}
.sidebar-bio p {
  font-size: 13px; color: rgba(255,255,255,0.6); line-height: 1.8; margin: 0;
}

/* 右侧主体 */
.resume-main {
  padding: 32px 36px 28px; min-width: 0;
}
.resume-block { margin-bottom: 24px; }
.resume-block:last-of-type { margin-bottom: 0; }

.block-head {
  display: flex; align-items: center; gap: 14px; margin-bottom: 14px;
}
.block-tag {
  font-size: 10px; font-weight: 600; letter-spacing: 0.18em;
  color: rgba(96, 165, 250, 0.55); white-space: nowrap;
}
.block-line {
  flex: 1; height: 1px;
  background: linear-gradient(to right, rgba(96,165,250,0.15), transparent);
}

/* Skills */
.skill-list { display: flex; flex-direction: column; gap: 14px; }
.skill-item { display: flex; align-items: center; gap: 12px; }
.skill-label { width: 90px; font-size: 12px; color: rgba(255,255,255,0.75); font-weight: 500; text-align: right; flex-shrink: 0; }
.skill-track {
  flex: 1; height: 5px; background: rgba(255,255,255,0.05);
  border-radius: 3px; overflow: hidden;
}
.skill-bar {
  height: 100%; border-radius: 3px;
  background: linear-gradient(90deg, #60a5fa 0%, #9b5cff 100%);
  transition: width 0.6s ease;
}
.skill-pct {
  width: 32px; font-size: 10px; font-family: 'SF Mono', monospace;
  color: #60a5fa; text-align: right;
}

/* Interests */
.tag-cloud { display: flex; flex-wrap: wrap; gap: 8px; }
.tag-pill {
  padding: 6px 14px; font-size: 12px; color: rgba(255,255,255,0.65);
  background: rgba(96,165,250,0.06); border: 1px solid rgba(96,165,250,0.12);
  border-radius: 7px; transition: all 0.2s;
}
.tag-pill:hover {
  background: rgba(96,165,250,0.14); border-color: rgba(96,165,250,0.3); color: #fff;
}

/* Hobbies */
.hobby-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(120px, 1fr)); gap: 10px; }
.hobby-card {
  display: flex; align-items: center; gap: 10px; padding: 12px 14px;
  background: rgba(255,255,255,0.025); border: 1px solid rgba(96,165,250,0.08);
  border-radius: 10px; transition: all 0.2s;
}
.hobby-card:hover {
  background: rgba(96,165,250,0.06); border-color: rgba(96,165,250,0.2); transform: translateY(-2px);
}
.hobby-icon { font-size: 20px; line-height: 1; }
.hobby-name { font-size: 13px; color: rgba(255,255,255,0.75); font-weight: 500; }

/* Tools */
.tool-grid { display: flex; flex-wrap: wrap; gap: 8px; }
.tool-item {
  display: flex; flex-direction: column; gap: 3px; padding: 8px 14px;
  background: rgba(255,255,255,0.02); border: 1px solid rgba(96,165,250,0.08);
  border-radius: 8px; transition: all 0.2s;
}
.tool-item:hover {
  background: rgba(96,165,250,0.06); border-color: rgba(96,165,250,0.2);
}
.tool-name { font-size: 13px; color: rgba(255,255,255,0.8); font-weight: 500; }
.tool-cat { font-size: 10px; color: rgba(255,255,255,0.35); }

/* Story / Name Origin */
.story-text {
  font-size: 14px; color: rgba(255,255,255,0.65); line-height: 1.9; margin: 0;
  padding: 14px 18px; background: rgba(255,255,255,0.02); border-left: 2px solid rgba(96,165,250,0.3);
  border-radius: 0 8px 8px 0;
}

/* Connect */
.connect-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(180px, 1fr)); gap: 10px; }
.connect-card {
  display: flex; align-items: center; gap: 10px; padding: 12px 14px;
  background: rgba(255,255,255,0.025); border: 1px solid rgba(96,165,250,0.08);
  border-radius: 10px; text-decoration: none; color: inherit;
  transition: all 0.25s var(--ease-out); cursor: pointer;
  position: relative; overflow: hidden;
}
.connect-card::after {
  content: ''; position: absolute; inset: 0;
  background: linear-gradient(120deg, transparent, rgba(96,165,250,0.06), transparent);
  transform: translateX(-100%); transition: transform 0.5s var(--ease-out);
  pointer-events: none;
}
.connect-card:hover {
  background: rgba(255,255,255,0.05); border-color: var(--card-color, #60a5fa);
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(0,0,0,0.2), 0 0 20px var(--card-color, rgba(96,165,250,0.15));
}
.connect-card:hover::after {
  transform: translateX(100%);
}
.cc-icon { display: flex; color: rgba(255,255,255,0.5); transition: color 0.2s; }
.connect-card:hover .cc-icon { color: var(--card-color, #60a5fa); }
.cc-info { flex: 1; min-width: 0; }
.cc-name { display: block; font-size: 13px; font-weight: 600; color: rgba(255,255,255,0.8); }
.cc-handle { display: block; margin-top: 2px; font-size: 11px; color: rgba(255,255,255,0.35); }
.cc-arrow { color: rgba(255,255,255,0.25); transition: color 0.2s; }
.connect-card:hover .cc-arrow { color: var(--card-color, #60a5fa); }
.qr-card::before {
  content: 'SCAN';
  position: absolute;
  top: 8px;
  right: 10px;
  color: rgba(226, 239, 255, 0.38);
  font: 800 9px/1 'SF Mono', 'Consolas', monospace;
  letter-spacing: 0.12em;
}
.placeholder-card { opacity: 0.45; cursor: default; }
.placeholder-card:hover { transform: none; }

/* 底部签名 */
.resume-footer {
  margin-top: 20px; text-align: center;
  font-family: 'SF Mono', monospace; font-size: 13px; color: rgba(255,255,255,0.25);
}
.footer-bracket { color: rgba(96,165,250,0.35); }

/* 角标 */
.corner { position: absolute; width: 24px; height: 24px; pointer-events: none; }
.corner::before, .corner::after { content: ''; position: absolute; background: rgba(96,165,250,0.35); }
.corner.top-left { top: 10px; left: 10px; }
.corner.top-left::before { width: 12px; height: 1.5px; top: 0; left: 0; }
.corner.top-left::after { width: 1.5px; height: 12px; top: 0; left: 0; }
.corner.top-right { top: 10px; right: 10px; }
.corner.top-right::before { width: 12px; height: 1.5px; top: 0; right: 0; }
.corner.top-right::after { width: 1.5px; height: 12px; top: 0; right: 0; }
.corner.bottom-left { bottom: 10px; left: 10px; }
.corner.bottom-left::before { width: 12px; height: 1.5px; bottom: 0; left: 0; }
.corner.bottom-left::after { width: 1.5px; height: 12px; bottom: 0; left: 0; }
.corner.bottom-right { bottom: 10px; right: 10px; }
.corner.bottom-right::before { width: 12px; height: 1.5px; bottom: 0; right: 0; }
.corner.bottom-right::after { width: 1.5px; height: 12px; bottom: 0; right: 0; }

/* ======== 编辑弹窗 ======== */
.editor-overlay {
  position: fixed; inset: 0; background: rgba(0,0,0,0.75);
  display: flex; align-items: center; justify-content: center; z-index: 1000;
  backdrop-filter: blur(4px);
}
.editor-panel {
  background: rgba(12,20,35,0.98); border: 1px solid rgba(96,165,250,0.2);
  border-radius: 14px; padding: 24px; width: 90%; max-width: 440px; max-height: 85vh; overflow-y: auto;
}
.editor-panel h3 { font-size: 16px; color: #fff; margin: 0 0 16px; }
.editor-row { display: flex; flex-direction: column; gap: 4px; margin-bottom: 12px; }
.editor-row label { font-size: 11px; color: rgba(255,255,255,0.5); text-transform: uppercase; letter-spacing: 0.05em; }
.editor-row input, .editor-row textarea {
  padding: 8px 10px; background: rgba(0,0,0,0.3); border: 1px solid rgba(255,255,255,0.1);
  border-radius: 6px; color: #fff; font-size: 13px; outline: none; font-family: inherit; resize: vertical;
}
.editor-row input:focus, .editor-row textarea:focus { border-color: rgba(96,165,250,0.4); }
.editor-json { font-family: 'JetBrains Mono', 'Consolas', monospace !important; font-size: 12px !important; }
.editor-actions { display: flex; gap: 10px; justify-content: flex-end; margin-top: 4px; }
.editor-cancel { padding: 6px 16px; background: none; border: 1px solid rgba(255,255,255,0.2); border-radius: 6px; color: #888; font-size: 13px; cursor: pointer; }
.editor-save { padding: 6px 20px; background: #60a5fa; border: none; border-radius: 6px; color: #0c1423; font-size: 13px; font-weight: 600; cursor: pointer; }
.editor-save:disabled { opacity: 0.5; }

/* ======== 二维码弹窗 ======== */
.qr-modal { position: fixed; inset: 0; background: rgba(0,0,0,0.8); display: flex; align-items: center; justify-content: center; z-index: 1001; }
.qr-content { background: linear-gradient(135deg, rgba(17,27,49,0.95), rgba(8,14,27,0.95)); border: 1px solid rgba(96,165,250,0.3); border-radius: 10px; padding: 32px; text-align: center; position: relative; max-width: 360px; width: min(100%, 360px); box-shadow: 0 0 40px rgba(96,165,250,0.2); }
.qr-close { position: absolute; top: 12px; right: 16px; background: none; border: none; color: rgba(255,255,255,0.6); font-size: 24px; cursor: pointer; }
.qr-close:hover { color: #fff; }
.qr-title { font-size: 18px; font-weight: 600; color: #fff; margin: 0 0 20px; }
.qr-image {
  width: min(260px, 74vw);
  height: min(260px, 74vw);
  object-fit: contain;
  border-radius: 10px;
  margin-bottom: 16px;
  padding: 10px;
  background: #fff;
  box-shadow: 0 18px 42px rgba(0,0,0,0.28);
}
.qr-tip { font-size: 14px; color: rgba(255,255,255,0.6); margin: 0; }

/* ======== 响应式 ======== */
@media (max-width: 720px) {
  .about-page { padding: 72px 14px 80px; }
  .resume-panel { grid-template-columns: 1fr; }
  .resume-sidebar { border-right: none; border-bottom: 1px solid rgba(96,165,250,0.1); padding: 28px 20px 20px; }
  .resume-main { padding: 20px; }
  .connect-grid { grid-template-columns: 1fr; }
  .skill-label { width: 65px; font-size: 11px; }
}
</style>
