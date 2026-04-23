<template>
  <div class="report-classification-stats-container">
    <!-- 页面标题 -->
    <el-card class="header-card">
      <template #header>
        <div class="card-header">
          <span>数据分类分级统计报告</span>
          <el-button type="primary" @click="handleGenerateReport">
            <el-icon><DataAnalysis /></el-icon> 生成统计报告
          </el-button>
        </div>
      </template>
      <el-alert
        title="报告说明"
        type="info"
        :closable="false"
        show-icon
      >
        <template #default>
          <p>数据分类分级统计报告按照分类分级标准统计各类数据的分布情况，包括按分类标准统计、按分级标准统计、各部门数据分类分级情况等。</p>
        </template>
      </el-alert>
    </el-card>

    <!-- 统计参数配置 -->
    <el-card class="config-card">
      <template #header>
        <span>统计参数配置</span>
      </template>
      <el-form :model="reportForm" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="统计维度">
              <el-select v-model="reportForm.dimension" placeholder="请选择统计维度" clearable style="width: 100%">
                <el-option label="按分类标准" value="classification" />
                <el-option label="按分级标准" value="grading" />
                <el-option label="按部门" value="department" />
                <el-option label="按资产类型" value="assetType" />
                <el-option label="综合统计" value="comprehensive" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="时间范围">
              <el-date-picker
                v-model="dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="输出格式">
              <el-radio-group v-model="reportForm.outputFormat">
                <el-radio value="excel">Excel</el-radio>
                <el-radio value="pdf">PDF</el-radio>
                <el-radio value="html">HTML</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="报告名称">
              <el-input
                v-model="reportForm.reportName"
                placeholder="请输入报告名称，默认为：数据分类分级统计报告_YYYYMMDD"
                clearable
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- 统计结果展示 -->
    <el-card class="result-card" v-if="statisticsData">
      <template #header>
        <div class="card-header">
          <span>统计结果</span>
          <div>
            <el-button type="success" @click="handleExportExcel" :disabled="!statisticsData">
              <el-icon><Download /></el-icon> 导出Excel
            </el-button>
            <el-button type="warning" @click="handleExportPDF" :disabled="!statisticsData">
              <el-icon><Download /></el-icon> 导出PDF
            </el-button>
          </div>
        </div>
      </template>

      <!-- 关键指标卡片 -->
      <el-row :gutter="20" class="metrics-row">
        <el-col :span="6">
          <el-card class="metric-card">
            <el-statistic title="总资产数" :value="statisticsData.totalAssets">
              <template #suffix>
                <el-icon style="vertical-align: -0.125em">
                  <component :is="'Document'" />
                </el-icon>
              </template>
            </el-statistic>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="metric-card">
            <el-statistic title="分类覆盖率" :value="parseFloat(statisticsData.classificationCoverage)">
              <template #suffix>
                <span style="color: inherit">%</span>
                <el-icon style="vertical-align: -0.125em">
                  <component :is="'CircleCheck'" />
                </el-icon>
              </template>
            </el-statistic>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="metric-card">
            <el-statistic title="分级覆盖率" :value="parseFloat(statisticsData.gradingCoverage)">
              <template #suffix>
                <span style="color: inherit">%</span>
                <el-icon style="vertical-align: -0.125em">
                  <component :is="'CircleCheck'" />
                </el-icon>
              </template>
            </el-statistic>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="metric-card">
            <el-statistic title="高级别资产" :value="statisticsData.highLevelAssets">
              <template #suffix>
                <el-icon style="vertical-align: -0.125em">
                  <component :is="'Warning'" />
                </el-icon>
              </template>
            </el-statistic>
          </el-card>
        </el-col>
      </el-row>

      <!-- 图表展示 -->
      <el-row :gutter="20" class="charts-row">
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>
              <span>分类分布</span>
            </template>
            <div ref="classificationChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>
              <span>分级分布</span>
            </template>
            <div ref="gradingChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="charts-row">
        <el-col :span="24">
          <el-card class="chart-card">
            <template #header>
              <span>各部门分类分级情况</span>
            </template>
            <div ref="departmentChartRef" class="chart-container-large"></div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 详细数据表格 -->
      <el-card class="table-card">
        <template #header>
          <span>详细统计数据</span>
        </template>
        <el-table :data="statisticsData.detailData" border stripe>
          <el-table-column prop="category" label="类别" width="150" />
          <el-table-column prop="count" label="数量" width="100" />
          <el-table-column prop="percentage" label="占比" width="100">
            <template #default="{ row }">
              {{ row.percentage + '%' }}
            </template>
          </el-table-column>
          <el-table-column prop="trend" label="趋势" width="100">
            <template #default="{ row }">
              <el-tag :type="row.trend >= 0 ? 'success' : 'danger'">
                {{ row.trend >= 0 ? '+' : '' }}{{ row.trend }}%
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="description" label="说明" show-overflow-tooltip />
        </el-table>
      </el-card>
    </el-card>

    <!-- 报告生成历史 -->
    <el-card class="history-card">
      <template #header>
        <span>报告生成历史</span>
      </template>
      <el-table :data="historyData" border stripe>
        <el-table-column prop="reportName" label="报告名称" width="200" />
        <el-table-column prop="dimension" label="统计维度" width="120" />
        <el-table-column prop="outputFormat" label="输出格式" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.outputFormat === 'excel'" type="success">Excel</el-tag>
            <el-tag v-else-if="row.outputFormat === 'pdf'" type="warning">PDF</el-tag>
            <el-tag v-else type="info">HTML</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="generatedBy" label="生成人" width="120" />
        <el-table-column prop="generationTime" label="生成时间" width="160" />
        <el-table-column prop="downloadCount" label="下载次数" width="100" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleDownload(row)">
              <el-icon><Download /></el-icon> 下载
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">
              <el-icon><Delete /></el-icon> 删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { DataAnalysis, Download, Delete } from '@element-plus/icons-vue'
