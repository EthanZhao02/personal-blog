<template>
  <div class="home-page" :style="spotlightStyle" @pointermove="onPointerMove">
    <canvas ref="starCanvas" class="star-canvas" aria-hidden="true"></canvas>

    <div class="hero-video-bg" aria-hidden="true">
      <video
        ref="heroVideo"
        autoplay
        muted
        playsinline
        preload="metadata"
        :style="{ opacity: videoOpacity }"
        @loadeddata="onHeroVideoLoaded"
        @timeupdate="onHeroVideoTimeUpdate"
        @ended="onHeroVideoEnded"
      >
        <source src="https://assets.mixkit.co/videos/preview/mixkit-digital-animation-of-futuristic-devices-99786-large.mp4" type="video/mp4">
      </video>
      <div class="video-overlay"></div>
    </div>

    <div class="hero-mesh-scene" aria-hidden="true">
      <span class="mesh-horizon"></span>
      <span class="mesh-mountain mountain-back"></span>
      <span class="mesh-mountain mountain-front"></span>
      <span class="mesh-plane"></span>
      <span class="mesh-pulse pulse-a"></span>
      <span class="mesh-pulse pulse-b"></span>
    </div>

    <div class="hero-ambient" aria-hidden="true">
      <span class="ambient-bar bar-one"></span>
      <span class="ambient-bar bar-two"></span>
      <span class="ambient-bar bar-three"></span>
    </div>

    <section class="hero-section" :class="{ 'hero-enter': heroVisible }">
      <div class="hero-copy reveal-up">
        <div class="hero-kicker">
          <span class="kicker-dot"></span>
          {{ currentTime || '00:00:00' }} / ETHAN NEXUS ONLINE
        </div>

        <h1>
          <span>Ethan</span>
          智域
        </h1>

        <p class="hero-role">{{ siteConfig.role }}</p>
        <p class="hero-slogan">{{ siteConfig.subtitle }}</p>

        <div class="signal-line" aria-label="AI route signal">
          <span class="signal-prompt">{{ ui.signalPrompt }}</span>
          <strong>{{ typedText }}</strong>
          <span class="cursor-blink">_</span>
        </div>

        <div class="hero-actions" aria-label="Primary links">
          <router-link to="/projects" class="hero-btn primary">
            <span>{{ ui.viewProjects }}</span>
            <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M5 12h14m-6-6 6 6-6 6"/></svg>
          </router-link>
          <a href="#journey" class="hero-btn">
            <span>{{ ui.viewTimeline }}</span>
            <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M5 5h14M5 12h14M5 19h9"/></svg>
          </a>
          <router-link to="/about" class="hero-btn">
            <span>{{ ui.about }}</span>
            <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 12a4 4 0 1 0 0-8 4 4 0 0 0 0 8Zm7 8a7 7 0 0 0-14 0"/></svg>
          </router-link>
        </div>

        <div class="hero-metrics" aria-label="Learning stages">
          <div v-for="item in journeyMetrics" :key="item.label" class="metric-item liquid-glass">
            <span class="metric-line"></span>
            <strong>{{ item.value }}</strong>
            <span>{{ item.label }}</span>
          </div>
        </div>
      </div>

      <div class="hero-visual reveal-up" style="animation-delay: 120ms">
        <div class="visual-frame">
          <div class="frame-topbar">
            <span></span>
            <span></span>
            <span></span>
            <strong>{{ ui.coreStatus }}</strong>
          </div>

          <img :src="activePhoto" alt="Ethan 智域 visual" class="visual-image" />
          <div class="digital-slices" aria-hidden="true">
            <span class="slice-a"></span>
            <span class="slice-b"></span>
            <span class="slice-c"></span>
          </div>
          <div class="face-vector" aria-hidden="true">
            <span v-for="point in 18" :key="point"></span>
          </div>
          <div class="holo-rain" aria-hidden="true">
            <span v-for="line in 12" :key="line"></span>
          </div>
          <div class="energy-outline" aria-hidden="true"></div>
          <div class="visual-grid" aria-hidden="true"></div>
          <div class="avatar-orbit" aria-hidden="true">
            <span></span>
            <span></span>
            <span></span>
          </div>
          <div class="scanner-dial" aria-hidden="true">
            <span></span>
            <span></span>
            <span></span>
          </div>

          <button v-if="hasMultiplePhotos" class="visual-next" type="button" @click="nextPhoto" aria-label="Switch visual">
            <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M9 5l7 7-7 7"/></svg>
          </button>

          <div class="visual-readout">
            <span>{{ ui.digitalTwin }}</span>
            <strong>{{ ui.online }}</strong>
          </div>
        </div>

        <div class="signal-stack" aria-label="Ethan capability signals">
          <span v-for="item in heroSignals" :key="item">{{ item }}</span>
        </div>

        <div class="journey-panel liquid-glass">
          <div class="journey-panel-head">
            <span>2021 -> 2026</span>
            <strong>WEB / BACKEND / AI</strong>
          </div>
          <div class="journey-panel-list">
            <div v-for="item in journeySummary" :key="item.year">
              <span>{{ item.year }}</span>
              <strong>{{ item.title }}</strong>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section id="journey" class="timeline-section" :class="{ 'content-enter': contentVisible }">
      <div class="section-head scroll-trigger">
        <div>
          <span>{{ ui.timelineBadge }}</span>
          <h2>{{ ui.timelineTitle }}</h2>
        </div>
        <p>{{ ui.timelineDesc }}</p>
      </div>

      <div class="life-timeline-track">
        <article
          class="life-timeline-item scroll-trigger"
          v-for="(item, idx) in timelineItems"
          :key="item.year"
          :style="{ '--reveal-delay': `${idx * 90}ms` }"
        >
          <div class="life-node">
            <span>{{ item.year }}</span>
          </div>
          <div class="life-card">
            <h3>{{ item.title }}</h3>
            <p>{{ item.description }}</p>
            <div class="life-tags" v-if="item.tags">
              <span v-for="tag in item.tags" :key="tag">{{ tag }}</span>
            </div>
          </div>
        </article>
      </div>
    </section>

    <section class="bento-grid showcase-grid" :class="{ 'content-enter': contentVisible }" aria-label="Portfolio overview">
      <article class="bento-card feature-card scroll-trigger">
        <div class="card-heading">
          <span>{{ ui.featuredProject }}</span>
          <router-link to="/projects">{{ ui.allProjects }}</router-link>
        </div>
        <div class="project-focus">
          <div class="project-mark">
            <img v-if="isProjectIconUrl(featuredProject.icon)" :src="featuredProject.icon" alt="project" />
            <span v-else>{{ featuredProject.icon || '◇' }}</span>
          </div>
          <div>
            <h2>{{ featuredProject.name }}</h2>
            <p>{{ featuredProject.description }}</p>
          </div>
        </div>
        <div class="tech-stack">
          <span v-for="tech in projectTech" :key="tech">{{ tech }}</span>
        </div>
      </article>

      <article class="bento-card articles-card scroll-trigger">
        <div class="card-heading">
          <span>{{ ui.latestArticles }}</span>
          <router-link to="/posts">{{ ui.archive }}</router-link>
        </div>
        <router-link
          v-for="article in latestArticles"
          :key="article.id"
          :to="`/article/${article.id}`"
          class="article-row"
        >
          <div class="article-meta">
            <time>{{ formatDate(article.createTime) }}</time>
            <span v-if="article.categoryName" class="article-cat">{{ article.categoryName }}</span>
          </div>
          <div class="article-body">
            <strong>{{ article.title }}</strong>
            <p v-if="article.summary" class="article-summary">{{ article.summary }}</p>
          </div>
        </router-link>
        <div v-if="!latestArticles.length" class="empty-hint">{{ ui.noArticles }}</div>
      </article>

      <article id="connect" class="bento-card connect-card scroll-trigger">
        <div class="card-heading">
          <span>{{ ui.quickLinks }}</span>
          <router-link to="/about">{{ ui.more }}</router-link>
        </div>
        <div class="quick-nav">
          <router-link v-for="link in quickLinks" :key="link.to" :to="link.to" class="quick-link">
            <span class="quick-icon" v-html="link.icon"></span>
            <span>
              <strong>{{ link.title }}</strong>
              <small>{{ link.text }}</small>
            </span>
          </router-link>
        </div>
      </article>
    </section>
  </div>
</template>

<script setup>
import { computed, inject, nextTick, onMounted, onUnmounted, ref, watch } from 'vue'
import siteConfig from '../config/site.config.js'
import { getArticleList } from '../api/article'
import { getProjects } from '../api/project'

const starCanvas = ref(null)
const heroVideo = ref(null)
const videoOpacity = ref(0)
const currentPhoto = ref(0)
const currentTime = ref('')
const heroVisible = ref(false)
const contentVisible = ref(false)
const typedText = ref('')
const mouse = ref({ x: '52vw', y: '42vh', tiltX: '0deg', tiltY: '0deg' })
const siteLanguage = inject('siteLanguage', ref(localStorage.getItem('ethan-language') || 'zh'))

