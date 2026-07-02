<template>
  <div class="projects-page">
    <div class="projects-inner">
      <!-- 页面头部 -->
      <header class="projects-header">
        <h1 class="projects-title">项目</h1>
        <p class="projects-desc">折腾过的有意思的东西</p>
      </header>

      <!-- 统计栏 -->
      <div class="stats-bar">
        <div class="stat-item">
          <span class="stat-count">{{ totalProjects }}</span>
          <span class="stat-label">个项目</span>
        </div>
        <span class="stat-divider">/</span>
        <div class="stat-item">
          <span class="stat-count">{{ categories.length }}</span>
          <span class="stat-label">个类目</span>
        </div>
        <span class="stat-divider">/</span>
        <div class="stat-item">
          <span class="stat-count">{{ totalTechs }}</span>
          <span class="stat-label">种技术</span>
        </div>
        <div class="stats-actions">
          <button 
            class="filter-btn" 
            :class="{ active: filterOpenSource }"
            @click="filterOpenSource = !filterOpenSource"
          >
            仅看开源项目 ({{ openSourceCount }})
          </button>
        </div>
      </div>

      <!-- 项目分组列表 -->
      <main class="projects-container">
        <section 
          v-for="cat in displayCategories" 
          :key="cat.key" 
          class="project-group"
        >
          <h2 class="group-title">
            <span class="group-icon">{{ cat.icon }}</span>
            {{ cat.name }}
            <span class="group-count">({{ getProjectsByCategory(cat.key).length }})</span>
            <button 
              class="group-collapse-btn" 
              @click="toggleGroup(cat.key)"
              :aria-label="collapsedGroups.includes(cat.key) ? '展开' : '收起'"
            >
              <span class="collapse-icon">{{ collapsedGroups.includes(cat.key) ? '+' : '−' }}</span>
            </button>
          </h2>
          
          <div class="project-list" v-show="!collapsedGroups.includes(cat.key)">
            <article 
              v-for="proj in getProjectsByCategory(cat.key)" 
              :key="proj.id" 
              class="project-card"
            >
              <!-- 项目截图 -->
              <figure class="project-gallery" v-if="proj.imageUrl">
                <div class="gallery-container">
                  <img 
                    :src="proj.imageUrl" 
                    :alt="proj.name" 
                    loading="lazy"
                  />
                </div>
              </figure>
              
              <!-- 项目头部 -->
              <header class="project-header">
                <h3 class="project-name">
                  <a :href="proj.url" target="_blank" rel="noopener">{{ proj.name }}</a>
                </h3>
                <div class="project-badges">
                  <span class="badge badge-year">{{ getYear(proj) }}</span>
                  <span class="badge badge-blue" v-if="proj.isOpenSource">开源</span>
                  <span class="badge badge-green" v-if="proj.status === '已上线' || !proj.status">活跃</span>
                  <span class="badge badge-yellow" v-else-if="proj.status?.includes('开发')">开发中</span>
                </div>
              </header>
              
              <!-- 项目统计 -->
              <div class="project-stats">
                <div class="stats-row">
                  <span class="stat" v-if="proj.stats">
                    <span class="stat-icon">◆</span>
                    <span class="stat-text">{{ proj.stats }}</span>
                  </span>
                  <span class="stat" v-if="proj.tag">
                    <span class="stat-lang"></span>
                    <span class="stat-text">{{ proj.tag }}</span>
                  </span>
                  <span class="stat" v-if="proj.isOpenSource && proj.githubUrl">
                    <a :href="proj.githubUrl" target="_blank" rel="noopener" class="github-link">
                      <svg viewBox="0 0 24 24" class="github-icon"><path fill="currentColor" d="M12 2A10 10 0 002 12c0 4.42 2.87 8.17 6.84 9.5.5.08.66-.23.66-.5v-1.69c-2.77.6-3.36-1.34-3.36-1.34-.46-1.16-1.11-1.47-1.11-1.47-.91-.62.07-.6.07-.6 1 .07 1.53 1.03 1.53 1.03.87 1.52 2.34 1.07 2.91.83.09-.65.35-1.09.63-1.34-2.22-.25-4.55-1.11-4.55-4.92 0-1.11.38-2 1.03-2.71-.1-.25-.45-1.29.1-2.64 0 0 .84-.27 2.75 1.02.79-.22 1.65-.33 2.5-.33s1.71.11 2.5.33c1.91-1.29 2.75-1.02 2.75-1.02.55 1.35.2 2.39.1 2.64.65.71 1.03 1.6 1.03 2.71 0 3.82-2.34 4.66-4.57 4.91.36.31.69.92.69 1.85V21c0 .27.16.59.67.5C19.14 20.16 22 16.42 22 12A10 10 0 0012 2z"/></svg>
                      GitHub
                    </a>
                  </span>
                </div>
                <!-- 技术栈 -->
                <div class="tech-stack" v-if="proj.techStack">
                  <span 
                    v-for="tech in proj.techStack.split(',')" 
                    :key="tech" 
                    class="tech-tag"
                  >{{ tech.trim() }}</span>
                </div>
              </div>
              
              <!-- 项目描述 -->
              <p class="project-desc">{{ proj.description || '暂无描述' }}</p>
              
              <!-- 链接 -->
              <div class="project-links">
                <a v-if="proj.url" :href="proj.url" target="_blank" rel="noopener" class="link-btn website">
                  <svg viewBox="0 0 24 24"><path fill="currentColor" d="M16.36 14c.08-.66.14-1.32.14-2s-.06-1.34-.14-2h3.38c.16.64.26 1.31.26 2s-.1 1.36-.26 2m-5.15 5.56c.6-1.11 1.06-2.31 1.38-3.56h2.95a8.03 8.03 0 01-4.33 3.56M14.34 14H9.66c-.1-.66-.16-1.32-.16-2s.06-1.35.16-2h4.68c.09.65.16 1.32.16 2s-.07 1.34-.16 2M12 19.96c-.83-1.2-1.5-2.53-1.91-3.96h3.82c-.41 1.43-1.08 2.76-1.91 3.96M8 8H5.08A7.923 7.923 0 019.4 4.44C8.8 5.55 8.35 6.75 8 8m-2.92 8H8c.35 1.25.8 2.45 1.4 3.56A8.008 8.008 0 015.08 16m-.82-2C4.1 13.36 4 12.69 4 12s.1-1.36.26-2h3.38c-.08.66-.14 1.32-.14 2s.06 1.34.14 2M12 4.03c.83 1.2 1.5 2.54 1.91 3.97h-3.82c.41-1.43 1.08-2.77 1.91-3.97M18.92 8h-2.95a15.65 15.65 0 00-1.38-3.56c1.84.63 3.37 1.9 4.33 3.56M12 2C6.47 2 2 6.5 2 12a10 10 0 0010 10 10 10 0 0010-10A10 10 0 0012 2z"/></svg>
                </a>
                <a v-if="proj.githubUrl" :href="proj.githubUrl" target="_blank" rel="noopener" class="link-btn github">
                  <svg viewBox="0 0 24 24"><path fill="currentColor" d="M12 2A10 10 0 002 12c0 4.42 2.87 8.17 6.84 9.5.5.08.66-.23.66-.5v-1.69c-2.77.6-3.36-1.34-3.36-1.34-.46-1.16-1.11-1.47-1.11-1.47-.91-.62.07-.6.07-.6 1 .07 1.53 1.03 1.53 1.03.87 1.52 2.34 1.07 2.91.83.09-.65.35-1.09.63-1.34-2.22-.25-4.55-1.11-4.55-4.92 0-1.11.38-2 1.03-2.71-.1-.25-.45-1.29.1-2.64 0 0 .84-.27 2.75 1.02.79-.22 1.65-.33 2.5-.33s1.71.11 2.5.33c1.91-1.29 2.75-1.02 2.75-1.02.55 1.35.2 2.39.1 2.64.65.71 1.03 1.6 1.03 2.71 0 3.82-2.34 4.66-4.57 4.91.36.31.69.92.69 1.85V21c0 .27.16.59.67.5C19.14 20.16 22 16.42 22 12A10 10 0 0012 2z"/></svg>
                </a>
                <router-link v-if="proj.articleUrl" :to="proj.articleUrl" class="link-btn article">
                  <svg viewBox="0 0 24 24"><path fill="currentColor" d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z"/></svg>
                </router-link>
              </div>
            </article>
          </div>
        </section>

        <!-- 空状态 -->
        <div class="empty" v-if="filteredProjects.length === 0 && !loading">
          <p>还没有项目，敬请期待 ~</p>
        </div>

        <!-- 加载状态 -->
        <div class="loading" v-if="loading">
          <span class="dot-anim">·</span><span class="dot-anim" style="animation-delay:.15s">·</span><span class="dot-anim" style="animation-delay:.3s">·</span>
        </div>
      </main>

      <!-- 管理员面板 -->
      <div v-if="isAdmin" class="admin-section">
        <div class="admin-toggle" @click="showAdmin = !showAdmin">
          管理员：项目管理
          <span class="toggle-arrow" :class="{ open: showAdmin }">▼</span>
        </div>

        <div v-if="showAdmin" class="admin-panel">
          <div class="panel-header">
            <span class="panel-title">全部项目（{{ allProjects.length }} 个）</span>
            <button class="refresh-btn" @click="loadAll">↻ 刷新</button>
          </div>

          <div class="admin-list">
            <div v-for="proj in allProjects" :key="proj.id" class="admin-item">
              <div class="item-info">
                <span class="item-icon">{{ proj.icon || '⌘' }}</span>
                <span class="item-name">{{ proj.name }}</span>
                <span class="item-tag" v-if="proj.tag">{{ proj.tag }}</span>
                <span class="item-category" v-if="proj.category">{{ getCategoryName(proj.category) }}</span>
                <span class="item-status" :class="proj.isActive ? 'online' : 'offline'">
                  {{ proj.isActive ? '上线' : '下线' }}
                </span>
              </div>
              <div class="item-actions">
                <button class="action-btn edit-btn" @click="startEdit(proj)">编辑</button>
                <button class="action-btn toggle-btn" @click="toggleActive(proj)">
                  {{ proj.isActive ? '下线' : '上线' }}
                </button>
                <button class="action-btn delete-btn" @click="confirmDelete(proj)">删除</button>
              </div>
            </div>
          </div>

          <!-- 添加/编辑表单 -->
          <div class="crud-form">
            <h4 class="form-title">{{ editingId ? '编辑项目' : '添加项目' }}</h4>
            <div class="form-row">
              <input v-model="form.name" placeholder="项目名称 *" class="form-input" />
              <input v-model="form.url" placeholder="项目链接 *" class="form-input" />
            </div>
            <div class="form-row">
              <input v-model="form.githubUrl" placeholder="GitHub链接（开源项目必填）" class="form-input" />
              <input v-model="form.imageUrl" placeholder="项目截图URL" class="form-input" />
            </div>
            <textarea v-model="form.description" placeholder="项目描述" class="form-textarea" rows="2" />
            <div class="form-row-3">
              <input v-model="form.icon" placeholder="图标或短标识" class="form-input" />
              <input v-model="form.tag" placeholder="主语言如 Java" class="form-input" />
              <input v-model="form.techStack" placeholder="技术栈（逗号分隔）" class="form-input" />
            </div>
            <div class="form-row-3">
              <select v-model="form.category" class="form-input">
                <option value="">选择分类</option>
                <option v-for="cat in categories" :key="cat.key" :value="cat.key">{{ cat.name }}</option>
              </select>
              <input v-model="form.badge" placeholder="角标如 NEW" class="form-input" />
              <input v-model="form.stats" placeholder="统计如 1.2k" class="form-input" />
            </div>
            <div class="form-row">
              <input v-model="form.status" placeholder="状态（已上线/开发中/维护中）" class="form-input" />
              <input v-model="form.articleUrl" placeholder="文章链接（选填）" class="form-input" />
            </div>
            <div class="form-row checkbox-row">
              <label class="checkbox-label">
                <input type="checkbox" v-model="form.isOpenSource" />
                <span>开源项目</span>
              </label>
              <label class="checkbox-label">
                <input type="checkbox" v-model="form.isActive" />
                <span>上线显示</span>
              </label>
            </div>
            <div class="form-actions">
              <button class="submit-btn" @click="doSubmit" :disabled="submitting">
                {{ submitting ? '保存中…' : (editingId ? '保存修改' : '添加项目') }}
              </button>
              <button v-if="editingId" class="cancel-btn" @click="cancelEdit">取消</button>
            </div>
            <p class="form-tip" v-if="formError">{{ formError }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { getProjects, getAllProjects, addProject, updateProject, deleteProject } from '../api/project.js'
import { useUserStore } from '../stores/user.js'
import { fallbackProjects, isStaticMode } from '../config/site.config.js'

const userStore = useUserStore()
const isAdmin = computed(() => userStore.isAdmin)

// 项目数据
const projects = ref([])
const allProjects = ref([])
const loading = ref(false)

// 筛选和折叠
const filterOpenSource = ref(false)
const collapsedGroups = ref([])

// 管理面板
const showAdmin = ref(false)
const editingId = ref(null)
const submitting = ref(false)
const formError = ref('')

// 分类配置
const categories = [
  { key: 'web', name: 'Web 应用', icon: '◇' },
  { key: 'tool', name: '效率工具', icon: '⌘' },
  { key: 'education', name: '教育类', icon: '▦' },
  { key: 'mobile', name: '移动端', icon: '▣' },
  { key: 'other', name: '其他', icon: '◆' }
]

// 表单
const form = ref({
  name: '',
  url: '',
  githubUrl: '',
  imageUrl: '',
  description: '',
  icon: '⌘',
  tag: '',
  techStack: '',
  badge: '',
  category: 'other',
  stats: '',
  status: '已上线',
  isOpenSource: false,
  isActive: true,
  articleUrl: '',
})

// 计算属性
const filteredProjects = computed(() => {
  if (!filterOpenSource.value) return projects.value
  return projects.value.filter(p => p.isOpenSource)
})

const openSourceCount = computed(() => projects.value.filter(p => p.isOpenSource).length)

const totalProjects = computed(() => projects.value.length)

const totalTechs = computed(() => {
  const techs = new Set()
  projects.value.forEach(p => {
    if (p.techStack) {
      p.techStack.split(',').forEach(t => techs.add(t.trim()))
    }
    if (p.tag) techs.add(p.tag)
  })
  return techs.size
})

const displayCategories = computed(() => {
  return categories.filter(cat => getProjectsByCategory(cat.key).length > 0)
})

// 方法
const getProjectsByCategory = (categoryKey) => {
  return filteredProjects.value.filter(p => (p.category || 'other') === categoryKey)
}

const getCategoryName = (key) => {
  const cat = categories.find(c => c.key === key)
  return cat ? cat.name : '其他'
}

const getYear = (proj) => {
  if (proj.createTime) {
    return new Date(proj.createTime).getFullYear()
  }
  return new Date().getFullYear()
}

const toggleGroup = (key) => {
  const idx = collapsedGroups.value.indexOf(key)
  if (idx === -1) {
    collapsedGroups.value.push(key)
  } else {
    collapsedGroups.value.splice(idx, 1)
  }
}

const activeFallbackProjects = () => fallbackProjects.filter(p => p.isActive !== 0)

// 加载数据
const loadProjects = async () => {
  loading.value = true
  if (isStaticMode) {
    projects.value = activeFallbackProjects()
    loading.value = false
    return
  }
  try {
    const res = await getProjects()
    projects.value = res.data?.length ? res.data : activeFallbackProjects()
  } catch (e) {
    projects.value = activeFallbackProjects()
    if (import.meta.env.DEV) console.info('[Projects] 使用静态项目数据', e?.message || e)
  } finally {
    loading.value = false
  }
}

const loadAll = async () => {
  if (isStaticMode) {
    allProjects.value = fallbackProjects
    return
  }
  try {
    const res = await getAllProjects()
    allProjects.value = res.data?.length ? res.data : fallbackProjects
  } catch (e) {
    allProjects.value = fallbackProjects
    if (import.meta.env.DEV) console.info('[Projects] 使用静态项目管理数据', e?.message || e)
  }
}

// 编辑
const startEdit = (proj) => {
  editingId.value = proj.id
  form.value = {
    name: proj.name,
    url: proj.url,
    githubUrl: proj.githubUrl || '',
    imageUrl: proj.imageUrl || '',
    description: proj.description || '',
    icon: proj.icon || '⌘',
    tag: proj.tag || '',
    techStack: proj.techStack || '',
    badge: proj.badge || '',
    category: proj.category || 'other',
    stats: proj.stats || '',
    status: proj.status || '已上线',
    isOpenSource: proj.isOpenSource || false,
    isActive: proj.isActive !== 0,
    articleUrl: proj.articleUrl || '',
  }
}

const cancelEdit = () => {
  editingId.value = null
  resetForm()
}

const resetForm = () => {
  form.value = {
    name: '', url: '', githubUrl: '', imageUrl: '', description: '',
    icon: '⌘', tag: '', techStack: '', badge: '', category: 'other',
    stats: '', status: '已上线', isOpenSource: false, isActive: true, articleUrl: ''
  }
  formError.value = ''
}

// 提交
const doSubmit = async () => {
  formError.value = ''
  if (!form.value.name.trim()) { formError.value = '项目名称不能为空'; return }
  if (!form.value.url.trim()) { formError.value = '项目链接不能为空'; return }
  submitting.value = true
  try {
    const data = { ...form.value, isActive: form.value.isActive ? 1 : 0, isOpenSource: form.value.isOpenSource ? 1 : 0 }
    if (editingId.value) {
      await updateProject(editingId.value, data)
    } else {
      await addProject(data)
    }
    editingId.value = null
    resetForm()
    await loadAll()
    await loadProjects()
  } catch (e) {
    formError.value = e.message || '保存失败'
  } finally {
    submitting.value = false
  }
}

// 上下线
const toggleActive = async (proj) => {
  try {
    await updateProject(proj.id, { ...proj, isActive: proj.isActive ? 0 : 1 })
    await loadAll()
    await loadProjects()
  } catch (e) {
    console.error('[Projects] 切换状态失败', e)
  }
}

// 删除
const confirmDelete = async (proj) => {
  if (!confirm(`确定删除项目「${proj.name}」吗？`)) return
  try {
    await deleteProject(proj.id)
    await loadAll()
    await loadProjects()
    if (editingId.value === proj.id) cancelEdit()
  } catch (e) {
    console.error('[Projects] 删除失败', e)
  }
}

watch(showAdmin, (val) => { if (val) loadAll() })
onMounted(() => { loadProjects() })
</script>

<style scoped>
.projects-page {
  min-height: calc(100vh - 72px);
  padding: 56px 0 86px;
}
.projects-inner {
  max-width: 1100px;
  margin: 0 auto;
  padding: 0 32px;
}
.projects-header {
  margin-bottom: 28px;
  animation: fadeInUp 0.68s var(--ease-out) both;
}
.projects-title {
  font-size: clamp(2rem, 4vw, 3.15rem);
  font-weight: 700;
  color: var(--text);
  margin-bottom: 6px;
  font-family: var(--font-serif);
  line-height: 1.1;
}
.projects-desc {
  font-size: 15px;
  color: var(--text-light);
}

/* 统计栏 */
.stats-bar {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 18px;
  margin-bottom: 32px;
  border: 1px solid var(--border);
  border-radius: 8px;
  background: rgba(8, 14, 27, 0.72);
  box-shadow: var(--shadow);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  flex-wrap: wrap;
  animation: fadeInUp 0.68s var(--ease-out) 0.08s both;
}
.stat-item {
  display: flex;
  align-items: baseline;
  gap: 4px;
}
.stat-count {
  font-size: 20px;
  font-weight: 700;
  color: var(--accent);
}
.stat-label {
  font-size: 13px;
  color: var(--text-light);
}
.stat-divider {
  color: var(--border);
  font-weight: 300;
}
.stats-actions {
  margin-left: auto;
}
.filter-btn {
  background: rgba(8, 14, 27, 0.62);
  border: 1px solid var(--border);
  border-radius: 999px;
  padding: 7px 16px;
  font-size: 13px;
  color: var(--text-light);
  cursor: pointer;
  transition: transform 0.22s var(--ease-out), color 0.22s, border-color 0.22s, background 0.22s;
}
.filter-btn:hover {
  border-color: var(--accent);
  color: var(--accent-dark);
  transform: translateY(-1px);
}
.filter-btn.active {
  background: linear-gradient(135deg, var(--accent), var(--violet));
  border-color: var(--accent);
  color: #06101f;
}

/* 项目分组 */
.project-group {
  margin-bottom: 38px;
  animation: fadeInUp 0.72s var(--ease-out) both;
}
.group-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 16px;
  padding: 0 0 10px;
  border-bottom: 1px solid var(--border);
  font-family: var(--font-serif);
}
.group-icon { font-size: 1.2em; }
.group-count {
  font-size: 14px;
  font-weight: 400;
  color: var(--text-light);
}
.group-collapse-btn {
  margin-left: auto;
  background: none;
  border: 1px solid var(--border);
  border-radius: 6px;
  width: 24px;
  height: 24px;
  cursor: pointer;
  color: var(--text-light);
  display: flex;
  align-items: center;
  justify-content: center;
}
.collapse-icon { font-size: 14px; }

