<template>
  <div id="app">
    <el-container class="app-container">
      <!-- 侧边栏 -->
      <el-aside :width="isCollapse ? '64px' : '220px'" class="app-aside">
        <div class="logo-area">
          <span v-if="!isCollapse">📊 投票系统</span>
          <span v-else>📊</span>
        </div>
        <el-menu
          :collapse="isCollapse"
          :collapse-transition="false"
          router
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409eff"
          class="app-menu"
        >
          <el-menu-item index="/">
            <el-icon><House /></el-icon>
            <template #title>投票列表</template>
          </el-menu-item>
          <el-menu-item index="/rankings">
            <el-icon><TrendCharts /></el-icon>
            <template #title>投票排行</template>
          </el-menu-item>
          <el-menu-item index="/create">
            <el-icon><EditPen /></el-icon>
            <template #title>发布投票</template>
          </el-menu-item>
          <el-menu-item index="/profile">
            <el-icon><UserFilled /></el-icon>
            <template #title>个人中心</template>
          </el-menu-item>
          <el-menu-item index="/anime-pk">
            <el-icon><StarFilled /></el-icon>
            <template #title>动漫PK</template>
          </el-menu-item>
          <!-- 新增社交菜单 -->
          <el-menu-item index="/friends">
            <el-icon><ChatLineRound /></el-icon>
            <template #title>
              社交
              <el-badge :value="unreadCount" :hidden="unreadCount === 0" style="margin-left: 8px;" />
            </template>
          </el-menu-item>
          <el-menu-item index="/admin" v-if="isLoggedIn && isAdmin">
            <el-icon><Setting /></el-icon>
            <template #title>后台管理</template>
          </el-menu-item>
          <el-menu-item index="/dashboard" v-if="isLoggedIn && isAdmin">
            <el-icon><DataAnalysis /></el-icon>
            <template #title>数据看板</template>
          </el-menu-item>
          <el-menu-item index="/shop">
            <el-icon><Shop /></el-icon>
            <template #title>积分商城</template>
          </el-menu-item>
        </el-menu>

        <!-- 主题切换 -->
        <div class="theme-switcher">
          <el-dropdown trigger="click" @command="changeTheme">
            <el-button size="small" circle style="color:#bfcbd9;background:transparent;border:1px solid #4a5a6a;">
              <el-icon><Brush /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="default">🌊 默认蓝</el-dropdown-item>
                <el-dropdown-item command="green">🌿 青绿</el-dropdown-item>
                <el-dropdown-item command="orange">🌅 暖橙</el-dropdown-item>
                <el-dropdown-item command="purple">🌙 紫韵</el-dropdown-item>
                <el-dropdown-item command="custom">🎨 自定义</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <input
            v-if="showCustomColor"
            type="color"
            @input="applyCustomColor($event.target.value)"
            style="width:32px;height:32px;border:none;cursor:pointer;background:transparent;margin-left:6px;"
          />
        </div>

        <!-- 折叠按钮 -->
        <div class="collapse-btn" @click="toggleCollapse">
          <el-icon>
            <DArrowLeft v-if="!isCollapse" />
            <DArrowRight v-else />
          </el-icon>
        </div>
      </el-aside>

      <!-- 主内容区 -->
      <el-container>
        <el-header class="app-header">
          <div class="header-left">
            <span class="welcome">📋 {{ currentPageTitle }}</span>
          </div>
          <div class="header-right">
            <el-button
              type="primary"
              link
              @click="goAdmin"
              v-if="isLoggedIn && isAdmin"
              class="header-btn"
            >
              <el-icon><Setting /></el-icon> 后台
            </el-button>

            <template v-if="isLoggedIn">
              <!-- 消息入口（带角标） -->
              <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="msg-badge">
                <el-button type="primary" link @click="goFriends" class="header-btn">
                  <el-icon><ChatLineRound /></el-icon>
                  消息
                </el-button>
              </el-badge>

              <span class="username">👤 {{ username }}</span>
              <span class="role-tag">{{ isAdmin ? '管理员' : '用户' }}</span>
              <el-button type="danger" link @click="handleLogout" class="header-btn">
                退出
              </el-button>
            </template>
            <template v-else>
              <el-button type="primary" link @click="goLogin" class="header-btn">登录</el-button>
              <el-button type="primary" link @click="goRegister" class="header-btn">注册</el-button>
            </template>
          </div>
        </el-header>
        <el-main class="app-main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>

    <!-- AI 小助手悬浮按钮 -->
    <AIChatButton />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, onBeforeUnmount } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import AIChatButton from '@/components/AIChatButton.vue'
