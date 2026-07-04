<template>
  <div class="home-page">
    <div class="tech-field" aria-hidden="true">
      <span
        v-for="dot in particleDots"
        :key="dot.id"
        class="field-dot"
        :style="{ left: dot.left, top: dot.top, animationDelay: dot.delay }"
      ></span>
    </div>

    <section class="hero-section">
      <div class="hero-copy reveal-up">
        <h1>Hi, I'm Ethan Zhao.</h1>
        <p class="hero-role">{{ siteConfig.role }}</p>
        <p class="hero-slogan">{{ siteConfig.subtitle }}</p>

        <div class="hero-actions" aria-label="Primary links">
          <router-link to="/projects" class="hero-btn primary">
            <span>View Projects</span>
            <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M5 12h14m-6-6 6 6-6 6"/></svg>
          </router-link>
          <router-link to="/posts" class="hero-btn">
            <span>Read Notes</span>
            <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M5 5h14M5 12h14M5 19h9"/></svg>
          </router-link>
          <router-link to="/about" class="hero-btn">
            <span>About</span>
            <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 12a4 4 0 1 0 0-8 4 4 0 0 0 0 8Zm7 8a7 7 0 0 0-14 0"/></svg>
          </router-link>
        </div>

        <div class="hero-metrics" aria-label="Site metrics">
          <div v-for="item in heroStats" :key="item.label" class="metric-item">
            <strong>{{ item.value }}</strong>
            <span>{{ item.label }}</span>
          </div>
        </div>
      </div>

      <div class="hero-visual reveal-up" style="animation-delay: 120ms">
        <div class="visual-frame">
          <img :src="activePhoto" alt="AI and future lab visual" class="visual-image" />
          <div class="visual-overlay" aria-hidden="true"></div>
          <div class="visual-readout">
            <span>ETHAN FUTURE LAB</span>
            <strong>{{ currentTime }}</strong>
          </div>
          <button class="visual-next" type="button" @click="nextPhoto" aria-label="Switch visual">
            <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M9 5l7 7-7 7"/></svg>
          </button>
        </div>
      </div>
    </section>

    <section class="bento-grid" aria-label="Portfolio overview">
      <article class="bento-card project-card reveal-up">
        <div class="card-heading">
          <span>Featured Projects</span>
          <router-link to="/projects">All Projects</router-link>
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

      <article class="bento-card articles-card reveal-up" style="animation-delay: 60ms">
        <div class="card-heading">
          <span>Latest Articles</span>
          <router-link to="/posts">Archive</router-link>
        </div>
        <router-link
          v-for="article in latestArticles"
          :key="article.id"
          :to="`/article/${article.id}`"
          class="article-row"
        >
          <time>{{ formatDate(article.createTime) }}</time>
          <strong>{{ article.title }}</strong>
        </router-link>
      </article>

      <article class="bento-card learning-card reveal-up" style="animation-delay: 120ms">
        <div class="card-heading">
          <span>Learning Track</span>
          <span class="heading-note">复试 / 求职 / 长期积累</span>
        </div>
        <div v-for="track in siteConfig.learningTracks" :key="track.title" class="track-row">
          <div class="track-top">
            <strong>{{ track.title }}</strong>
            <span>{{ track.progress }}%</span>
          </div>
          <p>{{ track.description }}</p>
          <div class="track-bar" aria-hidden="true">
            <span :style="{ width: `${track.progress}%` }"></span>
          </div>
        </div>
      </article>

      <article class="bento-card research-card reveal-up" style="animation-delay: 180ms">
        <div class="card-heading">
          <span>Research Interests</span>
        </div>
        <div class="interest-grid">
          <span v-for="interest in siteConfig.researchInterests" :key="interest">{{ interest }}</span>
        </div>
      </article>

      <article class="bento-card timeline-card reveal-up" style="animation-delay: 240ms">
        <div class="card-heading">
          <span>Timeline</span>
        </div>
        <div class="timeline-list">
          <div v-for="item in siteConfig.timeline" :key="`${item.time}-${item.title}`" class="timeline-item">
            <time>{{ item.time }}</time>
            <div>
              <strong>{{ item.title }}</strong>
              <p>{{ item.text }}</p>
            </div>
          </div>
        </div>
      </article>

      <article id="connect" class="bento-card connect-card reveal-up" style="animation-delay: 300ms">
        <div class="card-heading">
          <span>Quick Links</span>
          <router-link to="/about">More →</router-link>
        </div>
        <div class="quick-nav">
          <router-link to="/posts" class="quick-link">
            <span class="quick-icon">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>
            </span>
            <span>
              <strong>阅读文章</strong>
              <small>查看技术笔记与思考</small>
            </span>
          </router-link>
          <router-link to="/projects" class="quick-link">
            <span class="quick-icon">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="16 18 22 12 16 6"/><polyline points="8 6 2 12 8 18"/></svg>
            </span>
            <span>
              <strong>浏览项目</strong>
              <small>作品集与实验代码</small>
            </span>
          </router-link>
          <router-link to="/friends" class="quick-link">
            <span class="quick-icon">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
            </span>
            <span>
              <strong>友情链接</strong>
              <small>优秀博主与社区</small>
            </span>
          </router-link>
          <router-link to="/message" class="quick-link">
            <span class="quick-icon">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
            </span>
            <span>
              <strong>留言交流</strong>
              <small>留下你的想法</small>
            </span>
          </router-link>
        </div>
      </article>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import siteConfig from '../config/site.config.js'
