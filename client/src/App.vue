<template>
  <div class="souta-app" @click="dropCandy" @touchstart="dropCandy">
    <div class="ambient-layer" aria-hidden="true">
      <span class="ambient-line line-a"></span>
      <span class="ambient-line line-b"></span>
      <span class="ambient-stamp">保持热爱<br />奔赴山海</span>
    </div>

    <!-- ✨ 特效容器 -->
    <div class="candy-container">
      <span
        v-for="candy in candies"
        :key="candy.id"
        class="candy"
        :style="{
          left: candy.x + 'px',
          top: candy.y + 'px',
          animationDuration: candy.dur + 's',
          animationDelay: candy.delay + 's',
          fontSize: candy.size + 'px',
          opacity: candy.opacity,
          color: candy.color,
          '--dx': candy.dx + 'px',
          '--dy': candy.dy + 'px',
          '--rot': candy.rot + 'deg'
        }"
      >{{ candy.emoji }}</span>
    </div>

    <!-- ✅ 顶部固定导航 - 添加 @click.stop 防止触发特效 -->
    <header class="souta-nav" @click.stop @touchstart.stop>
      <nav class="souta-nav-inner" @click.stop @touchstart.stop>
        <router-link
          v-for="link in navLinks"
          :key="link.path"
          :to="link.path"
          class="nav-item"
          :class="{ active: isActive(link.path) }"
        >
          <span class="nav-icon" aria-hidden="true">{{ link.icon }}</span>
          <span>{{ link.name }}</span>
        </router-link>

        <!-- 已登录显示写文章入口 -->
        <router-link
          v-if="userStore.isLoggedIn"
          to="/write"
          class="nav-item nav-write"
          :class="{ active: isActive('/write') }"
        >写文章</router-link>

        <!-- 退出登录（仅已登录显示） -->
        <span
          v-if="userStore.isLoggedIn"
          class="nav-item nav-logout"
          @click="handleLogout"
        >退出</span>
      </nav>
    </header>

    <!-- 页面内容区 -->
    <main class="souta-main">
      <router-view v-slot="{ Component }">
        <transition name="page" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <!-- 页脚 -->
    <footer class="souta-footer">
      <p>Copyright © 2019-2026 Ethan All Rights Reserved.</p>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useRoute } from 'vue-router'
import { useUserStore } from './stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const handleLogout = () => {
  if (!confirm('确定要退出登录吗？')) return
  userStore.logout()
  router.push('/login')
}

const candies = ref([])
let candyId = 0

const navLinks = [
  { name: '首页', path: '/', icon: '⌂' },
  { name: '文章', path: '/posts', icon: '☷' },
  { name: '项目', path: '/projects', icon: '◇' },
  { name: '友链', path: '/friends', icon: '∞' },
  { name: '留言板', path: '/message', icon: '✎' },
  { name: '关于', path: '/about', icon: '○' },
]

const isActive = (path) => {
  if (path === '/') return route.path === '/'
  return route.path.startsWith(path)
}

// ✨ 萤火虫/金粉特效配置
// 使用微小符号和星星，配合暖金色系
const DUST_EMOJIS = ['✦', '·', '•', '∗']

