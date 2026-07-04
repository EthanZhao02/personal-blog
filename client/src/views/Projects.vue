<template>
  <div class="projects-page">
    <div class="page-bg" aria-hidden="true">
      <div class="bg-grid-pattern"></div>
    </div>

    <div class="projects-container">
      <!-- 项目矩阵 -->
      <main class="project-matrix">
        <div
          v-for="(project, index) in projects"
          :key="project.id || index"
          class="project-cell"
          :class="{ expanded: expandedId === (project.id || index) }"
        >
          <!-- 收起态 -->
          <div class="cell-collapsed" @click="toggleExpand(project.id || index)">
            <div class="cell-header">
              <div class="cell-icon" :style="{ '--icon-color': project.color || '#60a5fa' }">
                <img v-if="isImageUrl(project.icon)" :src="resolveUrl(project.icon)" :alt="project.name" />
                <span v-else>{{ project.icon || '◇' }}</span>
              </div>
              <div class="cell-info">
                <h3 class="cell-title">{{ project.name }}</h3>
                <div class="cell-meta-row">
                  <span class="cell-tag">{{ project.tag || project.category || 'PROJECT' }}</span>
                  <span class="cell-status" v-if="project.status" :class="statusClass(project.status)">{{ project.status }}</span>
                </div>
              </div>
            </div>
            <p class="cell-summary">{{ project.description }}</p>
            <div class="cell-expand-indicator">
              <span class="indicator-icon">{{ expandedId === (project.id || index) ? '−' : '+' }}</span>
            </div>
          </div>

          <!-- 展开态 -->
          <transition name="expand">
            <div v-if="expandedId === (project.id || index)" class="cell-expanded">
              <div class="expanded-content">
                <div class="expanded-section">
                  <span class="section-label">{{ ui.overview }}</span>
                  <p class="section-text">{{ project.description }}</p>
                </div>

                <div class="expanded-section" v-if="project.status">
                  <span class="section-label">{{ ui.status }}</span>
                  <span class="tech-tag" :class="{ 'status-active': project.status === '已上线' }">{{ project.status }}</span>
                </div>

                <div class="expanded-section" v-if="project.features?.length">
                  <span class="section-label">{{ ui.features }}</span>
                  <ul class="feature-list">
                    <li v-for="feature in project.features" :key="feature">{{ feature }}</li>
                  </ul>
                </div>

                <div class="expanded-section" v-if="project.techStack">
                  <span class="section-label">{{ ui.techStack }}</span>
                  <div class="tech-tags">
                    <span v-for="tech in project.techStack.split(', ')" :key="tech" class="tech-tag">{{ tech }}</span>
                  </div>
                </div>

                <div class="expanded-actions" v-if="project.url || project.githubUrl">
                  <a v-if="project.url && project.url !== '#'" :href="project.url" target="_blank" rel="noopener" class="action-btn primary">
                    <span>{{ ui.visit }}</span>
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M7 17l9.2-9.2M17 17V7H7"/>
                    </svg>
                  </a>
                  <a v-if="project.githubUrl" :href="project.githubUrl" target="_blank" rel="noopener" class="action-btn">
                    <span>{{ ui.source }}</span>
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor">
                      <path d="M12 2A10 10 0 0 0 2 12c0 4.42 2.87 8.17 6.84 9.5.5.08.66-.23.66-.5v-1.69c-2.77.6-3.36-1.34-3.36-1.34-.46-1.16-1.11-1.47-1.11-1.47-.91-.62.07-.6.07-.6 1 .07 1.53 1.03 1.53 1.03.87 1.52 2.34 1.07 2.91.83.09-.65.35-1.09.63-1.34-2.22-.25-4.55-1.11-4.55-4.92 0-1.11.38-2 1.03-2.71-.1-.25-.45-1.29.1-2.64 0 0 .84-.27 2.75 1.02.79-.22 1.65-.33 2.5-.33s1.71.11 2.5.33c1.91-1.29 2.75-1.02 2.75-1.02.55 1.35.2 2.39.1 2.64.65.71 1.03 1.6 1.03 2.71 0 3.82-2.34 4.66-4.57 4.91.36.31.69.92.69 1.85V21c0 .27.16.59.67.5C19.14 20.16 22 16.42 22 12A10 10 0 0 0 12 2z"/>
                    </svg>
                  </a>
                </div>
              </div>
            </div>
          </transition>

          <!-- 管理员按钮 -->
          <div class="cell-admin-bar" v-if="userStore.isAdmin">
            <button @click.stop="openEdit(project)" class="cell-admin-btn">编辑</button>
            <button @click.stop="handleDelete(project.id)" class="cell-admin-btn cell-del-btn">删除</button>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-if="!loading && !projects.length" class="empty-state">
          <div class="empty-icon">◇</div>
          <p>{{ ui.empty }}</p>
          <button v-if="userStore.isAdmin" class="add-project-btn" @click="openAdd">+ {{ ui.addProject }}</button>
        </div>
      </main>

      <!-- 管理员添加按钮 -->
      <button v-if="userStore.isAdmin && !loading && projects.length" class="add-project-btn" @click="openAdd">
        + {{ ui.addProject }}
      </button>

      <!-- 表单弹窗 -->
      <div v-if="showForm" class="form-overlay" @click.self="showForm = false">
        <div class="form-panel">
          <h3 class="form-title">{{ editingId ? '编辑项目' : '添加项目' }}</h3>
          <div class="form-row"><label>名称 *</label><input v-model="form.name" placeholder="项目名称" /></div>
          <div class="form-row"><label>图标</label>
            <div class="icon-input-row">
              <input v-model="form.icon" placeholder="文字或粘贴URL" style="flex:1" />
              <button class="icon-upload-btn" @click="triggerIconUpload" type="button">上传</button>
              <input ref="iconInput" type="file" accept="image/*" hidden @change="onIconFilePicked" />
            </div>
          </div>
          <div class="form-row"><label>主题色</label><input v-model="form.color" type="color" /></div>
          <div class="form-row"><label>标签</label><input v-model="form.tag" placeholder="如：TOOL / OPEN-SOURCE" /></div>
          <div class="form-row"><label>描述</label><input v-model="form.description" placeholder="一句话描述" /></div>
          <div class="form-row"><label>状态</label><input v-model="form.status" placeholder="如：已上线 / 开发中" /></div>
          <div class="form-row"><label>技术栈</label><input v-model="form.techStack" placeholder="用, 分隔" /></div>
          <div class="form-row"><label>功能</label><input v-model="form.features" placeholder="用, 分隔" /></div>
          <div class="form-row"><label>项目链接</label><input v-model="form.url" placeholder="https://..." /></div>
          <div class="form-row"><label>GitHub</label><input v-model="form.githubUrl" placeholder="https://github.com/..." /></div>
          <div class="form-actions">
            <button class="form-cancel" @click="showForm = false">取消</button>
            <button class="form-ok" @click="submitForm">{{ editingId ? '更新' : '添加' }}</button>
          </div>
        </div>
      </div>
    </div>
    <!-- 裁剪对话框 -->
    <CropDialog
      v-if="showCrop"
      :imageFile="cropFile"
      :aspectRatio="1"
      @crop="onIconCropDone"
      @cancel="showCrop = false"
    />
  </div>
