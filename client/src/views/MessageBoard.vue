<template>
  <div class="message-page">
    <div class="page-bg" aria-hidden="true">
      <div class="bg-scanlines"></div>
    </div>

    <div class="message-container">
      <!-- 页面头部 -->
      <header class="page-header">
        <div class="header-badge">COMMUNICATION</div>
        <h1 class="page-title">留言板</h1>
        <p class="page-desc">在此留下你的想法或建议</p>
      </header>

      <!-- 通信终端 -->
      <div class="terminal-panel">
        <!-- 终端头部 -->
        <div class="terminal-header">
          <div class="header-left">
            <span class="terminal-dot red"></span>
            <span class="terminal-dot yellow"></span>
            <span class="terminal-dot green"></span>
            <span class="terminal-title">message_channel</span>
          </div>
          <div class="header-right">
            <span class="terminal-status">
              <span class="status-pulse"></span>
              {{ isStaticMode ? 'GISCUS' : (loading ? 'RECEIVING' : 'READY') }}
            </span>
          </div>
        </div>

        <!-- Giscus 静态模式 -->
        <div v-if="isStaticMode" class="giscus-section">
          <div class="giscus-intro">
            <p>留言板基于 <a href="https://giscus.app" target="_blank" rel="noopener">Giscus</a>，使用 GitHub 账号登录即可留言。</p>
            <p class="giscus-hint">你的留言会保存到本仓库的 Discussions 区，博主会收到通知。</p>
          </div>
          <GiscusComments term="留言板" mapping="specific" />
        </div>

        <!-- 后端动态模式 -->
        <template v-else>
          <!-- 消息列表 -->
          <div class="terminal-body" ref="messageListRef">
            <div v-if="loading" class="terminal-loading">
              <span class="loading-text">$ 正在接收消息...</span>
            </div>

            <div v-else-if="messages.length === 0" class="terminal-empty">
              <span class="empty-prompt">$ 暂无消息记录</span>
              <span class="empty-hint">成为第一个留言的人</span>
            </div>

            <div v-else class="message-stream">
              <div
                v-for="msg in messages"
                :key="msg.id"
                class="message-bubble"
                :class="{ 'message-mine': msg.isMine }"
              >
                <div class="bubble-header">
                  <span class="bubble-name">{{ msg.nickname || '匿名用户' }}</span>
                  <div class="bubble-header-right">
                    <span class="bubble-time">{{ formatTime(msg.createTime) }}</span>
                    <template v-if="userStore.isAdmin">
                      <button class="msg-btn reply-btn" @click.stop="toggleReply(msg)">回复</button>
                      <button class="msg-btn del-msg-btn" @click.stop="handleDelete(msg.id)">删除</button>
                    </template>
                  </div>
                </div>
                <div class="bubble-content">{{ msg.content }}</div>
                <div class="sub-replies" v-if="msg.children && msg.children.length">
                  <div v-for="sub in msg.children" :key="sub.id" class="sub-reply-bubble">
                    <span class="sub-reply-name">{{ sub.nickname }}：</span>
                    <span class="sub-reply-content">{{ sub.content }}</span>
                  </div>
                </div>
                <div class="reply-form" v-if="showReplyInput === msg.id">
                  <textarea v-model="replyText" placeholder="输入回复内容..." class="reply-input" maxlength="300" rows="2"></textarea>
                  <div class="reply-actions">
                    <button class="reply-cancel" @click="cancelReply">取消</button>
                    <button class="reply-submit" @click="submitReply(msg.id)">发送</button>
                  </div>
                </div>
                <div class="bubble-footer" v-if="msg.location || msg.browser">
                  <span v-if="msg.location" class="bubble-meta">{{ msg.location }}</span>
                  <span v-if="msg.browser" class="bubble-meta">{{ msg.browser }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 输入区域 -->
          <div class="terminal-input-area">
            <div class="input-header">
              <span class="input-prompt">></span>
              <span class="input-label">输入消息</span>
            </div>
            <div class="input-form">
              <input
                v-model="nickname"
                type="text"
                class="form-input"
                placeholder="昵称（选填）"
                maxlength="30"
              />
              <input
                v-model="contact"
                type="text"
                class="form-input"
                placeholder="联系方式（选填）"
                maxlength="50"
              />
              <textarea
                v-model="content"
                class="form-textarea"
                placeholder="留下你的想法..."
                rows="3"
                maxlength="500"
                @keydown.enter.ctrl="handleSubmit"
              ></textarea>
              <button
                class="send-btn"
                :disabled="!content.trim() || sending"
                @click="handleSubmit"
              >
                <span v-if="!sending">发送消息</span>
                <span v-else>发送中...</span>
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="22" y1="2" x2="11" y2="13"/>
                  <polygon points="22 2 15 22 11 13 2 9 22 2"/>
                </svg>
              </button>

              <div v-if="submitMsg" class="submit-msg" :class="submitOk ? 'ok' : 'err'">
                {{ submitMsg }}
              </div>
            </div>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { getMessageList, addMessage, deleteMessage, replyMessage } from '../api/message'
import { isStaticMode } from '../config/site.config'
import { useUserStore } from '../stores/user'
import GiscusComments from '../components/GiscusComments.vue'

const userStore = useUserStore()
const replyTarget = ref(null)
const replyText = ref('')
const showReplyInput = ref(null)
const messages = ref([])
const loading = ref(true)
const sending = ref(false)
const nickname = ref('')
const contact = ref('')
const content = ref('')
const messageListRef = ref(null)
const submitMsg = ref('')
const submitOk = ref(false)

const loadMessages = async () => {
  loading.value = true
  if (isStaticMode) {
    messages.value = []
    loading.value = false
    return
  }
  try {
    const res = await getMessageList(1, 50)
    if (res.code === 200) {
      messages.value = res.data?.records || res.data || []
    }
  } catch (e) {
    messages.value = []
    if (import.meta.env.DEV) console.info('留言加载失败', e?.message || e)
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  if (!content.value.trim() || sending.value) return
  sending.value = true
  submitMsg.value = ''
  submitOk.value = false
  try {
    const res = await addMessage({
      nickname: nickname.value || '匿名用户',
      content: content.value.trim(),
      contact: contact.value || undefined
    })
    if (res.code === 200) {
      messages.value.push(res.data || {
        nickname: nickname.value,
        content: content.value.trim(),
        createTime: new Date().toISOString()
      })
      content.value = ''
      submitMsg.value = '✓ 消息发送成功'
      submitOk.value = true
      await nextTick()
      scrollToBottom()
      setTimeout(() => { submitMsg.value = '' }, 3000)
    } else {
      submitMsg.value = '✗ 发送失败：' + (res.message || '未知错误')
      submitOk.value = false
    }
  } catch (e) {
    submitMsg.value = '✗ 发送失败：' + (e?.message || '网络错误，请检查后端是否启动')
    submitOk.value = false
    if (import.meta.env.DEV) console.error('发送失败', e)
  } finally {
    sending.value = false
  }
}

const scrollToBottom = () => {
  if (messageListRef.value) {
    messageListRef.value.scrollTop = messageListRef.value.scrollHeight
  }
}


const formatTime = (s) => {
  if (!s) return ''
  // 后端返回 LocalDateTime 无时区信息，需按本地时间解析
  const d = new Date(s + '+08:00')
  if (isNaN(d.getTime())) return s
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

const handleDelete = async (id) => {
  if (!confirm('确定删除这条留言吗？')) return
  try {
    const res = await deleteMessage(id)
    if (res.code === 200 || res.code === 0) {
      messages.value = messages.value.filter(m => m.id !== id)
    } else {
      alert('删除失败：' + (res.message || '未知错误'))
    }
  } catch (e) {
    alert('删除失败：' + (e?.message || '网络错误'))
  }
}

const toggleReply = (msg) => {
  if (showReplyInput.value === msg.id) {
    showReplyInput.value = null
    replyText.value = ''
  } else {
    showReplyInput.value = msg.id
    replyText.value = ''
  }
}

const cancelReply = () => {
  showReplyInput.value = null
  replyText.value = ''
}

const submitReply = async (id) => {
  if (!replyText.value.trim()) {
    alert('回复内容不能为空')
    return
  }
  try {
    const res = await replyMessage(id, replyText.value.trim())
    if (res.code === 200 || res.code === 0) {
      showReplyInput.value = null
      replyText.value = ''
      loadMessages() // 重新加载列表
    } else {
      alert('回复失败：' + (res.message || '未知错误'))
    }
  } catch (e) {
    alert('回复失败：' + (e?.message || '网络错误'))
  }
}

onMounted(loadMessages)
</script>

<style scoped>
.message-page {
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

.bg-scanlines {
  position: absolute;
  inset: 0;
  background: 
    repeating-linear-gradient(
      0deg,
      transparent,
      transparent 3px,
      rgba(56, 248, 255, 0.015) 3px,
      rgba(56, 248, 255, 0.015) 6px
    ),
    radial-gradient(circle at 20% 50%, rgba(56, 248, 255, 0.04), transparent 50%),
    radial-gradient(circle at 80% 80%, rgba(155, 92, 255, 0.04), transparent 50%);
}

/* 浮动粒子背景 */
.bg-scanlines::before,
.bg-scanlines::after {
  content: '';
  position: absolute;
  width: 2px;
  height: 2px;
  border-radius: 50%;
  background: #38f8ff;
  box-shadow: 
    100px 200px 0 0 rgba(56, 248, 255, 0.3),
    300px 400px 0 0 rgba(56, 248, 255, 0.2),
    500px 100px 0 0 rgba(155, 92, 255, 0.3),
    700px 500px 0 0 rgba(56, 248, 255, 0.15),
    900px 300px 0 0 rgba(155, 92, 255, 0.2),
    1100px 700px 0 0 rgba(56, 248, 255, 0.25),
    200px 600px 0 0 rgba(56, 248, 255, 0.1),
    600px 150px 0 0 rgba(155, 92, 255, 0.15);
  animation: particleFloat 15s ease-in-out infinite;
}
.bg-scanlines::after {
  animation-delay: -7s;
  animation-duration: 20s;
}
@keyframes particleFloat {
  0%, 100% { transform: translateY(0) translateX(0); opacity: 0.3; }
  50% { transform: translateY(-30px) translateX(20px); opacity: 0.6; }
}

.message-container {
  position: relative;
  z-index: 1;
  max-width: 900px;
  margin: 0 auto;
}

/* 页面头部 */
.page-header {
  margin-bottom: 40px;
  animation: headerIn 0.6s var(--ease-out) backwards;
}

@keyframes headerIn {
  from { opacity: 0; transform: translateY(-15px); }
  to { opacity: 1; transform: translateY(0); }
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
  box-shadow: 0 0 20px rgba(56, 248, 255, 0.1);
  animation: badgePulse 3s ease-in-out infinite;
}

@keyframes badgePulse {
  0%, 100% { box-shadow: 0 0 20px rgba(56, 248, 255, 0.1); }
  50% { box-shadow: 0 0 30px rgba(56, 248, 255, 0.25); }
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

/* 通信终端面板 */
.terminal-panel {
  background: rgba(8, 12, 22, 0.9);
  border: 1px solid rgba(56, 248, 255, 0.2);
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.4), 0 0 60px rgba(56, 248, 255, 0.04);
  backdrop-filter: blur(10px);
  animation: panelIn 0.6s var(--ease-out) 0.2s backwards;
}

@keyframes panelIn {
  from { opacity: 0; transform: translateY(30px); filter: blur(8px); }
  to { opacity: 1; transform: translateY(0); filter: blur(0); }
}

/* 终端边框发光效果 */
.terminal-panel::before {
  content: '';
  position: absolute;
  inset: -1px;
  border-radius: 16px;
  padding: 1px;
  background: linear-gradient(135deg, rgba(56, 248, 255, 0.3), transparent 40%, transparent 60%, rgba(155, 92, 255, 0.2));
  -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  pointer-events: none;
}

/* 终端头部 */
.terminal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid rgba(56, 248, 255, 0.1);
  background: rgba(0, 0, 0, 0.3);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.terminal-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

.terminal-dot.red { background: #ff5f57; }
.terminal-dot.yellow { background: #ffbd2e; }
.terminal-dot.green { background: #28ca42; }

.terminal-title {
  margin-left: 12px;
  font-family: 'SF Mono', monospace;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.6);
}

.terminal-status {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 11px;
  font-weight: 600;
  color: #4ade80;
  letter-spacing: 0.1em;
}

.status-pulse {
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

/* 终端消息区 */
.terminal-body {
  max-height: 500px;
  overflow-y: auto;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.terminal-body::-webkit-scrollbar {
  width: 6px;
}

.terminal-body::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.05);
}

.terminal-body::-webkit-scrollbar-thumb {
  background: rgba(56, 248, 255, 0.3);
  border-radius: 3px;
}

/* 加载/空状态 */
.terminal-loading,
.terminal-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 40px 0;
  font-family: 'SF Mono', monospace;
  color: rgba(255, 255, 255, 0.4);
}

.loading-text,
.empty-prompt {
  font-size: 14px;
}

.empty-hint {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.25);
}

/* 消息气泡 */
.message-bubble {
  max-width: 80%;
  padding: 16px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  align-self: flex-start;
  animation: msgIn 0.4s var(--ease-out) backwards;
  transition: transform 0.2s, box-shadow 0.2s;
}

.message-bubble:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(56, 248, 255, 0.08);
  border-color: rgba(56, 248, 255, 0.2);
}

@keyframes msgIn {
  from { opacity: 0; transform: translateX(-20px) scale(0.95); }
  to { opacity: 1; transform: translateX(0) scale(1); }
}

.message-bubble.message-mine {
  align-self: flex-end;
  background: rgba(56, 248, 255, 0.1);
  border-color: rgba(56, 248, 255, 0.2);
}

.bubble-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  gap: 12px;
}

.bubble-header-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.bubble-name {
  font-size: 13px;
  font-weight: 600;
  color: #38f8ff;
}

.bubble-time {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.3);
  font-family: 'SF Mono', monospace;
}

.msg-btn {
  padding: 2px 8px;
  font-size: 11px;
  background: none;
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 4px;
  color: #888;
  cursor: pointer;
  transition: all 0.2s;
}

.msg-btn:hover {
  border-color: var(--accent);
  color: var(--accent);
}

.reply-btn:hover {
  border-color: var(--accent);
  color: var(--accent);
}

.del-msg-btn {
  border-color: rgba(255, 100, 100, 0.3);
  color: #ff8a8a;
}

.del-msg-btn:hover {
  background: rgba(255, 100, 100, 0.15);
  border-color: rgba(255, 100, 100, 0.5);
}

.sub-replies {
  margin-top: 10px;
  padding: 8px 10px;
  background: rgba(56, 248, 255, 0.04);
  border: 1px solid rgba(56, 248, 255, 0.12);
  border-radius: 6px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.sub-reply-bubble {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.7);
  line-height: 1.5;
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  align-items: baseline;
}

.sub-reply-name {
  color: var(--accent);
  font-weight: 600;
}

.sub-reply-content {
  flex: 1;
  min-width: 0;
}

.sub-reply-time {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.3);
  white-space: nowrap;
}

