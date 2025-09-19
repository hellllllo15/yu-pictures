<template>
  <div class="crawl-page">
    <div class="main-content">
      <div class="form-header">
        <h2 class="title">
          <span class="title-icon">🔍</span>
          <span>以图搜图</span>
        </h2>
        <p class="subtitle">输入图片URL，搜索相似图片（仅展示前 100 张）</p>
      </div>

      <div class="reminder-section">
        <div class="reminder-box">
          <div class="reminder-icon">💡</div>
          <div class="reminder-content">
            <h4 class="reminder-title">使用提醒</h4>
            <p class="reminder-text">支持各种图片格式，输入完整的图片URL地址，系统将自动搜索相似图片</p>
          </div>
        </div>
      </div>

      <div class="main-section">
        <!-- 输入区域 -->
        <form class="crawl-form" @submit.prevent="handleSearch">
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">图片URL地址 *</label>
              <div class="input-wrapper">
                <input 
                  v-model.trim="imageUrl" 
                  type="url" 
                  placeholder="如：https://example.com/image.jpg" 
                  class="form-input" 
                />
                <div class="input-border"></div>
              </div>
            </div>
            <div class="actions">
              <button class="btn btn-primary" type="submit" :disabled="isLoading || !imageUrl">
                <span v-if="!isLoading">开始搜索</span>
                <span v-else>搜索中...</span>
                <div class="btn-glow"></div>
              </button>
            </div>
          </div>
        </form>

        <!-- 输入图片预览 -->
        <div v-if="imageUrl" class="input-preview-section">
          <h3 class="section-title">搜索图片预览</h3>
          <div class="input-preview">
            <div class="input-image-container">
              <img 
                :src="imageUrl" 
                alt="搜索图片" 
                @error="onInputImageError"
                @load="onInputImageLoad"
                :class="{ 'img-loading': inputImageLoading, 'img-error': inputImageError }"
                referrerpolicy="no-referrer"
              />
              <div v-if="inputImageLoading" class="loading-overlay">
                <div class="loading-spinner"></div>
                <div class="loading-text">加载图片中...</div>
              </div>
              <div v-if="inputImageError" class="error-overlay">
                <div class="error-icon">⚠️</div>
                <div class="error-text">图片加载失败</div>
                <div class="error-hint">请检查URL是否正确</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 搜索结果 -->
        <div class="result-section" v-if="searchUrls.length > 0">
          <h3 class="section-title">搜索结果（前 100 张）</h3>
          <div class="grid">
            <div v-for="(url, idx) in displaySearchUrls" :key="url + '-' + idx" class="card">
              <div class="thumb">
                <img 
                  :src="url" 
                  :alt="buildName(idx)" 
                  @error="onImgError(idx, $event)"
                  @load="onImgLoad(idx)"
                  :class="{ 'img-normal': !imageErrorStates[idx] && !imageLoadingStates[idx] }"
                  referrerpolicy="no-referrer"
                />
                <div class="preview-overlay" v-if="!imageLoadingStates[idx]">
                  <button @click="previewImage(url, idx)" class="preview-btn">
                    <span class="preview-icon">👁️</span>
                    <span class="preview-text">预览</span>
                  </button>
                </div>
              </div>
              <div class="meta">
                <div class="name">{{ buildName(idx) }}</div>
                <div class="link" :title="url">{{ shortUrl(url) }}</div>
                <div v-if="imageErrorStates[idx]" class="error-hint">
                  <span class="error-icon-small">⚠️</span>
                  图片加载失败
                  <button @click="retryLoadImage(idx)" class="retry-btn-small">重试</button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div v-else-if="hasSearched" class="empty">
          {{ searchUrls.length === 0 ? '未找到相似图片' : '暂无数据，请输入图片URL后点击搜索' }}
        </div>
        <div v-else class="empty">请输入图片URL后点击搜索</div>
      </div>
    </div>

    <!-- 图片预览模态框 -->
    <div v-if="showPreview" class="preview-modal" @click="closePreview">
      <div class="preview-content" @click.stop>
        <div class="preview-body">
          <div class="preview-image-container">
            <img 
              :src="previewImageUrl" 
              :alt="previewTitle"
              @error="onPreviewImgError"
              @load="onPreviewImgLoad"
              :class="{ 'preview-loading': previewLoading, 'preview-error': previewError }"
            />
            <div v-if="previewLoading" class="preview-loading-overlay">
              <div class="preview-loading-spinner"></div>
              <div class="preview-loading-text">加载中...</div>
            </div>
            <div v-if="previewError" class="preview-error-overlay">
              <div class="preview-error-icon">⚠️</div>
              <div class="preview-error-text">图片加载失败</div>
            </div>
          </div>
          <div class="preview-info">
            <div class="preview-url-section">
              <label class="preview-url-label">图片地址：</label>
              <div class="preview-url-container">
                <input 
                  :value="previewOriginalUrl" 
                  readonly 
                  class="preview-url-input"
                  @click="copyUrl"
                />
                <button @click="copyUrl" class="copy-btn" :title="copyStatus">
                  {{ copyStatus === '已复制' ? '✓' : '📋' }}
                </button>
              </div>
            </div>
            <div class="preview-actions">
              <a :href="previewOriginalUrl" target="_blank" class="preview-action-btn">
                <span class="action-icon">🔗</span>
                在新窗口打开
              </a>
              <button @click="downloadImage" class="preview-action-btn" disabled>
                <span class="action-icon">💾</span>
                下载图片
              </button>
              <button @click="goBackToList" class="preview-action-btn back-btn-inline">
                <span class="action-icon">←</span>
                返回
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
// import { searchImageByUrlUsingPost } from '../../a/api/pictureController'
import request from '../../request'