let timeTimer = null
let carouselTimer = null
let typeTimer = null
let rafId = null
let videoFadeFrame = null
let videoResetTimer = null
let resizeHandler = null
let revealObserver = null
let fadingOutVideo = false
let stars = []

const ui = computed(() => siteLanguage.value === 'en'
  ? {
      viewProjects: 'View Projects',
      viewTimeline: 'Life Timeline',
      about: 'About',
      signalPrompt: 'AI ROUTE',
      coreStatus: 'DIGITAL TWIN CORE',
      digitalTwin: 'AI Digital Twin',
      online: 'ONLINE',
      timelineBadge: 'Life Timeline',
      timelineTitle: 'Life Timeline',
      timelineDesc: 'From frontend and database basics, to backend languages, then into AI / NLP and project practice.',
      featuredProject: 'Featured Project',
      allProjects: 'All Projects',
      latestArticles: 'Latest Articles',
      archive: 'Archive',
      noArticles: 'No articles yet. Stay tuned...',
      quickLinks: 'Quick Links',
      more: 'More',
      typing: 'From web basics to backend systems, then into AI and NLP.',
    }
  : {
      viewProjects: '查看项目',
      viewTimeline: '生命轨迹',
      about: '关于我',
      signalPrompt: 'AI 航线',
      coreStatus: '数字分身核心',
      digitalTwin: 'AI 数字分身',
      online: '在线',
      timelineBadge: 'Life Timeline',
      timelineTitle: '生命轨迹',
      timelineDesc: '从前端和数据库入门，到后端语言基础，再到 AI / NLP 与项目实践。',
      featuredProject: '精选项目',
      allProjects: '全部项目',
      latestArticles: '最新文章',
      archive: '文章归档',
      noArticles: '暂无文章，敬请期待...',
      quickLinks: '快捷入口',
      more: '更多',
      typing: '从 Web 基础到后端系统，再进入 AI 与 NLP。',
    })

const fullText = computed(() => ui.value.typing)

const articles = ref([])
const projectsData = ref([])

const timelineItems = computed(() => siteLanguage.value === 'en'
  ? [
      {
        year: '2021-2022',
        title: 'Frontend, PHP, and Database Basics',
        description: 'Started learning computer science through frontend pages, PHP, databases, and web development foundations: first making visible pages and data-backed systems run.',
        tags: ['HTML/CSS/JS', 'PHP', 'Database'],
      },
      {
        year: '2023-2024',
        title: 'Backend Languages and Programming Foundations',
        description: 'Moved toward backend and foundational programming: Python, C, C++, system development concepts, interfaces, data processing, and engineering practice.',
        tags: ['Python', 'C/C++', 'Backend'],
      },
      {
        year: '2025-2026',
        title: 'AI, NLP, and Project Practice',
        description: 'Entered the AI direction, focusing on NLP, intelligent tools, code assistance, and personal projects that connect web and backend skills to AI work.',
        tags: ['AI', 'NLP', 'Projects'],
      },
    ]
  : [
      {
        year: '2021-2022',
        title: '前端、PHP 与数据库入门',
        description: '开始系统接触计算机，主要学习前端页面、PHP、数据库以及 Web 开发里的基础知识，把能看见的页面和能存数据的系统先跑起来。',
        tags: ['HTML/CSS/JS', 'PHP', 'Database'],
      },
      {
        year: '2023-2024',
        title: '后端语言与编程基础',
        description: '转向更扎实的后端和基础语言学习，接触 Python、C、C++ 以及更多系统开发相关知识，逐步理解程序结构、接口、数据处理和工程实现。',
        tags: ['Python', 'C/C++', 'Backend'],
      },
      {
        year: '2025-2026',
        title: 'AI、NLP 与项目实践',
        description: '陆续进入 AI 方向，关注 NLP、智能工具、代码辅助和个人项目实践，把前面学过的 Web 与后端能力接到 AI 项目里。',
        tags: ['AI', 'NLP', 'Projects'],
      },
    ])

const journeySummary = computed(() => timelineItems.value.map(item => ({
  year: item.year,
  title: item.title,
})))

const quickLinks = computed(() => {
  const isEn = siteLanguage.value === 'en'
  return [
    {
      to: '/posts',
      title: isEn ? 'Read Articles' : '阅读文章',
      text: isEn ? 'Technical notes and stage reviews' : '技术笔记与阶段复盘',
      icon: '<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><path d="M14 2v6h6"/><path d="M16 13H8"/><path d="M16 17H8"/></svg>',
    },
    {
      to: '/projects',
      title: isEn ? 'Browse Projects' : '浏览项目',
      text: isEn ? 'Portfolio and experimental code' : '作品集与实验代码',
      icon: '<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m16 18 6-6-6-6"/><path d="m8 6-6 6 6 6"/></svg>',
    },
    {
      to: '/friends',
      title: isEn ? 'Friend Links' : '友情链接',
      text: isEn ? 'Bloggers and community nodes' : '优秀博主与社区',
      icon: '<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M22 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>',
    },
    {
      to: '/message',
      title: isEn ? 'Message Board' : '留言交流',
      text: isEn ? 'Leave ideas or suggestions' : '留下想法或建议',
      icon: '<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>',
    },
  ]
})

const spotlightStyle = computed(() => ({
  '--mouse-x': mouse.value.x,
  '--mouse-y': mouse.value.y,
  '--tilt-x': mouse.value.tiltX,
  '--tilt-y': mouse.value.tiltY,
}))

const journeyMetrics = computed(() => [
  { value: '2021', label: siteLanguage.value === 'en' ? 'Start CS' : '接触计算机' },
  { value: 'Web', label: 'PHP / DB' },
  { value: 'Code', label: siteLanguage.value === 'en' ? 'Python / C++' : '后端基础' },
  { value: 'AI', label: siteLanguage.value === 'en' ? 'NLP / Projects' : 'NLP / 项目' },
])

const heroSignals = computed(() => siteLanguage.value === 'en'
  ? ['Digital Twin', 'Web / Backend', 'NLP Direction']
  : ['AI 数字分身', 'Web / 后端', 'NLP 方向'])

const activePhoto = computed(() => siteConfig.photos[currentPhoto.value] || siteConfig.photos[0] || '')
const hasMultiplePhotos = computed(() => siteConfig.photos.length > 1)
const featuredProject = computed(() => projectsData.value[0] || siteConfig.projects?.[0] || {})
const latestArticles = computed(() => articles.value.slice(0, 4))
const projectTech = computed(() => {
  const ts = featuredProject.value?.techStack
  if (!ts) return []
  return ts.split(/[,\s]+/).filter(Boolean).slice(0, 6)
})

const isProjectIconUrl = (icon) => icon && /^https?:\/\//.test(icon)

const updateTime = () => {
  currentTime.value = new Date().toLocaleTimeString('en-US', {
    hour12: false,
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
  })
}

const nextPhoto = () => {
  if (siteConfig.photos.length <= 1) return
  currentPhoto.value = (currentPhoto.value + 1) % siteConfig.photos.length
}

const formatDate = (value) => {
  if (!value) return ''
  const d = new Date(value + '+08:00')
  if (isNaN(d.getTime())) return value
  return `${d.getMonth() + 1}.${String(d.getDate()).padStart(2, '0')}`
}

const onPointerMove = (event) => {
  const px = event.clientX / Math.max(window.innerWidth, 1) - 0.5
  const py = event.clientY / Math.max(window.innerHeight, 1) - 0.5
  mouse.value = {
    x: `${event.clientX}px`,
    y: `${event.clientY}px`,
    tiltX: `${(-py * 5.4).toFixed(2)}deg`,
    tiltY: `${(px * 7.2).toFixed(2)}deg`,
  }
}

const scrollHomeToTop = () => {
  window.scrollTo(0, 0)
}

const fadeHeroVideoTo = (targetOpacity, duration = 500) => {
  if (videoFadeFrame) {
    cancelAnimationFrame(videoFadeFrame)
    videoFadeFrame = null
  }

  const start = performance.now()
  const from = Number(videoOpacity.value) || 0
  const change = targetOpacity - from

  const tick = (now) => {
    const progress = Math.min((now - start) / duration, 1)
    const eased = 1 - Math.pow(1 - progress, 3)
    videoOpacity.value = Number((from + change * eased).toFixed(3))

    if (progress < 1) {
      videoFadeFrame = requestAnimationFrame(tick)
      return
    }

    videoFadeFrame = null
  }

  videoFadeFrame = requestAnimationFrame(tick)
}

const fadeInHeroVideo = () => {
  fadingOutVideo = false
  fadeHeroVideoTo(0.34)
}

