<template>
  <div class="quality-rule-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>质量规则管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增规则
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="规则名称">
          <el-input v-model="searchForm.name" placeholder="请输入规则名称" clearable />
        </el-form-item>
        <el-form-item label="规则类型">
          <el-select v-model="searchForm.type" placeholder="请选择" clearable style="width: 150px">
            <el-option label="全部" value="" />
            <el-option label="完整性" value="completeness" />
            <el-option label="唯一性" value="uniqueness" />
            <el-option label="准确性" value="accuracy" />
            <el-option label="一致性" value="consistency" />
            <el-option label="时效性" value="timeliness" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 150px">
            <el-option label="全部" value="" />
            <el-option label="启用" value="active" />
            <el-option label="禁用" value="inactive" />
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
        <el-table-column prop="name" label="规则名称" width="180" />
        <el-table-column prop="type" label="规则类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.type)">{{ getTypeName(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="target" label="检查对象" width="150" />
        <el-table-column prop="condition" label="检查条件" show-overflow-tooltip />
        <el-table-column prop="severity" label="严重级别" width="100">
          <template #default="{ row }">
            <el-tag :type="getSeverityTag(row.severity)">{{ getSeverityName(row.severity) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              active-value="active"
              inactive-value="inactive"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="primary" link @click="handleTest(row)">测试</el-button>
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
        <el-form-item label="规则名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入规则名称" />
        </el-form-item>
        <el-form-item label="规则类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择规则类型" style="width: 100%" @change="handleTypeChange">
            <el-option label="完整性" value="completeness" />
            <el-option label="唯一性" value="uniqueness" />
            <el-option label="准确性" value="accuracy" />
            <el-option label="一致性" value="consistency" />
            <el-option label="时效性" value="timeliness" />
          </el-select>
        </el-form-item>
        <el-form-item label="检查对象" prop="target">
          <el-input v-model="form.target" placeholder="例如：表名.字段名" />
        </el-form-item>
        <el-form-item label="检查条件" prop="condition">
          <el-input v-model="form.condition" type="textarea" :rows="3" placeholder="请输入检查条件表达式" />
        </el-form-item>
        <el-form-item label="严重级别" prop="severity">
          <el-select v-model="form.severity" placeholder="请选择严重级别" style="width: 100%">
            <el-option label="严重" value="critical" />
            <el-option label="重要" value="major" />
            <el-option label="一般" value="minor" />
            <el-option label="提示" value="info" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio value="active">启用</el-radio>
            <el-radio value="inactive">禁用</el-radio>
          </el-radio-group>
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
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

defineOptions({
  name: 'QualityRule'
})

// 搜索表单
const searchForm = reactive({
  name: '',
  type: '',
  status: ''
})

// 表格数据
const tableData = ref([
  {
    id: 1,
    name: '用户名非空检查',
    type: 'completeness',
    target: 'user.username',
    condition: 'IS NOT NULL',
    severity: 'critical',
    description: '用户名字段不能为空',
    status: 'active',
    createTime: '2026-05-28 10:00:00'
  },
  {
    id: 2,
    name: '用户ID唯一性检查',
    type: 'uniqueness',
    target: 'user.user_id',
    condition: 'COUNT(DISTINCT) = COUNT(*)',
    severity: 'critical',
    description: '用户ID必须唯一',
    status: 'active',
    createTime: '2026-05-28 10:00:00'
  },
  {
    id: 3,
    name: '邮箱格式检查',
    type: 'accuracy',
    target: 'user.email',
    condition: 'REGEXP: ^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$',
    severity: 'major',
    description: '邮箱格式必须正确',
    status: 'active',
    createTime: '2026-05-28 10:00:00'
  },
  {
    id: 4,
    name: '手机号格式检查',
    type: 'accuracy',
    target: 'user.phone',
    condition: 'REGEXP: ^1[3-9]\\d{9}$',
    severity: 'major',
    description: '手机号格式必须正确',
    status: 'active',
    createTime: '2026-05-28 10:00:00'
  }
])

const loading = ref(false)
const pagination = reactive({
  current: 1,
  size: 10,
  total: 4
})

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('新增规则')
const formRef = ref()
const form = reactive({
  id: null,
  name: '',
  type: 'completeness',
  target: '',
  condition: '',
  severity: 'major',
  description: '',
  status: 'active'
})

const rules = {
  name: [{ required: true, message: '请输入规则名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择规则类型', trigger: 'change' }],
  target: [{ required: true, message: '请输入检查对象', trigger: 'blur' }],
  condition: [{ required: true, message: '请输入检查条件', trigger: 'blur' }],
  severity: [{ required: true, message: '请选择严重级别', trigger: 'change' }]
}

// 类型映射
const typeMap: Record<string, string> = {
  completeness: '完整性',
  uniqueness: '唯一性',
  accuracy: '准确性',
  consistency: '一致性',
  timeliness: '时效性'
}

const severityMap: Record<string, string> = {
  critical: '严重',
  major: '重要',
  minor: '一般',
  info: '提示'
}

const getTypeName = (type: string) => typeMap[type] || type
const getSeverityName = (severity: string) => severityMap[severity] || severity

const getTypeTag = (type: string) => {
  const tagMap: Record<string, string> = {
    completeness: 'success',
    uniqueness: 'warning',
    accuracy: 'primary',
    consistency: 'info',
    timeliness: ''
  }
  return tagMap[type] || ''
}

const getSeverityTag = (severity: string) => {
  const tagMap: Record<string, string> = {
    critical: 'danger',
    major: 'warning',
    minor: 'info',
    info: ''
  }
  return tagMap[severity] || ''
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
  searchForm.type = ''
  searchForm.status = ''
  handleSearch()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增规则'
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑规则'
  Object.assign(form, row)
  dialogVisible.value = true
}

// 删除
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该规则吗？', '提示', {
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

// 状态切换
const handleStatusChange = (row: any) => {
  ElMessage.success(`规则已${row.status === 'active' ? '启用' : '禁用'}`)
}

// 测试
const handleTest = (row: any) => {
  ElMessage.info('规则测试功能开发中')
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()

    if (form.id) {
      // 编辑
      const index = tableData.value.findIndex(item => item.id === form.id)
      if (index > -1) {
        tableData.value[index] = { ...form, createTime: tableData.value[index].createTime }
      }
      ElMessage.success('编辑成功')
    } else {
      // 新增
      tableData.value.push({
        ...form,
        id: Date.now(),
        createTime: new Date().toLocaleString()
      })
      ElMessage.success('新增成功')
    }

    dialogVisible.value = false
  } catch (error) {
    // 表单验证失败
  }
}

// 自动填充默认值
const handleTypeChange = (type: string) => {
  // 根据规则类型自动设置默认值
  const defaults: Record<string, any> = {
    completeness: {
      condition: 'IS NOT NULL',
      description: '检查字段是否为空，确保数据完整性'
    },
    uniqueness: {
      condition: 'COUNT(DISTINCT) = COUNT(*)',
      description: '检查字段值是否唯一，确保无重复数据'
    },
    accuracy: {
      condition: 'REGEXP: ',
      description: '检查字段值是否符合指定格式，确保数据准确性'
    },
    consistency: {
      condition: 'VALUE IN (allowed_values)',
      description: '检查字段值是否在允许范围内，确保数据一致性'
    },
    timeliness: {
      condition: 'TIMESTAMP >= threshold',
      description: '检查数据是否在有效期内，确保数据时效性'
    }
  }

  if (defaults[type] && !form.id) {
    form.condition = defaults[type].condition
    if (!form.description) {
      form.description = defaults[type].description
    }
  }
}

// 关闭对话框
const handleDialogClose = () => {
  formRef.value?.resetFields()
  Object.assign(form, {
    id: null,
    name: '',
    type: 'completeness',
    target: '',
    condition: '',
    severity: 'major',
    description: '',
    status: 'active'
  })
}
</script>

<style scoped lang="scss">
.quality-rule-container {
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