import { getArticleList } from '../api/article'
import { getProjects } from '../api/project'

const currentPhoto = ref(0)
const currentTime = ref('')
let timeTimer = null
let carouselTimer = null

const particleDots = [
  { id: 1, left: '7%', top: '18%', delay: '0s' },
  { id: 2, left: '21%', top: '64%', delay: '1.4s' },
  { id: 3, left: '43%', top: '26%', delay: '0.8s' },
  { id: 4, left: '64%', top: '72%', delay: '2.1s' },
  { id: 5, left: '82%', top: '16%', delay: '1.1s' },
  { id: 6, left: '92%', top: '54%', delay: '2.7s' },
]

const projectsCount = ref(siteConfig.projects?.length || 0)
const projectsData = ref([])

const heroStats = computed(() => [
  { value: totalArticles.value, label: 'Articles' },
  { value: projectsCount.value, label: 'Projects' },
  { value: siteConfig.learningTracks?.length || 0, label: 'Tracks' },
  { value: siteConfig.researchInterests?.length || 0, label: 'Interests' },
])

const activePhoto = computed(() => siteConfig.photos[currentPhoto.value] || siteConfig.photos[0] || '')
const featuredProject = computed(() => projectsData.value[0] || siteConfig.projects?.[0] || {})
const isProjectIconUrl = (icon) => icon && /^https?:\/\//.test(icon)
const articles = ref([])
const latestArticles = computed(() => articles.value.slice(0, 3))
const totalArticles = computed(() => articles.value.length || siteConfig.content.articles?.length || 0)
const projectTech = computed(() => {
  const ts = featuredProject.value?.techStack
  if (!ts) return []
  return ts.split(/[,\s]+/).filter(Boolean).slice(0, 5)
})

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

onMounted(async () => {
  updateTime()
  timeTimer = setInterval(updateTime, 1000)
  carouselTimer = setInterval(nextPhoto, 5200)
  // 从API加载最新文章
  try {
    const res = await getArticleList(1, 3)
    if (res.code === 200 && res.data?.records) {
      articles.value = res.data.records
    }
  } catch (e) {
    // API失败时使用静态数据
    if (siteConfig.content.articles?.length) {
      articles.value = siteConfig.content.articles
    }
  }
  // 加载项目
  try {
    const projRes = await getProjects('/project/list')
    if ((projRes.code === 200 || projRes.code === 0) && projRes.data) {
      projectsData.value = projRes.data
      projectsCount.value = projRes.data.length
    }
  } catch (e) {
    // 失败时保持 siteConfig 默认值
  }
})

onUnmounted(() => {
  if (timeTimer) clearInterval(timeTimer)
  if (carouselTimer) clearInterval(carouselTimer)
})
</script>