const dropCandy = (e) => {
  // ✅ 排除导航、按钮、链接、输入框等交互元素
  const excludeSelectors = [
    '.souta-nav',
    '.nav-item',
    'button',
    'a',
    'input',
    'textarea',
    '.photo-wrapper',
    '.social-btn',
    '[contenteditable]',
    '.hero-wave',
    'router-link'
  ]
  
  if (excludeSelectors.some(sel => e.target.closest(sel))) {
    return
  }
  
  // 获取点击坐标（兼容触摸事件）
  const clientX = e.clientX || (e.touches && e.touches[0]?.clientX)
  const clientY = e.clientY || (e.touches && e.touches[0]?.clientY)
  if (!clientX || !clientY) return
  
  // 点击产生 5-9 个微粒
  const count = Math.floor(Math.random() * 5) + 5
  
  for (let i = 0; i < count; i++) {
    const id = ++candyId
    const x = clientX + (Math.random() - 0.5) * 26
    const y = clientY + (Math.random() - 0.5) * 18
    const dx = (Math.random() - 0.5) * 130
    const dy = -48 - Math.random() * 110
    const rot = (Math.random() - 0.5) * 220
    
    // 🎨 随机颜色：使用与你博客主题匹配的金色/琥珀色系
    // HSL: 色相40-55(金色/琥珀色), 饱和度80%, 亮度50-80%
    const hue = 40 + Math.random() * 15
    const lightness = 50 + Math.random() * 30
    const color = `hsl(${hue}, 80%, ${lightness}%)`
    
    const dur = 0.95 + Math.random() * 0.75
    const delay = Math.random() * 0.08
    const size = 6 + Math.random() * 10
    const opacity = 0.45 + Math.random() * 0.55
    
    // 随机选择 Emoji
    const emoji = DUST_EMOJIS[Math.floor(Math.random() * DUST_EMOJIS.length)]
    
    candies.value.push({ id, x, y, dx, dy, rot, emoji, color, dur, delay, size, opacity })
  }
  
  // 性能优化：限制最大数量
  if (candies.value.length > 60) {
    candies.value = candies.value.slice(-40)
  }
  
  // 自动清理过期微粒
  setTimeout(() => {
    candies.value = candies.value.filter(c => c.id > candyId - 40)
  }, 1800)
}

onMounted(() => {
  setTimeout(() => {
    dropCandy({
      clientX: window.innerWidth / 2,
      clientY: Math.min(window.innerHeight * 0.42, 380),
      target: { closest: () => null }
    })
  }, 650)
})
</script>

<style scoped>
/* ================================================
   souta.cc 风格 App.vue - 萤火虫金粉特效版
   ================================================ */

.souta-app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow-x: hidden;
}

.ambient-layer {
  position: fixed;
  inset: 0;
  z-index: 0;
  pointer-events: none;
  overflow: hidden;
}

.ambient-line {
  position: absolute;
  display: block;
  width: 240px;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(153, 97, 22, 0.28), transparent);
  transform-origin: center;
}

.line-a {
  --r: -7deg;
  top: 20%;
  right: 7%;
  transform: rotate(-7deg);
  animation: lineDrift 8s ease-in-out infinite;
}

.line-b {
  --r: 4deg;
  left: 6%;
  bottom: 18%;
  transform: rotate(4deg);
  animation: lineDrift 9s ease-in-out 0.8s infinite reverse;
}

.ambient-stamp {
  position: absolute;
  top: 18%;
  right: 4.5%;
  padding: 10px 8px;
  border: 1px solid rgba(153, 97, 22, 0.28);
  border-radius: 8px;
  color: rgba(153, 97, 22, 0.46);
  font-family: var(--font-serif);
  font-size: 14px;
  line-height: 1.55;
  writing-mode: vertical-rl;
  letter-spacing: 2px;
}

@keyframes lineDrift {
  0%, 100% { opacity: 0.35; transform: translateY(0) rotate(var(--r, -7deg)); }
  50% { opacity: 0.75; transform: translateY(12px) rotate(var(--r, -7deg)); }
}

/* ✨ 特效容器 */
.candy-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 9999;
  overflow: hidden;
}

/* ✨ 微粒基础样式 */
.candy {
  position: absolute;
  pointer-events: none;
  user-select: none;
  will-change: transform, opacity;
  font-weight: bold;
  filter: blur(0.15px) drop-shadow(0 0 5px rgba(255, 190, 50, 0.55));
  animation-fill-mode: forwards;
}

@keyframes dustBurst {
  0% {
    transform: translate3d(0, 0, 0) scale(0.3) rotate(0deg);
    opacity: 0;
  }
  18% {
    opacity: 1;
  }
  100% {
    transform: translate3d(var(--dx), var(--dy), 0) scale(0.9) rotate(var(--rot));
    opacity: 0;
  }
}

.candy {
  animation-name: dustBurst;
  animation-timing-function: var(--ease-out);
}

/* ================================================
   导航栏样式
   ================================================ */
.souta-nav {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  display: flex;
  justify-content: center;
  padding: 14px 16px;
}

