<template>
  <div class="classification-assist-result">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>结果审核</span>
          <el-badge :value="pendingCount" class="badge-item">
            <el-button type="primary">待审核</el-button>
          </el-badge>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 150px">
            <el-option label="待审核" value="PENDING" />
            <el-option label="已批准" value="APPROVED" />
            <el-option label="已拒绝" value="REJECTED" />
          </el-select>
        </el-form-item>
        <el-form-item label="规则">
          <el-select v-model="searchForm.ruleId" placeholder="请选择规则" clearable style="width: 150px">
            <el-option v-for="rule in rules" :key="rule.ruleId" :label="rule.ruleName" :value="rule.ruleId" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 批量操作 -->
      <div class="batch-actions" v-if="selectedRows.length > 0">
        <el-button type="success" @click="handleBatchApprove">批量批准 ({{ selectedRows.length }})</el-button>
        <el-button type="danger" @click="handleBatchReject">批量拒绝 ({{ selectedRows.length }})</el-button>
      </div>

      <!-- 表格 -->
      <el-table :data="tableData" border style="width: 100%" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="resultId" label="结果ID" width="80" />
        <el-table-column prop="ruleId" label="规则" width="150">
          <template #default="{ row }">
            {{ getRuleName(row.ruleId) }}
          </template>
        </el-table-column>
        <el-table-column prop="matchType" label="匹配类型" width="100" />
        <el-table-column prop="matchValue" label="匹配值" width="150" />
        <el-table-column prop="suggestGradingId" label="建议分级" width="100">
          <template #default="{ row }">
            <el-tag type="warning">L{{ row.suggestGradingId }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'PENDING'" type="info">待审核</el-tag>
            <el-tag v-else-if="row.status === 'APPROVED'" type="success">已批准</el-tag>
            <el-tag v-else-if="row.status === 'REJECTED'" type="danger">已拒绝</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="executeTime" label="执行时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button v-if="row.status === 'PENDING'" type="success" size="small" @click="handleApprove(row)">
              批准
            </el-button>
            <el-button v-if="row.status === 'PENDING'" type="danger" size="small" @click="handleReject(row)">
              拒绝
            </el-button>
            <el-button type="primary" size="small" @click="handleView(row)">查看</el-button>
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
      />
    </el-card>

    <!-- 审核对话框 -->
    <el-dialog v-model="dialogVisible" title="审核" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="审核意见">
          <el-input v-model="form.comment" type="textarea" :rows="3" placeholder="请输入审核意见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { classificationAssistResultApi, classificationAssistRuleApi } from '@/api'

const searchForm = reactive({
  status: 'PENDING',
  ruleId: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const tableData = ref<any[]>([])
const rules = ref<any[]>([])
const selectedRows = ref<any[]>([])
const pendingCount = ref(0)
const dialogVisible = ref(false)
const form = reactive({
  resultId: null,
  action: '',
  comment: ''
})

const loadData = async () => {
  try {
    const res = await classificationAssistResultApi.getList({
      current: pagination.current,
      size: pagination.size,
      ...searchForm
    })
    if (res.data.code === 200) {
      tableData.value = res.data.data.records
      pagination.total = res.data.data.total
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

const loadRules = async () => {
  try {
    const res = await classificationAssistRuleApi.getActiveRules()
    if (res.data.code === 200) {
      rules.value = res.data.data
    }
  } catch (error) {
    console.error('加载规则失败')
  }
}

const loadPendingCount = async () => {
  try {
    const res = await classificationAssistResultApi.getPendingCount()
    if (res.data.code === 200) {
      pendingCount.value = res.data.data
    }
  } catch (error) {
    console.error('加载待审核数量失败')
  }
}

const getRuleName = (ruleId: number) => {
  const rule = rules.value.find(r => r.ruleId === ruleId)
  return rule ? rule.ruleName : ruleId
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.status = 'PENDING'
  searchForm.ruleId = null
  handleSearch()
}

const handleSelectionChange = (rows: any[]) => {
  selectedRows.value = rows
}

const handleApprove = (row: any) => {
  form.resultId = row.resultId
  form.action = 'APPROVE'
  form.comment = ''
  dialogVisible.value = true
}

const handleReject = (row: any) => {
  form.resultId = row.resultId
  form.action = 'REJECT'
  form.comment = ''
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    const api = form.action === 'APPROVE' 
      ? classificationAssistResultApi.approve 
      : classificationAssistResultApi.reject
    const res = await api(form.resultId!, form.comment)
    if (res.data.code === 200) {
      ElMessage.success('审核成功')
      dialogVisible.value = false
      loadData()
      loadPendingCount()
    }
  } catch (error) {
    ElMessage.error('审核失败')
  }
}

const handleView = (row: any) => {
  ElMessage.info('查看详情功能待实现')
}

const handleBatchApprove = () => {
  ElMessageBox.prompt('请输入审核意见', '批量批准', {
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(async ({ value }) => {
    try {
      const resultIds = selectedRows.value.map(r => r.resultId)
      const res = await classificationAssistResultApi.batchReview(resultIds, 'APPROVED', value)
      if (res.data.code === 200) {
        ElMessage.success('批量审核成功')
        loadData()
        loadPendingCount()
      }
    } catch (error) {
      ElMessage.error('批量审核失败')
    }
  })
}

const handleBatchReject = () => {
  ElMessageBox.prompt('请输入审核意见', '批量拒绝', {
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(async ({ value }) => {
    try {
      const resultIds = selectedRows.value.map(r => r.resultId)
      const res = await classificationAssistResultApi.batchReview(resultIds, 'REJECTED', value)
      if (res.data.code === 200) {
        ElMessage.success('批量审核成功')
        loadData()
        loadPendingCount()
      }
    } catch (error) {
      ElMessage.error('批量审核失败')
    }
  })
}

onMounted(() => {
  loadData()
  loadRules()
  loadPendingCount()
})
</script>

<style scoped>
.classification-assist-result {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.batch-actions {
  margin-bottom: 20px;
}

.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}

.badge-item {
  margin-left: 10px;
}
</style>
