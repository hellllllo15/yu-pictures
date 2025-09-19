<template>
  <div class="websocket-test-container">
    <div class="container">
      <h1>WebSocket 图片编辑测试页面</h1>
      
      <!-- 认证状态显示 -->
      <div class="section">
        <h3>🔐 当前登录状态</h3>
        <div v-if="isLoggedIn" class="status-info logged-in">
          <div class="user-info">
            <strong>✅ 已登录用户：</strong>{{ userInfo?.userName }} (ID: {{ userInfo?.id }})
          </div>
          <div class="user-role">
            <strong>用户角色：</strong>{{ userInfo?.userRole || '普通用户' }}
          </div>
        </div>
        <div v-else class="status-info not-logged-in">
          <div>❌ 未登录</div>
          <div class="login-tip">
            请先 <router-link to="/user/UserAuth">登录</router-link> 后再使用WebSocket测试功能
          </div>
        </div>
      </div>
      
      <!-- 连接配置 -->
      <div class="section">
        <h3>连接配置</h3>
        <div class="input-group">
          <label for="serverUrl">服务器地址:</label>
          <input 
            type="text" 
            id="serverUrl" 
            v-model="serverUrl" 
            placeholder="WebSocket服务器地址"
          >
        </div>
        <div class="input-group">
          <label for="pictureId">图片ID:</label>
          <input 
            type="text" 
            id="pictureId" 
            v-model="pictureId" 
            placeholder="请输入图片ID"
          >
        </div>
        <div class="connection-buttons">
          <button @click="diagnoseConnection" :disabled="!isLoggedIn">诊断连接问题</button>
          <button @click="connect" :disabled="!isLoggedIn || isConnected">连接</button>
          <button @click="disconnect" :disabled="!isConnected">断开连接</button>
        </div>
      </div>
      
      <!-- 连接状态 -->
      <div class="section">
        <h3>连接状态</h3>
        <div :class="['status', connectionStatus.class]">
          {{ connectionStatus.text }}
        </div>
      </div>
      
      <!-- 消息发送 -->
      <div class="section">
        <h3>发送消息</h3>
        <div class="input-group">
          <label for="messageType">消息类型:</label>
          <select id="messageType" v-model="messageType">
            <option value="ENTER_EDIT">进入编辑</option>
            <option value="EXIT_EDIT">退出编辑</option>
            <option value="EDIT_ACTION">编辑操作</option>
          </select>
        </div>
        <div class="input-group">
          <label for="editAction">编辑操作 (仅当消息类型为EDIT_ACTION时有效):</label>
          <select id="editAction" v-model="editAction">
            <option value="ZOOM_IN">放大操作</option>
            <option value="ZOOM_OUT">缩小操作</option>
            <option value="ROTATE_LEFT">左旋操作</option>
            <option value="ROTATE_RIGHT">右旋操作</option>
          </select>
        </div>
        <div class="message-buttons">
          <button @click="sendMessage" :disabled="!isConnected">发送消息</button>
          <button @click="sendEnterEdit" :disabled="!isConnected">进入编辑</button>
          <button @click="sendExitEdit" :disabled="!isConnected">退出编辑</button>
          <button @click="sendZoomIn" :disabled="!isConnected">放大</button>
          <button @click="sendZoomOut" :disabled="!isConnected">缩小</button>
          <button @click="sendRotateLeft" :disabled="!isConnected">左旋</button>
          <button @click="sendRotateRight" :disabled="!isConnected">右旋</button>
        </div>
      </div>
      
      <!-- 消息记录 -->
      <div class="section">
        <h3>消息记录</h3>
        <button class="clear-btn" @click="clearMessages">清空消息</button>
        <div class="messages" ref="messagesContainer">
          <div 
            v-for="(message, index) in messages" 
            :key="index" 
            :class="['message', message.type]"
          >
            <div class="timestamp">[{{ message.timestamp }}]</div>
            <div v-html="message.content"></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { getLoginUserUsingGet } from '@/a/api/userController'

// 路由
const router = useRouter()

// 响应式数据
const isLoggedIn = ref(false)
const userInfo = ref<any>(null)
const serverUrl = ref('ws://localhost:8123/api/ws/picture/edit')
const pictureId = ref('1')
const messageType = ref('ENTER_EDIT')
const editAction = ref('ZOOM_IN')
const websocket = ref<WebSocket | null>(null)
const isConnected = ref(false)
const messages = ref<Array<{type: string, content: string, timestamp: string}>>([])