.project-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 20px;
}

.project-card {
  background: rgba(8, 14, 27, 0.72);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  overflow: hidden;
  box-shadow: var(--shadow);
  backdrop-filter: blur(14px);
  -webkit-backdrop-filter: blur(14px);
  transition: transform 0.28s var(--ease-out), box-shadow 0.28s var(--ease-out), border-color 0.28s;
}
.project-card:hover {
  box-shadow: var(--shadow-hover);
  transform: translateY(-6px);
  border-color: rgba(56, 248, 255, 0.36);
}

/* 项目截图 */
.project-gallery {
  margin: 0;
  background:
    linear-gradient(135deg, rgba(56, 248, 255, 0.08), rgba(155, 92, 255, 0.1)),
    var(--bg-soft);
}
.gallery-container {
  aspect-ratio: 16/9;
  overflow: hidden;
}
.gallery-container img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}
.project-card:hover .gallery-container img {
  transform: scale(1.035);
}

/* 项目头部 */
.project-header {
  padding: 16px 16px 0;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 8px;
}
.project-name {
  font-size: 16px;
  font-weight: 600;
  margin: 0;
}
.project-name a {
  color: var(--text);
  text-decoration: none;
  transition: color 0.2s;
}
.project-card:hover .project-name a {
  color: var(--accent-soft);
}
.project-badges {
  display: flex;
  gap: 6px;
  flex-shrink: 0;
}
.badge {
  font-size: 10px;
  padding: 2px 8px;
  border-radius: 999px;
  font-weight: 500;
}
.badge-year {
  background: rgba(8, 14, 27, 0.7);
  color: var(--text-light);
  border: 1px solid var(--border);
}
.badge-blue {
  background: rgba(56, 248, 255, 0.12);
  color: var(--accent);
}
.badge-green {
  background: rgba(141, 248, 199, 0.12);
  color: var(--ink-blue);
}
.badge-yellow {
  background: rgba(255, 189, 102, 0.12);
  color: var(--amber);
}

