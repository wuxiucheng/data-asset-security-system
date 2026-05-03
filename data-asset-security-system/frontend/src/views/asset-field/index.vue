<template>
  <div class="asset-field-container">
    <!-- 搜索表单 -->
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="所属资产">
          <el-select v-model="searchForm.assetId" placeholder="请选择资产" clearable filterable style="width: 200px">
            <el-option
              v-for="item in assetList"
              :key="item.assetId"
              :label="item.assetName"
              :value="item.assetId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="字段名称">
          <el-input v-model="searchForm.fieldName" placeholder="请输入字段名称" clearable />
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
      <el-button type="primary" @click="handleAdd" :disabled="!searchForm.assetId">
        <el-icon><Plus /></el-icon> 新增字段
      </el-button>
      <el-button type="success" @click="handleImport">
        <el-icon><Upload /></el-icon> 批量导入
      </el-button>
      <el-button type="warning" @click="handleDownloadTemplate">
        <el-icon><Download /></el-icon> 下载模板
      </el-button>
      <el-button type="info" @click="handleBatchUpdate" :disabled="!searchForm.assetId || selectedRows.length === 0">
        <el-icon><Operation /></el-icon> 批量更新
      </el-button>
    </div>

    <!-- 字段列表 -->
    <el-card class="table-card">
      <el-table
        :data="tableData"
        v-loading="loading"
        border
        stripe
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column type="index" label="序号" width="80" />
        <el-table-column prop="fieldName" label="字段名称" width="150" />
        <el-table-column prop="fieldCode" label="字段编码" width="150" />
        <el-table-column prop="rowCount" label="数据条数" width="120">
          <template #default="{ row }">
            <span v-if="refreshingFieldId === row.fieldId">
              <el-icon class="is-loading"><Loading /></el-icon>
            </span>
            <span v-else-if="!currentAssetCanRefresh" style="color: #909399;">不适用</span>
            <span v-else-if="row.rowCount != null">{{ row.rowCount.toLocaleString() }}</span>
            <span v-else style="color: #909399;">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="fieldType" label="字段类型" width="120" />
        <el-table-column prop="fieldLength" label="长度" width="80" />
        <el-table-column prop="isPrimaryKey" label="主键" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isPrimaryKey ? 'success' : 'info'">
              {{ row.isPrimaryKey ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isRequired" label="必填" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isRequired ? 'danger' : 'info'">
              {{ row.isRequired ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="defaultValue" label="默认值" width="120" />
        <el-table-column prop="classificationId" label="分类ID" width="100" />
        <el-table-column prop="gradingId" label="分级ID" width="100" />
        <el-table-column prop="sensitiveType" label="敏感类型" width="120" />
        <el-table-column prop="riskLevel" label="风险等级" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.riskLevel === 'HIGH'" type="danger">高</el-tag>
            <el-tag v-else-if="row.riskLevel === 'MEDIUM'" type="warning">中</el-tag>
            <el-tag v-else-if="row.riskLevel === 'LOW'" type="success">低</el-tag>
            <el-tag v-else type="info">-</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'">
              {{ row.status === 'ACTIVE' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-tooltip v-if="!currentAssetCanRefresh" content="所属资产需补充数据库连接信息后才可刷新" placement="top">
              <el-button type="success" link size="small" :disabled="true">
                <el-icon><RefreshRight /></el-icon> 刷新
              </el-button>
            </el-tooltip>
            <el-button v-else type="success" link size="small" @click="handleRefreshFieldRowCount(row)" :loading="refreshingFieldId === row.fieldId">
              <el-icon><RefreshRight /></el-icon> 刷新
            </el-button>
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

    <!-- 批量更新对话框 -->
    <el-dialog
      v-model="batchDialogVisible"
      title="批量更新字段"
      width="600px"
      @close="handleBatchDialogClose"
    >
      <el-form :model="batchFormData" ref="batchFormRef" label-width="120px">
        <el-form-item label="数据分类">
          <el-select v-model="batchFormData.classificationId" placeholder="请选择数据分类" clearable style="width: 100%">
            <el-option
              v-for="item in classificationList"
              :key="item.classificationId"
              :label="item.classificationName"
              :value="item.classificationId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="数据分级">
          <el-select v-model="batchFormData.gradingId" placeholder="请选择数据分级" clearable style="width: 100%">
            <el-option
              v-for="item in gradingList"
              :key="item.gradingId"
              :label="item.gradingName"
              :value="item.gradingId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="敏感类型">
          <el-input v-model="batchFormData.sensitiveType" placeholder="请输入敏感类型" />
        </el-form-item>
        <el-form-item label="风险等级">
          <el-radio-group v-model="batchFormData.riskLevel">
            <el-radio value="HIGH">高</el-radio>
            <el-radio value="MEDIUM">中</el-radio>
            <el-radio value="LOW">低</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="batchFormData.status">
            <el-radio value="ACTIVE">启用</el-radio>
            <el-radio value="INACTIVE">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="batchDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleBatchSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 批量导入对话框 -->
    <el-dialog
      v-model="importDialogVisible"
      title="批量导入字段"
      width="500px"
    >
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
        :auto-upload="false"
        :on-change="handleFileChange"
        :limit="1"
        accept=".csv,.xlsx,.xls"
      >
        <template #trigger>
          <el-button type="primary">选择文件</el-button>
        </template>
        <template #tip>
          <div style="color: #909399; font-size: 12px; margin-top: 8px;">
            支持 .csv, .xlsx, .xls 格式，文件大小不超过 10MB
          </div>
        </template>
      </el-upload>
      <template #footer>
        <el-button @click="importDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleImportSubmit" :loading="importLoading">导入</el-button>
      </template>
    </el-dialog>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      @close="handleDialogClose"
    >
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="120px">
        <el-form-item label="字段名称" prop="fieldName">
          <el-input v-model="formData.fieldName" placeholder="请输入字段名称" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="字段编码" prop="fieldCode">
          <el-input v-model="formData.fieldCode" placeholder="请输入字段编码" />
        </el-form-item>
        <el-form-item label="字段类型" prop="fieldType">
          <el-select v-model="formData.fieldType" placeholder="请选择字段类型" style="width: 100%">
            <el-option label="VARCHAR" value="VARCHAR" />
            <el-option label="INT" value="INT" />
            <el-option label="BIGINT" value="BIGINT" />
            <el-option label="DECIMAL" value="DECIMAL" />
            <el-option label="DATE" value="DATE" />
            <el-option label="DATETIME" value="DATETIME" />
            <el-option label="TEXT" value="TEXT" />
            <el-option label="BOOLEAN" value="BOOLEAN" />
          </el-select>
        </el-form-item>
        <el-form-item label="字段长度" prop="fieldLength">
          <el-input-number v-model="formData.fieldLength" :min="1" placeholder="请输入字段长度" style="width: 100%" />
        </el-form-item>
        <el-form-item label="是否主键">
          <el-switch v-model="formData.isPrimaryKey" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="是否必填">
          <el-switch v-model="formData.isRequired" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="默认值">
          <el-input v-model="formData.defaultValue" placeholder="请输入默认值" />
        </el-form-item>
        <el-form-item label="数据分类" prop="classificationId">
          <el-select v-model="formData.classificationId" placeholder="请选择数据分类" clearable style="width: 100%">
            <el-option
              v-for="item in classificationList"
              :key="item.classificationId"
              :label="item.classificationName"
              :value="item.classificationId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="数据分级" prop="gradingId">
          <el-select v-model="formData.gradingId" placeholder="请选择数据分级" clearable style="width: 100%">
            <el-option
              v-for="item in gradingList"
              :key="item.gradingId"
              :label="item.gradingName"
              :value="item.gradingId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="敏感类型">
          <el-input v-model="formData.sensitiveType" placeholder="请输入敏感类型" />
        </el-form-item>
        <el-form-item label="风险等级">
          <el-radio-group v-model="formData.riskLevel">
            <el-radio value="HIGH">高</el-radio>
            <el-radio value="MEDIUM">中</el-radio>
            <el-radio value="LOW">低</el-radio>
          </el-radio-group>
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
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, Operation, Upload, Download, RefreshRight, Loading } from '@element-plus/icons-vue'
import { assetFieldApi, dataAssetApi, dataClassificationApi, dataGradingApi, type AssetField } from '@/api'

// 搜索表单
const searchForm = reactive({
  assetId: null,
  fieldName: '',
  status: ''
})

// 表格数据
const tableData = ref([])
const assetList = ref([])
const classificationList = ref([])
const gradingList = ref([])
const loading = ref(false)
const selectedRows = ref([])

// 刷新数据条数相关
const refreshingFieldId = ref<number | null>(null)
const currentAssetDetail = ref<any>(null)
const currentAssetCanRefresh = computed(() => {
  if (!currentAssetDetail.value) return false
  const asset = currentAssetDetail.value
  if (asset.assetType !== 'DATABASE' && asset.assetType !== 'TABLE') return false
  // 关联了数据源配置
  if (asset.dataSourceId && asset.tableName) return true
  // 或自身有完整的数据库连接信息
  if (!asset.databaseHost || !asset.databasePort || !asset.databaseName || !asset.tableName) return false
  return true
})

// 对话框
const dialogVisible = ref(false)
const batchDialogVisible = ref(false)
const importDialogVisible = ref(false)
const dialogTitle = ref('新增字段')
const isEdit = ref(false)
const formRef = ref()
const batchFormRef = ref()
const importFile = ref()
const importLoading = ref(false)

// 表单数据
const formData = reactive({
  fieldId: 0,
  assetId: null,
  fieldName: '',
  fieldCode: '',
  fieldType: '',
  fieldLength: 0,
  isNullable: 0,
  isPrimaryKey: 0,
  isRequired: 0,
  defaultValue: '',
  fieldDescription: '',
  classificationId: null,
  gradingId: null,
  sensitiveType: '',
  riskLevel: '',
  status: 'ACTIVE'
})

// 批量更新表单数据
const batchFormData = reactive({
  classificationId: null,
  gradingId: null,
  sensitiveType: '',
  riskLevel: '',
  status: 'ACTIVE'
})

// 表单验证规则
const formRules = {
  fieldName: [
    { required: true, message: '请输入字段名称', trigger: 'blur' }
  ],
  fieldCode: [
    { required: true, message: '请输入字段编码', trigger: 'blur' }
  ],
  fieldType: [
    { required: true, message: '请选择字段类型', trigger: 'change' }
  ]
}

// 获取资产列表
const getAssetList = async () => {
  try {
    const res = await dataAssetApi.getList({ pageNum: 1, pageSize: 1000 })
    // 显示所有资产，不过滤状态（包括DRAFT和ACTIVE）
    assetList.value = res.data.records || res.data.list || []
  } catch (error) {
    ElMessage.error('获取资产列表失败')
  }
}

// 获取分类列表
const getClassificationList = async () => {
  try {
    const res = await dataClassificationApi.getList({ pageNum: 1, pageSize: 1000 })
    classificationList.value = res.data.list.filter((item: any) => item.status === 'ACTIVE')
  } catch (error) {
    ElMessage.error('获取分类列表失败')
  }
}

// 获取分级列表
const getGradingList = async () => {
  try {
    const res = await dataGradingApi.getList({ pageNum: 1, pageSize: 1000 })
    gradingList.value = res.data.list.filter((item: any) => item.status === 'ACTIVE')
  } catch (error) {
    ElMessage.error('获取分级列表失败')
  }
}

// 获取字段列表
const getFieldList = async () => {
  if (!searchForm.assetId) {
    ElMessage.warning('请先选择所属资产')
    return
  }
  loading.value = true
  try {
    const res = await assetFieldApi.getFieldsByAssetId(searchForm.assetId)
    tableData.value = res.data
    // 同步获取资产详情，用于判断刷新按钮是否可用
    try {
      const assetRes = await dataAssetApi.getDetail(searchForm.assetId)
      currentAssetDetail.value = assetRes.data?.data || assetRes.data
    } catch {
      currentAssetDetail.value = null
    }
  } catch (error) {
    ElMessage.error('获取字段列表失败')
  } finally {
    loading.value = false
  }
}

// 选择变化
const handleSelectionChange = (selection: any[]) => {
  selectedRows.value = selection
}

// 搜索
const handleSearch = () => {
  getFieldList()
}

// 重置
const handleReset = () => {
  searchForm.fieldName = ''
  searchForm.status = ''
  handleSearch()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增字段'
  isEdit.value = false
  Object.assign(formData, {
    fieldId: 0,
    assetId: searchForm.assetId,
    fieldName: '',
    fieldCode: '',
    fieldType: '',
    fieldLength: 0,
    isNullable: 0,
    isPrimaryKey: 0,
    isRequired: 0,
    defaultValue: '',
    fieldDescription: '',
    classificationId: null,
    gradingId: null,
    sensitiveType: '',
    riskLevel: '',
    status: 'ACTIVE'
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑字段'
  isEdit.value = true
  Object.assign(formData, row)
  dialogVisible.value = true
}

// 批量更新
const handleBatchUpdate = () => {
  Object.assign(batchFormData, {
    classificationId: null,
    gradingId: null,
    sensitiveType: '',
    riskLevel: '',
    status: 'ACTIVE'
  })
  batchDialogVisible.value = true
}

// 提交批量更新
const handleBatchSubmit = async () => {
  try {
    const fieldIds = selectedRows.value.map((row: any) => row.fieldId)
    await assetFieldApi.batchUpdate({
      fieldIds,
      ...batchFormData
    })
    ElMessage.success('批量更新成功')
    batchDialogVisible.value = false
    getFieldList()
  } catch (error) {
    ElMessage.error('批量更新失败')
  }
}

// 批量导入
const handleImport = () => {
  importDialogVisible.value = true
}

// 下载模板
const handleDownloadTemplate = async () => {
  try {
    const res = await assetFieldApi.getImportTemplate()
    if (res.code === 200) {
      // 生成CSV模板内容
      const columns = res.data.columns
      let csvContent = '\uFEFF' // UTF-8 BOM
      
      // 添加表头
      const headers = columns.map((col: any) => col.name).join(',')
      csvContent += headers + '\n'
      
      // 添加示例行
      const examples = columns.map((col: any) => col.example || '').join(',')
      csvContent += examples + '\n'
      
      // 创建Blob对象
      const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
      const url = URL.createObjectURL(blob)
      
      // 创建下载链接
      const link = document.createElement('a')
      link.href = url
      link.download = res.data.templateName
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

// 文件选择
const handleFileChange = (file: any) => {
  importFile.value = file.raw
}

// 提交导入
const handleImportSubmit = async () => {
  if (!importFile.value) {
    ElMessage.warning('请选择要导入的文件')
    return
  }

  importLoading.value = true
  try {
    const res = await assetFieldApi.import(importFile.value)
    if (res.code === 200) {
      ElMessage.success(res.message || `导入成功：成功${res.data.successCount}条，失败${res.data.failCount}条`)
      importDialogVisible.value = false
      getFieldList()
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

// 删除
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该字段吗？', '提示', {
      type: 'warning'
    })
    await assetFieldApi.delete(row.fieldId)
    ElMessage.success('删除成功')
    getFieldList()
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
      await assetFieldApi.update(formData)
      ElMessage.success('更新成功')
    } else {
      await assetFieldApi.create(formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    getFieldList()
  } catch (error) {
    if (error !== false) {
      ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
    }
  }
}

// 刷新字段数据条数
const handleRefreshFieldRowCount = async (row: any) => {
  refreshingFieldId.value = row.fieldId
  try {
    const res = await assetFieldApi.refreshRowCount(row.fieldId)
    const data = res.data
    if (data && data.rowCount !== undefined) {
      row.rowCount = data.rowCount
      ElMessage.success('字段数据条数刷新成功')
    } else {
      ElMessage.success('刷新完成')
    }
  } catch (error: any) {
    const msg = error?.response?.data?.message || error?.message || '刷新失败'
    ElMessage.error(msg)
  } finally {
    refreshingFieldId.value = null
  }
}

// 关闭对话框
const handleDialogClose = () => {
  formRef.value?.resetFields()
}

const handleBatchDialogClose = () => {
  batchFormRef.value?.resetFields()
}

// 初始化
onMounted(() => {
  getAssetList()
  getClassificationList()
  getGradingList()
})
</script>

<style scoped>
.asset-field-container {
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