const fadeOutHeroVideo = () => {
  if (fadingOutVideo) return
  fadingOutVideo = true
  fadeHeroVideoTo(0)
}

const onHeroVideoLoaded = () => {
  const video = heroVideo.value
  if (!video) return
  videoOpacity.value = 0
  video.play?.().catch(() => {})
  fadeInHeroVideo()
}

const onHeroVideoTimeUpdate = () => {
  const video = heroVideo.value
  if (!video || !Number.isFinite(video.duration) || video.duration <= 0) return
  if (video.duration - video.currentTime <= 0.55) {
    fadeOutHeroVideo()
  }
}

const onHeroVideoEnded = () => {
  const video = heroVideo.value
  if (!video) return

  if (videoFadeFrame) {
    cancelAnimationFrame(videoFadeFrame)
    videoFadeFrame = null
  }
  if (videoResetTimer) {
    clearTimeout(videoResetTimer)
  }

  videoOpacity.value = 0
  videoResetTimer = window.setTimeout(() => {
    const nextVideo = heroVideo.value
    if (!nextVideo) return
    nextVideo.currentTime = 0
    nextVideo.play?.().catch(() => {})
    fadeInHeroVideo()
  }, 100)
}

const startTyping = () => {
  if (typeTimer) {
    clearInterval(typeTimer)
    typeTimer = null
  }
  let i = 0
  const text = fullText.value
  typedText.value = ''
  typeTimer = setInterval(() => {
    if (i < text.length) {
      typedText.value += text[i]
      i += 1
      return
    }

    clearInterval(typeTimer)
    typeTimer = null
    setTimeout(() => {
      contentVisible.value = true
    }, 240)
  }, 34)
}

watch(siteLanguage, () => {
  if (!heroVisible.value) return
  startTyping()
})

const setupConstellation = () => {
  const canvas = starCanvas.value
  if (!canvas) return

  const ctx = canvas.getContext('2d')
  const dpr = Math.min(window.devicePixelRatio || 1, 2)

  const seedStars = () => {
    const rect = canvas.getBoundingClientRect()
    const count = Math.max(56, Math.round((rect.width * rect.height) / 15000))
    stars = Array.from({ length: count }, (_, index) => ({
      id: index,
      x: Math.random() * rect.width,
      y: Math.random() * rect.height,
      vx: (Math.random() - 0.5) * 0.24,
      vy: (Math.random() - 0.5) * 0.18,
      size: 0.8 + Math.random() * 1.8,
      pulse: Math.random() * Math.PI * 2,
    }))
  }

  const resize = () => {
    const rect = canvas.getBoundingClientRect()
    canvas.width = Math.floor(rect.width * dpr)
    canvas.height = Math.floor(rect.height * dpr)
    ctx.setTransform(dpr, 0, 0, dpr, 0, 0)
    seedStars()
  }

  const draw = () => {
    const rect = canvas.getBoundingClientRect()
    ctx.clearRect(0, 0, rect.width, rect.height)

    stars.forEach((star) => {
      star.x += star.vx
      star.y += star.vy
      star.pulse += 0.018

      if (star.x < -10) star.x = rect.width + 10
      if (star.x > rect.width + 10) star.x = -10
      if (star.y < -10) star.y = rect.height + 10
      if (star.y > rect.height + 10) star.y = -10

      const alpha = 0.34 + Math.sin(star.pulse) * 0.22
      ctx.beginPath()
      ctx.fillStyle = `rgba(147, 197, 253, ${alpha})`
      ctx.arc(star.x, star.y, star.size, 0, Math.PI * 2)
      ctx.fill()
    })

    for (let i = 0; i < stars.length; i += 1) {
      for (let j = i + 1; j < stars.length; j += 1) {
        const a = stars[i]
        const b = stars[j]
        const dx = a.x - b.x
        const dy = a.y - b.y
        const distance = Math.sqrt(dx * dx + dy * dy)
        if (distance < 120) {
          ctx.beginPath()
          ctx.strokeStyle = `rgba(96, 165, 250, ${(1 - distance / 120) * 0.16})`
          ctx.lineWidth = 1
          ctx.moveTo(a.x, a.y)
          ctx.lineTo(b.x, b.y)
          ctx.stroke()
        }
      }
    }

    rafId = requestAnimationFrame(draw)
  }

  resize()
  resizeHandler = resize
  window.addEventListener('resize', resize)
  rafId = requestAnimationFrame(draw)
}

const setupScrollReveals = () => {
  if (revealObserver) {
    revealObserver.disconnect()
  }

  const targets = document.querySelectorAll('.home-page .scroll-trigger')
  if (!targets.length) return

  if (!('IntersectionObserver' in window)) {
    targets.forEach((target) => target.classList.add('is-visible'))
    return
  }

  revealObserver = new IntersectionObserver((entries) => {
    entries.forEach((entry) => {
      if (!entry.isIntersecting) return
      entry.target.classList.add('is-visible')
      revealObserver?.unobserve(entry.target)
    })
  }, {
    threshold: 0.18,
    rootMargin: '0px 0px -10% 0px',
  })

  targets.forEach((target, index) => {
    if (!target.style.getPropertyValue('--reveal-delay')) {
      target.style.setProperty('--reveal-delay', `${Math.min(index * 80, 280)}ms`)
    }
    revealObserver.observe(target)
  })
}

onMounted(async () => {
  if ('scrollRestoration' in window.history) {
    window.history.scrollRestoration = 'manual'
  }
  scrollHomeToTop()
  requestAnimationFrame(scrollHomeToTop)
  setTimeout(scrollHomeToTop, 120)
  setTimeout(scrollHomeToTop, 520)
  updateTime()
  timeTimer = setInterval(updateTime, 1000)
  carouselTimer = setInterval(nextPhoto, 4800)
  heroVisible.value = true
  startTyping()

  try {
    const res = await getArticleList(1, 5)
    if (res.code === 200 && res.data?.records?.length) {
      articles.value = res.data.records
    } else if (siteConfig.content.articles?.length) {
      articles.value = siteConfig.content.articles
    }
  } catch (e) {
    if (siteConfig.content.articles?.length) {
      articles.value = siteConfig.content.articles
    }
  }

  try {
    const projRes = await getProjects('/project/list')
    if ((projRes.code === 200 || projRes.code === 0) && projRes.data?.length) {
      projectsData.value = projRes.data
    } else if (siteConfig.projects?.length) {
      projectsData.value = siteConfig.projects
    }
  } catch (e) {
    projectsData.value = siteConfig.projects || []
  }

  await nextTick()
  setupConstellation()
  setupScrollReveals()
})

onUnmounted(() => {
  if (timeTimer) clearInterval(timeTimer)
  if (carouselTimer) clearInterval(carouselTimer)
  if (typeTimer) clearInterval(typeTimer)
  if (rafId) cancelAnimationFrame(rafId)
  if (videoFadeFrame) cancelAnimationFrame(videoFadeFrame)
  if (videoResetTimer) clearTimeout(videoResetTimer)
  if (resizeHandler) window.removeEventListener('resize', resizeHandler)
  if (revealObserver) revealObserver.disconnect()
})
</script>

<style scoped>
.home-page {
  position: relative;
  min-height: 100vh;
  padding: 86px 24px 110px;
  overflow: hidden;
  isolation: isolate;
}

.home-page::before {
  content: '';
  position: fixed;
  inset: 0;
  z-index: -1;
  pointer-events: none;
  background:
    radial-gradient(circle 380px at var(--mouse-x, 52vw) var(--mouse-y, 42vh), rgba(96, 165, 250, 0.17), transparent 68%),
    radial-gradient(circle at 78% 24%, rgba(167, 139, 250, 0.12), transparent 32%),
    linear-gradient(225deg, rgba(147, 197, 253, 0.11), transparent 42%);
  transition: background 0.18s linear;
}

.star-canvas,
.hero-video-bg,
.hero-mesh-scene,
.hero-ambient {
  position: fixed;
  inset: 0;
  pointer-events: none;
}

.star-canvas {
  z-index: 0;
  width: 100%;
  height: 100%;
  opacity: 0.8;
}

.hero-video-bg {
  z-index: -2;
  overflow: hidden;
}

.hero-video-bg video {
  width: 100%;
  height: 112%;
  object-fit: cover;
  object-position: center center;
  filter: saturate(1.05) contrast(1.18) brightness(0.58) hue-rotate(8deg);
  transform: translateY(17%) scale(1.035);
  will-change: opacity;
}

.video-overlay {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(circle at 72% 42%, rgba(96, 165, 250, 0.18), transparent 36%),
    radial-gradient(circle at 35% 72%, rgba(167, 139, 250, 0.1), transparent 38%),
    linear-gradient(180deg, rgba(2, 6, 18, 0.42) 0%, rgba(4, 8, 20, 0.74) 48%, rgba(7, 8, 18, 0.98) 100%),
    linear-gradient(90deg, rgba(2, 6, 18, 0.96), transparent 34%, rgba(2, 6, 18, 0.78)),
    repeating-linear-gradient(0deg, rgba(255,255,255,0.025) 0 1px, transparent 1px 5px);
}

