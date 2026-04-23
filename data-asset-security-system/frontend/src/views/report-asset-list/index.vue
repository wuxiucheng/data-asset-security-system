<template>
  <div class="report-asset-list-container">
    <!-- 页面标题 -->
    <el-card class="header-card">
      <template #header>
        <div class="card-header">
          <span>数据资产清单报告</span>
          <el-button type="primary" @click="handleGenerateReport">
            <el-icon><Document /></el-icon> 生成报告
          </el-button>
        </div>
      </template>
      <el-alert
        title="报告说明"
        type="info"
        :closable="false"
        show-icon
      >
        <template #default>
          <p>数据资产清单报告包含所有数据资产的详细信息，包括资产基本信息、责任人信息、分类分级信息等。支持导出为Excel和PDF格式。</p>
        </template>
      </el-alert>
    </el-card>

    <!-- 报告参数配置 -->
    <el-card class="config-card">
      <template #header>
        <span>报告参数配置</span>
      </template>
      <el-form :model="reportForm" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="资产类型">
              <el-select v-model="reportForm.assetType" placeholder="请选择资产类型" clearable style="width: 100%">
                <el-option label="全部" value="" />
                <el-option label="数据库" value="DATABASE" />
                <el-option label="文件" value="FILE" />
                <el-option label="API" value="API" />
                <el-option label="其他" value="OTHER" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="所属部门">
              <el-select v-model="reportForm.departmentId" placeholder="请选择部门" clearable style="width: 100%">
                <el-option label="全部" value="" />
                <el-option
                  v-for="dept in departmentList"
                  :key="dept.departmentId"
                  :label="dept.departmentName"
                  :value="dept.departmentId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="资产状态">
              <el-select v-model="reportForm.status" placeholder="请选择状态" clearable style="width: 100%">
                <el-option label="全部" value="" />
                <el-option label="草稿" value="DRAFT" />
                <el-option label="启用" value="ACTIVE" />
                <el-option label="停用" value="INACTIVE" />
                <el-option label="归档" value="ARCHIVED" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="包含字段级信息">
              <el-switch v-model="reportForm.includeFieldDetails" />
              <span style="margin-left: 10px; color: #909399; font-size: 12px;">
                是否包含字段级别的分类分级详情
              </span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="输出格式">
              <el-radio-group v-model="reportForm.outputFormat">
                <el-radio value="excel">Excel</el-radio>
                <el-radio value="pdf">PDF</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="报告名称">
              <el-input
                v-model="reportForm.reportName"
                placeholder="请输入报告名称，默认为：数据资产清单报告_YYYYMMDD"
                clearable
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- 报告预览 -->
    <el-card class="preview-card" v-if="previewData.length > 0">
      <template #header>
        <div class="card-header">
          <span>报告预览</span>
          <div>
            <el-button type="success" @click="handleExportExcel" :disabled="!previewData.length">
              <el-icon><Download /></el-icon> 导出Excel
            </el-button>
            <el-button type="warning" @click="handleExportPDF" :disabled="!previewData.length">
              <el-icon><Download /></el-icon> 导出文本报告
            </el-button>
          </div>
        </div>
      </template>
      
      <!-- 数据统计 -->
      <el-row :gutter="20" class="stats-row">
        <el-col :span="6">
          <el-statistic title="总资产数" :value="previewData.length" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="数据库资产" :value="getAssetCount('DATABASE')" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="文件资产" :value="getAssetCount('FILE')" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="API资产" :value="getAssetCount('API')" />
        </el-col>
      </el-row>

      <!-- 数据表格 -->
      <el-table :data="previewData" border stripe max-height="400" style="margin-top: 20px">
        <el-table-column prop="assetName" label="资产名称" width="150" />
        <el-table-column prop="assetCode" label="资产编码" width="120" />
        <el-table-column prop="assetType" label="资产类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.assetType === 'DATABASE'" type="primary">数据库</el-tag>
            <el-tag v-else-if="row.assetType === 'FILE'" type="success">文件</el-tag>
            <el-tag v-else-if="row.assetType === 'API'" type="warning">API</el-tag>
            <el-tag v-else type="info">其他</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="departmentName" label="所属部门" width="120" />
        <el-table-column prop="ownerName" label="责任人" width="100" />
        <el-table-column prop="classificationName" label="分类" width="120" />
        <el-table-column prop="gradingName" label="分级" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.gradingName === '绝密'" type="danger">绝密</el-tag>
            <el-tag v-else-if="row.gradingName === '机密'" type="warning">机密</el-tag>
            <el-tag v-else-if="row.gradingName === '秘密'" type="primary">秘密</el-tag>
            <el-tag v-else type="info">内部</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'ACTIVE'" type="success">启用</el-tag>
            <el-tag v-else-if="row.status === 'INACTIVE'" type="danger">停用</el-tag>
            <el-tag v-else-if="row.status === 'DRAFT'" type="info">草稿</el-tag>
            <el-tag v-else type="warning">归档</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="120" />
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="previewData.length"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 报告生成历史 -->
    <el-card class="history-card">
      <template #header>
        <span>报告生成历史</span>
      </template>
      <el-table :data="historyData" border stripe>
        <el-table-column prop="reportName" label="报告名称" width="200" />
        <el-table-column prop="reportType" label="报告类型" width="120" />
        <el-table-column prop="outputFormat" label="输出格式" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.outputFormat === 'excel'" type="success">Excel</el-tag>
            <el-tag v-else-if="row.outputFormat === 'pdf'" type="warning">PDF</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="recordCount" label="记录数" width="100" />
        <el-table-column prop="generatedBy" label="生成人" width="120" />
        <el-table-column prop="generationTime" label="生成时间" width="160" />
        <el-table-column prop="downloadCount" label="下载次数" width="100" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleDownload(row)">
              <el-icon><Download /></el-icon> 下载
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">
              <el-icon><Delete /></el-icon> 删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Document, Download, Delete } from '@element-plus/icons-vue'
