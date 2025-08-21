<template>
  <div class="crawl-page">
    <div class="main-content">
      <div class="form-header">
        <h2 class="title">
          <span class="title-icon">🕷️</span>
          <span>批量抓取图片</span>
        </h2>
        <p class="subtitle">输入关键词与偏移量，抓取 Bing 返回的原图链接（仅展示前 10 张）</p>
      </div>

      <form class="crawl-form" @submit.prevent="handleCrawl">
        <div class="form-row">
          <div class="form-group">
            <label class="form-label">关键词 *</label>
            <div class="input-wrapper">
              <input v-model.trim="keyword" type="text" placeholder="如：猫咪、风景" class="form-input" />
              <div class="input-border"></div>
            </div>
          </div>
          <div class="form-group">
            <label class="form-label">偏移量</label>
            <div class="input-wrapper">
              <input v-model.number="offset" type="number" min="0" step="1" placeholder="0, 1, 2..." class="form-input" />
              <div class="input-border"></div>
            </div>
            <div class="hint">first = 偏移量 × 20 + 1</div>
          </div>
          <div class="actions">
            <button class="btn btn-primary" type="submit" :disabled="isLoading || !keyword">
              <span v-if="!isLoading">开始抓取</span>
              <span v-else>抓取中...</span>
              <div class="btn-glow"></div>
            </button>
          </div>
        </div>
      </form>

      <div class="result-section" v-if="displayUrls.length > 0">
        <h3 class="section-title">抓取结果（前 10 张）</h3>
        <div class="grid">
          <div v-for="(url, idx) in displayUrls" :key="url + '-' + idx" class="card">
            <div class="thumb">
              <img :src="url" :alt="buildName(idx)" @error="onImgError(idx)" />
            </div>
            <div class="meta">
              <div class="name">{{ buildName(idx) }}</div>
              <div class="link" :title="url">{{ shortUrl(url) }}</div>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="empty">暂无数据，请输入关键词后点击抓取</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

const keyword = ref('')
const offset = ref<number>(0)
const isLoading = ref(false)
const urls = ref<string[]>([])

const displayUrls = computed(() => urls.value.slice(0, 10))

const handleCrawl = async () => {
  if (!keyword.value) return
  isLoading.value = true
  urls.value = []
  try {
    const q = new URLSearchParams({ keyword: keyword.value, offset: String(offset.value || 0) })
    const resp = await fetch(`/api/picture/crawl/urls?${q.toString()}`)
    if (!resp.ok) throw new Error('请求失败')
    const data = await resp.json()
    // 后端为 BaseResponse 格式或直接数组的两种兼容
    const arr = Array.isArray(data) ? data : data?.data
    urls.value = Array.isArray(arr) ? arr.filter((u) => typeof u === 'string') : []
  } catch (e) {
    console.error('抓取失败', e)
    urls.value = []
    alert('抓取失败，请稍后重试')
  } finally {
    isLoading.value = false
  }
}

const buildName = (index: number) => {
  const base = keyword.value || '图片'
  return `${base}${index + 1}`
}

const shortUrl = (u: string, max = 48) => {
  if (!u) return ''
  return u.length > max ? u.slice(0, max) + '...' : u
}

const onImgError = (idx: number) => {
  // 失败时简单标记
  const u = displayUrls.value[idx]
  console.warn('图片加载失败: ', u)
}
</script>

<style scoped>
.crawl-page {
  width: 100%;
  min-height: 100vh;
  background: transparent;
  padding: 2rem;
  position: relative;
  overflow: hidden;
  opacity: 0.9;
  color: var(--text-primary);
}

.main-content {
  position: relative;
  z-index: 10;
  max-width: 1100px;
  margin: 0 auto;
  background: rgba(17,24,39,0.7);
  color: var(--text-primary);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 2rem;
  box-shadow: 0 25px 60px rgba(0, 0, 0, 0.2);
  border: 1px solid var(--border-color);
}

.form-header { text-align: center; margin-bottom: 1rem; }
.title { display: flex; gap: .6rem; align-items: center; justify-content: center; margin: 0; font-size: 1.6rem; font-weight: 700; color: #e5e7eb; }
.title-icon { font-size: 1.8rem; }
.subtitle { color: var(--text-secondary); margin: .25rem 0 0; }

.crawl-form { margin-top: 1rem; }
.form-row { display: flex; gap: 1rem; flex-wrap: wrap; align-items: flex-end; }
.form-group { display: flex; flex-direction: column; gap: .5rem; min-width: 220px; }
.form-label { font-weight: 600; color: #cbd5e1; }
.input-wrapper { position: relative; }
.form-input { width: 100%; padding: .8rem 1rem; border: 2px solid rgba(102,126,234,0.2); border-radius: 10px; background: rgba(255,255,255,0.9); color: #1e3c72; }
.input-border { position: absolute; bottom: 0; left: 0; width: 0; height: 2px; background: linear-gradient(90deg, #667eea, #764ba2); transition: width .3s ease; }
.form-input:focus { outline: none; background: rgba(255,255,255,1); border-color: #667eea; }
.form-input:focus ~ .input-border { width: 100%; }
.hint { color: var(--text-secondary); font-size: .85rem; }

.actions { display: flex; align-items: center; gap: .5rem; }
.btn { padding: .9rem 1.6rem; border: none; border-radius: 10px; font-weight: 700; cursor: pointer; position: relative; overflow: hidden; }
.btn-primary { background: linear-gradient(135deg, #667eea, #764ba2); color: white; box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3); }
.btn-primary:disabled { opacity: .6; cursor: not-allowed; }
.btn-glow { position: absolute; top: 0; left: -100%; width: 100%; height: 100%; background: linear-gradient(90deg, transparent, rgba(255,255,255,.3), transparent); transition: left .6s ease; }
.btn:hover .btn-glow { left: 100%; }

.result-section { margin-top: 1.5rem; }
.section-title { margin: 0 0 .75rem; font-size: 1.1rem; color: #e5e7eb; }
.grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(180px, 1fr)); gap: 1rem; }
.card { background: rgba(17,24,39,0.6); border: 1px solid var(--border-color); border-radius: 12px; overflow: hidden; box-shadow: 0 10px 24px rgba(0,0,0,.15); }
.thumb { width: 100%; height: 140px; overflow: hidden; background: rgba(0,0,0,.2); display: flex; align-items: center; justify-content: center; }
.thumb img { width: 100%; height: 100%; object-fit: cover; display: block; }
.meta { padding: .6rem .75rem .8rem; }
.name { font-weight: 700; color: #e5e7eb; margin-bottom: .25rem; }
.link { color: #cbd5e1; font-size: .85rem; word-break: break-all; }

.empty { text-align: center; color: var(--text-secondary); padding: 2rem 0; }
</style>
