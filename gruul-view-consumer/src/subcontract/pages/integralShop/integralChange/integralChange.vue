<template>
  <view>
    <view class="backPage">
      <view class="circle">
        <view class="circle__inside">
          <view class="circle__inside--title">今日步数</view>
          <view class="circle__inside--num">{{ todayStep }}</view>
          <view class="circle__inside--rules">兑换{{ targetStep }}步数可获得{{ integral }}积分</view>
          <view class="circle__inside--finish">{{ completionDegree }}%已完成</view>
        </view>
      </view>
    </view>
    <view class="backPage" @tap="changeCode">
      <view :class="(todayStep > targetStep ? 'change' : 'noChange') + ' '">步数兑换</view>
    </view>
    <image src="https://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/gruul/20191127/c106f55afc9094084326f21ab359580.png" class="bottomImg" />
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { stepChangeRule, changeSteps } from '@/api/modules/integral'

const todayStep = ref(0)
const targetStep = ref(0)
const integral = ref(0)
const completionDegree = ref('')
const timeout = ref<NodeJS.Timeout | number>(0)

onLoad((options: any) => {
  getChangeRule(options.todayStep)
})

const getChangeRule = (todayStep: any) => {
  stepChangeRule({
    theCurrentStep: todayStep,
  }).then((res) => {
    todayStep.value = res.todayStep
    targetStep.value = res.targetStep
    integral.value = res.integral
    completionDegree.value = res.completionDegree
  })
}

const changeCode = () => {
  if (todayStep.value < targetStep.value) return
  if (timeout.value) {
    clearTimeout(timeout.value as NodeJS.Timeout)
  }
  timeout.value = setTimeout(() => {
    const parma = {
      targetStep: targetStep.value,
      todayStep: todayStep.value,
    }
    changeSteps(parma)
      .then((res) => {
        uni.showToast({
          title: '兑换成功',
          icon: 'none',
        })
        getChangeRule(res)
      })
      .catch((err) => {
        uni.showToast({
          title: err,
          icon: 'none',
        })
      })
  }, 300)
}
</script>

<style lang="scss" scoped>
page {
  background-color: #f6f6f6;
}

.backPage {
  display: flex;
  justify-content: center;
  margin-bottom: -30rpx;
}

@include b(circle) {
  margin-top: 150rpx;
  width: 450rpx;
  height: 450rpx;
  border-radius: 100%;
  background-color: #ff3363;
  @include flex(center, center);

  @include e(inside) {
    width: 400rpx;
    height: 400rpx;
    border-radius: 100%;
    background-color: #fafafa;
    position: relative;

    @include m(title) {
      position: relative;
      top: 50rpx;
      left: 0;
      font-size: 34rpx;
      display: flex;
      justify-content: center;
    }

    @include m(num) {
      position: relative;
      top: 60rpx;
      left: 0rpx;
      font-size: 80rpx;
      font-weight: bold;
      color: #ff3363;
      display: flex;
      justify-content: center;
    }

    @include m(rules) {
      position: relative;
      top: 70rpx;
      left: 20rpx;
      font-size: 22rpx;
      width: 360rpx;
      height: 80rpx;
      border-radius: 80rpx;
      @include flex(center);
      color: #ff3363;
      border: 1px solid #ff3363;
    }

    @include m(finish) {
      position: relative;
      top: 100rpx;
      left: 0;
      font-size: 28rpx;
      display: flex;
      justify-content: center;
    }
  }
}

.change {
  width: 360rpx;
  height: 80rpx;
  border-radius: 80rpx;
  color: white;
  background-color: #ff3363;
  display: flex;
  justify-content: center;
  -moz-box-shadow:
    -4rpx -1px 40rpx #fca6ba,
    4rpx 4rpx 10rpx #fca6ba;
  -webkit-box-shadow:
    -4rpx -1px 40rpx #fca6ba,
    4rpx 4rpx 10rpx #fca6ba;
  box-shadow:
    -4rpx -1px 40rpx #fca6ba,
    4rpx 4rpx 10rpx #fca6ba;
  margin-top: 80rpx;
  @include flex(center);
}

.noChange {
  width: 360rpx;
  height: 80rpx;
  border-radius: 80rpx;
  color: white;
  background-color: #a1a1a1;
  display: flex;
  justify-content: center;
  -moz-box-shadow:
    -4rpx -1px 40rpx #a1a1a1,
    4rpx 4rpx 10rpx rgb(153, 153, 153);
  -webkit-box-shadow:
    -4rpx -1px 40rpx rgb(153, 153, 153),
    4rpx 4rpx 10rpx rgb(153, 153, 153);
  box-shadow:
    -4rpx -1px 40rpx rgb(153, 153, 153),
    4rpx 4rpx 10rpx rgb(153, 153, 153);
  margin-top: 80rpx;
  @include flex(center);
}

.bottomImg {
  position: fixed;
  bottom: 0;
  width: 100%;
}
</style>
