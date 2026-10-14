<template>
  <view class="order__detail">
    <view class="order--status">
      <!-- 交易关闭 -->
      <view v-if="isClose(detail.status)">
        <view class="status--text">交易关闭</view>
        <view style="status--description">{{ getOrderStatusTips(detail.status) }}</view>
      </view>
      <!-- 待付款 -->
      <view v-else-if="isPendingPayment(detail.status)">
        <view class="status--text">等待买家付款</view>
        <view class="status--description">
          <text>剩</text>
          <uv-count-down :time="detail.expireTime" />
          <text>自动关闭</text>
        </view>
      </view>
      <view v-else>
        <view class="status--text">{{ getOrderStatusName(detail.status) }}</view>
      </view>
      <view :style="{ marginLeft: '30rpx' }">
        <MIcon :name="getStatusIcon(detail.status)" />
      </view>
    </view>
    <view class="order--receiver">
      <uv-cell-group :customStyle="{ background: '#fff' }">
        <uv-cell title="配送方式" :value="getDeliveryTypeName(detail.orderDelivery.deliveryType)" value-class="logistic--type">
          <template #icon>
            <view class="iconfont icondingdanxiangqing-wuliu" />
          </template>
        </uv-cell>
        <uv-cell
          v-if="steps.length"
          :title="steps[0] && steps[0].context"
          :label="steps[0] && steps[0].time"
          title-class="logistic--detail"
          isLink
          @tap="goDeliveryDetail"
        >
          <template #icon>
            <view class="iconfont icondingdanxiangqing-wuliu" />
          </template>
        </uv-cell>
        <uv-cell
          v-if="detail.orderDelivery.deliveryType === 'LOGISTICS'"
          :title="'收货人：' + detail.orderDelivery.receiverName"
          :value="detail.orderDelivery.receiverPhone"
          :label="'取货地址：' + getAddress(detail.orderDelivery)"
          label-class="addr"
          value-class="phone"
          custom-class="receiver-info"
        >
          <template #icon>
            <view class="iconfont icongerenzhongxin-dizhiguanli" />
          </template>
        </uv-cell>
        <uv-cell
          v-else
          center
          :title="detail.orderDelivery.receiverName + ' ' + detail.orderDelivery.receiverPhone"
          :label="getAddress(detail.orderDelivery)"
          label-class="addr"
          value-class="phone"
          title-class="cell-style"
          label-style="cell-style"
          custom-class="receiver-info"
        >
          <template #icon>
            <view class="iconfont icongerenzhongxin-dizhiguanli" />
          </template>
          <template #right-icon>
            <uv-button
              v-if="canUpdateAddress(detail.status)"
              class="addr__button"
              :data-item="detail.orderDelivery"
              color="#FF4E63"
              plain
              size="mini"
              @tap="handleChangeAddress"
            >
              修改
            </uv-button>
          </template>
        </uv-cell>
      </uv-cell-group>
    </view>
    <view v-if="detail.userNote" class="order--message">
      <view class="message--l">
        <image class="message--icon" src="https://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/images/message-icon.png" />
      </view>
      <view class="message--r">
        <view class="message--label">买家留言</view>
        <view class="message--content">{{ detail.userNote }}</view>
      </view>
    </view>
    <view class="order--goods">
      <view v-for="(item, index) in detail.orderItemList" :key="index">
        <view :data-id="item.productId" :data-img="item.productPic" @tap="goGoods">
          <Goods :key="index" :goodsData="item" />
        </view>

        <!-- 配送中不可申请售后 -->
        <block v-if="userApplyBtnVisible(detail.status, item.afs) && applyNumNotExceeded(item.afs, item.applyNum) && !detail.expire">
          <view
            v-if="userApplyVisible(item.afs) || isRevoke(item.afs)"
            class="a__btn"
            :data-actions="afterActions('user', detail.status, detail.orderDelivery?.deliveryType === 'LOGISTICS')"
            :data-afs="item.afs"
            :data-goods="item"
            @tap="handleApply"
          >
            {{ afterActions('user', detail.status, detail.orderDelivery?.deliveryType === 'LOGISTICS')?.length === 1 ? '申请退款' : '申请售后' }}
          </view>
          <block v-else>
            <view class="a__btn" :data-goods="item" :data-afsid="item.afs?.id" @tap="goAfterDetail">
              {{ getAfterTypeName(item.afs?.type || '') }} {{ getAfterStatusName(item.afs?.status || '') }}
            </view>
            <!-- <view wx:else class="a__btn" bind:tap="goAfterDetail" data-goods="{{item}}" data-afsid="{{item.afs.id}}"></view> -->
          </block>
        </block>
      </view>
    </view>
    <uv-cell-group :customStyle="{ background: '#fff' }">
      <uv-cell v-if="detail.couponAmount" title="优惠券" :value="'-￥' + detail.couponAmount" value-class="price" />
      <uv-cell v-if="detail.fullScaleAmount" title="满减优惠" :value="'-￥' + detail.fullScaleAmount" value-class="price" />
      <uv-cell v-if="detail.memberAmount" title="会员本单立省" :value="detail.memberAmount" value-class="price" />
      <uv-cell v-if="detail.integrationAmount" title="积分抵扣" :value="'-￥' + detail.integrationAmount" value-class="price" />
      <uv-cell v-if="detail.freightAmount" title="配送费" :value="detail.freightAmountstr" value-class="price" />
      <uv-cell title="支付总金额" :value="'¥' + detail.payAmount" value-class="price" custom-class="order-price" />
    </uv-cell-group>
    <block v-for="(item, index) in customForm" :key="index">
      <block v-if="item.value">
        <uv-cell v-if="isInputItem(item.type)" :key="'input-' + index" :title="item.key" :value="item.value" custom-class="order-price" />
        <uv-cell v-else-if="isDateItem(item.type)" :key="'date-' + index" :title="item.key" :value="item.time" custom-class="order-price" />
        <uv-cell v-else :key="'other-' + index" :data-item="item" :title="item.key" is-link value="点击查看" :border="false" @tap="handleSeePhoto" />
      </block>
    </block>
    <view class="order--info">
      <view>订单编号：{{ detail.id }}</view>
      <view>支付方式：{{ getPayTypeName(detail.payType) }}</view>
      <view>创建时间：{{ detail.createTime }}</view>
      <view v-if="detail.payTime">付款时间：{{ detail.payTime }}</view>
      <view v-if="detail.orderDelivery.deliveryTime"> 发货时间：{{ detail.orderDelivery.deliveryTime }} </view>
      <view v-if="detail.closeTime">关闭时间：{{ detail.closeTime }}</view>
      <view v-if="detail.commentTime">评价时间：{{ detail.commentTime }}</view>
      <view v-if="detail.orderDelivery.receiveTime"> 成交时间：{{ detail.orderDelivery.receiveTime }} </view>
    </view>
    <view v-if="userType === 'MALL'" class="order--buttons">
      <!-- 待付款 -->
      <block v-if="isPendingPayment(detail.status)">
        <uv-button @tap="backHome">返回首页</uv-button>
        <uv-button type="error" @click="gotoPay">立即付款</uv-button>
      </block>
      <!-- 已关闭 -->
      <block v-else-if="isClose(detail.status)">
        <uv-button @tap="backHome">返回首页</uv-button>
        <uv-button type="error" @tap="buyAgain">再次购买</uv-button>
      </block>
      <!-- 待收货 -->
      <block v-else-if="isWaitTake(detail.status)">
        <uv-button @tap="backHome">返回首页</uv-button>
        <uv-button v-if="!detail.rBtnStatus" type="error" @click="receipt({ id: detail.id, disabled: false }, callbackAction)"> 确认收货 </uv-button>
      </block>
      <!-- 待评价 -->
      <block v-else-if="isWaitComment(detail.status)">
        <uv-button @tap="backHome">返回首页</uv-button>
        <uv-button v-if="detail.openEvaluate" type="primary" @click="goReviews(detail.id)">立即评价</uv-button>
      </block>
      <block v-else>
        <uv-button @tap="backHome">返回首页</uv-button>
      </block>
    </view>
    <uv-action-sheet
      ref="actionSheetRef"
      title="申请售后"
      :actions="afterActions('user', detail.status, detail.orderDelivery.deliveryType === 'LOGISTICS')"
      :round="20"
      :safeAreaInsetBottom="true"
      @select="onSelect"
    />
    <uv-action-sheet ref="pickerRef" :actions="columns" @select="confirm"></uv-action-sheet>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick, onBeforeUnmount } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import { useOrder } from '@/composables/mixins/useOrder'
