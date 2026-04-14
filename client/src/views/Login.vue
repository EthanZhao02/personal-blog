<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="ornament">✦</div>
      <h2 class="auth-title">登录</h2>
      <p class="auth-subtitle">欢迎回来</p>

      <form @submit.prevent="handleLogin" class="auth-form">
        <div class="form-group">
          <label class="form-label">用户名</label>
          <input
            v-model="form.username"
            type="text"
            placeholder="请输入用户名"
            class="form-input"
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
            required
          />
        </div>

        <div class="form-error" v-if="error">{{ error }}</div>

        <button type="submit" class="btn btn-block" :disabled="loading">
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </form>

      <div class="auth-footer">
        仅管理员可登录
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()

const form = ref({ username: '', password: '' })
const loading = ref(false)
const error = ref('')

const handleLogin = async () => {
  error.value = ''
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
  padding-top: 20px;
}

.auth-card {
  width: 100%;
  max-width: 400px;
  background: var(--card);
  border: 1px solid var(--border);
  border-radius: 2px;
  padding: 48px 44px;
  text-align: center;
}

.auth-title {
  font-family: var(--font-display);
  font-size: 1.4rem;
  font-weight: 400;
  color: var(--text);
  letter-spacing: 4px;
  margin-bottom: 6px;
}

.auth-subtitle {
  font-size: 13px;
  color: var(--text-lighter);
  margin-bottom: 32px;
  letter-spacing: 2px;
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
  letter-spacing: 2px;
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
  border-radius: 2px;
  transition: border-color 0.2s;
}

.form-input:focus {
  border-color: var(--accent);
}

.form-error {
  color: #c0392b;
  font-size: 13px;
  margin-bottom: 16px;
  padding: 10px;
  background: #fdf2f2;
  border: 1px solid #f5c6cb;
  border-radius: 2px;
}

.btn-block {
  width: 100%;
  text-align: center;
  margin-top: 8px;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
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
