<template>
  <div class="space-management-container">
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
          <span class="title-icon">🏠</span>
          <span class="title-text">空间管理系统</span>
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
            <label class="search-label">空间名称:</label>
            <div class="input-wrapper">
              <input 
                type="text" 
                placeholder="请输入空间名称" 
                class="search-input"
                v-model="searchForm.spaceName"
              />
              <div class="input-border"></div>
            </div>
          </div>
          
          <div class="search-item">
            <label class="search-label">空间级别:</label>
            <div class="input-wrapper">
              <select class="search-select" v-model="searchForm.spaceLevel">
                <option value="">请选择空间级别</option>
                <option v-for="level in spaceLevels" :key="level.value" :value="level.value">
                  {{ level.label }}
                </option>
              </select>
              <div class="input-border"></div>
            </div>
          </div>
          
          <div class="search-item">
            <label class="search-label">用户 ID:</label>
            <div class="input-wrapper">
              <input 
                type="text" 
                placeholder="请输入用户 ID" 
                class="search-input"
                v-model="searchForm.userId"
              />
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
          <button class="add-btn" @click="handleAddSpace">
            <span class="btn-icon">➕</span>
            <span class="btn-text">添加空间</span>
            <div class="btn-glow"></div>
          </button>
          
          <button class="batch-btn" @click="handleBatchDelete" :disabled="selectedSpaces.length === 0">
            <span class="btn-icon">🗑️</span>
            <span class="btn-text">批量删除</span>
            <div class="btn-glow"></div>
          </button>
          
          <button class="export-btn" @click="handleExport">
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
                <th>
                  <input type="checkbox" class="select-all" v-model="selectAll" @change="handleSelectAll" />
                </th>
                <th>ID</th>
                <th>空间名称</th>
                <th>空间级别</th>
                <th>使用情况</th>
                <th>用户 ID</th>
                <th>创建时间</th>
                <th>更新时间</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr 
                v-for="space in spaceList" 
                :key="space.id" 
                class="table-row"
              >
                <td class="checkbox-cell">
                  <input 
                    type="checkbox" 
                    class="row-checkbox" 
                    :value="space.id" 
                    v-model="selectedSpaces"
                  />
                </td>
                <td class="id-cell">{{ shortId(space.id) }}</td>
                <td class="name-cell">
                  <div class="space-name-container">
                    <span class="space-name">{{ space.spaceName }}</span>
                  </div>
                </td>
                <td class="level-cell">
                  <span class="level-tag" :class="`level-${space.spaceLevel}`">
                    {{ getSpaceLevelText(Number(space.spaceLevel)) }}
                  </span>
                </td>
                <td class="usage-cell">
                  <div class="usage-info">
                    <div>大小: {{ formatFileSize(space.totalSize || 0) }} / {{ formatFileSize(space.maxSize || 0) }}</div>
                    <div>数量: {{ space.totalCount || 0 }} / {{ space.maxCount || 0 }}</div>
                  </div>
                </td>
                <td class="user-cell">{{ shortId(space.userId) }}</td>
                <td class="time-cell">{{ formatDateTime(space.createTime) }}</td>
                <td class="time-cell">{{ formatDateTime(space.editTime) }}</td>
                <td class="action-cell">
                  <div class="action-buttons">
                    <button class="edit-btn" @click="editSpace(space)" title="编辑">
                      <span class="btn-icon">✏️</span>
                    </button>
                    <button class="delete-btn" @click="deleteSpace(space.id)" title="删除">
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
            <span class="total-text">共{{ totalItems }}条</span>
            <span class="selected-info" v-if="selectedSpaces.length > 0">
              已选择 {{ selectedSpaces.length }} 条
            </span>
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
          
          <div class="page-size-selector">
            <select class="page-size-select" v-model="pageSize" @change="handlePageSizeChange">
              <option value="10">10 / page</option>
              <option value="20">20 / page</option>
              <option value="50">50 / page</option>
            </select>
            <span class="select-arrow">▼</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive, watch, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { 
  listSpaceByPageUsingPost, 
  deleteSpaceUsingPost, 
  listSpaceLevelUsingGet 
} from '../../a/api/spaceController'

