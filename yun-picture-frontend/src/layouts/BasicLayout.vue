<template>
  <div class="basic-layout">
    <!-- 全局背景：固定在最底层；在 /background 路由允许交互 -->
    <BackgroundFrame :interactive="isBackgroundRoute" />

    <!-- 左上角返回按钮（仅在 /background 显示） -->
    <button v-if="isBackgroundRoute" class="bg-back-button" @click="handleBgBack" aria-label="返回">← 返回</button>

    <!-- 顶部导航区域（在 /background 隐藏） -->
    <header class="header foreground" v-if="!isBackgroundRoute">
      <div class="header-container">
        <!-- 左侧 Logo 区域 -->
        <div class="logo-section">
          <div class="logo-icon">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M21 19V5C21 3.9 20.1 3 19 3H5C3.9 3 3 3.9 3 5V19C3 20.1 3.9 21 5 21H19C20.1 21 21 20.1 21 19ZM8.5 13.5L11 16.51L14.5 12L19 18H5L8.5 13.5Z" fill="currentColor"/>
            </svg>
          </div>
          <h1 class="logo-text">云图库</h1>
        </div>

        <!-- 中间导航菜单 -->
        <nav class="nav-menu">
          <RouterLink to="/" class="nav-item" active-class="active">首页</RouterLink>
          <RouterLink to="/admin/userManage" class="nav-item" active-class="active">用户管理</RouterLink>
          <RouterLink to="/admin/pictureManage" class="nav-item" active-class="active">图片管理</RouterLink>
          <RouterLink to="/admin/spaceManage" class="nav-item" active-class="active">空间管理</RouterLink>
          <RouterLink to="/space/my" class="nav-item" active-class="active">我的空间</RouterLink>
          <RouterLink to="/picture/upload" class="nav-item" active-class="active">上传图片</RouterLink>
          <RouterLink to="/picture/crawl" class="nav-item" active-class="active">抓取图片</RouterLink>
          <RouterLink to="/background" class="nav-item" active-class="active">切换背景</RouterLink>
        </nav>

        <!-- 右侧用户区域 -->
        <div class="user-section">
          <div class="user-avatar" @click="toggleUserDropdown">
            <img 
              :src="userInfo?.userAvatar || 'https://via.placeholder.com/40x40/6366f1/ffffff?text=U'" 
              :alt="userInfo?.userName || '用户头像'" 
            />
            <div class="avatar-glow"></div>
          </div>
          <span class="username" @click="toggleUserDropdown">
            {{ userInfo?.userName || '用户名' }}
          </span>
          <div class="user-dropdown" @click="toggleUserDropdown">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M7 10L12 15L17 10H7Z" fill="currentColor"/>
            </svg>
          </div>
          
          <!-- 用户下拉菜单 -->
          <div class="user-dropdown-menu" v-if="showUserDropdown">
            <div class="dropdown-header">
              <div class="dropdown-user-info">
                <img 
                  :src="userInfo?.userAvatar || 'https://via.placeholder.com/40x40/6366f1/ffffff?text=U'" 
                  :alt="userInfo?.userName || '用户头像'"
                  class="dropdown-avatar"
                />
                <div class="dropdown-user-details">
                  <div class="dropdown-username">{{ userInfo?.userName || '用户名' }}</div>
                  <div class="dropdown-account">{{ userInfo?.userAccount || '账号' }}</div>
                </div>
              </div>
            </div>
            <div class="dropdown-divider"></div>
            <div class="dropdown-menu-items">
              <div class="dropdown-item" @click="goToUserProfile">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M12 12C14.21 12 16 10.21 16 8C16 5.79 14.21 4 12 4C9.79 4 8 5.79 8 8C8 10.21 9.79 12 12 12ZM12 14C9.33 14 4 15.34 4 18V20H20V18C20 15.34 14.67 14 12 14Z" fill="currentColor"/>
                </svg>
                <span>个人资料</span>
              </div>
              <div class="dropdown-item" @click="goToSettings">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M19.14 12.94C19.18 12.64 19.2 12.33 19.2 12C19.2 11.67 19.18 11.36 19.14 11.06L21.16 9.48C21.34 9.34 21.39 9.07 21.28 8.87L19.36 5.55C19.24 5.33 18.99 5.26 18.77 5.33L16.38 6.29C15.88 5.91 15.35 5.59 14.76 5.35L14.4 2.81C14.36 2.57 14.16 2.4 13.92 2.4H10.08C9.84 2.4 9.65 2.57 9.61 2.81L9.25 5.35C8.66 5.59 8.12 5.92 7.63 6.29L5.24 5.33C5.02 5.25 4.77 5.33 4.65 5.55L2.74 8.87C2.62 9.08 2.66 9.34 2.86 9.48L4.88 11.06C4.84 11.36 4.8 11.69 4.8 12C4.8 12.31 4.82 12.64 4.86 12.94L2.84 14.52C2.66 14.66 2.61 14.93 2.72 15.13L4.64 18.45C4.76 18.67 5.01 18.74 5.23 18.67L7.62 17.71C8.12 18.09 8.65 18.41 9.24 18.65L9.6 21.19C9.65 21.43 9.84 21.6 10.08 21.6H13.92C14.16 21.6 14.36 21.43 14.39 21.19L14.75 18.65C15.34 18.41 15.88 18.09 16.37 17.71L18.76 18.67C18.98 18.75 19.23 18.67 19.35 18.45L21.27 15.13C21.39 14.91 21.34 14.66 21.15 14.52L19.14 12.94ZM12 15.6C10.02 15.6 8.4 13.98 8.4 12C8.4 10.02 10.02 8.4 12 8.4C13.98 8.4 15.6 10.02 15.6 12C15.6 13.98 13.98 15.6 12 15.6Z" fill="currentColor"/>
                </svg>
                <span>设置</span>
              </div>
            </div>
            <div class="dropdown-divider"></div>
            <div class="dropdown-menu-items">
              <div class="dropdown-item logout-item" @click="handleLogout">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M17 7L15.59 8.41L18.17 11H8V13H18.17L15.59 15.59L17 17L22 12L17 7ZM4 5H12V3H4C2.9 3 2 3.9 2 5V19C2 20.1 2.9 21 4 21H12V19H4V5Z" fill="currentColor"/>
                </svg>
                <span>退出登录</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </header>

    <!-- 中间内容区域（在 /background 不渲染，以便全局背景可交互） -->
    <main class="main-content foreground" v-if="!isBackgroundRoute">
      <slot />
    </main>

    <!-- 底部区域（在 /background 隐藏） -->
    <footer class="footer foreground" v-if="!isBackgroundRoute">
      <div class="footer-container">
        <!-- 左侧项目介绍 -->
        <div class="footer-section">
          <h3 class="footer-title">云图库</h3>
          <p class="footer-desc">发现世界的美好瞬间，精选全球优质图片，让创意无限可能</p>
          <div class="contact-info">
            <span>📧</span>
            <span>📱 </span>
          </div>
        </div>

        <!-- 中间快速链接 -->
        <div class="footer-section">
          <h4 class="footer-subtitle">快速链接</h4>
          <div class="footer-links">
            <a href="#" class="footer-link">关于我们</a>
            <a href="#" class="footer-link">帮助中心</a>
            <a href="#" class="footer-link">API文档</a>
            <a href="#" class="footer-link">使用条款</a>
          </div>
        </div>

        <!-- 右侧社交媒体 -->
        <div class="footer-section">
          <h4 class="footer-subtitle">关注我们</h4>
          <div class="social-links">
            <a href="#" class="social-link">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M24 4.557C23.117 4.949 22.168 5.15 21.172 5.19C22.189 4.723 22.962 3.94 23.33 2.964C22.386 3.374 21.348 3.62 20.25 3.68C19.31 2.863 18.13 2.346 16.88 2.346C14.61 2.346 12.79 4.166 12.79 6.426C12.79 6.696 12.821 6.962 12.88 7.218C8.28 6.93 4.11 4.718 1.28 1.64C0.99 2.198 0.83 2.824 0.83 3.49C0.83 4.808 1.49 5.976 2.56 6.68C1.69 6.65 0.86 6.4 0.13 6.02V6.08C0.13 8.08 1.22 9.865 2.92 10.35C2.65 10.42 2.37 10.46 2.08 10.46C1.89 10.46 1.71 10.43 1.53 10.38C1.89 12.23 3.56 13.58 5.56 13.62C3.95 14.9 1.98 15.6 0 15.6C0.73 15.6 1.43 15.5 2.08 15.3C4.26 16.68 6.92 17.5 9.8 17.5C16.88 17.5 20.96 10.46 20.96 4.79C20.96 4.6 20.96 4.42 20.95 4.24C21.93 3.54 22.77 2.68 23.46 1.7L24 4.557Z" fill="currentColor"/>
              </svg>
            </a>
            <a href="#" class="social-link">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 2.04C6.5 2.04 2 6.53 2 12.06C2 17.06 5.66 21.21 10.44 21.96V14.96H7.9V12.06H10.44V9.85C10.44 7.34 11.93 5.96 14.22 5.96C15.31 5.96 16.45 6.15 16.45 6.15V8.62H15.19C13.95 8.62 13.56 9.39 13.56 10.81V12.06H16.34L15.89 14.96H13.56V21.96A10 10 0 0 0 22 12.06C22 6.53 17.5 2.04 12 2.04Z" fill="currentColor"/>
              </svg>
            </a>
            <a href="#" class="social-link">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M7.8 2H16.2C19.4 2 22 4.6 22 7.8V16.2C22 19.4 19.4 22 16.2 22H7.8C4.6 22 2 19.4 2 7.8V7.8C2 4.6 4.6 2 7.8 2M7.6 4C6.8 4 6.2 4.6 6.2 5.4V18.6C6.2 19.4 6.8 20 7.6 20H16.4C17.2 20 17.8 19.4 17.8 18.6V5.4C17.8 4.6 17.2 4 16.4 4H7.6M12 7C13.7 7 15 8.3 15 10C15 11.7 13.7 13 12 13C10.3 13 9 11.7 9 10C9 8.3 10.3 7 12 7M12 9C11.4 9 11 9.4 11 10C11 10.6 11.4 11 12 11C12.6 11 13 10.6 13 10C13 9.4 12.6 9 12 9M6.5 17.5C6.5 16.7 7.1 16.1 7.9 16.1C8.7 16.1 9.3 16.7 9.3 17.5C9.3 18.3 8.7 18.9 7.9 18.9C7.1 18.9 6.5 18.3 6.5 17.5Z" fill="currentColor"/>
              </svg>
            </a>
          </div>
          <div class="newsletter">
            <input type="email" placeholder="订阅我们的更新" class="newsletter-input" />
            <button class="newsletter-btn">订阅</button>
          </div>
        </div>
      </div>
      
      <!-- 波浪分割线 -->
      <div class="wave-divider">
        <svg viewBox="0 0 1200 120" preserveAspectRatio="none">
          <path d="M0,0V46.29c47.79,22.2,103.59,32.17,158,28,70.36-5.37,136.33-33.31,206.8-37.5C438.64,32.43,512.34,53.67,583,72.05c69.27,18,138.3,24.88,209.4,13.08,36.15-6,69.85-17.84,104.45-29.34C989.49,25,1113-14.29,1200,52.47V0Z" opacity=".25" fill="currentColor"></path>
          <path d="M0,0V15.81C13,36.92,27.64,56.86,47.69,72.05,99.41,111.27,165,111,224.58,91.58c31.15-10.15,60.09-26.07,89.67-39.8,40.92-19,84.73-46,130.83-49.67,36.26-2.85,70.9,9.42,98.6,31.56,31.77,25.39,62.32,62,103.63,73,40.44,10.79,81.35-6.69,119.13-24.28s75.16-39,116.92-43.05c59.73-5.85,113.28,22.88,168.9,38.84,30.2,8.66,59,6.17,87.09-7.5,22.43-10.89,48-26.93,60.65-49.24V0Z" opacity=".5" fill="currentColor"></path>
          <path d="M0,0V5.63C149.93,59,314.09,71.32,475.83,42.57c43-7.64,84.23-20.12,127.61-26.46,59-8.63,112.48,12.24,165.56,35.4C827.93,77.22,886,95.24,951.2,90c86.53-7,172.46-45.71,248.8-84.81V0Z" fill="currentColor"></path>
        </svg>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch, computed } from 'vue'
