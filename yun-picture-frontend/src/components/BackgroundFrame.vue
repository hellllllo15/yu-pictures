<template>
  <div class="bg-frame">
    <iframe ref="iframeRef" class="bg-iframe" :class="{ interactive: props.interactive }" src="/background/BackGround.html" title="全局背景" referrerpolicy="no-referrer" @load="handleIframeLoad"></iframe>
    <button v-if="props.interactive" class="bg-back-btn" @click="handleBack" aria-label="返回">← 返回</button>
  </div>
</template>

<script setup lang="ts">
import { onMounted, watch, ref, onBeforeUnmount } from 'vue'
import { useRouter, useRoute } from 'vue-router'
const props = defineProps<{ interactive?: boolean }>()
const router = useRouter()
const route = useRoute()
const iframeRef = ref<HTMLIFrameElement | null>(null)
const iframeLoaded = ref(false)

const postControlsVisible = (visible: boolean) => {
  const iframe = iframeRef.value
  if (!iframe || !iframe.contentWindow || !iframeLoaded.value) return
  
  // 确保 iframe 已经完全加载
  try {
    iframe.contentWindow.postMessage({ type: 'SET_CONTROLS_VISIBLE', visible }, '*')
  } catch (error) {
    console.warn('发送消息到 iframe 失败:', error)
  }
}

// 监听 iframe 加载完成
const handleIframeLoad = () => {
  iframeLoaded.value = true
  // iframe 加载完成后，根据当前状态发送消息
  // 默认情况下，控制组件应该是隐藏的，除非在 /background 路由
  postControlsVisible(!!props.interactive)
}

onMounted(() => {
  // 不在这里立即发送消息，等待 iframe 加载完成
  // 这样可以避免页面刷新后控制组件错误显示
})

watch(() => props.interactive, (val) => {
  // 只有在 iframe 加载完成后才发送消息
  if (iframeLoaded.value) {
    postControlsVisible(!!val)
  }
})

watch(() => route.path, () => {
  // 路由变化时，只有在 iframe 加载完成后才发送消息
  if (iframeLoaded.value) {
    postControlsVisible(!!props.interactive)
  }
})

const handleBack = () => {
  if (history.length > 1) router.back()
  else router.push('/')
}

onBeforeUnmount(() => {
  // 组件卸载时，确保控制组件是隐藏的
  if (iframeLoaded.value) {
    postControlsVisible(false)
  }
})
</script>

<style scoped>
.bg-frame {
  position: fixed;
  inset: 0;
  z-index: 0;
}

.bg-iframe {
  width: 100%;
  height: 100%;
  border: none;
  display: block;
  pointer-events: none;
}

.bg-iframe.interactive {
  pointer-events: auto;
}

.bg-back-btn {
  position: fixed;
  top: 16px;
  left: 16px;
  z-index: 99999;
  padding: 8px 12px;
  border: none;
  border-radius: 8px;
  color: #fff;
  cursor: pointer;
  background: linear-gradient(135deg, #667eea, #2a5298);
  box-shadow: 0 6px 16px rgba(0,0,0,.25);
}
.bg-back-btn:hover { transform: translateY(-1px); box-shadow: 0 10px 24px rgba(0,0,0,.3); }
</style> 