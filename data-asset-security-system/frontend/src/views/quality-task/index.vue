<template>
  <div class="quality-task-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>质量任务管理</span>
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
          质量任务功能将支持执行数据质量检查任务，生成质量报告。
        </p>
      </el-alert>

      <el-empty description="质量任务功能开发中" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

defineOptions({
  name: 'QualityTask'
})
</script>

<style scoped lang="scss">
.quality-task-container {
  padding: 20px;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
