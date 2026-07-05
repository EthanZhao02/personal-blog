<template>
  <div class="souta-app" :style="pointerStyle" @click="dropCandy" @touchstart="dropCandy" @pointermove="trackPointer">
    <div class="cursor-aura" aria-hidden="true"></div>

    <div class="ambient-layer" aria-hidden="true">
      <span class="ambient-line line-a"></span>
      <span class="ambient-line line-b"></span>
      <span class="ambient-node node-one"></span>
      <span class="ambient-node node-two"></span>
      <span class="ambient-node node-three"></span>
      <span class="ambient-node node-four"></span>
    </div>

    <transition name="welcome">
      <section v-if="welcomeVisible" class="welcome-portal" aria-label="欢迎进入个人博客" @click.stop @touchstart.stop>
        <div class="portal-grid" aria-hidden="true"></div>
        <div class="welcome-stage" aria-hidden="true">
          <span class="stage-starfield"></span>
          <span class="stage-mesh"></span>
          <span class="stage-mountain mountain-back"></span>
          <span class="stage-mountain mountain-front"></span>
          <span class="stage-scanline"></span>
        </div>
        <div class="portal-core" aria-hidden="true">
          <span class="portal-ring ring-a"></span>
          <span class="portal-ring ring-b"></span>
          <span class="portal-ring ring-c"></span>
          <span class="portal-beam beam-a"></span>
          <span class="portal-beam beam-b"></span>
          <span class="portal-beam beam-c"></span>
          <span class="portal-node node-a"></span>
          <span class="portal-node node-b"></span>
          <span class="portal-node node-c"></span>
        </div>
        <div class="welcome-copy" :data-language="language">
          <span class="welcome-code">{{ welcomeCopy.code }}</span>
          <h1>{{ welcomeCopy.title }}</h1>
          <p>{{ welcomeCopy.desc }}</p>
          <div class="welcome-tech" aria-hidden="true">
            <span>Vue.js</span>
            <span>Spring Boot</span>
            <span>NLP</span>
            <span>Blender</span>
          </div>
          <div class="welcome-connect" :aria-label="welcomeCopy.connectLabel">
            <span class="connect-label">{{ welcomeCopy.connectLabel }}</span>
            <a
              v-for="item in welcomeConnectLinks"
              :key="item.name"
              :href="item.url"
              target="_blank"
              rel="noopener"
            >
              {{ item.name }}
            </a>
          </div>
          <div class="welcome-progress" aria-hidden="true"><span></span></div>
          <button class="welcome-enter" type="button" @click="enterWelcome">
            <span>{{ welcomeCopy.action }}</span>
            <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M5 12h14m-6-6 6 6-6 6"/></svg>
          </button>
        </div>
        <button class="welcome-skip" type="button" @click="enterWelcome">SKIP</button>
      </section>
    </transition>

    <transition name="time-gate">
      <section v-if="showTimeGate" class="time-gate-overlay" @click.self="closeTimeGate" @touchstart.stop>
        <div class="time-gate-panel">
          <button class="time-gate-close" type="button" @click="closeTimeGate" :aria-label="timeGateCopy.close">×</button>
          <div class="time-gate-orbit" aria-hidden="true">
            <span></span>
            <span></span>
            <span></span>
          </div>
          <div class="time-gate-copy">
            <span class="time-gate-code">{{ timeGateCopy.code }}</span>
            <h2>{{ timeGateCopy.title }}</h2>
            <p>{{ timeGateCopy.desc }}</p>
            <div class="time-gate-path">
              <span v-for="item in timeGateCopy.nodes" :key="item">{{ item }}</span>
            </div>
            <button class="time-gate-action" type="button" @click="enterTimeGate">
              {{ timeGateCopy.action }}
            </button>
          </div>
        </div>
      </section>
    </transition>

    <!-- 特效容器 -->
    <div class="candy-container" aria-hidden="true">
      <span
        v-for="candy in candies"
        :key="candy.id"
        class="candy"
        :style="{
          left: candy.x + 'px',
          top: candy.y + 'px',
          animationDuration: candy.dur + 's',
          animationDelay: candy.delay + 's',
          '--particle-size': candy.size + 'px',
          opacity: candy.opacity,
          color: candy.color,
          '--dx': candy.dx + 'px',
          '--dy': candy.dy + 'px',
          '--rot': candy.rot + 'deg'
        }"
      ></span>
    </div>

    <!-- 顶部固定导航 -->
    <header class="souta-nav" @click.stop @touchstart.stop>
      <nav class="souta-nav-inner" @click.stop @touchstart.stop>
        <router-link to="/" class="nav-brand" aria-label="Ethan 智域">
          <strong>Ethan</strong>
          <span>智域</span>
        </router-link>

        <div class="nav-links" aria-label="Primary navigation">
          <router-link
            v-for="link in navLinks"
            :key="link.path"
            :to="link.path"
            class="nav-item"
            :class="{ active: isActive(link.path) }"
          >
            <span class="nav-icon" aria-hidden="true">{{ link.icon }}</span>
            <span>{{ link.name }}</span>
          </router-link>
        </div>

        <div class="nav-tools" aria-label="Site utilities">
          <button class="nav-tool" type="button" :title="languageTitle" @click="toggleLanguage">{{ languageLabel }}</button>
          <button class="nav-tool" type="button" :title="themeTitle" @click="toggleTheme">{{ themeGlyph }}</button>
          <a class="nav-tool" :href="rssUrl" target="_blank" rel="noopener" title="RSS 订阅">RSS</a>
        </div>
      </nav>
    </header>

    <!-- 页面内容区 -->
    <main class="souta-main">
      <router-view v-slot="{ Component }">
        <transition name="page" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <!-- 页脚 -->
    <footer class="souta-footer">
      <p>Copyright © 2019-2026 Ethan All Rights Reserved.</p>
      <!-- 隐藏管理入口：访客几乎不会注意到，博主自己用 -->
      <div class="souta-footer-admin">
        <template v-if="userStore.isLoggedIn">
          <router-link to="/write" class="footer-admin-link">
            <span>✎</span> 写文章
          </router-link>
          <span class="footer-admin-sep">·</span>
          <span class="footer-admin-link" @click="handleLogout">退出</span>
        </template>
        <template v-else>
          <router-link to="/login" class="footer-admin-link footer-admin-login" :title="authNavLabel">
            <span>⚙</span>
          </router-link>
        </template>
      </div>
      <div class="future-rail-links">
        <button type="button" @click="openTimeGate('past')">{{ railCopy.past }}</button>
        <button type="button" @click="openTimeGate('future')">{{ railCopy.future }}</button>
        <a :href="rssUrl" target="_blank" rel="noopener">{{ railCopy.rss }}</a>
      </div>
      <div class="site-telemetry" :title="statsCopy.title">
        <span>{{ statsCopy.pv }} <strong>{{ formatStat(siteStats.pageViews) }}</strong></span>
        <span>{{ statsCopy.uv }} <strong>{{ formatStat(siteStats.uniqueVisitors) }}</strong></span>
      </div>
    </footer>

    <AIGuide />
  </div>
