<template>
  <aside class="ai-guide" :class="{ open: isOpen }" aria-label="Ethan AI Guide" @click.stop @touchstart.stop>
    <transition name="guide-panel">
      <section v-if="isOpen" class="guide-panel">
        <div class="guide-panel-bg" aria-hidden="true"></div>
        <div class="guide-head">
          <div class="guide-portrait">
            <img :src="avatarUrl" alt="Ethan digital avatar" />
            <span class="portrait-scan" aria-hidden="true"></span>
          </div>
          <div>
            <span class="guide-code">{{ copy.code }}</span>
            <h2>{{ copy.title }}</h2>
            <p>{{ copy.subtitle }}</p>
          </div>
          <button class="guide-close" type="button" :aria-label="copy.close" @click="isOpen = false">
            <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M6 6l12 12M18 6 6 18" /></svg>
          </button>
        </div>

        <div class="guide-answer">
          <span class="answer-label">{{ copy.answerLabel }}</span>
          <p>{{ activePrompt.answer }}</p>
        </div>

        <div class="guide-prompts" :aria-label="copy.promptLabel">
          <button
            v-for="item in prompts"
            :key="item.id"
            type="button"
            :class="{ active: activePromptId === item.id }"
            @click="activePromptId = item.id"
          >
            {{ item.question }}
          </button>
        </div>

        <div v-if="chatMessages.length" ref="chatLogRef" class="guide-chat-log" :aria-label="copy.chatLabel">
          <div
            v-for="(message, index) in chatMessages"
            :key="`${message.role}-${index}`"
            class="guide-chat-bubble"
            :class="message.role"
          >
            {{ message.content }}
          </div>
        </div>

        <form class="guide-chat" :aria-label="copy.chatLabel" @submit.prevent="sendChat">
          <input
            v-model="chatInput"
            type="text"
            :placeholder="copy.chatPlaceholder"
            maxlength="180"
          />
          <button type="submit" :disabled="!chatInput.trim() || chatLoading">
            {{ chatLoading ? copy.thinking : copy.send }}
          </button>
        </form>

        <p class="guide-mode">{{ remoteChatEnabled ? copy.realMode : copy.localMode }}</p>

        <div class="guide-actions" :aria-label="copy.actionLabel">
          <button
            v-for="action in actions"
            :key="action.to"
            type="button"
            :class="{ active: route.path === action.to }"
            @click="go(action.to)"
          >
            <span class="action-mark">{{ action.mark }}</span>
            <span>
              <strong>{{ action.label }}</strong>
              <small>{{ action.desc }}</small>
            </span>
          </button>
        </div>
      </section>
    </transition>

    <button
      class="guide-orb"
      type="button"
      :aria-label="isOpen ? copy.close : copy.open"
      :aria-expanded="isOpen"
      @click="isOpen = !isOpen"
    >
      <span class="orb-radar" aria-hidden="true"></span>
      <img :src="avatarUrl" alt="" />
      <span class="orb-status" aria-hidden="true"></span>
      <span class="orb-text">AI</span>
    </button>
  </aside>
</template>

