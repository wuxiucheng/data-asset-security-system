<template>
  <div class="permission-container">
    <!-- 搜索表单 -->
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="权限名称">
          <el-input v-model="searchForm.permissionName" placeholder="请输入权限名称" clearable />
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

    <!-- 权限树形表格 -->
    <el-card class="table-card">
      <el-table
        :data="tableData"
        v-loading="loading"
        border
        stripe
        row-key="permissionId"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      >
        <el-table-column prop="permissionId" label="ID" width="80" />
        <el-table-column prop="permissionCode" label="权限编码" width="200" />
        <el-table-column prop="permissionName" label="权限名称" width="150" />
        <el-table-column prop="permissionType" label="权限类型" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.permissionType === 'MENU'" type="primary">菜单</el-tag>
            <el-tag v-else-if="row.permissionType === 'BUTTON'" type="success">按钮</el-tag>
            <el-tag v-else type="info">API</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="permissionUrl" label="资源路径" show-overflow-tooltip />
        <el-table-column prop="permissionMethod" label="请求方法" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.permissionMethod === 'GET'" type="success">GET</el-tag>
            <el-tag v-else-if="row.permissionMethod === 'POST'" type="primary">POST</el-tag>
            <el-tag v-else-if="row.permissionMethod === 'PUT'" type="warning">PUT</el-tag>
            <el-tag v-else-if="row.permissionMethod === 'DELETE'" type="danger">DELETE</el-tag>
            <el-tag v-else type="info">{{ row.permissionMethod }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'">
              {{ row.status === 'ACTIVE' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import { permissionApi } from '@/api'

// 搜索表单
const searchForm = reactive({
  permissionName: '',
  status: ''
})

// 表格数据
const tableData = ref([])
const loading = ref(false)

// 获取权限列表
const getPermissionList = async () => {
  loading.value = true
  try {
    // 过滤空值参数
    const params: any = {
      pageNum: 1,
      pageSize: 1000
    }
    if (searchForm.permissionName) params.permissionName = searchForm.permissionName
    if (searchForm.status) params.status = searchForm.status

    const res = await permissionApi.getList(params)
    tableData.value = res.data.list
  } catch (error) {
    ElMessage.error('获取权限列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  getPermissionList()
}

// 重置
const handleReset = () => {
  searchForm.permissionName = ''
  searchForm.status = ''
  handleSearch()
}

// 初始化
onMounted(() => {
  getPermissionList()
})
</script>

<style scoped>
.permission-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}
</style>