.hero-mesh-scene {
  z-index: -1;
  overflow: hidden;
  opacity: 0.92;
  transform: translate3d(0, calc(var(--scroll-drift, 0px) * 0.22), 0);
  transition: transform 0.14s linear;
}

.mesh-horizon {
  position: absolute;
  left: 8%;
  right: 8%;
  top: 58%;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(96, 165, 250, 0.64), rgba(167, 139, 250, 0.42), transparent);
  box-shadow: 0 0 28px rgba(96, 165, 250, 0.34);
  animation: meshHorizon 5.8s ease-in-out infinite;
}

.mesh-plane {
  position: absolute;
  left: -16%;
  right: -16%;
  bottom: -14%;
  height: 46%;
  background:
    linear-gradient(90deg, rgba(96, 165, 250, 0.14) 1px, transparent 1px) 0 0 / 72px 72px,
    linear-gradient(0deg, rgba(167, 139, 250, 0.12) 1px, transparent 1px) 0 0 / 72px 72px;
  transform: rotateX(66deg);
  transform-origin: 50% 100%;
  filter: drop-shadow(0 0 26px rgba(96, 165, 250, 0.16));
  mask-image: linear-gradient(180deg, transparent, #000 24%, #000 82%, transparent);
  animation: meshPlane 7.2s linear infinite;
}

.mesh-mountain {
  position: absolute;
  left: -6%;
  right: -6%;
  bottom: 18%;
  height: 30%;
  clip-path: polygon(0 82%, 8% 58%, 17% 72%, 27% 33%, 38% 78%, 48% 44%, 58% 70%, 69% 28%, 79% 75%, 88% 50%, 100% 78%, 100% 100%, 0 100%);
  background:
    linear-gradient(180deg, rgba(96, 165, 250, 0.16), rgba(7, 10, 28, 0.58)),
    linear-gradient(90deg, rgba(96, 165, 250, 0.18), rgba(167, 139, 250, 0.12));
  opacity: 0.36;
  filter: drop-shadow(0 0 32px rgba(96, 165, 250, 0.12));
  animation: meshMountain 9s ease-in-out infinite;
}

.hero-mesh-scene .mountain-front {
  bottom: 7%;
  height: 36%;
  opacity: 0.5;
  clip-path: polygon(0 76%, 9% 48%, 17% 68%, 29% 22%, 39% 74%, 49% 40%, 58% 68%, 69% 16%, 80% 72%, 90% 42%, 100% 74%, 100% 100%, 0 100%);
  animation-delay: -2.5s;
}

.mesh-pulse {
  position: absolute;
  width: 220px;
  height: 220px;
  border: 1px solid rgba(96, 165, 250, 0.28);
  border-radius: 999px;
  filter: blur(0.2px) drop-shadow(0 0 24px rgba(96, 165, 250, 0.2));
  opacity: 0;
  animation: meshPulse 6s ease-out infinite;
}

.pulse-a {
  left: 16%;
  top: 46%;
}

.pulse-b {
  right: 18%;
  top: 34%;
  animation-delay: 2.2s;
}

.hero-ambient {
  z-index: 0;
  overflow: hidden;
}

.ambient-bar {
  position: absolute;
  display: block;
  height: 1px;
  width: 42vw;
  background: linear-gradient(90deg, transparent, rgba(96, 165, 250, 0.58), rgba(167, 139, 250, 0.34), transparent);
  filter: drop-shadow(0 0 18px rgba(96, 165, 250, 0.32));
  animation: railFloat 8s ease-in-out infinite;
}

.bar-one {
  top: 22%;
  right: -8vw;
  transform: rotate(-10deg);
}

.bar-two {
  left: -10vw;
  bottom: 24%;
  animation-delay: 1.2s;
  transform: rotate(8deg);
}

.bar-three {
  width: 26vw;
  right: 18vw;
  bottom: 9%;
  animation-delay: 2.4s;
  transform: rotate(2deg);
}

.hero-section,
.timeline-section,
.bento-grid {
  position: relative;
  z-index: 1;
  width: min(1240px, 100%);
  margin: 0 auto;
}

.hero-section {
  min-height: calc(100vh - 132px);
  display: grid;
  grid-template-columns: minmax(0, 0.95fr) minmax(360px, 0.78fr);
  align-items: center;
  gap: 46px;
  padding: 34px 0 54px;
  opacity: 0;
  transform: translateY(22px);
  transition: opacity 0.72s var(--ease-out), transform 0.72s var(--ease-out);
}

.hero-section.hero-enter {
  opacity: 1;
  transform: translateY(0);
}

.liquid-glass {
  position: relative;
  overflow: hidden;
  border: 0 !important;
  background:
    linear-gradient(135deg, rgba(255, 255, 255, 0.035), rgba(255, 255, 255, 0.01)),
    rgba(255, 255, 255, 0.01) !important;
  background-blend-mode: luminosity;
  box-shadow: inset 0 1px 1px rgba(255, 255, 255, 0.1), 0 20px 54px rgba(0, 0, 0, 0.28) !important;
  backdrop-filter: blur(4px) saturate(1.22);
  -webkit-backdrop-filter: blur(4px) saturate(1.22);
}

.liquid-glass::before {
  content: '';
  position: absolute;
  inset: 0;
  z-index: 2;
  border-radius: inherit;
  padding: 1.4px;
  background: linear-gradient(
    180deg,
    rgba(255,255,255,0.45) 0%,
    rgba(255,255,255,0.15) 20%,
    rgba(255,255,255,0) 40%,
    rgba(255,255,255,0) 60%,
    rgba(255,255,255,0.15) 80%,
    rgba(255,255,255,0.45) 100%
  );
  -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  pointer-events: none;
}

.liquid-glass > * {
  position: relative;
  z-index: 3;
}

.hero-copy,
.hero-visual {
  min-width: 0;
}

.hero-kicker {
  width: fit-content;
  display: inline-flex;
  align-items: center;
  gap: 9px;
  margin-bottom: 18px;
  padding: 8px 11px;
  border: 1px solid rgba(147, 197, 253, 0.28);
  border-radius: 999px;
  background: rgba(8, 14, 27, 0.32);
  backdrop-filter: blur(12px);
  color: rgba(199, 251, 255, 0.72);
  font: 700 11px/1 'SF Mono', 'Consolas', monospace;
  letter-spacing: 0.12em;
}

.kicker-dot {
  width: 8px;
  height: 8px;
  border-radius: 999px;
  background: var(--ink-blue);
  box-shadow: 0 0 18px rgba(147, 197, 253, 0.72);
  animation: livePulse 1.8s ease-in-out infinite;
}

.hero-copy h1 {
  max-width: 760px;
  margin: 0;
  color: #f8fbff;
  font-size: clamp(4.8rem, 8vw, 7.4rem);
  line-height: 0.9;
  font-weight: 900;
  letter-spacing: 0;
  text-shadow: 0 0 38px rgba(96, 165, 250, 0.13);
}

.hero-copy h1 span {
  display: block;
  color: transparent;
  background: linear-gradient(90deg, #ffffff, #93c5fd 46%, #a78bfa 88%);
  -webkit-background-clip: text;
  background-clip: text;
  text-shadow: 0 0 34px rgba(96, 165, 250, 0.12);
}

.hero-role {
  margin: 24px 0 0;
  color: #93c5fd;
  font-size: 1.08rem;
  font-weight: 800;
}

.hero-slogan {
  max-width: 650px;
  margin: 16px 0 0;
  color: rgba(226, 239, 255, 0.74);
  font-size: 1.02rem;
  line-height: 1.86;
}

.signal-line {
  width: min(650px, 100%);
  min-height: 50px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 24px;
  padding: 12px 16px;
  border: 1px solid rgba(147, 197, 253, 0.18);
  border-radius: 999px;
  background: rgba(5, 8, 22, 0.38);
  color: rgba(238, 247, 255, 0.82);
  font: 700 13px/1.5 'SF Mono', 'Consolas', monospace;
  box-shadow: inset 0 0 28px rgba(96, 165, 250, 0.045), 0 18px 46px rgba(0, 0, 0, 0.18);
  backdrop-filter: blur(16px);
}

.signal-prompt {
  color: #93c5fd;
  letter-spacing: 0.12em;
}

.cursor-blink {
  color: #60a5fa;
  animation: blink 0.8s step-end infinite;
}

.hero-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 32px;
}

.hero-btn {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  min-height: 48px;
  padding: 0 20px;
  overflow: hidden;
  border: 1px solid rgba(148, 226, 255, 0.22);
  border-radius: 999px;
  background: rgba(8, 14, 27, 0.38);
  color: rgba(238, 247, 255, 0.86);
  font-weight: 800;
  text-decoration: none;
  backdrop-filter: blur(12px);
  transition: transform 0.22s var(--ease-out), border-color 0.22s, box-shadow 0.22s, background 0.22s;
}

.hero-btn::after {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(110deg, transparent, rgba(255, 255, 255, 0.18), transparent);
  transform: translateX(-120%);
  transition: transform 0.5s var(--ease-out);
}

.hero-btn:hover::after {
  transform: translateX(120%);
}

.hero-btn svg {
  width: 17px;
  height: 17px;
  fill: none;
  stroke: currentColor;
  stroke-linecap: round;
  stroke-linejoin: round;
  stroke-width: 2;
}

.hero-btn.primary {
  border-color: rgba(96, 165, 250, 0.6);
  background: linear-gradient(135deg, rgba(96, 165, 250, 0.92), rgba(167, 139, 250, 0.86));
  color: #06101f;
  box-shadow: 0 18px 46px rgba(96, 165, 250, 0.2);
}

.hero-btn:hover {
  transform: translateY(-2px);
  border-color: rgba(96, 165, 250, 0.72);
  box-shadow: 0 18px 42px rgba(96, 165, 250, 0.14);
}

.hero-metrics {
  display: grid;
  grid-template-columns: repeat(4, minmax(92px, 1fr));
  gap: 12px;
  max-width: 660px;
  margin-top: 32px;
}

.metric-item {
  position: relative;
  min-height: 94px;
  padding: 16px 14px;
  overflow: hidden;
  border-radius: 8px;
}

.metric-line {
  position: absolute;
  left: 0;
  top: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, #60a5fa, #93c5fd, #a78bfa);
  transform: scaleX(0.4);
  transform-origin: left;
  transition: transform 0.28s var(--ease-out);
}

.metric-item:hover .metric-line {
  transform: scaleX(1);
}

.metric-item strong {
  display: block;
  color: #eaf7ff;
  font-size: 1.8rem;
  line-height: 1;
}

.metric-item span:last-child {
  display: block;
  margin-top: 10px;
  color: rgba(226, 239, 255, 0.52);
  font-size: 0.82rem;
}

.hero-visual {
  display: grid;
  gap: 14px;
}

.visual-frame {
  position: relative;
  aspect-ratio: 4 / 5;
  min-height: 540px;
  overflow: hidden;
  border: 1px solid rgba(148, 226, 255, 0.32);
  border-radius: 8px;
  background:
    radial-gradient(circle at 48% 25%, rgba(96, 165, 250, 0.18), transparent 32%),
    rgba(7, 11, 21, 0.58);
  box-shadow: 0 34px 96px rgba(0, 0, 0, 0.5), inset 0 0 84px rgba(96, 165, 250, 0.1);
  backdrop-filter: blur(10px) saturate(1.18);
  transform-style: preserve-3d;
  transform: perspective(1100px) rotateX(var(--tilt-x, 0deg)) rotateY(var(--tilt-y, 0deg));
  transition: transform 0.18s linear, border-color 0.3s, box-shadow 0.3s;
  animation: frameHover 8s ease-in-out infinite;
}

.visual-frame::before {
  content: '';
  position: absolute;
  inset: 0;
  z-index: 5;
  border-radius: inherit;
  padding: 1.4px;
  background: linear-gradient(180deg, rgba(255,255,255,0.45), rgba(255,255,255,0.12) 22%, transparent 43%, transparent 58%, rgba(255,255,255,0.14) 78%, rgba(255,255,255,0.42));
  -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  pointer-events: none;
}

.visual-frame::after {
  content: '';
  position: absolute;
  z-index: 4;
  left: -30%;
  right: -30%;
  top: -18%;
  height: 28%;
  pointer-events: none;
  background:
    linear-gradient(180deg, transparent, rgba(96, 165, 250, 0.18), rgba(147, 197, 253, 0.08), transparent),
    repeating-linear-gradient(90deg, rgba(255, 255, 255, 0.08) 0 1px, transparent 1px 11px);
  mix-blend-mode: screen;
  filter: blur(0.2px);
  animation: portraitScan 4.8s ease-in-out infinite;
}

.frame-topbar {
  position: absolute;
  z-index: 5;
  top: 0;
  left: 0;
  right: 0;
  height: 48px;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 16px;
  border-bottom: 1px solid rgba(148, 226, 255, 0.16);
  background: rgba(5, 8, 22, 0.44);
  backdrop-filter: blur(16px);
}

.frame-topbar span {
  width: 8px;
  height: 8px;
  border-radius: 999px;
  background: #60a5fa;
  box-shadow: 0 0 14px rgba(96, 165, 250, 0.6);
}

.frame-topbar span:nth-child(2) {
  background: #93c5fd;
}

.frame-topbar span:nth-child(3) {
  background: #a78bfa;
}

.frame-topbar strong {
  margin-left: auto;
  color: rgba(238, 247, 255, 0.72);
  font: 700 11px/1 'SF Mono', 'Consolas', monospace;
  letter-spacing: 0.12em;
}

.visual-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  filter: saturate(1.02) contrast(1.12) brightness(0.76) hue-rotate(2deg);
  transform: scale(1.06) translate3d(0, 0, 0);
  transform-origin: 50% 38%;
  will-change: transform, filter;
  animation: portraitDrift 9.6s ease-in-out infinite;
  transition: transform 0.8s var(--ease-out), filter 0.8s var(--ease-out);
}

