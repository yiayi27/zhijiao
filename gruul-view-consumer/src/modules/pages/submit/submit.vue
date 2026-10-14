<template>
  <view class="submit_page" style="padding-bottom: 148rpx">
    <view v-if="form.deliverType !== 'LOGISTICS' && deliveryActions.length" class="delivery">
      <view>
        <block>配送方式</block>
      </view>
      <view style="display: flex">
        物流配送
        <uv-icon name="arrow-right" class="arrow" />
      </view>
    </view>
    <view class="address">
      <!-- 用户地址 -->
      <block v-if="miniAccountAddress.length">
        <view class="address__lead" @click.stop="goSelectAddress">
          <view class="address__lead--left">
            <uv-icon name="map" />
            <view style="margin-left: 20rpx">{{ miniAccountAddress[addressIndex].userName }}</view>
            <view style="margin-left: 20rpx">{{ miniAccountAddress[addressIndex].phone }}</view>
          </view>
          <view class="address__lead--right">切换地址 ></view>
        </view>
      </block>
      <block v-else>
        <view class="address__empty" @click.stop="goSelectAddress">
          <view>请填写收货地址</view>
          <view>></view>
        </view>
        <view style="width: 20rpx; height: 20rpx" />
      </block>
      <image src="https://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/%E5%9B%BE%E6%A0%87/static/xian-fd12ebbd3d.png" />
    </view>
    <view class="card">
      <view v-for="(item, index) in itemVoList" :key="index" class="card__good">
        <image :src="item.productSkuPic || item.productPic" />
        <view class="card__good--detail">
          <view class="goods--name">{{ item.productName }}</view>
          <view class="card__good--detail--sec">{{ item.specs }}</view>
          <view class="card__good--detail--sec" style="margin-top: 40rpx">
            <view>数量 X {{ item.productQuantity }}</view>
            <view class="price">￥{{ item.productPrice }}</view>
          </view>
        </view>
      </view>
    </view>
    <view class="priceCard">
      <view class="priceCard__price">
        <view class="item--label">商品总价</view>
        <view>￥{{ totalPrice }}</view>
      </view>
      <view v-if="couponItemShow" class="priceCard__coupon">
        <view class="line--middle"> 优惠券 </view>
        <view class="priceCard__coupon--coupNum" @click="toggleCoupon">
          <block v-if="offerOptions.coupon">-￥{{ offerOptions.coupon }}></block>
          <block v-else>
            <block v-if="getCanUseCoupon(memberCoupons).length">
              <view>{{ getCanUseCoupon(memberCoupons).length }}张可用</view>
              <view class="arrow">></view>
            </block>
            <block v-else>
              <view style="color: #ccc">无可用优惠券</view>
            </block>
          </block>
        </view>
      </view>
      <view v-if="offerOptions.fullScales" class="priceCard__coupon">
        <view class="line--middle"> 满减 </view>
        <view class="priceCard__coupon--coupNum">-￥{{ offerOptions.fullScales }}</view>
      </view>
      <view v-if="canUseIntegralPrice && offerOptions.integralPrice" class="priceCard__code">
        <view class="line--middle">
          积分可抵扣
          <view class="priceCard__code--price">￥{{ offerOptions.integralPrice }}</view>
        </view>
        <uv-switch v-model="offerOptions.usetIntegral" size="20" activeColor="#FC425A" @change="onIntegralUseChnage" />
      </view>
      <view v-if="isSelfPick(form.deliverType)" class="priceCard__freight">
        <block v-if="!offerOptions.freeShipping">
          <view>运费</view>
          <view>￥{{ offerOptions.freightAmount }}</view>
        </block>
        <block v-else>
          <view>运费</view>
          <view>会员免运费</view>
        </block>
      </view>
      <view class="priceCard__all">
        <view>合计：</view>
        <view class="priceCard__all--price">￥{{ realPrice }}</view>
      </view>
    </view>
    <!-- 会员 -->
    <block v-if="openMember || !notIsVip">
      <view v-if="offerOptions.giftIntegration || offerOptions.totalRebate" class="vip">
        <view class="vip__top">
          <view class="ifMember">
            <view style="margin-left: 40rpx" />
            <view class="vip__top--num">
              <view>购买可获得{{ offerOptions.giftIntegration }}积分</view>
              <view v-if="useRebate && offerOptions.totalRebate"> ，返利￥{{ offerOptions.totalRebate }} </view>
            </view>
          </view>
        </view>
      </view>
      <!-- 非会员 -->
      <view v-if="notIsVip" class="vip">
        <view class="vip__top">
          <view class="vip__top--sign">会员价</view>
          <view class="ifMember">
            <view style="margin-left: 40rpx">
              会员本单立省
              <span class="vip__top--num">{{ offerOptions.reliefAmount }}元</span>
            </view>
            <view class="vip__top--num" @click="becomeVip">立即购买</view>
          </view>
        </view>
      </view>
      <!-- 会员 -->
      <view v-else class="vip">
        <view class="vip__top">
          <view class="vip__top--sign">会员价</view>
          <view class="ifMember">
            <view style="margin-left: 40rpx">
              会员本单立省
              <span class="vip__top--num">{{ offerOptions.reliefAmount }}元</span>
            </view>
            <view class="vip__top--num">-￥{{ offerOptions.reliefAmount }}</view>
          </view>
        </view>
      </view>
    </block>
    <view v-if="forms.length" class="form custom">
      <CustomForm ref="CustomFormRef" v-model:forms="forms" />
    </view>
    <view class="payWay">
      <uv-radio-group v-model="form.payType">
        <uv-cell-group>
          <uv-cell
            v-if="userbalanceUseCondition"
            clickable
            name="BALANCE"
            :data-disabled="offerOptions.supplyBonus < realPrice"
            @click="onPayTypeClick"
          >
            <template #title>
              <view class="payWay__title">
                <view>余额支付</view>
                <view style="margin-left: 10rpx">￥{{ offerOptions.supplyBonus }}</view>
              </view>
            </template>
            <template #right-icon>
              <uv-radio activeColor="#FC425A" name="BALANCE" :disabled="offerOptions.supplyBonus < realPrice" @change="onPayTypeClick" />
            </template>
            />
          </uv-cell>
          <uv-cell clickable name="WECHAT" @click="onPayTypeClick">
            <template #title>
              <view class="payWay__title">
                <view>微信支付</view>
              </view>
            </template>
            <template #right-icon>
              <uv-radio activeColor="#FC425A" name="WECHAT" @change="onPayTypeClick" />
            </template>
          </uv-cell>
        </uv-cell-group>
      </uv-radio-group>
    </view>
    <view class="submit">
      <view class="submit__left">
        <view>合计：</view>
        <view class="submit__left--all">
          <view>￥</view>
          <view style="font-size: 40rpx">{{ realPrice }}</view>
        </view>
      </view>
      <uv-button type="primary" @click="handleSubmit">
        <text>提交订单</text>
      </uv-button>
    </view>
    <CouponSelect v-model:visible="couponShow" :coupons="memberCoupons" state="use" @onClose="toggleCoupon" @onSelect="onCouponSelect" />
    <view v-if="hasSubmited" class="submit__mask" />
    <Auth />
  </view>