const isContentVisible = ref(false)
const isLoading = ref(false)
const router = useRouter()

const searchForm = reactive({
  spaceName: '',
  spaceLevel: '',
  userId: ''
})

const currentPage = ref(1)
const pageSize = ref(10)
const totalItems = ref(0)
const totalPages = ref(1)
const selectedSpaces = ref<string[]>([])
const selectAll = ref(false)

const spaceList = ref<any[]>([])
const spaceLevels = ref<any[]>([])

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

// 获取空间级别列表
const fetchSpaceLevels = async () => {
  try {
    const response = await listSpaceLevelUsingGet()
    if (response.data?.code === 0 && response.data?.data) {
      // 转换数据结构，提取text字段并映射到对应的value
      spaceLevels.value = response.data.data.map((level: any) => ({
        value: level.value,
        label: level.text,
        maxCount: level.maxCount,
        maxSize: level.maxSize
      }))
    }
  } catch (error) {
    console.error('获取空间级别失败:', error)
  }
}

// 获取空间列表
const fetchSpaceList = async () => {
  isLoading.value = true
  try {
    const body: any = {
      current: currentPage.value,
      pageSize: pageSize.value,
      spaceName: searchForm.spaceName || undefined,
      spaceLevel: searchForm.spaceLevel || undefined, // 使用spaceLevel参数
      userId: searchForm.userId || undefined
    }
    
    const response = await listSpaceByPageUsingPost(body)
    if (response.data?.code === 0 && response.data?.data) {
      const pageData = response.data.data
      spaceList.value = pageData.records || []
      totalItems.value = pageData.total || 0
      totalPages.value = Math.max(1, pageData.pages || 1)
      currentPage.value = Math.min(Math.max(1, pageData.current || 1), totalPages.value)
    } else {
      spaceList.value = []
      totalItems.value = 0
      totalPages.value = 1
    }
  } catch (error) {
    console.error('获取空间列表失败:', error)
    spaceList.value = []
    totalItems.value = 0
    totalPages.value = 1
  } finally {
    isLoading.value = false
  }
}

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchSpaceList()
}

// 处理分页
const handlePrevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
    fetchSpaceList()
  }
}

const handleNextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
    fetchSpaceList()
  }
}

const handlePageSizeChange = () => {
  currentPage.value = 1
  fetchSpaceList()
}

// 处理全选
const handleSelectAll = () => {
  if (selectAll.value) {
    selectedSpaces.value = spaceList.value.map(space => space.id)
  } else {
    selectedSpaces.value = []
  }
}

// 监听选择变化
watch(selectedSpaces, (newVal) => {
  selectAll.value = newVal.length === spaceList.value.length && spaceList.value.length > 0
})

// 添加空间
const handleAddSpace = () => {
  router.push('/space/add')
}

// 编辑空间
const editSpace = (space: any) => {
  router.push({ 
    path: '/space/add', 
    query: { 
      id: space.id, 
      spaceName: space.spaceName, 
      spaceLevel: space.spaceLevel 
    } 
  })
}

