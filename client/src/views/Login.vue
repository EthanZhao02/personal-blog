<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="ornament">✦</div>
      <h2 class="auth-title">{{ ui.title }}</h2>
      <p class="auth-subtitle">{{ isStaticMode ? ui.staticSubtitle : ui.subtitle }}</p>

      <div class="auth-notice" :class="{ static: isStaticMode }">
        <strong>{{ isStaticMode ? ui.staticTitle : ui.localTitle }}</strong>
        <p>{{ isStaticMode ? ui.staticNotice : ui.localNotice }}</p>
        <router-link v-if="isStaticMode" to="/message">{{ ui.githubMessage }}</router-link>
      </div>

      <form @submit.prevent="handleLogin" class="auth-form">
        <div class="form-group">
          <label class="form-label">{{ ui.username }}</label>
          <input
            v-model="form.username"
            type="text"
            :placeholder="ui.usernamePlaceholder"
            class="form-input"
            :disabled="isStaticMode"
            required
          />
        </div>
        <div class="form-group">
          <label class="form-label">{{ ui.password }}</label>
          <input
            v-model="form.password"
            type="password"
            :placeholder="ui.passwordPlaceholder"
            class="form-input"
            :disabled="isStaticMode"
            required
          />
        </div>

        <div class="form-error" v-if="error">{{ error }}</div>

        <button type="submit" class="btn btn-block" :disabled="loading || isStaticMode">
          {{ isStaticMode ? ui.waitBackend : (loading ? ui.loggingIn : ui.login) }}
        </button>
      </form>

      <div class="auth-footer">
        {{ isStaticMode ? ui.footerStatic : ui.footerLocal }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, inject, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import siteConfig, { isStaticMode } from '../config/site.config'

const router = useRouter()
const userStore = useUserStore()
const siteLanguage = inject('siteLanguage', ref(localStorage.getItem('ethan-language') || 'zh'))

const form = ref({ username: '', password: '' })
const loading = ref(false)
const error = ref('')
const ui = computed(() => siteLanguage.value === 'en'
  ? {
      title: 'Account Login',
      subtitle: 'Welcome back',
      staticSubtitle: 'Static Deployment',
      staticTitle: 'Static site cannot log in directly',
      localTitle: 'Backend login is connected',
      staticNotice: 'The current online static site does not connect to the backend login service.',
      localNotice: 'Admin login is available when the backend service is running.',
      githubMessage: 'Use GitHub account in the message board',
      username: 'Username',
      password: 'Password',
      usernamePlaceholder: 'Enter username',
      passwordPlaceholder: 'Enter password',
      waitBackend: 'Waiting for backend',
      loggingIn: 'Logging in...',
      login: 'Login',
      footerStatic: 'Set VITE_API_BASE_URL after deployment to enable site accounts.',
      footerLocal: 'Admin only.',
      failed: 'Login failed. Please check username and password.',
    }
  : {
      title: '账号登录',
      subtitle: '欢迎回来',
      staticSubtitle: '静态部署说明',
      staticTitle: '当前线上静态站不能直接登录',
      localTitle: '后端登录已接入',
      staticNotice: siteConfig.auth.staticNotice,
      localNotice: siteConfig.auth.localNotice,
      githubMessage: '留言区可使用 GitHub 账号',
      username: '用户名',
      password: '密码',
      usernamePlaceholder: '请输入用户名',
      passwordPlaceholder: '请输入密码',
      waitBackend: '等待后端部署',
      loggingIn: '登录中...',
      login: '登录',
      footerStatic: '部署后设置 VITE_API_BASE_URL 即可启用站内账号。',
      footerLocal: '仅管理员可登录。',
      failed: '登录失败，请检查用户名和密码',
    })

const handleLogin = async () => {
  error.value = ''
  if (isStaticMode) {
    error.value = ui.value.staticNotice
    return
  }
  loading.value = true
  try {
    await userStore.login(form.value.username, form.value.password)
    router.push('/')
  } catch (e) {
    error.value = e.message || ui.value.failed
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  min-height: calc(100vh - 180px);
  padding: 52px 16px 80px;
}

.auth-card {
  width: 100%;
  max-width: 460px;
  background:
    linear-gradient(135deg, rgba(17, 27, 49, 0.86), rgba(8, 14, 27, 0.72)),
    rgba(8, 14, 27, 0.74);
  border: 1px solid rgba(148, 226, 255, 0.18);
  border-radius: 10px;
  padding: 42px;
  text-align: center;
  box-shadow: var(--shadow);
  backdrop-filter: blur(18px);
}

.auth-title {
  font-family: var(--font-display);
  font-size: 1.65rem;
  font-weight: 800;
  color: var(--text);
  letter-spacing: 0;
  margin-bottom: 6px;
}

.auth-subtitle {
  font-size: 13px;
  color: var(--text-lighter);
  margin-bottom: 22px;
  letter-spacing: 0;
}

.auth-notice {
  margin-bottom: 26px;
  padding: 14px 16px;
  border: 1px solid rgba(147, 197, 253, 0.22);
  border-radius: 8px;
  background: rgba(147, 197, 253, 0.07);
  text-align: left;
}

.auth-notice.static {
  border-color: rgba(255, 189, 102, 0.28);
  background: rgba(255, 189, 102, 0.08);
}

.auth-notice strong {
  display: block;
  color: var(--text);
  font-size: 14px;
  margin-bottom: 6px;
}

.auth-notice p {
  margin: 0;
  color: var(--text-light);
  font-size: 13px;
  line-height: 1.7;
}

.auth-notice a {
  display: inline-flex;
  margin-top: 8px;
  color: var(--accent);
  font-size: 13px;
  font-weight: 700;
}

.auth-form {
  text-align: left;
}

.form-group {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  font-size: 12px;
  color: var(--text-light);
  letter-spacing: 0;
  margin-bottom: 8px;
}

.form-input {
  width: 100%;
  padding: 11px 14px;
  border: 1px solid var(--border);
  background: var(--bg);
  color: var(--text);
  font-family: var(--font-serif);
  font-size: 14px;
  outline: none;
  border-radius: 8px;
  transition: border-color 0.2s;
}

.form-input:focus {
  border-color: var(--accent);
}

.form-error {
  color: #ffd3d3;
  font-size: 13px;
  margin-bottom: 16px;
  padding: 10px;
  background: rgba(239, 68, 68, 0.12);
  border: 1px solid rgba(239, 68, 68, 0.3);
  border-radius: 8px;
}

.btn-block {
  width: 100%;
  text-align: center;
  margin-top: 8px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.auth-footer {
  margin-top: 24px;
  font-size: 13px;
  color: var(--text-lighter);
}

.auth-footer a {
  color: var(--accent-dark);
  text-decoration: underline;
  text-underline-offset: 3px;
}
</style>