/* 项目统计 */
.project-stats {
  padding: 12px 16px;
}
.stats-row {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}
.stat {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--text-light);
}
.stat-icon { font-size: 12px; }
.stat-lang {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: var(--accent);
}
.github-link {
  color: var(--text-light);
  text-decoration: none;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: color 0.2s;
}
.github-link:hover { color: var(--text); }
.github-icon {
  width: 14px;
  height: 14px;
}
.tech-stack {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 8px;
}
.tech-tag {
  font-size: 11px;
  padding: 3px 8px;
  background: rgba(8, 14, 27, 0.62);
  border: 1px solid var(--border);
  border-radius: 999px;
  color: var(--text-light);
}

/* 项目描述 */
.project-desc {
  padding: 0 16px 12px;
  font-size: 13px;
  color: var(--text-light);
  line-height: 1.6;
  margin: 0;
}

/* 项目链接 */
.project-links {
  display: flex;
  gap: 8px;
  padding: 12px 16px;
  border-top: 1px solid var(--border);
}
.link-btn {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(8, 14, 27, 0.62);
  border: 1px solid var(--border);
  color: var(--text-light);
  text-decoration: none;
  transition: all 0.2s;
}
.link-btn:hover {
  border-color: var(--accent);
  color: var(--accent);
  transform: translateY(-2px);
}
.link-btn svg {
  width: 16px;
  height: 16px;
}

