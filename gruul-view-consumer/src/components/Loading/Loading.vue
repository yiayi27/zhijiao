<template>
  <view class="loading">
    <uv-loading-icon></uv-loading-icon>
  </view>
</template>

<script setup lang="ts">
// 这是一个简单的loading组件，不需要特定的数据或方法
</script>

<style lang="scss" scoped>
.loading {
  @include flex;
  width: 100vw;
  height: 100vh;
  position: fixed;
  left: 0;
  top: 0;
  z-index: 99;
  background: #fff;
}
</style>
