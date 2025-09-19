<template>
  <div class="space-usage-analyze">
    <div class="analyze-card">
      <div class="card-header">
        <h3 class="card-title">空间资源使用分析</h3>
      </div>
      <div class="card-content">
        <v-chart :option="options" style="height: 320px" :loading="loading" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watchEffect } from 'vue'
import VChart from 'vue-echarts'
import { getSpaceUsageAnalyzeUsingPost } from '../../a/api/spaceAnalyzeController'

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
const data = ref<any>({})
const loading = ref(true)

/**
 * 格式化文件大小
 */
const formatSize = (size?: number): string => {
  if (!size) return '0 B'
  const units = ['B', 'KB', 'MB', 'GB', 'TB']
  let index = 0
  let fileSize = size
  while (fileSize >= 1024 && index < units.length - 1) {
    fileSize /= 1024
    index++
  }
  return `${fileSize.toFixed(2)} ${units[index]}`
}

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
    
    const res = await getSpaceUsageAnalyzeUsingPost(requestParams)
    if (res.data.code === 0 && res.data.data) {
      data.value = res.data.data
    } else {
      console.error('获取数据失败，' + res.data.message)
    }
  } catch (error) {
    console.error('获取数据失败')
    console.error('获取空间使用分析数据失败:', error)
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
  const sizeUsageRatio = data.value.sizeUsageRatio ?? 0
  const countUsageRatio = data.value.countUsageRatio ?? 0
  const usedSize = data.value.usedSize ?? 0
  const maxSize = data.value.maxSize
  const usedCount = data.value.usedCount ?? 0
  const maxCount = data.value.maxCount

  return {
    title: {
      text: '空间资源使用情况',
      left: 'center',
      textStyle: {
        fontSize: 16,
        fontWeight: 'bold'
      }
    },
    tooltip: {
      trigger: 'item',
      formatter: function(params: any) {
        if (params.seriesName === '存储空间') {
          return `${params.name}<br/>使用比例: ${params.value}%<br/>已使用: ${formatSize(usedSize)}<br/>总容量: ${maxSize ? formatSize(maxSize) : '无限制'}`
        } else if (params.seriesName === '图片数量') {
          return `${params.name}<br/>使用比例: ${params.value}%<br/>已使用: ${usedCount} 张<br/>最大数量: ${maxCount ?? '无限制'} 张`
        }
        return params.name + ': ' + params.value + '%'
      }
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      top: 'middle'
    },
    series: [
      {
        name: '存储空间',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['30%', '50%'],
        data: [
          {
            value: sizeUsageRatio,
            name: '已使用',
            itemStyle: {
              color: '#1890ff'
            }
          },
          {
            value: 100 - sizeUsageRatio,
            name: '剩余',
            itemStyle: {
              color: '#f0f0f0'
            }
          }
        ],
        label: {
          show: true,
          formatter: '{b}: {d}%'
        },
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      },
      {
        name: '图片数量',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['70%', '50%'],
        data: [
          {
            value: countUsageRatio,
            name: '已使用',
            itemStyle: {
              color: '#52c41a'
            }
          },
          {
            value: 100 - countUsageRatio,
            name: '剩余',
            itemStyle: {
              color: '#f0f0f0'
            }
          }
        ],
        label: {
          show: true,
          formatter: '{b}: {d}%'
        },
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
})
</script>

<style scoped>
.space-usage-analyze {
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
  name: 'SpaceUsageAnalyze'
}
</script>