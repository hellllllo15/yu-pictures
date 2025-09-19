<template>
  <div class="my-space-container">
    <!-- 动态背景 -->
    <div class="background-decoration">
      <div class="floating-shapes">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
        <div class="shape shape-4"></div>
      </div>
    </div>
    
    <!-- 图片编辑组件 -->
    <ImageEditor
      :visible="showImageEditor"
      :imageUrl="editingPicture?.url"
      :picture="editingPicture"
      :spaceId="currentSpaceId ? Number(currentSpaceId) : undefined"
      :onSuccess="onImageEditSuccess"
      :onClose="closeImageEditor"
    />
    
    <!-- 主要内容 -->
    <div class="main-content">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1 class="page-title">
          <span class="title-icon">🏠</span>
          <span class="title-text">{{ getPageTitle() }}</span>
          <span class="title-decoration"></span>
          <button class="create-space-btn" @click="goToCreateSpace">创建空间</button>
          <div class="button-group">
            <button class="joined-spaces-btn" @click="showJoinedSpaces">我加入的空间</button>
            <button v-if="canManageMembers" class="manage-members-btn" @click="manageMembers">管理空间成员</button>
          </div>
        </h1>
        <p class="page-subtitle">{{ getPageSubtitle() }}</p>
        <div v-if="route.query.spaceId && userRole" class="role-info">
          <span class="role-badge" :class="getRoleBadgeClass()">
            {{ getRoleText() }}
          </span>
        </div>
      </div>
      
      <!-- 搜索筛选区域 -->
      <div class="search-section">
        <div class="search-container">
          <div class="search-item">
            <label class="search-label">图片名称:</label>
            <div class="input-wrapper">
              <input 
                type="text" 
                placeholder="请输入图片名称" 
                class="search-input"
                v-model="searchForm.name"
              />
              <div class="input-border"></div>
            </div>
          </div>
          
          <div class="search-item">
            <label class="search-label">分类:</label>
            <div class="input-wrapper">
              <select class="search-select" v-model="searchForm.category">
                <option value="">全部分类</option>
                <option v-for="category in categories" :key="category" :value="category">
                  {{ category }}
                </option>
              </select>
              <div class="input-border"></div>
            </div>
          </div>
          
          <div class="search-item">
            <label class="search-label">标签:</label>
            <div class="input-wrapper">
              <select class="search-select" v-model="searchForm.tags">
                <option value="">全部标签</option>
                <option v-for="tag in tags" :key="tag" :value="tag">
                  {{ tag }}
                </option>
              </select>
              <div class="input-border"></div>
            </div>
          </div>
          
          <button class="search-btn" @click="handleSearch">
            <span class="btn-text">搜索</span>
            <div class="btn-glow"></div>
          </button>
        </div>
      </div>
      
      <!-- 统计信息 -->
      <div class="stats-section">
        <div class="stats-container">
          <div class="stat-item">
            <div class="stat-icon">📊</div>
            <div class="stat-content">
              <div class="stat-value">{{ totalPictures }}</div>
              <div class="stat-label">{{ currentSpaceId && route.query.spaceId ? '团队图片数' : '总图片数' }}</div>
            </div>
          </div>
          <div class="stat-item">
            <div class="stat-icon">💾</div>
            <div class="stat-content">
              <div class="stat-value">{{ formatFileSize(spaceTotalSize) }}</div>
              <div class="stat-label">{{ currentSpaceId && route.query.spaceId ? '团队存储空间' : '总存储空间' }}</div>
            </div>
          </div>
          <div class="stat-item">
            <div class="stat-icon">📁</div>
            <div class="stat-content">
              <div class="stat-value">{{ totalPages }}</div>
              <div class="stat-label">总页数</div>
            </div>
          </div>
        </div>
        
        <!-- 空间使用情况圆环 -->
        <div class="space-usage-section">
          <div class="usage-container">
            <div class="usage-title">{{ currentSpaceId && route.query.spaceId ? '团队空间使用情况' : '空间使用情况' }}</div>
            <div class="usage-circle-container">
              <div class="usage-circle">
                <svg class="circle-svg" viewBox="0 0 120 120">
                  <!-- 背景圆环 -->
                  <circle 
                    cx="60" 
                    cy="60" 
                    r="50" 
                    fill="none" 
                    stroke="rgba(102, 126, 234, 0.2)" 
                    stroke-width="8"
                  />
                  <!-- 进度圆环 -->
                  <circle 
                    cx="60" 
                    cy="60" 
                    r="50" 
                    fill="none" 
                    stroke="#667eea" 
                    stroke-width="8"
                    stroke-linecap="round"
                    :stroke-dasharray="circumference"
                    :stroke-dashoffset="strokeDashoffset"
                    transform="rotate(-90 60 60)"
                  />
                </svg>
                <div class="circle-content">
                  <div class="usage-percentage">{{ usagePercentageText }}%</div>
                  <div class="usage-text">已使用</div>
                </div>
              </div>
              <div class="usage-details">
                <div class="detail-item">
                  <span class="detail-label">已使用:</span>
                  <span class="detail-value">{{ formatFileSize(spaceTotalSize) }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">总容量:</span>
                  <span class="detail-value">{{ formatFileSize(maxSpaceSize) }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">剩余:</span>
                  <span class="detail-value">{{ formatFileSize(remainingSpace) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 图片展示区域 -->
      <div class="pictures-section">
        <div class="pictures-container">
          <!-- 加载状态 -->
          <div v-if="isLoading" class="loading-container">
            <div class="loading-spinner"></div>
            <p class="loading-text">{{ currentSpaceId && route.query.spaceId ? '正在加载团队空间的图片...' : '正在加载您的图片...' }}</p>
          </div>
          
          <!-- 空状态 -->
          <div v-else-if="pictureList.length === 0" class="empty-container">
            <div class="empty-icon">🖼️</div>
            <h3 class="empty-title">{{ getEmptyTitle() }}</h3>
            <p class="empty-text">{{ getEmptyText() }}</p>
            <button v-if="canEdit" class="upload-btn" @click="goToUpload">
              <span class="btn-icon">📤</span>
              <span class="btn-text">上传图片</span>
            </button>
          </div>
          
          <!-- 图片网格 -->
          <div v-else class="pictures-grid">
            <div 
              v-for="(picture, index) in pictureList" 
              :key="picture.id" 
              class="picture-card"
              :data-picture-id="picture.id"
              @click="viewPicture(picture)"
            >
              <div class="picture-image">
                <img 
                  :src="picture.url" 
                  :alt="picture.name" 
                  :class="{ 'img-normal': !imageErrorStates[index] && !imageLoadingStates[index] }"
                  @load="onImageLoad"
                  @error="onImageError"
                  referrerpolicy="no-referrer"
                />
                <!-- 加载状态指示器 -->
                <div class="image-loading" v-if="imageLoadingStates[index]">
                  <div class="loading-spinner-small"></div>
                </div>
                <!-- 错误状态指示器 -->
                <div class="image-error" v-if="imageErrorStates[index]">
                  <div class="error-icon">⚠️</div>
                  <div class="error-text">加载失败</div>
                  <button @click.stop="retryLoadImage(index)" class="retry-btn">重试</button>
                </div>
                <!-- 审核状态覆盖层 -->
                <div v-if="picture.reviewStatus !== 1" class="review-status-overlay">
                  <div class="review-status-content">
                    <div class="review-status-icon">
                      <span v-if="picture.reviewStatus === 0">⏳</span>
                      <span v-else-if="picture.reviewStatus === 2">❌</span>
                    </div>
                    <div class="review-status-text">
                      <span v-if="picture.reviewStatus === 0">待审核</span>
                      <span v-else-if="picture.reviewStatus === 2">审核未通过</span>
                    </div>
                  </div>
                </div>
                
                <!-- 操作按钮覆盖层（仅审核通过时显示） -->
                <div v-if="picture.reviewStatus === 1" class="picture-overlay">
                  <div class="overlay-actions">
                    <button v-if="canEdit" class="action-btn edit-btn" @click.stop="editPicture(picture)" title="编辑">
                      <span class="btn-icon">✏️</span>
                    </button>
                    <button v-if="canEdit" class="action-btn delete-btn" @click.stop="deletePicture(picture.id)" title="删除">
                      <span class="btn-icon">🗑️</span>
                    </button>
                  </div>
                </div>
              </div>
              <div class="picture-info">
                <h4 class="picture-name">{{ picture.name }}</h4>
                <p class="picture-intro" v-if="picture.introduction">{{ picture.introduction }}</p>
                <div class="picture-meta">
                  <span class="meta-item">
                    <span class="meta-icon">📅</span>
                    {{ formatDateTime(picture.createTime) }}
                  </span>
                  <span class="meta-item">
                    <span class="meta-icon">💾</span>
                    {{ formatFileSize(picture.picSize || 0) }}
                  </span>
                </div>
                <div class="picture-tags" v-if="picture.tags && picture.tags.length > 0">
                  <span 
                    v-for="tag in picture.tags" 
                    :key="tag" 
                    class="tag-item"
                  >
                    {{ tag }}
                  </span>
                </div>
                <div class="picture-visibility">
                  <span 
                    :class="['visibility-badge', picture.spaceId ? 'private' : 'public']"
                  >
                    {{ picture.spaceId ? '🔒 私密' : '🌐 公开' }}
                  </span>
                </div>
                
                <!-- 审核状态显示（仅显示待审核和审核未通过） -->
                <div class="review-status-badge" v-if="picture.reviewStatus !== 1">
                  <span 
                    :class="['status-badge', getReviewStatusClass(picture.reviewStatus)]"
                  >
                    <span class="status-icon">
                      <span v-if="picture.reviewStatus === 0">⏳</span>
                      <span v-else-if="picture.reviewStatus === 2">❌</span>
                    </span>
                    <span class="status-text">
                      <span v-if="picture.reviewStatus === 0">待审核</span>
                      <span v-else-if="picture.reviewStatus === 2">审核未通过</span>
                    </span>
                  </span>
                </div>
                <!-- 复制图片地址按钮 -->
                <div class="copy-url-section">
                  <button class="copy-url-btn" @click="copyImageUrl(picture.url)" title="复制图片地址">
                    <span class="copy-icon">📋</span>
                    <span class="copy-text">复制图片地址</span>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 分页区域 -->
      <div class="pagination-section" v-if="totalPages > 1">
        <div class="pagination-container">
          <button 
            class="page-btn" 
            :disabled="currentPage === 1"
            @click="changePage(currentPage - 1)"
          >
            <span class="btn-icon">←</span>
            上一页
          </button>
          
          <div class="page-numbers">
            <button 
              v-for="page in visiblePages" 
              :key="page"
              class="page-number"
              :class="{ active: page === currentPage }"
              @click="changePage(page)"
            >
              {{ page }}
            </button>
          </div>
          
          <button 
            class="page-btn" 
            :disabled="currentPage === totalPages"
            @click="changePage(currentPage + 1)"
          >
            下一页
            <span class="btn-icon">→</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { listPictureVoByPageUsingPost, listPictureTagCategoryUsingGet, deletePictureUsingPost } from '../../a/api/pictureController'
import { listSpaceVoByPageUsingPost } from '../../a/api/spaceController'
import { useLoginUserStore } from '../../stores/useLoginUserStore'
import ImageEditor from '../picture/ImageEditor.vue'

// 状态管理
const isLoading = ref(false)
const pictureList = ref<any[]>([])
const categories = ref<string[]>([])
const tags = ref<string[]>([])
const currentPage = ref(1)
const pageSize = ref(12)
const totalPictures = ref(0)
const totalPages = ref(0)

// 图片加载状态管理（参考爬取图片页面的逻辑）
const imageLoadingStates = ref<boolean[]>([])
const imageErrorStates = ref<boolean[]>([])
const imageLoadStrategies = ref<Map<number, number>>(new Map()) // 记录每个图片的当前策略索引
// 空间用量与容量（来自空间分页返回值）
const spaceTotalSize = ref(0)
const maxSpaceSize = ref(10 * 1024 * 1024 * 1024) // 默认10GB，占位，实际从接口赋值
const currentSpaceId = ref<string | null>(null)
const userSpaceInfo = ref<any>(null)

// 用户登录状态
const loginUserStore = useLoginUserStore()
const router = useRouter()
const route = useRoute()

// 用户角色状态
const userRole = ref<string>('')
const isViewer = computed(() => userRole.value === 'viewer')
const isEditor = computed(() => userRole.value === 'editor')
const isAdmin = computed(() => userRole.value === 'admin')
const canEdit = computed(() => isEditor.value || isAdmin.value)
const canManageMembers = computed(() => isAdmin.value)

// 图片编辑状态
const showImageEditor = ref(false)
const editingPicture = ref<any>(null)

// 搜索表单
const searchForm = reactive({
  name: '',
  category: '',
  tags: ''
})

// 计算属性
const visiblePages = computed(() => {
  const pages = []
  const start = Math.max(1, currentPage.value - 2)
  const end = Math.min(totalPages.value, currentPage.value + 2)
  
  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  return pages
})

// 空间使用情况计算属性（基于空间接口返回）
const circumference = computed(() => 2 * Math.PI * 50) // 圆环周长
// 数值百分比用于绘制
const usagePercentNumber = computed(() => {
  if (!maxSpaceSize.value) return 0
  const pct = (spaceTotalSize.value / maxSpaceSize.value) * 100
  return Math.min(100, Math.max(0, pct))
})
// 文本百分比（保留1位小数，小于0.1且大于0时显示0.1）
const usagePercentageText = computed(() => {
  if (!maxSpaceSize.value) return '0.0'
  const pct = usagePercentNumber.value
  if (pct > 0 && pct < 0.1) return '0.1'
  return pct.toFixed(1)
})
const strokeDashoffset = computed(() => {
  return circumference.value - (usagePercentNumber.value / 100) * circumference.value
})
const remainingSpace = computed(() => Math.max(0, maxSpaceSize.value - spaceTotalSize.value))

// 数值安全转换（字符串/数字 均可）
const toNum = (v: any, def = 0): number => {
  if (v === null || v === undefined) return def
  const n = Number(v)
  return Number.isFinite(n) ? n : def
}

// 获取用户空间信息（分页取第一个空间）
const fetchUserSpaceInfo = async () => {
  // 确保登录
  if (!loginUserStore.loginUser.id) {
    await loginUserStore.fetchLoginUser()
  }
  if (!loginUserStore.loginUser.id) return

  try {
    // 检查URL参数中是否有spaceId
    const urlSpaceId = route.query.spaceId as string
    if (urlSpaceId) {
      // 如果URL中有spaceId，直接使用
      currentSpaceId.value = urlSpaceId
      // 这里可以调用接口获取空间详情，或者直接使用URL中的ID
      // 暂时使用默认值，实际项目中可能需要调用空间详情接口
      maxSpaceSize.value = 10 * 1024 * 1024 * 1024 // 默认10GB
      spaceTotalSize.value = 0
      return
    }

    // 如果没有URL参数，则获取用户的第一个空间
    const resp = await listSpaceVoByPageUsingPost({
      current: 1,
      pageSize: 1,
      userId: loginUserStore.loginUser.id as any
    })
    const pageData: any = resp?.data?.data
    const first = Array.isArray(pageData?.records) && pageData.records.length > 0 ? pageData.records[0] : null
    if (first) {
      userSpaceInfo.value = first
      currentSpaceId.value = String(first.id)
      // 接口返回为字符串数值，需转换
      maxSpaceSize.value = toNum(first.maxSize, maxSpaceSize.value)
      spaceTotalSize.value = toNum(first.totalSize, 0)
    } else {
      // 没有空间，直接跳转到创建空间页面
      router.push('/space/add')
      return
    }
  } catch (e) {
    console.error('获取用户空间失败', e)
  }
}

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

// 定义加载策略（参考爬取图片页面的逻辑）
const loadStrategies = [
  { name: '原始URL', url: (originalUrl: string) => originalUrl, attrs: {} },
  { name: '清理后URL', url: (originalUrl: string) => getCleanImageUrl(originalUrl), attrs: {} },
  { name: '添加crossorigin', url: (originalUrl: string) => getCleanImageUrl(originalUrl), attrs: { crossorigin: 'anonymous' } },
  { name: '添加referrerpolicy', url: (originalUrl: string) => getCleanImageUrl(originalUrl), attrs: { referrerpolicy: 'no-referrer' } },
  { name: '两个属性都加', url: (originalUrl: string) => getCleanImageUrl(originalUrl), attrs: { crossorigin: 'anonymous', referrerpolicy: 'no-referrer' } }
]

// 获取当前策略
const getCurrentStrategy = (idx: number) => {
  const strategyIndex = imageLoadStrategies.value.get(idx) || 0
  return loadStrategies[strategyIndex]
}

// 应用策略到图片元素
const applyStrategyToImage = (img: HTMLImageElement, strategy: any, originalUrl: string) => {
  // 设置src
  img.src = strategy.url(originalUrl)
  
  // 设置属性
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

// URL清理函数（参考爬取图片页面的逻辑）
const getCleanImageUrl = (url: string) => {
  if (!url) return url
  
  try {
    let cleanedUrl = url
    
    // 如果URL包含@符号，截取@之前的部分
    if (url.includes('@')) {
      cleanedUrl = url.split('@')[0]
      console.log(`URL清理(@): ${url} → ${cleanedUrl}`)
    }
    
    // 如果URL包含查询参数（?），截取?之前的部分
    if (url.includes('?')) {
      cleanedUrl = url.split('?')[0]
      console.log(`URL清理(?): ${url} → ${cleanedUrl}`)
    }
    
    // 如果URL包含#，截取#之前的部分
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

// 获取图片列表（限定空间ID）
const fetchPictureList = async () => {
  isLoading.value = true
  try {
    // 确保用户已登录
    if (!loginUserStore.loginUser.id) {
      await loginUserStore.fetchLoginUser()
    }
    
    if (!loginUserStore.loginUser.id) {
      console.error('用户未登录')
      return
    }
    
    const body = {
      current: currentPage.value,
      pageSize: pageSize.value,
      name: searchForm.name || undefined,
      category: searchForm.category || undefined,
      tags: searchForm.tags ? [searchForm.tags] : undefined,
      userId: loginUserStore.loginUser.id,
      // 指定空间ID（接口期望number，这里使用any以兼容长整型）
      spaceId: currentSpaceId.value ? (currentSpaceId.value as any) : undefined
    }
    
    const response = await listPictureVoByPageUsingPost(body)
    
    if (response.data?.code === 0 && response.data.data) {
      const pageData = response.data.data
      const images = pageData.records || []
      
      // 为每个图片添加loaded状态
      images.forEach((image: any) => {
        image.loaded = false
      })
      
      pictureList.value = images
      totalPictures.value = pageData.total || 0
      totalPages.value = pageData.pages || 0
      
      // 初始化图片状态（参考爬取图片页面的逻辑）
      imageLoadingStates.value = new Array(images.length).fill(true)
      imageErrorStates.value = new Array(images.length).fill(false)
      
      // 重置所有图片的策略索引
      imageLoadStrategies.value.clear()
      images.forEach((_, idx) => {
        imageLoadStrategies.value.set(idx, 0)
      })
    }
  } catch (error) {
    console.error('获取图片列表失败:', error)
  } finally {
    isLoading.value = false
  }
}

// 搜索处理
const handleSearch = () => {
  currentPage.value = 1
  fetchPictureList()
}

// 分页处理
const changePage = (page: number) => {
  // 边界检查：确保页码在有效范围内
  if (page < 1 || page > totalPages.value) {
    return
  }
  currentPage.value = page
  fetchPictureList()
}

// 查看图片
const viewPicture = (picture: any) => {
  // 可以跳转到图片详情页面或打开预览
  console.log('查看图片:', picture)
}

// 编辑图片
const editPicture = (picture: any) => {
  try {
    console.log('编辑图片:', picture) // 调试日志
    console.log('图片URL:', picture?.url) // 调试图片URL
    
    // 设置正在编辑的图片
    editingPicture.value = picture
    // 显示图片编辑器
    showImageEditor.value = true
  } catch (e) {
    console.error('打开编辑失败', e)
    alert('打开编辑失败，请重试')
  }
}

// 删除图片
const deletePicture = async (pictureId: number) => {
  if (confirm('确定要删除这张图片吗？')) {
    try {
      const response = await deletePictureUsingPost({ id: pictureId })
      if (response.data?.code === 0) {
        alert('删除成功')
        fetchPictureList()
      } else {
        alert('删除失败：' + (response.data?.message || '未知错误'))
      }
    } catch (error) {
      console.error('删除图片失败:', error)
      alert('删除失败，请重试')
    }
  }
}

// 跳转到上传页面
const goToUpload = () => {
  // 如果当前在查看特定空间，传递空间ID参数
  if (currentSpaceId.value && route.query.spaceId) {
    console.log('跳转上传页面，传递空间ID:', currentSpaceId.value)
    router.push({
      path: '/picture/upload',
      query: { spaceId: currentSpaceId.value }
    })
  } else {
    console.log('跳转上传页面，无空间ID参数')
    router.push('/picture/upload')
  }
}

// 跳转到创建空间
const goToCreateSpace = () => {
  router.push('/space/add')
}

// 跳转到我加入的空间
const showJoinedSpaces = () => {
  router.push('/joined-spaces')
}

// 管理空间成员
const manageMembers = () => {
  if (!currentSpaceId.value) {
    alert('请先选择一个空间')
    return
  }
  router.push({
    path: '/space/members',
    query: { spaceId: currentSpaceId.value }
  })
}

// 图片编辑成功回调
const onImageEditSuccess = (updatedPicture: any) => {
  console.log('图片编辑成功:', updatedPicture)
  // 刷新图片列表
  fetchPictureList()
  // 关闭编辑器
  closeImageEditor()
}

// 关闭图片编辑器
const closeImageEditor = () => {
  showImageEditor.value = false
  editingPicture.value = null
}

// 工具函数
const formatFileSize = (bytes: number): string => {
  if (!bytes && bytes !== 0) return '0 B'
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const formatDateTime = (v: any): string => {
  if (!v) return '-'
  const d = new Date(v)
  if (isNaN(d.getTime())) return '-'
  return d.toLocaleString('zh-CN')
}

// 获取页面标题
const getPageTitle = () => {
  if (currentSpaceId.value && route.query.spaceId) {
    return '团队空间'
  }
  return '我的空间'
}

// 获取页面副标题
const getPageSubtitle = () => {
  if (currentSpaceId.value && route.query.spaceId) {
    return '查看团队空间的图片'
  }
  return '管理您的专属图片收藏'
}

// 获取空状态标题
const getEmptyTitle = () => {
  if (currentSpaceId.value && route.query.spaceId) {
    return '该空间暂无图片'
  }
  return '暂无图片'
}

// 获取空状态文本
const getEmptyText = () => {
  if (currentSpaceId.value && route.query.spaceId) {
    return '这个团队空间中还没有图片'
  }
  return '您的空间中还没有图片，快去上传一些吧！'
}

// 获取用户角色文本
const getRoleText = () => {
  if (isViewer.value) return '访客'
  if (isEditor.value) return '编辑者'
  if (isAdmin.value) return '管理员'
  return '未知'
}

// 获取用户角色徽章类名
const getRoleBadgeClass = () => {
  if (isViewer.value) return 'viewer'
  if (isEditor.value) return 'editor'
  if (isAdmin.value) return 'admin'
  return ''
}

// 获取审核状态样式类名
const getReviewStatusClass = (reviewStatus: number) => {
  if (reviewStatus === 0) return 'pending'
  if (reviewStatus === 1) return 'approved'
  if (reviewStatus === 2) return 'rejected'
  return 'unknown'
}

// 图片加载完成处理（参考爬取图片页面的逻辑）
const onImageLoad = (event: Event) => {
  const img = event.target as HTMLImageElement
  const imageCard = img.closest('.picture-card')
  if (imageCard) {
    const pictureId = imageCard.getAttribute('data-picture-id')
    if (pictureId) {
      const imageIndex = pictureList.value.findIndex(pic => pic.id == pictureId)
      if (imageIndex !== -1) {
        const currentStrategyIndex = imageLoadStrategies.value.get(imageIndex) || 0
        const currentStrategy = loadStrategies[currentStrategyIndex]
        
        console.log(`图片加载成功 [${imageIndex}]: 策略=${currentStrategy.name}`)
        
        imageLoadingStates.value[imageIndex] = false
        imageErrorStates.value[imageIndex] = false
        
        const image = pictureList.value[imageIndex]
        if (image) {
          image.loaded = true
          // 添加loaded类到图片元素
          img.classList.add('loaded')
        }
      }
    }
  }
}

// 图片加载失败处理（参考爬取图片页面的逻辑）
const onImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  const imageCard = img.closest('.picture-card')
  if (imageCard) {
    const pictureId = imageCard.getAttribute('data-picture-id')
    if (pictureId) {
      const imageIndex = pictureList.value.findIndex(pic => pic.id == pictureId)
      if (imageIndex !== -1) {
        const originalUrl = pictureList.value[imageIndex].url
        const currentStrategyIndex = imageLoadStrategies.value.get(imageIndex) || 0
        
        console.warn(`图片加载失败 [${imageIndex}]: 当前策略=${loadStrategies[currentStrategyIndex].name}, URL=${img.src}`)
        
        // 如果还有更多策略可以尝试
        if (currentStrategyIndex < loadStrategies.length - 1) {
          const nextStrategyIndex = currentStrategyIndex + 1
          const nextStrategy = loadStrategies[nextStrategyIndex]
          
          // 更新策略索引
          imageLoadStrategies.value.set(imageIndex, nextStrategyIndex)
          
          // 重置加载状态，给下一个策略一次机会
          imageLoadingStates.value[imageIndex] = true
          imageErrorStates.value[imageIndex] = false
          
          // 应用下一个策略
          applyStrategyToImage(img, nextStrategy, originalUrl)
          return
        }
        
        // 如果所有策略都尝试过了，标记为最终失败
        console.error(`图片 [${imageIndex}] 所有策略都尝试失败: ${originalUrl}`)
        imageErrorStates.value[imageIndex] = true
        imageLoadingStates.value[imageIndex] = false
      }
    }
  }
}

// 重试加载图片
const retryLoadImage = (imageIndex: number) => {
  // 重置策略索引，从头开始
  imageLoadStrategies.value.set(imageIndex, 0)
  
  imageErrorStates.value[imageIndex] = false
  imageLoadingStates.value[imageIndex] = true
  
  const image = pictureList.value[imageIndex]
  if (image) {
    const img = document.querySelector(`[data-picture-id="${image.id}"] img`) as HTMLImageElement
    if (img) {
      // 重试时，从第一个策略开始
      const firstStrategy = loadStrategies[0]
      applyStrategyToImage(img, firstStrategy, image.url)
    }
  }
}

// 复制图片地址
const copyImageUrl = async (url: string) => {
  try {
    await navigator.clipboard.writeText(url)
    showMessage('图片地址已复制到剪贴板', 'success')
  } catch (error) {
    console.error('复制失败:', error)
    // 降级方案：使用传统方法复制
    const textArea = document.createElement('textarea')
    textArea.value = url
    document.body.appendChild(textArea)
    textArea.select()
    document.execCommand('copy')
    document.body.removeChild(textArea)
    showMessage('图片地址已复制到剪贴板', 'success')
  }
}

// 显示消息提示
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

// 组件挂载时获取数据
onMounted(() => {
  fetchTagCategories()
  // 先获取空间信息，再拉取图片
  fetchUserSpaceInfo().then(() => fetchPictureList())
  
  // 读取用户角色信息
  const urlUserRole = route.query.userRole as string
  if (urlUserRole) {
    userRole.value = urlUserRole
  }
})

// 监听路由参数变化
watch(() => route.query.spaceId, (newSpaceId) => {
  if (newSpaceId && newSpaceId !== currentSpaceId.value) {
    currentSpaceId.value = newSpaceId as string
    // 重新获取图片列表
    fetchPictureList()
  }
}, { immediate: true })

// 监听用户角色变化
watch(() => route.query.userRole, (newUserRole) => {
  if (newUserRole) {
    userRole.value = newUserRole as string
  }
}, { immediate: true })
</script>

<style scoped>
.my-space-container {
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
  width: 120px;
  height: 120px;
  top: 5%;
  left: 5%;
  animation-delay: 0s;
}

.shape-2 {
  width: 180px;
  height: 180px;
  top: 50%;
  right: 10%;
  animation-delay: 2s;
}

.shape-3 {
  width: 100px;
  height: 100px;
  bottom: 15%;
  left: 15%;
  animation-delay: 4s;
}

.shape-4 {
  width: 140px;
  height: 140px;
  top: 25%;
  right: 25%;
  animation-delay: 1s;
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
  max-width: 1400px;
  margin: 0 auto;
}

/* 页面标题 */
.page-header {
  text-align: center;
  margin-bottom: 3rem;
}

.page-title {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
  font-size: 3rem;
  font-weight: 700;
  color: #2d3748;
  margin-bottom: 1rem;
}

.title-icon {
  font-size: 3.5rem;
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

.title-decoration {
  width: 60px;
  height: 4px;
  background: linear-gradient(90deg, #667eea, #764ba2);
  border-radius: 2px;
  margin-left: 1rem;
}

.page-subtitle {
  color: #718096;
  font-size: 1.2rem;
  margin: 0;
}

.role-info {
  margin-top: 1rem;
  text-align: center;
}

.role-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.4rem 1rem;
  border-radius: 12px;
  font-size: 0.9rem;
  font-weight: 600;
  letter-spacing: 0.03em;
  border: 1px solid var(--border-color);
  background: rgba(17,24,39,0.6);
  backdrop-filter: blur(20px);
  color: #718096;
}

.role-badge.viewer {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: #fff;
}
.role-badge.editor {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}
.role-badge.admin {
  background: linear-gradient(135deg, #4299e1 0%, #3182ce 100%);
  color: #fff;
}

.create-space-btn {
  margin-left: auto;
  padding: 0.6rem 1rem;
  border: none;
  border-radius: 10px;
  background: linear-gradient(135deg, #10b981, #059669);
  color: #fff;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 10px 20px rgba(16,185,129,0.25);
}
.create-space-btn:hover { transform: translateY(-1px); box-shadow: 0 14px 28px rgba(16,185,129,0.35); }

.joined-spaces-btn {
  margin-left: 1rem;
  padding: 0.6rem 1rem;
  border: none;
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 10px 20px rgba(102, 126, 234, 0.25);
}
.joined-spaces-btn:hover { transform: translateY(-1px); box-shadow: 0 14px 28px rgba(102, 126, 234, 0.35); }

.manage-members-btn {
  margin-left: 1rem;
  padding: 0.6rem 1rem;
  border: none;
  border-radius: 10px;
  background: linear-gradient(135deg, #4299e1, #3182ce);
  color: #fff;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 10px 20px rgba(66, 153, 225, 0.25);
}
.manage-members-btn:hover { transform: translateY(-1px); box-shadow: 0 14px 28px rgba(66, 153, 225, 0.35); }

.button-group {
  display: flex;
  gap: 1rem;
  margin-left: 1rem;
  flex-wrap: wrap;
}

/* 搜索区域 */
.search-section {
  margin-bottom: 2rem;
}

.search-container {
  display: flex;
  gap: 1.5rem;
  align-items: end;
  flex-wrap: wrap;
  background: rgba(17,24,39,0.6);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 2rem;
  border: 1px solid var(--border-color);
}

.search-item {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  min-width: 200px;
}

.search-label {
  font-weight: 600;
  color: #2d3748;
  font-size: 0.9rem;
}

.input-wrapper {
  position: relative;
}

.search-input,
.search-select {
  width: 100%;
  padding: 0.8rem 1rem;
  border: 2px solid rgba(102, 126, 234, 0.2);
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.9);
  color: #2d3748;
  font-size: 0.95rem;
  transition: all 0.3s ease;
}

.search-input:focus,
.search-select:focus {
  outline: none;
  border-color: #667eea;
  background: rgba(255, 255, 255, 1);
  box-shadow: 0 0 20px rgba(102, 126, 234, 0.15);
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

.search-input:focus ~ .input-border,
.search-select:focus ~ .input-border {
  width: 100%;
}

.search-btn {
  padding: 0.8rem 2rem;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  border: none;
  border-radius: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.search-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
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

.search-btn:hover .btn-glow {
  left: 100%;
}

/* 统计信息 */
.stats-section {
  margin-bottom: 3rem;
}

.stats-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  background: rgba(17,24,39,0.6);
  backdrop-filter: blur(20px);
  border-radius: 16px;
  padding: 1.5rem;
  border: 1px solid var(--border-color);
  transition: all 0.3s ease;
}

.stat-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
}

.stat-icon {
  font-size: 2.5rem;
  animation: iconPulse 2s ease-in-out infinite;
}

@keyframes iconPulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 1.8rem;
  font-weight: 700;
  color: #667eea;
  margin-bottom: 0.3rem;
}

.stat-label {
  color: #718096;
  font-size: 0.9rem;
}

/* 空间使用情况圆环 */
.space-usage-section {
  margin-top: 2rem;
}

.usage-container {
  background: rgba(17,24,39,0.6);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 2rem;
  border: 1px solid var(--border-color);
}

.usage-title {
  text-align: center;
  font-size: 1.5rem;
  font-weight: 600;
  color: #2d3748;
  margin-bottom: 2rem;
}

.usage-circle-container {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 3rem;
  flex-wrap: wrap;
}

.usage-circle {
  position: relative;
  width: 120px;
  height: 120px;
}

.circle-svg {
  width: 100%;
  height: 100%;
  transform: rotate(-90deg);
}

.circle-content {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
}

.usage-percentage {
  font-size: 1.5rem;
  font-weight: 700;
  color: #667eea;
  line-height: 1;
}

.usage-text {
  font-size: 0.8rem;
  color: #718096;
  margin-top: 0.2rem;
}

.usage-details {
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
  min-width: 200px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 1rem;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  border: 1px solid rgba(102, 126, 234, 0.2);
}

.detail-label {
  color: var(--text-secondary);
  font-size: 0.9rem;
}

.detail-value {
  color: var(--text-primary);
  font-weight: 600;
  font-size: 0.9rem;
}

/* 图片展示区域 */
.pictures-section {
  margin-bottom: 3rem;
}

.pictures-container {
  min-height: 400px;
}

/* 加载状态 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
}

.loading-spinner {
  width: 60px;
  height: 60px;
  border: 4px solid rgba(102, 126, 234, 0.2);
  border-top: 4px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 1rem;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-text {
  color: #718096;
  font-size: 1.1rem;
}

/* 空状态 */
.empty-container {
  text-align: center;
  padding: 4rem 2rem;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
  opacity: 0.6;
}

.empty-title {
  font-size: 1.5rem;
  color: #2d3748;
  margin-bottom: 0.5rem;
}

.empty-text {
  color: #718096;
  margin-bottom: 2rem;
}

.upload-btn {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 1rem 2rem;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  border: none;
  border-radius: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.upload-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}

/* 图片网格 */
.pictures-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 2rem;
}

.picture-card {
  background: rgba(17,24,39,0.6);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  overflow: hidden;
  border: 1px solid var(--border-color);
  transition: all 0.3s ease;
  cursor: pointer;
}

.picture-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
}

.picture-image {
  position: relative;
  width: 100%;
  height: 250px;
  overflow: hidden;
}

.picture-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

/* 图片加载状态样式 */
.image-loading {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0,0,0,0.7);
  z-index: 10;
}

.loading-spinner-small {
  width: 24px;
  height: 24px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top: 2px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

/* 错误状态样式 */
.image-error {
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
  z-index: 10;
}

.error-icon {
  font-size: 1.5rem;
  margin-bottom: 0.5rem;
}

.error-text {
  font-size: 0.8rem;
  margin-bottom: 0.5rem;
}

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

.retry-btn:hover {
  background: #5a67d8;
}

.img-normal {
  opacity: 1;
  transition: opacity 0.3s ease;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.picture-card:hover .picture-image img {
  transform: scale(1.05);
}

.picture-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  opacity: 0;
  transition: opacity 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.picture-card:hover .picture-overlay {
  opacity: 1;
}

.overlay-actions {
  display: flex;
  gap: 1rem;
}

.action-btn {
  width: 50px;
  height: 50px;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2rem;
}

.edit-btn {
  background: rgba(59, 130, 246, 0.9);
  color: white;
}

.edit-btn:hover {
  background: rgba(59, 130, 246, 1);
  transform: scale(1.1);
}

.delete-btn {
  background: rgba(239, 68, 68, 0.9);
  color: white;
}

.delete-btn:hover {
  background: rgba(239, 68, 68, 1);
  transform: scale(1.1);
}

.picture-info {
  padding: 1.5rem;
}

.picture-name {
  font-size: 1.2rem;
  font-weight: 600;
  color: #2d3748;
  margin: 0 0 0.5rem 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.picture-intro {
  color: #718096;
  font-size: 0.9rem;
  margin: 0 0 1rem 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.picture-meta {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 0.3rem;
  color: #718096;
  font-size: 0.8rem;
}

.meta-icon {
  font-size: 0.9rem;
}

.picture-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.tag-item {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
  padding: 0.3rem 0.6rem;
  border-radius: 6px;
  font-size: 0.8rem;
  border: 1px solid rgba(102, 126, 234, 0.2);
}

.picture-visibility { margin-top: 0.5rem; }
.visibility-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.35rem;
  padding: 0.35rem 0.8rem;
  border-radius: 999px;
  font-size: 0.78rem;
  font-weight: 700;
  letter-spacing: 0.02em;
  border: none;
  box-shadow: 0 6px 14px rgba(0,0,0,0.18);
}
.visibility-badge.private {
  background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
  color: #fff;
}
.visibility-badge.public {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: #fff;
}

/* 分页区域 */
.pagination-section {
  display: flex;
  justify-content: center;
  margin-top: 3rem;
}

.pagination-container {
  display: flex;
  align-items: center;
  gap: 1rem;
  background: rgba(17,24,39,0.6);
  backdrop-filter: blur(20px);
  border-radius: 16px;
  padding: 1rem 2rem;
  border: 1px solid var(--border-color);
}

.page-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.8rem 1.5rem;
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
  border: 1px solid rgba(102, 126, 234, 0.2);
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 600;
}

.page-btn:hover:not(:disabled) {
  background: rgba(102, 126, 234, 0.2);
  transform: translateY(-2px);
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-numbers {
  display: flex;
  gap: 0.5rem;
}

.page-number {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.1);
  color: #2d3748;
  border: 1px solid rgba(102, 126, 234, 0.2);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 600;
}

.page-number:hover {
  background: rgba(102, 126, 234, 0.1);
  border-color: #667eea;
}

.page-number.active {
  background: #667eea;
  color: white;
  border-color: #667eea;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .my-space-container {
    padding: 1rem;
  }
  
  .page-title {
    font-size: 2.5rem;
  }
  
  .search-container {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-item {
    min-width: auto;
  }
  
  .stats-container {
    grid-template-columns: 1fr;
  }
  
  .usage-circle-container {
    flex-direction: column;
    gap: 2rem;
  }
  
  .usage-details {
    min-width: auto;
    width: 100%;
  }
  
  .pictures-grid {
    grid-template-columns: 1fr;
  }
  
  .pagination-container {
    flex-direction: column;
    gap: 1rem;
  }

  .button-group {
    flex-direction: column;
    margin-left: 0;
    margin-top: 1rem;
  }
}

@media (max-width: 480px) {
  .page-title {
    font-size: 2rem;
  }
  
  .title-icon {
    font-size: 2.5rem;
  }
  
  .search-container {
    padding: 1.5rem;
  }
  
  .usage-container {
    padding: 1.5rem;
  }
  
  .usage-circle {
    width: 100px;
    height: 100px;
  }
  
  .usage-percentage {
    font-size: 1.2rem;
  }
  
  .picture-card {
    margin: 0 0.5rem;
  }
}

/* 复制图片地址按钮样式 */
.copy-url-section {
  margin-top: 0.75rem;
  display: flex;
  justify-content: center;
}

.copy-url-btn {
  background: rgba(102, 126, 234, 0.2);
  border: 1px solid rgba(102, 126, 234, 0.3);
  color: #667eea;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.85rem;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 600;
}

.copy-url-btn:hover {
  background: rgba(102, 126, 234, 0.3);
  border-color: rgba(102, 126, 234, 0.5);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
}

.copy-icon {
  font-size: 1rem;
}

.copy-text {
  font-size: 0.85rem;
}

/* 审核状态覆盖层样式 */
.review-status-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 15;
}

.review-status-content {
  text-align: center;
  color: white;
}

.review-status-icon {
  font-size: 2.5rem;
  margin-bottom: 0.5rem;
  display: block;
}

.review-status-text {
  font-size: 1.1rem;
  font-weight: 600;
  letter-spacing: 0.05em;
}

/* 审核状态徽章样式 */
.review-status-badge {
  margin-top: 0.5rem;
  display: flex;
  justify-content: center;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  padding: 0.4rem 0.8rem;
  border-radius: 999px;
  font-size: 0.8rem;
  font-weight: 600;
  letter-spacing: 0.02em;
  border: none;
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.status-badge.pending {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  color: #fff;
}

.status-badge.approved {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: #fff;
}

.status-badge.rejected {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  color: #fff;
}

.status-badge.unknown {
  background: linear-gradient(135deg, #6b7280 0%, #4b5563 100%);
  color: #fff;
}

.status-icon {
  font-size: 0.9rem;
}

.status-text {
  font-size: 0.8rem;
}
</style>
