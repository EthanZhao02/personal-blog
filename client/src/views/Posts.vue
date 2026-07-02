<template>
  <div class="posts-page">
    <div class="posts-inner">
      <!-- 页面头部 -->
      <header class="posts-header">
        <h1 class="posts-title">归档</h1>
        <p class="posts-count">共 {{ totalCount }} 篇文章</p>
      </header>

      <!-- 筛选栏 -->
      <div class="filter-bar" v-if="categories.length || allTags.length">
        <div class="filter-group" v-if="categories.length">
          <button
            class="filter-chip"
            :class="{ active: !selectedCategoryId }"
            @click="selectCategory(null)"
          >全部</button>
          <button
            v-for="cat in categories"
            :key="cat.id"
            class="filter-chip"
            :class="{ active: selectedCategoryId === cat.id }"
            @click="selectCategory(cat.id)"
          >{{ cat.name }}</button>
        </div>
        <div class="filter-group" v-if="allTags.length">
          <select v-model="selectedTagName" class="tag-select">
            <option value="">按标签筛选</option>
            <option v-for="tag in allTags" :key="tag.id" :value="tag.name">{{ tag.name }}</option>
          </select>
        </div>
      </div>

      <!-- 按年份分组的文章列表 -->
      <main class="posts-container">
        <section
          v-for="year in sortedYears"
          :key="year"
          class="year-group"
        >
          <h2 class="year-title">{{ year }} 年</h2>
          
          <div class="article-list">
            <article
              v-for="article in getArticlesByYear(year)"
              :key="article.id"
              class="article-item"
            >
              <time class="article-date">{{ formatDate(article.createTime) }}</time>
              <div class="article-content">
                <router-link :to="`/article/${article.id}`" class="article-link">
                  <h3 class="article-title">{{ article.title }}</h3>
                </router-link>
                <p class="article-summary" v-if="article.summary">{{ article.summary }}</p>
                <div class="article-tags" v-if="article.tags?.length">
                  <span v-for="tag in article.tags.slice(0, 4)" :key="tag.id || tag.name">#{{ tag.name }}</span>
                </div>
              </div>
            </article>
          </div>
        </section>

        <!-- 空状态 -->
        <div class="empty" v-if="filteredArticles.length === 0 && !loading">
          <p class="empty-icon">--</p>
          <p class="empty-text">还没有文章</p>
        </div>

        <!-- 加载中 -->
        <div class="loading-wrap" v-if="loading">
          <span class="loading-dot">·</span><span class="loading-dot">·</span><span class="loading-dot">·</span>
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