</template>

<script setup lang="ts">
import { ref, computed, nextTick, onBeforeUnmount } from 'vue'
import {
  getConfirmOrder,
  createOrder,
  payment as paymentApi,
  getOrderStatus as getOrderStatusApi,
  getFreightAmount as getFreightAmountApi,
} from '@/api'
import { sum } from 'lodash-es'
import CustomForm from './CustomForm/CustomForm.vue'
import CouponSelect from '@/components/CouponSelect/CouponSelect.vue'
import type { IAdress, ICoupon, IProcuct, IFull, IRebateSet, SubmitOptions } from './interface'
import { useHelper } from './composables/useHelper'
import { useSettingStore } from '@/store/modules/setting'
import { useMessageStore } from '@/store/modules/message'
import { $Popup } from '@/utils/helper'
import { onHide, onLoad, onShareAppMessage, onShow, onUnload } from '@dcloudio/uni-app'
import storage from '@/utils/storage'
import { APP_LAUNCH, CHOOSE_ADDRESS, LOGIN_SUCCESS } from '@/utils/emitNameConfig'
import { useUserStore } from '@/store/modules/user'
import { requestPayment } from '@/utils/requestPayment'

// 状态定义
const { getCanUseCoupon, isSelfPick } = useHelper()
const couponShow = ref(false)
const couponItemShow = ref(true)
const valueOfIntegral = ref(1)
const deliveryActions = ref([
  {
    name: '物流配送',
    value: 'LOGISTICS',
  },
])
const forms = ref([])
const CustomFormRef = ref()
let payType = 'WECHAT'
// #ifdef MP-WEIXIN
payType = 'WECHAT'
// #endif
// #ifndef MP-WEIXIN
payType = 'BALANCE'
// #endif
const form = ref({
  couponId: 0 as Long,
  customForm: '',
  deliverType: 'LOGISTICS',
  formId: '',
  fullScaleId: 0,
  groupLeaderId: '',
  integral: '0.00',
  itemDtoList: [] as Obj[],
  miniAccountAddressId: '',
  userNote: '',
  orderType: 'MALL',
  payType,
  sourceType: 'MINI_PROGRAM',
  useMemberPrice: false,
})

const offerOptions = ref({
  usetIntegral: false,
  fullScales: 0,
  freightAmount: 0,
  coupon: 0,
  integralPrice: 0,
  giftIntegration: 0,
  reliefAmount: 0,
  freeShipping: false,
  totalRebate: 0,
  supplyBonus: 0,
})

const itemVoList = ref<IProcuct[]>([])
const memberCoupons = ref<ICoupon[]>([])
const miniAccountAddress = ref<IAdress[]>([])
const fullScales = ref([])
const addressIndex = ref(0)
const realPrice = ref(0)
const totalPrice = ref(0)
const integral = ref(0)
const discountInfoDto = ref({
  ceiling: 0,
  ratio: 0,
})

