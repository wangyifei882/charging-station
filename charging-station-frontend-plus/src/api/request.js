import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    console.log('[Request] URL:', config.url, 'Token:', token ? '存在' : '不存在')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
      console.log('[Request] Authorization header set')
    }
    return config
  },
  (error) => {
    console.error('[Request] Error:', error)
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  (response) => {
    console.log('[Response] Full response:', response)
    const res = response.data
    console.log('[Response] Data:', res)
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message))
    }
    return res
  },
  (error) => {
    console.error('[Response] Error:', error)
    console.error('[Response] Error response:', error.response)
    if (error.response?.status === 401 || error.response?.status === 403) {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      window.location.href = '/plus/login'
      return Promise.reject(new Error('登录已过期，请重新登录'))
    }
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default request