import { getOrderDetail, getdeliveryInfo } from '@/api/modules/order'
import { getApplyNumber } from '@/api/modules/afterSale'
import { isNumber } from 'lodash-es'
import { useCustomForm } from '@/composables/helper/useCustomForm'
import { useAfterSale } from '@/composables/helper/useAfterSale'
import { useHelper } from './composables/useHelper'
import { useOrder as useLocalOrder } from '../order/composables/useOrder'
import { useSettingStore } from '@/store/modules/setting'
import { $Popup } from '@/utils/helper'
import { APP_LAUNCH } from '@/utils/emitNameConfig'
import type { OrderDetail, IFromItem, OrderItemWithAfs } from './types'

const actionSheetRef = ref<any>(null)
// 状态定义
const detail = ref<OrderDetail>({
  id: '',
  status: '',
  type: '',
  payType: '',
  orderItemList: [],
  orderDelivery: {
    deliveryType: '',
    receiverName: '',
    receiverPhone: '',
  },
  applyNumHasExceeded: true,
})
const userType = ref('user')
const steps = ref<any[]>([])
const customForm = ref<IFromItem[]>([])
const afterSaleForm = reactive({
  description: '',
  images: '',
  productQuantity: '',
  productSkuId: '',
  refundAmount: '',
  templateId: '',
  type: '',
})
const afGoods = ref<any>({})
const afs = ref<any>({})
const options = ref<any>({})

