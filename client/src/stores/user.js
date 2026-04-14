import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi } from '../api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.isAdmin === true)
  const userId = computed(() => user.value?.id ?? null)

  // 登录
  const login = async (username, password) => {
    const res = await loginApi({ username, password })
    if (res.code === 200 && res.data) {
      token.value = res.data.token
      user.value = res.data
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('userInfo', JSON.stringify(res.data))
    } else {
      throw new Error(res.message || '登录失败')
    }
  }

  // 设置用户信息
  const setUser = (userData) => {
    if (userData) {
      token.value = userData.token || ''
      user.value = userData
      localStorage.setItem('token', userData.token || '')
      localStorage.setItem('userInfo', JSON.stringify(userData))
    }
  }

  // 退出登录
  const logout = () => {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  return {
    token,
    user,
    isLoggedIn,
    isAdmin,
    userId,
    login,
    setUser,
    logout
  }
})
