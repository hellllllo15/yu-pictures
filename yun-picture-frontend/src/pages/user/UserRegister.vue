<template>
  <div class="register-container">
    <!-- 动态粒子背景 -->
    <div class="particle-background">
      <div class="particle" v-for="n in 50" :key="n" :style="getParticleStyle(n)"></div>
    </div>
    
    <!-- 流动渐变背景 -->
    <div class="flowing-gradient"></div>
    
    <!-- 左侧动态背景 -->
    <div class="left-dynamic-bg">
      <!-- 渐变光柱 -->
      <div class="gradient-pillar pillar-1"></div>
      <div class="gradient-pillar pillar-2"></div>
      
      <!-- 浮动光点 -->
      <div class="floating-light light-1"></div>
      <div class="floating-light light-2"></div>
      <div class="floating-light light-3"></div>
      
      <!-- 动态边框 -->
      <div class="dynamic-border left-border"></div>
    </div>
    
    <!-- 右侧动态背景 -->
    <div class="right-dynamic-bg">
      <!-- 科技线条 -->
      <div class="tech-line tech-line-1"></div>
      <div class="tech-line tech-line-2"></div>
      <div class="tech-line tech-line-3"></div>
      
      <!-- 脉冲圆环 -->
      <div class="pulse-circle circle-1"></div>
      <div class="pulse-circle circle-2"></div>
      
      <!-- 动态边框 -->
      <div class="dynamic-border right-border"></div>
    </div>
    
    <!-- 注册表单容器 -->
    <div class="register-form-container" :class="{ 'form-visible': isFormVisible }">
      <!-- 装饰性几何图形 -->
      <div class="geometric-shapes">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
        <div class="shape shape-4"></div>
      </div>
      
      <!-- 注册表单 -->
      <div class="register-form">
        <div class="form-header">
          <h1 class="register-title">
            <span class="title-main">创建账户</span>
            <span class="title-sub">开启智能协同之旅</span>
          </h1>
          <div class="title-decoration">
            <div class="decoration-line"></div>
            <div class="decoration-dot"></div>
            <div class="decoration-line"></div>
          </div>
        </div>
        
        <form @submit.prevent="handleRegister" class="form">
          <div class="input-group">
            <div class="input-wrapper">
              <input 
                type="text" 
                id="userAccount" 
                v-model="formData.userAccount" 
                required
                class="form-input"
                :class="{ 'input-focused': focusedInput === 'userAccount', 'input-error': formErrors.userAccount, 'has-value': hasUserAccount }"
                @focus="focusedInput = 'userAccount'"
                @blur="focusedInput = ''"
                placeholder="请输入用户名"
              />
              <label for="userAccount" class="input-label">用户名</label>
              <div class="input-border"></div>
              <span v-if="formErrors.userAccount" class="error-message">{{ formErrors.userAccount }}</span>
            </div>
          </div>
          
          <div class="input-group">
            <div class="input-wrapper">
              <input 
                type="password" 
                id="userPassword" 
                v-model="formData.userPassword" 
                required
                class="form-input"
                :class="{ 'input-focused': focusedInput === 'userPassword', 'input-error': formErrors.userPassword, 'has-value': hasUserPassword }"
                @focus="focusedInput = 'userPassword'"
                @blur="focusedInput = ''"
                placeholder="请输入密码"
              />
              <label for="userPassword" class="input-label">密码</label>
              <div class="input-border"></div>
              <span v-if="formErrors.userPassword" class="error-message">{{ formErrors.userPassword }}</span>
            </div>
          </div>
          
          <div class="input-group">
            <div class="input-wrapper">
              <input 
                type="password" 
                id="checkPassword" 
                v-model="formData.checkPassword" 
                required
                class="form-input"
                :class="{ 'input-focused': focusedInput === 'checkPassword', 'input-error': formErrors.checkPassword, 'has-value': hasCheckPassword }"
                @focus="focusedInput = 'checkPassword'"
                @blur="focusedInput = ''"
                placeholder="请再次输入密码"
              />
              <label for="checkPassword" class="input-label">确认密码</label>
              <div class="input-border"></div>
              <span v-if="formErrors.checkPassword" class="error-message">{{ formErrors.checkPassword }}</span>
            </div>
          </div>
          
          <div class="form-actions">
            <button type="submit" class="register-btn" :disabled="isLoading">
              <span class="btn-text">{{ isLoading ? '注册中...' : '立即注册' }}</span>
              <div class="btn-glow"></div>
            </button>
          </div>
        </form>
        
        <div class="form-footer">
          <p class="login-link">
            已有账户？ 
            <button type="button" @click="goToLogin" class="link-text">立即登录</button>
          </p>
          <button type="button" @click="goBack" class="back-btn">返回</button>
        </div>
      </div>
    </div>
    </div>
  </template>
  
