<template>
  <div class="statistics-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon total-icon">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statisticsData.totalAssets }}</div>
              <div class="stat-label">数据资产总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon classification-icon">
              <el-icon><Files /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statisticsData.totalClassifications }}</div>
              <div class="stat-label">数据分类总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon grading-icon">
              <el-icon><Lock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statisticsData.totalGradings }}</div>
              <div class="stat-label">数据分级总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon department-icon">
              <el-icon><OfficeBuilding /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statisticsData.totalDepartments }}</div>
              <div class="stat-label">管理部门总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-row">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>按部门统计</span>
              <el-button type="primary" size="small" @click="refreshData">
                <el-icon><Refresh /></el-icon> 刷新
              </el-button>
            </div>
          </template>
          <div ref="departmentChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>按分类统计</span>
              <el-button type="primary" size="small" @click="refreshData">
                <el-icon><Refresh /></el-icon> 刷新
              </el-button>
            </div>
          </template>
          <div ref="classificationChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="charts-row">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>按分级统计</span>
              <el-button type="primary" size="small" @click="refreshData">
                <el-icon><Refresh /></el-icon> 刷新
              </el-button>
            </div>
          </template>
          <div ref="gradingChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>按状态统计</span>
              <el-button type="primary" size="small" @click="refreshData">
                <el-icon><Refresh /></el-icon> 刷新
              </el-button>
            </div>
          </template>
          <div ref="statusChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 详细数据表格 -->
    <el-row :gutter="20" class="table-row">
      <el-col :span="24">
        <el-card class="table-card">
          <template #header>
            <div class="card-header">
              <span>资产分布详情</span>
              <el-button type="primary" size="small" @click="exportData">
                <el-icon><Download /></el-icon> 导出数据
              </el-button>
            </div>
          </template>
          <el-table :data="tableData" border stripe>
            <el-table-column prop="departmentName" label="部门名称" width="150" />
            <el-table-column prop="classificationName" label="分类名称" width="150" />
            <el-table-column prop="gradingName" label="分级名称" width="120">
              <template #default="{ row }">
                <el-tag :type="getGradingTagType(row.gradingName)">
                  {{ row.gradingName }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="assetCount" label="资产数量" width="120">
              <template #default="{ row }">
                <span class="number-cell">{{ row.assetCount }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="percentage" label="占比" width="120">
              <template #default="{ row }">
                <el-progress :percentage="row.percentage" :stroke-width="10" />
              </template>
            </el-table-column>
            <el-table-column prop="lastUpdateTime" label="最后更新时间" width="180" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh, Download, Document, Files, Lock, OfficeBuilding } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { statisticsApi } from '@/api'

// 统计数据
const statisticsData = reactive({
  totalAssets: 0,
  totalClassifications: 0,
  totalGradings: 0,
  totalDepartments: 0
})

// 图表相关
const departmentChartRef = ref()
const classificationChartRef = ref()
const gradingChartRef = ref()
const statusChartRef = ref()

let departmentChart: echarts.ECharts | null = null
let classificationChart: echarts.ECharts | null = null
let gradingChart: echarts.ECharts | null = null
let statusChart: echarts.ECharts | null = null

// 表格数据
const tableData = ref([
  { departmentName: '技术部', classificationName: '业务数据', gradingName: '二级', assetCount: 45, percentage: 30, lastUpdateTime: '2025-06-15 14:30:00' },
  { departmentName: '业务部', classificationName: '业务数据', gradingName: '二级', assetCount: 35, percentage: 23, lastUpdateTime: '2025-06-15 16:45:00' },
  { departmentName: '技术部', classificationName: '财务数据', gradingName: '三级', assetCount: 20, percentage: 13, lastUpdateTime: '2025-06-14 10:20:00' },
  { departmentName: '财务部', classificationName: '财务数据', gradingName: '三级', assetCount: 30, percentage: 20, lastUpdateTime: '2025-06-15 09:15:00' },
  { departmentName: '人事部', classificationName: '人力资源数据', gradingName: '二级', assetCount: 20, percentage: 14, lastUpdateTime: '2025-06-15 11:00:00' },
])

// 获取分级标签类型
const getGradingTagType = (gradingName: string) => {
  if (gradingName === '一级') return 'info'
  if (gradingName === '二级') return 'success'
  if (gradingName === '三级') return 'warning'
  if (gradingName === '四级') return 'danger'
  return 'info'
}

// 获取统计数据
const getStatisticsData = async () => {
  try {
    const res = await statisticsApi.getAssetStatistics()
    statisticsData.totalAssets = res.data.totalAssets
    statisticsData.totalClassifications = res.data.byClassification.reduce((sum: number, item: any) => sum + item.value, 0)
    statisticsData.totalGradings = res.data.byGrading.length
    statisticsData.totalDepartments = res.data.byDepartment.length
    
    // 更新图表
    updateCharts(res.data)
  } catch (error) {
    ElMessage.error('获取统计数据失败')
  }
}

// 更新图表
const updateCharts = (data: any) => {
  // 按部门统计图表
  if (departmentChart) {
    departmentChart.setOption({
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: [
        {
          name: '资产分布',
          type: 'pie',
          radius: '50%',
          data: data.byDepartment,
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    })
  }

  // 按分类统计图表
  if (classificationChart) {
    classificationChart.setOption({
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'value'
      },
      yAxis: {
        type: 'category',
        data: data.byClassification.map((item: any) => item.name)
      },
      series: [
        {
          name: '资产数量',
          type: 'bar',
          data: data.byClassification.map((item: any) => item.value),
          itemStyle: {
            color: '#67C23A'
          }
        }
      ]
    })
  }

  // 按分级统计图表
  if (gradingChart) {
    gradingChart.setOption({
      tooltip: {
        trigger: 'item'
      },
      legend: {
        top: '5%',
        left: 'center'
      },
      series: [
        {
          name: '资产分级',
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: false,
            position: 'center'
          },
          emphasis: {
            label: {
              show: true,
              fontSize: 20,
              fontWeight: 'bold'
            }
          },
          labelLine: {
            show: false
          },
          data: data.byGrading.map((item: any, index: number) => ({
            value: item.value,
            name: item.name,
            itemStyle: {
              color: ['#67C23A', '#E6A23C', '#F56C6C', '#909399'][index]
            }
          }))
        }
      ]
    })
  }

  // 按状态统计图表
  if (statusChart) {
    statusChart.setOption({
      tooltip: {
        trigger: 'axis'
      },
      legend: {
        data: ['资产数量']
      },
      xAxis: {
        type: 'category',
        data: data.byStatus.map((item: any) => item.name)
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: '资产数量',
          type: 'line',
          data: data.byStatus.map((item: any) => item.value),
          smooth: true,
          itemStyle: {
            color: '#409EFF'
          },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
              { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
            ])
          }
        }
      ]
    })
  }
}

