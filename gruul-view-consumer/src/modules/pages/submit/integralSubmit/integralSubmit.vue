<template>
  <view class="page">
    <view class="address" @tap="goSelectAddress">
      <view v-if="addressOption.province" class="address__top">
        <view class="address__top--sign" style="font-size: 22rpx">收货地址</view>
        <view>{{ addressOption.province }} {{ addressOption.county }} {{ addressOption.city }}</view>
      </view>
      <view class="address__sec">
        <view style="font-weight: bold">
          <view v-if="addressOption.province">{{ addressOption.detailInfo }}</view>
          <view v-else>请选择收货地址</view>
        </view>
        <uv-icon name="arrow-right" class="arrow" />
      </view>
      <view v-if="addressOption.province" class="address__user">
        <view>{{ addressOption.userName }}</view>
        <view style="margin-left: 20rpx">{{ addressOption.showPhone }}</view>
      </view>
      <image src="https://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/%E5%9B%BE%E6%A0%87/static/xian-fd12ebbd3d.png" />
    </view>
    <view class="card">
      <view class="card__good">
        <image :src="goodDetail.pic" />
        <view class="card__good--detail">
          <view style="font-weight: bold">{{ goodDetail.integralGoodName }}</view>
          <view class="card__good--detail--sec" style="margin-top: 40rpx">
            <view>数量 X 1</view>
            <view class="price">{{ goodDetail.integralPrice }} 积分</view>
          </view>
        </view>
      </view>
    </view>
    <view class="priceCard">
      <view class="priceCard__freight" style="padding-top: 40rpx">
        <view>运费</view>
        <view>￥{{ logistFee }}</view>
      </view>
      <view class="priceCard__all">
        合计：
        <view class="priceCard__all--price">{{ goodDetail.integralPrice }}积分(+￥{{ logistFee }})</view>
      </view>
    </view>
    <view class="form">
      买家留言：
      <uv-textarea class="form-textare" placeholder="对本次交易说明（45字内）" maxlength="45" @input="handleIptChange" />
    </view>
    <view class="payWay">
      <view v-for="(item, index) in payList" :key="index" class="payWay__balance">
        <view style="display: flex">
          <MIcon v-if="item.id === 100" name="icontijiaodingdan-yue" color="#FF9C01" />
          <MIcon v-if="item.id === 101" name="icontijiaodingdan-weixin" color="#6BCC03" />
          <view style="margin-left: 20rpx">{{ item.pay }}</view>
        </view>
        <view style="margin-right: 20rpx">
          <view v-if="item.id === 100">
            <view v-if="!canPayBayAccount">
              <view style="width: 34rpx; height: 34rpx; border-radius: 100rpx; background-color: #f5f7fa; border: 1px solid #e8eaf0" />
            </view>
            <view v-else :data-info="item" @tap="choosePayWay">
              <MIcon v-if="!item.chooseFlag" name="iconweigouxuan" color="#FB485D" />
              <MIcon v-else name="icongouxuan" color="#FB485D" />
            </view>
          </view>
          <view v-if="item.id === 101" :data-info="item" @tap="choosePayWay">
            <MIcon v-if="!item.chooseFlag" name="iconweigouxuan" color="#FB485D" />
            <MIcon v-else name="icongouxuan" color="#FB485D" />
          </view>
        </view>
      </view>
    </view>
    <view class="submit">
      <view class="submit__left" style="height: 90rpx">
        <view>总计:</view>
        <view class="submit__left--all">{{ goodDetail.integralPrice }}积分(+￥{{ logistFee }})</view>
      </view>
      <uv-button type="primary" @click="handleSubmit">提交订单</uv-button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onBeforeUnmount, ref } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import { getPrizeOrder, payOrder, payOrderCallBack, getAllAddress, getLogistFee } from '@/api/modules/integral'
