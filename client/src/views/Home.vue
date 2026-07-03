<template>
  <div class="home-page">
    <!-- 背景装饰 -->
    <div class="bg-grid" aria-hidden="true"></div>
    <div class="bg-glow" aria-hidden="true"></div>
    
    <div class="home-dashboard">
      <!-- 左侧：个人控制台 -->
      <section class="hero-console reveal-up">
        <!-- 顶部状态栏 -->
        <div class="console-header">
          <div class="status-indicator">
            <span class="status-dot"></span>
            <span class="status-text">SYSTEM ONLINE</span>
          </div>
          <div class="console-time">{{ currentTime }}</div>
        </div>

        <!-- 主内容区 -->
        <div class="console-body">
          <div class="hero-greeting">
            <span class="greeting-label">WELCOME TO</span>
            <h1 class="hero-title">
              <span class="title-main">{{ siteConfig.name }}</span>
              <span class="title-lab">Future Lab</span>
            </h1>
          </div>

          <p class="hero-subtitle">{{ siteConfig.subtitle }}</p>

          <!-- 快捷操作 -->
          <div class="hero-actions">
            <router-link to="/posts" class="action-btn primary">
              <span class="btn-icon">◈</span>
              <span>文章系统</span>
            </router-link>
            <router-link to="/projects" class="action-btn">
              <span class="btn-icon">◇</span>
              <span>项目矩阵</span>
            </router-link>
            <router-link to="/about" class="action-btn">
              <span class="btn-icon">○</span>
              <span>个人档案</span>
            </router-link>
          </div>

          <!-- 数据面板 -->
          <div class="data-panel">
            <div class="data-row">
              <div v-for="item in heroStats" :key="item.label" class="data-cell">
                <span class="data-value">{{ item.value }}</span>
                <span class="data-label">{{ item.label }}</span>
              </div>
            </div>
            <div class="data-bar">
              <div class="bar-fill" :style="{ width: '78%' }"></div>
              <span class="bar-text">SYSTEM LOAD</span>
            </div>
          </div>
        </div>

        <!-- 底部社交 -->
        <div class="console-footer">
          <div class="social-grid">
            <a
              v-for="social in siteConfig.socials.slice(0, 6)"
              :key="social.name"
              :href="social.url"
              target="_blank"
              rel="noopener noreferrer"
              class="social-node"
              :style="{ '--node-color': social.color }"
              :title="social.name"
            >
              <span v-html="getSocialIcon(social.icon)"></span>
            </a>
          </div>
        </div>

        <!-- 装饰角标 -->
        <div class="corner-decor top-left" aria-hidden="true"></div>
        <div class="corner-decor top-right" aria-hidden="true"></div>
        <div class="corner-decor bottom-left" aria-hidden="true"></div>
        <div class="corner-decor bottom-right" aria-hidden="true"></div>
      </section>

      <!-- 右侧：视觉展示区 -->
      <section class="visual-command reveal-up" style="animation-delay: 120ms">
        <!-- 视觉面板 -->
        <div class="visual-panel">
          <div class="panel-header">
            <div class="header-tabs">
              <span class="tab active">VISUAL_FEED</span>
              <span class="tab">TERMINAL</span>
            </div>
            <div class="header-counter">
              {{ String(currentPhoto + 1).padStart(2, '0') }} / {{ String(siteConfig.photos.length).padStart(2, '0') }}
            </div>
          </div>

          <!-- 轮播区域 -->
          <div
            class="carousel-stage"
            @mouseenter="stopCarousel"
            @mouseleave="startCarousel"
            @click="handlePhotoClick"
          >
            <div class="stage-frame">
              <div
                class="frame-backdrop"
                :style="{ backgroundImage: activePhoto ? `url(${activePhoto})` : 'none' }"
                aria-hidden="true"
              ></div>
              <div ref="photoInnerRef" class="frame-content">
                <img
                  v-for="(photo, i) in siteConfig.photos"
                  :key="i"
                  :src="photo"
                  :alt="`Visual ${i + 1}`"
                  class="carousel-img"
                  :class="{ active: i === currentPhoto }"
                />
              </div>
              <!-- 扫描线效果 -->
              <div class="scan-overlay" aria-hidden="true"></div>
            </div>

            <!-- 控制点 -->
            <div class="carousel-controls">
              <button
                v-for="(_, i) in siteConfig.photos"
                :key="i"
                class="control-dot"
                :class="{ active: i === currentPhoto }"
                :aria-label="`Photo ${i + 1}`"
                @click.stop="selectPhoto(i)"
              ></button>
            </div>
          </div>

          <!-- 底部信息条 -->
          <div class="panel-footer">
            <div class="footer-info">
              <span class="info-label">MODE</span>
              <span class="info-value">STATIC_DEPLOY</span>
            </div>
            <div class="footer-info">
              <span class="info-label">PLATFORM</span>
              <span class="info-value">GitHub Pages</span>
            </div>
            <div class="footer-status">
              <span class="status-pulse"></span>
              <span>ACTIVE</span>
            </div>
          </div>
        </div>

        <!-- 快捷预览卡片 -->
        <div class="preview-cards">
          <router-link to="/posts" class="preview-card">
            <div class="card-icon">☷</div>
            <div class="card-info">
              <span class="card-title">最新文章</span>
              <span class="card-count">{{ siteConfig.content.articles?.length || 0 }} 篇</span>
            </div>
          </router-link>
          <router-link to="/projects" class="preview-card">
            <div class="card-icon">◇</div>
            <div class="card-info">
              <span class="card-title">项目展示</span>
              <span class="card-count">{{ siteConfig.projects?.length || 0 }} 个</span>
            </div>
          </router-link>
        </div>
      </section>
    </div>

    <!-- 滚动提示 -->
    <router-link to="/posts" class="scroll-hint" aria-label="View articles">
      <div class="hint-line"></div>
      <span class="hint-text">SCROLL</span>
    </router-link>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, onUnmounted } from 'vue'
