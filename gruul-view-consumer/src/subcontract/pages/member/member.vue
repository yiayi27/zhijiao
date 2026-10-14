<template>
  <view>
    <!-- 授权后购买会员 -->
    <view class="topUser">
      <view class="topUser__left">
        <image
          :src="avatarUrl || 'https://images.unsplash.com/photo-1551334787-21e6bd3ab135?w=640'"
          style="width: 80rpx; height: 80rpx; border-radius: 100%"
        />
        <view class="topUser__left--phone">{{ nikeName }}</view>
      </view>
      <view class="topUser__right" @click="showDesc">
        会员说明
        <uv-icon name="question-circle" />
      </view>
    </view>
    <UPopup v-model:show="memDescFlag" :customStyle="{ backgroundColor: 'transparent', top: '40%' }" @close="onClose">
      <view class="descConter">
        <view class="descConter__top">会员说明</view>
        <uv-icon name="close-circle" color="#CCCBC9" size="40rpx" class="descConter__top--icon" @click="onClose" />
        <view class="descConter__conter">
          <view class="descConter__conter__title">
            <text>1</text>
            会员介绍
          </view>
          <view class="descConter__conter_con">
            客户可以选择相应的会员等级进行购买，以此来开通会员，享受不同的会员权益，在某一等级使用期间，可通过付费或积分兑换进行升级，开通时长不会累计。
          </view>
        </view>
        <view class="descConter__conter">
          <view class="descConter__conter__title">
            <text>2</text>
            会员有效期
          </view>
          <view class="descConter__conter_con">支付成功后即日起开始计算，12个月有效期为365天，到期后将自动取消会员享有的所有权益。</view>
        </view>
        <view class="descConter__conter">
          <view class="descConter__conter__title">
            <text>3</text>
            关于退款
          </view>
          <view class="descConter__conter_con">购买此卡后不可做任何退款申请。</view>
        </view>
      </view>
    </UPopup>
    <view class="imageContainer_Wrap">
      <swiper
        class="imageContainer"
        previous-margin="50rpx"
        next-margin="50rpx"
        circular
        indicator-dots="true"
        indicator-color="#a39f99"
        indicator-active-color="#f49641"
        @change="handleChange"
      >
        <block v-for="(item, index) in memberCards" :key="index">
          <swiper-item class="imageContainer__swiperItem">
            <view :class="['imageContainer__swiperItem__content', currentIndex === index && 'imageContainer__swiperItem__content__active']">
              <view style="font-size: 36rpx; margin-bottom: 8rpx">{{ title }}</view>
              <view style="font-size: 24rpx; display: inline-block; color: #666; border-radius: 32rpx; padding: 4rpx 16rpx; background: #fff">
                {{ item.rankName }}LV{{ item.rankCode }}
              </view>
              <view v-if="openType === 2" class="imageContainer__swiperItem__paymount"> {{ item.integralCount }}积分／{{ useInDate }}天 </view>
              <view v-if="openType === 1" class="imageContainer__swiperItem__paymount"> ¥{{ item.payAmount }}／{{ useInDate }}天 </view>
              <view v-if="openType === 4" class="imageContainer__swiperItem__paymount"> 购买指定商品自动发放 </view>
              <view v-if="openType === 3" class="imageContainer__swiperItem__paymount"> 消费满{{ item.consumeCount }}元自动发放 </view>
            </view>
            <image
              :class="['imageContainer__swiperItem__itemImg', currentIndex === index ? 'imageContainer__swiperItem__itemImg--active' : '']"
              :src="item.backGroundPicUrl || defaultImg"
            />
          </swiper-item>
        </block>
      </swiper>
    </view>
    <view class="privilege">
      <view style="display: flex; font-size: 26rpx">
        <view class="privilege__sign" />
        会员特权
      </view>
      <view class="privilege__conter">
        <view v-for="(item, index) in privilegeList" :key="index" class="privilege__conter--list">
          <view :data-index="index" class="privilege__conter--list--item" @click="onPrivilegeTap">
            <image
              :src="item.icon || ptypeMapIcon[item.ptype as keyof typeof ptypeMapIcon]"
              :class="['privilege-img', privilegeIndex === index ? '-active' : '']"
            />
          </view>
          <view :data-index="index" style="width: 100%; text-align: center" @click="onPrivilegeTap">{{ item.privilegeName }} </view>
        </view>
      </view>
      <view class="privilege__desc">
        <view style="font-weight: bold">{{ privilege.privilegeName }}</view>
        <view v-if="privilege.ptype === 1" style="margin-top: 20rpx">
          {{ privilege.illustrate ? privilege.illustrate + (privilege.value ? '(折扣' + privilege.value / 10 + '折)' : '') : '暂无特殊优待' }}
        </view>
        <view v-if="privilege.ptype === 3" style="margin-top: 20rpx">
          {{ privilege.illustrate ? (privilege.value ? privilege.illustrate + '(加' + privilege.value + '倍)' : '') : '暂无特殊优待' }}
        </view>
        <view v-if="privilege.ptype !== 3 && privilege.ptype !== 1" style="margin-top: 20rpx">
          {{ privilege.illustrate || '暂无特殊优待' }}
        </view>
      </view>
    </view>
    <view v-if="openType === 1 || openType === 2" class="opening" @click="openMem">立即开通</view>
    <view v-if="openType === 4" class="good__member">
      <view style="height: 80rpx; line-height: 80rpx; text-align: center; font-size: 30rpx; color: #fff" class="good__member__btn">
        <view style="color: #fff; height: 80rpx; border-radius: 50rpx; display: inline-block; background: rgba(230, 183, 98, 1); padding: 0rpx 40rpx">
          购买下方商品自动发放会员卡
        </view>
      </view>
      <view style="margin: 30rpx 0; text-align: center; font-size: 30rpx; color: #666">
        <text>购买任一商品即可获得会员卡</text>
      </view>
      <view class="good-list">
        <block v-for="(item, index) in goodsList" :key="index">
          <view class="good-list-item" :data-id="item.id" @click="goodClick">
            <image style="height: 380rpx" :src="item.pic" />
            <view class="good-list-item-name">{{ item.name }}</view>
            <view style="color: red" class="good-list-item-price">¥{{ item.maxPrice }}</view>
          </view>
        </block>
      </view>
    </view>
    <UPopup v-model:show="payTypeFlag" mode="bottom" round="10" @close="onClose">
      <view class="payConter" @click="hideMemberDrop">
        <view class="payConter__top">
          <uv-icon name="arrow-left" style="margin-right: 40rpx" @click="onClose" />
          开通会员
        </view>
        <view class="payConter__openingMode">
          <view>会员等级</view>
          <view style="position: relative; display: flex" @click.stop="onShowMember">
            {{ selectOption.rankName }}
            <uv-icon name="arrow-down" style="margin-left: 10rpx" />
            <view v-if="showMemberDrop" class="custom__drop--down">
              <view
                v-for="(item, index) in memberLevel.options"
                :key="index"
                :class="['member-item', item.id === memberLevel.id && 'member-item-active']"
                :data-id="item.id"
                @click.stop="onMemberSelect"
              >
                {{ item.rankName }}
              </view>
            </view>
          </view>
        </view>
        <view v-if="openType === 1" class="payConter__openingMode">
          <view>价格</view>
          <view>¥{{ selectOption.payAmount }}</view>
        </view>
        <view v-if="openType === 2" class="payConter__openingMode">
          <view>积分</view>
          <view>{{ selectOption.integralCount }}</view>
        </view>
        <view class="payConter__openingMode">
          <view>有效期</view>
          <view>{{ useInDate }}天</view>
        </view>
        <view v-if="openType === 1" class="payConter__openingMode">
          <view>支付方式</view>
          <view style="position: relative; display: flex" @click.stop="onShowPayType">
            {{ payType.text }}
            <uv-icon name="arrow-down" style="margin-left: 10rpx" />
            <view v-if="showPayType" class="custom__drop--down">
              <view
                v-for="(item, index) in payType.options"
                :key="index"
                :class="'member-item ' + (item.type === payType.type && 'member-item-active')"
                :data-type="item.type"
                @click.stop="onPayTypeSelect"
              >
                {{ item.text }}
              </view>
            </view>
          </view>
        </view>
        <view v-if="openType === 2" style="display: flex; justify-content: center">
          <text class="can__use">当前可用积分{{ useAmount }}</text>
          <view :class="['payConter__btn', selectOption.integralCount > useAmount && 'payConter__btn-no']" @click="paySure">
            {{ selectOption.integralCount > useAmount ? '积分不足' : '确认兑换' }}
          </view>
        </view>
        <view v-if="openType === 1" style="display: flex; justify-content: center">
          <text v-if="payType.type === 2" class="can__use">当前可用余额¥{{ useAmount }}</text>
          <view :class="['payConter__btn', selectOption.payAmount > useAmount && payType.type === 2 && 'payConter__btn-no']" @click="paySure">
            {{ selectOption.payAmount > useAmount && payType.type === 2 ? '余额不足' : '确认支付' }}
          </view>
        </view>
      </view>
    </UPopup>
    <view class="popup-wrap">
      <UPopup v-model:show="userInfoShow" mode="bottom" @close="onClose">
        <view v-if="nextFlag" class="pop">
          <view>
            <view class="payConter__top">
              <uv-icon name="arrow-left" style="margin-right: 40rpx" @click="onClose" />
              填写信息
            </view>
            <view class="pop__conter">
              <view class="pop__conter--phone">
                <view class="pop__conter--phone--text">
                  <view class="pop__conter--phone-label">
                    手机号
                    <text>{{ phoneForm.isRequired ? '*' : '' }}</text>
                  </view>
                  <uv-input
                    v-model="userRegisterForm.phone"
                    :maxlength="11"
                    :placeholder="phoneForm.tips"
                    style="margin-left: 40rpx"
                    @blur="onPhoneBlur"
                  />
                </view>
                <view :class="['pop__conter--phone--code', formCode.isDisabled && 'phone__code--none']" @click="onSendCode">
                  {{ formCode.buttonName }}
                </view>
              </view>
              <view class="pop__conter--phone">
                <view class="pop__conter--phone--text">
                  <view class="pop__conter--phone-label">
                    验证码
                    <text>*</text>
                  </view>
                  <uv-input v-model="userRegisterForm.code" placeholder="请输入验证码" style="margin-left: 40rpx" />
                </view>
              </view>
              <view v-for="(item, index) in memberRegisterFormVos" :key="index" class="pop__conter--phone">
                <view class="pop__conter--phone--text">
                  <view class="pop__conter--phone-label">
                    {{ item.formName }}
                    <text>{{ item.isRequired ? '*' : '' }}</text>
                  </view>
                  <uv-input v-model="registerData[index]" :placeholder="item.tips" style="margin-left: 40rpx" />
                </view>
              </view>
            </view>
          </view>
          <view style="display: flex; justify-content: center">
            <view class="pop__next" @click="onPassMember">保存并开通会员</view>
          </view>
        </view>
      </UPopup>
    </view>
  </view>
