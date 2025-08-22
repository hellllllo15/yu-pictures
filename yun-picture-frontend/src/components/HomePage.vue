<template>
  <div class="home-page">
    <!-- 英雄区域 -->
    <section class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">发现世界的美好瞬间</h1>
        <p class="hero-subtitle">精选全球优质图片，让创意无限可能</p>
        
        <!-- 搜索区域 -->
        <div class="search-section">
          <div class="search-container">
            <input type="text" placeholder="搜索你想要的图片..." class="search-input" v-model="searchText" />
            <select class="category-select" v-model="selectedCategory">
              <option value="">所有分类</option>
              <option value="nature">自然风景</option>
              <option value="city">城市建筑</option>
              <option value="people">人物肖像</option>
            </select>
            <button class="search-btn" @click="handleSearch">搜索</button>
          </div>
        </div>
      </div>
    </section>

    <!-- 推荐图片展示区 -->
    <section class="featured-section">
      <div class="section-header">
        <h2 class="section-title">精选推荐</h2>
        <p class="section-subtitle">发现最受欢迎的优质图片</p>
      </div>
      
      <div class="image-grid">
        <div class="image-card" v-for="(image, index) in featuredImages" :key="index">
          <div class="image-wrapper">
            <img :src="image.url" :alt="image.title" class="card-image" />
            <div class="image-overlay">
              <h3 class="image-title">{{ image.title }}</h3>
              <div class="image-meta">
                <span class="author-name">{{ image.author.name }}</span>
                <span class="likes">❤️ {{ image.likes }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 分页查询图片区域 -->
    <section class="pagination-section">
      <div class="section-header">
        <h2 class="section-title">图片库</h2>
        <p class="section-subtitle">浏览更多精彩图片</p>
      </div>
      
      <!-- 筛选器 -->
      <div class="filter-container">
        <div class="filter-group">
          <label>分类筛选：</label>
          <select v-model="filterCategory" @change="handleFilterChange">
            <option value="">全部分类</option>
            <option value="nature">自然风景</option>
            <option value="city">城市建筑</option>
            <option value="people">人物肖像</option>
            <option value="food">美食佳肴</option>
            <option value="travel">旅行摄影</option>
          </select>
        </div>
        <div class="filter-group">
          <label>排序方式：</label>
          <select v-model="sortOrder" @change="handleFilterChange">
            <option value="createTime">最新上传</option>
            <option value="updateTime">最近更新</option>
            <option value="name">名称排序</option>
          </select>
        </div>
      </div>
      
      <!-- 图片网格 -->
      <div class="image-grid" v-if="paginationImages.length > 0">
        <div class="image-card" v-for="image in paginationImages" :key="image.id" :data-image-id="image.id">
          <div class="image-wrapper">
            <!-- 缩略图容器 -->
            <div class="thumbnail-container">
              <!-- 低质量缩略图 -->
              <img 
                :src="getThumbnailUrl(image.url, 'low')" 
                :alt="image.name" 
                class="card-image thumbnail-low"
                loading="lazy"
              />
              <!-- 高质量图片（懒加载） -->
              <img 
                :src="getThumbnailUrl(image.url, 'high')" 
                :alt="image.name" 
                class="card-image thumbnail-high"
                loading="lazy"
                @load="onImageLoad"
                @error="onImageError"
              />
              <!-- 加载状态指示器 -->
              <div class="image-loading" v-if="!image.loaded">
                <div class="loading-spinner-small"></div>
              </div>
            </div>
            
            <div class="image-overlay">
              <h3 class="image-title">{{ image.name }}</h3>
              <p class="image-intro">{{ image.introduction || '暂无描述' }}</p>
              <div class="image-meta">
                <span class="author-name">{{ image.user?.userName || '未知用户' }}</span>
                <span class="category-tag">{{ image.category || '未分类' }}</span>
              </div>
              <div class="image-tags" v-if="image.tags && image.tags.length > 0">
                <span class="tag" v-for="tag in image.tags" :key="tag">{{ tag }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 加载状态 -->
      <div class="loading-container" v-if="loading">
        <div class="loading-spinner"></div>
        <p>正在加载图片...</p>
      </div>
      
      <!-- 空状态 -->
      <div class="empty-container" v-if="!loading && paginationImages.length === 0">
        <p>暂无图片数据</p>
      </div>
      
      <!-- 分页器 -->
      <div class="pagination-container" v-if="totalPages > 1">
        <button 
          class="pagination-btn" 
          :disabled="currentPage === 1"
          @click="handlePageChange(currentPage - 1)"
        >
          上一页
        </button>
        
        <div class="page-numbers">
          <button 
            v-for="page in visiblePages" 
            :key="page"
            class="page-btn"
            :class="{ active: page === currentPage }"
            @click="handlePageChange(page)"
          >
            {{ page }}
          </button>
        </div>
        
        <button 
          class="pagination-btn" 
          :disabled="currentPage === totalPages"
          @click="handlePageChange(currentPage + 1)"
        >
          下一页
        </button>
      </div>
      
      <!-- 分页信息 -->
      <div class="pagination-info">
        <span>第 {{ currentPage }} 页，共 {{ totalPages }} 页</span>
        <span>总计 {{ total }} 张图片</span>
      </div>
    </section>

    <!-- 分类快速入口 -->
    <section class="categories-section">
      <div class="section-header">
        <h2 class="section-title" style="color: white;">探索分类</h2>
        <p class="section-subtitle" style="color: white;">按主题浏览精彩内容</p>
      </div>
      
      <div class="categories-container">
        <div class="category-card" v-for="(category, index) in categories" :key="index">
          <h3 class="category-name">{{ category.name }}</h3>
          <p class="category-count">{{ category.count }} 张图片</p>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch, nextTick } from 'vue'
import { listPictureVoByPageUsingPost } from '../a/api/pictureController'

// 推荐图片数据
const featuredImages = ref([
  {
    url: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400&h=300&fit=crop',
    title: '雪山日出',
    author: { name: '摄影师小王' },
    likes: 128
  },
  {
    url: 'https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=400&h=300&fit=crop',
    title: '森林小径',
    author: { name: '自然摄影' },
    likes: 89
  },
  {
    url: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400&h=300&fit=crop',
    title: '城市夜景',
    author: { name: '城市探索者' },
    likes: 156
  }
])

// 分类数据
const categories = ref([
  { name: '自然风景', count: 1250 },
  { name: '城市建筑', count: 890 },
  { name: '人物肖像', count: 756 },
  { name: '美食佳肴', count: 634 },
  { name: '旅行摄影', count: 445 }
])

// 分页查询相关状态
const paginationImages = ref<any[]>([])
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)
const totalPages = ref(0)
const loading = ref(false)
const isChangingPage = ref(false) // 防止重复点击

