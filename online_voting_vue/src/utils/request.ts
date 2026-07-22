//导入axios
import axios from 'axios'
import {  useMessage, useMessageBox } from "@/hooks/message";
// import store from '@/store/index'
//创建axios实例
const service = axios.create({
    baseURL: '/api',
    timeout: 10000,
    withCredentials: true,//跨域请求时携带cookie
})


//请求拦截器
service.interceptors.request.use(
    config => {

        // if (store.getters.token) {
        //     config.headers['token'] = getToken()
        // }
        //放行请求
        return config
    },
    error => {
       // console.log(error)

        return Promise.reject(error)
    }
)


//响应拦截器
service.interceptors.response.use(
    response => {
        //返回的数据
        const res = response.data

        if (res.code !== 200 && res.code !== '200') {
            // ElMessage({
            //     message: res.message || 'Error',
            //     type: 'error',
            //     duration: 5 * 1000
            // })

            // 50008: Illegal token; 50012: Other clients logged in; 50014: Token expired;
            if (res.code === 50008 || res.code === 50012 || res.code === 50014) {
                // to re-login
                // useMessageBox.confirm('You have been logged out, you can cancel to stay on this page, or log in again').then(() => {
                //     store.dispatch('user/resetToken').then(() => {
                //         location.reload()
                //     })
                // })
            }
            return Promise.reject(new Error(res.message || 'Error'))
        } else {
            return res
        }
    },
    error => {
        //console.log('err' + error) // for debug
        // Message({
        //     message: error.message,
        //     type: 'error',
        //     duration: 5 * 1000
        // })
        return Promise.reject(error)
    }
)

export default service
