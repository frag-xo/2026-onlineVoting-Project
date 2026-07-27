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
  { path: '/anime-pk', name: 'AnimePK', component: () => import('../views/AnimePK.vue') },
  { path: '/pk', name: 'PkIndex', component: () => import('../views/PkIndex.vue') },
  { path: '/pk/battle/:id', name: 'PkBattle', component: () => import('../views/PkBattle.vue') },
  { path: '/friends', name: 'Friends', component: () => import('@/views/Friends.vue'), meta: { requiresAuth: true } },
  { path: '/chat/:friendId', name: 'Chat', component: () => import('@/views/Chat.vue'), meta: { requiresAuth: true } },
  { path: '/shop', name: 'ShopIndex', component: () => import('../views/Shop/ShopIndex.vue') },
  { path: '/shop/my-items', name: 'MyItems', component: () => import('../views/Shop/MyItems.vue') },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：未登录跳转登录页
router.beforeEach((to) => {
  const publicPages = ['/', '/login', '/register', '/detail', '/result', '/anime-pk', '/pk']
  if (!publicPages.includes(to.path) && !to.path.startsWith('/detail') && !to.path.startsWith('/result')) {
    const token = getToken()
    if (!token) {
      return { path: '/login', query: { redirect: to.fullPath } }
    }
  }
  return true
})


export default router