</template>

<script setup>
import { computed, provide, ref, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useRoute } from 'vue-router'
import { useUserStore } from './stores/user'
import { isStaticMode, visibleSocials } from './config/site.config'
import { getSiteStats, recordSiteVisit } from './api/siteStats'
import AIGuide from './components/AIGuide.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const pointer = ref({ x: '50vw', y: '40vh' })
const language = ref(localStorage.getItem('ethan-language') || 'zh')
const themeMode = ref(localStorage.getItem('ethan-theme') || 'dark')
const showWelcome = ref(false)
const showTimeGate = ref(false)
const timeGateMode = ref('future')
const siteStats = ref({ pageViews: null, uniqueVisitors: null })
const scrollProgress = ref(0)
let welcomeTimer = null
let lastTrackedPath = ''
let scrollFrame = null

provide('siteLanguage', language)

const pointerStyle = computed(() => ({
  '--cursor-x': pointer.value.x,
  '--cursor-y': pointer.value.y,
  '--scroll-progress': scrollProgress.value.toFixed(4),
  '--scroll-depth': `${Math.round(scrollProgress.value * 120)}px`,
  '--scroll-drift': `${Math.round(scrollProgress.value * -80)}px`,
}))

const baseUrl = (import.meta.env.BASE_URL || '/').replace(/\/$/, '')
const rssUrl = computed(() => `${baseUrl || ''}/rss.xml`)
const welcomeConnectLinks = computed(() => {
  const preferred = ['GitHub', '邮箱', '语雀']
  const socials = preferred
    .map(name => visibleSocials.find(item => item.name === name))
    .filter(Boolean)
    .map(item => ({ name: item.name === '邮箱' && language.value === 'en' ? 'Email' : item.name, url: item.url }))

  return [
    ...socials,
    { name: 'RSS', url: rssUrl.value },
  ]
})

const navText = {
  zh: [
    { name: '首页', path: '/', icon: '⌂' },
    { name: '文章', path: '/posts', icon: '☷' },
    { name: '项目', path: '/projects', icon: '◇' },
    { name: '友链', path: '/friends', icon: '∞' },
    { name: '留言板', path: '/message', icon: '✎' },
    { name: '关于', path: '/about', icon: '○' },
  ],
  en: [
    { name: 'Home', path: '/', icon: '⌂' },
    { name: 'Posts', path: '/posts', icon: '☷' },
    { name: 'Projects', path: '/projects', icon: '◇' },
    { name: 'Friends', path: '/friends', icon: '∞' },
    { name: 'Board', path: '/message', icon: '✎' },
    { name: 'About', path: '/about', icon: '○' },
  ],
}

const navLinks = computed(() => navText[language.value] || navText.zh)
const languageLabel = computed(() => (language.value === 'zh' ? 'EN' : '中'))
const languageTitle = computed(() => (language.value === 'zh' ? 'Switch to English' : '切换到中文'))
const themeGlyph = computed(() => (themeMode.value === 'dark' ? 'LOW' : 'DEEP'))
const themeTitle = computed(() => (themeMode.value === 'dark' ? '切换到柔和深空模式' : '切换到高能深空模式'))
const welcomeVisible = computed(() => showWelcome.value && route.path === '/')
const welcomeCopy = computed(() => language.value === 'zh'
  ? {
      code: 'VL-INTERACTIF / ETHAN BLOG BOOT',
      title: '欢迎来到我的博客',
      desc: '正在接入文章、项目、生命轨迹与 AI 数字档案，准备进入 Ethan 的个人技术空间。',
      action: '进入博客',
      connectLabel: '连接入口',
    }
  : {
      code: 'VL-INTERACTIF / ETHAN BLOG BOOT',
      title: 'Welcome to My Blog',
      desc: 'Connecting articles, projects, life timeline, and AI archive before entering Ethan personal tech space.',
      action: 'Enter Blog',
      connectLabel: 'Connect',
    })

const railCopy = computed(() => language.value === 'zh'
  ? { past: '开往过去', future: '开往未来', rss: 'RSS 订阅' }
  : { past: 'Past Gate', future: 'Future Gate', rss: 'RSS' })

const statsCopy = computed(() => language.value === 'zh'
  ? { pv: '访问', uv: '访客', title: '站点访问统计：访问次数 / 独立访客' }
  : { pv: 'PV', uv: 'UV', title: 'Site traffic: page views / unique visitors' })

const timeGateCopy = computed(() => {
  const isPast = timeGateMode.value === 'past'
  if (language.value === 'en') {
    return isPast
      ? {
          code: 'TIME ROUTE / 2021-2024',
          title: 'Past Learning Archive',
          desc: 'Replaying the path from frontend basics, PHP and databases to backend languages and programming foundations.',
          nodes: ['Frontend', 'PHP / DB', 'Python', 'C / C++'],
          action: 'Open Archive',
          route: '/posts',
          close: 'Close time gate',
        }
      : {
          code: 'TIME ROUTE / 2025-2026',
          title: 'Future AI Route',
          desc: 'Entering the AI-facing world: NLP, intelligent tools, project experiments, and the next build log.',
          nodes: ['AI', 'NLP', 'Agents', 'Projects'],
          action: 'Enter Projects',
          route: '/projects',
          close: 'Close time gate',
        }
  }

  return isPast
    ? {
        code: 'TIME ROUTE / 2021-2024',
        title: '开往过去：学习档案',
        desc: '回放从前端、PHP、数据库，到 Python、C/C++ 与后端基础的成长轨迹，不再只是跳到文章列表。',
        nodes: ['前端入门', 'PHP / 数据库', '后端语言', '阶段复盘'],
        action: '查看学习档案',
        route: '/posts',
        close: '关闭时空航线',
      }
    : {
        code: 'TIME ROUTE / 2025-2026',
        title: '开往未来：AI 航线',
        desc: '进入 AI / NLP、智能工具和项目实验的下一层世界，像进入新空间一样看作品与计划。',
        nodes: ['AI', 'NLP', '智能工具', '项目实践'],
        action: '进入未来项目',
        route: '/projects',
        close: '关闭时空航线',
      }
})

const handleLogout = () => {
  if (!confirm('确定要退出登录吗？')) return
  userStore.logout()
  router.push('/login')
}

const trackPointer = (event) => {
  pointer.value = {
    x: `${event.clientX}px`,
    y: `${event.clientY}px`,
  }
}

const candies = ref([])
let candyId = 0

const authNavLabel = computed(() => isStaticMode ? '账号说明' : '登录')

const isActive = (path) => {
  if (path === '/') return route.path === '/'
  return route.path.startsWith(path)
}

// 光点特效配置
const DUST_COLORS = ['#60a5fa', '#7c3aed', '#93c5fd', '#ffbd66', '#ff4fd8']

