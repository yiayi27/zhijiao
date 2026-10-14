<template>
  <view class="coupon" :style="'opacity:' + (coupon.restNum === 0 || useType || (coupon.useStatus && coupon.useStatus !== 0) ? '0.5' : '')">
    <view
      class="coupon__left"
      :style="
        'background-color:' +
        (coupon.useStatus === 1 || coupon.useStatus === 2 ? '' : leftColor[coupon.couponType]) +
        ';border:1px solid ' +
        (coupon.useStatus === 1 || coupon.useStatus === 2 ? '#D0D0D0' : lineColor[coupon.couponType]) +
        ';border-right: none;'
      "
    >
      <view class="coupon__left--cont" :style="'color:' + (coupon.useStatus === 1 || coupon.useStatus === 2 ? '' : color[coupon.couponType])">
        <view class="coupon__left--cont--discount">
          <span v-if="coupon.couponType === 0 || coupon.couponType === 2">{{ coupon.discount }}</span>
          <span v-else>{{ coupon.discountAmount }}</span>
        </view>
        <view class="coupon__left--cont--type">
          {{ coupon.couponType === 0 || coupon.couponType === 2 ? '折' : '元' }}
        </view>
      </view>
      <view class="coupon__left--disType">
        <view
          v-if="coupon.couponType === 0 || coupon.couponType === 1"
          :style="'color:' + (coupon.useStatus === 1 || coupon.useStatus === 2 ? '' : color[coupon.couponType])"
        >
          无门槛使用
        </view>
        <view
          v-if="coupon.couponType === 2 || coupon.couponType === 3"
          :style="'color:' + (coupon.useStatus === 1 || coupon.useStatus === 2 ? '' : color[coupon.couponType])"
        >
          满{{ coupon.conditionAmount }}可使用
        </view>
      </view>
    </view>
    <view
      class="coupon__right"
      :style="{
        display: 'flex',
        justifyContent: 'space-between',
        backgroundColor: coupon.useStatus === 1 || coupon.useStatus === 2 ? '' : rightColor[coupon.couponType],
        border: `1px solid + ${coupon.useStatus === 1 || coupon.useStatus === 2 ? '#D0D0D0' : lineColor[coupon.couponType]}`,
        borderLeft: 'none',
      }"
      @click="choose(coupon)"
    >
      <view class="coupon__right--top">
        {{ coupon.productType === 0 ? '全店商品' : coupon.productType === 1 ? '指定商品可用' : '指定商品不可用' }}
        <span>({{ coupon.couponType === 0 || coupon.couponType === 2 ? '折扣券' : '现金券' }})</span>
      </view>
      <view class="coupon__right--bottom">
        <view v-if="state === 'noUse'" class="coupon__right--bottom--date">
          <span v-if="coupon.effectiveType === 1">领取后{{ coupon.effectiveDays }}天内有效</span>
          <view v-else class="coupon__right--bottom--date">
            {{ effectiveStartTime }}
            <span class="coupon__right--bottom--date--cont">-</span>
            {{ effectiveEndTime }}
          </view>
        </view>
        <view v-if="state === 'use'" class="coupon__right--bottom--date">
          {{ effectiveStartTime }}
          <span class="coupon__right--bottom--date--cont">-</span>
          {{ effectiveEndTime }}
        </view>
        <view v-if="state === 'car'" />
        <view v-if="state === 'car'">
          <!-- <span wx:if="{{coupon.canUse}}">立即使用</span> -->
          <!-- <span wx:else>暂不可用</span> -->
          <view v-if="coupon.canUse" style="margin-bottom: 80rpx">
            <MIcon v-if="!coupon.isChoose" name="iconweigouxuan" size="40rpx" color="#BBB8B9" />
            <MIcon v-else name="icongouxuan" color="#F71820" size="40rpx" />
          </view>
          <view v-else>暂不可用</view>
        </view>
        <view
          v-else
          class="coupon__right--bottom--button"
          :style="'background-color:' + (coupon.useStatus === 1 || coupon.useStatus === 2 ? '' : 'red')"
        >
          <span v-if="state === 'use'">
            <span v-if="coupon.useStatus === 0" @click.stop="gotoUse(coupon)"> 去使用 </span>
            <span v-if="coupon.useStatus === 2">已过期</span>
            <span v-if="coupon.useStatus === 1">已使用</span>
          </span>
          <span v-if="state === 'noUse'">
            <span v-if="coupon.restNum === 0">已领取</span>
            <span v-else @click.stop="myCouponGet">立即领取</span>
          </span>
        </view>
      </view>
    </view>
  </view>
