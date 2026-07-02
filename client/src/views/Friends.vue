<template>
  <div class="friends-page">
    <div class="friends-inner">
      <header class="friends-header">
        <h1 class="friends-title">朋友们</h1>
        <p class="friends-desc">有趣的灵魂终会相遇</p>
      </header>

      <!-- 友链分类展示 -->
      <div class="friends-categories">
        <template v-for="cat in categories" :key="cat.key">
          <div
            v-if="getFriendsByCategory(cat.key).length > 0"
            class="friend-category"
          >
            <h3 class="category-title">
              <span class="cat-icon">{{ cat.icon }}</span>
              {{ cat.name }}
            </h3>
            <div class="friends-grid">
              <a
                v-for="friend in getFriendsByCategory(cat.key)"
                :key="friend.id"
                :href="friend.url"
                target="_blank"
                rel="noopener noreferrer"
                class="friend-simple-card"
              >
                <div class="simple-avatar">
                  <img
                    v-if="friend.avatar"
                    :src="friend.avatar"
                    :alt="friend.name"
                    @error="$event.target.style.display='none'"
                  />
                  <span v-else class="simple-avatar-fallback">{{ friend.name.slice(0, 1) }}</span>
                </div>
                <p class="simple-name">{{ friend.name }}</p>
                <p class="simple-desc">{{ friend.description || '暂无描述' }}</p>
              </a>
            </div>
          </div>
        </template>
      </div>

      <!-- 申请友链 - 明信片风格 -->
      <div class="apply-section">
        <div class="apply-postcard" @click="showApply = !showApply">
          <div class="postcard-stamp">✉</div>
          <div class="postcard-content">
            <p class="postcard-title">交换友链？</p>
            <p class="postcard-hint">点击展开申请表单</p>
          </div>
          <div class="postcard-arrow" :class="{ open: showApply }">▼</div>
        </div>

        <div v-if="showApply" class="apply-panel">
          <div class="apply-row">
            <input v-model="applyForm.name" placeholder="网站名称 *" class="apply-input" />
            <input v-model="applyForm.url" placeholder="网站地址 *" class="apply-input" />
          </div>
          <input v-model="applyForm.description" placeholder="网站简介（选填）" class="apply-input full" />
          <input v-model="applyForm.email" placeholder="联系邮箱（选填）" class="apply-input full" type="email" />
          <div class="avatar-input-row">
            <input v-model="applyForm.avatar" placeholder="头像URL（选填）" class="apply-input" />
            <label class="upload-avatar-btn" :class="{ uploading: uploadingAvatar }">
              {{ uploadingAvatar ? '上传中…' : '上传' }}
              <input type="file" accept="image/*" @change="onAvatarUpload($event, 'apply')" :disabled="uploadingAvatar" />
            </label>
          </div>
          <div class="category-select">
            <span class="cat-label">分类：</span>
            <label class="cat-option">
              <input type="radio" v-model="applyForm.category" value="tech" /> 技术
            </label>
            <label class="cat-option">
              <input type="radio" v-model="applyForm.category" value="life" /> 生活
            </label>
            <label class="cat-option">
              <input type="radio" v-model="applyForm.category" value="other" /> 其他
            </label>
          </div>
          <button class="apply-btn" @click="doApply">提交申请</button>
          <p class="apply-note">提交后我会尽快审核并添加 ~</p>
        </div>
      </div>

      <!-- 管理员操作区 -->
      <div v-if="isAdmin" class="admin-section">
        <div class="admin-toggle" @click="showAdmin = !showAdmin">
          <span>友链管理{{ showAdmin ? ' ▲' : ' ▼' }}</span>
        </div>
        <div v-if="showAdmin" class="admin-panel">
          <div v-for="link in allLinks" :key="link.id" class="admin-link-row">
            <div class="link-info">
              <span class="link-name">{{ link.name }}</span>
              <span class="link-cat" v-if="link.category">[{{ getCategoryName(link.category) }}]</span>
              <span class="link-status" :class="link.isActive === 1 ? 'active' : 'inactive'">
                {{ link.isActive === 1 ? '已上架' : '待审核' }}
              </span>
            </div>
            <div class="link-actions">
              <button v-if="link.isActive !== 1" class="admin-btn approve" @click="doApprove(link.id, 1)">通过</button>
              <button v-if="link.isActive === 1" class="admin-btn down" @click="doApprove(link.id, 0)">下架</button>
              <button class="admin-btn edit" @click="openEditModal(link)">编辑</button>
              <button class="admin-btn del" @click="doDelete(link.id)">删除</button>
            </div>
          </div>
          <p v-if="!allLinks.length" class="empty">暂无友链数据</p>
        </div>
      </div>

      <!-- 编辑弹窗 -->
      <div v-if="showEditModal" class="modal-overlay" @click.self="closeEditModal">
        <div class="modal-content">
          <h3 class="modal-title">编辑友链</h3>
          <div class="modal-field">
            <label>名称</label>
            <input v-model="editForm.name" class="apply-input" />
          </div>
          <div class="modal-field">
            <label>地址</label>
            <input v-model="editForm.url" class="apply-input" />
          </div>
          <div class="modal-field">
            <label>简介</label>
            <textarea v-model="editForm.description" class="apply-input" rows="3"></textarea>
          </div>
          <div class="modal-field">
            <label>头像</label>
            <div class="avatar-input-row">
              <input v-model="editForm.avatar" class="apply-input" />
              <label class="upload-avatar-btn small" :class="{ uploading: uploadingAvatar }">
                {{ uploadingAvatar ? '…' : '上传' }}
                <input type="file" accept="image/*" @change="onAvatarUpload($event, 'edit')" :disabled="uploadingAvatar" />
              </label>
            </div>
          </div>
          <div class="modal-field">
            <label>分类</label>
            <div class="category-select">
              <label class="cat-option">
                <input type="radio" v-model="editForm.category" value="tech" /> 技术
              </label>
              <label class="cat-option">
                <input type="radio" v-model="editForm.category" value="life" /> 生活
              </label>
              <label class="cat-option">
                <input type="radio" v-model="editForm.category" value="other" /> 其他
              </label>
            </div>
          </div>
          <div class="modal-field">
            <label>状态</label>
            <div class="status-toggle">
              <button
                class="status-btn"
                :class="{ active: editForm.isActive === 1 }"
                @click="editForm.isActive = 1"
              >上架</button>
              <button
                class="status-btn"
                :class="{ active: editForm.isActive === 0 }"
                @click="editForm.isActive = 0"
              >下架</button>
            </div>
          </div>
          <div class="modal-actions">
            <button class="admin-btn cancel" @click="closeEditModal">取消</button>
            <button class="admin-btn save" @click="doEdit">保存</button>
          </div>
        </div>
      </div>

      <div class="empty" v-if="!loading && friends.length === 0">
        <p>还没有友链，敬请期待~</p>
      </div>

      <div class="loading" v-if="loading">
        <span class="loading-dot"></span>
        <span class="loading-dot"></span>
        <span class="loading-dot"></span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useUserStore } from '../stores/user'