import { placeAround, getadCodBycityCode } from '@/api/modules/shipping'
import { getAddressList } from '@/api/modules/address'
import { userMsgGet } from '@/api/modules/user'
import { useMessageStore } from '@/store/modules/message'
import { $Popup } from '@/utils/helper'
import MIcon from '@/components/MIcon/MIcon.vue'
import storage from '@/utils/storage'
import { CHOOSE_ADDRESS } from '@/utils/emitNameConfig'
import { requestPayment } from '@/utils/requestPayment'

// 定义接口
interface AddressOption {
  id?: Long
  province?: string
  city?: string
  county?: string
  detailInfo?: string
  userName?: string
  phone?: string
  showPhone?: string
  location?: string
  isDefault?: number
  [key: string]: any
}

interface GoodDetail {
  goodName?: string
  goodSec?: string
  num?: number
  code?: number
  payFee?: number
  integralGoodName?: string
  integralPrice?: number
  id?: Long
  pic?: string
  freightTemplateId?: Long
  weight?: number
  [key: string]: any
}

interface PayItem {
  pay: string
  hasType: boolean
  chooseFlag: boolean
  id: number
}

// 状态
const goodDetail = ref<GoodDetail>({
  goodName: 'A级红石榴',
  goodSec: '新鲜可口 美味诱人',
  num: 1,
  code: 6000,
  payFee: 10,
})
const buyersNote = ref('')
const addressOption = ref<AddressOption>({})
const region = ref('')
const logistFee = ref(0)
const inAround = ref(true)
const address = ref<AddressOption>({})
const payList = ref<PayItem[]>([
  {
    pay: '余额支付',
    hasType: true,
    chooseFlag: false,
    id: 100,
  },
  {
    pay: '微信支付',
    hasType: true,
    chooseFlag: true,
    id: 101,
  },
])
const haSubscribed = ref(false)
const payId = ref(101)
const canPayBayAccount = ref(true)
const canClick = ref(false)

// 方法 - 按调用顺序排列
// getCodeFee 函数 - 获取运费
const getCodeFee = () => {
  const param = {
    freightTemplateId: goodDetail.value.freightTemplateId,
    regionCode: region.value,
    weight: goodDetail.value.weight,
  }
  getLogistFee(param)
    .then(async (res: number) => {
      if (res === -1) {
        uni.showToast({
          title: '选择地址不支持发货',
          icon: 'none',
        })
      }
      const userInfo: any = await userMsgGet({
        infoLevel: 2,
      })
      const userMemberInfo = userInfo.infoExtends.memberInfo[0]
      const supplyBonus = userMemberInfo.supplyBonus
      logistFee.value = res <= 0 ? 0.01 : res
      inAround.value = res < 0 ? false : true
      canPayBayAccount.value = Number(supplyBonus) < Number(res <= 0 ? 0.01 : res) ? false : true
    })
    .catch(() => {
      uni.showToast({
        title: '选择地址不支持发货',
        icon: 'none',
      })
      inAround.value = true
    })
}

// getAddressCode 函数 - 获取地址邮编
const getAddressCode = () => {
  const addressOptionValue = addressOption.value
  getAllAddress({})
    .then((res: any) => {
      res.miniAccountAddress.forEach((item: any) => {
        if (item.id === addressOptionValue.id) {
          region.value = item.postCode
        }
      })
    })
    .then(() => {
      getCodeFee()
    })
    .catch((err) => {
      console.log(err)
      uni.showToast({
        title: '获取运费失败',
        icon: 'none',
      })
    })
}

// getUserAdd 函数 - 获取用户地址
const getUserAdd = () => {
  let addressOptionValue = addressOption.value
  const addressValue = address.value
  getAddressList({
    type: 1,
  })
    .then((res: any[]) => {
      if (res.length > 0) {
        res.forEach((item: AddressOption) => {
          if (item.isDefault === 1) {
            addressOptionValue = item
            addressOptionValue.showPhone = addressOptionValue.phone?.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2') || ''
          }
        })
      }
      addressOption.value = addressValue.id ? addressValue : addressOptionValue
    })
    .then(() => {
      getAddressCode()
    })
    .catch(() => {
      uni.showToast({
        title: '获取地址邮编失败',
        icon: 'none',
      })
    })
}