const isFirst = ref(true)
const address = ref<Obj>({})
const haSubscribed = ref(false)
const hasSubmited = ref(false)
const prevRoute = ref<string | null | undefined>(null)
const notIsVip = ref(true)
const useRebate = ref(true)
const userbalanceUseCondition = ref(false)
const canUseIntegralPrice = ref(true)
const supplyBonus = ref(0)
const dataFrom = ref('miniApp')
const timer = ref<NodeJS.Timeout | null>(null)
const options = ref<SubmitOptions>({
  id: '',
  solitaireActivityId: '',
  type: '',
  items: '',
})

// 计算属性
const openMember = computed(() => {
  return useSettingStore().shopSetting.openMember
})

// 生命周期钩子
onLoad(async (optionsObj) => {
  uni.$on(LOGIN_SUCCESS, () => {
    getOrderData(options.value)
  })
  options.value = optionsObj as SubmitOptions
  if (options.value.type && options.value.type === 'h5') {
    dataFrom.value = options.value.type
    await useSettingStore().getSetting()
    getOrderData(options.value)
  } else {
    getOrderData(options.value)
    uni.$on(APP_LAUNCH, () => {
      getOrderData(options.value)
    })
  }
})

onBeforeUnmount(() => {
  uni.$off(APP_LAUNCH)
  uni.$off(CHOOSE_ADDRESS)
  uni.$off(LOGIN_SUCCESS)
})

onShow(() => {
  if (!isFirst.value) {
    getOrderData(options.value)
  }
})
onHide(() => {
  isFirst.value = false
})
onUnload(() => {
  if (timer.value) {
    clearInterval(timer.value as NodeJS.Timeout)
    timer.value = null
  }
})

// 分享功能
onShareAppMessage(() => {
  const shopName = useSettingStore().shopSetting.curShopInfoDto.shopName
  return {
    title: shopName,
    path: '/pages/index/index?page=home',
    imageUrl: '',
  }
})

const becomeVip = () => {
  uni.navigateTo({
    url: '/subcontract/pages/member/member',
  })
}

const getPrevRoute = () => {
  try {
    const pages = getCurrentPages()
    return pages[pages.length - 2].route
  } catch {
    return null
  }
}

const getShipperSetting = () => {
  const _form = form.value
  return new Promise((resolve) => {
    _form.deliverType = 'LOGISTICS'
    deliveryActions.value = [
      {
        name: '物流配送',
        value: 'LOGISTICS',
      },
    ]
    form.value = _form
    return resolve('1')
  })
}

const getTotalGiftIntegration = () => {
  offerOptions.value.giftIntegration = sum(itemVoList.value.map((item) => item.giftIntegration * item.productQuantity * valueOfIntegral.value))
}

const getRealPrice = () => {
  const { fullScales, coupon, usetIntegral, integralPrice, reliefAmount } = offerOptions.value
  return (
    (totalPrice.value * 100000000 -
      fullScales * 100000000 -
      coupon * 100000000 -
      (usetIntegral ? integralPrice : 0) * 100000000 -
      (notIsVip.value ? 0 : reliefAmount) * 100000000) /
    100000000
  )
}

const setRealPrice = () => {
  const { freeShipping, freightAmount } = offerOptions.value
  const realPriceValue = +getRealPrice().toFixed(2)
  const freight = !notIsVip.value && freeShipping ? 0 : freightAmount
  realPrice.value = realPriceValue <= 0 ? (freight > 0 ? freight : 0.01) : (realPriceValue * 100000000 + freight * 100000000) / 100000000
  nextTick(() => {
    // 如果少于余额，使用微信支付
    if (realPrice.value > offerOptions.value.supplyBonus) {
      form.value.payType = 'WECHAT'
    }
  })
}

const setTotalPrice = () => {
  const totalPriceValue = sum(
    itemVoList.value.map((item) => {
      return item.productPrice * 1000000 * item.productQuantity
    }),
  )
  totalPrice.value = Number((totalPriceValue / 1000000).toFixed(2))
  nextTick(() => {
    setRealPrice()
  })
}

/**
 * 刷新页面
 */
const refreshPage = async () => {
  return new Promise((resolve) => {
    uni.redirectTo({
      url: `/${getCurrentPages()[getCurrentPages().length - 1].route || '/modules/pages/submit/submit'}?items=${options.value.items}`,
      complete: () => {
        resolve(true)
      },
    })
  })
}

const getOrderStatus = (orderId: string, cb: (...args: any[]) => void) => {
  if (timer.value) {
    clearInterval(timer.value as NodeJS.Timeout)
    timer.value = null
  }
  timer.value = setInterval(() => {
    getOrderStatusApi({
      orderId,
      items: JSON.parse(decodeURIComponent(options.value.items || getOrSetItemStorage(true))),
    })
      .then((res) => {
        // -1->秒杀失败;0->排队中;order->成功;
        if (res === -1) {
          clearInterval(timer.value as NodeJS.Timeout)
          timer.value = null
          return $Popup.toast('下单失败，请重试')
        }
        if (res === 0) {
          return
        }
        if (res) {
          clearInterval(timer.value as NodeJS.Timeout)
          timer.value = null
          cb(res)
        }
      })
      .catch((err) => {
        clearInterval(timer.value as NodeJS.Timeout)
        timer.value = null
        $Popup
          .alert({
            title: '温馨提示',
            content: err || '订单状态获取失败',
          })
          .then(() => {
            refreshPage()
          })
      })
  }, 1500)
}

