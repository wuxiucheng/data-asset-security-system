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
            <el-button type="info" @click="handleBatchRefresh">
              <el-icon><RefreshRight /></el-icon>
              批量刷新
            </el-button>
            <el-button type="danger" @click="handleBatchDelete" :disabled="selectedRows.length === 0">
              <el-icon><Delete /></el-icon>
              批量删除 ({{ selectedRows.length }})
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
          <el-select v-model="queryParams.assetType" placeholder="请选择资产类型" clearable style="width: 180px">
            <el-option label="数据库" value="DATABASE" />
            <el-option label="表" value="TABLE" />
            <el-option label="文件" value="FILE" />
            <el-option label="API" value="API" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 180px">
            <el-option label="草稿" value="DRAFT" />
            <el-option label="启用" value="ACTIVE" />
            <el-option label="停用" value="INACTIVE" />
            <el-option label="归档" value="ARCHIVED" />
          </el-select>
        </el-form-item>
        <el-form-item label="数据库类型">
          <el-select v-model="queryParams.databaseType" placeholder="请选择" clearable style="width: 180px">
            <el-option label="MySQL" value="MYSQL" />
            <el-option label="Oracle" value="ORACLE" />
            <el-option label="PostgreSQL" value="POSTGRESQL" />
            <el-option label="SQL Server" value="SQLSERVER" />
          </el-select>
        </el-form-item>
        <el-form-item label="数据库名称">
          <el-input v-model="queryParams.databaseName" placeholder="请输入数据库名称" clearable />
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
      <el-table :data="assetList" v-loading="loading" border @selection-change="handleSelectionChange" ref="assetTableRef">
        <el-table-column type="selection" width="55" />
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
        <el-table-column prop="rowCount" label="数据条数" width="120">
          <template #default="{ row }">
            <span v-if="refreshingAssetId === row.assetId">
              <el-icon class="is-loading"><Loading /></el-icon>
            </span>
            <span v-else-if="!canRefreshAsset(row)" style="color: #909399;">不适用</span>
            <span v-else-if="row.rowCount != null">{{ row.rowCount.toLocaleString() }}</span>
            <span v-else style="color: #909399;">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="dataSourceName" label="数据源" width="120">
          <template #default="{ row }">
            <span v-if="row.dataSourceName">{{ row.dataSourceName }}</span>
            <span v-else style="color: #909399;">-</span>
          </template>
        </el-table-column>
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
        <el-table-column prop="createdTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-tooltip v-if="!canRefreshAsset(row)" content="需关联数据源配置或补充数据库连接信息后才可刷新" placement="top">
              <el-button link type="success" :disabled="true">刷新</el-button>
            </el-tooltip>
            <el-button v-else link type="success" @click="handleRefreshRowCount(row)" :loading="refreshingAssetId === row.assetId">刷新</el-button>
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
                <el-option label="表" value="TABLE" />
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
            <el-form-item label="关联数据源">
              <el-select v-model="assetForm.dataSourceId" placeholder="选择数据源配置" clearable style="width: 100%" @change="handleDataSourceChange">
                <el-option v-for="ds in activeDataSources" :key="ds.dataSourceId" :label="ds.dataSourceName" :value="ds.dataSourceId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="表名" prop="tableName">
              <el-input v-model="assetForm.tableName" placeholder="请输入表名" />
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
      <div style="margin-bottom: 16px;">
        <el-button type="text" @click="handleDownloadTemplate">
          <el-icon><Download /></el-icon>
          下载导入模板
        </el-button>
        <span style="color: #909399; font-size: 12px; margin-left: 8px;">
          请按模板格式准备数据
        </span>
      </div>
      <el-upload
        ref="uploadRef"
        :auto-upload="false"
        :limit="1"
        accept=".csv,.xlsx,.xls"
        @change="handleFileChange"
      >
        <el-button type="primary">选择文件</el-button>
        <template #tip>
          <div class="el-upload__tip">
            支持 CSV、XLSX、XLS 格式文件，且不超过 10MB
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
        <el-descriptions-item label="创建时间">{{ currentAsset.createdTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ currentAsset.updatedTime || '-' }}</el-descriptions-item>
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
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus, Search, Refresh, Upload, Download, RefreshRight, Loading, Delete } from '@element-plus/icons-vue'
import { dataAssetApi, dataSourceConfigApi, type DataAsset } from '@/api'

const router = useRouter()

// 查询参数
const queryParams = reactive({
  assetName: '',
  assetCode: '',
  assetType: '',
  status: '',
  databaseType: '',
  databaseName: '',
  pageNum: 1,
  pageSize: 10,
})

// 资产列表
const assetList = ref<DataAsset[]>([])
const total = ref(0)
const loading = ref(false)
const assetTableRef = ref()
const selectedRows = ref<DataAsset[]>([])

// 多选变更
const handleSelectionChange = (rows: DataAsset[]) => {
  selectedRows.value = rows
}

// 刷新数据条数相关
const refreshingAssetId = ref<number | null>(null)