import { useRouter, RouterLink, useRoute } from 'vue-router'
import { getLoginUserUsingGet, userLogoutUsingPost } from '../a/api/userController'
import BackgroundFrame from '../components/BackgroundFrame.vue'

const router = useRouter()
const route = useRoute()
const isBackgroundRoute = computed(() => route.path === '/background')

// 返回上一页
const handleBgBack = () => {
  if (history.length > 1) router.back()
  else router.push('/')
}

// 用户信息类型定义
interface LoginUserVO {
  createTime?: string
  editText?: string
  id?: number
  updateTime?: string
  userAccount?: string
  userAvatar?: string
  userName?: string
  userProfile?: string
  userRole?: string
}

// 用户信息
const userInfo = ref<LoginUserVO | null>(null)

// 用户下拉菜单显示状态
const showUserDropdown = ref(false)

// 登录状态
const isLoggedIn = ref(false)

// 关闭用户下拉菜单
const closeUserDropdown = () => {
  showUserDropdown.value = false
}

// 切换用户下拉菜单显示状态
const toggleUserDropdown = () => {
  showUserDropdown.value = !showUserDropdown.value
}

// 检查登录状态
const checkLoginStatus = () => {
  const storedLoginStatus = localStorage.getItem('isLoggedIn')
  const storedUserInfo = localStorage.getItem('userInfo')
  
  if (storedLoginStatus === 'true' && storedUserInfo) {
    try {
      userInfo.value = JSON.parse(storedUserInfo)
      isLoggedIn.value = true
      return true
    } catch (error) {
      console.error('解析用户信息失败:', error)
      localStorage.removeItem('isLoggedIn')
      localStorage.removeItem('userInfo')
      isLoggedIn.value = false
      return false
    }
  } else {
    isLoggedIn.value = false
    return false
  }
}

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    // 检查本地登录状态
    if (!checkLoginStatus()) {
      return
    }
    
    // 调用 API 获取最新用户信息
    const response = await getLoginUserUsingGet()
    if (response.data?.code === 0 && response.data?.data) {
      userInfo.value = response.data.data
      isLoggedIn.value = true
      // 更新 localStorage
      localStorage.setItem('userInfo', JSON.stringify(response.data.data))
      localStorage.setItem('isLoggedIn', 'true')
    } else {
      console.error('获取用户信息失败:', response.data?.message)
      // 如果获取失败，清除登录状态
      handleLogout()
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    // 如果获取失败，清除登录状态
    handleLogout()
  }
}

