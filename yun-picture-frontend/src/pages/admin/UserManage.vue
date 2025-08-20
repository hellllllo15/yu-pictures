<template>
  <div class="user-management-container">
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
          <span class="title-icon">👥</span>
          <span class="title-text">用户管理系统</span>
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
        <div class="search-header">
          <h3 class="search-title">搜索筛选</h3>
          <div class="search-status" v-if="searchForm.userAccount || searchForm.userName || searchForm.userRole">
            <span class="status-indicator"></span>
            <span class="status-text">搜索中...</span>
          </div>
        </div>
        <div class="search-container">
          <div class="search-item">
            <label class="search-label">账号:</label>
            <div class="input-wrapper">
              <input 
                type="text" 
                placeholder="输入账号" 
                class="search-input"
                v-model="searchForm.userAccount"
              />
              <div class="input-border"></div>
            </div>
          </div>
          
          <div class="search-item">
            <label class="search-label">用户名:</label>
            <div class="input-wrapper">
              <input 
                type="text" 
                placeholder="输入用户名" 
                class="search-input"
                v-model="searchForm.userName"
              />
              <div class="input-border"></div>
            </div>
          </div>
          
          <div class="search-item">
            <label class="search-label">角色类型:</label>
            <div class="input-wrapper">
              <select class="search-input" v-model="searchForm.userRole">
                <option value="">全部角色</option>
                <option value="admin">admin (管理员)</option>
                <option value="user">user (普通用户)</option>
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
          
          <button class="reset-btn" @click="handleReset">
            <span class="btn-text">重置</span>
          </button>
        </div>
      </div>
      
      <!-- 数据表格区域 -->
      <div class="table-section">
        <div class="table-container">
          <table class="data-table">
            <thead>
              <tr class="table-header">
                <th>ID</th>
                <th>账号</th>
                <th>用户名</th>
                <th>头像</th>
                <th>简介</th>
                <th>用户角色</th>
                <th>创建时间</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="user in userList" :key="user.id" class="table-row">
                <td>{{ user.id }}</td>
                <td>{{ user.userAccount }}</td>
                <td>{{ user.userName || '无名' }}</td>
                <td>
                  <div class="avatar-cell">
                    <img 
                      :src="user.userAvatar || 'https://via.placeholder.com/40x40/6366f1/ffffff?text=U'" 
                      :alt="user.userName || '用户头像'"
                      class="user-avatar-img"
                    />
                  </div>
                </td>
                <td>{{ user.userProfile || '暂无简介' }}</td>
                <td>
                  <span class="role-badge" :class="getRoleClass(user.userRole)">
                    {{ user.userRole || 'user' }}
                  </span>
                </td>
                <td>{{ formatDate(user.createTime) }}</td>
                <td>
                  <div class="action-buttons">
                    <button class="edit-btn" @click="handleEdit(user)">
                      <span class="btn-text">编辑</span>
                    </button>
                    <button class="delete-btn" @click="handleDelete(user)">
                    <span class="btn-text">删除</span>
                  </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
      
          <!-- 分页组件 -->
      <div class="pagination-section">
            <div class="pagination-info">
              <span v-if="isLoading" class="loading-text">加载中...</span>
              <span v-else-if="userList.length === 0" class="no-data-text">暂无数据</span>
              <span v-else>
                共 {{ pagination.total }} 条记录，第 {{ pagination.current }}/{{ pagination.pages || 1 }} 页
                <span v-if="searchForm.userAccount || searchForm.userName || searchForm.userRole" class="search-tip">
                  (搜索结果)
                </span>
              </span>
          </div>
            <div class="pagination-controls" v-if="pagination.pages > 1">
              <button 
                class="pagination-btn" 
                :disabled="pagination.current <= 1"
                @click="handlePageChange(pagination.current - 1)"
              >
                上一页
              </button>
              
              <div class="page-numbers">
                <button 
                  v-for="page in getPageNumbers()" 
                  :key="page"
                  class="page-number"
                  :class="{ 'active': page === pagination.current }"
                  @click="handlePageChange(page)"
                >
                  {{ page }}
            </button>
          </div>
          
              <button 
                class="pagination-btn" 
                :disabled="pagination.current >= pagination.pages"
                @click="handlePageChange(pagination.current + 1)"
              >
                下一页
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive, watch } from 'vue'
import { listUserVoByPageUsingPost, deleteUserUsingPost } from '../../a/api/userController'