</template>

<script setup>
import { computed, inject, ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'
import { getProjects, addProject, updateProject, deleteProject } from '../api/project'
import { uploadImage } from '../api/upload'
import CropDialog from '../components/CropDialog.vue'
import { fallbackProjects } from '../config/site.config.js'

const apiBaseUrl = (import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080').replace(/\/api\/?$/, '')
const resolveUrl = (url) => {
  if (!url) return ''
  if (/^https?:\/\//.test(url)) return url
  if (url.startsWith('/upload/')) return apiBaseUrl + url
  return url
}
const isImageUrl = (url) => /\.(png|jpg|jpeg|gif|webp|svg)(\?.*)?$/i.test(url) || url?.startsWith('http') && /\.(png|jpg|jpeg|gif|webp|svg)/i.test(url?.split('?')[0])

const statusClass = (s) => {
  if (!s) return ''
  if (s.includes('已上线') || s.includes('Live') || s.includes('online')) return 'status-live'
  if (s.includes('开发') || s.includes('WIP') || s.includes('Dev')) return 'status-wip'
  if (s.includes('开源') || s.includes('Open')) return 'status-oss'
  return ''
}

const route = useRoute()
const userStore = useUserStore()
const siteLanguage = inject('siteLanguage', ref(localStorage.getItem('ethan-language') || 'zh'))
const expandedId = ref(null)
const projects = ref([])
const loading = ref(true)
const showForm = ref(false)
const editingId = ref(null)
const form = ref({ name: '', icon: '', color: '#60a5fa', tag: '', description: '', status: '', techStack: '', features: '', url: '', githubUrl: '' })
const iconInput = ref(null)
const showCrop = ref(false)
const cropFile = ref(null)

const ui = computed(() => siteLanguage.value === 'en'
  ? {
      badge: 'PROJECTS',
      title: 'Project Matrix',
      desc: 'Products · Tools · Experiments · Open Source',
      overview: 'Overview',
      status: 'Status',
      features: 'Core Features',
      techStack: 'Tech Stack',
      visit: 'Visit Project',
      source: 'Source Code',
      empty: 'No projects yet',
      addProject: 'Add Project',
    }
  : {
      badge: 'PROJECTS',
      title: '项目矩阵',
      desc: '产品 · 工具 · 实验 · 开源',
      overview: '概述',
      status: '状态',
      features: '核心功能',
      techStack: '技术栈',
      visit: '访问项目',
      source: '源代码',
      empty: '暂无项目',
      addProject: '添加项目',
    })

const normalizeProjects = (list = []) => list.map(p => ({
  ...p,
  features: Array.isArray(p.features)
    ? p.features
    : (p.features ? String(p.features).split(/[,，]\s*/).filter(Boolean) : [])
}))

const useFallbackProjects = () => {
  projects.value = normalizeProjects(fallbackProjects)
}

const triggerIconUpload = () => iconInput.value?.click()
const onIconFilePicked = (e) => {
  const file = e.target.files[0]
  if (!file) return
  cropFile.value = file
  showCrop.value = true
  e.target.value = ''
}
const onIconCropDone = async (croppedFile) => {
  showCrop.value = false
  try {
    const res = await uploadImage(croppedFile)
    if (res.code === 200 && res.data) {
      form.value.icon = resolveUrl(res.data)
    }
  } catch (err) {
    console.error('图标上传失败', err)
  }
}

const loadProjects = async () => {
  loading.value = true
  try {
    const url = userStore.isAdmin ? '/project/all' : '/project/list'
    const res = await getProjects(url)
    if ((res.code === 200 || res.code === 0) && res.data?.length) {
      projects.value = normalizeProjects(res.data)
    } else {
      useFallbackProjects()
    }
  } catch (e) {
    useFallbackProjects()
  }
  loading.value = false
}

// 切换展开
const toggleExpand = (id) => {
  expandedId.value = expandedId.value === id ? null : id
}

// 从路由查询参数自动展开
if (route.query.id) {
  const id = isNaN(Number(route.query.id)) ? route.query.id : Number(route.query.id)
  expandedId.value = id
}

const openAdd = () => {
  editingId.value = null
  form.value = { name: '', icon: '◇', color: '#60a5fa', tag: '', description: '', status: '', techStack: '', features: '', url: '', githubUrl: '' }
  showForm.value = true
}

const openEdit = (p) => {
  editingId.value = p.id
  form.value = {
    name: p.name || '', icon: p.icon || '◇', color: p.color || '#60a5fa',
    tag: p.tag || '', description: p.description || '', status: p.status || '',
    techStack: p.techStack || '', features: Array.isArray(p.features) ? p.features.join(', ') : (p.features || ''),
    url: p.url || '', githubUrl: p.githubUrl || ''
  }
  showForm.value = true
}

const submitForm = async () => {
  if (!form.value.name) { alert('项目名称必填'); return }
  const data = { ...form.value }
  try {
    if (editingId.value) {
      await updateProject(editingId.value, data)
    } else {
      await addProject(data)
    }
    showForm.value = false
    loadProjects()
  } catch (e) {
    alert('操作失败：' + (e?.message || '网络错误'))
  }
}

const handleDelete = async (id) => {
  if (!confirm('确定删除这个项目吗？')) return
  try {
    await deleteProject(id)
    loadProjects()
  } catch (e) {
    alert('删除失败：' + (e?.message || '网络错误'))
  }
}

onMounted(loadProjects)
</script>

<style scoped>
.projects-page {
  position: relative;
  min-height: 100vh;
  padding: 80px 24px 100px;
}

.page-bg {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 0;
}

.bg-grid-pattern {
  position: absolute;
  inset: 0;
  background-image: 
    linear-gradient(rgba(96, 165, 250, 0.02) 1px, transparent 1px),
    linear-gradient(90deg, rgba(96, 165, 250, 0.02) 1px, transparent 1px);
  background-size: 40px 40px;
  mask-image: radial-gradient(ellipse at center, black 0%, transparent 70%);
}

.projects-container {
  position: relative;
  z-index: 1;
  max-width: 1200px;
  margin: 0 auto;
}

/* 项目矩阵 */
.project-matrix {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 20px;
}

.project-cell {
  position: relative;
  background: rgba(12, 20, 35, 0.6);
  border: 1px solid rgba(96, 165, 250, 0.1);
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
}

.project-cell:hover {
  border-color: rgba(96, 165, 250, 0.25);
}

.project-cell.expanded {
  grid-column: 1 / -1;
  border-color: rgba(96, 165, 250, 0.3);
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.3);
}

/* 收起态 */
.cell-collapsed {
  padding: 24px;
  cursor: pointer;
  position: relative;
}

.cell-header {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 16px;
}

.cell-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: var(--icon-color, #60a5fa);
  background: rgba(96, 165, 250, 0.1);
  border: 1px solid rgba(96, 165, 250, 0.2);
  border-radius: 12px;
  flex-shrink: 0;
  overflow: hidden;
}

.cell-icon img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.icon-input-row {
  display: flex;
  gap: 8px;
}

.icon-upload-btn {
  padding: 8px 14px;
  background: rgba(96, 165, 250, 0.1);
  border: 1px solid rgba(96, 165, 250, 0.3);
  border-radius: 6px;
  color: var(--accent);
  font-size: 13px;
  cursor: pointer;
  white-space: nowrap;
}

.cell-info {
  flex: 1;
  min-width: 0;
}

.cell-title {
  font-size: 16px;
  font-weight: 600;
  color: #fff;
  margin: 0 0 4px 0;
}

.cell-tag {
  font-size: 11px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.4);
  letter-spacing: 0.1em;
}

.cell-meta-row {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-wrap: wrap;
}

.cell-status {
  font-size: 10px;
  font-weight: 600;
  padding: 1px 6px;
  border-radius: 3px;
  letter-spacing: 0.05em;
}

.cell-status.status-live {
  background: rgba(56, 189, 248, 0.14);
  color: #60a5fa;
  border: 1px solid rgba(56, 189, 248, 0.32);
}

.cell-status.status-wip {
  background: rgba(250, 204, 21, 0.15);
  color: #facc15;
  border: 1px solid rgba(250, 204, 21, 0.3);
}

.cell-status.status-oss {
  background: rgba(96, 165, 250, 0.1);
  color: #60a5fa;
  border: 1px solid rgba(96, 165, 250, 0.2);
}

.project-cell:hover .cell-icon {
  transform: scale(1.08);
  transition: transform 0.3s;
}

.cell-summary {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
  line-height: 1.6;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.cell-expand-indicator {
  position: absolute;
  top: 24px;
  right: 24px;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(96, 165, 250, 0.2);
  border-radius: 8px;
  transition: all 0.2s ease;
}

.project-cell:hover .cell-expand-indicator {
  border-color: rgba(96, 165, 250, 0.4);
  background: rgba(96, 165, 250, 0.1);
}

.indicator-icon {
  font-size: 20px;
  font-weight: 300;
  color: #60a5fa;
  line-height: 1;
}

/* 展开态 */
.cell-expanded {
  border-top: 1px solid rgba(96, 165, 250, 0.1);
}

.expanded-content {
  padding: 24px;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 32px;
}

.expanded-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.section-label {
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.15em;
  color: rgba(96, 165, 250, 0.6);
  padding-bottom: 8px;
  border-bottom: 1px solid rgba(96, 165, 250, 0.1);
}

.section-text {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  line-height: 1.7;
  margin: 0;
}

.feature-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.feature-list li {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.6);
  padding-left: 20px;
  position: relative;
  line-height: 1.5;
}

.feature-list li::before {
  content: '▹';
  position: absolute;
  left: 0;
  color: #60a5fa;
}

.tech-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tech-tag {
  font-size: 12px;
  padding: 6px 12px;
  background: rgba(96, 165, 250, 0.1);
  border: 1px solid rgba(96, 165, 250, 0.2);
  border-radius: 6px;
  color: rgba(96, 165, 250, 0.8);
}

.expanded-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.2s ease;
  border: 1px solid rgba(96, 165, 250, 0.2);
  background: rgba(96, 165, 250, 0.05);
  color: rgba(255, 255, 255, 0.8);
}

