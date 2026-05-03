<template>
  <div class="asset-discovery-container">
    <el-card class="discovery-card">
      <template #header>
        <div class="card-header">
          <h2>发现资产</h2>
          <el-tag type="info">通过数据库连接自动发现数据表并导入为资产</el-tag>
        </div>
      </template>

      <el-steps :active="currentStep" finish-status="success" align-center class="discovery-steps">
        <el-step title="配置连接" description="填写数据库连接信息" />
        <el-step title="扫描表" description="扫描数据库中的表" />
        <el-step title="选择导入" description="勾选要导入的表" />
      </el-steps>

      <!-- 步骤1：数据库连接配置 -->
      <div v-if="currentStep === 0" class="step-content">
        <!-- 选择已配置数据源 或 手动输入 -->
        <el-form label-width="120px" class="connection-form">
          <el-form-item label="连接方式">
            <el-radio-group v-model="connectionMode" @change="handleConnectionModeChange">
              <el-radio value="datasource">选择已配置数据源</el-radio>
              <el-radio value="manual">手动输入连接信息</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>

        <!-- 选择已配置数据源 -->
        <el-form v-if="connectionMode === 'datasource'" label-width="120px" class="connection-form">
          <el-form-item label="数据源">
            <el-select v-model="selectedDataSourceId" placeholder="请选择已配置的数据源" style="width: 100%" @change="handleDataSourceSelect" filterable>
              <el-option v-for="ds in activeDataSources" :key="ds.dataSourceId" :label="`${ds.dataSourceName} (${ds.databaseType} - ${ds.host}:${ds.port}/${ds.databaseName})`" :value="ds.dataSourceId" />
            </el-select>
          </el-form-item>
          <el-form-item v-if="selectedDataSourceId" label="连接信息">
            <el-descriptions :column="2" border size="small">
              <el-descriptions-item label="数据库类型">{{ selectedDataSource?.databaseType }}</el-descriptions-item>
              <el-descriptions-item label="地址">{{ selectedDataSource?.host }}:{{ selectedDataSource?.port }}</el-descriptions-item>
              <el-descriptions-item label="数据库名称">{{ selectedDataSource?.databaseName }}</el-descriptions-item>
              <el-descriptions-item label="用户名">{{ selectedDataSource?.username }}</el-descriptions-item>
            </el-descriptions>
          </el-form-item>
        </el-form>

        <!-- 手动输入连接信息 -->
        <el-form v-if="connectionMode === 'manual'" :model="connectionForm" :rules="connectionRules" ref="connectionFormRef" label-width="120px" class="connection-form">
          <el-form-item label="数据库类型" prop="databaseType">
            <el-select v-model="connectionForm.databaseType" placeholder="请选择数据库类型" style="width: 100%">
              <el-option label="MySQL" value="MYSQL" />
              <el-option label="PostgreSQL" value="POSTGRESQL" />
            </el-select>
          </el-form-item>
          <el-form-item label="主机地址" prop="host">
            <el-input v-model="connectionForm.host" placeholder="请输入数据库主机地址" />
          </el-form-item>
          <el-form-item label="端口号" prop="port">
            <el-input-number v-model="connectionForm.port" :min="1" :max="65535" style="width: 100%" />
          </el-form-item>
          <el-form-item label="数据库名称" prop="databaseName">
            <el-input v-model="connectionForm.databaseName" placeholder="请输入数据库名称" />
          </el-form-item>
          <el-form-item label="用户名" prop="username">
            <el-input v-model="connectionForm.username" placeholder="请输入数据库用户名" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="connectionForm.password" type="password" placeholder="请输入数据库密码" show-password />
          </el-form-item>
        </el-form>
        <div class="step-actions">
          <el-button type="primary" @click="testConnection" :loading="testing" :disabled="connectionMode === 'datasource' && !selectedDataSourceId">测试连接</el-button>
          <el-button type="success" @click="scanTables" :loading="scanning" :disabled="!connectionTested">
            扫描表
            <el-icon class="el-icon--right"><Search /></el-icon>
          </el-button>
        </div>
      </div>

      <!-- 步骤2：扫描结果 -->
      <div v-if="currentStep === 1" class="step-content">
        <div class="scan-info">
          <el-descriptions :column="3" border size="small">
            <el-descriptions-item label="数据库类型">{{ connectionForm.databaseType }}</el-descriptions-item>
            <el-descriptions-item label="主机">{{ connectionForm.host }}:{{ connectionForm.port }}</el-descriptions-item>
            <el-descriptions-item label="数据库">{{ connectionForm.databaseName }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="table-toolbar">
          <el-input v-model="tableFilter" placeholder="搜索表名" clearable style="width: 250px" prefix-icon="Search" />
          <div>
            <el-button @click="toggleSelectAll">{{ isAllSelected ? '取消全选' : '全选' }}</el-button>
            <el-button type="primary" @click="currentStep = 2" :disabled="selectedCount === 0">
              下一步：选择导入选项 ({{ selectedCount }})
            </el-button>
          </div>
        </div>

        <el-table :data="filteredTables" border stripe @selection-change="handleSelectionChange" ref="tableRef" v-loading="scanning">
          <el-table-column type="selection" width="55" />
          <el-table-column prop="tableName" label="表名" min-width="180" />
          <el-table-column prop="tableComment" label="表注释" min-width="200" />
          <el-table-column prop="tableType" label="表类型" width="120" />
          <el-table-column prop="rowCount" label="行数(估算)" width="120" />
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button type="primary" link @click="viewFields(row)">查看字段</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="step-actions">
          <el-button @click="currentStep = 0">上一步</el-button>
        </div>
      </div>

      <!-- 步骤3：导入选项 -->
      <div v-if="currentStep === 2" class="step-content">
        <el-form :model="importForm" label-width="120px" class="import-form">
          <el-form-item label="所属系统">
            <el-input v-model="importForm.systemName" placeholder="请输入所属系统名称" />
          </el-form-item>
          <el-form-item label="责任部门">
            <el-select v-model="importForm.departmentId" placeholder="请选择责任部门" clearable style="width: 100%">
              <el-option v-for="dept in departmentList" :key="dept.departmentId" :label="dept.departmentName" :value="dept.departmentId" />
            </el-select>
          </el-form-item>
          <el-form-item label="责任人">
            <el-select v-model="importForm.ownerId" placeholder="请选择责任人" clearable style="width: 100%">
              <el-option v-for="owner in ownerList" :key="owner.ownerId" :label="owner.name" :value="owner.ownerId" />
            </el-select>
          </el-form-item>
        </el-form>

        <h4>已选择的表 ({{ selectedTables.length }})</h4>
        <el-table :data="selectedTables" border stripe size="small">
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="tableName" label="表名" min-width="180" />
          <el-table-column prop="tableComment" label="表注释" min-width="200" />
          <el-table-column prop="rowCount" label="行数(估算)" width="120" />
        </el-table>

        <div class="step-actions">
          <el-button @click="currentStep = 1">上一步</el-button>
          <el-button type="primary" @click="importAssets" :loading="importing">确认导入</el-button>
        </div>
      </div>
    </el-card>

    <!-- 字段详情对话框 -->
    <el-dialog v-model="fieldDialogVisible" :title="`字段详情 - ${currentTable?.tableName}`" width="800px">
      <div v-loading="loadingFields">
        <el-table :data="currentTable?.fields || []" border stripe size="small">
          <el-table-column prop="fieldName" label="字段名" min-width="150" />
          <el-table-column prop="fieldType" label="类型" width="100" />
          <el-table-column prop="fieldLength" label="长度" width="80" />
          <el-table-column label="可空" width="60">
            <template #default="{ row }">
              <el-tag :type="row.nullable ? 'info' : 'danger'" size="small">{{ row.nullable ? '是' : '否' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="主键" width="60">
            <template #default="{ row }">
              <el-tag v-if="row.isPrimaryKey" type="warning" size="small">PK</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="fieldComment" label="注释" min-width="150" />
          <el-table-column prop="defaultValue" label="默认值" width="100" />
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { assetDiscoveryApi, departmentApi, ownerApi, dataSourceConfigApi, type DiscoveredTable } from '@/api'

const currentStep = ref(0)
const testing = ref(false)
const scanning = ref(false)
const importing = ref(false)
const loadingFields = ref(false)
const connectionTested = ref(false)
const tableFilter = ref('')
const fieldDialogVisible = ref(false)

// 连接方式：datasource=选择已配置数据源，manual=手动输入
const connectionMode = ref<'datasource' | 'manual'>('datasource')
const activeDataSources = ref<any[]>([])
const selectedDataSourceId = ref<number | null>(null)
const selectedDataSource = computed(() => activeDataSources.value.find(ds => ds.dataSourceId === selectedDataSourceId.value))

const connectionFormRef = ref<FormInstance>()
const tableRef = ref()

const connectionForm = ref({
  databaseType: 'MYSQL',
  host: '',
  port: 3306,
  databaseName: '',
  username: '',
  password: ''
})

const importForm = ref({
  systemName: '',
  departmentId: null as number | null,
  ownerId: null as number | null
})

const connectionRules: FormRules = {
  databaseType: [{ required: true, message: '请选择数据库类型', trigger: 'change' }],
  host: [{ required: true, message: '请输入主机地址', trigger: 'blur' }],
  port: [{ required: true, message: '请输入端口号', trigger: 'blur' }],
  databaseName: [{ required: true, message: '请输入数据库名称', trigger: 'blur' }],
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const discoveredTables = ref<DiscoveredTable[]>([])
const selectedTables = ref<DiscoveredTable[]>([])
const currentTable = ref<DiscoveredTable | null>(null)
const departmentList = ref<any[]>([])
const ownerList = ref<any[]>([])

const filteredTables = computed(() => {
  if (!tableFilter.value) return discoveredTables.value
  const keyword = tableFilter.value.toLowerCase()
  return discoveredTables.value.filter(t =>
    t.tableName.toLowerCase().includes(keyword) ||
    (t.tableComment && t.tableComment.toLowerCase().includes(keyword))
  )
})

const selectedCount = computed(() => selectedTables.value.length)
const isAllSelected = computed(() =>
  filteredTables.value.length > 0 && selectedTables.value.length === filteredTables.value.length
)

// 切换连接方式
const handleConnectionModeChange = () => {
  connectionTested.value = false
  selectedDataSourceId.value = null
}

// 选择数据源
const handleDataSourceSelect = (id: number) => {
  connectionTested.value = false
  const ds = selectedDataSource.value
  if (ds) {
    // 同步到 connectionForm，供后续扫描和导入使用
    connectionForm.value = {
      databaseType: ds.databaseType,
      host: ds.host,
      port: ds.port,
      databaseName: ds.databaseName,
      username: ds.username,
      password: '' // 密码不返回前端，后端通过dataSourceId获取
    }
  }
}

// 获取当前连接信息（用于API调用）
const getCurrentConnection = () => {
  if (connectionMode.value === 'datasource' && selectedDataSource.value) {
    const ds = selectedDataSource.value
    return { databaseType: ds.databaseType, host: ds.host, port: ds.port, databaseName: ds.databaseName, username: ds.username, password: '', dataSourceId: ds.dataSourceId }
  }
  return { ...connectionForm.value, dataSourceId: null }
}

// 测试连接
const testConnection = async () => {
  if (connectionMode.value === 'datasource') {
    if (!selectedDataSourceId.value) {
      ElMessage.warning('请先选择数据源')
      return
    }
    testing.value = true
    try {
      const res = await dataSourceConfigApi.testConnection(selectedDataSourceId.value)
      if (res.data === true) {
        connectionTested.value = true
        ElMessage.success('数据库连接成功')
      } else {
        connectionTested.value = false
        ElMessage.error('数据库连接失败，请检查数据源配置')
      }
    } catch (error: any) {
      connectionTested.value = false
      ElMessage.error(error.message || '连接测试失败')
    } finally {
      testing.value = false
    }
    return
  }

  // 手动输入模式
  if (!connectionFormRef.value) return
  await connectionFormRef.value.validate(async (valid) => {
    if (!valid) return
    testing.value = true
    try {
      const res = await assetDiscoveryApi.testConnection(connectionForm.value)
      if (res.data === true) {
        connectionTested.value = true
        ElMessage.success('数据库连接成功')
      } else {
        connectionTested.value = false
        ElMessage.error('数据库连接失败，请检查连接信息')
      }
    } catch (error: any) {
      connectionTested.value = false
      ElMessage.error(error.message || '连接测试失败')
    } finally {
      testing.value = false
    }
  })
}

// 扫描表
const scanTables = async () => {
  if (!connectionTested.value) {
    ElMessage.warning('请先测试连接')
    return
  }
  scanning.value = true
  try {
    const res = await assetDiscoveryApi.scanTables(getCurrentConnection())
    discoveredTables.value = res.data || []
    selectedTables.value = []
    currentStep.value = 1
    ElMessage.success(`扫描完成，共发现 ${discoveredTables.value.length} 张表`)
  } catch (error: any) {
    ElMessage.error(error.message || '扫描失败')
  } finally {
    scanning.value = false
  }
}

// 查看字段
const viewFields = async (row: DiscoveredTable) => {
  currentTable.value = row
  fieldDialogVisible.value = true
  if (!row.fields || row.fields.length === 0) {
    loadingFields.value = true
    try {
      const res = await assetDiscoveryApi.scanFields(getCurrentConnection(), row.tableName)
      row.fields = res.data?.fields || []
      currentTable.value = { ...row }
    } catch (error: any) {
      ElMessage.error('获取字段信息失败')
    } finally {
      loadingFields.value = false
    }
  }
}

// 选择变更
const handleSelectionChange = (selection: DiscoveredTable[]) => {
  selectedTables.value = selection
}

// 全选/取消全选
const toggleSelectAll = () => {
  if (isAllSelected.value) {
    tableRef.value?.clearSelection()
  } else {
    filteredTables.value.forEach(row => {
      tableRef.value?.toggleRowSelection(row, true)
    })
  }
}

// 导入资产
const importAssets = async () => {
  if (selectedTables.value.length === 0) {
    ElMessage.warning('请至少选择一张表')
    return
  }
  importing.value = true
  try {
    const data = {
      ...getCurrentConnection(),
      systemName: importForm.value.systemName,
      departmentId: importForm.value.departmentId,
      ownerId: importForm.value.ownerId,
      dataSourceId: connectionMode.value === 'datasource' ? selectedDataSourceId.value : null,
      tables: selectedTables.value.map(t => ({
        tableName: t.tableName,
        tableComment: t.tableComment,
        selected: true
      }))
    }

    // 先检测重复
    const checkRes = await assetDiscoveryApi.checkDuplicates(data)
    const checkData = checkRes.data

    if (checkData.hasDuplicate) {
      // 有重复，弹窗让用户选择
      importing.value = false
      const duplicateCount = checkData.duplicates.length
      const newCount = checkData.newCount
      const duplicateNames = checkData.duplicates.slice(0, 5).map((d: any) => d.tableName).join('、')
      const moreText = duplicateCount > 5 ? `等${duplicateCount}个` : ''

      try {
        await ElMessageBox.confirm(
          `检测到 ${duplicateCount} 个表已存在于资产列表中（${duplicateNames}${moreText}），另有 ${newCount} 个新表。\n\n请选择处理方式：`,
          '发现重复资产',
          {
            distinguishCancelAndClose: true,
            confirmButtonText: '覆盖更新',
            cancelButtonText: '跳过重复',
            type: 'warning',
          }
        )
        // 用户选择"覆盖更新"
        data.duplicateStrategy = 'OVERWRITE'
      } catch (action: any) {
        if (action === 'cancel') {
          // 用户选择"跳过重复"
          data.duplicateStrategy = 'SKIP'
        } else {
          // 用户关闭弹窗，取消导入
          return
        }
      }
      importing.value = true
    }

    const res = await assetDiscoveryApi.importAssets(data)
    const count = res.data?.length || selectedTables.value.length
    ElMessage.success(`成功导入 ${count} 个资产`)
    currentStep.value = 0
    connectionTested.value = false
    discoveredTables.value = []
    selectedTables.value = []
  } catch (error: any) {
    ElMessage.error(error.message || '导入失败')
  } finally {
    importing.value = false
  }
}

// 加载部门和责任人列表
const loadOptions = async () => {
  try {
    const [deptRes, ownerRes, dsRes] = await Promise.all([
      departmentApi.getList({ pageNum: 1, pageSize: 1000 }),
      ownerApi.getList({ pageNum: 1, pageSize: 1000 }),
      dataSourceConfigApi.listActive()
    ])
    departmentList.value = deptRes.data?.list || deptRes.data || []
    ownerList.value = ownerRes.data?.list || ownerRes.data || []
    activeDataSources.value = dsRes.data || []
  } catch (error) {
    console.error('加载选项失败:', error)
  }
}

onMounted(() => {
  loadOptions()
})
</script>

<style scoped>
.asset-discovery-container {
  max-width: 1200px;
  margin: 0 auto;
}

.discovery-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.discovery-steps {
  margin-bottom: 30px;
}

.step-content {
  padding: 20px 0;
}

.connection-form {
  max-width: 600px;
  margin: 0 auto;
}

.step-actions {
  margin-top: 30px;
  text-align: center;
}

.step-actions .el-button {
  margin: 0 10px;
}

.scan-info {
  margin-bottom: 20px;
}

.table-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.import-form {
  max-width: 600px;
  margin: 0 auto 20px;
}

.import-form h4 {
  margin: 20px 0 10px;
  color: #303133;
}
</style>
