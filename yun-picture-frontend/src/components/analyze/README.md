# 空间分析组件

## 组件说明

本项目包含六个主要的空间分析组件：

1. **`SpaceUsageAnalyze.vue`** - 空间资源使用分析组件
   - 使用 vue-echarts 实现的空间资源使用分析组件
   - 用于展示存储空间和图片数量的使用情况

2. **`SpaceCategoryAnalyze.vue`** - 图片分类分析组件
   - 使用 vue-echarts 实现的图片分类分析组件
   - 用于展示不同分类下图片的数量和总大小占比

3. **`SpaceTagAnalyze.vue`** - 图片标签分析组件
   - 使用 vue-echarts 实现的图片标签分析组件
   - 使用词云图展示不同标签的关联图片数量

4. **`SpaceSizeAnalyze.vue`** - 图片大小分析组件
   - 使用 vue-echarts 实现的图片大小分析组件
   - 使用饼图展示不同大小范围的图片数量分布

5. **`SpaceUserAnalyze.vue`** - 用户上传行为分析组件
   - 使用 vue-echarts 实现的用户上传行为分析组件
   - 使用折线图展示时间序列趋势

6. **`SpaceRankAnalyze.vue`** - 空间使用排行分析组件
   - 使用 vue-echarts 实现的空间使用排行分析组件
   - 使用柱状图展示排名和存储使用量

## 功能特性

### SpaceUsageAnalyze.vue
- 使用 ECharts 饼图展示存储空间和图片数量的使用比例
- 支持查询指定空间、所有空间或公共图库
- 实时数据更新，支持动态参数变化
- 美观的卡片式设计
- 详细的工具提示信息

### SpaceCategoryAnalyze.vue
- 使用 ECharts 分组条形图展示不同分类的图片数量和大小
- 双Y轴设计，左侧显示图片数量，右侧显示图片总大小(MB)
- 支持查询指定空间、所有空间或公共图库
- 实时数据更新，支持动态参数变化
- 美观的卡片式设计，与使用分析组件风格一致

### SpaceTagAnalyze.vue
- 使用 ECharts 词云图展示不同标签的关联图片数量
- 字体大小根据图片数量动态调整，突出常用标签
- 丰富的颜色方案，增强视觉效果
- 支持查询指定空间、所有空间或公共图库
- 实时数据更新，支持动态参数变化
- 美观的卡片式设计，与其他分析组件风格一致

### SpaceSizeAnalyze.vue
- 使用 ECharts 饼图展示不同大小范围的图片数量分布
- 按图片大小分段统计（如 <100 KB、100 KB-1 MB、>1 MB）
- 支持查询指定空间、所有空间或公共图库
- 实时数据更新，支持动态参数变化
- 美观的卡片式设计，与其他分析组件风格一致

### SpaceUserAnalyze.vue
- 使用 ECharts 折线图展示用户上传行为的时间序列趋势
- 支持按日、周、月三种时间维度进行统计
- 支持按用户ID筛选，查看特定用户的上传行为
- 实时数据更新，支持动态参数变化
- 美观的卡片式设计，与其他分析组件风格一致

### SpaceRankAnalyze.vue
- 使用 ECharts 柱状图展示空间使用排行
- 按存储使用量排序，统计占用存储最多的前N个空间
- 支持查询指定空间、所有空间或公共图库
- 实时数据更新，支持动态参数变化
- 美观的卡片式设计，与其他分析组件风格一致

## 使用方法

### 1. 基本使用

```vue
<template>
  <SpaceUsageAnalyze />
</template>

<script setup>
import SpaceUsageAnalyze from './SpaceUsageAnalyze.vue'
</script>
```

### 2. 带参数使用

```vue
<template>
  <!-- 空间使用分析 -->
  <SpaceUsageAnalyze 
    :queryAll="true"
    :queryPublic="false"
    :spaceId="1"
  />
  
  <!-- 图片分类分析 -->
  <SpaceCategoryAnalyze 
    :queryAll="true"
    :queryPublic="false"
    :spaceId="1"
  />
  
  <!-- 图片标签分析 -->
  <SpaceTagAnalyze 
    :queryAll="true"
    :queryPublic="false"
    :spaceId="1"
  />
  
  <!-- 图片大小分析 -->
  <SpaceSizeAnalyze 
    :queryAll="true"
    :queryPublic="false"
    :spaceId="1"
  />
  
  <!-- 用户上传行为分析 -->
  <SpaceUserAnalyze 
    :queryAll="true"
    :queryPublic="false"
    :spaceId="1"
  />
  
  <!-- 空间使用排行分析 -->
  <SpaceRankAnalyze 
    :queryAll="true"
    :queryPublic="false"
    :spaceId="1"
  />
</template>

<script setup>
import SpaceUsageAnalyze from './SpaceUsageAnalyze.vue'
import SpaceCategoryAnalyze from './SpaceCategoryAnalyze.vue'
import SpaceTagAnalyze from './SpaceTagAnalyze.vue'
import SpaceSizeAnalyze from './SpaceSizeAnalyze.vue'
import SpaceUserAnalyze from './SpaceUserAnalyze.vue'
import SpaceRankAnalyze from './SpaceRankAnalyze.vue'
</script>
```