/* 空状态和加载 */
.empty {
  text-align: center;
  padding: 60px 0;
  color: var(--text-lighter);
  font-size: 14px;
}
.loading {
  text-align: center;
  padding: 20px;
  color: var(--text-lighter);
  font-size: 20px;
  letter-spacing: 6px;
}
.dot-anim {
  animation: dotBlink 1.2s ease-in-out infinite;
  display: inline-block;
}
@keyframes dotBlink {
  0%,80%,100% { opacity: 0.2; }
  40% { opacity: 1; }
}

/* 管理区 */
.admin-section { margin-top: 48px; }
.admin-toggle {
  background: rgba(8, 14, 27, 0.72);
  border: 1px dashed var(--accent);
  border-radius: 8px;
  padding: 12px 20px;
  cursor: pointer;
  font-size: 14px;
  color: var(--accent);
  display: flex;
  align-items: center;
  gap: 8px;
  user-select: none;
}
.admin-toggle:hover { background: rgba(56, 248, 255, 0.08); }
.toggle-arrow {
  margin-left: auto;
  transition: transform 0.2s;
  font-size: 11px;
}
.toggle-arrow.open { transform: rotate(180deg); }

.admin-panel {
  background: rgba(8, 14, 27, 0.82);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  padding: 20px;
  margin-top: 12px;
}
.panel-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}
.panel-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text);
}
.refresh-btn {
  background: var(--bg);
  border: 1px solid var(--border);
  border-radius: 6px;
  padding: 4px 12px;
  font-size: 12px;
  color: var(--text-light);
  cursor: pointer;
}

