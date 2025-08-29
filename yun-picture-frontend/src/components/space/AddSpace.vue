<template>
  <div class="add-space-container">
    <!-- 动态背景 -->
    <div class="background-decoration">
      <div class="floating-shapes">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
      </div>
    </div>
    
    <!-- 主要内容 -->
    <div class="main-content">
      <div class="form-header">
        <h2 class="form-title">
          <span class="title-icon">🚀</span>
          <span class="title-text">创建空间</span>
        </h2>
        <p class="form-subtitle">创建您的专属图片存储空间</p>
      </div>
      
      <!-- 空间信息表单 -->
      <form class="space-form" @submit.prevent="handleSubmit">
        <div class="form-group">
          <label class="form-label">空间名称 *</label>
          <div class="input-wrapper">
            <input 
              type="text" 
              v-model="formData.spaceName"
              placeholder="请输入空间名称"
              class="form-input"
              required
            />
            <div class="input-border"></div>
          </div>
        </div>
        
        <div class="form-group">
          <label class="form-label">空间类型 *</label>
          <div class="input-wrapper">
            <select 
              v-model="formData.spaceType"
              class="form-select"
              required
            >
              <option value="">请选择空间类型</option>
              <option value="0">私有空间</option>
              <option value="1">团队空间</option>
            </select>
            <div class="input-border"></div>
          </div>
          <div class="type-description" v-if="formData.spaceType !== ''">
            <p class="description-text">
              {{ formData.spaceType === '0' ? '私有空间：仅供您个人使用的专属空间' : '团队空间：可以邀请其他用户加入的协作空间' }}
            </p>
          </div>
        </div>
        
        <div class="form-group">
          <label class="form-label">空间级别 *</label>
          <div class="input-wrapper">
            <select 
              v-model="formData.spaceLevel"
              class="form-select"
              required
            >
              <option value="">请选择空间级别</option>
              <option 
                v-for="level in spaceLevels" 
                :key="level.value" 
                :value="level.value"
              >
                {{ level.text }} - 最大{{ level.maxCount }}张图片，{{ formatFileSize(level.maxSize) }}
              </option>
            </select>
            <div class="input-border"></div>
          </div>
        </div>
        
        <div class="level-info" v-if="selectedLevelInfo">
          <div class="info-card">
            <h4 class="info-title">{{ selectedLevelInfo.text }}</h4>
            <div class="info-details">
              <div class="info-item">
                <span class="info-label">最大图片数量：</span>
                <span class="info-value">{{ selectedLevelInfo.maxCount }} 张</span>
              </div>
              <div class="info-item">
                <span class="info-label">最大存储空间：</span>
                <span class="info-value">{{ formatFileSize(selectedLevelInfo.maxSize) }}</span>
              </div>
            </div>
          </div>
        </div>
        
        <div class="form-actions">
          <button 
            type="button" 
            class="btn btn-secondary" 
            @click="resetForm"
            :disabled="isSubmitting"
          >
            重置
          </button>
          <button 
            type="submit" 
            class="btn btn-primary" 
            :disabled="isSubmitting || !isFormValid"
          >
            <span v-if="!isSubmitting">{{ isEditing ? '更新' : '创建' }}</span>
            <span v-else>{{ isEditing ? '更新中...' : '创建中...' }}</span>
            <div class="btn-glow"></div>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { addSpaceUsingPost, editSpaceUsingPost, listSpaceLevelUsingGet } from '../../a/api/spaceController'

// 状态管理
const isSubmitting = ref(false)
const spaceLevels = ref<any[]>([])
const router = useRouter()

// 路由参数（用于编辑预填）
const route = useRoute()
const editingId = ref<number | undefined>(undefined)
const isEditing = computed(() => !!editingId.value)

// 表单数据
const formData = reactive({
  spaceName: '',
  spaceType: '', // 新增空间类型字段
  spaceLevel: undefined as number | undefined
})

// 计算属性
const selectedLevelInfo = computed(() => {
  if (!formData.spaceLevel) return null
  return spaceLevels.value.find(level => level.value === formData.spaceLevel)
})

const isFormValid = computed(() => {
  return formData.spaceName.trim() && formData.spaceType !== '' && formData.spaceLevel !== undefined
})