<script setup lang="ts">
import { ref, onMounted, onUnmounted, reactive, nextTick, computed } from 'vue'
import { useRouter } from 'vue-router'
import { userRegisterUsingPost } from '../../a/api/userController'

const router = useRouter()
const isFormVisible = ref(false)
const focusedInput = ref('')
const isLoading = ref(false)

const formData = reactive({
  userAccount: '',
  userPassword: '',
  checkPassword: ''
})

// 表单验证状态
const formErrors = reactive({
  userAccount: '',
  userPassword: '',
  checkPassword: ''
})

// 计算属性：检测输入框是否有内容
const hasUserAccount = computed(() => formData.userAccount.trim().length > 0)
const hasUserPassword = computed(() => formData.userPassword.trim().length > 0)
const hasCheckPassword = computed(() => formData.checkPassword.trim().length > 0)

// 生成粒子样式
const getParticleStyle = (index: number) => {
  const size = Math.random() * 4 + 2
  const animationDelay = Math.random() * 20
  const animationDuration = Math.random() * 10 + 15
  
  return {
    '--particle-size': `${size}px`,
    '--animation-delay': `${animationDelay}s`,
    '--animation-duration': `${animationDuration}s`
  }
}

// 表单验证
const validateForm = () => {
  // 清空之前的错误信息
  formErrors.userAccount = ''
  formErrors.userPassword = ''
  formErrors.checkPassword = ''
  
  let isValid = true
  
  // 验证用户名
  if (!formData.userAccount.trim()) {
    formErrors.userAccount = '请输入用户名'
    isValid = false
  } else if (formData.userAccount.trim().length < 3) {
    formErrors.userAccount = '用户名至少需要3个字符'
    isValid = false
  }
  
  // 验证密码
  if (!formData.userPassword.trim()) {
    formErrors.userPassword = '请输入密码'
    isValid = false
  } else if (formData.userPassword.trim().length < 6) {
    formErrors.userPassword = '密码至少需要6个字符'
    isValid = false
  }
  
  // 验证确认密码
  if (!formData.checkPassword.trim()) {
    formErrors.checkPassword = '请确认密码'
    isValid = false
  } else if (formData.userPassword !== formData.checkPassword) {
    formErrors.checkPassword = '两次输入的密码不一致'
    isValid = false
  }
  
  return isValid
}

const handleRegister = async () => {
  if (!validateForm()) {
    return
  }
  
  isLoading.value = true
  
  try {
    // 调用注册 API
    const response = await userRegisterUsingPost({
      userAccount: formData.userAccount,
      userPassword: formData.userPassword,
      checkPassword: formData.checkPassword
    })
    
    if (response.data?.code === 0) {
      alert('注册成功！请登录')
      // 跳转到登录页面
      try {
        await router.push('/user/login')
      } catch (error) {
        console.error('注册后跳转失败:', error)
        // 如果跳转失败，尝试重新跳转
        setTimeout(async () => {
          try {
            await router.push('/user/login')
          } catch (retryError) {
            console.error('重试跳转也失败:', retryError)
            // 使用 window.location 作为最后的备选方案
            window.location.href = '/user/login'
          }
        }, 100)
      }
    } else {
      alert(response.data?.message || '注册失败，请重试')
    }
  } catch (error) {
    console.error('注册失败:', error)
    alert('注册失败，请重试')
  } finally {
    isLoading.value = false
  }
}