.admin-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 20px;
}
.admin-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 10px 14px;
  background: var(--bg);
  border-radius: 8px;
  border: 1px solid var(--border);
}
.item-info {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
  min-width: 0;
}
.item-icon { font-size: 1.2rem; }
.item-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text);
}
.item-tag, .item-category {
  font-size: 11px;
  color: var(--text-light);
  background: var(--card);
  border: 1px solid var(--border);
  padding: 1px 8px;
  border-radius: 10px;
}
.item-status {
  font-size: 11px;
  padding: 1px 8px;
  border-radius: 10px;
}
.item-status.online { background: rgba(74,174,80,0.12); color: #4aae50; }
.item-status.offline { background: rgba(200,169,126,0.12); color: var(--text-lighter); }
.item-actions { display: flex; gap: 6px; }
.action-btn {
  background: none;
  border: 1px solid var(--border);
  border-radius: 6px;
  padding: 3px 10px;
  font-size: 12px;
  cursor: pointer;
  color: var(--text-light);
  transition: all 0.15s;
}
.action-btn:hover { border-color: var(--accent); color: var(--accent-dark); }
.delete-btn:hover { border-color: #e5533c; color: #e5533c; }

/* 表单 */
.crud-form {
  border-top: 1px solid var(--border);
  padding-top: 20px;
}
.form-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 14px;
}
.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-bottom: 10px;
}
.form-row-3 {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 12px;
  margin-bottom: 10px;
}
.form-input {
  background: rgba(8, 14, 27, 0.62);
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 8px 14px;
  font-size: 13px;
  color: var(--text);
  width: 100%;
  box-sizing: border-box;
  transition: border-color 0.2s;
}
.form-input:focus { outline: none; border-color: var(--accent); }
.form-textarea {
  background: rgba(8, 14, 27, 0.62);
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 8px 14px;
  font-size: 13px;
  color: var(--text);
  width: 100%;
  box-sizing: border-box;
  resize: vertical;
  font-family: inherit;
  margin-bottom: 10px;
}
.checkbox-row {
  display: flex;
  gap: 20px;
}
.checkbox-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--text-light);
  cursor: pointer;
}
.form-actions {
  display: flex;
  gap: 10px;
  margin-top: 12px;
}
.submit-btn {
  background: var(--accent);
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 8px 20px;
  font-size: 13px;
  cursor: pointer;
}
.submit-btn:hover { opacity: 0.85; }
.submit-btn:disabled { opacity: 0.5; cursor: not-allowed; }
.cancel-btn {
  background: none;
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 8px 16px;
  font-size: 13px;
  color: var(--text-light);
  cursor: pointer;
}
.form-tip {
  font-size: 12px;
  color: #e5533c;
  margin-top: 8px;
}

@media (max-width: 600px) {
  .projects-inner { padding: 0 20px; }
  .project-list { grid-template-columns: 1fr; }
  .form-row, .form-row-3 { grid-template-columns: 1fr; }
  .admin-item { flex-direction: column; align-items: flex-start; }
  .item-actions { flex-wrap: wrap; }
  .stats-bar {
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    align-items: stretch;
    gap: 10px;
  }
  .stat-divider { display: none; }
  .stat-item {
    flex-direction: column;
    padding: 10px;
    border: 1px solid var(--border);
    border-radius: 8px;
    background: rgba(8, 14, 27, 0.42);
  }
  .stats-actions {
    grid-column: 1 / -1;
    margin-left: 0;
    margin-top: 2px;
  }
  .filter-btn {
    width: 100%;
    justify-content: center;
  }
}
</style>
