<template>
  <div class="space-category-analyze">
    <div class="analyze-card">
      <div class="card-header">
        <h3 class="card-title">图库分类占用分析</h3>
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
import { getSpaceCategoryAnalyzeUsingPost } from '../../a/api/spaceAnalyzeController'

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
const dataList = ref<API.SpaceCategoryAnalyzeResponse[]>([])
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
    
    const res = await getSpaceCategoryAnalyzeUsingPost(requestParams)
    if (res.data.code === 0) {
      dataList.value = res.data.data ?? []
    } else {
      console.error('获取数据失败，' + res.data.message)
    }
  } catch (error) {
    console.error('获取数据失败')
    console.error('获取空间分类分析数据失败:', error)
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
  const categories = dataList.value.map((item) => item.category || '未分类')
  const countData = dataList.value.map((item) => item.count || 0)
  const sizeData = dataList.value.map((item) => 
    item.totalSize ? (item.totalSize / (1024 * 1024)).toFixed(2) : '0'
  ) // 转为 MB

  return {
    tooltip: { 
      trigger: 'axis',
      formatter: function(params: any) {
        let result = params[0].name + '<br/>'
        params.forEach((param: any) => {
          if (param.seriesName === '图片数量') {
            result += param.marker + param.seriesName + ': ' + param.value + ' 张<br/>'
          } else if (param.seriesName === '图片总大小') {
            result += param.marker + param.seriesName + ': ' + param.value + ' MB<br/>'
          }
        })
        return result
      }
    },
    legend: { 
      data: ['图片数量', '图片总大小'], 
      top: 'bottom' 
    },
    xAxis: { 
      type: 'category', 
      data: categories,
      axisLabel: {
        rotate: 45,
        interval: 0
      }
    },
    yAxis: [
      {
        type: 'value',
        name: '图片数量',
        axisLine: { show: true, lineStyle: { color: '#5470C6' } }, // 左轴颜色
        splitLine: {
          lineStyle: {
            color: '#5470C6',
            type: 'dashed',
          },
        },
      },
      {
        type: 'value',
        name: '图片总大小 (MB)',
        position: 'right',
        axisLine: { show: true, lineStyle: { color: '#91CC75' } }, // 右轴颜色
        splitLine: {
          lineStyle: {
            color: '#91CC75',
            type: 'dashed',
          },
        },
      },
    ],
    series: [
      { 
        name: '图片数量', 
        type: 'bar', 
        data: countData, 
        yAxisIndex: 0,
        itemStyle: {
          color: '#5470C6'
        }
      },
      { 
        name: '图片总大小', 
        type: 'bar', 
        data: sizeData, 
        yAxisIndex: 1,
        itemStyle: {
          color: '#91CC75'
        }
      },
    ],
  }
})
</script>

<style scoped>
.space-category-analyze {
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
  name: 'SpaceCategoryAnalyze'
}
</script>