// 获取空间级别列表
const fetchSpaceLevels = async () => {
  try {
    const response = await listSpaceLevelUsingGet()
    if (response.data?.code === 0 && response.data.data) {
      spaceLevels.value = response.data.data
    }
  } catch (error) {
    console.error('获取空间级别失败:', error)
    showMessage('获取空间级别失败', 'error')
  }
}

// 简单的消息提示函数
const showMessage = (message: string, type: 'success' | 'error' = 'success') => {
  const messageDiv = document.createElement('div')
  messageDiv.textContent = message
  messageDiv.style.cssText = `
    position: fixed;
    top: 20px;
    right: 20px;
    padding: 12px 20px;
    border-radius: 8px;
    color: white;
    font-weight: 600;
    z-index: 9999;
    background: ${type === 'success' ? '#10b981' : '#ef4444'};
    box-shadow: 0 4px 12px rgba(0,0,0,0.15);
    animation: slideIn 0.3s ease;
  `
  
  document.body.appendChild(messageDiv)
  
  setTimeout(() => {
    messageDiv.style.animation = 'slideOut 0.3s ease'
    setTimeout(() => document.body.removeChild(messageDiv), 300)
  }, 3000)
}

// 添加CSS动画
const style = document.createElement('style')
style.textContent = `
  @keyframes slideIn {
    from { transform: translateX(100%); opacity: 0; }
    to { transform: translateX(0); opacity: 1; }
  }
  @keyframes slideOut {
    from { transform: translateX(0); opacity: 1; }
    to { transform: translateX(100%); opacity: 0; }
  }
`
document.head.appendChild(style)

// 格式化文件大小
const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 表单提交
const handleSubmit = async () => {
  if (!isFormValid.value) {
    if (!formData.spaceName.trim()) {
      showMessage('请输入空间名称', 'error')
      return
    }
    if (formData.spaceType === '') {
      showMessage('请选择空间类型', 'error')
      return
    }
    if (formData.spaceLevel === undefined) {
      showMessage('请选择空间级别', 'error')
      return
    }
    showMessage('请填写完整的空间信息', 'error')
    return
  }
  
  isSubmitting.value = true
  try {
    if (isEditing.value && editingId.value) {
      // 编辑空间
      const response = await editSpaceUsingPost({
        id: editingId.value,
        spaceName: formData.spaceName
      })
      
      if (response.data?.code === 0) {
        showMessage('空间更新成功！', 'success')
        // 跳转回空间管理页面
        router.push('/admin/spaceManage')
      } else {
        showMessage(response.data?.message || '更新失败', 'error')
      }
    } else if (formData.spaceLevel !== undefined) {
      // 创建空间
      const response = await addSpaceUsingPost({
        spaceName: formData.spaceName,
        spaceType: parseInt(formData.spaceType), // 添加空间类型参数
        spaceLevel: formData.spaceLevel
      })
      
      if (response.data?.code === 0) {
        showMessage('空间创建成功！', 'success')
        resetForm()
      } else {
        showMessage(response.data?.message || '创建失败', 'error')
      }
    } else {
      showMessage('请选择空间级别', 'error')
    }
  } catch (error) {
    console.error('操作失败:', error)
    showMessage('操作失败，请重试', 'error')
  } finally {
    isSubmitting.value = false
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(formData, {
    spaceName: '',
    spaceType: '', // 重置空间类型
    spaceLevel: undefined
  })
}

// 组件挂载时获取空间级别数据
onMounted(() => {
  fetchSpaceLevels()
  
  // 从路由读取预填数据（用于编辑）
  const q = route.query as any
  if (q) {
    if (q.id) editingId.value = Number(q.id) || undefined
    if (q.spaceName) formData.spaceName = String(q.spaceName)
    if (q.spaceType) formData.spaceType = String(q.spaceType)
    if (q.spaceLevel) formData.spaceLevel = Number(q.spaceLevel) || undefined
  }
})
</script>

<style scoped>
.add-space-container {
  width: 100%;
  min-height: 100vh;
  background: transparent;
  padding: 2rem;
  position: relative;
  overflow: hidden;
  opacity: 0.9;
  color: var(--text-primary);
}

/* 背景装饰 */
.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}

.floating-shapes {
  position: relative;
  width: 100%;
  height: 100%;
}

.shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.shape-1 {
  width: 100px;
  height: 100px;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.shape-2 {
  width: 150px;
  height: 150px;
  top: 60%;
  right: 15%;
  animation-delay: 2s;
}

.shape-3 {
  width: 80px;
  height: 80px;
  bottom: 20%;
  left: 20%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
    opacity: 0.3;
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
    opacity: 0.6;
  }
}

/* 主要内容 */
.main-content {
  position: relative;
  z-index: 10;
  max-width: 600px;
  margin: 0 auto;
  background: rgba(17,24,39,0.7);
  color: var(--text-primary);
  backdrop-filter: blur(20px);
  border-radius: 25px;
  padding: 3rem;
  box-shadow: 0 25px 60px rgba(0, 0, 0, 0.2);
  border: 1px solid var(--border-color);
}

/* 表单头部 */
.form-header {
  text-align: center;
  margin-bottom: 3rem;
}

.form-title {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
  font-size: 2.5rem;
  font-weight: 700;
  color: #2d3748;
  margin-bottom: 1rem;
}

.title-icon {
  font-size: 3rem;
  animation: iconBounce 2s ease-in-out infinite;
}

@keyframes iconBounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.form-subtitle {
  color: #718096;
  font-size: 1.1rem;
  margin: 0;
}

/* 表单样式 */
.space-form {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-label {
  font-weight: 600;
  color: #2d3748;
  font-size: 1rem;
}

.input-wrapper {
  position: relative;
}

.form-input,
.form-select {
  width: 100%;
  padding: 1rem 1.2rem;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-size: 1rem;
  color: #2d3748;
  background: rgba(255, 255, 255, 0.9);
  transition: all 0.3s ease;
  box-sizing: border-box;
}

.form-input:focus,
.form-select:focus {
  outline: none;
  border-color: #667eea;
  background: rgba(255, 255, 255, 1);
  box-shadow: 0 0 20px rgba(102, 126, 234, 0.15);
}

.form-select {
  cursor: pointer;
}

.form-select option {
  background: white;
  color: #2d3748;
  padding: 0.5rem;
}

.type-description {
  margin-top: 0.5rem;
}

.description-text {
  color: #718096;
  font-size: 0.9rem;
  margin: 0;
  padding: 0.5rem;
  background: rgba(102, 126, 234, 0.1);
  border-radius: 8px;
  border-left: 3px solid #667eea;
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

.form-input:focus ~ .input-border,
.form-select:focus ~ .input-border {
  width: 100%;
}

/* 级别信息卡片 */
.level-info {
  margin-top: 1rem;
}

.info-card {
  background: rgba(102, 126, 234, 0.1);
  border: 1px solid rgba(102, 126, 234, 0.2);
  border-radius: 12px;
  padding: 1.5rem;
}

.info-title {
  color: #667eea;
  font-size: 1.2rem;
  font-weight: 600;
  margin: 0 0 1rem 0;
  text-align: center;
}

.info-details {
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 0;
  border-bottom: 1px solid rgba(102, 126, 234, 0.1);
}

.info-item:last-child {
  border-bottom: none;
}

.info-label {
  color: #718096;
  font-size: 0.9rem;
}

.info-value {
  color: #2d3748;
  font-weight: 600;
  font-size: 0.9rem;
}

/* 表单操作按钮 */
.form-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  margin-top: 1rem;
}

.btn {
  padding: 1rem 2rem;
  border: none;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  min-width: 120px;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none !important;
}

.btn-secondary {
  background: linear-gradient(135deg, #a0aec0, #718096);
  color: white;
  box-shadow: 0 8px 25px rgba(160, 174, 192, 0.3);
}

.btn-secondary:hover:not(:disabled) {
  transform: translateY(-3px);
  box-shadow: 0 12px 35px rgba(160, 174, 192, 0.4);
}

.btn-primary {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-3px);
  box-shadow: 0 12px 35px rgba(102, 126, 234, 0.4);
}

.btn-glow {
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.6s ease;
}

.btn-primary:hover:not(:disabled) .btn-glow {
  left: 100%;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .add-space-container {
    padding: 1rem;
  }
  
  .main-content {
    padding: 2rem 1.5rem;
  }
  
  .form-title {
    font-size: 2rem;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .btn {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .main-content {
    padding: 1.5rem 1rem;
  }
  
  .form-title {
    font-size: 1.8rem;
  }
}
</style>