<script setup>
import { computed, inject, nextTick, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { resolveAssetUrl } from '../config/site.config'

const route = useRoute()
const router = useRouter()
const siteLanguage = inject('siteLanguage', ref(localStorage.getItem('ethan-language') || 'zh'))
const isOpen = ref(false)
const activePromptId = ref('intro')
const chatInput = ref('')
const chatLoading = ref(false)
const chatMessages = ref([])
const chatLogRef = ref(null)
const avatarUrl = resolveAssetUrl('/photos/ethan-digital-avatar-v1.png')
const configuredChatEndpoint = (import.meta.env.VITE_AI_CHAT_ENDPOINT || '').trim()
const chatEndpoint = configuredChatEndpoint || '/api/ai/chat'
const remoteChatEnabled = computed(() => Boolean(configuredChatEndpoint))

const copy = computed(() => siteLanguage.value === 'en'
  ? {
      title: 'Ethan AI Guide',
      code: remoteChatEnabled.value ? 'ETHAN-AI / ONLINE' : 'ETHAN-AI / LOCAL GUIDE',
      subtitle: 'A lightweight digital guide for projects, articles, links, and messages.',
      answerLabel: 'Response',
      promptLabel: 'Preset questions',
      actionLabel: 'Quick navigation',
      chatLabel: 'Ask Ethan AI',
      chatPlaceholder: 'Ask about posts, projects, links...',
      send: 'Send',
      thinking: 'Thinking',
      realMode: 'Real AI endpoint enabled.',
      localMode: 'Local guide mode. Stable site navigation is available now.',
      open: 'Open Ethan AI Guide',
      close: 'Close Ethan AI Guide',
    }
  : {
      title: 'Ethan 数字分身',
      code: remoteChatEnabled.value ? 'ETHAN-AI / ONLINE' : 'ETHAN-AI / 本地导览',
      subtitle: '负责带你快速进入文章、项目、友链申请和留言入口。',
      answerLabel: '应答',
      promptLabel: '预设问题',
      actionLabel: '快捷导航',
      chatLabel: '询问 Ethan AI',
      chatPlaceholder: '问文章、项目、友链或博客结构...',
      send: '发送',
      thinking: '思考中',
      realMode: '真实 AI 接口已启用。',
      localMode: '当前为本地导览模式，先稳定负责站内问答与跳转。',
      open: '打开 Ethan 数字分身',
      close: '关闭 Ethan 数字分身',
    })

const prompts = computed(() => siteLanguage.value === 'en'
  ? [
      {
        id: 'intro',
        question: 'Who are you?',
        answer: 'I am the lightweight Ethan AI Guide. This version runs fully on the static site and helps visitors understand the blog structure first.',
      },
      {
        id: 'route',
        question: 'Where should I start?',
        answer: 'Start with Projects to see what Ethan has built, then read Posts for learning notes and project writeups.',
      },
      {
        id: 'friend',
        question: 'How do I apply for a link?',
        answer: 'Open Friends and submit the link application form. Ethan can review it after logging in as admin.',
      },
    ]
  : [
      {
        id: 'intro',
        question: '你是谁？',
        answer: '我是 Ethan 智域的轻量数字分身。当前版本不依赖真实 AI 后端，先负责导览、问答和页面跳转，稳定适合静态部署。',
      },
      {
        id: 'route',
        question: '先看哪里？',
        answer: '建议先看项目矩阵，了解博客、后端和 AI/NLP 方向；再看文章归档，补充学习路线和项目复盘。',
      },
      {
        id: 'friend',
        question: '怎么申请友链？',
        answer: '进入友链页后点击申请友链，填写站点名称、链接、头像和简介。登录管理员账号后可以审核通过或继续编辑。',
      },
    ])

const actions = computed(() => siteLanguage.value === 'en'
  ? [
      { mark: '01', label: 'Projects', desc: 'Built work', to: '/projects' },
      { mark: '02', label: 'Posts', desc: 'Notes archive', to: '/posts' },
      { mark: '03', label: 'Friends', desc: 'Link apply', to: '/friends' },
      { mark: '04', label: 'Message', desc: 'Leave a note', to: '/message' },
      { mark: '05', label: 'About', desc: 'Profile', to: '/about' },
    ]
  : [
      { mark: '01', label: '项目矩阵', desc: '查看作品', to: '/projects' },
      { mark: '02', label: '文章归档', desc: '学习记录', to: '/posts' },
      { mark: '03', label: '申请友链', desc: '提交站点', to: '/friends' },
      { mark: '04', label: '留言板', desc: '留下便签', to: '/message' },
      { mark: '05', label: '关于 Ethan', desc: '个人档案', to: '/about' },
    ])

const activePrompt = computed(() => prompts.value.find(item => item.id === activePromptId.value) || prompts.value[0])

const scrollChatToBottom = async () => {
  await nextTick()
  if (chatLogRef.value) {
    chatLogRef.value.scrollTop = chatLogRef.value.scrollHeight
  }
}

const localGuideAnswer = (question = '') => {
  const normalized = question.toLowerCase()
  const isEn = siteLanguage.value === 'en'

  if (normalized.includes('friend') || normalized.includes('link') || question.includes('友链')) {
    return isEn
      ? 'Open Friends, submit the application form, then Ethan can review it from the admin side.'
      : '进入友链页点击申请友链，填写站点名称、链接、头像和简介。管理员登录后可以审核，通过后会显示在友链页。'
  }

  if (normalized.includes('project') || normalized.includes('nlp') || normalized.includes('ai') || question.includes('项目') || question.includes('人工智能')) {
    return isEn
      ? 'Start with Projects. The current focus is a portfolio blog, backend practice, and NLP or AI engineering work.'
      : '可以先看项目矩阵：这里会集中展示博客、后端实践、NLP/AI 工程和后续毕业设计相关沉淀。'
  }

  if (normalized.includes('post') || normalized.includes('article') || question.includes('文章') || question.includes('学习')) {
    return isEn
      ? 'Posts are the learning archive: frontend, backend, AI notes, project writeups, and review materials.'
      : '文章归档适合看学习路线、技术笔记、项目复盘和后续复试/论文准备材料。'
  }

  if (normalized.includes('open') || normalized.includes('source') || question.includes('开源')) {
    return isEn
      ? 'The blog can be public as a portfolio. Graduation-project code should stay private until the thesis and defense are stable.'
      : '博客适合公开展示；毕业设计项目还在论文和开发阶段，建议先保持私有，等答辩和核心实验稳定后再考虑开源。'
  }

  return isEn
    ? activePrompt.value.answer
    : '我现在是本地导览模式，可以回答博客结构、文章、项目、友链和留言相关问题。接入真实 AI 后，就可以根据站内内容做更自然的连续对话。'
}

const sendChat = async () => {
  const question = chatInput.value.trim()
  if (!question || chatLoading.value) return

  chatMessages.value.push({ role: 'user', content: question })
  chatInput.value = ''
  chatLoading.value = true
  await scrollChatToBottom()

  try {
    let answer = ''
    if (remoteChatEnabled.value) {
      const response = await fetch(chatEndpoint, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          question,
          locale: siteLanguage.value,
          messages: chatMessages.value.slice(-8),
          page: route.path,
        }),
      })
      if (!response.ok) throw new Error(`AI endpoint failed: ${response.status}`)
      const data = await response.json()
      answer = data.answer || data.message || data.output_text || ''
    }
    chatMessages.value.push({ role: 'assistant', content: answer || localGuideAnswer(question) })
  } catch (error) {
    if (import.meta.env.DEV) console.info('AI guide fallback', error?.message || error)
    chatMessages.value.push({ role: 'assistant', content: localGuideAnswer(question) })
  } finally {
    chatLoading.value = false
    await scrollChatToBottom()
  }
}

