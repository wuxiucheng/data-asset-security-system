<template>
  <div class="batch-refresh-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>批量刷新数据条数</span>
          <el-button @click="handleGoBack">返回资产列表</el-button>
        </div>
      </template>

      <!-- 提示信息 -->
      <el-alert
        title="仅展示已配置数据库连接信息（数据库类型、地址、端口、库名、表名）的资产，这些资产才能执行数据条数刷新操作。"
        type="info"
        :closable="false"
        show-icon
        style="margin-bottom: 16px;"
      />

      <!-- 筛选条件 -->
      <el-form :model="filterForm" inline class="filter-form">
        <el-form-item label="数据库类型">
          <el-select v-model="filterForm.databaseType" placeholder="请选择" clearable style="width: 150px">
            <el-option label="MySQL" value="MYSQL" />
            <el-option label="Oracle" value="ORACLE" />
            <el-option label="PostgreSQL" value="POSTGRESQL" />
            <el-option label="SQL Server" value="SQLSERVER" />
          </el-select>
        </el-form-item>
        <el-form-item label="数据库地址">
          <el-input v-model="filterForm.databaseHost" placeholder="请输入" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="数据库名称">
          <el-input v-model="filterForm.databaseName" placeholder="请输入" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <el-icon><Search /></el-icon> 查询
          </el-button>
          <el-button @click="handleResetFilter">
            <el-icon><Refresh /></el-icon> 重置
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 刷新范围选择 -->
      <div class="scope-select">
        <span class="scope-label">刷新范围：</span>
        <el-radio-group v-model="refreshScope">
          <el-radio value="ASSET_ONLY">仅刷新资产数据条数</el-radio>
          <el-radio value="ASSET_AND_FIELD">同时刷新资产和字段数据条数</el-radio>
        </el-radio-group>
      </div>

      <!-- 资产列表 -->
      <el-table
        ref="assetTableRef"
        :data="assetList"
        v-loading="loading"
        border
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="assetId" label="资产ID" width="80" />
        <el-table-column prop="assetCode" label="资产编码" width="120" />
        <el-table-column prop="assetName" label="资产名称" width="160" />
        <el-table-column prop="databaseType" label="数据库类型" width="110" />
        <el-table-column label="数据库地址" width="180">
          <template #default="{ row }">
            {{ row.databaseHost }}:{{ row.databasePort }}
          </template>
        </el-table-column>
        <el-table-column prop="databaseName" label="数据库名称" width="130" />
        <el-table-column prop="tableName" label="表名" width="130" />
        <el-table-column prop="rowCount" label="当前数据条数" width="130">
          <template #default="{ row }">
            <span v-if="row.rowCount != null">{{ row.rowCount.toLocaleString() }}</span>
            <span v-else style="color: #909399;">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'">
              {{ row.status === 'ACTIVE' ? '启用' : row.status === 'DRAFT' ? '草稿' : row.status === 'INACTIVE' ? '停用' : '归档' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>

      <!-- 无数据提示 -->
      <div v-if="!loading && assetList.length === 0" class="empty-tip">
        <el-empty description="暂无已配置数据库连接的资产，请先在资产列表中完善数据库连接信息" />
      </div>

      <!-- 操作区域 -->
      <div class="action-bar">
        <el-button type="primary" @click="handleStartRefresh" :disabled="selectedAssets.length === 0 || isRefreshing" :loading="isRefreshing">
          <el-icon><RefreshRight /></el-icon> 开始刷新
        </el-button>
        <span v-if="selectedAssets.length > 0" class="selected-info">
          已选择 {{ selectedAssets.length }} 条资产
        </span>
        <span class="total-info">
          共 {{ assetList.length }} 条可刷新资产
        </span>
      </div>

      <!-- 进度展示 -->
      <div v-if="progress" class="progress-section">
        <el-divider>刷新进度</el-divider>
        <el-progress
          :percentage="progress.totalCount ? Math.round(progress.completedCount / progress.totalCount * 100) : 0"
          :status="progress.status === 'COMPLETED' ? 'success' : ''"
        />
        <div class="progress-info">
          <span>已完成：{{ progress.completedCount }} / {{ progress.totalCount }}</span>
          <span v-if="progress.status === 'RUNNING' && progress.currentAssetName">
            正在刷新：{{ progress.currentAssetName }}
          </span>
          <span v-if="progress.status === 'COMPLETED'" style="color: #67c23a;">刷新完成</span>
        </div>
      </div>

      <!-- 结果汇总 -->
      <div v-if="progress && progress.status === 'COMPLETED'" class="result-section">
        <el-divider>刷新结果</el-divider>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="成功数量">
            <el-tag type="success">{{ successCount }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="失败数量">
            <el-tag :type="failCount > 0 ? 'danger' : 'success'">{{ failCount }}</el-tag>
          </el-descriptions-item>
        </el-descriptions>
        <div v-if="failCount > 0" class="fail-list">
          <h4>失败列表：</h4>
          <el-table :data="failedResults" border size="small">
            <el-table-column prop="assetName" label="资产名称" />
            <el-table-column prop="errorMessage" label="失败原因" />
          </el-table>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Refresh, RefreshRight } from '@element-plus/icons-vue'
import { dataAssetApi } from '@/api'

const router = useRouter()

// 筛选表单
const filterForm = reactive({
  databaseType: '',
  databaseHost: '',
  databaseName: '',
})

// 刷新范围
const refreshScope = ref('ASSET_AND_FIELD')

// 资产列表
const assetList = ref<any[]>([])
const selectedAssets = ref<any[]>([])
const loading = ref(false)
const assetTableRef = ref()

// 刷新状态
const isRefreshing = ref(false)
const progress = ref<any>(null)
let pollingTimer: ReturnType<typeof setInterval> | null = null

// 成功/失败数量
const successCount = computed(() => {
  if (!progress.value?.results) return 0
  return progress.value.results.filter((r: any) => r.status === 'SUCCESS').length
})
const failCount = computed(() => {
  if (!progress.value?.results) return 0
  return progress.value.results.filter((r: any) => r.status === 'FAILED').length
})
const failedResults = computed(() => {
  if (!progress.value?.results) return []
  return progress.value.results.filter((r: any) => r.status === 'FAILED')
})

// 判断资产是否具备完整的数据库连接信息
const canConnectDatabase = (asset: any) => {
  if (asset.assetType !== 'DATABASE' && asset.assetType !== 'TABLE') return false
  // 关联了数据源配置且有表名
  if (asset.dataSourceId && asset.tableName) return true
  // 或自身有完整的数据库连接信息
  return asset.databaseType
    && asset.databaseHost
    && asset.databasePort
    && asset.databaseName
    && asset.tableName
}

// 查询资产列表 - 只展示已配置数据库连接信息的资产
const handleQuery = async () => {
  loading.value = true
  try {
    const params: any = {
      pageNum: 1,
      pageSize: 1000,
      assetType: 'DATABASE'
    }
    if (filterForm.databaseType) params.databaseType = filterForm.databaseType
    if (filterForm.databaseHost) params.databaseHost = filterForm.databaseHost
    if (filterForm.databaseName) params.databaseName = filterForm.databaseName

    const res = await dataAssetApi.getList(params)
    const allAssets = res.data.records || res.data.list || []

    // 前端过滤：只保留已配置完整数据库连接信息的资产
    assetList.value = allAssets.filter(canConnectDatabase)
  } catch (error) {
    ElMessage.error('查询资产列表失败')
  } finally {
    loading.value = false
  }
}

// 重置筛选
const handleResetFilter = () => {
  filterForm.databaseType = ''
  filterForm.databaseHost = ''
  filterForm.databaseName = ''
  handleQuery()
}

// 选择变化
const handleSelectionChange = (selection: any[]) => {
  selectedAssets.value = selection
}

// 开始批量刷新
const handleStartRefresh = async () => {
  if (selectedAssets.value.length === 0) {
    ElMessage.warning('请选择至少一条资产')
    return
  }

  isRefreshing.value = true
  progress.value = null

  try {
    const assetIds = selectedAssets.value.map(a => a.assetId)
    const res = await dataAssetApi.batchRefreshRowCount({
      assetIds,
      refreshScope: refreshScope.value
    })

    const resData = res.data || res
    const data = resData.data || resData
    const taskId = data.taskId

    if (taskId) {
      startPolling(taskId)
    }
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.message || '提交批量刷新任务失败')
    isRefreshing.value = false
  }
}

// 开始轮询进度
const startPolling = (taskId: string) => {
  stopPolling()
  pollingTimer = setInterval(async () => {
    try {
      const res = await dataAssetApi.getBatchRefreshProgress(taskId)
      const resData = res.data || res
      const data = resData.data || resData
      progress.value = data

      if (data.status === 'COMPLETED' || data.status === 'FAILED') {
        stopPolling()
        isRefreshing.value = false
        handleQuery()
      }
    } catch {
      stopPolling()
      isRefreshing.value = false
    }
  }, 2000)
}

// 停止轮询
const stopPolling = () => {
  if (pollingTimer) {
    clearInterval(pollingTimer)
    pollingTimer = null
  }
}

// 返回资产列表
const handleGoBack = () => {
  router.push('/asset')
}

// 组件销毁时停止轮询
onUnmounted(() => {
  stopPolling()
})

// 初始化加载
handleQuery()
</script>

<style scoped>
.batch-refresh-container {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-form {
  margin-bottom: 16px;
}

.scope-select {
  margin-bottom: 16px;
}

.scope-label {
  font-weight: 500;
  margin-right: 8px;
}

.empty-tip {
  margin-top: 16px;
}

.action-bar {
  margin-top: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.selected-info {
  color: #409eff;
  font-size: 14px;
}

.total-info {
  color: #909399;
  font-size: 14px;
}

.progress-section {
  margin-top: 20px;
}

.progress-info {
  margin-top: 8px;
  display: flex;
  gap: 24px;
  font-size: 14px;
  color: #606266;
}

.result-section {
  margin-top: 20px;
}

.fail-list {
  margin-top: 16px;
}

.fail-list h4 {
  margin-bottom: 8px;
  color: #f56c6c;
}
</style>
