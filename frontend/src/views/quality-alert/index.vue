<template>
  <div class="quality-alert-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <div>
            <span>质量告警管理</span>
            <el-badge :value="pendingCount" :max="99" class="badge-item" v-if="pendingCount > 0">
              <el-button type="warning" size="small" @click="handleShowPending">待处理</el-button>
            </el-badge>
          </div>
          <el-button type="primary" @click="handleBatchResolve" :disabled="selectedAlerts.length === 0">
            批量解决
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="探查任务">
          <el-input v-model.number="searchForm.taskId" placeholder="请输入任务ID" clearable type="number" />
        </el-form-item>
        <el-form-item label="告警级别">
          <el-select v-model="searchForm.alertLevel" placeholder="请选择" clearable style="width: 150px">
            <el-option label="全部" value="" />
            <el-option label="信息" value="INFO" />
            <el-option label="警告" value="WARNING" />
            <el-option label="错误" value="ERROR" />
            <el-option label="严重" value="CRITICAL" />
          </el-select>
        </el-form-item>
        <el-form-item label="告警状态">
          <el-select v-model="searchForm.alertStatus" placeholder="请选择" clearable style="width: 150px">
            <el-option label="全部" value="" />
            <el-option label="待处理" value="PENDING" />
            <el-option label="处理中" value="PROCESSING" />
            <el-option label="已解决" value="RESOLVED" />
            <el-option label="已忽略" value="IGNORED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table 
        :data="tableData" 
        border 
        stripe 
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="alertId" label="ID" width="80" />
        <el-table-column prop="taskId" label="任务ID" width="100" />
        <el-table-column prop="alertLevel" label="级别" width="100">
          <template #default="{ row }">
            <el-tag :type="getLevelType(row.alertLevel)">
              {{ getLevelName(row.alertLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="alertTitle" label="告警标题" width="200" show-overflow-tooltip />
        <el-table-column prop="alertContent" label="告警内容" show-overflow-tooltip />
        <el-table-column prop="alertStatus" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.alertStatus)">
              {{ getStatusName(row.alertStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="assigneeId" label="处理人" width="100">
          <template #default="{ row }">
            {{ row.assigneeId || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button v-if="row.alertStatus === 'PENDING'" type="warning" link @click="handleAssign(row)">分配</el-button>
            <el-button v-if="row.alertStatus !== 'RESOLVED' && row.alertStatus !== 'IGNORED'" type="success" link @click="handleResolve(row)">解决</el-button>
            <el-button v-if="row.alertStatus !== 'RESOLVED' && row.alertStatus !== 'IGNORED'" type="info" link @click="handleIgnore(row)">忽略</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSearch"
        @current-change="handleSearch"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 查看详情对话框 -->
    <el-dialog v-model="viewDialogVisible" title="告警详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="告警ID">{{ currentAlert.alertId }}</el-descriptions-item>
        <el-descriptions-item label="任务ID">{{ currentAlert.taskId }}</el-descriptions-item>
        <el-descriptions-item label="资产ID">{{ currentAlert.assetId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="字段ID">{{ currentAlert.fieldId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="规则ID">{{ currentAlert.ruleId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="告警类型">{{ currentAlert.alertType }}</el-descriptions-item>
        <el-descriptions-item label="告警级别">
          <el-tag :type="getLevelType(currentAlert.alertLevel)">
            {{ getLevelName(currentAlert.alertLevel) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="告警状态">
          <el-tag :type="getStatusType(currentAlert.alertStatus)">
            {{ getStatusName(currentAlert.alertStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="告警标题" :span="2">{{ currentAlert.alertTitle }}</el-descriptions-item>
        <el-descriptions-item label="告警内容" :span="2">{{ currentAlert.alertContent }}</el-descriptions-item>
        <el-descriptions-item label="处理人">{{ currentAlert.assigneeId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentAlert.createdTime }}</el-descriptions-item>
        <el-descriptions-item label="解决时间">{{ currentAlert.resolvedTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="解决备注">{{ currentAlert.resolvedRemark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 分配对话框 -->
    <el-dialog v-model="assignDialogVisible" title="分配告警" width="400px">
      <el-form :model="assignForm" label-width="100px">
        <el-form-item label="处理人ID">
          <el-input v-model.number="assignForm.assigneeId" placeholder="请输入处理人ID" type="number" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitAssign">确定</el-button>
      </template>
    </el-dialog>

    <!-- 解决/忽略对话框 -->
    <el-dialog v-model="resolveDialogVisible" :title="resolveDialogTitle" width="500px">
      <el-form :model="resolveForm" label-width="100px">
        <el-form-item label="备注">
          <el-input v-model="resolveForm.remark" type="textarea" :rows="4" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resolveDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitResolve">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

// 数据定义
const loading = ref(false)
const tableData = ref<any[]>([])
const pendingCount = ref(0)
const selectedAlerts = ref<any[]>([])
const viewDialogVisible = ref(false)
const assignDialogVisible = ref(false)
const resolveDialogVisible = ref(false)
const resolveDialogTitle = ref('解决告警')
const currentAlert = ref<any>({})

const searchForm = reactive({
  taskId: null as number | null,
  alertLevel: '',
  alertStatus: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const assignForm = reactive({
  alertId: null as number | null,
  assigneeId: null as number | null
})

const resolveForm = reactive({
  alertId: null as number | null,
  remark: '',
  action: '' // 'resolve' or 'ignore'
})

// 方法
const loadPendingCount = async () => {
  try {
    const res = await request.get('/quality-probe/alert/pending-count')
    if (res.data !== undefined) {
      pendingCount.value = res.data
    }
  } catch (error) {
    console.error('加载待处理数量失败:', error)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const params: any = {
      current: pagination.current,
      size: pagination.size
    }
    if (searchForm.taskId) params.taskId = searchForm.taskId
    if (searchForm.alertLevel) params.alertLevel = searchForm.alertLevel
    if (searchForm.alertStatus) params.alertStatus = searchForm.alertStatus

    const res = await request.get('/quality-probe/alert/page', { params })
    if (res.data) {
      tableData.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const getLevelType = (level: string) => {
  const map: any = {
    'INFO': 'info',
    'WARNING': 'warning',
    'ERROR': 'danger',
    'CRITICAL': 'danger'
  }
  return map[level] || 'info'
}

const getLevelName = (level: string) => {
  const map: any = {
    'INFO': '信息',
    'WARNING': '警告',
    'ERROR': '错误',
    'CRITICAL': '严重'
  }
  return map[level] || level
}

const getStatusType = (status: string) => {
  const map: any = {
    'PENDING': 'warning',
    'PROCESSING': 'primary',
    'RESOLVED': 'success',
    'IGNORED': 'info'
  }
  return map[status] || 'info'
}

const getStatusName = (status: string) => {
  const map: any = {
    'PENDING': '待处理',
    'PROCESSING': '处理中',
    'RESOLVED': '已解决',
    'IGNORED': '已忽略'
  }
  return map[status] || status
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.taskId = null
  searchForm.alertLevel = ''
  searchForm.alertStatus = ''
  handleSearch()
}

const handleShowPending = () => {
  searchForm.alertStatus = 'PENDING'
  handleSearch()
}

const handleSelectionChange = (selection: any[]) => {
  selectedAlerts.value = selection
}

const handleView = (row: any) => {
  currentAlert.value = { ...row }
  viewDialogVisible.value = true
}

const handleAssign = (row: any) => {
  assignForm.alertId = row.alertId
  assignForm.assigneeId = null
  assignDialogVisible.value = true
}

const handleSubmitAssign = async () => {
  if (!assignForm.assigneeId) {
    ElMessage.warning('请输入处理人ID')
    return
  }
  try {
    await request.put(`/quality-probe/alert/${assignForm.alertId}/assign`, null, {
      params: { assigneeId: assignForm.assigneeId }
    })
    ElMessage.success('分配成功')
    assignDialogVisible.value = false
    loadData()
    loadPendingCount()
  } catch (error) {
    ElMessage.error('分配失败')
  }
}

const handleResolve = (row: any) => {
  resolveForm.alertId = row.alertId
  resolveForm.remark = ''
  resolveForm.action = 'resolve'
  resolveDialogTitle.value = '解决告警'
  resolveDialogVisible.value = true
}

const handleIgnore = (row: any) => {
  resolveForm.alertId = row.alertId
  resolveForm.remark = ''
  resolveForm.action = 'ignore'
  resolveDialogTitle.value = '忽略告警'
  resolveDialogVisible.value = true
}

const handleSubmitResolve = async () => {
  try {
    const url = resolveForm.action === 'resolve' 
      ? `/quality-probe/alert/${resolveForm.alertId}/resolve`
      : `/quality-probe/alert/${resolveForm.alertId}/ignore`
    
    await request.put(url, null, {
      params: { resolvedRemark: resolveForm.remark }
    })
    ElMessage.success(resolveForm.action === 'resolve' ? '解决成功' : '忽略成功')
    resolveDialogVisible.value = false
    loadData()
    loadPendingCount()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleBatchResolve = async () => {
  try {
    await ElMessageBox.confirm(`确定要批量解决选中的 ${selectedAlerts.value.length} 个告警吗？`, '提示', {
      type: 'warning'
    })
    
    const alertIds = selectedAlerts.value.map(a => a.alertId)
    await request.put('/quality-probe/alert/batch-resolve', alertIds)
    ElMessage.success('批量解决成功')
    loadData()
    loadPendingCount()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量解决失败')
    }
  }
}

// 生命周期
onMounted(() => {
  loadData()
  loadPendingCount()
})
</script>

<style scoped lang="scss">
.quality-alert-container {
  padding: 20px;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .badge-item {
      margin-left: 10px;
    }
  }

  .search-form {
    margin-bottom: 20px;
  }
}
</style>