const go = async (to) => {
  await router.push(to)
  isOpen.value = false
}
</script>

<style scoped>
.ai-guide {
  position: fixed;
  right: clamp(14px, 3vw, 28px);
  bottom: calc(18px + env(safe-area-inset-bottom));
  z-index: 160;
  pointer-events: none;
}

.guide-panel,
.guide-orb {
  pointer-events: auto;
}

.guide-panel {
  position: absolute;
  z-index: 2;
  right: 0;
  bottom: 92px;
  width: min(440px, calc(100vw - 28px));
  overflow: hidden;
  isolation: isolate;
  border: 0;
  border-radius: 8px;
  background:
    linear-gradient(135deg, rgba(255, 255, 255, 0.035), rgba(255, 255, 255, 0.01)),
    linear-gradient(135deg, rgba(10, 20, 42, 0.76), rgba(3, 7, 18, 0.82)),
    repeating-linear-gradient(90deg, rgba(147, 197, 253, 0.05) 0 1px, transparent 1px 64px);
  background-blend-mode: luminosity, normal, normal;
  box-shadow: 0 30px 96px rgba(0, 0, 0, 0.52), inset 0 1px 1px rgba(255, 255, 255, 0.1), inset 0 0 80px rgba(96, 165, 250, 0.06);
  backdrop-filter: blur(18px) saturate(1.22);
  -webkit-backdrop-filter: blur(18px) saturate(1.22);
}

