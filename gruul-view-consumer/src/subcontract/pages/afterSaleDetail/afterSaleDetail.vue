<template>
  <view>
    <!--<import src="./exchange.wxml"></import>-->
    <view class="after--detail">
      <!-- 因uniapp不支持微信小程序模板语法，故做此处理。 -->
      <!-- 退货退款 -->
      <template v-if="templateName === 'RETURN_REFUND-WAIT_FOR_BUSINESS_APPROVED'">
        <view class="detail--top">
          <view class="detail--top__l">
            <view class="top--status">请等待卖家处理</view>
            <view class="top--time">还剩{{ getTimeDifference(detail.deadline) }}</view>
          </view>
        </view>
        <view class="detail--description">
          <view class="description--top">你已成功起发退货退款申请，请耐心等待商家处理。</view>
          <view class="description--center">
            <view>
              <text class="dot">●</text>
              商家同意或者超时未处理，系统将默认同意。
            </view>
            <view>
              <text class="dot">●</text>
              如果商家拒绝，你可以再次发起售后，卖家会重新处理。
            </view>
          </view>
          <view class="description--buttom">
            <uv-button size="small" :customStyle="{ fontSize: '28rpx' }" style="margin-left: 15rpx" @click="handleCancel">撤销申请</uv-button>
          </view>
        </view>
      </template>
      <template v-if="templateName === 'RETURN_REFUND-WAIT_FOR_RETURN'">
        <view class="detail--top">
          <view class="detail--top__l">
            <view class="top--status">等待买家退货</view>
            <view class="top--time">还剩{{ getTimeDifference(detail.deadline) }}</view>
          </view>
        </view>
        <view class="detail--description">
          <view class="description--top">商家已同意你的退货申请，请尽快退货</view>
          <view v-if="detail.originalOrder.orderDelivery.deliveryType === 'LOGISTICS'" class="description--addr">
            <uv-cell-group :border="false">
              <uv-cell
                :title="'收货人：' + detail.address.name"
                :value="detail.address.phone"
                :label="'收货地址：' + detail.address.address"
                label-class="addr"
                value-class="phone"
                custom-class="receiver-info"
                :border="false"
              >
                <template #icon>
                  <view class="iconfont icongerenzhongxin-dizhiguanli" />
                </template>
              </uv-cell>
            </uv-cell-group>
          </view>
          <!-- wx:if="{{detail.originalOrder.orderDelivery.deliveryType==='LOGISTICS'}}" -->
          <view class="description--center border__t">
            <view>
              <text class="dot">●</text>
              超时未处理，系统将自动关闭退款申请
            </view>
            <view>
              <text class="dot">●</text>
              未与商家协商一致，请勿使用到付或平邮，以免商家拒签货物，请填写正确的物流信息
            </view>
          </view>
          <view class="description--buttom" style="width: 300rpx">
            <uv-button size="small" :customStyle="{ fontSize: '28rpx' }" @click="handleCancel">撤销申请</uv-button>
            <uv-button size="small" :customStyle="{ fontSize: '28rpx' }" style="margin-left: 15rpx" @click="handleUserReturn">退货</uv-button>
          </view>
        </view>
      </template>
      <!-- 物流退货退款，待商家确认 -->
      <template v-if="templateName === 'RETURN_REFUND-WAIT_FOR_BUSINESS_RECEIPT'">
        <view class="detail--top">
          <view class="detail--top__l">
            <view class="top--status">等待商家确认收货</view>
          </view>
        </view>
        <view class="detail--description">
          <view class="description--top">如果商家收到货并验货无误，将操作退款给您</view>
          <uv-cell
            v-if="detail.steps && detail.steps.length"
            :title="detail.steps[detail.steps.length - 1] && detail.steps[detail.steps.length - 1].text"
            :label="detail.steps[detail.steps.length - 1] && detail.steps[detail.steps.length - 1].desc"
            title-class="logistic--detail"
            isLink
          >
            <template #icon>
              <view class="iconfont icondingdanxiangqing-wuliu" />
            </template>
          </uv-cell>
          <view class="description--center">
            <view>
              <text class="dot">●</text>
              如果商家拒绝退款，需要您和商家协商。
            </view>
            <view>
              <text class="dot">●</text>
              商家同意或者超时未处理，系统将默认同意。
            </view>
          </view>
        </view>
      </template>
      <!-- 换货超时 -->
      <template v-if="templateName === 'RETURN_REFUND-CLOSE'">
        <view class="detail--top">
          <view class="detail--top__l">
            <view class="top--status">退货退款关闭</view>
            <view class="top--time">{{ detail.closeTime }}</view>
          </view>
        </view>
        <view class="detail--description">
          <view class="description--top"> 因{{ getCloseType(detail.closeType) }}，本次退货退款关闭。 </view>
          <view class="description--center">
            <text class="dot">●</text>
            如你的问题未解决，你还可以发起{{ 3 - detail.applyNum }}次申请。
          </view>
          <view class="description--buttom">
            <uv-button
              size="small"
              style="margin-left: 15rpx; font-size: 28rpx"
              @click="
                handleApplyAgain(afterActions('user', detail.originalOrder.status, detail.originalOrder.orderDelivery.deliveryType === 'LOGISTICS'))
              "
            >
              再次申请
            </uv-button>
          </view>
        </view>
      </template>
      <template v-if="templateName === 'RETURN_REFUND-SUCCESS'">
        <view class="detail--top">
          <view class="detail--top__l">
            <view class="top--status">退货成功</view>
          </view>
        </view>
      </template>
      <!-- 退款 -->
      <template v-if="templateName === 'POINT_REFUND-WAIT_FOR_BUSINESS_APPROVED' || templateName === 'REFUND-WAIT_FOR_BUSINESS_APPROVED'">
        <view class="detail--top">
          <view class="detail--top__l">
            <view class="top--status">请等待卖家处理</view>
            <view class="top--time">还剩{{ getTimeDifference(detail.deadline) }}</view>
          </view>
        </view>
        <view class="detail--description">
          <view class="description--top">你已成功发起退款申请，请耐心等待商家处理。</view>
          <view class="description--center">
            <view>
              <text class="dot">●</text>
              商家同意或者超时未处理，系统将退款给你。
            </view>
            <view>
              <text class="dot">●</text>
              如果商家拒绝，你可以再次发起售后，卖家会重新处理。
            </view>
          </view>
          <view class="description--buttom">
            <uv-button size="small" :customStyle="{ fontSize: '28rpx' }" style="margin-left: 15rpx" @click="handleCancel">撤销申请</uv-button>
          </view>
        </view>
      </template>

      <template v-if="templateName === 'POINT_REFUND-CLOSE' || templateName === 'REFUND-CLOSE'">
        <view class="detail--top">
          <view class="detail--top__l">
            <view class="top--status">退款关闭</view>
            <view v-if="detail.closeTime" class="top--time">{{ detail.closeTime }}</view>
          </view>
        </view>
        <view class="detail--description">
          <view class="description--top"> 因{{ getCloseType(detail.closeType) }}，本次售后关闭。 </view>
          <view class="description--center">
            <view v-if="detail.applyNum < 3">
              <text class="dot">●</text>
              如你的问题未解决，你还可以发起{{ 3 - detail.applyNum }}次申请。
            </view>
            <view v-if="detail.applyNum === 3">
              <text class="dot">●</text>
              你已达最大申请次数
            </view>
          </view>
          <view class="description--buttom">
            <uv-button
              size="small"
              style="margin-left: 15rpx; font-size: 28rpx"
              @click="
                handleApplyAgain(afterActions('user', detail.originalOrder.status, detail.originalOrder.orderDelivery.deliveryType === 'LOGISTICS'))
              "
            >
              再次申请
            </uv-button>
          </view>
        </view>
      </template>

      <template v-if="templateName === 'POINT_REFUND-SUCCESS' || templateName === 'REFUND-SUCCESS'">
        <view class="detail--top">
          <view class="detail--top__l">
            <view class="top--status">退款成功</view>
            <view class="top--time">{{ detail.successTime }}</view>
          </view>
        </view>
      </template>
      <!-- 换货 -->
      <template v-if="templateName === 'POINT_EXCHANGE-WAIT_FOR_BUSINESS_APPROVED' || templateName === 'EXCHANGE-WAIT_FOR_BUSINESS_APPROVED'">
        <view class="detail--top">
          <view class="detail--top__l">
            <view class="top--status">请等待卖家处理</view>
            <view class="top--time">还剩{{ getTimeDifference(detail.deadline) }}</view>
          </view>
        </view>
        <view class="detail--description">
          <view class="description--top">你已成功发起重新发货申请，请耐心等待商家处理。</view>
          <view class="description--center">
            <view>
              <text class="dot">●</text>
              如果商家拒绝，你可以再次发起售后，卖家会重新处理。
            </view>
          </view>
          <view class="description--buttom">
            <uv-button size="small" style="margin-left: 15rpx; font-size: 28rpx" :data-id="detail.id" @click="handleCancel">撤销申请</uv-button>
          </view>
        </view>
      </template>

      <!-- 换货 待发货 -->
      <template v-if="templateName === 'POINT_EXCHANGE-WAIT_FOR_SEND' || templateName === 'EXCHANGE-WAIT_FOR_SEND'">
        <view class="detail--top">
          <view class="detail--top__l">
            <view class="top--status">请等待卖家发货</view>
          </view>
        </view>
        <view class="detail--description">
          <view class="description--top">商家同意你的重新发货申请，待发出商品。</view>
        </view>
      </template>

      <!-- 换货 关闭 -->
      <template v-if="templateName === 'POINT_EXCHANGE-CLOSE' || templateName === 'EXCHANGE-CLOSE'">
        <view class="detail--top">
          <view class="detail--top__l">
            <view class="top--status">重新发货关闭</view>
            <view class="top--time">{{ detail.closeTime }}</view>
          </view>
        </view>
        <view class="detail--description">
          <view class="description--top"> 因{{ getCloseType(detail.closeType) }}，本次售后关闭。 </view>
          <view class="description--center">
            <view>
              <text class="dot">●</text>
              如你的问题未解决，你可以重新发起申请
            </view>
          </view>
          <view class="description--buttom">
            <uv-button
              size="small"
              style="margin-left: 15rpx; font-size: 28rpx"
              @click="
                handleApplyAgain(afterActions('user', detail.originalOrder.status, detail.originalOrder.orderDelivery.deliveryType === 'LOGISTICS'))
              "
            >
              再次申请
            </uv-button>
          </view>
        </view>
      </template>
      <!-- 换货 待确认 -->
      <template v-if="templateName === 'POINT_EXCHANGE-SUCCESS' || templateName === 'EXCHANGE-SUCCESS'">
        <view class="detail--top">
          <view class="detail--top__l">
            <view class="top--status">换货成功</view>
            <view class="top--time">{{ detail.successTime }}</view>
          </view>
        </view>
        <view class="detail--description">
          <view class="description--top">你已主动确认签收商品，重新发货成功。</view>
        </view>
      </template>

      <template v-if="templateName === 'POINT_EXCHANGE-WAIT_FOR_POINT_RECEIPT'">
        <view class="detail--top">
          <view class="detail--top__l">
            <view class="top--status">配送中</view>
            <view class="top--time">还剩{{ getTimeDifference(detail.deadline) }}</view>
          </view>
        </view>
        <view class="detail--description">
          <view class="description--top">商家已发出换货商品，订单配送中</view>
        </view>
      </template>
      <uv-cell-group :customStyle="{ background: '#fff' }">
        <view class="detail--delivery">
          <uv-cell v-if="isRefund(detail.type)">
            <template #title>
              <view class="detail--delivery__l">
                <text>申请退款金额</text>
              </view>
            </template>
            <template #value>
              <view>￥{{ detail.refundAmount }}</view>
            </template>
          </uv-cell>
          <uv-cell
            isLink
            :url="
              '/subcontract/pages/negotiationHistory/negotiationHistory?id=' +
              detail.item.orderId +
              '&type=' +
              '1' +
              '&isGroupLookMember=' +
              detail.isGroupLookMember
            "
          >
            <template #title>
              <view class="detail--delivery__l">
                <text>协商历史</text>
              </view>
            </template>
          </uv-cell>
        </view>
      </uv-cell-group>
      <view class="detail--goods" style="margin-top: 16rpx">
        <view v-if="isRefund(detail.type)" class="detail--goods__title">退款信息</view>
        <view v-else class="detail--goods__title">换货信息</view>
        <view class="detail--goods__container">
          <Goods :goodsData="goods" :data-id="detail.item.productId" :data-img="goods.productPic" @click="goGoods" />
        </view>
        <view class="detail--payinfo">
          <view class="detail--payinfo--description">原因 ：{{ detail.description }}</view>
          <view v-if="isRefund(detail.type)">退款金额 ￥{{ detail.refundAmount }}</view>
          <view v-else>换货数量 : {{ detail.item.productQuantity }}</view>
          <view v-if="detail.createTime">申请时间 ：{{ detail.createTime }}</view>
          <view v-if="detail.closeTime">拒绝时间 ：{{ detail.closeTime }}</view>
          <view v-if="detail.successTime">成功时间 ：{{ detail.successTime }}</view>
          <view v-if="detail.closeTime">关闭时间 ：{{ detail.closeTime }}</view>
        </view>
        <view v-if="detail.images && detail.images.length" class="detail--images" mode="aspectFill">
          <view class="detail--images__title">凭证</view>
          <view class="image--list">
            <image v-for="(item, index) in detail.images" :key="index" :src="item" @click="showImage(item)" />
          </view>
        </view>
      </view>
      <view class="bottom-btns">
        <view class="detail--btn" style="background: #fb4363; color: #fff" @click="pushHome">返回首页</view>
      </view>
      <uv-action-sheet
        ref="actionSheetRef"
        title="申请售后"
        :actions="afterActions('user', detail.originalOrder.status, detail.originalOrder.orderDelivery.deliveryType === 'LOGISTICS')"
        @select="onSelect"
      />
    </view>
    <!-- actions="{{after.afterActions('user',detail.status, detail.orderDelivery.deliveryType==='LOGISTICS')}}" -->
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onBeforeUnmount } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import Goods from '@/components/Goods/Goods.vue'
import {
  getAfterDetail as fetchAfterDetail,
  cancel,
  returnOfGoods,
  getApplyNumber,
  getReturnAddress as fetchReturnAddress,
} from '@/api/modules/afterSale'
import { getdeliveryInfo as getdeliveryInfoApi } from '@/api/modules/order'
import { useAfterSale } from '@/composables/helper/useAfterSale'
import { useDate } from '@/composables/helper/useDate'
import { useSettingStore } from '@/store/modules/setting'
import { $Popup } from '@/utils/helper'
import { APP_LAUNCH } from '@/utils/emitNameConfig'

