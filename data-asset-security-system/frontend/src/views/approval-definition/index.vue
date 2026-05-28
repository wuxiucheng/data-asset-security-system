<template>
  <div class="approval-definition-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>流程定义管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新建流程
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="流程名称">
          <el-input v-model="searchForm.name" placeholder="请输入流程名称" clearable />
        </el-form-item>
        <el-form-item label="流程分类">
          <el-select v-model="searchForm.category" placeholder="请选择" clearable style="width: 150px">
            <el-option label="全部" value="" />
            <el-option label="数据审批" value="data" />
            <el-option label="资产审批" value="asset" />
            <el-option label="权限审批" value="permission" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 150px">
            <el-option label="全部" value="" />
            <el-option label="已发布" value="published" />
            <el-option label="未发布" value="draft" />
            <el-option label="已停用" value="disabled" />
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
        <el-table-column prop="name" label="流程名称" width="200" />
        <el-table-column prop="category" label="流程分类" width="120">
          <template #default="{ row }">
            <el-tag>{{ getCategoryName(row.category) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="version" label="版本" width="80" />
        <el-table-column prop="nodeCount" label="节点数" width="100">
          <template #default="{ row }">
            <el-tag type="info">{{ row.nodeCount }} 个</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)">{{ getStatusName(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleDesign(row)">设计</el-button>
            <el-button type="primary" link @click="handleEdit(row)" v-if="row.status === 'draft'">编辑</el-button>
            <el-button type="success" link @click="handlePublish(row)" v-if="row.status === 'draft'">发布</el-button>
            <el-button type="warning" link @click="handleDisable(row)" v-if="row.status === 'published'">停用</el-button>
            <el-button type="success" link @click="handleEnable(row)" v-if="row.status === 'disabled'">启用</el-button>
            <el-button type="danger" link @click="handleDelete(row)" v-if="row.status === 'draft'">删除</el-button>
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
        <el-form-item label="流程名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入流程名称" />
        </el-form-item>
        <el-form-item label="流程分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择流程分类" style="width: 100%">
            <el-option label="数据审批" value="data" />
            <el-option label="资产审批" value="asset" />
            <el-option label="权限审批" value="permission" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="流程描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入流程描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 流程设计对话框 -->
    <el-dialog v-model="designDialogVisible" title="流程设计" width="900px">
      <el-alert type="info" :closable="false" style="margin-bottom: 20px">
        <template #title>流程设计器开发中</template>
        <p style="margin-top: 8px">将支持可视化拖拽设计审批流程节点和连线。</p>
      </el-alert>
      <el-empty description="流程设计器开发中" />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

defineOptions({
  name: 'ApprovalDefinition'
})

// 搜索表单
const searchForm = reactive({
  name: '',
  category: '',
  status: ''
})

// 表格数据
const tableData = ref([
  {
    id: 1,
    name: '数据分类审批流程',
    category: 'data',
    version: 'v1.0',
    nodeCount: 3,
    description: '数据分类标准发布审批流程',
    status: 'published',
    createTime: '2026-05-28 10:00:00'
  },
  {
    id: 2,
    name: '数据分级审批流程',
    category: 'data',
    version: 'v1.0',
    nodeCount: 4,
    description: '数据分级标准发布审批流程',
    status: 'published',
    createTime: '2026-05-28 10:00:00'
  },
  {
    id: 3,
    name: '资产导入审批流程',
    category: 'asset',
    version: 'v1.0',
    nodeCount: 2,
    description: '数据资产批量导入审批流程',
    status: 'draft',
    createTime: '2026-05-28 11:00:00'
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
const dialogTitle = ref('新建流程')
const formRef = ref()
const form = reactive({
  id: null,
  name: '',
  category: 'data',
  description: ''
})

const rules = {
  name: [{ required: true, message: '请输入流程名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择流程分类', trigger: 'change' }]
}

// 流程设计对话框
const designDialogVisible = ref(false)

// 映射
const categoryMap: Record<string, string> = {
  data: '数据审批',
  asset: '资产审批',
  permission: '权限审批',
  other: '其他'
}

const statusMap: Record<string, string> = {
  published: '已发布',
  draft: '未发布',
  disabled: '已停用'
}

const getCategoryName = (category: string) => categoryMap[category] || category
const getStatusName = (status: string) => statusMap[status] || status

const getStatusTag = (status: string) => {
  const tagMap: Record<string, string> = {
    published: 'success',
    draft: 'info',
    disabled: 'danger'
  }
  return tagMap[status] || ''
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
  searchForm.category = ''
  searchForm.status = ''
  handleSearch()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新建流程'
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑流程'
  Object.assign(form, row)
  dialogVisible.value = true
}

// 设计
const handleDesign = (row: any) => {
  designDialogVisible.value = true
}

// 发布
const handlePublish = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要发布该流程吗？', '提示', { type: 'warning' })
    row.status = 'published'
    ElMessage.success('流程发布成功')
  } catch (error) {}
}

// 停用
const handleDisable = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要停用该流程吗？', '提示', { type: 'warning' })
    row.status = 'disabled'
    ElMessage.success('流程已停用')
  } catch (error) {}
}

// 启用
const handleEnable = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要启用该流程吗？', '提示', { type: 'warning' })
    row.status = 'published'
    ElMessage.success('流程已启用')
  } catch (error) {}
}

// 删除
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该流程吗？', '提示', { type: 'warning' })
    const index = tableData.value.findIndex(item => item.id === row.id)
    if (index > -1) {
      tableData.value.splice(index, 1)
      ElMessage.success('删除成功')
    }
  } catch (error) {}
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()

    if (form.id) {
      const index = tableData.value.findIndex(item => item.id === form.id)
      if (index > -1) {
        tableData.value[index] = { ...tableData.value[index], ...form }
      }
      ElMessage.success('编辑成功')
    } else {
      tableData.value.push({
        ...form,
        id: Date.now(),
        version: 'v1.0',
        nodeCount: 0,
        status: 'draft',
        createTime: new Date().toLocaleString()
      })
      ElMessage.success('新建成功')
    }

    dialogVisible.value = false
  } catch (error) {}
}

// 关闭对话框
const handleDialogClose = () => {
  formRef.value?.resetFields()
  Object.assign(form, {
    id: null,
    name: '',
    category: 'data',
    description: ''
  })
}
</script>

<style scoped lang="scss">
.approval-definition-container {
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
