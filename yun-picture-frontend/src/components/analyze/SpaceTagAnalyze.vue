<template>
  <div class="space-tag-analyze">
    <div class="analyze-card">
      <div class="card-header">
        <h3 class="card-title">图库标签词云</h3>
      </div>
      <div class="card-content">
        <v-chart :option="options" style="height: 320px; max-width: 100%;" :loading="loading" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import VChart from 'vue-echarts'
import { getSpaceTagAnalyzeUsingPost } from '../../a/api/spaceAnalyzeController'

// 定义组件属性
interface Props {
  queryAll?: boolean
  queryPublic?: boolean
  spaceId?: string | number
}

const props = withDefaults(defineProps<Props>(), {
  queryAll: false,
  queryPublic: false,
})

// 图表数据
const dataList = ref<API.SpaceTagAnalyzeResponse[]>([])
const loading = ref(true)

/**
 * 加载数据
 */
const fetchData = async () => {
  loading.value = true
  try {
    // 构建请求参数，确保一次只传一种查询方式
    const requestParams: any = {}
    
    if (props.queryAll) {
      requestParams.queryAll = true
    } else if (props.queryPublic) {
      requestParams.queryPublic = true
    } else if (props.spaceId) {
      // 确保 spaceId 作为字符串传递，避免精度丢失
      requestParams.spaceId = String(props.spaceId)
    }
    
    const res = await getSpaceTagAnalyzeUsingPost(requestParams)
    if (res.data.code === 0) {
      dataList.value = res.data.data ?? []
    } else {
      console.error('获取数据失败，' + res.data.message)
    }
  } catch (error) {
    console.error('获取数据失败')
    console.error('获取空间标签分析数据失败:', error)
  }
  loading.value = false
}

/**
 * 暴露查询方法给父组件
 */
defineExpose({
  fetchData
})

/**
 * 计算图表配置
 */
const options = computed(() => {
  // 处理数据，确保count是数值类型
  const tagData = dataList.value.map((item) => ({
    name: item.tag || '未命名标签',
    value: typeof item.count === 'string' ? parseInt(item.count) || 0 : (item.count || 0), // 安全转换数值
  }))

  // 调试信息
  console.log('词云数据:', tagData)
  console.log('数据列表:', dataList.value)

  // 如果没有数据，使用测试数据
  const finalData = tagData.length > 0 ? tagData : [
    { name: '测试标签1', value: 10 },
    { name: '测试标签2', value: 8 },
    { name: '测试标签3', value: 6 },
    { name: '测试标签4', value: 4 },
    { name: '测试标签5', value: 2 }
  ]

  return {
    tooltip: {
      trigger: 'item',
      formatter: (params: any) => `${params.name}: ${params.value} 张图片`,
    },
    series: [
      {
        type: 'wordCloud',
        gridSize: 8,
        sizeRange: [14, 60], // 调整字体大小范围，让差异更明显
        rotationRange: [-45, 45], // 减少旋转角度，提高可读性
        shape: 'circle',
        width: '100%',
        height: '100%',
        textStyle: {
          fontFamily: 'sans-serif',
          fontWeight: 'bold',
          color: () => {
            // 使用更丰富的颜色方案
            const colors = [
              '#5470C6', '#91CC75', '#FAC858', '#EE6666', 
              '#73C0DE', '#3BA272', '#FC8452', '#9A60B4',
              '#EA7CCC', '#FF9F7F', '#FFDB5C', '#37A2FF'
            ]
            return colors[Math.floor(Math.random() * colors.length)]
          },
        },
        emphasis: {
          textStyle: {
            shadowBlur: 10,
            shadowColor: '#333'
          }
        },
        data: finalData,
      },
    ],
  }
})
</script>

<style scoped>
.space-tag-analyze {
  margin-bottom: 16px;
}

.analyze-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.card-header {
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.card-title {
  margin: 0;
  color: white;
  font-size: 18px;
  font-weight: 600;
}

.card-content {
  padding: 20px;
}
</style>

<script lang="ts">
export default {
  name: 'SpaceTagAnalyze'
}
</script>
