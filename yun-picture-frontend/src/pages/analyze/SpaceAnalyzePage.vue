<template>
  <div class="space-analyze-page">
    <div class="page-container">
      <div class="page-header">
        <h1>空间资源分析</h1>
        <p>查看空间使用情况和资源分布</p>
      </div>
      
             <div class="controls-section">
         <div class="control-panel">
           <div class="control-item">
             <label>
               <input 
                 type="radio" 
                 name="queryType" 
                 value="all" 
                 v-model="queryType"
               />
               查询所有空间
             </label>
           </div>
           <div class="control-item">
             <label>
               <input 
                 type="radio" 
                 name="queryType" 
                 value="public" 
                 v-model="queryType"
               />
               查询公共图库
             </label>
           </div>
                       <div class="control-item">
              <label>
                <input 
                  type="radio" 
                  name="queryType" 
                  value="specific" 
                  v-model="queryType"
                />
                根据ID查询
              </label>
              <input 
                type="text" 
                v-model="spaceIdInput" 
                placeholder="输入空间ID"
                :disabled="queryType !== 'specific'"
                class="space-id-input"
              />
              <button 
                @click="handleQuery"
                :disabled="queryType !== 'specific' || !spaceIdInput"
                class="query-btn"
              >
                查询
              </button>
            </div>
         </div>
       </div>
      
             <div class="analyze-section">
         <SpaceUsageAnalyze 
           ref="usageAnalyzeComponent"
           :queryAll="queryAll"
           :queryPublic="queryPublic"
           :spaceId="spaceId"
         />
         
                   <SpaceCategoryAnalyze 
            ref="categoryAnalyzeComponent"
            :queryAll="queryAll"
            :queryPublic="queryPublic"
            :spaceId="spaceId"
          />
          
          <SpaceTagAnalyze 
            ref="tagAnalyzeComponent"
            :queryAll="queryAll"
            :queryPublic="queryPublic"
            :spaceId="spaceId"
          />
          
          <SpaceSizeAnalyze 
            ref="sizeAnalyzeComponent"
            :queryAll="queryAll"
            :queryPublic="queryPublic"
            :spaceId="spaceId"
          />
          
          <SpaceUserAnalyze 
            ref="userAnalyzeComponent"
            :queryAll="queryAll"
            :queryPublic="queryPublic"
            :spaceId="spaceId"
          />
          
          <SpaceRankAnalyze 
            ref="rankAnalyzeComponent"
            :queryAll="queryAll"
            :queryPublic="queryPublic"
            :spaceId="spaceId"
          />
       </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick, onMounted } from 'vue'
import SpaceUsageAnalyze from '../../components/analyze/SpaceUsageAnalyze.vue'
import SpaceCategoryAnalyze from '../../components/analyze/SpaceCategoryAnalyze.vue'
import SpaceTagAnalyze from '../../components/analyze/SpaceTagAnalyze.vue'
import SpaceSizeAnalyze from '../../components/analyze/SpaceSizeAnalyze.vue'
import SpaceUserAnalyze from '../../components/analyze/SpaceUserAnalyze.vue'
import SpaceRankAnalyze from '../../components/analyze/SpaceRankAnalyze.vue'

const queryType = ref<'all' | 'public' | 'specific'>('all')
const spaceId = ref<string>()
const spaceIdInput = ref<string>('')
const usageAnalyzeComponent = ref()
const categoryAnalyzeComponent = ref()
const tagAnalyzeComponent = ref()
const sizeAnalyzeComponent = ref()
const userAnalyzeComponent = ref()
const rankAnalyzeComponent = ref()

// 计算属性，根据选择的查询类型返回对应的参数
const queryAll = computed(() => queryType.value === 'all')
const queryPublic = computed(() => queryType.value === 'public')

// 监听查询类型变化，自动触发查询（除了specific类型）
watch(queryType, (newType) => {
  if (newType !== 'specific') {
    nextTick(() => {
      usageAnalyzeComponent.value?.fetchData()
      categoryAnalyzeComponent.value?.fetchData()
      tagAnalyzeComponent.value?.fetchData()
      sizeAnalyzeComponent.value?.fetchData()
      userAnalyzeComponent.value?.fetchData()
      rankAnalyzeComponent.value?.fetchData()
    })
  }
})

// 处理查询按钮点击
const handleQuery = () => {
  if (queryType.value === 'specific' && spaceIdInput.value) {
    spaceId.value = spaceIdInput.value
    nextTick(() => {
      usageAnalyzeComponent.value?.fetchData()
      categoryAnalyzeComponent.value?.fetchData()
      tagAnalyzeComponent.value?.fetchData()
      sizeAnalyzeComponent.value?.fetchData()
      userAnalyzeComponent.value?.fetchData()
      rankAnalyzeComponent.value?.fetchData()
    })
  }
}

// 页面加载时自动获取数据
onMounted(() => {
  // 延迟一点时间确保所有组件都已挂载
  setTimeout(() => {
    usageAnalyzeComponent.value?.fetchData()
    categoryAnalyzeComponent.value?.fetchData()
    tagAnalyzeComponent.value?.fetchData()
    sizeAnalyzeComponent.value?.fetchData()
    userAnalyzeComponent.value?.fetchData()
    rankAnalyzeComponent.value?.fetchData()
  }, 100)
})
</script>

<style scoped>
.space-analyze-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.page-container {
  max-width: 1200px;
  margin: 0 auto;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 40px;
  text-align: center;
}

.page-header h1 {
  margin: 0 0 10px 0;
  font-size: 32px;
  font-weight: 700;
}

.page-header p {
  margin: 0;
  font-size: 18px;
  opacity: 0.9;
}

.controls-section {
  padding: 30px;
  background: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
}

.control-panel {
  display: flex;
  gap: 30px;
  align-items: center;
  flex-wrap: wrap;
  justify-content: center;
}

.control-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.control-item label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-weight: 500;
  color: #495057;
}

.control-item input[type="radio"] {
  width: 18px;
  height: 18px;
  cursor: pointer;
  margin-right: 8px;
}

.space-id-input {
  margin-left: 10px;
  padding: 10px 15px;
  border: 2px solid #e9ecef;
  border-radius: 6px;
  width: 200px;
  font-size: 14px;
  transition: border-color 0.3s ease;
}

.space-id-input:focus {
  outline: none;
  border-color: #667eea;
}

.space-id-input:disabled {
  background: #f8f9fa;
  cursor: not-allowed;
  opacity: 0.6;
}

.query-btn {
  margin-left: 10px;
  padding: 10px 20px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.query-btn:hover:not(:disabled) {
  background: #5a6fd8;
}

.query-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.analyze-section {
  padding: 30px;
}
</style>
