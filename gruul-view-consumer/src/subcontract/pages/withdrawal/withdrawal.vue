<template>
  <view class="page">
    <view class="title">提现金额</view>
    <view class="input">
      <uv-input v-model="customPrice" type="digit" style="text-align: center" maxlength="11" placeholder="请输入提现金额" />
    </view>
    <view class="payNow">
      <view class="payNow__btn" @tap="openMem">立即提现</view>
    </view>
    <view class="record" @click="gotoWithdrawalsRecord">提现记录</view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { userMsgUpdate as userMsgUpdateAPI } from '@/api/modules/distribution'
import { userIdGet } from '@/api/modules/user'
import { throttle } from 'lodash-es'

// 响应式数据
const customPrice = ref<string | null>(null)

// 跳转到提现记录页面
const gotoWithdrawalsRecord = () => {
  uni.navigateTo({
    url: `/subcontract/pages/withdrawalsRecord/withdrawalsRecord`,
  })
}

// 打开提现操作
const openMem = () => {
  throttle(userMsgUpdate, 10000)
}

// 提现操作
const userMsgUpdate = () => {
  userIdGet({}).then((id: any) => {
    userMsgUpdateAPI({
      accountType: 'USER',
      channel: 'WECAHT',
      amount: customPrice.value,
      userId: id,
    })
      .then(() => {
        uni.showToast({
          title: '提现请求已提交，请等待审核',
          icon: 'success',
        })
        setTimeout(() => {
          uni.navigateTo({
            url: `/subcontract/pages/balanceValue/balanceValue`,
          })
        }, 1500)
      })
      .catch((err: any) => {
        uni.showToast({
          title: err,
          icon: 'none',
        })
      })
  })
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
.record {
  margin: 40rpx auto;
  color: rgb(41, 96, 199);
  font-size: 28rpx;
  text-align: center;
}
</style>
