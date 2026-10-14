<template>
  <view>
    <view class="noCoupon">
      <image
        src="https://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/api/discount_coupon%20.png"
        style="width: 69%; height: 480rpx; margin-top: 160rpx"
      />
    </view>
    <view class="noCoupon__content">
      <view class="noCoupon__content--title">
        <view>暂无优惠券</view>
        <view class="goButn" @tap.stop="goButn">去逛逛</view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { useSettingStore } from '@/store/modules/setting'

// 定义方法
const goButn = () => {
  useSettingStore().backHome()
}
</script>

<style lang="scss" scoped>
.noCoupon {
  display: flex;
  align-items: center;
  justify-content: center;

  &__content {
    margin-top: 20rpx;
    display: flex;
    align-items: center;
    justify-content: center;

    &--title {
      color: #acacac;
      width: 240rpx;
      flex-wrap: wrap;
      // margin-top: 50rpx;
      // margin-left: 80rpx;
      display: flex;
      align-items: center;
      justify-content: center;
    }
  }
}

.goButn {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 240rpx;
  height: 80rpx;
  border-radius: 100rpx;
  color: #e4322c;
  border: 1px solid #e4322c;
  margin-top: 40rpx;
  font-size: 28rpx;
}
</style>