const getFreightAmount = (query: { addressId: string }) => {
  // 配送类型：1->物流模式（需要总金额、收货地址ID）
  const type = 1
  // 物流
  const addressData = miniAccountAddress.value[addressIndex.value]

  // 如果没有地址数据 不发起请求
  if (!addressData) {
    return
  }

  // 如果为物流模式 需要将addressId改为postCode
  const addressId = addressData.postCode
  Object.assign(query, {
    amount: totalPrice.value,
    addressId,
    region: addressId,
    items: form.value.itemDtoList,
  })
  getFreightAmountApi({
    type,
    amount: totalPrice.value,
    ...query,
  })
    .then(({ cost }) => {
      if (cost === null || cost === -1) {
        return $Popup.toast('不在配送范围内')
      }
      offerOptions.value.freightAmount = cost
      nextTick(() => {
        setRealPrice()
      })
    })
    .catch((err) => {
      console.log(err)
      $Popup.toast(err || '运费获取失败')
    })
}

const calulateRebate = (memberInfo: any, itemVos: any, rebateSet: any) => {
  let rebateRatio = 0
  if ((!memberInfo && rebateSet.rebateCondition?.includes('0')) || memberInfo) {
    // 默认返利比例
    rebateRatio = rebateSet.rebateRatio / 100
  }

  // 返利总价
  return sum(
    itemVos.map((item: any) => {
      const data = (item.skuRebates || []).find((sItem: any) => +sItem.skuId === +item.productSkuId) as any
      let price = data && data.rebatePrice * item.productQuantity

      // 折扣
      if (price && item.rebateType === 2) {
        price = +((item.productPrice * item.productQuantity * price) / 100).toFixed(2)
        return price
      }
      if (!price && price !== 0 && item.rebateType === 3) {
        price = item.productPrice * item.productQuantity * rebateRatio
        return price
      }

      // 不返利
      if (item.rebateType === 0) {
        price = 0
      }
      return price
    }),
  )
}

const calculateMemberDiscount = (memberInfo: any, itemVos: any, topMemberCardInfo: any) => {
  const rebateSet = useSettingStore().shopSetting.rebateSet as unknown as IRebateSet
  if (!memberInfo) {
    calculateMemberData(topMemberCardInfo, itemVos, rebateSet)
  } else {
    calculateMemberData(memberInfo, itemVos, rebateSet, memberInfo.state === 1 ? false : true)
  }
}

const calculateMemberData = (memberInfo: any, itemVos: any, rebateSet: any, isNotVip = true) => {
  // ptype 1 商品折扣 2 包邮
  // 默认折扣
  const { defValue = 0 } = memberInfo?.memberCardPrivileges?.find((item: any) => item.ptype === '1') || {}
  // 获取商品优惠后的总价
  const memberPriceList = itemVos.map((item: any) => {
    let price = 0
    const priceInfo = (item.memberPrices || []).find((rule: any) => {
      return +rule.memberLevelId === (isNotVip ? +item.memberPrices[item.memberPrices.length - 1].memberLevelId : +memberInfo.rankCode)
    })
    price = priceInfo && priceInfo.memberPrice

    // 计算直接减免
    if (price && item.memberType === 1) {
      price = +(item.productQuantity * price).toFixed(2)
    }

    // 折扣
    if (price && item.memberType === 2) {
      price = +((item.productPrice * item.productQuantity * price) / 100).toFixed(2)
    }

    // 统一设置
    if (price && item.memberType === 3) {
      price = (item.productPrice * item.productQuantity * defValue) / 100
    }

    // 不使用会员价
    if (item.memberType === 0) {
      return item.productPrice * item.productQuantity
    }
    if (!price && price !== 0) {
      price = (item.productPrice * item.productQuantity * defValue) / 100
    }
    const fullData = getFullScalesPrice(fullScales.value, itemVoList.value)
    if (!fullData.canUseMemberPrice && fullData.price > 0) {
      return item.productPrice * item.productQuantity
    }
    return price
  })
  const totalMemberPrice = sum(memberPriceList)

  // 实际减免价格
  const relPrice = +(totalPrice.value * 1000000 - totalMemberPrice * 1000000) / 1000000
  // 获取减免金额
  const reliefAmount = totalMemberPrice ? relPrice.toFixed(2) : 0
  // 是否免运费
  const freeShipping = !isNotVip && !!(memberInfo.memberCardPrivileges || []).find((item: any) => item.ptype === '2')
  const useRebateTemp = (isNotVip && rebateSet?.rebateCondition?.includes('0')) || (!isNotVip && rebateSet?.rebateCondition?.includes('1'))
  const userbalanceUseConditionTemp =
    (isNotVip && rebateSet?.balanceUseCondition?.includes('0')) || (!isNotVip && rebateSet?.balanceUseCondition?.includes('1'))

  // 返利
  const totalRebate = useRebateTemp ? calulateRebate(memberInfo, itemVos, rebateSet) : 0
  offerOptions.value.supplyBonus = memberInfo.supplyBonus ? memberInfo.supplyBonus : supplyBonus.value
  offerOptions.value.reliefAmount = +reliefAmount
  offerOptions.value.totalRebate = +totalRebate.toFixed(2)
  offerOptions.value.freeShipping = isNotVip ? false : freeShipping
  notIsVip.value = isNotVip
  useRebate.value = useRebateTemp
  userbalanceUseCondition.value = userbalanceUseConditionTemp
  nextTick(() => {
    setRealPrice()
  })
}