</template>
<script setup lang="ts">
import { ref, nextTick } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getMemberCardInfo, confirmPayment, sendCode, updateRegisterData } from '@/api/modules/member'
import { ptypeMapIcon } from '@/utils/const'
import { $Popup, isPhone } from '@/utils/helper'
import { requestPayment } from '@/utils/requestPayment'

// 定义接口
interface MemberOption {
  id: number
  label?: string
  rankName?: string
  rankCode?: string
  backGroundPicUrl?: string
  integralCount?: number
  payAmount?: number
  consumeCount?: number
  memberPrivilegeVos?: any[]
  memberCardGoodsVo?: {
    discountProductVos?: any[]
  }
  isOpen?: string
  isSelected?: string
}

interface MemberLevel {
  options: MemberOption[]
  id: number
}

interface FormCode {
  buttonName: string
  isDisabled: boolean
  time: number
}

interface UserRegisterForm {
  phone: string | null
  code: string | null
}

interface TableMap {
  [key: Long]: string
}

// 响应式数据
const memShow = ref(false)
const defaultImg = ref('https://images.unsplash.com/photo-1551446591-142875a901a1?w=640')
const nextFlag = ref(true)
const memberCards = ref<any[]>([])
const title = ref('')
const useInDate = ref(365)
const avatarUrl = ref('')
const currentIndex = ref(0)
const privilegeList = ref<any[]>([])
const privilege = ref<Obj>({})
const privilegeIndex = ref(0)
const goodsList = ref<any[]>([])
const memDescFlag = ref(false)
const payTypeFlag = ref(false)
const openFlag = ref(false)
const openType = ref(0)
const useAmount = ref(0)
const memberLevel = ref<MemberLevel>({
  options: [
    {
      id: 1,
      label: '普卡会员',
    },
    {
      id: 2,
      label: '金卡会员',
    },
    {
      id: 3,
      label: '钻石会员',
    },
  ],
  id: 1,
})
const showMemberDrop = ref(false)
const showPayType = ref(false)
const userInfoShow = ref(false)
const phoneForm = ref<Obj>({})
const memberRegisterFormVos = ref<any[]>([])
const payType = ref({
  options: [
    {
      type: 1,
      text: '微信支付',
    },
    {
      type: 2,
      text: '余额支付',
    },
  ],
  type: 1,
  text: '微信支付',
})
const formCode = ref<FormCode>({
  buttonName: '获取验证码',
  isDisabled: false,
  time: 60,
})
const userRegisterForm = ref<UserRegisterForm>({
  phone: null,
  code: null,
})
const registerData = ref<any[]>([])
const selectOption = ref<Obj>({})
const nikeName = ref('')
const tableMap = ref<TableMap>({
  1: 'paymentVos',
  2: 'integralVos',
  3: 'consumeVos',
  4: 'assignProductVos',
})

