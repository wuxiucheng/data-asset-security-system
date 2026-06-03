<template>
  <div class="lifecycle-container">
    <el-card>
      <template #header>
        <span>数据生命周期管理</span>
      </template>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="生命周期策略" name="policy">
          <el-table :data="policies" border stripe>
            <el-table-column prop="policyId" label="ID" width="80" />
            <el-table-column prop="policyName" label="策略名称" width="180" />
            <el-table-column prop="policyType" label="策略类型" width="120" />
            <el-table-column prop="retentionDays" label="保留天数" width="100" />
            <el-table-column prop="status" label="状态" width="100" />
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="生命周期状态" name="status">
          <el-table :data="statuses" border stripe>
            <el-table-column prop="statusId" label="ID" width="80" />
            <el-table-column prop="policyId" label="策略ID" width="100" />
            <el-table-column prop="lifecycleStage" label="阶段" width="120" />
            <el-table-column prop="daysRemaining" label="剩余天数" width="100" />
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const activeTab = ref('policy')
const policies = ref<any[]>([])
const statuses = ref<any[]>([])

onMounted(async () => {
  const res1 = await request.get('/lifecycle/policy/list')
  if (res1.data) policies.value = res1.data
  
  const res2 = await request.get('/lifecycle/status/list')
  if (res2.data) statuses.value = res2.data
})
</script>

<style scoped>
.lifecycle-container { padding: 20px; }
</style>
