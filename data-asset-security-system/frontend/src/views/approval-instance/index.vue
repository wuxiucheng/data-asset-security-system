<template>
  <div class="approval-instance-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>流程实例管理</span>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="流程名称">
          <el-input v-model="searchForm.name" placeholder="请输入流程名称" clearable />
        </el-form-item>
        <el-form-item label="实例状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 150px">
            <el-option label="全部" value="" />
            <el-option label="进行中" value="running" />
            <el-option label="已完成" value="completed" />
            <el-option label="已取消" value="cancelled" />
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
        <el-table-column prop="title" label="实例标题" width="200" />
        <el-table-column prop="initiator" label="发起人" width="100" />
        <el-table-column prop="currentNode" label="当前节点" width="120" />
        <el-table-column prop="status" label="实例状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)">{{ getStatusName(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="progress" label="进度" width="180">
          <template #default="{ row }">
            <el-progress :percentage="row.progress" :status="row.status === 'completed' ? 'success' : undefined" />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发起时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button type="primary" link @click="handleTimeline(row)">流程图</el-button>
            <el-button type="danger" link @click="handleCancel(row)" v-if="row.status === 'running'">取消</el-button>
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
    <el-dialog v-model="viewDialogVisible" title="实例详情" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="流程名称">{{ currentInstance.processName }}</el-descriptions-item>
        <el-descriptions-item label="实例标题">{{ currentInstance.title }}</el-descriptions-item>
        <el-descriptions-item label="发起人">{{ currentInstance.initiator }}</el-descriptions-item>
        <el-descriptions-item label="当前节点">{{ currentInstance.currentNode }}</el-descriptions-item>
        <el-descriptions-item label="实例状态">
          <el-tag :type="getStatusTag(currentInstance.status)">{{ getStatusName(currentInstance.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发起时间">{{ currentInstance.createTime }}</el-descriptions-item>
      </el-descriptions>

      <el-divider content-position="left">审批记录</el-divider>
      <el-timeline>
        <el-timeline-item
          v-for="record in approvalRecords"
          :key="record.id"
          :timestamp="record.time"
          :type="record.type"
        >
          <el-card>
            <h4>{{ record.node }}</h4>
            <p>审批人：{{ record.approver }}</p>
            <p>审批结果：{{ record.result }}</p>
            <p v-if="record.comment">审批意见：{{ record.comment }}</p>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </el-dialog>

    <!-- 流程图对话框 -->
    <el-dialog v-model="timelineDialogVisible" title="流程图" width="900px">
      <el-alert type="info" :closable="false" style="margin-bottom: 20px">
        <template #title>流程图可视化开发中</template>
        <p style="margin-top: 8px">将支持可视化展示流程实例的执行路径和当前节点。</p>
      </el-alert>
      <el-empty description="流程图开发中" />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

defineOptions({
  name: 'ApprovalInstance'
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
    currentNode: '部门经理审批',
    status: 'running',
    progress: 50,
    createTime: '2026-05-28 10:00:00'
  },
  {
    id: 2,
    processName: '数据分级审批流程',
    title: '用户数据分级标准发布申请',
    initiator: '李四',
    currentNode: '已完成',
    status: 'completed',
    progress: 100,
    createTime: '2026-05-27 14:00:00'
  }
])

const loading = ref(false)
const pagination = reactive({
  current: 1,
  size: 10,
  total: 2
})

// 查看详情
const viewDialogVisible = ref(false)
const currentInstance = ref<any>({})
const approvalRecords = ref([
  {
    id: 1,
    node: '发起申请',
    approver: '张三',
    result: '提交',
    comment: '申请发布客户数据分类标准',
    time: '2026-05-28 10:00:00',
    type: 'primary'
  },
  {
    id: 2,
    node: '部门经理审批',
    approver: '王经理',
    result: '同意',
    comment: '同意发布',
    time: '2026-05-28 11:00:00',
    type: 'success'
  }
])

// 流程图
const timelineDialogVisible = ref(false)

// 映射
const statusMap: Record<string, string> = {
  running: '进行中',
  completed: '已完成',
  cancelled: '已取消'
}

const getStatusName = (status: string) => statusMap[status] || status

const getStatusTag = (status: string) => {
  const tagMap: Record<string, string> = {
    running: 'warning',
    completed: 'success',
    cancelled: 'info'
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
  currentInstance.value = row
  viewDialogVisible.value = true
}

// 流程图
const handleTimeline = (row: any) => {
  timelineDialogVisible.value = true
}

// 取消
const handleCancel = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要取消该流程实例吗？', '提示', { type: 'warning' })
    row.status = 'cancelled'
    ElMessage.success('流程已取消')
  } catch (error) {}
}
</script>

<style scoped lang="scss">
.approval-instance-container {
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