const toggleLanguage = () => {
  language.value = language.value === 'zh' ? 'en' : 'zh'
}

const toggleTheme = () => {
  themeMode.value = themeMode.value === 'dark' ? 'light' : 'dark'
}

const applyTheme = () => {
  document.documentElement.dataset.theme = themeMode.value
}

const enterWelcome = () => {
  showWelcome.value = false
  if (welcomeTimer) {
    clearTimeout(welcomeTimer)
    welcomeTimer = null
  }
}

const openTimeGate = (mode) => {
  timeGateMode.value = mode
  showTimeGate.value = true
}

const closeTimeGate = () => {
  showTimeGate.value = false
}

const enterTimeGate = () => {
  const target = timeGateCopy.value.route
  showTimeGate.value = false
  router.push(target)
}

const formatStat = (value) => {
  if (value === null || value === undefined || value === '') return '--'
  const number = Number(value)
  return Number.isFinite(number) ? number.toLocaleString('en-US') : '--'
}

const getVisitorId = () => {
  const key = 'ethan-visitor-id'
  const existing = localStorage.getItem(key)
  if (existing) return existing
  const next = window.crypto?.randomUUID
    ? window.crypto.randomUUID()
    : `visitor-${Date.now()}-${Math.random().toString(16).slice(2)}`
  localStorage.setItem(key, next)
  return next
}

const updateSiteStats = (res) => {
  if (res?.code === 200 && res.data) {
    siteStats.value = {
      pageViews: res.data.pageViews,
      uniqueVisitors: res.data.uniqueVisitors,
    }
  }
}

const refreshSiteStats = async () => {
  updateSiteStats(await getSiteStats())
}

const trackSiteVisit = async (path) => {
  if (lastTrackedPath === path) return
  lastTrackedPath = path
  const res = await recordSiteVisit({
    visitorId: getVisitorId(),
    path,
  })
  if (res) {
    updateSiteStats(res)
  } else {
    await refreshSiteStats()
  }
}

const dropCandy = (e) => {
  // 排除导航、按钮、链接、输入框等交互元素
  const excludeSelectors = [
    '.souta-nav',
    '.nav-item',
    'button',
    'a',
    'input',
    'textarea',
    '.photo-wrapper',
    '.social-btn',
    '[contenteditable]',
    '.hero-wave',
    'router-link'
  ]
  
  if (excludeSelectors.some(sel => e.target.closest(sel))) {
    return
  }
  
  // 获取点击坐标（兼容触摸事件）
  const clientX = e.clientX || (e.touches && e.touches[0]?.clientX)
  const clientY = e.clientY || (e.touches && e.touches[0]?.clientY)
  if (!clientX || !clientY) return
  
  // 点击产生 5-9 个微粒
  const count = Math.floor(Math.random() * 5) + 5
  
  for (let i = 0; i < count; i++) {
    const id = ++candyId
    const x = clientX + (Math.random() - 0.5) * 26
    const y = clientY + (Math.random() - 0.5) * 18
    const dx = (Math.random() - 0.5) * 130
    const dy = -48 - Math.random() * 110
    const rot = (Math.random() - 0.5) * 220
    
    const color = DUST_COLORS[Math.floor(Math.random() * DUST_COLORS.length)]
    
    const dur = 0.95 + Math.random() * 0.75
    const delay = Math.random() * 0.08
    const size = 6 + Math.random() * 10
    const opacity = 0.45 + Math.random() * 0.55
    
    candies.value.push({ id, x, y, dx, dy, rot, color, dur, delay, size, opacity })
  }
  
  // 性能优化：限制最大数量
  if (candies.value.length > 60) {
    candies.value = candies.value.slice(-40)
  }
  
  // 自动清理过期微粒
  setTimeout(() => {
    candies.value = candies.value.filter(c => c.id > candyId - 40)
  }, 1800)
}

const startWelcomeIfHome = () => {
  if (route.path !== '/') return
  showWelcome.value = true
  welcomeTimer = window.setTimeout(enterWelcome, 8600)
}

const updateScrollProgress = () => {
  const doc = document.documentElement
  const max = Math.max(doc.scrollHeight - window.innerHeight, 1)
  scrollProgress.value = Math.min(Math.max(window.scrollY / max, 0), 1)
  scrollFrame = null
}

const handleScroll = () => {
  if (scrollFrame) return
  scrollFrame = window.requestAnimationFrame(updateScrollProgress)
}

onMounted(async () => {
  applyTheme()
  await router.isReady()
  updateScrollProgress()
  window.addEventListener('scroll', handleScroll, { passive: true })
  trackSiteVisit(route.fullPath)
  startWelcomeIfHome()

  setTimeout(() => {
    dropCandy({
      clientX: window.innerWidth / 2,
      clientY: Math.min(window.innerHeight * 0.42, 380),
      target: { closest: () => null }
    })
  }, 650)
})

watch(language, (value) => {
  localStorage.setItem('ethan-language', value)
})

watch(themeMode, (value) => {
  localStorage.setItem('ethan-theme', value)
  applyTheme()
}, { immediate: true })

watch(() => route.path, (path) => {
  if (path !== '/' && showWelcome.value) {
    enterWelcome()
  }
})

watch(() => route.fullPath, (path) => {
  trackSiteVisit(path)
})