// 初始化响应式变量
const { getCloseType, isRefund, afterActions } = useAfterSale()
const { getTimeDifference } = useDate()
const detail = ref<any>({
  item: {
    orderId: '',
    productId: '',
  },
  originalOrder: {
    status: '',
    orderDelivery: {
      deliveryType: '',
    },
  },
})
const goods = ref<Obj>({})
const options = ref<Obj>({})

// 计算属性
const mergePoint = computed(() => {
  // 如果mergePoint不存在，返回默认值
  return (useSettingStore() as any).mergePoint || false
})

const templateName = computed(() => {
  return detail.value.type + '-' + detail.value.status
})

// 方法定义 - 前置声明不会在声明前调用的函数
const getdeliveryInfo = (res: any) => {
  if (!res || Object.values(res).filter((item) => !item).length) return
  getdeliveryInfoApi({
    tracesId: res.tracesId,
    companyName: res.companyName,
    deliveryCode: res.deliveryCode,
    mobile: res.mobile,
  })
    .then((res) => {
      const stepsData = JSON.parse(res) || []
      detail.value.steps = stepsData
        .map(({ context, time }: { context: string; time: string }) => {
          return {
            text: context,
            desc: time,
          }
        })
        .reverse()
    })
    .catch(() => {
      detail.value.steps = []
    })
}