<style scoped>
.home-page {
  position: relative;
  min-height: 100vh;
  padding: 86px 24px 100px;
  overflow: hidden;
}

.tech-field {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 0;
  background-image:
    linear-gradient(rgba(56, 189, 248, 0.045) 1px, transparent 1px),
    linear-gradient(90deg, rgba(56, 189, 248, 0.045) 1px, transparent 1px);
  background-size: 64px 64px;
  mask-image: linear-gradient(to bottom, #000 0%, rgba(0, 0, 0, 0.72) 56%, transparent 100%);
}

.field-dot {
  position: absolute;
  width: 3px;
  height: 3px;
  border-radius: 999px;
  background: #22d3ee;
  box-shadow: 0 0 18px rgba(34, 211, 238, 0.9);
  animation: driftDot 6s ease-in-out infinite;
}

.hero-section,
.bento-grid {
  position: relative;
  z-index: 1;
  width: min(1240px, 100%);
  margin: 0 auto;
}

.hero-section {
  min-height: calc(100vh - 150px);
  display: grid;
  grid-template-columns: minmax(0, 0.92fr) minmax(360px, 0.78fr);
  align-items: center;
  gap: 42px;
  padding: 24px 0 34px;
}

.hero-copy h1 {
  max-width: 720px;
  margin: 0;
  color: #f8fbff;
  font-size: 4.8rem;
  line-height: 0.98;
  font-weight: 800;
}

.hero-copy,
.hero-visual {
  min-width: 0;
}

.hero-role {
  margin: 22px 0 0;
  color: #38bdf8;
  font-size: 1.22rem;
  font-weight: 700;
}

.hero-slogan {
  max-width: 610px;
  margin: 16px 0 0;
  color: rgba(226, 239, 255, 0.72);
  font-size: 1.04rem;
  line-height: 1.8;
}

.hero-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 34px;
}

.hero-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  min-height: 44px;
  padding: 0 18px;
  border: 1px solid rgba(148, 226, 255, 0.22);
  border-radius: 8px;
  background: rgba(8, 14, 27, 0.56);
  color: rgba(238, 247, 255, 0.82);
  font-weight: 700;
  text-decoration: none;
  transition: transform 0.22s var(--ease-out), border-color 0.22s, box-shadow 0.22s;
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
  border-color: rgba(56, 189, 248, 0.58);
  background: linear-gradient(135deg, #38bdf8, #22d3ee);
  color: #04101f;
  box-shadow: 0 18px 46px rgba(34, 211, 238, 0.18);
}

.hero-btn:hover {
  transform: translateY(-2px);
  border-color: rgba(56, 189, 248, 0.68);
  box-shadow: 0 18px 42px rgba(56, 189, 248, 0.14);
}

.hero-metrics {
  display: grid;
  grid-template-columns: repeat(4, minmax(92px, 1fr));
  gap: 12px;
  max-width: 620px;
  margin-top: 34px;
}

.metric-item {
  padding: 16px 14px;
  border: 1px solid rgba(148, 226, 255, 0.16);
  border-radius: 8px;
  background: rgba(8, 14, 27, 0.46);
}

.metric-item strong {
  display: block;
  color: #eaf7ff;
  font-size: 1.5rem;
  line-height: 1;
}

.metric-item span {
  display: block;
  margin-top: 8px;
  color: rgba(226, 239, 255, 0.5);
  font-size: 0.82rem;
}

.hero-visual {
  align-self: center;
}

.visual-frame {
  position: relative;
  aspect-ratio: 4 / 5;
  min-height: 520px;
  overflow: hidden;
  border: 1px solid rgba(148, 226, 255, 0.24);
  border-radius: 14px;
  background: rgba(7, 11, 21, 0.74);
  box-shadow: 0 32px 90px rgba(0, 0, 0, 0.46), inset 0 0 70px rgba(56, 189, 248, 0.08);
}

.visual-frame::before {
  content: '';
  position: absolute;
  inset: 16px;
  z-index: 2;
  border: 1px solid rgba(148, 226, 255, 0.15);
  border-radius: 10px;
  pointer-events: none;
}