// 使用各种composables
const { applyNumNotExceeded, isRevoke, userApplyBtnVisible, userApplyVisible, getAfterStatusName, getAfterTypeName, afterActions } = useAfterSale()
const { isInputItem, isDateItem } = useCustomForm()
const { getAddress, canUpdateAddress, getStatusIcon } = useHelper()
const { isPendingPayment, getOrderStatusName, isWaitTake, isClose, getDeliveryTypeName, isWaitComment, getPayTypeName, getOrderStatusTips } =
  useLocalOrder()
const { pay: orderPay, cancelOrder: cancel, receiptOrder: receipt, receiptDisabled } = useOrder(() => callbackAction())

// 方法定义
const getApplyNum = async () => {
  const { number, expire } = await getApplyNumber(options.value.orderId)
  try {
    let orderItemList: OrderItemWithAfs[] = []
    detail.value.orderItemList.forEach((goods) => {
      const apply = number.filter((item: any) => {
        return item.productSkuId === goods.productSkuId
      })
      const applyNum = apply.length > 0 ? apply[0].userApplyNumber : 0
      orderItemList.push({
        ...goods,
        applyNum,
      })
    })
    detail.value.orderItemList = orderItemList
    detail.value.expire = expire
  } catch (error) {
    console.log(error)
    $Popup.toast('申请次数获取失败')
  }
}

const handleApply = ({
  currentTarget: {
    dataset: { actions, goods, afs: afsData },
  },
}: {
  currentTarget: {
    dataset: { actions: any; goods: any; afs: any }
  }
}) => {
  if (goods.applyNum > 3) return $Popup.toast('超过最大申请数')
  afs.value = afsData
  if (actions.length === 1) {
    afGoods.value = goods
    handleApplyAfter('退款', 'REFUND', {
      hasPicked: 1,
    })
    return
  }
  afGoods.value = goods
  toggleSheetVisible()
}