import { getFriendLinks, getAllFriendLinks, applyFriendLink, approveFriendLink, updateFriendLink, deleteFriendLink } from '../api/friendLink'
import { uploadImage } from '../api/upload'
import { fallbackFriends, isStaticMode } from '../config/site.config'

const userStore = useUserStore()
const isAdmin = computed(() => userStore.isAdmin)

const categories = [
  { key: 'tech', name: '技术伙伴', icon: '◇' },
  { key: 'life', name: '生活日志', icon: '∿' },
  { key: 'other', name: '其他', icon: '◆' }
]

const friends = ref([])
const allLinks = ref([])
const loading = ref(true)
const showApply = ref(false)
const showAdmin = ref(false)
const showEditModal = ref(false)
const uploadingAvatar = ref(false)
const editForm = ref({ id: null, name: '', url: '', description: '', email: '', avatar: '', category: 'tech', isActive: 1 })
const applyForm = ref({ name: '', url: '', description: '', email: '', avatar: '', category: 'tech' })
const localFriendKey = 'ethan_blog_friend_links'

function getLocalFriends() {
  try {
    return JSON.parse(localStorage.getItem(localFriendKey) || '[]')
  } catch {
    return []
  }
}

function setLocalFriends(list) {
  localStorage.setItem(localFriendKey, JSON.stringify(list))
}

