<template>
  <div class="friends-page">
    <div class="page-bg" aria-hidden="true">
      <div class="bg-nodes"></div>
    </div>

    <div class="friends-container">
      <!-- 页面头部 -->
      <header class="page-header">
        <div class="header-badge">{{ ui.badge }}</div>
        <h1 class="page-title">{{ ui.title }}</h1>
        <p class="page-desc">{{ ui.desc }}</p>
      </header>

      <!-- 申请入口 -->
      <section v-if="!userStore.isAdmin" class="apply-panel apply-compact">
        <div class="apply-info">
          <div class="apply-icon" aria-hidden="true">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#60a5fa" stroke-width="2"><path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71"/><path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71"/></svg>
          </div>
          <div>
            <h3 class="apply-title">{{ ui.applyTitle }}</h3>
            <p class="apply-desc">{{ ui.applyDesc }}</p>
          </div>
        </div>
        <button class="apply-cta" type="button" @click="openApplyForm">{{ ui.applyAction }}</button>
      </section>
      <section v-else class="apply-panel apply-compact admin-compact">
        <div class="apply-info">
          <div class="apply-icon" aria-hidden="true">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#60a5fa" stroke-width="2"><path d="M12 5v14"/><path d="M5 12h14"/><path d="M4 7.5A3.5 3.5 0 0 1 7.5 4h9A3.5 3.5 0 0 1 20 7.5v9a3.5 3.5 0 0 1-3.5 3.5h-9A3.5 3.5 0 0 1 4 16.5z"/></svg>
          </div>
          <div>
            <h3 class="apply-title">{{ ui.adminTitle }}</h3>
            <p class="apply-desc">{{ ui.adminDesc }}</p>
          </div>
        </div>
        <button class="apply-cta admin-cta" type="button" @click="openAdd">+ {{ ui.addFriend }}</button>
      </section>

      <transition name="apply-modal">
        <div v-if="!userStore.isAdmin && showApplyForm" class="apply-modal-overlay" @click.self="closeApplyForm">
          <form class="apply-modal-panel friend-apply-form" @submit.prevent="submitApplication">
            <button class="apply-modal-close" type="button" @click="closeApplyForm" :aria-label="ui.close">×</button>
            <div class="apply-modal-head">
              <span>{{ ui.applyBadge }}</span>
              <h3>{{ ui.applyTitle }}</h3>
              <p>{{ ui.applyModalDesc }}</p>
            </div>
        <div class="apply-form-grid">
          <label>
                <span>{{ ui.siteName }}</span>
                <input v-model.trim="applyForm.name" :placeholder="ui.siteNamePlaceholder" maxlength="40" />
          </label>
          <label>
                <span>{{ ui.siteUrl }}</span>
            <input v-model.trim="applyForm.url" placeholder="https://example.com" />
          </label>
          <label>
                <span>{{ ui.avatar }}</span>
            <input v-model.trim="applyForm.avatar" placeholder="https://example.com/avatar.png" />
          </label>
          <label>
                <span>{{ ui.email }}</span>
            <input v-model.trim="applyForm.email" placeholder="contact@example.com" maxlength="80" />
          </label>
          <label>
                <span>{{ ui.category }}</span>
                <input v-model.trim="applyForm.category" :placeholder="ui.categoryPlaceholder" maxlength="40" />
          </label>
          <label class="apply-wide">
                <span>{{ ui.intro }}</span>
                <input v-model.trim="applyForm.description" :placeholder="ui.introPlaceholder" maxlength="100" />
          </label>
        </div>

        <div class="apply-actions">
          <button class="apply-btn" type="submit" :disabled="applying">
                {{ applying ? ui.submitting : ui.submit }}
          </button>
          <p v-if="applyMsg" class="apply-msg" :class="{ ok: applyOk }">{{ applyMsg }}</p>
        </div>
          </form>
        </div>
      </transition>

      <!-- 管理员添加按钮(有友链时) -->
      <!-- 友链网格 -->
      <main class="friends-grid" v-if="!loading && friends.length">
        <div
          v-for="friend in friends"
          :key="friend.id || friend.url"
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
          <div class="node-admin-actions" v-if="userStore.isAdmin && !friend.isFallback">
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
        <p>{{ ui.empty }}</p>
        <p class="empty-hint">{{ ui.emptyHint }}</p>
        <button v-if="userStore.isAdmin" class="add-friend-btn" @click="openAdd">+ {{ ui.addFriend }}</button>
      </div>

      <!-- 编辑/添加表单弹窗 -->
      <div v-if="showForm" class="form-overlay" @click.self="showForm = false">
        <div class="form-panel">
          <h3 class="form-title">{{ editingId ? '编辑友链' : '添加友链' }}</h3>
          <div class="form-row"><label>名称 *</label><input v-model="form.name" placeholder="网站名称" /></div>
          <div class="form-row"><label>链接 *</label><input v-model="form.url" placeholder="https://..." /></div>
          <div class="form-row">
            <label>头像</label>
            <div class="avatar-upload-row">
              <input v-model="form.avatar" placeholder="输入链接或上传图片" />
              <input ref="avatarFileInput" type="file" accept="image/*" hidden @change="handleAvatarUpload" />
              <button type="button" class="avatar-upload-btn" @click="() => avatarFileInput?.click()">上传</button>
            </div>
            <div class="avatar-preview" v-if="form.avatar">
              <img :src="form.avatar" @error="form.avatar = ''" />
            </div>
          </div>
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
    <!-- 裁剪对话框 -->
    <CropDialog
      v-if="showCrop"
      :imageFile="cropFile"
      :aspectRatio="1"
      @crop="onAvatarCropDone"
      @cancel="showCrop = false"
    />
  </div>