// 用户信息类型定义
interface UserVO {
  id?: number
  userAccount?: string
  userName?: string
  userAvatar?: string
  userProfile?: string
  userRole?: string
  createTime?: string
}

// 分页查询请求类型
interface UserQueryRequest {
  current?: number
  pageSize?: number
  userAccount?: string
  userName?: string
  userRole?: string
}

// 页面状态
const isContentVisible = ref(false)

// 搜索表单
const searchForm = reactive({
  userAccount: '',
  userName: '',
  userRole: ''
})

// 分页信息
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  pages: 0
})

// 用户列表
const userList = ref<UserVO[]>([])

// 加载状态
const isLoading = ref(false)

// 监听搜索条件变化，重置分页
watch([() => searchForm.userAccount, () => searchForm.userName, () => searchForm.userRole], (newValues, oldValues) => {
  // 检查是否有实际的搜索条件变化
  const hasSearchChange = newValues.some((value, index) => value !== oldValues?.[index])
  
  if (hasSearchChange && pagination.current !== 1) {
    // 当搜索条件变化时，重置分页到第一页
    pagination.current = 1
    // 延迟执行搜索，避免频繁API调用
    setTimeout(() => {
      if (searchForm.userAccount || searchForm.userName || searchForm.userRole) {
        fetchUserList()
      }
    }, 300)
  }
}, { deep: true })

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

// 搜索用户
const handleSearch = async () => {
  // 搜索后重置分页到第一页
  pagination.current = 1
  await fetchUserList()
}

// 重置搜索条件
const handleReset = () => {
  searchForm.userAccount = ''
  searchForm.userName = ''
  searchForm.userRole = ''
  // 重置分页到第一页
  pagination.current = 1
  handleSearch()
}