.guide-panel::before {
  content: '';
  position: absolute;
  inset: 0;
  z-index: 2;
  border-radius: inherit;
  padding: 1.4px;
  background: linear-gradient(
    180deg,
    rgba(255,255,255,0.45) 0%,
    rgba(255,255,255,0.15) 20%,
    rgba(255,255,255,0) 40%,
    rgba(255,255,255,0) 60%,
    rgba(255,255,255,0.15) 80%,
    rgba(255,255,255,0.45) 100%
  );
  -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  pointer-events: none;
}

.guide-panel::after {
  content: '';
  position: absolute;
  inset: -40% -20% auto;
  z-index: 1;
  height: 42%;
  pointer-events: none;
  background:
    linear-gradient(180deg, transparent, rgba(96, 165, 250, 0.16), transparent),
    repeating-linear-gradient(90deg, rgba(255, 255, 255, 0.075) 0 1px, transparent 1px 12px);
  opacity: 0.48;
  transform: rotate(-7deg);
  animation: guidePanelScan 5.4s ease-in-out infinite;
  mix-blend-mode: screen;
}

.guide-panel-bg {
  position: absolute;
  inset: 0;
  z-index: 0;
  pointer-events: none;
  background:
    radial-gradient(circle at 20% 10%, rgba(96, 165, 250, 0.24), transparent 34%),
    radial-gradient(circle at 88% 42%, rgba(167, 139, 250, 0.18), transparent 32%),
    linear-gradient(120deg, transparent 0 44%, rgba(147, 197, 253, 0.08) 48%, transparent 56%);
  opacity: 0.9;
}

.guide-head,
.guide-answer,
.guide-prompts,
.guide-chat-log,
.guide-chat,
.guide-mode,
.guide-actions {
  position: relative;
  z-index: 3;
}

.guide-head {
  display: grid;
  grid-template-columns: 76px minmax(0, 1fr) 34px;
  gap: 13px;
  align-items: center;
  padding: 17px;
  border-bottom: 1px solid rgba(147, 197, 253, 0.16);
}

.guide-portrait {
  position: relative;
  width: 76px;
  height: 92px;
  overflow: hidden;
  border: 1px solid rgba(147, 197, 253, 0.34);
  border-radius: 8px;
  background: rgba(5, 8, 22, 0.6);
  box-shadow: 0 0 28px rgba(96, 165, 250, 0.14);
}

.guide-portrait::before,
.guide-portrait::after {
  content: '';
  position: absolute;
  inset: 8px;
  z-index: 2;
  border: 1px solid rgba(147, 197, 253, 0.2);
  border-radius: 999px;
  pointer-events: none;
  animation: radarSpin 8s linear infinite;
}

.guide-portrait::after {
  inset: 17px 7px;
  border-style: dashed;
  border-color: rgba(167, 139, 250, 0.26);
  animation-duration: 5.6s;
  animation-direction: reverse;
}

.guide-portrait img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: 52% 18%;
  filter: saturate(1.05) contrast(1.05) brightness(0.92);
  transform: scale(1.02);
  animation: guideAvatarBreath 5.6s ease-in-out infinite;
}

.portrait-scan {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, transparent, rgba(147, 197, 253, 0.28), transparent);
  transform: translateY(-100%);
  animation: guideScan 3.6s ease-in-out infinite;
  mix-blend-mode: screen;
}