// 跳转到用户认证页面
const goToUserAuth = () => {
  router.push('/user/UserAuth')
}

// 跳转到用户个人资料页面
const goToUserProfile = () => {
  router.push('/user/UserProfile')
}

// 跳转到设置页面
const goToSettings = () => {
  router.push('/user/Settings')
}

// 处理退出登录
const handleLogout = async () => {
  try {
    // 调用退出登录 API
    await userLogoutUsingPost()
  } catch (error) {
    console.error('退出登录 API 调用失败:', error)
  } finally {
    // 清除本地存储
    localStorage.removeItem('isLoggedIn')
    localStorage.removeItem('userInfo')
    userInfo.value = null
    isLoggedIn.value = false
    showUserDropdown.value = false
    
    // 跳转到登录页面
    router.push('/user/UserAuth')
  }
}

// 监听路由变化，检查登录状态
watch(() => route.path, (newPath) => {
  // 只在访问需要登录的页面时检查，避免重复检查
  if (newPath.startsWith('/admin/') && !isLoggedIn.value) {
    if (!checkLoginStatus()) {
      router.push('/user/UserAuth')
    }
  }
}, { immediate: false }) // 改为 false，避免初始化时重复检查

// 监听 localStorage 变化
const handleStorageChange = (e: StorageEvent) => {
  if (e.key === 'isLoggedIn' || e.key === 'userInfo') {
    checkLoginStatus()
  }
}

