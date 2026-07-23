import axios from 'axios'

// 设置后端基础地址
const baseURL = 'http://localhost:8080/api'

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
    const token = localStorage.getItem('token')
    console.log('🔐 拦截器读取到 token:', token)  // 调试日志
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
      console.log('✅ 已添加 Authorization 头')
    } else {
      console.warn('⚠️ 没有 token，请求可能被拒绝')
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