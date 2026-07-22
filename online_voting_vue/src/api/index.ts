import axios from 'axios'

// 配置后端地址
const baseURL = 'http://localhost:8080/api'

const request = axios.create({
  baseURL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器（可加 token）
request.interceptors.request.use(
  config => {
    // 如果有 token，在这里添加
    // const token = localStorage.getItem('token')
    // if (token) {
    //   config.headers.Authorization = `Bearer ${token}`
    // }
    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器：适配后端返回格式 { code, msg, t, ... }
request.interceptors.response.use(
  response => {
    const res = response.data
    // 后端成功返回 code === 0，数据在 t 字段
    if (res.code === 0) {
      // 如果 t 存在则返回 t，否则返回整个 res（兼容其他情况）
      return res.t !== undefined ? res.t : res
    } else {
      // 错误时返回 msg
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
  },
  error => {
    console.error('接口请求失败：', error)
    return Promise.reject(error)
  }
)

export default request