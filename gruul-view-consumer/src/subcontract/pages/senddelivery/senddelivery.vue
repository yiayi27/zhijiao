<template>
  <view class="delivery">
    <view class="delivery__goods">
      <view class="delivery__img">
        <image :src="image" />
      </view>
      <view class="delivery__info">
        <view>承运公司：{{ orderDelivery.shipmentName }}</view>
        <view style="margin: 10rpx 0">运单编号：{{ orderDelivery.waybillNo }}</view>
        <view>官方电话：暂无</view>
      </view>
    </view>
    <uv-steps :steps="steps" :current="active" direction="column" activeColor="#ee0a24">
      <uv-steps-item v-for="item in steps" :key="item.text" :title="item.text" :desc="item.desc" />
    </uv-steps>
  </view>
</template>

<script setup lang="ts">
import { onBeforeUnmount, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getSenddelivery as getSenddeliveryApi } from '@/api/modules/order'
import { APP_LAUNCH } from '@/utils/emitNameConfig'

// 响应式变量
const steps = ref<any[]>([])
const image = ref('')
const orderDelivery = ref<Obj>({})
const active = ref(0)

// 生命周期钩子
onLoad((options: any) => {
  if (options.data) {
    const { image: imgSrc, orderDelivery: delivery } = JSON.parse(decodeURIComponent(options.data))
    image.value = imgSrc
    orderDelivery.value = delivery
    getSenddelivery({
      waybillNo: delivery.waybillNo,
      shipperType: delivery.shipperType,
    })

    uni.$on(APP_LAUNCH, () => {
      getSenddelivery({
        waybillNo: delivery.waybillNo,
        shipperType: delivery.shipperType,
      })
    })
  }
})

onBeforeUnmount(() => {
  uni.$off(APP_LAUNCH)
})

// 方法定义
const getSenddelivery = ({ waybillNo, shipperType }: { waybillNo: string; shipperType: string }) => {
  getSenddeliveryApi({
    waybillNo,
    shipperType,
  }).then((res) => {
    const parsedSteps = JSON.parse(res) || []
    if (!parsedSteps.map) return
    steps.value = parsedSteps.map(({ context, time }: { context: string; time: string }) => {
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
