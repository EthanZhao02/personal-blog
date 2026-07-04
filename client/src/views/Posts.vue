<template>
  <div class="posts-page">
    <!-- 背景装饰 -->
    <div class="page-bg" aria-hidden="true">
      <div class="bg-line"></div>
      <div class="bg-line"></div>
    </div>

    <div class="posts-container">
      <!-- 页面头部 -->
      <header class="page-header">
        <div class="header-content">
          <div class="header-badge">ARCHIVE</div>
          <h1 class="page-title">文章归档</h1>
          <p class="page-desc">探索技术、记录思考、分享经验</p>
        </div>
        <div class="header-stats">
          <div class="stat-box">
            <span class="stat-num">{{ totalCount }}</span>
            <span class="stat-label">篇文章</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-box">
            <span class="stat-num">{{ categories.length }}</span>
            <span class="stat-label">个分类</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-box">
            <span class="stat-num">{{ allTags.length }}</span>
            <span class="stat-label">个标签</span>
          </div>
        </div>
      </header>

      <!-- 筛选栏 -->
      <div class="filter-panel">
        <div class="filter-section">
          <span class="filter-label">分类</span>
          <div class="filter-chips">
            <button
              class="chip"
              :class="{ active: !selectedCategoryId }"
              @click="selectCategory(null)"
            >全部</button>
            <button
              v-for="cat in categories"
              :key="cat.id"
              class="chip"
              :class="{ active: selectedCategoryId === cat.id }"
              @click="selectCategory(cat.id)"
            >{{ cat.name }}</button>
          </div>
        </div>
        <div class="filter-section" v-if="allTags.length">
          <span class="filter-label">标签</span>
          <select v-model="selectedTagName" class="filter-select">
            <option value="">全部标签</option>
            <option v-for="tag in allTags" :key="tag.id" :value="tag.name">{{ tag.name }}</option>
          </select>
        </div>
      </div>

      <!-- 时间轴文章列表 -->
      <main class="timeline-wrapper">
        <div v-if="loading" class="loading-state">
          <div class="loading-spinner"></div>
          <span>加载中...</span>
        </div>

        <div v-else-if="filteredArticles.length === 0" class="empty-state">
          <div class="empty-icon">◈</div>
          <p>暂无文章</p>
        </div>

        <div v-else class="timeline">
          <div
            v-for="year in sortedYears"
            :key="year"
            class="timeline-year"
          >
            <div class="year-marker">
              <span class="year-num">{{ year }}</span>
              <span class="year-line"></span>
            </div>
            
            <div class="articles-grid">
              <article
                v-for="article in getArticlesByYear(year)"
                :key="article.id"
                class="article-card"
                :class="{ 'has-cover': article.coverImage }"
              >
                <router-link :to="`/article/${article.id}`" class="card-link">
                  <div class="card-bg" v-if="article.coverImage">
                    <img :src="resolveImageUrl(article.coverImage)" :alt="article.title" loading="lazy" />
                  </div>
                  <div class="card-bg card-bg-fallback" v-else>
                    <span class="fallback-icon">◇</span>
                  </div>
                  <div class="card-overlay"></div>
                  <div class="card-body">
                    <div class="card-tag-row">
                      <span class="card-category" v-if="getCategoryName(article.categoryId)">
                        {{ getCategoryName(article.categoryId) }}
                      </span>
                      <span class="card-views">
                        <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                        {{ article.viewCount || 0 }}
                      </span>
                    </div>
                    <h3 class="card-title">{{ article.title }}</h3>
                    <div class="card-meta">
                      <time>{{ formatDate(article.createTime) }}</time>
                      <span class="card-tags" v-if="article.tags?.length">
                        <span v-for="tag in article.tags.slice(0, 3)" :key="tag.id || tag.name">#{{ tag.name }}</span>
                      </span>
                    </div>
                  </div>
                </router-link>
              </article>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getArticleList } from '../api/article'
import { getTagList } from '../api/tag'
import { getCategoryList } from '../api/category'
import { fallbackArticles, fallbackCategories, fallbackTags, isStaticMode } from '../config/site.config'