// 方法定义
const showDesc = () => {
  memDescFlag.value = true
}

const handleChange = (e: any) => {
  const memberCard: any = memberCards.value[e.detail.current]
  const newPrivilegeList = (memberCard.memberPrivilegeVos || []).filter((v: any) => v.isSelected + '' === '1')
  currentIndex.value = e.detail.current
  privilegeList.value = newPrivilegeList
  privilege.value = newPrivilegeList[0] || {}
  privilegeIndex.value = 0
  goodsList.value = memberCard.memberCardGoodsVo && memberCard.memberCardGoodsVo.discountProductVos
}

const openMem = () => {
  payTypeFlag.value = true
}

const paySure = () => {
  const currentSelectOption: any = selectOption.value
  if (openType.value === 2 && currentSelectOption.integralCount > useAmount.value) {
    uni.showToast({
      title: '余额不足',
      icon: 'none',
      duration: 1500,
    })
    return null
  }
  if (openType.value === 1 && currentSelectOption.payAmount > useAmount.value && payType.value.type === 2) {
    uni.showToast({
      title: '积分不足',
      icon: 'none',
      duration: 1500,
    })
    return null
  }

  confirmPayment({
    openType: openType.value,
    paymentType: openType.value === 1 ? payType.value.type : undefined,
    useAmount: useAmount.value,
    availableAmount: currentSelectOption.integralCount || currentSelectOption.payAmount,
    memberCardInfoId: currentSelectOption.id,
    useInDate: useInDate.value,
  })
    .then((res) => {
      if (openType.value === 2) {
        payTypeFlag.value = false
        uni.showToast({
          title: '开通成功',
          icon: 'none',
        })
      }
      if (openType.value === 1) {
        if (payType.value.type === 1) {
          // 微信支付
          requestPayment(
            res.wxResult,
            () => {
              payTypeFlag.value = false
              nextTick(() => {
                uni.navigateTo({
                  url: `/subcontract/pages/memberInfo/memberInfo?cardId=${currentSelectOption.id}`,
                })
              })
              uni.showToast({
                title: '支付成功',
                icon: 'success',
                duration: 2000,
              })
            },
            (e: any) => {
              console.log(e)
              const isCancel = (msg: string) => msg.includes('cancel')
              if (!isCancel(e.errMsg)) {
                uni.showToast({
                  title: '支付失败',
                  icon: 'none',
                  duration: 2000,
                })
              }
            },
          )
        }
        if (payType.value.type === 2) {
          // 余额支付
          uni.navigateTo({
            url: `/subcontract/pages/memberInfo/memberInfo?cardId=${currentSelectOption.id}`,
          })
        }
      }
    })
    .catch((err) => {
      uni.showToast({
        title: err,
        icon: 'none',
        duration: 2000,
      })
    })
  return null
}

