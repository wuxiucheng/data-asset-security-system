<template>
  <div class="grading-container">
    <!-- 搜索表单 -->
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="分级名称">
          <el-input v-model="searchForm.gradingName" placeholder="请输入分级名称" clearable />
        </el-form-item>
        <el-form-item label="所属标准">
          <el-select v-model="searchForm.standardId" placeholder="请选择标准" clearable style="width: 180px">
            <el-option
              v-for="item in standardList"
              :key="item.standardId"
              :label="item.standardName"
              :value="item.standardId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 180px">
            <el-option label="启用" value="ACTIVE" />
            <el-option label="禁用" value="INACTIVE" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon> 搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon> 重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作按钮 -->
    <div class="toolbar">
      <el-button type="primary" @click="handleAdd" :disabled="!searchForm.standardId">
        <el-icon><Plus /></el-icon> 新增分级
      </el-button>
    </div>

    <!-- 分级列表 -->
    <el-card class="table-card">
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column type="index" label="序号" width="80" />
        <el-table-column prop="gradingCode" label="分级编码" width="120" />
        <el-table-column prop="gradingName" label="分级名称" width="150" />
        <el-table-column prop="levelValue" label="等级值" width="100">
          <template #default="{ row }">
            <el-tag :type="getGradingTagType(row.levelValue)">
              L{{ row.levelValue }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="gradingDescription" label="分级描述" show-overflow-tooltip />
        <el-table-column prop="securityMeasures" label="安全措施" show-overflow-tooltip />
        <el-table-column prop="accessControl" label="访问控制" show-overflow-tooltip />
        <el-table-column prop="retentionPeriod" label="保留期限" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'">
              {{ row.status === 'ACTIVE' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">
              <el-icon><Edit /></el-icon> 编辑
            </el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">
              <el-icon><Delete /></el-icon> 删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        :total="pagination.total"
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        class="pagination"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      @close="handleDialogClose"
    >
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="120px">
        <el-form-item label="所属标准" prop="standardId">
          <el-select v-model="formData.standardId" placeholder="请选择标准" style="width: 100%">
            <el-option
              v-for="item in standardList"
              :key="item.standardId"
              :label="item.standardName"
              :value="item.standardId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="分级编码" prop="gradingCode">
          <el-input v-model="formData.gradingCode" placeholder="请输入分级编码，如：L1" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="分级名称" prop="gradingName">
          <el-input v-model="formData.gradingName" placeholder="请输入分级名称，如：一级" />
        </el-form-item>
        <el-form-item label="等级值" prop="levelValue">
          <el-input-number
            v-model="formData.levelValue"
            :min="1"
            :max="10"
            placeholder="请输入等级值（1-10）"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="分级描述" prop="gradingDescription">
          <el-input
            v-model="formData.gradingDescription"
            type="textarea"
            :rows="3"
            placeholder="请输入分级描述"
          />
        </el-form-item>
        <el-form-item label="安全措施" prop="securityMeasures">
          <el-input
            v-model="formData.securityMeasures"
            type="textarea"
            :rows="2"
            placeholder="请输入安全措施"
          />
        </el-form-item>
        <el-form-item label="访问控制" prop="accessControl">
          <el-input
            v-model="formData.accessControl"
            type="textarea"
            :rows="2"
            placeholder="请输入访问控制策略"
          />
        </el-form-item>
        <el-form-item label="保留期限" prop="retentionPeriod">
          <el-input v-model="formData.retentionPeriod" placeholder="请输入保留期限，如：永久、5年" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio value="ACTIVE">启用</el-radio>
            <el-radio value="INACTIVE">禁用</el-radio>
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
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { dataGradingApi, gradingStandardApi } from '@/api'

// 搜索表单
const searchForm = reactive({
  gradingName: '',
  standardId: null,
  status: ''
})

// 表格数据
const tableData = ref([])
const standardList = ref([])
const loading = ref(false)

// 分页
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('新增分级')
const isEdit = ref(false)
const formRef = ref()

// 表单数据
const formData = reactive({
  gradingId: 0,
  standardId: null,
  gradingCode: '',
  gradingName: '',
  levelValue: 1,
  gradingDescription: '',
  securityMeasures: '',
  accessControl: '',
  retentionPeriod: '',
  status: 'ACTIVE'
})

// 表单验证规则
const formRules = {
  standardId: [
    { required: true, message: '请选择所属标准', trigger: 'change' }
  ],
  gradingCode: [
    { required: true, message: '请输入分级编码', trigger: 'blur' }
  ],
  gradingName: [
    { required: true, message: '请输入分级名称', trigger: 'blur' }
  ],
  levelValue: [
    { required: true, message: '请输入等级值', trigger: 'blur' }
  ],
  gradingDescription: [
    { required: true, message: '请输入分级描述', trigger: 'blur' }
  ]
}

// 获取标准列表
const getStandardList = async () => {
  try {
    const res = await gradingStandardApi.getList({ pageNum: 1, pageSize: 1000 })
    standardList.value = res.data.list.filter((item: any) => item.status === 'ACTIVE')
  } catch (error) {
    ElMessage.error('获取标准列表失败')
  }
}

// 获取分级列表
const getGradingList = async () => {
  if (!searchForm.standardId) {
    ElMessage.warning('请先选择所属标准')
    return
  }
  loading.value = true
  try {
    // 过滤空值参数
    const params: any = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      standardId: searchForm.standardId
    }
    if (searchForm.gradingName) params.gradingName = searchForm.gradingName
    if (searchForm.status) params.status = searchForm.status

    const res = await dataGradingApi.getList(params)
    tableData.value = res.data.list
    pagination.total = res.data.total
  } catch (error) {
    ElMessage.error('获取分级列表失败')
  } finally {
    loading.value = false
  }
}

// 获取分级标签类型
const getGradingTagType = (levelValue: number) => {
  if (levelValue === 1) return 'info'
  if (levelValue === 2) return 'success'
  if (levelValue === 3) return 'warning'
  if (levelValue >= 4) return 'danger'
  return 'info'
}

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1
  getGradingList()
}

// 重置
const handleReset = () => {
  searchForm.gradingName = ''
  searchForm.status = ''
  handleSearch()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增分级'
  isEdit.value = false
  Object.assign(formData, {
    gradingId: 0,
    standardId: searchForm.standardId,
    gradingCode: '',
    gradingName: '',
    levelValue: 1,
    gradingDescription: '',
    securityMeasures: '',
    accessControl: '',
    retentionPeriod: '',
    status: 'ACTIVE'
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑分级'
  isEdit.value = true
  Object.assign(formData, row)
  dialogVisible.value = true
}

// 删除
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该分级吗？', '提示', {
      type: 'warning'
    })
    await dataGradingApi.delete(row.gradingId)
    ElMessage.success('删除成功')
    getGradingList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    if (isEdit.value) {
      await dataGradingApi.update(formData)
      ElMessage.success('更新成功')
    } else {
      await dataGradingApi.create(formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    getGradingList()
  } catch (error) {
    if (error !== false) {
      ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
    }
  }
}

// 关闭对话框
const handleDialogClose = () => {
  formRef.value?.resetFields()
}

// 分页变化
const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  getGradingList()
}

const handleCurrentChange = (page: number) => {
  pagination.pageNum = page
  getGradingList()
}

// 监听分页变化
watch(() => pagination.pageNum, () => {
  getGradingList()
})

watch(() => pagination.pageSize, () => {
  getGradingList()
})

// 初始化
onMounted(() => {
  getStandardList()
})
</script>

<style scoped>
.grading-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.toolbar {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