import siteConfig from '../config/site.config.js'

const siteConfigData = siteConfig
const currentPhoto = ref(0)
const photoInnerRef = ref(null)
const currentTime = ref('')
let carouselTimer = null
let timeTimer = null

const heroStats = computed(() => [
  { value: siteConfig.content.articles?.length || 0, label: 'ARTICLES' },
  { value: siteConfig.projects?.length || 0, label: 'PROJECTS' },
  { value: siteConfig.socials?.length || 0, label: 'CONNECTIONS' },
  { value: siteConfig.about?.skills?.length || 0, label: 'SKILLS' },
])

const activePhoto = computed(() => siteConfigData.photos[currentPhoto.value] || siteConfigData.photos[0] || '')

// 更新时间
const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleTimeString('en-US', { 
    hour12: false, 
    hour: '2-digit', 
    minute: '2-digit',
    second: '2-digit'
  })
}

// 触发切换动画
const triggerTransition = () => {
  if (!photoInnerRef.value) return
  photoInnerRef.value.classList.remove('transitioning')
  void photoInnerRef.value.offsetWidth
  photoInnerRef.value.classList.add('transitioning')
}

// 自动轮播
const startCarousel = () => {
  if (siteConfigData.photos.length <= 1 || carouselTimer) return
  carouselTimer = setInterval(() => {
    currentPhoto.value = (currentPhoto.value + 1) % siteConfigData.photos.length
    triggerTransition()
  }, 5000)
}

const stopCarousel = () => {
  if (carouselTimer) {
    clearInterval(carouselTimer)
    carouselTimer = null
  }
}

const handlePhotoClick = () => {
  currentPhoto.value = (currentPhoto.value + 1) % siteConfigData.photos.length
  triggerTransition()
}

const selectPhoto = (index) => {
  currentPhoto.value = index
  triggerTransition()
  stopCarousel()
  startCarousel()
}

