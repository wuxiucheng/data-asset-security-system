<template>
  <div class="classification-assist-rule">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>分类分级辅助规则</span>
          <el-button type="primary" @click="handleAdd">新增规则</el-button>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="规则名称">
          <el-input v-model="queryParams.ruleName" placeholder="请输入规则名称" clearable />
        </el-form-item>
        <el-form-item label="规则类型">
          <el-select v-model="queryParams.ruleType" placeholder="请选择" clearable style="width: 150px">
            <el-option label="字段名匹配" value="FIELD_NAME" />
            <el-option label="字段模式匹配" value="FIELD_PATTERN" />
            <el-option label="样本匹配" value="FIELD_SAMPLE" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable style="width: 150px">
            <el-option label="启用" value="ACTIVE" />
            <el-option label="禁用" value="INACTIVE" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="ruleName" label="规则名称" min-width="150" />
        <el-table-column prop="ruleCode" label="规则代码" width="120" />
        <el-table-column prop="ruleType" label="规则类型" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.ruleType === 'FIELD_NAME'" type="primary">字段名匹配</el-tag>
            <el-tag v-else-if="row.ruleType === 'FIELD_PATTERN'" type="success">字段模式匹配</el-tag>
            <el-tag v-else-if="row.ruleType === 'FIELD_SAMPLE'" type="warning">样本匹配</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="fieldNamePattern" label="匹配模式" min-width="200" show-overflow-tooltip />
        <el-table-column prop="suggestGradingId" label="建议分级" width="100">
          <template #default="{ row }">
            <el-tag :type="getGradingTagType(row.suggestGradingId)">L{{ row.suggestGradingId }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'ACTIVE'" type="success">启用</el-tag>
            <el-tag v-else type="info">禁用</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applyCount" label="应用次数" width="100" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.status === 'ACTIVE'" link type="warning" @click="handleDisable(row)">禁用</el-button>
            <el-button v-else link type="success" @click="handleEnable(row)">启用</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
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
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="规则名称" prop="ruleName">
          <el-input v-model="form.ruleName" placeholder="请输入规则名称" />
        </el-form-item>
        <el-form-item label="规则代码" prop="ruleCode">
          <el-input v-model="form.ruleCode" placeholder="请输入规则代码" :disabled="!!form.ruleId" />
        </el-form-item>
        <el-form-item label="规则类型" prop="ruleType">
          <el-select v-model="form.ruleType" placeholder="请选择规则类型" style="width: 100%">
            <el-option label="字段名匹配" value="FIELD_NAME" />
            <el-option label="字段模式匹配" value="FIELD_PATTERN" />
            <el-option label="样本匹配" value="FIELD_SAMPLE" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.ruleType === 'FIELD_NAME'" label="字段名模式" prop="fieldNamePattern">
          <el-input v-model="form.fieldNamePattern" placeholder="支持通配符，如：*phone*,*mobile*" />
        </el-form-item>
        <el-form-item v-if="form.ruleType === 'FIELD_PATTERN'" label="字段值正则" prop="fieldValuePattern">
          <el-input v-model="form.fieldValuePattern" placeholder="正则表达式，如：^1[3-9]\d{9}$" />
        </el-form-item>
        <el-form-item label="建议分级" prop="suggestGradingId">
          <el-select v-model="form.suggestGradingId" placeholder="请选择建议分级" style="width: 100%">
            <el-option label="L1级 - 公开" :value="1" />
            <el-option label="L2级 - 内部" :value="2" />
            <el-option label="L3级 - 敏感" :value="3" />
            <el-option label="L4级 - 高敏感" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="分级原因" prop="suggestGradingReason">
          <el-input v-model="form.suggestGradingReason" type="textarea" :rows="3" placeholder="请输入分级原因说明" />
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-input-number v-model="form.priority" :min="1" :max="999" />
        </el-form-item>
        <el-form-item label="自动应用">
          <el-switch v-model="form.autoApply" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="需要审核">
          <el-switch v-model="form.needReview" :active-value="1" :inactive-value="0" />
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
import { classificationAssistRuleApi } from '@/api'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('新增规则')
const formRef = ref()

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  ruleName: '',
  ruleType: '',
  status: ''
})

const form = reactive({
  ruleId: null,
  ruleName: '',
  ruleCode: '',
  ruleType: 'FIELD_NAME',
  fieldNamePattern: '',
  fieldValuePattern: '',
  suggestGradingId: 3,
  suggestGradingReason: '',
  priority: 100,
  autoApply: 0,
  needReview: 1,
  status: 'ACTIVE'
})

const rules = {
  ruleName: [{ required: true, message: '请输入规则名称', trigger: 'blur' }],
  ruleCode: [{ required: true, message: '请输入规则代码', trigger: 'blur' }],
  ruleType: [{ required: true, message: '请选择规则类型', trigger: 'change' }],
  suggestGradingId: [{ required: true, message: '请选择建议分级', trigger: 'change' }]
}

const getGradingTagType = (level: number) => {
  const types: Record<number, any> = {
    1: 'info',
    2: 'success',
    3: 'warning',
    4: 'danger'
  }
  return types[level] || 'info'
}

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await classificationAssistRuleApi.getList(queryParams)
    tableData.value = res.data.list || res.data.records || []
    total.value = res.data.total
  } catch (error) {
    console.error('查询失败:', error)
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  queryParams.ruleName = ''
  queryParams.ruleType = ''
  queryParams.status = ''
  handleQuery()
}

const handleAdd = () => {
  dialogTitle.value = '新增规则'
  Object.assign(form, {
    ruleId: null,
    ruleName: '',
    ruleCode: '',
    ruleType: 'FIELD_NAME',
    fieldNamePattern: '',
    fieldValuePattern: '',
    suggestGradingId: 3,
    suggestGradingReason: '',
    priority: 100,
    autoApply: 0,
    needReview: 1,
    status: 'ACTIVE'
  })
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑规则'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  try {
    if (form.ruleId) {
      await classificationAssistRuleApi.update(form)
      ElMessage.success('更新成功')
    } else {
      await classificationAssistRuleApi.create(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    handleQuery()
  } catch (error) {
    console.error('操作失败:', error)
  }
}

const handleEnable = async (row: any) => {
  try {
    await classificationAssistRuleApi.enable(row.ruleId)
    ElMessage.success('启用成功')
    handleQuery()
  } catch (error) {
    console.error('启用失败:', error)
  }
}

const handleDisable = async (row: any) => {
  try {
    await classificationAssistRuleApi.disable(row.ruleId)
    ElMessage.success('禁用成功')
    handleQuery()
  } catch (error) {
    console.error('禁用失败:', error)
  }
}

const handleDelete = async (row: any) => {
  await ElMessageBox.confirm('确定要删除该规则吗？', '提示', {
    type: 'warning'
  })
  try {
    await classificationAssistRuleApi.delete(row.ruleId)
    ElMessage.success('删除成功')
    handleQuery()
  } catch (error) {
    console.error('删除失败:', error)
  }
}

onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.classification-assist-rule {
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
