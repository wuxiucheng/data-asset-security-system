<template>
  <div class="approval-task-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>审批任务管理</span>
          <el-badge :value="pendingCount" class="item">
            <el-button type="warning">待审批</el-button>
          </el-badge>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="任务名称">
          <el-input v-model="searchForm.name" placeholder="请输入任务名称" clearable />
        </el-form-item>
        <el-form-item label="任务状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 150px">
            <el-option label="全部" value="" />
            <el-option label="待审批" value="pending" />
            <el-option label="已同意" value="approved" />
            <el-option label="已拒绝" value="rejected" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="processName" label="流程名称" width="180" />
        <el-table-column prop="title" label="任务标题" width="200" />
        <el-table-column prop="initiator" label="申请人" width="100" />
        <el-table-column prop="node" label="审批节点" width="120" />
        <el-table-column prop="priority" label="优先级" width="100">
          <template #default="{ row }">
            <el-tag :type="getPriorityTag(row.priority)">{{ getPriorityName(row.priority) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="任务状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)">{{ getStatusName(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button type="success" link @click="handleApprove(row)" v-if="row.status === 'pending'">同意</el-button>
            <el-button type="danger" link @click="handleReject(row)" v-if="row.status === 'pending'">拒绝</el-button>
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
    <el-dialog v-model="viewDialogVisible" title="任务详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="流程名称">{{ currentTask.processName }}</el-descriptions-item>
        <el-descriptions-item label="任务标题">{{ currentTask.title }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ currentTask.initiator }}</el-descriptions-item>
        <el-descriptions-item label="审批节点">{{ currentTask.node }}</el-descriptions-item>
        <el-descriptions-item label="优先级">
          <el-tag :type="getPriorityTag(currentTask.priority)">{{ getPriorityName(currentTask.priority) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="任务状态">
          <el-tag :type="getStatusTag(currentTask.status)">{{ getStatusName(currentTask.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="申请说明" :span="2">{{ currentTask.description }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 审批对话框 -->
    <el-dialog v-model="approveDialogVisible" title="审批" width="500px">
      <el-form :model="approveForm" label-width="80px">
        <el-form-item label="审批结果">
          <el-radio-group v-model="approveForm.result">
            <el-radio value="approved">同意</el-radio>
            <el-radio value="rejected">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审批意见">
          <el-input v-model="approveForm.comment" type="textarea" :rows="3" placeholder="请输入审批意见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approveDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitApprove">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

defineOptions({
  name: 'ApprovalTask'
})

// 搜索表单
const searchForm = reactive({
  name: '',
  status: ''
})

// 表格数据
const tableData = ref([
  {
    id: 1,
    processName: '数据分类审批流程',
    title: '客户数据分类标准发布申请',
    initiator: '张三',
    node: '部门经理审批',
    priority: 'high',
    status: 'pending',
    description: '申请发布客户数据分类标准，包含客户基本信息、联系方式等分类规则',
    createTime: '2026-05-28 10:00:00'
  },
  {
    id: 2,
    processName: '资产导入审批流程',
    title: '批量导入数据资产申请',
    initiator: '李四',
    node: '数据管理员审批',
    priority: 'normal',
    status: 'pending',
    description: '申请批量导入1000条数据资产记录',
    createTime: '2026-05-28 11:00:00'
  },
  {
    id: 3,
    processName: '数据分级审批流程',
    title: '用户数据分级标准发布申请',
    initiator: '王五',
    node: '部门经理审批',
    priority: 'low',
    status: 'approved',
    description: '申请发布用户数据分级标准',
    createTime: '2026-05-27 14:00:00'
  }
])

const loading = ref(false)
const pagination = reactive({
  current: 1,
  size: 10,
  total: 3
})

// 待审批数量
const pendingCount = computed(() => tableData.value.filter(item => item.status === 'pending').length)

// 查看详情
const viewDialogVisible = ref(false)
const currentTask = ref<any>({})

// 审批
const approveDialogVisible = ref(false)
const approveForm = reactive({
  result: 'approved',
  comment: ''
})
const currentApproveTask = ref<any>({})

// 映射
const priorityMap: Record<string, string> = {
  high: '高',
  normal: '中',
  low: '低'
}

const statusMap: Record<string, string> = {
  pending: '待审批',
  approved: '已同意',
  rejected: '已拒绝'
}

const getPriorityName = (priority: string) => priorityMap[priority] || priority
const getStatusName = (status: string) => statusMap[status] || status

const getPriorityTag = (priority: string) => {
  const tagMap: Record<string, string> = {
    high: 'danger',
    normal: 'warning',
    low: 'info'
  }
  return tagMap[priority] || ''
}

const getStatusTag = (status: string) => {
  const tagMap: Record<string, string> = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger'
  }
  return tagMap[status] || ''
}

// 搜索
const handleSearch = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
  }, 500)
}

const handleReset = () => {
  searchForm.name = ''
  searchForm.status = ''
  handleSearch()
}

// 查看
const handleView = (row: any) => {
  currentTask.value = row
  viewDialogVisible.value = true
}

// 同意
const handleApprove = (row: any) => {
  currentApproveTask.value = row
  approveForm.result = 'approved'
  approveForm.comment = ''
  approveDialogVisible.value = true
}

// 拒绝
const handleReject = (row: any) => {
  currentApproveTask.value = row
  approveForm.result = 'rejected'
  approveForm.comment = ''
  approveDialogVisible.value = true
}

// 提交审批
const handleSubmitApprove = async () => {
  try {
    const task = currentApproveTask.value
    task.status = approveForm.result

    if (approveForm.result === 'approved') {
      ElMessage.success('审批通过')
    } else {
      ElMessage.success('审批拒绝')
    }

    approveDialogVisible.value = false
  } catch (error) {}
}
</script>

<style scoped lang="scss">
.approval-task-container {
  padding: 20px;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .search-form {
    margin-bottom: 20px;
  }
}
</style>
