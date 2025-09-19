<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { userLoginUsingPost } from '../../a/api/userController'

const router = useRouter()
const isPageLoaded = ref(false)
const isFormVisible = ref(false)
const isBubblesVisible = ref(false)

// 表单数据
const formData = ref({
  userAccount: '',
  userPassword: ''
})

// 表单验证状态
const formErrors = ref({
  userAccount: '',
  userPassword: ''
})

// 登录状态
const isLoggingIn = ref(false)

onMounted(() => {
  // 页面进入动画序列
  setTimeout(() => {
    isPageLoaded.value = true
  }, 100)
  
  setTimeout(() => {
    isFormVisible.value = true
  }, 800)
  
  setTimeout(() => {
    isBubblesVisible.value = true
  }, 1200)
})

// 表单验证
const validateForm = () => {
  formErrors.value = { userAccount: '', userPassword: '' }
  let isValid = true
  
  if (!formData.value.userAccount.trim()) {
    formErrors.value.userAccount = '请输入用户名'
    isValid = false
  }
  
  if (!formData.value.userPassword.trim()) {
    formErrors.value.userPassword = '请输入密码'
    isValid = false
  }
  
  return isValid
}

// 处理登录
const handleLogin = async () => {
  if (!validateForm()) {
    return
  }
  
  isLoggingIn.value = true
  
  try {
    // 调用登录 API
    const response = await userLoginUsingPost({
      userAccount: formData.value.userAccount,
      userPassword: formData.value.userPassword
    })
      
    if (response.data?.code === 0 && response.data?.data) {
      // 登录成功，保存用户信息到 localStorage
      localStorage.setItem('userInfo', JSON.stringify(response.data.data))
      localStorage.setItem('isLoggedIn', 'true')
      
      console.log('登录成功:', response.data.data)
      
      // 处理登录成功后的跳转
      try {
        // 检查是否有重定向参数
        const urlParams = new URLSearchParams(window.location.search)
        const redirectPath = urlParams.get('redirect')
        
        let targetPath = '/'
        if (redirectPath && redirectPath !== '/user/login' && redirectPath !== '/user/UserAuth') {
          targetPath = decodeURIComponent(redirectPath)
        }
        
        await router.push(targetPath)
        console.log('登录成功，跳转完成到:', targetPath)
      } catch (routerError) {
        console.error('路由跳转失败:', routerError)
        // 如果跳转失败，尝试重新跳转
        setTimeout(async () => {
          try {
            await router.push('/')
          } catch (retryError) {
            console.error('重试跳转也失败:', retryError)
            alert('登录成功，但页面跳转失败，请手动刷新')
          }
        }, 100)
      }
    } else {
      // 登录失败
      alert(response.data?.message || '登录失败，请检查用户名和密码')
    }
    
  } catch (error) {
    console.error('登录失败:', error)
    alert('登录失败，请重试')
  } finally {
    isLoggingIn.value = false
  }
}

// 返回用户选择页面
const goBack = async () => {
  try {
    await router.push('/user/UserAuth')
  } catch (error) {
    console.error('返回跳转失败:', error)
    // 如果跳转失败，尝试重新跳转
    setTimeout(async () => {
      try {
        await router.push('/user/UserAuth')
      } catch (retryError) {
        console.error('重试返回跳转也失败:', retryError)
        // 使用 window.location 作为最后的备选方案
        window.location.href = '/user/UserAuth'
      }
    }, 100)
  }
}
</script>

