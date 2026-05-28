<template>
  <div class="quality-task-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>质量任务管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新建任务
          </el-button>
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
            <el-option label="待执行" value="pending" />
            <el-option label="执行中" value="running" />
            <el-option label="已完成" value="completed" />
            <el-option label="失败" value="failed" />
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
        <el-table-column prop="name" label="任务名称" width="200" />
        <el-table-column prop="ruleCount" label="检查规则数" width="120">
          <template #default="{ row }">
            <el-tag>{{ row.ruleCount }} 条</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="targetScope" label="检查范围" width="150" />
        <el-table-column prop="status" label="任务状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)">{{ getStatusName(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="progress" label="执行进度" width="180">
          <template #default="{ row }">
            <el-progress
              :percentage="row.progress"
              :status="row.status === 'failed' ? 'exception' : row.status === 'completed' ? 'success' : undefined"
            />
          </template>
        </el-table-column>
        <el-table-column prop="result" label="检查结果" width="150">
          <template #default="{ row }">
            <span v-if="row.status === 'completed'">
              通过 {{ row.passCount }} / 失败 {{ row.failCount }}
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              link
              @click="handleExecute(row)"
              v-if="row.status === 'pending'"
            >
              执行
            </el-button>
            <el-button
              type="warning"
              link
              @click="handleStop(row)"
              v-if="row.status === 'running'"
            >
              停止
            </el-button>
            <el-button type="primary" link @click="handleViewResult(row)" v-if="row.status === 'completed'">
              查看结果
            </el-button>
            <el-button type="primary" link @click="handleEdit(row)" v-if="row.status === 'pending'">
              编辑
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
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

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="任务名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入任务名称" />
        </el-form-item>
        <el-form-item label="检查规则" prop="rules">
          <el-select v-model="form.rules" multiple placeholder="请选择检查规则" style="width: 100%">
            <el-option label="用户名非空检查" value="1" />
            <el-option label="用户ID唯一性检查" value="2" />
            <el-option label="邮箱格式检查" value="3" />
            <el-option label="手机号格式检查" value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="检查范围" prop="targetScope">
          <el-select v-model="form.targetScope" placeholder="请选择检查范围" style="width: 100%">
            <el-option label="全库" value="all" />
            <el-option label="用户表" value="user" />
            <el-option label="资产表" value="asset" />
            <el-option label="自定义" value="custom" />
          </el-select>
        </el-form-item>
        <el-form-item label="执行方式" prop="executeType">
          <el-radio-group v-model="form.executeType">
            <el-radio value="immediate">立即执行</el-radio>
            <el-radio value="scheduled">定时执行</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="执行时间" prop="scheduleTime" v-if="form.executeType === 'scheduled'">
          <el-date-picker
            v-model="form.scheduleTime"
            type="datetime"
            placeholder="选择执行时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 结果查看对话框 -->
    <el-dialog v-model="resultDialogVisible" title="检查结果详情" width="800px">
      <el-table :data="resultData" border stripe max-height="400">
        <el-table-column prop="ruleName" label="规则名称" width="180" />
        <el-table-column prop="target" label="检查对象" width="150" />
        <el-table-column prop="result" label="检查结果" width="100">
          <template #default="{ row }">
            <el-tag :type="row.result === 'pass' ? 'success' : 'danger'">
              {{ row.result === 'pass' ? '通过' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="message" label="详细信息" show-overflow-tooltip />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

defineOptions({
  name: 'QualityTask'
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
    name: '用户数据质量检查',
    ruleCount: 4,
    targetScope: '用户表',
    status: 'completed',
    progress: 100,
    passCount: 3,
    failCount: 1,
    createTime: '2026-05-28 10:00:00'
  },
  {
    id: 2,
    name: '资产数据质量检查',
    ruleCount: 3,
    targetScope: '资产表',
    status: 'running',
    progress: 60,
    passCount: 0,
    failCount: 0,
    createTime: '2026-05-28 11:00:00'
  },
  {
    id: 3,
    name: '全库质量检查',
    ruleCount: 10,
    targetScope: '全库',
    status: 'pending',
    progress: 0,
    passCount: 0,
    failCount: 0,
    createTime: '2026-05-28 12:00:00'
  }
])

const loading = ref(false)
const pagination = reactive({
  current: 1,
  size: 10,
  total: 3
})

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('新建任务')
const formRef = ref()
const form = reactive({
  id: null,
  name: '',
  rules: [] as string[],
  targetScope: 'all',
  executeType: 'immediate',
  scheduleTime: '',
  description: ''
})

const rules = {
  name: [{ required: true, message: '请输入任务名称', trigger: 'blur' }],
  rules: [{ required: true, message: '请选择检查规则', trigger: 'change', type: 'array' }],
  targetScope: [{ required: true, message: '请选择检查范围', trigger: 'change' }]
}

// 结果对话框
const resultDialogVisible = ref(false)
const resultData = ref([
  {
    ruleName: '用户名非空检查',
    target: 'user.username',
    result: 'pass',
    message: '检查通过，共检查 1000 条记录'
  },
  {
    ruleName: '用户ID唯一性检查',
    target: 'user.user_id',
    result: 'pass',
    message: '检查通过，未发现重复ID'
  },
  {
    ruleName: '邮箱格式检查',
    target: 'user.email',
    result: 'fail',
    message: '发现 5 条记录邮箱格式不正确'
  },
  {
    ruleName: '手机号格式检查',
    target: 'user.phone',
    result: 'pass',
    message: '检查通过，共检查 800 条记录'
  }
])

// 状态映射
const statusMap: Record<string, string> = {
  pending: '待执行',
  running: '执行中',
  completed: '已完成',
  failed: '失败'
}

const getStatusName = (status: string) => statusMap[status] || status

const getStatusTag = (status: string) => {
  const tagMap: Record<string, string> = {
    pending: 'info',
    running: 'warning',
    completed: 'success',
    failed: 'danger'
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

// 新增
const handleAdd = () => {
  dialogTitle.value = '新建任务'
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑任务'
  dialogVisible.value = true
}

// 执行任务
const handleExecute = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要执行该任务吗？', '提示', {
      type: 'warning'
    })

    row.status = 'running'
    ElMessage.success('任务已开始执行')

    // 模拟执行过程
    const timer = setInterval(() => {
      if (row.progress < 100) {
        row.progress += 10
      } else {
        clearInterval(timer)
        row.status = 'completed'
        row.passCount = 3
        row.failCount = 1
        ElMessage.success('任务执行完成')
      }
    }, 1000)
  } catch (error) {
    // 取消执行
  }
}

// 停止任务
const handleStop = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要停止该任务吗？', '提示', {
      type: 'warning'
    })
    row.status = 'pending'
    row.progress = 0
    ElMessage.success('任务已停止')
  } catch (error) {
    // 取消停止
  }
}