// 计算属性
const connectionStatus = computed(() => {
  if (isConnected.value) {
    return { class: 'connected', text: '已连接' }
  } else if (websocket.value && websocket.value.readyState === WebSocket.CONNECTING) {
    return { class: 'connecting', text: '正在连接...' }
  } else {
    return { class: 'disconnected', text: '未连接' }
  }
})

// 获取当前时间戳
const getTimestamp = () => {
  return new Date().toLocaleTimeString()
}

// 添加消息到显示区域
const addMessage = (content: string, type: string = 'info') => {
  messages.value.push({
    type,
    content,
    timestamp: getTimestamp()
  })
  
  // 自动滚动到底部
  nextTick(() => {
    const container = document.querySelector('.messages') as HTMLElement
    if (container) {
      container.scrollTop = container.scrollHeight
    }
  })
}

// 检查登录状态
const checkLoginStatus = async () => {
  try {
    const response = await getLoginUserUsingGet()
    if (response.data?.code === 0 && response.data?.data) {
      userInfo.value = response.data.data
      isLoggedIn.value = true
      addMessage(`✅ 已登录用户: ${userInfo.value.userName} (ID: ${userInfo.value.id})`, 'info')
    } else {
      isLoggedIn.value = false
      addMessage('❌ 未登录或登录状态无效', 'error')
    }
  } catch (error) {
    isLoggedIn.value = false
    addMessage('❌ 获取登录状态失败，请重新登录', 'error')
  }
}

// 诊断连接问题
const diagnoseConnection = async () => {
  addMessage('🔍 开始诊断WebSocket连接问题...', 'info')
  
  // 1. 检查登录状态
  if (!isLoggedIn.value) {
    addMessage('❌ 问题1: 用户未登录', 'error')
    addMessage('💡 解决方案: 请先登录系统', 'info')
    return
  }
  addMessage('✅ 检查1: 用户已登录', 'info')
  
  // 2. 检查图片ID
  if (!pictureId.value) {
    addMessage('❌ 问题2: 图片ID为空', 'error')
    addMessage('💡 解决方案: 请输入有效的图片ID', 'info')
    return
  }
  addMessage('✅ 检查2: 图片ID已填写', 'info')
  
  // 3. 检查服务器连接
  try {
    addMessage('🔍 检查3: 测试服务器连接...', 'info')
    const response = await fetch(`http://localhost:8123/api/user/get/login`, {
      method: 'GET',
      credentials: 'include' // 包含cookies
    })
    
    if (response.ok) {
      addMessage('✅ 检查3: 服务器连接正常', 'info')
    } else {
      addMessage(`❌ 问题3: 服务器响应异常 (${response.status})`, 'error')
      addMessage('💡 解决方案: 检查后端服务是否正常运行', 'info')
      return
    }
  } catch (error: any) {
    addMessage(`❌ 问题3: 无法连接到服务器 (${error.message})`, 'error')
    addMessage('💡 解决方案: 确保后端服务在8123端口运行', 'info')
    return
  }
  
  // 4. 检查WebSocket URL
  const wsUrl = `${serverUrl.value}?pictureId=${pictureId.value}`
  addMessage(`✅ 检查4: WebSocket URL正确: ${wsUrl}`, 'info')
  
  // 5. 可能的问题提示
  addMessage('📋 常见问题排查:', 'info')
  addMessage('• 图片ID=1 必须在数据库中存在', 'info')
  addMessage('• 该图片必须属于团队空间(spaceType=1)', 'info')
  addMessage('• 您必须有该图片的编辑权限', 'info')
  addMessage('• 检查浏览器开发者工具Console标签页的错误信息', 'info')
  addMessage('• 检查后端控制台的日志输出', 'info')
  
  addMessage('💡 建议: 尝试使用数据库中确实存在的图片ID', 'info')
}

