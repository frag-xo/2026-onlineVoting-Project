import { createRouter, createWebHistory } from 'vue-router'
import VoteList from '../views/VoteList.vue'

const routes = [
  { path: '/', name: 'VoteList', component: VoteList },
  { path: '/login', name: 'Login', component: () => import('../views/Login.vue') },
  { path: '/register', name: 'Register', component: () => import('../views/Register.vue') },
  { path: '/detail/:id', name: 'VoteDetail', component: () => import('../views/VoteDetail.vue') },
  { path: '/result/:id', name: 'VoteResult', component: () => import('../views/VoteResult.vue') },
  { path: '/admin', name: 'Admin', component: () => import('../views/Admin.vue') }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：只拦截 /admin，检查用户是否为管理员
router.beforeEach((to) => {
  if (to.path === '/admin') {
    const token = localStorage.getItem('token')
    const utype = localStorage.getItem('utype')
    if (!token || utype !== 'ROLE_1') {
      return { path: '/', query: { redirect: to.fullPath } }
    }
  }
  return true
})


export default router