const imageUrl = ref('')
const isLoading = ref(false)
const hasSearched = ref(false)
const searchUrls = ref<string[]>([])
const imageLoadingStates = ref<boolean[]>([])
const imageErrorStates = ref<boolean[]>([])

// 输入图片预览状态
const inputImageLoading = ref(false)
const inputImageError = ref(false)

// 预览相关状态
const showPreview = ref(false)
const previewOriginalUrl = ref('')
const previewImageUrl = ref('')
const previewTitle = ref('')
const previewLoading = ref(false)
const previewError = ref(false)
const copyStatus = ref('复制地址')

// 图片加载策略管理
const imageLoadStrategies = ref<Map<number, number>>(new Map())

// 定义加载策略
const loadStrategies = [
  { name: '原始URL', url: (originalUrl: string) => originalUrl, attrs: {} },
  { name: '清理后URL', url: (originalUrl: string) => getCleanImageUrl(originalUrl), attrs: {} },
  { name: '添加crossorigin', url: (originalUrl: string) => getCleanImageUrl(originalUrl), attrs: { crossorigin: 'anonymous' } },
  { name: '添加referrerpolicy', url: (originalUrl: string) => getCleanImageUrl(originalUrl), attrs: { referrerpolicy: 'no-referrer' } },
  { name: '两个属性都加', url: (originalUrl: string) => getCleanImageUrl(originalUrl), attrs: { crossorigin: 'anonymous', referrerpolicy: 'no-referrer' } }
]

const displaySearchUrls = computed(() => searchUrls.value.slice(0, 100))

// 输入图片事件处理
const onInputImageError = () => {
  inputImageError.value = true
  inputImageLoading.value = false
}

const onInputImageLoad = () => {
  inputImageLoading.value = false
  inputImageError.value = false
}

// 搜索函数
const handleSearch = async () => {
  if (!imageUrl.value) return
  
  isLoading.value = true
  hasSearched.value = true
  searchUrls.value = []
  imageLoadingStates.value = []
  imageErrorStates.value = []
  
  try {
    console.log('开始以图搜图，输入URL:', imageUrl.value)
    
    const response = await request('/api/picture/search/by-image', {
      method: 'POST',
      params: {
        imageUrl: imageUrl.value
      }
    })
    
    console.log('搜索响应:', response)
    
    if (response.data?.code === 0) {
      const arr = Array.isArray(response.data.data) ? response.data.data : []
      searchUrls.value = arr.filter((u: any) => typeof u === 'string')
      
      console.log('搜索到图片数量:', searchUrls.value.length)
      
      // 初始化图片状态
      imageLoadingStates.value = new Array(searchUrls.value.length).fill(true)
      imageErrorStates.value = new Array(searchUrls.value.length).fill(false)
      
      // 重置所有图片的策略索引
      imageLoadStrategies.value.clear()
      searchUrls.value.forEach((_, idx) => {
        imageLoadStrategies.value.set(idx, 0)
      })
    } else {
      throw new Error(response.data?.message || '搜索失败')
    }
  } catch (e) {
    console.error('以图搜图失败', e)
    searchUrls.value = []
    alert('搜索失败，请稍后重试或检查图片URL是否正确')
  } finally {
    isLoading.value = false
  }
}

