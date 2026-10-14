<template>
  <view class="balance">
    <image
      class="balance__photo"
      src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20211108/db7f54855eb244ad9cfd74df970807df.png"
    />
    <view class="balance__word">我的余额</view>
    <view class="balance__num"
      ><span class="balance__num--symbol">￥</span><span>{{ supplyBonus }}</span></view
    >
    <view class="balance__detail" @click="balanceJumpHandle">余额明细</view>
    <view class="balance__button balance__button--recharge" @click="gotoRechage">充值</view>
    <view class="balance__button balance__button--withdrawal" @click="gotoWithdrawal">提现</view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { userMsgGet } from '@/api/modules/user'
import { useUserStore } from '@/store/modules/user'

// 响应式变量
const supplyBonus = ref(0)

// 方法定义
const balanceJumpHandle = () => {
  const balanceDetail = '/pages/balanceDetail/balanceDetail'
  uni.navigateTo({
    url: '/subcontract' + balanceDetail,
  })
}

const gotoRechage = () => {
  const userInfo = useUserStore().userInfo
  const userMemberInfo = userInfo.infoExtends.memberInfo[0]
  // 用户会员状态 0未注册 1已注册 2会员到期
  const state = userMemberInfo.state

  if (state === 1) {
    uni.navigateTo({
      url: '/subcontract/pages/rechage/rechage',
    })
  } else {
    uni.navigateTo({
      url: '/subcontract/pages/newRecharge/newRecharge',
    })
  }
}

const gotoWithdrawal = () => {
  uni.navigateTo({
    url: '/subcontract/pages/withdrawal/withdrawal',
  })
}

// UniApp 生命周期钩子
onLoad(() => {
  userMsgGet({
    infoLevel: 2,
  }).then((res) => {
    supplyBonus.value = (res.infoExtends.supplyBonus + res.infoExtends.rebateBonus).toFixed(2)
  })
})
</script>

<style lang="scss" scoped>
@import '@/assets/styles/balanceValue/balanceValue.scss';
</style>
