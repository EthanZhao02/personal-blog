<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="ornament">✦</div>
      <h2 class="auth-title">注册</h2>
      <p class="auth-subtitle">创建你的账号</p>

      <form @submit.prevent="handleRegister" class="auth-form">
        <div class="form-group">
          <label class="form-label">昵称</label>
          <input
            v-model="form.nickname"
            type="text"
            placeholder="你希望被称呼什么"
            class="form-input"
            required
          />
        </div>
        <div class="form-group">
          <label class="form-label">用户名</label>
          <input
            v-model="form.username"
            type="text"
            placeholder="用于登录"
            class="form-input"
            required
          />
        </div>
        <div class="form-group">
          <label class="form-label">密码</label>
          <input
            v-model="form.password"
            type="password"
            placeholder="设置密码"
            class="form-input"
            required
            minlength="6"
          />
        </div>

        <div class="form-error" v-if="error">{{ error }}</div>

        <button type="submit" class="btn btn-block" :disabled="loading">
          {{ loading ? '注册中...' : '注册' }}
        </button>
      </form>

      <div class="auth-footer">
        已有账号？<router-link to="/login">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '../api/user'

const router = useRouter()
const form = ref({ nickname: '', username: '', password: '' })
const loading = ref(false)
const error = ref('')

const handleRegister = async () => {
  error.value = ''
  loading.value = true
  try {
    const res = await register(form.value)
    if (res.code === 200) {
      router.push('/login')
    } else {
      error.value = res.message || '注册失败'
    }
  } catch (e) {
    error.value = e.message || '注册失败，请稍后重试'
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