// 社交图标
const getSocialIcon = (icon) => {
  const icons = {
    github: '<svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor"><path d="M12 2A10 10 0 0 0 2 12c0 4.42 2.87 8.17 6.84 9.5.5.08.66-.23.66-.5v-1.69c-2.77.6-3.36-1.34-3.36-1.34-.46-1.16-1.11-1.47-1.11-1.47-.91-.62.07-.6.07-.6 1 .07 1.53 1.03 1.53 1.03.87 1.52 2.34 1.07 2.91.83.09-.65.35-1.09.63-1.34-2.22-.25-4.55-1.11-4.55-4.92 0-1.11.38-2 1.03-2.71-.1-.25-.45-1.29.1-2.64 0 0 .84-.27 2.75 1.02.79-.22 1.65-.33 2.5-.33s1.71.11 2.5.33c1.91-1.29 2.75-1.02 2.75-1.02.55 1.35.2 2.39.1 2.64.65.71 1.03 1.6 1.03 2.71 0 3.82-2.34 4.66-4.57 4.91.36.31.69.92.69 1.85V21c0 .27.16.59.67.5C19.14 20.16 22 16.42 22 12A10 10 0 0 0 12 2z"/></svg>',
    bilibili: '<svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor"><path d="M17.813 4.653h.854c1.51.054 2.769.578 3.773 1.574 1.004.995 1.524 2.249 1.56 3.76v7.36c-.036 1.51-.556 2.769-1.56 3.773s-2.262 1.524-3.773 1.56H5.333c-1.51-.036-2.769-.556-3.773-1.56S.036 18.858 0 17.347v-7.36c.036-1.511.556-2.765 1.56-3.76 1.004-.996 2.262-1.52 3.773-1.574h.774l-1.174-1.12a1.234 1.234 0 0 1-.373-.906c0-.356.124-.659.373-.907l.027-.027c.267-.249.573-.373.92-.373.347 0 .653.124.92.373L9.653 4.44c.071.071.134.142.187.213h4.267a.836.836 0 0 1 .16-.213l2.853-2.747c.267-.249.573-.373.92-.373.347 0 .662.151.929.4.267.249.391.551.391.907 0 .355-.124.657-.373.906zM5.333 7.24c-.746.018-1.373.276-1.88.773-.506.498-.769 1.13-.786 1.894v7.52c.017.764.28 1.395.786 1.893.507.498 1.134.756 1.88.773h13.334c.746-.017 1.373-.275 1.88-.773.506-.498.769-1.129.786-1.893v-7.52c-.017-.765-.28-1.396-.786-1.894-.507-.497-1.134-.755-1.88-.773zM8 11.107c.373 0 .684.124.933.373.25.249.383.569.4.96v1.173c-.017.391-.15.711-.4.96-.249.25-.56.374-.933.374s-.684-.125-.933-.374c-.25-.249-.383-.569-.4-.96V12.44c0-.373.129-.689.386-.947.258-.257.574-.386.947-.386zm8 0c.373 0 .684.124.933.373.25.249.383.569.4.96v1.173c-.017.391-.15.711-.4.96-.249.25-.56.374-.933.374s-.684-.125-.933-.374c-.25-.249-.383-.569-.4-.96V12.44c.017-.391.15-.711.4-.96.249-.249.56-.373.933-.373z"/></svg>',
    zhihu: '<svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor"><path d="M5.721 0C2.251 0 0 2.25 0 5.719V18.28C0 21.751 2.252 24 5.721 24h12.56C21.751 24 24 21.75 24 18.281V5.72C24 2.249 21.75 0 18.281 0zm1.964 4.078c-.271.73-.5 1.434-.68 2.11h4.587c.545-.006.445 1.168.445 1.171H9.384a58.104 58.104 0 0 1-.112 3.797h2.726c.388.023.393 1.251.393 1.251H9.183c.062 1.191.328 2.559.837 4.106.388.116.774-.078.774-.078 0-.078-.291-1.07-.291-1.07a5.597 5.597 0 0 1 2.831.078c.097.485.155.95.184 1.398a3.29 3.29 0 0 1-.753.252c-.078.272-.35.932-.35.932l-.078.155c-.252.66-.524 1.282-.815 1.863a6.43 6.43 0 0 1-1.126 1.67 5.243 5.243 0 0 1-1.553 1.126c-.582.291-1.204.436-1.864.436-.66 0-1.282-.145-1.863-.436a5.244 5.244 0 0 1-1.554-1.126 6.43 6.43 0 0 1-1.126-1.67 7.768 7.768 0 0 1-.815-1.863l-.078-.155s-.272-.66-.35-.932a3.29 3.29 0 0 1-.753-.252c.029-.448.087-.913.184-1.398.66-.155 1.864-.078 2.831-.078 0 0-.252.992-.291 1.07 0 0 .388.194.774.078.51-1.547.776-2.915.837-4.106H4.077s.006-1.228.393-1.251h2.726a58.104 58.104 0 0 1-.112-3.797H4.077s-.1-1.177.445-1.171h4.587c-.18-.676-.409-1.38-.68-2.11z"/></svg>',
    weibo: '<svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor"><path d="M10.098 20.323c-3.977.391-7.414-1.406-7.672-4.02-.259-2.609 2.759-5.047 6.74-5.441 3.979-.394 7.413 1.404 7.671 4.018.259 2.6-2.759 5.049-6.737 5.439l-.002.004zM9.05 17.219c-.384.616-1.208.884-1.829.602-.612-.279-.793-.991-.406-1.593.379-.595 1.176-.861 1.793-.601.622.263.82.972.442 1.592zm1.27-1.627c-.141.237-.449.353-.689.253-.236-.09-.313-.361-.177-.586.138-.227.436-.346.672-.24.239.09.315.36.18.573h.014zm.176-2.719c-1.893-.493-4.033.45-4.857 2.118-.836 1.704-.026 3.591 1.886 4.21 1.983.64 4.318-.341 5.132-2.179.8-1.793-.201-3.642-2.161-4.149zm7.563-1.224c-.346-.105-.579-.18-.402-.649.386-1.031.425-1.922.008-2.557-.781-1.192-2.924-1.128-5.354-.034 0 0-.767.334-.571-.271.378-1.207.32-2.217-.266-2.8-1.331-1.32-4.869.047-7.91 3.052C1.834 10.411.124 12.695.124 14.555c0 3.557 4.567 5.72 9.031 5.72 5.847 0 9.737-3.397 9.737-6.093 0-1.631-1.377-2.553-2.813-2.913l.002-.002zm1.627-3.885c-.752-.856-1.864-1.291-3.031-1.291-.184 0-.369.015-.553.044l-.134.022-.038.134c-.059.214.067.437.281.496l.133.038c.151.038.306.058.462.058.793 0 1.531.295 2.078.833.548.537.84 1.257.822 2.028l-.007.135.134.038c.213.059.437-.067.496-.281l.038-.134c.029-.185.044-.369.044-.554 0-1.166-.435-2.278-1.291-3.031l-.434-.415z"/></svg>',
    douyin: '<svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor"><path d="M12.525.02c1.31-.02 2.61-.01 3.91-.02.08 1.53.63 3.09 1.75 4.17 1.12 1.11 2.7 1.62 4.24 1.79v4.03c-1.44-.05-2.89-.35-4.2-.97-.57-.26-1.1-.59-1.62-.93-.01 2.92.01 5.84-.02 8.75-.08 1.4-.54 2.79-1.35 3.94-1.31 1.92-3.58 3.17-5.91 3.21-1.43.08-2.86-.31-4.08-1.03-2.02-1.19-3.44-3.37-3.65-5.71-.02-.5-.03-1-.01-1.49.18-1.9 1.12-3.72 2.58-4.96 1.66-1.44 3.98-2.13 6.15-1.72.02 1.48-.04 2.96-.04 4.44-.99-.32-2.15-.23-3.02.37-.63.41-1.11 1.04-1.36 1.75-.21.51-.15 1.07-.14 1.61.24 1.64 1.82 3.02 3.5 2.87 1.12-.01 2.19-.66 2.77-1.61.19-.33.4-.67.41-1.06.1-1.79.06-3.57.07-5.36.01-4.03-.01-8.05.02-12.07z"/></svg>',
    xiaohongshu: '<svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm4.64 6.8c-.15 1.58-.8 5.42-1.13 7.19-.14.75-.42 1-.68 1.03-.58.05-1.02-.38-1.58-.75-.88-.58-1.38-.94-2.23-1.5-.99-.65-.35-1.01.22-1.59.15-.15 2.71-2.48 2.76-2.69a.2.2 0 0 0-.05-.18c-.06-.05-.14-.03-.21-.02-.09.02-1.49.95-4.22 2.79-.4.27-.76.41-1.08.4-.36-.01-1.04-.2-1.55-.37-.63-.2-1.12-.31-1.08-.66.02-.18.27-.36.74-.55 2.92-1.27 4.86-2.11 5.83-2.51 2.78-1.16 3.35-1.36 3.73-1.36.08 0 .27.02.39.12.1.08.13.19.14.27-.01.06.01.24 0 .38z"/></svg>',
    telegram: '<svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor"><path d="M11.944 0A12 12 0 0 0 0 12a12 12 0 0 0 12 12 12 12 0 0 0 12-12A12 12 0 0 0 12 0a12 12 0 0 0-.056 0zm4.962 7.224c.1-.002.321.023.465.14a.506.506 0 0 1 .171.325c.016.093.036.306.02.472-.18 1.898-.962 6.502-1.36 8.627-.168.9-.499 1.201-.82 1.23-.696.065-1.225-.46-1.9-.902-1.056-.693-1.653-1.124-2.678-1.8-1.185-.78-.417-1.21.258-1.91.177-.184 3.247-2.977 3.307-3.23.007-.032.014-.15-.056-.212s-.174-.041-.249-.024c-.106.024-1.793 1.14-5.061 3.345-.48.33-.913.49-1.302.48-.428-.008-1.252-.241-1.865-.44-.752-.245-1.349-.374-1.297-.789.027-.216.325-.437.893-.663 3.498-1.524 5.83-2.529 6.998-3.014 3.332-1.386 4.025-1.627 4.476-1.635z"/></svg>',
    email: '<svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor"><path d="M20 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 4l-8 5-8-5V6l8 5 8-5v2z"/></svg>',
    twitter: '<svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor"><path d="M18.244 2.25h3.308l-7.227 8.26 8.502 11.24H16.17l-5.214-6.817L4.99 21.75H1.68l7.73-8.835L1.254 2.25H8.08l4.713 6.231zm-1.161 17.52h1.833L7.084 4.126H5.117z"/></svg>',
    gitlab: '<span class="social-letter">GL</span>',
    gitee: '<span class="social-letter">GE</span>',
    csdn: '<span class="social-letter">CN</span>',
  }
  return icons[icon] || '<span class="social-letter">--</span>'
}

