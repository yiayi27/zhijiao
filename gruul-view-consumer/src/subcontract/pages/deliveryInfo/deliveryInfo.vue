<template>
  <view class="delivery">
    <view class="delivery__goods">
      <view class="delivery__img">
        <image :src="orderItemList[0].productPic" />
      </view>
      <view class="delivery__info">
        <view>承运公司：{{ orderDelivery.deliveryCompany }}</view>
        <view style="margin: 10rpx 0">运单编号：{{ orderDelivery.deliverySn }}</view>
        <view>官方电话：暂无</view>
      </view>
    </view>
    <view style="background: #fff">
      <uv-steps :current="active" direction="column" dot="true" activeColor="#ee0a24">
        <uv-steps-item v-for="item in steps" :key="item.text" :title="item.text" :desc="item.desc" />
      </uv-steps>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onBeforeUnmount, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getdeliveryInfo as getdeliveryInfoApi } from '@/api/modules/order'
import { APP_LAUNCH } from '@/utils/emitNameConfig'

// 响应式变量
const steps = ref<any[]>([])
const orderItemList = ref<any[]>([])
const orderDelivery = ref<Obj>({})
const active = ref(0)

// 生命周期钩子
onLoad((options: any) => {
  if (options.data) {
    const { orderItemList: itemList, orderDelivery: delivery } = JSON.parse(decodeURIComponent(options.data))
    orderItemList.value = itemList
    orderDelivery.value = delivery
    getdeliveryInfo({
      tracesId: delivery.deliverySn,
      companyName: delivery.deliveryCompany,
      deliveryCode: delivery.deliveryCode,
      mobile: delivery.receiverPhone,
    })

    uni.$on(APP_LAUNCH, () => {
      getdeliveryInfo({
        tracesId: delivery.deliverySn,
        companyName: delivery.deliveryCompany,
        deliveryCode: delivery.deliveryCode,
        mobile: delivery.receiverPhone,
      })
    })
  }
})

onBeforeUnmount(() => {
  uni.$off(APP_LAUNCH)
})

// 方法定义
const getdeliveryInfo = ({
  tracesId,
  companyName,
  deliveryCode,
  mobile,
}: {
  tracesId: string
  companyName: string
  deliveryCode: string
  mobile: string
}) => {
  getdeliveryInfoApi({
    tracesId,
    companyName,
    deliveryCode,
    mobile,
  }).then((res) => {
    if (!res.map) return
    steps.value = res.map(({ context, time }: { context: string; time: string }) => {
      return {
        text: context,
        desc: time,
      }
    })
  })
}
</script>

<style lang="scss" scoped>
page {
  background: $main-background;
}

@include b(delivery) {
  @include e(goods) {
    @include flex(flex-start);

    padding: 40rpx 24rpx;
    margin-bottom: 20rpx;
    background-color: #fff;
  }

  @include e(img) {
    width: 146rpx;
    height: 146rpx;
    margin-right: 20rpx;

    image {
      width: 100%;
      height: 100%;
    }
  }

  @include e(info) {
    @include flex(flex-start, flex-start);

    flex-direction: column;
  }
}
</style>