// getSubmitDetail 函数 - 获取提交详情
const getSubmitDetail = (goodDetailStr: string) => {
  goodDetail.value = JSON.parse(goodDetailStr)
  getUserAdd()
}

// getByAccount 函数 - 通过账户支付
const getByAccount = (param: any) => {
  if (canClick.value) {
    return
  }
  uni.showLoading({
    title: '订单提交中...',
  })
  getPrizeOrder(param)
    .then((res: any) => {
      canClick.value = true
      $Popup
        .modal({
          title: '支付成功',
          content: '您已成功支付，可联系商家查看物流信息',
        })
        .then(() => {
          payOrderCallBack({
            isSucceed: true,
            prizeOrderId: res,
          }).then(() => {
            canClick.value = false
            $Popup.toast('支付成功')
            setTimeout(() => {
              uni.navigateBack()
            }, 1000)
          })
        })
        .catch(() => {
          $Popup.toast('支付失败')
        })
      uni.hideLoading()
    })
    .catch((err: any) => {
      uni.showToast({
        title: `${err}`,
        icon: 'none',
      })
      uni.hideLoading()
    })
}

// 独立函数 - 其他函数

// goSelectAddress 函数 - 跳转选择地址页面
const goSelectAddress = () => {
  uni.$on(CHOOSE_ADDRESS, (addressOption) => {
    address.value = addressOption
  })
  uni.navigateTo({
    url: `/subcontract/pages/address/address?redirect=submit`,
  })
}

// choosePayWay 函数 - 选择支付方式
const choosePayWay = (e: { currentTarget: { dataset: { info: PayItem } } }) => {
  const info = e.currentTarget.dataset.info
  let newPayId = 101
  const newPayList = payList.value.map((item) => {
    item.chooseFlag = false
    if (info.pay === item.pay) {
      item.chooseFlag = true
      newPayId = item.id
    }
    return item
  })
  payList.value = newPayList
  payId.value = newPayId
}

// handleIptChange 函数 - 处理输入变更
const handleIptChange = (e: string) => {
  buyersNote.value = e
}

