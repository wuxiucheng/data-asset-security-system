<template>
  <div class="audit-log-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>审计日志</h2>
          <div class="header-actions">
            <el-button @click="toggleStatistics">
              <el-icon><DataAnalysis /></el-icon>
              {{ showStatistics ? '隐藏统计' : '显示统计' }}
            </el-button>
            <el-button type="primary" @click="exportLogs" :loading="exporting">
              <el-icon><Download /></el-icon>
              导出日志
            </el-button>
          </div>
        </div>
      </template>

      <!-- 查询条件 -->
      <el-form :model="queryForm" inline class="query-form">
        <el-form-item label="操作类型">
          <el-select v-model="queryForm.operationType" placeholder="请选择" clearable style="width: 180px">
            <el-option label="登录" value="LOGIN" />
            <el-option label="登出" value="LOGOUT" />
            <el-option label="创建" value="CREATE" />
            <el-option label="更新" value="UPDATE" />
            <el-option label="删除" value="DELETE" />
            <el-option label="查询" value="QUERY" />
            <el-option label="审批" value="APPROVE" />
          </el-select>
        </el-form-item>

        <el-form-item label="模块">
          <el-select v-model="queryForm.moduleName" placeholder="请选择" clearable style="width: 180px">
            <el-option label="用户" value="USER" />
            <el-option label="角色" value="ROLE" />
            <el-option label="权限" value="PERMISSION" />
            <el-option label="资产" value="ASSET" />
            <el-option label="分类" value="CLASSIFICATION" />
            <el-option label="分级" value="GRADING" />
          </el-select>
        </el-form-item>

        <el-form-item label="操作结果">
          <el-select v-model="queryForm.operationResult" placeholder="请选择" clearable style="width: 180px">
            <el-option label="成功" value="SUCCESS" />
            <el-option label="失败" value="FAILURE" />
          </el-select>
        </el-form-item>

        <el-form-item label="操作人">
          <el-input v-model="queryForm.operatorUsername" placeholder="请输入操作人" clearable style="width: 180px" />
        </el-form-item>

        <el-form-item label="时间范围">
          <el-date-picker
            v-model="dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 360px"
          />
        </el-form-item>

        <el-form-item label="关键词">
          <el-input v-model="queryForm.keyword" placeholder="请输入关键词" clearable style="width: 180px" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleQuery" :loading="loading">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="handleReset">
            <el-icon><RefreshLeft /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 统计信息 -->
      <div v-if="showStatistics && statistics" class="statistics-section">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-statistic title="总操作次数" :value="statistics.totalOperations" />
          </el-col>
          <el-col :span="6">
            <el-statistic title="成功次数" :value="statistics.successCount">
              <template #suffix>
                <el-icon color="#67C23A"><SuccessFilled /></el-icon>
              </template>
            </el-statistic>
          </el-col>
          <el-col :span="6">
            <el-statistic title="失败次数" :value="statistics.failureCount">
              <template #suffix>
                <el-icon color="#F56C6C"><CircleCloseFilled /></el-icon>
              </template>
            </el-statistic>
          </el-col>
          <el-col :span="6">
            <el-statistic title="成功率" :value="statistics.successRate" suffix="%" :precision="2" />
          </el-col>
        </el-row>

        <el-row :gutter="20" style="margin-top: 20px">
          <el-col :span="12">
            <div class="chart-container">
              <h4>按操作类型统计</h4>
              <div v-for="(count, type) in statistics.operationTypeStats" :key="type" class="stat-item">
                <span class="stat-label">{{ type }}:</span>
                <span class="stat-value">{{ count }}</span>
              </div>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="chart-container">
              <h4>按模块统计</h4>
              <div v-for="(count, module) in statistics.moduleStats" :key="module" class="stat-item">
                <span class="stat-label">{{ module }}:</span>
                <span class="stat-value">{{ count }}</span>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 日志列表 -->
      <el-table
        :data="logs"
        v-loading="loading"
        stripe
        style="width: 100%"
        :default-sort="{ prop: 'operationTime', order: 'descending' }"
      >
        <el-table-column prop="logId" label="日志ID" width="80" />
        <el-table-column prop="operationType" label="操作类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getOperationTypeColor(row.operationType)" size="small">
              {{ getOperationTypeText(row.operationType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="module" label="模块" width="100" />
        <el-table-column prop="objectType" label="对象类型" width="120" />
        <el-table-column prop="objectName" label="对象名称" width="150" show-overflow-tooltip />
        <el-table-column prop="operationDescription" label="操作描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="operatorName" label="操作人" width="100" />
        <el-table-column label="操作时间" width="160" sortable>
          <template #default="{ row }">
            {{ formatDateTime(row.operationTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="operationIp" label="操作IP" width="130" />
        <el-table-column label="操作结果" width="100">
          <template #default="{ row }">
            <el-tag :type="row.operationResult === 'SUCCESS' ? 'success' : 'danger'" size="small">
              {{ row.operationResult === 'SUCCESS' ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="showDetail(row)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="queryForm.pageNum"
        v-model:page-size="queryForm.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        style="margin-top: 20px; justify-content: center"
      />
    </el-card>

    <!-- 日志详情对话框 -->
    <el-dialog v-model="detailVisible" title="日志详情" width="600px">
      <el-descriptions v-if="currentLog" :column="2" border>
        <el-descriptions-item label="日志ID" :span="2">
          {{ currentLog.logId }}
        </el-descriptions-item>
        <el-descriptions-item label="操作类型">
          {{ getOperationTypeText(currentLog.operationType) }}
        </el-descriptions-item>
        <el-descriptions-item label="模块">
          {{ currentLog.module }}
        </el-descriptions-item>
        <el-descriptions-item label="对象类型">
          {{ currentLog.objectType }}
        </el-descriptions-item>
        <el-descriptions-item label="对象ID">
          {{ currentLog.objectId || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="对象名称" :span="2">
          {{ currentLog.objectName || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="操作描述" :span="2">
          {{ currentLog.operationDescription || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="操作内容" :span="2">
          <div class="log-content">{{ currentLog.operationContent || '-' }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="操作结果">
          <el-tag :type="currentLog.operationResult === 'SUCCESS' ? 'success' : 'danger'">
            {{ currentLog.operationResult === 'SUCCESS' ? '成功' : '失败' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="执行时间">
          {{ currentLog.executionTime ? currentLog.executionTime + 'ms' : '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="操作人">
          {{ currentLog.operatorName || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="操作人用户名">
          {{ currentLog.operatorUsername || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="操作时间" :span="2">
          {{ formatDateTime(currentLog.operationTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="操作IP">
          {{ currentLog.operationIp || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="操作地点">
          {{ currentLog.operationLocation || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="请求方法">
          {{ currentLog.requestMethod || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="请求URL" :span="2">
          {{ currentLog.requestUrl || '-' }}
        </el-descriptions-item>
        <el-descriptions-item v-if="currentLog.errorMessage" label="错误信息" :span="2">
          <div class="error-message">{{ currentLog.errorMessage }}</div>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, RefreshLeft, Download, DataAnalysis, SuccessFilled, CircleCloseFilled } from '@element-plus/icons-vue'
import { auditLogApi, type AuditLogQuery, type AuditLogInfo, type AuditLogStatistics } from '@/api'

const loading = ref(false)
const exporting = ref(false)
const showStatistics = ref(false)
const detailVisible = ref(false)

const queryForm = reactive<AuditLogQuery>({
  pageNum: 1,
  pageSize: 20
})

const dateRange = ref<[string, string] | null>(null)
const logs = ref<AuditLogInfo[]>([])
const statistics = ref<AuditLogStatistics | null>(null)
const currentLog = ref<AuditLogInfo | null>(null)
const total = ref(0)

// 查询日志
const handleQuery = async () => {
  loading.value = true
  try {
    // 处理时间范围
    if (dateRange.value) {
      queryForm.startTime = dateRange.value[0]
      queryForm.endTime = dateRange.value[1]
    } else {
      queryForm.startTime = undefined
      queryForm.endTime = undefined
    }

    const { data } = await auditLogApi.queryAuditLogs(queryForm)
    logs.value = data.records
    total.value = data.total

    // 如果显示统计，同时加载统计数据
    if (showStatistics.value) {
      await loadStatistics()
    }
  } catch (error) {
    ElMessage.error('查询日志失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 加载统计数据
const loadStatistics = async () => {
  try {
    const res = await auditLogApi.statisticsAuditLogs(queryForm)
    statistics.value = res.data
  } catch (error) {
    ElMessage.error('加载统计数据失败')
    console.error(error)
  }
}

// 切换统计显示
const toggleStatistics = async () => {
  showStatistics.value = !showStatistics.value
  if (showStatistics.value && !statistics.value) {
    await loadStatistics()
  }
}

// 重置查询条件
const handleReset = () => {
  Object.assign(queryForm, {
    pageNum: 1,
    pageSize: 20,
    operationType: undefined,
    moduleName: undefined,
    operationResult: undefined,
    operatorUsername: undefined,
    keyword: undefined
  })
  dateRange.value = null
  handleQuery()
}

// 显示日志详情
const showDetail = (log: AuditLogInfo) => {
  currentLog.value = log
  detailVisible.value = true
}

// 导出日志
const exportLogs = async () => {
  exporting.value = true
  try {
    const res = await auditLogApi.exportAuditLogs(queryForm)
    const filePath = res.data?.filePath || res.data
    ElMessage.success(`日志导出成功，文件路径：${filePath}`)
  } catch (error) {
    ElMessage.error('导出日志失败')
    console.error(error)
  } finally {
    exporting.value = false
  }
}

// 分页大小改变
const handleSizeChange = (size: number) => {
  queryForm.pageSize = size
  handleQuery()
}

// 当前页改变
const handleCurrentChange = (page: number) => {
  queryForm.pageNum = page
  handleQuery()
}

// 格式化日期时间
const formatDateTime = (dateTime: string) => {
  if (!dateTime) return '-'
  const date = new Date(dateTime)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 获取操作类型颜色
const getOperationTypeColor = (type: string) => {
  const colorMap: Record<string, string> = {
    LOGIN: 'success',
    LOGOUT: 'info',
    CREATE: 'primary',
    UPDATE: 'warning',
    DELETE: 'danger',
    QUERY: 'info',
    APPROVE: 'success'
  }
  return colorMap[type] || 'info'
}

// 获取操作类型文本
const getOperationTypeText = (type: string) => {
  const textMap: Record<string, string> = {
    LOGIN: '登录',
    LOGOUT: '登出',
    CREATE: '创建',
    UPDATE: '更新',
    DELETE: '删除',
    QUERY: '查询',
    APPROVE: '审批'
  }
  return textMap[type] || type
}

onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.audit-log-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.query-form {
  margin-bottom: 20px;
}

.statistics-section {
  margin-bottom: 20px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 4px;
}

.chart-container {
  padding: 15px;
  background: white;
  border-radius: 4px;
}

.chart-container h4 {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #606266;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #ebeef5;
}

.stat-item:last-child {
  border-bottom: none;
}

.stat-label {
  color: #909399;
}

.stat-value {
  font-weight: bold;
  color: #303133;
}

.log-content {
  max-height: 200px;
  overflow-y: auto;
  white-space: pre-wrap;
  word-break: break-all;
  font-size: 12px;
  color: #606266;
  background: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
}

.error-message {
  color: #f56c6c;
  background: #fef0f0;
  padding: 10px;
  border-radius: 4px;
  font-size: 12px;
}

:deep(.el-pagination) {
  display: flex;
}
</style>
