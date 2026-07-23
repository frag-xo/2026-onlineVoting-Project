import axios from 'axios'

const baseURL = 'http://localhost:8080/api'

const request = axios.create({
  baseURL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
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
  (response) => {
    const res = response.data
    // 后端成功状态码可能是 0 或 200
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