.guide-code {
  display: block;
  margin-bottom: 7px;
  color: rgba(191, 219, 254, 0.82);
  font: 800 10px/1 'SF Mono', 'Consolas', monospace;
  letter-spacing: 0.16em;
}

.guide-head h2 {
  margin: 0 0 6px;
  color: #f8fbff;
  font-size: 18px;
  line-height: 1.15;
}

.guide-head p {
  margin: 0;
  color: rgba(226, 239, 255, 0.62);
  font-size: 12px;
  line-height: 1.65;
}

.guide-close {
  width: 34px;
  height: 34px;
  display: grid;
  place-items: center;
  border: 1px solid rgba(147, 197, 253, 0.18);
  border-radius: 8px;
  background: rgba(8, 14, 27, 0.54);
  color: rgba(226, 239, 255, 0.72);
  cursor: pointer;
}

.guide-close svg {
  width: 17px;
  height: 17px;
  fill: none;
  stroke: currentColor;
  stroke-width: 2.2;
  stroke-linecap: round;
}

.guide-answer {
  margin: 14px 16px 12px;
  padding: 14px 15px;
  border: 1px solid rgba(96, 165, 250, 0.18);
  border-radius: 8px;
  background: rgba(5, 8, 22, 0.34);
  box-shadow: inset 0 0 28px rgba(96, 165, 250, 0.045);
}

.answer-label {
  display: block;
  margin-bottom: 6px;
  color: #93c5fd;
  font: 800 10px/1 'SF Mono', 'Consolas', monospace;
  letter-spacing: 0.14em;
}

.guide-answer p {
  margin: 0;
  color: rgba(238, 247, 255, 0.8);
  font-size: 13px;
  line-height: 1.8;
}

.guide-prompts {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding: 0 16px 12px;
  scrollbar-width: none;
}

.guide-prompts::-webkit-scrollbar {
  display: none;
}

.guide-prompts button {
  flex: 0 0 auto;
  min-height: 32px;
  padding: 0 10px;
  border: 1px solid rgba(147, 197, 253, 0.18);
  border-radius: 999px;
  background: rgba(8, 14, 27, 0.42);
  color: rgba(226, 239, 255, 0.72);
  font-size: 12px;
  cursor: pointer;
  transition: transform 0.22s var(--ease-out), border-color 0.22s, color 0.22s, background 0.22s;
}

.guide-prompts button:hover,
.guide-prompts button.active {
  transform: translateY(-1px);
  border-color: rgba(96, 165, 250, 0.52);
  color: #f8fbff;
  background: rgba(96, 165, 250, 0.1);
}

.guide-chat-log {
  display: flex;
  flex-direction: column;
  gap: 8px;
  max-height: 144px;
  margin: 0 16px 12px;
  padding: 10px;
  overflow-y: auto;
  border: 1px solid rgba(147, 197, 253, 0.14);
  border-radius: 8px;
  background: rgba(5, 8, 22, 0.28);
  scrollbar-width: thin;
  scrollbar-color: rgba(96, 165, 250, 0.4) transparent;
}

.guide-chat-bubble {
  max-width: 88%;
  padding: 8px 10px;
  border: 1px solid rgba(147, 197, 253, 0.14);
  border-radius: 10px;
  color: rgba(238, 247, 255, 0.84);
  font-size: 12px;
  line-height: 1.65;
  word-break: break-word;
}

.guide-chat-bubble.user {
  align-self: flex-end;
  border-color: rgba(96, 165, 250, 0.32);
  background: rgba(96, 165, 250, 0.14);
}

.guide-chat-bubble.assistant {
  align-self: flex-start;
  background: rgba(8, 14, 27, 0.62);
}

.guide-chat {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 70px;
  gap: 8px;
  padding: 0 16px 8px;
}

.guide-chat input,
.guide-chat button {
  min-height: 38px;
  border-radius: 999px;
  font-size: 12px;
  outline: none;
}