.souta-nav-inner {
  display: flex;
  align-items: center;
  gap: 6px;
  max-width: min(100%, 820px);
  padding: 8px 10px;
  border: 1px solid rgba(85, 71, 54, 0.14);
  border-radius: 999px;
  background: rgba(255, 250, 241, 0.72);
  backdrop-filter: blur(18px) saturate(1.25);
  -webkit-backdrop-filter: blur(18px) saturate(1.25);
  box-shadow: 0 18px 40px rgba(88, 66, 38, 0.12);
}

.nav-item {
  position: relative;
  display: inline-flex;
  align-items: center;
  gap: 7px;
  padding: 8px 15px;
  font-size: 14px;
  font-weight: 500;
  color: var(--text-light, #666);
  border-radius: 999px;
  transition: color 0.22s, background 0.22s, transform 0.22s var(--ease-out);
  cursor: pointer;
  user-select: none;
  text-decoration: none;
  white-space: nowrap;
}

.nav-icon {
  color: var(--accent-dark);
  font-size: 13px;
  line-height: 1;
  opacity: 0.72;
}

.nav-item:hover {
  color: var(--text, #333);
  background: rgba(201, 133, 36, 0.1);
  transform: translateY(-1px);
}

.nav-item.active {
  background: rgba(41, 36, 29, 0.93);
  color: #fffaf1;
  box-shadow: 0 10px 24px rgba(41, 36, 29, 0.22);
}

.nav-item.active .nav-icon {
  color: var(--accent-soft);
  opacity: 1;
}

/* 写文章按钮高亮 */
.nav-write {
  color: var(--accent-dark, #d97706);
  font-weight: 500;
}

.nav-write:hover {
  color: var(--accent-dark, #d97706);
  background: var(--accent-dim, #fef3c7);
}

.nav-write.active {
  background: var(--accent-dark, #d97706);
  color: #fff;
}

/* 退出登录按钮 */
.nav-logout {
  color: #999;
  font-size: 13px;
}

.nav-logout:hover {
  color: #e74c3c;
  background: rgba(231, 76, 60, 0.06);
}

/* 登录按钮 */
.nav-login {
  color: var(--accent-dark, #d97706);
  font-weight: 500;
}

.nav-login:hover {
  color: var(--accent-dark, #d97706);
  background: var(--accent-dim, #fef3c7);
}

/* ================================================
   主内容区 & 页脚
   ================================================ */
.souta-main {
  flex: 1;
  position: relative;
  z-index: 1;
  padding-top: 78px;
}

.souta-footer {
  position: relative;
  z-index: 1;
  padding: 28px 0;
  text-align: center;
  font-size: 13px;
  color: var(--text-lighter, #999);
  border-top: 1px solid var(--border, #eee);
  margin-top: 40px;
}

.souta-footer p {
  letter-spacing: 0.3px;
}

/* 页面过渡动画 */
.page-enter-active,
.page-leave-active {
  transition: opacity 0.34s var(--ease-out), transform 0.34s var(--ease-out), filter 0.34s var(--ease-out);
}

.page-enter-from {
  opacity: 0;
  transform: translateY(16px);
  filter: blur(4px);
}

.page-leave-to {
  opacity: 0;
  transform: translateY(-10px);
  filter: blur(3px);
}

/* 响应式 */
@media (max-width: 768px) {
  .souta-nav {
    align-items: flex-start;
    overflow-x: auto;
    justify-content: flex-start;
    scrollbar-width: none;
  }

  .souta-nav::-webkit-scrollbar {
    display: none;
  }

  .souta-nav-inner {
    gap: 3px;
    padding: 6px;
    min-width: max-content;
  }
  
  .nav-item {
    padding: 7px 11px;
    font-size: 12px;
  }

  .nav-icon {
    display: none;
  }
  
  .souta-main {
    padding-top: 68px;
  }

  .ambient-stamp {
    display: none;
  }
}

/* 深色模式适配 */
@media (prefers-color-scheme: dark) {
  .souta-nav-inner {
    background: rgba(30, 30, 30, 0.85);
    border-color: rgba(255, 255, 255, 0.1);
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3);
  }
  
  .nav-item:hover {
    background: rgba(255, 255, 255, 0.08);
  }
}

@media (prefers-reduced-motion: reduce) {
  .candy,
  .ambient-line,
  .page-enter-active,
  .page-leave-active {
    animation: none !important;
    transition: none !important;
  }
}
</style>
