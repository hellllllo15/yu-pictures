<template>
    <div class="add-picture-container">
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
            <span class="title-icon">🖼️</span>
            <span class="title-text">上传图片</span>
          </h2>
          <p class="form-subtitle">支持拖拽上传或点击选择文件</p>
          <div class="upload-mode-toggle">
            <button type="button" class="toggle-btn" :class="{ active: !isUrlMode }" @click="switchToFile">文件上传</button>
            <button type="button" class="toggle-btn" :class="{ active: isUrlMode }" @click="switchToUrl">URL上传</button>
          </div>
        </div>
        
        <!-- 上传区域 -->
        <div class="upload-section" v-show="!isUrlMode">
          <div 
            class="upload-area"
            :class="{ 
              'drag-over': isDragOver, 
              'has-file': selectedFile,
              'uploading': isUploading 
            }"
            @drop="handleDrop"
            @dragover="handleDragOver"
            @dragleave="handleDragLeave"
            @click="triggerFileInput"
          >
            <div class="upload-content" v-if="!selectedFile">
              <div class="upload-icon">
                <svg width="64" height="64" viewBox="0 0 24 24" fill="none">
                  <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <polyline points="7,10 12,15 17,10" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <line x1="12" y1="15" x2="12" y2="3" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <h3 class="upload-text">拖拽图片到此处或点击选择</h3>
              <p class="upload-hint">支持 JPG、PNG、GIF 格式，最大 10MB</p>
            </div>
            
            <div class="file-preview" v-else>
              <div class="preview-image">
                <img :src="previewUrl" :alt="selectedFile.name" />
              </div>
              <div class="file-info">
                <h4 class="file-name">{{ selectedFile.name }}</h4>
                <p class="file-size">{{ formatFileSize(selectedFile.size) }}</p>
              </div>
              <button class="remove-file" @click="removeFile">
                <span>×</span>
              </button>
            </div>
            
            <div class="upload-progress" v-if="isUploading">
              <div class="progress-bar">
                <div class="progress-fill" :style="{ width: uploadProgress + '%' }"></div>
              </div>
              <span class="progress-text">{{ uploadProgress }}%</span>
            </div>
          </div>
          
          <input 
            ref="fileInput"
            type="file"
            accept="image/*"
            @change="handleFileSelect"
            style="display: none"
          />
        </div>

        <!-- URL 上传区域 -->
        <div class="url-section" v-if="isUrlMode">
          <div class="form-group">
            <label class="form-label">图片 URL *</label>
            <div class="input-wrapper">
              <input
                type="text"
                v-model.trim="urlInput"
                placeholder="请输入以 http 或 https 开头的图片链接"
                class="form-input"
              />
              <div class="input-border"></div>
            </div>
            <p class="url-hint" v-if="urlInput && !isValidUrl">链接必须以 http:// 或 https:// 开头</p>
          </div>
          <div class="url-preview" v-if="isValidUrl">
            <img :src="urlInput" alt="URL 预览" />
          </div>
        </div>
        
        <!-- 图片信息表单 -->
        <form class="picture-form" @submit.prevent="handleSubmit">
          <div class="form-group">
            <label class="form-label">名称 *</label>
            <div class="input-wrapper">
              <input 
                type="text" 
                v-model="formData.name"
                placeholder="请输入图片名称"
                class="form-input"
                required
              />
              <div class="input-border"></div>
            </div>
          </div>
          
          <div class="form-group">
            <label class="form-label">简介</label>
            <div class="input-wrapper">
              <textarea 
                v-model="formData.introduction"
                placeholder="请输入图片简介"
                class="form-textarea"
                rows="4"
              ></textarea>
              <div class="input-border"></div>
            </div>
          </div>
          
          <div class="form-group">
            <label class="form-label">分类</label>
            <div class="input-wrapper">
              <select 
                v-model="formData.category"
                class="form-select"
                placeholder="请选择分类"
              >
                <option value="">请选择分类</option>
                <option v-for="category in categories" :key="category" :value="category">
                  {{ category }}
                </option>
              </select>
              <div class="input-border"></div>
            </div>
          </div>
          
          <div class="form-group">
            <label class="form-label">上传空间</label>
            <div class="input-wrapper">
              <div class="space-selection">
                <div class="space-options">
                  <label class="space-option">
                    <input 
                      type="radio" 
                      v-model="uploadToPrivate" 
                      :value="true"
                      class="space-radio"
                    />
                    <span class="option-text">上传到私密空间</span>
                    <span class="option-hint">（系统会自动创建或使用您的私密空间）</span>
                  </label>
                  <label class="space-option">
                    <input 
                      type="radio" 
                      v-model="uploadToPrivate" 
                      :value="false"
                      class="space-radio"
                    />
                    <span class="option-text">上传到公共空间</span>
                    <span class="option-hint">（所有用户可见）</span>
                  </label>
                </div>
              </div>
              <div class="input-border"></div>
            </div>
          </div>
          
          <div class="form-group">
            <label class="form-label">标签</label>
            <div class="input-wrapper">
              <div class="tags-container">
                <div class="tags-list">
                  <span 
                    v-for="tag in tags" 
                    :key="tag" 
                    class="tag-item"
                    :class="{ 'selected': formData.tags.includes(tag) }"
                    @click="toggleTag(tag)"
                  >
                    {{ tag }}
                  </span>
                </div>
                <div class="selected-tags" v-if="formData.tags.length > 0">
                  <span class="selected-label">已选择：</span>
                  <span 
                    v-for="tag in formData.tags" 
                    :key="tag" 
                    class="selected-tag"
                    @click="removeTag(tag)"
                  >
                    {{ tag }} ×
                  </span>
                </div>
              </div>
              <div class="input-border"></div>
            </div>
          </div>
          
          <div class="form-actions">
            <button 
              type="button" 
              class="btn btn-secondary" 
              @click="resetForm"
              :disabled="isUploading"
            >
              重置
            </button>
            <button 
              type="submit" 
              class="btn btn-primary" 
              :disabled="isUploading || (isUrlMode ? !isValidUrl : !selectedFile)"
            >
              <span v-if="!isUploading">{{ isEditing ? '更新' : '创建' }}</span>
              <span v-else>{{ isEditing ? '更新中...' : '上传中...' }}</span>
              <div class="btn-glow"></div>
            </button>
          </div>
        </form>
      </div>
    </div>
  </template>
  
  <script setup lang="ts">
  import { ref, reactive, onMounted, computed } from 'vue'
  import { useRoute } from 'vue-router'
  import { 
    listPictureTagCategoryUsingGet,
    addPictureByUrlUsingPost,
    addPictureUsingPost
  } from '../../a/api/pictureController'
