<template>
  <div class="message-page">
    <div class="message-container">
      <!-- 标题区 -->
      <header class="message-header">
        <h1 class="message-title">留言板</h1>
        <p class="message-subtitle">想说点什么？留下你的足迹</p>
      </header>

      <!-- 发布留言 -->
      <div class="message-form">
        <div class="form-row">
          <input
            v-model="form.nickname"
            class="form-input"
            placeholder="你的昵称（选填，默认匿名）"
            maxlength="50"
          />
          <input
            v-model="form.email"
            class="form-input"
            type="email"
            placeholder="邮箱（选填，收到回复会通知）"
            maxlength="100"
          />
        </div>
        <div class="form-textarea-wrap">
          <textarea
            v-model="form.content"
            class="form-textarea"
            placeholder="写下你的留言..."
            maxlength="500"
            rows="4"
            @keydown.ctrl.enter="submitMessage"
          ></textarea>
          <div class="textarea-footer">
            <span class="char-count" :class="{ warn: form.content.length > 450 }">
              {{ form.content.length }}/500
            </span>
            <button class="submit-btn" @click="submitMessage" :disabled="submitting || !form.content.trim()">
              {{ submitting ? '提交中...' : '发表评论' }}
            </button>
          </div>
        </div>
        <p class="form-tip">Ctrl+Enter 快捷提交 · 留言仅博主可见</p>
      </div>

      <!-- 留言列表 -->
      <div class="message-list">
        <div v-if="loading" class="loading">
          <span class="loading-dot"></span>
          <span class="loading-dot"></span>
          <span class="loading-dot"></span>
        </div>

        <div v-else-if="!messages.length" class="empty-message">
          <p>还没有留言，来做第一个留言的人吧~</p>
        </div>

        <div v-else>
          <div
            v-for="(msg, idx) in messages"
            :key="msg.id"
            class="message-item"
            :style="{ animationDelay: `${idx * 60}ms` }"
          >
            <div class="msg-avatar">
              <img
                v-if="msg.avatar"
                :src="msg.avatar"
                :alt="msg.nickname"
              />
              <span v-else class="avatar-default">
                {{ getInitial(msg.nickname) }}
              </span>
            </div>
            <div class="msg-body">
              <div class="msg-meta">
                <span class="msg-name">{{ msg.nickname || '匿名用户' }}</span>
                <span class="msg-time">{{ formatTime(msg.createTime) }}</span>
                <span v-if="msg.ipAddress && isAdmin" class="msg-ip">IP: {{ maskIp(msg.ipAddress) }}</span>
                <span v-if="msg.email && isAdmin" class="msg-email">✉ {{ msg.email }}</span>
              </div>
              <div class="msg-content">{{ msg.content }}</div>
              <!-- 博主回复（旧版兼容） -->
              <div v-if="msg.reply" class="msg-reply">
                <span class="reply-badge">博主回复</span>
                <span class="reply-text">{{ msg.reply }}</span>
              </div>
              <!-- 子留言（连续回复） -->
              <div v-if="msg.children && msg.children.length" class="msg-children">
                <div v-for="child in msg.children" :key="child.id" class="child-message">
                  <div class="child-meta">
                    <span class="child-name">{{ child.nickname || '博主' }}</span>
                    <span class="child-time">{{ formatTime(child.createTime) }}</span>
                  </div>
                  <div class="child-content">{{ child.content }}</div>
                </div>
              </div>
              <div class="msg-actions" v-if="isAdmin">
                <button class="reply-btn" @click="replyingId = replyingId === msg.id ? null : msg.id; replyContent = ''">回复</button>
                <button class="del-btn" @click="deleteMsg(msg.id)">删除</button>
              </div>
              <!-- 回复输入 -->
              <div v-if="replyingId === msg.id" class="reply-input-row">
                <input v-model="replyContent" class="reply-input" placeholder="输入回复内容..." />
                <button class="reply-send" @click="doReply(msg.id)">发送</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getMessageList, addMessage, deleteMessage, replyMessage } from '../api/message'