.visual-frame:hover .visual-image {
  filter: saturate(1.18) contrast(1.16) brightness(0.86) hue-rotate(-3deg);
  transform: scale(1.09) translate3d(1.5%, -1.5%, 0);
  animation-play-state: paused;
}

.digital-slices {
  position: absolute;
  inset: 56px 0 88px;
  z-index: 2;
  pointer-events: none;
  mix-blend-mode: screen;
}

.digital-slices span {
  position: absolute;
  left: 0;
  right: 0;
  height: 16%;
  background:
    linear-gradient(90deg, rgba(96, 165, 250, 0.02), rgba(147, 197, 253, 0.22), rgba(167, 139, 250, 0.12), transparent),
    repeating-linear-gradient(90deg, rgba(255, 255, 255, 0.1) 0 1px, transparent 1px 10px);
  opacity: 0;
  filter: blur(0.2px);
  clip-path: inset(0 0 0 0);
  animation: sliceGlitch 5.4s ease-in-out infinite;
}

.digital-slices .slice-a {
  top: 16%;
}

.digital-slices .slice-b {
  top: 42%;
  animation-delay: 1.4s;
}

.digital-slices .slice-c {
  top: 64%;
  animation-delay: 2.8s;
}

.face-vector {
  position: absolute;
  z-index: 3;
  inset: 18% 18% 24%;
  pointer-events: none;
  opacity: 0.82;
  mix-blend-mode: screen;
}

.face-vector span {
  position: absolute;
  width: 5px;
  height: 5px;
  border-radius: 999px;
  background: rgba(219, 234, 254, 0.9);
  box-shadow: 0 0 16px rgba(96, 165, 250, 0.74);
  animation: vectorPulse 3.6s ease-in-out infinite;
}

.face-vector span:nth-child(1) { left: 38%; top: 18%; animation-delay: 0.05s; }
.face-vector span:nth-child(2) { left: 55%; top: 18%; animation-delay: 0.15s; }
.face-vector span:nth-child(3) { left: 31%; top: 29%; animation-delay: 0.25s; }
.face-vector span:nth-child(4) { left: 64%; top: 29%; animation-delay: 0.35s; }
.face-vector span:nth-child(5) { left: 44%; top: 36%; animation-delay: 0.45s; }
.face-vector span:nth-child(6) { left: 52%; top: 36%; animation-delay: 0.55s; }
.face-vector span:nth-child(7) { left: 48%; top: 48%; animation-delay: 0.65s; }
.face-vector span:nth-child(8) { left: 38%; top: 58%; animation-delay: 0.75s; }
.face-vector span:nth-child(9) { left: 58%; top: 58%; animation-delay: 0.85s; }
.face-vector span:nth-child(10) { left: 43%; top: 68%; animation-delay: 0.95s; }
.face-vector span:nth-child(11) { left: 53%; top: 68%; animation-delay: 1.05s; }
.face-vector span:nth-child(12) { left: 28%; top: 45%; animation-delay: 1.15s; }
.face-vector span:nth-child(13) { left: 68%; top: 45%; animation-delay: 1.25s; }
.face-vector span:nth-child(14) { left: 34%; top: 76%; animation-delay: 1.35s; }
.face-vector span:nth-child(15) { left: 62%; top: 76%; animation-delay: 1.45s; }
.face-vector span:nth-child(16) { left: 20%; top: 62%; animation-delay: 1.55s; }
.face-vector span:nth-child(17) { left: 76%; top: 62%; animation-delay: 1.65s; }
.face-vector span:nth-child(18) { left: 48%; top: 82%; animation-delay: 1.75s; }

