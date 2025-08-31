<template>
  <div class="joined-spaces-container">
    <!-- 动态背景 -->
    <div class="background-decoration">
      <div class="floating-shapes">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
        <div class="shape shape-4"></div>
      </div>
    </div>
    
    <!-- 主要内容 -->
    <div class="main-content">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1 class="page-title">
          <span class="title-icon">👥</span>
          <span class="title-text">我加入的空间</span>
          <span class="title-decoration"></span>
          <button class="back-btn" @click="goBack">返回</button>
        </h1>
        <p class="page-subtitle">查看您加入的所有团队空间</p>
      </div>
      
      <!-- 加载状态 -->
      <div v-if="isLoading" class="loading-container">
        <div class="loading-spinner"></div>
        <p class="loading-text">正在加载您加入的空间...</p>
      </div>
      
      <!-- 空状态 -->
      <div v-else-if="joinedSpaces.length === 0" class="empty-container">
        <div class="empty-icon">🏢</div>
        <h3 class="empty-title">暂无加入的空间</h3>
        <p class="empty-text">您还没有加入任何团队空间</p>
        <button class="create-space-btn" @click="goToCreateSpace">
          <span class="btn-icon">➕</span>
          <span class="btn-text">创建空间</span>
        </button>
      </div>
      
      <!-- 空间列表 -->
      <div v-else class="spaces-section">
        <div class="spaces-grid">
          <div 
            v-for="space in joinedSpaces" 
            :key="space.id" 
            class="space-card"
            @click="selectSpace(space)"
          >
            <div class="space-header">
              <div class="space-icon">🏢</div>
              <div class="space-info">
                <h3 class="space-name">{{ space.spaceName || space.space?.spaceName }}</h3>
                <p class="space-role">角色: {{ getRoleText(space.spaceRole) }}</p>
              </div>
            </div>
            <div class="space-stats">
              <div class="stat-item">
                <span class="stat-label">图片数量:</span>
                <span class="stat-value">{{ space.totalCount || space.space?.totalCount || 0 }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">已用空间:</span>
                <span class="stat-value">{{ formatFileSize(space.totalSize || space.space?.totalSize || 0) }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">空间级别:</span>
                <span class="stat-value">{{ getLevelText(space.spaceLevel || space.space?.spaceLevel) }}</span>
              </div>
            </div>
            <div class="space-actions">
              <button class="view-btn" @click.stop="viewSpacePictures(space)">
                <span class="btn-icon">👁️</span>
                <span class="btn-text">查看图片</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { listMyTeamSpaceUsingPost } from '../../a/api/spaceUserController'
import { useLoginUserStore } from '../../stores/useLoginUserStore'

// 状态管理
const isLoading = ref(false)
const joinedSpaces = ref<any[]>([])
const router = useRouter()
const loginUserStore = useLoginUserStore()

// 获取我加入的空间列表
const fetchJoinedSpaces = async () => {
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
    
    const response = await listMyTeamSpaceUsingPost()
    
    if (response.data?.code === 0 && response.data.data) {
      joinedSpaces.value = response.data.data || []
    }
  } catch (error) {
    console.error('获取加入的空间列表失败:', error)
  } finally {
    isLoading.value = false
  }
}

// 查看空间图片
const viewSpacePictures = (space: any) => {
  // 跳转到我的空间页面，并传递空间ID参数和用户角色
  const spaceId = space.spaceId || space.space?.id
  const userRole = space.spaceRole // 用户在该空间中的角色
  router.push({
    path: '/space/my',
    query: { 
      spaceId: spaceId,
      userRole: userRole // 传递用户角色
    }
  })
}

// 选择空间
const selectSpace = (space: any) => {
  viewSpacePictures(space)
}

// 返回上一页
const goBack = () => {
  router.back()
}

// 跳转到创建空间
const goToCreateSpace = () => {
  router.push('/space/add')
}

// 获取角色文本
const getRoleText = (role: string) => {
  const roleMap: { [key: string]: string } = {
    'admin': '管理员',
    'editor': '编辑者',
    'viewer': '查看者'
  }
  return roleMap[role] || role
}

// 获取级别文本
const getLevelText = (level: number) => {
  const levelMap: { [key: number]: string } = {
    0: '普通版',
    1: '专业版',
    2: '旗舰版'
  }
  return levelMap[level] || '未知'
}

// 格式化文件大小
const formatFileSize = (bytes: number): string => {
  if (!bytes && bytes !== 0) return '0 B'
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 组件挂载时获取数据
onMounted(() => {
  fetchJoinedSpaces()
})
</script>

<style scoped>
.joined-spaces-container {
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

.back-btn {
  margin-left: auto;
  padding: 0.6rem 1rem;
  border: none;
  border-radius: 10px;
  background: linear-gradient(135deg, #6b7280, #4b5563);
  color: #fff;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 10px 20px rgba(107, 114, 128, 0.25);
}

.back-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 14px 28px rgba(107, 114, 128, 0.35);
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

.create-space-btn {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 1rem 2rem;
  background: linear-gradient(135deg, #10b981, #059669);
  color: white;
  border: none;
  border-radius: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.create-space-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(16, 185, 129, 0.3);
}

/* 空间网格 */
.spaces-section {
  margin-bottom: 3rem;
}

.spaces-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 2rem;
}

.space-card {
  background: rgba(17, 24, 39, 0.6);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 2rem;
  border: 1px solid var(--border-color);
  transition: all 0.3s ease;
  cursor: pointer;
}

.space-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
}

.space-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.space-icon {
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

.space-info {
  flex: 1;
}

.space-name {
  font-size: 1.3rem;
  font-weight: 600;
  color: #2d3748;
  margin: 0 0 0.5rem 0;
}

.space-role {
  color: #667eea;
  font-size: 0.9rem;
  margin: 0;
  font-weight: 500;
}

.space-stats {
  margin-bottom: 1.5rem;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 0;
  border-bottom: 1px solid rgba(102, 126, 234, 0.1);
}

.stat-item:last-child {
  border-bottom: none;
}

.stat-label {
  color: #718096;
  font-size: 0.9rem;
}

.stat-value {
  color: #2d3748;
  font-weight: 600;
  font-size: 0.9rem;
}

.space-actions {
  display: flex;
  justify-content: center;
}

.view-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.8rem 1.5rem;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  border: none;
  border-radius: 10px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.view-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .joined-spaces-container {
    padding: 1rem;
  }
  
  .page-title {
    font-size: 2.5rem;
  }
  
  .spaces-grid {
    grid-template-columns: 1fr;
  }
  
  .space-card {
    padding: 1.5rem;
  }
}

@media (max-width: 480px) {
  .page-title {
    font-size: 2rem;
  }
  
  .title-icon {
    font-size: 2.5rem;
  }
  
  .space-card {
    margin: 0 0.5rem;
  }
}
</style>