import { useUserStore } from '../stores/user'
import { fallbackMessages, isStaticMode } from '../config/site.config'

const userStore = useUserStore()
const isAdmin = computed(() => userStore.isAdmin)

const messages = ref([])
const loading = ref(true)
const submitting = ref(false)
const replyingId = ref(null)
const replyContent = ref('')
const form = ref({
  nickname: '',
  email: '',
  content: ''
})
const localMessageKey = 'ethan_blog_messages'

function getLocalMessages() {
  try {
    return JSON.parse(localStorage.getItem(localMessageKey) || '[]')
  } catch {
    return []
  }
}

function setLocalMessages(list) {
  localStorage.setItem(localMessageKey, JSON.stringify(list))
}

function useFallbackMessages() {
  messages.value = [...getLocalMessages(), ...fallbackMessages]
}

function getInitial(name) {
  if (!name) return '游'
  return name.slice(0, 1).toUpperCase()
}

function formatTime(time) {
  if (!time) return ''
  const d = new Date(time)
  const now = new Date()
  const diff = (now - d) / 1000
  if (diff < 60) return '刚刚'
  if (diff < 3600) return Math.floor(diff / 60) + '分钟前'
  if (diff < 86400) return Math.floor(diff / 3600) + '小时前'
  if (diff < 2592000) return Math.floor(diff / 86400) + '天前'
  return `${d.getMonth() + 1}月${d.getDate()}日`
}

function maskIp(ip) {
  if (!ip) return ''
  const parts = ip.split('.')
  if (parts.length === 4) return parts[0] + '.' + parts[1] + '.*.*'
  return ip
}

async function fetchMessages() {
  loading.value = true
  if (isStaticMode) {
    useFallbackMessages()
    loading.value = false
    return
  }
  try {
    const res = await getMessageList()
    if (res.code === 200) {
      messages.value = res.data?.length ? res.data : [...getLocalMessages(), ...fallbackMessages]
    } else {
      useFallbackMessages()
    }
  } catch (e) {
    useFallbackMessages()
    if (import.meta.env.DEV) console.info('使用静态留言数据', e?.message || e)
  } finally {
    loading.value = false
  }
}

async function submitMessage() {
  if (!form.value.content.trim()) return
  if (form.value.content.length > 500) {
    alert('留言不能超过500字')
    return
  }
  submitting.value = true
  if (isStaticMode) {
    const localMessage = {
      id: Date.now(),
      nickname: form.value.nickname.trim() || '匿名用户',
      email: form.value.email.trim(),
      content: form.value.content.trim(),
      createTime: new Date().toISOString(),
    }
    const next = [localMessage, ...getLocalMessages()]
    setLocalMessages(next)
    messages.value = [localMessage, ...messages.value]
    form.value.content = ''
    submitting.value = false
    alert('当前是静态部署模式，留言已保存在本机浏览器。')
    return
  }
  try {
    const res = await addMessage({
      nickname: form.value.nickname,
      email: form.value.email,
      content: form.value.content.trim()
    })
    if (res.code === 200) {
      form.value.content = ''
      // 已有昵称则保留
      await fetchMessages()
    } else {
      alert(res.message || '提交失败')
    }
  } catch (e) {
    const localMessage = {
      id: Date.now(),
      nickname: form.value.nickname.trim() || '匿名用户',
      email: form.value.email.trim(),
      content: form.value.content.trim(),
      createTime: new Date().toISOString(),
    }
    const next = [localMessage, ...getLocalMessages()]
    setLocalMessages(next)
    messages.value = [localMessage, ...messages.value]
    form.value.content = ''
    alert('当前是静态部署模式，留言已保存在本机浏览器。')
  } finally {
    submitting.value = false
  }
}

