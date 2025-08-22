<template>
    <div class="image-management-container">
      <!-- 动态粒子背景 -->
      <div class="particle-background">
        <div class="particle" v-for="n in 80" :key="n" :style="getParticleStyle(n)"></div>
      </div>
      
      <!-- 流动网格背景 -->
      <div class="grid-background"></div>
      
      <!-- 左侧动态装饰 -->
      <div class="left-decoration">
        <div class="floating-card card-1"></div>
        <div class="floating-card card-2"></div>
        <div class="energy-orb orb-1"></div>
        <div class="energy-orb orb-2"></div>
      </div>
      
      <!-- 右侧动态装饰 -->
      <div class="right-decoration">
        <div class="tech-circle circle-1"></div>
        <div class="tech-circle circle-2"></div>
        <div class="data-stream stream-1"></div>
        <div class="data-stream stream-2"></div>
      </div>
      
      <!-- 主要内容区域 -->
      <div class="main-content" :class="{ 'content-visible': isContentVisible }">
        <!-- 页面标题 -->
        <div class="page-header">
          <h1 class="page-title">
            <span class="title-icon">🖼️</span>
            <span class="title-text">图片管理系统</span>
            <span class="title-decoration"></span>
          </h1>
          <div class="header-decoration">
            <div class="decoration-line"></div>
            <div class="decoration-dot"></div>
            <div class="decoration-line"></div>
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
                  placeholder="输入图片名称" 
                  class="search-input"
                  v-model="searchForm.imageName"
                />
                <div class="input-border"></div>
              </div>
            </div>
            
            <div class="search-item">
              <label class="search-label">上传者:</label>
              <div class="input-wrapper">
                <input 
                  type="text" 
                  placeholder="输入上传者" 
                  class="search-input"
                  v-model="searchForm.uploader"
                />
                <div class="input-border"></div>
              </div>
            </div>
            
            <div class="search-item">
              <label class="search-label">图片类型:</label>
              <div class="input-wrapper">
                <select class="search-select" v-model="searchForm.imageType">
                  <option value="">全部类型</option>
                  <option value="jpg">JPG</option>
                  <option value="png">PNG</option>
                  <option value="gif">GIF</option>
                  <option value="webp">WebP</option>
                </select>
                <div class="input-border"></div>
              </div>
            </div>
            
            <div class="search-item">
              <label class="search-label">审核状态:</label>
              <div class="input-wrapper">
                <select class="search-select" v-model="searchForm.reviewStatus">
                  <option value="">全部状态</option>
                  <option value="0">待审核</option>
                  <option value="1">已通过</option>
                  <option value="2">已拒绝</option>
                </select>
                <div class="input-border"></div>
              </div>
            </div>
            
            <button class="search-btn" @click="handleSearch">
              <span class="btn-text">搜索</span>
              <div class="btn-glow"></div>
              <div class="btn-particles">
                <span></span>
                <span></span>
                <span></span>
              </div>
            </button>
          </div>
        </div>
        
        <!-- 操作按钮区域 -->
        <div class="action-section">
          <div class="action-container">
            <button class="upload-btn disabled" disabled>
              <span class="btn-icon">📤</span>
              <span class="btn-text">上传图片</span>
              <div class="btn-glow"></div>
            </button>
            
            <button class="batch-btn disabled" disabled>
              <span class="btn-icon">🗑️</span>
              <span class="btn-text">批量删除</span>
              <div class="btn-glow"></div>
            </button>
            
            <button class="export-btn disabled" disabled>
              <span class="btn-icon">📥</span>
              <span class="btn-text">导出数据</span>
              <div class="btn-glow"></div>
            </button>
          </div>
        </div>
        
        <!-- 数据表格区域 -->
        <div class="table-section">
          <div class="table-container">
            <table class="data-table">
              <thead>
                <tr class="table-header">
                  <th>id</th>
                  <th>图片</th>
                  <th>名称</th>
                  <th>简介</th>
                  <th>类型</th>
                  <th>标签</th>
                  <th>图片信息</th>
                  <th>用户id</th>
                  <th>创建时间</th>
                  <th>审核状态</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr 
                  v-for="image in imageList" 
                  :key="image.id" 
                  class="table-row"
                >
                  <td class="id-cell">{{ shortId(image.id) }}</td>
                  <td class="preview-cell">
                    <div class="image-preview">
                      <img :src="image.thumbnail" :alt="image.name" class="preview-img" />
                    </div>
                  </td>
                  <td class="name-cell">
                    <div class="image-name-container">
                      <span class="image-name">{{ image.name }}</span>
                    </div>
                  </td>
                  <td class="intro-cell"><span class="clamp-3">{{ image.profile || '-' }}</span></td>
                  <td class="type-cell">
                    <span class="type-tag" :class="`type-${image.type.toLowerCase()}`">{{ image.type.toUpperCase() }}</span>
                  </td>
                  <td class="tags-cell">
                    <span v-for="tag in image.tags" :key="tag" class="tag-badge">{{ tag }}</span>
                  </td>
                  <td class="imginfo-cell">
                    <div class="image-info">
                      <div>格式: {{ (image.format || image.type).toUpperCase() }}</div>
                      <div>宽度: {{ image.width }}</div>
                      <div>高度: {{ image.height }}</div>
                      <div>宽高比: {{ formatRatio(image.width, image.height) }}</div>
                      <div>大小: {{ formatFileSize(image.size) }}</div>
                    </div>
                  </td>
                  <td class="uploader-cell">{{ shortId(image.userId) }}</td>
                  <td class="time-cell">{{ formatDateTime(image.createTime) }}</td>
                  <td class="review-status-cell">
                    <span class="status-tag" :class="`status-${image.reviewStatus}`">
                      {{ getReviewStatusText(image.reviewStatus) }}
                    </span>
                  </td>
                  <td class="action-cell">
                    <div class="action-buttons">
                      <button class="view-btn disabled" disabled title="查看">
                        <span class="btn-icon">👁️</span>
                      </button>
                      <button class="edit-btn" @click="editImage(image)" title="编辑">
                        <span class="btn-icon">✏️</span>
                      </button>
                      <button 
                        class="review-btn" 
                        @click="openReviewDialog(image)" 
                        title="审核"
                      >
                        <span class="btn-icon">✅</span>
                      </button>
                      <button class="delete-btn" @click="deleteImage(image.id)" title="删除">
                        <span class="btn-icon">🗑️</span>
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
        
        <!-- 分页区域 -->
        <div class="pagination-section">
          <div class="pagination-container">
            <div class="total-info">
              <span class="total-text">共{{ totalItems }}张图片</span>
            </div>
            
            <div class="pagination-controls">
              <button class="page-btn prev-btn" :disabled="currentPage === 1" @click="handlePrevPage">
                <span class="arrow">←</span>
              </button>
              <button class="page-btn current-page">{{ currentPage }}</button>
              <button class="page-btn next-btn" :disabled="currentPage >= totalPages" @click="handleNextPage">
                <span class="arrow">→</span>
              </button>
            </div>
          </div>
            </div>
            
        <!-- 审核对话框 -->
        <teleport to="body">
          <div v-if="showReviewDialog" class="review-dialog-overlay" @click="closeReviewDialog">
            <div class="review-dialog" role="dialog" aria-modal="true" @click.stop>
              <div class="review-dialog-header">
                <h3>审核图片</h3>
                <button class="close-btn" @click="closeReviewDialog" aria-label="关闭">×</button>
              </div>
              <div class="review-dialog-content">
                <div class="review-form">
                  <div class="form-group">
                    <label>审核结果:</label>
                    <select v-model="reviewForm.status" class="review-select">
                      <option value="1">通过</option>
                      <option value="2">拒绝</option>
              </select>
            </div>
                  <div class="form-group">
                    <label>审核意见:</label>
                    <textarea 
                      v-model="reviewForm.message" 
                      placeholder="请输入审核意见（可选）"
                      class="review-textarea"
                      rows="3"
                    ></textarea>
          </div>
        </div>
              </div>
              <div class="review-dialog-actions">
                <button class="btn btn-secondary" @click="closeReviewDialog">取消</button>
                <button class="btn btn-primary" @click="submitReview" :disabled="isSubmitting">
                  {{ isSubmitting ? '提交中...' : '确认审核' }}
                </button>
              </div>
            </div>
          </div>
        </teleport>
      </div>
    </div>
  </template>
  
  <script setup lang="ts">
  import { ref, onMounted, reactive, watch, onUnmounted } from 'vue'
  import { useRouter } from 'vue-router'
  import { listPictureByPageUsingPost, deletePictureUsingPost, doPictureReviewUsingPost } from '../../a/api/pictureController'
  
  const isContentVisible = ref(false)
  const isLoading = ref(false)
  const showReviewDialog = ref(false)
  const isSubmitting = ref(false)
  const currentReviewImage = ref<any>(null)
  const router = useRouter()
  
  const searchForm = reactive({
    imageName: '',
    uploader: '',
    imageType: '',
    reviewStatus: ''
  })
  
  const reviewForm = reactive({
    status: 1,
    message: ''
  })
  
  const currentPage = ref(1)
  const pageSize = ref(10) // 固定每页10张
  const totalItems = ref(0)
  const totalPages = ref(1)
  
  const imageList = ref([
    {
      id: '1866859179860885505',
      name: '2024-12-11_HLN32CIZK9VAaYv5',
      profile: 'a',
      type: 'webp',
      format: 'webp',
      width: 300,
      height: 300,
      size: 6172,
      thumbnail: 'https://picsum.photos/80/80?random=11',
      tags: ['生活','搞笑'],
      userId: '186609',
      createTime: '2024-12-11 22:54:47',
      reviewStatus: 0
    },
    {
      id: '1866859179860885506',
      name: '海边日落',
      profile: '黄昏与海',
      type: 'jpg',
      format: 'jpg',
      width: 1920,
      height: 1080,
      size: 3145728,
      thumbnail: 'https://picsum.photos/80/80?random=22',
      tags: ['风景'],
      userId: '188001',
      createTime: '2024-11-02 18:20:10',
      reviewStatus: 1
    }
  ])
  
  // 生成粒子样式
  const getParticleStyle = (index: number) => {
    const size = Math.random() * 6 + 3
    const animationDelay = Math.random() * 15
    const animationDuration = Math.random() * 8 + 12
    
    return {
      '--particle-size': `${size}px`,
      '--animation-delay': `${animationDelay}s`,
      '--animation-duration': `${animationDuration}s`
    }
  }
  
  const normalizeTags = (tags: any): string[] => {
    if (!tags) return []
    if (Array.isArray(tags)) return tags
    if (typeof tags === 'string') {
      try { const arr = JSON.parse(tags); return Array.isArray(arr) ? arr : [tags] } catch { return tags.split(',').map((s) => s.trim()).filter(Boolean) }
    }
    return []
  }
  
  const normalizePicture = (r: any) => {
    const picFormat = (r.picFormat ?? r.format ?? r.type ?? 'JPG').toString()
    return {
      id: r.id ?? r.pictureId ?? r.picId ?? '-',
      name: r.name ?? r.picName ?? r.title ?? '-',
      profile: r.profile ?? r.introduction ?? r.description ?? '-',
      type: picFormat.toUpperCase(),
      format: picFormat.toUpperCase(),
      width: r.width ?? r.picWidth ?? r.imageWidth ?? '-',
      height: r.height ?? r.picHeight ?? r.imageHeight ?? '-',
      size: r.size ?? r.picSize ?? r.bytes ?? 0,
      thumbnail: r.thumbnail ?? r.thumbUrl ?? r.url ?? r.picUrl ?? 'https://via.placeholder.com/80',
      tags: normalizeTags(r.tags ?? r.tagList ?? r.picTags),
      userId: r.userId ?? r.ownerId ?? '-',
      createTime: r.createTime ?? r.createdAt ?? '-',
      reviewStatus: r.reviewStatus ?? r.status ?? 0,
    }
  }
  
  // 数值安全转换
  const toNum = (v: any, def = 0) => {
    const n = Number(v)
    return Number.isFinite(n) ? n : def
  }
  
  const fetchImageList = async () => {
    isLoading.value = true
    try {
      const body: any = {
        current: toNum(currentPage.value, 1),
        pageSize: 10, // 固定每页10张
        name: searchForm.imageName || undefined,
        userId: searchForm.uploader || undefined,
        picFormat: searchForm.imageType ? String(searchForm.imageType).toUpperCase() : undefined,
        reviewStatus: searchForm.reviewStatus ? toNum(searchForm.reviewStatus) : undefined,
      }
      // 使用正确的API路由 /list/page
      const resp = await listPictureByPageUsingPost(body)
      const pageData: any = resp?.data?.data
      if (pageData) {
        const records = Array.isArray(pageData.records) ? pageData.records : []
        imageList.value = records.map(normalizePicture)
        totalItems.value = toNum(pageData.total, records.length)
        totalPages.value = Math.max(1, toNum(pageData.pages, 1))
        const cp = toNum(pageData.current, toNum(currentPage.value, 1))
        currentPage.value = Math.min(Math.max(1, cp), toNum(totalPages.value, 1))
      } else {
        imageList.value = []
        totalItems.value = 0
        totalPages.value = 1
      }
    } catch (e) {
      console.error('获取图片列表失败:', e)
      imageList.value = []
      totalItems.value = 0
      totalPages.value = 1
    } finally {
      isLoading.value = false
    }
  }
  
  const handleSearch = () => {
    currentPage.value = 1
    fetchImageList()
  }
  
  const handlePrevPage = () => {
    const curr = toNum(currentPage.value, 1)
    if (curr > 1) {
      currentPage.value = curr - 1
      fetchImageList()
    }
  }
  
  const handleNextPage = () => {
    const curr = toNum(currentPage.value, 1)
    const total = toNum(totalPages.value, 1)
    if (curr < total) {
      currentPage.value = curr + 1
      fetchImageList()
    }
  }
  
  // 获取审核状态文本
  const getReviewStatusText = (status: number) => {
    const statusMap: { [key: number]: string } = {
      0: '待审核',
      1: '已通过',
      2: '已拒绝'
    }
    return statusMap[status] || '未知'
  }
  
  // 打开审核对话框
  const openReviewDialog = (image: any) => {
    currentReviewImage.value = image
    reviewForm.status = 1
    reviewForm.message = ''
    showReviewDialog.value = true
  }
  
  // 关闭审核对话框
  const closeReviewDialog = () => {
    showReviewDialog.value = false
    currentReviewImage.value = null
  }
  
  // 提交审核
  const submitReview = async () => {
    if (!currentReviewImage.value) return
    
    isSubmitting.value = true
    try {
      await doPictureReviewUsingPost({
        id: currentReviewImage.value.id,
        reviewStatus: reviewForm.status,
        reviewMessage: reviewForm.message
      })
      
      alert('审核提交成功')
      closeReviewDialog()
      fetchImageList() // 刷新列表
    } catch (error) {
      console.error('审核提交失败:', error)
      alert('审核提交失败，请重试')
    } finally {
      isSubmitting.value = false
    }
  }

  // 弹窗打开时禁止页面滚动
  const toggleBodyScroll = (lock: boolean) => {
    const body = document.body
    if (!body) return
    if (lock) {
      body.style.overflow = 'hidden'
    } else {
      body.style.overflow = ''
    }
  }

  watch(showReviewDialog, (val) => {
    toggleBodyScroll(!!val)
  })

  onUnmounted(() => {
    toggleBodyScroll(false)
  })
  
  const downloadImage = (image: any) => {
    alert(`下载图片: ${image.name}`)
  }
  
  const editImage = (image: any) => {
    try {
      const query: any = {
        id: image.id,
        name: image.name,
        introduction: image.profile || '',
        category: (image as any).category || '',
        tags: Array.isArray(image.tags) ? JSON.stringify(image.tags) : (image.tags || '[]'),
        url: (image as any).url || image.thumbnail || ''
      }
      router.push({ path: '/picture/upload', query })
    } catch (e) {
      console.error('跳转编辑失败', e)
      alert('跳转编辑失败，请重试')
    }
  }
  
  const deleteImage = async (imageId: string) => {
    if (confirm('确定要删除这张图片吗？')) {
      try {
        // 直接传递字符串ID，避免JavaScript数字精度丢失
        await deletePictureUsingPost({ id: imageId as any })
      alert('删除成功')
        // 重新获取数据
        fetchImageList()
      } catch (error) {
        console.error('删除图片失败:', error)
        alert('删除失败，请重试')
      }
    }
  }
  
  const shortId = (val: any) => {
    const s = String(val ?? '')
    return s.length > 10 ? s.slice(0, 10) + '...' : s
  }
  
  const formatFileSize = (bytes: number) => {
    if (!bytes && bytes !== 0) return '-'
    if (bytes === 0) return '0 B'
    const k = 1024
    const sizes = ['B', 'KB', 'MB', 'GB']
    const i = Math.floor(Math.log(bytes) / Math.log(k))
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
  }
  
  const toDate = (input: any): Date | null => {
    if (!input) return null
    const s = String(input)
    if (/^\d+$/.test(s)) {
      const n = Number(s)
      return new Date(n < 1e12 ? n * 1000 : n)
    }
    const iso = s.includes('T') ? s : s.replace(' ', 'T')
    const d = new Date(iso)
    return isNaN(d.getTime()) ? null : d
  }
  
  const pad = (n: number) => String(n).padStart(2, '0')
  const formatDateTime = (v: any) => {
    const d = toDate(v)
    if (!d) return '-'
    return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
  }
  
  const formatRatio = (w?: number, h?: number) => {
    if (!w || !h) return '-'
    const r = w / h
    return Number.isFinite(r) ? r.toFixed(2) : '-'
  }
  
  const getStatusText = (status: string) => {
    const statusMap: { [key: string]: string } = {
      active: '正常',
      pending: '待审核',
      inactive: '已禁用'
    }
    return statusMap[status] || status
  }
  
  onMounted(() => {
    setTimeout(() => { isContentVisible.value = true }, 300)
    fetchImageList()
  })
  </script>
  
  <style scoped>
  .image-management-container {
    width: 100vw;
    height: 100vh;
    position: relative;
    overflow: hidden;
    background: transparent;
    display: flex;
    align-items: center;
    justify-content: center;
    min-height: 100vh;
    min-width: 100vw;
    padding: 2rem;
    box-sizing: border-box;
    opacity: 0.9;
    color: var(--text-primary);
  }
  
  /* 动态粒子背景 */
  .particle-background { position: absolute; top: 0; left: 0; width: 100%; height: 100%; z-index: 1; }
  .particle { position: absolute; width: var(--particle-size); height: var(--particle-size); background: radial-gradient(circle, rgba(255, 255, 255, 0.8), rgba(255, 255, 255, 0.2)); border-radius: 50%; animation: float var(--animation-duration) ease-in-out infinite; animation-delay: var(--animation-delay); box-shadow: 0 0 10px rgba(255, 255, 255, 0.3); }
  @keyframes float { 0%, 100% { transform: translateY(0px) rotate(0deg) scale(1); opacity: 0.4; } 50% { transform: translateY(-120px) rotate(180deg) scale(1.2); opacity: 0.9; } }
  
  /* 流动网格背景 */
  .grid-background { position: absolute; top: 0; left: 0; width: 100%; height: 100%; background-image: linear-gradient(rgba(255, 255, 255, 0.03) 1px, transparent 1px), linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px); background-size: 50px 50px; animation: gridMove 20s linear infinite; z-index: 2; }
  @keyframes gridMove { 0% { transform: translate(0, 0); } 100% { transform: translate(50px, 50px); } }
  
  /* 左侧动态装饰 */
  .left-decoration { position: absolute; top: 0; left: 0; width: 30%; height: 100%; z-index: 3; pointer-events: none; }
  .floating-card { position: absolute; width: 120px; height: 80px; background: linear-gradient(135deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.05)); border-radius: 15px; border: 1px solid rgba(255, 255, 255, 0.1); backdrop-filter: blur(10px); animation: cardFloat 8s ease-in-out infinite; }
  .card-1 { top: 20%; left: 15%; animation-delay: 0s; }
  .card-2 { top: 60%; left: 25%; animation-delay: 4s; }
  @keyframes cardFloat { 0%, 100% { transform: translateY(0) rotate(0deg) scale(1); opacity: 0.6; } 50% { transform: translateY(-40px) rotate(5deg) scale(1.1); opacity: 0.9; } }
  .energy-orb { position: absolute; width: 60px; height: 60px; background: radial-gradient(circle, rgba(102, 126, 234, 0.8), rgba(102, 126, 234, 0.2)); border-radius: 50%; animation: orbPulse 6s ease-in-out infinite; box-shadow: 0 0 20px rgba(102, 126, 234, 0.5); }
  .orb-1 { top: 40%; left: 10%; animation-delay: 0s; }
  .orb-2 { top: 80%; left: 20%; animation-delay: 3s; }
  @keyframes orbPulse { 0%, 100% { transform: scale(1); opacity: 0.6; } 50% { transform: scale(1.3); opacity: 1; } }
  
  /* 右侧动态装饰 */
  .right-decoration { position: absolute; top: 0; right: 0; width: 30%; height: 100%; z-index: 3; pointer-events: none; }
  .tech-circle { position: absolute; width: 100px; height: 100px; border: 3px solid rgba(255, 255, 255, 0.15); border-radius: 50%; animation: circleRotate 12s linear infinite; }
  .circle-1 { top: 25%; right: 20%; animation-delay: 0s; }
  .circle-2 { top: 65%; right: 15%; animation-delay: 6s; }
  @keyframes circleRotate { 0% { transform: rotate(0deg) scale(1); opacity: 0.4; } 50% { transform: rotate(180deg) scale(1.2); opacity: 0.8; } 100% { transform: rotate(360deg) scale(1); opacity: 0.4; } }
  .data-stream { position: absolute; width: 2px; height: 80px; background: linear-gradient(to bottom, transparent, rgba(255, 255, 255, 0.3), transparent); animation: streamFlow 4s ease-in-out infinite; }
  .stream-1 { top: 15%; right: 35%; animation-delay: 0s; }
  .stream-2 { top: 75%; right: 30%; animation-delay: 2s; }
  @keyframes streamFlow { 0%, 100% { height: 60px; opacity: 0.3; } 50% { height: 120px; opacity: 0.8; } }
  
  /* 主要内容区域（隐藏滚动条但可滚动） */
  .main-content { position: relative; z-index: 10; background: rgba(17,24,39,0.7); backdrop-filter: blur(20px); border-radius: 25px; padding: 2rem; box-shadow: 0 25px 60px rgba(0, 0, 0, 0.3); border: 1px solid var(--border-color); transform: translateY(50px) scale(0.9); opacity: 0; transition: all 0.8s cubic-bezier(0.4, 0, 0.2, 1); max-width: 1600px; width: 100%; max-height: 90vh; overflow-y: auto; scrollbar-width: none; -ms-overflow-style: none; }
  .main-content::-webkit-scrollbar { display: none; }
  .main-content.content-visible { transform: translateY(0) scale(1); opacity: 1; }
  
  /* 页面标题 */
  .page-header { text-align: center; margin-bottom: 2rem; padding-bottom: 1.5rem; border-bottom: 2px solid rgba(30, 60, 114, 0.1); }
  .page-title { display: flex; align-items: center; justify-content: center; gap: 1rem; margin-bottom: 1rem; font-size: 2.2rem; font-weight: 700; color: #1e3c72; }
  .title-icon { font-size: 2.5rem; animation: iconBounce 2s ease-in-out infinite; }
  @keyframes iconBounce { 0%, 100% { transform: translateY(0); } 50% { transform: translateY(-10px); } }
  .title-decoration { width: 60px; height: 4px; background: linear-gradient(90deg, #667eea, #2a5298); border-radius: 2px; animation: titleGlow 3s ease-in-out infinite; }
  @keyframes titleGlow { 0%, 100% { box-shadow: 0 0 10px rgba(102, 126, 234, 0.5); } 50% { box-shadow: 0 0 20px rgba(102, 126, 234, 0.8); } }
  .header-decoration { display: flex; align-items: center; justify-content: center; gap: 1rem; }
  .decoration-line { width: 30px; height: 2px; background: linear-gradient(90deg, transparent, rgba(30, 60, 114, 0.3), transparent); }
  .decoration-dot { width: 6px; height: 6px; background: #667eea; border-radius: 50%; animation: dotPulse 2s ease-in-out infinite; }
  @keyframes dotPulse { 0%, 100% { transform: scale(1); opacity: 0.7; } 50% { transform: scale(1.3); opacity: 1; } }
  
  /* 搜索筛选区域 */
  .search-section { margin-bottom: 2rem; padding: 1.5rem; background: rgba(17,24,39,0.5); border-radius: 15px; border: 1px solid var(--border-color); }
  .search-container { display: flex; align-items: center; gap: 2rem; flex-wrap: wrap; }
  .search-item { display: flex; align-items: center; gap: 0.8rem; }
  .search-label { font-weight: 600; color: #1e3c72; white-space: nowrap; }
  .input-wrapper { position: relative; }
  .search-input, .search-select { padding: 0.8rem 1rem; border: 2px solid rgba(102, 126, 234, 0.2); border-radius: 10px; background: rgba(255, 255, 255, 0.8); color: #1e3c72; font-size: 0.95rem; transition: all 0.3s ease; min-width: 200px; }
  .search-input:focus, .search-select:focus { outline: none; border-color: #667eea; background: rgba(255, 255, 255, 1); box-shadow: 0 0 15px rgba(102, 126, 234, 0.2); }
  .input-border { position: absolute; bottom: 0; left: 0; width: 0; height: 2px; background: linear-gradient(90deg, #667eea, #2a5298); transition: width 0.3s ease; }
  .search-input:focus ~ .input-border { width: 100%; }
  
  /* 搜索按钮 */
  .search-btn { padding: 0.8rem 2rem; background: linear-gradient(135deg, #667eea, #2a5298); border: none; border-radius: 10px; color: white; font-size: 1rem; font-weight: 600; cursor: pointer; position: relative; overflow: hidden; transition: all 0.3s ease; box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3); }
  .search-btn:hover { transform: translateY(-2px); box-shadow: 0 12px 35px rgba(102, 126, 234, 0.4); }
  .btn-text { position: relative; z-index: 2; }
  .btn-glow { position: absolute; top: 0; left: -100%; width: 100%; height: 100%; background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent); transition: left 0.6s ease; }
  .search-btn:hover .btn-glow { left: 100%; }
  .btn-particles span { position: absolute; width: 4px; height: 4px; background: rgba(255, 255, 255, 0.8); border-radius: 50%; animation: particleFloat 2s ease-in-out infinite; }
  .btn-particles span:nth-child(1) { top: 20%; left: 20%; animation-delay: 0s; }
  .btn-particles span:nth-child(2) { top: 60%; right: 20%; animation-delay: 0.5s; }
  .btn-particles span:nth-child(3) { bottom: 20%; left: 50%; animation-delay: 1s; }
  @keyframes particleFloat { 0%, 100% { transform: translateY(0) scale(1); opacity: 0; } 50% { transform: translateY(-20px) scale(1.5); opacity: 1; } }
  
  /* 操作按钮区域 */
  .action-section { margin-bottom: 2rem; }
  .action-container { display: flex; gap: 1rem; flex-wrap: wrap; }
  .upload-btn, .batch-btn, .export-btn { padding: 0.8rem 1.5rem; border: none; border-radius: 10px; color: white; font-size: 0.95rem; font-weight: 600; cursor: pointer; position: relative; overflow: hidden; transition: all 0.3s ease; display: flex; align-items: center; gap: 0.5rem; }
  .upload-btn { background: linear-gradient(135deg, #10b981, #059669); box-shadow: 0 8px 25px rgba(16, 185, 129, 0.3); }
  .batch-btn { background: linear-gradient(135deg, #f59e0b, #d97706); box-shadow: 0 8px 25px rgba(245, 158, 11, 0.3); }
  .export-btn { background: linear-gradient(135deg, #8b5cf6, #7c3aed); box-shadow: 0 8px 25px rgba(139, 92, 246, 0.3); }
  .upload-btn:hover, .batch-btn:hover, .export-btn:hover { transform: translateY(-2px); }
  .upload-btn:hover { box-shadow: 0 12px 35px rgba(16, 185, 129, 0.4); }
  .batch-btn:hover { box-shadow: 0 12px 35px rgba(245, 158, 11, 0.4); }
  .export-btn:hover { box-shadow: 0 12px 35px rgba(139, 92, 246, 0.4); }
  .btn-icon { font-size: 1.1rem; }
  
  /* 数据表格区域 */
  .table-section { margin-bottom: 2rem; }
  .table-container { background: rgba(17,24,39,0.65); border-radius: 15px; overflow: hidden; box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1); border: 1px solid var(--border-color); }
  .data-table { width: 100%; border-collapse: collapse; }
  .table-header { background: linear-gradient(135deg, #1e3c72, #2a5298); color: white; }
  .table-header th { padding: 1rem; text-align: left; font-weight: 600; font-size: 0.9rem; border-bottom: 2px solid rgba(255, 255, 255, 0.1); position: relative; }
  .table-header th::after { content: ''; position: absolute; bottom: 0; left: 0; width: 0; height: 2px; background: rgba(255, 255, 255, 0.8); transition: width 0.3s ease; }
  .table-header:hover th::after { width: 100%; }
  .table-row { transition: all 0.3s ease; border-bottom: 1px solid rgba(102, 126, 234, 0.1); }
  .table-row:hover { background: rgba(102, 126, 234, 0.05); transform: translateX(5px); }
  .table-row td { padding: 1rem; font-size: 0.9rem; color: var(--text-primary); }
  
  /* 复选框样式 */
  .checkbox-cell { text-align: center; }
  .select-all, .row-checkbox { width: 18px; height: 18px; cursor: pointer; accent-color: #667eea; }
  
  /* 图片预览样式 */
  .preview-cell { text-align: center; }
  .image-preview {
    position: relative;
    width: 80px;
    height: 80px;
    border-radius: 10px;
    overflow: hidden;
    cursor: default;
    transition: all 0.3s ease;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  }
  
  .preview-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  
  /* 图片名称样式 */
  .name-cell { max-width: 200px; }
  .image-name-container { display: flex; flex-direction: column; gap: 0.2rem; }
  .image-name {
    font-weight: 600;
    color: #1e3c72;
    word-wrap: break-word; 
    word-break: break-all; 
    line-height: 1.4; 
    max-height: 3em; 
    overflow: hidden;
  }
  .image-extension { font-size: 0.8rem; color: #666; font-family: 'Courier New', monospace; }
  
  /* 文件大小样式 */
  .size-cell { font-family: 'Courier New', monospace; color: #667eea; font-weight: 600; }
  
  /* 图片类型标签 */
  .type-cell { text-align: center; }
  .type-tag { padding: 0.3rem 0.8rem; border-radius: 20px; font-size: 0.8rem; font-weight: 600; color: white; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2); }
  .type-jpg { background: linear-gradient(135deg, #ef4444, #dc2626); }
  .type-png { background: linear-gradient(135deg, #3b82f6, #2563eb); }
  .type-gif { background: linear-gradient(135deg, #8b5cf6, #7c3aed); }
  .type-webp { background: linear-gradient(135deg, #10b981, #059669); }
  
  /* 上传者样式 */
  .uploader-cell { color: #2a5298; font-weight: 500; }
  
  /* 时间样式 */
  .time-cell { font-family: 'Courier New', monospace; color: var(--text-secondary); font-size: 0.85rem; }
  
  /* 状态标签 */
  .status-cell { text-align: center; }
  .status-tag { padding: 0.3rem 0.8rem; border-radius: 20px; font-size: 0.8rem; font-weight: 600; color: white; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2); }
  .status-active { background: linear-gradient(135deg, #10b981, #059669); }
  .status-pending { background: linear-gradient(135deg, #f59e0b, #d97706); }
  .status-inactive { background: linear-gradient(135deg, #6b7280, #4b5563); }
  
  /* 操作按钮 */
  .action-cell { text-align: center; }
  .action-buttons { display: flex; gap: 0.5rem; justify-content: center; }
  .view-btn, .download-btn, .edit-btn, .delete-btn { width: 35px; height: 35px; border: none; border-radius: 8px; cursor: pointer; display: flex; align-items: center; justify-content: center; transition: all 0.3s ease; font-size: 1rem; }
  .view-btn { background: linear-gradient(135deg, #3b82f6, #2563eb); color: white; }
  .download-btn { background: linear-gradient(135deg, #10b981, #059669); color: white; }
  .edit-btn { background: linear-gradient(135deg, #f59e0b, #d97706); color: white; }
  .delete-btn { background: linear-gradient(135deg, #ef4444, #dc2626); color: white; }
  .view-btn:hover, .download-btn:hover, .edit-btn:hover, .delete-btn:hover { transform: translateY(-2px); box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3); }
  
  /* 审核按钮 */
  .review-btn { width: 35px; height: 35px; border: none; border-radius: 8px; cursor: pointer; display: flex; align-items: center; justify-content: center; transition: all 0.3s ease; font-size: 1rem; background: linear-gradient(135deg, #10b981, #059669); color: white; }
  .review-btn:hover { transform: translateY(-2px); box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3); }
  
  /* 审核状态标签 */
  .review-status-cell { text-align: center; }
  .status-tag { padding: 0.3rem 0.8rem; border-radius: 20px; font-size: 0.8rem; font-weight: 600; color: white; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2); }
  .status-0 { background: linear-gradient(135deg, #f59e0b, #d97706); }
  .status-1 { background: linear-gradient(135deg, #10b981, #059669); }
  .status-2 { background: linear-gradient(135deg, #ef4444, #dc2626); }
  
  /* 审核对话框样式 */
  .review-dialog-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 10000;
  }
  
  .review-dialog {
    background: rgba(17, 24, 39, 0.95);
    border-radius: 15px;
    padding: 2rem;
    min-width: 400px;
    max-width: 500px;
    border: 1px solid var(--border-color);
    backdrop-filter: blur(20px);
  }
  
  .review-dialog-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1.5rem;
    padding-bottom: 1rem;
    border-bottom: 1px solid var(--border-color);
  }
  
  .review-dialog-header h3 {
    margin: 0;
    color: var(--text-primary);
    font-size: 1.5rem;
  }
  
  .close-btn {
    background: none;
    border: none;
    color: var(--text-secondary);
    font-size: 1.5rem;
    cursor: pointer;
    padding: 0.5rem;
    border-radius: 50%;
    transition: all 0.3s ease;
  }
  
  .close-btn:hover {
    background: rgba(255, 255, 255, 0.1);
    color: var(--text-primary);
  }
  
  .review-form .form-group {
    margin-bottom: 1.5rem;
  }
  
  .review-form label {
    display: block;
    margin-bottom: 0.5rem;
    color: var(--text-primary);
    font-weight: 600;
  }
  
  .review-select {
    width: 100%;
    padding: 0.8rem;
    border: 2px solid var(--border-color);
    border-radius: 8px;
    background: rgba(255, 255, 255, 0.1);
    color: var(--text-primary);
    font-size: 1rem;
  }
  
  .review-textarea {
    width: 100%;
    padding: 0.8rem;
    border: 2px solid var(--border-color);
    border-radius: 8px;
    background: rgba(255, 255, 255, 0.1);
    color: var(--text-primary);
    font-size: 1rem;
    resize: vertical;
  }
  
  .review-dialog-actions {
    display: flex;
    gap: 1rem;
    justify-content: flex-end;
    margin-top: 2rem;
    padding-top: 1rem;
    border-top: 1px solid var(--border-color);
  }
  
  .review-dialog-actions .btn {
    padding: 0.8rem 1.5rem;
    border: none;
    border-radius: 8px;
    font-size: 1rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
  }
  
  .review-dialog-actions .btn-secondary {
    background: rgba(160, 174, 192, 0.3);
    color: var(--text-secondary);
  }
  
  .review-dialog-actions .btn-primary {
    background: linear-gradient(135deg, #667eea, #764ba2);
    color: white;
  }
  
  .review-dialog-actions .btn:hover {
    transform: translateY(-2px);
  }
  
  /* 分页区域 */
  .pagination-section { padding: 1.5rem; background: linear-gradient(135deg, rgba(102, 126, 234, 0.05), rgba(42, 82, 152, 0.05)); border-radius: 15px; border: 1px solid rgba(102, 126, 234, 0.1); }
  .pagination-container { display: flex; align-items: center; justify-content: space-between; flex-wrap: wrap; gap: 1rem; }
  .total-info { color: #1e3c72; font-weight: 600; display: flex; gap: 1rem; }
  .total-text { font-size: 1rem; }
  .selected-info { color: #667eea; font-size: 0.9rem; }
  .pagination-controls { display: flex; align-items: center; gap: 0.5rem; }
  .page-btn { padding: 0.6rem 1rem; border: 2px solid rgba(102, 126, 234, 0.2); background: white; border-radius: 8px; cursor: pointer; transition: all 0.3s ease; font-weight: 600; min-width: 40px; }
  .page-btn:hover:not(:disabled) { border-color: #667eea; background: rgba(102, 126, 234, 0.1); transform: translateY(-2px); }
  .page-btn:disabled { opacity: 0.5; cursor: not-allowed; }
  .current-page { background: linear-gradient(135deg, #667eea, #2a5298); color: white; border-color: #667eea; box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3); }
  .arrow { font-size: 1.1rem; font-weight: bold; }
  .page-size-selector { position: relative; }
  .page-size-select { padding: 0.6rem 2rem 0.6rem 1rem; border: 2px solid rgba(102, 126, 234, 0.2); border-radius: 8px; background: white; color: #1e3c72; font-size: 0.9rem; cursor: pointer; appearance: none; transition: all 0.3s ease; }
  .page-size-select:focus { outline: none; border-color: #667eea; box-shadow: 0 0 15px rgba(102, 126, 234, 0.2); }
  .select-arrow { position: absolute; right: 0.8rem; top: 50%; transform: translateY(-50%); color: #667eea; font-size: 0.8rem; pointer-events: none; transition: transform 0.3s ease; }
  .page-size-select:focus + .select-arrow { transform: translateY(-50%) rotate(180deg); }
  
  /* 响应式设计 */
  @media (max-width: 1400px) { .search-container { flex-direction: column; align-items: stretch; } .search-item { justify-content: space-between; } .search-input, .search-select { min-width: 150px; } .action-container { justify-content: center; } }
  @media (max-width: 768px) { .main-content { padding: 1.5rem; margin: 1rem; } .page-title { font-size: 1.8rem; flex-direction: column; gap: 0.5rem; } .table-container { overflow-x: auto; } .pagination-container { flex-direction: column; align-items: center; gap: 1rem; } .action-buttons { flex-direction: column; gap: 0.3rem; } }
  @media (max-width: 480px) { .main-content { padding: 1rem; } .page-title { font-size: 1.5rem; } .search-section { padding: 1rem; } .table-row td { padding: 0.8rem 0.5rem; font-size: 0.8rem; } .image-preview { width: 60px; height: 60px; } }
  
  /* 禁用按钮样式 */
  .disabled {
    opacity: 0.5;
    cursor: not-allowed !important;
    pointer-events: none;
  }
  
  .disabled:hover {
    transform: none !important;
    box-shadow: none !important;
  }
  </style> 