.visual-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  filter: saturate(0.96) contrast(1.08) brightness(0.72);
}

.visual-overlay {
  position: absolute;
  inset: 0;
  background:
    linear-gradient(180deg, rgba(5, 8, 22, 0.16), rgba(5, 8, 22, 0.46)),
    repeating-linear-gradient(0deg, rgba(255, 255, 255, 0.03) 0 1px, transparent 1px 5px);
  pointer-events: none;
}

.visual-readout {
  position: absolute;
  left: 28px;
  right: 28px;
  bottom: 28px;
  z-index: 3;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 14px 16px;
  border: 1px solid rgba(148, 226, 255, 0.22);
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
  color: #22d3ee;
}

.visual-next {
  position: absolute;
  top: 28px;
  right: 28px;
  z-index: 3;
  width: 42px;
  height: 42px;
  display: grid;
  place-items: center;
  border: 1px solid rgba(148, 226, 255, 0.28);
  border-radius: 8px;
  background: rgba(5, 8, 22, 0.6);
  color: #eaf7ff;
  cursor: pointer;
  transition: transform 0.22s var(--ease-out), border-color 0.22s;
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
  border-color: #38bdf8;
}

.bento-grid {
  display: grid;
  grid-template-columns: repeat(12, 1fr);
  gap: 16px;
  margin-top: 18px;
}

.bento-card {
  position: relative;
  min-height: 210px;
  padding: 22px;
  overflow: hidden;
  border: 1px solid rgba(148, 226, 255, 0.16);
  border-radius: 10px;
  background:
    linear-gradient(135deg, rgba(17, 27, 49, 0.82), rgba(8, 14, 27, 0.66)),
    rgba(8, 14, 27, 0.68);
  box-shadow: var(--shadow);
  backdrop-filter: blur(16px);
}

.bento-card::after {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(120deg, transparent, rgba(56, 189, 248, 0.08), transparent);
  transform: translateX(-100%);
  transition: transform 0.6s var(--ease-out);
  pointer-events: none;
}

.bento-card:hover::after {
  transform: translateX(100%);
}

/* Bento Grid Layout - 3+3 整齐布局 */
.project-card {
  grid-column: span 4;
}

.articles-card {
  grid-column: span 4;
}

.research-card {
  grid-column: span 4;
}

.learning-card {
  grid-column: span 6;
}

.timeline-card {
  grid-column: span 6;
}

.connect-card {
  grid-column: span 12;
}

.card-heading {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  margin-bottom: 18px;
  color: #eaf7ff;
  font-weight: 800;
}

.card-heading a,
.heading-note {
  color: rgba(226, 239, 255, 0.52);
  font-size: 0.82rem;
  font-weight: 600;
}

.card-heading a:hover {
  color: #38bdf8;
}

.project-focus {
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
  border: 1px solid rgba(56, 189, 248, 0.34);
  border-radius: 8px;
  background: rgba(56, 189, 248, 0.09);
  color: #38bdf8;
  font-size: 1.6rem;
  overflow: hidden;
}

.project-mark img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
}

.project-focus h2 {
  margin: 0 0 8px;
  color: #fff;
  font-size: 1.45rem;
}

.project-focus p,
.track-row p,
.timeline-item p {
  margin: 0;
  color: rgba(226, 239, 255, 0.66);
  line-height: 1.75;
}

.tech-stack,
.interest-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 20px;
}

.tech-stack span,
.interest-grid span {
  padding: 7px 10px;
  border: 1px solid rgba(148, 226, 255, 0.16);
  border-radius: 7px;
  background: rgba(5, 8, 22, 0.36);
  color: rgba(226, 239, 255, 0.78);
  font-size: 0.82rem;
}

.article-row {
  display: grid;
  grid-template-columns: 54px 1fr;
  gap: 14px;
  align-items: start;
  padding: 13px 0;
  border-top: 1px solid rgba(148, 226, 255, 0.11);
}

.article-row:first-of-type {
  border-top: 0;
}

.article-row time {
  color: #22d3ee;
  font-family: 'SF Mono', 'Consolas', monospace;
  font-size: 0.82rem;
}

