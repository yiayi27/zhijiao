<template>
  <view class="data__empty--order">
    <image class="order--image" src="https://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/images/empty__order.png" />
    <view class="order--tips">{{ tips || '暂无任何订单记录~~' }}</view>
    <block v-if="infoVisible">
      <view class="order--btn" @tap="goIndex">去下单</view>
    </block>
  </view>
</template>

<script setup lang="ts">
import { useSettingStore } from '@/store/modules/setting'

// 定义props
defineProps({
  infoVisible: {
    type: Boolean,
    default: true,
  },
  tips: {
    type: String,
    default: '',
  },
})

// 方法
const goIndex = () => {
  useSettingStore().backHome()
}
</script>

<style lang="scss" scoped>
@include b(data) {
  @include e(empty) {
    @include m(order) {
      @include flex(flex-start, center);

      width: 100vw;
      height: calc(100vh - 80rpx);
      min-height: 940rpx;
      flex-direction: column;
      background: #fff;
      padding-top: 60rpx;

      .order--img {
        width: 360rpx;
      }

      .order--tips {
        color: #4f4f4f;
        margin: 60rpx 0;
      }

      .order--btn {
        $c: #ec5b54;
        width: 170rpx;
        height: 90rpx;
        border: 1px solid $c;
        color: #4f4f4f;
        text-align: center;
        line-height: 90rpx;
        border-radius: 70rpx;
        color: $c;
      }
    }
  }
}
</style>