// 获取用户列表
const fetchUserList = async () => {
  isLoading.value = true
  try {
    const requestBody: UserQueryRequest = {
      current: pagination.current,
      pageSize: pagination.pageSize,
      userAccount: searchForm.userAccount || undefined,
      userName: searchForm.userName || undefined,
      userRole: searchForm.userRole || undefined
    }
    
    const response = await listUserVoByPageUsingPost(requestBody)
    
    if (response.data?.code === 0 && response.data?.data) {
      const pageData = response.data.data
      userList.value = pageData.records || []
      pagination.total = pageData.total || 0
      pagination.pages = pageData.pages || 0
      pagination.current = pageData.current || 1
    } else {
      console.error('获取用户列表失败:', response.data?.message)
      userList.value = []
      pagination.total = 0
      pagination.pages = 0
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
    userList.value = []
    pagination.total = 0
    pagination.pages = 0
  } finally {
    isLoading.value = false
  }
}

// 删除用户
const handleDelete = async (user: UserVO) => {
  if (!user.id) {
    alert('用户ID不存在')
    return
  }
  
  if (confirm(`确定要删除用户 "${user.userName || user.userAccount}" 吗？`)) {
    try {
      const response = await deleteUserUsingPost({ id: user.id })
      
      if (response.data?.code === 0 && response.data?.data) {
        alert('删除成功')
        // 重新获取用户列表
        await fetchUserList()
      } else {
        alert(response.data?.message || '删除失败')
      }
    } catch (error) {
      console.error('删除用户失败:', error)
      alert('删除失败，请重试')
    }
  }
}

// 编辑用户
const handleEdit = (user: UserVO) => {
  console.log('编辑用户:', user)
  // TODO: 跳转到编辑页面或打开编辑弹窗
  alert('编辑功能开发中...')
}

// 分页切换
const handlePageChange = async (page: number | string) => {
  if (typeof page === 'string' || page < 1 || page > pagination.pages) {
    return
  }
  
  // 更新当前页码
  pagination.current = page
  
  // 重新获取用户列表，保持搜索条件
  await fetchUserList()
  
  // 滚动到表格顶部，提升用户体验
  const tableSection = document.querySelector('.table-section')
  if (tableSection) {
    tableSection.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
}

// 获取分页数字数组
const getPageNumbers = () => {
  const pages = pagination.pages
  const current = pagination.current
  const delta = 2 // 显示当前页前后两页
  const range: (number | string)[] = []

  if (pages <= 1) {
    return [1]
  }

  if (current - delta > 1) {
    range.push(1)
    if (current - delta > 2) {
      range.push('...')
    }
  }

  for (let i = Math.max(1, current - delta); i <= Math.min(pages, current + delta); i++) {
    range.push(i)
  }

  if (current + delta < pages - 1) {
    if (current + delta < pages - 2) {
      range.push('...')
    }
    range.push(pages)
  }

  return range
}

// 格式化日期
const formatDate = (timestamp?: string) => {
  if (!timestamp) return '未知时间'
  
  try {
    const date = new Date(timestamp)
    if (isNaN(date.getTime())) return '未知时间'
    
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    
    return `${year}-${month}-${day} ${hours}:${minutes}`
  } catch (error) {
    return '未知时间'
  }
}

// 获取角色样式类
const getRoleClass = (role?: string) => {
  // admin: 管理员角色，user: 普通用户角色
  if (role === 'admin') {
    return 'admin-role'
  } else if (role === 'user') {
    return 'user-role'
  }
  return 'default-role'
}

// 页面加载
onMounted(async () => {
  // 页面加载动画
  setTimeout(() => {
    isContentVisible.value = true
  }, 300)
  
  // 获取用户列表
  await fetchUserList()
})
</script>

<style scoped>
.user-management-container {
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
  background: radial-gradient(circle, rgba(255, 255, 255, 0.8), rgba(255, 255, 255, 0.2));
  border-radius: 50%;
  animation: float var(--animation-duration) ease-in-out infinite;
  animation-delay: var(--animation-delay);
  box-shadow: 0 0 10px rgba(255, 255, 255, 0.3);
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg) scale(1);
    opacity: 0.4;
  }
  50% {
    transform: translateY(-120px) rotate(180deg) scale(1.2);
    opacity: 0.9;
  }
}

/* 流动网格背景 */
.grid-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: 
    linear-gradient(rgba(255, 255, 255, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
  background-size: 50px 50px;
  animation: gridMove 20s linear infinite;
  z-index: 2;
}

@keyframes gridMove {
  0% {
    transform: translate(0, 0);
  }
  100% {
    transform: translate(50px, 50px);
  }
}

/* 左侧动态装饰 */
.left-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 30%;
  height: 100%;
  z-index: 3;
  pointer-events: none;
}

.floating-card {
  position: absolute;
  width: 120px;
  height: 80px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.05));
  border-radius: 15px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  animation: cardFloat 8s ease-in-out infinite;
}

.card-1 {
  top: 20%;
  left: 15%;
  animation-delay: 0s;
}

.card-2 {
  top: 60%;
  left: 25%;
  animation-delay: 4s;
}

@keyframes cardFloat {
  0%, 100% {
    transform: translateY(0) rotate(0deg) scale(1);
    opacity: 0.6;
  }
  50% {
    transform: translateY(-40px) rotate(5deg) scale(1.1);
    opacity: 0.9;
  }
}

