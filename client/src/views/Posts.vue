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
              <router-link :to="`/article/${article.id}`" class="article-link">
                <h3 class="article-title">{{ article.title }}</h3>
              </router-link>
            </article>
          </div>
        </section>

        <!-- 空状态 -->
        <div class="empty" v-if="filteredArticles.length === 0 && !loading">
          <p class="empty-icon">📭</p>
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

const loadArticles = async () => {
  loading.value = true
  try {
    const res = await getArticleList(1, 200)
    if (res.code === 200) {
      articles.value = res.data?.records || []
    }
  } catch (e) {
    console.error('加载文章失败', e)
  } finally {
    loading.value = false
  }
}

const loadTags = async () => {
  try {
    const res = await getTagList()
    if (res.code === 200) {
      allTags.value = res.data || []
    }
  } catch (e) { console.error('加载标签失败', e) }
}

const loadCategories = async () => {
  try {
    const res = await getCategoryList()
    if (res.code === 200) {
      categories.value = res.data || []
    }
  } catch (e) { console.error('加载分类失败', e) }
}

onMounted(() => {
  Promise.all([loadArticles(), loadTags(), loadCategories()])
})
</script>

<style scoped>
.posts-page {
  min-height: calc(100vh - 72px);
  padding: 40px 0;
}

.posts-inner {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 32px;
}

/* 页面头部 */
.posts-header {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 24px;
}

.posts-title {
  font-size: 1.8rem;
  font-weight: 700;
  color: var(--text);
  letter-spacing: 1px;
}

.posts-count {
  font-size: 13px;
  color: var(--text-lighter);
}

/* 筛选栏 */
.filter-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 32px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--border);
}

.filter-group {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}

.filter-chip {
  padding: 5px 16px;
  border: 1px solid var(--border);
  border-radius: 20px;
  background: transparent;
  color: var(--text-light);
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.filter-chip:hover {
  border-color: var(--accent);
  color: var(--accent-dark);
}

.filter-chip.active {
  background: var(--accent);
  border-color: var(--accent);
  color: #fff;
}

.tag-select {
  padding: 6px 12px;
  border: 1px solid var(--border);
  border-radius: 20px;
  background: var(--bg);
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
  margin-bottom: 40px;
}

.year-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 2px solid var(--border);
  position: relative;
}

.year-title::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 60px;
  height: 2px;
  background: var(--accent);
}

/* 文章列表 */
.article-list {
  display: flex;
  flex-direction: column;
}

.article-item {
  display: flex;
  align-items: baseline;
  gap: 16px;
  padding: 12px 0;
  border-bottom: 1px dashed var(--border);
  transition: all 0.2s;
}

.article-item:hover {
  background: rgba(200, 169, 126, 0.04);
  padding-left: 8px;
  margin-left: -8px;
}

.article-date {
  flex-shrink: 0;
  font-size: 13px;
  color: var(--text-lighter);
  font-family: 'SF Mono', 'Consolas', monospace;
  min-width: 48px;
}

.article-link {
  flex: 1;
  text-decoration: none;
}

.article-title {
  font-size: 15px;
  font-weight: 500;
  color: var(--text);
  line-height: 1.5;
  margin: 0;
  transition: color 0.2s;
}

.article-item:hover .article-title {
  color: var(--accent-dark);
}

/* 空状态 */
.empty {
  padding: 80px 0;
  text-align: center;
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: 12px;
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
    gap: 4px;
    padding: 14px 0;
  }
  
  .article-date {
    font-size: 12px;
  }
  
  .article-title {
    font-size: 14px;
  }
}
</style>