const onPassMember = () => {
  const { phone, code } = userRegisterForm.value
  const currentRegisterData = registerData.value
  if (!phone || !code) {
    $Popup.alert({
      content: '请输入完整信息',
    })
    return null
  }
  updateRegisterData({
    phone,
    code,
    registerData: currentRegisterData.join(','),
  })
    .then(() => {
      openFlag.value = true
      userInfoShow.value = false
      uni.redirectTo({
        url: '/subcontract/pages/memberVip/memberVip',
      })
    })
    .catch((res) => {
      $Popup.toast(res)
    })
  return null
}

const onClose = () => {
  memShow.value = false
  memDescFlag.value = false
  payTypeFlag.value = false
  openFlag.value = false
  userInfoShow.value = false
}

const onPrivilegeTap = (e: any) => {
  const index = e.target.dataset.index
  privilege.value = privilegeList.value[index]
  privilegeIndex.value = index
}

const onShowMember = () => {
  showMemberDrop.value = true
}

const onShowPayType = () => {
  showPayType.value = true
}

const hideMemberDrop = () => {
  showMemberDrop.value = false
  showPayType.value = false
}

const onMemberSelect = (e: any) => {
  const currentMemberLevel = memberLevel.value
  currentMemberLevel.id = e.target.dataset.id
  const newSelectOption: any = currentMemberLevel.options.find((v) => v.id === currentMemberLevel.id) || {}
  showMemberDrop.value = false
  memberLevel.value = currentMemberLevel
  selectOption.value = newSelectOption
}

