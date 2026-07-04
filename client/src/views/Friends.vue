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
        <div
          v-for="friend in friends"
          :key="friend.id"
          class="friend-node"
          :class="{ 'friend-pending': friend.isActive === 0 }"
        >
          <a
            :href="friend.url"
            target="_blank"
            rel="noopener noreferrer"
            class="node-link"
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
              <span class="status-dot" :class="{ 'status-pending': friend.isActive === 0 }"></span>
              <span class="status-text">{{ friend.isActive === 0 ? 'PENDING' : 'ONLINE' }}</span>
            </div>
          </a>
          <!-- 管理员按钮 -->
          <div class="node-admin-actions" v-if="userStore.isAdmin">
            <button @click="openEdit(friend)" class="admin-btn edit-btn">编辑</button>
            <button @click="handleDelete(friend.id)" class="admin-btn del-btn">删除</button>
          </div>
        </div>
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
        <button v-if="userStore.isAdmin" class="add-friend-btn" @click="openAdd">+ 添加友链</button>
      </div>

      <!-- 编辑/添加表单弹窗 -->
      <div v-if="showForm" class="form-overlay" @click.self="showForm = false">
        <div class="form-panel">
          <h3 class="form-title">{{ editingId ? '编辑友链' : '添加友链' }}</h3>
          <div class="form-row"><label>名称 *</label><input v-model="form.name" placeholder="网站名称" /></div>
          <div class="form-row"><label>链接 *</label><input v-model="form.url" placeholder="https://..." /></div>
          <div class="form-row"><label>头像</label><input v-model="form.avatar" placeholder="https://..." /></div>
          <div class="form-row"><label>简介</label><input v-model="form.description" placeholder="一句话介绍" /></div>
          <div class="form-row"><label>分类</label><input v-model="form.category" placeholder="如：技术博客" /></div>
          <div class="form-row"><label>邮箱</label><input v-model="form.email" placeholder="contact@..." /></div>
          <div class="form-actions">
            <button class="form-cancel" @click="showForm = false">取消</button>
            <button class="form-ok" @click="submitForm">{{ editingId ? '更新' : '添加' }}</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '../stores/user'
import { getFriendLinks, addFriendLink, updateFriendLink, deleteFriendLink } from '../api/friend'

const userStore = useUserStore()
const friends = ref([])
const loading = ref(true)
const showForm = ref(false)
const editingId = ref(null)
const form = ref({ name: '', url: '', avatar: '', description: '', category: '', email: '' })

const loadFriends = async () => {
  loading.value = true
  try {
    const url = userStore.isAdmin ? '/friend/link/all' : '/friend/link/list'
    const res = await getFriendLinks(url)
    if (res.code === 200 || res.code === 0) {
      friends.value = res.data || []
    } else {
      friends.value = []
    }
  } catch (e) {
    friends.value = []
  }
  loading.value = false
}

const onAvatarError = (e) => {
  e.target.style.display = 'none'
}

const openAdd = () => {
  editingId.value = null
  form.value = { name: '', url: '', avatar: '', description: '', category: '', email: '' }
  showForm.value = true
}

const openEdit = (f) => {
  editingId.value = f.id
  form.value = { name: f.name || '', url: f.url || '', avatar: f.avatar || '', description: f.description || '', category: f.category || '', email: f.email || '' }
  showForm.value = true
}

const submitForm = async () => {
  if (!form.value.name || !form.value.url) { alert('名称和链接必填'); return }
  try {
    if (editingId.value) {
      await updateFriendLink(editingId.value, form.value)
    } else {
      await addFriendLink(form.value)
    }
    showForm.value = false
    loadFriends()
  } catch (e) {
    alert('操作失败：' + (e?.message || '网络错误'))
  }
}

const handleDelete = async (id) => {
  if (!confirm('确定删除这条友链吗？')) return
  try {
    await deleteFriendLink(id)
    loadFriends()
  } catch (e) {
    alert('删除失败：' + (e?.message || '网络错误'))
  }
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
  background: rgba(12, 20, 35, 0.6);
  border: 1px solid rgba(56, 248, 255, 0.1);
  border-radius: 12px;
  transition: all 0.3s ease;
  overflow: visible;
}

.friend-node:hover {
  border-color: rgba(56, 248, 255, 0.3);
  transform: translateY(-4px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.3);
}

.friend-pending {
  opacity: 0.6;
}

.node-link {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  text-decoration: none;
  color: inherit;
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

/* 管理员按钮 */
.node-admin-actions {
  display: flex;
  gap: 6px;
  padding: 0 20px 12px;
}

.admin-btn {
  padding: 3px 10px;
  font-size: 11px;
  border-radius: 4px;
  border: 1px solid rgba(255, 255, 255, 0.15);
  background: none;
  color: #888;
  cursor: pointer;
  transition: all 0.2s;
}

.edit-btn:hover {
  border-color: var(--accent);
  color: var(--accent);
}

.del-btn:hover {
  border-color: #ff6464;
  color: #ff6464;
}

.add-friend-btn {
  display: block;
  margin: 24px auto 0;
  padding: 10px 24px;
  background: linear-gradient(135deg, rgba(56, 248, 255, 0.2), rgba(155, 92, 255, 0.2));
  border: 1px solid rgba(56, 248, 255, 0.4);
  border-radius: 8px;
  color: #fff;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.add-friend-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(56, 248, 255, 0.2);
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
  border: 1px solid rgba(56, 248, 255, 0.2);
  border-radius: 16px;
  padding: 24px;
  width: 90%;
  max-width: 420px;
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
  margin-bottom: 12px;
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