onMounted(() => {
  startCarousel()
  updateTime()
  timeTimer = setInterval(updateTime, 1000)
})

onUnmounted(() => {
  stopCarousel()
  if (timeTimer) clearInterval(timeTimer)
})
</script>

<style scoped>
.home-page {
  position: relative;
  min-height: 100vh;
  padding: 80px 24px 100px;
  overflow: hidden;
}

/* 背景网格 */
.bg-grid {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 0;
  background-image: 
    linear-gradient(rgba(56, 248, 255, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(56, 248, 255, 0.03) 1px, transparent 1px);
  background-size: 60px 60px;
  mask-image: radial-gradient(ellipse at center, black 0%, transparent 70%);
}

.bg-glow {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 0;
  background: 
    radial-gradient(ellipse 80% 50% at 20% 40%, rgba(56, 248, 255, 0.08) 0%, transparent 50%),
    radial-gradient(ellipse 60% 40% at 80% 60%, rgba(155, 92, 255, 0.06) 0%, transparent 50%);
}

/* 主布局 */
.home-dashboard {
  position: relative;
  z-index: 1;
  max-width: 1400px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: minmax(400px, 1fr) minmax(450px, 1.1fr);
  gap: 32px;
  align-items: start;
}

/* 左侧控制台 */
.hero-console {
  position: relative;
  background: 
    linear-gradient(135deg, rgba(12, 20, 35, 0.95) 0%, rgba(8, 12, 22, 0.98) 100%);
  border: 1px solid rgba(56, 248, 255, 0.2);
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 
    0 0 0 1px rgba(56, 248, 255, 0.05),
    0 25px 50px -12px rgba(0, 0, 0, 0.5),
    inset 0 1px 0 rgba(255, 255, 255, 0.05);
}

/* 角标装饰 */
.corner-decor {
  position: absolute;
  width: 40px;
  height: 40px;
  pointer-events: none;
}

.corner-decor::before,
.corner-decor::after {
  content: '';
  position: absolute;
  background: rgba(56, 248, 255, 0.4);
}

.corner-decor.top-left {
  top: 12px;
  left: 12px;
}
.corner-decor.top-left::before {
  width: 20px;
  height: 2px;
  top: 0;
  left: 0;
}
.corner-decor.top-left::after {
  width: 2px;
  height: 20px;
  top: 0;
  left: 0;
}

.corner-decor.top-right {
  top: 12px;
  right: 12px;
}
.corner-decor.top-right::before {
  width: 20px;
  height: 2px;
  top: 0;
  right: 0;
}
.corner-decor.top-right::after {
  width: 2px;
  height: 20px;
  top: 0;
  right: 0;
}

.corner-decor.bottom-left {
  bottom: 12px;
  left: 12px;
}
.corner-decor.bottom-left::before {
  width: 20px;
  height: 2px;
  bottom: 0;
  left: 0;
}
.corner-decor.bottom-left::after {
  width: 2px;
  height: 20px;
  bottom: 0;
  left: 0;
}

.corner-decor.bottom-right {
  bottom: 12px;
  right: 12px;
}
.corner-decor.bottom-right::before {
  width: 20px;
  height: 2px;
  bottom: 0;
  right: 0;
}
.corner-decor.bottom-right::after {
  width: 2px;
  height: 20px;
  bottom: 0;
  right: 0;
}

/* 控制台头部 */
.console-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid rgba(56, 248, 255, 0.1);
  background: rgba(0, 0, 0, 0.2);
}

