<template>
  <div class="mask-whitelist-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>脱敏白名单管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增白名单
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="脱敏策略">
          <el-select v-model="searchForm.strategyId" placeholder="请选择策略" clearable style="width: 200px">
            <el-option label="全部" :value="null" />
            <el-option v-for="strategy in strategyList" :key="strategy.strategyId" 
                       :label="strategy.sensitiveType" :value="strategy.strategyId" />
          </el-select>
        </el-form-item>
        <el-form-item label="白名单类型">
          <el-select v-model="searchForm.whitelistType" placeholder="请选择" clearable style="width: 150px">
            <el-option label="全部" value="" />
            <el-option label="用户" value="USER" />
            <el-option label="角色" value="ROLE" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 150px">
            <el-option label="全部" value="" />
            <el-option label="启用" value="ACTIVE" />
            <el-option label="禁用" value="INACTIVE" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="whitelistId" label="ID" width="80" />
        <el-table-column prop="strategyId" label="脱敏策略" width="150">
          <template #default="{ row }">
            {{ getStrategyName(row.strategyId) }}
          </template>
        </el-table-column>
        <el-table-column prop="whitelistType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.whitelistType === 'USER' ? 'primary' : 'success'">
              {{ row.whitelistType === 'USER' ? '用户' : '角色' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="userId" label="用户ID" width="100">
          <template #default="{ row }">
            {{ row.userId || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="roleId" label="角色ID" width="100">
          <template #default="{ row }">
            {{ row.roleId || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="effectiveStart" label="生效开始" width="180">
          <template #default="{ row }">
            {{ row.effectiveStart || '不限' }}
          </template>
        </el-table-column>
        <el-table-column prop="effectiveEnd" label="生效结束" width="180">
          <template #default="{ row }">
            {{ row.effectiveEnd || '不限' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              active-value="ACTIVE"
              inactive-value="INACTIVE"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
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
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="脱敏策略" prop="strategyId">
          <el-select v-model="form.strategyId" placeholder="请选择策略" style="width: 100%">
            <el-option v-for="strategy in strategyList" :key="strategy.strategyId" 
                       :label="strategy.sensitiveType" :value="strategy.strategyId" />
          </el-select>
        </el-form-item>
        <el-form-item label="白名单类型" prop="whitelistType">
          <el-radio-group v-model="form.whitelistType">
            <el-radio value="USER">用户</el-radio>
            <el-radio value="ROLE">角色</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="form.whitelistType === 'USER'" label="用户ID" prop="userId">
          <el-input v-model.number="form.userId" placeholder="请输入用户ID" type="number" />
        </el-form-item>
        <el-form-item v-if="form.whitelistType === 'ROLE'" label="角色ID" prop="roleId">
          <el-input v-model.number="form.roleId" placeholder="请输入角色ID" type="number" />
        </el-form-item>
        <el-form-item label="生效时间">
          <el-col :span="11">
            <el-date-picker
              v-model="form.effectiveStart"
              type="datetime"
              placeholder="开始时间"
              style="width: 100%"
            />
          </el-col>
          <el-col :span="2" style="text-align: center">至</el-col>
          <el-col :span="11">
            <el-date-picker
              v-model="form.effectiveEnd"
              type="datetime"
              placeholder="结束时间"
              style="width: 100%"
            />
          </el-col>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
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
import { Plus } from '@element-plus/icons-vue'
import request from '@/utils/request'

// 数据定义
const loading = ref(false)
const tableData = ref<any[]>([])
const strategyList = ref<any[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增白名单')
const formRef = ref()

const searchForm = reactive({
  strategyId: null as number | null,
  whitelistType: '',
  status: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const form = reactive({
  whitelistId: null as number | null,
  strategyId: null as number | null,
  whitelistType: 'USER',
  userId: null as number | null,
  roleId: null as number | null,
  effectiveStart: null as string | null,
  effectiveEnd: null as string | null,
  remark: ''
})

const rules = {
  strategyId: [{ required: true, message: '请选择脱敏策略', trigger: 'change' }],
  whitelistType: [{ required: true, message: '请选择白名单类型', trigger: 'change' }],
  userId: [{ required: true, message: '请输入用户ID', trigger: 'blur' }],
  roleId: [{ required: true, message: '请输入角色ID', trigger: 'blur' }]
}

// 方法
const loadStrategyList = async () => {
  try {
    const res = await request.get('/mask-strategy/list')
    if (res.data) {
      strategyList.value = res.data
    }
  } catch (error) {
    console.error('加载策略列表失败:', error)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const params: any = {
      current: pagination.current,
      size: pagination.size
    }
    if (searchForm.strategyId) params.strategyId = searchForm.strategyId
    if (searchForm.whitelistType) params.whitelistType = searchForm.whitelistType
    if (searchForm.status) params.status = searchForm.status

    const res = await request.get('/mask-strategy/whitelist/page', { params })
    if (res.data) {
      tableData.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const getStrategyName = (strategyId: number) => {
  const strategy = strategyList.value.find(s => s.strategyId === strategyId)
  return strategy ? strategy.sensitiveType : strategyId
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.strategyId = null
  searchForm.whitelistType = ''
  searchForm.status = ''
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增白名单'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑白名单'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该白名单吗？', '提示', {
      type: 'warning'
    })
    await request.delete(`/mask-strategy/whitelist/${row.whitelistId}`)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleStatusChange = async (row: any) => {
  try {
    await request.put(`/mask-strategy/whitelist/${row.whitelistId}/status`, null, {
      params: { status: row.status }
    })
    ElMessage.success('状态更新成功')
  } catch (error) {
    ElMessage.error('状态更新失败')
    row.status = row.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    const data = { ...form }
    if (form.whitelistId) {
      await request.put('/mask-strategy/whitelist', data)
      ElMessage.success('更新成功')
    } else {
      await request.post('/mask-strategy/whitelist', data)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleDialogClose = () => {
  formRef.value?.resetFields()
}

const resetForm = () => {
  form.whitelistId = null
  form.strategyId = null
  form.whitelistType = 'USER'
  form.userId = null
  form.roleId = null
  form.effectiveStart = null
  form.effectiveEnd = null
  form.remark = ''
}

// 生命周期
onMounted(() => {
  loadStrategyList()
  loadData()
})
</script>

<style scoped lang="scss">
.mask-whitelist-container {
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
