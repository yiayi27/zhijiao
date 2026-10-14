<template>
  <view>
    <view class="success">
      <!-- <view class="success__image">
    <image src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/api/os-c.png"></image>
  </view> -->
      <view class="success__top">
        <image class="success__top--img" src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/api/os.jpg" />
        <image class="success__top--icon" src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/api/zficon.png" />
        <view class="success__title">支付成功!</view>
      </view>
      <view class="success__card">
        <view class="success__card--price">
          <view class="price--p">￥</view>
          <view>{{ price }}</view>
        </view>
        <view class="success__card--field">订单编号: {{ orderDetail.id }}</view>
        <view class="success__card--field">下单时间: {{ orderDetail.createTime }}</view>
        <view v-if="totalRebate" class="success__card--field">返利金额: ￥{{ totalRebate }}</view>
        <view v-if="orderDetail.giftIntegration" class="success__card--field"> 获得积分: {{ orderDetail.giftIntegration }} </view>
        <view class="success__card--field">支付方式:{{ getPayTypeName(orderDetail.payType) }}</view>
        <view class="success__btn">
          <view class="success__btn--item" @tap="backHome">返回首页</view>
        </view>
      </view>
      <!-- <image uni:if="{{image}}" src="{{image}}"></image> -->
      <canvas id="goodsCanvas" style="width: 750rpx; height: 600rpx" canvas-id="goodsCanvas" class="success__canvas" />
      <view v-if="shareVisible" class="share">
        <div class="share__img">
          <image :src="image" />
        </div>
        <view class="success__btn" style="margin-top: 180rpx">
          <view class="success__btn--item" @tap="backHome">返回首页</view>
          <uv-button class="success__btn--item" open-type="share">立即分享</uv-button>
        </view>
      </view>
    </view>
    <UPopup v-model:show="isshow" mode="center" @close="cancel">
      <view class="popup">
        <image :src="homePromptBgImg" />
        <view class="text">{{ foundationPrice }}元</view>
        <view class="close" @tap="cancel" />
        <view class="btn" @tap="topage" />
      </view>
    </UPopup>
  </view>
</template>

<script setup lang="ts">
import { onBeforeUnmount, ref } from 'vue'
import { onLoad, onShareAppMessage } from '@dcloudio/uni-app'
import { getOrderDetail as fetchOrderDetail } from '@/api/modules/order'
import { divide, multiply, floor } from 'lodash-es'
import { useOrder } from '../order/composables/useOrder'
import { useSettingStore } from '@/store/modules/setting'
import { $Popup } from '@/utils/helper'
import { APP_LAUNCH } from '@/utils/emitNameConfig'

// 响应式数据
const { getPayTypeName } = useOrder()
const detail = ref<Obj>({})
const image = ref('')
const price = ref(0)
const fundQuota = ref(0)
const goodsNumber = ref(0)
const shareVisible = ref(false)
const orderDetail = ref<Obj>({})
const orderId = ref<string | null>(null)
const isshow = ref(false)
const homePromptBgImg = ref('')
const appMode = ref('MALL')
const isFirstEnter = ref(true)
const totalRebate = ref(0)
const foundationPrice = ref(0)

// 方法
const getOrderDetail = (id: string) => {
  if (!id) return

  fetchOrderDetail(id)
    .then((detailData: any) => {
      price.value = detailData.payAmount
      totalRebate.value = detailData.rebateAmount
      goodsNumber.value = detailData.productTotalQuantity
      orderDetail.value = detailData
      isFirstEnter.value = false
      foundationPrice.value = floor(multiply(detailData.payAmount, floor(divide(useSettingStore().recyclingsetting.fundQuota, 100), 2)), 2)
    })
    .catch(() => {
      $Popup.toast('详情获取失败')
    })
}

const backHome = () => {
  useSettingStore().backHome()
}

const cancel = () => {
  isshow.value = false
}

const topage = () => {
  uni.redirectTo({
    url: '/subcontract/pages/myfund/myfund',
  })
}

