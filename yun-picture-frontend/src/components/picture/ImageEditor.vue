<template>
  <div class="image-editor-modal" v-if="visible">
    <div class="modal-overlay" @click="closeModal"></div>
    <div class="modal-content">
      <div class="modal-header">
        <h3 class="modal-title">编辑图片</h3>
        <button class="close-btn" @click="closeModal">×</button>
      </div>
      
      <div class="modal-body">
        <!-- 图片编辑区域 -->
        <div class="editor-container">
          <div class="image-preview">
            <img 
              ref="imageRef"
              :src="imageUrl" 
              :style="imageStyle"
              @load="onImageLoad"
              @error="onImageError"
              alt="编辑图片"
            />
          </div>
        </div>
        
        <!-- 操作按钮 -->
        <div class="editor-actions">
          <div class="action-group">
            <button class="action-btn" @click="rotateLeft" :disabled="loading">
              <span class="btn-icon">↺</span>
              向左旋转
            </button>
            <button class="action-btn" @click="rotateRight" :disabled="loading">
              <span class="btn-icon">↻</span>
              向右旋转
            </button>
          </div>
          
          <div class="action-group">
            <button class="action-btn" @click="zoomIn" :disabled="loading">
              <span class="btn-icon">🔍+</span>
              放大
            </button>
            <button class="action-btn" @click="zoomOut" :disabled="loading">
              <span class="btn-icon">🔍-</span>
              缩小
            </button>
          </div>
          
          <div class="action-group">
            <button class="action-btn reset-btn" @click="resetImage" :disabled="loading">
              <span class="btn-icon">🔄</span>
              重置
            </button>
            <button class="action-btn confirm-btn" @click="handleConfirm" :disabled="loading">
              <span class="btn-icon">✓</span>
              {{ loading ? '保存中...' : '确认保存' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { addPictureUsingPost } from '../../a/api/pictureController'
import { testDownloadFileUsingGet } from '../../a/api/fileController'

interface Props {
  visible?: boolean
  imageUrl?: string
  picture?: any
  spaceId?: number
  onSuccess?: (newPicture: any) => void
  onClose?: () => void
}

const props = withDefaults(defineProps<Props>(), {
  visible: false,
  imageUrl: '',
  spaceId: undefined,
  onSuccess: undefined,
  onClose: undefined
})

// 图片引用
const imageRef = ref<HTMLImageElement>()

// 编辑状态
const rotation = ref(0)
const scale = ref(1)
const loading = ref(false)

// 计算图片样式
const imageStyle = computed(() => {
  return {
    transform: `rotate(${rotation.value}deg) scale(${scale.value})`,
    transition: 'transform 0.3s ease',
    maxWidth: '100%',
    maxHeight: '400px',
    objectFit: 'contain'
  }
})

// 图片加载成功
const onImageLoad = () => {
  console.log('图片加载成功')
}

// 图片加载失败
const onImageError = () => {
  console.error('图片加载失败')
  alert('图片加载失败，请检查图片URL')
}

// 向左旋转
const rotateLeft = () => {
  rotation.value -= 90
}

// 向右旋转
const rotateRight = () => {
  rotation.value += 90
}

// 放大
const zoomIn = () => {
  scale.value = Math.min(scale.value * 1.2, 3)
}

// 缩小
const zoomOut = () => {
  scale.value = Math.max(scale.value / 1.2, 0.5)
}

// 重置图片
const resetImage = () => {
  rotation.value = 0
  scale.value = 1
}

// 确认保存
const handleConfirm = async () => {
  if (!imageRef.value || !props.imageUrl) {
    alert('图片未加载')
    return
  }
  
  loading.value = true
  
  try {
    console.log('开始处理图片，URL:', props.imageUrl)
    
    // 检查是否有变换
    const hasTransform = rotation.value !== 0 || scale.value !== 1
    
    if (!hasTransform) {
      console.log('没有变换，直接上传原图')
      // 如果没有变换，直接上传原图
      await uploadOriginalImage()
    } else {
      console.log('有变换，尝试处理图片')
      // 如果有变换，尝试处理图片
      await processImageWithTransform()
    }
    
  } catch (error) {
    console.error('图片处理失败', error)
    alert('图片处理失败，请重试: ' + error.message)
    loading.value = false
  }
}

// 上传原图（无变换）
const uploadOriginalImage = async () => {
  try {
    console.log('尝试获取原图数据...')
    console.log('图片URL:', props.imageUrl)
    
    // 检查URL是否有双斜杠问题
    let imageUrl = props.imageUrl!
    if (imageUrl.includes('//space/')) {
      imageUrl = imageUrl.replace('//space/', '/space/')
      console.log('修复URL双斜杠问题:', imageUrl)
    }
    
    // 尝试通过fetch获取原图
    const response = await fetch(imageUrl, {
      mode: 'cors',
      credentials: 'omit',
      headers: {
        'Accept': 'image/*,*/*;q=0.8',
        'Cache-Control': 'no-cache'
      }
    })
    
    if (!response.ok) {
      throw new Error(`HTTP ${response.status}: ${response.statusText}`)
    }
    
    const blob = await response.blob()
    console.log('成功获取原图数据，大小:', blob.size)
    
    // 创建文件
    const fileName = (props.picture?.name || 'image') + '.png'
    const file = new File([blob], fileName, { type: blob.type })
    
    console.log('创建文件成功:', fileName, '大小:', file.size)
    
    // 上传图片
    await handleUpload(file)
    
  } catch (error) {
    console.warn('无法获取原图，尝试其他方法:', error)
    
    // 如果fetch失败，尝试通过后端代理
    try {
      await uploadViaBackendProxy()
    } catch (proxyError) {
      console.error('后端代理也失败:', proxyError)
      alert(`图片处理失败，可能的原因：
1. 图片服务器设置了防盗链
2. 图片URL有跨域限制
3. 网络连接问题

建议：
- 检查图片URL是否正确
- 联系管理员检查图片服务器的CORS设置
- 或者手动下载图片后重新上传`)
      loading.value = false
    }
  }
}

// 通过后端代理获取图片
const uploadViaBackendProxy = async () => {
  console.log('尝试通过后端代理获取图片...')
  
  try {
    // 从图片URL中提取文件路径
    const imageUrl = props.imageUrl!
    const urlParts = imageUrl.split('/space/')
    if (urlParts.length < 2) {
      throw new Error('无法解析图片路径')
    }
    
    // 构建文件路径
    const filepath = '/space/' + urlParts[1]
    console.log('提取的文件路径:', filepath)
    
    // 调用后端下载接口
    const response = await testDownloadFileUsingGet({ filepath })
    
    if (response.data?.code === 0 && response.data?.data) {
      console.log('后端代理成功获取图片数据')
      
      // 将base64数据转换为blob
      const base64Data = response.data.data
      const blob = await base64ToBlob(base64Data)
      
      // 创建文件
      const fileName = (props.picture?.name || 'image') + '.png'
      const file = new File([blob], fileName, { type: 'image/png' })
      
      console.log('创建文件成功:', fileName, '大小:', file.size)
      
      // 上传图片
      await handleUpload(file)
    } else {
      throw new Error('后端代理获取图片失败: ' + (response.data?.message || '未知错误'))
    }
    
  } catch (error) {
    console.error('后端代理获取图片失败:', error)
    throw error
  }
}

// 将base64转换为blob
const base64ToBlob = async (base64Data: string): Promise<Blob> => {
  // 如果base64数据包含data:image前缀，需要去掉
  const base64 = base64Data.includes(',') ? base64Data.split(',')[1] : base64Data
  
  // 将base64转换为二进制数据
  const binaryString = atob(base64)
  const bytes = new Uint8Array(binaryString.length)
  
  for (let i = 0; i < binaryString.length; i++) {
    bytes[i] = binaryString.charCodeAt(i)
  }
  
  return new Blob([bytes], { type: 'image/png' })
}

// 处理图片变换
const processImageWithTransform = async () => {
  try {
    // 创建一个隐藏的canvas来获取图片数据
    const tempCanvas = document.createElement('canvas')
    const tempCtx = tempCanvas.getContext('2d')
    
    if (!tempCtx) {
      throw new Error('无法创建临时画布')
    }
    
    // 设置临时canvas尺寸
    tempCanvas.width = imageRef.value!.naturalWidth
    tempCanvas.height = imageRef.value!.naturalHeight
    
    // 尝试绘制图片
    try {
      tempCtx.drawImage(imageRef.value!, 0, 0)
      console.log('成功绘制到临时canvas')
    } catch (drawError) {
      console.warn('无法绘制到canvas，可能是跨域问题:', drawError)
      throw new Error('由于跨域限制，无法处理图片变换。请尝试其他方法。')
    }
    
    // 创建新的canvas来处理变换
    const canvas = document.createElement('canvas')
    const ctx = canvas.getContext('2d')
    
    if (!ctx) {
      throw new Error('无法创建画布')
    }
    
    // 获取原始图片尺寸
    const imgWidth = imageRef.value!.naturalWidth
    const imgHeight = imageRef.value!.naturalHeight
    
    // 计算旋转后的尺寸
    const radians = (rotation.value * Math.PI) / 180
    const cos = Math.abs(Math.cos(radians))
    const sin = Math.abs(Math.sin(radians))
    
    const newWidth = imgWidth * cos + imgHeight * sin
    const newHeight = imgWidth * sin + imgHeight * cos
    
    // 设置canvas尺寸
    canvas.width = newWidth * scale.value
    canvas.height = newHeight * scale.value
    
    console.log('Canvas尺寸:', canvas.width, 'x', canvas.height)
    
    // 应用变换
    ctx.save()
    ctx.translate(canvas.width / 2, canvas.height / 2)
    ctx.rotate(radians)
    ctx.scale(scale.value, scale.value)
    ctx.drawImage(imageRef.value!, -imgWidth / 2, -imgHeight / 2, imgWidth, imgHeight)
    ctx.restore()
    
    console.log('图片绘制完成，开始转换为blob')
    
    // 转换为blob
    const blob = await new Promise<Blob>((resolve, reject) => {
      canvas.toBlob((result) => {
        if (result) {
          console.log('成功转换为blob，大小:', result.size)
          resolve(result)
        } else {
          reject(new Error('Canvas转换失败'))
        }
      }, 'image/png', 0.9)
    })
    
    // 创建文件
    const fileName = (props.picture?.name || 'image') + '.png'
    const file = new File([blob], fileName, { type: blob.type })
    
    console.log('创建文件成功:', fileName, '大小:', file.size)
    
    // 上传图片
    await handleUpload(file)
    
  } catch (error) {
    console.error('图片变换处理失败:', error)
    alert('图片变换处理失败: ' + error.message)
    loading.value = false
  }
}


// 上传图片
const handleUpload = async (file: File) => {
  try {
    // 参考AddPicture组件的参数格式
    const params = {
      name: props.picture?.name || '编辑后的图片',
      introduction: props.picture?.introduction || '',
      category: props.picture?.category || undefined,
      id: props.picture?.id || undefined  // 关键：传递图片ID用于更新
    } as any
    
    const body = {
      tags: props.picture?.tags?.length > 0 ? props.picture.tags : undefined,
      isPublic: !props.spaceId, // 有spaceId表示私密空间
      spaceId: props.spaceId ? String(props.spaceId) : undefined
    }
    
    console.log('图片编辑上传参数:', { params, body })
    
    const res = await addPictureUsingPost(params, body, file)
    if (res.data?.code === 0) {
      alert('图片保存成功')
      // 将上传成功的图片信息传递给父组件
      if (props.onSuccess) {
        props.onSuccess(res.data.data || props.picture)
      }
      closeModal()
    } else {
      alert('图片保存失败：' + (res.data?.message || '未知错误'))
    }
  } catch (error) {
    console.error('图片保存失败', error)
    alert('图片保存失败，请重试')
  }
  
  loading.value = false
}

// 关闭弹窗
const closeModal = () => {
  if (props.onClose) {
    props.onClose()
  }
}

// 监听visible变化，重置状态
watch(() => props.visible, (newVisible) => {
  if (newVisible) {
    resetImage()
  }
})
</script>

<style scoped>
.image-editor-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.7);
}