const getOrderData = async ({ items }: { items?: string }) => {
  if (!items) {
    items = getOrSetItemStorage(true)
  }
  try {
    const res = await getConfirmOrder({
      items: JSON.parse(decodeURIComponent(items || '')),
      type: 'MALL',
    })
    // 设置一系列数据
    setResToData(res, items || '')
  } catch (err) {
    console.log(err)
    $Popup.toast(err || '结算页面信息获取失败')
  }
}

const setResToData = (res: any, items: string) => {
  const {
    miniAccountAddress: miniAccountAddressTemp,
    itemVoList: itemVoListTemp,
    memberCoupons: memberCouponsTemp,
    fullScales: fullScalesTemp,
    integral: integralTemp,
    discountInfoDto: discountInfoDtoTemp,
    memberInfo,
    topMemberCardInfo,
    supplyBonus: supplyBonusTemp,
  } = res
  let integralValue = 1
  if (memberInfo?.memberCardPrivileges) {
    const itemMemberCardPrivilege = memberInfo.memberCardPrivileges.filter((item: any) => item.ptype === '3')[0]
    integralValue = itemMemberCardPrivilege ? itemMemberCardPrivilege.defValue : 1
  }

  // 设置items缓存做跳转回来使用
  getOrSetItemStorage(false, items)
  miniAccountAddress.value = miniAccountAddressTemp
  itemVoList.value = itemVoListTemp
  fullScales.value = fullScalesTemp
  integral.value = integralTemp
  discountInfoDto.value = discountInfoDtoTemp
  valueOfIntegral.value = integralValue
  supplyBonus.value = supplyBonusTemp
  form.value.itemDtoList = JSON.parse(decodeURIComponent(items))
  nextTick(async () => {
    try {
      forms.value = getCustomFrom()
      memberCoupons.value = judgeCouponCanuse((memberCouponsTemp as any) || [], (itemVoListTemp as IProcuct[]) || [])
      prevRoute.value = getPrevRoute()
      await getShipperSetting()
      setAddressIndexAndId()
      setTotalPrice()

      // 计算满减金额
      const fullData = getFullScalesPrice(fullScales.value, itemVoList.value)
      if (fullData.fullScaleId && !fullData.sharedUseCoupon) {
        couponItemShow.value = false
      }

      // 获取积分是否可用
      getIntegralStatus(fullData as any)

      // 设置可获取积分
      getTotalGiftIntegration()
      calculateMemberDiscount(memberInfo, itemVoList.value as any, topMemberCardInfo)
    } catch (err) {
      console.log(err)
      $Popup.toast(err)
    }
  })
}

const getCustomFrom = () => {
  try {
    return JSON.parse(useSettingStore().order.customFrom)
  } catch {
    return []
  }
}

const judgeCouponCanuse = (coupons: ICoupon[], goodsList: Array<IProcuct>) => {
  // 是否为满减优惠券
  const isFullReduction = (type: number) => {
    return type === 2 || type === 3
  }

  // 判断门槛优惠券是否满足使用条件canUse
  const getFullReductionMeet = ({
    couponType,
    conditionAmount,
    products,
    productType,
  }: {
    couponType: number
    conditionAmount: number
    products: IProcuct[]
    productType: number
  }) => {
    if (productType === 0) {
      products = goodsList
    }
    const ids = products.map((item) => item.productId)
    products = goodsList.filter((item) => {
      return ids.includes(item.productId)
      // && item.productPrice * item.productQuantity >= conditionAmount
    })

    // 商品总价
    const total = sum(products.map((item) => item.productPrice * item.productQuantity))

    // 判断是否满足满减条件
    if (isFullReduction(couponType) && conditionAmount > total) {
      return {
        status: false,
        products,
      }
    }
    return {
      status: !!products.length && true,
      products,
    }
  }

  // 全部商品参与的优惠券的可用状态
  const getAllStatus = ({ conditionAmount, couponType, productType }: { conditionAmount: number; couponType: number; productType: number }) => {
    const status = getFullReductionMeet({
      conditionAmount,
      products: goodsList,
      couponType,
      productType,
    })
    return status
  }

  // 指定商品参与的优惠券的可用状态
  const getSpecifyStatus = ({
    couponProducts,
    conditionAmount,
    couponType,
    productType,
  }: {
    couponProducts: any
    conditionAmount: number
    couponType: number
    productType: number
  }) => {
    // 如果为满减优惠券 并且总价少于满足条件 设置不可用
    const status = getFullReductionMeet({
      conditionAmount,
      products: couponProducts,
      couponType,
      productType,
    })

    // 优惠券可用的商品id
    return status
  }

  // 排除商品不参与的优惠券的可用状态
  const getExcludeStatus = ({
    couponProducts,
    conditionAmount,
    couponType,
    productType,
  }: {
    couponProducts: any
    conditionAmount: number
    couponType: number
    productType: number
  }) => {
    const ids = couponProducts.map((item: any) => item.productId)
    const products = goodsList
      .filter((item: any) => !ids.includes(item.productId))
      .map((item) => ({
        productId: item.productId,
      })) as any
    const status = getFullReductionMeet({
      conditionAmount,
      products,
      couponType,
      productType,
    })
    return status
  }
  return coupons.map((item) => {
    const { status, products } = [getAllStatus, getSpecifyStatus, getExcludeStatus][item.productType](item)
    item.canUse = status
    item.couponProducts = products
    return item
  })
}