<template>
  <div class="page-container">
    <!-- 翻页动画遮罩 -->
    <div class="page-flip-overlay" :class="{ 'flip-active': isPageLoaded }"></div>
    
    <div class="wrapper" :class="{ 'page-loaded': isPageLoaded }">
      <div class="container" :class="{ 'container-visible': isPageLoaded }">
        <h1 class="title-animation" :class="{ 'title-visible': isPageLoaded }">Welcome</h1>
        
        <form class="form" :class="{ 'form-visible': isFormVisible }" @submit.prevent="handleLogin">
          <div class="input-group">
            <input 
              type="text" 
              v-model="formData.userAccount"
              placeholder="用户名" 
              class="input-animation"
              :class="{ 'error': formErrors.userAccount }"
            >
            <span v-if="formErrors.userAccount" class="error-message">{{ formErrors.userAccount }}</span>
          </div>
          
          <div class="input-group">
            <input 
              type="password" 
              v-model="formData.userPassword"
              placeholder="密码" 
              class="input-animation"
              :class="{ 'error': formErrors.userPassword }"
            >
            <span v-if="formErrors.userPassword" class="error-message">{{ formErrors.userPassword }}</span>
          </div>
          
          <button 
            type="submit" 
            class="button-animation"
            :disabled="isLoggingIn"
          >
            {{ isLoggingIn ? '登录中...' : '登录' }}
          </button>
          
          <div class="form-actions">
            <button type="button" class="back-btn" @click="goBack">返回</button>
          </div>
        </form>
      </div>
      
      <ul class="bg-bubbles" :class="{ 'bubbles-visible': isBubblesVisible }">
        <li class="bubble-item" v-for="i in 10" :key="i"></li>
      </ul>
    </div>
  </div>
</template>

<style scoped>
@import url(https://fonts.googleapis.com/css?family=Source+Sans+Pro:200,300);

:root {
  --prim: #53e3a6;
}

* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
  font-weight: 300;
}

body {
  font-family: 'Source Sans Pro', sans-serif;
  color: white;
  font-weight: 300;
}

body ::-webkit-input-placeholder { /* WebKit browsers */
  font-family: 'Source Sans Pro', sans-serif;
  color: white;
  font-weight: 300;
}

body :-moz-placeholder { /* Mozilla Firefox 4 to 18 */
  font-family: 'Source Sans Pro', sans-serif;
  opacity: 1;
  font-weight: 300;
}

body ::-moz-placeholder { /* Mozilla Firefox 19+ */
  font-family: 'Source Sans Pro', sans-serif;
  opacity: 1;
  font-weight: 300;
}

body :-ms-input-placeholder { /* Internet Explorer 10+ */
  font-family: 'Source Sans Pro', sans-serif;
  font-weight: 300;
}

.page-container {
  position: relative;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  background: linear-gradient(to bottom right, #50a3a2 0%, #53e3a6 100%);
}

/* 翻页动画遮罩 */
.page-flip-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(45deg, #50a3a2, #53e3a6);
  z-index: 9999;
  transform: translateX(-100%);
  transition: transform 0.8s cubic-bezier(0.4, 0, 0.2, 1);
  pointer-events: none; /* 确保遮罩不阻挡点击 */
}

.page-flip-overlay.flip-active {
  transform: translateX(100%);
}

.wrapper {
  background: transparent;
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  
  opacity: 0;
  transform: scale(0.9) translateY(20px);
  transition: all 1s cubic-bezier(0.4, 0, 0.2, 1);
}

.wrapper.page-loaded {
  opacity: 1;
  transform: scale(1) translateY(0);
}

.container {
  max-width: 600px;
  margin: 0 auto;
  padding: 80px 20px;
  height: 100%;
  text-align: center;
  display: flex;
  flex-direction: column;
  justify-content: center;
  
  opacity: 0;
  transform: translateY(30px);
  transition: all 0.8s cubic-bezier(0.4, 0, 0.2, 1) 0.3s;
}

.container.container-visible {
  opacity: 1;
  transform: translateY(0);
}

.container h1 {
  font-size: 48px;
  transition-duration: 1s;
  transition-timing-function: ease-in-out;
  font-weight: 200;
  margin-bottom: 40px;
  color: white;
}

.title-animation {
  opacity: 0;
  transform: translateY(20px);
  transition: all 0.8s cubic-bezier(0.4, 0, 0.2, 1) 0.5s;
}

.title-animation.title-visible {
  opacity: 1;
  transform: translateY(0);
}

.form {
  opacity: 0;
  transform: translateY(30px);
  transition: all 0.8s cubic-bezier(0.4, 0, 0.2, 1) 0.8s;
  max-width: 400px;
  margin: 0 auto;
  position: relative;
  z-index: 10; /* 确保表单在最上层 */
}

.form.form-visible {
  opacity: 1;
  transform: translateY(0);
}

.input-group {
  margin-bottom: 20px;
  text-align: left;
  position: relative;
  z-index: 10; /* 确保输入框在最上层 */
}

.input-animation {
  width: 100%;
  padding: 15px 20px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 25px;
  background: rgba(255, 255, 255, 0.1);
  color: white;
  font-size: 16px;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
  position: relative;
  z-index: 10; /* 确保输入框在最上层 */
  pointer-events: auto; /* 确保可以点击 */
}

.input-animation::placeholder {
  color: rgba(255, 255, 255, 0.7);
}

.input-animation:focus {
  outline: none;
  border-color: rgba(255, 255, 255, 0.8);
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
}

.input-animation.error {
  border-color: #ff6b6b;
  background: rgba(255, 107, 107, 0.1);
}

.error-message {
  color: #ff6b6b;
  font-size: 14px;
  margin-top: 5px;
  display: block;
  text-align: left;
  padding-left: 20px;
}

.button-animation {
  width: 100%;
  padding: 15px 20px;
  border: none;
  border-radius: 25px;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
  margin-bottom: 20px;
  position: relative;
  z-index: 10; /* 确保按钮在最上层 */
  pointer-events: auto; /* 确保可以点击 */
}

.button-animation:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
}