const goAfterDetail = ({
  currentTarget: {
    dataset: { afsid, goods },
  },
}: {
  currentTarget: {
    dataset: { afsid: string; goods: any }
  }
}) => {
  uni.navigateTo({
    url: `/subcontract/pages/afterSaleDetail/afterSaleDetail?afsid=${afsid}&userType=${userType.value}&goods=${encodeURIComponent(JSON.stringify(goods))}`,
  })
}

const onSelect = ({ name, id: type }: { name: string; id: string }) => {
  handleApplyAfter(name, type, {})
  toggleSheetVisible(false)
}

const objectToQueryStr = (data: Obj) => {
  let str = ''
  Object.keys(data).map((key, i) => {
    return (str += `${!i ? '' : '&'}${key}=${data[key]}`)
  })
  return str
}

const handleApplyAfter = (name: string, type: string, params: Obj = {}) => {
  const { id, status } = detail.value
  const goods = afGoods.value
  const formStr = objectToQueryStr({
    ...afterSaleForm,
    title: name,
    type,
    orderId: id,
    productSkuId: goods.productSkuId,
    goods: encodeURIComponent(JSON.stringify(goods)),
    productQuantity: goods.productQuantity,
    refundAmount: goods.realAmount,
    hasPicked: status === 'WAIT_FOR_PICKUP',
    userType: userType.value,
    ...params,
  })
  uni.navigateTo({
    url: `/subcontract/pages/applyAfterSale/applyAfterSale?${formStr}`,
  })
}

const toggleSheetVisible = (bool: boolean = true) => {
  if (!bool) {
    actionSheetRef.value.close()
  } else {
    actionSheetRef.value.open()
  }
}

const transformFormToData = (forms: string) => {
  try {
    const parsedForm = JSON.parse(forms)
    customForm.value = parsedForm
    return true
  } catch {
    return false
  }
}

const handleChangeAddress = ({
  currentTarget: {
    dataset: { item },
  },
}: {
  currentTarget: {
    dataset: { item: any }
  }
}) => {
  uni.navigateTo({
    url: `/subcontract/pages/updateOrderAddress/updateOrderAddress?form=${JSON.stringify({
      ...item,
      orderId: detail.value.id,
    })}`,
  })
}

const goDeliveryDetail = () => {
  const data = {
    orderItemList: detail.value.orderItemList,
    orderDelivery: detail.value.orderDelivery,
  }
  uni.navigateTo({
    url: `/subcontract/pages/deliveryInfo/deliveryInfo?data=${encodeURIComponent(JSON.stringify(data))}`,
  })
}

const fetchDeliveryInfo = async ({ tracesId, companyName, deliveryCode }: { tracesId: string; companyName: string; deliveryCode: string }) => {
  if (!tracesId) {
    steps.value = []
    return
  }
  const res = await getdeliveryInfo({
    tracesId,
    companyName,
    deliveryCode,
    mobile: detail.value.orderDelivery.receiverPhone,
  })
  steps.value = res
}

const handleSeePhoto = ({
  currentTarget: {
    dataset: { item },
  },
}: {
  currentTarget: {
    dataset: { item: IFromItem }
  }
}) => {
  uni.previewImage({
    current: item.value,
    urls: [item.value],
  })
}

const callbackAction = () => {
  getOrderDetail(options.value.orderId || options.value.orderId)
}

const columns = ref([
  {
    name: '余额支付',
    value: 'BALANCE',
  },
  {
    name: '微信支付',
    value: 'WECHAT',
  },
])
const pickerRef = ref()

const gotoPay = () => {
  pickerRef.value.open()
}

