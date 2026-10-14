<template>
  <view class="order">
    <uv-tabs
      :list="tabsList"
      :current="currentTabIndex"
      itemStyle="font-size:28rpx;lineHeight:88rpx;background:#fff"
      lineWidth="40"
      lineColor="#ee0a24"
      :activeStyle="{
        color: '#303133',
        fontWeight: 'bold',
        transform: 'scale(1.05)',
      }"
      :inactiveStyle="{
        color: '#606266',
        transform: 'scale(1)',
      }"
      @change="onTabChange"
    />
    <OrderList :orders="orders" :query="query" :isLast="isLast" :isFistEnter="isFistEnter" :active="active" @call="callbackAction" />
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick, onBeforeUnmount } from 'vue'
import { onLoad, onShow, onReachBottom } from '@dcloudio/uni-app'
import { getOrders } from '@/api'
import { useOrder } from '@/composables/mixins/useOrder'
import { isNumber } from 'lodash-es'
import { getAfterSaleList } from '@/api/modules/afterSale'
import OrderList from './order-list/order-list.vue'
import { useSettingStore } from '@/store/modules/setting'
import { APP_LAUNCH } from '@/utils/emitNameConfig'

// 数据状态
const active = ref('-1')
const currentTabIndex = ref(0)
const orders = ref<any[]>([])
const query = reactive({
  current: 1,
  keyword: '',
  /**
   * -1：所有订单, 0.待付款（待买家付款）,
   *  1.待发货（买家已付款）,
   *  3.待收货（物流订单已发货）, 4.已完成（用户已经签收）,
   *  5.待评价, 6.售后订单, 7.已关闭
   */
  orderStatus: '-1',
  size: 10,
})
const isLast = ref(false)
const isFistEnter = ref(true)
const tabsList = [
  { name: '全部', type: '-1' },
  { name: '待付款', type: '0' },
  { name: '待发货', type: '1' },
  { name: '待收货', type: '3' },
  { name: '已完成', type: '4' },
  { name: '待评价', type: '5' },
  { name: '售后订单', type: '6' },
  { name: '已关闭', type: '7' },
]

// 从useOrder获取方法
const { receiptDisabled } = useOrder(() => callbackAction())

// 方法
const callbackAction = () => {
  getOrderslist(
    {
      current: 1,
    },
    true,
  )
}

const onTabChange = async ({ type, index }: { type: string; index: number }) => {
  orders.value = []
  updateQuery({
    orderStatus: type,
    current: 1,
  })
  getOrderslist({}, false, () => {
    active.value = type
    currentTabIndex.value = index
  })
}

const updateQuery = (params: Obj) => {
  Object.assign(query, params)
  isLast.value = false
}

const getOrderslist = async (customQuery: Obj = {}, refresh = false, cb?: () => void) => {
  /** 如果已为最后一条记录，跳过请求 */
  if (isLast.value && !refresh) {
    cb?.()
    return
  }
  uni.showLoading({
    title: '加载中...',
  })
  const queryObj = {
    ...query,
    ...customQuery,
  }
  const res = query.orderStatus === '6' ? await getAfterSaleList(queryObj) : await getOrders(queryObj)
  const { current, pages, list } = res
  orders.value = handOrderList(current > 1 && !refresh ? [...orders.value, ...list] : list)

  /** 如果为最后一页设置到底状态 */
  if (current === pages) {
    isLast.value = true
  }
  cb?.()
  uni.hideLoading()
}

const handOrderList = (list: any[]) => {
  return list.map((item) => {
    if (item.payAmount && item.payAmount.toFixed) {
      item.payAmount = item.payAmount.toFixed(2)
    }
    if (item.expireTime && !isNumber(item.expireTime)) {
      item.expireTime = +new Date(item.expireTime.replace(/-/gi, '/')) - +new Date()
    }
    let status = false
    if (item.itemVoList) {
      item.itemVoList.forEach((goods: any) => {
        if (goods.afs) {
          if (!status) {
            status = receiptDisabled(goods.afs)
          }
        }
      })
    }
    item.disabled = status
    return item
  })
}

// 生命周期钩子
onLoad((options: Obj = {}) => {
  const type = options.type || '-1'
  const normalTabIndex = options.type
    ? tabsList
        .map((item, index) => {
          return item.type === options.type ? index : 0
        })
        .filter(Boolean)[0]
    : 0

  if (useSettingStore().isReady) {
    active.value = type
    query.orderStatus = normalTabIndex ? type : '-1'
    currentTabIndex.value = normalTabIndex
    nextTick(() => {
      getOrderslist()
    })
  }

  uni.$on(APP_LAUNCH, () => {
    active.value = type
    currentTabIndex.value = normalTabIndex
    nextTick(() => {
      getOrderslist()
    })
  })
})

onBeforeUnmount(() => {
  uni.$off(APP_LAUNCH)
})

onShow(async () => {
  await useSettingStore().getSetting()
  if (isFistEnter.value) {
    isFistEnter.value = false
    return
  } else {
    getOrderslist(
      {
        current: 1,
      },
      true,
    )
  }
})

onReachBottom(() => {
  query.current++
  getOrderslist()
})
</script>

<style lang="scss" scoped>
@import '@/assets/styles/var.scss';

page {
  background: $main-background;
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
