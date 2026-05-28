<template>
  <div class="approval-definition-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>流程定义管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新建流程
          </el-button>
        </div>
      </template>

      <el-alert
        type="info"
        :closable="false"
        show-icon
        style="margin-bottom: 20px"
      >
        <template #title>
          功能开发中，敬请期待
        </template>
        <p style="margin-top: 8px">
          流程定义功能将支持自定义审批流程，配置审批节点和流转规则。
        </p>
      </el-alert>

      <el-empty description="流程定义功能开发中" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Plus } from '@element-plus/icons-vue'

defineOptions({
  name: 'ApprovalDefinition'
})

const handleAdd = () => {
  // TODO: 实现新建流程
}
</script>

<style scoped lang="scss">
.approval-definition-container {
  padding: 20px;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