// 数据源配置列表（编辑表单用）
const activeDataSources = ref<any[]>([])

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
  dataSourceId: null as number | null,
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
    // 过滤空值参数
    const params: any = {
      pageNum: queryParams.pageNum,
      pageSize: queryParams.pageSize
    }
    if (queryParams.assetName) params.assetName = queryParams.assetName
    if (queryParams.assetCode) params.assetCode = queryParams.assetCode
    if (queryParams.assetType) params.assetType = queryParams.assetType
    if (queryParams.status) params.status = queryParams.status
    if (queryParams.databaseType) params.databaseType = queryParams.databaseType
    if (queryParams.databaseName) params.databaseName = queryParams.databaseName

    const res = await dataAssetApi.getList(params)
    assetList.value = res.data.records || res.data.list || []
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
  queryParams.databaseType = ''
  queryParams.databaseName = ''
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

// 批量删除
const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择要删除的资产')
    return
  }
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedRows.value.length} 个资产吗？`,
      '提示',
      { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
    )
    const ids = selectedRows.value.map(row => row.assetId)
    await dataAssetApi.batchDelete(ids)
    ElMessage.success(`成功删除 ${ids.length} 个资产`)
    selectedRows.value = []
    loadAssetList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败')
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
    const res = await dataAssetApi.import(importFile.value)
    if (res.code === 200) {
      ElMessage.success(res.message || `导入成功：成功${res.data.successCount}条，失败${res.data.failCount}条`)
      importDialogVisible.value = false
      loadAssetList()
    } else {
      ElMessage.error(res.message || '导入失败')
    }
  } catch (error) {
    console.error('导入失败:', error)
    ElMessage.error('导入失败')
  } finally {
    importLoading.value = false
  }
}

// 批量导出
const handleExport = async () => {
  try {
    const res = await dataAssetApi.export(queryParams)
    if (res.code === 200) {
      // 创建下载链接
      const link = document.createElement('a')
      link.href = res.data.filePath
      link.download = res.data.fileName
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      ElMessage.success(`导出成功，共${res.data.totalCount}条数据`)
    }
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

// 下载导入模板
const handleDownloadTemplate = async () => {
  try {
    const res = await dataAssetApi.getImportTemplate()
    if (res.code === 200) {
      // 生成CSV模板内容
      const columns = res.data.columns
      let csvContent = '\uFEFF' // UTF-8 BOM
      
      // 添加表头
      const headers = columns.map(col => col.name).join(',')
      csvContent += headers + '\n'
      
      // 添加示例行
      const examples = columns.map(col => col.example || '').join(',')
      csvContent += examples + '\n'
      
      // 创建Blob对象
      const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
      const url = URL.createObjectURL(blob)
      
      // 创建下载链接
      const link = document.createElement('a')
      link.href = url
      link.download = res.data.templateName.replace('.xlsx', '.csv')
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      URL.revokeObjectURL(url)
      
      ElMessage.success('模板下载成功')
    }
  } catch (error) {
    console.error('下载模板失败:', error)
    ElMessage.error('下载模板失败')
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
    dataSourceId: null as number | null,
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
    TABLE: '表',
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

// 选择数据源时自动填充连接信息
const handleDataSourceChange = (dsId: number | null) => {
  if (dsId) {
    const ds = activeDataSources.value.find(d => d.dataSourceId === dsId)
    if (ds) {
      assetForm.databaseType = ds.databaseType
      assetForm.databaseHost = ds.host
      assetForm.databasePort = ds.port
      assetForm.databaseName = ds.databaseName
    }
  }
}

// 加载数据源配置列表
const loadDataSources = async () => {
  try {
    const res = await dataSourceConfigApi.listActive()
    activeDataSources.value = res.data || []
  } catch (error) {
    console.error('加载数据源列表失败:', error)
  }
}

// 判断资产是否可刷新
const canRefreshAsset = (row: DataAsset) => {
  if (row.assetType !== 'DATABASE' && row.assetType !== 'TABLE') return false
  // 关联了数据源配置，且有表名，即可刷新
  if (row.dataSourceId && row.tableName) return true
  // 否则检查资产自身的数据库连接信息
  if (!row.databaseHost || !row.databasePort || !row.databaseName || !row.tableName) return false
  return true
}

// 刷新资产数据条数
const handleRefreshRowCount = async (row: DataAsset) => {
  refreshingAssetId.value = row.assetId
  try {
    const res = await dataAssetApi.refreshRowCount(row.assetId)
    // 响应拦截器已返回 {code, message, data}，成功时 code=200
    const data = res.data
    if (data && data.rowCount !== undefined) {
      row.rowCount = data.rowCount
      ElMessage.success('数据条数刷新成功')
    } else {
      ElMessage.success('刷新完成')
    }
    loadAssetList()
  } catch (error: any) {
    const msg = error?.response?.data?.message || error?.message || '刷新失败'
    ElMessage.error(msg)
  } finally {
    refreshingAssetId.value = null
  }
}

// 批量刷新
const handleBatchRefresh = () => {
  router.push('/batch-refresh')
}

onMounted(() => {
  loadAssetList()
  loadDataSources()
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
