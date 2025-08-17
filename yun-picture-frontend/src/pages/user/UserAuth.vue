<template>
  <div class="dashboard-container">
    <!-- 直接引用原始HTML文件，确保效果完全一样 -->
    <iframe 
      ref="authIframe"
      src="/UserAuth/auth.html" 
      frameborder="0" 
      class="dashboard-iframe"
      allowfullscreen
      @load="onIframeLoad"
    ></iframe>
  </div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const authIframe = ref<HTMLIFrameElement>()

onMounted(() => {
  console.log('UserAuth mounted')
})

const onIframeLoad = () => {
  console.log('UserAuth iframe loaded successfully')
  
  // 监听 iframe 内的消息
  window.addEventListener('message', handleIframeMessage)
}

const handleIframeMessage = (event: MessageEvent) => {
  console.log('收到 iframe 消息:', event.data);
  console.log('消息来源:', event.origin);
  console.log('消息类型:', event.data?.type);
  
  // 检查消息来源和类型
  if (event.data && event.data.type) {
    switch (event.data.type) {
      case 'LOGIN_CLICK':
        console.log('跳转到登录页面')
        try {
          // 添加延迟确保状态稳定
          setTimeout(() => {
            router.push('/user/login').catch(err => {
              console.error('登录页面跳转失败:', err)
              // 如果跳转失败，尝试重新跳转
              setTimeout(() => {
                router.push('/user/login')
              }, 100)
            })
          }, 100)
          console.log('路由跳转已触发')
        } catch (error) {
          console.error('路由跳转失败:', error)
        }
        break
      case 'REGISTER_CLICK':
        console.log('跳转到注册页面')
        try {
          // 添加延迟确保状态稳定
          setTimeout(() => {
            router.push('/user/register').catch(err => {
              console.error('注册页面跳转失败:', err)
              // 如果跳转失败，尝试重新跳转
              setTimeout(() => {
                router.push('/user/register')
              }, 100)
            })
          }, 100)
          console.log('路由跳转已触发')
        } catch (error) {
          console.error('路由跳转失败:', error)
        }
        break
      case 'DEVELOPER_CLICK':
        console.log('开发者页面')
        // 可以跳转到开发者页面或显示提示
        break
      default:
        console.log('未知消息类型:', event.data.type)
    }
  } else {
    console.log('消息格式不正确:', event.data)
  }
}

// 组件卸载时清理事件监听器
onUnmounted(() => {
  window.removeEventListener('message', handleIframeMessage)
})
</script>

<style scoped>
.dashboard-container {
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: #000;
}

.dashboard-iframe {
  width: 100%;
  height: 100%;
  border: none;
  display: block;
  background: transparent;
}
</style> 