.energy-orb {
  position: absolute;
  width: 60px;
  height: 60px;
  background: radial-gradient(circle, rgba(102, 126, 234, 0.8), rgba(102, 126, 234, 0.2));
  border-radius: 50%;
  animation: orbPulse 6s ease-in-out infinite;
  box-shadow: 0 0 20px rgba(102, 126, 234, 0.5);
}

.orb-1 {
  top: 40%;
  left: 10%;
  animation-delay: 0s;
}

.orb-2 {
  top: 80%;
  left: 20%;
  animation-delay: 3s;
}

@keyframes orbPulse {
  0%, 100% {
    transform: scale(1);
    opacity: 0.6;
  }
  50% {
    transform: scale(1.3);
    opacity: 1;
  }
}

/* 右侧动态装饰 */
.right-decoration {
  position: absolute;
  top: 0;
  right: 0;
  width: 30%;
  height: 100%;
  z-index: 3;
  pointer-events: none;
}

.tech-circle {
  position: absolute;
  width: 100px;
  height: 100px;
  border: 3px solid rgba(255, 255, 255, 0.15);
  border-radius: 50%;
  animation: circleRotate 12s linear infinite;
}

.circle-1 {
  top: 25%;
  right: 20%;
  animation-delay: 0s;
}

.circle-2 {
  top: 65%;
  right: 15%;
  animation-delay: 6s;
}

@keyframes circleRotate {
  0% {
    transform: rotate(0deg) scale(1);
    opacity: 0.4;
  }
  50% {
    transform: rotate(180deg) scale(1.2);
    opacity: 0.8;
  }
  100% {
    transform: rotate(360deg) scale(1);
    opacity: 0.4;
  }
}

.data-stream {
  position: absolute;
  width: 2px;
  height: 80px;
  background: linear-gradient(to bottom, transparent, rgba(255, 255, 255, 0.3), transparent);
  animation: streamFlow 4s ease-in-out infinite;
}

.stream-1 {
  top: 15%;
  right: 35%;
  animation-delay: 0s;
}

.stream-2 {
  top: 75%;
  right: 30%;
  animation-delay: 2s;
}

@keyframes streamFlow {
  0%, 100% {
    height: 60px;
    opacity: 0.3;
  }
  50% {
    height: 120px;
    opacity: 0.8;
  }
}

/* 主要内容区域 */
.main-content {
  position: relative;
  z-index: 10;
  background: rgba(17,24,39,0.7);
  backdrop-filter: blur(20px);
  border-radius: 25px;
  padding: 2rem;
  box-shadow: 0 25px 60px rgba(0, 0, 0, 0.3);
  border: 1px solid var(--border-color);
  transform: translateY(50px) scale(0.9);
  opacity: 0;
  transition: all 0.8s cubic-bezier(0.4, 0, 0.2, 1);
  max-width: 1400px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  /* 隐藏滚动条但可滚动 */
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* IE/Edge */
}
.main-content::-webkit-scrollbar { display: none; } /* Chrome/Safari */

.main-content.content-visible {
  transform: translateY(0) scale(1);
  opacity: 1;
}

/* 页面标题 */
.page-header {
  text-align: center;
  margin-bottom: 2rem;
  padding-bottom: 1.5rem;
  border-bottom: 2px solid rgba(30, 60, 114, 0.1);
}

.page-title {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
  margin-bottom: 1rem;
  font-size: 2.2rem;
  font-weight: 700;
  color: #1e3c72;
}