.face-vector::before,
.face-vector::after {
  content: '';
  position: absolute;
  inset: 12% 18% 18%;
  border: 1px solid rgba(147, 197, 253, 0.22);
  border-radius: 45% 45% 46% 46%;
  filter: drop-shadow(0 0 14px rgba(96, 165, 250, 0.22));
  animation: vectorFace 4.8s ease-in-out infinite;
}

.face-vector::after {
  inset: 24% 26% 26%;
  border-style: dashed;
  border-color: rgba(167, 139, 250, 0.25);
  animation-delay: -1.2s;
}

.holo-rain {
  position: absolute;
  inset: 54px 20px 90px;
  z-index: 3;
  pointer-events: none;
  overflow: hidden;
  opacity: 0.58;
  mix-blend-mode: screen;
}

.holo-rain span {
  position: absolute;
  top: -22%;
  width: 1px;
  height: 72px;
  background: linear-gradient(180deg, transparent, rgba(147, 197, 253, 0.76), transparent);
  animation: rainFall 4.6s linear infinite;
}

.holo-rain span:nth-child(1) { left: 8%; animation-delay: 0s; }
.holo-rain span:nth-child(2) { left: 16%; animation-delay: 1.1s; height: 52px; }
.holo-rain span:nth-child(3) { left: 24%; animation-delay: 0.4s; }
.holo-rain span:nth-child(4) { left: 34%; animation-delay: 1.8s; height: 64px; }
.holo-rain span:nth-child(5) { left: 42%; animation-delay: 0.7s; }
.holo-rain span:nth-child(6) { left: 52%; animation-delay: 2.1s; height: 58px; }
.holo-rain span:nth-child(7) { left: 61%; animation-delay: 0.2s; }
.holo-rain span:nth-child(8) { left: 69%; animation-delay: 1.5s; height: 54px; }
.holo-rain span:nth-child(9) { left: 78%; animation-delay: 0.9s; }
.holo-rain span:nth-child(10) { left: 86%; animation-delay: 2.5s; height: 68px; }
.holo-rain span:nth-child(11) { left: 92%; animation-delay: 1.2s; }
.holo-rain span:nth-child(12) { left: 48%; animation-delay: 3s; height: 46px; }

.energy-outline {
  position: absolute;
  z-index: 3;
  left: 50%;
  top: 47%;
  width: 62%;
  height: 70%;
  transform: translate(-50%, -50%);
  border-radius: 46% 46% 40% 40%;
  pointer-events: none;
  background:
    radial-gradient(ellipse at 50% 20%, transparent 42%, rgba(96, 165, 250, 0.14) 43%, transparent 50%),
    linear-gradient(90deg, transparent 0 16%, rgba(96, 165, 250, 0.42) 17%, transparent 19% 81%, rgba(167, 139, 250, 0.38) 83%, transparent 85%);
  filter: drop-shadow(0 0 24px rgba(96, 165, 250, 0.25));
  opacity: 0.66;
  animation: outlineBreathe 4.8s ease-in-out infinite;
}

.visual-grid {
  position: absolute;
  inset: 0;
  z-index: 2;
  background:
    linear-gradient(180deg, rgba(5, 8, 22, 0.05), rgba(5, 8, 22, 0.5)),
    repeating-linear-gradient(0deg, rgba(255, 255, 255, 0.032) 0 1px, transparent 1px 5px),
    repeating-linear-gradient(90deg, rgba(96, 165, 250, 0.05) 0 1px, transparent 1px 74px);
  pointer-events: none;
}

.visual-grid::after {
  content: '';
  position: absolute;
  inset: 0;
  background:
    radial-gradient(circle at 48% 30%, rgba(255, 255, 255, 0.14), transparent 16%),
    linear-gradient(120deg, transparent 34%, rgba(96, 165, 250, 0.12) 48%, transparent 62%);
  opacity: 0.58;
  mix-blend-mode: screen;
  animation: holoBreath 5.8s ease-in-out infinite;
}

.avatar-orbit {
  position: absolute;
  z-index: 3;
  inset: 68px 42px 112px;
  pointer-events: none;
  opacity: 0.76;
}

.avatar-orbit span {
  position: absolute;
  border-radius: 999px;
  border: 1px solid rgba(147, 197, 253, 0.28);
  box-shadow: inset 0 0 34px rgba(96, 165, 250, 0.06), 0 0 24px rgba(96, 165, 250, 0.1);
  animation: scanSpin 12s linear infinite;
}

.avatar-orbit span:nth-child(1) {
  inset: 7% 15%;
}

.avatar-orbit span:nth-child(2) {
  inset: 18% 6%;
  border-style: dashed;
  border-color: rgba(167, 139, 250, 0.34);
  animation-duration: 8s;
  animation-direction: reverse;
}

.avatar-orbit span:nth-child(3) {
  inset: 31% 23%;
  border-color: rgba(255, 255, 255, 0.18);
  animation-duration: 5.6s;
}

.scanner-dial {
  position: absolute;
  z-index: 3;
  right: 30px;
  bottom: 94px;
  width: 116px;
  height: 116px;
  border: 1px solid rgba(147, 197, 253, 0.42);
  border-radius: 999px;
  box-shadow: inset 0 0 36px rgba(147, 197, 253, 0.12), 0 0 34px rgba(147, 197, 253, 0.12);
}

.scanner-dial::before {
  content: '';
  position: absolute;
  inset: 12px;
  border: 1px dashed rgba(96, 165, 250, 0.34);
  border-radius: inherit;
  animation: scanSpin 7s linear infinite;
}

.scanner-dial span {
  position: absolute;
  left: 50%;
  top: 50%;
  width: 44%;
  height: 1px;
  background: linear-gradient(90deg, rgba(96, 165, 250, 0.85), transparent);
  transform-origin: left center;
  animation: scanSpin 3.2s linear infinite;
}

.scanner-dial span:nth-child(2) {
  animation-delay: -1s;
  opacity: 0.54;
}

.scanner-dial span:nth-child(3) {
  animation-delay: -2s;
  opacity: 0.32;
}

.visual-readout {
  position: absolute;
  left: 28px;
  right: 28px;
  bottom: 28px;
  z-index: 5;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 14px 16px;
  border: 1px solid rgba(148, 226, 255, 0.24);
  border-radius: 8px;
  background: rgba(5, 8, 22, 0.68);
  backdrop-filter: blur(14px);
}

.visual-readout span,
.visual-readout strong {
  font-family: 'SF Mono', 'Consolas', monospace;
  color: rgba(238, 247, 255, 0.82);
  font-size: 0.82rem;
}

.visual-readout span {
  color: #93c5fd;
}

.visual-next {
  position: absolute;
  top: 60px;
  right: 28px;
  z-index: 6;
  width: 42px;
  height: 42px;
  display: grid;
  place-items: center;
  border: 1px solid rgba(148, 226, 255, 0.28);
  border-radius: 8px;
  background: rgba(5, 8, 22, 0.6);
  color: #eaf7ff;
  cursor: pointer;
  transition: transform 0.22s var(--ease-out), border-color 0.22s, background 0.22s;
}

.visual-next svg {
  width: 18px;
  height: 18px;
  fill: none;
  stroke: currentColor;
  stroke-linecap: round;
  stroke-linejoin: round;
  stroke-width: 2.2;
}

.visual-next:hover {
  transform: translateX(2px);
  border-color: #60a5fa;
  background: rgba(96, 165, 250, 0.12);
}

.journey-panel {
  padding: 16px;
  border-radius: 8px;
}

.signal-stack {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
}

.signal-stack span {
  min-height: 42px;
  display: grid;
  place-items: center;
  border: 1px solid rgba(147, 197, 253, 0.16);
  border-radius: 999px;
  background: rgba(5, 8, 22, 0.34);
  color: rgba(226, 239, 255, 0.72);
  font: 800 11px/1 'SF Mono', 'Consolas', monospace;
  letter-spacing: 0.04em;
  backdrop-filter: blur(12px);
}

.journey-panel-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
  color: rgba(238, 247, 255, 0.72);
  font: 700 12px/1 'SF Mono', 'Consolas', monospace;
  letter-spacing: 0.12em;
}

.journey-panel-head strong {
  color: #93c5fd;
  text-align: right;
}

.journey-panel-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
}

.journey-panel-list div {
  min-height: 72px;
  padding: 10px;
  border: 1px solid rgba(148, 226, 255, 0.12);
  border-radius: 8px;
  background: rgba(5, 8, 22, 0.35);
}

.journey-panel-list span {
  display: block;
  color: #ffbd66;
  font: 800 0.72rem/1.1 'SF Mono', 'Consolas', monospace;
}