// handleSubmit 函数 - 处理提交
const handleSubmit = async () => {
  const messageStore = useMessageStore()
  await messageStore.subscribe([messageStore.type.sendMsg])
  haSubscribed.value = true
  const addressOptionValue = addressOption.value
  if (!addressOptionValue.detailInfo) {
    uni.showToast({
      title: '请先选择收货地址',
      icon: 'none',
    })
    return
  }
  if (!inAround.value) {
    uni.showToast({
      title: '选择地址不支持发货',
      icon: 'none',
    })
    return
  }
  uni.showLoading({
    mask: true,
    title: '订单创建中...',
  })
  const deliveryTemplateId = await messageStore.getTemplateId(messageStore.type.sendMsg)
  if (!addressOptionValue.location) {
    uni.showToast({
      title: '地址信息不完整',
      icon: 'none',
    })
    uni.hideLoading()
    return
  }

  const location = addressOptionValue.location.split(',')
  const addressDetail = await placeAround(`${location[0]},${location[1]}`)
  const poi = addressDetail.pois[0]
  const cityCode = await getadCodBycityCode(poi.citycode)
  const { pcode, adcode } = poi
  const param = {
    provinceCode: pcode,
    cityCode: cityCode,
    areaCode: adcode,
    address: `${addressOptionValue.province} ${addressOptionValue.city} ${addressOptionValue.county} ${addressOptionValue.detailInfo}`,
    orderType: false,
    payType: payId.value,
    price: goodDetail.value.integralPrice,
    prizeId: goodDetail.value.id,
    integralGoodsPic: goodDetail.value.pic,
    receiverName: addressOptionValue.userName,
    shippingFee: logistFee.value,
    telPhone: addressOptionValue.phone,
    buyersNote: buyersNote.value,
    deliveryTemplateId: haSubscribed.value ? deliveryTemplateId : -1,
  }
  if (payId.value === 100) {
    getByAccount(param)
    uni.hideLoading()
    return
  }
  uni.showLoading({
    title: '订单提交中...',
  })
  getPrizeOrder(param)
    .then((res: any) => {
      const prizeOrderId = res
      uni.hideLoading()
      uni.showLoading({
        title: '支付中...',
      })
      payOrder(res, {})
        .then((v: any) => {
          if (v.returnCode === 'FAIL') {
            uni.hideLoading()
            uni.showToast({
              title: v.returnMsg,
              icon: 'none',
            })
            return
          }
          const wxResult = v.wxResult
          requestPayment(
            wxResult,
            () => {
              payOrderCallBack({
                isSucceed: true,
                prizeOrderId,
              })
              uni.hideLoading()
              $Popup
                .modal({
                  title: '支付成功',
                  content: '您已成功支付，可联系商家查看物流信息',
                })
                .then(() => {
                  $Popup.toast('支付成功')
                  setTimeout(() => {
                    uni.navigateBack()
                  }, 1000)
                })
                .catch(() => {
                  $Popup.toast('支付失败')
                })
            },
            (err: any) => {
              console.log(err)
              uni.hideLoading()
              $Popup.toast('支付失败')
              payOrderCallBack({
                isSucceed: false,
                prizeOrderId,
              })
            },
          )
        })
        .catch((err) => {
          console.log(err)
          uni.showToast({
            title: '支付失败',
            icon: 'none',
          })
          uni.hideLoading()
          payOrderCallBack({
            isSucceed: false,
            prizeOrderId,
          })
        })
    })
    .catch((err: any) => {
      $Popup.toast(err || '支付失败')
    })
  uni.hideLoading()
}

// 生命周期
onLoad((options?: Obj) => {
  if (options?.goodDetail) {
    storage.clear()
    storage.set('submitData', options.goodDetail)
    getSubmitDetail(options.goodDetail)
  } else {
    storage.get('submitData')
    if (options?.address) {
      address.value = JSON.parse(options.address)
    }
    getSubmitDetail(storage.get('submitData'))
  }
})

onShow(() => {
  const pages = getCurrentPages()
  const currPage = pages[pages.length - 1] as any
  if (currPage.$vm.address?.id) {
    currPage.$vm.address.showPhone = currPage.$vm.address.phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
    addressOption.value = currPage.$vm.address
    getAddressCode()
  }
})

onBeforeUnmount(() => {
  uni.$off(CHOOSE_ADDRESS)
})
</script>

<style lang="scss" scoped>
@import '@/assets/styles/submit/submit.scss';
.page {
  padding-bottom: calc(170rpx + constant(safe-area-inset-bottom));
  padding-bottom: calc(170rpx + env(safe-area-inset-bottom));
}

.payWay__balance {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100rpx;
}

.submit {
  display: -webkit-box;
  display: flex;
  -webkit-box-orient: horizontal;
  -webkit-box-direction: normal;
  flex-direction: row;
  -webkit-box-align: center;
  align-items: center;
  height: 170rpx;
  position: fixed;
  bottom: 0;
  padding-left: 40rpx;
  width: 750rpx;
  border-top: 1rpx solid #e9e9e9;
  background-color: white;
  display: flex;
  -webkit-box-pack: end;
  justify-content: space-between;
  -webkit-box-align: end;
  align-items: flex-end;
}

.submit__left--all {
  display: -webkit-box;
  display: flex;
  -webkit-box-align: end;
  align-items: flex-end;
  color: #fa5555;
  margin-left: 20rpx;
}
</style>
