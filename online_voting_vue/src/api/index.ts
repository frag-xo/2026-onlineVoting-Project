import axios from 'axios'
import { getToken } from '@/utils/auth'
import { API_BASE } from '@/config'

// 设置后端基础地址
const baseURL = `${API_BASE}/api`

const request = axios.create({
  baseURL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// ✅ 请求拦截器：自动添加 Token
request.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code === 0 || res.code === 200) {
      return res.t !== undefined ? res.t : res
    } else {
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
  },
  (error) => {
    console.error('接口请求失败：', error)
    return Promise.reject(error)
  }
)

export default request