// 搜索和筛选相关状态
const searchText = ref('')
const selectedCategory = ref('')
const filterCategory = ref('')
const sortOrder = ref('createTime')

// 计算可见的页码
const visiblePages = computed(() => {
  const pages = []
  const start = Math.max(1, currentPage.value - 2)
  const end = Math.min(totalPages.value, currentPage.value + 2)
  
  console.log('计算可见页码:', {
    currentPage: currentPage.value,
    totalPages: totalPages.value,
    start,
    end
  })
  
  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  
  console.log('生成的页码数组:', pages)
  return pages
})

// 调试：监听页码变化
watch(currentPage, (newPage, oldPage) => {
  console.log('页码监听器触发:', { oldPage, newPage })
}, { immediate: true })

// 获取分页图片数据
const fetchPaginationImages = async () => {
  console.log('开始获取分页数据:', { 
    currentPage: currentPage.value, 
    pageSize: pageSize.value,
    sortField: sortOrder.value 
  })
  
  // 验证页码是否正确
  if (currentPage.value < 1) {
    console.error('页码无效，重置为1:', currentPage.value)
    currentPage.value = 1
  }
  
  loading.value = true
  try {
    const request: any = {
      current: currentPage.value,
      pageSize: pageSize.value,
      sortField: sortOrder.value,
      sortOrder: 'desc'
    }
    
    console.log('构建请求参数:', request)
    
    // 添加分类筛选
    if (filterCategory.value) {
      request.category = filterCategory.value
    }
    
    // 添加搜索文本
    if (searchText.value.trim()) {
      request.searchText = searchText.value.trim()
    }
    
    console.log('发送API请求:', request)
    const response = await listPictureVoByPageUsingPost(request)
    console.log('收到API响应:', response)
    
    if (response.data.code === 0 && response.data.data) {
      console.log('API响应数据:', response.data.data)
      
      // 为每个图片添加loaded状态
      const images = (response.data.data.records || []) as any[]
      images.forEach(image => {
        image.loaded = false
      })
      
      paginationImages.value = images
      total.value = response.data.data.total || 0
      totalPages.value = response.data.data.pages || 0
      
      // 验证后端返回的页码是否合理
      const backendCurrentPage = response.data.data.current || 1
      const backendTotalPages = response.data.data.pages || 1
      
      console.log('后端返回的页码信息:', {
        backendCurrentPage,
        backendTotalPages,
        userCurrentPage: currentPage.value
      })
      
      // 只在初始化时设置页码，不要覆盖用户主动设置的页码
      if (currentPage.value === 1 && !paginationImages.value.length) {
        currentPage.value = backendCurrentPage
        console.log('初始化时设置页码:', currentPage.value)
      }
      
      // 记录但不修改用户设置的页码
      if (backendCurrentPage !== currentPage.value) {
        console.log('后端返回的页码与用户设置的不一致:', {
          backend: backendCurrentPage,
          user: currentPage.value
        })
        console.log('保持用户设置的页码:', currentPage.value)
      }
      
      console.log('分页状态更新:', {
        currentPage: currentPage.value,
        totalPages: totalPages.value,
        total: total.value,
        recordsCount: paginationImages.value.length
      })
    } else {
      console.error('获取图片列表失败:', response.data.message)
      paginationImages.value = []
      total.value = 0
      totalPages.value = 0
    }
  } catch (error) {
    console.error('获取图片列表出错:', error)
    paginationImages.value = []
    total.value = 0
    totalPages.value = 0
  } finally {
    loading.value = false
    isChangingPage.value = false
  }
}

