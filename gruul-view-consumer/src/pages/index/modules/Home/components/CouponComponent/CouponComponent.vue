<template>
  <!-- 优惠券组件 -->
  <view v-if="!allHide" class="coupon_component">
    <scroll-view scroll-x="true" style="white-space: nowrap; display: flex">
      <view v-for="(coupon, index) in couponList" :key="index" class="coupon_c">
        <view
          :data-id="coupon.id"
          :data-hasUse="coupon.hasUse"
          :data-index="index"
          :class="
            'boxCoupon ' +
            (coupon.couponType === 1 ? 'bg_orange' : '') +
            '' +
            (coupon.couponType === 2 ? 'bg_green' : '') +
            ' ' +
            (coupon.couponType === 3 ? 'bg_red' : '') +
            ' ' +
            (coupon.couponType === 0 ? 'bg_blue' : '') +
            ' ' +
            (coupon.hasUse ? 'bg_gray' : '')
          "
          @tap="getCoupon"
        >
          <view class="boxCoupon__scope">
            <view v-if="coupon.couponType === 1 || coupon.couponType === 3" class="boxCoupon__scope--price">
              <view class="boxCoupon__scope--price--left">
                <text class="coupon_title">{{ coupon.discountAmount }}</text>
                元
              </view>
            </view>
            <view v-else class="boxCoupon__scope--price">
              <view class="boxCoupon__scope--price--left">
                <text class="coupon_title">{{ coupon.discount }}</text>
                折
              </view>
            </view>
          </view>
          <!-- 使用规则 -->
          <view class="boxCoupon__rule">
            <view v-if="coupon.couponType === 2 || coupon.couponType === 3" class="boxCoupon__rule--right">
              满{{ coupon.conditionAmount }}可使用
            </view>
            <view v-if="coupon.couponType === 0 || coupon.couponType === 1" class="boxCoupon__rule--right"> 无门槛使用 </view>
          </view>
          <!-- 已领取图案 -->
          <view v-if="coupon.hasUse" class="circle">
            <view class="circle__inside">
              <view class="circle__inside--title">已领取</view>
            </view>
          </view>
          <view v-if="decoretionProperties?.style === 'style2'" class="left_icon" />
          <view v-if="decoretionProperties?.style === 'style2'" class="right_icon" />
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { getCouponCoup, getOneCoupon } from '@/api/modules/coupon'
import { useUserStore } from '@/store/modules/user'
import { useSettingStore } from '@/store/modules/setting'
import { LOGIN_OPEN, APP_LAUNCH } from '@/utils/emitNameConfig'
import { storeToRefs } from 'pinia'

const { isLogin } = storeToRefs(useUserStore())

interface CouponItem {
  id: Long
  hasUse: boolean
  restNum: number
  available: number
  totalAvailable: number
  couponType: number
  discountAmount?: number
  discount?: number
  conditionAmount?: number
  index: number
  [key: string]: any
}

// 定义props
const props = defineProps({
  decoretionProperties: {
    type: Object,
    default: () => ({}),
  },
})

// 定义emits
const emit = defineEmits(['stopRoll'])

// 响应式变量
const couponList = ref<CouponItem[]>([])
const isShow = ref(false)
const canGet = ref(true)
const formData = ref<Obj>({})
const allHide = ref(false)

// 方法定义
const filterList = (res: any[]): CouponItem[] => {
  const list = res.filter((item: any, index: number) => {
    item.hasUse = item.totalAvailable > 0 && item.available > 0 ? false : true
    item.index = index
    item.restNum = item.available > item.totalAvailable ? item.totalAvailable : item.available
    return formData.value.hide ? item.available : true
  })
  return [...list]
}