// 获取当前策略
const getCurrentStrategy = (idx: number) => {
  const strategyIndex = imageLoadStrategies.value.get(idx) || 0
  return loadStrategies[strategyIndex]
}

// 应用策略到图片元素
const applyStrategyToImage = (img: HTMLImageElement, strategy: any, originalUrl: string) => {
  img.src = strategy.url(originalUrl)
  
  if (strategy.attrs.crossorigin) {
    img.setAttribute('crossorigin', strategy.attrs.crossorigin)
  } else {
    img.removeAttribute('crossorigin')
  }
  
  if (strategy.attrs.referrerpolicy) {
    img.setAttribute('referrerpolicy', strategy.attrs.referrerpolicy)
  } else {
    img.removeAttribute('referrerpolicy')
  }
  
  console.log(`应用策略 [${strategy.name}] 到图片 ${img.alt}: ${strategy.url(originalUrl)}`)
}

const getCleanImageUrl = (url: string) => {
  if (!url) return url
  
  try {
    let cleanedUrl = url
    
    if (url.includes('@')) {
      cleanedUrl = url.split('@')[0]
      console.log(`URL清理(@): ${url} → ${cleanedUrl}`)
    }
    
    if (url.includes('?')) {
      cleanedUrl = url.split('?')[0]
      console.log(`URL清理(?): ${url} → ${cleanedUrl}`)
    }
    
    if (url.includes('#')) {
      cleanedUrl = url.split('#')[0]
      console.log(`URL清理(#): ${url} → ${cleanedUrl}`)
    }
    
    if (cleanedUrl !== url) {
      console.log(`最终清理结果: ${url} → ${cleanedUrl}`)
    }
    
    return cleanedUrl
  } catch (error) {
    console.warn('URL清理失败:', error)
    return url
  }
}

const buildName = (index: number) => {
  return `相似图片${index + 1}`
}

const shortUrl = (u: string, max = 48) => {
  if (!u) return ''
  return u.length > max ? u.slice(0, max) + '...' : u
}

const onImgError = (idx: number, event: Event) => {
  const img = event.target as HTMLImageElement
  const originalUrl = displaySearchUrls.value[idx]
  const currentStrategyIndex = imageLoadStrategies.value.get(idx) || 0
  
  console.warn(`图片加载失败 [${idx}]: 当前策略=${loadStrategies[currentStrategyIndex].name}, URL=${img.src}`)
  
  if (currentStrategyIndex < loadStrategies.length - 1) {
    const nextStrategyIndex = currentStrategyIndex + 1
    const nextStrategy = loadStrategies[nextStrategyIndex]
    
    imageLoadStrategies.value.set(idx, nextStrategyIndex)
    imageLoadingStates.value[idx] = true
    imageErrorStates.value[idx] = false
    
    applyStrategyToImage(img, nextStrategy, originalUrl)
    return
  }
  
  console.error(`图片 [${idx}] 所有策略都尝试失败: ${originalUrl}`)
  imageErrorStates.value[idx] = true
  imageLoadingStates.value[idx] = false
}

const onImgLoad = (idx: number) => {
  const currentStrategyIndex = imageLoadStrategies.value.get(idx) || 0
  const currentStrategy = loadStrategies[currentStrategyIndex]
  
  console.log(`图片加载成功 [${idx}]: 策略=${currentStrategy.name}`)
  
  imageLoadingStates.value[idx] = false
  imageErrorStates.value[idx] = false
}

const retryLoadImage = (idx: number) => {
  imageLoadStrategies.value.set(idx, 0)
  imageErrorStates.value[idx] = false
  imageLoadingStates.value[idx] = true
  
  const img = document.querySelector(`[alt="${buildName(idx)}"]`) as HTMLImageElement
  if (img) {
    const firstStrategy = loadStrategies[0]
    applyStrategyToImage(img, firstStrategy, displaySearchUrls.value[idx])
  }
}

// 预览功能
const previewImage = (url: string, idx: number) => {
  previewOriginalUrl.value = url
  previewImageUrl.value = url
  previewTitle.value = buildName(idx)
  showPreview.value = true
  previewLoading.value = true
  previewError.value = false
  copyStatus.value = '复制地址'
}

const closePreview = () => {
  showPreview.value = false
  previewLoading.value = false
  previewError.value = false
}

