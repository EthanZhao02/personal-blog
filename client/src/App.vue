<template>
  <div class="souta-app" @click="dropCandy" @touchstart="dropCandy">
    <!-- ✨ 特效容器 -->
    <div class="candy-container" ref="candyRef">
      <span
        v-for="candy in candies"
        :key="candy.id"
        class="candy"
        :style="{
          left: candy.x + 'px',
          animationDuration: candy.dur + 's',
          animationDelay: candy.delay + 's',
          fontSize: candy.size + 'px',
          opacity: candy.opacity,
          color: candy.color
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
        >{{ link.name }}</router-link>

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

const candyRef = ref(null)
const candies = ref([])
let candyId = 0

const navLinks = [
  { name: '首页', path: '/' },
  { name: '文章', path: '/posts' },
  { name: '项目', path: '/projects' },
  { name: '友链', path: '/friends' },
  { name: '留言板', path: '/message' },
  { name: '关于', path: '/about' },
]

const isActive = (path) => {
  if (path === '/') return route.path === '/'
  return route.path.startsWith(path)
}

// ✨ 萤火虫/金粉特效配置
// 使用微小符号和星星，配合暖金色系
const DUST_EMOJIS = ['✨', '·', '•', '∗', '']

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
  if (!clientX) return
  
  // 点击产生 4-8 个微粒
  const count = Math.floor(Math.random() * 5) + 4
  
  for (let i = 0; i < count; i++) {
    const id = ++candyId
    // 随机分布范围（比星星更宽）
    const x = clientX + (Math.random() - 0.5) * 100
    
    // 🎨 随机颜色：使用与你博客主题匹配的金色/琥珀色系
    // HSL: 色相40-55(金色/琥珀色), 饱和度80%, 亮度50-80%
    const hue = 40 + Math.random() * 15
    const lightness = 50 + Math.random() * 30
    const color = `hsl(${hue}, 80%, ${lightness}%)`
    
    const dur = 2 + Math.random() * 1.5  // 飘得比较慢（2-3.5秒）
    const delay = Math.random() * 0.5
    const size = 6 + Math.random() * 12   // 大小不一，像尘埃（6-18px）
    const opacity = 0.4 + Math.random() * 0.6 // 半透明（0.4-1.0）
    
    // 随机选择 Emoji
    const emoji = DUST_EMOJIS[Math.floor(Math.random() * DUST_EMOJIS.length)]
    
    candies.value.push({ id, x, emoji, color, dur, delay, size, opacity })
  }
  
  // 性能优化：限制最大数量
  if (candies.value.length > 60) {
    candies.value = candies.value.slice(-40)
  }
  
  // 自动清理过期微粒
  setTimeout(() => {
    candies.value = candies.value.filter(c => c.id > candyId - 40)
  }, 3500)
}

onMounted(() => {
  // 可选：页面加载时显示欢迎特效
  // setTimeout(() => {
  //   const event = { 
  //     clientX: window.innerWidth / 2, 
  //     target: { closest: () => null } 
  //   }
  //   dropCandy(event)
  // }, 800)
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
  top: -20px;
  pointer-events: none;
  user-select: none;
  will-change: transform, opacity;
  font-weight: bold;
  /* 关键：添加柔和发光效果，让金色在米色背景上显现 */
  filter: blur(0.5px) drop-shadow(0 0 2px rgba(255, 190, 50, 0.5));
  animation-fill-mode: forwards;
}

/* 🌟 萤火虫飘落动画：带左右摇摆 */
@keyframes dustFall {
  0% {
    transform: translateY(-10px) translateX(0) scale(0.5) rotate(0deg);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  25% {
    transform: translateY(25vh) translateX(15px) scale(1) rotate(90deg);
  }
  50% {
    transform: translateY(50vh) translateX(-15px) scale(0.9) rotate(180deg);
  }
  75% {
    transform: translateY(75vh) translateX(10px) scale(1.1) rotate(270deg);
  }
  100% {
    transform: translateY(110vh) translateX(-10px) scale(0.8) rotate(360deg);
    opacity: 0;
  }
}

.candy {
  animation-name: dustFall;
  animation-timing-function: linear;
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
  padding: 10px 0;
}

.souta-nav-inner {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 8px;
  border: 1px solid rgba(0,0,0,0.08);
  border-radius: 10px;
  background: rgba(255,255,255,0.85);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
}

.nav-item {
  padding: 6px 14px;
  font-size: 14px;
  font-weight: 400;
  color: var(--text-light, #666);
  border-radius: 6px;
  transition: all 0.2s;
  cursor: pointer;
  user-select: none;
  text-decoration: none;
}

.nav-item:hover {
  color: var(--text, #333);
  background: rgba(0,0,0,0.04);
}

.nav-item.active {
  background: var(--text, #333);
  color: #fff;
  font-weight: 500;
  box-shadow: 0 2px 12px rgba(0,0,0,0.25);
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
  padding-top: 72px;
}

.souta-footer {
  padding: 24px 0;
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
  transition: opacity 0.25s ease, transform 0.25s ease;
}

.page-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.page-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* 响应式 */
@media (max-width: 768px) {
  .souta-nav-inner {
    gap: 2px;
    padding: 4px 6px;
  }
  
  .nav-item {
    padding: 5px 10px;
    font-size: 13px;
  }
  
  .souta-main {
    padding-top: 64px;
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
</style>