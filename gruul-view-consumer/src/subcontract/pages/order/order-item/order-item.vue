<template>
  <view class="order__item" :data-id="orderData.orderId || orderData.applyOrderId" :data-type="orderData.type" @tap="goDetail">
    <view class="order__item--header">
      <view class="header--left">
        <slot name="header--left" />
      </view>
      <view class="header--right">
        <slot name="header-right" />
      </view>
    </view>
    <view class="order__item--goods">
      <block v-for="(item, index) in orderData.itemVoList || [orderData]" :key="index">
        <Goods :goodsData="item">
          <block v-if="currentOrderType === '6' || orderItemStatusVisible(item.afs?.status)">
            <view class="order__item--status green" style="margin-bottom: 20rpx">
              <block v-if="currentOrderType === '6'">
                {{ getAfterStatusName(item.status) }}
                <block v-if="timeVisible(item.status)"> 还剩{{ getTimeDifference(item.deadline) }} </block>
              </block>
              <block v-else> {{ getAfterTypeName(item.afs?.type) }} {{ getAfterStatusName(item.afs?.status) }} </block>
            </view>
          </block>
        </Goods>
      </block>
    </view>
    <view class="order__item--control">
      <view class="control__info">
        <view class="info--left text__green">
          <slot name="control-af" />
        </view>
        <view class="info--right">
          支付金额：
          <text>￥{{ orderData.payAmount }}</text>
        </view>
      </view>
      <view class="control__button">
        <slot name="control-button" />
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { useAfterSale } from '@/composables/helper/useAfterSale'
import { useDate } from '@/composables/helper/useDate'

defineProps({
  orderData: {
    type: Object,
    default: () => ({}),
  },
  currentOrderType: {
    type: String,
    default: '6',
  },
})

const { timeVisible, orderItemStatusVisible, getAfterStatusName, getAfterTypeName } = useAfterSale()
const { getTimeDifference } = useDate()

const goDetail = ({
  currentTarget: {
    dataset: { id, type },
  },
}: any) => {
  uni.navigateTo({
    url: `/subcontract/pages/orderDetail/orderDetail?orderId=${id}&userType=${type}`,
  })
}
</script>

<style lang="scss" scoped>
@import '@/assets/styles/var.scss';

.order__item {
  $p-v: 20rpx;

  background: #fff;
  font-size: 24rpx;
  margin-top: 20rpx;

  .green {
    color: #09bb07;
  }

  &--header {
    @include flex(space-between);

    width: 100%;
    height: 90rpx;
    padding: 0 $p-v;
    border-bottom: 1px solid $main-border-color;
  }

  &--goods {
    padding: $p-v;
    border-bottom: 1px solid $main-border-color;
  }

  &--control {
    padding: $p-v $p-v 10rpx;

    .control__info {
      @include flex(space-between, center);

      .info--right {
        font-size: 26rpx;
        color: rgba(28, 28, 28, 1);

        text {
          font-weight: bold;
        }
      }
    }

    .control__button {
      @include flex(flex-end, center);

      width: 100%;
      margin-top: 24rpx;
    }
  }

  .text__green {
    color: #08bb07;
    margin-bottom: 20rpx;
  }
}
</style>
