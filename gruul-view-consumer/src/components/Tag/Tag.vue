<template>
  <view>
    <text class="tag">{{ label }}</text>
  </view>
</template>

<script setup lang="ts">
// 定义props
defineProps({
  label: {
    type: String,
    default: '',
  },
})
</script>

<style lang="scss" scoped>
@include b(tag) {
  background: $main-light-color;
  color: #fff;
  display: inline-block;
  padding: 1px 4rpx;
  margin-right: 10rpx;
}
</style>