</template>
<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import DateUtil from '@/mixins/date'
import MIcon from '@/components/MIcon/MIcon.vue'

// 定义Props
const props = defineProps({
  coupon: {
    type: Object,
    default: () => ({}),
  },
  state: {
    type: String,
    default: '',
  },
})

// 定义emit
const emit = defineEmits(['gotoUse', 'myCouponGet', 'catchchooseCoupon'])

// 响应式数据
const color = reactive(['#ff7800', '#ff0108', '#54bb62', '#00c9e3'])
const leftColor = reactive(['#ffeee5 ', '#ffebec', '#edfff0 ', '#e5fcff'])
const rightColor = reactive(['#fefcfa', '#fff9f9', '#fbfdfc', '#f9fdfd'])
const lineColor = reactive(['#fae0cd', '#fddde1', '#ccfadd', '#cdf0fa'])
const useType = ref(false)
const effectiveStartTime = ref('')
const effectiveEndTime = ref('')

// 监听props变化
watch(
  () => props.coupon,
  (newCoupon) => {
    if (newCoupon) {
      const coupon = newCoupon
      let startTime = ''
      let endTime = ''

      if (coupon.startTime) {
        startTime = new DateUtil('').getYMDs(coupon.startTime)
        endTime = new DateUtil('').getYMDs(coupon.endTime)
      } else {
        startTime = new DateUtil('').getYMDs(coupon.createTime)
        endTime = new DateUtil('').getAddDays(coupon.createTime, coupon.effectiveDays)
      }

      effectiveStartTime.value = startTime.replace(/-/g, '.')
      effectiveEndTime.value = endTime.replace(/-/g, '.')
      useType.value = Object.prototype.hasOwnProperty.call(coupon, 'canUse') ? !coupon.canUse : false
    }
  },
  { immediate: true, deep: true },
)

// 方法
const gotoUse = (item: any) => {
  emit('gotoUse', { item })
}

const myCouponGet = () => {
  emit('myCouponGet', props.coupon)
}

const choose = (item: any) => {
  emit('catchchooseCoupon', item)
}
</script>
<style lang="scss" scoped>
@include b(coupon) {
  height: 180rpx;
  background-color: #fff;
  display: flex;
  margin: 10rpx 28rpx;
  // box-shadow: 0 0 0 #EBEBEB;
  border-radius: 20rpx;
  width: 692rpx;

  @include e(left) {
    width: 33%;
    // height: 180rpx;
    @include flex(center, center);
    flex-wrap: wrap;
    position: relative;
    background-color: #fcefe6;
    border-top-left-radius: 20rpx;
    border-bottom-left-radius: 20rpx;

    @include m(cont) {
      height: 100rpx;
      display: flex;
      align-items: flex-end;
      flex-wrap: wrap;
      color: red;

      @include m(discount) {
        font-size: 70rpx;
        display: flex;
        align-items: center;
        font-weight: bold;
        height: 94rpx;
        justify-content: center;
      }

      @include m(type) {
        margin-left: 4rpx;
        height: 60rpx;
        display: flex;
        justify-content: center;
        align-items: center;
      }
    }

    @include m(disType) {
      width: 240rpx;
      height: 46rpx;
      border-radius: 100rpx;
      @include flex(center, center);
      color: red;
      font-size: 24rpx;
      margin-top: -40rpx;
    }
  }

  @include e(right) {
    // height: 180rpx;
    // width: 100%;
    // width: 444rpx;
    width: 67%;
    // padding-top: 30rpx;
    display: flex;
    flex-wrap: wrap;
    background-color: #fdfcfa;
    // padding-left: 40rpx;
    padding: 30rpx 40rpx;
    padding-right: 0;
    border-top-right-radius: 20rpx;
    border-bottom-right-radius: 20rpx;

    @include m(top) {
      font-size: 28rpx;
      font-weight: bold;
      color: #2d2d2d;
    }

    @include m(bottom) {
      font-size: 22rpx;
      color: #595959;
      width: 100%;
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding-right: 20rpx;

      @include m(date) {
        display: flex;

        @include m(cont) {
          margin: 0 4rpx;
        }
      }

      @include m(button) {
        width: 140rpx;
        height: 50rpx;
        border-radius: 100rpx;
        background-color: red;
        color: white;
        border-radius: 100rpx;
        @include flex(center, center);
        color: white;
        font-size: 24rpx;
        margin-left: 20rpx;
      }
    }
  }
}
</style>