.status-indicator {
  display: flex;
  align-items: center;
  gap: 8px;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #4ade80;
  box-shadow: 0 0 10px #4ade80;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.status-text {
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.1em;
  color: rgba(255, 255, 255, 0.6);
}

.console-time {
  font-family: 'SF Mono', monospace;
  font-size: 12px;
  color: rgba(56, 248, 255, 0.8);
  letter-spacing: 0.05em;
}

/* 控制台主体 */
.console-body {
  padding: 32px 24px;
}

.hero-greeting {
  margin-bottom: 16px;
}

.greeting-label {
  display: block;
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.2em;
  color: rgba(56, 248, 255, 0.6);
  margin-bottom: 8px;
}

.hero-title {
  margin: 0;
  line-height: 1.1;
}

.title-main {
  display: block;
  font-size: clamp(2.5rem, 5vw, 4rem);
  font-weight: 700;
  background: linear-gradient(135deg, #fff 0%, #38f8ff 50%, #9b5cff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.title-lab {
  display: block;
  font-size: clamp(1rem, 2vw, 1.5rem);
  font-weight: 500;
  color: rgba(255, 255, 255, 0.5);
  letter-spacing: 0.1em;
  margin-top: 4px;
}

.hero-subtitle {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  line-height: 1.6;
  margin: 0 0 24px 0;
  max-width: 400px;
}

/* 操作按钮 */
.hero-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 32px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.2s ease;
  border: 1px solid rgba(56, 248, 255, 0.2);
  background: rgba(56, 248, 255, 0.05);
  color: rgba(255, 255, 255, 0.8);
}

