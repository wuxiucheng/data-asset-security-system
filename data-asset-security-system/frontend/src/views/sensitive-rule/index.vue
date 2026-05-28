<template>
  <div class="sensitive-rule-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>敏感识别规则管理</span>
          <div class="header-actions">
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新建规则
            </el-button>
            <el-button @click="handleInitBuiltin">
              <el-icon><Refresh /></el-icon>
              初始化内置规则
            </el-button>
          </div>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="规则名称">
          <el-input v-model="queryParams.ruleName" placeholder="请输入规则名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="敏感类型">
          <el-select v-model="queryParams.sensitiveType" placeholder="请选择" clearable style="width: 180px">
            <el-option label="手机号" value="PHONE" />
            <el-option label="身份证号" value="ID_CARD" />
            <el-option label="邮箱" value="EMAIL" />
            <el-option label="银行卡号" value="BANK_CARD" />
            <el-option label="姓名" value="NAME" />
            <el-option label="地址" value="ADDRESS" />
            <el-option label="密码" value="PASSWORD" />
          </el-select>
        </el-form-item>
        <el-form-item label="匹配模式">
          <el-select v-model="queryParams.matchMode" placeholder="请选择" clearable style="width: 180px">
            <el-option label="字段名匹配" value="FIELD_NAME" />
            <el-option label="正则表达式" value="REGEX" />
            <el-option label="样本匹配" value="SAMPLE" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable style="width: 180px">
            <el-option label="启用" value="ENABLED" />
            <el-option label="禁用" value="DISABLED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="ruleId" label="规则ID" width="80" />
        <el-table-column prop="ruleName" label="规则名称" min-width="150" />
        <el-table-column prop="sensitiveType" label="敏感类型" width="120">
          <template #default="{ row }">
            <el-tag>{{ row.sensitiveType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="matchMode" label="匹配模式" width="120">
          <template #default="{ row }">
            <el-tag :type="getMatchModeType(row.matchMode)">
              {{ getMatchModeLabel(row.matchMode) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="matchExpression" label="匹配表达式" min-width="200" show-overflow-tooltip />
        <el-table-column prop="confidenceWeight" label="置信度" width="80">
          <template #default="{ row }">
            {{ (row.confidenceWeight * 100).toFixed(0) }}%
          </template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" width="80" />
        <el-table-column prop="isBuiltin" label="内置规则" width="90">
          <template #default="{ row }">
            <el-tag :type="row.isBuiltin ? 'success' : 'info'">
              {{ row.isBuiltin ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ENABLED' ? 'success' : 'danger'">
              {{ row.status === 'ENABLED' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button 
              link 
              :type="row.status === 'ENABLED' ? 'warning' : 'success'" 
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 'ENABLED' ? '禁用' : '启用' }}
            </el-button>
            <el-button 
              v-if="!row.isBuiltin" 
              link 
              type="danger" 
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleQuery"
        @current-change="handleQuery"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 新建/编辑对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogTitle" 
      width="600px"
      @close="handleDialogClose"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="规则名称" prop="ruleName">
          <el-input v-model="form.ruleName" placeholder="请输入规则名称" />
        </el-form-item>
        <el-form-item label="敏感类型" prop="sensitiveType">
          <el-select v-model="form.sensitiveType" placeholder="请选择敏感类型" style="width: 100%">
            <el-option label="手机号" value="PHONE" />
            <el-option label="身份证号" value="ID_CARD" />
            <el-option label="邮箱" value="EMAIL" />
            <el-option label="银行卡号" value="BANK_CARD" />
            <el-option label="姓名" value="NAME" />
            <el-option label="地址" value="ADDRESS" />
            <el-option label="密码" value="PASSWORD" />
          </el-select>
        </el-form-item>
        <el-form-item label="匹配模式" prop="matchMode">
          <el-select v-model="form.matchMode" placeholder="请选择匹配模式" style="width: 100%">
            <el-option label="字段名匹配" value="FIELD_NAME" />
            <el-option label="正则表达式" value="REGEX" />
            <el-option label="样本匹配" value="SAMPLE" />
          </el-select>
        </el-form-item>
        <el-form-item label="匹配表达式" prop="matchExpression">
          <el-input 
            v-model="form.matchExpression" 
            type="textarea" 
            :rows="3"
            placeholder="字段名匹配: 逗号分隔的字段名列表&#10;正则表达式: 正则表达式&#10;样本匹配: 样本数据"
          />
        </el-form-item>
        <el-form-item label="置信度权重" prop="confidenceWeight">
          <el-slider v-model="form.confidenceWeight" :min="0" :max="1" :step="0.05" show-input />
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-input-number v-model="form.priority" :min="1" :max="1000" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="ENABLED">启用</el-radio>
            <el-radio label="DISABLED">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh, Search } from '@element-plus/icons-vue'
import { sensitiveIdentApi } from '@/api'

const loading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)

const queryParams = reactive({
  ruleName: '',
  sensitiveType: '',
  matchMode: '',
  status: '',
  pageNum: 1,
  pageSize: 10
})

const dialogVisible = ref(false)
const dialogTitle = ref('新建规则')
const submitLoading = ref(false)
const formRef = ref()

const form = reactive({
  ruleId: null as number | null,
  ruleName: '',
  sensitiveType: '',
  matchMode: '',
  matchExpression: '',
  confidenceWeight: 0.8,
  priority: 100,
  status: 'ENABLED'
})

const rules = {
  ruleName: [{ required: true, message: '请输入规则名称', trigger: 'blur' }],
  sensitiveType: [{ required: true, message: '请选择敏感类型', trigger: 'change' }],
  matchMode: [{ required: true, message: '请选择匹配模式', trigger: 'change' }],
  matchExpression: [{ required: true, message: '请输入匹配表达式', trigger: 'blur' }]
}

// 查询列表
const getList = async () => {
  loading.value = true
  try {
    const res = await sensitiveIdentApi.getRulePage(queryParams)
    tableData.value = res.data?.records || res.data?.list || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('查询失败:', error)
  } finally {
    loading.value = false
  }
}

// 查询
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置
const handleReset = () => {
  queryParams.ruleName = ''
  queryParams.sensitiveType = ''
  queryParams.matchMode = ''
  queryParams.status = ''
  queryParams.pageNum = 1
  getList()
}

// 新建
const handleAdd = () => {
  dialogTitle.value = '新建规则'
  form.ruleId = null
  form.ruleName = ''
  form.sensitiveType = ''
  form.matchMode = ''
  form.matchExpression = ''
  form.confidenceWeight = 0.8
  form.priority = 100
  form.status = 'ENABLED'
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
    await ElMessageBox.confirm('确定要删除该规则吗?', '提示', {
      type: 'warning'
    })
    await sensitiveIdentApi.deleteRule(row.ruleId)
    ElMessage.success('删除成功')
    getList()
  } catch (error) {
    console.error('删除失败:', error)
  }
}

// 切换状态
const handleToggleStatus = async (row: any) => {
  const newStatus = row.status === 'ENABLED' ? 'DISABLED' : 'ENABLED'
  const action = newStatus === 'ENABLED' ? '启用' : '禁用'
  
  try {
    await ElMessageBox.confirm(`确定要${action}该规则吗?`, '提示', {
      type: 'warning'
    })
    await sensitiveIdentApi.updateRuleStatus(row.ruleId, newStatus)
    ElMessage.success(`${action}成功`)
    getList()
  } catch (error) {
    console.error('操作失败:', error)
  }
}

// 初始化内置规则
const handleInitBuiltin = async () => {
  try {
    await ElMessageBox.confirm('确定要初始化内置规则吗?', '提示', {
      type: 'warning'
    })
    await sensitiveIdentApi.initBuiltinRules()
    ElMessage.success('初始化成功')
    getList()
  } catch (error) {
    console.error('初始化失败:', error)
  }
}

// 提交表单
const handleSubmit = async () => {
  await formRef.value.validate()
  submitLoading.value = true
  
  try {
    if (form.ruleId) {
      await sensitiveIdentApi.updateRule(form)
      ElMessage.success('更新成功')
    } else {
      await sensitiveIdentApi.createRule(form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    getList()
  } catch (error) {
    console.error('操作失败:', error)
  } finally {
    submitLoading.value = false
  }
}

// 关闭对话框
const handleDialogClose = () => {
  formRef.value?.resetFields()
}

// 匹配模式标签
const getMatchModeType = (mode: string) => {
  const map: any = {
    'FIELD_NAME': 'primary',
    'REGEX': 'success',
    'SAMPLE': 'warning'
  }
  return map[mode] || 'info'
}

const getMatchModeLabel = (mode: string) => {
  const map: any = {
    'FIELD_NAME': '字段名',
    'REGEX': '正则',
    'SAMPLE': '样本'
  }
  return map[mode] || mode
}

onMounted(() => {
  getList()
})
</script>

<style scoped lang="scss">
.sensitive-rule-container {
  padding: 20px;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .header-actions {
      display: flex;
      gap: 10px;
    }
  }

  .search-form {
    margin-bottom: 20px;
  }
}
</style>
