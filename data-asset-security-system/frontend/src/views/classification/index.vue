<template>
  <div class="classification-container">
    <!-- 搜索表单 -->
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="分类名称">
          <el-input v-model="searchForm.classificationName" placeholder="请输入分类名称" clearable />
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
        <el-icon><Plus /></el-icon> 新增分类
      </el-button>
      <el-button type="success" @click="handleTreeView" :disabled="!searchForm.standardId">
        <el-icon><Menu /></el-icon> 树形视图
      </el-button>
    </div>

    <!-- 分类列表 -->
    <el-card class="table-card">
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column type="index" label="序号" width="80" />
        <el-table-column prop="classificationCode" label="分类编码" width="150" />
        <el-table-column prop="classificationName" label="分类名称" width="200" />
        <el-table-column prop="level" label="层级" width="80" />
        <el-table-column prop="parentId" label="父级ID" width="100" />
        <el-table-column prop="classificationDescription" label="分类描述" show-overflow-tooltip />
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

    <!-- 树形视图对话框 -->
    <el-dialog
      v-model="treeDialogVisible"
      title="分类树形视图"
      width="800px"
    >
      <el-tree
        :data="treeData"
        :props="{ label: 'classificationName', children: 'children' }"
        node-key="classificationId"
        default-expand-all
      >
        <template #default="{ node, data }">
          <span class="custom-tree-node">
            <span>{{ node.label }}</span>
            <span>
              <el-tag size="small" type="info">{{ data.classificationCode }}</el-tag>
            </span>
          </span>
        </template>
      </el-tree>
    </el-dialog>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
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
        <el-form-item label="分类编码" prop="classificationCode">
          <el-input v-model="formData.classificationCode" placeholder="请输入分类编码" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="分类名称" prop="classificationName">
          <el-input v-model="formData.classificationName" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="父级分类" prop="parentId">
          <el-tree-select
            v-model="formData.parentId"
            :data="classificationTree"
            :props="{ value: 'classificationId', label: 'classificationName', children: 'children' }"
            placeholder="请选择父级分类（不选则为顶级分类）"
            clearable
            check-strictly
          />
        </el-form-item>
        <el-form-item label="分类描述" prop="classificationDescription">
          <el-input
            v-model="formData.classificationDescription"
            type="textarea"
            :rows="3"
            placeholder="请输入分类描述"
          />
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
import { Search, Refresh, Plus, Edit, Delete, Menu } from '@element-plus/icons-vue'
import { dataClassificationApi, classificationStandardApi } from '@/api'

// 搜索表单
const searchForm = reactive({
  classificationName: '',
  standardId: null,
  status: ''
})

// 表格数据
const tableData = ref([])
const treeData = ref([])
const classificationTree = ref([])
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
const treeDialogVisible = ref(false)
const dialogTitle = ref('新增分类')
const isEdit = ref(false)
const formRef = ref()

// 表单数据
const formData = reactive({
  classificationId: 0,
  standardId: null,
  classificationCode: '',
  classificationName: '',
  parentId: null,
  classificationDescription: '',
  status: 'ACTIVE'
})

// 表单验证规则
const formRules = {
  standardId: [
    { required: true, message: '请选择所属标准', trigger: 'change' }
  ],
  classificationCode: [
    { required: true, message: '请输入分类编码', trigger: 'blur' }
  ],
  classificationName: [
    { required: true, message: '请输入分类名称', trigger: 'blur' }
  ]
}

// 获取标准列表
const getStandardList = async () => {
  try {
    const res = await classificationStandardApi.getList({ pageNum: 1, pageSize: 1000 })
    standardList.value = res.data.list.filter((item: any) => item.status === 'PUBLISHED' || item.status === 'ACTIVE')
  } catch (error) {
    ElMessage.error('获取标准列表失败')
  }
}

// 获取分类列表
const getClassificationList = async () => {
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
    if (searchForm.classificationName) params.classificationName = searchForm.classificationName
    if (searchForm.status) params.status = searchForm.status

    const res = await dataClassificationApi.getList(params)
    tableData.value = res.data.list
    pagination.total = res.data.total
  } catch (error) {
    ElMessage.error('获取分类列表失败')
  } finally {
    loading.value = false
  }
}

// 获取分类树
const getClassificationTree = async () => {
  if (!searchForm.standardId) {
    ElMessage.warning('请先选择所属标准')
    return
  }
  try {
    const res = await dataClassificationApi.getTree(searchForm.standardId)
    treeData.value = res.data
    classificationTree.value = res.data
  } catch (error) {
    ElMessage.error('获取分类树失败')
  }
}

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1
  getClassificationList()
}

// 重置
const handleReset = () => {
  searchForm.classificationName = ''
  searchForm.status = ''
  handleSearch()
}

// 树形视图
const handleTreeView = async () => {
  await getClassificationTree()
  treeDialogVisible.value = true
}

// 新增
const handleAdd = async () => {
  await getClassificationTree()
  dialogTitle.value = '新增分类'
  isEdit.value = false
  Object.assign(formData, {
    classificationId: 0,
    standardId: searchForm.standardId,
    classificationCode: '',
    classificationName: '',
    parentId: null,
    classificationDescription: '',
    status: 'ACTIVE'
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = async (row: any) => {
  await getClassificationTree()
  dialogTitle.value = '编辑分类'
  isEdit.value = true
  Object.assign(formData, row)
  dialogVisible.value = true
}

// 删除
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该分类吗？', '提示', {
      type: 'warning'
    })
    await dataClassificationApi.delete(row.classificationId)
    ElMessage.success('删除成功')
    getClassificationList()
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
      await dataClassificationApi.update(formData)
      ElMessage.success('更新成功')
    } else {
      await dataClassificationApi.create(formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    getClassificationList()
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
// const handleSizeChange = (size: number) => {
//   pagination.pageSize = size
//   getClassificationList()
// }

// const handleCurrentChange = (page: number) => {
//   pagination.pageNum = page
//   getClassificationList()
// }

// 监听分页变化
// watch(() => pagination.pageNum, () => {
//   getClassificationList()
// })

// watch(() => pagination.pageSize, () => {
//   getClassificationList()
// })

watch(
  () => [pagination.pageNum, pagination.pageSize],
  () => {
    getClassificationList()
  }
)

// 初始化
onMounted(() => {
  getStandardList()
})
</script>

<style scoped>
.classification-container {
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

.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-right: 8px;
}
</style>
