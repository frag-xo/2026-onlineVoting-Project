import axios from 'axios'

axios.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded'

// 请求拦截器
axios.interceptors.request.use(function(config) {
  return config;
}, function(error) {
  return Promise.reject(error);
})
// 响应拦截器
axios.interceptors.response.use(function(response) {
  return response;
}, function(error) {
  return Promise.reject(error);
})

// 封装axios的post get请求
export function fetch(url, params,type,headers) {
  return new Promise((resolve, reject) => {
    if(type=='get') {
      axios.get(url, params)
        .then(response => {
          resolve(response.data);
        })
        .catch((error) => {
          reject(error);
        })
    }else if(type=='post'){
      if(headers==undefined) {
        axios.post(url, params)
          .then(response => {
            resolve(response.data);
          })
          .catch((error) => {
            reject(error);
          })
      }else{
        axios.post(url, params, headers)
          .then(response => {
            resolve(response.data);
          })
          .catch((error) => {
            reject(error);
          })
      }
    }
  })
}

export default {
  setDataFromAxios(url, params,type,headers) {
    return fetch(url, params,type,headers);
  }
}