## 组件属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| queryAll | boolean | false | 是否查询所有空间 |
| queryPublic | boolean | false | 是否查询公共图库 |
| spaceId | string \| number | undefined | 指定空间ID（建议使用字符串避免精度丢失） |

**注意**: 
- 一次只能选择一种查询方式，优先级为：queryAll > queryPublic > spaceId
- 对于大整数ID，建议使用字符串类型避免JavaScript精度丢失问题

## 传参逻辑

组件支持三种查询方式，一次只能选择一种：

1. **查询所有空间**: 传参 `{ "queryAll": true }`
2. **查询公共图库**: 传参 `{ "queryPublic": true }`
3. **根据ID查询**: 传参 `{ "spaceId": "1959981618412191746" }`（建议使用字符串）

**精度问题解决方案**：
- 对于大整数ID（如：1959981618412191746），使用字符串类型避免JavaScript精度丢失
- 组件内部会自动将spaceId转换为字符串传递给后端

## 数据接口

### SpaceUsageAnalyze.vue
组件调用后端 `/api/space/analyze/usage` 接口获取数据，返回的数据结构包含：

- `usedSize`: 已使用大小（字节）
- `maxSize`: 总大小（字节）
- `sizeUsageRatio`: 空间使用比例（百分比）
- `usedCount`: 当前图片数量
- `maxCount`: 最大图片数量
- `countUsageRatio`: 图片数量占比（百分比）

### SpaceCategoryAnalyze.vue
组件调用后端 `/api/space/analyze/category` 接口获取数据，返回的数据结构包含：

- `category`: 图片分类名称
- `count`: 该分类下的图片数量
- `totalSize`: 该分类下的图片总大小（字节）

### SpaceTagAnalyze.vue
组件调用后端 `/api/space/analyze/tag` 接口获取数据，返回的数据结构包含：

- `tag`: 图片标签名称
- `count`: 该标签关联的图片数量

### SpaceSizeAnalyze.vue
组件调用后端 `/api/space/analyze/size` 接口获取数据，返回的数据结构包含：

- `sizeRange`: 图片大小范围（如 "<100 KB"、"100 KB-1 MB"、">1 MB"）
- `count`: 该大小范围内的图片数量

### SpaceUserAnalyze.vue
组件调用后端 `/api/space/analyze/user` 接口获取数据，返回的数据结构包含：

- `period`: 时间区间（如 "2024-01-01"、"2024-W01"、"2024-01"）
- `count`: 该时间区间内的上传数量

### SpaceRankAnalyze.vue
组件调用后端 `/api/space/analyze/rank` 接口获取数据，返回的数据结构包含：

- `spaceName`: 空间名称
- `totalSize`: 空间总使用量（字节）
- `totalCount`: 空间总图片数量
- `maxSize`: 空间最大容量（字节）
- `maxCount`: 空间最大图片数量

## 图表展示

### SpaceUsageAnalyze.vue
组件使用两个并排的饼图来展示：
1. 左侧：存储空间使用情况
2. 右侧：图片数量使用情况

每个饼图都显示已使用和剩余的比例，并支持鼠标悬停查看详细信息。

### SpaceCategoryAnalyze.vue
组件使用分组条形图来展示：
1. 双Y轴设计：左侧显示图片数量，右侧显示图片总大小(MB)
2. X轴显示不同的图片分类
3. 支持鼠标悬停查看详细信息
4. 自动处理分类名称过长的情况（45度旋转显示）

### SpaceTagAnalyze.vue
组件使用词云图来展示：
1. 字体大小根据图片数量动态调整，数量越多字体越大
2. 丰富的颜色方案，增强视觉效果
3. 支持鼠标悬停查看详细信息
4. 圆形布局，美观且信息密度高
5. 优化的字体大小范围和旋转角度，提高可读性

### SpaceSizeAnalyze.vue
组件使用饼图来展示：
1. 按图片大小分段统计，清晰显示各大小范围的占比
2. 支持鼠标悬停查看详细信息
3. 图例支持滚动，适应大量数据
4. 标签显示在饼图上，直观展示数量信息

### SpaceUserAnalyze.vue
组件使用折线图来展示：
1. 时间序列趋势，清晰显示上传数量的变化趋势
2. 支持鼠标悬停查看详细信息
3. 平滑折线设计，美观且易于理解
4. 渐变填充区域，增强视觉效果

### SpaceRankAnalyze.vue
组件使用柱状图来展示：
1. 按存储使用量排序，直观显示排名情况
2. 支持鼠标悬停查看详细信息
3. 柱状图标签显示，直观展示使用量
4. 自动处理空间名称过长的情况（45度旋转显示）

## 依赖要求

- Vue 3
- vue-echarts
- echarts
- echarts-wordcloud（用于词云图）

确保项目中已安装相关依赖：

```bash
npm install vue-echarts echarts echarts-wordcloud
```
