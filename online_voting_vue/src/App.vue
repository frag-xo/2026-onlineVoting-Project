<template>
  <div id="app">
    <header class="app-header">
      <div class="header-left">
        <span class="logo-icon">📋</span>
        <h2 @click="goHome" class="logo">在线投票系统</h2>
      </div>
      <div class="header-right">
        <el-button
          class="nav-btn"
          @click="goAdmin"
          v-if="isLoggedIn && isAdmin"
        >
          后台管理
        </el-button>

        <template v-if="isLoggedIn">
          <span class="username">👋 {{ username }}</span>
          <el-button class="logout-btn" @click="handleLogout">退出</el-button>
        </template>

        <template v-else>
          <el-button class="nav-btn" @click="goLogin">登录</el-button>
          <el-button class="nav-btn nav-btn-reg" @click="goRegister">注册</el-button>
        </template>
      </div>
    </header>

    <main class="app-main">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()

const isLoggedIn = ref(false)
const username = ref('')
const isAdmin = ref(false)

const checkLoginStatus = () => {
  const token = localStorage.getItem('token')
  if (token) {
    isLoggedIn.value = true
    username.value = localStorage.getItem('username') || '用户'
    const utype = localStorage.getItem('utype')
    // ROLE_1 是管理员，其他都是普通用户
    isAdmin.value = utype === 'ROLE_1'
  } else {
    isLoggedIn.value = false
    username.value = ''
    isAdmin.value = false
  }
}

onMounted(() => {
  checkLoginStatus()
})

const goHome = () => router.push('/')
const goLogin = () => router.push('/login')
const goRegister = () => router.push('/register')
const goAdmin = () => router.push('/admin')

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userId')
  localStorage.removeItem('username')
  localStorage.removeItem('utype')
  isLoggedIn.value = false
  ElMessage.success('已退出登录')
  router.push('/')
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
  background: #f8f9fb;
  min-height: 100vh;
  color: #1a1a2e;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

h1, h2, h3, h4, h5, h6 {
  font-family: 'Outfit', sans-serif;
  font-weight: 600;
  letter-spacing: -0.02em;
}

#app {
  min-height: 100vh;
}

.app-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
  padding: 0 36px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.04);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.logo-icon {
  font-size: 22px;
}

.header-left .logo {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a2e;
  cursor: pointer;
  user-select: none;
  letter-spacing: 0.5px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-right .username {
  color: #606266;
  font-size: 14px;
}

.nav-btn {
  border-radius: 8px !important;
  font-size: 13px !important;
  padding: 8px 18px !important;
  border: 1px solid #dcdfe6 !important;
  background: #fff !important;
  color: #303133 !important;
  transition: all 0.2s !important;
}

.nav-btn:hover {
  border-color: #409eff !important;
  color: #409eff !important;
  background: #ecf5ff !important;
}

.nav-btn-reg {
  background: #409eff !important;
  color: #fff !important;
  border-color: #409eff !important;
}

.nav-btn-reg:hover {
  background: #66b1ff !important;
  border-color: #66b1ff !important;
  color: #fff !important;
}

.logout-btn {
  border-radius: 8px !important;
  font-size: 13px !important;
  padding: 8px 18px !important;
  border: 1px solid #f56c6c !important;
  background: #fff !important;
  color: #f56c6c !important;
  transition: all 0.2s !important;
}

.logout-btn:hover {
  background: #fef0f0 !important;
}

.app-main {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px 20px 40px;
}
</style>