import { reportApi } from '@/api'

// 报告表单
const reportForm = reactive({
  assetType: '',
  departmentId: '',
  status: '',
  includeFieldDetails: false,
  outputFormat: 'excel',
  reportName: ''
})

// 部门列表
const departmentList = ref([
  { departmentId: 1, departmentName: '根部门' },
  { departmentId: 2, departmentName: '技术部' },
  { departmentId: 3, departmentName: '业务部' },
  { departmentId: 4, departmentName: '财务部' }
])

// 预览数据
const previewData = ref([])

// 分页
const pagination = reactive({
  pageNum: 1,
  pageSize: 10
})

// 历史数据
const historyData = ref([
  {
    id: 1,
    reportName: '数据资产清单报告_20250418',
    reportType: '数据资产清单',
    outputFormat: 'excel',
    recordCount: 125,
    generatedBy: 'admin',
    generationTime: '2025-04-18 10:30:00',
    downloadCount: 3
  },
  {
    id: 2,
    reportName: '技术部资产清单_20250415',
    reportType: '数据资产清单',
    outputFormat: 'pdf',
    recordCount: 45,
    generatedBy: 'admin',
    generationTime: '2025-04-15 14:20:00',
    downloadCount: 5
  }
])

// 生成报告
const handleGenerateReport = async () => {
  try {
    const params = {
      ...reportForm,
      pageNum: 1,
      pageSize: 1000
    }
    
    const res = await reportApi.generateAssetListReport(params)
    previewData.value = res.data.list
    
    ElMessage.success('报告生成成功，共生成 ' + res.data.list.length + ' 条记录')
  } catch (error) {
    ElMessage.error('报告生成失败')
    console.error('报告生成失败:', error)
  }
}

// 导出Excel
const handleExportExcel = async () => {
  try {
    const params = {
      ...reportForm,
      outputFormat: 'excel'
    }
    
    const res = await reportApi.exportAssetListReport(params)
    
    // 创建下载链接
    const blob = new Blob([res.data])
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `${reportForm.reportName || '数据资产清单报告'}_${new Date().getTime()}.xlsx`
    link.click()
    
    ElMessage.success('Excel导出成功')
  } catch (error) {
    ElMessage.error('Excel导出失败')
    console.error('Excel导出失败:', error)
  }
}

// 导出PDF
const handleExportPDF = async () => {
  try {
    const params = {
      ...reportForm,
      outputFormat: 'pdf'
    }

    const res = await reportApi.exportAssetListReport(params)

    // 创建下载链接
    const blob = new Blob([res.data], { type: 'text/plain;charset=utf-8' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `${reportForm.reportName || '数据资产清单报告'}_${new Date().getTime()}.txt`
    link.click()

    ElMessage.success('报告导出成功')
  } catch (error) {
    ElMessage.error('报告导出失败')
    console.error('报告导出失败:', error)
  }
}

// 下载历史报告
const handleDownload = async (row: any) => {
  try {
    ElMessage.info('开始下载：' + row.reportName);
    
    // 模拟文件下载
    const mockFileContent = `报告名称: ${row.reportName}\n报告类型: ${row.reportType}\n生成时间: ${row.generationTime}\n下载次数: ${row.downloadCount}`;
    const blob = new Blob([mockFileContent], { type: 'text/plain;charset=utf-8' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `${row.reportName}.txt`;
    link.click();
    window.URL.revokeObjectURL(url);
    
    // 更新下载次数
    row.downloadCount++;
    
    ElMessage.success('下载成功：' + row.reportName);
  } catch (error) {
    ElMessage.error('下载失败')
    console.error('下载失败:', error)
  }
}

// 删除历史报告
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该报告吗？', '提示', {
      type: 'warning'
    })
    
    // 这里应该调用实际的删除API
    historyData.value = historyData.value.filter(item => item.id !== row.id)
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 获取资产数量统计
const getAssetCount = (type: string) => {
  return previewData.value.filter((item: any) => item.assetType === type).length
}

// 初始化
onMounted(() => {
  // 加载初始数据
})
</script>

<style scoped lang="scss">
.report-asset-list-container {
  padding: 20px;

  .header-card {
    margin-bottom: 20px;

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }

  .config-card {
    margin-bottom: 20px;
  }

  .preview-card {
    margin-bottom: 20px;

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .stats-row {
      margin-bottom: 20px;
    }
  }

  .history-card {
    margin-bottom: 20px;
  }
}
</style>