const onPayTypeSelect = (e: any) => {
  const currentPayType = payType.value
  currentPayType.type = e.target.dataset.type
  const newSelectOption: any = currentPayType.options.find((v) => v.type === currentPayType.type) || {}
  currentPayType.text = newSelectOption.text
  showPayType.value = false
  payType.value = currentPayType
}

const validPhone = (value: string, noTip?: boolean) => {
  if (!value) {
    if (!noTip) {
      $Popup.alert({
        content: '请输入电话号码',
      })
    }
  } else if (!isPhone(value)) {
    if (!noTip) {
      $Popup.alert({
        content: '请输入正确的11位手机号码',
      })
    }
  } else {
    return true
  }
  return false
}

const onPhoneBlur = (e: any) => {
  validPhone(e, false)
}

const onSendCode = () => {
  const phone = userRegisterForm.value.phone
  let timer: ReturnType<typeof setInterval> | null = null
  if ((phone && validPhone(phone, false)) || formCode.value.isDisabled) {
    formCode.value.isDisabled = true
    timer = setInterval(() => {
      formCode.value.time -= 1
      formCode.value.buttonName = `${formCode.value.time}秒后重新发送`
      if (formCode.value.time === 0) {
        formCode.value.buttonName = '重新发送'
        formCode.value.isDisabled = false
        if (timer) clearInterval(timer)
      }
    }, 1000)
    if (phone) {
      sendCode({
        phone,
      })
    }
  }
}

const goodClick = (e: any) => {
  const id = e.currentTarget.dataset.id
  uni.navigateTo({
    url: `/subcontract/pages/detail/detail?id=${id}`,
  })
}

// 生命周期钩子
onLoad(() => {
  uni.showLoading({
    title: '加载中',
  })

  getMemberCardInfo(null)
    .then((data) => {
      if (data) {
        let newMemberCards = ((data[tableMap.value[data.openType as keyof typeof tableMap.value]] as any[]) || []).filter(
          (v: any) => v.isOpen + '' === '1',
        )
        const newPrivilegeList = (newMemberCards[0]?.memberPrivilegeVos || []).filter((v: any) => v.isSelected + '' === '1')
        newMemberCards = newMemberCards.filter((v: any) => v.rankName !== '')

        uni.hideLoading()

        openType.value = data.openType
        avatarUrl.value = data.avatarUrl
        nikeName.value = data.nikeName
        memberLevel.value = {
          options: newMemberCards,
          id: newMemberCards[0]?.id,
        }
        selectOption.value = newMemberCards[0] || {}
        privilegeList.value = newPrivilegeList
        privilege.value = newPrivilegeList[0] || {}
        privilegeIndex.value = 0
        memberCards.value = newMemberCards
        title.value = data.title
        useInDate.value = data.useInDate
        phoneForm.value = data.memberRegisterFormVos[0]
        useAmount.value = data.useAmount
        memberRegisterFormVos.value = data.memberRegisterFormVos.filter((v: any) => v.formId + '' !== '1')
        goodsList.value = newMemberCards[0]?.memberCardGoodsVo && newMemberCards[0].memberCardGoodsVo.discountProductVos
      }
    })
    .catch(() => {
      uni.hideLoading()
      uni.showToast({
        title: '获取会员信息失败',
        icon: 'none',
        duration: 2000,
      })
    })
})
</script>
<style lang="scss" scoped>
@mixin middle-center {
  display: flex;
  justify-content: center;
  align-items: center;
}

.phoneAuthorize {
  position: fixed;
  width: 500rpx;
  height: 90rpx;
  background-color: #eac67a;
  bottom: 450rpx;
  font-size: 32rpx;
  @include middle-center;
}

