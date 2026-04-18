<template>
  <div class="trend-container">
    <!-- 筛选条件 -->
    <el-card class="filter-card">
      <el-form :model="filterForm" inline>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="handleDateChange"
          />
        </el-form-item>
        <el-form-item label="分析类型">
          <el-select v-model="filterForm.type" placeholder="请选择分析类型" clearable style="width: 180px">
            <el-option label="资产增长" value="asset" />
            <el-option label="分类增长" value="classification" />
            <el-option label="分级增长" value="grading" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <el-icon><Search /></el-icon> 查询
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon> 重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon asset-icon">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ totalAssets }}</div>
              <div class="stat-label">总资产数</div>
              <div class="stat-trend" :class="assetTrend >= 0 ? 'up' : 'down'">
                <el-icon><component :is="assetTrend >= 0 ? 'ArrowUp' : 'ArrowDown'" /></el-icon>
                {{ Math.abs(assetTrend) }}%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon classification-icon">
              <el-icon><Files /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ totalClassifications }}</div>
              <div class="stat-label">总分类数</div>
              <div class="stat-trend" :class="classificationTrend >= 0 ? 'up' : 'down'">
                <el-icon><component :is="classificationTrend >= 0 ? 'ArrowUp' : 'ArrowDown'" /></el-icon>
                {{ Math.abs(classificationTrend) }}%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon grading-icon">
              <el-icon><Lock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ totalGradings }}</div>
              <div class="stat-label">总分级数</div>
              <div class="stat-trend" :class="gradingTrend >= 0 ? 'up' : 'down'">
                <el-icon><component :is="gradingTrend >= 0 ? 'ArrowUp' : 'ArrowDown'" /></el-icon>
                {{ Math.abs(gradingTrend) }}%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 趋势图表 -->
    <el-row :gutter="20" class="charts-row">
      <el-col :span="24">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>数据增长趋势</span>
              <el-radio-group v-model="chartType" size="small">
                <el-radio-button value="line">折线图</el-radio-button>
                <el-radio-button value="bar">柱状图</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="trendChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 详细数据表格 -->
    <el-row :gutter="20" class="table-row">
      <el-col :span="24">
        <el-card class="table-card">
          <template #header>
            <div class="card-header">
              <span>详细数据</span>
              <el-button type="primary" size="small" @click="handleExport">
                <el-icon><Download /></el-icon> 导出数据
              </el-button>
            </div>
          </template>
          <el-table :data="tableData" border stripe>
            <el-table-column prop="date" label="日期" width="120" />
            <el-table-column prop="assetCount" label="资产数量" width="120">
              <template #default="{ row }">
                <span class="number-cell">{{ row.assetCount }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="assetGrowth" label="资产增长" width="120">
              <template #default="{ row }">
                <span :class="row.assetGrowth >= 0 ? 'positive' : 'negative'">
                  {{ row.assetGrowth >= 0 ? '+' : '' }}{{ row.assetGrowth }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="classificationCount" label="分类数量" width="120">
              <template #default="{ row }">
                <span class="number-cell">{{ row.classificationCount }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="classificationGrowth" label="分类增长" width="120">
              <template #default="{ row }">
                <span :class="row.classificationGrowth >= 0 ? 'positive' : 'negative'">
                  {{ row.classificationGrowth >= 0 ? '+' : '' }}{{ row.classificationGrowth }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="gradingCount" label="分级数量" width="120">
              <template #default="{ row }">
                <span class="number-cell">{{ row.gradingCount }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="gradingGrowth" label="分级增长" width="120">
              <template #default="{ row }">
                <span :class="row.gradingGrowth >= 0 ? 'positive' : 'negative'">
                  {{ row.gradingGrowth >= 0 ? '+' : '' }}{{ row.gradingGrowth }}
                </span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Download, Document, Files, Lock, ArrowUp, ArrowDown } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { statisticsApi } from '@/api'

// 筛选表单
const filterForm = reactive({
  type: '',
  startDate: '',
  endDate: ''
})

// 日期范围
const dateRange = ref([])

// 统计数据
const totalAssets = ref(0)
const totalClassifications = ref(0)
const totalGradings = ref(0)
const assetTrend = ref(0)
const classificationTrend = ref(0)
const gradingTrend = ref(0)

// 图表相关
const trendChartRef = ref()
const chartType = ref('line')
let trendChart: echarts.ECharts | null = null

// 表格数据
const tableData = ref([])

// 日期变化
const handleDateChange = (value: any) => {
  if (value && value.length === 2) {
    filterForm.startDate = value[0]
    filterForm.endDate = value[1]
  } else {
    filterForm.startDate = ''
    filterForm.endDate = ''
  }
}

// 查询
const handleQuery = () => {
  getTrendData()
}

// 重置
const handleReset = () => {
  dateRange.value = []
  filterForm.type = ''
  filterForm.startDate = ''
  filterForm.endDate = ''
  getTrendData()
}

// 导出
const handleExport = async () => {
  try {
    // 准备导出数据
    const exportData = tableData.value.map(row => ({
      '日期': row.date,
      '资产总数': row.assetCount,
      '资产增长': row.assetGrowth,
      '分类总数': row.classificationCount,
      '分类增长': row.classificationGrowth,
      '分级总数': row.gradingCount,
      '分级增长': row.gradingGrowth
    }))

    // 创建CSV内容
    const headers = Object.keys(exportData[0]).join(',')
    const rows = exportData.map(row => Object.values(row).join(','))
    const csvContent = [headers, ...rows].join('\n')

    // 创建Blob对象
    const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' })
    
    // 创建下载链接
    const link = document.createElement('a')
    const url = URL.createObjectURL(blob)
    link.setAttribute('href', url)
    link.setAttribute('download', `数据增长趋势_${new Date().toLocaleDateString()}.csv`)
    link.style.visibility = 'hidden'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    
    ElMessage.success('数据导出成功')
  } catch (error) {
    ElMessage.error('数据导出失败')
  }
}

// 获取趋势数据
const getTrendData = async () => {
  try {
    // 过滤空值参数
    const params: any = {}
    if (filterForm.type) params.type = filterForm.type
    if (filterForm.startDate) params.startDate = filterForm.startDate
    if (filterForm.endDate) params.endDate = filterForm.endDate

    const res = await statisticsApi.getTrendData(params)
    const data = res.data

    // 更新统计数据
    if (data.assetGrowth && data.assetGrowth.length > 0) {
      totalAssets.value = data.assetGrowth[data.assetGrowth.length - 1]
      const firstAsset = data.assetGrowth[0] || 0
      const lastAsset = data.assetGrowth[data.assetGrowth.length - 1] || 0
      assetTrend.value = firstAsset > 0 ? ((lastAsset - firstAsset) / firstAsset * 100).toFixed(1) as any : 0
    }

    if (data.classificationGrowth && data.classificationGrowth.length > 0) {
      totalClassifications.value = data.classificationGrowth[data.classificationGrowth.length - 1]
      const firstClassification = data.classificationGrowth[0] || 0
      const lastClassification = data.classificationGrowth[data.classificationGrowth.length - 1] || 0
      classificationTrend.value = firstClassification > 0 ? ((lastClassification - firstClassification) / firstClassification * 100).toFixed(1) as any : 0
    }

    if (data.gradingGrowth && data.gradingGrowth.length > 0) {
      totalGradings.value = data.gradingGrowth[data.gradingGrowth.length - 1]
      const firstGrading = data.gradingGrowth[0] || 0
      const lastGrading = data.gradingGrowth[data.gradingGrowth.length - 1] || 0
      gradingTrend.value = firstGrading > 0 ? ((lastGrading - firstGrading) / firstGrading * 100).toFixed(1) as any : 0
    }

    // 更新表格数据
    tableData.value = data.dates.map((date: string, index: number) => ({
      date,
      assetCount: data.assetGrowth[index] || 0,
      assetGrowth: index > 0 ? (data.assetGrowth[index] || 0) - (data.assetGrowth[index - 1] || 0) : 0,
      classificationCount: data.classificationGrowth[index] || 0,
      classificationGrowth: index > 0 ? (data.classificationGrowth[index] || 0) - (data.classificationGrowth[index - 1] || 0) : 0,
      gradingCount: data.gradingGrowth[index] || 0,
      gradingGrowth: index > 0 ? (data.gradingGrowth[index] || 0) - (data.gradingGrowth[index - 1] || 0) : 0
    }))
    
    // 更新图表
    updateTrendChart(data)
  } catch (error) {
    ElMessage.error('获取趋势数据失败')
  }
}

// 更新趋势图表
const updateTrendChart = (data: any) => {
  if (!trendChart) return
  
  const option: echarts.EChartsOption = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    legend: {
      data: ['资产数量', '分类数量', '分级数量']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: chartType.value === 'bar',
      data: data.dates
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '资产数量',
        type: chartType.value,
        data: data.assetGrowth,
        smooth: true,
        itemStyle: {
          color: '#409EFF'
        }
      },
      {
        name: '分类数量',
        type: chartType.value,
        data: data.classificationGrowth,
        smooth: true,
        itemStyle: {
          color: '#67C23A'
        }
      },
      {
        name: '分级数量',
        type: chartType.value,
        data: data.gradingGrowth,
        smooth: true,
        itemStyle: {
          color: '#E6A23C'
        }
      }
    ]
  }
  
  trendChart.setOption(option)
}

watch(chartType, () => {
  if (!trendChart) return
  
  trendChart.setOption({
    xAxis: {
      boundaryGap: chartType.value === 'bar'
    },
    series: trendChart.getOption().series.map((s: any) => ({
      ...s,
      type: chartType.value,
      smooth: chartType.value === 'line'
    }))
  })
})

// 初始化图表
const initTrendChart = () => {
  if (trendChartRef.value) {
    trendChart = echarts.init(trendChartRef.value)
    
    // 监听窗口大小变化
    window.addEventListener('resize', () => {
      trendChart?.resize()
    })
  }
}

// 初始化
onMounted(async () => {
  await nextTick()
  initTrendChart()
  getTrendData()
})
</script>

<style scoped>
.trend-container {
  padding: 20px;
}

.filter-card {
  margin-bottom: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  cursor: pointer;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  font-size: 28px;
  color: white;
}

.asset-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.classification-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.grading-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-trend {
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.stat-trend.up {
  color: #67C23A;
}

.stat-trend.down {
  color: #F56C6C;
}

.charts-row {
  margin-bottom: 20px;
}

.chart-card {
  height: 500px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  width: 100%;
  height: 420px;
}

.table-row {
  margin-bottom: 20px;
}

.number-cell {
  font-weight: bold;
  color: #409EFF;
}

.positive {
  color: #67C23A;
  font-weight: bold;
}

.negative {
  color: #F56C6C;
  font-weight: bold;
}
</style>
