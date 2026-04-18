<template>
  <div class="owner-container">
    <!-- 搜索表单 -->
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="责任人姓名">
          <el-input v-model="searchForm.name" placeholder="请输入责任人姓名" clearable />
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
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon> 新增责任人
      </el-button>
    </div>

    <!-- 责任人列表 -->
    <el-card class="table-card">
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="ownerId" label="ID" width="80" />
        <el-table-column prop="employeeNo" label="工号" width="120" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="departmentId" label="部门ID" width="100" />
        <el-table-column prop="position" label="职位" width="150" />
        <el-table-column prop="contactPhone" label="联系电话" width="150" />
        <el-table-column prop="email" label="邮箱" width="200" />
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
      width="600px"
      @close="handleDialogClose"
    >
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="工号" prop="employeeNo">
          <el-input v-model="formData.employeeNo" placeholder="请输入工号" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="formData.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="部门" prop="departmentId">
          <el-input v-model="formData.departmentId" placeholder="请输入部门ID" />
        </el-form-item>
        <el-form-item label="职位" prop="position">
          <el-input v-model="formData.position" placeholder="请输入职位" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="formData.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
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
import { ownerApi } from '@/api'

// 搜索表单
const searchForm = reactive({
  name: '',
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
const dialogTitle = ref('新增责任人')
const isEdit = ref(false)
const formRef = ref()

// 表单数据
const formData = reactive({
  ownerId: 0,
  employeeNo: '',
  name: '',
  departmentId: null,
  position: '',
  contactPhone: '',
  email: '',
  status: 'ACTIVE'
})

// 表单验证规则
const formRules = {
  employeeNo: [
    { required: true, message: '请输入工号', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

// 获取责任人列表
const getOwnerList = async () => {
  loading.value = true
  try {
    // 过滤空值参数
    const params: any = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    if (searchForm.name) params.name = searchForm.name
    if (searchForm.status) params.status = searchForm.status

    const res = await ownerApi.getList(params)
    tableData.value = res.data.list
    pagination.total = res.data.total
  } catch (error) {
    ElMessage.error('获取责任人列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1
  getOwnerList()
}

// 重置
const handleReset = () => {
  searchForm.name = ''
  searchForm.status = ''
  handleSearch()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增责任人'
  isEdit.value = false
  Object.assign(formData, {
    ownerId: 0,
    employeeNo: '',
    name: '',
    departmentId: null,
    position: '',
    contactPhone: '',
    email: '',
    status: 'ACTIVE'
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑责任人'
  isEdit.value = true
  Object.assign(formData, row)
  dialogVisible.value = true
}

// 删除
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该责任人吗？', '提示', {
      type: 'warning'
    })
    await ownerApi.delete(row.ownerId)
    ElMessage.success('删除成功')
    getOwnerList()
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
      await ownerApi.update(formData)
      ElMessage.success('更新成功')
    } else {
      await ownerApi.create(formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    getOwnerList()
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
  getOwnerList()
}

const handleCurrentChange = (page: number) => {
  pagination.pageNum = page
  getOwnerList()
}

// 监听分页变化
watch(() => pagination.pageNum, () => {
  getOwnerList()
})

watch(() => pagination.pageSize, () => {
  getOwnerList()
})

// 初始化
onMounted(() => {
  getOwnerList()
})
</script>

<style scoped>
.owner-container {
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
