<template>
  <div class="approval-task-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>审批任务管理</span>
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
          审批任务功能将展示待处理的审批任务，支持审批操作。
        </p>
      </el-alert>

      <el-empty description="审批任务功能开发中" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
defineOptions({
  name: 'ApprovalTask'
})
</script>

<style scoped lang="scss">
.approval-task-container {
  padding: 20px;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
