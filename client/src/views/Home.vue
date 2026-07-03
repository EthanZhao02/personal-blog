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
          <div class="project-mark">{{ featuredProject.icon || '◇' }}</div>
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
          <span>Connect</span>
          <router-link to="/message">Message</router-link>
        </div>
        <div class="social-list">
          <a
            v-for="social in visibleSocials.slice(0, 6)"
            :key="social.name"
            :href="social.url"
            :style="{ '--social-color': social.color }"
            class="social-link"
            target="_blank"
            rel="noopener noreferrer"
            :title="`${social.name}: ${social.handle || social.url}`"
          >
            <span v-html="getSocialIcon(social.icon)"></span>
            <span>
              <strong>{{ social.name }}</strong>
              <small>{{ social.handle }}</small>
            </span>
          </a>
        </div>
      </article>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import siteConfig, { visibleSocials } from '../config/site.config.js'

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

const heroStats = computed(() => [
  { value: siteConfig.content.articles?.length || 0, label: 'Articles' },
  { value: siteConfig.projects?.length || 0, label: 'Projects' },
  { value: siteConfig.learningTracks?.length || 0, label: 'Tracks' },
  { value: siteConfig.researchInterests?.length || 0, label: 'Interests' },
])

const activePhoto = computed(() => siteConfig.photos[currentPhoto.value] || siteConfig.photos[0] || '')
const featuredProject = computed(() => siteConfig.projects?.[0] || {})
const latestArticles = computed(() => (siteConfig.content.articles || []).slice(0, 3))
const projectTech = computed(() => featuredProject.value.techStack?.split(', ').slice(0, 5) || [])

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
  const d = new Date(value)
  return `${d.getMonth() + 1}.${String(d.getDate()).padStart(2, '0')}`
}

const getSocialIcon = (icon) => {
  const icons = {
    github: '<svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 2A10 10 0 0 0 2 12c0 4.42 2.87 8.17 6.84 9.5.5.08.66-.23.66-.5v-1.69c-2.77.6-3.36-1.34-3.36-1.34-.46-1.16-1.11-1.47-1.11-1.47-.91-.62.07-.6.07-.6 1 .07 1.53 1.03 1.53 1.03.87 1.52 2.34 1.07 2.91.83.09-.65.35-1.09.63-1.34-2.22-.25-4.55-1.11-4.55-4.92 0-1.11.38-2 1.03-2.71-.1-.25-.45-1.29.1-2.64 0 0 .84-.27 2.75 1.02.79-.22 1.65-.33 2.5-.33s1.71.11 2.5.33c1.91-1.29 2.75-1.02 2.75-1.02.55 1.35.2 2.39.1 2.64.65.71 1.03 1.6 1.03 2.71 0 3.82-2.34 4.66-4.57 4.91.36.31.69.92.69 1.85V21c0 .27.16.59.67.5C19.14 20.16 22 16.42 22 12A10 10 0 0 0 12 2z"/></svg>',
    email: '<svg viewBox="0 0 24 24" fill="currentColor"><path d="M20 4H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2Zm0 4-8 5-8-5V6l8 5 8-5v2Z"/></svg>',
    juejin: '<span class="social-letter">掘</span>',
    leetcode: '<span class="social-letter">LC</span>',
    wechat: '<span class="social-letter">微</span>',
    qq: '<span class="social-letter">QQ</span>',
    bilibili: '<span class="social-letter">B</span>',
    csdn: '<span class="social-letter">CN</span>',
    zhihu: '<span class="social-letter">知</span>',
  }
  return icons[icon] || '<span class="social-letter">ID</span>'
}

onMounted(() => {
  updateTime()
  timeTimer = setInterval(updateTime, 1000)
  carouselTimer = setInterval(nextPhoto, 5200)
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

.project-card {
  grid-column: span 5;
}

.articles-card {
  grid-column: span 4;
}

.research-card {
  grid-column: span 3;
}

.learning-card {
  grid-column: span 5;
}

.timeline-card {
  grid-column: span 4;
}

.connect-card {
  grid-column: span 3;
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

.social-list {
  display: grid;
  gap: 10px;
}

.social-link {
  display: grid;
  grid-template-columns: 36px 1fr;
  gap: 12px;
  align-items: center;
  min-height: 52px;
  padding: 8px;
  border: 1px solid rgba(148, 226, 255, 0.13);
  border-radius: 8px;
  background: rgba(5, 8, 22, 0.32);
}

.social-link > span:first-child {
  width: 36px;
  height: 36px;
  display: grid;
  place-items: center;
  border-radius: 8px;
  color: var(--social-color, #38bdf8);
  background: rgba(255, 255, 255, 0.045);
}

.social-link svg {
  width: 18px;
  height: 18px;
}

.social-link strong,
.social-link small {
  display: block;
}

.social-link strong {
  color: #f8fbff;
  font-size: 0.9rem;
}

.social-link small {
  margin-top: 2px;
  color: rgba(226, 239, 255, 0.48);
  font-size: 0.76rem;
  overflow-wrap: anywhere;
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
  .research-card,
  .learning-card,
  .timeline-card,
  .connect-card {
    grid-column: span 6;
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