// 连接WebSocket
const connect = async () => {
  if (!isLoggedIn.value) {
    addMessage('❌ 请先登录后再连接WebSocket', 'error')
    return
  }
  
  if (!pictureId.value) {
    addMessage('❌ 请输入图片ID', 'error')
    return
  }
  
  // 构建WebSocket URL，添加pictureId参数
  const wsUrl = `${serverUrl.value}?pictureId=${pictureId.value}`
  
  try {
    addMessage(`正在连接到: ${wsUrl}`, 'info')
    addMessage('💡 提示: 如果连接失败，请点击"诊断连接问题"按钮', 'info')
    
    websocket.value = new WebSocket(wsUrl)
    
    websocket.value.onopen = (event) => {
      isConnected.value = true
      addMessage('✅ WebSocket连接成功！', 'info')
      console.log('WebSocket连接成功:', event)
      console.log('WebSocket URL:', wsUrl)
    }
    
    websocket.value.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data)
        addMessage(`收到消息: ${JSON.stringify(data, null, 2)}`, 'received')
      } catch (e) {
        addMessage(`收到消息: ${event.data}`, 'received')
      }
    }
    
    websocket.value.onclose = (event) => {
      isConnected.value = false
      addMessage(`❌ 连接关闭: ${event.code} - ${event.reason}`, 'error')
      console.log('WebSocket连接关闭:', event)
      
      // 根据关闭码提供具体建议
      if (event.code === 1006) {
        addMessage('💡 连接异常关闭，可能是认证失败或权限不足', 'error')
      } else if (event.code === 1002) {
        addMessage('💡 协议错误，请检查WebSocket配置', 'error')
      } else if (event.code === 1003) {
        addMessage('💡 数据类型错误，请检查消息格式', 'error')
      }
      
      websocket.value = null
    }
    
    websocket.value.onerror = (error) => {
      isConnected.value = false
      addMessage('❌ WebSocket连接错误', 'error')
      addMessage('💡 请检查:', 'error')
      addMessage('• 后端服务是否正常运行', 'error')
      addMessage('• 图片ID是否存在且您有权限', 'error')
      addMessage('• 浏览器开发者工具Console中的错误信息', 'error')
      console.error('WebSocket连接错误:', error)
      console.log('WebSocket URL:', wsUrl)
    }
    
  } catch (error: any) {
    addMessage(`❌ 连接失败: ${error.message}`, 'error')
  }
}

// 断开连接
const disconnect = () => {
  if (websocket.value) {
    websocket.value.close()
    websocket.value = null
  }
}

// 发送消息
const sendMessage = () => {
  if (!isConnected.value || !websocket.value) {
    addMessage('❌ 请先连接WebSocket', 'error')
    return
  }
  
  const message = {
    type: messageType.value
  }
  
  // 根据消息类型添加相应字段
  if (messageType.value === 'EDIT_ACTION') {
    message.editAction = editAction.value
  }
  
  try {
    const messageStr = JSON.stringify(message)
    console.log('发送WebSocket消息:', messageStr)
    websocket.value.send(messageStr)
    addMessage(`发送消息: ${JSON.stringify(message, null, 2)}`, 'sent')
  } catch (error: any) {
    addMessage(`发送失败: ${error.message}`, 'error')
  }
}

// 快捷发送方法
const sendEnterEdit = () => {
  if (!isConnected.value || !websocket.value) {
    addMessage('❌ 请先连接WebSocket', 'error')
    return
  }
  
  const message = {
    type: 'ENTER_EDIT'
  }
  
  try {
    const messageStr = JSON.stringify(message)
    console.log('发送进入编辑消息:', messageStr)
    websocket.value.send(messageStr)
    addMessage(`发送消息: ${JSON.stringify(message, null, 2)}`, 'sent')
  } catch (error: any) {
    addMessage(`发送失败: ${error.message}`, 'error')
  }
}

const sendExitEdit = () => {
  if (!isConnected.value || !websocket.value) {
    addMessage('❌ 请先连接WebSocket', 'error')
    return
  }
  
  const message = {
    type: 'EXIT_EDIT'
  }
  
  try {
    const messageStr = JSON.stringify(message)
    console.log('发送退出编辑消息:', messageStr)
    websocket.value.send(messageStr)
    addMessage(`发送消息: ${JSON.stringify(message, null, 2)}`, 'sent')
  } catch (error: any) {
    addMessage(`发送失败: ${error.message}`, 'error')
  }
}

const sendZoomIn = () => {
  if (!isConnected.value || !websocket.value) {
    addMessage('❌ 请先连接WebSocket', 'error')
    return
  }
  
  const message = {
    type: 'EDIT_ACTION',
    editAction: 'ZOOM_IN'
  }
  
  try {
    const messageStr = JSON.stringify(message)
    console.log('发送放大操作消息:', messageStr)
    websocket.value.send(messageStr)
    addMessage(`发送消息: ${JSON.stringify(message, null, 2)}`, 'sent')
  } catch (error: any) {
    addMessage(`发送失败: ${error.message}`, 'error')
  }
}

const sendZoomOut = () => {
  if (!isConnected.value || !websocket.value) {
    addMessage('❌ 请先连接WebSocket', 'error')
    return
  }
  
  const message = {
    type: 'EDIT_ACTION',
    editAction: 'ZOOM_OUT'
  }
  
  try {
    const messageStr = JSON.stringify(message)
    console.log('发送缩小操作消息:', messageStr)
    websocket.value.send(messageStr)
    addMessage(`发送消息: ${JSON.stringify(message, null, 2)}`, 'sent')
  } catch (error: any) {
    addMessage(`发送失败: ${error.message}`, 'error')
  }
}

