import axios from 'axios'
import router from '@/router'

const baseURL = import.meta.env.VITE_API_BASE_URL || '/api'

const request = axios.create({
  baseURL,
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器
request.interceptors.response.use(
  response => response.data,
  error => {
    if (error.response) {
      const status = error.response.status
      const msg = error.response.data?.message || error.response.data?.msg
      switch (status) {
        case 401:
          localStorage.removeItem('token')
          localStorage.removeItem('userInfo')
          router.push('/login')
          break
        case 403:
        case 404:
          console.warn(`[${status}] ${error.config?.url}`, msg || '')
          break
        default:
          if (msg) console.error(`[${status}] ${error.config?.url}`, msg)
      }
    } else {
      error.message = '网络错误，请检查后端服务'
    }
    return Promise.reject(error)
  }
)

export default request