const calculateCoupon = (coupon: ICoupon) => {
  if (!coupon?.id) {
    couponShow.value = true
    offerOptions.value.coupon = 0
    form.value.couponId = 0
    return
  }
  const ids = (
    coupon.couponProducts as {
      productId: string
    }[]
  ).map((item) => item.productId)
  console.log('ids', ids)
  const itemData = itemVoList.value
    .filter((item) => ids.includes(item.productId))
    .sort((a, b) => {
      return b.productPrice * b.productQuantity - a.productPrice * a.productQuantity
    })
  console.log('1111111111', itemData, itemVoList.value)
  if (!itemData) {
    return $Popup.toast('不满足使用条件')
  }

  let { productPrice, productQuantity } = itemData[0]
  // 折扣
  if (coupon.couponType === 0 || coupon.couponType === 2) {
    productPrice = +((productPrice * productQuantity * 1000000 * ((10 - coupon.discount) / 10)) / 1000000).toFixed(2)
  }

  // 金额券
  if (coupon.couponType === 1 || coupon.couponType === 3) {
    productPrice = productPrice * productQuantity <= coupon.discountAmount ? productPrice * productQuantity : coupon.discountAmount
  }
  form.value.couponId = coupon.id
  offerOptions.value.coupon = productPrice >= 0.01 ? +productPrice.toFixed(2) : 0
}

const getIntegralStatus = (
  { useIntegral }: Partial<IFull> = {
    useIntegral: true,
  },
) => {
  const canDeduction = (totalPrice.value * 1000000 * (discountInfoDto.value.ceiling / 100)) / 1000000
  const usedIntegral = (canDeduction * 1000000) / (discountInfoDto.value.ratio / 100) / 1000000
  const status = usedIntegral <= integral.value
  canUseIntegralPrice.value = status
  offerOptions.value.integralPrice = useIntegral ? +(Math.ceil(canDeduction * 100) / 100).toFixed(2) : 0
  return {
    status,
    integral: usedIntegral,
    canDeduction,
  }
}

const getFullScalesPrice = (fullScales: any, itemVoList: IProcuct[]) => {
  // applyType 适用优惠：0-会员价 1-优惠劵 满减可以共用条件
  // deductionType 可用抵扣： 0-积分 为0时可共用
  // const usedCoupon = true;
  // const usedMemberPrice = true;
  const [
    maxItem = {
      fullScaleId: null,
      price: 0,
      canUseMemberPrice: false,
      useIntegral: true,
      sharedUseCoupon: true,
    },
  ] = (fullScales || [])
    .map(
      ({
        fullScaleProducts,
        deductionType,
        applyType,
        productType,
        fullScaleRules,
      }: {
        fullScaleProducts: any
        deductionType: number
        applyType: string
        productType: number
        fullScaleRules: any
      }) => {
        // 适用商品： 0-全部商品参与 1-指定商品参与 2-指定商品不参与
        // 是否能与积分共用
        const sharedUseIntegral = !!deductionType
        // 是否能与会员价共用
        const sharedUseMemberPrice = !!applyType?.includes('0')
        // 是否能与优惠券共用
        const sharedUseCoupon = !!applyType?.includes('1')

        // 满足条件产品Id组
        const productIds = productType === 0 ? itemVoList.map((p: IProcuct) => p.productId) : fullScaleProducts.map((p: any) => p.productId)

        // 从所有商品中获取Id组内的商品 productType为2时 为指定商品不参数 做排除
        const product = itemVoList
          .filter((p) => (productType !== 2 ? productIds.includes(p.productId) : !productIds.includes(p.productId)))
          .sort((a, b) => {
            return b.productPrice - a.productPrice
          })

        // 商品总价
        const totalPrice = sum(product.map((p) => p.productPrice * p.productQuantity))

        // 获取优惠价格的数据
        const priceGroup = fullScaleRules
          .sort((a: any, b: any) => {
            let aPrice: number
            let bPrice: number
            if (a.ruleType === 0) {
              aPrice = a.discountAmount
            } else {
              aPrice = +((totalPrice * 1000000 * ((10 - a.discount) / 10)) / 1000000).toFixed(2)
            }
            if (b.ruleType === 0) {
              bPrice = b.discountAmount
            } else {
              bPrice = +((totalPrice * 1000000 * ((10 - b.discount) / 10)) / 1000000).toFixed(2)
            }
            return bPrice - aPrice
          })
          .filter((rule: any) => {
            return rule.conditionAmount <= totalPrice
          })
          .map(
            ({
              ruleType,
              discountAmount,
              discount,
              fullScaleId,
            }: {
              ruleType: number
              discountAmount: number
              discount: number
              fullScaleId: number
            }) => {
              // 满减劵
              if (ruleType === 0) {
                return {
                  fullScaleId,
                  price: discountAmount,
                  canUseMemberPrice: sharedUseMemberPrice,
                  useIntegral: sharedUseIntegral,
                  sharedUseCoupon,
                }
              }

              // 满折劵
              if (ruleType === 1) {
                return {
                  fullScaleId,
                  price: +((totalPrice * 1000000 * ((10 - discount) / 10)) / 1000000).toFixed(2),
                  canUseMemberPrice: sharedUseMemberPrice,
                  useIntegral: sharedUseIntegral,
                  sharedUseCoupon,
                }
              }
              return {
                fullScaleId,
                price: 0,
                canUseMemberPrice: sharedUseMemberPrice,
                useIntegral: sharedUseIntegral,
                sharedUseCoupon,
              }
            },
          )
          .filter((item: any) => !!item)
        const fullData = priceGroup[0] || {
          fullScaleId: null,
          price: 0,
        }

        // 返回优惠金额
        return {
          ...fullData,
          canUseMemberPrice: sharedUseMemberPrice,
          useIntegral: fullData.fullScaleId ? sharedUseIntegral : true,
          sharedUseCoupon,
        }
      },
    )
    .sort((a: any, b: any) => {
      return b.price - a.price
    })
  form.value.fullScaleId = maxItem.fullScaleId
  offerOptions.value.fullScales = maxItem.price
  return maxItem
}