// 查看结果
const handleViewResult = (row: any) => {
  resultDialogVisible.value = true
}

// 删除
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该任务吗？', '提示', {
      type: 'warning'
    })
    const index = tableData.value.findIndex(item => item.id === row.id)
    if (index > -1) {
      tableData.value.splice(index, 1)
      ElMessage.success('删除成功')
    }
  } catch (error) {
    // 取消删除
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()

    if (form.id) {
      // 编辑
      ElMessage.success('编辑成功')
    } else {
      // 新增
      tableData.value.push({
        id: Date.now(),
        name: form.name,
        ruleCount: form.rules.length,
        targetScope: form.targetScope === 'all' ? '全库' : form.targetScope === 'user' ? '用户表' : '资产表',
        status: 'pending',
        progress: 0,
        passCount: 0,
        failCount: 0,
        createTime: new Date().toLocaleString()
      })
      ElMessage.success('新建成功')
    }

    dialogVisible.value = false
  } catch (error) {
    // 表单验证失败
  }
}

// 关闭对话框
const handleDialogClose = () => {
  formRef.value?.resetFields()
  Object.assign(form, {
    id: null,
    name: '',
    rules: [],
    targetScope: 'all',
    executeType: 'immediate',
    scheduleTime: '',
    description: ''
  })
}
</script>

<style scoped lang="scss">
.quality-task-container {
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