</template>

<script setup>
import { computed, inject, ref, onMounted } from 'vue'
import { useUserStore } from '../stores/user'
import { getFriendLinks, addFriendLink, applyFriendLink, updateFriendLink, deleteFriendLink } from '../api/friend'
import { uploadImage } from '../api/upload'
import CropDialog from '../components/CropDialog.vue'
import { fallbackFriends } from '../config/site.config.js'

const userStore = useUserStore()
const friends = ref([])
const loading = ref(true)
const showForm = ref(false)
const showApplyForm = ref(false)
const editingId = ref(null)
const form = ref({ name: '', url: '', avatar: '', description: '', category: '', email: '' })
const applyForm = ref({ name: '', url: '', avatar: '', description: '', category: '', email: '' })
const applying = ref(false)
const applyMsg = ref('')
const applyOk = ref(false)
const avatarFileInput = ref(null)
const siteLanguage = inject('siteLanguage', ref(localStorage.getItem('ethan-language') || 'zh'))

const ui = computed(() => siteLanguage.value === 'en'
  ? {
      badge: 'CONNECTIONS',
      title: 'Friend Links',
      desc: 'Connected nodes, shared growth.',
      applyTitle: 'Apply For A Link',
      applyDesc: 'The page keeps the link network clean. Open the application cabin only when you need to submit a site.',
      applyAction: 'Apply',
      adminTitle: 'Friend Link Console',
      adminDesc: 'Admin mode is active. Add, edit, or remove real friend-link nodes here.',
      applyBadge: 'LINK REQUEST',
      applyModalDesc: 'Submit your site information here. When the backend is available, it will enter the review queue.',
      siteName: 'Site Name *',
      siteNamePlaceholder: 'Example: Ethan Nexus',
      siteUrl: 'Site URL *',
      avatar: 'Avatar URL',
      email: 'Email',
      category: 'Category / ICP note',
      categoryPlaceholder: 'Tech blog / ICP ready / Personal site',
      intro: 'One-line Intro',
      introPlaceholder: 'Describe your site style and content direction',
      submit: 'Submit Request',
      submitting: 'Submitting...',
      required: 'Please fill in at least the site name and URL.',
      success: 'Request submitted. Waiting for review.',
      offlineSaved: 'Backend is not connected. The request has been cached locally and shown as a pending node.',
      failed: 'Submit failed. Please try again later.',
      close: 'Close application form',
      addFriend: 'Add Link',
      empty: 'No friend links yet',
      emptyHint: 'Become the first connected node',
    }
  : {
      badge: 'CONNECTIONS',
      title: '友情链接',
      desc: '连接彼此，共同成长',
      applyTitle: '申请友链',
      applyDesc: '友链页先展示关系网络，需要提交站点时再打开申请舱，页面不会一进来就铺满表单。',
      applyAction: '申请友链',
      adminTitle: '友链管理舱',
      adminDesc: '当前为管理员模式，可以在这里新增、编辑或移除真实友链节点。',
      applyBadge: 'LINK REQUEST',
      applyModalDesc: '把站点资料投递到这里，后台开启后会以待审核节点进入管理列表。',
      siteName: '站点名称 *',
      siteNamePlaceholder: '例如：Ethan Nexus',
      siteUrl: '站点链接 *',
      avatar: '头像 URL',
      email: '邮箱',
      category: '分类 / 备案备注',
      categoryPlaceholder: '技术博客 / 已备案 / 个人站',
      intro: '一句话简介',
      introPlaceholder: '介绍一下你的站点风格和内容方向',
      submit: '提交申请',
      submitting: '提交中...',
      required: '请至少填写站点名称和链接。',
      success: '申请已提交，等待后台审核。',
      offlineSaved: '后端暂未连接，申请已先暂存在本机并显示为待审核节点。',
      failed: '提交失败，请稍后重试。',
      close: '关闭申请表单',
      addFriend: '添加友链',
      empty: '暂无友链',
      emptyHint: '成为第一个连接的节点',
    })