function normalizeFriends(list) {
  return (list || []).map(f => ({
    ...f,
    category: f.category || 'tech',
    isActive: f.isActive ?? 1,
  }))
}

function useFallbackFriends() {
  friends.value = normalizeFriends([...getLocalFriends(), ...fallbackFriends])
}

function getFriendsByCategory(cat) {
  return friends.value.filter(f => f.category === cat)
}

function getCategoryName(cat) {
  const c = categories.find(c => c.key === cat)
  return c ? c.name : '其他'
}

const onAvatarUpload = async (e, target) => {
  const file = e.target.files?.[0]
  if (!file) return
  if (isStaticMode) {
    alert('静态部署模式暂不支持上传图片，请先粘贴头像 URL。')
    e.target.value = ''
    return
  }
  if (file.size > 2 * 1024 * 1024) { alert('图片不能超过 2MB'); return }
  uploadingAvatar.value = true
  try {
    const res = await uploadImage(file)
    if (res.code === 200 && res.data) {
      const url = res.data.url || res.data
      if (target === 'apply') applyForm.value.avatar = url
      else editForm.value.avatar = url
    } else {
      alert('上传失败：' + (res.msg || '未知错误'))
    }
  } catch (err) {
    alert('上传失败，请检查网络')
  } finally {
    uploadingAvatar.value = false
    e.target.value = ''
  }
}

async function fetchFriends() {
  loading.value = true
  if (isStaticMode) {
    useFallbackFriends()
    loading.value = false
    return
  }
  try {
    const res = await getFriendLinks()
    if (res.code === 200) {
      friends.value = normalizeFriends(res.data?.length ? res.data : [...getLocalFriends(), ...fallbackFriends])
    } else {
      useFallbackFriends()
    }
  } catch (e) {
    useFallbackFriends()
    if (import.meta.env.DEV) console.info('使用静态友链数据', e?.message || e)
  } finally {
    loading.value = false
  }
}

async function doApply() {
  if (!applyForm.value.name.trim()) { alert('请填写网站名称'); return }
  if (!applyForm.value.url.trim()) { alert('请填写网站地址'); return }
  if (isStaticMode) {
    const localLink = {
      ...applyForm.value,
      id: Date.now(),
      isActive: 1,
      avatar: applyForm.value.avatar || `https://api.dicebear.com/7.x/identicon/svg?seed=${encodeURIComponent(applyForm.value.name)}`,
    }
    const next = [localLink, ...getLocalFriends()]
    setLocalFriends(next)
    useFallbackFriends()
    applyForm.value = { name: '', url: '', description: '', email: '', avatar: '', category: 'tech' }
    showApply.value = false
    alert('当前是静态部署模式，友链申请已暂存在本机浏览器。')
    return
  }
  try {
    const res = await applyFriendLink(applyForm.value)
    if (res.code === 200) {
      alert('申请已提交，我会尽快处理！')
      applyForm.value = { name: '', url: '', description: '', email: '', avatar: '', category: 'tech' }
      showApply.value = false
    } else {
      alert(res.message || '提交失败')
    }
  } catch (e) {
    const localLink = {
      ...applyForm.value,
      id: Date.now(),
      isActive: 1,
      avatar: applyForm.value.avatar || `https://api.dicebear.com/7.x/identicon/svg?seed=${encodeURIComponent(applyForm.value.name)}`,
    }
    const next = [localLink, ...getLocalFriends()]
    setLocalFriends(next)
    useFallbackFriends()
    applyForm.value = { name: '', url: '', description: '', email: '', avatar: '', category: 'tech' }
    showApply.value = false
    alert('当前是静态部署模式，友链申请已暂存在本机浏览器。')
  }
}

async function fetchAllLinks() {
  if (!isAdmin.value) return
  if (isStaticMode) {
    allLinks.value = normalizeFriends([...getLocalFriends(), ...fallbackFriends])
    return
  }
  try {
    const res = await getAllFriendLinks()
    if (res.code === 200) allLinks.value = res.data?.length ? res.data : normalizeFriends([...getLocalFriends(), ...fallbackFriends])
  } catch (e) {
    allLinks.value = normalizeFriends([...getLocalFriends(), ...fallbackFriends])
    if (import.meta.env.DEV) console.info('使用静态友链管理数据', e?.message || e)
  }
}

