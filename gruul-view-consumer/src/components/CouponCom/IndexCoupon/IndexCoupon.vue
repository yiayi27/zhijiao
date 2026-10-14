<template>
  <view class="coupon">
    <view class="couponLeft" :style="'color:' + (!coupon.hasUse ? 'red' : '#999999')">
      <view class="boxCoupon">
        <view class="boxCoupon__scope">
          <view v-if="coupon.couponType === 1 || coupon.couponType === 3" class="boxCoupon__scope--price">
            <view class="boxCoupon__scope--price--left">
              {{ coupon.discountAmount }}
              <span style="font-weight: normal; font-size: 30rpx; margin-left: 12rpx; margin-bottom: 6rpx"> 元 </span>
            </view>
            <span style="font-weight: normal; font-size: 30rpx; margin-left: 12rpx; margin-bottom: 6rpx">
              ({{ coupon.productType === 0 ? '全店商品' : '指定商品' }})
            </span>
          </view>
          <view v-else class="boxCoupon__scope--price">
            <view class="boxCoupon__scope--price--left">
              {{ coupon.discount }}
              <span style="font-weight: normal; font-size: 30rpx; margin-left: 12rpx; margin-bottom: 6rpx"> 折 </span>
            </view>
            <span style="font-weight: normal; font-size: 30rpx; margin-left: 12rpx; margin-bottom: 6rpx">
              ({{ coupon.productType === 0 ? '全店商品' : '指定商品' }})
            </span>
          </view>
        </view>
        <view class="boxCoupon__rule">
          <view class="boxCoupon__rule--left">
            {{ coupon.couponType === 1 || coupon.couponType === 3 ? '现金券' : '折扣券' }}
          </view>
          <view v-if="coupon.couponType === 2 || coupon.couponType === 3" class="boxCoupon__rule--right"> 满{{ coupon.conditionAmount }}可使用 </view>
          <view v-if="coupon.couponType === 0 || coupon.couponType === 1" class="boxCoupon__rule--right"> 无门槛使用 </view>
        </view>
        <view class="boxCoupon__date">
          <span v-if="coupon.effectiveType === 1">领取后{{ coupon.effectiveDays }}天内有效</span>
          <view v-else>
            <span class="boxCoupon__start">{{ effectiveStartTime }}</span>
            <span>-</span>
            <span class="boxCoupon__end">{{ effectiveEndTime }}</span>
          </view>
        </view>
      </view>
    </view>
    <view class="couponRight">
      <view v-if="!coupon.hasUse" class="radius--coupon" @tap="getCoupon">立即领取</view>
      <view v-else class="circle">
        <view class="circle__inside">
          <view class="circle__inside--title">已领取</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import DateUtil from '@/mixins/date'

// 定义优惠券对象类型
interface CouponType {
  hasUse: boolean
  couponType: number
  discountAmount: number
  productType: number
  discount: number
  conditionAmount: number
  effectiveType: number
  effectiveDays: number
  startTime?: string
  endTime?: string
  createTime: string
  [key: string]: any
}

// 定义props
const props = defineProps<{
  coupon: CouponType
}>()

// 定义emits
const emit = defineEmits(['getCoupon'])

// 定义响应式状态
const effectiveStartTime = ref('')
const effectiveEndTime = ref('')

// 监听coupon属性变化
watch(
  () => props.coupon,
  (coupon) => {
    let startTime = effectiveStartTime.value
    let endTime = effectiveEndTime.value

    if (coupon.startTime) {
      startTime = new DateUtil('').getYMDs(coupon.startTime)
      endTime = new DateUtil('').getYMDs(coupon.endTime)
    } else {
      startTime = new DateUtil('').getYMDs(coupon.createTime)
      endTime = new DateUtil('').getAddDays(coupon.createTime, coupon.effectiveDays)
    }

    effectiveStartTime.value = startTime.replace(/-/g, '.')
    effectiveEndTime.value = endTime.replace(/-/g, '.')
  },
  { immediate: true },
)

// 定义方法
const getCoupon = () => {
  emit('getCoupon', props.coupon)
}
</script>

<style lang="scss" scoped>
.coupon {
  display: flex;
  box-shadow: #cb5f13 0 20rpx 10rpx -10rpx;
}

.couponLeft {
  width: 350rpx;
  height: 170rpx;
  position: relative;
  background-color: white;
}

.couponRight {
  width: 170rpx;
  height: 170rpx;
  position: relative;
  background-color: white;
  border-left: 1px dotted #e7e7e7;
  font-size: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.boxCoupon {
  width: 400rpx;
  height: 170rpx;
  padding-left: 26rpx;
  padding-top: 20rpx;
  position: relative;
  flex: 0 0 400rpx;
  background-color: white;

  &__scope {
    font-size: 34rpx;
    font-weight: bolder;

    &--price {
      display: flex;
      align-items: flex-end;

      &--left {
        font-weight: bold;
        margin-right: 24rpx;
        font-size: 42rpx;
        display: flex;
        align-items: flex-end;
      }
    }
  }

  &__rule {
    display: flex;
    align-items: center;
    margin-top: 4rpx;
    height: 19.1rpx;

    &--left {
      font-weight: bold;
      font-size: 28rpx;
    }

    &--right {
      margin-left: 10rpx;
      font-size: 24rpx;
    }
  }

  &__date {
    margin-top: 8rpx;
    font-size: 22rpx;
    color: #c9c9c9;
  }
}

.radius--coupon {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #ff0000;
}

@include b(circle) {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 152rpx;
  height: 152rpx;
  border-radius: 100%;
  border: 6rpx solid #999999;
  @include flex(center, center);

  @include e(inside) {
    width: 112rpx;
    height: 112rpx;
    border-radius: 100%;
    border: 1px solid #999999;
    position: relative;
    @include flex(center, center);

    @include m(title) {
      position: relative;
      top: 0;
      left: 0;
      font-size: 28rpx;
      transform: rotate(-15deg);
      color: #999999;
    }
  }
}
</style>