// 格式化日期（MM-DD）
const formatDate = (s) => {
  if (!s) return ''
  const d = new Date(s)
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${m}-${day}`
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
  if (isStaticMode) {
    useFallbackArticles()
    loading.value = false
    return
  }
  try {
    const res = await getArticleList(1, 200)
    if (res.code === 200) {
      articles.value = res.data?.records || []
    } else {
      useFallbackArticles()
    }
  } catch (e) {
    useFallbackArticles()
    if (import.meta.env.DEV) console.info('使用静态文章数据', e?.message || e)
  } finally {
    loading.value = false
  }
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

onMounted(() => {
  Promise.all([loadArticles(), loadTags(), loadCategories()])
})
</script>

<style scoped>
.posts-page {
  min-height: calc(100vh - 72px);
  padding: 56px 0 78px;
}

.posts-inner {
  max-width: 860px;
  margin: 0 auto;
  padding: 0 32px;
}

/* 页面头部 */
.posts-header {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 28px;
  animation: fadeInUp 0.68s var(--ease-out) both;
}

.posts-title {
  font-size: clamp(2rem, 4vw, 3.15rem);
  font-weight: 700;
  color: var(--text);
  letter-spacing: 0;
  font-family: var(--font-serif);
  line-height: 1.1;
}

.posts-count {
  font-size: 13px;
  color: var(--text-lighter);
  padding-bottom: 5px;
}

/* 筛选栏 */
.filter-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 36px;
  padding: 14px;
  border: 1px solid var(--border);
  border-radius: 8px;
  background: rgba(8, 14, 27, 0.72);
  box-shadow: var(--shadow);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  animation: fadeInUp 0.68s var(--ease-out) 0.08s both;
}

.filter-group {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}

.filter-chip {
  padding: 7px 15px;
  border: 1px solid var(--border);
  border-radius: 999px;
  background: rgba(8, 14, 27, 0.62);
  color: var(--text-light);
  font-size: 13px;
  cursor: pointer;
  transition: transform 0.22s var(--ease-out), color 0.22s, border-color 0.22s, background 0.22s;
}

.filter-chip:hover {
  border-color: var(--accent);
  color: var(--accent-dark);
  transform: translateY(-1px);
}

.filter-chip.active {
  background: linear-gradient(135deg, var(--accent), var(--violet));
  border-color: var(--accent);
  color: #06101f;
}

.tag-select {
  padding: 7px 12px;
  border: 1px solid var(--border);
  border-radius: 999px;
  background: rgba(8, 14, 27, 0.62);
  color: var(--text);
  font-size: 13px;
  cursor: pointer;
  outline: none;
}

.tag-select:focus {
  border-color: var(--accent);
}

/* 年份分组 */
.year-group {
  position: relative;
  margin-bottom: 44px;
  padding-left: 18px;
  animation: fadeInUp 0.72s var(--ease-out) both;
}

.year-group::before {
  content: '';
  position: absolute;
  left: 0;
  top: 38px;
  bottom: 8px;
  width: 1px;
  background: linear-gradient(180deg, rgba(56, 248, 255, 0.6), rgba(155, 92, 255, 0.24), transparent);
}

.year-title {
  font-size: 22px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 18px;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--border);
  position: relative;
  font-family: var(--font-serif);
}

.year-title::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 60px;
  height: 1px;
  background: var(--accent);
}

/* 文章列表 */
.article-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.article-item {
  display: flex;
  align-items: flex-start;
  gap: 18px;
  padding: 20px;
  border: 1px solid transparent;
  border-radius: 8px;
  background: rgba(8, 14, 27, 0.44);
  transition: transform 0.24s var(--ease-out), box-shadow 0.24s var(--ease-out), border-color 0.24s, background 0.24s;
}

.article-item:hover {
  background: rgba(16, 28, 50, 0.78);
  border-color: rgba(56, 248, 255, 0.28);
  box-shadow: 0 14px 36px rgba(56, 248, 255, 0.1);
  transform: translateX(8px);
}

.article-date {
  flex-shrink: 0;
  font-size: 13px;
  color: var(--accent);
  font-family: 'SF Mono', 'Consolas', monospace;
  min-width: 48px;
}

.article-link {
  flex: 1;
  text-decoration: none;
}

.article-content {
  min-width: 0;
  flex: 1;
}

.article-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--text);
  line-height: 1.5;
  margin: 0;
  transition: color 0.2s;
}

.article-item:hover .article-title {
  color: var(--accent-soft);
}

.article-summary {
  margin-top: 8px;
  color: var(--text-light);
  font-size: 14px;
  line-height: 1.7;
}

.article-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.article-tags span {
  color: var(--accent);
  font: 700 11px/1 'SF Mono', 'Consolas', monospace;
  padding: 6px 8px;
  border: 1px solid rgba(126, 238, 255, 0.18);
  border-radius: 999px;
  background: rgba(56, 248, 255, 0.07);
}

/* 空状态 */
.empty {
  padding: 80px 0;
  text-align: center;
  animation: fadeInUp 0.6s var(--ease-out) both;
}

.empty-icon {
  font-size: 1.6rem;
  margin-bottom: 12px;
  letter-spacing: 0.2em;
  color: var(--accent);
}

.empty-text {
  color: var(--text-lighter);
  font-size: 14px;
}

/* 加载 */
.loading-wrap {
  text-align: center;
  padding: 60px 0;
  display: flex;
  justify-content: center;
  gap: 4px;
}

.loading-dot {
  font-size: 28px;
  color: var(--text-lighter);
  animation: blink 1.2s infinite;
}

.loading-dot:nth-child(2) { animation-delay: 0.2s; }
.loading-dot:nth-child(3) { animation-delay: 0.4s; }

@keyframes blink {
  0%, 80%, 100% { opacity: 0.2; }
  40% { opacity: 1; }
}

/* 响应式 */
@media (max-width: 600px) {
  .posts-inner {
    padding: 0 20px;
  }
  
  .posts-title {
    font-size: 1.5rem;
  }
  
  .filter-bar {
    flex-direction: column;
    gap: 12px;
  }
  
  .article-item {
    flex-direction: column;
    gap: 5px;
    padding: 14px 0;
    background: transparent;
  }

  .article-item:hover {
    transform: none;
    padding-left: 0;
  }
  
  .article-date {
    font-size: 12px;
  }
  
  .article-title {
    font-size: 14px;
  }
}
</style>