import { getToken, getUsername, getUtype, isAdmin as checkIsAdmin, clearAuth } from '@/utils/auth'
import { getTotalUnread } from '@/api/chat'
import {
  House,
  Setting,
  DataAnalysis,
  TrendCharts,
  EditPen,
  StarFilled,
  UserFilled,
  DArrowLeft,
  DArrowRight,
  Brush,
  ChatLineRound
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const isCollapse = ref(localStorage.getItem('sidebarCollapse') === 'true')
const isLoggedIn = ref(false)
const username = ref('')
const isAdmin = ref(false)
const currentPageTitle = ref('投票列表')
const showCustomColor = ref(false)
const unreadCount = ref(0)

// 定时器
let timer: number | null = null

const pageTitles: Record<string, string> = {
  '/': '投票列表',
  '/login': '登录',
  '/register': '注册',
  '/admin': '后台管理',
  '/dashboard': '数据看板',
  '/profile': '个人中心',
  '/rankings': '投票排行',
  '/create': '发布投票',
  '/friends': '社交'
}

const updatePageTitle = () => {
  const path = route.path
  if (path.startsWith('/detail')) {
    currentPageTitle.value = '投票详情'
  } else if (path.startsWith('/result')) {
    currentPageTitle.value = '投票结果'
  } else if (path.startsWith('/chat')) {
    currentPageTitle.value = '聊天'
  } else {
    currentPageTitle.value = pageTitles[path] || '投票系统'
  }
}

const checkLoginStatus = () => {
  const token = getToken()
  if (token) {
    isLoggedIn.value = true
    username.value = getUsername()
    isAdmin.value = checkIsAdmin()
  } else {
    isLoggedIn.value = false
    username.value = ''
    isAdmin.value = false
  }
}

// 获取未读消息数
const loadUnread = async () => {
  if (!isLoggedIn.value) {
    unreadCount.value = 0
    return
  }
  try {
    const count = await getTotalUnread()
    unreadCount.value = count || 0
  } catch (e) {
    // 忽略错误
  }
}

// 主题切换
const changeTheme = (command: string) => {
  const root = document.documentElement
  if (command === 'custom') {
    showCustomColor.value = !showCustomColor.value
    return
  }
  showCustomColor.value = false
  const colors: Record<string, string> = {
    default: '#409eff',
    green: '#67c23a',
    orange: '#e6a23c',
    purple: '#8e44ad'
  }
  const primary = colors[command] || '#409eff'
  root.style.setProperty('--el-color-primary', primary)
  root.style.setProperty('--menu-active-color', primary)
  localStorage.setItem('theme-primary', primary)
}

const applyCustomColor = (hex: string) => {
  document.documentElement.style.setProperty('--el-color-primary', hex)
  document.documentElement.style.setProperty('--menu-active-color', hex)
  localStorage.setItem('theme-primary', hex)
}

// 恢复主题
const restoreTheme = () => {
  const saved = localStorage.getItem('theme-primary')
  if (saved) {
    document.documentElement.style.setProperty('--el-color-primary', saved)
    document.documentElement.style.setProperty('--menu-active-color', saved)
  }
}

onMounted(() => {
  checkLoginStatus()
  updatePageTitle()
  restoreTheme()
  loadUnread()
  // 每30秒刷新未读数
  timer = window.setInterval(loadUnread, 30000)

  // 监听其他 Tab 的登录状态变化
  window.addEventListener('storage', (e) => {
    if (e.key === 'token' && e.oldValue && e.newValue && e.oldValue !== e.newValue) {
      ElMessage.warning('检测到其他账号登录，当前页面已失效，请重新登录')
      handleLogout()
    }
  })
})

onBeforeUnmount(() => {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
})

watch(() => route.path, () => {
  updatePageTitle()
  checkLoginStatus()
})

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
  localStorage.setItem('sidebarCollapse', String(isCollapse.value))
}

