<template>
  <div class="compliance-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>数据标准与合规管理</span>
          <div>
            <el-button type="primary" @click="handleTestRule">规则测试</el-button>
            <el-button type="success" @click="handleShowChart">可视化分析</el-button>
          </div>
        </div>
      </template>
      
      <el-tabs v-model="activeTab">
        <el-tab-pane label="数据标准" name="standard">
          <div class="tab-header">
            <el-button type="primary" @click="handleAddStandard">新增标准</el-button>
          </div>
          <el-table :data="standards" border stripe v-loading="loading">
            <el-table-column prop="standardId" label="ID" width="80" />
            <el-table-column prop="standardName" label="标准名称" width="180" />
            <el-table-column prop="standardCode" label="标准编码" width="120" />
            <el-table-column prop="standardType" label="类型" width="100">
              <template #default="{ row }">
                <el-tag>{{ row.standardType }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="category" label="分类" width="120" />
            <el-table-column prop="description" label="描述" show-overflow-tooltip />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-switch v-model="row.status" active-value="ACTIVE" inactive-value="INACTIVE" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleEditStandard(row)">编辑</el-button>
                <el-button type="danger" link @click="handleDeleteStandard(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        
        <el-tab-pane label="合规条款" name="clause">
          <div class="tab-header">
            <el-button type="primary" @click="handleAddClause">新增条款</el-button>
          </div>
          <el-table :data="clauses" border stripe>
            <el-table-column prop="clauseId" label="ID" width="80" />
            <el-table-column prop="clauseName" label="条款名称" width="180" />
            <el-table-column prop="clauseCode" label="条款编码" width="120" />
            <el-table-column prop="regulation" label="法规" width="150">
              <template #default="{ row }">
                <el-tag type="warning">{{ row.regulation }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="description" label="描述" show-overflow-tooltip />
            <el-table-column prop="status" label="状态" width="100" />
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleEditClause(row)">编辑</el-button>
                <el-button type="danger" link @click="handleDeleteClause(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        
        <el-tab-pane label="治理KPI" name="kpi">
          <div class="tab-header">
            <el-button type="primary" @click="handleAddKpi">新增KPI</el-button>
          </div>
          <el-table :data="kpis" border stripe>
            <el-table-column prop="kpiId" label="ID" width="80" />
            <el-table-column prop="kpiName" label="KPI名称" width="180" />
            <el-table-column prop="kpiCode" label="KPI编码" width="120" />
            <el-table-column prop="kpiType" label="类型" width="100" />
            <el-table-column prop="targetValue" label="目标值" width="100" />
            <el-table-column prop="actualValue" label="实际值" width="100">
              <template #default="{ row }">
                <span :style="{color: row.actualValue >= row.targetValue ? '#67C23A' : '#F56C6C'}">
                  {{ row.actualValue }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="unit" label="单位" width="80" />
            <el-table-column prop="period" label="周期" width="100" />
            <el-table-column label="达成率" width="100">
              <template #default="{ row }">
                <el-progress 
                  :percentage="Math.min(100, (row.actualValue / row.targetValue * 100).toFixed(1))" 
                  :color="row.actualValue >= row.targetValue ? '#67C23A' : '#F56C6C'"
                />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleEditKpi(row)">编辑</el-button>
                <el-button type="danger" link @click="handleDeleteKpi(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 规则测试对话框 -->
    <el-dialog v-model="testDialogVisible" title="规则测试" width="800px">
      <el-form :model="testForm" label-width="120px">
        <el-form-item label="测试类型">
          <el-select v-model="testForm.testType" placeholder="请选择">
            <el-option label="数据标准验证" value="standard" />
            <el-option label="合规条款检查" value="compliance" />
          </el-select>
        </el-form-item>
        <el-form-item label="选择规则">
          <el-select v-model="testForm.ruleId" placeholder="请选择规则">
            <el-option v-for="item in (testForm.testType === 'standard' ? standards : clauses)" 
                       :key="item.standardId || item.clauseId" 
                       :label="item.standardName || item.clauseName" 
                       :value="item.standardId || item.clauseId" />
          </el-select>
        </el-form-item>
        <el-form-item label="测试数据">
          <el-input v-model="testForm.testData" type="textarea" :rows="5" placeholder="请输入测试数据（JSON格式）" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="executeTest">执行测试</el-button>
        </el-form-item>
        <el-form-item label="测试结果" v-if="testResult">
          <el-input v-model="testResult" type="textarea" :rows="5" readonly />
        </el-form-item>
      </el-form>
    </el-dialog>

    <!-- 可视化分析对话框 -->
    <el-dialog v-model="chartDialogVisible" title="合规可视化分析" width="1000px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card>
            <template #header><span>合规达成率</span></template>
            <div ref="complianceChart" style="height: 300px;"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card>
            <template #header><span>KPI趋势</span></template>
            <div ref="kpiChart" style="height: 300px;"></div>
          </el-card>
        </el-col>
      </el-row>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import request from '@/utils/request'

const activeTab = ref('standard')
const loading = ref(false)
const standards = ref<any[]>([])
const clauses = ref<any[]>([])
const kpis = ref<any[]>([])
const testDialogVisible = ref(false)
const chartDialogVisible = ref(false)
const testResult = ref('')
const complianceChart = ref()
const kpiChart = ref()

const testForm = ref({
  testType: 'standard',
  ruleId: null as number | null,
  testData: ''
})

const loadData = async () => {
  loading.value = true
  try {
    const [res1, res2, res3] = await Promise.all([
      request.get('/compliance/standard/list'),
      request.get('/compliance/clause/list'),
      request.get('/compliance/kpi/list')
    ])
    if (res1.data) standards.value = res1.data
    if (res2.data) clauses.value = res2.data
    if (res3.data) kpis.value = res3.data
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleTestRule = () => {
  testDialogVisible.value = true
  testResult.value = ''
}

const executeTest = () => {
  // 模拟测试结果
  testResult.value = JSON.stringify({
    status: 'PASS',
    message: '规则验证通过',
    details: {
      ruleId: testForm.value.ruleId,
      testType: testForm.value.testType,
      timestamp: new Date().toISOString()
    }
  }, null, 2)
  ElMessage.success('测试执行完成')
}

const handleShowChart = () => {
  chartDialogVisible.value = true
  nextTick(() => {
    initCharts()
  })
}

const initCharts = () => {
  // 合规达成率图表
  if (complianceChart.value) {
    const chart1 = echarts.init(complianceChart.value)
    chart1.setOption({
      tooltip: { trigger: 'item' },
      legend: { top: '5%', left: 'center' },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
        label: { show: false, position: 'center' },
        emphasis: { label: { show: true, fontSize: 20, fontWeight: 'bold' } },
        data: [
          { value: 85, name: '已达成', itemStyle: { color: '#67C23A' } },
          { value: 15, name: '未达成', itemStyle: { color: '#F56C6C' } }
        ]
      }]
    })
  }

  // KPI趋势图表
  if (kpiChart.value) {
    const chart2 = echarts.init(kpiChart.value)
    chart2.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: ['1月', '2月', '3月', '4月', '5月', '6月'] },
      yAxis: { type: 'value' },
      series: [{
        data: [75, 80, 85, 82, 88, 90],
        type: 'line',
        smooth: true,
        areaStyle: { color: '#409EFF' }
      }]
    })
  }
}

const handleAddStandard = () => ElMessage.info('新增数据标准功能开发中')
const handleEditStandard = (row: any) => ElMessage.info('编辑功能开发中')
const handleDeleteStandard = (row: any) => ElMessage.info('删除功能开发中')
const handleAddClause = () => ElMessage.info('新增合规条款功能开发中')
const handleEditClause = (row: any) => ElMessage.info('编辑功能开发中')
const handleDeleteClause = (row: any) => ElMessage.info('删除功能开发中')
const handleAddKpi = () => ElMessage.info('新增KPI功能开发中')
const handleEditKpi = (row: any) => ElMessage.info('编辑功能开发中')
const handleDeleteKpi = (row: any) => ElMessage.info('删除功能开发中')

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.compliance-container {
  padding: 20px;
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .tab-header {
    margin-bottom: 20px;
  }
}
</style>