.modal-content {
  position: relative;
  background: white;
  border-radius: 12px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
  max-width: 90vw;
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e8e8e8;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.modal-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  color: white;
  cursor: pointer;
  padding: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: background-color 0.2s;
}

.close-btn:hover {
  background-color: rgba(255, 255, 255, 0.2);
}

.modal-body {
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.editor-container {
  width: 100%;
  height: 400px;
  border: 2px solid #e8e8e8;
  border-radius: 8px;
  overflow: hidden;
  position: relative;
  background: #f8f9fa;
  display: flex;
  align-items: center;
  justify-content: center;
}

.image-preview {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.editor-actions {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.action-group {
  display: flex;
  gap: 12px;
  justify-content: center;
  flex-wrap: wrap;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  border: 2px solid #e8e8e8;
  border-radius: 8px;
  background: white;
  color: #333;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  min-width: 120px;
  justify-content: center;
}

.action-btn:hover:not(:disabled) {
  border-color: #667eea;
  background: #f8f9ff;
  transform: translateY(-1px);
}

.action-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-icon {
  font-size: 16px;
}

.reset-btn {
  border-color: #ffa726;
  color: #ff8f00;
}

.reset-btn:hover:not(:disabled) {
  border-color: #ff8f00;
  background: #fff8e1;
}

.confirm-btn {
  border-color: #4caf50;
  background: #4caf50;
  color: white;
}

.confirm-btn:hover:not(:disabled) {
  background: #45a049;
  border-color: #45a049;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .modal-content {
    max-width: 95vw;
    max-height: 95vh;
  }
  
  .modal-body {
    padding: 16px;
  }
  
  .editor-container {
    height: 300px;
  }
  
  .action-group {
    flex-direction: column;
  }
  
  .action-btn {
    min-width: 100%;
  }
}
</style>