const getReturnAddress = (data: any) => {
  if (data.orderDelivery.deliveryType !== 'LOGISTICS' || !data.id) return
  fetchReturnAddress(data.id)
    .then((addressData: any) => {
      detail.value.address = addressData
    })
    .catch(() => {
      $Popup.toast('退货地址获取失败')
    })
}

const getApplyNum = () => {
  getApplyNumber(detail.value.item.orderId)
    .then(({ number }) => {
      let applyNum = 0
      const apply = number.filter((item: any) => {
        return item.productSkuId === goods.value.productSkuId
      })
      applyNum = apply.length > 0 ? apply[0].userApplyNumber : 0
      detail.value.applyNum = applyNum
    })
    .catch(() => {
      $Popup.toast('申请次数获取失败')
    })
}

const objectToQueryStr = (data: any) => {
  let str = ''
  Object.keys(data).map((key, i) => {
    return (str += `${!i ? '' : '&'}${key}=${data[key]}`)
  })
  return str
}

const userApply = (name: string, type: string, params: any = {}) => {
  const { item, originalOrder } = detail.value
  const goodsItem = originalOrder.orderItemList.find((g: any) => g.productSkuId === item.productSkuId)
  const formStr = objectToQueryStr({
    title: name,
    type,
    orderId: goodsItem.orderId,
    productSkuId: goodsItem.productSkuId,
    goods: encodeURIComponent(JSON.stringify(item)),
    productQuantity: goodsItem.productQuantity,
    refundAmount: goodsItem.realAmount || item.refundAmount,
    userType: 'MALL',
    ...params,
  })
  uni.navigateTo({
    url: `/subcontract/pages/applyAfterSale/applyAfterSale?${formStr}`,
  })
}

