import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus';
import 'element-plus/theme-chalk/index.css';
import './main.css';
//重点 样式必须要加
import 'element-plus/dist/index.css'
import http from "@/utils/request"; //引入request.js
// 注册所有图标

import App from './App.vue'
import router from './router'
import Validate from './vee-validate'




const app = createApp(App)

// app.use(createPinia())
app.use(router)
    .use(ElementPlus)
    .use(http)
//.use(Validate)

app.mount('#app')