import { listSpaceVoByPageUsingPost } from '../../a/api/spaceController'
import { useLoginUserStore } from '../../stores/useLoginUserStore'

  // 文件输入引用
  const fileInput = ref<HTMLInputElement>()

  // 状态管理
  const isDragOver = ref(false)
  const isUploading = ref(false)
  const uploadProgress = ref(0)
  const selectedFile = ref<File | null>(null)
  const previewUrl = ref<string>('')
  const isUrlMode = ref(false)
  const urlInput = ref('')
  const isValidUrl = computed(() => /^(https?:)\/\//i.test(urlInput.value))

  const switchToFile = () => { isUrlMode.value = false }
  const switchToUrl = () => { isUrlMode.value = true; removeFile() }

  // 分类和标签数据
  const categories = ref<string[]>([])
  const tags = ref<string[]>([])

  // 空间选择相关
  const uploadToPrivate = ref(true) // 默认选择私密空间
  const userSpaceId = ref<number | null>(null)
  
  // 用户登录状态
  const loginUserStore = useLoginUserStore()

  // 路由参数（用于编辑预填）
  const route = useRoute()
  const editingId = ref<string | number | null>(null)
  const isEditing = computed(() => !!editingId.value)

  // 表单数据
  const formData = reactive({
    name: '',
    introduction: '',
    category: '',
    tags: [] as string[]
  })
  
  // 获取分类和标签数据
  const fetchTagCategories = async () => {
    try {
      const response = await listPictureTagCategoryUsingGet()
      if (response.data?.code === 0 && response.data.data) {
        const data = response.data.data
        categories.value = data.categoryList || []
        tags.value = data.tagList || []
      }
    } catch (error) {
      console.error('获取分类和标签失败:', error)
    }
  }

  // 获取用户空间ID
  const fetchUserSpaceId = async () => {
    try {
      // 确保用户已登录
      if (!loginUserStore.loginUser.id) {
        await loginUserStore.fetchLoginUser()
      }
      
      if (loginUserStore.loginUser.id) {
        const response = await listSpaceVoByPageUsingPost({
          current: 1,
          pageSize: 10,
          userId: loginUserStore.loginUser.id
        })
        
        if (response.data?.code === 0 && response.data.data) {
          const pageData = response.data.data
          const userSpaces = pageData.records || []
          // 如果有用户空间，使用第一个
          if (userSpaces.length > 0) {
            userSpaceId.value = userSpaces[0].id || null
          }
        }
      }
    } catch (error) {
      console.error('获取用户空间失败:', error)
      // 即使获取失败，也不影响上传，后端会自动创建
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

  // 拖拽处理
  const handleDragOver = (e: DragEvent) => {
    e.preventDefault()
    isDragOver.value = true
  }

  const handleDragLeave = (e: DragEvent) => {
    e.preventDefault()
    isDragOver.value = false
  }

  const handleDrop = (e: DragEvent) => {
    e.preventDefault()
    isDragOver.value = false
    
    const files = e.dataTransfer?.files
    if (files && files.length > 0) {
      handleFile(files[0])
    }
  }

  // 文件选择处理
  const triggerFileInput = () => {
    fileInput.value?.click()
  }

  const handleFileSelect = (e: Event) => {
    const target = e.target as HTMLInputElement
    if (target.files && target.files.length > 0) {
      handleFile(target.files[0])
    }
  }

  // 文件处理
  const handleFile = (file: File) => {
    // 验证文件类型
    if (!file.type.startsWith('image/')) {
      showMessage('请选择图片文件', 'error')
      return
    }
    
    // 验证文件大小 (10MB)
    if (file.size > 10 * 1024 * 1024) {
      showMessage('文件大小不能超过 10MB', 'error')
      return
    }
    
    selectedFile.value = file
    
    // 生成预览
    const reader = new FileReader()
    reader.onload = (e) => {
      previewUrl.value = e.target?.result as string
    }
    reader.readAsDataURL(file)
    
    // 自动填充名称
    if (!formData.name) {
      formData.name = file.name.replace(/\.[^/.]+$/, '')
    }
  }

  // 移除文件
  const removeFile = () => {
    selectedFile.value = null
    previewUrl.value = ''
    if (fileInput.value) {
      fileInput.value.value = ''
    }
  }

  // 格式化文件大小
  const formatFileSize = (bytes: number): string => {
    if (bytes === 0) return '0 Bytes'
    const k = 1024
    const sizes = ['Bytes', 'KB', 'MB', 'GB']
    const i = Math.floor(Math.log(bytes) / Math.log(k))
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
  }
  
  // 切换标签选择
  const toggleTag = (tag: string) => {
    if (formData.tags.includes(tag)) {
      formData.tags = formData.tags.filter(t => t !== tag)
    } else {
      formData.tags.push(tag)
    }
  }

  // 移除已选择的标签
  const removeTag = (tag: string) => {
    formData.tags = formData.tags.filter(t => t !== tag)
  }

  // 表单提交
  const handleSubmit = async () => {
    if (!formData.name.trim()) {
      showMessage('请输入图片名称', 'error')
      return
    }
    
    // URL 上传分支
    if (isUrlMode.value) {
      if (!isValidUrl.value) {
        showMessage('请输入以 http 或 https 开头的图片链接', 'error')
        return
      }
      isUploading.value = true
      try {
        // 准备上传参数 - 将基本参数和业务参数都放在params中（URL查询参数）
        const params = {
          name: formData.name,
          introduction: formData.introduction,
          category: formData.category || undefined,
          id: editingId.value || undefined,
          isPublic: !uploadToPrivate.value, // 私密空间为false，公共空间为true
          spaceId: uploadToPrivate.value && userSpaceId.value ? userSpaceId.value : undefined
        } as any
        
        // 同时将业务参数放在body中，确保兼容性
        const body = {
          fileUrl: urlInput.value,
          picName: formData.name,
          tags: formData.tags.length > 0 ? formData.tags : undefined,
          isPublic: !uploadToPrivate.value, // 私密空间为false，公共空间为true
          spaceId: uploadToPrivate.value && userSpaceId.value ? userSpaceId.value : undefined
        } as any
        
        // 添加调试日志
        console.log('URL上传参数:', { params, body })
        
        const resp = await addPictureByUrlUsingPost(
          params,
          body
        )
        if (resp.data?.code === 0) {
          showMessage('图片 URL 上传成功！', 'success')
          resetForm()
          urlInput.value = ''
          isUrlMode.value = false
        } else {
          showMessage(resp.data?.message || '上传失败', 'error')
        }
      } catch (error) {
        console.error('URL 上传错误:', error)
        showMessage('上传失败，请重试', 'error')
      } finally {
        isUploading.value = false
      }
      return
    }

    // 文件上传分支
    if (!selectedFile.value) {
      showMessage('请选择要上传的图片', 'error')
      return
    }
    isUploading.value = true
    uploadProgress.value = 0
    try {
      const progressInterval = setInterval(() => {
        if (uploadProgress.value < 90) {
          uploadProgress.value += Math.random() * 20
        }
      }, 200)
      
      // 准备上传参数 - 将基本参数放在params中，复杂参数放在body中
      const params = {
        name: formData.name,
        introduction: formData.introduction,
        category: formData.category || undefined,
        id: editingId.value || undefined
      } as any
      
      const body = {
        tags: formData.tags.length > 0 ? formData.tags : undefined,
        isPublic: !uploadToPrivate.value, // 私密空间为false，公共空间为true
        spaceId: uploadToPrivate.value && userSpaceId.value ? userSpaceId.value : undefined
      }
      
      // 添加调试日志
      console.log('文件上传参数:', { params, body })
      
      const response = await addPictureUsingPost(
        params,
        body,
        selectedFile.value
      )
      clearInterval(progressInterval)
      uploadProgress.value = 100
      if (response.data?.code === 0) {
        showMessage('图片上传成功！', 'success')
        resetForm()
      } else {
        showMessage(response.data?.message || '上传失败', 'error')
      }
    } catch (error) {
      console.error('上传错误:', error)
      showMessage('上传失败，请检查网络连接', 'error')
    } finally {
      isUploading.value = false
      uploadProgress.value = 0
    }
  }

  // 重置表单
  const resetForm = () => {
    removeFile()
    Object.assign(formData, {
      name: '',
      introduction: '',
      category: '',
      tags: []
    })
    urlInput.value = ''
    // 重置空间选择
    uploadToPrivate.value = true
  }

  // 组件挂载时获取分类和标签数据
  onMounted(() => {
    fetchTagCategories()
    fetchUserSpaceId() // 获取用户空间ID
    // 从路由读取预填数据
    const q = route.query as any
    if (q) {
      if (q.id) editingId.value = q.id
      if (q.name) formData.name = String(q.name)
      if (q.introduction) formData.introduction = String(q.introduction)
      if (q.category) formData.category = String(q.category)
      if (q.tags) {
        try {
          const arr = JSON.parse(q.tags)
          if (Array.isArray(arr)) formData.tags = arr
        } catch { /* ignore */ }
      }
      if (q.url && /^(https?:)\/\//i.test(String(q.url))) {
        isUrlMode.value = true
        urlInput.value = String(q.url)
      }
    }
  })
  </script>
  
  <style scoped>
  .add-picture-container {
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
    max-width: 800px;
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

  .upload-mode-toggle { display: flex; gap: 0.75rem; justify-content: center; margin-top: 1rem; }
  .toggle-btn { padding: 0.5rem 1rem; border: 1px solid rgba(102,126,234,0.4); background: rgba(255,255,255,0.85); color: #1e3c72; border-radius: 999px; cursor: pointer; font-weight: 600; transition: all .2s ease; }
  .toggle-btn.active { background: linear-gradient(135deg, #667eea, #764ba2); color: #fff; border-color: transparent; box-shadow: 0 8px 24px rgba(102,126,234,.35); }
  .toggle-btn:not(.active):hover { background: rgba(255,255,255,1); }

  .url-section { margin-bottom: 2rem; }
  .url-hint { color: #ef4444; margin-top: .5rem; font-size: .9rem; }
  .url-preview { margin-top: 1rem; border: 1px solid rgba(102,126,234,0.25); border-radius: 12px; overflow: hidden; background: rgba(17,24,39,0.6); }
  .url-preview img { width: 100%; max-height: 360px; object-fit: contain; display: block; }
  
  /* 上传区域 */
  .upload-section {
    margin-bottom: 3rem;
  }
  
  .upload-area {
    border: 3px dashed #cbd5e0;
    border-radius: 20px;
    padding: 3rem 2rem;
    text-align: center;
    cursor: pointer;
    transition: all 0.3s ease;
    background: rgba(17,24,39,0.6);
    position: relative;
    overflow: hidden;
    border-color: rgba(148,163,184,0.3);
  }
  
  .upload-area:hover {
    border-color: #667eea;
    background: rgba(17,24,39,0.7);
    transform: translateY(-2px);
  }
  
  .upload-area.drag-over {
    border-color: #667eea;
    background: rgba(102, 126, 234, 0.1);
    transform: scale(1.02);
  }
  
  .upload-area.has-file {
    border-color: #48bb78;
    background: rgba(72, 187, 120, 0.1);
  }
  
  .upload-area.uploading {
    border-color: #ed8936;
    background: rgba(237, 137, 54, 0.1);
  }
  
  .upload-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 1rem;
  }
  
  .upload-icon {
    color: #a0aec0;
    transition: color 0.3s ease;
  }
  
  .upload-area:hover .upload-icon {
    color: #667eea;
  }
  
  .upload-text {
    font-size: 1.5rem;
    font-weight: 600;
    color: #2d3748;
    margin: 0;
  }
  
  .upload-hint {
    color: #718096;
    margin: 0;
    font-size: 0.95rem;
  }
  
  /* 文件预览 */
  .file-preview {
    display: flex;
    align-items: center;
    gap: 1.5rem;
    padding: 1rem;
    background: rgba(17,24,39,0.6);
    border-radius: 15px;
    border: 1px solid rgba(72, 187, 120, 0.25);
  }
  
  .preview-image {
    width: 80px;
    height: 80px;
    border-radius: 10px;
    overflow: hidden;
    border: 2px solid rgba(72, 187, 120, 0.3);
  }
  
  .preview-image img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  
  .file-info {
    flex: 1;
  }
  
  .file-name {
    font-size: 1.1rem;
    font-weight: 600;
    color: #2d3748;
    margin: 0 0 0.5rem 0;
  }
  
  .file-size {
    color: #718096;
    margin: 0;
    font-size: 0.9rem;
  }
  
  .remove-file {
    width: 40px;
    height: 40px;
    border: none;
    background: rgba(245, 101, 101, 0.1);
    color: #e53e3e;
    border-radius: 50%;
    cursor: pointer;
    font-size: 1.5rem;
    font-weight: bold;
    transition: all 0.3s ease;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .remove-file:hover {
    background: rgba(245, 101, 101, 0.2);
    transform: scale(1.1);
  }
  
  /* 上传进度 */
  .upload-progress {
    margin-top: 1rem;
    text-align: center;
  }
  
  .progress-bar {
    width: 100%;
    height: 8px;
    background: rgba(237, 137, 54, 0.2);
    border-radius: 4px;
    overflow: hidden;
    margin-bottom: 0.5rem;
  }
  
  .progress-fill {
    height: 100%;
    background: linear-gradient(90deg, #ed8936, #f56565);
    border-radius: 4px;
    transition: width 0.3s ease;
  }
  
  .progress-text {
    color: #ed8936;
    font-weight: 600;
    font-size: 0.9rem;
  }
  
  /* 表单样式 */
  .picture-form {
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
  .form-textarea {
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
  .form-textarea:focus {
    outline: none;
    border-color: #667eea;
    background: rgba(255, 255, 255, 1);
    box-shadow: 0 0 20px rgba(102, 126, 234, 0.15);
  }
  
  .form-textarea {
    resize: vertical;
    min-height: 100px;
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
  .form-textarea:focus ~ .input-border {
    width: 100%;
  }
  
  /* 空间选择样式 */
  .space-selection {
    padding: 1rem;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 12px;
    border: 1px solid rgba(102, 126, 234, 0.2);
  }
  
  .space-options {
    display: flex;
    gap: 2rem;
  }
  
  .space-option {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    cursor: pointer;
    padding: 0.5rem 1rem;
    border-radius: 8px;
    transition: all 0.3s ease;
  }
  
  .space-option:hover {
    background: rgba(102, 126, 234, 0.1);
  }
  
  .space-radio {
    width: 18px;
    height: 18px;
    accent-color: #667eea;
    cursor: pointer;
  }
  
  .option-text {
    font-weight: 600;
    color: #1e3c72;
    font-size: 0.95rem;
  }
  
  .option-hint {
    font-size: 0.8rem;
    color: #718096;
    font-weight: normal;
    margin-left: 0.5rem;
  }
  
  /* 下拉框样式 */
  .form-select {
    width: 100%;
    padding: 0.8rem 1rem;
    border: 2px solid rgba(102, 126, 234, 0.2);
    border-radius: 10px;
    background: rgba(255, 255, 255, 0.8);
    color: #1e3c72;
    font-size: 0.95rem;
    transition: all 0.3s ease;
    cursor: pointer;
  }
  
  .form-select:focus {
    outline: none;
    border-color: #667eea;
    background: rgba(255, 255, 255, 1);
    box-shadow: 0 0 15px rgba(102, 126, 234, 0.2);
  }
  
  .form-select option {
    background: white;
    color: #1e3c72;
    padding: 0.5rem;
  }
  
  .form-select option:hover {
    background: rgba(102, 126, 234, 0.1);
  }
  
  /* 标签选择器样式 */
  .tags-container {
    display: flex;
    flex-wrap: wrap;
    gap: 0.5rem;
    padding: 0.5rem;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 10px;
    border: 1px solid rgba(102, 126, 234, 0.2);
  }

  .tags-list {
    display: flex;
    flex-wrap: wrap;
    gap: 0.5rem;
  }

  .tag-item {
    background: rgba(102, 126, 234, 0.1);
    color: #667eea;
    padding: 0.4rem 0.8rem;
    border-radius: 8px;
    font-size: 0.85rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s ease;
    border: 1px solid rgba(102, 126, 234, 0.3);
  }

  .tag-item.selected {
    background: #667eea;
    color: white;
    border-color: #667eea;
  }

  .selected-tags {
    margin-top: 0.5rem;
    display: flex;
    flex-wrap: wrap;
    gap: 0.5rem;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 8px;
    padding: 0.4rem 0.8rem;
    border: 1px solid rgba(102, 126, 234, 0.2);
  }

  .selected-label {
    color: #a0aec0;
    font-size: 0.85rem;
    margin-right: 0.5rem;
  }

  .selected-tag {
    background: rgba(245, 101, 101, 0.1);
    color: #e53e3e;
    padding: 0.3rem 0.7rem;
    border-radius: 6px;
    font-size: 0.8rem;
    font-weight: 600;
    cursor: pointer;
    border: 1px solid rgba(245, 101, 101, 0.3);
  }

  .selected-tag:hover {
    background: rgba(245, 101, 101, 0.2);
    border-color: rgba(245, 101, 101, 0.5);
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
    .add-picture-container {
      padding: 1rem;
    }
    
    .main-content {
      padding: 2rem 1.5rem;
    }
    
    .form-title {
      font-size: 2rem;
    }
    
    .upload-area {
      padding: 2rem 1rem;
    }
    
    .file-preview {
      flex-direction: column;
      text-align: center;
    }
    
    .form-actions {
      flex-direction: column;
    }
    
    .btn {
      width: 100%;
    }
    
    .space-options {
      flex-direction: column;
      gap: 1rem;
    }
    
    .space-option {
      justify-content: center;
    }
  }
  
  @media (max-width: 480px) {
    .main-content {
      padding: 1.5rem 1rem;
    }
    
    .form-title {
      font-size: 1.8rem;
    }
    
    .upload-text {
      font-size: 1.3rem;
    }
  }
  </style>