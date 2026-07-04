<template>
  <div class="message-page">
    <div class="page-bg" aria-hidden="true">
      <div class="bg-scanlines"></div>
    </div>

    <div class="message-container">
      <!-- 页面头部 -->
      <header class="page-header">
        <div class="header-badge">{{ ui.badge }}</div>
        <h1 class="page-title">{{ ui.title }}</h1>
        <p class="page-desc">{{ ui.desc }}</p>
      </header>

      <!-- 便签墙 -->
      <section class="message-lab">
        <aside class="note-composer">
          <div class="composer-orbit" aria-hidden="true">
            <span></span>
            <span></span>
            <span></span>
          </div>
          <div class="composer-head">
            <span>{{ ui.uplink }}</span>
            <strong>{{ isStaticMode ? ui.githubMode : (loading ? ui.syncing : ui.ready) }}</strong>
          </div>

          <template v-if="isStaticMode">
            <div class="giscus-intro">
              <p>{{ ui.staticIntro }}</p>
              <p class="giscus-hint">{{ ui.staticHint }}</p>
            </div>
            <GiscusComments term="留言板" mapping="specific" />
          </template>

          <template v-else>
            <div class="input-form">
              <input
                v-model="nickname"
                type="text"
                class="form-input"
                :placeholder="ui.nickname"
                maxlength="30"
              />
              <input
                v-model="contact"
                type="text"
                class="form-input"
                :placeholder="ui.contact"
                maxlength="50"
              />
              <textarea
                v-model="content"
                class="form-textarea"
                :placeholder="ui.content"
                rows="4"
                maxlength="500"
                @keydown.enter.ctrl="handleSubmit"
              ></textarea>
              <button
                class="send-btn"
                :disabled="!content.trim() || sending"
                @click="handleSubmit"
              >
                <span v-if="!sending">{{ ui.send }}</span>
                <span v-else>{{ ui.sending }}</span>
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="22" y1="2" x2="11" y2="13"/>
                  <polygon points="22 2 15 22 11 13 2 9 22 2"/>
                </svg>
              </button>

              <div v-if="submitMsg" class="submit-msg" :class="submitOk ? 'ok' : 'err'">
                {{ submitMsg }}
              </div>
            </div>
          </template>
        </aside>

        <section class="note-board" ref="messageListRef" :aria-label="ui.boardAria">
          <div class="board-network" aria-hidden="true"></div>

          <div v-if="!isStaticMode && loading" class="note-state">
            <span>{{ ui.loading }}</span>
          </div>

          <div v-else-if="visibleMessages.length === 0" class="note-state">
            <span>{{ ui.empty }}</span>
          </div>

          <div v-else class="note-grid">
            <article
              v-for="(msg, index) in visibleMessages"
              :key="msg.id || index"
              class="message-note"
              :class="{ 'message-mine': msg.isMine, 'message-local': msg.isLocal || msg.isFallback }"
              :style="{ '--note-rotate': noteRotation(index) }"
            >
              <div class="note-pin" aria-hidden="true"></div>
              <div class="bubble-header">
                <span class="bubble-name">{{ msg.nickname || ui.anonymous }}</span>
                <div class="bubble-header-right">
                  <span class="bubble-time">{{ formatTime(msg.createTime) }}</span>
                  <template v-if="userStore.isAdmin && !isStaticMode">
                    <button class="msg-btn reply-btn" @click.stop="toggleReply(msg)">{{ ui.reply }}</button>
                    <button class="msg-btn del-msg-btn" @click.stop="handleDelete(msg.id)">{{ ui.delete }}</button>
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
                <textarea v-model="replyText" :placeholder="ui.replyPlaceholder" class="reply-input" maxlength="300" rows="2"></textarea>
                <div class="reply-actions">
                  <button class="reply-cancel" @click="cancelReply">{{ ui.cancel }}</button>
                  <button class="reply-submit" @click="submitReply(msg.id)">{{ ui.sendShort }}</button>
                </div>
              </div>
              <div class="bubble-footer" v-if="msg.location || msg.browser || msg.isLocal || msg.isFallback">
                <span v-if="msg.location" class="bubble-meta">{{ msg.location }}</span>
                <span v-if="msg.browser" class="bubble-meta">{{ msg.browser }}</span>
                <span v-if="msg.isLocal" class="bubble-meta">LOCAL PREVIEW</span>
                <span v-if="msg.isFallback" class="bubble-meta">STATIC NOTE</span>
              </div>
            </article>
          </div>
        </section>
      </section>
    </div>
  </div>
</template>

