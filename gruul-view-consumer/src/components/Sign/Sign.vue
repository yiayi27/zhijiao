<template>
  <view class="box">
    <text class="text">{{ text }}</text>
  </view>
</template>

<script setup lang="ts">
// 定义Props
defineProps({
  text: {
    type: String,
    default: '',
  },
  icon: {
    type: String,
    default: '',
  },
})
</script>

<style lang="scss" scoped>
.box {
  width: 100%;
  /* position: absolute; */
  /* bottom:0; */
  display: flex;
  justify-content: center;
  align-items: center;
  height: 70rpx;
}

.icon {
  width: 44rpx;
  height: 44rpx;
  vertical-align: bottom;
  margin-right: 10rpx;
}

.text {
  font-size: 22rpx;
  color: #999999;
}
</style>
