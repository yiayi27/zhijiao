<template>
  <view>
    <view class="coupPage">
      <view v-if="couponList.length > 0">
        <view v-for="(coupon, index) in couponList" :key="index" class="coupon">
          <Coupon :coupon="coupon" state="noUse" @myCouponGet="myCouponGet" />
        </view>
      </view>
      <view v-else>
        <NoCoupon />
      </view>
    </view>
    <Auth v-model:authType="authType" />
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getCouponIndex, getOneCoupon } from '@/api/modules/coupon'
import Coupon from '@/components/CouponCom/Coupon/Coupon.vue'
import NoCoupon from '@/components/CouponCom/NoCoupon/NoCoupon.vue'
import Auth from '@/components/Auth/Auth.vue'
import { useUserStore } from '@/store/modules/user'
import { storeToRefs } from 'pinia'

const userStore = useUserStore()
const { isLogin } = storeToRefs(userStore)

// 响应式变量
const couponList = ref<any[]>([])
const authType = ref(false)

// 生命周期钩子
onShow(() => {
  getList()
})

// 方法定义
const getList = () => {
  getCouponIndex({}).then((res) => {
    res.forEach((item: any, index: number) => {
      item.hasUse = false
      item.index = index
      item.checkType = true
    })
    couponList.value = res
  })
}

const myCouponGet = (options: any) => {
  if (!isLogin.value) {
    authType.value = true
    return
  }
  getOneCoupon(options.id, {})
    .then(() => {
      uni.showToast({
        title: '领取成功',
        icon: 'none',
        duration: 1500,
      })
      getList()
    })
    .catch((err) => {
      uni.showToast({
        title: err,
        icon: 'none',
        duration: 1500,
      })
      getList()
    })
}
</script>

<style lang="scss" scoped>
.coupPage {
  background-color: #fff;
  height: 1200rpx;
}

.coupon {
  display: flex;
}

.noCoupon {
  display: flex;
  align-items: center;
  justify-content: center;

  &__content {
    margin-top: 200rpx;

    &--title {
      width: 160rpx;
      flex-wrap: wrap;
      margin-top: 50rpx;
      margin-left: 80rpx;
    }
  }
}

.goButn {
  width: 140rpx;
  height: 50rpx;
  border-radius: 100rpx;
  background-image: linear-gradient(#fe708f, #fe5369);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-top: 30rpx;
}
</style>