.action-btn:hover {
  background: rgba(56, 248, 255, 0.1);
  border-color: rgba(56, 248, 255, 0.4);
  transform: translateY(-2px);
}

.action-btn.primary {
  background: linear-gradient(135deg, rgba(56, 248, 255, 0.2) 0%, rgba(155, 92, 255, 0.2) 100%);
  border-color: rgba(56, 248, 255, 0.4);
  color: #fff;
}

.action-btn.primary:hover {
  background: linear-gradient(135deg, rgba(56, 248, 255, 0.3) 0%, rgba(155, 92, 255, 0.3) 100%);
}

.btn-icon {
  font-size: 14px;
  opacity: 0.8;
}

/* 数据面板 */
.data-panel {
  background: rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(56, 248, 255, 0.1);
  border-radius: 12px;
  padding: 20px;
}

.data-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.data-cell {
  text-align: center;
}

.data-value {
  display: block;
  font-size: 24px;
  font-weight: 700;
  color: #38f8ff;
  line-height: 1;
  margin-bottom: 4px;
}

.data-label {
  font-size: 10px;
  font-weight: 600;
  letter-spacing: 0.1em;
  color: rgba(255, 255, 255, 0.4);
}

.data-bar {
  position: relative;
  height: 24px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 4px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #38f8ff 0%, #9b5cff 100%);
  border-radius: 4px;
  transition: width 0.5s ease;
}