.title-icon {
  font-size: 2.5rem;
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
  background: linear-gradient(90deg, #667eea, #2a5298);
  border-radius: 2px;
  animation: titleGlow 3s ease-in-out infinite;
}

@keyframes titleGlow {
  0%, 100% {
    box-shadow: 0 0 10px rgba(102, 126, 234, 0.5);
  }
  50% {
    box-shadow: 0 0 20px rgba(102, 126, 234, 0.8);
  }
}

.header-decoration {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
}

.decoration-line {
  width: 30px;
  height: 2px;
  background: linear-gradient(90deg, transparent, rgba(30, 60, 114, 0.3), transparent);
}

.decoration-dot {
  width: 6px;
  height: 6px;
  background: #667eea;
  border-radius: 50%;
  animation: dotPulse 2s ease-in-out infinite;
}

@keyframes dotPulse {
  0%, 100% {
    transform: scale(1);
    opacity: 0.7;
  }
  50% {
    transform: scale(1.3);
    opacity: 1;
  }
}

/* 搜索筛选区域 */
.search-section {
  margin-bottom: 2rem;
  padding: 1.5rem;
  background: rgba(17,24,39,0.5);
  border-radius: 15px;
  border: 1px solid var(--border-color);
}

.search-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid rgba(102, 126, 234, 0.1);
}

.search-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #1e3c72;
  margin-bottom: 0;
}

.search-status {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: rgba(16, 185, 129, 0.1);
  padding: 0.4rem 0.8rem;
  border-radius: 15px;
  border: 1px solid rgba(16, 185, 129, 0.2);
  color: #10b981;
  font-size: 0.9rem;
  font-weight: 500;
}

.status-indicator {
  width: 10px;
  height: 10px;
  background: #10b981;
  border-radius: 50%;
  animation: pulse 1.5s infinite ease-in-out;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(0.8);
    opacity: 0.7;
  }
  50% {
    transform: scale(1.2);
    opacity: 1;
  }
}

.search-container {
  display: flex;
  align-items: center;
  gap: 2rem;
  flex-wrap: wrap;
}

.search-item {
  display: flex;
  align-items: center;
  gap: 0.8rem;
}

.search-label {
  font-weight: 600;
  color: #1e3c72;
  white-space: nowrap;
}

.input-wrapper {
  position: relative;
}

.search-input {
  padding: 0.8rem 1rem;
  border: 2px solid rgba(102, 126, 234, 0.2);
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.8);
  color: #1e3c72;
  font-size: 0.95rem;
  transition: all 0.3s ease;
  min-width: 200px;
}

.search-input:focus {
  outline: none;
  border-color: #667eea;
  background: rgba(255, 255, 255, 1);
  box-shadow: 0 0 15px rgba(102, 126, 234, 0.2);
}

