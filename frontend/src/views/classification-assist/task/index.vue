<template>
  <div class="classification-assist-task">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>执行任务管理</span>
          <el-button type="primary" @click="handleAdd">新建任务</el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="任务名称">
          <el-input v-model="searchForm.taskName" placeholder="请输入任务名称" clearable style="width: 250px"/>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 150px">
            <el-option label="待执行" value="PENDING" />
            <el-option label="执行中" value="RUNNING" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="失败" value="FAILED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="taskId" label="任务ID" width="80" />
        <el-table-column prop="taskName" label="任务名称" />
        <el-table-column prop="taskType" label="任务类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.taskType === 'MANUAL'" type="primary">手动</el-tag>
            <el-tag v-else-if="row.taskType === 'SCHEDULED'" type="success">定时</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="scopeType" label="执行范围" width="100">
          <template #default="{ row }">
            <span v-if="row.scopeType === 'ALL'">全部资产</span>
            <span v-else-if="row.scopeType === 'DATASOURCE'">指定数据源</span>
            <span v-else-if="row.scopeType === 'ASSET'">指定资产</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'PENDING'" type="info">待执行</el-tag>
            <el-tag v-else-if="row.status === 'RUNNING'" type="warning">执行中</el-tag>
            <el-tag v-else-if="row.status === 'COMPLETED'" type="success">已完成</el-tag>
            <el-tag v-else-if="row.status === 'FAILED'" type="danger">失败</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="matchedCount" label="匹配数" width="100" />
        <el-table-column prop="createdTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="250">
          <template #default="{ row }">
            <el-button v-if="row.status === 'PENDING'" type="primary" size="small" @click="handleExecute(row)">
              执行
            </el-button>
            <el-button v-if="row.status === 'RUNNING'" type="warning" size="small" @click="handleCancel(row)">
              取消
            </el-button>
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
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

    <!-- 新建/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" label-width="120px">
        <el-form-item label="任务名称">
          <el-input v-model="form.taskName" placeholder="请输入任务名称" />
        </el-form-item>
        <el-form-item label="任务类型">
          <el-radio-group v-model="form.taskType">
            <el-radio label="MANUAL">手动执行</el-radio>
            <el-radio label="SCHEDULED">定时执行</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="执行范围">
          <el-select v-model="form.scopeType" placeholder="请选择执行范围">
            <el-option label="全部资产" value="ALL" />
            <el-option label="指定数据源" value="DATASOURCE" />
            <el-option label="指定资产" value="ASSET" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.taskType === 'SCHEDULED'" label="Cron表达式">
          <el-input v-model="form.cronExpression" placeholder="请输入Cron表达式" />
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
import { classificationAssistTaskApi } from '@/api'

const searchForm = reactive({
  taskName: '',
  status: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const tableData = ref<any[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('新建任务')
const form = reactive({
  taskId: null,
  taskName: '',
  taskType: 'MANUAL',
  scopeType: 'ALL',
  scopeConfig: '',
  ruleIds: '',
  cronExpression: ''
})

const loadData = async () => {
  try {
    const res = await classificationAssistTaskApi.getList({
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

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.taskName = ''
  searchForm.status = ''
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新建任务'
  Object.assign(form, {
    taskId: null,
    taskName: '',
    taskType: 'MANUAL',
    scopeType: 'ALL',
    scopeConfig: '',
    ruleIds: '',
    cronExpression: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑任务'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    const api = form.taskId ? classificationAssistTaskApi.update : classificationAssistTaskApi.create
    const res = await api(form)
    if (res.data.code === 200) {
      ElMessage.success('操作成功')
      dialogVisible.value = false
      loadData()
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定删除该任务吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      const res = await classificationAssistTaskApi.delete(row.taskId)
      if (res.data.code === 200) {
        ElMessage.success('删除成功')
        loadData()
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

const handleExecute = async (row: any) => {
  try {
    const res = await classificationAssistTaskApi.execute(row.taskId)
    if (res.data.code === 200) {
      ElMessage.success('任务已开始执行')
      loadData()
    }
  } catch (error) {
    ElMessage.error('执行失败')
  }
}

const handleCancel = async (row: any) => {
  try {
    const res = await classificationAssistTaskApi.cancel(row.taskId)
    if (res.data.code === 200) {
      ElMessage.success('任务已取消')
      loadData()
    }
  } catch (error) {
    ElMessage.error('取消失败')
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.classification-assist-task {
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

.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
