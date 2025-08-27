<template>
  <div class="space-user-analyze">
    <div class="analyze-card">
      <div class="card-header">
        <h3 class="card-title">用户上传行为分析</h3>
        <div class="card-controls">
          <div class="control-group">
            <label class="control-label">时间维度：</label>
            <div class="segmented-control">
              <button 
                v-for="option in timeDimensionOptions" 
                :key="option.value"
                :class="['segmented-btn', { active: timeDimension === option.value }]"
                @click="timeDimension = option.value"
              >
                {{ option.label }}
              </button>
            </div>
          </div>
          <div class="control-group">
            <input 
              type="text" 
              v-model="userIdInput" 
              placeholder="请输入用户ID"
              class="user-id-input"
            />
            <button 
              @click="doSearch"
              class="search-btn"
            >
              搜索用户
            </button>
          </div>
        </div>
      </div>
      <div class="card-content">
        <v-chart :option="options" style="height: 320px; max-width: 100%;" :loading="loading" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import VChart from 'vue-echarts'
import { getSpaceUserAnalyzeUsingPost } from '../../a/api/spaceAnalyzeController'

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
const dataList = ref<API.SpaceUserAnalyzeResponse[]>([])
const loading = ref(true)

// 控制变量
const userId = ref<string>()
const userIdInput = ref<string>('')
const timeDimension = ref<string>('day')
const timeDimensionOptions = [
  {
    label: '日',
    value: 'day',
  },
  {
    label: '周',
    value: 'week',
  },
  {
    label: '月',
    value: 'month',
  },
]

/**
 * 搜索用户
 */
const doSearch = () => {
  userId.value = userIdInput.value
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
    
    // 添加用户分析特有的参数
    requestParams.timeDimension = timeDimension.value
    if (userId.value) {
      requestParams.userId = userId.value
    }
    
    const res = await getSpaceUserAnalyzeUsingPost(requestParams)
    if (res.data.code === 0) {
      dataList.value = res.data.data ?? []
    } else {
      console.error('获取数据失败，' + res.data.message)
    }
  } catch (error) {
    console.error('获取数据失败')
    console.error('获取用户上传行为分析数据失败:', error)
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
 * 监听参数变化，自动重新加载数据
 */
watch([timeDimension, userId], () => {
  fetchData()
})

/**
 * 计算图表配置
 */
const options = computed(() => {
  const periods = dataList.value.map((item) => item.period || '未知时间') // 时间区间
  const counts = dataList.value.map((item) => item.count || 0) // 上传数量

  return {
    tooltip: { 
      trigger: 'axis',
      formatter: function(params: any) {
        const data = params[0]
        return `${data.name}<br/>上传数量: ${data.value} 张`
      }
    },
    xAxis: { 
      type: 'category', 
      data: periods, 
      name: '时间区间',
      axisLabel: {
        rotate: 45,
        interval: 0
      }
    },
    yAxis: { 
      type: 'value', 
      name: '上传数量' 
    },
    series: [
      {
        name: '上传数量',
        type: 'line',
        data: counts,
        smooth: true, // 平滑折线
        emphasis: {
          focus: 'series',
        },
        itemStyle: {
          color: '#667eea'
        },
        lineStyle: {
          color: '#667eea',
          width: 3
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(102, 126, 234, 0.3)' },
              { offset: 1, color: 'rgba(102, 126, 234, 0.1)' }
            ]
          }
        }
      },
    ],
  }
})
</script>

<style scoped>
.space-user-analyze {
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
  margin: 0 0 16px 0;
  color: white;
  font-size: 18px;
  font-weight: 600;
}

.card-controls {
  display: flex;
  gap: 20px;
  align-items: center;
  flex-wrap: wrap;
}

.control-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.control-label {
  color: white;
  font-size: 14px;
  font-weight: 500;
}

.segmented-control {
  display: flex;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 6px;
  padding: 2px;
}

.segmented-btn {
  padding: 6px 12px;
  border: none;
  background: transparent;
  color: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.segmented-btn.active {
  background: rgba(255, 255, 255, 0.3);
  font-weight: 600;
}

.segmented-btn:hover:not(.active) {
  background: rgba(255, 255, 255, 0.1);
}

.user-id-input {
  padding: 6px 12px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 4px;
  background: rgba(255, 255, 255, 0.1);
  color: white;
  font-size: 14px;
  width: 150px;
}

.user-id-input::placeholder {
  color: rgba(255, 255, 255, 0.7);
}

.user-id-input:focus {
  outline: none;
  border-color: rgba(255, 255, 255, 0.5);
  background: rgba(255, 255, 255, 0.2);
}

.search-btn {
  padding: 6px 12px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 4px;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.search-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.5);
}

.card-content {
  padding: 20px;
}

@media (max-width: 768px) {
  .card-controls {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .control-group {
    width: 100%;
    justify-content: space-between;
  }
}
</style>