.action-btn:hover {
  background: rgba(96, 165, 250, 0.1);
  border-color: rgba(96, 165, 250, 0.4);
}

.action-btn.primary {
  background: linear-gradient(135deg, rgba(96, 165, 250, 0.2), rgba(155, 92, 255, 0.2));
  border-color: rgba(96, 165, 250, 0.4);
  color: #fff;
}

/* 展开动画 */
.expand-enter-active,
.expand-leave-active {
  transition: all 0.3s ease;
  overflow: hidden;
}

.expand-enter-from,
.expand-leave-to {
  opacity: 0;
  max-height: 0;
}

.expand-enter-to,
.expand-leave-from {
  opacity: 1;
  max-height: 600px;
}

/* 空状态 */
.empty-state {
  grid-column: 1 / -1;
  text-align: center;
  padding: 80px 0;
  color: rgba(255, 255, 255, 0.5);
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
  opacity: 0.3;
}

/* 管理员按钮 */
.cell-admin-bar {
  display: flex;
  gap: 6px;
  padding: 0 20px 12px;
}

.cell-admin-btn {
  padding: 3px 10px;
  font-size: 11px;
  border-radius: 4px;
  border: 1px solid rgba(255, 255, 255, 0.15);
  background: none;
  color: #888;
  cursor: pointer;
  transition: all 0.2s;
}