// 点击外部关闭下拉菜单
const handleClickOutside = (event: Event) => {
  const target = event.target as HTMLElement
  if (!target.closest('.user-section')) {
    showUserDropdown.value = false
  }
}

onMounted(() => {
  fetchUserInfo()
  // 监听 localStorage 变化
  window.addEventListener('storage', handleStorageChange)
  // 监听点击事件
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  window.removeEventListener('storage', handleStorageChange)
  document.removeEventListener('click', handleClickOutside)
})
</script>

<script lang="ts">
export default {
  name: 'BasicLayout'
}
</script>

<style scoped>
.basic-layout {
  min-height: 100vh;
  width: 100%;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  background: transparent;
}

/* 背景页返回按钮 */
.bg-back-button {
  position: fixed;
  top: 16px;
  left: 16px;
  z-index: 99999;
  padding: 8px 12px;
  border: none;
  border-radius: 8px;
  color: #fff;
  cursor: pointer;
  background: linear-gradient(135deg, #667eea, #2a5298);
  box-shadow: 0 6px 16px rgba(0,0,0,.25);
  pointer-events: auto;
}
.bg-back-button:hover { transform: translateY(-1px); box-shadow: 0 10px 24px rgba(0,0,0,.3); }

/* 让前景内容层级在背景之上 */
.foreground { z-index: 1; position: relative; }

/* 顶部导航样式（暗化） */
.header {
  background: var(--bg-header);
  backdrop-filter: blur(16px);
  border-bottom: 1px solid var(--border-color);
  padding: 0;
  position: sticky;
  top: 0;
  z-index: 1000;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.25);
}

