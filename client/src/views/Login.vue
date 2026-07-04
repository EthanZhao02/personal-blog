<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="ornament">✦</div>
      <h2 class="auth-title">账号登录</h2>
      <p class="auth-subtitle">{{ isStaticMode ? '静态部署说明' : '欢迎回来' }}</p>

      <div class="auth-notice" :class="{ static: isStaticMode }">
        <strong>{{ isStaticMode ? '当前线上静态站不能直接登录' : '后端登录已接入' }}</strong>
        <p>{{ isStaticMode ? siteConfig.auth.staticNotice : siteConfig.auth.localNotice }}</p>
        <router-link v-if="isStaticMode" to="/message">留言区可使用 GitHub 账号</router-link>
      </div>

      <form @submit.prevent="handleLogin" class="auth-form">
        <div class="form-group">
          <label class="form-label">用户名</label>
          <input
            v-model="form.username"
            type="text"
            placeholder="请输入用户名"
            class="form-input"
            :disabled="isStaticMode"
            required
          />
        </div>
        <div class="form-group">
          <label class="form-label">密码</label>
          <input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            class="form-input"
            :disabled="isStaticMode"
            required
          />
        </div>

        <div class="form-error" v-if="error">{{ error }}</div>

        <button type="submit" class="btn btn-block" :disabled="loading || isStaticMode">
          {{ isStaticMode ? '等待后端部署' : (loading ? '登录中...' : '登录') }}
        </button>
      </form>

      <div class="auth-footer">
        {{ isStaticMode ? '部署后设置 VITE_API_BASE_URL 即可启用站内账号。' : '仅管理员可登录。' }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import siteConfig, { isStaticMode } from '../config/site.config'

const router = useRouter()
const userStore = useUserStore()

const form = ref({ username: '', password: '' })
const loading = ref(false)
const error = ref('')

const handleLogin = async () => {
  error.value = ''
  if (isStaticMode) {
    error.value = siteConfig.auth.staticNotice
    return
  }
  loading.value = true
  try {
    await userStore.login(form.value.username, form.value.password)
    router.push('/')
  } catch (e) {
    error.value = e.message || '登录失败，请检查用户名和密码'
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
  border: 1px solid rgba(141, 248, 199, 0.22);
  border-radius: 8px;
  background: rgba(141, 248, 199, 0.07);
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