.bar-text {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  font-weight: 600;
  letter-spacing: 0.1em;
  color: rgba(255, 255, 255, 0.8);
}

/* 控制台底部 */
.console-footer {
  padding: 20px 24px;
  border-top: 1px solid rgba(56, 248, 255, 0.1);
  background: rgba(0, 0, 0, 0.2);
}

.social-grid {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.social-node {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.05);
  color: rgba(255, 255, 255, 0.6);
  transition: all 0.2s ease;
  text-decoration: none;
}

.social-node:hover {
  border-color: var(--node-color, #38f8ff);
  color: var(--node-color, #38f8ff);
  background: rgba(255, 255, 255, 0.1);
  transform: translateY(-2px);
}

/* 右侧视觉区 */
.visual-command {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.visual-panel {
  background: 
    linear-gradient(135deg, rgba(12, 20, 35, 0.95) 0%, rgba(8, 12, 22, 0.98) 100%);
  border: 1px solid rgba(56, 248, 255, 0.2);
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 
    0 0 0 1px rgba(56, 248, 255, 0.05),
    0 25px 50px -12px rgba(0, 0, 0, 0.5);
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid rgba(56, 248, 255, 0.1);
  background: rgba(0, 0, 0, 0.2);
}

.header-tabs {
  display: flex;
  gap: 16px;
}

.tab {
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.1em;
  color: rgba(255, 255, 255, 0.4);
  cursor: pointer;
  transition: color 0.2s;
}

.tab.active {
  color: #38f8ff;
}

.header-counter {
  font-family: 'SF Mono', monospace;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
}

/* 轮播舞台 */
.carousel-stage {
  position: relative;
  padding: 20px;
  cursor: pointer;
}

.stage-frame {
  position: relative;
  aspect-ratio: 4/3;
  border-radius: 12px;
  overflow: hidden;
  background: rgba(0, 0, 0, 0.5);
}

.frame-backdrop {
  position: absolute;
  inset: -20%;
  background-size: cover;
  background-position: center;
  filter: blur(40px) brightness(0.5);
  opacity: 0.6;
}

.frame-content {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.carousel-img {
  max-width: 85%;
  max-height: 85%;
  object-fit: contain;
  opacity: 0;
  transform: scale(0.95);
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
  position: absolute;
  filter: drop-shadow(0 20px 40px rgba(0, 0, 0, 0.5));
}

.carousel-img.active {
  opacity: 1;
  transform: scale(1);
}

.frame-content.transitioning .carousel-img.active {
  animation: imagePulse 0.5s ease;
}

@keyframes imagePulse {
  0% { transform: scale(0.95); }
  50% { transform: scale(1.02); }
  100% { transform: scale(1); }
}

.scan-overlay {
  position: absolute;
  inset: 0;
  background: repeating-linear-gradient(
    0deg,
    transparent,
    transparent 2px,
    rgba(56, 248, 255, 0.03) 2px,
    rgba(56, 248, 255, 0.03) 4px
  );
  pointer-events: none;
}

/* 轮播控制 */
.carousel-controls {
  display: flex;
  justify-content: center;
  gap: 8px;
  padding: 16px 20px;
}

.control-dot {
  width: 24px;
  height: 4px;
  border-radius: 2px;
  border: none;
  background: rgba(255, 255, 255, 0.2);
  cursor: pointer;
  transition: all 0.2s ease;
}

.control-dot.active {
  width: 40px;
  background: linear-gradient(90deg, #38f8ff, #9b5cff);
}

.control-dot:hover {
  background: rgba(255, 255, 255, 0.4);
}

/* 面板底部 */
.panel-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  border-top: 1px solid rgba(56, 248, 255, 0.1);
  background: rgba(0, 0, 0, 0.2);
}

.footer-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.info-label {
  font-size: 9px;
  font-weight: 600;
  letter-spacing: 0.1em;
  color: rgba(255, 255, 255, 0.4);
}

.info-value {
  font-size: 11px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.7);
}

.footer-status {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 11px;
  font-weight: 600;
  color: #4ade80;
}

.status-pulse {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #4ade80;
  box-shadow: 0 0 8px #4ade80;
  animation: pulse 2s ease-in-out infinite;
}

/* 预览卡片 */
.preview-cards {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.preview-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: rgba(12, 20, 35, 0.8);
  border: 1px solid rgba(56, 248, 255, 0.15);
  border-radius: 12px;
  text-decoration: none;
  transition: all 0.2s ease;
}

.preview-card:hover {
  background: rgba(12, 20, 35, 0.95);
  border-color: rgba(56, 248, 255, 0.3);
  transform: translateY(-2px);
}

.card-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #38f8ff;
  background: rgba(56, 248, 255, 0.1);
  border-radius: 12px;
}

.card-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.card-title {
  font-size: 13px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.9);
}

.card-count {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.5);
}