.input-border {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 2px;
  background: linear-gradient(90deg, #667eea, #2a5298);
  transition: width 0.3s ease;
}

.search-input:focus ~ .input-border {
  width: 100%;
}

/* 搜索按钮 */
.search-btn {
  padding: 0.8rem 1.5rem;
  background: linear-gradient(135deg, #667eea, #2a5298);
  border: none;
  border-radius: 12px;
  color: white;
  font-weight: 600;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
  min-width: 100px;
}

.search-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 35px rgba(102, 126, 234, 0.4);
}

.reset-btn {
  padding: 0.8rem 1.5rem;
  background: linear-gradient(135deg, #94a3b8, #64748b);
  border: none;
  border-radius: 12px;
  color: white;
  font-weight: 600;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
  box-shadow: 0 8px 25px rgba(148, 163, 184, 0.3);
  min-width: 100px;
}

.reset-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 35px rgba(148, 163, 184, 0.4);
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
  transition: left 0.6s ease;
}

.search-btn:hover .btn-glow {
  left: 100%;
}

.btn-particles span {
  position: absolute;
  width: 4px;
  height: 4px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 50%;
  animation: particleFloat 2s ease-in-out infinite;
}

.btn-particles span:nth-child(1) {
  top: 20%;
  left: 20%;
  animation-delay: 0s;
}

.btn-particles span:nth-child(2) {
  top: 60%;
  right: 20%;
  animation-delay: 0.5s;
}

.btn-particles span:nth-child(3) {
  bottom: 20%;
  left: 50%;
  animation-delay: 1s;
}

@keyframes particleFloat {
  0%, 100% {
    transform: translateY(0) scale(1);
    opacity: 0;
  }
  50% {
    transform: translateY(-20px) scale(1.5);
    opacity: 1;
  }
}

/* 数据表格区域 */
.table-section {
  margin-top: 2rem;
  background: rgba(17,24,39,0.65);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 2rem;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  border: 1px solid var(--border-color);
  overflow: visible; /* 去掉内层滚动条 */
}

.table-container {
  width: 100%;
  overflow: visible; /* 去掉内层滚动条 */
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 2rem;
  background: transparent;
  overflow: visible; /* 去掉内层滚动条 */
}

.table-header {
  background: linear-gradient(135deg, #1e3c72, #2a5298);
  color: white;
}

.table-header th {
  padding: 1rem;
  text-align: left;
  font-weight: 600;
  font-size: 0.95rem;
  border-bottom: 2px solid rgba(255, 255, 255, 0.1);
  position: relative;
}

.table-header th::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 2px;
  background: rgba(255, 255, 255, 0.8);
  transition: width 0.3s ease;
}

.table-header:hover th::after {
  width: 100%;
}

.table-row {
  transition: all 0.3s ease;
  border-bottom: 1px solid rgba(102, 126, 234, 0.1);
}

.table-row:hover {
  background: rgba(102, 126, 234, 0.05);
  transform: translateX(5px);
}

.table-row td {
  padding: 1rem;
  font-size: 0.9rem;
  color: var(--text-primary);
}

.id-cell {
  font-family: 'Courier New', monospace;
  color: #667eea;
  font-weight: 600;
}

.account-cell {
  color: #1e3c72;
  font-weight: 500;
}

.username-cell {
  color: #2a5298;
}

.avatar-cell {
  text-align: center;
}

.user-avatar-img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.intro-text {
  color: var(--text-secondary);
  font-style: italic;
}

.role-tag {
  background: linear-gradient(135deg, #4ade80, #22c55e);
  color: white;
  padding: 0.3rem 0.8rem;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(74, 222, 128, 0.3);
  animation: tagPulse 2s ease-in-out infinite;
}

.role-badge {
  padding: 0.3rem 0.8rem;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(74, 222, 128, 0.3);
  animation: tagPulse 2s ease-in-out infinite;
}

.admin-role {
  background: linear-gradient(135deg, #667eea, #2a5298);
  color: white;
}

.user-role {
  background: linear-gradient(135deg, #4ade80, #22c55e);
  color: white;
}

.default-role {
  background: #e0e0e0;
  color: #333;
}

@keyframes tagPulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

.time-cell {
  font-family: 'Courier New', monospace;
  color: var(--text-secondary);
  font-size: 0.85rem;
}

.action-cell {
  text-align: center;
}

.action-buttons {
  display: flex;
  gap: 0.5rem;
}

.edit-btn {
  padding: 0.5rem 1rem;
  background: linear-gradient(135deg, #4f46e5, #3b82f6);
  border: none;
  border-radius: 8px;
  color: white;
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(79, 70, 229, 0.3);
}

.edit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(79, 70, 229, 0.4);
}

.delete-btn {
  padding: 0.5rem 1rem;
  background: linear-gradient(135deg, #ef4444, #dc2626);
  border: none;
  border-radius: 8px;
  color: white;
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(239, 68, 68, 0.3);
}

.delete-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(239, 68, 68, 0.4);
}

.btn-danger-glow {
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.5s ease;
}

.delete-btn:hover .btn-danger-glow {
  left: 100%;
}

/* 分页区域 */
.pagination-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem 0;
  border-top: 1px solid rgba(102, 126, 234, 0.1);
  margin-top: 2rem;
}

.pagination-info {
  color: #1e3c72;
  font-weight: 600;
  font-size: 1rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.loading-text {
  color: #667eea;
  font-style: italic;
}

.no-data-text {
  color: #94a3b8;
  font-style: italic;
}

.search-tip {
  color: #10b981;
  font-size: 0.9rem;
  font-weight: 500;
  background: rgba(16, 185, 129, 0.1);
  padding: 0.25rem 0.5rem;
  border-radius: 6px;
  border: 1px solid rgba(16, 185, 129, 0.2);
}

.pagination-controls {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.pagination-btn {
  padding: 0.6rem 1rem;
  border: 2px solid rgba(102, 126, 234, 0.2);
  background: white;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 600;
  color: #1e3c72;
  min-width: 80px;
}

.pagination-btn:hover:not(:disabled) {
  border-color: #667eea;
  background: rgba(102, 126, 234, 0.1);
  transform: translateY(-2px);
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-numbers {
  display: flex;
  gap: 0.5rem;
}

.page-number {
  padding: 0.6rem 1rem;
  border: 2px solid rgba(102, 126, 234, 0.2);
  background: white;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 600;
  min-width: 40px;
  text-align: center;
}

.page-number:hover:not(.active) {
  border-color: #667eea;
  background: rgba(102, 126, 234, 0.1);
  transform: translateY(-2px);
}

.page-number.active {
  background: linear-gradient(135deg, #667eea, #2a5298);
  color: white;
  border-color: #667eea;
}

.arrow {
  font-size: 1.1rem;
  font-weight: bold;
}

.page-size-selector {
  position: relative;
}

.page-size-select {
  padding: 0.6rem 2rem 0.6rem 1rem;
  border: 2px solid rgba(102, 126, 234, 0.2);
  border-radius: 8px;
  background: white;
  color: #1e3c72;
  font-size: 0.9rem;
  cursor: pointer;
  appearance: none;
  transition: all 0.3s ease;
}

.page-size-select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 15px rgba(102, 126, 234, 0.2);
}

.select-arrow {
  position: absolute;
  right: 0.8rem;
  top: 50%;
  transform: translateY(-50%);
  color: #667eea;
  font-size: 0.8rem;
  pointer-events: none;
  transition: transform 0.3s ease;
}

.page-size-select:focus + .select-arrow {
  transform: translateY(-50%) rotate(180deg);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .search-container {
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 1.5rem;
  }
  
  .pagination-section {
    flex-direction: column;
    gap: 1rem;
    align-items: center;
  }
}

@media (max-width: 768px) {
  .main-content {
    padding: 1.5rem;
    margin: 1rem;
  }
  
  .page-title .title-main {
    font-size: 2rem;
  }
  
  .search-container {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
  
  .search-item {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-label {
    margin-bottom: 0.5rem;
    text-align: left;
  }
  
  .table-section {
    padding: 1rem;
    margin-top: 1rem;
  }
  
  .data-table {
    font-size: 0.9rem;
  }
  
  .table-header th,
  .table-row td {
    padding: 0.5rem 0.25rem;
  }
  
  .action-buttons {
    flex-direction: column;
    gap: 0.25rem;
  }
  
  .edit-btn,
  .delete-btn {
    padding: 0.4rem 0.8rem;
    font-size: 0.8rem;
  }
  
  .pagination-controls {
    flex-wrap: wrap;
    justify-content: center;
    gap: 0.5rem;
  }
  
  .page-numbers {
    order: -1;
    width: 100%;
    justify-content: center;
    margin-bottom: 0.5rem;
  }
}

@media (max-width: 480px) {
  .main-content {
    padding: 1rem;
    margin: 0.5rem;
  }
  
  .page-title .title-main {
    font-size: 1.5rem;
  }
  
  .page-title .title-sub {
    font-size: 0.9rem;
  }
  
  .search-section {
    padding: 1rem;
  }
  
  .table-section {
    padding: 0.75rem;
  }
  
  .data-table {
    font-size: 0.8rem;
  }
  
  .table-header th,
  .table-row td {
    padding: 0.4rem 0.2rem;
  }
  
  .pagination-info {
    font-size: 0.9rem;
    text-align: center;
  }
}
</style> 