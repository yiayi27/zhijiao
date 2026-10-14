<template>
  <view>
    <view>
      <uv-tabs ref="tabs" :current="active" :list="tabArr" style="margin-bottom: 20rpx" @change="onShowChange" />
      <view class="coupPage">
        <view v-if="list.length > 0">
          <view v-for="(coupon, index) in list" :key="index" class="coupon">
            <Coupon :coupon="coupon" state="use" @gotoUse="gotoUse" />
          </view>
        </view>
        <NoCoupon v-else />
      </view>
    </view>
    <Auth v-model:authType="authType" />
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getUserCoupon } from '@/api/modules/coupon'
import Coupon from '@/components/CouponCom/Coupon/Coupon.vue'
import NoCoupon from '@/components/CouponCom/NoCoupon/NoCoupon.vue'
import Auth from '@/components/Auth/Auth.vue'
import { useSettingStore } from '@/store/modules/setting'

// 响应式变量
const active = ref(0)
const tabArr = ref([
  {
    name: '未使用',
  },
  {
    name: '已使用',
  },
  {
    name: '已过期',
  },
])
const list = ref<any[]>([])
const useLesslist = ref<any[]>([])
const authType = ref(false)

// 方法定义
const onShowChange = (e: { index: number }) => {
  getList(e.index)
  active.value = e.index
}

const getList = (type: number) => {
  const dealList: any[] = []
  const useLessList: any[] = []

  getUserCoupon({}).then((res) => {
    res.forEach((item: any, index: number) => {
      item.hasUse = false
      item.index = index
      if (item.status === 2) {
        item.useStatus = 2
      }
      if (item.useStatus === type) {
        dealList.push(item)
      } else {
        useLessList.push(item)
      }
    })

    list.value = dealList
    useLesslist.value = useLessList
  })
}

const gotoUse = (e: { item: any }) => {
  const item = e.item
  if (item.productType === 0) {
    useSettingStore().backHome()
  } else {
    uni.navigateTo({
      url: `/subcontract/pages/spellPage/second-catgoods/second-catgoods?chanel=${5}&couponId=${item.id}`,
    })
  }
}

// 初始化
onMounted(() => {
  getList(active.value)
})
</script>

<style lang="scss" scoped>
.coupPage {
  background-color: #fff;
  height: 100%;
  padding-top: 20rpx;
  // padding: 20rpx 40rpx;
}

.coupon {
  display: flex;
  // padding: 10rpx;
}

.center {
  @include flex(center, center);
}

.showButton {
  width: 600rpx;
  height: 80rpx;
  border-radius: 100rpx;
  background-image: linear-gradient(#fa425f, #fd2d23);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-top: 30rpx;
  box-shadow: 10rpx -10rpx #ffdadd;
}
</style>