// 跳转到登录页面
const goToLogin = async () => {
  try {
    await router.push('/user/login')
  } catch (error) {
    console.error('跳转登录页面失败:', error)
    // 如果跳转失败，尝试重新跳转
    setTimeout(async () => {
      try {
        await router.push('/user/login')
      } catch (retryError) {
        console.error('重试跳转也失败:', retryError)
        // 使用 window.location 作为最后的备选方案
        window.location.href = '/user/login'
      }
    }, 100)
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

onMounted(() => {
  // 页面加载动画
  setTimeout(() => {
    isFormVisible.value = true
  }, 300)
  
  // 检查输入框是否有预填内容，如果有则立即更新标签位置
  nextTick(() => {
    // 强制触发一次响应式更新，确保标签位置正确
    if (formData.userAccount.trim()) {
      formData.userAccount = formData.userAccount
    }
    if (formData.userPassword.trim()) {
      formData.userPassword = formData.userPassword
    }
    if (formData.checkPassword.trim()) {
      formData.checkPassword = formData.checkPassword
    }
  })
  
  // 监听页面可见性变化，确保标签位置正确
  const handleVisibilityChange = () => {
    if (!document.hidden) {
      nextTick(() => {
        // 重新检查输入框状态
        if (formData.userAccount.trim()) {
          formData.userAccount = formData.userAccount
        }
        if (formData.userPassword.trim()) {
          formData.userPassword = formData.userPassword
        }
        if (formData.checkPassword.trim()) {
          formData.checkPassword = formData.checkPassword
        }
      })
    }
  }
  
  document.addEventListener('visibilitychange', handleVisibilityChange)
  
  // 清理事件监听器
  onUnmounted(() => {
    document.removeEventListener('visibilitychange', handleVisibilityChange)
  })
})
  </script>

<style scoped>
.register-container {
  width: 100vw;
  height: 100vh;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  min-width: 100vw;
}

/* 动态粒子背景 */
.particle-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.particle {
  position: absolute;
  width: var(--particle-size);
  height: var(--particle-size);
  background: rgba(255, 255, 255, 0.6);
  border-radius: 50%;
  animation: float var(--animation-duration) ease-in-out infinite;
  animation-delay: var(--animation-delay);
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
    opacity: 0.3;
  }
  50% {
    transform: translateY(-100px) rotate(180deg);
    opacity: 0.8;
  }
}

/* 流动渐变背景 */
.flowing-gradient {
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(
    45deg,
    rgba(255, 255, 255, 0.1) 0%,
    rgba(255, 255, 255, 0.05) 25%,
    rgba(255, 255, 255, 0.1) 50%,
    rgba(255, 255, 255, 0.05) 75%,
    rgba(255, 255, 255, 0.1) 100%
  );
  animation: flow 20s linear infinite;
  z-index: 2;
}

@keyframes flow {
  0% {
    transform: rotate(0deg) scale(1);
  }
  100% {
    transform: rotate(360deg) scale(1.2);
  }
}

/* 左侧动态背景 */
.left-dynamic-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 50%;
  height: 100%;
  z-index: 1;
  overflow: hidden;
}

.gradient-pillar {
  position: absolute;
  width: 80px;
  height: 100%;
  background: linear-gradient(to bottom, rgba(255, 255, 255, 0.15), rgba(255, 255, 255, 0.05));
  border-radius: 40px;
  animation: pillarAnimation 4s ease-in-out infinite;
  opacity: 0.8;
}

.pillar-1 {
  left: 15%;
  animation-delay: 0s;
}

.pillar-2 {
  left: 35%;
  animation-delay: 2s;
}

@keyframes pillarAnimation {
  0%, 100% {
    transform: scaleY(0.8);
    opacity: 0.4;
  }
  50% {
    transform: scaleY(1.2);
    opacity: 1;
  }
}

.floating-light {
  position: absolute;
  width: 10px;
  height: 10px;
  background: radial-gradient(circle, rgba(255, 255, 255, 1), rgba(255, 255, 255, 0.6));
  border-radius: 50%;
  animation: floatLight 3s ease-in-out infinite;
  box-shadow: 0 0 15px rgba(255, 255, 255, 0.8);
}

.light-1 {
  top: 25%;
  left: 20%;
  animation-delay: 0s;
}

.light-2 {
  top: 55%;
  right: 25%;
  animation-delay: 1s;
}

.light-3 {
  bottom: 30%;
  left: 40%;
  animation-delay: 2s;
}

@keyframes floatLight {
  0%, 100% {
    transform: translateY(0) scale(1);
    opacity: 0.7;
  }
  50% {
    transform: translateY(-30px) scale(1.8);
    opacity: 1;
  }
}

.dynamic-border {
  position: absolute;
  top: 20px;
  left: 20px;
  right: 20px;
  bottom: 20px;
  border: 2px solid rgba(255, 255, 255, 0.15);
  border-radius: 25px;
  animation: borderPulse 4s ease-in-out infinite;
  z-index: -1;
}

.left-border {
  border-color: rgba(255, 255, 255, 0.15);
}

@keyframes borderPulse {
  0%, 100% {
    transform: scale(1);
    opacity: 0.5;
  }
  50% {
    transform: scale(1.05);
    opacity: 0.9;
  }
}