const onPreviewImgError = (event: Event) => {
  const img = event.target as HTMLImageElement
  const originalUrl = previewOriginalUrl.value
  const currentSrc = img.src
  
  const isCleanUrl = currentSrc !== originalUrl && !currentSrc.includes('images.weserv.nl')
  
  if (isCleanUrl) {
    console.error(`预览清理后的URL也加载失败: ${currentSrc}`)
    previewError.value = true
    previewLoading.value = false
    return
  }
  
  if (currentSrc.includes('images.weserv.nl')) {
    img.src = originalUrl
    return
  }
  
  const cleanUrl = getCleanImageUrl(originalUrl)
  if (cleanUrl !== originalUrl) {
    console.warn(`预览原图加载失败，正在尝试使用清理后的URL: ${cleanUrl}`)
    previewLoading.value = true
    previewError.value = false
    img.src = cleanUrl
    return
  }
  
  previewError.value = true
  previewLoading.value = false
}

const onPreviewImgLoad = () => {
  previewLoading.value = false
  previewError.value = false
}

const copyUrl = async () => {
  try {
    await navigator.clipboard.writeText(previewOriginalUrl.value)
    copyStatus.value = '已复制'
    setTimeout(() => {
      copyStatus.value = '复制地址'
    }, 2000)
  } catch (err) {
    console.error('复制失败:', err)
    const textArea = document.createElement('textarea')
    textArea.value = previewOriginalUrl.value
    document.body.appendChild(textArea)
    textArea.select()
    document.execCommand('copy')
    document.body.removeChild(textArea)
    copyStatus.value = '已复制'
    setTimeout(() => {
      copyStatus.value = '复制地址'
    }, 2000)
  }
}

const downloadImage = async () => {
  try {
    const response = await fetch(previewOriginalUrl.value)
    const blob = await response.blob()
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `${previewTitle.value}.jpg`
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    window.URL.revokeObjectURL(url)
  } catch (err) {
    console.error('下载失败:', err)
    alert('下载失败，请稍后重试')
  }
}

const goBackToList = () => {
  showPreview.value = false
}

// 监听输入URL变化，设置加载状态
import { watch } from 'vue'
watch(imageUrl, (newUrl) => {
  if (newUrl) {
    inputImageLoading.value = true
    inputImageError.value = false
  }
})
</script>

<style scoped>
.crawl-page {
  width: 100%;
  min-height: 100vh;
  background: transparent;
  padding: 2rem;
  position: relative;
  overflow: hidden;
  opacity: 0.9;
  color: var(--text-primary);
}

.main-content {
  position: relative;
  z-index: 10;
  max-width: 1400px;
  margin: 0 auto;
  background: rgba(17,24,39,0.7);
  color: var(--text-primary);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 2rem;
  box-shadow: 0 25px 60px rgba(0, 0, 0, 0.2);
  border: 1px solid var(--border-color);
}

