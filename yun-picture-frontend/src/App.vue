

<template>
  <div class="app-container">
    <!-- 对于 UserAuth、登录、注册页面，不使用 BasicLayout -->
    <RouterView v-if="['/user/UserAuth', '/user/login', '/user/register'].includes($route.path)" />
    
    <!-- 其他页面使用 BasicLayout -->
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
const protectedRoutes = ['/admin/userManage', '/user/profile', '/user/settings']

// 检查登录状态
const checkLoginStatus = () => {
  const isLoggedIn = localStorage.getItem('isLoggedIn')
  const userInfo = localStorage.getItem('userInfo')
  return isLoggedIn === 'true' && userInfo !== null
}

// 路由守卫
const handleRouteChange = (to: string) => {
  // 如果是受保护的页面且未登录，跳转到登录页面
  if (protectedRoutes.some(route => to.startsWith(route)) && !checkLoginStatus()) {
    router.push('/user/UserAuth')
    return false
  }
  return true
}

// 监听路由变化
watch(() => route.path, (newPath) => {
  handleRouteChange(newPath)
}, { immediate: true })

onMounted(async () => {
  // 页面加载时检查登录状态
  if (route.path !== '/user/UserAuth' && route.path !== '/user/login' && route.path !== '/user/register') {
    if (!checkLoginStatus()) {
      router.push('/user/UserAuth')
    }
  }
})
</script>

<style>
/* 最基本的全局样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
  overflow-x: hidden;
}

.app-container {
  width: 100%;
  min-height: 100vh;
  margin: 0;
  padding: 0;
}

/* 隐藏 Vue DevTools 的组件检查器覆盖层 */
#_vue-devtools-container_,
#_vue-devtools-component-inspector,
#_vue-devtools-component-inspector_card_ {
  display: none !important;
  visibility: hidden !important;
  opacity: 0 !important;
  pointer-events: none !important;
}
</style>
