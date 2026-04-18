<template>
  <div class="asset-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>数据资产管理</span>
          <div>
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新增资产
            </el-button>
            <el-button type="success" @click="handleImport">
              <el-icon><Upload /></el-icon>
              批量导入
            </el-button>
            <el-button type="warning" @click="handleExport">
              <el-icon><Download /></el-icon>
              批量导出
            </el-button>
          </div>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="资产名称">
          <el-input v-model="queryParams.assetName" placeholder="请输入资产名称" clearable />
        </el-form-item>
        <el-form-item label="资产编码">
          <el-input v-model="queryParams.assetCode" placeholder="请输入资产编码" clearable />
        </el-form-item>
        <el-form-item label="资产类型">
          <el-select v-model="queryParams.assetType" placeholder="请选择资产类型" clearable>
            <el-option label="数据库" value="DATABASE" />
            <el-option label="文件" value="FILE" />
            <el-option label="API" value="API" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="草稿" value="DRAFT" />
            <el-option label="启用" value="ACTIVE" />
            <el-option label="停用" value="INACTIVE" />
            <el-option label="归档" value="ARCHIVED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="assetList" v-loading="loading" border>
        <el-table-column prop="assetId" label="资产ID" width="80" />
        <el-table-column prop="assetCode" label="资产编码" width="120" />
        <el-table-column prop="assetName" label="资产名称" width="150" />
        <el-table-column prop="assetType" label="资产类型" width="100">
          <template #default="{ row }">
            <el-tag>{{ getAssetTypeName(row.assetType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="systemName" label="所属系统" width="120" />
        <el-table-column prop="databaseType" label="数据库类型" width="100" />
        <el-table-column prop="databaseName" label="数据库名称" width="120" />
        <el-table-column prop="tableName" label="表名" width="120" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="importanceLevel" label="重要性" width="80">
          <template #default="{ row }">
            <el-tag :type="getImportanceType(row.importanceLevel)">
              {{ getImportanceName(row.importanceLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="containsSensitiveData" label="敏感数据" width="80">
          <template #default="{ row }">
            <el-tag :type="row.containsSensitiveData ? 'danger' : 'success'">
              {{ row.containsSensitiveData ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        :total="total"
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 资产表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      @close="handleDialogClose"
    >
      <el-form ref="assetFormRef" :model="assetForm" :rules="assetRules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="资产名称" prop="assetName">
              <el-input v-model="assetForm.assetName" placeholder="请输入资产名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="资产编码" prop="assetCode">
              <el-input v-model="assetForm.assetCode" placeholder="请输入资产编码" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="资产类型" prop="assetType">
              <el-select v-model="assetForm.assetType" placeholder="请选择资产类型" style="width: 100%">
                <el-option label="数据库" value="DATABASE" />
                <el-option label="文件" value="FILE" />
                <el-option label="API" value="API" />
                <el-option label="其他" value="OTHER" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属系统" prop="systemName">
              <el-input v-model="assetForm.systemName" placeholder="请输入所属系统" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="数据库类型" prop="databaseType">
              <el-select v-model="assetForm.databaseType" placeholder="请选择数据库类型" style="width: 100%">
                <el-option label="MySQL" value="MYSQL" />
                <el-option label="Oracle" value="ORACLE" />
                <el-option label="PostgreSQL" value="POSTGRESQL" />
                <el-option label="SQL Server" value="SQLSERVER" />
                <el-option label="其他" value="OTHER" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="数据库地址" prop="databaseHost">
              <el-input v-model="assetForm.databaseHost" placeholder="请输入数据库地址" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="数据库名称" prop="databaseName">
              <el-input v-model="assetForm.databaseName" placeholder="请输入数据库名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="表名" prop="tableName">
              <el-input v-model="assetForm.tableName" placeholder="请输入表名" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="资产描述" prop="assetDescription">
          <el-input v-model="assetForm.assetDescription" type="textarea" :rows="3" placeholder="请输入资产描述" />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="责任部门" prop="departmentId">
              <el-input v-model="assetForm.departmentId" placeholder="请输入责任部门ID" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="责任人" prop="ownerId">
              <el-input v-model="assetForm.ownerId" placeholder="请输入责任人ID" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="数据分类" prop="classificationId">
              <el-input v-model="assetForm.classificationId" placeholder="请输入分类ID" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="数据分级" prop="gradingId">
              <el-input v-model="assetForm.gradingId" placeholder="请输入分级ID" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="数据重要性" prop="importanceLevel">
              <el-select v-model="assetForm.importanceLevel" placeholder="请选择数据重要性" style="width: 100%">
                <el-option label="低" value="LOW" />
                <el-option label="中" value="MEDIUM" />
                <el-option label="高" value="HIGH" />
                <el-option label="核心" value="CRITICAL" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="敏感数据" prop="containsSensitiveData">
              <el-switch v-model="assetForm.containsSensitiveData" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="assetForm.status">
            <el-radio value="DRAFT">草稿</el-radio>
            <el-radio value="ACTIVE">启用</el-radio>
            <el-radio value="INACTIVE">停用</el-radio>
            <el-radio value="ARCHIVED">归档</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="备注" prop="remarks">
          <el-input v-model="assetForm.remarks" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 导入对话框 -->
    <el-dialog v-model="importDialogVisible" title="批量导入" width="500px">
      <el-upload
        ref="uploadRef"
        :auto-upload="false"
        :limit="1"
        accept=".xlsx,.xls"
        @change="handleFileChange"
      >
        <el-button type="primary">选择文件</el-button>
        <template #tip>
          <div class="el-upload__tip">
            只能上传 xlsx/xls 文件，且不超过 10MB
          </div>
        </template>
      </el-upload>
      <template #footer>
        <el-button @click="importDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleImportSubmit" :loading="importLoading">导入</el-button>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog v-model="viewDialogVisible" title="资产详情" width="800px">
      <el-descriptions :column="2" border v-if="currentAsset">
        <el-descriptions-item label="资产ID">{{ currentAsset.assetId }}</el-descriptions-item>
        <el-descriptions-item label="资产名称">{{ currentAsset.assetName }}</el-descriptions-item>
        <el-descriptions-item label="资产编码">{{ currentAsset.assetCode }}</el-descriptions-item>
        <el-descriptions-item label="资产类型">
          <el-tag v-if="currentAsset.assetType === 'DATABASE'" type="primary">数据库</el-tag>
          <el-tag v-else-if="currentAsset.assetType === 'FILE'" type="success">文件</el-tag>
          <el-tag v-else-if="currentAsset.assetType === 'API'" type="warning">API</el-tag>
          <el-tag v-else type="info">其他</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="系统名称">{{ currentAsset.systemName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="数据库类型">{{ currentAsset.databaseType || '-' }}</el-descriptions-item>
        <el-descriptions-item label="数据库名称">{{ currentAsset.databaseName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="表名称">{{ currentAsset.tableName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="所属部门">{{ currentAsset.departmentName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="责任人">{{ currentAsset.ownerName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="数据分类">
          <el-tag type="success">{{ currentAsset.classificationName || '-' }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="数据分级">
          <el-tag :type="getGradingTagType(currentAsset.gradingName)">{{ currentAsset.gradingName || '-' }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="敏感数据">
          <el-tag :type="currentAsset.containsSensitiveData ? 'warning' : 'info'">
            {{ currentAsset.containsSensitiveData ? '包含' : '不包含' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="敏感类型">{{ currentAsset.sensitiveDataType || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusTagType(currentAsset.status)">
            {{ getStatusText(currentAsset.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentAsset.createTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ currentAsset.updateTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="资产描述" :span="2">{{ currentAsset.assetDescription || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleEdit(currentAsset)">编辑</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus, Search, Refresh, Upload, Download } from '@element-plus/icons-vue'
import { dataAssetApi, type DataAsset } from '@/api'

// 查询参数
const queryParams = reactive({
  assetName: '',
  assetCode: '',
  assetType: '',
  status: '',
  pageNum: 1,
  pageSize: 10,
})

// 资产列表
const assetList = ref<DataAsset[]>([])
const total = ref(0)
const loading = ref(false)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('新增资产')
const submitLoading = ref(false)

// 导入对话框
const importDialogVisible = ref(false)
const importLoading = ref(false)
const uploadRef = ref()
const importFile = ref<File | null>(null)

// 查看详情对话框
const viewDialogVisible = ref(false)
const currentAsset = ref<DataAsset | null>(null)

// 资产表单
const assetFormRef = ref<FormInstance>()
const assetForm = reactive({
  assetId: 0,
  assetName: '',
  assetCode: '',
  assetType: 'DATABASE',
  systemName: '',
  databaseType: 'MYSQL',
  databaseHost: '',
  databasePort: 3306,
  databaseName: '',
  tableName: '',
  assetDescription: '',
  departmentId: 0,
  ownerId: 0,
  classificationId: 0,
  gradingId: 0,
  importanceLevel: 'MEDIUM',
  containsSensitiveData: false,
  status: 'DRAFT',
  remarks: '',
})

// 表单验证规则
const assetRules: FormRules = {
  assetName: [
    { required: true, message: '请输入资产名称', trigger: 'blur' },
  ],
  assetCode: [
    { required: true, message: '请输入资产编码', trigger: 'blur' },
  ],
  assetType: [
    { required: true, message: '请选择资产类型', trigger: 'change' },
  ],
}

// 加载资产列表
const loadAssetList = async () => {
  loading.value = true
  try {
    const res = await dataAssetApi.getList(queryParams)
    assetList.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    console.error('加载资产列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  queryParams.pageNum = 1
  loadAssetList()
}

// 重置
const handleReset = () => {
  queryParams.assetName = ''
  queryParams.assetCode = ''
  queryParams.assetType = ''
  queryParams.status = ''
  queryParams.pageNum = 1
  loadAssetList()
}

// 新增资产
const handleAdd = () => {
  dialogTitle.value = '新增资产'
  dialogVisible.value = true
}

// 查看资产
const handleView = (row: DataAsset) => {
  currentAsset.value = { ...row }
  viewDialogVisible.value = true
}

// 编辑资产
const handleEdit = (row: DataAsset) => {
  dialogTitle.value = '编辑资产'
  Object.assign(assetForm, row)
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  if (!assetFormRef.value) return

  await assetFormRef.value.validate(async (valid) => {
    if (!valid) return

    submitLoading.value = true
    try {
      if (dialogTitle.value === '新增资产') {
        await dataAssetApi.create(assetForm)
        ElMessage.success('新增资产成功')
      } else {
        await dataAssetApi.update(assetForm)
        ElMessage.success('更新资产成功')
      }

      dialogVisible.value = false
      loadAssetList()
    } catch (error) {
      console.error('提交失败:', error)
    } finally {
      submitLoading.value = false
    }
  })
}

// 删除资产
const handleDelete = async (row: DataAsset) => {
  try {
    await ElMessageBox.confirm(`确定要删除资产"${row.assetName}"吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    await dataAssetApi.delete(row.assetId)
    ElMessage.success('删除资产成功')
    loadAssetList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除资产失败:', error)
    }
  }
}

// 批量导入
const handleImport = () => {
  importDialogVisible.value = true
}

// 文件变化
const handleFileChange = (file: any) => {
  importFile.value = file.raw
}

// 导入提交
const handleImportSubmit = async () => {
  if (!importFile.value) {
    ElMessage.warning('请选择要导入的文件')
    return
  }

  importLoading.value = true
  try {
    await dataAssetApi.import(importFile.value)
    ElMessage.success('导入成功')
    importDialogVisible.value = false
    loadAssetList()
  } catch (error) {
    console.error('导入失败:', error)
  } finally {
    importLoading.value = false
  }
}

// 批量导出
const handleExport = async () => {
  try {
    await dataAssetApi.export(queryParams)
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
  }
}

// 对话框关闭
const handleDialogClose = () => {
  assetFormRef.value?.resetFields()
  Object.assign(assetForm, {
    assetId: 0,
    assetName: '',
    assetCode: '',
    assetType: 'DATABASE',
    systemName: '',
    databaseType: 'MYSQL',
    databaseHost: '',
    databasePort: 3306,
    databaseName: '',
    tableName: '',
    assetDescription: '',
    departmentId: 0,
    ownerId: 0,
    classificationId: 0,
    gradingId: 0,
    importanceLevel: 'MEDIUM',
    containsSensitiveData: false,
    status: 'DRAFT',
    remarks: '',
  })
}

// 辅助函数
const getAssetTypeName = (type: string) => {
  const map: Record<string, string> = {
    DATABASE: '数据库',
    FILE: '文件',
    API: 'API',
    OTHER: '其他',
  }
  return map[type] || type
}

const getStatusName = (status: string) => {
  const map: Record<string, string> = {
    DRAFT: '草稿',
    ACTIVE: '启用',
    INACTIVE: '停用',
    ARCHIVED: '归档',
  }
  return map[status] || status
}

const getStatusType = (status?: string) => {
  const map: Record<string, string> = {
    DRAFT: 'info',
    ACTIVE: 'success',
    INACTIVE: 'warning',
    ARCHIVED: 'danger',
  }
  return map[status || ''] ?? 'info'
}

const getImportanceName = (importance: string) => {
  const map: Record<string, string> = {
    LOW: '低',
    MEDIUM: '中',
    HIGH: '高',
    CRITICAL: '核心',
  }
  return map[importance] || importance
}

const getImportanceType = (importance: string) => {
  const map: Record<string, any> = {
    LOW: 'info',
    MEDIUM: 'info',   // ✅ 改掉
    HIGH: 'warning',
    CRITICAL: 'danger',
  }
  return map[importance] ?? 'info'
}

// 获取分级标签类型
const getGradingTagType = (gradingName: string) => {
  if (!gradingName) return 'info'
  if (gradingName.includes('一级')) return 'info'   // ✅
  if (gradingName.includes('二级')) return 'success'
  if (gradingName.includes('三级')) return 'warning'
  if (gradingName.includes('四级')) return 'danger'
  return 'info'
}

// 获取状态标签类型
const getStatusTagType = (status: string) => {
  if (!status) return 'info'
  if (status === 'ACTIVE') return 'success'
  if (status === 'DRAFT') return 'info'
  if (status === 'INACTIVE') return 'warning'
  if (status === 'ARCHIVED') return 'info'   // ✅
  return 'info'
}

// 获取状态文本
const getStatusText = (status: string) => {
  if (!status) return '未知'
  const statusMap: Record<string, string> = {
    'ACTIVE': '启用',
    'DRAFT': '草稿',
    'INACTIVE': '停用',
    'ARCHIVED': '归档'
  }
  return statusMap[status] || status
}

onMounted(() => {
  loadAssetList()
})
</script>

<style scoped>
.asset-management {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}
</style>
