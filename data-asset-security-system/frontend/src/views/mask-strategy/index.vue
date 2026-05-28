<template>
  <div class="mask-strategy-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>脱敏策略管理</span>
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
          脱敏策略功能将支持对敏感数据进行动态脱敏处理，保护数据安全。
        </p>
      </el-alert>

      <el-empty description="脱敏策略功能开发中" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

defineOptions({
  name: 'MaskStrategy'
})
</script>

<style scoped lang="scss">
.mask-strategy-container {
  padding: 20px;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