const getOrSetItemStorage = (isGet = false, items?: string) => {
  if (isGet) {
    return storage.get('order-items') || items
  }
  storage.set('order-items', items || options.value.items)
}

const getOrsetStorage = (isGet = false) => {
  try {
    const keys = ['form', 'itemVoList', 'memberCoupons', 'miniAccountAddress', 'addressIndex']
    const data = {}
    const storageData = storage.get('submitData')
    const setData = (store: any) => {
      keys.forEach((key) => {
        // @ts-ignore-next-line
        data[key] = store[key]
      })
    }
    if (isGet) {
      if (storageData) {
        storage.get('submitData')
        setData(storageData)
        return data
      } else {
        return {}
      }
    }
    //   FIXME 订单信息存储
    //   setData(this.data);
    //   return storage.set("submitData", JSON.stringify(data));
  } catch (err) {
    console.log(err)
    return false
  }
}

const handleVerification = () => {
  // 自定义表单验证
  if (!CustomFormRef.value || !CustomFormRef.value?.verificationForm()) {
    return false
  }
  const addressData = miniAccountAddress.value[addressIndex.value]
  if (!addressData || !addressData.id) {
    $Popup
      .alert({
        title: '温馨提示',
        content: '请添加地址',
      })
      .then(() => {
        goSelectAddress()
      })
    return false
  }
  return true
}

const handleSubmit = async () => {
  try {
    if (!handleVerification() || hasSubmited.value) {
      return
    }
    hasSubmited.value = true
    if (!haSubscribed.value) {
      /** 模板订阅 */
      await useMessageStore().subscribe([useMessageStore().type.sendMsg, useMessageStore().type.memberCartOpen])
      haSubscribed.value = true
    }

    /** 如果是从H5进入小程序支付订单 删除activityId 添加solitaireActivityId */
    if (dataFrom.value === 'h5') {
      form.value.itemDtoList.forEach((inItem: any) => {
        inItem.solitaireActivityId = inItem.activityId
        delete inItem.activityId
      })
    }
    const { id } = miniAccountAddress.value[addressIndex.value]
    // 创建订单
    uni.showLoading({
      title: '创建订单中...',
    })
    const orderId = await createOrder(
      Object.assign(form.value, {
        groupLeaderId: form.value.groupLeaderId || '',
        groupLeaderNickname: '',
        pointAuthor: '',
        point_phone: '',
        miniAccountAddressId: id,
        customForm: CustomFormRef.value ? JSON.stringify(CustomFormRef.value.localForms) : '',
        deliveryTemplateId: (await useMessageStore().getTemplateId(useMessageStore().type.sendMsg)) || null,
        memberTemplateId: (await useMessageStore().getTemplateId(useMessageStore().type.memberCartOpen)) || null,
        useMemberPrice: !!offerOptions.value.reliefAmount,
        solitaireActivityId: form.value.itemDtoList[0].solitaireActivityId ? form.value.itemDtoList[0].solitaireActivityId : null,
      }),
    )
    uni.hideLoading()
    uni.showLoading({
      title: '提交订单中...',
    })
    // 轮询订单状态
    getOrderStatus(orderId, async () => {
      handleSubmitType(orderId)
        .then(() => {
          uni.hideLoading()
        })
        .catch((err) => {
          console.log(err)
          $Popup.toast(err || '支付失败')
          hasSubmited.value = false
          uni.hideLoading()
        })
    })
  } catch (err: any) {
    hasSubmited.value = false

    $Popup
      .alert({
        title: '温馨提示',
        content: err || '提交订单失败',
      })
      .then(() => {
        // 返回上两页
        uni.navigateBack()
      })

    // 关闭提交订单中...
    uni.hideLoading()
  }
}

