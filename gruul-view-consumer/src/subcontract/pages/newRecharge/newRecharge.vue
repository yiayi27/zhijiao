<template>
  <view class="page">
    <view class="title">充值金额</view>
    <view class="input">
      <uv-input v-model="customPrice" type="digit" style="text-align: center" maxlength="11" placeholder="请输入充值金额" />
    </view>
    <view class="payNow">
      <view class="payNow__btn" @tap="openMem">立即充值</view>
    </view>

    <UPopup v-model:show="payTypeFlag" @close="onClose">
      <view class="payConter">
        <view class="payConter__top">
          <uv-icon name="arrow-left" style="margin-right: 40rpx" @tap="onClose" />
          确认付款
        </view>
        <view class="payConter__num">￥{{ customPrice }}</view>
        <view class="payConter__openingMode">
          <view>充值金额</view>
          <view>{{ customPrice }}</view>
        </view>
        <view class="payConter__openingMode">
          <view>赠送金额</view>
          <view>{{ discountAmount || 0 }}</view>
        </view>
        <view class="payConter__openingMode">
          <view>到账金额</view>
          <view>{{ customPrice }}</view>
        </view>
        <view class="payConter__openingMode">
          <view>付款方式</view>
          <view>微信支付</view>
        </view>
        <view class="center">
          <view class="payConter__btn" @tap="paySure">确认付款</view>
        </view>
      </view>
    </UPopup>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { userPay as apiUserPay } from '@/api/modules/member'
import { throttle } from 'lodash-es'
import { requestPayment } from '@/utils/requestPayment'

// 响应式数据
const customPrice = ref('')
const discountAmount = ref(0)
const payTypeFlag = ref(false)

const handleUserPay = () => {
  apiUserPay({
    paymentType: 1,
    discountAmount: discountAmount.value,
    payAmount: customPrice.value,
  })
    .then((res: any) => {
      requestPayment(
        res.wxResult,
        () => {
          payTypeFlag.value = false
          uni.showToast({
            title: '充值成功',
            icon: 'success',
          })
          setTimeout(() => {
            uni.navigateTo({
              url: `/subcontract/pages/balanceValue/balanceValue`,
            })
          }, 1500)
        },
        (e: any) => {
          console.log(e)
          const isCancel = (msg: string) => msg.includes('cancel')
          if (!isCancel(e.errMsg)) {
            uni.showToast({
              title: '支付失败',
            })
          }
        },
      )
    })
    .catch((err: any) => {
      uni.showToast({
        title: err || '支付失败',
      })
    })
}

const paySure = () => {
  throttle(handleUserPay, 10000)
}

const openMem = () => {
  if (!customPrice.value) return null
  payTypeFlag.value = true
  return null
}

const onClose = () => {
  payTypeFlag.value = false
}
</script>

<style lang="scss" scoped>
@mixin middle-center {
  display: flex;
  justify-content: center;
  align-items: center;
}

.page {
  padding-left: 30rpx;
  padding-right: 30rpx;
  height: 100vh;
  background: #fff;
}

.title {
  font-size: 38rpx;
  padding-top: 40rpx;
  padding-bottom: 40rpx;
}

.rechage {
  display: flex;
  justify-content: space-between;
  width: 100%;
  flex-wrap: wrap;

  &__item {
    height: 140rpx;
    display: flex;
    justify-content: center;
    align-items: center;
    border-radius: 20rpx;
    margin-bottom: 30rpx;
    padding-left: 30rpx;
    padding-right: 30rpx;
    border: 1rpx solid #d8dce5;

    &--content {
      display: flex;
      justify-content: center;
      flex-wrap: wrap;
      font-size: 38rpx;
    }
  }
}

.input {
  height: 100rpx;
  border-radius: 20rpx;
  margin-top: 40rpx;
  @include middle-center;
  background-color: #e5e5e5;
}

.payNow {
  display: flex;
  margin-top: 120rpx;
  justify-content: center;
}

.payNow__btn {
  width: 600rpx;
  height: 80rpx;
  background: linear-gradient(to right, #ebcc89, #b4893b);
  border-radius: 50rpx;
  color: white;
  font-size: 32rpx;
  @include middle-center;
}

.payNow__nbtn {
  background: #888;
}

.payConter {
  height: 650rpx;
  padding: 20rpx;
}

.payConter__top {
  font-size: 30rpx;
  display: flex;
  align-items: center;
}

.payConter__num {
  padding: 40rpx;
  font-size: 80rpx;
  @include middle-center;
}

.payConter__openingMode {
  margin-bottom: 20rpx;
  font-size: 32rpx;
  padding-left: 20rpx;
  padding-right: 20rpx;
  color: #666666;
  display: flex;
  justify-content: space-between;
}

.payConter__btn {
  margin: 50rpx auto 30rpx;
  width: 450rpx;
  height: 80rpx;
  border-radius: 50rpx;
  background: linear-gradient(to right, #ebcc89, #b4893b);
  color: white;
  font-size: 30rpx;
  // box-shadow: 0 0 80rpx #FEDBDF;
  @include middle-center;
}
</style>