// 获取售后详情
const getAfterDetail = (afsid: any) => {
  fetchAfterDetail(afsid)
    .then((res: any) => {
      // 处理截止日期
      if (res.deadline) {
        res.deadline = res.deadline.replace(/-/g, '/')
      }
      // 处理售后图片
      if (res.images) {
        res.images = res.images.split(',')
      }
      res.isGroupLookMember = options.value.isGroupLookMember
      res.mergePoint = mergePoint.value
      getReturnAddress(res.originalOrder)
      detail.value = res
      goods.value = res.item
      setTimeout(() => {
        const orderDelivery = res.originalOrder.orderDelivery
        getdeliveryInfo({
          tracesId: orderDelivery.deliverySn,
          companyName: orderDelivery.deliveryCompany,
          deliveryCode: orderDelivery.deliveryCode,
          mobile: res.originalOrder.orderDelivery.receiverPhone,
        })
        getApplyNum()
      }, 0)
    })
    .catch(() => {
      $Popup.toast('售后详情请求失败')
    })
}
const actionSheetRef = ref<any>(null)
const toggleSheetVisible = (bool: boolean = true) => {
  if (!bool) {
    actionSheetRef.value.close()
  } else {
    actionSheetRef.value.open()
  }
}

const onSelect = ({ name, id }: { name: string; id: string }) => {
  userApply(name, id)
  toggleSheetVisible(false)
}

