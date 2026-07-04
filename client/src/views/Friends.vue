<template>
  <div class="friends-page">
    <div class="page-bg" aria-hidden="true">
      <div class="bg-nodes"></div>
    </div>

    <div class="friends-container">
      <!-- 页面头部 -->
      <header class="page-header">
        <div class="header-badge">CONNECTIONS</div>
        <h1 class="page-title">友情链接</h1>
        <p class="page-desc">连接彼此，共同成长</p>
      </header>

      <!-- 顶部申请区域 -->
      <div class="apply-panel">
        <div class="apply-info">
          <div class="apply-icon"><svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#38f8ff" stroke-width="2"><path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71"/><path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71"/></svg></div>
          <div>
            <h3 class="apply-title">申请友链</h3>
            <p class="apply-desc">在留言板留言申请友链，提供你的博客名称、链接和简介</p>
          </div>
        </div>
        <router-link to="/message" class="apply-btn">前往留言板</router-link>
      </div>

      <!-- 友链网格 -->
      <main class="friends-grid" v-if="!loading && friends.length">
        <a
          v-for="friend in friends"
          :key="friend.id"
          :href="friend.url"
          target="_blank"
          rel="noopener noreferrer"
          class="friend-node"
        >
          <div class="node-connector" aria-hidden="true"></div>
          <div class="node-avatar">
            <img
              v-if="friend.avatar"
              :src="friend.avatar"
              :alt="friend.name"
              @error="onAvatarError"
            />
            <span v-else class="avatar-placeholder">{{ friend.name?.charAt(0) || '?' }}</span>
          </div>
          <div class="node-info">
            <h3 class="node-name">{{ friend.name }}</h3>
            <p class="node-desc">{{ friend.description }}</p>
          </div>
          <div class="node-status">
            <span class="status-dot"></span>
            <span class="status-text">ONLINE</span>
          </div>
        </a>
      </main>

      <!-- 加载中 -->
      <div v-if="loading" class="loading-state">
        <div class="loading-spinner"></div>
        <span>扫描节点中...</span>
      </div>

      <!-- 空状态 -->
      <div v-if="!loading && !friends.length" class="empty-state">
        <div class="empty-icon">◉</div>
        <p>暂无友链</p>
        <p class="empty-hint">成为第一个连接的节点</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import siteConfig from '../config/site.config'

const friends = ref([])
const loading = ref(true)

const loadFriends = () => {
  // 从配置加载
  if (siteConfig.friends && siteConfig.friends.length) {
    friends.value = siteConfig.friends
  }
  loading.value = false
}

const onAvatarError = (e) => {
  e.target.style.display = 'none'
}

onMounted(loadFriends)
</script>

<style scoped>
.friends-page {
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

.bg-nodes {
  position: absolute;
  inset: 0;
  background-image: 
    radial-gradient(circle at 20% 30%, rgba(56, 248, 255, 0.06) 0%, transparent 40%),
    radial-gradient(circle at 80% 70%, rgba(155, 92, 255, 0.06) 0%, transparent 40%);
}

.friends-container {
  position: relative;
  z-index: 1;
  max-width: 1200px;
  margin: 0 auto;
}

/* 页面头部 */
.page-header {
  margin-bottom: 40px;
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

/* 申请面板 */
.apply-panel {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  background: linear-gradient(135deg, rgba(12, 20, 35, 0.8), rgba(20, 30, 50, 0.6));
  border: 1px solid rgba(56, 248, 255, 0.2);
  border-radius: 16px;
  margin-bottom: 40px;
  backdrop-filter: blur(10px);
}

.apply-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.apply-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  background: rgba(56, 248, 255, 0.1);
  border: 1px solid rgba(56, 248, 255, 0.2);
  border-radius: 12px;
}

.apply-title {
  font-size: 16px;
  font-weight: 600;
  color: #fff;
  margin: 0 0 4px 0;
}

.apply-desc {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
  margin: 0;
}

.apply-btn {
  padding: 12px 24px;
  background: linear-gradient(135deg, rgba(56, 248, 255, 0.2), rgba(155, 92, 255, 0.2));
  border: 1px solid rgba(56, 248, 255, 0.4);
  border-radius: 8px;
  color: #fff;
  font-size: 13px;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.apply-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(56, 248, 255, 0.2);
}

/* 友链网格 */
.friends-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.friend-node {
  position: relative;
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: rgba(12, 20, 35, 0.6);
  border: 1px solid rgba(56, 248, 255, 0.1);
  border-radius: 12px;
  text-decoration: none;
  color: inherit;
  transition: all 0.3s ease;
  overflow: hidden;
}

.friend-node:hover {
  border-color: rgba(56, 248, 255, 0.3);
  transform: translateY(-4px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.3);
}

/* 连接线装饰 */
.node-connector {
  position: absolute;
  top: 0;
  left: 32px;
  width: 2px;
  height: 8px;
  background: rgba(56, 248, 255, 0.3);
}

.friend-node:first-child .node-connector {
  display: none;
}

/* 头像 */
.node-avatar {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  overflow: hidden;
  flex-shrink: 0;
  background: rgba(56, 248, 255, 0.1);
  border: 1px solid rgba(56, 248, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
}

.node-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  font-size: 20px;
  font-weight: 700;
  color: #38f8ff;
}

/* 节点信息 */
.node-info {
  flex: 1;
  min-width: 0;
}

.node-name {
  font-size: 15px;
  font-weight: 600;
  color: #fff;
  margin: 0 0 4px 0;
}

.node-desc {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 状态指示 */
.node-status {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 10px;
  font-weight: 600;
  color: #4ade80;
  letter-spacing: 0.1em;
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #4ade80;
  box-shadow: 0 0 8px #4ade80;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
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

.empty-hint {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.3);
  margin-top: 8px;
}

/* 响应式 */
@media (max-width: 768px) {
  .friends-page {
    padding: 72px 16px 80px;
  }
  
  .apply-panel {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }
  
  .apply-info {
    flex-direction: column;
    text-align: center;
  }
  
  .friends-grid {
    grid-template-columns: 1fr;
  }
}
</style>