const apiBaseUrl = (import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080').replace(/\/api\/?$/, '')

const articles = ref([])
const categories = ref([])
const allTags = ref([])
const selectedCategoryId = ref(null)
const selectedTagName = ref('')
const loading = ref(true)

// 筛选后的文章
const filteredArticles = computed(() => {
  let list = [...articles.value]
  if (selectedCategoryId.value) {
    list = list.filter(a => a.categoryId === selectedCategoryId.value)
  }
  if (selectedTagName.value) {
    list = list.filter(a =>
      a.tags?.some(t => t.name === selectedTagName.value)
    )
  }
  return list.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
})

// 文章总数
const totalCount = computed(() => filteredArticles.value.length)

// 获取所有年份（降序）
const sortedYears = computed(() => {
  const years = new Set()
  filteredArticles.value.forEach(a => {
    if (a.createTime) {
      years.add(new Date(a.createTime).getFullYear())
    }
  })
  return Array.from(years).sort((a, b) => b - a)
})

// 按年份获取文章
const getArticlesByYear = (year) => {
  return filteredArticles.value.filter(a => {
    if (!a.createTime) return false
    return new Date(a.createTime).getFullYear() === year
  })
}

// 获取分类名称
const getCategoryName = (categoryId) => {
  if (!categoryId) return ''
  const cat = categories.value.find(c => c.id === categoryId)
  return cat ? cat.name : ''
}

// 格式化日期
const formatDay = (s) => {
  if (!s) return ''
  return String(new Date(s).getDate()).padStart(2, '0')
}

const formatMonth = (s) => {
  if (!s) return ''
  const months = ['JAN', 'FEB', 'MAR', 'APR', 'MAY', 'JUN', 'JUL', 'AUG', 'SEP', 'OCT', 'NOV', 'DEC']
  return months[new Date(s).getMonth()]
}

const formatDate = (s) => {
  if (!s) return ''
  const d = new Date(s)
  return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`
}

const selectCategory = (id) => {
  selectedCategoryId.value = id
}

const useFallbackArticles = () => {
  articles.value = fallbackArticles
}

const useFallbackTags = () => {
  allTags.value = fallbackTags
}

const useFallbackCategories = () => {
  categories.value = fallbackCategories
}

const loadArticles = async () => {
  loading.value = true
  try {
    const res = await getArticleList(1, 100)
    if (res.code === 200 && res.data?.records) {
      articles.value = res.data.records
    } else {
      useFallbackArticles()
    }
  } catch (e) {
    useFallbackArticles()
  }
  loading.value = false
}

const loadTags = async () => {
  if (isStaticMode) {
    useFallbackTags()
    return
  }
  try {
    const res = await getTagList()
    if (res.code === 200) {
      allTags.value = res.data || []
    } else {
      useFallbackTags()
    }
  } catch (e) {
    useFallbackTags()
    if (import.meta.env.DEV) console.info('使用静态标签数据', e?.message || e)
  }
}

const loadCategories = async () => {
  if (isStaticMode) {
    useFallbackCategories()
    return
  }
  try {
    const res = await getCategoryList()
    if (res.code === 200) {
      categories.value = res.data || []
    } else {
      useFallbackCategories()
    }
  } catch (e) {
    useFallbackCategories()
    if (import.meta.env.DEV) console.info('使用静态分类数据', e?.message || e)
  }
}

const resolveImageUrl = (url) => {
  if (!url) return ''
  if (/^https?:\/\//.test(url)) return url
  if (url.startsWith('/upload/') || url.startsWith('/uploads/')) {
    return `${apiBaseUrl}${url}`
  }
  return url
}

onMounted(() => {
  Promise.all([loadArticles(), loadTags(), loadCategories()])
})
</script>

<style scoped>
.posts-page {
  position: relative;
  min-height: 100vh;
  padding: 80px 24px 100px;
}

/* 背景 */
.page-bg {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 0;
}

.bg-line {
  position: absolute;
  width: 1px;
  height: 100%;
  background: linear-gradient(to bottom, transparent, rgba(56, 248, 255, 0.1), transparent);
}

.bg-line:nth-child(1) { left: 20%; }
.bg-line:nth-child(2) { right: 20%; }

/* 容器 */
.posts-container {
  position: relative;
  z-index: 1;
  max-width: 1200px;
  margin: 0 auto;
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 40px;
  padding-bottom: 32px;
  border-bottom: 1px solid rgba(56, 248, 255, 0.1);
}

.header-content {
  flex: 1;
}

.header-badge {
  display: inline-block;
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.2em;
  color: #38f8ff;
  padding: 6px 12px;
  border: 1px solid rgba(56, 248, 255, 0.3);
  border-radius: 4px;
  margin-bottom: 16px;
}

.page-title {
  font-size: clamp(2rem, 4vw, 3rem);
  font-weight: 700;
  color: #fff;
  margin: 0 0 8px 0;
  background: linear-gradient(135deg, #fff 0%, #38f8ff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.page-desc {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.5);
  margin: 0;
}

.header-stats {
  display: flex;
  align-items: center;
  gap: 24px;
}

.stat-box {
  text-align: center;
}

.stat-num {
  display: block;
  font-size: 32px;
  font-weight: 700;
  color: #38f8ff;
  line-height: 1;
}

.stat-label {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.4);
  letter-spacing: 0.1em;
}

.stat-divider {
  width: 1px;
  height: 40px;
  background: rgba(56, 248, 255, 0.2);
}

/* 筛选面板 */
.filter-panel {
  display: flex;
  flex-wrap: wrap;
  gap: 32px;
  padding: 24px;
  background: rgba(12, 20, 35, 0.6);
  border: 1px solid rgba(56, 248, 255, 0.1);
  border-radius: 12px;
  margin-bottom: 40px;
  backdrop-filter: blur(10px);
}

.filter-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

.filter-label {
  font-size: 12px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.4);
  letter-spacing: 0.1em;
}

.filter-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.chip {
  padding: 8px 18px;
  border: 1px solid rgba(56, 248, 255, 0.15);
  border-radius: 20px;
  background: rgba(12, 20, 35, 0.6);
  color: rgba(255, 255, 255, 0.6);
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.25s ease;
  position: relative;
  overflow: hidden;
}

.chip::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, rgba(56, 248, 255, 0.1), rgba(155, 92, 255, 0.1));
  opacity: 0;
  transition: opacity 0.25s;
}

.chip:hover {
  border-color: rgba(56, 248, 255, 0.4);
  color: #38f8ff;
  box-shadow: 0 0 15px rgba(56, 248, 255, 0.15);
}

.chip:hover::before {
  opacity: 1;
}

.chip.active {
  background: linear-gradient(135deg, rgba(56, 248, 255, 0.15), rgba(155, 92, 255, 0.15));
  border-color: rgba(56, 248, 255, 0.5);
  color: #fff;
  box-shadow: 0 0 20px rgba(56, 248, 255, 0.2), inset 0 1px 0 rgba(255, 255, 255, 0.1);
}

.filter-select {
  padding: 8px 16px;
  border: 1px solid rgba(56, 248, 255, 0.2);
  border-radius: 8px;
  background: rgba(12, 20, 35, 0.8);
  color: rgba(255, 255, 255, 0.9);
  font-size: 13px;
  cursor: pointer;
  min-width: 140px;
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 24 24' fill='none' stroke='%2338f8ff' stroke-width='2'%3E%3Cpath d='M6 9l6 6 6-6'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 12px center;
  padding-right: 32px;
}

.filter-select:focus {
  outline: none;
  border-color: rgba(56, 248, 255, 0.5);
  box-shadow: 0 0 10px rgba(56, 248, 255, 0.1);
}

.filter-select option {
  background: rgba(12, 20, 35, 0.95);
  color: rgba(255, 255, 255, 0.9);
  padding: 8px;
}

/* 时间轴 */
.timeline-wrapper {
  position: relative;
}

.timeline {
  position: relative;
}

.timeline::before {
  content: '';
  position: absolute;
  left: 60px;
  top: 0;
  bottom: 0;
  width: 2px;
  background: linear-gradient(to bottom, 
    rgba(56, 248, 255, 0.3),
    rgba(155, 92, 255, 0.3),
    transparent
  );
}

.timeline-year {
  position: relative;
  margin-bottom: 48px;
}

.timeline-year:last-child {
  margin-bottom: 0;
}

.year-marker {
  display: flex;
  align-items: center;
  gap: 24px;
  margin-bottom: 24px;
  padding-left: 24px;
}

.year-num {
  font-size: 48px;
  font-weight: 800;
  color: transparent;
  -webkit-text-stroke: 1px rgba(56, 248, 255, 0.25);
  line-height: 1;
  font-family: 'SF Mono', monospace;
  letter-spacing: -2px;
}

.year-line {
  flex: 1;
  height: 1px;
  background: linear-gradient(to right, rgba(56, 248, 255, 0.2), transparent);
}

/* 文章网格 */
.articles-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
  padding-left: 100px;
  align-items: stretch;
}

.article-card {
  position: relative;
  background: linear-gradient(135deg, #1a1a2e, #16213e);
  border: 1px solid rgba(56, 248, 255, 0.08);
  border-radius: 10px;
  overflow: hidden;
  transition: all 0.3s ease;
  min-height: 200px;
}

.article-card.has-cover {
  background: #0c1423;
}

.article-card:hover {
  transform: translateY(-3px);
  border-color: rgba(56, 248, 255, 0.25);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.4);
}

.card-bg {
  position: absolute;
  inset: 0;
}

.card-bg img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.article-card:hover .card-bg img {
  transform: scale(1.06);
}

.card-bg-fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(56, 248, 255, 0.05), rgba(155, 92, 255, 0.05));
}

.fallback-icon {
  font-size: 48px;
  color: rgba(56, 248, 255, 0.1);
}

.card-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to top, rgba(0,0,0,0.85) 0%, rgba(0,0,0,0.3) 50%, rgba(0,0,0,0.05) 100%);
  z-index: 1;
}

.card-link {
  position: relative;
  z-index: 2;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  height: 100%;
  min-height: 200px;
  padding: 14px;
  text-decoration: none;
  color: #fff;
}

.card-body {
  position: relative;
  z-index: 1;
}

.card-tag-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
  gap: 8px;
}

.card-category {
  font-size: 10px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  padding: 2px 8px;
  background: rgba(56, 248, 255, 0.2);
  border: 1px solid rgba(56, 248, 255, 0.3);
  border-radius: 3px;
  color: #38f8ff;
}

.card-views {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  color: rgba(255, 255, 255, 0.55);
}

.card-views svg {
  opacity: 0.5;
}

.card-title {
  font-size: 15px;
  font-weight: 700;
  color: #fff;
  margin: 0 0 6px 0;
  line-height: 1.35;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-shadow: 0 1px 4px rgba(0,0,0,0.5);
}

.card-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.card-meta time {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.45);
}

.card-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.card-tags span {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.6);
  background: rgba(255, 255, 255, 0.08);
  padding: 1px 6px;
  border-radius: 3px;
}

/* 加载状态 */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  padding: 80px 0;
  color: rgba(255, 255, 255, 0.5);
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 2px solid rgba(56, 248, 255, 0.2);
  border-top-color: #38f8ff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 80px 0;
  color: rgba(255, 255, 255, 0.5);
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
  opacity: 0.3;
}

/* 响应式 */
@media (max-width: 900px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 24px;
  }
  
  .header-stats {
    width: 100%;
    justify-content: flex-start;
  }
  
  .timeline::before {
    left: 20px;
  }
  
  .year-marker {
    padding-left: 0;
  }
  
  .year-num {
    font-size: 32px;
  }
  
  .articles-grid {
    padding-left: 50px;
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .posts-page {
    padding: 72px 16px 80px;
  }
  
  .filter-panel {
    flex-direction: column;
    gap: 20px;
  }
  
  .filter-section {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .timeline::before {
    display: none;
  }
  
  .articles-grid {
    padding-left: 0;
  }
  
  .year-marker {
    padding-left: 0;
  }
}
</style>
