<template>
  <view>
    <UPopup v-model:show="showType" mode="bottom" @close="onClose">
      <view class="border" style="height: 700upx">
        <view class="border__title">店铺优惠</view>
        <view style="background-color: #f0f0f0; height: 8rpx" />
        <view style="height: 85%; overflow-y: auto">
          <view v-for="(coupon, index) in list" :key="index">
            <Coupon :coupon="coupon" state="noUse" @myCouponGet="myCouponGet" />
          </view>
        </view>
      </view>
    </UPopup>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import Coupon from '@/components/CouponCom/Coupon/Coupon.vue'

// 类型定义
interface CouponItem {
  [key: string]: any
}

// 使用defineModel处理双向绑定
const showType = defineModel<boolean>('showType', { default: false })

// 定义其他props
const props = defineProps({
  couponList: {
    type: Array as () => CouponItem[],
    default: () => [],
  },
})

// 定义emit
const emit = defineEmits(['close', 'myCouponGet'])

// 计算属性
const list = computed(() => {
  return [].slice.call(props.couponList)
})

// 方法
const onClose = () => {
  emit('close', false)
}

const myCouponGet = (e: CouponItem) => {
  emit('myCouponGet', e)
}
</script>

<style lang="scss" scoped>
.border {
  background: white;

  &__title {
    background-color: white;
    height: 100rpx;
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 36rpx;
    font-weight: bold;
  }
}

.coupon {
  color: black;
  display: flex;
}
</style>
