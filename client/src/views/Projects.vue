<template>
  <div class="projects-page">
    <div class="page-bg" aria-hidden="true">
      <div class="bg-grid-pattern"></div>
    </div>

    <div class="projects-container">
      <!-- 页面头部 -->
      <header class="page-header">
        <div class="header-badge">PROJECTS</div>
        <h1 class="page-title">项目矩阵</h1>
        <p class="page-desc">产品 · 工具 · 实验 · 开源</p>
      </header>

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
              <div class="cell-icon" :style="{ '--icon-color': project.color || '#38f8ff' }">
                {{ project.icon || '◇' }}
              </div>
              <div class="cell-info">
                <h3 class="cell-title">{{ project.name }}</h3>
                <span class="cell-tag">{{ project.tag || project.category || 'PROJECT' }}</span>
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
                  <span class="section-label">概述</span>
                  <p class="section-text">{{ project.description }}</p>
                </div>

                <div class="expanded-section" v-if="project.status">
                  <span class="section-label">状态</span>
                  <span class="tech-tag" :class="{ 'status-active': project.status === '已上线' }">{{ project.status }}</span>
                </div>

                <div class="expanded-section" v-if="project.features?.length">
                  <span class="section-label">核心功能</span>
                  <ul class="feature-list">
                    <li v-for="feature in project.features" :key="feature">{{ feature }}</li>
                  </ul>
                </div>

                <div class="expanded-section" v-if="project.techStack">
                  <span class="section-label">技术栈</span>
                  <div class="tech-tags">
                    <span v-for="tech in project.techStack.split(', ')" :key="tech" class="tech-tag">{{ tech }}</span>
                  </div>
                </div>

                <div class="expanded-actions" v-if="project.url || project.githubUrl">
                  <a v-if="project.url && project.url !== '#'" :href="project.url" target="_blank" rel="noopener" class="action-btn primary">
                    <span>访问项目</span>
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M7 17l9.2-9.2M17 17V7H7"/>
                    </svg>
                  </a>
                  <a v-if="project.githubUrl" :href="project.githubUrl" target="_blank" rel="noopener" class="action-btn">
                    <span>源代码</span>
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor">
                      <path d="M12 2A10 10 0 0 0 2 12c0 4.42 2.87 8.17 6.84 9.5.5.08.66-.23.66-.5v-1.69c-2.77.6-3.36-1.34-3.36-1.34-.46-1.16-1.11-1.47-1.11-1.47-.91-.62.07-.6.07-.6 1 .07 1.53 1.03 1.53 1.03.87 1.52 2.34 1.07 2.91.83.09-.65.35-1.09.63-1.34-2.22-.25-4.55-1.11-4.55-4.92 0-1.11.38-2 1.03-2.71-.1-.25-.45-1.29.1-2.64 0 0 .84-.27 2.75 1.02.79-.22 1.65-.33 2.5-.33s1.71.11 2.5.33c1.91-1.29 2.75-1.02 2.75-1.02.55 1.35.2 2.39.1 2.64.65.71 1.03 1.6 1.03 2.71 0 3.82-2.34 4.66-4.57 4.91.36.31.69.92.69 1.85V21c0 .27.16.59.67.5C19.14 20.16 22 16.42 22 12A10 10 0 0 0 12 2z"/>
                    </svg>
                  </a>
                </div>
              </div>
            </div>
          </transition>
        </div>

        <!-- 空状态 -->
        <div v-if="!projects.length" class="empty-state">
          <div class="empty-icon">◇</div>
          <p>暂无项目</p>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import siteConfig from '../config/site.config'

const route = useRoute()
const expandedId = ref(null)

// 从配置获取项目列表
const projects = computed(() => {
  return siteConfig.projects || []
})

// 切换展开
const toggleExpand = (id) => {
  expandedId.value = expandedId.value === id ? null : id
}

// 从路由查询参数自动展开
if (route.query.id) {
  const id = isNaN(Number(route.query.id)) ? route.query.id : Number(route.query.id)
  expandedId.value = id
}
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
    linear-gradient(rgba(56, 248, 255, 0.02) 1px, transparent 1px),
    linear-gradient(90deg, rgba(56, 248, 255, 0.02) 1px, transparent 1px);
  background-size: 40px 40px;
  mask-image: radial-gradient(ellipse at center, black 0%, transparent 70%);
}

.projects-container {
  position: relative;
  z-index: 1;
  max-width: 1200px;
  margin: 0 auto;
}

/* 页面头部 */
.page-header {
  margin-bottom: 48px;
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

/* 项目矩阵 */
.project-matrix {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 20px;
}

.project-cell {
  position: relative;
  background: rgba(12, 20, 35, 0.6);
  border: 1px solid rgba(56, 248, 255, 0.1);
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
}

.project-cell:hover {
  border-color: rgba(56, 248, 255, 0.25);
}

.project-cell.expanded {
  grid-column: 1 / -1;
  border-color: rgba(56, 248, 255, 0.3);
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
  color: var(--icon-color, #38f8ff);
  background: rgba(56, 248, 255, 0.1);
  border: 1px solid rgba(56, 248, 255, 0.2);
  border-radius: 12px;
  flex-shrink: 0;
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
  border: 1px solid rgba(56, 248, 255, 0.2);
  border-radius: 8px;
  transition: all 0.2s ease;
}

.project-cell:hover .cell-expand-indicator {
  border-color: rgba(56, 248, 255, 0.4);
  background: rgba(56, 248, 255, 0.1);
}

.indicator-icon {
  font-size: 20px;
  font-weight: 300;
  color: #38f8ff;
  line-height: 1;
}

/* 展开态 */
.cell-expanded {
  border-top: 1px solid rgba(56, 248, 255, 0.1);
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
  color: rgba(56, 248, 255, 0.6);
  padding-bottom: 8px;
  border-bottom: 1px solid rgba(56, 248, 255, 0.1);
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
  color: #38f8ff;
}

.tech-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tech-tag {
  font-size: 12px;
  padding: 6px 12px;
  background: rgba(56, 248, 255, 0.1);
  border: 1px solid rgba(56, 248, 255, 0.2);
  border-radius: 6px;
  color: rgba(56, 248, 255, 0.8);
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
  border: 1px solid rgba(56, 248, 255, 0.2);
  background: rgba(56, 248, 255, 0.05);
  color: rgba(255, 255, 255, 0.8);
}

.action-btn:hover {
  background: rgba(56, 248, 255, 0.1);
  border-color: rgba(56, 248, 255, 0.4);
}

.action-btn.primary {
  background: linear-gradient(135deg, rgba(56, 248, 255, 0.2), rgba(155, 92, 255, 0.2));
  border-color: rgba(56, 248, 255, 0.4);
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