.button-animation:active:not(:disabled) {
  transform: translateY(0);
}

.button-animation:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 15px;
  position: relative;
  z-index: 10; /* 确保按钮组在最上层 */
}

.back-btn {
  padding: 10px 20px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 20px;
  background: transparent;
  color: white;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  z-index: 10; /* 确保返回按钮在最上层 */
  pointer-events: auto; /* 确保可以点击 */
}

.back-btn:hover {
  border-color: rgba(255, 255, 255, 0.6);
  background: rgba(255, 255, 255, 0.1);
}

.bg-bubbles {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 1;
  list-style: none;
  opacity: 0;
  transition: opacity 1s ease 1.2s;
  pointer-events: none; /* 确保气泡不阻挡点击 */
}

.bg-bubbles.bubbles-visible {
  opacity: 1;
}

.bubble-item {
  position: absolute;
  bottom: -160px;
  width: 40px;
  height: 40px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  opacity: 0.5;
  animation: square 25s infinite;
  pointer-events: none; /* 确保气泡不阻挡点击 */
}

.bubble-item:nth-child(1) {
  left: 10%;
  animation-delay: 0s;
}

.bubble-item:nth-child(2) {
  left: 20%;
  width: 80px;
  height: 80px;
  animation-delay: 2s;
  animation-duration: 17s;
}

.bubble-item:nth-child(3) {
  left: 25%;
  animation-delay: 4s;
}

.bubble-item:nth-child(4) {
  left: 40%;
  width: 60px;
  height: 60px;
  animation-delay: 6s;
  animation-duration: 22s;
}

.bubble-item:nth-child(5) {
  left: 70%;
  animation-delay: 8s;
}

.bubble-item:nth-child(6) {
  left: 80%;
  width: 120px;
  height: 120px;
  animation-delay: 10s;
  animation-duration: 19s;
}

.bubble-item:nth-child(7) {
  left: 70%;
  width: 160px;
  height: 160px;
  animation-delay: 12s;
  animation-duration: 25s;
}

.bubble-item:nth-child(8) {
  left: 75%;
  width: 20px;
  height: 20px;
  animation-delay: 14s;
  animation-duration: 12s;
}

.bubble-item:nth-child(9) {
  left: 80%;
  width: 160px;
  height: 160px;
  animation-delay: 16s;
  animation-duration: 22s;
}

.bubble-item:nth-child(10) {
  left: 85%;
  width: 40px;
  height: 40px;
  animation-delay: 18s;
  animation-duration: 15s;
}

@keyframes square {
  0% {
    transform: translateY(0) rotate(0deg);
    opacity: 0.5;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
  100% {
    transform: translateY(-1000px) rotate(720deg);
    opacity: 0;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .container {
    padding: 40px 20px;
  }
  
  .container h1 {
    font-size: 36px;
    margin-bottom: 30px;
  }
  
  .form {
    max-width: 100%;
  }
  
  .input-animation,
  .button-animation {
    padding: 12px 16px;
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  .container h1 {
    font-size: 28px;
    margin-bottom: 25px;
  }
  
  .input-animation,
  .button-animation {
    padding: 10px 14px;
    font-size: 13px;
  }
}
</style> 