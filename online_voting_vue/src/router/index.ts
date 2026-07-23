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

// 路由守卫：判断是否登录
// router.beforeEach((to) => {
//  const token = localStorage.getItem('token')
//  const requiresAuth = ['/admin']
//  if (requiresAuth.includes(to.path)) {
//    if (!token) {
//      return { path: '/login', query: { redirect: to.fullPath } }
//    }
//  }
//  return true
//})

export default router