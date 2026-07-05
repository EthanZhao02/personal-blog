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
            <span class="guide-code">ETHAN-AI / ONLINE</span>
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
import { computed, inject, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { resolveAssetUrl } from '../config/site.config'

const route = useRoute()
const router = useRouter()
const siteLanguage = inject('siteLanguage', ref(localStorage.getItem('ethan-language') || 'zh'))
const isOpen = ref(false)
const activePromptId = ref('intro')
const avatarUrl = resolveAssetUrl('/photos/ethan-digital-avatar-v1.png')

const copy = computed(() => siteLanguage.value === 'en'
  ? {
      title: 'Ethan AI Guide',
      subtitle: 'A lightweight digital guide for projects, articles, links, and messages.',
      answerLabel: 'Response',
      promptLabel: 'Preset questions',
      actionLabel: 'Quick navigation',
      open: 'Open Ethan AI Guide',
      close: 'Close Ethan AI Guide',
    }
  : {
      title: 'Ethan 数字分身',
      subtitle: '负责带你快速进入文章、项目、友链申请和留言入口。',
      answerLabel: '应答',
      promptLabel: '预设问题',
      actionLabel: '快捷导航',
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
  width: min(378px, calc(100vw - 28px));
  overflow: hidden;
  isolation: isolate;
  border: 1px solid rgba(96, 165, 250, 0.32);
  border-radius: 8px;
  background:
    linear-gradient(135deg, rgba(11, 22, 43, 0.94), rgba(4, 8, 20, 0.92)),
    repeating-linear-gradient(90deg, rgba(147, 197, 253, 0.05) 0 1px, transparent 1px 64px);
  box-shadow: 0 28px 88px rgba(0, 0, 0, 0.48), inset 0 0 80px rgba(96, 165, 250, 0.06);
  backdrop-filter: blur(18px) saturate(1.22);
}

.guide-panel-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
  background:
    radial-gradient(circle at 20% 10%, rgba(96, 165, 250, 0.22), transparent 34%),
    radial-gradient(circle at 88% 42%, rgba(167, 139, 250, 0.16), transparent 32%),
    linear-gradient(120deg, transparent 0 44%, rgba(147, 197, 253, 0.08) 48%, transparent 56%);
  opacity: 0.9;
}

.guide-head,
.guide-answer,
.guide-prompts,
.guide-actions {
  position: relative;
  z-index: 1;
}

.guide-head {
  display: grid;
  grid-template-columns: 72px minmax(0, 1fr) 34px;
  gap: 12px;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid rgba(147, 197, 253, 0.16);
}

.guide-portrait {
  position: relative;
  width: 72px;
  height: 86px;
  overflow: hidden;
  border: 1px solid rgba(147, 197, 253, 0.34);
  border-radius: 8px;
  background: rgba(5, 8, 22, 0.6);
  box-shadow: 0 0 28px rgba(96, 165, 250, 0.14);
}

.guide-portrait img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: 52% 18%;
  filter: saturate(1.05) contrast(1.05) brightness(0.92);
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
  color: rgba(147, 197, 253, 0.78);
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
  padding: 13px 14px;
  border: 1px solid rgba(96, 165, 250, 0.18);
  border-radius: 8px;
  background: rgba(5, 8, 22, 0.44);
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
  border-radius: 8px;
  background: rgba(8, 14, 27, 0.54);
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
  background: rgba(8, 14, 27, 0.48);
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
  width: 74px;
  height: 74px;
  display: grid;
  place-items: center;
  overflow: visible;
  border: 1px solid rgba(147, 197, 253, 0.42);
  border-radius: 999px;
  background:
    radial-gradient(circle at 50% 20%, rgba(147, 197, 253, 0.18), transparent 42%),
    rgba(5, 8, 22, 0.82);
  box-shadow: 0 18px 54px rgba(0, 0, 0, 0.44), 0 0 34px rgba(96, 165, 250, 0.2);
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
  width: 56px;
  height: 56px;
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

@media (max-width: 640px) {
  .ai-guide {
    right: 12px;
    bottom: calc(14px + env(safe-area-inset-bottom));
  }

  .guide-panel {
    bottom: 86px;
    width: calc(100vw - 24px);
    border-color: rgba(147, 197, 253, 0.42);
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
}

@media (prefers-reduced-motion: reduce) {
  .portrait-scan,
  .orb-radar {
    animation: none !important;
  }

  .guide-panel-enter-active,
  .guide-panel-leave-active,
  .guide-orb,
  .guide-prompts button,
  .guide-actions button {
    transition: none !important;
  }
}
</style>
