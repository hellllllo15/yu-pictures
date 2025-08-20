<template>
  <div class="bg-frame">
    <iframe ref="iframeRef" class="bg-iframe" :class="{ interactive: props.interactive }" src="/background/BackGround.html" title="全局背景" referrerpolicy="no-referrer"></iframe>
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

const postControlsVisible = (visible: boolean) => {
  const iframe = iframeRef.value
  if (!iframe || !iframe.contentWindow) return
  iframe.contentWindow.postMessage({ type: 'SET_CONTROLS_VISIBLE', visible }, '*')
}

onMounted(() => {
  // 初始发送一次
  postControlsVisible(!!props.interactive)
})

watch(() => props.interactive, (val) => {
  postControlsVisible(!!val)
})

watch(() => route.path, () => {
  postControlsVisible(!!props.interactive)
})

const handleBack = () => {
  if (history.length > 1) router.back()
  else router.push('/')
}

onBeforeUnmount(() => {
  postControlsVisible(true)
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