async function deleteMsg(id) {
  if (!confirm('确定删除这条留言？')) return

  // 检查登录状态
  if (!userStore.isLoggedIn) {
    alert('请先登录')
    return
  }

  try {
    const res = await deleteMessage(id)
    if (res.code === 200 || res.code === 0) {
      messages.value = messages.value.filter(m => m.id !== id)
      alert('删除成功')
    } else {
      alert('删除失败: ' + (res.message || '未知错误'))
    }
  } catch (e) {
    console.error('删除留言失败:', e)
    const msg = e?.response?.data?.message || e?.message || '删除失败'
    if (e?.response?.status === 401) {
      alert('登录已过期，请重新登录')
    } else {
      alert(msg)
    }
  }
}

async function doReply(id) {
  if (!replyContent.value.trim()) return
  try {
    const res = await replyMessage(id, replyContent.value.trim())
    if (res.code === 200) {
      replyingId.value = null
      replyContent.value = ''
      await fetchMessages()
    } else {
      alert(res.message || '回复失败')
    }
  } catch (e) {
    alert('回复失败')
  }
}

onMounted(() => {
  fetchMessages()
})
</script>

<style scoped>
.message-page {
  min-height: calc(100vh - 72px);
  padding: 56px 0 86px;
}

.message-container {
  max-width: 760px;
  margin: 0 auto;
  padding: 0 32px;
}

.message-header {
  margin-bottom: 32px;
  animation: fadeInUp 0.68s var(--ease-out) both;
}

.message-title {
  font-size: clamp(2rem, 4vw, 3.15rem);
  font-weight: 700;
  color: var(--text);
  margin-bottom: 6px;
  font-family: var(--font-serif);
  line-height: 1.1;
}

.message-subtitle {
  font-size: 15px;
  color: var(--text-light);
}

/* 留言表单 */
.message-form {
  background: rgba(255, 250, 241, 0.78);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  padding: 24px;
  margin-bottom: 40px;
  box-shadow: var(--shadow);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  animation: fadeInUp 0.68s var(--ease-out) 0.08s both;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-bottom: 12px;
}

.form-input {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid var(--border);
  border-radius: 8px;
  background: rgba(255, 250, 241, 0.72);
  color: var(--text);
  font-size: 14px;
  font-family: inherit;
  transition: border-color 0.2s, box-shadow 0.2s;
  box-sizing: border-box;
}

.form-input:focus {
  outline: none;
  border-color: var(--accent);
}

.form-textarea-wrap {
  position: relative;
}

.form-textarea {
  width: 100%;
  padding: 12px 14px;
  border: 1px solid var(--border);
  border-radius: 8px;
  background: rgba(255, 250, 241, 0.72);
  color: var(--text);
  font-size: 14px;
  font-family: inherit;
  resize: vertical;
  min-height: 100px;
  transition: border-color 0.2s, box-shadow 0.2s;
  box-sizing: border-box;
}

.form-textarea:focus {
  outline: none;
  border-color: var(--accent);
}

.textarea-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 8px;
}

.char-count {
  font-size: 12px;
  color: var(--text-lighter);
}

.char-count.warn {
  color: #e6a23c;
}

.submit-btn {
  padding: 8px 20px;
  background: var(--accent);
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: transform 0.22s var(--ease-out), background 0.2s, box-shadow 0.22s;
  font-family: inherit;
}

.submit-btn:hover:not(:disabled) {
  background: var(--accent-dark);
  transform: translateY(-2px);
  box-shadow: 0 14px 24px rgba(153, 97, 22, 0.18);
}

.submit-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.form-tip {
  font-size: 12px;
  color: var(--text-lighter);
  margin-top: 8px;
  text-align: right;
}

/* 留言列表 */
.message-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
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

.loading-dot:nth-child(2) { animation-delay: 0.2s; }
.loading-dot:nth-child(3) { animation-delay: 0.4s; }

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0.6); opacity: 0.4; }
  40% { transform: scale(1); opacity: 1; }
}