import { reportApi } from '@/api'
import * as echarts from 'echarts'

// 报告表单
const reportForm = reactive({
  dimension: 'comprehensive',
  outputFormat: 'excel',
  reportName: ''
})

// 日期范围
const dateRange = ref([])

// 统计数据
const statisticsData = ref(null)

// 图表引用
const classificationChartRef = ref()
const gradingChartRef = ref()
const departmentChartRef = ref()

// 图表实例
let classificationChart: echarts.ECharts | null = null
let gradingChart: echarts.ECharts | null = null
let departmentChart: echarts.ECharts | null = null

// 历史数据
const historyData = ref([
  {
    id: 1,
    reportName: '数据分类分级统计报告_20250418',
    dimension: '综合统计',
    outputFormat: 'excel',
    generatedBy: 'admin',
    generationTime: '2025-04-18 10:30:00',
    downloadCount: 3
  },
  {
    id: 2,
    reportName: '部门分类分级统计_20250415',
    dimension: '按部门',
    outputFormat: 'pdf',
    generatedBy: 'admin',
    generationTime: '2025-04-15 14:20:00',
    downloadCount: 5
  }
])

// 生成统计报告
const handleGenerateReport = async () => {
  try {
    const params = {
      ...reportForm,
      startDate: dateRange.value[0],
      endDate: dateRange.value[1]
    }
    
    const res = await reportApi.generateClassificationStatsReport(params)
    statisticsData.value = res.data
    
    ElMessage.success('统计报告生成成功')
    
    // 渲染图表
    await nextTick()
    renderCharts()
  } catch (error) {
    ElMessage.error('统计报告生成失败')
    console.error('统计报告生成失败:', error)
  }
}