const getCouponList = () => {
  const components = useSettingStore().components
  const couponItem = components.filter((item) => item.value === 'Coupon')
  if (couponItem.length === 0) {
    return
  }
  const hide = couponItem[0].formData.hide
  getCouponCoup({}).then((res) => {
    if (!res.length) {
      emit('stopRoll', false)
      return
    }
    if (isLogin.value) {
      res = filterList(res)
      if (hide) {
        const resdata = JSON.parse(JSON.stringify(res))
        res = resdata.filter((item: any) => {
          return item.restNum !== 0
        })
      }
    }
    allHide.value = res.length === 0 ? true : false
    couponList.value = res
    canGet.value = true
  })
}

const getCoupon = (options: { currentTarget: { dataset: { index: number; id: Long; hasuse: boolean } } }) => {
  const { index, id, hasuse } = options.currentTarget.dataset
  if (hasuse) return
  if (!isLogin.value) {
    uni.$emit(LOGIN_OPEN, true)
    isShow.value = false
    return
  }
  const currentItem = couponList.value[index]
  getOneCoupon(id, {})
    .then(() => {
      currentItem.restNum = currentItem.restNum - 1
      if (currentItem.restNum === 0) {
        currentItem.hasUse = !currentItem.hasUse
      }
      couponList.value[index] = currentItem
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
      couponList.value[index] = currentItem
    })
}

// 监听props变化
watch(
  () => props.decoretionProperties,
  (newVal) => {
    if (newVal) {
      formData.value = newVal
    }
  },
  {
    immediate: true,
  },
)

// 初始化
onMounted(() => {
  getCouponList()
  isShow.value = false
  emit('stopRoll', isShow.value)
})

// 在组件卸载时清除事件监听
onMounted(() => {
  return () => {
    uni.$off(APP_LAUNCH)
  }
})
</script>

<style lang="scss" scoped>
.coupon_component {
  background: #fff;
  margin-top: 20rpx;
  margin-bottom: 20rpx;
  padding-left: 20rpx;

  .coupon_c {
    display: inline-block;
    margin-right: 20rpx;
  }

  .boxCoupon {
    width: 300rpx;
    height: 150rpx;
    display: inline-block;
    background: #ccc;
    overflow: hidden;
    position: relative;
  }

  .left_icon {
    width: 12rpx;
    height: 150rpx;
    position: absolute;
    background-image: url('https://imagesize.hrwork.com/sp/files/a0be5a1f-9c89-4ce7-8148-773e3972657f.png');
    background-size: 100% 100%;
    left: 0;
    top: 0;
  }

  .right_icon {
    width: 12rpx;
    height: 150rpx;
    position: absolute;
    background-image: url('https://imagesize.hrwork.com/sp/files/b96e2952-4eea-48a7-89bb-e0e489d90333.png');
    background-size: 100% 100%;
    right: 0;
    top: 0;
  }

  .bg_orange {
    background-color: rgba(246, 164, 44, 1);
  }

  .bg_red {
    background-color: rgba(252, 92, 123, 1);
  }

  .bg_green {
    background-color: rgba(31, 222, 148, 1);
  }

  .bg_blue {
    background-color: rgba(116, 211, 215, 1);
  }

  .bg_gray {
    background-color: #ccc;
  }

  .boxCoupon__scope {
    .boxCoupon__scope--price--left {
      font-size: 28rpx;
      text-align: center;
      color: #fff;

      .coupon_title {
        font-size: 72rpx;
      }
    }
  }

  .boxCoupon__rule {
    .boxCoupon__rule--right {
      font-size: 28rpx;
      text-align: center;
      color: #fff;
    }
  }

  .circle {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 112rpx;
    height: 112rpx;
    border-radius: 100%;
    border: 4rpx solid #999999;
    position: absolute;
    top: 0;
    right: 0;
  }

  .circle__inside {
    width: 72rpx;
    height: 72rpx;
    border-radius: 100%;
    border: 1px solid #999999;
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .circle__inside--title {
    position: relative;
    top: 0;
    left: 0;
    font-size: 24rpx;
    transform: rotate(-15deg);
    color: #999999;
  }
}
</style>
