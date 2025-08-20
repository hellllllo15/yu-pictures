

<template>
  <div class="app-container">
    <!-- 对于 UserAuth、登录、注册 页面，不使用 BasicLayout -->
    <RouterView v-if="['/user/UserAuth', '/user/login', '/user/register'].includes($route.path)" />
    
    <!-- 其他页面使用 BasicLayout 包裹，包括 /background -->
    <BasicLayout v-else>
      <RouterView />
    </BasicLayout>
  </div>
</template>

<script setup lang="ts">
import { onMounted, watch } from 'vue'
import { RouterView, useRoute, useRouter } from 'vue-router'
import BasicLayout from "./layouts/BasicLayout.vue"

const route = useRoute()
const router = useRouter()

// 需要登录的页面路径
const protectedRoutes = ['/admin/userManage', '/user/profile', '/user/settings', '/picture/upload']

// 检查登录状态
const checkLoginStatus = () => {
  const isLoggedIn = localStorage.getItem('isLoggedIn')
  const userInfo = localStorage.getItem('userInfo')
  return isLoggedIn === 'true' && userInfo !== null
}

// 路由守卫
const handleRouteChange = (to: string) => {
  if (protectedRoutes.some(route => to.startsWith(route)) && !checkLoginStatus()) {
    router.push('/user/UserAuth')
    return false
  }
  return true
}

watch(() => route.path, (newPath) => {
  handleRouteChange(newPath)
}, { immediate: true })

onMounted(async () => {
  if (
    route.path !== '/user/UserAuth' &&
    route.path !== '/user/login' &&
    route.path !== '/user/register'
  ) {
    if (!checkLoginStatus()) {
      router.push('/user/UserAuth')
    }
  }
})
</script>

<style>
* { margin: 0; padding: 0; box-sizing: border-box; }
:root {
  --bg-header: rgba(13, 17, 23, 0.75);
  --bg-surface: rgba(17, 24, 39, 0.6);
  --bg-surface-strong: rgba(17, 24, 39, 0.7);
  --text-primary: #e5e7eb;
  --text-secondary: #cbd5e1;
  --border-color: rgba(255, 255, 255, 0.08);
  --hover-bg: rgba(99, 102, 241, 0.12);
}
html, body { margin: 0; padding: 0; width: 100%; height: 100%; overflow-x: hidden; color: var(--text-primary); }
.app-container { width: 100%; min-height: 100vh; margin: 0; padding: 0; position: relative; }
#_vue-devtools-container_, #_vue-devtools-component-inspector, #_vue-devtools-component-inspector_card_ { display: none !important; visibility: hidden !important; opacity: 0 !important; pointer-events: none !important; }
</style>