// 处理页码变化
const handlePageChange = (page: number) => {
  // 防止重复点击
  if (isChangingPage.value || loading.value) {
    console.log('正在处理中，跳过重复请求')
    return
  }
  
  console.log('页码变化请求:', { 
    from: currentPage.value, 
    to: page, 
    totalPages: totalPages.value,
    valid: page >= 1 && page <= totalPages.value 
  })
  
  // 确保页码在有效范围内
  const validPage = Math.max(1, Math.min(page, totalPages.value || 1))
  
  if (validPage !== currentPage.value) {
    isChangingPage.value = true
    const oldPage = currentPage.value
    
    // 直接设置页码，不使用 nextTick
    currentPage.value = validPage
    console.log('页码已更新:', { oldPage, newPage: currentPage.value })
    
    // 立即验证页码是否正确设置
    console.log('页码设置验证:', { 
      expected: validPage, 
      actual: currentPage.value,
      isCorrect: currentPage.value === validPage 
    })
    
    // 延迟一点再发送请求，确保页码设置完成
    setTimeout(() => {
      console.log('发送请求前的最终页码验证:', currentPage.value)
      fetchPaginationImages()
    }, 10)
  } else {
    console.log('页码未变化，跳过请求')
  }
}

// 处理筛选变化
const handleFilterChange = () => {
  currentPage.value = 1
  fetchPaginationImages()
}

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchPaginationImages()
}

// 缩略图URL生成函数
const getThumbnailUrl = (originalUrl: string, quality: 'low' | 'high') => {
  if (!originalUrl) return ''
  
  // 如果是 Unsplash 图片，使用其缩略图服务
  if (originalUrl.includes('unsplash.com')) {
    if (quality === 'low') {
      // 低质量缩略图：200x150
      return originalUrl.replace(/[?&]w=\d+&h=\d+/, '?w=200&h=150&fit=crop&q=30')
    } else {
      // 高质量缩略图：400x300
      return originalUrl.replace(/[?&]w=\d+&h=\d+/, '?w=400&h=300&fit=crop&q=80')
    }
  }
  
  // 如果是其他图片，尝试添加缩略图参数
  if (quality === 'low') {
    return `${originalUrl}?w=200&h=150&fit=crop&q=30`
  } else {
    return `${originalUrl}?w=400&h=300&fit=crop&q=80`
  }
}