const handleCancel = () => {
  $Popup
    .modal({
      title: '提示',
      content: '确认撤销此次售后申请吗？',
    })
    .then(() => {
      cancel(detail.value.id)
        .then(() => {
          $Popup.toast('撤销成功')
          getAfterDetail(options.value.afsid)
        })
        .catch(() => {
          $Popup.toast('撤销失败')
        })
    })
    .catch(() => null)
}

const handleApplyAgain = (action: any) => {
  // try{}
  if (detail.value.applyNum >= 3) return $Popup.toast('已超过最大申请次数')
  if (action && action.length === 1) {
    return userApply('退款', 'REFUND', {
      hasPicked: 1,
    })
  }
  toggleSheetVisible()
}

const goGoods = (event: any) => {
  const { id, img } = event.target.dataset
  uni.navigateTo({
    url: `/subcontract/pages/detail/detail?id=${id}`,
  })
}

const handleUserReturn = () => {
  const isLogistics = !detail.value.pointId
  if (isLogistics) {
    return uni.navigateTo({
      url: `/subcontract/pages/afterSaleReturn/afterSaleReturn?afsId=${detail.value.id}&goods=${JSON.stringify(detail.value.item)}`,
    })
  }
  returnOfGoods({
    afsId: detail.value.id,
    deliverySn: '',
  })
    .then(() => {
      getAfterDetail(options.value.afsid)
      $Popup.toast('退货成功')
    })
    .catch(() => {
      $Popup.toast('退货失败')
    })
}

const pushHome = () => {
  useSettingStore().backHome()
}

const showImage = (item: string) => {
  uni.previewImage({
    current: item,
    urls: detail.value.images,
  })
}

// 生命周期钩子
onLoad((loadOptions: any) => {
  options.value = loadOptions || {}
  const { afsid = 301 } = options.value
  if (useSettingStore().isReady) {
    getAfterDetail(afsid)
  }
  uni.$on(APP_LAUNCH, () => {
    getAfterDetail(afsid)
  })
})

onBeforeUnmount(() => {
  uni.$off(APP_LAUNCH)
})

onShow(() => {
  if (options.value.afsid) {
    getAfterDetail(options.value.afsid)
  }
})
</script>
<style lang="scss" scoped>
page {
  background: #f4f4f4;
}

.after--detail {
  width: 100%;
  height: auto;
  padding-bottom: 100rpx;
}