/* 滚动提示 */
.scroll-hint {
  position: absolute;
  bottom: 40px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  text-decoration: none;
  opacity: 0.6;
  transition: opacity 0.2s;
}

.scroll-hint:hover {
  opacity: 1;
}

.hint-line {
  width: 1px;
  height: 40px;
  background: linear-gradient(to bottom, #38f8ff, transparent);
  animation: scrollLine 1.5s ease-in-out infinite;
}

@keyframes scrollLine {
  0%, 100% { transform: scaleY(1); opacity: 1; }
  50% { transform: scaleY(0.5); opacity: 0.5; }
}

.hint-text {
  font-size: 10px;
  font-weight: 600;
  letter-spacing: 0.2em;
  color: rgba(56, 248, 255, 0.6);
}

/* 动画 */
.reveal-up {
  animation: revealUp 0.6s cubic-bezier(0.4, 0, 0.2, 1) both;
}

@keyframes revealUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式 */
@media (max-width: 1024px) {
  .home-dashboard {
    grid-template-columns: 1fr;
  }
  
  .data-row {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .home-page {
    padding: 72px 16px 80px;
  }
  
  .hero-actions {
    flex-direction: column;
  }
  
  .action-btn {
    justify-content: center;
  }
  
  .preview-cards {
    grid-template-columns: 1fr;
  }
  
  .panel-footer {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
}
</style>