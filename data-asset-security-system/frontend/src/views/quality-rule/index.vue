<template>
  <div class="quality-rule-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>质量规则管理</span>
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
          质量规则功能将支持定义数据质量检查规则，确保数据的准确性和完整性。
        </p>
      </el-alert>

      <el-empty description="质量规则功能开发中" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

defineOptions({
  name: 'QualityRule'
})
</script>

<style scoped lang="scss">
.quality-rule-container {
  padding: 20px;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