.phoneAnother {
  position: fixed;
  width: 500rpx;
  height: 90rpx;
  background-color: #3c3b43;
  bottom: 300rpx;
  font-size: 32rpx;
  border: 1rpx solid #eac67a;
  color: #eac67a;
  @include middle-center;
}

.pop {
  // height: 600rpx;
  height: 100%;
  font-size: 30rpx;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.pop__top {
  background-color: #edebec;
  padding-left: 40rpx;
  padding-right: 40rpx;
  padding-top: 30rpx;
  padding-bottom: 30rpx;
  color: #9c9c9c;
}

.pop__conter {
  margin-top: 40rpx;

  &--text {
    color: #9c9c9c;
    margin-left: 40rpx;
    margin-right: 40rpx;
    padding-bottom: 20rpx;
    border-bottom: 1rpx solid #f7f5f6;
  }

  &--phone {
    color: #9c9c9c;
    margin-left: 20rpx;
    margin-right: 20rpx;
    padding-bottom: 40rpx;
    padding-top: 40rpx;
    border-bottom: 1rpx solid #f7f5f6;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
}

.pop__conter--phone--text {
  // width: 600rpx;
  display: flex;
  align-items: center;
}

.pop__conter--phone--code {
  width: 250rpx;
  height: 50rpx;
  border-radius: 25rpx;
  border: 2rpx solid #e0c286;
  color: #e0c286;
  font-size: 28rpx;
  display: flex;
  @include middle-center;
}

.pop__next {
  width: 500rpx;
  height: 80rpx;
  background-color: #dab46b;
  color: white;
  border-radius: 40rpx;
  @include middle-center;
}

.topUser {
  background-color: #3e3d45;
  padding-top: 20rpx;
  padding-left: 40rpx;
  padding-right: 40rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 28rpx;
  color: #fff;

  &__left {
    display: flex;
    align-items: center;

    &--phone {
      margin-left: 20rpx;
    }
  }

  &__right {
    display: flex;
    align-items: center;
  }
}

.descConter {
  background-color: #fff6e5;
  padding-bottom: 40rpx;
}

.descConter__top {
  padding-top: 20rpx;
  font-size: 32rpx;
  font-weight: bold;
  // background: linear-gradient(to right, #63583A, #AC941F);
  background-clip: text;
  color: transparent;
  display: flex;
  justify-content: center;

  &--icon {
    display: flex;
    justify-content: flex-end;
    margin-top: -40rpx;
    margin-right: 20rpx;
  }
}

.descConter__conter {
  margin-left: 40rpx;
  margin-top: 20rpx;
  font-size: 26rpx;
  margin-right: 40rpx;

  &__title {
    line-height: 35rpx;

    > text {
      display: inline-block;
      width: 35rpx;
      height: 35rpx;
      text-align: center;
      line-height: 35rpx;
      border-radius: 35rpx;
      background-color: rgba(235, 200, 98, 1);
    }
  }

  &_con {
    font-size: 24rpx;
    color: #666;
  }
}

.imageContainer_Wrap {
  width: 100%;
  height: 300rpx;
  margin-bottom: 100rpx;
  position: relative;
  background-color: #3e3d45;
}

.imageContainer {
  width: 100%;
  height: 400rpx;
  top: 14rpx;
  position: absolute;

  &::after {
    content: '';
    display: block;
    width: 100%;
    height: 50rpx;
    background-color: #fff;
    position: absolute;
    bottom: 0;
  }
}

.imageContainer__swiperItem {
  height: 350rpx;
}

.imageContainer__swiperItem__itemImg,
.imageContainer__swiperItem__content {
  position: absolute;
  width: 100%;
  height: 230rpx;
  border-radius: 15rpx;
  z-index: 5;
  opacity: 0.7;
  top: 13%;
}

.imageContainer__swiperItem__itemImg--active,
.imageContainer__swiperItem__content__active {
  opacity: 1;
  z-index: 10;
  height: 310rpx;
  top: 7%;
  transition: all 0.2s ease-in 0s;
}

.privilege {
  margin-top: 50rpx;
  padding: 20rpx;
}

.privilege__sign {
  width: 12rpx;
  height: 40rpx;
  background-color: #e1b56a;
  margin-right: 20rpx;
}

.privilege__conter {
  display: flex;
  // justify-content: space-between;
  flex-wrap: wrap;
  margin-top: 10rpx;
}

.privilege__conter--list {
  width: 25%;
  margin-bottom: 10rpx;
  margin-top: 10rpx;
  flex-wrap: wrap;
  font-size: 24rpx;
  display: flex;
  justify-content: center;

  .privilege__conter--list--item {
    width: 100rpx;
    height: 100rpx;
    // background-color: #FFFBF0;
    border-radius: 100%;
    @include middle-center;
  }
}

.privilege__desc {
  padding: 20rpx;
  margin: 20rpx;
  border: 1rpx solid #efefef;
  font-size: 26rpx;
}

.opening {
  position: fixed;
  bottom: 0;
  width: 100%;
  height: 90rpx;
  font-size: 30rpx;
  font-weight: bold;
  color: white;
  background: linear-gradient(to right, #ebcc89, #b4893b);
  @include middle-center;
}

.payConter {
  width: 100%;
  height: 720rpx;
  padding: 20rpx;
}

.payConter__top {
  font-size: 34rpx;
  display: flex;
  align-items: center;
}

.payConter__num {
  padding: 60rpx;
  font-size: 80rpx;
  @include middle-center;
}

.payConter__openingMode {
  font-size: 32rpx;
  padding-left: 20rpx;
  padding-right: 20rpx;
  color: #666666;
  display: flex;
  justify-content: space-between;
  margin: 40rpx 0;
}

.payConter__btn {
  margin-top: 80rpx;
  width: 450rpx;
  height: 80rpx;
  border-radius: 50rpx;
  background-color: #fe4e63;
  color: white;
  font-size: 30rpx;
  box-shadow: 0 0 80rpx #fedbdf;
  @include middle-center;

  &-no {
    background-color: #aaa;
  }
}

.imageContainer__swiperItem__content {
  z-index: 100;
  padding: 40rpx 32rpx;
  color: #fff;
}

.good-list {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  padding: 0 24rpx;
}

.good-list-item {
  width: 49%;
  height: 500rpx;
  margin-bottom: 14rpx;
  position: relative;

  > image {
    width: 100%;
    background-color: #666;
    object-fit: cover;
  }
}

.custom__drop--down {
  position: absolute;
  width: 200rpx;
  background: #fff;
  box-shadow: 0 1px 6rpx rgba(0, 0, 0, 0.3);
  padding: 20rpx;
  z-index: 1000;
  right: 0;
}

.member-item {
  margin: 15rpx 0;

  &-active {
    color: #e1b56a;
  }
}

.popup-wrap {
  .van-popup--bottom.van-popup--safe {
    height: 100%;
  }
}

.privilege-img {
  width: 50rpx;
  height: 50rpx;

  &-active {
    width: 60rpx;
    height: 60rpx;
  }
}

.imageContainer__swiperItem__paymount {
  // box-shadow: -4rpx 4rpx 4rpx #888888;
  font-size: 30rpx;
  position: absolute;
  bottom: 40rpx;
  left: 32rpx;
  padding: 12rpx 36rpx;
  border-radius: 80rpx;
  background-color: #fff;
  color: #666;
  // background: linear-gradient(to right, rgb(202,191,212), rgb(138, 142,145));
}

.can__use {
  position: absolute;
  font-size: 24rpx;
  color: #999;
}

.phone__code--none {
  border: 1rpx solid #b7b7b7;
}

.good-list-item-name {
  font-size: 32rpx;
  width: 100%;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.pop__conter--phone-label {
  white-space: nowrap;

  text {
    color: red;
  }
}

.topBack__member {
  // width: 90%;
  // margin-left: 5%;
  width: 400rpx;
  height: 200rpx;
  border-radius: 15rpx;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 0 20rpx;
  background-size: 100% 100%;
  color: white;
  font-size: 30rpx;
  margin: 0 100rpx;
}

.topBack__member-level {
  margin-top: 18rpx;
  padding: 6rpx 12rpx;
  background-color: #fff;
  font-size: 26rpx;
  color: #aaa;
  border-radius: 50rpx;
  display: inline-block;
}

.topBack__member-renew {
  float: right;
  padding: 10rpx 30rpx;
  border-radius: 50rpx;
  background: linear-gradient(to right, rgb(202, 191, 212), rgb(138, 142, 145));
}

.topBack__member-empty {
  float: right;
  padding: 10rpx 30rpx;
  height: 60rpx;
}

.topBack__member-time {
  float: left;
  font-size: 24rpx;

  text {
    display: block;
  }
}
</style>