.form-header { text-align: center; margin-bottom: 1rem; }
.title { display: flex; gap: .6rem; align-items: center; justify-content: center; margin: 0; font-size: 1.6rem; font-weight: 700; color: #e5e7eb; }
.title-icon { font-size: 1.8rem; }
.subtitle { color: var(--text-secondary); margin: .25rem 0 0; }

.reminder-section {
  margin: 1rem 0;
  padding: 1rem;
  background: rgba(102, 126, 234, 0.1);
  border-radius: 12px;
  border: 1px solid rgba(102, 126, 234, 0.3);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.reminder-box {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 0.5rem;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.reminder-icon {
  font-size: 1.5rem;
  color: #667eea;
  flex-shrink: 0;
}

.reminder-content {
  flex: 1;
}

.reminder-title {
  margin: 0 0 0.25rem 0;
  font-size: 0.9rem;
  font-weight: 600;
  color: #e5e7eb;
}

.reminder-text {
  margin: 0;
  font-size: 0.8rem;
  color: var(--text-secondary);
  line-height: 1.4;
}

.main-section {
  width: 100%;
}

.crawl-form { margin-top: 1rem; }
.form-row { 
  display: flex; 
  gap: 1.5rem; 
  flex-wrap: wrap; 
  align-items: flex-end; 
  justify-content: center;
}

.form-group { 
  display: flex; 
  flex-direction: column; 
  gap: .5rem; 
  min-width: 350px; 
  flex: 1;
  max-width: 500px;
}
.form-label { font-weight: 600; color: #cbd5e1; }
.input-wrapper { position: relative; }
.form-input { width: 100%; padding: .8rem 1rem; border: 2px solid rgba(102,126,234,0.2); border-radius: 10px; background: rgba(255,255,255,0.9); color: #1e3c72; }
.input-border { position: absolute; bottom: 0; left: 0; width: 0; height: 2px; background: linear-gradient(90deg, #667eea, #764ba2); transition: width .3s ease; }
.form-input:focus { outline: none; background: rgba(255,255,255,1); border-color: #667eea; }
.form-input:focus ~ .input-border { width: 100%; }

.actions { 
  display: flex; 
  align-items: center; 
  gap: .5rem; 
  justify-content: center;
  min-width: 200px;
}
.btn { padding: .9rem 1.6rem; border: none; border-radius: 10px; font-weight: 700; cursor: pointer; position: relative; overflow: hidden; }
.btn-primary { background: linear-gradient(135deg, #667eea, #764ba2); color: white; box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3); }
.btn-primary:disabled { opacity: .6; cursor: not-allowed; }
.btn-glow { position: absolute; top: 0; left: -100%; width: 100%; height: 100%; background: linear-gradient(90deg, transparent, rgba(255,255,255,.3), transparent); transition: left .6s ease; }
.btn:hover .btn-glow { left: 100%; }

/* 输入图片预览区域 */
.input-preview-section {
  margin: 2rem 0;
  padding: 1.5rem;
  background: rgba(17,24,39,0.5);
  border-radius: 12px;
  border: 1px solid var(--border-color);
}

.input-preview {
  display: flex;
  justify-content: center;
}

.input-image-container {
  position: relative;
  width: 300px;
  height: 200px;
  border-radius: 8px;
  overflow: hidden;
  background: rgba(0,0,0,0.2);
  display: flex;
  align-items: center;
  justify-content: center;
}

.input-image-container img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
  transition: opacity 0.3s ease;
}

.input-image-container img.img-loading {
  opacity: 0.3;
}

.input-image-container img.img-error {
  opacity: 0.1;
}

.result-section { margin-top: 1.5rem; }
.section-title { margin: 0 0 .75rem; font-size: 1.1rem; color: #e5e7eb; }
.grid { 
  display: grid; 
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); 
  gap: 1.5rem; 
}
.card { background: rgba(17,24,39,0.6); border: 1px solid var(--border-color); border-radius: 12px; overflow: hidden; box-shadow: 0 10px 24px rgba(0,0,0,.15); }
.thumb { width: 100%; height: 140px; overflow: hidden; background: rgba(0,0,0,.2); display: flex; align-items: center; justify-content: center; position: relative; }
.thumb img { width: 100%; height: 100%; object-fit: cover; display: block; transition: opacity 0.3s ease; }
.thumb img.img-loading { opacity: 0.3; }
.thumb img.img-error { opacity: 0.1; }

.loading-overlay, .error-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: rgba(0,0,0,0.7);
  color: white;
}

.loading-spinner {
  width: 30px;
  height: 30px;
  border: 3px solid rgba(255,255,255,0.3);
  border-top: 3px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.loading-text {
  margin-top: 0.5rem;
  font-size: 0.8rem;
  color: #cbd5e1;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-icon { font-size: 1.5rem; margin-bottom: 0.5rem; }
.error-text { font-size: 0.8rem; margin-bottom: 0.5rem; }
.error-hint { font-size: 0.7rem; color: #9ca3af; }

.retry-btn {
  padding: 0.3rem 0.8rem;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 5px;
  font-size: 0.7rem;
  cursor: pointer;
  transition: background 0.3s ease;
}
.retry-btn:hover { background: #5a67d8; }

/* 预览按钮样式 */
.preview-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.thumb:hover .preview-overlay {
  opacity: 1;
}

.preview-btn {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  border: none;
  border-radius: 8px;
  padding: 0.5rem 1rem;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.25rem;
  font-size: 0.8rem;
  font-weight: 600;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  box-shadow: 0 4px 12px rgba(0,0,0,0.3);
}

.preview-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0,0,0,0.4);
}

.preview-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
}

.preview-icon { font-size: 1.2rem; }
.preview-text { font-size: 0.7rem; }

.meta { padding: .6rem .75rem .8rem; }
.name { font-weight: 700; color: #e5e7eb; margin-bottom: .25rem; }
.link { color: #cbd5e1; font-size: .85rem; word-break: break-all; }
.error-hint { 
  color: #ef4444; 
  font-size: 0.75rem; 
  margin-top: 0.25rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: rgba(239, 68, 68, 0.1);
  padding: 0.5rem;
  border-radius: 6px;
  border: 1px solid rgba(239, 68, 68, 0.3);
}

.error-icon-small {
  font-size: 0.8rem;
}

.retry-btn-small {
  padding: 0.2rem 0.5rem;
  background: #ef4444;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 0.7rem;
  cursor: pointer;
  transition: background 0.2s ease;
  margin-left: auto;
}

.retry-btn-small:hover {
  background: #dc2626;
}

.img-normal {
  opacity: 1;
  transition: opacity 0.3s ease;
}

.empty { text-align: center; color: var(--text-secondary); padding: 2rem 0; }

/* 预览模态框样式 - 复用CrawlPictures的样式 */
.preview-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.8);
  display: flex;
  align-items: flex-start;
  justify-content: center;
  z-index: 9999;
  backdrop-filter: blur(10px);
  padding: 6rem 1rem 1rem 1rem;
  overflow-y: auto;
  overflow-x: hidden;
}

.preview-content {
   background: rgba(17,24,39,0.95);
   border-radius: 16px;
   width: calc(100vw - 2rem);
   max-width: 1200px;
   min-height: calc(100vh - 5rem);
   max-height: none;
   overflow: visible;
   box-shadow: 0 25px 60px rgba(0,0,0,0.5);
   border: 1px solid var(--border-color);
   margin: 0 auto;
   display: flex;
   flex-direction: column;
 }

.preview-body {
   display: flex;
   flex-direction: column;
   flex: 1;
   min-height: 0;
 }

.preview-image-container {
   position: relative;
   flex: 1;
   min-height: 400px;
   max-height: 75vh;
   overflow: auto;
   display: flex;
   align-items: flex-start;
   justify-content: center;
   background: rgba(0,0,0,0.3);
   padding: 2rem 1rem 1rem 1rem;
 }

.preview-image-container img {
   max-width: 100%;
   max-height: none;
   width: auto;
   height: auto;
   object-fit: contain;
   transition: opacity 0.3s ease;
   margin: 2rem;
 }

.preview-image-container img.preview-loading {
  opacity: 0.3;
}

.preview-image-container img.preview-error {
  opacity: 0.1;
}

.preview-loading-overlay, .preview-error-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: rgba(0,0,0,0.7);
  color: white;
}

.preview-loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid rgba(255,255,255,0.3);
  border-top: 4px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 1rem;
}

.preview-loading-text {
  font-size: 1rem;
  color: #cbd5e1;
}

.preview-error-icon {
  font-size: 2rem;
  margin-bottom: 0.5rem;
}

.preview-error-text {
  font-size: 1rem;
  color: #ef4444;
}

.preview-info {
  padding: 1.5rem;
  background: rgba(17,24,39,0.6);
}

.preview-url-section {
  margin-bottom: 1rem;
}

.preview-url-label {
  display: block;
  color: #cbd5e1;
  font-weight: 600;
  margin-bottom: 0.5rem;
}

.preview-url-container {
  display: flex;
  gap: 0.5rem;
  align-items: center;
}

.preview-url-input {
  flex: 1;
  padding: 0.75rem;
  background: rgba(255,255,255,0.1);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  color: #e5e7eb;
  font-size: 0.9rem;
  cursor: text;
}

.preview-url-input:focus {
  outline: none;
  border-color: #667eea;
  background: rgba(255,255,255,0.15);
}

.copy-btn {
  padding: 0.75rem 1rem;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1rem;
  transition: background 0.2s ease;
}

.copy-btn:hover {
  background: #5a67d8;
}

.preview-actions {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}

.preview-action-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1rem;
  background: rgba(102,126,234,0.2);
  color: #e5e7eb;
  border: 1px solid #667eea;
  border-radius: 8px;
  text-decoration: none;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.2s ease;
}

.preview-action-btn:hover {
  background: rgba(102,126,234,0.3);
  transform: translateY(-1px);
}

.preview-action-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
  background: rgba(102,126,234,0.1);
  border-color: rgba(102,126,234,0.3);
}

.preview-action-btn:disabled:hover {
  background: rgba(102,126,234,0.1);
  transform: none;
}

.back-btn-inline {
  background: rgba(102,126,234,0.2);
  border-color: #667eea;
}

.back-btn-inline:hover {
  background: rgba(102,126,234,0.3);
}

.action-icon {
  font-size: 1rem;
}
</style>