onUnmounted(() => {
  if (welcomeTimer) clearTimeout(welcomeTimer)
  if (scrollFrame) cancelAnimationFrame(scrollFrame)
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.souta-app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow-x: hidden;
}

.cursor-aura {
  position: fixed;
  left: 0;
  top: 0;
  z-index: 2;
  width: 360px;
  height: 360px;
  pointer-events: none;
  border-radius: 999px;
  background:
    radial-gradient(circle, rgba(96, 165, 250, 0.18), rgba(124, 58, 237, 0.08) 38%, transparent 68%);
  filter: blur(18px);
  opacity: 0.72;
  mix-blend-mode: screen;
  transform: translate3d(calc(var(--cursor-x, 50vw) - 180px), calc(var(--cursor-y, 40vh) - 180px), 0);
  transition: transform 0.12s linear, opacity 0.2s;
}

.welcome-portal {
  position: fixed;
  inset: 0;
  z-index: 10000;
  display: grid;
  place-items: center;
  overflow: hidden;
  padding: 28px;
  background:
    radial-gradient(circle at 50% 44%, rgba(96, 165, 250, 0.24), transparent 30%),
    radial-gradient(circle at 52% 54%, rgba(124, 58, 237, 0.22), transparent 52%),
    linear-gradient(135deg, rgba(2, 6, 18, 0.99), rgba(8, 13, 35, 0.98));
}

.welcome-portal::before,
.welcome-portal::after {
  content: '';
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.welcome-portal::before {
  z-index: 0;
  background:
    radial-gradient(circle at 18% 18%, rgba(96, 165, 250, 0.16), transparent 26%),
    radial-gradient(circle at 82% 28%, rgba(167, 139, 250, 0.14), transparent 28%),
    radial-gradient(circle at 50% 100%, rgba(14, 165, 233, 0.14), transparent 38%);
  animation: stageGlow 8.2s ease-in-out infinite;
}

.welcome-portal::after {
  z-index: 4;
  background:
    linear-gradient(180deg, transparent 0 48%, rgba(147, 197, 253, 0.08) 49%, transparent 50% 100%),
    repeating-linear-gradient(0deg, rgba(255, 255, 255, 0.026) 0 1px, transparent 1px 5px);
  mix-blend-mode: screen;
  opacity: 0.62;
  animation: bootScan 3.6s linear infinite;
}

.portal-grid {
  position: absolute;
  z-index: 1;
  inset: 0;
  background:
    linear-gradient(90deg, rgba(147, 197, 253, 0.05) 1px, transparent 1px) 0 0 / 72px 72px,
    linear-gradient(0deg, rgba(147, 197, 253, 0.04) 1px, transparent 1px) 0 0 / 72px 72px,
    repeating-linear-gradient(115deg, transparent 0 12px, rgba(255, 255, 255, 0.025) 12px 13px);
  mask-image: radial-gradient(circle at 50% 50%, #000 0%, transparent 70%);
  animation: portalGrid 7s linear infinite;
}

.welcome-stage {
  position: absolute;
  inset: 0;
  z-index: 1;
  overflow: hidden;
  pointer-events: none;
  perspective: 900px;
}

.stage-starfield {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(circle, rgba(238, 247, 255, 0.72) 0 1px, transparent 1.6px) 10% 18% / 138px 138px,
    radial-gradient(circle, rgba(147, 197, 253, 0.6) 0 1px, transparent 1.6px) 0 0 / 220px 220px;
  opacity: 0.45;
  animation: starDrift 18s linear infinite;
}

.stage-mesh {
  position: absolute;
  left: -12%;
  right: -12%;
  bottom: -12%;
  height: 54%;
  background:
    linear-gradient(90deg, rgba(96, 165, 250, 0.18) 1px, transparent 1px) 0 0 / 72px 72px,
    linear-gradient(0deg, rgba(167, 139, 250, 0.16) 1px, transparent 1px) 0 0 / 72px 72px;
  transform: rotateX(66deg) translateY(8%);
  transform-origin: 50% 100%;
  filter: drop-shadow(0 0 28px rgba(96, 165, 250, 0.18));
  mask-image: linear-gradient(180deg, transparent 0%, #000 36%, #000 86%, transparent 100%);
  animation: meshRunway 6.8s linear infinite;
}

.stage-mountain {
  position: absolute;
  left: -8%;
  right: -8%;
  bottom: 15%;
  height: 32%;
  clip-path: polygon(0 82%, 7% 58%, 15% 74%, 23% 32%, 33% 70%, 42% 44%, 51% 72%, 61% 24%, 72% 76%, 82% 48%, 91% 69%, 100% 38%, 100% 100%, 0 100%);
  background:
    linear-gradient(180deg, rgba(96, 165, 250, 0.22), rgba(7, 11, 30, 0.62)),
    linear-gradient(90deg, rgba(96, 165, 250, 0.18), rgba(167, 139, 250, 0.16));
  opacity: 0.42;
  filter: blur(0.2px) drop-shadow(0 0 32px rgba(96, 165, 250, 0.16));
  animation: mountainDrift 9s ease-in-out infinite;
}

.mountain-front {
  bottom: 5%;
  height: 38%;
  opacity: 0.58;
  clip-path: polygon(0 74%, 9% 46%, 17% 66%, 27% 26%, 38% 72%, 49% 42%, 57% 66%, 68% 20%, 78% 70%, 88% 38%, 100% 72%, 100% 100%, 0 100%);
  animation-delay: -2s;
}

.stage-scanline {
  position: absolute;
  left: 8%;
  right: 8%;
  top: 50%;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(147, 197, 253, 0.9), rgba(167, 139, 250, 0.7), transparent);
  box-shadow: 0 0 22px rgba(96, 165, 250, 0.42);
  animation: horizonScan 4.4s ease-in-out infinite;
}

.portal-core {
  position: absolute;
  z-index: 2;
  width: min(70vw, 680px);
  aspect-ratio: 1;
  display: grid;
  place-items: center;
  filter: drop-shadow(0 0 34px rgba(56, 189, 248, 0.18));
}

.portal-ring {
  position: absolute;
  border-radius: 999px;
  border: 1px solid rgba(147, 197, 253, 0.32);
  box-shadow: inset 0 0 34px rgba(56, 189, 248, 0.08), 0 0 34px rgba(56, 189, 248, 0.12);
}

.ring-a {
  inset: 4%;
  animation: portalSpin 15s linear infinite;
}

.ring-b {
  inset: 16%;
  border-style: dashed;
  border-color: rgba(167, 139, 250, 0.42);
  animation: portalSpin 9s linear infinite reverse;
}

.ring-c {
  inset: 29%;
  border-color: rgba(255, 255, 255, 0.16);
  animation: portalPulse 2.8s ease-in-out infinite;
}

.portal-beam {
  position: absolute;
  width: 58%;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(96, 165, 250, 0.84), rgba(167, 139, 250, 0.72), transparent);
  transform-origin: center;
  filter: drop-shadow(0 0 14px rgba(96, 165, 250, 0.55));
}

.beam-a { transform: rotate(16deg); animation: beamSweep 4.6s ease-in-out infinite; }
.beam-b { transform: rotate(104deg); animation: beamSweep 5.4s ease-in-out 0.7s infinite reverse; }
.beam-c { transform: rotate(-38deg); animation: beamSweep 6s ease-in-out 1.1s infinite; }

.portal-node {
  position: absolute;
  width: 9px;
  height: 9px;
  border-radius: 999px;
  background: #dbeafe;
  box-shadow: 0 0 18px rgba(96, 165, 250, 0.8);
}

.node-a { transform: translate(190px, -124px); animation: nodeFloat 4s ease-in-out infinite; }
.node-b { transform: translate(-178px, 104px); animation: nodeFloat 5s ease-in-out 0.5s infinite reverse; }
.node-c { transform: translate(28px, 218px); animation: nodeFloat 4.7s ease-in-out 1s infinite; }

.welcome-copy {
  position: relative;
  z-index: 5;
  width: min(920px, 100%);
  max-width: calc(100vw - 56px);
  text-align: center;
  padding: clamp(28px, 5vw, 48px);
}

.welcome-code {
  display: inline-flex;
  margin-bottom: 18px;
  color: rgba(191, 219, 254, 0.72);
  font: 800 11px/1 'SF Mono', 'Consolas', monospace;
  letter-spacing: 0.2em;
}

.welcome-copy h1 {
  margin: 0;
  color: transparent;
  background: linear-gradient(90deg, #ffffff, #60a5fa 42%, #a78bfa 82%);
  -webkit-background-clip: text;
  background-clip: text;
  font-size: clamp(2.35rem, 6vw, 5.1rem);
  line-height: 1.04;
  font-weight: 950;
  overflow-wrap: normal;
  text-wrap: balance;
  text-shadow: 0 0 34px rgba(96, 165, 250, 0.1);
}

.welcome-copy[data-language="zh"] h1 {
  font-size: clamp(2.25rem, 5.5vw, 5rem);
  white-space: nowrap;
}

.welcome-copy p {
  max-width: 560px;
  margin: 22px auto 0;
  color: rgba(226, 239, 255, 0.76);
  font-size: clamp(0.96rem, 2vw, 1.12rem);
  line-height: 1.9;
}

.welcome-tech {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 8px;
  margin: 24px auto 0;
}

.welcome-tech span {
  min-height: 30px;
  display: inline-grid;
  place-items: center;
  padding: 0 12px;
  border: 1px solid rgba(147, 197, 253, 0.22);
  border-radius: 999px;
  background: rgba(8, 14, 27, 0.34);
  color: rgba(226, 239, 255, 0.68);
  font: 800 10px/1 'SF Mono', 'Consolas', monospace;
  letter-spacing: 0.1em;
  box-shadow: inset 0 0 18px rgba(96, 165, 250, 0.04);
  animation: techWake 3.8s ease-in-out infinite;
}

.welcome-tech span:nth-child(2) { animation-delay: 0.3s; }
.welcome-tech span:nth-child(3) { animation-delay: 0.6s; }
.welcome-tech span:nth-child(4) { animation-delay: 0.9s; }

.welcome-connect {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
  gap: 8px;
  max-width: min(560px, 100%);
  margin: 18px auto 0;
  padding: 8px;
  border: 1px solid rgba(147, 197, 253, 0.16);
  border-radius: 999px;
  background: rgba(5, 8, 22, 0.22);
  box-shadow: inset 0 0 22px rgba(96, 165, 250, 0.04);
  backdrop-filter: blur(10px);
}

.connect-label,
.welcome-connect a {
  min-height: 28px;
  display: inline-grid;
  place-items: center;
  padding: 0 11px;
  border-radius: 999px;
  font: 800 10px/1 'SF Mono', 'Consolas', monospace;
  letter-spacing: 0.1em;
  white-space: nowrap;
}

.connect-label {
  color: rgba(147, 197, 253, 0.58);
}

.welcome-connect a {
  border: 1px solid rgba(147, 197, 253, 0.16);
  color: rgba(226, 239, 255, 0.74);
  text-decoration: none;
  background: rgba(8, 14, 27, 0.28);
  transition: transform 0.22s var(--ease-out), border-color 0.22s, color 0.22s, background 0.22s;
}

.welcome-connect a:hover {
  transform: translateY(-2px);
  border-color: rgba(96, 165, 250, 0.52);
  color: #f8fbff;
  background: rgba(96, 165, 250, 0.1);
}

.welcome-progress {
  width: min(440px, 82%);
  height: 3px;
  margin: 28px auto 26px;
  overflow: hidden;
  border-radius: 999px;
  background: rgba(147, 197, 253, 0.12);
  box-shadow: inset 0 0 18px rgba(96, 165, 250, 0.06);
}

.welcome-progress span {
  display: block;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, #60a5fa, #a78bfa, #ffffff, #93c5fd);
  box-shadow: 0 0 18px rgba(96, 165, 250, 0.46);
  transform-origin: left;
  animation: portalLoad 8.2s var(--ease-out) forwards;
}

.welcome-enter {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  min-height: 48px;
  padding: 0 22px;
  border: 1px solid rgba(147, 197, 253, 0.52);
  border-radius: 8px;
  background: rgba(8, 14, 27, 0.58);
  color: #f8fbff;
  font-weight: 850;
  cursor: pointer;
  box-shadow: 0 18px 44px rgba(96, 165, 250, 0.13);
  transition: transform 0.22s var(--ease-out), border-color 0.22s, background 0.22s;
}

.welcome-enter svg {
  width: 18px;
  height: 18px;
  fill: none;
  stroke: currentColor;
  stroke-width: 2.2;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.welcome-enter:hover {
  transform: translateY(-2px);
  border-color: #60a5fa;
  background: rgba(96, 165, 250, 0.12);
}

.welcome-skip {
  position: absolute;
  top: 24px;
  right: 24px;
  z-index: 2;
  padding: 8px 10px;
  border: 1px solid rgba(147, 197, 253, 0.28);
  border-radius: 8px;
  background: rgba(8, 14, 27, 0.42);
  color: rgba(226, 239, 255, 0.66);
  font: 800 11px/1 'SF Mono', 'Consolas', monospace;
  letter-spacing: 0.16em;
  cursor: pointer;
}

.welcome-enter-active,
.welcome-leave-active {
  transition: opacity 0.46s var(--ease-out), filter 0.46s var(--ease-out);
}

.welcome-enter-from,
.welcome-leave-to {
  opacity: 0;
  filter: blur(10px);
}

@keyframes portalGrid {
  from { transform: translate3d(0, 0, 0); }
  to { transform: translate3d(72px, 72px, 0); }
}

@keyframes stageGlow {
  0%, 100% { opacity: 0.78; filter: saturate(1); }
  50% { opacity: 1; filter: saturate(1.18); }
}

@keyframes bootScan {
  from { transform: translateY(-18%); }
  to { transform: translateY(18%); }
}

@keyframes starDrift {
  from { transform: translate3d(0, 0, 0); }
  to { transform: translate3d(-120px, 80px, 0); }
}

@keyframes meshRunway {
  from { background-position: 0 0, 0 0; }
  to { background-position: 0 72px, 0 72px; }
}

@keyframes mountainDrift {
  0%, 100% { transform: translate3d(-1.2%, 0, 0) scale(1.02); }
  50% { transform: translate3d(1.2%, 1.8%, 0) scale(1.04); }
}

@keyframes horizonScan {
  0%, 100% { opacity: 0.22; transform: translateY(-90px) scaleX(0.72); }
  48% { opacity: 0.92; transform: translateY(82px) scaleX(1); }
}

@keyframes techWake {
  0%, 100% { opacity: 0.5; transform: translateY(0); }
  50% { opacity: 0.94; transform: translateY(-2px); }
}

@keyframes portalSpin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@keyframes portalPulse {
  0%, 100% { opacity: 0.34; transform: scale(0.94); }
  50% { opacity: 0.78; transform: scale(1.02); }
}

@keyframes beamSweep {
  0%, 100% { opacity: 0.2; scale: 0.82 1; }
  50% { opacity: 0.82; scale: 1.12 1; }
}

@keyframes nodeFloat {
  0%, 100% { opacity: 0.54; filter: blur(0); }
  50% { opacity: 1; filter: blur(0.4px); }
}

@keyframes portalLoad {
  from { transform: scaleX(0); }
  to { transform: scaleX(1); }
}

.ambient-layer {
  position: fixed;
  inset: 0;
  z-index: 0;
  pointer-events: none;
  overflow: hidden;
  background:
    linear-gradient(35deg, transparent 0 43%, rgba(147, 197, 253, 0.055) 43.5% 44%, transparent 44.5% 100%) 0 0 / 260px 260px,
    linear-gradient(145deg, transparent 0 58%, rgba(167, 139, 250, 0.05) 58.5% 59%, transparent 59.5% 100%) 80px 40px / 300px 300px,
    radial-gradient(circle, rgba(238, 247, 255, 0.32) 0 1px, transparent 1.6px) 0 0 / 138px 138px;
  opacity: 0.82;
  transform: translate3d(0, var(--scroll-drift, 0px), 0);
  transition: transform 0.12s linear;
}

.ambient-layer::before,
.ambient-layer::after {
  content: '';
  position: absolute;
  inset: 9% 5%;
  border: 1px solid rgba(96, 165, 250, 0.08);
  border-radius: 18px;
  transform: skewY(-4deg);
  animation: frameBreath 7s ease-in-out infinite;
}

.ambient-layer::after {
  inset: auto 8% 11% 12%;
  height: 140px;
  border-color: rgba(255, 189, 102, 0.1);
  transform: skewY(3deg);
  animation-delay: 1.4s;
}

.ambient-line {
  position: absolute;
  display: block;
  width: 360px;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(96, 165, 250, 0.44), rgba(155, 92, 255, 0.3), transparent);
  transform-origin: center;
  filter: drop-shadow(0 0 12px rgba(96, 165, 250, 0.3));
}

.line-a {
  --r: -7deg;
  top: 20%;
  right: 7%;
  transform: rotate(-7deg);
  animation: lineDrift 8s ease-in-out infinite;
}

.line-b {
  --r: 4deg;
  left: 6%;
  bottom: 18%;
  transform: rotate(4deg);
  animation: lineDrift 9s ease-in-out 0.8s infinite reverse;
}

@keyframes lineDrift {
  0%, 100% { opacity: 0.35; transform: translateY(0) rotate(var(--r, -7deg)); }
  50% { opacity: 0.75; transform: translateY(12px) rotate(var(--r, -7deg)); }
}

.ambient-node {
  position: absolute;
  width: 8px;
  height: 8px;
  border-radius: 999px;
  background: rgba(226, 247, 255, 0.9);
  box-shadow: 0 0 18px rgba(96, 165, 250, 0.66), 0 0 34px rgba(167, 139, 250, 0.16);
  animation: nodeSignal 6.4s ease-in-out infinite;
}

.node-one { left: 14%; top: 26%; }
.node-two { right: 18%; top: 32%; animation-delay: 1.1s; }
.node-three { left: 24%; bottom: 19%; animation-delay: 2.2s; }
.node-four { right: 28%; bottom: 23%; animation-delay: 3.3s; }

@keyframes frameBreath {
  0%, 100% { opacity: 0.3; transform: translateY(0) skewY(-4deg); }
  50% { opacity: 0.72; transform: translateY(10px) skewY(-4deg); }
}

@keyframes nodeSignal {
  0%, 100% { opacity: 0.35; transform: scale(0.86); }
  45% { opacity: 1; transform: scale(1.12); }
}

.time-gate-overlay {
  position: fixed;
  inset: 0;
  z-index: 9998;
  display: grid;
  place-items: center;
  padding: 24px;
  background:
    radial-gradient(circle at 50% 50%, rgba(96, 165, 250, 0.14), transparent 32%),
    rgba(2, 6, 18, 0.76);
  backdrop-filter: blur(16px);
}

.time-gate-panel {
  position: relative;
  width: min(760px, 100%);
  min-height: 430px;
  display: grid;
  grid-template-columns: minmax(210px, 0.72fr) minmax(0, 1fr);
  gap: clamp(18px, 4vw, 34px);
  align-items: center;
  overflow: hidden;
  padding: clamp(24px, 5vw, 42px);
  border: 1px solid rgba(147, 197, 253, 0.26);
  border-radius: 8px;
  background:
    linear-gradient(135deg, rgba(12, 24, 46, 0.86), rgba(4, 8, 20, 0.88)),
    repeating-linear-gradient(90deg, rgba(147, 197, 253, 0.055) 0 1px, transparent 1px 84px);
  box-shadow: 0 30px 100px rgba(0, 0, 0, 0.46), inset 0 0 90px rgba(96, 165, 250, 0.06);
}

.time-gate-close {
  position: absolute;
  top: 14px;
  right: 16px;
  z-index: 2;
  border: 0;
  background: transparent;
  color: rgba(226, 239, 255, 0.56);
  font-size: 26px;
  line-height: 1;
  cursor: pointer;
}

.time-gate-orbit {
  position: relative;
  width: min(260px, 52vw);
  aspect-ratio: 1;
  justify-self: center;
  border-radius: 999px;
  border: 1px solid rgba(147, 197, 253, 0.3);
  box-shadow: inset 0 0 42px rgba(96, 165, 250, 0.08), 0 0 42px rgba(96, 165, 250, 0.1);
}

.time-gate-orbit::before,
.time-gate-orbit::after {
  content: '';
  position: absolute;
  inset: 16%;
  border-radius: inherit;
  border: 1px dashed rgba(167, 139, 250, 0.38);
  animation: portalSpin 12s linear infinite;
}

.time-gate-orbit::after {
  inset: 34%;
  border-style: solid;
  border-color: rgba(255, 189, 102, 0.28);
  animation-duration: 7s;
  animation-direction: reverse;
}

.time-gate-orbit span {
  position: absolute;
  left: 50%;
  top: 50%;
  width: 48%;
  height: 1px;
  background: linear-gradient(90deg, rgba(96, 165, 250, 0.9), transparent);
  transform-origin: left center;
  animation: portalSpin 4.6s linear infinite;
}

.time-gate-orbit span:nth-child(2) { animation-delay: -1.4s; opacity: 0.58; }
.time-gate-orbit span:nth-child(3) { animation-delay: -2.8s; opacity: 0.34; }

.time-gate-copy {
  position: relative;
  z-index: 1;
}

.time-gate-code {
  color: #93c5fd;
  font: 800 11px/1 'SF Mono', 'Consolas', monospace;
  letter-spacing: 0.18em;
}

.time-gate-copy h2 {
  margin: 16px 0 12px;
  color: #f8fbff;
  font-size: clamp(2rem, 5vw, 4.2rem);
  line-height: 1;
}

.time-gate-copy p {
  margin: 0;
  color: rgba(226, 239, 255, 0.7);
  line-height: 1.85;
}

.time-gate-path {
  display: flex;
  flex-wrap: wrap;
  gap: 9px;
  margin-top: 22px;
}

.time-gate-path span {
  padding: 7px 10px;
  border: 1px solid rgba(147, 197, 253, 0.18);
  border-radius: 999px;
  background: rgba(8, 14, 27, 0.48);
  color: rgba(226, 239, 255, 0.72);
  font-size: 12px;
}

.time-gate-action {
  margin-top: 28px;
  min-height: 44px;
  padding: 0 20px;
  border: 1px solid rgba(96, 165, 250, 0.54);
  border-radius: 8px;
  background: linear-gradient(135deg, rgba(96, 165, 250, 0.2), rgba(124, 58, 237, 0.22));
  color: #f8fbff;
  font-weight: 850;
  cursor: pointer;
  transition: transform 0.22s var(--ease-out), box-shadow 0.22s;
}

.time-gate-action:hover {
  transform: translateY(-2px);
  box-shadow: 0 16px 36px rgba(96, 165, 250, 0.16);
}

.time-gate-enter-active,
.time-gate-leave-active {
  transition: opacity 0.28s var(--ease-out), filter 0.28s var(--ease-out);
}

.time-gate-enter-from,
.time-gate-leave-to {
  opacity: 0;
  filter: blur(8px);
}

/* 特效容器 */
.candy-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 9999;
  overflow: hidden;
}

/* 微粒基础样式 */
.candy {
  position: absolute;
  width: var(--particle-size, 8px);
  height: var(--particle-size, 8px);
  border-radius: 999px;
  background: currentColor;
  pointer-events: none;
  user-select: none;
  will-change: transform, opacity;
  filter: blur(0.1px) drop-shadow(0 0 8px currentColor);
  animation-fill-mode: forwards;
}

@keyframes dustBurst {
  0% {
    transform: translate3d(0, 0, 0) scale(0.3) rotate(0deg);
    opacity: 0;
  }
  18% {
    opacity: 1;
  }
  100% {
    transform: translate3d(var(--dx), var(--dy), 0) scale(0.9) rotate(var(--rot));
    opacity: 0;
  }
}

.candy {
  animation-name: dustBurst;
  animation-timing-function: var(--ease-out);
}

/* ================================================
   导航栏样式
   ================================================ */
.souta-nav {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  display: flex;
  justify-content: center;
  padding: 14px clamp(14px, 4vw, 44px);
  pointer-events: none;
}

.souta-nav-inner {
  display: flex;
  align-items: center;
  gap: 6px;
  width: min(1280px, 100%);
  max-width: min(100%, 1280px);
  padding: 0;
  border: 0;
  border-radius: 0;
  background: transparent;
  backdrop-filter: none;
  -webkit-backdrop-filter: none;
  box-shadow: none;
  position: relative;
  overflow: visible;
  pointer-events: auto;
}

.nav-brand {
  position: relative;
  z-index: 1;
  min-width: 170px;
  margin-right: auto;
  padding: 8px 13px 8px 0;
  display: inline-grid;
  gap: 2px;
  color: var(--text);
  line-height: 1.05;
  text-shadow: 0 0 18px rgba(96, 165, 250, 0.2), 0 2px 12px rgba(0, 0, 0, 0.65);
}

.nav-brand strong {
  font-size: 22px;
  letter-spacing: 0;
  background: linear-gradient(90deg, #ffffff, var(--accent));
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.nav-brand span {
  color: var(--text-lighter);
  font: 700 10px/1 'SF Mono', 'Consolas', monospace;
  letter-spacing: 0.14em;
  text-transform: uppercase;
}

.nav-links {
  position: relative;
  z-index: 1;
  display: inline-flex;
  align-items: center;
  gap: 0;
  min-width: 0;
}

.souta-nav-inner::before {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  bottom: -8px;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(96, 165, 250, 0.22), rgba(124, 58, 237, 0.18), transparent);
  opacity: 0.55;
  pointer-events: none;
}

.souta-nav-inner::after {
  content: '';
  position: absolute;
  top: 0;
  bottom: 0;
  width: 120px;
  left: -140px;
  background: linear-gradient(90deg, transparent, rgba(147, 197, 253, 0.24), transparent);
  animation: navSweep 6.2s var(--ease-out) infinite;
  pointer-events: none;
}

.nav-item {
  position: relative;
  z-index: 1;
  display: inline-flex;
  align-items: center;
  gap: 7px;
  padding: 8px 13px;
  font-size: 14px;
  font-weight: 700;
  color: var(--text-light, #666);
  border-radius: 0;
  transition: color 0.22s, text-shadow 0.22s, transform 0.22s var(--ease-out);
  cursor: pointer;
  user-select: none;
  text-decoration: none;
  white-space: nowrap;
  text-shadow: 0 2px 12px rgba(0, 0, 0, 0.72);
}

.nav-icon {
  color: var(--accent);
  font-size: 13px;
  line-height: 1;
  opacity: 0.72;
}

.nav-item:hover {
  color: var(--accent-soft);
  transform: translateY(-1px);
  text-shadow: 0 0 18px rgba(96, 165, 250, 0.52), 0 2px 12px rgba(0, 0, 0, 0.72);
}

.nav-item.active {
  color: #ffffff;
  text-shadow: 0 0 20px rgba(96, 165, 250, 0.62), 0 2px 12px rgba(0, 0, 0, 0.72);
}

.nav-item.active::after {
  content: '';
  position: absolute;
  left: 14px;
  right: 14px;
  bottom: 2px;
  height: 2px;
  background: linear-gradient(90deg, transparent, var(--accent), transparent);
  box-shadow: 0 0 12px rgba(96, 165, 250, 0.6);
}

.nav-item.active .nav-icon {
  color: var(--accent-soft);
  opacity: 1;
}

.nav-tools {
  position: relative;
  z-index: 1;
  display: inline-flex;
  align-items: center;
  gap: 5px;
  margin-left: 6px;
}

.nav-tool {
  min-width: 34px;
  height: 32px;
  display: inline-grid;
  place-items: center;
  padding: 0 8px;
  border: 1px solid rgba(147, 197, 253, 0.22);
  border-radius: 8px;
  background: rgba(5, 8, 22, 0.22);
  color: rgba(226, 239, 255, 0.76);
  font: 800 10px/1 'SF Mono', 'Consolas', monospace;
  letter-spacing: 0.04em;
  text-decoration: none;
  cursor: pointer;
  transition: transform 0.22s var(--ease-out), border-color 0.22s, color 0.22s, background 0.22s;
}

.nav-tool:hover {
  transform: translateY(-1px);
  border-color: rgba(96, 165, 250, 0.54);
  color: #ffffff;
  background: rgba(96, 165, 250, 0.08);
}

@keyframes navSweep {
  0% { transform: translateX(0); opacity: 0; }
  12% { opacity: 1; }
  48%, 100% { transform: translateX(1420px); opacity: 0; }
}

/* 写文章按钮高亮 */
.nav-write {
  color: var(--accent);
  font-weight: 500;
}

.nav-write:hover {
  color: var(--accent-soft);
  background: var(--accent-dim);
}

.nav-write.active {
  background: linear-gradient(135deg, var(--accent), var(--violet));
  color: #06101f;
}

/* 退出登录按钮 */
.nav-logout {
  color: #999;
  font-size: 13px;
}

.nav-logout:hover {
  color: #e74c3c;
  background: rgba(231, 76, 60, 0.06);
}

/* 登录按钮已迁移到页脚 */

/* ================================================
   主内容区 & 页脚
   ================================================ */
.souta-main {
  flex: 1;
  position: relative;
  z-index: 1;
  padding-top: 78px;
}

.souta-footer {
  position: relative;
  z-index: 1;
  padding: 28px 0;
  text-align: center;
  font-size: 13px;
  color: var(--text-lighter, #999);
  border-top: 1px solid var(--border);
  margin-top: 40px;
  background: linear-gradient(180deg, transparent, rgba(7, 8, 18, 0.72));
}

.souta-footer p {
  letter-spacing: 0.3px;
}

/* 页脚管理入口：低调隐藏 */
.souta-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 18px;
  flex-wrap: wrap;
}

.souta-footer-admin {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  opacity: 0.35;
  transition: opacity 0.3s;
  font-size: 12px;
}

.souta-footer-admin:hover {
  opacity: 1;
}

.future-rail-links {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  font-size: 12px;
}

.site-telemetry {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  min-height: 28px;
  padding: 0 12px;
  border: 1px solid rgba(96, 165, 250, 0.18);
  border-radius: 999px;
  color: rgba(226, 239, 255, 0.56);
  background: rgba(6, 12, 26, 0.42);
  box-shadow: inset 0 0 14px rgba(96, 165, 250, 0.05);
  font: 700 11px/1 'SF Mono', 'Consolas', monospace;
  letter-spacing: 0.02em;
}

.site-telemetry span {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  white-space: nowrap;
}

.site-telemetry strong {
  color: #93c5fd;
  font-weight: 800;
}

.future-rail-links a,
.future-rail-links button {
  color: rgba(226, 239, 255, 0.54);
  border-bottom: 1px solid rgba(147, 197, 253, 0.2);
  border-top: 0;
  border-left: 0;
  border-right: 0;
  background: transparent;
  padding: 0;
  font: inherit;
  cursor: pointer;
  transition: color 0.2s, border-color 0.2s, text-shadow 0.2s;
}

.future-rail-links a:hover,
.future-rail-links button:hover {
  color: #eaf7ff;
  border-color: rgba(96, 165, 250, 0.58);
  text-shadow: 0 0 16px rgba(96, 165, 250, 0.3);
}

.footer-admin-link {
  color: var(--text-lighter, #999);
  text-decoration: none;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 2px 6px;
  border-radius: 4px;
  transition: color 0.2s, background 0.2s;
}

.footer-admin-link:hover {
  color: var(--accent);
  background: rgba(96, 165, 250, 0.08);
}

.footer-admin-login {
  font-size: 13px;
}

.footer-admin-sep {
  color: var(--text-lighter, #999);
  opacity: 0.5;
}

/* 页面过渡动画 */
.page-enter-active,
.page-leave-active {
  transition: opacity 0.34s var(--ease-out), transform 0.34s var(--ease-out), filter 0.34s var(--ease-out);
}

.page-enter-from {
  opacity: 0;
  transform: translateY(16px);
  filter: blur(4px);
}

.page-leave-to {
  opacity: 0;
  transform: translateY(-10px);
  filter: blur(3px);
}

/* 响应式 */
@media (max-width: 768px) {
  .souta-nav {
    padding: 10px 10px 8px;
    align-items: flex-start;
    overflow: visible;
    justify-content: center;
  }

  .souta-nav::-webkit-scrollbar {
    display: none;
  }

  .souta-nav-inner {
    display: grid;
    grid-template-columns: minmax(0, 1fr) auto;
    row-gap: 6px;
    column-gap: 10px;
    width: 100%;
    max-width: 100%;
    min-width: 0;
    padding: 2px 0 0;
    overflow: visible;
  }

  .nav-brand {
    grid-column: 1;
    min-width: 0;
    margin-right: 0;
    padding: 6px 0;
  }

  .nav-brand strong {
    font-size: 18px;
  }

  .nav-brand span {
    font-size: 8px;
  }

  .nav-links {
    grid-column: 1 / -1;
    display: flex;
    gap: 4px;
    width: 100%;
    max-width: 100%;
    min-width: 0;
    overflow-x: auto;
    overflow-y: hidden;
    padding: 2px 0 8px;
    scrollbar-width: none;
  }

  .nav-links::-webkit-scrollbar {
    display: none;
  }
  
  .nav-item {
    flex: 0 0 auto;
    padding: 7px 9px;
    font-size: 12px;
  }

  .nav-tools {
    grid-column: 2;
    justify-self: end;
    gap: 4px;
    margin-left: 0;
  }

  .nav-tool {
    min-width: 30px;
    height: 30px;
    padding: 0 7px;
  }

  .nav-icon {
    display: none;
  }
  
  .souta-main {
    padding-top: 108px;
  }

  .portal-core {
    width: min(104vw, 520px);
  }

  .welcome-copy {
    padding: 24px 10px;
  }

  .welcome-copy[data-language="zh"] h1 {
    font-size: clamp(2rem, 9.4vw, 3.6rem);
    white-space: normal;
  }

  .welcome-connect {
    gap: 6px;
    max-width: 100%;
    padding: 7px;
    border-radius: 18px;
  }

  .connect-label,
  .welcome-connect a {
    min-height: 26px;
    padding: 0 9px;
    font-size: 9px;
  }

  .time-gate-panel {
    grid-template-columns: 1fr;
    min-height: 0;
  }

  .time-gate-orbit {
    width: min(220px, 60vw);
  }
}

/* 深色模式适配 */
@media (prefers-color-scheme: dark) {
  .souta-nav-inner {
    background: transparent;
    border-color: transparent;
    box-shadow: none;
  }
  
  .nav-item:hover {
    color: var(--accent-soft);
  }
}

@media (prefers-reduced-motion: reduce) {
  .candy,
  .ambient-line,
  .welcome-portal::before,
  .welcome-portal::after,
  .stage-starfield,
  .stage-mesh,
  .stage-mountain,
  .stage-scanline,
  .welcome-tech span,
  .portal-grid,
  .portal-ring,
  .portal-beam,
  .portal-node,
  .ambient-node,
  .time-gate-orbit,
  .time-gate-orbit::before,
  .time-gate-orbit::after,
  .time-gate-orbit span,
  .welcome-progress span,
  .page-enter-active,
  .page-leave-active {
    animation: none !important;
    transition: none !important;
  }
}
</style>