/* 右侧动态背景 */
.right-dynamic-bg {
  position: absolute;
  top: 0;
  right: 0;
  width: 50%;
  height: 100%;
  z-index: 1;
  overflow: hidden;
}

.tech-line {
  position: absolute;
  width: 100%;
  height: 3px;
  background: linear-gradient(to right, transparent, rgba(255, 255, 255, 0.25), transparent);
  animation: techLineAnimation 3s ease-in-out infinite;
  opacity: 0.9;
  box-shadow: 0 0 10px rgba(255, 255, 255, 0.3);
}

.tech-line-1 {
  top: 15%;
  animation-delay: 0s;
}

.tech-line-2 {
  top: 45%;
  animation-delay: 1s;
}

.tech-line-3 {
  top: 75%;
  animation-delay: 2s;
}

@keyframes techLineAnimation {
  0%, 100% {
    transform: scaleX(0.2);
    opacity: 0.4;
  }
  50% {
    transform: scaleX(1.2);
    opacity: 1;
  }
}

.pulse-circle {
  position: absolute;
  width: 120px;
  height: 120px;
  border: 4px solid rgba(255, 255, 255, 0.25);
  border-radius: 50%;
  animation: pulseCircle 5s ease-in-out infinite;
  opacity: 0.7;
  box-shadow: 0 0 20px rgba(255, 255, 255, 0.2);
}

.circle-1 {
  top: 15%;
  right: 25%;
  animation-delay: 0s;
}

.circle-2 {
  bottom: 20%;
  right: 15%;
  animation-delay: 2.5s;
}

@keyframes pulseCircle {
  0%, 100% {
    transform: scale(0.7);
    opacity: 0.4;
    border-color: rgba(255, 255, 255, 0.15);
  }
  50% {
    transform: scale(1.4);
    opacity: 0.9;
    border-color: rgba(255, 255, 255, 0.4);
  }
}

.right-border {
  border-color: rgba(255, 255, 255, 0.15);
}

.light-sweep {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(to right, transparent, rgba(255, 255, 255, 0.1), transparent);
  animation: sweepAnimation 15s linear infinite;
  z-index: -1;
}

.left-sweep {
  left: -100%;
}

@keyframes sweepAnimation {
  0% {
    left: -100%;
  }
  100% {
    left: 100%;
  }
}

/* 注册表单容器 */
.register-form-container {
  position: relative;
  z-index: 10;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(20px);
  border-radius: 30px;
  padding: 3rem;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transform: translateY(50px) scale(0.9);
  opacity: 0;
  transition: all 0.8s cubic-bezier(0.4, 0, 0.2, 1);
  max-width: 450px;
  width: 90%;
}

.register-form-container.form-visible {
  transform: translateY(0) scale(1);
  opacity: 1;
}

/* 几何装饰图形 */
.geometric-shapes {
  position: absolute;
  top: -20px;
  left: -20px;
  right: -20px;
  bottom: -20px;
  pointer-events: none;
  z-index: -1;
}

.shape {
  position: absolute;
  border: 2px solid rgba(255, 255, 255, 0.1);
  animation: shapeFloat 8s ease-in-out infinite;
}

.shape-1 {
  top: 10%;
  left: 10%;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  animation-delay: 0s;
}

.shape-2 {
  top: 20%;
  right: 15%;
  width: 40px;
  height: 40px;
  transform: rotate(45deg);
  animation-delay: 2s;
}

.shape-3 {
  bottom: 20%;
  left: 20%;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  animation-delay: 4s;
}

.shape-4 {
  bottom: 15%;
  right: 10%;
  width: 30px;
  height: 30px;
  transform: rotate(30deg);
  animation-delay: 6s;
}

@keyframes shapeFloat {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
    opacity: 0.3;
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
    opacity: 0.7;
  }
}

/* 表单头部 */
.form-header {
  text-align: center;
  margin-bottom: 2.5rem;
}

.register-title {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 1.5rem;
}

.title-main {
  font-size: 2.5rem;
  font-weight: 700;
  color: #ffffff;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
  letter-spacing: 1px;
}

.title-sub {
  font-size: 1.1rem;
  color: rgba(255, 255, 255, 0.8);
  font-weight: 300;
}

.title-decoration {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
}

.decoration-line {
  width: 40px;
  height: 2px;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.6), transparent);
}

.decoration-dot {
  width: 8px;
  height: 8px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 50%;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 0.8;
  }
  50% {
    transform: scale(1.2);
    opacity: 1;
  }
}

