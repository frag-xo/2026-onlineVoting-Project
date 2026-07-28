import { createRouter, createWebHistory } from 'vue-router'
import { getToken, getUtype } from '@/utils/auth'
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

// ============================================================
// 路由守卫
// ============================================================
router.beforeEach((to) => {
  const token = getToken()
  const utype = getUtype()

  // ===== 1. 公开页面（无需登录） =====
  // ✅ 明确列出所有公开页面，包括首页 /
  const publicPages = [
    '/', '/login', '/register', '/detail', '/result',
    '/anime-pk', '/pk', '/pk/battle', '/rankings'
  ]

  // 检查当前路径是否匹配公开页面
  const isPublic = publicPages.some(path => {
    // 精确匹配或者动态路由匹配
    if (path === to.path) return true
    if (path === '/detail' && to.path.startsWith('/detail/')) return true
    if (path === '/result' && to.path.startsWith('/result/')) return true
    return false
  })

  // ===== 公开页面：直接放行 =====
  if (isPublic) {
    // 已登录用户访问登录/注册页 → 跳转首页
    if (to.path === '/login' || to.path === '/register') {
      if (token) {
        return { path: '/' }
      }
      return true
    }
    return true
  }

  // ===== 2. 需要登录 =====
  if (!token) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }

  // ===== 3. 管理员专属 =====
  if (to.path === '/admin' || to.path === '/dashboard') {
    if (utype !== 'ROLE_1') {
      return { path: '/' }
    }
    return true
  }

  // ===== 4. 其他页面（登录即可访问） =====
  return true
})

export default router