async function doApprove(id, isActive) {
  if (!userStore.isLoggedIn) {
    alert('请先登录')
    return
  }
  try {
    const res = await approveFriendLink(id, isActive)
    if (res.code === 200 || res.code === 0) {
      alert(isActive === 1 ? '已通过' : '已下架')
      await fetchAllLinks()
      await fetchFriends()
    } else {
      alert(res.message || '操作失败')
    }
  } catch (e) {
    console.error('审核失败:', e)
    if (e?.response?.status === 401) {
      alert('登录已过期，请重新登录')
    } else {
      alert('操作失败')
    }
  }
}

async function doDelete(id) {
  if (!confirm('确定删除该友链？')) return
  if (!userStore.isLoggedIn) {
    alert('请先登录')
    return
  }
  try {
    const res = await deleteFriendLink(id)
    if (res.code === 200 || res.code === 0) {
      alert('删除成功')
      await fetchAllLinks()
      await fetchFriends()
    } else {
      alert('删除失败: ' + (res.message || '未知错误'))
    }
  } catch (e) {
    console.error('删除失败:', e)
    if (e?.response?.status === 401) {
      alert('登录已过期，请重新登录')
    } else {
      alert('删除失败')
    }
  }
}

function openEditModal(link) {
  editForm.value = {
    id: link.id,
    name: link.name,
    url: link.url,
    description: link.description || '',
    email: link.email || '',
    avatar: link.avatar || '',
    category: link.category || 'tech',
    isActive: link.isActive
  }
  showEditModal.value = true
}

function closeEditModal() {
  showEditModal.value = false
}

async function doEdit() {
  try {
    const res = await updateFriendLink(editForm.value.id, editForm.value)
    if (res.code === 200) {
      closeEditModal()
      await fetchAllLinks()
      await fetchFriends()
    } else {
      alert(res.message || '更新失败')
    }
  } catch (e) { alert('更新失败') }
}

onMounted(() => {
  fetchFriends()
})

// 打开管理面板时加载全部数据
watch(showAdmin, (val) => {
  if (val) fetchAllLinks()
})
</script>

<style scoped>
.friends-page {
  min-height: calc(100vh - 72px);
  padding: 56px 0 86px;
}

.friends-inner {
  max-width: 900px;
  margin: 0 auto;
  padding: 0 32px;
}

.friends-header {
  margin-bottom: 36px;
  text-align: center;
  animation: fadeInUp 0.68s var(--ease-out) both;
}

.friends-title {
  font-size: clamp(2rem, 4vw, 3.15rem);
  font-weight: 700;
  color: var(--text);
  margin-bottom: 6px;
  font-family: var(--font-serif);
  line-height: 1.1;
}

.friends-desc {
  font-size: 15px;
  color: var(--text-light);
}

/* 申请区域 - 明信片风格 */
.apply-section {
  margin-bottom: 40px;
}

.apply-postcard {
  background: rgba(8, 14, 27, 0.72);
  border: 1px dashed var(--accent);
  border-radius: 8px;
  padding: 20px 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  cursor: pointer;
  box-shadow: var(--shadow);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  transition: transform 0.26s var(--ease-out), box-shadow 0.26s var(--ease-out), border-color 0.26s;
}

.apply-postcard:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-hover);
  border-color: rgba(56, 248, 255, 0.52);
}

.postcard-stamp {
  font-size: 28px;
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, var(--accent), var(--violet));
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #06101f;
  transform: rotate(-5deg);
  box-shadow: 2px 2px 0 rgba(0,0,0,0.1);
}

.postcard-content {
  flex: 1;
}

.postcard-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 2px;
}

.postcard-hint {
  font-size: 12px;
  color: var(--text-lighter);
}

.postcard-arrow {
  color: var(--accent);
  font-size: 12px;
  transition: transform 0.3s;
}

.postcard-arrow.open {
  transform: rotate(180deg);
}

.apply-panel {
  background: rgba(8, 14, 27, 0.82);
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 24px;
  margin-top: 12px;
  box-shadow: var(--shadow);
  animation: fadeIn 0.3s var(--ease-out);
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-8px); }
  to { opacity: 1; transform: translateY(0); }
}