// 生命周期钩子
onLoad((options: { orderId?: string } = {}) => {
  // 异步初始化
  const init = async () => {
    await useSettingStore().getRecyclingSetting()
    orderId.value = options?.orderId || null
    homePromptBgImg.value = useSettingStore().recyclingsetting.buyBgImg
    fundQuota.value = useSettingStore().recyclingsetting.fundQuota
    isshow.value = useSettingStore().recyclingsetting.goodsFundState
    appMode.value = useSettingStore().shopSetting.appMode || 'MALL'

    if (useSettingStore().isReady && options?.orderId) {
      getOrderDetail(options.orderId)
    }
  }

  init()

  uni.$on(APP_LAUNCH, () => {
    if (options?.orderId) {
      getOrderDetail(options.orderId)
    }
  })
})

onBeforeUnmount(() => {
  uni.$off(APP_LAUNCH)
})

// 分享设置
onShareAppMessage(() => {
  return {
    title: detail.value.title,
    path: '/subcontract/pages/index/index',
    imageUrl: image.value,
  }
})
</script>

<style lang="scss" scoped>
@include b(success) {
  @include e(top) {
    // @include flex(flex-start);
    width: 100%;
    height: 340rpx;
    padding-left: 240rpx;
    padding-top: 100rpx;
    background: #f9604c;
    color: #fff;
    position: relative;

    @include m(img) {
      position: absolute;
      width: 100%;
      height: 100%;
      left: 0;
      top: 0;

      image {
        width: 100%;
        height: 100%;
      }
    }

    @include m(icon) {
      position: absolute;
      width: 282rpx;
      height: 242rpx;
      left: 0;
      top: 0;
      z-index: 3;
      left: 0;
      top: 30rpx;
    }
  }

  @include e(title) {
    font-weight: bold;
    position: relative;
    z-index: 3;
    margin-bottom: 20rpx;
  }

  @include e(time) {
    font-size: 28rpx;
    position: relative;
    z-index: 3;
  }

  @include e(canvas) {
    position: fixed;
    top: -2000rpx;
  }

  @include e(card) {
    width: 690rpx;
    margin: 0 auto;
    margin-bottom: 20rpx;
    box-shadow: 0 0 20rpx rgba($color: #000000, $alpha: 0.2);
    padding: 0 20rpx;
    margin-top: -80rpx;
    background: #fff;
    z-index: 3;
    position: relative;
    &::before {
      content: '';
      position: absolute;
      z-index: 3;
      width: 710rpx;
      height: 12rpx;
      background: #c43e2e;
      margin-left: -30rpx;
      border-radius: 6rpx;
    }
    @include m(price) {
      @include flex;

      text-align: center;
      font-size: 48rpx;
      padding: 40rpx;
      border-bottom: 1px solid rgb(235, 235, 235);
      vertical-align: bottom;

      .price--p {
        font-size: 28rpx;
        position: relative;
        bottom: -6rpx;
      }
    }

    @include m(field) {
      font-size: 24rpx;
      margin: 20rpx 0;
    }
  }

  @include e(image) {
    @include flex;

    margin-top: 240rpx;

    image {
      width: 514rpx;
      height: 332rpx;
      display: block;
    }
  }

  @include e(btn) {
    @include flex();

    @include m(item) {
      border: 1px solid #ff5a52;
      padding: 10rpx 30rpx;
      border-radius: 30rpx;
      font-size: 28rpx;
      margin: 20rpx;
      color: #ff5a52;

      &.solid {
        background: #ff5a52;
        color: #fff;
      }
    }
  }

  .share {
    @include flex();

    width: 100vw;
    height: 100vh;
    background-color: rgba($color: #000000, $alpha: 0.8);
    flex-direction: column;
    position: fixed;
    top: 0;
    left: 0;
    z-index: 99;

    &__img {
      width: auto;
      height: 440rpx;
      background: #fff;
    }

    img {
      width: 100%;
      height: auto;
    }
  }
}

.popup {
  width: 653rpx;
  height: 681rpx;
  position: relative;

  image {
    width: 100%;
    height: 100%;
  }

  .text {
    width: 100%;
    font-size: 88rpx;
    color: #fc575a;
    position: absolute;
    top: 280rpx;
    left: 0;
    right: 0;
    text-align: center;
  }

  .close {
    width: 100rpx;
    height: 100rpx;
    // background-color: rgba(0,0,0,0.3);
    position: absolute;
    top: 0;
    right: 40rpx;
  }

  .btn {
    width: 420rpx;
    height: 90rpx;
    position: absolute;
    bottom: 35rpx;
    right: 0;
    left: 0;
    margin: 0 auto;
  }
}
</style>
