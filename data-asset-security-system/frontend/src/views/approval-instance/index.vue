<template>
  <div class="approval-instance-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>流程实例管理</span>
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
          流程实例功能将展示所有审批流程的执行实例和状态。
        </p>
      </el-alert>

      <el-empty description="流程实例功能开发中" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
defineOptions({
  name: 'ApprovalInstance'
})
</script>

<style scoped lang="scss">
.approval-instance-container {
  padding: 20px;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
