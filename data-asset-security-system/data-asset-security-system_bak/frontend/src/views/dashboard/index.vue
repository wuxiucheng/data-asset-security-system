<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #409eff">
              <el-icon :size="30"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalAssets || 0 }}</div>
              <div class="stat-label">数据资产总数</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #67c23a">
              <el-icon :size="30"><Files /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.byDepartment?.length || 0 }}</div>
              <div class="stat-label">涉及部门数</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #e6a23c">
              <el-icon :size="30"><DataLine /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.byClassification?.length || 0 }}</div>
              <div class="stat-label">数据分类数</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #f56c6c">
              <el-icon :size="30"><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ sensitiveAssetCount }}</div>
              <div class="stat-label">敏感资产数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>按部门分布</span>
            </div>
          </template>
          <div ref="departmentChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>按分类分布</span>
            </div>
          </template>
          <div ref="classificationChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>按分级分布</span>
            </div>
          </template>
          <div ref="gradingChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>按状态分布</span>
            </div>
          </template>
          <div ref="statusChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import * as echarts from 'echarts'
import { statisticsApi, type Statistics } from '@/api'
import { Document, Files, DataLine, Warning } from '@element-plus/icons-vue'

const statistics = ref<Statistics>({
  totalAssets: 0,
  byDepartment: [],
  byClassification: [],
  byGrading: [],
  byStatus: [],
})

const departmentChartRef = ref<HTMLElement>()
const classificationChartRef = ref<HTMLElement>()
const gradingChartRef = ref<HTMLElement>()
const statusChartRef = ref<HTMLElement>()

const sensitiveAssetCount = computed(() => {
  return statistics.value.byGrading?.reduce((sum, item) => {
    if (item.name.includes('L3') || item.name.includes('L4')) {
      return sum + item.value
    }
    return sum
  }, 0) || 0
})

const loadStatistics = async () => {
  try {
    const res = await statisticsApi.getAssetStatistics()
    statistics.value = res

    // 渲染图表
    renderCharts()
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const renderCharts = () => {
  // 部门分布图
  if (departmentChartRef.value) {
    const departmentChart = echarts.init(departmentChartRef.value)
    departmentChart.setOption({
      tooltip: {
        trigger: 'item',
      },
      legend: {
        orient: 'vertical',
        left: 'left',
      },
      series: [
        {
          name: '数据资产',
          type: 'pie',
          radius: '50%',
          data: statistics.value.byDepartment,
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)',
            },
          },
        },
      ],
    })
  }

  // 分类分布图
  if (classificationChartRef.value) {
    const classificationChart = echarts.init(classificationChartRef.value)
    classificationChart.setOption({
      tooltip: {
        trigger: 'item',
      },
      legend: {
        orient: 'vertical',
        left: 'left',
      },
      series: [
        {
          name: '数据资产',
          type: 'pie',
          radius: '50%',
          data: statistics.value.byClassification,
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)',
            },
          },
        },
      ],
    })
  }

  // 分级分布图
  if (gradingChartRef.value) {
    const gradingChart = echarts.init(gradingChartRef.value)
    gradingChart.setOption({
      tooltip: {
        trigger: 'item',
      },
      legend: {
        orient: 'vertical',
        left: 'left',
      },
      series: [
        {
          name: '数据资产',
          type: 'pie',
          radius: '50%',
          data: statistics.value.byGrading,
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)',
            },
          },
        },
      ],
    })
  }

  // 状态分布图
  if (statusChartRef.value) {
    const statusChart = echarts.init(statusChartRef.value)
    statusChart.setOption({
      tooltip: {
        trigger: 'item',
      },
      legend: {
        orient: 'vertical',
        left: 'left',
      },
      series: [
        {
          name: '数据资产',
          type: 'pie',
          radius: '50%',
          data: statistics.value.byStatus,
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)',
            },
          },
        },
      ],
    })
  }
}

onMounted(() => {
  loadStatistics()

  // 监听窗口大小变化
  window.addEventListener('resize', () => {
    if (departmentChartRef.value) {
      echarts.getInstanceByDom(departmentChartRef.value)?.resize()
    }
    if (classificationChartRef.value) {
      echarts.getInstanceByDom(classificationChartRef.value)?.resize()
    }
    if (gradingChartRef.value) {
      echarts.getInstanceByDom(gradingChartRef.value)?.resize()
    }
    if (statusChartRef.value) {
      echarts.getInstanceByDom(statusChartRef.value)?.resize()
    }
  })
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.stat-card {
  margin-bottom: 0;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #999;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header span {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}
</style>