// 图片加载完成处理
const onImageLoad = (event: Event) => {
  const img = event.target as HTMLImageElement
  const imageCard = img.closest('.image-card')
  if (imageCard) {
    const imageId = imageCard.getAttribute('data-image-id')
    if (imageId) {
      const image = paginationImages.value.find(img => img.id == imageId)
      if (image) {
        image.loaded = true
        // 添加loaded类到图片元素
        img.classList.add('loaded')
      }
    }
  }
}

// 图片加载失败处理
const onImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  console.warn('图片加载失败:', img.src)
  // 可以设置默认图片或隐藏加载状态
}

// 组件挂载时获取数据
onMounted(() => {
  fetchPaginationImages()
})
</script>

<script lang="ts">
export default {
  name: 'HomePage'
}
</script>

<style scoped>
.home-page {
  width: 100%;
  min-height: 100vh;
  opacity: 0.9;
}

/* 英雄区域样式 */
.hero-section {
  min-height: 60vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  color: white;
  text-align: center;
  padding: 2rem;
}

.hero-title {
  font-size: 3rem;
  font-weight: 800;
  margin-bottom: 1rem;
  text-shadow: 0 0 20px rgba(255, 255, 255, 0.3);
}

.hero-subtitle {
  font-size: 1.25rem;
  margin-bottom: 2rem;
  opacity: 0.9;
}

.search-container {
  display: flex;
  gap: 1rem;
  max-width: 500px;
  margin: 0 auto;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border-radius: 50px;
  padding: 0.5rem;
}

.search-input {
  flex: 1;
  border: none;
  background: white;
  border-radius: 25px;
  padding: 0.75rem 1rem;
  outline: none;
}

.category-select {
  background: white;
  border: none;
  border-radius: 25px;
  padding: 0.75rem 1rem;
  outline: none;
}

.search-btn {
  background: linear-gradient(45deg, #ff6b6b, #4ecdc4);
  border: none;
  border-radius: 25px;
  padding: 0.75rem 2rem;
  color: white;
  font-weight: 600;
  cursor: pointer;
}

/* 通用区域样式 */
.section-header {
  text-align: center;
  margin-bottom: 3rem;
}

.section-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 1rem;
}

.section-subtitle {
  font-size: 1.1rem;
  color: #64748b;
}

/* 推荐图片区域 */
.featured-section {
  padding: 5rem 2rem;
  background: transparent;
  width: 100%;
  max-width: 100%;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
  width: 100%;
  max-width: 100%;
  margin: 0;
  padding: 0;
}

.image-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
  width: 100%;
}

.image-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
}

/* 缩略图样式 */
.thumbnail-container {
  position: relative;
  width: 100%;
  height: 250px;
  overflow: hidden;
}

.thumbnail-low {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  filter: blur(2px);
  transform: scale(1.1);
}

.thumbnail-high {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  opacity: 0;
  transition: opacity 0.5s ease;
}

.thumbnail-high.loaded {
  opacity: 1;
}

.image-loading {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 10;
}

.loading-spinner-small {
  width: 24px;
  height: 24px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top: 2px solid #4ecdc4;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

/* 图片卡片样式 */
.image-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
  width: 100%;
}

.image-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
}

.image-overlay {
  padding: 1.5rem;
}

.image-title {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 1rem;
  color: #1e293b;
}

.image-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.author-name {
  color: #64748b;
  font-size: 0.9rem;
}

.likes {
  color: #ef4444;
  font-weight: 600;
}

/* 分类区域 */
.categories-section {
  padding: 5rem 2rem;
  background: transparent;
  width: 100%;
  max-width: 100%;
}

.categories-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 2rem;
  width: 100%;
  max-width: 100%;
  margin: 0;
  padding: 0;
}

.category-card {
  background: white;
  border-radius: 16px;
  padding: 2rem;
  text-align: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  cursor: pointer;
  width: 100%;
}

.category-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.12);
}

.category-name {
  font-size: 1.25rem;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 0.5rem;
}

.category-count {
  color: #64748b;
}

/* 分页查询区域样式 */
.pagination-section {
  padding: 5rem 2rem;
  background: transparent;
  width: 100%;
  max-width: 100%;
}

