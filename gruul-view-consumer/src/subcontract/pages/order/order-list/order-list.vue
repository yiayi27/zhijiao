<template>
  <view>
    <OrderItem v-for="(item, index) in orders" :key="index" :orderData="item" :currentOrderType="active">
      <template #header--left>
        <view class="order__top">
          <block v-if="query.orderStatus === '6'">
            <Tag v-if="item.afs" :label="getAfterTypeName(item.afs.type)" />
          </block>
          <block v-else>
            <Tag v-if="getOrderTypeName(item.type)" :label="getOrderTypeName(item.type)" />
          </block>
          订单号：{{ query.orderStatus === '6' ? item.no : item.orderId || item.applyOrderId }}
          <view class="order__top--copy text__pink" :data-item="item.orderId || item.applyOrderId" @tap.stop="copyOrder"> 复制 </view>
        </view>
      </template>
      <template #header--right>
        <block v-if="item.originalOrderId">
          <text class="order__status text__pink" :data-id="item.originalOrderId" :data-type="item.type" @tap.stop="goDetail"> 查看原订单 </text>
        </block>
        <!-- 待付款 bind:finish="onFinish" -->
        <block v-else-if="isPendingPayment(item.status)">
          <view class="order__expire">
            <text>还剩</text>
            <!-- bind:finish="onFinish" -->
            <uv-count-down :time="item.expireTime" />
            <text>关闭</text>
          </view>
          <!-- <text class="text__pink"></text> -->
        </block>
        <!-- 待发货 wx:elif="{{!isClose(item.status)}}" -->
        <block v-else>
          <text v-if="!isExcludeStatus(item.status)" class="order__status text__pink">
            {{ getOrderStatusName(item.status) || '' }}
          </text>
        </block>
      </template>

      <template #control-button>
        <view class="buttons_container">
          <!-- 待付款 -->
          <block v-if="isPendingPayment(item.status)" class="order__button">
            <uv-button @tap.capture.stop="cancel({ id: item.orderId }, callbackAction)">取消订单</uv-button>
            <uv-button type="error">支付</uv-button>
          </block>
          <!-- 待收货 -->
          <uv-button
            v-if="isWaitTake(item.status)"
            type="primary"
            :disabled="item.disabled"
            :class="{ disabled: item.disabled }"
            @tap.stop="receipt({ id: item.orderId, disabled: item.disabled }, callbackAction)"
          >
            确认收货
          </uv-button>
          <!-- 待评价 -->
          <uv-button
            v-if="isWaitComment(item.status) && hasOpenEvaluate && !excludeActive([4], active) && isafs(item.itemVoList)"
            type="primary"
            :data-id="item.orderId"
            @tap.stop="goReviews"
            >立即评价</uv-button
          >
          <uv-button v-if="isWaitReturn(item.afs && item.afs.status)" type="error" :data-id="item.afs.id" @tap.stop="handleReturn"
            >我已退货</uv-button
          >
        </view>
      </template>
    </OrderItem>
    <DataEmpty v-if="!orders.length" />
    <uv-divider v-if="isLast" textPosition="center" text="到底了" />
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { returnOfGoods } from '@/api/modules/afterSale'
import { useOrder } from '@/composables/mixins/useOrder'
import OrderItem from '../order-item/order-item.vue'
import DataEmpty from '@/components/DataEmpty/DataEmpty.vue'
import { useAfterSale } from '@/composables/helper/useAfterSale'
import { useOrder as useLocalOrder } from '../composables/useOrder'
import { useSettingStore } from '@/store/modules/setting'
import { $Popup } from '@/utils/helper'

// Props定义
interface Props {
  orders: any[]
  query: Obj
  active: string
  isLast: boolean
  isFistEnter: boolean
}

defineProps<Props>()
const emit = defineEmits(['call'])

// 状态
const { isWaitReturn, getAfterTypeName } = useAfterSale()
const { getOrderTypeName, isPendingPayment, getOrderStatusName, isWaitTake, isWaitComment, isExcludeStatus, excludeActive, isafs } = useLocalOrder()

// 从useOrder获取方法
const { cancelOrder, receiptOrder, goReviews } = useOrder((event: string) => {
  if (event === 'call') {
    emit('call')
  }
})

// 计算属性
const hasOpenEvaluate = computed(() => {
  return useSettingStore().shopSetting.hasOpenEvaluate
})

// 方法
const copyOrder = (e: { currentTarget: { dataset: { item: string } } }) => {
  const orderId = e.currentTarget.dataset.item
  uni.setClipboardData({
    data: orderId,
    success() {
      uni.getClipboardData({})
    },
  })
}

const goDetail = ({
  currentTarget: {
    dataset: { id, type },
  },
}: {
  currentTarget: {
    dataset: { id: string; type: string }
  }
}) => {
  uni.navigateTo({
    url: `/subcontract/pages/orderDetail/orderDetail?orderId=${id}&userType=${type}`,
  })
}

const handleReturn = ({
  currentTarget: {
    dataset: { id },
  },
}: {
  currentTarget: {
    dataset: { id: string }
  }
}) => {
  returnOfGoods({
    afsId: id,
    deliverySn: '',
  })
    .then(() => {
      $Popup.toast('操作成功')
    })
    .catch(() => {
      $Popup.toast('操作失败')
    })
}

// 回调函数
const callbackAction = () => {
  emit('call')
}

// 暴露给模板的action方法
const cancel = (payload: any, callback: () => void) => {
  cancelOrder(payload, callback)
}

const receipt = (payload: any, callback: () => void) => {
  receiptOrder(payload, callback)
}
</script>

<style lang="scss" scoped>
.buttons_container {
  display: flex;
}

.m__button {
  min-width: 140rpx;
  padding: 6rpx 0;
  text-align: center;
  border-radius: 30rpx;
  border: 1px solid #989898;
  outline: none;
  background: #fff;
  margin-left: 20rpx;

  &.primary {
    background: $main-light-color;
    border-color: $main-light-color;
    color: #fff;

    &[disabled] {
      $d-c: #fda7b2;
      border-color: $d-c;
      background: $d-c;
      color: #fff3f6;
    }
  }
}
.order {
  &__button {
    @include flex(flex-end);
  }

  &__top {
    @include flex();

    &--copy {
      margin-left: 20rpx;
    }
  }

  &__expire {
    @include flex();

    .van-count-down {
      // background-color: red;
      position: relative;
      bottom: -1rpx;
      color: $main-light-color;
      margin: 0 6rpx;
    }
  }

  &__status {
    position: relative;
    top: -14rpx;
  }

  .van-dialog__message {
    padding: 30rpx;
  }

  .van-dialog__message-text {
    font-size: 32rpx;
  }
}
</style>