const fetchOrderDetail = (orderId: string) => {
  getOrderDetail(orderId)
    .then((orderDetail) => {
      setLocalFreightAmount(orderDetail)
      orderDetail = setCountdown(orderDetail)
      orderDetail = setAfsData(orderDetail)
      // 如果存在物流数据 获取物流信息
      detail.value = orderDetail
      userType.value = orderDetail.type
      nextTick(() => getApplyNum())
      if (orderDetail.orderDelivery) {
        fetchDeliveryInfo({
          tracesId: orderDetail.orderDelivery.deliverySn,
          companyName: orderDetail.orderDelivery.deliveryCompany,
          deliveryCode: orderDetail.orderDelivery.deliveryCode,
        })
      }
      transformFormToData(orderDetail.customForm || '[]')
    })
    .catch((err) => {
      console.log(err)
      $Popup.toast('详情获取失败')
    })
}

const setAfsData = (item: any) => {
  let status = false
  if (item.orderItemList) {
    item.orderItemList.forEach((goods: any) => {
      if (goods.afs) {
        if (!status) {
          status = receiptDisabled(goods.afs)
        }
      }
    })
  }
  item.rBtnStatus = status
  return item
}

const setLocalFreightAmount = (orderDetail: any) => {
  orderDetail.freightAmountstr = orderDetail.privileges && orderDetail.privileges.includes('2') ? '免配送费' : '￥' + orderDetail.freightAmount
}

const setCountdown = (orderDetail: any) => {
  if (orderDetail.expireTime && !isNumber(orderDetail.expireTime)) {
    orderDetail.expireTime = +new Date(orderDetail.expireTime.replace(/-/gi, '/')) - +new Date()
  }
  return orderDetail
}

const goGoods = ({
  currentTarget: {
    dataset: { id, img },
  },
}: {
  currentTarget: {
    dataset: { id: string; img: string }
  }
}) => {
  uni.navigateTo({
    url: `/subcontract/pages/detail/detail?id=${id}`,
  })
}

const backHome = () => {
  useSettingStore().backHome()
}

const buyAgain = () => {
  const items = detail.value.orderItemList.map((item) => {
    return {
      activityId: item.activityId,
      activityProductId: item.activityProductId,
      dominoState: item.dominoState,
      number: item.productQuantity,
      skuId: item.productSkuId,
    }
  })
  uni.navigateTo({
    url: `/modules/pages/submit/submit?items=${encodeURIComponent(JSON.stringify(items))}`,
  })
}

// 生命周期钩子
onLoad((loadOptions: Record<string, string> = {}) => {
  const orderId = loadOptions.orderId
  options.value = loadOptions
  if (useSettingStore().isReady && orderId) {
    fetchOrderDetail(orderId)
  }
  uni.$on(APP_LAUNCH, () => {
    if (orderId) {
      fetchOrderDetail(orderId)
    }
  })
})

onBeforeUnmount(() => {
  uni.$off(APP_LAUNCH)
})

onShow(() => {
  fetchOrderDetail(options.value.orderId)
})

// 方法暴露给模板
const pay = (payload: any, callback: () => void) => {
  console.log(payload)
  // 处理付款逻辑
  uni.showModal({
    title: '温馨提示',
    content: '确认支付订单吗？',
    success: (res) => {
      if (res.confirm) {
        // TODOS:选择支付方式
        orderPay(payload, callback)
      }
    },
  })
}

const confirm = (item: any) => {
  pay({ id: detail.value.id, type: item.value }, callbackAction)
}

const goReviews = (id: string) => {
  uni.navigateTo({
    url: `/subcontract/pages/orderEvaluation/orderEvaluation?id=${id}`,
  })
}
</script>

<style lang="scss" scoped>
@import '@/assets/styles/var.scss';

:deep(.u-count-down) {
  color: #fff;
}

