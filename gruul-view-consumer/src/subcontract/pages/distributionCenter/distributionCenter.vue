<template>
  <view>
    <view class="distrib__header">
      <view class="distrib__userinfo">
        <image class="distrib__userinfo__img" :src="userInfo.avatarUrl" />
        <view class="distrib__userinfo__name">{{ userInfo.nikeName }}</view>
      </view>
      <!-- #ifdef MP-WEIXIN -->
      <uv-button open-type="share">分享链接</uv-button>
      <!-- #endif -->
    </view>
    <view class="distrib__commission">
      <view class="distrib__commission--word">当前累计佣金收益(元)</view>
      <view class="distrib__commission--num">{{ totalDistributionAmount }}</view>
      <view class="distrib__commission--notes">不包含待结算佣金</view>
    </view>
    <view class="distrib__customer" @click="gotocustomer">
      <view class="flex">
        <image
          class="distrib__customer--img"
          src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20211108/aa8dba6cd22a44849b615123cdcc5a41.png"
        />
        <view>分销客户</view>
      </view>
      <view>{{ distributionUserCount }}></view>
    </view>
    <view class="distrib__code" @click="gotocode">
      <view class="flex">
        <image
          class="distrib__code--img"
          src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20211108/dc8faab86695455e8b6a26b4ea38e485.png"
        />
        <view>推广二维码</view>
      </view>
      <view>></view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { distributionGet } from '@/api/modules/distribution'
import { userIdGet } from '@/api/modules/user'
import { useUserStore } from '@/store/modules/user'

const distributionUserCount = ref<number | null>(null)
const totalDistributionAmount = ref<number>(0)
const userInfo = ref<Obj>({})
const id = ref<string>('')

const gotocustomer = () => {
  uni.navigateTo({
    url: `/subcontract/pages/distributionCustomers/distributionCustomers`,
  })
}

const gotocode = () => {
  uni.navigateTo({
    url: `/subcontract/pages/promotionCode/promotionCode`,
  })
}

const userStore = useUserStore()
userInfo.value = userStore.userInfo

distributionGet({}).then((res) => {
  distributionUserCount.value = res.distributionUserCount
  totalDistributionAmount.value = res.totalDistributionAmount === null ? 0 : res.totalDistributionAmount
})

userIdGet({}).then((res) => {
  id.value = res
})
</script>

<style lang="scss" scoped>
@import '@/assets/styles/distributionCenter/distributionCenter.scss';
</style>