.filter-container {
  display: flex;
  justify-content: center;
  gap: 2rem;
  margin-bottom: 3rem;
  flex-wrap: wrap;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 1rem;
  color: #1e293b;
}

.filter-group label {
  font-weight: 600;
}

.filter-group select {
  padding: 0.75rem 1.5rem;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  background-color: #f9fafb;
  color: #1e293b;
  cursor: pointer;
  font-size: 0.95rem;
}

.filter-group select:focus {
  outline: none;
  border-color: #4f46e5;
  box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.2);
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
  width: 100%;
  max-width: 100%;
  margin: 0;
  padding: 0;
}

.image-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
  width: 100%;
}

.image-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
}

.card-image {
  width: 100%;
  height: 250px;
  object-fit: cover;
}

.image-overlay {
  padding: 1.5rem;
}

.image-title {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 1rem;
  color: #1e293b;
}

.image-intro {
  font-size: 0.9rem;
  color: #64748b;
  margin-bottom: 1rem;
  line-height: 1.4;
}

.image-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.author-name {
  color: #64748b;
  font-size: 0.9rem;
}

.category-tag {
  background-color: #e0e7ff;
  color: #4f46e5;
  padding: 0.4rem 0.8rem;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 600;
}

.image-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-top: 0.5rem;
}

.tag {
  background-color: #f3e8ff;
  color: #8b5cf6;
  padding: 0.3rem 0.7rem;
  border-radius: 10px;
  font-size: 0.75rem;
  font-weight: 600;
}

.loading-container {
  text-align: center;
  padding: 3rem 0;
  color: #64748b;
}