.reply-form {
  margin-top: 12px;
  padding: 10px;
  background: rgba(10, 15, 30, 0.6);
  border: 1px solid rgba(56, 248, 255, 0.2);
  border-radius: 8px;
}

.reply-input {
  width: 100%;
  background: rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 6px;
  padding: 8px 10px;
  color: var(--text);
  font-size: 13px;
  resize: vertical;
  outline: none;
  box-sizing: border-box;
}

.reply-input:focus {
  border-color: var(--accent);
}

.reply-actions {
  display: flex;
  gap: 8px;
  margin-top: 8px;
  justify-content: flex-end;
}

.reply-submit {
  padding: 4px 14px;
  background: var(--accent);
  border: none;
  border-radius: 4px;
  color: var(--bg);
  font-size: 12px;
  cursor: pointer;
}

.reply-cancel {
  padding: 4px 14px;
  background: none;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 4px;
  color: #888;
  font-size: 12px;
  cursor: pointer;
}

.bubble-content {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  line-height: 1.6;
  word-break: break-word;
}

.bubble-footer {
  display: flex;
  gap: 12px;
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
}

.bubble-meta {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.3);
}

/* 输入区域 */
.terminal-input-area {
  padding: 20px;
  border-top: 1px solid rgba(56, 248, 255, 0.1);
  background: rgba(0, 0, 0, 0.2);
}