const goLogin = () => router.push('/login')
const goRegister = () => router.push('/register')
const goAdmin = () => router.push('/admin')
const goFriends = () => router.push('/friends')

const handleLogout = () => {
  clearAuth()
  isLoggedIn.value = false
  isAdmin.value = false
  unreadCount.value = 0
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style>
/* 原有样式保留，仅添加消息角标相关 */
.msg-badge {
  display: inline-flex;
  align-items: center;
}
.msg-badge .el-badge__content.is-fixed {
  top: 6px;
  right: 8px;
}

/* 其余样式与之前完全一致，此处省略（请确保保留之前的全部样式） */
* { margin: 0; padding: 0; box-sizing: border-box; }
body { font-family: 'Helvetica Neue', Arial, sans-serif; background-color: #f0f2f5; height: 100%; }
html, #app { height: 100%; }
.app-container { height: 100%; }

.app-aside {
  background-color: #304156;
  transition: width 0.3s;
  display: flex;
  flex-direction: column;
  height: 100vh;
  position: sticky;
  top: 0;
  overflow: hidden;
}
.logo-area {
  height: 60px; line-height: 60px; text-align: center; color: #fff; font-size: 18px;
  font-weight: bold; background-color: #2b3a4a; white-space: nowrap; overflow: hidden;
}
.app-menu {
  border-right: none;
  flex: 1;
  overflow-y: auto;
}
.app-menu .el-menu-item { height: 50px; line-height: 50px; }
.app-menu .el-menu-item.is-active {
  background-color: var(--menu-active-color, #409eff) !important;
  color: #fff !important;
}
.app-menu .el-menu-item.is-active .el-icon {
  color: #fff !important;
}

.theme-switcher {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 8px 0;
  background-color: #2b3a4a;
}

.collapse-btn {
  height: 50px; line-height: 50px; text-align: center; color: #bfcbd9;
  cursor: pointer; background-color: #2b3a4a;
}
.collapse-btn:hover { background-color: #1f2d3d; }
.collapse-btn .el-icon { font-size: 20px; }

.app-header {
  background-color: #fff; box-shadow: 0 1px 4px rgba(0,21,41,0.08);
  display: flex; justify-content: space-between; align-items: center;
  padding: 0 24px; height: 60px;
}
.header-left .welcome { font-size: 16px; color: #303133; }
.header-right { display: flex; align-items: center; gap: 16px; }
.header-btn { font-size: 14px; }
.username { color: #303133; font-size: 14px; }
.role-tag { font-size: 12px; color: #909399; background-color: #f4f4f5; padding: 2px 12px; border-radius: 12px; }

.app-main {
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  background-size: 400% 400%;
  animation: gradientMove 15s ease infinite;
  padding: 20px;
  overflow-y: auto;
  height: calc(100vh - 60px);
}
@keyframes gradientMove {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

.el-card {
  transition: transform 0.3s cubic-bezier(.34,1.56,.64,1), box-shadow 0.3s ease !important;
  border-radius: 16px !important;
}
.el-card:hover {
  transform: translateY(-4px) scale(1.01);
  box-shadow: 0 20px 40px rgba(0,0,0,0.12) !important;
}

.el-button {
  position: relative;
  overflow: hidden;
  transition: all 0.25s ease;
  border-radius: 10px !important;
}
.el-button::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: rgba(255,255,255,0.3);
  transform: translate(-50%, -50%);
  transition: width 0.5s, height 0.5s;
}
.el-button:active::after {
  width: 200px;
  height: 200px;
}

.el-dialog,
.el-menu {
  border-radius: 16px !important;
}
</style>