const sendRotateLeft = () => {
  if (!isConnected.value || !websocket.value) {
    addMessage('❌ 请先连接WebSocket', 'error')
    return
  }
  
  const message = {
    type: 'EDIT_ACTION',
    editAction: 'ROTATE_LEFT'
  }
  
  try {
    const messageStr = JSON.stringify(message)
    console.log('发送左旋操作消息:', messageStr)
    websocket.value.send(messageStr)
    addMessage(`发送消息: ${JSON.stringify(message, null, 2)}`, 'sent')
  } catch (error: any) {
    addMessage(`发送失败: ${error.message}`, 'error')
  }
}

const sendRotateRight = () => {
  if (!isConnected.value || !websocket.value) {
    addMessage('❌ 请先连接WebSocket', 'error')
    return
  }
  
  const message = {
    type: 'EDIT_ACTION',
    editAction: 'ROTATE_RIGHT'
  }
  
  try {
    const messageStr = JSON.stringify(message)
    console.log('发送右旋操作消息:', messageStr)
    websocket.value.send(messageStr)
    addMessage(`发送消息: ${JSON.stringify(message, null, 2)}`, 'sent')
  } catch (error: any) {
    addMessage(`发送失败: ${error.message}`, 'error')
  }
}

// 清空消息
const clearMessages = () => {
  messages.value = []
}

// 组件挂载时检查登录状态
onMounted(async () => {
  addMessage('WebSocket测试页面已加载', 'info')
  addMessage('📋 使用步骤：', 'info')
  addMessage('1. 确认已登录（上方显示登录状态）', 'info')
  addMessage('2. 输入有效的图片ID', 'info')
  addMessage('3. 点击"连接"建立WebSocket连接', 'info')
  addMessage('4. 使用各种按钮测试消息发送', 'info')
  
  await checkLoginStatus()
})

// 组件卸载时断开连接
onUnmounted(() => {
  if (websocket.value) {
    websocket.value.close()
  }
})
</script>

<style scoped>
.websocket-test-container {
  font-family: 'Microsoft YaHei', Arial, sans-serif;
  margin: 0;
  padding: 20px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.container {
  max-width: 800px;
  margin: 0 auto;
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  padding: 20px;
}

h1 {
  color: #333;
  text-align: center;
  margin-bottom: 30px;
}

.section {
  margin-bottom: 25px;
  padding: 15px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  background-color: #fafafa;
}

.section h3 {
  margin-top: 0;
  color: #555;
}

.input-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
  color: #666;
}

input, select {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  box-sizing: border-box;
}

button {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  margin-right: 10px;
  margin-bottom: 10px;
}

button:hover:not(:disabled) {
  background-color: #0056b3;
}

button:disabled {
  background-color: #6c757d;
  cursor: not-allowed;
}

.status {
  padding: 10px;
  border-radius: 4px;
  margin-bottom: 15px;
  font-weight: bold;
}

.status.connected {
  background-color: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.status.disconnected {
  background-color: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}

.status.connecting {
  background-color: #fff3cd;
  color: #856404;
  border: 1px solid #ffeaa7;
}

.status-info {
  padding: 15px;
  border-radius: 5px;
  margin-bottom: 15px;
}

.status-info.logged-in {
  background-color: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.status-info.not-logged-in {
  background-color: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}

.user-info, .user-role {
  margin-bottom: 5px;
}

.login-tip {
  margin-top: 10px;
}

.login-tip a {
  color: #007bff;
  text-decoration: none;
}

.login-tip a:hover {
  text-decoration: underline;
}

.connection-buttons, .message-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.clear-btn {
  background-color: #dc3545;
}

.clear-btn:hover:not(:disabled) {
  background-color: #c82333;
}

.messages {
  height: 300px;
  overflow-y: auto;
  border: 1px solid #ddd;
  padding: 10px;
  background-color: white;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  font-size: 12px;
}

.message {
  margin-bottom: 8px;
  padding: 5px;
  border-radius: 3px;
}

.message.sent {
  background-color: #e3f2fd;
  border-left: 3px solid #2196f3;
}

.message.received {
  background-color: #f3e5f5;
  border-left: 3px solid #9c27b0;
}

.message.info {
  background-color: #e8f5e8;
  border-left: 3px solid #4caf50;
}

.message.error {
  background-color: #ffebee;
  border-left: 3px solid #f44336;
}

.timestamp {
  color: #666;
  font-size: 10px;
}
</style>

