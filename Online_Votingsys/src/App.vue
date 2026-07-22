<template>
  <div id="app">
    <!-- 导航栏 -->
    <header class="app-header">
      <div class="header-left">
        <h2 @click="goHome" class="logo">📊 在线投票系统</h2>
      </div>
      <div class="header-right">
        <!-- 管理员可见：后台管理入口 -->
        <el-button
          type="primary"
          link
          @click="goAdmin"
          v-if="isLoggedIn && isAdmin"
        >
          后台管理
        </el-button>

        <!-- 已登录：显示用户名 + 退出按钮 -->
        <template v-if="isLoggedIn">
          <span class="username">欢迎，{{ username }}</span>
          <el-button type="danger" link @click="handleLogout">退出登录</el-button>
        </template>

        <!-- 未登录：显示登录 + 注册按钮 -->
        <template v-else>
          <el-button type="primary" link @click="goLogin">登录</el-button>
          <el-button type="primary" link @click="goRegister">注册</el-button>
        </template>
      </div>
    </header>

    <!-- 页面内容 -->
    <router-view />
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
    // 后端角色：ROLE_1=管理员，ROLE_3=普通用户
    const utype = localStorage.getItem('utype')
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
  font-family: 'Helvetica Neue', Arial, sans-serif;
  background-color: #f5f7fa;
}

#app {
  min-height: 100vh;
}

.app-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
  padding: 0 30px;
  background: #ffffff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-left .logo {
  font-size: 20px;
  color: #303133;
  cursor: pointer;
  user-select: none;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-right .username {
  color: #606266;
  font-size: 14px;
}
</style>