.input-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
}

.input-prompt {
  font-family: 'SF Mono', monospace;
  font-size: 14px;
  color: #38f8ff;
}

.input-label {
  font-size: 12px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.4);
  letter-spacing: 0.1em;
}

.input-form {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.form-input {
  padding: 12px 16px;
  border: 1px solid rgba(56, 248, 255, 0.15);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.03);
  color: #fff;
  font-size: 13px;
  transition: border-color 0.2s;
}

.form-input:focus {
  outline: none;
  border-color: rgba(56, 248, 255, 0.4);
}

.form-input::placeholder {
  color: rgba(255, 255, 255, 0.25);
}

.form-textarea {
  padding: 12px 16px;
  border: 1px solid rgba(56, 248, 255, 0.15);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.03);
  color: #fff;
  font-size: 14px;
  font-family: inherit;
  resize: vertical;
  min-height: 80px;
  transition: border-color 0.2s;
}

.form-textarea:focus {
  outline: none;
  border-color: rgba(56, 248, 255, 0.4);
}

.form-textarea::placeholder {
  color: rgba(255, 255, 255, 0.25);
}

.send-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  background: linear-gradient(135deg, rgba(56, 248, 255, 0.2), rgba(155, 92, 255, 0.2));
  border: 1px solid rgba(56, 248, 255, 0.4);
  color: #fff;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  align-self: flex-end;
}