// 删除空间
const deleteSpace = async (spaceId: string) => {
  if (confirm('确定要删除这个空间吗？')) {
    try {
      await deleteSpaceUsingPost({ id: Number(spaceId) })
      alert('删除成功')
      fetchSpaceList()
    } catch (error) {
      console.error('删除空间失败:', error)
      alert('删除失败，请重试')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  if (selectedSpaces.value.length === 0) {
    alert('请先选择要删除的空间')
    return
  }
  
  if (confirm(`确定要删除选中的 ${selectedSpaces.value.length} 个空间吗？`)) {
    try {
      for (const spaceId of selectedSpaces.value) {
        await deleteSpaceUsingPost({ id: Number(spaceId) })
      }
      alert('批量删除成功')
      selectedSpaces.value = []
      fetchSpaceList()
    } catch (error) {
      console.error('批量删除失败:', error)
      alert('批量删除失败，请重试')
    }
  }
}

// 导出数据
const handleExport = () => {
  alert('导出功能开发中...')
}

// 工具函数
const shortId = (val: any) => {
  const s = String(val ?? '')
  return s.length > 10 ? s.slice(0, 10) + '...' : s
}

const formatFileSize = (bytes: number) => {
  if (!bytes && bytes !== 0) return '0 B'
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const formatDateTime = (v: any) => {
  if (!v) return '-'
  const d = new Date(v)
  if (isNaN(d.getTime())) return '-'
  return d.toLocaleString('zh-CN')
}

const getSpaceLevelText = (level: number) => {
  const levelMap: { [key: number]: string } = {
    0: '普通版',
    1: '专业版',
    2: '旗舰版'
  }
  return levelMap[level] || `级别${level}`
}

onMounted(() => {
  setTimeout(() => { isContentVisible.value = true }, 300)
  fetchSpaceLevels()
  fetchSpaceList()
})
</script>

<style scoped>
.space-management-container {
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

/* 主要内容区域 */
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
.add-btn, .batch-btn, .export-btn { padding: 0.8rem 1.5rem; border: none; border-radius: 10px; color: white; font-size: 0.95rem; font-weight: 600; cursor: pointer; position: relative; overflow: hidden; transition: all 0.3s ease; display: flex; align-items: center; gap: 0.5rem; }
.add-btn { background: linear-gradient(135deg, #10b981, #059669); box-shadow: 0 8px 25px rgba(16, 185, 129, 0.3); }
.batch-btn { background: linear-gradient(135deg, #f59e0b, #d97706); box-shadow: 0 8px 25px rgba(245, 158, 11, 0.3); }
.export-btn { background: linear-gradient(135deg, #8b5cf6, #7c3aed); box-shadow: 0 8px 25px rgba(139, 92, 246, 0.3); }
.add-btn:hover, .batch-btn:hover, .export-btn:hover { transform: translateY(-2px); }
.add-btn:hover { box-shadow: 0 12px 35px rgba(16, 185, 129, 0.4); }
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

/* 空间名称样式 */
.name-cell { max-width: 200px; }
.space-name-container { display: flex; flex-direction: column; gap: 0.2rem; }
.space-name { font-weight: 600; color: #1e3c72; word-wrap: break-word; word-break: break-all; line-height: 1.4; max-height: 3em; overflow: hidden; }

/* 空间级别标签 */
.level-cell { text-align: center; }
.level-tag { padding: 0.3rem 0.8rem; border-radius: 20px; font-size: 0.8rem; font-weight: 600; color: white; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2); }
.level-0 { background: linear-gradient(135deg, #10b981, #059669); } /* 普通版 */
.level-1 { background: linear-gradient(135deg, #3b82f6, #2563eb); } /* 专业版 */
.level-2 { background: linear-gradient(135deg, #8b5cf6, #7c3aed); } /* 旗舰版 */

/* 使用情况样式 */
.usage-cell { font-size: 0.85rem; }
.usage-info { display: flex; flex-direction: column; gap: 0.2rem; }

/* 用户ID样式 */
.user-cell { color: #2a5298; font-weight: 500; }

/* 时间样式 */
.time-cell { font-family: 'Courier New', monospace; color: var(--text-secondary); font-size: 0.85rem; }

/* 操作按钮 */
.action-cell { text-align: center; }
.action-buttons { display: flex; gap: 0.5rem; justify-content: center; }
.edit-btn, .delete-btn { width: 35px; height: 35px; border: none; border-radius: 8px; cursor: pointer; display: flex; align-items: center; justify-content: center; transition: all 0.3s ease; font-size: 1rem; }
.edit-btn { background: linear-gradient(135deg, #3b82f6, #2563eb); color: white; }
.delete-btn { background: linear-gradient(135deg, #ef4444, #dc2626); color: white; }
.edit-btn:hover, .delete-btn:hover { transform: translateY(-2px); box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3); }

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
@media (max-width: 480px) { .main-content { padding: 1rem; } .page-title { font-size: 1.5rem; } .search-section { padding: 1rem; } .table-row td { padding: 0.8rem 0.5rem; font-size: 0.8rem; } }
</style>
