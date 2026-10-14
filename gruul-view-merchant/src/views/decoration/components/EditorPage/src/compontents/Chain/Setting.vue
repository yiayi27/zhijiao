<script setup lang="ts">
import { Setting } from '@element-plus/icons-vue'
</script>

<template>
  <div class="form-tips">
    <div class="form-tips-logo">
      <el-icon size="100" class="mb-[20px]">
        <Setting />
      </el-icon>
      <p>当前组件,无法进行设置</p>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.form-tips {
  .form-tips-logo {
    width: 435px;
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
    text-align: center;
    color: #b2b2b2;
  }
}
</style>