<script setup>
import { computed, inject, ref, onMounted, nextTick } from 'vue'
import { getMessageList, addMessage, deleteMessage, replyMessage } from '../api/message'
import { fallbackMessages, isStaticMode } from '../config/site.config'
import { useUserStore } from '../stores/user'
import GiscusComments from '../components/GiscusComments.vue'

const userStore = useUserStore()
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
const siteLanguage = inject('siteLanguage', ref(localStorage.getItem('ethan-language') || 'zh'))

const ui = computed(() => siteLanguage.value === 'en'
  ? {
      badge: 'COMMUNICATION',
      title: 'Message Board',
      desc: 'Leave a note, idea, or link request.',
      uplink: 'NOTE UPLINK',
      githubMode: 'GITHUB DISCUSSION',
      syncing: 'SYNCING',
      ready: 'READY',
      staticIntro: 'This is a public note wall. After signing in with GitHub, your message will be attached to the repository Discussions.',
      staticHint: 'Good for suggestions, greetings, friend-link communication, and project feedback.',
      nickname: 'Nickname (optional)',
      contact: 'Contact (optional)',
      content: 'Write a note and pin it to the wall...',
      send: 'Pin Note',
      sending: 'Sending...',
      boardAria: 'Message note wall',
      loading: 'Syncing notes...',
      empty: 'This wall is quiet, waiting for the first note.',
      anonymous: 'Anonymous',
      reply: 'Reply',
      delete: 'Delete',
      replyPlaceholder: 'Write a reply...',
      cancel: 'Cancel',
      sendShort: 'Send',
      success: 'Note sent successfully.',
      localSaved: 'Backend is not connected. The note has been pinned locally for preview.',
      deleteConfirm: 'Delete this message?',
      replyRequired: 'Reply content cannot be empty',
    }
  : {
      badge: 'COMMUNICATION',
      title: '留言板',
      desc: '在这里留下一张便签、一个想法或一条建议',
      uplink: 'NOTE UPLINK',
      githubMode: 'GITHUB DISCUSSION',
      syncing: 'SYNCING',
      ready: 'READY',
      staticIntro: '这里是一面公开便签墙。使用 GitHub 账号登录后，你的留言会贴到仓库 Discussions 区。',
      staticHint: '适合留下建议、问候、友链沟通和项目反馈。',
      nickname: '昵称（选填）',
      contact: '联系方式（选填）',
      content: '写一张便签，贴到这面墙上...',
      send: '贴上便签',
      sending: '投递中...',
      boardAria: '留言便签墙',
      loading: '同步便签中...',
      empty: '这面墙还很安静，等第一张便签。',
      anonymous: '匿名用户',
      reply: '回复',
      delete: '删除',
      replyPlaceholder: '输入回复内容...',
      cancel: '取消',
      sendShort: '发送',
      success: '便签发送成功',
      localSaved: '后端暂未连接，便签已先贴到本地预览。',
      deleteConfirm: '确定删除这条留言吗？',
      replyRequired: '回复内容不能为空',
    })

const visibleMessages = computed(() => {
  if (!isStaticMode) return messages.value
  return fallbackMessages.map((message, index) => ({
    ...message,
    id: message.id || `fallback-${index}`,
    isFallback: true,
  }))
})

const useFallbackMessages = () => {
  messages.value = fallbackMessages.map(message => ({
    ...message,
    isFallback: true,
  }))
}

const loadMessages = async () => {
  loading.value = true
  if (isStaticMode) {
    useFallbackMessages()
    loading.value = false
    return
  }
  try {
    const res = await getMessageList(1, 50)
    if (res.code === 200) {
      messages.value = res.data?.records || res.data || []
    } else {
      useFallbackMessages()
    }
  } catch (e) {
    useFallbackMessages()
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
      nickname: nickname.value || ui.value.anonymous,
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
      submitMsg.value = `✓ ${ui.value.success}`
      submitOk.value = true
      await nextTick()
      scrollToBottom()
      setTimeout(() => { submitMsg.value = '' }, 3000)
    } else {
      const localMessage = createLocalMessage()
      messages.value.push(localMessage)
      content.value = ''
      submitMsg.value = ui.value.localSaved
      submitOk.value = true
      await nextTick()
      scrollToBottom()
      setTimeout(() => { submitMsg.value = '' }, 3000)
      if (import.meta.env.DEV) console.info('后端返回非成功状态', res.message || '未知错误')
      return
    }
  } catch (e) {
    const localMessage = createLocalMessage()
    messages.value.push(localMessage)
    content.value = ''
    submitMsg.value = ui.value.localSaved
    submitOk.value = true
    await nextTick()
    scrollToBottom()
    setTimeout(() => { submitMsg.value = '' }, 3000)
    if (import.meta.env.DEV) console.error('发送失败', e)
  } finally {
    sending.value = false
  }
}