const handleSubmitType = (orderId: string) => {
  uni.showLoading({
    title: '支付中...',
  })
  switch (form.value.payType) {
    case 'BALANCE':
      return balancePayment(orderId)
    case 'WECHAT':
      return payment(orderId)
    case 'FRIEND':
      return Promise.resolve($Popup.toast('FRIEND'))
    default:
      return Promise.resolve()
  }
}

const balancePayment = (orderId: string) => {
  return $Popup
    .modal({
      title: '温馨提示',
      content: '确认使用余额支付吗？',
    })
    .then(() => {
      return paymentApi(orderId, true)
        .then(() => {
          return orderSuccess(orderId)
        })
        .catch((err) => {
          console.log(err)
          $Popup.toast(err || '支付失败')
        })
    })
    .catch(() => {
      paymentApi(orderId, false)
        .then(() => {
          return orderSuccess(
            orderId,
            () =>
              uni.redirectTo({
                url: '/subcontract/pages/order/order',
              }),
            false,
          )
        })
        .catch((err) => {
          console.log(err)
          $Popup.toast(err)
        })
    })
}

const payment = (orderId: string) => {
  if (timer.value) {
    clearInterval(timer.value as NodeJS.Timeout)
    timer.value = null
  }
  // return mockPay(orderId).then(res => {
  //   uni.redirectTo({ url: "/pages/order/order" });
  // });
  return paymentApi(orderId)
    .then((res) => {
      console.log('支付回调', res)
      wecharPay(res.wxResult, orderId)
    })
    .catch((err) => {
      console.log(err)
      // 刷新页面
      refreshPage().then(() => {
        $Popup.toast(err || '支付失败')
      })
    })
}

const orderSuccess = (orderId: string, cb?: () => void, succ = true) => {
  if (succ) {
    $Popup.toast('支付成功')
    // 支付成功后，重新获取购物车数量
    useUserStore().getShopCarNum()
  }
  if (cb) return cb()
  const arr = JSON.parse(decodeURIComponent(options.value.items || getOrSetItemStorage(true)))
  const number = arr.reduce((prev: number, item: any) => {
    return prev + item.number
  }, 0)
  return uni.redirectTo({
    url: `/subcontract/pages/orderSuccess/orderSuccess?price=${realPrice.value}&orderId=${orderId}&totalRebate=${offerOptions.value.totalRebate}&number=${number}`,
  })
}

const wecharPay = (res: any, orderId: string) => {
  const isCancel = (msg: string) => msg.includes('cancel')
  // #ifdef H5
  console.log('res', res)
  if (res.resultCode === 'SUCCESS') {
    // 跳转至H5支付页面
    window.location.href = res.mwebUrl
  }
  // #endif
  // #ifdef MP-WEIXIN
  requestPayment(
    res,
    () => {
      orderSuccess(orderId)
    },
    (e) => {
      console.log(e)
      if (timer.value) {
        clearInterval(timer.value as NodeJS.Timeout)
        timer.value = null
      }
      // 如果为取消 跳至订单详情
      if (isCancel(e.errMsg)) {
        return uni.redirectTo({
          url: `/subcontract/pages/order/order?type=0`,
        })
      }
      $Popup.toast('支付失败')
    },
  )
  // #endif
}

const goSelectAddress = () => {
  getOrsetStorage()
  uni.$on(CHOOSE_ADDRESS, (addressOption) => {
    address.value = addressOption
  })
  uni.navigateTo({
    url: `/subcontract/pages/address/address?redirect=${encodeURIComponent(`/modules/pages/submit/submit`)}`,
  })
}

const setAddressIndexAndId = () => {
  const defaultAddressIndex = miniAccountAddress.value.findIndex((item) => {
    return item.isDefault === 1
  })
  const addressIndexValue = miniAccountAddress.value.findIndex((item) => {
    return item.id === address.value.id
  })
  const index = addressIndexValue >= 0 ? addressIndexValue : defaultAddressIndex
  const addressData = miniAccountAddress.value[index]
  const id = addressData ? (form.value.deliverType === 'LOGISTICS' ? addressData.id : addressData.postCode) : ''
  addressIndex.value = index
  form.value.miniAccountAddressId = id
  nextTick(() => {
    // 获取运费
    getFreightAmount({
      addressId: id ? id : '',
    })
  })
}

const toggleCoupon = () => {
  couponShow.value = !couponShow.value
}

const onCouponSelect = (item: ICoupon) => {
  if (item?.id && !item?.canUse) {
    return $Popup.toast('优惠券不可用')
  }
  calculateCoupon(item)
  setRealPrice()
  toggleCoupon()
}

const onPayTypeClick = (e: any) => {
  if (e.name) {
    form.value.payType = e.name
  } else {
    form.value.payType = e
  }
}

const onIntegralUseChnage = (e: boolean) => {
  const { status, integral, canDeduction } = getIntegralStatus()
  if (!status || canDeduction < 0.01) {
    offerOptions.value.usetIntegral = false
    return $Popup.toast('不满足使用条件')
  }
  offerOptions.value.usetIntegral = e
  form.value.integral = e ? integral.toFixed(2) : '0.00'
  nextTick(() => {
    setRealPrice()
  })
}
</script>
<style lang="scss" scoped>
@import '@/assets/styles/submit/submit.scss';
</style>