.detail--goods__container {
  width: 750rpx;
  background: #fafafa;
  margin-left: -25rpx;
  padding: 20rpx 25rpx 5rpx;
}

.detail--top {
  width: 100%;
  height: 210rpx;
  background: #fe4e63;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-sizing: border-box;
  padding: 101rpx;
  font-size: 34rpx;
}

// .detail--top__l {}

.detail--top__r {
  width: 150rpx;
  height: 109rpx;
}

.detail--top__img {
  width: 150rpx;
  height: 109rpx;
}

.detail--delivery {
  width: 100%;
  height: auto;
  border-bottom: 1px solid #f4f4f4;
}

.detail--delivery__l {
  display: flex;
  align-items: center;
  justify-content: flex-start;
}

.detail--delivery__icon {
  width: 44rpx;
  height: 37rpx;
  margin-right: 10rpx;
}

.detail--cell {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 32rpx 30rpx;
  box-sizing: border-box;
  background: #fff;
  color: #585858;
}

.detail--cell__c {
  font-size: 28rpx;
  margin-left: 24rpx;
  margin-right: auto;
}

.detail--cell__icon {
  width: 30rpx;
  height: 40rpx;
}

.detail--cell__text {
  max-width: 647rpx;
}

.detail--goods {
  padding: 25rpx;
  box-sizing: border-box;
  background: #fff;
}

.detail--goods__btn {
  display: flex;
  justify-content: flex-end;
  padding: 5rpx;
  /* border: 1px solid #999; */
}

.goods-class {
  padding-bottom: 0 !important;
}

.detail__amount {
  color: #fe4e63;
}

.detail--concat {
  background: #fff;
  padding: 15rpx 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.detail--concat__icon {
  width: 41rpx;
  height: 34rpx;
  margin-right: 15rpx;
}

.detail--payinfo {
  /* padding: 25rpx; */
  box-sizing: border-box;
  background: #fff;
  font-size: 26rpx;
  color: #585858;
  margin-top: 16rpx;
}

.detail--payinfo view {
  margin-bottom: 10rpx;
}

.detail--btn {
  width: 100%;
  text-align: center;
  padding: 20rpx 0;
  margin-top: 16rpx;
  background: #fff;
  font-size: 30rpx;
}

.top--status {
  font-size: 34rpx;
}

.top--time {
  margin-top: 14rpx;
  font-size: 26rpx;
}

.detail--description {
  width: 100%;
  padding-left: 31rpx;
  box-sizing: border-box;
  display: flex;
  justify-content: flex-end;
  flex-direction: column;
  justify-items: flex-end;
  background: #fff;
  margin-bottom: 20rpx;
}

.description--top {
  font-size: 30rpx;
  color: #000;
  padding: 31rpx 0;
  border-bottom: 1px solid #e7e7e7;
  margin-bottom: 15rpx;
}

.description--center {
  font-size: 24rpx;
  color: #8d8d8d;
  margin-bottom: 20rpx;
}

.description--center view:first {
  margin-bottom: 15rpx;
}

.description--buttom {
  display: flex;
  margin-left: auto;
  margin-right: 20rpx;
  margin-bottom: 20rpx;
}

.detail--goods__title {
  margin: 10rpx 0 24rpx;
  font-size: 34rpx;
}

.detail--images {
  width: 100%;
}

.image--list {
  width: 100%;
  display: flex;
}

.image--list image {
  margin-right: 8rpx;
  width: 158rpx;
  height: 158rpx;
}

.detail--images__title {
  font-size: 24rpx;
  margin-bottom: 15rpx;
  color: #000000;
}

.dot {
  font-size: 16rpx;
  color: #e2e2e2;
  position: relative;
  top: -3rpx;
}

.bottom-btns {
  position: fixed;
  display: flex;
  width: 100%;
  left: 0;
  bottom: 0;
}

.description--addr {
  width: calc(100% + 40rpx);
  position: relative;
  left: -40rpx;
}

.addr {
  width: 528rpx;
}

.receiver-info {
  padding-bottom: 40rpx;
}

.van-icon,
.iconfont {
  margin-right: 20rpx;
  font-size: 36rpx;
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

.description--addr {
  margin-bottom: 10rpx;
}

.border__t {
  border-top: 1px solid #f4f4f4;
  padding-top: 10rpx;
}

.detail--payinfo--description {
  word-break: break-all;
}
</style>
