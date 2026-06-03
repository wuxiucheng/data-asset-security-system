<template>
  <div class="mask-strategy-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>脱敏策略管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增策略
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="策略名称">
          <el-input v-model="searchForm.name" placeholder="请输入策略名称" clearable />
        </el-form-item>
        <el-form-item label="脱敏类型">
          <el-select v-model="searchForm.type" placeholder="请选择" clearable style="width: 150px">
            <el-option label="全部" value="" />
            <el-option label="手机号" value="phone" />
            <el-option label="身份证" value="idcard" />
            <el-option label="邮箱" value="email" />
            <el-option label="银行卡" value="bankcard" />
            <el-option label="姓名" value="name" />
            <el-option label="地址" value="address" />
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
        <el-table-column prop="name" label="策略名称" width="180" />
        <el-table-column prop="type" label="脱敏类型" width="120">
          <template #default="{ row }">
            <el-tag>{{ getTypeName(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="algorithm" label="脱敏算法" width="150">
          <template #default="{ row }">
            {{ getAlgorithmName(row.algorithm) }}
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
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
        <el-form-item label="策略名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入策略名称" />
        </el-form-item>
        <el-form-item label="脱敏类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择脱敏类型" style="width: 100%" @change="handleTypeChange">
            <el-option label="手机号" value="phone" />
            <el-option label="身份证" value="idcard" />
            <el-option label="邮箱" value="email" />
            <el-option label="银行卡" value="bankcard" />
            <el-option label="姓名" value="name" />
            <el-option label="地址" value="address" />
            <el-option label="自定义" value="custom" />
          </el-select>
        </el-form-item>
        <el-form-item label="脱敏算法" prop="algorithm">
          <el-select v-model="form.algorithm" placeholder="请选择脱敏算法" style="width: 100%" @change="handleAlgorithmChange">
            <el-option label="部分遮盖" value="partial" />
            <el-option label="完全遮盖" value="full" />
            <el-option label="哈希脱敏" value="hash" />
            <el-option label="随机替换" value="random" />
            <el-option label="正则替换" value="regex" />
          </el-select>
        </el-form-item>
        <el-form-item label="遮盖规则" prop="rule" v-if="form.algorithm === 'partial'">
          <el-input v-model="form.rule" placeholder="例如：3,4 表示保留前3位和后4位" />
        </el-form-item>
        <el-form-item label="正则表达式" prop="regex" v-if="form.algorithm === 'regex'">
          <el-input v-model="form.regex" placeholder="请输入正则表达式" />
        </el-form-item>
        <el-form-item label="替换字符" prop="replaceChar">
          <el-input v-model="form.replaceChar" placeholder="默认为 *" maxlength="1" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" />
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

    <!-- 测试对话框 -->
    <el-dialog v-model="testDialogVisible" title="脱敏测试" width="500px">
      <el-form label-width="100px">
        <el-form-item label="原始数据">
          <el-input v-model="testData.original" placeholder="请输入要测试的数据" />
        </el-form-item>
        <el-form-item label="脱敏结果">
          <el-input v-model="testData.masked" readonly />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="testDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleTestMask">测试</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

defineOptions({
  name: 'MaskStrategy'
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
    name: '手机号脱敏',
    type: 'phone',
    algorithm: 'partial',
    rule: '3,4',
    description: '保留前3位和后4位，中间用*替换',
    status: 'active',
    createTime: '2026-05-28 10:00:00'
  },
  {
    id: 2,
    name: '身份证脱敏',
    type: 'idcard',
    algorithm: 'partial',
    rule: '6,4',
    description: '保留前6位和后4位，中间用*替换',
    status: 'active',
    createTime: '2026-05-28 10:00:00'
  },
  {
    id: 3,
    name: '邮箱脱敏',
    type: 'email',
    algorithm: 'partial',
    rule: '3,@',
    description: '保留前3位和@后的域名部分',
    status: 'active',
    createTime: '2026-05-28 10:00:00'
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
const dialogTitle = ref('新增策略')
const formRef = ref()
const form = reactive({
  id: null,
  name: '',
  type: 'phone',
  algorithm: 'partial',
  rule: '',
  regex: '',
  replaceChar: '*',
  description: '',
  status: 'active'
})

const rules = {
  name: [{ required: true, message: '请输入策略名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择脱敏类型', trigger: 'change' }],
  algorithm: [{ required: true, message: '请选择脱敏算法', trigger: 'change' }]
}

// 测试对话框
const testDialogVisible = ref(false)
const testData = reactive({
  original: '',
  masked: '',
  strategy: null as any
})

// 类型映射
const typeMap: Record<string, string> = {
  phone: '手机号',
  idcard: '身份证',
  email: '邮箱',
  bankcard: '银行卡',
  name: '姓名',
  address: '地址',
  custom: '自定义'
}

const algorithmMap: Record<string, string> = {
  partial: '部分遮盖',
  full: '完全遮盖',
  hash: '哈希脱敏',
  random: '随机替换',
  regex: '正则替换'
}

const getTypeName = (type: string) => typeMap[type] || type
const getAlgorithmName = (algorithm: string) => algorithmMap[algorithm] || algorithm

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
  dialogTitle.value = '新增策略'
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑策略'
  Object.assign(form, row)
  dialogVisible.value = true
}

// 删除
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该策略吗？', '提示', {
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
  ElMessage.success(`策略已${row.status === 'active' ? '启用' : '禁用'}`)
}

// 测试
const handleTest = (row: any) => {
  testData.strategy = row
  testData.original = ''
  testData.masked = ''
  testDialogVisible.value = true
}

// 执行脱敏测试
const handleTestMask = () => {
  if (!testData.original) {
    ElMessage.warning('请输入要测试的数据')
    return
  }

  // 模拟脱敏处理
  const original = testData.original
  const strategy = testData.strategy

  if (strategy.algorithm === 'partial') {
    const rule = strategy.rule.split(',')
    const keepStart = parseInt(rule[0])
    const keepEnd = parseInt(rule[1])

    if (strategy.type === 'phone') {
      // 手机号脱敏：138****1234
      testData.masked = original.substring(0, keepStart) +
        '*'.repeat(original.length - keepStart - keepEnd) +
        original.substring(original.length - keepEnd)
    } else if (strategy.type === 'idcard') {
      // 身份证脱敏：110108********1234
      testData.masked = original.substring(0, keepStart) +
        '*'.repeat(original.length - keepStart - keepEnd) +
        original.substring(original.length - keepEnd)
    } else if (strategy.type === 'email') {
      // 邮箱脱敏：abc***@example.com
      const atIndex = original.indexOf('@')
      testData.masked = original.substring(0, keepStart) +
        '*'.repeat(atIndex - keepStart) +
        original.substring(atIndex)
    } else {
      testData.masked = original.substring(0, keepStart) +
        '*'.repeat(original.length - keepStart - keepEnd) +
        original.substring(original.length - keepEnd)
    }
  } else if (strategy.algorithm === 'full') {
    testData.masked = '*'.repeat(original.length)
  } else {
    testData.masked = '***'
  }

  ElMessage.success('脱敏测试完成')
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
  // 根据脱敏类型自动设置默认值
  const defaults: Record<string, any> = {
    phone: { rule: '3,4', description: '保留前3位和后4位，中间用*替换' },
    idcard: { rule: '6,4', description: '保留前6位和后4位，中间用*替换' },
    email: { rule: '3,@', description: '保留前3位和@后的域名部分' },
    bankcard: { rule: '4,4', description: '保留前4位和后4位，中间用*替换' },
    name: { rule: '1,0', description: '保留姓氏，名字用*替换' },
    address: { rule: '6,0', description: '保留前6位，后面用*替换' }
  }

  if (defaults[type] && !form.id) {
    form.rule = defaults[type].rule
    if (!form.description) {
      form.description = defaults[type].description
    }
  }
}

const handleAlgorithmChange = (algorithm: string) => {
  // 根据脱敏算法自动设置默认描述
  const descriptions: Record<string, string> = {
    partial: '部分遮盖：保留部分字符，其余用*替换',
    full: '完全遮盖：所有字符用*替换',
    hash: '哈希脱敏：使用哈希算法转换数据',
    random: '随机替换：用随机字符替换原数据',
    regex: '正则替换：使用正则表达式匹配替换'
  }

  if (!form.description && !form.id) {
    form.description = descriptions[algorithm] || ''
  }
}

// 关闭对话框
const handleDialogClose = () => {
  formRef.value?.resetFields()
  Object.assign(form, {
    id: null,
    name: '',
    type: 'phone',
    algorithm: 'partial',
    rule: '',
    regex: '',
    replaceChar: '*',
    description: '',
    status: 'active'
  })
}
</script>

<style scoped lang="scss">
.mask-strategy-container {
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