.cell-admin-btn:hover {
  border-color: var(--accent);
  color: var(--accent);
}

.cell-del-btn:hover {
  border-color: #ff6464;
  color: #ff6464;
}

.add-project-btn {
  display: block;
  margin: 24px auto 0;
  padding: 10px 24px;
  background: linear-gradient(135deg, rgba(96, 165, 250, 0.2), rgba(155, 92, 255, 0.2));
  border: 1px solid rgba(96, 165, 250, 0.4);
  border-radius: 8px;
  color: #fff;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.add-project-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(96, 165, 250, 0.2);
}

/* 表单弹窗 */
.form-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.form-panel {
  background: rgba(12, 20, 35, 0.95);
  border: 1px solid rgba(96, 165, 250, 0.2);
  border-radius: 16px;
  padding: 24px;
  width: 90%;
  max-width: 420px;
  max-height: 80vh;
  overflow-y: auto;
  backdrop-filter: blur(10px);
}

.form-title {
  font-size: 16px;
  color: #fff;
  margin: 0 0 16px;
}

.form-row {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-bottom: 10px;
}

.form-row label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
}

.form-row input {
  padding: 8px 10px;
  background: rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 6px;
  color: #fff;
  font-size: 13px;
  outline: none;
}

.form-row input:focus {
  border-color: var(--accent);
}

.form-row input[type="color"] {
  width: 60px;
  height: 36px;
  padding: 2px;
}

.form-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  margin-top: 16px;
}

.form-cancel {
  padding: 6px 16px;
  background: none;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 6px;
  color: #888;
  font-size: 13px;
  cursor: pointer;
}

.form-ok {
  padding: 6px 16px;
  background: var(--accent);
  border: none;
  border-radius: 6px;
  color: var(--bg);
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
}

/* 响应式 */
@media (max-width: 768px) {
  .projects-page {
    padding: 72px 16px 80px;
  }

  .project-matrix {
    grid-template-columns: 1fr;
  }

  .expanded-content {
    grid-template-columns: 1fr;
  }

  .cell-collapsed {
    padding: 20px;
  }
}
</style>
