<template>
  <div class="space-size-analyze">
    <div class="analyze-card">
      <div class="card-header">
        <h3 class="card-title">空间图片大小分析</h3>
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
import { getSpaceSizeAnalyzeUsingPost } from '../../a/api/spaceAnalyzeController'

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
const dataList = ref<API.SpaceSizeAnalyzeResponse[]>([])
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
    
    const res = await getSpaceSizeAnalyzeUsingPost(requestParams)
    if (res.data.code === 0) {
      dataList.value = res.data.data ?? []
    } else {
      console.error('获取数据失败，' + res.data.message)
    }
  } catch (error) {
    console.error('获取数据失败')
    console.error('获取空间大小分析数据失败:', error)
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
  const pieData = dataList.value.map((item) => ({
    name: item.sizeRange || '未知大小',
    value: item.count || 0,
  }))

  return {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} 张 ({d}%)',
    },
    legend: {
      top: 'bottom',
      type: 'scroll',
    },
    series: [
      {
        name: '图片大小',
        type: 'pie',
        radius: '50%',
        center: ['50%', '45%'],
        data: pieData,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        },
        label: {
          show: true,
          formatter: '{b}: {c} 张'
        }
      },
    ],
  }
})
</script>

<style scoped>
.space-size-analyze {
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