.journey-panel-list strong {
  display: block;
  margin-top: 7px;
  color: rgba(238, 247, 255, 0.88);
  font-size: 0.86rem;
  line-height: 1.5;
}

.timeline-section,
.bento-grid {
  opacity: 0;
  transform: translateY(30px);
  transition: opacity 0.72s var(--ease-out), transform 0.72s var(--ease-out);
}

.timeline-section.content-enter,
.bento-grid.content-enter {
  opacity: 1;
  transform: translateY(0);
}

.timeline-section {
  margin-top: 62px;
  padding: 10px 0 20px;
}

.scroll-trigger {
  opacity: 0;
  transform: translate3d(0, 26px, 0) rotateX(2deg);
  filter: blur(6px);
  transition:
    opacity 0.72s var(--ease-out),
    transform 0.72s var(--ease-out),
    filter 0.72s var(--ease-out);
  transition-delay: var(--reveal-delay, 0ms);
  will-change: transform, opacity, filter;
}

.scroll-trigger.is-visible {
  opacity: 1;
  transform: translate3d(0, 0, 0) rotateX(0deg);
  filter: blur(0);
}

.section-head {
  display: flex;
  align-items: end;
  justify-content: space-between;
  gap: 18px;
  margin-bottom: 28px;
}

.section-head span {
  color: #93c5fd;
  font: 800 12px/1 'SF Mono', 'Consolas', monospace;
  letter-spacing: 0.16em;
  text-transform: uppercase;
}

.section-head h2 {
  margin: 0;
  color: #f8fbff;
  font-size: 2.4rem;
  line-height: 1;
}

.section-head p {
  max-width: 520px;
  margin: 0;
  color: rgba(226, 239, 255, 0.62);
  line-height: 1.8;
  text-align: right;
}

.life-timeline-track {
  position: relative;
  display: grid;
  grid-template-columns: repeat(3, minmax(240px, 1fr));
  gap: 18px;
  padding: 8px 4px 20px;
}

.life-timeline-track::before {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  top: 46px;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(96, 165, 250, 0.5), rgba(255, 189, 102, 0.35), transparent);
}

.life-timeline-item {
  position: relative;
}

.life-node {
  position: relative;
  z-index: 2;
  display: grid;
  place-items: center;
  width: 78px;
  height: 78px;
  margin: 0 auto 16px;
  border: 1px solid rgba(147, 197, 253, 0.36);
  border-radius: 999px;
  background: rgba(5, 8, 22, 0.82);
  box-shadow: 0 0 28px rgba(96, 165, 250, 0.12);
}

.life-node span {
  color: #93c5fd;
  font: 900 13px/1 'SF Mono', 'Consolas', monospace;
}

.life-card {
  min-height: 245px;
  padding: 20px;
  border: 1px solid rgba(148, 226, 255, 0.15);
  border-radius: 8px;
  background: rgba(8, 14, 27, 0.54);
  transition: transform 0.24s var(--ease-out), border-color 0.24s, box-shadow 0.24s;
}

.life-card:hover {
  transform: translateY(-5px);
  border-color: rgba(96, 165, 250, 0.44);
  box-shadow: 0 18px 42px rgba(96, 165, 250, 0.1);
}

.life-card h3 {
  margin: 0 0 10px;
  color: #f8fbff;
  font-size: 1.02rem;
}

.life-card p {
  margin: 0;
  color: rgba(226, 239, 255, 0.62);
  font-size: 0.88rem;
  line-height: 1.72;
}

.life-tags {
  display: flex;
  gap: 7px;
  flex-wrap: wrap;
  margin-top: 16px;
}

.life-tags span {
  padding: 5px 8px;
  border: 1px solid rgba(147, 197, 253, 0.18);
  border-radius: 999px;
  background: rgba(147, 197, 253, 0.07);
  color: rgba(199, 251, 255, 0.82);
  font-size: 0.72rem;
}

.bento-grid {
  display: grid;
  grid-template-columns: repeat(12, 1fr);
  gap: 16px;
  margin-top: 48px;
}

.bento-card {
  position: relative;
  min-height: 224px;
  padding: 22px;
  overflow: hidden;
  border: 1px solid rgba(148, 226, 255, 0.16);
  border-radius: 8px;
  background:
    linear-gradient(135deg, rgba(17, 27, 49, 0.82), rgba(8, 14, 27, 0.66)),
    rgba(8, 14, 27, 0.68);
  box-shadow: var(--shadow);
  backdrop-filter: blur(16px);
  transition: transform 0.25s var(--ease-out), border-color 0.25s, box-shadow 0.25s;
}

.bento-card::after {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(120deg, transparent, rgba(96, 165, 250, 0.08), transparent);
  transform: translateX(-100%);
  transition: transform 0.6s var(--ease-out);
  pointer-events: none;
}

.bento-card:hover {
  transform: translateY(-5px);
  border-color: rgba(96, 165, 250, 0.38);
  box-shadow: var(--shadow-hover);
}

.bento-card:hover::after {
  transform: translateX(100%);
}

.feature-card,
.articles-card {
  grid-column: span 6;
}

.connect-card {
  grid-column: span 12;
}

.card-heading {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  margin-bottom: 18px;
  color: #eaf7ff;
  font-weight: 800;
}

.card-heading a {
  color: rgba(226, 239, 255, 0.52);
  font-size: 0.82rem;
  font-weight: 700;
}

.card-heading a:hover {
  color: #60a5fa;
}

.project-focus {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: 58px 1fr;
  gap: 16px;
  align-items: start;
}

.project-mark {
  width: 58px;
  height: 58px;
  display: grid;
  place-items: center;
  border: 1px solid rgba(96, 165, 250, 0.34);
  border-radius: 8px;
  background: rgba(96, 165, 250, 0.09);
  color: #60a5fa;
  font-size: 1.6rem;
  overflow: hidden;
}

.project-mark img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.project-focus h2 {
  margin: 0 0 8px;
  color: #fff;
  font-size: 1.45rem;
}

.project-focus p {
  margin: 0;
  color: rgba(226, 239, 255, 0.66);
  line-height: 1.75;
}

.tech-stack {
  position: relative;
  z-index: 1;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 20px;
}

.tech-stack span {
  padding: 7px 10px;
  border: 1px solid rgba(148, 226, 255, 0.16);
  border-radius: 7px;
  background: rgba(5, 8, 22, 0.36);
  color: rgba(226, 239, 255, 0.78);
  font-size: 0.82rem;
}

.article-row {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: 64px 1fr;
  gap: 14px;
  align-items: start;
  padding: 13px 0;
  border-top: 1px solid rgba(148, 226, 255, 0.11);
  text-decoration: none;
  transition: padding-left 0.2s, color 0.2s;
}

.article-row:first-of-type {
  border-top: 0;
}

.article-row:hover {
  padding-left: 6px;
}

.article-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.article-row time {
  color: #22d3ee;
  font-family: 'SF Mono', 'Consolas', monospace;
  font-size: 0.82rem;
}

.article-cat {
  width: fit-content;
  max-width: 100%;
  font-size: 0.7rem;
  color: rgba(148, 226, 255, 0.5);
  padding: 2px 6px;
  border: 1px solid rgba(148, 226, 255, 0.12);
  border-radius: 4px;
  white-space: nowrap;
}

.article-body {
  min-width: 0;
}

.article-body strong {
  color: rgba(238, 247, 255, 0.88);
  line-height: 1.55;
  display: block;
}