.apply-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-bottom: 12px;
}

.apply-input {
  padding: 10px 14px;
  border: 1px solid var(--border);
  border-radius: 8px;
  background: rgba(8, 14, 27, 0.62);
  color: var(--text);
  font-size: 14px;
  font-family: inherit;
  transition: border-color 0.2s;
  box-sizing: border-box;
  width: 100%;
}

.apply-input.full {
  width: 100%;
  margin-bottom: 12px;
}

.apply-input:focus {
  outline: none;
  border-color: var(--accent);
}

/* 分类选择 */
.category-select {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.cat-label {
  font-size: 14px;
  color: var(--text-light);
}

.cat-option {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: var(--text);
  cursor: pointer;
}

.cat-option input {
  accent-color: var(--accent);
}

/* 头像上传 */
.avatar-input-row {
  display: flex;
  gap: 8px;
  width: 100%;
  margin-bottom: 12px;
}

.avatar-input-row .apply-input {
  flex: 1;
}

.upload-avatar-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0 14px;
  background: linear-gradient(135deg, var(--accent), var(--violet));
  color: #06101f;
  border-radius: 8px;
  font-size: 13px;
  cursor: pointer;
  white-space: nowrap;
  transition: transform 0.22s var(--ease-out), background 0.2s;
  flex-shrink: 0;
}

.upload-avatar-btn:hover {
  background: linear-gradient(135deg, var(--accent), var(--ink-blue));
  transform: translateY(-1px);
}

.upload-avatar-btn.uploading {
  opacity: 0.6;
  pointer-events: none;
}

.upload-avatar-btn input[type="file"] {
  display: none;
}

.upload-avatar-btn.small {
  padding: 0 10px;
  font-size: 16px;
}

.apply-btn {
  width: 100%;
  padding: 12px;
  background: linear-gradient(135deg, var(--accent), var(--violet));
  color: #06101f;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: transform 0.22s var(--ease-out), background 0.2s, box-shadow 0.22s;
  font-family: inherit;
  margin-top: 8px;
}

.apply-btn:hover {
  background: linear-gradient(135deg, var(--accent), var(--ink-blue));
  transform: translateY(-2px);
  box-shadow: 0 14px 30px rgba(56, 248, 255, 0.16);
}

.apply-note {
  font-size: 12px;
  color: var(--text-lighter);
  text-align: center;
  margin-top: 10px;
}

/* 友链分类 */
.friends-categories {
  margin-bottom: 40px;
}

.friend-category {
  margin-bottom: 32px;
  animation: fadeInUp 0.68s var(--ease-out) both;
}

.category-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-family: var(--font-serif);
}

.cat-icon {
  font-size: 18px;
}

/* 翻转卡片网格 */
.friends-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 16px;
}

/* 简单友链卡片 */
.friend-simple-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-decoration: none;
  padding: 20px 16px;
  background: rgba(8, 14, 27, 0.72);
  border: 1px solid var(--border);
  border-radius: 8px;
  box-shadow: var(--shadow);
  backdrop-filter: blur(14px);
  -webkit-backdrop-filter: blur(14px);
  transition: transform 0.26s var(--ease-out), box-shadow 0.26s var(--ease-out), border-color 0.26s;
  cursor: pointer;
  text-align: center;
}

.friend-simple-card:hover {
  transform: translateY(-6px) rotate(-0.6deg);
  box-shadow: var(--shadow-hover);
  border-color: rgba(56, 248, 255, 0.36);
}

.simple-avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid rgba(56, 248, 255, 0.34);
  background: var(--bg-soft);
  box-shadow: 0 10px 26px rgba(56, 248, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 10px;
}

.simple-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.simple-avatar-fallback {
  font-size: 22px;
  font-weight: 700;
  color: var(--accent);
  font-family: 'Noto Serif SC', serif;
}

.simple-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 6px;
}