.article-row strong {
  color: rgba(238, 247, 255, 0.88);
  line-height: 1.55;
}

.track-row {
  padding: 14px 0;
  border-top: 1px solid rgba(148, 226, 255, 0.1);
}

.track-row:first-of-type {
  border-top: 0;
  padding-top: 0;
}

.track-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  color: #f8fbff;
  font-weight: 800;
}

.track-top span {
  color: #22d3ee;
  font-family: 'SF Mono', 'Consolas', monospace;
  font-size: 0.82rem;
}

.track-bar {
  height: 5px;
  margin-top: 10px;
  overflow: hidden;
  border-radius: 999px;
  background: rgba(238, 247, 255, 0.08);
}

.track-bar span {
  display: block;
  height: 100%;
  border-radius: inherit;
  background: linear-gradient(90deg, #38bdf8, #22d3ee, #8b5cf6);
}

.timeline-list {
  display: grid;
  gap: 16px;
}

.timeline-item {
  display: grid;
  grid-template-columns: 56px 1fr;
  gap: 14px;
}

.timeline-item time {
  color: #22d3ee;
  font-family: 'SF Mono', 'Consolas', monospace;
  font-weight: 700;
}

.timeline-item strong {
  display: block;
  margin-bottom: 4px;
  color: #f8fbff;
}

.quick-nav {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin-top: 8px;
}

.quick-link {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px;
  border: 1px solid rgba(148, 226, 255, 0.13);
  border-radius: 8px;
  background: rgba(5, 8, 22, 0.32);
  text-decoration: none;
  transition: all 0.2s;
}

.quick-link:hover {
  border-color: rgba(56, 248, 255, 0.4);
  background: rgba(56, 248, 255, 0.06);
  transform: translateY(-1px);
}

.quick-icon {
  width: 36px;
  height: 36px;
  display: grid;
  place-items: center;
  border-radius: 8px;
  color: #38f8ff;
  background: rgba(56, 248, 255, 0.1);
  flex-shrink: 0;
}

.quick-link strong {
  display: block;
  color: #f8fbff;
  font-size: 14px;
  font-weight: 600;
}

.quick-link small {
  display: block;
  margin-top: 2px;
  color: rgba(226, 239, 255, 0.5);
  font-size: 12px;
}

@keyframes driftDot {
  0%, 100% { opacity: 0.22; transform: translateY(0); }
  50% { opacity: 0.9; transform: translateY(-14px); }
}

@media (max-width: 1040px) {
  .hero-section {
    grid-template-columns: 1fr;
    min-height: auto;
  }

  .hero-copy h1 {
    font-size: 4rem;
  }

  .visual-frame {
    min-height: 420px;
    aspect-ratio: 16 / 9;
  }

  .project-card,
  .articles-card,
  .research-card {
    grid-column: span 6;
  }

  .learning-card,
  .timeline-card {
    grid-column: span 6;
  }

  .connect-card {
    grid-column: span 12;
  }
}

@media (max-width: 720px) {
  .home-page {
    padding: 74px 14px 72px;
  }

  .hero-copy h1 {
    max-width: 100%;
    font-size: 2.35rem;
    line-height: 1.08;
    overflow-wrap: break-word;
  }

  .hero-role {
    font-size: 1rem;
  }

  .hero-metrics {
    grid-template-columns: repeat(2, 1fr);
  }

  .hero-actions {
    display: grid;
    grid-template-columns: 1fr;
  }

  .hero-btn {
    width: 100%;
  }

  .visual-frame {
    min-height: 330px;
  }

  .visual-readout {
    left: 18px;
    right: 18px;
    bottom: 18px;
    flex-direction: column;
    align-items: flex-start;
  }

  .bento-grid {
    grid-template-columns: 1fr;
  }

  .project-card,
  .articles-card,
  .research-card,
  .learning-card,
  .timeline-card,
  .connect-card {
    grid-column: auto;
  }
}

@media (prefers-reduced-motion: reduce) {
  .field-dot,
  .reveal-up {
    animation: none !important;
  }

  .bento-card,
  .hero-btn,
  .visual-next {
    transition: none !important;
  }
}
</style>