.header-container {
  max-width: 100%;
  margin: 0;
  padding: 0 2rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 80px;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.logo-icon {
  width: 40px;
  height: 40px;
  color: #667eea;
  filter: drop-shadow(0 0 10px rgba(102, 126, 234, 0.3));
}

.logo-text {
  font-size: 1.75rem;
  font-weight: 800;
  color: #1e293b;
  background: linear-gradient(45deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.nav-menu {
  display: flex;
  gap: 2rem;
}

.nav-item {
  color: var(--text-secondary);
  text-decoration: none;
  font-weight: 500;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  transition: all 0.3s ease;
  position: relative;
}

.nav-item:hover,
.nav-item.active { color: #a5b4fc; background: var(--hover-bg); }

.nav-item::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 50%;
  width: 0;
  height: 2px;
  background: #667eea;
  transition: all 0.3s ease;
  transform: translateX(-50%);
}

.nav-item:hover::after,
.nav-item.active::after {
  width: 80%;
}

.user-section {
  display: flex;
  align-items: center;
  gap: 1rem;
  color: #1e293b;
}

.user-avatar {
  position: relative;
  cursor: pointer;
  transition: all 0.3s ease;
}

.user-avatar:hover {
  transform: scale(1.05);
}

.user-avatar img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: 2px solid rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
}

.avatar-glow {
  position: absolute;
  top: -2px;
  left: -2px;
  right: -2px;
  bottom: -2px;
  border-radius: 50%;
  background: linear-gradient(45deg, #ff6b6b, #4ecdc4, #45b7d1);
  z-index: -1;
  opacity: 0;
  transition: all 0.3s ease;
}

.user-avatar:hover img {
  border-color: rgba(102, 126, 234, 0.8);
  transform: scale(1.1);
}

.user-avatar:hover .avatar-glow {
  opacity: 0.6;
  animation: rotate 2s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 用户名与下拉箭头颜色 */
.username { color: var(--text-secondary); }
.user-dropdown svg { color: var(--text-secondary); }

/* 用户下拉菜单 */
.user-dropdown-menu {
  position: absolute;
  top: 100%;
  right: 0;
  width: 280px;
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(20px);
  border-radius: 16px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.2);
  z-index: 1001;
  margin-top: 0.5rem;
  animation: dropdownSlideIn 0.3s ease-out;
  overflow: hidden;
}

@keyframes dropdownSlideIn {
  from {
    opacity: 0;
    transform: translateY(-10px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.dropdown-header {
  padding: 1.5rem;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(42, 82, 152, 0.1));
  border-bottom: 1px solid rgba(102, 126, 234, 0.1);
}

.dropdown-user-info {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.dropdown-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  border: 3px solid rgba(102, 126, 234, 0.3);
  object-fit: cover;
}

.dropdown-user-details {
  flex: 1;
}

.dropdown-username {
  font-size: 1.1rem;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 0.25rem;
}

.dropdown-account {
  font-size: 0.9rem;
  color: #64748b;
  font-family: 'Courier New', monospace;
}

.dropdown-divider {
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(102, 126, 234, 0.2), transparent);
  margin: 0.5rem 0;
}

.dropdown-menu-items {
  padding: 0.5rem 0;
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem 1.5rem;
  cursor: pointer;
  transition: all 0.3s ease;
  color: #475569;
}

.dropdown-item:hover {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
  transform: translateX(5px);
}

.dropdown-item svg {
  width: 18px;
  height: 18px;
  color: currentColor;
}

.dropdown-item span {
  font-size: 0.95rem;
  font-weight: 500;
}

.logout-item {
  color: #ef4444;
}

.logout-item:hover {
  background: rgba(239, 68, 68, 0.1);
  color: #dc2626;
}

.logout-item svg {
  color: currentColor;
}

/* 中间内容区域 */
.main-content {
  flex: 1;
  width: 100%;
  margin: 0;
  padding: 0;
  background: transparent;
  min-height: calc(100vh - 80px);
}

/* 底部样式（暗化） */
.footer {
  background: linear-gradient(135deg, rgba(2,6,23,0.9) 0%, rgba(15,23,42,0.9) 100%);
  color: var(--text-primary);
  padding: 3rem 0 1rem;
  position: relative;
}

.footer-container {
  width: 100%;
  max-width: 100%;
  margin: 0;
  padding: 0 2rem;
  display: grid;
  grid-template-columns: 2fr 1fr 1fr;
  gap: 3rem;
}

.footer-section h3,
.footer-section h4 {
  margin-bottom: 1rem;
  color: #fff;
}

.footer-title {
  font-size: 1.5rem;
  font-weight: 700;
  background: linear-gradient(45deg, #60a5fa, #a78bfa);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.footer-subtitle {
  font-size: 1.1rem;
  font-weight: 600;
  color: #cbd5e1;
}

.footer-desc {
  color: #94a3b8;
  line-height: 1.6;
  margin-bottom: 1.5rem;
}

.contact-info {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  color: #94a3b8;
  font-size: 0.9rem;
}

.footer-links {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.footer-link {
  color: var(--text-secondary);
  text-decoration: none;
  transition: all 0.3s ease;
  padding: 0.25rem 0;
}

.footer-link:hover {
  color: #93c5fd;
  transform: translateX(5px);
}

.social-links {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.social-link {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  color: #fff;
  transition: all 0.3s ease;
}

.social-link:hover {
  background: #60a5fa;
  transform: translateY(-3px);
  box-shadow: 0 10px 20px rgba(96, 165, 250, 0.3);
}

.social-link svg {
  width: 20px;
  height: 20px;
}

.newsletter {
  display: flex;
  gap: 0.5rem;
}

.newsletter-input {
  flex: 1;
  padding: 0.75rem;
  border: none;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
  outline: none;
}

.newsletter-input::placeholder {
  color: #94a3b8;
}

.newsletter-btn {
  padding: 0.75rem 1rem;
  background: linear-gradient(45deg, #60a5fa, #a78bfa);
  border: none;
  border-radius: 8px;
  color: #fff;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.newsletter-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(96, 165, 250, 0.3);
}

.wave-divider {
  position: absolute;
  top: -2px;
  left: 0;
  right: 0;
  height: 60px;
  overflow: hidden;
}

.wave-divider svg {
  width: 100%;
  height: 100%;
  color: #f8fafc;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .header-container {
    padding: 0 1.5rem;
  }
  
  .footer-container {
    padding: 0 1.5rem;
    gap: 2rem;
  }
  
  .user-dropdown-menu {
    width: 260px;
  }
}

@media (max-width: 768px) {
  .header-container {
    padding: 0 1rem;
    height: 70px;
  }
  
  .logo-text {
    font-size: 1.5rem;
  }
  
  .nav-menu {
    display: none;
  }
  
  .user-section {
    gap: 0.5rem;
    position: relative;
  }
  
  .username {
    display: none;
  }
  
  .user-dropdown-menu {
    width: 240px;
    right: -1rem;
  }
  
  .dropdown-header {
    padding: 1rem;
  }
  
  .dropdown-user-info {
    gap: 0.75rem;
  }
  
  .dropdown-avatar {
    width: 40px;
    height: 40px;
  }
  
  .dropdown-username {
    font-size: 1rem;
  }
  
  .dropdown-account {
    font-size: 0.8rem;
  }
  
  .dropdown-item {
    padding: 0.6rem 1rem;
  }
  
  .footer-container {
    grid-template-columns: 1fr;
    gap: 2rem;
    padding: 0 1rem;
  }
  
  .footer-section {
    text-align: center;
  }
  
  .social-links {
    justify-content: center;
  }
  
  .newsletter {
    flex-direction: column;
    gap: 1rem;
  }
}

@media (max-width: 480px) {
  .header-container {
    padding: 0 0.75rem;
    height: 60px;
  }
  
  .logo-icon {
    width: 32px;
    height: 32px;
  }
  
  .logo-text {
    font-size: 1.25rem;
  }
  
  .user-avatar img {
    width: 32px;
    height: 32px;
  }
  
  .user-dropdown-menu {
    width: 220px;
    right: -0.75rem;
  }
  
  .dropdown-header {
    padding: 0.75rem;
  }
  
  .dropdown-user-info {
    gap: 0.5rem;
  }
  
  .dropdown-avatar {
    width: 36px;
    height: 36px;
  }
  
  .dropdown-username {
    font-size: 0.9rem;
  }
  
  .dropdown-account {
    font-size: 0.75rem;
  }
  
  .dropdown-item {
    padding: 0.5rem 0.75rem;
    font-size: 0.9rem;
  }
  
  .footer-container {
    padding: 0 0.75rem;
  }
}
</style>