.loading-spinner {
  border: 4px solid rgba(255, 255, 255, 0.3);
  border-top: 4px solid #4ecdc4;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;
  margin: 0 auto 1rem;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.empty-container {
  text-align: center;
  padding: 3rem 0;
  color: #64748b;
}

.pagination-container {
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin-top: 3rem;
  flex-wrap: wrap;
}

.pagination-btn {
  background: linear-gradient(45deg, #4f46e5, #475569);
  border: none;
  border-radius: 8px;
  padding: 0.75rem 1.5rem;
  color: white;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.3s ease;
}

.pagination-btn:hover:not(:disabled) {
  background: linear-gradient(45deg, #6366f1, #4f46e5);
}

.pagination-btn:disabled {
  background: #d1d5db;
  color: #9ca3af;
  cursor: not-allowed;
}

.page-numbers {
  display: flex;
  gap: 0.5rem;
}

.page-btn {
  background: #f3f4f6;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  padding: 0.5rem 1rem;
  color: #1e293b;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.3s ease, border-color 0.3s ease;
}

.page-btn:hover:not(.active) {
  background: #e5e7eb;
  border-color: #d1d5db;
}

.page-btn.active {
  background: #4f46e5;
  color: white;
  border-color: #4f46e5;
}

.pagination-info {
  text-align: center;
  margin-top: 2rem;
  color: #64748b;
  font-size: 0.9rem;
  display: flex;
  justify-content: center;
  gap: 2rem;
  flex-wrap: wrap;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .hero-section {
    padding: 2rem 1.5rem;
  }
  
  .featured-section,
  .categories-section,
  .pagination-section {
    padding: 4rem 1.5rem;
  }
  
  .image-grid {
    gap: 1.5rem;
  }
  
  .categories-container {
    gap: 1.5rem;
  }
  
  .filter-container {
    flex-direction: column;
    gap: 1rem;
  }
  
  .filter-group {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.3rem;
  }
  
  .filter-group label {
    width: 100%;
    text-align: left;
  }
  
  .filter-group select {
    width: 100%;
    padding: 0.6rem 1rem;
  }

  .filter-container {
    flex-direction: column;
    gap: 1rem;
  }

  .filter-group {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.3rem;
  }

  .filter-group label {
    width: 100%;
    text-align: left;
  }

  .filter-group select {
    width: 100%;
    padding: 0.6rem 1rem;
  }
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 2.5rem;
  }
  
  .hero-subtitle {
    font-size: 1.1rem;
  }
  
  .search-container {
    flex-direction: column;
    max-width: 100%;
    border-radius: 16px;
  }
  
  .search-input,
  .category-select,
  .search-btn {
    border-radius: 12px;
  }
  
  .image-grid {
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 1rem;
  }
  
  .categories-container {
    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
    gap: 1rem;
  }
  
  .featured-section,
  .categories-section,
  .pagination-section {
    padding: 3rem 1rem;
  }
  
  .section-title {
    font-size: 2rem;
  }
  
  .section-subtitle {
    font-size: 1rem;
  }
  
  .filter-container {
    gap: 1rem;
  }
  
  .filter-group {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.3rem;
  }
  
  .filter-group label {
    width: 100%;
    text-align: left;
  }
  
  .filter-group select {
    width: 100%;
    padding: 0.6rem 1rem;
  }

  .filter-container {
    gap: 1rem;
  }

  .filter-group {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.3rem;
  }

  .filter-group label {
    width: 100%;
    text-align: left;
  }

  .filter-group select {
    width: 100%;
    padding: 0.6rem 1rem;
  }
}

@media (max-width: 480px) {
  .hero-section {
    padding: 1.5rem 1rem;
    min-height: 50vh;
  }
  
  .hero-title {
    font-size: 2rem;
  }
  
  .hero-subtitle {
    font-size: 1rem;
  }
  
  .search-container {
    padding: 0.25rem;
  }
  
  .search-input,
  .category-select,
  .search-btn {
    padding: 0.5rem 0.75rem;
    font-size: 0.9rem;
  }
  
  .featured-section,
  .categories-section,
  .pagination-section {
    padding: 2rem 0.75rem;
  }
  
  .image-grid {
    grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
    gap: 0.75rem;
  }
  
  .categories-container {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
  
  .image-card {
    border-radius: 12px;
  }
  
  .image-overlay {
    padding: 1rem;
  }
  
  .image-title {
    font-size: 1.1rem;
  }
  
  .category-card {
    padding: 1.5rem;
    border-radius: 12px;
  }
  
  .category-name {
    font-size: 1.1rem;
  }
  
  .filter-container {
    gap: 0.5rem;
  }
  
  .filter-group {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.3rem;
  }
  
  .filter-group label {
    width: 100%;
    text-align: left;
  }
  
  .filter-group select {
    width: 100%;
    padding: 0.6rem 1rem;
  }

  .filter-container {
    gap: 0.5rem;
  }

  .filter-group {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.3rem;
  }

  .filter-group label {
    width: 100%;
    text-align: left;
  }

  .filter-group select {
    width: 100%;
    padding: 0.6rem 1rem;
  }
}

/* 追加暗色主题覆盖 */
.image-card { background: var(--bg-surface-strong); box-shadow: 0 4px 20px rgba(0,0,0,.25); }
.image-title { color: var(--text-primary); }
.author-name { color: var(--text-secondary); }
.likes { color: #fda4af; }

.category-card { background: var(--bg-surface); box-shadow: 0 4px 20px rgba(0,0,0,.2); }
.category-name { color: var(--text-primary); }
.category-count { color: var(--text-secondary); }

/* 精选推荐标题与副标题使用纯白 */
.featured-section .section-title { color: #ffffff; }
.featured-section .section-subtitle { color: #ffffff; }

/* 分页区域暗色主题覆盖 */
.pagination-section .section-title { color: #ffffff; }
.pagination-section .section-subtitle { color: #ffffff; }

.filter-group { color: var(--text-primary, #ffffff); }
.filter-group select { 
  background-color: var(--bg-surface, rgba(255, 255, 255, 0.1)); 
  color: var(--text-primary, #ffffff);
  border-color: var(--border-color, rgba(255, 255, 255, 0.2));
}

.image-intro { color: var(--text-secondary, rgba(255, 255, 255, 0.8)); }
.category-tag { 
  background-color: var(--bg-surface-strong, rgba(79, 70, 229, 0.2)); 
  color: var(--text-primary, #ffffff);
}
.tag { 
  background-color: var(--bg-surface-strong, rgba(139, 92, 246, 0.2)); 
  color: var(--text-primary, #ffffff);
}

/* 缩略图暗色主题覆盖 */
.thumbnail-container { 
  background-color: var(--bg-surface-strong, rgba(0, 0, 0, 0.3)); 
}

.thumbnail-low { 
  filter: blur(2px) brightness(0.8); 
}

.image-loading { 
  color: var(--text-primary, #ffffff); 
}
</style> 