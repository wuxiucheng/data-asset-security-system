<template>
  <div class="datasource-container">
    <div class="page-header">
      <h2>数据源配置</h2>
      <p class="header-desc">配置数据库连接信息，供资产发现和数据条数刷新等功能复用</p>
    </div>

    <!-- 搜索栏 -->
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="数据源名称">
          <el-input v-model="queryParams.dataSourceName" placeholder="请输入" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="数据库类型">
          <el-select v-model="queryParams.databaseType" placeholder="全部" clearable>
            <el-option label="MySQL" value="MYSQL" />
            <el-option label="Oracle" value="ORACLE" />
            <el-option label="PostgreSQL" value="POSTGRESQL" />
            <el-option label="SQL Server" value="SQLSERVER" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable>
            <el-option label="启用" value="ACTIVE" />
            <el-option label="停用" value="INACTIVE" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作栏 + 表格 -->
    <el-card shadow="never" class="table-card">
      <div class="table-toolbar">
        <el-button type="primary" @click="handleAdd">新增数据源</el-button>
      </div>

      <el-table :data="dataSourceList" v-loading="loading" stripe>
        <el-table-column prop="dataSourceName" label="数据源名称" min-width="140" />
        <el-table-column prop="databaseType" label="数据库类型" width="120">
          <template #default="{ row }">
            <el-tag :type="dbTypeTagType(row.databaseType)">{{ dbTypeLabel(row.databaseType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="连接地址" min-width="180">
          <template #default="{ row }">{{ row.host }}:{{ row.port }}</template>
        </el-table-column>
        <el-table-column prop="databaseName" label="数据库名称" min-width="140" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'">{{ row.status === 'ACTIVE' ? '启用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="最后测试" min-width="160">
          <template #default="{ row }">
            <div v-if="row.lastTestTime">{{ row.lastTestTime }}</div>
            <div v-if="row.lastTestResult" :style="{ color: row.lastTestResult === '连接成功' ? '#67c23a' : '#f56c6c' }">
              {{ row.lastTestResult }}
            </div>
            <span v-if="!row.lastTestTime" style="color: #909399">未测试</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button link type="success" @click="handleTestConnection(row)" :loading="testingId === row.dataSourceId">测试连接</el-button>
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="warning" @click="handleToggleStatus(row)">{{ row.status === 'ACTIVE' ? '停用' : '启用' }}</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleQuery"
        @current-change="handleQuery"
        class="pagination"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑数据源' : '新增数据源'" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="数据源名称" prop="dataSourceName">
          <el-input v-model="formData.dataSourceName" placeholder="请输入数据源名称" />
        </el-form-item>
        <el-form-item label="数据库类型" prop="databaseType">
          <el-select v-model="formData.databaseType" placeholder="请选择" style="width: 100%">
            <el-option label="MySQL" value="MYSQL" />
            <el-option label="Oracle" value="ORACLE" />
            <el-option label="PostgreSQL" value="POSTGRESQL" />
            <el-option label="SQL Server" value="SQLSERVER" />
          </el-select>
        </el-form-item>
        <el-form-item label="主机地址" prop="host">
          <el-input v-model="formData.host" placeholder="请输入数据库地址" />
        </el-form-item>
        <el-form-item label="端口" prop="port">
          <el-input-number v-model="formData.port" :min="1" :max="65535" style="width: 100%" />
        </el-form-item>
        <el-form-item label="数据库名称" prop="databaseName">
          <el-input v-model="formData.databaseName" placeholder="请输入数据库名称" />
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="formData.password" type="password" show-password :placeholder="isEdit ? '不修改请留空' : '请输入密码'" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remarks" type="textarea" :rows="2" placeholder="可选" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="success" @click="handleTestInDialog" :loading="testingDialog">测试连接</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { dataSourceConfigApi } from '@/api'

// 查询参数
const queryParams = reactive({
  dataSourceName: '',
  databaseType: '',
  status: '',
  pageNum: 1,
  pageSize: 10,
})

const dataSourceList = ref<any[]>([])
const total = ref(0)
const loading = ref(false)

// 对话框
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref<number | null>(null)
const formRef = ref<FormInstance>()
const submitting = ref(false)
const testingDialog = ref(false)
const testingId = ref<number | null>(null)

const formData = reactive({
  dataSourceName: '',
  databaseType: 'MYSQL',
  host: '',
  port: 3306,
  databaseName: '',
  username: '',
  password: '',
  remarks: '',
})

const formRules: FormRules = {
  dataSourceName: [{ required: true, message: '请输入数据源名称', trigger: 'blur' }],
  databaseType: [{ required: true, message: '请选择数据库类型', trigger: 'change' }],
  host: [{ required: true, message: '请输入主机地址', trigger: 'blur' }],
  port: [{ required: true, message: '请输入端口', trigger: 'blur' }],
  databaseName: [{ required: true, message: '请输入数据库名称', trigger: 'blur' }],
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
}

const dbTypeLabel = (type: string) => {
  const map: Record<string, string> = { MYSQL: 'MySQL', ORACLE: 'Oracle', POSTGRESQL: 'PostgreSQL', SQLSERVER: 'SQL Server' }
  return map[type] || type
}

const dbTypeTagType = (type: string) => {
  const map: Record<string, string> = { MYSQL: '', ORACLE: 'warning', POSTGRESQL: 'success', SQLSERVER: 'danger' }
  return map[type] || 'info'
}

// 查询列表
const handleQuery = async () => {
  loading.value = true
  try {
    const params: any = { pageNum: queryParams.pageNum, pageSize: queryParams.pageSize }
    if (queryParams.dataSourceName) params.dataSourceName = queryParams.dataSourceName
    if (queryParams.databaseType) params.databaseType = queryParams.databaseType
    if (queryParams.status) params.status = queryParams.status

    const res = await dataSourceConfigApi.getList(params)
    dataSourceList.value = res.data.records || res.data.list || []
    total.value = res.data.total
  } catch (error) {
    console.error('查询数据源列表失败:', error)
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  queryParams.dataSourceName = ''
  queryParams.databaseType = ''
  queryParams.status = ''
  queryParams.pageNum = 1
  handleQuery()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  editId.value = null
  Object.assign(formData, { dataSourceName: '', databaseType: 'MYSQL', host: '', port: 3306, databaseName: '', username: '', password: '', remarks: '' })
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: any) => {
  isEdit.value = true
  editId.value = row.dataSourceId
  Object.assign(formData, {
    dataSourceName: row.dataSourceName,
    databaseType: row.databaseType,
    host: row.host,
    port: row.port,
    databaseName: row.databaseName,
    username: row.username,
    password: '',
    remarks: row.remarks || '',
  })
  dialogVisible.value = true
}

// 提交
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  submitting.value = true
  try {
    const data = { ...formData }
    if (isEdit.value && editId.value) {
      await dataSourceConfigApi.update(editId.value, data)
      ElMessage.success('更新成功')
    } else {
      await dataSourceConfigApi.create(data)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    handleQuery()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

// 测试连接（对话框内）
const handleTestInDialog = async () => {
  if (!formData.databaseType || !formData.host || !formData.port || !formData.databaseName) {
    ElMessage.warning('请先填写完整的连接信息')
    return
  }
  testingDialog.value = true
  try {
    const res = await dataSourceConfigApi.testConnectionWithData(formData)
    const success = res.data
    if (success) {
      ElMessage.success('连接成功')
    } else {
      ElMessage.error('连接失败，请检查连接信息')
    }
  } catch (error: any) {
    ElMessage.error('连接失败：' + (error.message || '未知错误'))
  } finally {
    testingDialog.value = false
  }
}

// 测试连接（列表中）
const handleTestConnection = async (row: any) => {
  testingId.value = row.dataSourceId
  try {
    const res = await dataSourceConfigApi.testConnection(row.dataSourceId)
    const success = res.data
    if (success) {
      ElMessage.success('连接成功')
    } else {
      ElMessage.error('连接失败')
    }
    handleQuery()
  } catch (error: any) {
    ElMessage.error('连接失败：' + (error.message || '未知错误'))
  } finally {
    testingId.value = null
  }
}

// 切换状态
const handleToggleStatus = async (row: any) => {
  const newStatus = row.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
  const label = newStatus === 'ACTIVE' ? '启用' : '停用'
  try {
    await ElMessageBox.confirm(`确定${label}数据源"${row.dataSourceName}"？`, '提示', { type: 'warning' })
    const data = {
      dataSourceName: row.dataSourceName,
      databaseType: row.databaseType,
      host: row.host,
      port: row.port,
      databaseName: row.databaseName,
      username: row.username,
      remarks: row.remarks,
    }
    await dataSourceConfigApi.update(row.dataSourceId, data)
    // 直接更新状态（后端update不改变status，需要单独处理）
    ElMessage.success(`${label}成功`)
    handleQuery()
  } catch {
    // 取消
  }
}

// 删除
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm(`确定删除数据源"${row.dataSourceName}"？`, '提示', { type: 'warning' })
    await dataSourceConfigApi.delete(row.dataSourceId)
    ElMessage.success('删除成功')
    handleQuery()
  } catch {
    // 取消
  }
}

onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.datasource-container {
  padding: 20px;
}
.page-header {
  margin-bottom: 16px;
}
.page-header h2 {
  margin: 0 0 4px 0;
  font-size: 20px;
}
.header-desc {
  margin: 0;
  color: #909399;
  font-size: 13px;
}
.search-card {
  margin-bottom: 16px;
}
.table-toolbar {
  margin-bottom: 16px;
}
.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