.simple-desc {
  font-size: 12px;
  color: var(--text-light);
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 管理区 */
.admin-section {
  margin-top: 40px;
  margin-bottom: 28px;
}

.admin-toggle {
  background: rgba(8, 14, 27, 0.72);
  border: 1px dashed var(--accent);
  border-radius: 8px;
  padding: 12px 20px;
  cursor: pointer;
  font-size: 14px;
  color: var(--accent);
  transition: all 0.2s;
}

.admin-toggle:hover {
  background: rgba(56, 248, 255, 0.08);
}

.admin-panel {
  background: rgba(8, 14, 27, 0.82);
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 16px;
  margin-top: 8px;
}

.admin-link-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  padding: 12px 0;
  border-bottom: 1px solid var(--border);
}

.admin-link-row:last-child {
  border-bottom: none;
}

.link-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.link-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text);
}

.link-cat {
  font-size: 11px;
  color: var(--text-lighter);
}

.link-status {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 10px;
}

.link-status.active {
  background: #e8f5e9;
  color: #2e7d32;
}

.link-status.inactive {
  background: #fff3e0;
  color: #e65100;
}

.link-actions {
  display: flex;
  gap: 6px;
}

.admin-btn {
  padding: 4px 12px;
  border: 1px solid var(--border);
  border-radius: 6px;
  background: rgba(8, 14, 27, 0.62);
  font-size: 12px;
  cursor: pointer;
  color: var(--text);
  transition: all 0.15s;
  font-family: inherit;
}

.admin-btn:hover {
  border-color: var(--accent);
}

.admin-btn.approve {
  color: #2e7d32;
  border-color: #a5d6a7;
}

.admin-btn.approve:hover {
  background: #e8f5e9;
}

.admin-btn.down {
  color: #e65100;
  border-color: #ffcc80;
}

.admin-btn.down:hover {
  background: #fff3e0;
}

.admin-btn.edit {
  color: var(--accent-dark);
}

.admin-btn.del {
  color: #c62828;
  border-color: #ef9a9a;
}

.admin-btn.del:hover {
  background: #ffebee;
}

.admin-btn.save {
  color: #fff;
  background: var(--accent);
  border-color: var(--accent);
}

.admin-btn.save:hover {
  background: var(--accent-dark);
}

.admin-btn.cancel {
  color: var(--text-light);
}

/* 弹窗 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.modal-content {
  background: var(--paper);
  border-radius: 8px;
  padding: 24px;
  width: 100%;
  max-width: 480px;
  max-height: 90vh;
  overflow-y: auto;
  animation: modalIn 0.3s ease;
}

@keyframes modalIn {
  from { opacity: 0; transform: scale(0.95); }
  to { opacity: 1; transform: scale(1); }
}

.modal-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 20px;
  text-align: center;
}

.modal-field {
  margin-bottom: 16px;
}

.modal-field label {
  display: block;
  font-size: 13px;
  color: var(--text-light);
  margin-bottom: 6px;
}

.modal-field .apply-input {
  margin-bottom: 0;
}

.status-toggle {
  display: flex;
  gap: 8px;
}

.status-btn {
  flex: 1;
  padding: 8px 16px;
  border: 1px solid var(--border);
  border-radius: 6px;
  background: var(--bg);
  font-size: 13px;
  color: var(--text-light);
  cursor: pointer;
  transition: all 0.2s;
}

.status-btn.active {
  background: var(--accent);
  border-color: var(--accent);
  color: #fff;
}

.modal-actions {
  display: flex;
  gap: 12px;
  margin-top: 24px;
}

.modal-actions .admin-btn {
  flex: 1;
  padding: 10px;
}

.loading {
  display: flex;
  justify-content: center;
  gap: 6px;
  padding: 40px 0;
}

.loading-dot {
  width: 8px;
  height: 8px;
  background: var(--accent);
  border-radius: 50%;
  animation: bounce 1.2s infinite;
}

.loading-dot:nth-child(2) {
  animation-delay: 0.2s;
}

.loading-dot:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes bounce {
  0%, 80%, 100% {
    transform: scale(0.6);
    opacity: 0.4;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

.empty {
  text-align: center;
  padding: 40px 0;
  color: var(--text-lighter);
  font-size: 14px;
}

@media (max-width: 600px) {
  .friends-inner {
    padding: 0 20px;
  }
  .friends-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }
  .friend-card-flip {
    height: 150px;
  }
  .apply-row {
    grid-template-columns: 1fr;
  }
  .category-select {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
</style>