// 刷新数据
const refreshData = () => {
  getStatisticsData()
  ElMessage.success('数据刷新成功')
}

// 导出数据
const exportData = async () => {
  try {
    // 准备导出数据
    const exportData = tableData.value.map(row => ({
      '部门名称': row.departmentName,
      '分类名称': row.classificationName,
      '分级名称': row.gradingName,
      '资产数量': row.assetCount,
      '占比(%)': row.percentage,
      '最后更新时间': row.lastUpdateTime
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
    link.setAttribute('download', `资产分布统计_${new Date().toLocaleDateString()}.csv`)
    link.style.visibility = 'hidden'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    
    ElMessage.success('数据导出成功')
  } catch (error) {
    ElMessage.error('数据导出失败')
  }
}

// 初始化图表
const initCharts = () => {
  if (departmentChartRef.value) {
    departmentChart = echarts.init(departmentChartRef.value)
  }
  if (classificationChartRef.value) {
    classificationChart = echarts.init(classificationChartRef.value)
  }
  if (gradingChartRef.value) {
    gradingChart = echarts.init(gradingChartRef.value)
  }
  if (statusChartRef.value) {
    statusChart = echarts.init(statusChartRef.value)
  }

  // 监听窗口大小变化
  window.addEventListener('resize', () => {
    departmentChart?.resize()
    classificationChart?.resize()
    gradingChart?.resize()
    statusChart?.resize()
  })
}

// 初始化
onMounted(async () => {
  await nextTick()
  initCharts()
  getStatisticsData()
})
</script>

<style scoped>
.statistics-container {
  padding: 20px;
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

.total-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.classification-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.grading-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.department-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
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
}

.charts-row {
  margin-bottom: 20px;
}

.chart-card {
  height: 400px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  width: 100%;
  height: 320px;
}

.table-row {
  margin-bottom: 20px;
}

.number-cell {
  font-weight: bold;
  color: #409EFF;
  font-size: 16px;
}
</style>