.send-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(56, 248, 255, 0.2);
}

.send-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.submit-msg {
  margin-top: 12px;
  padding: 10px 14px;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
}

.submit-msg.ok {
  background: rgba(56, 248, 255, 0.12);
  border: 1px solid rgba(56, 248, 255, 0.3);
  color: #38f8ff;
}

.submit-msg.err {
  background: rgba(255, 100, 100, 0.12);
  border: 1px solid rgba(255, 100, 100, 0.3);
  color: #ff8a8a;
}

/* Giscus 静态模式 */
.giscus-section {
  padding: 24px;
}

.giscus-intro {
  margin-bottom: 20px;
  padding: 16px;
  background: rgba(56, 248, 255, 0.05);
  border: 1px solid rgba(56, 248, 255, 0.15);
  border-radius: 8px;
}

.giscus-intro p {
  margin: 0 0 6px 0;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.7);
  line-height: 1.6;
}

.giscus-intro p:last-child {
  margin-bottom: 0;
}

.giscus-intro a {
  color: #38f8ff;
  text-decoration: none;
  font-weight: 600;
}

.giscus-intro a:hover {
  text-decoration: underline;
}

.giscus-hint {
  font-size: 12px !important;
  color: rgba(255, 255, 255, 0.4) !important;
}

/* 响应式 */
@media (max-width: 768px) {
  .message-page {
    padding: 72px 16px 80px;
  }

  .message-bubble {
    max-width: 95%;
  }

  .bubble-footer {
    flex-direction: column;
    gap: 4px;
  }

  .message-stream {
    column-count: 1 !important;
  }
}

/* 瀑布流布局 */
.message-stream {
  column-count: 2;
  column-gap: 16px;
  align-items: stretch;
}

.message-bubble {
  break-inside: avoid;
  margin-bottom: 16px;
  max-width: 100%;
}

.message-bubble:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(56, 248, 255, 0.08);
  border-color: rgba(56, 248, 255, 0.2);
}
</style>