<template>
  <UPopup v-model:show="visible" mode="bottom" safeAreaInsetTop @close="onClose">
    <view class="border">
      <view class="border__title" @click="chooseWay">
        <view :class="hasClick ? 'click' : 'noClick'">可用优惠券({{ ycoupons.length }})</view>
        <view :class="!hasClick ? 'click' : 'noClick'">不可用优惠券({{ noCoupons.length }})</view>
        <MIcon name="iconshangpinxiangqing-guanbi" size="38rpx" style="position: absolute; top: 36rpx; right: 20rpx; z-index: 99" @click="onClose" />
      </view>
      <view v-show="hasClick" class="couponBox">
        <view v-for="item in ycoupons" :key="`${item.id}-${item.isChoose ? 'true' : 'false'}`" class="coupon" :data-item="item">
          <coupon :coupon="item" state="car" @catchchooseCoupon="chooseCoupon" />
        </view>
      </view>
      <view v-show="!hasClick" class="couponBox">
        <view v-for="item in noCoupons" :key="`${item.id}-${item.isChoose ? 'true' : 'false'}`" class="coupon" :data-item="item">
          <Coupon :coupon="item" state="car" />
        </view>
      </view>
      <view v-if="ycoupons && hasClick" class="sureBtn" @click.stop="onTap">
        <view class="sureBtn__content">确定</view>
      </view>
    </view>
  </UPopup>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import Coupon from '@/components/CouponCom/Coupon/Coupon.vue'
import MIcon from '@/components/MIcon/MIcon.vue'

interface CouponItem {
  id: Long
  isChoose: boolean
  canUse: boolean
  [key: string]: any
}

const props = defineProps({
  coupons: {
    type: Array as () => CouponItem[],
    default: () => [],
  },
})

const emit = defineEmits(['onClose', 'onSelect'])

const visible = defineModel<boolean>('visible', { required: true })

const ycoupons = ref<CouponItem[]>([])
const noCoupons = ref<CouponItem[]>([])
const hasClick = ref(true)

watch(
  () => props.coupons,
  (value) => {
    const useList: CouponItem[] = []
    const noUseList: CouponItem[] = []

    if (value) {
      value.forEach((item: CouponItem) => {
        item.isChoose = false
        if (item.canUse) {
          useList.push(item)
        }
        if (!item.canUse) {
          noUseList.push(item)
        }
      })
    }

    ycoupons.value = useList
    noCoupons.value = noUseList
  },
  { immediate: true },
)

const onClose = () => {
  visible.value = false
  emit('onClose')
}

const chooseWay = () => {
  hasClick.value = !hasClick.value
}

const chooseCoupon = (item: CouponItem) => {
  const cItem = item
  ycoupons.value.forEach((useItem) => {
    if (useItem.id === cItem.id) {
      useItem.isChoose = !useItem.isChoose
    } else {
      useItem.isChoose = false
    }
  })
  // 触发响应式更新
  ycoupons.value = [...ycoupons.value]
}

const onTap = () => {
  const selectedItems = ycoupons.value.filter((v) => v.isChoose === true)
  if (selectedItems.length) {
    emit('onSelect', selectedItems[0])
  } else {
    emit('onSelect')
  }
}
</script>

<style lang="scss" scoped>
.border {
  height: 1000rpx;
  background: #fff;
  overflow-y: auto;
  // max-height: 600rpx;

  &__title {
    background-color: white;
    height: 100rpx;
    display: flex;
    justify-content: space-around;
    align-items: center;
    position: fixed;
    width: 100%;
    z-index: 1000;
    border-bottom: 1px solid #ddddde;
    padding: 0 20rpx;
    border-top-right-radius: 40rpx;
    border-top-left-radius: 40rpx;
    margin-top: -1px;

    .click {
      font-weight: bold;
      font-size: 36rpx;
    }

    .noClick {
      font-weight: normal;
      color: #b2b2b2;
    }
  }
}

.couponBox {
  padding-top: 116rpx;
  padding-bottom: 100rpx;
  overflow: hidden;
}

.couponBox::-webkit-scrollbar {
  display: none;
}

.coupon {
  color: black;
  display: flex;
}

.sureBtn {
  position: fixed;
  bottom: 0;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 88rpx;
  background-image: linear-gradient(to bottom right, red, #ea4156);
  color: white;

  // &__content {
  //   width: 80rpx;
  //   height: 60rpx;
  //   border-radius: 100rpx;
  //   border: 1px solid #EA4156;
  //   background-color: #EA4156;
  //   color: white;
  //   display: flex;
  //   align-items: center;
  //   justify-content: center;
  // }
}
</style>
