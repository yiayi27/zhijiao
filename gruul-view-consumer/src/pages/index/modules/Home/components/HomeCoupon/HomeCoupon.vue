<template>
  <view>
    <UPopup v-model:show="isShow" mode="center" bgColor="transparent" @close="cancel">
      <view class="coupon">
        <view class="coupon__center">
          <image src="https://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/api/head1.png" style="width: 100%; height: 29%; margin-top: -40rpx" />
          <view class="coupon__center--allList" :style="'height:50%;'">
            <view v-for="(coupon, index) in couponList" :key="index" style="margin-top: 10rpx">
              <IndexCoupon :coupon="coupon" @getCoupon="getCoupon" />
            </view>
          </view>
          <view v-if="couponList.length > 0" style="height: 10%; padding-top: 10rpx">
            <view v-if="canGet" class="getBtn" @click="getAll">一键领取</view>
          </view>
          <view v-else style="height: 20%; padding-top: 10rpx">
            <view class="nogetBtn">暂无可领优惠券</view>
          </view>
        </view>
        <uv-icon name="close-circle-fill" size="28" color="white" @click="cancel" />
      </view>
    </UPopup>
    <image
      v-if="!isShow"
      src="https://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/api/float.gif"
      :lazy-load="true"
      class="floatGif"
      :animation="animation"
      @click="couponPop"
    />
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getCouponIndex, getOneCoupon, getAllCoupon } from '@/api/modules/coupon'
import { loginGetInt } from '@/api/modules/integral'
import IndexCoupon from '@/components/CouponCom/IndexCoupon/IndexCoupon.vue'
import { useUserStore } from '@/store/modules/user'
import { useShopsetStore } from '@/store/modules/shopset'
import { useSettingStore } from '@/store/modules/setting'
import storage from '@/utils/storage'
import { LOGIN_OPEN } from '@/utils/emitNameConfig'
import { storeToRefs } from 'pinia'

const userStore = useUserStore()
const { isLogin } = storeToRefs(userStore)

// 定义props
defineProps({
  couponType: {
    type: Boolean,
    default: false,
  },
  animation: {
    type: Object,
    default: () => {},
  },
})

// 定义emits
const emit = defineEmits(['stopRoll', 'cancel'])

// 响应式变量
const couponList = ref<any[]>([])
const isShow = ref(false)
const canGet = ref(true)
const timeout = ref<NodeJS.Timeout | null>(null)

// 方法定义

const juageUserLogin = async () => {
  if (!isLogin.value) return
  if (!useSettingStore().integral.load) return
  const res = await loginGetInt({})
  if (res) {
    uni.showToast({
      title: `登录成功,获得${res}积分`,
      icon: 'none',
    })
  }
}

const getCouponList = async () => {
  const shopMsg = useShopsetStore().shopInfo as { status?: number }
  if (!shopMsg || shopMsg.status !== 2) return
  const res = await getCouponIndex({})
  juageUserLogin()
  if (!res.length) {
    emit('stopRoll', false)
    return
  }
  res.forEach((item: any, index: number) => {
    item.hasUse = false
    item.index = index
    item.restNum = item.available > item.totalAvailable ? item.totalAvailable : item.available
  })
  couponList.value = res
  canGet.value = true
}

const appLaunchInit = async () => {
  await getCouponList()
  let userInfo = userStore.userInfo
  userInfo = {
    ...userInfo,
    couponType: false,
  }
  userStore.setUserInfo(userInfo)
  const nowDay = Math.floor(+new Date() / (1000 * 60 * 60 * 24))
  if (couponList.value.length > 0) {
    isShow.value = true
    storage.set('couponDay', nowDay)
    emit('stopRoll', true)
  }
}

const getCoupon = (options: any) => {
  if (!isLogin.value) {
    uni.$emit(LOGIN_OPEN, true)
    isShow.value = false
    emit('stopRoll', false)
    return
  }
  const currentItem = couponList.value[options.index]
  getOneCoupon(options.id, {})
    .then(() => {
      currentItem.restNum = currentItem.restNum - 1
      if (currentItem.restNum === 0) {
        currentItem.hasUse = !currentItem.hasUse
      }
      couponList.value[options.index] = currentItem
      uni.showToast({
        title: '领取成功',
        icon: 'none',
        duration: 500,
      })
    })
    .catch(() => {
      uni.showToast({
        title: '优惠券已被领光',
        icon: 'none',
        duration: 1000,
      })
      currentItem.hasUse = !currentItem.hasUse
      couponList.value[options.detail.index] = currentItem
    })
}

const getAll = () => {
  if (!isLogin.value) {
    uni.$emit(LOGIN_OPEN, true)
    isShow.value = false
    emit('stopRoll', false)
    return
  }
  if (timeout.value) {
    clearTimeout(timeout.value)
  }
  timeout.value = setTimeout(() => {
    const ids: any[] = []
    couponList.value.forEach((item: any) => {
      item.hasUse = true
      ids.push(item.id)
    })
    getAllCoupon(ids, {}).then(() => {
      uni.showToast({
        title: '领取成功,可在我的优惠券查看',
        icon: 'none',
      })
      canGet.value = false
    })
  }, 300)
}

const cancel = () => {
  const type = couponList.value.every((item: any) => {
    return item.hasUse === true
  })
  emit('cancel', type ? 1 : 0)
  let userInfo = userStore.userInfo
  userInfo = {
    ...userInfo,
    couponType: true,
  }
  isShow.value = false
  userStore.setUserInfo(userInfo)
  emit('stopRoll', false)
}

const couponPop = async () => {
  await getCouponList()
  isShow.value = true
  emit('stopRoll', true)
  const userInfo = {
    ...userStore.userInfo,
    couponType: true,
  }
  userStore.setUserInfo(userInfo)
}

// 生命周期钩子
onMounted(() => {
  getCouponList()
  isShow.value = false
  emit('stopRoll', false)
  const nowDay = Math.floor(+new Date() / (1000 * 60 * 60 * 24))
  const storageDay = storage.get('couponDay')
  if (!storageDay || nowDay > storageDay) {
    appLaunchInit()
  }
})
</script>

<style lang="scss" scoped>
@include b(coupon) {
  position: relative;
  // top: 0;
  width: 600rpx;
  height: 1000rpx;
  // padding-top: 60rpx;
  // background-color: rgba(0, 0, 0, 00);
  // background-color: rgba(0,0,0,0);
  background: transparent;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-between;
  z-index: 100;
  padding-top: 13.1rpx;

  @include e(center) {
    // width: 82%;
    width: 93%;
    height: 90%;
    background-color: #f97417;
    display: flex;
    justify-content: center;
    flex-wrap: wrap;

    @include m(icon) {
      position: absolute;
      bottom: -10rpx;
      font-size: 56rpx;
      color: #dbd5cf;
    }

    @include m(allList) {
      display: flex;
      justify-content: center;
      flex-wrap: wrap;
      // height: 680rpx;
      // height: 67%;
      overflow-y: scroll;
      overflow-x: hidden;
    }
  }
}

// .coupon__center--allList::-webkit-scrollbar {
//     display: none;
// }

.getBtn {
  width: 200rpx;
  height: 60rpx;
  background-color: #ff9900;
  color: #fde2b6;
  @include flex(center, center);
}

.nogetBtn {
  width: 300rpx;
  height: 80rpx;
  color: #ffffff;
  text-align: center;
}

.floatGif {
  width: 180rpx;
  height: 180rpx;
  position: fixed;
  bottom: 210rpx;
  right: -30rpx;
  z-index: 100;
}
</style>
