import { createRouter, createWebHistory } from 'vue-router'
import VoteList from '../views/VoteList.vue'

const routes = [
  { path: '/', name: 'VoteList', component: VoteList },
  { path: '/login', name: 'Login', component: () => import('../views/Login.vue') },
  { path: '/register', name: 'Register', component: () => import('../views/Register.vue') },
  { path: '/detail/:id', name: 'VoteDetail', component: () => import('../views/VoteDetail.vue') },
  { path: '/result/:id', name: 'VoteResult', component: () => import('../views/VoteResult.vue') },
  { path: '/admin', name: 'Admin', component: () => import('../views/Admin.vue') },
  { path: '/dashboard', name: 'Dashboard', component: () => import('../views/Dashboard.vue') }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：未登录只能访问登录/注册页，已登录不重复跳到登录页
router.beforeEach((to) => {
  const token = localStorage.getItem('token')
  const utype = localStorage.getItem('utype')

  // 未登录 → 跳登录页（静态资源/登录/注册放行）
  if (!token && to.path !== '/login' && to.path !== '/register') {
    return { path: '/login', query: { redirect: to.fullPath } }
  }

  // 已登录 → 不允许跳登录/注册页（回首页）
  if (token && (to.path === '/login' || to.path === '/register')) {
    return { path: '/' }
  }

  // 管理员专有页
  if (to.path === '/admin' && utype !== 'ROLE_1') {
    return { path: '/', query: { redirect: to.fullPath } }
  }

  return true
})

export default router