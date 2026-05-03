<template>
  <div class="department-container">
    <!-- 搜索表单 -->
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="部门名称">
          <el-input v-model="searchForm.departmentName" placeholder="请输入部门名称" clearable />
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
        <el-icon><Plus /></el-icon> 新增部门
      </el-button>
    </div>

    <!-- 部门树形表格 -->
    <el-card class="table-card">
      <el-table
        :data="tableData"
        v-loading="loading"
        border
        stripe
        row-key="departmentId"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      >
        <el-table-column type="index" label="序号" width="80" />
        <el-table-column prop="departmentCode" label="部门编码" width="150" />
        <el-table-column prop="departmentName" label="部门名称" width="200" />
        <el-table-column prop="leaderId" label="负责人ID" width="120" />
        <el-table-column prop="contactPhone" label="联系电话" width="150" />
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
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="部门编码" prop="departmentCode">
          <el-input v-model="formData.departmentCode" placeholder="请输入部门编码" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="部门名称" prop="departmentName">
          <el-input v-model="formData.departmentName" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="上级部门" prop="parentId">
          <el-tree-select
            v-model="formData.parentId"
            :data="departmentTree"
            :props="{ value: 'departmentId', label: 'departmentName', children: 'children' }"
            placeholder="请选择上级部门"
            clearable
            check-strictly
          />
        </el-form-item>
        <el-form-item label="负责人ID" prop="leaderId">
          <el-input v-model="formData.leaderId" placeholder="请输入负责人ID" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="formData.contactPhone" placeholder="请输入联系电话" />
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { departmentApi } from '@/api'

// 搜索表单
const searchForm = reactive({
  departmentName: '',
  status: ''
})

// 表格数据
const tableData = ref([])
const departmentTree = ref([])
const loading = ref(false)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('新增部门')
const isEdit = ref(false)
const formRef = ref()

// 表单数据
const formData = reactive({
  departmentId: 0,
  departmentCode: '',
  departmentName: '',
  parentId: null,
  leaderId: null,
  contactPhone: '',
  status: 'ACTIVE'
})

// 表单验证规则
const formRules = {
  departmentCode: [
    { required: true, message: '请输入部门编码', trigger: 'blur' }
  ],
  departmentName: [
    { required: true, message: '请输入部门名称', trigger: 'blur' }
  ]
}

// 获取部门树
const getDepartmentTree = async () => {
  loading.value = true
  try {
    // 过滤空值参数
    const params: any = {}
    if (searchForm.departmentName) params.departmentName = searchForm.departmentName
    if (searchForm.status) params.status = searchForm.status

    const res = await departmentApi.getTree(params)
    tableData.value = res.data
    departmentTree.value = res.data
  } catch (error) {
    ElMessage.error('获取部门树失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  getDepartmentTree()
}

// 重置
const handleReset = () => {
  searchForm.departmentName = ''
  searchForm.status = ''
  handleSearch()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增部门'
  isEdit.value = false
  Object.assign(formData, {
    departmentId: 0,
    departmentCode: '',
    departmentName: '',
    parentId: null,
    leaderId: null,
    contactPhone: '',
    status: 'ACTIVE'
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑部门'
  isEdit.value = true
  Object.assign(formData, row)
  dialogVisible.value = true
}

// 删除
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该部门吗？', '提示', {
      type: 'warning'
    })
    await departmentApi.delete(row.departmentId)
    ElMessage.success('删除成功')
    getDepartmentTree()
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
      await departmentApi.update(formData)
      ElMessage.success('更新成功')
    } else {
      await departmentApi.create(formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    getDepartmentTree()
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

// 初始化
onMounted(() => {
  getDepartmentTree()
})
</script>

<style scoped>
.department-container {
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
</style>
