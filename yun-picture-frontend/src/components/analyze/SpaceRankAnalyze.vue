<template>
  <div class="space-rank-analyze">
    <div class="analyze-card">
      <div class="card-header">
        <h3 class="card-title">空间使用排行分析</h3>
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
import { getSpaceRankAnalyzeUsingPost } from '../../a/api/spaceAnalyzeController'

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
const dataList = ref<API.SpaceVO[]>([])
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
    
    // 排行分析不需要传 topN，后端会使用默认值（10条）
    
    const res = await getSpaceRankAnalyzeUsingPost(requestParams)
    if (res.data.code === 0) {
      dataList.value = res.data.data ?? []
    } else {
      console.error('获取数据失败，' + res.data.message)
    }
  } catch (error) {
    console.error('获取数据失败')
    console.error('获取空间排行分析数据失败:', error)
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
  const spaceNames = dataList.value.map((item) => item.spaceName || '未命名空间')
  const usageData = dataList.value.map((item) => 
    item.totalSize ? (item.totalSize / (1024 * 1024)).toFixed(2) : '0'
  ) // 转为 MB

  return {
    tooltip: { 
      trigger: 'axis',
      formatter: function(params: any) {
        const data = params[0]
        return `${data.name}<br/>存储使用量: ${data.value} MB`
      }
    },
    xAxis: {
      type: 'category',
      data: spaceNames,
      axisLabel: {
        rotate: 45,
        interval: 0
      }
    },
    yAxis: {
      type: 'value',
      name: '空间使用量 (MB)',
    },
    series: [
      {
        name: '空间使用量 (MB)',
        type: 'bar',
        data: usageData,
        itemStyle: {
          color: '#5470C6', // 自定义柱状图颜色
        },
        label: {
          show: true,
          position: 'top',
          formatter: '{c} MB'
        }
      },
    ],
  }
})
</script>

<style scoped>
.space-rank-analyze {
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
