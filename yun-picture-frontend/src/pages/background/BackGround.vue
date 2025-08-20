<template>
  <div class="background-wrapper">
    <button class="back-button" @click="handleBack" aria-label="返回">
      ← 返回
    </button>
    <iframe class="background-iframe" src="/background/BackGround.html" title="背景页面" referrerpolicy="no-referrer"></iframe>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
const router = useRouter()

const handleBack = () => {
  if (history.length > 1) router.back()
  else router.push('/')
}
</script>

<style scoped>
.background-wrapper {
  width: 100%;
  height: 100%;
  min-height: calc(100vh - 80px);
  margin: 0;
  padding: 0;
}

.background-iframe {
  width: 100%;
  height: 100%;
  border: none;
  display: block;
}

.back-button {
  position: fixed;
  top: 16px;
  left: 16px;
  z-index: 10000;
  padding: 8px 12px;
  border: none;
  border-radius: 8px;
  color: #fff;
  cursor: pointer;
  background: linear-gradient(135deg, #667eea, #2a5298);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.25);
}

.back-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 10px 24px rgba(0, 0, 0, 0.3);
}
</style> 