const createLocalMessage = () => ({
  id: `local-${Date.now()}`,
  nickname: nickname.value || ui.value.anonymous,
  content: content.value.trim(),
  createTime: new Date().toISOString(),
  isLocal: true,
})

const noteRotation = (index) => {
  const rotations = ['-1.4deg', '0.9deg', '-0.5deg', '1.2deg', '-0.9deg', '0.4deg']
  return rotations[index % rotations.length]
}

const scrollToBottom = () => {
  if (messageListRef.value) {
    messageListRef.value.scrollTop = messageListRef.value.scrollHeight
  }
}


const formatTime = (s) => {
  if (!s) return ''
  // 后端返回 LocalDateTime 无时区信息，需按本地时间解析
  const source = /(?:Z|[+-]\d{2}:?\d{2})$/.test(s) ? s : `${s}+08:00`
  const d = new Date(source)
  if (isNaN(d.getTime())) return s
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

const handleDelete = async (id) => {
  if (!confirm(ui.value.deleteConfirm)) return
  if (String(id).startsWith('local-')) {
    messages.value = messages.value.filter(m => m.id !== id)
    return
  }
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
    alert(ui.value.replyRequired)
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
    radial-gradient(circle at 18% 30%, rgba(56, 248, 255, 0.06), transparent 34%),
    radial-gradient(circle at 82% 72%, rgba(124, 58, 237, 0.08), transparent 38%),
    linear-gradient(90deg, rgba(125, 211, 252, 0.025) 1px, transparent 1px) 0 0 / 58px 58px,
    linear-gradient(0deg, rgba(125, 211, 252, 0.02) 1px, transparent 1px) 0 0 / 58px 58px;
}

.message-container {
  position: relative;
  z-index: 1;
  display: block !important;
  width: min(1180px, calc(100% - 40px)) !important;
  max-width: 1180px !important;
  margin: 0 auto !important;
}

.message-lab {
  position: relative;
  display: grid;
  grid-template-columns: minmax(280px, 0.72fr) minmax(420px, 1.28fr);
  gap: clamp(18px, 4vw, 34px);
  align-items: start;
  min-height: 620px;
  padding: clamp(10px, 2vw, 18px) 0 0;
}

.message-lab::before {
  content: '';
  position: absolute;
  inset: 34px 0 0;
  pointer-events: none;
  background:
    linear-gradient(100deg, transparent 0 34%, rgba(56, 248, 255, 0.12) 34.4% 34.7%, transparent 35% 100%),
    linear-gradient(32deg, transparent 0 64%, rgba(167, 139, 250, 0.1) 64.4% 64.7%, transparent 65% 100%),
    radial-gradient(circle, rgba(238, 247, 255, 0.28) 0 1px, transparent 1.6px) 0 0 / 92px 92px;
  opacity: 0.42;
}

.note-composer,
.note-board {
  position: relative;
  overflow: hidden;
}

.note-composer {
  align-self: start;
  padding: 22px;
  border: 1px solid rgba(125, 211, 252, 0.22);
  border-radius: 8px;
  background:
    linear-gradient(135deg, rgba(15, 27, 50, 0.86), rgba(6, 10, 22, 0.7)),
    rgba(8, 14, 27, 0.66);
  box-shadow: var(--shadow);
  backdrop-filter: blur(16px);
  clip-path: polygon(0 0, calc(100% - 18px) 0, 100% 18px, 100% 100%, 18px 100%, 0 calc(100% - 18px));
}

.composer-orbit {
  width: 126px;
  height: 126px;
  margin: 0 auto 18px;
  position: relative;
  display: grid;
  place-items: center;
  border-radius: 999px;
  border: 1px solid rgba(125, 211, 252, 0.22);
  box-shadow: inset 0 0 28px rgba(56, 248, 255, 0.08), 0 0 34px rgba(56, 248, 255, 0.08);
}

.composer-orbit::before,
.composer-orbit::after {
  content: '';
  position: absolute;
  inset: 16px;
  border-radius: inherit;
  border: 1px dashed rgba(167, 139, 250, 0.34);
  animation: noteSpin 9s linear infinite;
}

.composer-orbit::after {
  inset: 35px;
  border-color: rgba(56, 248, 255, 0.42);
  animation-duration: 5.5s;
  animation-direction: reverse;
}

.composer-orbit span {
  position: absolute;
  width: 8px;
  height: 8px;
  border-radius: 999px;
  background: #e0f7ff;
  box-shadow: 0 0 16px rgba(56, 248, 255, 0.76);
}

.composer-orbit span:nth-child(1) { transform: translate(44px, -28px); }
.composer-orbit span:nth-child(2) { transform: translate(-46px, 18px); background: #a78bfa; }
.composer-orbit span:nth-child(3) { transform: translate(8px, 48px); background: #ffbd66; }

.composer-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 18px;
  color: rgba(226, 239, 255, 0.68);
  font: 800 11px/1.4 'SF Mono', 'Consolas', monospace;
  letter-spacing: 0.14em;
}

.composer-head strong {
  color: #7dd3fc;
}

.input-form {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.form-input,
.form-textarea {
  width: 100%;
  border: 1px solid rgba(125, 211, 252, 0.16);
  border-radius: 8px;
  background: rgba(5, 8, 22, 0.48);
  color: #fff;
  font: 500 13px/1.6 inherit;
  outline: none;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.form-input {
  min-height: 44px;
  padding: 0 14px;
}

.form-textarea {
  min-height: 116px;
  padding: 12px 14px;
  resize: vertical;
}

.form-input:focus,
.form-textarea:focus {
  border-color: rgba(56, 248, 255, 0.48);
  box-shadow: 0 0 0 4px rgba(56, 248, 255, 0.08);
}

.send-btn {
  align-self: flex-end;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  min-height: 44px;
  padding: 0 20px;
  border: 1px solid rgba(56, 248, 255, 0.42);
  border-radius: 8px;
  background: linear-gradient(135deg, rgba(56, 248, 255, 0.2), rgba(124, 58, 237, 0.22));
  color: #fff;
  font-size: 13px;
  font-weight: 800;
  cursor: pointer;
  transition: transform 0.22s var(--ease-out), box-shadow 0.22s, opacity 0.22s;
}

.send-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 12px 28px rgba(56, 248, 255, 0.16);
}

.send-btn:disabled {
  opacity: 0.48;
  cursor: not-allowed;
}

.submit-msg {
  padding: 10px 12px;
  border-radius: 8px;
  border: 1px solid rgba(255, 100, 100, 0.28);
  background: rgba(255, 100, 100, 0.09);
  color: #ff9f9f;
  font-size: 13px;
}

.submit-msg.ok {
  border-color: rgba(56, 248, 255, 0.32);
  background: rgba(56, 248, 255, 0.1);
  color: #7dd3fc;
}

.note-board {
  min-height: 560px;
  max-height: 760px;
  overflow-y: auto;
  padding: 12px 4px 24px;
  border: 0;
  border-radius: 0;
  background: transparent;
  box-shadow: none;
  backdrop-filter: none;
}

.board-network {
  position: absolute;
  inset: 0;
  pointer-events: none;
  background:
    linear-gradient(115deg, transparent 0 44%, rgba(56, 248, 255, 0.12) 45%, transparent 46% 100%),
    linear-gradient(35deg, transparent 0 52%, rgba(167, 139, 250, 0.11) 53%, transparent 54% 100%),
    radial-gradient(circle, rgba(238, 247, 255, 0.36) 0 1px, transparent 1.5px) 0 0 / 90px 90px;
  opacity: 0.18;
}

.note-grid {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: clamp(14px, 2vw, 22px);
  align-items: start;
}

.message-note {
  position: relative;
  min-height: 188px;
  padding: 20px 18px 16px;
  border: 1px solid rgba(125, 211, 252, 0.22);
  border-radius: 8px;
  background:
    linear-gradient(145deg, rgba(18, 32, 58, 0.9), rgba(9, 15, 29, 0.82)),
    repeating-linear-gradient(180deg, transparent 0 31px, rgba(125, 211, 252, 0.05) 31px 32px);
  box-shadow: 0 18px 42px rgba(0, 0, 0, 0.26), inset 0 1px 0 rgba(255, 255, 255, 0.04);
  transform: rotate(var(--note-rotate, 0deg));
  transform-origin: 50% 10%;
  transition: transform 0.25s var(--ease-out), border-color 0.25s, box-shadow 0.25s;
  animation: noteIn 0.56s var(--ease-out) both;
}

.message-note:nth-child(3n + 2) {
  margin-top: 28px;
}

.message-note:nth-child(4n) {
  margin-top: 12px;
}

.message-note:hover {
  transform: translateY(-5px) rotate(0deg);
  border-color: rgba(56, 248, 255, 0.46);
  box-shadow: 0 22px 56px rgba(56, 248, 255, 0.12), 0 20px 52px rgba(0, 0, 0, 0.32);
}

.message-note.message-local {
  border-color: rgba(255, 189, 102, 0.26);
}

.note-pin {
  position: absolute;
  top: 10px;
  right: 14px;
  width: 10px;
  height: 10px;
  border-radius: 999px;
  background: #7dd3fc;
  box-shadow: 0 0 18px rgba(125, 211, 252, 0.72);
}

.bubble-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 12px;
}

.bubble-header-right {
  display: flex;
  align-items: center;
  gap: 7px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.bubble-name {
  color: #f8fbff;
  font-size: 14px;
  font-weight: 800;
}

.bubble-time {
  color: rgba(226, 239, 255, 0.44);
  font: 700 10px/1.5 'SF Mono', 'Consolas', monospace;
}

.bubble-content {
  color: rgba(226, 239, 255, 0.8);
  font-size: 14px;
  line-height: 1.78;
  word-break: break-word;
}

.bubble-footer {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-top: 14px;
  padding-top: 10px;
  border-top: 1px solid rgba(125, 211, 252, 0.11);
}

.bubble-meta {
  color: rgba(125, 211, 252, 0.58);
  font: 800 10px/1 'SF Mono', 'Consolas', monospace;
}

.msg-btn,
.reply-submit,
.reply-cancel {
  border-radius: 6px;
  cursor: pointer;
  transition: border-color 0.2s, color 0.2s, background 0.2s;
}

.msg-btn {
  padding: 3px 8px;
  border: 1px solid rgba(125, 211, 252, 0.16);
  background: rgba(5, 8, 22, 0.38);
  color: rgba(226, 239, 255, 0.5);
  font-size: 11px;
}

.msg-btn:hover {
  border-color: rgba(56, 248, 255, 0.48);
  color: #eaf7ff;
}

.del-msg-btn {
  border-color: rgba(255, 100, 100, 0.28);
  color: #ff9f9f;
}

.sub-replies,
.reply-form {
  margin-top: 12px;
  padding: 10px;
  border: 1px solid rgba(125, 211, 252, 0.14);
  border-radius: 8px;
  background: rgba(5, 8, 22, 0.38);
}

.sub-reply-bubble {
  color: rgba(226, 239, 255, 0.7);
  font-size: 13px;
  line-height: 1.6;
}

.sub-reply-name {
  color: #7dd3fc;
  font-weight: 800;
}

.reply-input {
  width: 100%;
  padding: 8px 10px;
  border: 1px solid rgba(125, 211, 252, 0.16);
  border-radius: 7px;
  background: rgba(0, 0, 0, 0.26);
  color: #fff;
  resize: vertical;
}

.reply-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 8px;
}

.reply-submit {
  padding: 5px 14px;
  border: 0;
  background: #38f8ff;
  color: #06101f;
  font-weight: 800;
}

.reply-cancel {
  padding: 5px 14px;
  border: 1px solid rgba(125, 211, 252, 0.18);
  background: transparent;
  color: rgba(226, 239, 255, 0.56);
}

.note-state {
  position: relative;
  z-index: 1;
  min-height: 260px;
  display: grid;
  place-items: center;
  color: rgba(226, 239, 255, 0.56);
  font-weight: 700;
}

.giscus-intro {
  margin-bottom: 18px;
  padding: 14px;
  border: 1px solid rgba(125, 211, 252, 0.16);
  border-radius: 8px;
  background: rgba(5, 8, 22, 0.34);
}

.giscus-intro p {
  margin: 0 0 6px;
  color: rgba(226, 239, 255, 0.72);
  font-size: 13px;
  line-height: 1.7;
}

.giscus-intro p:last-child {
  margin-bottom: 0;
}

.giscus-hint {
  color: rgba(226, 239, 255, 0.46) !important;
}

@keyframes noteSpin {
  to { transform: rotate(360deg); }
}

@keyframes noteIn {
  from { opacity: 0; transform: translateY(18px) rotate(var(--note-rotate, 0deg)); filter: blur(6px); }
  to { opacity: 1; transform: translateY(0) rotate(var(--note-rotate, 0deg)); filter: blur(0); }
}

@media (max-width: 900px) {
  .message-lab {
    grid-template-columns: 1fr;
  }

  .note-board {
    max-height: none;
  }
}

@media (max-width: 768px) {
  .message-page {
    padding: 72px 16px 80px;
  }

  .note-composer,
  .note-board {
    padding: 18px;
  }

  .note-grid {
    grid-template-columns: 1fr;
  }
}
</style>