.guide-chat input {
  min-width: 0;
  border: 1px solid rgba(147, 197, 253, 0.16);
  background: rgba(5, 8, 22, 0.46);
  color: #f8fbff;
  padding: 0 12px;
}

.guide-chat input:focus {
  border-color: rgba(96, 165, 250, 0.5);
  box-shadow: 0 0 0 4px rgba(96, 165, 250, 0.08);
}

.guide-chat button {
  border: 1px solid rgba(96, 165, 250, 0.42);
  background: linear-gradient(135deg, rgba(96, 165, 250, 0.18), rgba(167, 139, 250, 0.18));
  color: #eef7ff;
  font-weight: 800;
  cursor: pointer;
  transition: transform 0.22s var(--ease-out), border-color 0.22s, opacity 0.22s;
}

.guide-chat button:hover:not(:disabled) {
  transform: translateY(-1px);
  border-color: rgba(147, 197, 253, 0.64);
}

.guide-chat button:disabled {
  opacity: 0.48;
  cursor: not-allowed;
}

.guide-mode {
  margin: 0 16px 12px;
  color: rgba(226, 239, 255, 0.4);
  font: 700 10px/1.5 'SF Mono', 'Consolas', monospace;
}

.guide-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 9px;
  padding: 0 16px 16px;
}

.guide-actions button {
  display: grid;
  grid-template-columns: 30px minmax(0, 1fr);
  gap: 9px;
  align-items: center;
  min-height: 56px;
  padding: 9px;
  border: 1px solid rgba(96, 165, 250, 0.16);
  border-radius: 8px;
  background: rgba(8, 14, 27, 0.38);
  color: rgba(238, 247, 255, 0.78);
  text-align: left;
  cursor: pointer;
  transition: transform 0.22s var(--ease-out), border-color 0.22s, box-shadow 0.22s;
}

.guide-actions button:hover,
.guide-actions button.active {
  transform: translateY(-2px);
  border-color: rgba(96, 165, 250, 0.5);
  box-shadow: 0 0 24px rgba(96, 165, 250, 0.1);
}

.action-mark {
  width: 30px;
  height: 30px;
  display: grid;
  place-items: center;
  border: 1px solid rgba(147, 197, 253, 0.22);
  border-radius: 8px;
  color: #93c5fd;
  background: rgba(96, 165, 250, 0.08);
  font: 800 10px/1 'SF Mono', 'Consolas', monospace;
}

.guide-actions strong,
.guide-actions small {
  display: block;
  min-width: 0;
}

.guide-actions strong {
  color: #f8fbff;
  font-size: 12px;
  line-height: 1.25;
}

.guide-actions small {
  margin-top: 3px;
  color: rgba(226, 239, 255, 0.48);
  font-size: 10px;
  line-height: 1.2;
}

.guide-orb {
  position: relative;
  z-index: 3;
  width: 78px;
  height: 78px;
  display: grid;
  place-items: center;
  overflow: visible;
  border: 1px solid rgba(147, 197, 253, 0.42);
  border-radius: 999px;
  background:
    conic-gradient(from 110deg, rgba(96, 165, 250, 0.28), rgba(167, 139, 250, 0.1), rgba(96, 165, 250, 0.28)),
    radial-gradient(circle at 50% 20%, rgba(147, 197, 253, 0.22), transparent 42%),
    rgba(5, 8, 22, 0.82);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.48), 0 0 38px rgba(96, 165, 250, 0.24);
  cursor: pointer;
  transition: transform 0.24s var(--ease-out), box-shadow 0.24s, border-color 0.24s;
}

.guide-orb:hover,
.ai-guide.open .guide-orb {
  transform: translateY(-3px);
  border-color: rgba(147, 197, 253, 0.68);
  box-shadow: 0 24px 70px rgba(0, 0, 0, 0.48), 0 0 44px rgba(96, 165, 250, 0.32);
}

.orb-radar {
  position: absolute;
  inset: -9px;
  border-radius: 999px;
  border: 1px dashed rgba(96, 165, 250, 0.38);
  animation: radarSpin 9s linear infinite;
}