const useFallbackFriends = () => {
  friends.value = fallbackFriends.map(friend => ({
    ...friend,
    avatar: resolveUploadUrl(friend.avatar),
    isFallback: true,
  }))
}

const backendTestNames = new Set(['嘀咕嘀咕', 'vDVD', 'VS VS v', '放松放松'])
const backendTestUrls = new Set(['的DVD', 'VS VS', '三十分'])

const isValidFriend = (friend) => {
  const name = String(friend?.name || '').trim()
  const url = String(friend?.url || '').trim()
  if (!name || !/^https?:\/\//i.test(url)) return false
  if (backendTestNames.has(name) || backendTestUrls.has(url)) return false
  return true
}

const normalizeFriends = (list = []) => list
  .map(friend => ({
    ...friend,
    name: String(friend.name || '').trim(),
    url: String(friend.url || '').trim(),
    avatar: resolveUploadUrl(friend.avatar),
  }))
  .filter(isValidFriend)

const loadFriends = async () => {
  loading.value = true
  try {
    const url = userStore.isAdmin ? '/friend/link/all' : '/friend/link/list'
    const res = await getFriendLinks(url)
    if ((res.code === 200 || res.code === 0) && res.data?.length) {
      const normalizedFriends = normalizeFriends(res.data)
      if (normalizedFriends.length) {
        friends.value = normalizedFriends
      } else {
        useFallbackFriends()
      }
    } else {
      useFallbackFriends()
    }
  } catch (e) {
    useFallbackFriends()
  }
  loading.value = false
}

const appendPendingFriend = (payload) => {
  const pending = {
    ...payload,
    id: `pending-${Date.now()}`,
    avatar: resolveUploadUrl(payload.avatar),
    isActive: 0,
  }
  friends.value = [pending, ...friends.value.filter(item => item.url !== payload.url)]
}

const cachePendingApplication = (payload) => {
  const key = 'ethan-friend-applications'
  let oldList = []
  try {
    oldList = JSON.parse(localStorage.getItem(key) || '[]')
  } catch {
    oldList = []
  }
  localStorage.setItem(key, JSON.stringify([{ ...payload, createTime: new Date().toISOString() }, ...oldList].slice(0, 20)))
}

const onAvatarError = (e) => {
  e.target.style.display = 'none'
}

const apiBaseUrl = (import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080').replace(/\/api\/?$/, '')

const resolveUploadUrl = (url) => {
  if (!url) return ''
  if (/^https?:\/\//.test(url)) return url
  if (url.startsWith('/upload/') || url.startsWith('/uploads/')) {
    return `${apiBaseUrl}${url}`
  }
  return url
}

const handleAvatarUpload = async (e) => {
  const file = e.target.files[0]
  if (!file) return
  cropFile.value = file
  showCrop.value = true
  e.target.value = ''
}

const showCrop = ref(false)
const cropFile = ref(null)

const onAvatarCropDone = async (croppedFile) => {
  showCrop.value = false
  try {
    const res = await uploadImage(croppedFile)
    if (res.code === 200 && res.data) {
      form.value.avatar = resolveUploadUrl(res.data)
    }
  } catch (err) {
    console.error('头像上传失败:', err)
  }
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

const openApplyForm = () => {
  applyMsg.value = ''
  applyOk.value = false
  showApplyForm.value = true
}

const closeApplyForm = () => {
  if (applying.value) return
  showApplyForm.value = false
}

const submitApplication = async () => {
  if (!applyForm.value.name || !applyForm.value.url) {
    applyMsg.value = ui.value.required
    applyOk.value = false
    return
  }

  applying.value = true
  applyMsg.value = ''
  applyOk.value = false
  const payload = { ...applyForm.value }

  try {
    const res = await applyFriendLink(payload)
    if (res.code === 200 || res.code === 0) {
      appendPendingFriend(payload)
      applyMsg.value = res.message || res.data || ui.value.success
      applyOk.value = true
      applyForm.value = { name: '', url: '', avatar: '', description: '', category: '', email: '' }
      return
    }
    applyMsg.value = res.message || ui.value.failed
  } catch (e) {
    cachePendingApplication(payload)
    appendPendingFriend(payload)
    applyMsg.value = ui.value.offlineSaved
    applyOk.value = true
    applyForm.value = { name: '', url: '', avatar: '', description: '', category: '', email: '' }
  } finally {
    applying.value = false
  }
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
    radial-gradient(circle at 20% 30%, rgba(96, 165, 250, 0.06) 0%, transparent 40%),
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
  color: #60a5fa;
  padding: 6px 12px;
  border: 1px solid rgba(96, 165, 250, 0.3);
  border-radius: 4px;
  margin-bottom: 16px;
}

.page-title {
  font-size: clamp(2rem, 4vw, 3rem);
  font-weight: 700;
  margin: 0 0 8px 0;
  background: linear-gradient(135deg, #fff 0%, #60a5fa 100%);
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
  align-items: center;
  justify-content: space-between;
  gap: 22px;
  padding: 18px 0 22px !important;
  margin-bottom: 34px;
  border: 0 !important;
  border-bottom: 1px solid rgba(96, 165, 250, 0.16) !important;
  border-radius: 0 !important;
  background:
    linear-gradient(90deg, rgba(96, 165, 250, 0.08), transparent 58%),
    transparent !important;
  box-shadow: none !important;
  backdrop-filter: none !important;
}

.apply-compact::before,
.apply-compact::after {
  display: none !important;
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
  background: rgba(96, 165, 250, 0.1);
  border: 1px solid rgba(96, 165, 250, 0.2);
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
  line-height: 1.7;
}

.apply-cta {
  flex-shrink: 0;
  min-height: 42px;
  padding: 0 18px;
  border: 1px solid rgba(96, 165, 250, 0.42);
  border-radius: 8px;
  background: rgba(96, 165, 250, 0.08);
  color: #f8fbff;
  font-size: 13px;
  font-weight: 800;
  cursor: pointer;
  transition: transform 0.22s var(--ease-out), border-color 0.22s, box-shadow 0.22s;
}

.apply-cta:hover {
  transform: translateY(-2px);
  border-color: rgba(96, 165, 250, 0.68);
  box-shadow: 0 14px 28px rgba(96, 165, 250, 0.13);
}

.apply-modal-overlay {
  position: fixed;
  inset: 0;
  z-index: 1000;
  display: grid;
  place-items: center;
  padding: 20px;
  background: rgba(2, 6, 18, 0.72);
  backdrop-filter: blur(14px);
}

.apply-modal-panel {
  position: relative;
  width: min(760px, 100%);
  max-height: min(86vh, 780px);
  overflow-y: auto;
  padding: clamp(22px, 4vw, 32px);
  border: 1px solid rgba(147, 197, 253, 0.24);
  border-radius: 8px;
  background:
    linear-gradient(135deg, rgba(16, 29, 54, 0.95), rgba(5, 9, 21, 0.94)),
    repeating-linear-gradient(90deg, rgba(147, 197, 253, 0.045) 0 1px, transparent 1px 76px);
  box-shadow: 0 30px 90px rgba(0, 0, 0, 0.48), inset 0 0 80px rgba(96, 165, 250, 0.045);
}

.apply-modal-close {
  position: absolute;
  top: 12px;
  right: 16px;
  border: 0;
  background: transparent;
  color: rgba(226, 239, 255, 0.56);
  font-size: 26px;
  line-height: 1;
  cursor: pointer;
}

.apply-modal-head {
  max-width: 560px;
  margin-bottom: 22px;
}

.apply-modal-head span {
  color: #93c5fd;
  font: 800 11px/1 'SF Mono', 'Consolas', monospace;
  letter-spacing: 0.16em;
}

.apply-modal-head h3 {
  margin: 12px 0 8px;
  color: #f8fbff;
  font-size: clamp(1.55rem, 4vw, 2.6rem);
  line-height: 1.05;
}

.apply-modal-head p {
  margin: 0;
  color: rgba(226, 239, 255, 0.62);
  line-height: 1.8;
}

.apply-modal-enter-active,
.apply-modal-leave-active {
  transition: opacity 0.25s var(--ease-out), filter 0.25s var(--ease-out);
}

.apply-modal-enter-from,
.apply-modal-leave-to {
  opacity: 0;
  filter: blur(8px);
}

.apply-form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.apply-form-grid label {
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 0;
}

.apply-form-grid span {
  color: rgba(226, 239, 255, 0.52);
  font-size: 12px;
  font-weight: 700;
}

.apply-form-grid input {
  min-height: 42px;
  padding: 0 12px;
  border: 1px solid rgba(147, 197, 253, 0.16);
  border-radius: 8px;
  background: rgba(5, 8, 22, 0.42);
  color: #eef7ff;
  outline: none;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.apply-form-grid input:focus {
  border-color: rgba(96, 165, 250, 0.48);
  box-shadow: 0 0 0 4px rgba(96, 165, 250, 0.08);
}

.apply-wide {
  grid-column: 1 / -1;
}

.apply-actions {
  grid-column: 2;
  display: flex;
  align-items: center;
  gap: 12px;
  justify-content: flex-end;
  flex-wrap: wrap;
}

.apply-btn {
  border: 1px solid rgba(96, 165, 250, 0.4);
  padding: 12px 24px;
  background: linear-gradient(135deg, rgba(96, 165, 250, 0.2), rgba(155, 92, 255, 0.2));
  border-radius: 8px;
  color: #fff;
  font-size: 13px;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.2s ease;
  white-space: nowrap;
  cursor: pointer;
}

.apply-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(96, 165, 250, 0.2);
}

.apply-btn:disabled {
  opacity: 0.58;
  cursor: not-allowed;
  transform: none;
}

.apply-msg {
  flex: 1;
  min-width: min(100%, 240px);
  margin: 0;
  color: #ff9f9f;
  font-size: 12px;
  line-height: 1.6;
}

.apply-msg.ok {
  color: #93c5fd;
}

/* 友链网格 */
.friends-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 10px;
}

.friend-node {
  position: relative;
  background: rgba(12, 20, 35, 0.6);
  border: 1px solid rgba(96, 165, 250, 0.08);
  border-radius: 10px;
  transition: all 0.3s ease;
  overflow: hidden;
  text-align: center;
}

.friend-node:hover {
  border-color: rgba(96, 165, 250, 0.25);
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.3);
}

.friend-pending {
  opacity: 0.6;
}

.node-link {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 14px 10px;
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
  background: rgba(96, 165, 250, 0.3);
}

.friend-node:first-child .node-connector {
  display: none;
}

/* 头像 */
.node-avatar {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
  background: rgba(96, 165, 250, 0.1);
  border: 1px solid rgba(96, 165, 250, 0.2);
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
  font-size: 16px;
  font-weight: 700;
  color: #60a5fa;
}

/* 节点信息 */
.node-info {
  flex: 1;
  min-width: 0;
}

.node-name {
  font-size: 13px;
  font-weight: 600;
  color: #fff;
  margin: 0;
  line-height: 1.3;
}

.node-desc {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.4);
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 100%;
}

/* 状态指示 */
.node-status {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 10px;
  font-weight: 600;
  color: #60a5fa;
  letter-spacing: 0.1em;
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #60a5fa;
  box-shadow: 0 0 8px #60a5fa;
  animation: pulse 2s ease-in-out infinite;
}

.status-dot.status-pending {
  background: #ffbd66;
  box-shadow: 0 0 8px rgba(255, 189, 102, 0.76);
}

.friend-pending .node-status {
  color: #ffbd66;
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
  border: 2px solid rgba(96, 165, 250, 0.2);
  border-top-color: #60a5fa;
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
  gap: 4px;
  justify-content: center;
  padding: 0 10px 10px;
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
  background: linear-gradient(135deg, rgba(96, 165, 250, 0.2), rgba(155, 92, 255, 0.2));
  border: 1px solid rgba(96, 165, 250, 0.4);
  border-radius: 8px;
  color: #fff;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.add-friend-btn:hover {
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

.avatar-upload-row {
  display: flex;
  gap: 8px;
}

.avatar-upload-row input {
  flex: 1;
}

.avatar-upload-btn {
  padding: 8px 14px;
  background: rgba(96, 165, 250, 0.15);
  border: 1px solid rgba(96, 165, 250, 0.3);
  border-radius: 6px;
  color: var(--accent);
  font-size: 13px;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.2s;
}

.avatar-upload-btn:hover {
  background: rgba(96, 165, 250, 0.25);
}

.avatar-preview {
  margin-top: 8px;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid rgba(96, 165, 250, 0.3);
}

.avatar-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
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
    align-items: stretch;
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
  
  .apply-info {
    flex-direction: column;
    text-align: center;
  }

  .apply-form-grid {
    grid-template-columns: 1fr;
  }

  .apply-actions {
    justify-content: center;
  }
  
  .friends-grid {
    grid-template-columns: 1fr;
  }
}
</style>
