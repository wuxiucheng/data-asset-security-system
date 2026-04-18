<template>
  <div class="grading-standard-container">
    <!-- 搜索表单 -->
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="标准名称">
          <el-input v-model="searchForm.standardName" placeholder="请输入标准名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="草稿" value="DRAFT" />
            <el-option label="已发布" value="ACTIVE" />
            <el-option label="已停用" value="INACTIVE" />
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
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon> 新增分级标准
      </el-button>
    </div>

    <!-- 分级标准列表 -->
    <el-card class="table-card">
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="standardId" label="ID" width="80" />
        <el-table-column prop="standardCode" label="标准编码" width="150" />
        <el-table-column prop="standardName" label="标准名称" width="200" />
        <el-table-column prop="version" label="版本" width="100" />
        <el-table-column prop="publishDate" label="发布日期" width="150" />
        <el-table-column prop="standardDescription" label="标准描述" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'DRAFT'" type="info">草稿</el-tag>
            <el-tag v-else-if="row.status === 'ACTIVE'" type="success">已发布</el-tag>
            <el-tag v-else type="warning">已停用</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">
              <el-icon><Edit /></el-icon> 编辑
            </el-button>
            <el-button
              v-if="row.status === 'DRAFT'"
              type="success"
              link
              size="small"
              @click="handlePublish(row)"
            >
              <el-icon><Promotion /></el-icon> 发布
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
      width="600px"
      @close="handleDialogClose"
    >
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="标准编码" prop="standardCode">
          <el-input v-model="formData.standardCode" placeholder="请输入标准编码" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="标准名称" prop="standardName">
          <el-input v-model="formData.standardName" placeholder="请输入标准名称" />
        </el-form-item>
        <el-form-item label="版本" prop="version">
          <el-input v-model="formData.version" placeholder="请输入版本号，如：1.0" />
        </el-form-item>
        <el-form-item label="发布日期" prop="publishDate">
          <el-date-picker
            v-model="formData.publishDate"
            type="date"
            placeholder="请选择发布日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="标准描述" prop="standardDescription">
          <el-input
            v-model="formData.standardDescription"
            type="textarea"
            :rows="3"
            placeholder="请输入标准描述"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio value="DRAFT">草稿</el-radio>
            <el-radio value="ACTIVE">已发布</el-radio>
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
import { Search, Refresh, Plus, Edit, Delete, Promotion } from '@element-plus/icons-vue'
import { gradingStandardApi } from '@/api'

// 搜索表单
const searchForm = reactive({
  standardName: '',
  status: ''
})

// 表格数据
const tableData = ref([])
const loading = ref(false)

// 分页
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('新增分级标准')
const isEdit = ref(false)
const formRef = ref()

// 表单数据
const formData = reactive({
  standardId: 0,
  standardCode: '',
  standardName: '',
  version: '',
  publishDate: '',
  standardDescription: '',
  status: 'DRAFT'
})

// 表单验证规则
const formRules = {
  standardCode: [
    { required: true, message: '请输入标准编码', trigger: 'blur' }
  ],
  standardName: [
    { required: true, message: '请输入标准名称', trigger: 'blur' }
  ],
  version: [
    { required: true, message: '请输入版本号', trigger: 'blur' }
  ]
}

// 获取分级标准列表
const getGradingStandardList = async () => {
  loading.value = true
  try {
    const res = await gradingStandardApi.getList({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...searchForm
    })
    tableData.value = res.data.list
    pagination.total = res.data.total
  } catch (error) {
    ElMessage.error('获取分级标准列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1
  getGradingStandardList()
}

// 重置
const handleReset = () => {
  searchForm.standardName = ''
  searchForm.status = ''
  handleSearch()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增分级标准'
  isEdit.value = false
  Object.assign(formData, {
    standardId: 0,
    standardCode: '',
    standardName: '',
    version: '',
    publishDate: '',
    standardDescription: '',
    status: 'DRAFT'
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑分级标准'
  isEdit.value = true
  Object.assign(formData, row)
  dialogVisible.value = true
}

// 发布
const handlePublish = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要发布该分级标准吗？', '提示', {
      type: 'warning'
    })
    await gradingStandardApi.publish(row.standardId)
    ElMessage.success('发布成功')
    getGradingStandardList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('发布失败')
    }
  }
}

// 删除
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该分级标准吗？', '提示', {
      type: 'warning'
    })
    await gradingStandardApi.delete(row.standardId)
    ElMessage.success('删除成功')
    getGradingStandardList()
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
      await gradingStandardApi.update(formData)
      ElMessage.success('更新成功')
    } else {
      await gradingStandardApi.create(formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    getGradingStandardList()
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
  getGradingStandardList()
}

const handleCurrentChange = (page: number) => {
  pagination.pageNum = page
  getGradingStandardList()
}

// 监听分页变化
watch(() => pagination.pageNum, () => {
  getGradingStandardList()
})

watch(() => pagination.pageSize, () => {
  getGradingStandardList()
})

// 初始化
onMounted(() => {
  getGradingStandardList()
})
</script>

<style scoped>
.grading-standard-container {
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