.guide-orb img {
  width: 58px;
  height: 58px;
  border-radius: 999px;
  object-fit: cover;
  object-position: 52% 18%;
  filter: saturate(1.05) contrast(1.07) brightness(0.92);
}

.orb-status {
  position: absolute;
  right: 8px;
  top: 9px;
  width: 10px;
  height: 10px;
  border: 2px solid #08111f;
  border-radius: 999px;
  background: #60a5fa;
  box-shadow: 0 0 16px rgba(96, 165, 250, 0.9);
}

.orb-text {
  position: absolute;
  left: -3px;
  bottom: 3px;
  display: grid;
  place-items: center;
  min-width: 28px;
  height: 20px;
  border: 1px solid rgba(147, 197, 253, 0.38);
  border-radius: 999px;
  background: rgba(5, 8, 22, 0.88);
  color: #dbeafe;
  font: 900 10px/1 'SF Mono', 'Consolas', monospace;
  letter-spacing: 0.06em;
}

.guide-panel-enter-active,
.guide-panel-leave-active {
  transition: opacity 0.28s var(--ease-out), transform 0.28s var(--ease-out), filter 0.28s var(--ease-out);
}

.guide-panel-enter-from,
.guide-panel-leave-to {
  opacity: 0;
  transform: translateY(14px) scale(0.96);
  filter: blur(8px);
}

@keyframes guideScan {
  0%, 18% { transform: translateY(-100%); opacity: 0; }
  38%, 56% { opacity: 0.8; }
  82%, 100% { transform: translateY(100%); opacity: 0; }
}

@keyframes radarSpin {
  to { transform: rotate(360deg); }
}

@keyframes guidePanelScan {
  0% { transform: translateY(-60%) rotate(-7deg); opacity: 0; }
  20% { opacity: 0.5; }
  100% { transform: translateY(360%) rotate(-7deg); opacity: 0; }
}

@keyframes guideAvatarBreath {
  0%, 100% { transform: scale(1.02) translateY(0); filter: saturate(1.05) contrast(1.05) brightness(0.92); }
  50% { transform: scale(1.055) translateY(-2px); filter: saturate(1.16) contrast(1.08) brightness(0.98); }
}

@media (max-width: 640px) {
  .ai-guide {
    right: 10px;
    bottom: calc(12px + env(safe-area-inset-bottom));
  }

  .guide-panel {
    bottom: 86px;
    width: calc(100vw - 24px);
    background:
      linear-gradient(135deg, rgba(8, 17, 34, 0.98), rgba(3, 7, 18, 0.97)),
      repeating-linear-gradient(90deg, rgba(147, 197, 253, 0.055) 0 1px, transparent 1px 56px);
    box-shadow: 0 24px 90px rgba(0, 0, 0, 0.7), inset 0 0 90px rgba(96, 165, 250, 0.08);
  }

  .guide-head {
    grid-template-columns: 58px minmax(0, 1fr) 34px;
    padding: 14px;
  }

  .guide-portrait {
    width: 58px;
    height: 70px;
  }

  .guide-actions {
    grid-template-columns: 1fr;
  }

  .guide-orb {
    width: 64px;
    height: 64px;
  }

  .guide-orb img {
    width: 48px;
    height: 48px;
  }

  .orb-radar {
    inset: -7px;
  }

  .orb-text {
    min-width: 24px;
    height: 18px;
    font-size: 9px;
  }
}

@media (prefers-reduced-motion: reduce) {
  .portrait-scan,
  .guide-panel::after,
  .guide-portrait img,
  .guide-portrait::before,
  .guide-portrait::after,
  .orb-radar {
    animation: none !important;
  }

  .guide-panel-enter-active,
  .guide-panel-leave-active,
  .guide-orb,
  .guide-prompts button,
  .guide-chat button,
  .guide-actions button {
    transition: none !important;
  }
}
</style>