.empty-message {
  text-align: center;
  padding: 40px 0;
  color: var(--text-lighter);
  font-size: 14px;
}

.message-item {
  display: flex;
  gap: 14px;
  padding: 18px;
  border: 1px solid var(--border);
  border-radius: 8px;
  background: rgba(255, 250, 241, 0.62);
  box-shadow: 0 10px 24px rgba(88, 66, 38, 0.06);
  backdrop-filter: blur(14px);
  -webkit-backdrop-filter: blur(14px);
  animation: fadeUp 0.45s var(--ease-out) both;
  transition: transform 0.24s var(--ease-out), box-shadow 0.24s var(--ease-out), border-color 0.24s;
}

.message-item:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow);
  border-color: rgba(201, 133, 36, 0.28);
}

@keyframes fadeUp {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}

.msg-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
  background: var(--bg-soft);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 10px 22px rgba(88, 66, 38, 0.12);
}

.msg-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-default {
  font-size: 14px;
  font-weight: 700;
  color: var(--accent);
  font-family: 'Noto Serif SC', serif;
}

.msg-body {
  flex: 1;
  min-width: 0;
}

.msg-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 6px;
}

.msg-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text);
}

.msg-time {
  font-size: 12px;
  color: var(--text-lighter);
}

.msg-ip {
  font-size: 11px;
  color: var(--text-lighter);
  background: rgba(47, 72, 88, 0.08);
  padding: 1px 6px;
  border-radius: 4px;
}

.msg-content {
  font-size: 14px;
  color: var(--text);
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-word;
}

.msg-actions {
  margin-top: 6px;
}

.del-btn {
  font-size: 12px;
  color: #c0c0c0;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  transition: color 0.2s, transform 0.2s var(--ease-out);
}

.del-btn:hover {
  color: #f56c6c;
  transform: translateY(-1px);
}

.reply-btn {
  font-size: 12px;
  color: #c0c0c0;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  margin-right: 10px;
  transition: color 0.2s;
}
.reply-btn:hover { color: var(--accent); }

.msg-email {
  font-size: 11px;
  color: var(--text-lighter);
  margin-left: 6px;
}

.msg-reply {
  margin-top: 8px;
  padding: 8px 12px;
  background: rgba(201, 133, 36, 0.08);
  border-radius: 8px;
  border-left: 3px solid var(--accent);
}
.reply-badge {
  font-size: 11px;
  color: var(--accent-dark);
  font-weight: 600;
  margin-right: 6px;
}
.reply-text {
  font-size: 13px;
  color: var(--text);
}

.reply-input-row {
  display: flex;
  gap: 8px;
  margin-top: 8px;
}
.reply-input {
  flex: 1;
  padding: 6px 12px;
  border: 1px solid var(--border);
  border-radius: 6px;
  font-size: 13px;
  font-family: inherit;
  background: rgba(255, 250, 241, 0.72);
  color: var(--text);
}
.reply-input:focus { outline: none; border-color: var(--accent); }
.reply-send {
  padding: 6px 16px;
  background: var(--accent);
  color: #fff;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
  font-family: inherit;
}
.reply-send:hover { background: var(--accent-dark); }

/* 子留言样式 */
.msg-children {
  margin-top: 12px;
  padding-left: 12px;
  border-left: 2px solid var(--border);
}
.child-message {
  padding: 8px 12px;
  background: rgba(255, 250, 241, 0.72);
  border-radius: 6px;
  margin-bottom: 8px;
}
.child-message:last-child {
  margin-bottom: 0;
}
.child-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}
.child-name {
  font-size: 13px;
  font-weight: 600;
  color: var(--accent);
}
.child-time {
  font-size: 12px;
  color: var(--text-lighter);
}
.child-content {
  font-size: 13px;
  color: var(--text);
  line-height: 1.5;
}

@media (max-width: 600px) {
  .message-container { padding: 0 20px; }
  .form-row { grid-template-columns: 1fr; }
}
</style>