// 渲染图表
const renderCharts = () => {
  // 分类分布饼图
  if (classificationChartRef.value) {
    classificationChart = echarts.init(classificationChartRef.value)
    classificationChart.setOption({
      title: {
        text: '分类分布',
        left: 'center'
      },
      tooltip: {
        trigger: 'item'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: [
        {
          name: '分类分布',
          type: 'pie',
          radius: '50%',
          data: statisticsData.value.classificationDistribution,
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

  // 分级分布柱状图
  if (gradingChartRef.value) {
    gradingChart = echarts.init(gradingChartRef.value)
    gradingChart.setOption({
      title: {
        text: '分级分布',
        left: 'center'
      },
      tooltip: {
        trigger: 'axis'
      },
      xAxis: {
        type: 'category',
        data: statisticsData.value.gradingDistribution.map(item => item.name)
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: '资产数量',
          type: 'bar',
          data: statisticsData.value.gradingDistribution.map(item => item.value),
          itemStyle: {
            color: function(params) {
              const colors = ['#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272']
              return colors[params.dataIndex % colors.length]
            }
          }
        }
      ]
    })
  }

  // 部门分类分级情况堆叠柱状图
  if (departmentChartRef.value) {
    departmentChart = echarts.init(departmentChartRef.value)
    departmentChart.setOption({
      title: {
        text: '各部门分类分级情况',
        left: 'center'
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      legend: {
        data: statisticsData.value.departmentStats.categories,
        top: '10%'
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        top: '20%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: statisticsData.value.departmentStats.departments
      },
      yAxis: {
        type: 'value'
      },
      series: statisticsData.value.departmentStats.series
    })
  }
}

// 导出Excel
const handleExportExcel = async () => {
  try {
    const params = {
      ...reportForm,
      outputFormat: 'excel'
    }
    
    const res = await reportApi.exportClassificationStatsReport(params)
    
    const blob = new Blob([res.data])
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `${reportForm.reportName || '数据分类分级统计报告'}_${new Date().getTime()}.xlsx`
    link.click()
    
    ElMessage.success('Excel导出成功')
  } catch (error) {
    ElMessage.error('Excel导出失败')
    console.error('Excel导出失败:', error)
  }
}

// 导出PDF
const handleExportPDF = async () => {
  try {
    const params = {
      ...reportForm,
      outputFormat: 'pdf'
    }
    
    const res = await reportApi.exportClassificationStatsReport(params)
    
    const blob = new Blob([res.data], { type: 'application/pdf' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `${reportForm.reportName || '数据分类分级统计报告'}_${new Date().getTime()}.pdf`
    link.click()
    
    ElMessage.success('PDF导出成功')
  } catch (error) {
    ElMessage.error('PDF导出失败')
    console.error('PDF导出失败:', error)
  }
}

// 下载历史报告
const handleDownload = async (row: any) => {
  try {
    ElMessage.info('开始下载：' + row.reportName)
    
    // 模拟文件下载
    const mockFileContent = `报告名称: ${row.reportName}\n报告类型: ${row.reportType}\n生成时间: ${row.generationTime}\n下载次数: ${row.downloadCount}`;
    const blob = new Blob([mockFileContent], { type: 'text/plain;charset=utf-8' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `${row.reportName}.txt`;
    link.click();
    window.URL.revokeObjectURL(url);
    
    // 更新下载次数
    row.downloadCount++;
    
    ElMessage.success('下载成功：' + row.reportName);
  } catch (error) {
    ElMessage.error('下载失败')
    console.error('下载失败:', error)
  }
}

// 删除历史报告
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该报告吗？', '提示', {
      type: 'warning'
    })
    
    historyData.value = historyData.value.filter(item => item.id !== row.id)
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 初始化
onMounted(() => {
  // 可以在这里加载默认统计
})
</script>

<style scoped lang="scss">
.report-classification-stats-container {
  padding: 20px;

  .header-card {
    margin-bottom: 20px;

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }

  .config-card {
    margin-bottom: 20px;
  }

  .result-card {
    margin-bottom: 20px;

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .metrics-row {
      margin-bottom: 20px;

      .metric-card {
        text-align: center;
        border: 1px solid #ebeef5;
        border-radius: 4px;
        padding: 10px;
      }
    }

    .charts-row {
      margin-bottom: 20px;

      .chart-card {
        .chart-container {
          height: 300px;
        }

        .chart-container-large {
          height: 400px;
        }
      }
    }

    .table-card {
      margin-top: 20px;
    }
  }

  .history-card {
    margin-bottom: 20px;
  }
}
</style>