/* 表单样式 */
.form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.input-group {
  position: relative;
}

.input-wrapper {
  position: relative;
}

.form-input {
  width: 100%;
  padding: 1rem 1.5rem;
  background: rgba(255, 255, 255, 0.1);
  border: none;
  border-radius: 15px;
  color: #ffffff;
  font-size: 1rem;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
}

.form-input::placeholder {
  color: rgba(255, 255, 255, 0.5);
}

.form-input:focus {
  outline: none;
  background: rgba(255, 255, 255, 0.15);
  box-shadow: 0 0 20px rgba(255, 255, 255, 0.2);
}

.form-input.input-error {
  border: 2px solid rgba(255, 107, 107, 0.8);
  background: rgba(255, 107, 107, 0.1);
  box-shadow: 0 0 15px rgba(255, 107, 107, 0.3);
}

.error-message {
  color: #ff6b6b;
  font-size: 0.85rem;
  margin-top: 0.5rem;
  display: block;
  text-align: left;
  padding-left: 0.5rem;
  text-shadow: 0 0 5px rgba(255, 107, 107, 0.3);
}

.input-label {
  position: absolute;
  top: 50%;
  left: 1.5rem;
  transform: translateY(-50%);
  color: rgba(255, 255, 255, 0.7);
  font-size: 1rem;
  transition: all 0.3s ease;
  pointer-events: none;
  background: transparent;
  padding: 0.2rem 0.5rem;
  border-radius: 8px;
  opacity: 0; /* 未上移时完全透明 */
}

/* 当输入框有内容时，标签自动上移并显示 */
.form-input.has-value + .input-label {
  top: 0;
  left: 1rem;
  font-size: 0.8rem;
  color: rgba(255, 255, 255, 0.9);
  background: rgba(102, 126, 234, 0.8);
  transform: translateY(-50%);
  opacity: 1; /* 有内容时完全显示 */
}

/* 当输入框获得焦点时，标签上移并增强样式 */
.form-input:focus + .input-label {
  top: 0;
  left: 1rem;
  font-size: 0.8rem;
  color: rgba(255, 255, 255, 0.9);
  background: rgba(102, 126, 234, 0.9);
  transform: translateY(-50%);
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
  opacity: 1; /* 获得焦点时完全显示 */
}

.input-border {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 2px;
  background: linear-gradient(90deg, #667eea, #764ba2);
  transition: width 0.3s ease;
}

.form-input:focus ~ .input-border {
  width: 100%;
}

/* 注册按钮 */
.form-actions {
  margin-top: 1rem;
}

.register-btn {
  width: 100%;
  padding: 1rem 2rem;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  border-radius: 15px;
  color: #ffffff;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.4);
}

.register-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 15px 40px rgba(102, 126, 234, 0.6);
}

.register-btn:active:not(:disabled) {
  transform: translateY(0);
}

.register-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.btn-text {
  position: relative;
  z-index: 2;
}

.btn-glow {
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.5s ease;
}

.register-btn:hover:not(:disabled) .btn-glow {
  left: 100%;
}

/* 表单底部 */
.form-footer {
  text-align: center;
  margin-top: 2rem;
  padding-top: 1.5rem;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  flex-direction: column;
  gap: 1rem;
  align-items: center;
}

.login-link {
  color: rgba(255, 255, 255, 0.7);
  font-size: 0.95rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.link-text {
  color: #ffffff;
  text-decoration: none;
  font-weight: 600;
  transition: all 0.3s ease;
  border-bottom: 1px solid transparent;
  background: rgba(255, 255, 255, 0.15);
  padding: 0.5rem 1rem;
  border-radius: 8px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  cursor: pointer;
  font-size: 0.9rem;
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.link-text:hover {
  background: rgba(255, 255, 255, 0.25);
  border-color: rgba(255, 255, 255, 0.4);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  transform: translateY(-1px);
}

.link-text:active {
  transform: translateY(0);
}

.back-btn {
  margin-top: 1rem;
  padding: 0.8rem 1.5rem;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 10px;
  color: #ffffff;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.3);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
}

.back-btn:active {
  transform: translateY(1px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .register-form-container {
    padding: 2rem;
    margin: 1rem;
  }
  
  .title-main {
    font-size: 2rem;
  }
  
  .title-sub {
    font-size: 1rem;
  }
}

@media (max-width: 480px) {
  .register-form-container {
    padding: 1.5rem;
  }
  
  .title-main {
    font-size: 1.8rem;
  }
}
</style> 