.article-summary {
  margin: 6px 0 0;
  color: rgba(226, 239, 255, 0.45);
  font-size: 0.82rem;
  line-height: 1.65;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.empty-hint {
  padding: 20px 0;
  text-align: center;
  color: rgba(226, 239, 255, 0.3);
  font-size: 0.88rem;
}

.quick-nav {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin-top: 8px;
}

.quick-link {
  display: flex;
  align-items: center;
  gap: 12px;
  min-height: 86px;
  padding: 14px;
  border: 1px solid rgba(148, 226, 255, 0.13);
  border-radius: 8px;
  background: rgba(5, 8, 22, 0.32);
  text-decoration: none;
  transition: transform 0.2s var(--ease-out), border-color 0.2s, background 0.2s;
}

.quick-link:hover {
  border-color: rgba(96, 165, 250, 0.4);
  background: rgba(96, 165, 250, 0.06);
  transform: translateY(-2px);
}

.quick-icon {
  width: 38px;
  height: 38px;
  display: grid;
  place-items: center;
  border-radius: 8px;
  color: #60a5fa;
  background: rgba(96, 165, 250, 0.1);
  flex-shrink: 0;
}

.quick-link strong {
  display: block;
  color: #f8fbff;
  font-size: 14px;
  font-weight: 700;
}

.quick-link small {
  display: block;
  margin-top: 2px;
  color: rgba(226, 239, 255, 0.5);
  font-size: 12px;
}

@keyframes railFloat {
  0%, 100% { opacity: 0.28; transform: translateY(0) rotate(var(--rail-rotate, -10deg)); }
  50% { opacity: 0.76; transform: translateY(18px) rotate(var(--rail-rotate, -10deg)); }
}

@keyframes livePulse {
  0%, 100% { opacity: 0.56; transform: scale(0.86); }
  50% { opacity: 1; transform: scale(1); }
}

@keyframes blink {
  0%, 50% { opacity: 1; }
  51%, 100% { opacity: 0; }
}

@keyframes scanSpin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@keyframes frameHover {
  0%, 100% { translate: 0 0; }
  50% { translate: 0 -8px; }
}

@keyframes meshHorizon {
  0%, 100% { opacity: 0.32; transform: translateY(-18px) scaleX(0.82); }
  50% { opacity: 0.88; transform: translateY(18px) scaleX(1); }
}

@keyframes meshPlane {
  from { background-position: 0 0, 0 0; }
  to { background-position: 0 72px, 0 72px; }
}

@keyframes meshMountain {
  0%, 100% { transform: translate3d(-1%, 0, 0) scale(1.02); }
  50% { transform: translate3d(1.2%, 1.5%, 0) scale(1.04); }
}

@keyframes meshPulse {
  0% { opacity: 0; transform: scale(0.25); }
  12% { opacity: 0.52; }
  100% { opacity: 0; transform: scale(1.8); }
}

@keyframes sliceGlitch {
  0%, 72%, 100% { opacity: 0; transform: translate3d(0, 0, 0) skewX(0deg); }
  76% { opacity: 0.7; transform: translate3d(-4%, 0, 0) skewX(-8deg); }
  79% { opacity: 0.36; transform: translate3d(5%, 0, 0) skewX(7deg); }
  84% { opacity: 0; transform: translate3d(0, 0, 0) skewX(0deg); }
}

@keyframes vectorPulse {
  0%, 100% { opacity: 0.28; transform: scale(0.72); }
  48% { opacity: 1; transform: scale(1.18); }
}

@keyframes vectorFace {
  0%, 100% { opacity: 0.3; transform: scale(0.98); }
  50% { opacity: 0.66; transform: scale(1.03); }
}

@keyframes rainFall {
  from { transform: translateY(-18%); opacity: 0; }
  18% { opacity: 0.74; }
  to { transform: translateY(140%); opacity: 0; }
}

@keyframes outlineBreathe {
  0%, 100% { opacity: 0.48; transform: translate(-50%, -50%) scale(0.98); }
  50% { opacity: 0.82; transform: translate(-50%, -50%) scale(1.025); }
}

@keyframes portraitDrift {
  0%, 100% { transform: scale(1.055) translate3d(-0.8%, 0.4%, 0); }
  28% { transform: scale(1.075) translate3d(1.2%, -1.2%, 0); }
  58% { transform: scale(1.065) translate3d(0.4%, 1.1%, 0); }
  78% { transform: scale(1.085) translate3d(-1%, -0.6%, 0); }
}

@keyframes portraitScan {
  0% { transform: translateY(-120%) rotate(-8deg); opacity: 0; }
  18% { opacity: 0.75; }
  52% { opacity: 0.42; }
  100% { transform: translateY(560%) rotate(-8deg); opacity: 0; }
}

@keyframes holoBreath {
  0%, 100% { opacity: 0.42; transform: translate3d(0, 0, 0); }
  50% { opacity: 0.72; transform: translate3d(1.6%, -1.2%, 0); }
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

@media (max-width: 980px) {
  .hero-section {
    grid-template-columns: 1fr;
    min-height: auto;
  }

  .hero-copy h1 {
    font-size: 5rem;
  }

  .visual-frame {
    min-height: 440px;
    aspect-ratio: 16 / 10;
  }

  .feature-card,
  .articles-card {
    grid-column: span 6;
  }

  .quick-nav {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 760px) {
  .home-page {
    padding: 42px 14px 78px;
  }

  .hero-section {
    gap: 26px;
    padding-top: 14px;
  }

  .hero-kicker {
    max-width: 100%;
    flex-wrap: wrap;
    line-height: 1.45;
  }

  .hero-copy h1 {
    font-size: 2.86rem;
    line-height: 0.98;
    overflow-wrap: break-word;
  }

  .hero-role {
    margin-top: 18px;
    font-size: 0.98rem;
  }

  .hero-slogan {
    margin-top: 12px;
    line-height: 1.72;
  }

  .signal-line {
    min-height: 46px;
    margin-top: 20px;
  }

  .hero-actions {
    display: grid;
    grid-template-columns: 1fr;
    gap: 10px;
    margin-top: 24px;
  }

  .hero-btn {
    width: 100%;
  }

  .hero-metrics,
  .signal-stack,
  .quick-nav {
    grid-template-columns: 1fr;
  }

  .visual-frame {
    width: 100%;
    max-width: 100%;
    min-height: 0;
    aspect-ratio: 4 / 5;
  }

  .avatar-orbit {
    inset: 68px 24px 104px;
  }

  .scanner-dial {
    width: 88px;
    height: 88px;
    right: 22px;
    bottom: 86px;
  }

  .visual-readout {
    left: 18px;
    right: 18px;
    bottom: 18px;
    flex-direction: column;
    align-items: flex-start;
  }

  .journey-panel-list {
    grid-template-columns: 1fr;
  }

  .section-head {
    align-items: flex-start;
    flex-direction: column;
  }

  .section-head p {
    text-align: left;
  }

  .section-head h2 {
    font-size: 2rem;
  }

  .life-timeline-track {
    grid-template-columns: 1fr;
  }

  .bento-grid {
    grid-template-columns: 1fr;
  }

  .feature-card,
  .articles-card,
  .connect-card {
    grid-column: auto;
  }

  .project-focus,
  .article-row {
    grid-template-columns: 1fr;
  }
}

/* Phase 4: keep the hero cinematic, remove fake CSS mountains and orbit scenery. */
.hero-mesh-scene {
  opacity: 0.58;
}

.mesh-plane,
.mesh-mountain,
.mesh-pulse {
  display: none;
}

.hero-mesh-scene::before,
.hero-mesh-scene::after {
  content: '';
  position: absolute;
  pointer-events: none;
}

.hero-mesh-scene::before {
  left: 7%;
  right: 7%;
  top: 28%;
  height: 42%;
  border-top: 1px solid rgba(147, 197, 253, 0.1);
  border-bottom: 1px solid rgba(167, 139, 250, 0.08);
  background:
    linear-gradient(90deg, transparent, rgba(96, 165, 250, 0.075), transparent),
    repeating-linear-gradient(90deg, transparent 0 84px, rgba(147, 197, 253, 0.085) 84px 85px),
    repeating-linear-gradient(0deg, transparent 0 38px, rgba(147, 197, 253, 0.035) 38px 39px);
  mask-image: linear-gradient(90deg, transparent, #000 18%, #000 82%, transparent);
  animation: heroDataChannel 8.4s ease-in-out infinite;
}

.hero-mesh-scene::after {
  left: 0;
  right: 0;
  top: 56%;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(96, 165, 250, 0.7), rgba(167, 139, 250, 0.34), transparent);
  box-shadow: 0 0 22px rgba(96, 165, 250, 0.22);
  opacity: 0.55;
  animation: heroDataSweep 5.2s ease-in-out infinite;
}

.mesh-horizon {
  top: 56%;
  left: 10%;
  right: 10%;
  opacity: 0.46;
  box-shadow: 0 0 20px rgba(96, 165, 250, 0.22);
}

.avatar-orbit {
  opacity: 0.22;
}

.avatar-orbit span,
.scanner-dial::before,
.scanner-dial span {
  animation: none !important;
}

@keyframes heroDataChannel {
  0%, 100% { opacity: 0.28; transform: translateY(0); }
  50% { opacity: 0.48; transform: translateY(-5px); }
}

@keyframes heroDataSweep {
  0%, 100% { opacity: 0.32; transform: scaleX(0.78); }
  48% { opacity: 0.68; transform: scaleX(1); }
}

@media (prefers-reduced-motion: reduce) {
  .ambient-bar,
  .mesh-horizon,
  .mesh-plane,
  .mesh-mountain,
  .mesh-pulse,
  .kicker-dot,
  .cursor-blink,
  .digital-slices span,
  .face-vector span,
  .face-vector::before,
  .face-vector::after,
  .holo-rain span,
  .energy-outline,
  .scanner-dial::before,
  .scanner-dial span,
  .avatar-orbit span,
  .visual-frame,
  .visual-frame::after,
  .visual-grid::after,
  .visual-image,
  .life-timeline-item {
    animation: none !important;
  }

  .hero-section,
  .timeline-section,
  .bento-grid,
  .hero-btn,
  .bento-card,
  .quick-link,
  .visual-image {
    transition: none !important;
  }
}
</style>
