import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'
import VoteList from '../views/VoteList.vue'

const routes = [
  { path: '/', name: 'VoteList', component: VoteList },
  { path: '/login', name: 'Login', component: () => import('../views/Login.vue') },
  { path: '/register', name: 'Register', component: () => import('../views/Register.vue') },
  { path: '/detail/:id', name: 'VoteDetail', component: () => import('../views/VoteDetail.vue') },
  { path: '/result/:id', name: 'VoteResult', component: () => import('../views/VoteResult.vue') },
  { path: '/admin', name: 'Admin', component: () => import('../views/Admin.vue') },
  { path: '/dashboard', name: 'Dashboard', component: () => import('../views/Dashboard.vue') },
  { path: '/profile', name: 'Profile', component: () => import('../views/Profile.vue') },
  { path: '/rankings', name: 'Rankings', component: () => import('../views/Rankings.vue') },
  { path: '/create', name: 'CreateVote', component: () => import('../views/CreateVote.vue') },
  { path: '/anime-pk', name: 'AnimePK', component: () => import('../views/AnimePK.vue') }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：未登录跳转登录页
router.beforeEach((to) => {
  const publicPages = ['/', '/login', '/register', '/detail', '/result', '/anime-pk']
  if (!publicPages.includes(to.path) && !to.path.startsWith('/detail') && !to.path.startsWith('/result')) {
    const token = getToken()
    if (!token) {
      return { path: '/login', query: { redirect: to.fullPath } }
    }
  }
  return true
})

export default router