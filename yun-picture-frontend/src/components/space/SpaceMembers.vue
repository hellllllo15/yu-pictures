<template>
  <div class="space-members-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">
        <span class="title-icon">👥</span>
        <span class="title-text">管理空间成员</span>
        <span class="title-decoration"></span>
        <button class="add-member-btn" @click="showAddMemberModal = true">
          <span class="btn-icon">➕</span>
          <span class="btn-text">添加成员</span>
        </button>
        <button class="back-btn" @click="goBack">
          <span class="btn-icon">←</span>
          <span class="btn-text">返回</span>
        </button>
      </h1>
      <p class="page-subtitle">管理 {{ spaceName }} 的成员和权限</p>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p class="loading-text">正在加载成员列表...</p>
    </div>

    <!-- 成员列表 -->
    <div v-else-if="members.length > 0" class="members-container">
      <div class="members-header">
        <h2 class="members-title">成员列表 ({{ members.length }})</h2>
        <div class="search-box">
          <input 
            v-model="searchKeyword" 
            type="text" 
            placeholder="搜索成员..." 
            class="search-input"
          />
          <span class="search-icon">🔍</span>
        </div>
      </div>

      <div class="members-grid">
        <div 
          v-for="member in filteredMembers" 
          :key="member.id" 
          class="member-card"
        >
          <div class="member-avatar">
            <img 
              :src="member.user?.userAvatar || '/default-avatar.png'" 
              :alt="member.user?.userName || '用户'"
              class="avatar-img"
            />
            <div class="role-badge" :class="getRoleBadgeClass(member.spaceRole)">
              {{ getRoleText(member.spaceRole) }}
            </div>
          </div>
          
          <div class="member-info">
            <h3 class="member-name">{{ member.user?.userName || '未知用户' }}</h3>
            <p class="member-account">{{ member.user?.userAccount || '未知账号' }}</p>
            <p class="member-join-time">加入时间：{{ formatDate(member.createTime) }}</p>
          </div>

          <div class="member-actions">
            <button 
              v-if="canEditMember(member)" 
              class="action-btn edit-btn" 
              @click="editMember(member)"
              title="编辑权限"
            >
              <span class="btn-icon">✏️</span>
            </button>
            <button 
              v-if="canRemoveMember(member)" 
              class="action-btn remove-btn" 
              @click="removeMember(member)"
              title="移除成员"
            >
              <span class="btn-icon">❌</span>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="empty-container">
      <div class="empty-icon">👥</div>
      <h3 class="empty-title">暂无成员</h3>
      <p class="empty-text">这个空间还没有其他成员，快去邀请他们加入吧！</p>
      <button class="add-member-btn" @click="showAddMemberModal = true">
        <span class="btn-icon">➕</span>
        <span class="btn-text">添加第一个成员</span>
      </button>
    </div>

    <!-- 添加成员模态框 -->
    <div v-if="showAddMemberModal" class="modal-overlay" @click="showAddMemberModal = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3 class="modal-title">添加成员</h3>
          <button class="modal-close" @click="showAddMemberModal = false">×</button>
        </div>
        
        <div class="modal-body">
          <div class="form-group">
            <label class="form-label">用户账号</label>
            <input 
              v-model="addMemberForm.userAccount" 
              type="text" 
              placeholder="请输入用户账号"
              class="form-input"
            />
          </div>
          
          <div class="form-group">
            <label class="form-label">成员角色</label>
            <select v-model="addMemberForm.spaceRole" class="form-select">
              <option value="viewer">访客 (只能查看)</option>
              <option value="editor">编辑者 (可以编辑)</option>
              <option value="admin">管理员 (完全权限)</option>
            </select>
          </div>
        </div>
        
        <div class="modal-footer">
          <button class="btn-cancel" @click="showAddMemberModal = false">取消</button>
          <button class="btn-confirm" @click="addMember" :disabled="addingMember">
            {{ addingMember ? '添加中...' : '确认添加' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 编辑成员模态框 -->
    <div v-if="showEditMemberModal" class="modal-overlay" @click="showEditMemberModal = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3 class="modal-title">编辑成员权限</h3>
          <button class="modal-close" @click="showEditMemberModal = false">×</button>
        </div>
        
        <div class="modal-body">
          <div class="member-info-display">
            <img 
              :src="editingMember?.user?.userAvatar || '/default-avatar.png'" 
              :alt="editingMember?.user?.userName || '用户'"
              class="member-avatar-small"
            />
            <div class="member-info-text">
              <h4>{{ editingMember?.user?.userName || '未知用户' }}</h4>
              <p>{{ editingMember?.user?.userAccount || '未知账号' }}</p>
            </div>
          </div>
          
          <div class="form-group">
            <label class="form-label">成员角色</label>
            <select v-model="editMemberForm.spaceRole" class="form-select">
              <option value="viewer">访客 (只能查看)</option>
              <option value="editor">编辑者 (可以编辑)</option>
              <option value="admin">管理员 (完全权限)</option>
            </select>
          </div>
        </div>
        
        <div class="modal-footer">
          <button class="btn-cancel" @click="showEditMemberModal = false">取消</button>
          <button class="btn-confirm" @click="updateMember" :disabled="updatingMember">
            {{ updatingMember ? '更新中...' : '确认更新' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { 
  listSpaceUserUsingPost, 
  addSpaceUserUsingPost, 
  editSpaceUserUsingPost, 
  deleteSpaceUserUsingPost 
} from '../../a/api/spaceUserController'

const route = useRoute()
const router = useRouter()

// 简单的消息提示函数
const message = {
  success: (msg: string) => alert(`✅ ${msg}`),
  error: (msg: string) => alert(`❌ ${msg}`)
}

// 响应式数据
const loading = ref(false)
const members = ref<any[]>([])
const searchKeyword = ref('')
const spaceName = ref('')
const currentUserId = ref('')

// 模态框状态
const showAddMemberModal = ref(false)
const showEditMemberModal = ref(false)
const addingMember = ref(false)
const updatingMember = ref(false)

// 表单数据
const addMemberForm = ref({
  userAccount: '',
  spaceRole: 'viewer'
})

const editMemberForm = ref({
  id: 0,
  spaceRole: 'viewer'
})

const editingMember = ref<any>(null)

// 计算属性
const filteredMembers = computed(() => {
  if (!searchKeyword.value) return members.value
  
  return members.value.filter(member => 
    member.user?.userName?.includes(searchKeyword.value) ||
    member.user?.userAccount?.includes(searchKeyword.value)
  )
})

// 获取成员列表
const fetchMembers = async () => {
  if (!route.query.spaceId) {
    message.error('空间ID不存在')
    return
  }

  loading.value = true
  try {
    const response = await listSpaceUserUsingPost({
      spaceId: Number(route.query.spaceId)
    })
    
    if (response.data?.code === 0 && response.data?.data) {
      members.value = response.data.data
    } else {
      message.error(response.data?.message || '获取成员列表失败')
    }
  } catch (error) {
    console.error('获取成员列表失败:', error)
    message.error('获取成员列表失败')
  } finally {
    loading.value = false
  }
}

// 添加成员
const addMember = async () => {
  if (!addMemberForm.value.userAccount.trim()) {
    message.error('请输入用户账号')
    return
  }

  if (!route.query.spaceId) {
    message.error('空间ID不存在')
    return
  }

  addingMember.value = true
  try {
    const response = await addSpaceUserUsingPost({
      spaceId: Number(route.query.spaceId),
      spaceRole: addMemberForm.value.spaceRole,
      userId: undefined // 后端会根据userAccount查找用户
    })
    
    if (response.data?.code === 0) {
      message.success('添加成员成功')
      showAddMemberModal.value = false
      addMemberForm.value = { userAccount: '', spaceRole: 'viewer' }
      await fetchMembers()
    } else {
      message.error(response.data?.message || '添加成员失败')
    }
  } catch (error) {
    console.error('添加成员失败:', error)
    message.error('添加成员失败')
  } finally {
    addingMember.value = false
  }
}

// 编辑成员
const editMember = (member: any) => {
  editingMember.value = member
  editMemberForm.value = {
    id: member.id,
    spaceRole: member.spaceRole
  }
  showEditMemberModal.value = true
}

// 更新成员
const updateMember = async () => {
  updatingMember.value = true
  try {
    const response = await editSpaceUserUsingPost({
      id: editMemberForm.value.id,
      spaceRole: editMemberForm.value.spaceRole
    })
    
    if (response.data?.code === 0) {
      message.success('更新成员权限成功')
      showEditMemberModal.value = false
      await fetchMembers()
    } else {
      message.error(response.data?.message || '更新成员权限失败')
    }
  } catch (error) {
    console.error('更新成员权限失败:', error)
    message.error('更新成员权限失败')
  } finally {
    updatingMember.value = false
  }
}

// 移除成员
const removeMember = async (member: any) => {
  if (!confirm(`确定要移除成员 "${member.user?.userName || member.user?.userAccount}" 吗？`)) {
    return
  }

  try {
    const response = await deleteSpaceUserUsingPost({
      id: member.id
    })
    
    if (response.data?.code === 0) {
      message.success('移除成员成功')
      await fetchMembers()
    } else {
      message.error(response.data?.message || '移除成员失败')
    }
  } catch (error) {
    console.error('移除成员失败:', error)
    message.error('移除成员失败')
  }
}

// 权限检查
const canEditMember = (member: any) => {
  // 不能编辑自己
  if (member.userId === currentUserId.value) return false
  // 只有管理员可以编辑其他成员
  return true // 这里可以根据实际需求调整
}

const canRemoveMember = (member: any) => {
  // 不能移除自己
  if (member.userId === currentUserId.value) return false
  // 只有管理员可以移除其他成员
  return true // 这里可以根据实际需求调整
}

// 工具函数
const getRoleText = (role: string) => {
  const roleMap: { [key: string]: string } = {
    'viewer': '访客',
    'editor': '编辑者',
    'admin': '管理员'
  }
  return roleMap[role] || '未知'
}

const getRoleBadgeClass = (role: string) => {
  const classMap: { [key: string]: string } = {
    'viewer': 'viewer',
    'editor': 'editor',
    'admin': 'admin'
  }
  return classMap[role] || ''
}

const formatDate = (dateString: string) => {
  if (!dateString) return '未知时间'
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN')
}

const goBack = () => {
  router.back()
}

// 组件挂载
onMounted(() => {
  fetchMembers()
})
</script>

<style scoped>
.space-members-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.page-header {
  text-align: center;
  margin-bottom: 3rem;
  color: #fff;
}

.page-title {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
  font-size: 2.5rem;
  font-weight: 800;
  margin: 0;
  flex-wrap: wrap;
}

.title-icon {
  font-size: 2.5rem;
  animation: bounce 2s infinite;
}

.title-decoration {
  width: 60px;
  height: 4px;
  background: linear-gradient(90deg, #ff6b6b, #4ecdc4);
  border-radius: 2px;
  margin: 0 1rem;
}

.page-subtitle {
  font-size: 1.1rem;
  opacity: 0.9;
  margin-top: 1rem;
}

.add-member-btn, .back-btn {
  padding: 0.8rem 1.5rem;
  border: none;
  border-radius: 12px;
  font-weight: 700;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  transition: all 0.3s ease;
  box-shadow: 0 8px 16px rgba(0,0,0,0.1);
}

.add-member-btn {
  background: linear-gradient(135deg, #10b981, #059669);
  color: #fff;
}

.back-btn {
  background: linear-gradient(135deg, #6b7280, #4b5563);
  color: #fff;
}

.add-member-btn:hover, .back-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 24px rgba(0,0,0,0.15);
}

.loading-container {
  text-align: center;
  padding: 4rem 0;
  color: #fff;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 4px solid rgba(255,255,255,0.3);
  border-top: 4px solid #fff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 1rem;
}

.loading-text {
  font-size: 1.1rem;
  opacity: 0.8;
}

.members-container {
  background: rgba(255,255,255,0.95);
  border-radius: 20px;
  padding: 2rem;
  box-shadow: 0 20px 40px rgba(0,0,0,0.1);
  backdrop-filter: blur(20px);
}

.members-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  flex-wrap: wrap;
  gap: 1rem;
}

.members-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1f2937;
  margin: 0;
}

.search-box {
  position: relative;
  display: flex;
  align-items: center;
}

.search-input {
  padding: 0.8rem 1rem 0.8rem 2.5rem;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  font-size: 1rem;
  width: 250px;
  transition: all 0.3s ease;
}

.search-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.search-icon {
  position: absolute;
  left: 0.8rem;
  color: #9ca3af;
  font-size: 1.1rem;
}

.members-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
}

.member-card {
  background: #fff;
  border-radius: 16px;
  padding: 1.5rem;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
  border: 1px solid #e5e7eb;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 1rem;
}

.member-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.1);
}

.member-avatar {
  position: relative;
  flex-shrink: 0;
}

.avatar-img {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #e5e7eb;
}

.role-badge {
  position: absolute;
  bottom: -5px;
  right: -5px;
  padding: 0.2rem 0.5rem;
  border-radius: 8px;
  font-size: 0.7rem;
  font-weight: 600;
  color: #fff;
}

.role-badge.viewer {
  background: linear-gradient(135deg, #10b981, #059669);
}

.role-badge.editor {
  background: linear-gradient(135deg, #667eea, #764ba2);
}

.role-badge.admin {
  background: linear-gradient(135deg, #f59e0b, #d97706);
}

.member-info {
  flex: 1;
  min-width: 0;
}

.member-name {
  font-size: 1.1rem;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 0.3rem 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.member-account {
  font-size: 0.9rem;
  color: #6b7280;
  margin: 0 0 0.5rem 0;
}

.member-join-time {
  font-size: 0.8rem;
  color: #9ca3af;
  margin: 0;
}

.member-actions {
  display: flex;
  gap: 0.5rem;
  flex-shrink: 0;
}

.action-btn {
  padding: 0.5rem;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 1rem;
}

.edit-btn {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
}

.remove-btn {
  background: linear-gradient(135deg, #ef4444, #dc2626);
  color: #fff;
}

.action-btn:hover {
  transform: scale(1.1);
}

.empty-container {
  text-align: center;
  padding: 4rem 0;
  color: #fff;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
  opacity: 0.7;
}

.empty-title {
  font-size: 1.5rem;
  font-weight: 600;
  margin: 0 0 1rem 0;
}

.empty-text {
  font-size: 1rem;
  opacity: 0.8;
  margin: 0 0 2rem 0;
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(5px);
}

.modal-content {
  background: #fff;
  border-radius: 16px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 40px rgba(0,0,0,0.2);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  border-bottom: 1px solid #e5e7eb;
}

.modal-title {
  font-size: 1.3rem;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.modal-close {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #6b7280;
  padding: 0.5rem;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.modal-close:hover {
  background: #f3f4f6;
  color: #1f2937;
}

.modal-body {
  padding: 1.5rem;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  padding: 1.5rem;
  border-top: 1px solid #e5e7eb;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-label {
  display: block;
  font-weight: 600;
  color: #374151;
  margin-bottom: 0.5rem;
}

.form-input, .form-select {
  width: 100%;
  padding: 0.8rem;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  font-size: 1rem;
  transition: all 0.3s ease;
}

.form-input:focus, .form-select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.member-info-display {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background: #f9fafb;
  border-radius: 8px;
  margin-bottom: 1.5rem;
}

.member-avatar-small {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.member-info-text h4 {
  margin: 0 0 0.2rem 0;
  color: #1f2937;
}

.member-info-text p {
  margin: 0;
  color: #6b7280;
  font-size: 0.9rem;
}

.btn-cancel, .btn-confirm {
  padding: 0.8rem 1.5rem;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-cancel {
  background: #f3f4f6;
  color: #374151;
}

.btn-confirm {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
}

.btn-cancel:hover {
  background: #e5e7eb;
}

.btn-confirm:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.btn-confirm:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

/* 动画 */
@keyframes bounce {
  0%, 20%, 50%, 80%, 100% { transform: translateY(0); }
  40% { transform: translateY(-10px); }
  60% { transform: translateY(-5px); }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .space-members-container {
    padding: 1rem;
  }
  
  .page-title {
    font-size: 2rem;
    flex-direction: column;
    gap: 0.5rem;
  }
  
  .members-header {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-input {
    width: 100%;
  }
  
  .members-grid {
    grid-template-columns: 1fr;
  }
  
  .member-card {
    flex-direction: column;
    text-align: center;
  }
  
  .member-actions {
    justify-content: center;
    margin-top: 1rem;
  }
}
</style>