.order__detail {
  @mixin m-t {
    margin-bottom: 20rpx;
  }

  background: $main-background;
  min-height: 100%;
  padding-bottom: 80rpx;

  .a__btn {
    display: block;
    max-width: 350rpx;
    min-width: 160rpx;
    padding: 10rpx 20rpx;
    text-align: center;
    border: 1px solid $main-light-color;
    margin: 0 20rpx 20rpx auto;
    border-radius: 30rpx;
    margin-left: auto;
    color: $main-light-color;
  }

  .addr__button {
    margin-left: 0;
  }

  &--tag {
    width: 100rpx;
    height: 32rpx;
    background-color: #ffecee;
    border-radius: 25rpx;
    color: #fe7888;
    margin-right: 20rpx;
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 24rpx;
    border: 1px solid #fe7888;
    padding: 0 10rpx;
  }

  .order--status {
    display: flex;
    align-items: center;
    padding: 0 80rpx;
    height: 270rpx;
    background: $main-light-color;
    color: #ffffff;

    .status--text {
      font-size: 38rpx;
    }

    .status--description {
      @include flex();

      margin-top: 15rpx;
    }

    .iconfont {
      font-size: 140rpx;
    }
  }

  .order--receiver {
    @include m-t;

    &__left {
      @include flex(flex-start);
    }

    .logistic--type {
      font-size: 28rpx;
      color: #fff;
      background: $main-light-color;
      border-radius: 10rpx;
      width: 132rpx;
      text-align: center;
      position: absolute;
      right: 30rpx;
    }

    .addr,
    .phone {
      font-size: 28rpx;
      color: #000000;
    }

    .phone {
      position: absolute;
      right: 30rpx;
    }

    .addr {
      width: 528rpx;
      // position: absolute;
    }

    .receiver-info {
      padding-bottom: 40rpx;
    }

    .van-icon,
    .iconfont {
      margin-right: 20rpx;
      font-size: 36rpx;
    }

    .icongerenzhongxin-dizhiguanli {
      position: relative;
      // top: 20rpx;
    }
  }

  .order--message {
    @include flex(space-between);
    @include m-t;

    width: 100%;
    height: auto;
    background: #fff;
    padding: 20rpx 12.1rpx;
    box-sizing: border-box;
    color: #585858;
    font-size: 28rpx;

    .message {
      &--l {
        width: 30rpx;
        height: 30rpx;
        flex: 0 0 30rpx;
      }

      &--r {
        width: calc(100% - 44rpx);
        padding-left: 20rpx;
        box-sizing: border-box;
      }

      &--icon {
        width: 30rpx;
        height: 30rpx;
      }
    }
  }

  .order--goods {
    padding: 40rpx 20rpx 20rpx;
    background: #fafafa;
  }

  .order--contact {
    height: 80rpx;
    background: #ffffff;
    display: flex;
    justify-content: center;
    align-items: center;

    image {
      width: 50rpx;
      height: 50rpx;
      margin-right: 24rpx;
    }
  }

  .order--info {
    @include m-t;

    font-size: 26rpx;
    line-height: 50rpx;
    padding: 40rpx;
    margin-top: 20rpx;
    background: #ffffff;
    color: #5c5c5c;
  }

  .order--buttons {
    @include flex;
    justify-content: space-between;
    position: fixed;
    left: 0;
    bottom: 0;
    width: 100%;
    padding-bottom: 0;
    padding-bottom: env(safe-area-inset-bottom);
    padding-bottom: constant(safe-area-inset-bottom);
    :deep(.uv-button-wrapper) {
      flex: 1 1;
    }
    .button--item {
      flex: 1 1;
      text-align: center;
      padding: 20rpx 20rpx;
      background: #fff;

      &.primary {
        background: $main-light-color;
        color: #fff;
      }
    }
  }

  .price {
    font-size: 32rpx;
    color: $main-light-color;
  }

  .logistic--detail {
    color: rgba(18, 177, 0, 1);
  }

  .receiver--info {
    .van-cell__value,
    .van-cell-text {
      color: #979797;
    }
  }
}

.cell-style {
  width: 560rpx;
}
</style>
