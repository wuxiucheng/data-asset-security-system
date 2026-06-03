<template>
  <div class="lineage-container">
    <el-card>
      <template #header>
        <span>数据血缘与影响分析</span>
      </template>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="血缘关系" name="relation">
          <el-table :data="relations" border stripe>
            <el-table-column prop="lineageId" label="ID" width="80" />
            <el-table-column prop="sourceAssetId" label="源资产ID" width="120" />
            <el-table-column prop="targetAssetId" label="目标资产ID" width="120" />
            <el-table-column prop="relationType" label="关系类型" width="120" />
            <el-table-column prop="level" label="层级" width="80" />
            <el-table-column prop="status" label="状态" width="100" />
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="影响分析" name="analysis">
          <el-table :data="analyses" border stripe>
            <el-table-column prop="analysisId" label="ID" width="80" />
            <el-table-column prop="changeAssetId" label="变更资产ID" width="120" />
            <el-table-column prop="changeType" label="变更类型" width="120" />
            <el-table-column prop="impactLevel" label="影响级别" width="100" />
            <el-table-column prop="analysisStatus" label="状态" width="100" />
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const activeTab = ref('relation')
const relations = ref<any[]>([])
const analyses = ref<any[]>([])

onMounted(async () => {
  const res1 = await request.get('/lineage/relation/list')
  if (res1.data) relations.value = res1.data
  
  const res2 = await request.get('/lineage/analysis/list')
  if (res2.data) analyses.value = res2.data
})
</script>

<style scoped>
.lineage-container { padding: 20px; }
</style>
