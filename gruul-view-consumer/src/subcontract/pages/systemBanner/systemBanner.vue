<template>
  <view>
    <image class="img" src="https://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/images/banner.jpg"></image>
    <cover-view class="btn" @tap.stop="makeCall"></cover-view>
  </view>
</template>
<script setup lang="ts">
const makeCall = () => {
  uni.makePhoneCall({
    phoneNumber: '18967889883',
  })
}
</script>
<style lang="scss" scoped>
page {
  position: relative;
}

.img {
  width: 100vw;
  height: 4114rpx;
}
.btn {
  position: absolute;
  width: 100%;
  height: 102rpx;
  bottom: 61rpx;
  z-index: 999;
}
</style>
