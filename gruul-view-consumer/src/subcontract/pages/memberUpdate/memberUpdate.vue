<template>
  <view>
    <!-- 授权后购买会员 -->
    <view class="topUser">
      <view class="topUser__left">
        <image :src="avatarUrl" style="width: 80rpx; height: 80rpx; border-radius: 100%" />
        <view class="topUser__left--phone">{{ nikeName }}</view>
      </view>
    </view>
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
            <view style="font-size: 36rpx; margin-bottom: 8rpx">{{ item.title }}</view>
            <view style="font-size: 24rpx; display: inline-block; color: #666; border-radius: 32rpx; padding: 4rpx 16rpx; background: #fff">
              {{ item.rankName }}LV{{ item.rankCode }}
            </view>
            <view v-if="item.openType === 1" class="imageContainer__swiperItem__paymount"> ¥{{ item.openValue }}／{{ item.useInDate }}天 </view>
            <view v-if="item.openType === 2" class="imageContainer__swiperItem__paymount"> {{ item.openValue }}积分／{{ item.useInDate }}天 </view>
          </view>
          <image
            :class="['imageContainer__swiperItem__itemImg', currentIndex === index ? 'imageContainer__swiperItem__itemImg--active' : '']"
            :src="item.backGroundPicUrl || defaultImg"
          />
        </swiper-item>
      </block>
    </swiper>
    <view class="privilege">
      <view style="display: flex; font-size: 26rpx">
        <view class="privilege__sign" />
        会员特权
      </view>
      <view class="privilege__conter">
        <view v-for="(item, index) in privilegeList" :key="index" class="privilege__conter--list">
          <view :data-index="index" class="privilege__conter--list--item" @tap="onPrivilegeTap">
            <image
              :src="item.icon || ptypeMapIcon[item.ptype as keyof typeof ptypeMapIcon]"
              :class="['privilege-img', privilegeIndex === index ? '-active' : '']"
            />
          </view>
          <view style="width: 100%; text-align: center">{{ item.privilegeName }}</view>
        </view>
      </view>
      <view class="privilege__desc">
        <view style="font-weight: bold">{{ privilege.privilegeName }}</view>
        <view v-if="privilege.ptype === '1'" style="margin-top: 20rpx">
          {{ privilege.illustrate ? privilege.illustrate + (privilege.value ? '(折扣' + privilege.value / 10 + '折)' : '') : '暂无特殊优待' }}
        </view>
        <view v-if="privilege.ptype === '3'" style="margin-top: 20rpx">
          {{ privilege.illustrate ? (privilege.value ? privilege.illustrate + '(加' + privilege.value + '倍)' : '') : '暂无特殊优待' }}
        </view>
        <view v-if="privilege.ptype !== '3' && privilege.ptype !== '1'" style="margin-top: 20rpx">
          {{ privilege.illustrate || '暂无特殊优待' }}
        </view>
      </view>
    </view>
    <view class="opening" @tap="openMem">立即升级</view>
    <UPopup v-model:show="payTypeFlag" mode="bottom" @close="onClose">
      <view class="payConter" @tap="hideMemberDrop">
        <view class="payConter__top">
          <uv-icon label="会员升级" labelColor="#000" labelSize="15" space="10" name="arrow-left" @tap="onClose" />
        </view>
        <view class="payConter__openingMode">
          <view>当前等级</view>
          <view>{{ curMember.rankName }}</view>
        </view>
        <view class="payConter__openingMode">
          <view>剩余时间</view>
          <view>{{ remainDate }}天</view>
        </view>
        <view class="payConter__openingMode">
          <view>升级会员等级</view>
          <view style="position: relative" @tap.stop="onShowMember">
            <uv-icon
              v-if="memberLevel.options.length > 1"
              :label="selectOption.rankName"
              labelPos="left"
              labelColor="#666"
              labelSize="15"
              space="10"
              name="arrow-down"
              style="margin-left: 10rpx"
            />
            <view v-if="memberLevel.options.length > 1 && showMemberDrop" class="custom__drop--down">
              <view
                v-for="(item, index) in memberLevel.options"
                :key="index"
                :class="['member-item', item.rankCode === memberLevel.rankCode && 'member-item-active']"
                :data-code="item.rankCode"
                @tap.stop="onMemberSelect"
              >
                {{ item.rankName }}
              </view>
            </view>
          </view>
        </view>
        <view class="payConter__openingMode">
          <view>价格</view>
          <view v-if="openType === 1"> ¥{{ selectOption.openValue }}/{{ selectOption.useInDate }}天 </view>
          <view v-if="openType === 2"> {{ selectOption.openValue }}积分/{{ selectOption.useInDate }}天 </view>
        </view>
        <view class="payConter__openingMode">
          <view>实际支付</view>
          <view> {{ openType === 1 ? '¥' : '' }}{{ selectOption.actualOpenValue }}{{ openType === 2 ? '积分' : '' }} </view>
        </view>
        <view v-if="openType === 1" class="payConter__openingMode">
          <view>支付方式</view>
          <view style="position: relative" @tap.stop="onShowPayType">
            {{ payType.text }}
            <uv-icon name="arrow-down" style="margin-left: 10rpx" />
            <view v-if="showPayType" class="custom__drop--down">
              <view
                v-for="(item, index) in payType.options"
                :key="index"
                :class="['member-item', item.type === payType.type && 'member-item-active']"
                :data-type="item.type"
                @tap.stop="onPayTypeSelect"
              >
                {{ item.text }}
              </view>
            </view>
          </view>
        </view>
        <view v-if="openType === 2" style="display: flex; justify-content: center">
          <text class="can__use">当前可用积分{{ useAmount }}</text>
          <view :class="['payConter__btn', selectOption.actualOpenValue > useAmount || (useAmount === 0 && 'payConter__btn-no')]" @tap="paySure">
            {{ selectOption.actualOpenValue > useAmount || useAmount === 0 ? '积分不足' : '确认兑换' }}
          </view>
        </view>
        <view v-if="openType === 1" style="display: flex; justify-content: center">
          <text v-if="payType.type === 2" class="can__use">当前可用余额¥{{ accountBalance }}</text>
          <view
            :class="[
              'payConter__btn ',
              ((selectOption.actualOpenValue > accountBalance && payType.type === 2) || (payType.type === 2 && accountBalance === 0)) &&
                'payConter__btn-no',
            ]"
            @tap="paySure"
          >
            {{
              (selectOption.actualOpenValue > accountBalance && payType.type === 2) || (payType.type === 2 && accountBalance === 0)
                ? '余额不足'
                : '确认支付'
            }}
          </view>
        </view>
      </view>
    </UPopup>
    <UPopup v-model:show="openFlag" round="25" :customStyle="{ top: '40%' }" @close="onClose">
      <view class="congratulate">
        <view class="congratulate__text">恭喜!</view>
        <view class="congratulate__text">您的{{ selectOption.rankName }}开通成功</view>
        <view class="congratulate__img">
          <image :src="selectOption.backGroundPicUrl" style="width: 400rpx; height: 200rpx" />
        </view>
        <view class="congratulate__sure">
          <view class="congratulate__sure--btn" @tap="onClose">确 定</view>
        </view>
      </view>
    </UPopup>
  </view>
</template>
<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { upgradeMemberRank, getMemberCardUpgrade } from '@/api/modules/member'
import { ptypeMapIcon } from '@/utils/const'
import { requestPayment } from '@/utils/requestPayment'

// 响应式数据
const memShow = ref(false)
const accountBalance = ref(0)
const nextFlag = ref(true)
const memberCards = ref<any[]>([])
const canUpdateMembers = ref<any[]>([])
const memberNumber = ref(null)
const curMember = ref<Obj>({})
const useAmount = ref(0)
const selectOption = ref<Obj>({})
const defaultImg = ref('https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/api/general_card.png')
const currentIndex = ref(0)
const privilegeList = ref<any[]>([])
const memDescFlag = ref(false)
const payTypeFlag = ref(false)
const openFlag = ref(false)
const openType = ref(1)
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
const memberLevel = ref<Obj>({})
const showMemberDrop = ref(false)
const userInfoShow = ref(false)
const avatarUrl = ref('')
const nikeName = ref('')
const remainDate = ref<any>('')
const privilege = ref<{
  ptype: string
  [key: string]: any
}>({} as any)
const privilegeIndex = ref(0)
const showPayType = ref(true)

// 方法定义
const handleChange = (e: any) => {
  const memberCard: any = memberCards.value[e.detail.current]
  // const curMemberCard: any = this.memberCards[e.detail.current];
  const newPrivilegeList = (memberCard.memberCardPrivileges || []).filter((v: any) => v.isSelected + '' === '1')
  // const expriseDate = new Date(curMemberCard.memberExpireTime);
  // const curDate = new Date();
  // const expriseTime = `${expriseDate.getFullYear()}-${expriseDate.getMonth() +
  //   1}-${expriseDate.getDate()}`;
  // const curTime = `${curDate.getFullYear()}-${curDate.getMonth() +
  //   1}-${curDate.getDate()}`;
  currentIndex.value = e.detail.current
  privilegeList.value = newPrivilegeList
  privilege.value = newPrivilegeList[0] || {}
  privilegeIndex.value = 0
  openType.value = memberCard.openType
}

const nextInput = () => {
  nextFlag.value = false
}

const openMem = () => {
  const curMemberValue = curMember.value
  if (memberCards.value.every((v) => v.rankCode <= curMemberValue.rankCode)) {
    uni.showToast({
      title: '您已经是最高等级，无需升级',
      icon: 'none',
      duration: 2000,
    })
    return null
  }
  payTypeFlag.value = true
  return null
}

const paySure = () => {
  const selectOptionValue = selectOption.value
  if ((openType.value === 2 && payType.value.type === 2 && selectOptionValue.actualOpenValue > useAmount.value) || useAmount.value + '' === '0') {
    uni.showToast({
      title: '余额不足',
      icon: 'none',
      duration: 1500,
    })
    return null
  }
  if (
    (openType.value + '' === '1' && payType.value.type + '' === '2' && selectOptionValue.actualOpenValue > accountBalance.value) ||
    accountBalance.value + '' === '0'
  ) {
    uni.showToast({
      title: '积分不足',
      icon: 'none',
      duration: 1500,
    })
    return null
  }
  upgradeMemberRank({
    memberNumber: memberNumber.value,
    openType: openType.value,
    paymentType: openType.value + '' === '1' ? payType.value.type : undefined,
    rankName: selectOptionValue.rankName,
    rankCode: selectOptionValue.rankCode,
    availableAmount: selectOptionValue.actualOpenValue,
    useAmount: useAmount.value,
  })
    .then((res) => {
      if (openType.value === 2 || (openType.value === 1 && payType.value.type === 2)) {
        uni.showToast({
          title: '升级成功',
          icon: 'success',
          duration: 2000,
        })
        uni.navigateTo({
          url: `/subcontract/pages/memberVip/memberVip`,
        })
        return false
      }

      if (payType.value.type === 1) {
        requestPayment(
          res.wxResult,
          () => {
            payTypeFlag.value = false
            openFlag.value = false
            uni.showToast({
              title: '升级成功',
              icon: 'success',
              duration: 2000,
            })
            uni.navigateTo({
              url: `/subcontract/pages/memberVip/memberVip`,
            })
          },
          (e: any) => {
            console.log(e)
            const isCancel = (msg: string) => msg.includes('cancel')
            if (!isCancel(e.errMsg)) {
              uni.showToast({
                title: '升级失败',
                icon: 'none',
                duration: 2000,
              })
            }
          },
        )
      }
      return null
    })
    .catch((err) => {
      uni.showToast({
        title: err || '升级失败',
        icon: 'none',
        duration: 2000,
      })
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

const onShowPayType = () => {
  showPayType.value = true
}

const onPayTypeSelect = (e: any) => {
  const payTypeValue = payType.value
  payTypeValue.type = e.currentTarget.dataset.type
  const payTypeOptions = payTypeValue.options.find((v) => v.type + '' === payTypeValue.type + '') || ({} as any)
  payTypeValue.text = payTypeOptions.text
  showPayType.value = false
  payType.value = payTypeValue
}

const onShowMember = () => {
  showMemberDrop.value = true
}

const getPhoneNumber = (e: any) => {
  console.log(e)
}

const fillPhone = (e: any) => {
  console.log(e)
}

const hideMemberDrop = () => {
  showMemberDrop.value = false
  showPayType.value = false
}

const onPrivilegeTap = (e: any) => {
  const index = e.currentTarget.dataset.index
  privilege.value = privilegeList.value[index]
  privilegeIndex.value = index
}

const onMemberSelect = (e: any) => {
  const memberLevelValue = memberLevel.value as any
  memberLevelValue.rankCode = e.currentTarget.dataset.code
  const selectOptionValue = memberLevelValue.options.find((v: any) => v.rankCode + '' === memberLevelValue.rankCode + '') || {}
  showMemberDrop.value = false
  memberLevel.value = memberLevelValue
  selectOption.value = selectOptionValue
}

// 生命周期
onLoad((options) => {
  uni.showLoading({
    title: '加载中',
  })
  getMemberCardUpgrade({
    memberId: options?.memberId,
  })
    .then((data) => {
      const newMemberCards = (data.memberCardInfoCenterVos || []).filter((v: any) => v.isOpen + '' === '1')
      const newCanUpdateMembers = (newMemberCards || []).filter((v: any) => v.rankCode > data.rankCode)
      // const curMemberCard = memberCards[0];
      // const expriseDate = new Date(curMemberCard.memberExpireTime);
      // const curDate = new Date();
      // const expriseTime = `${expriseDate.getFullYear()}-${expriseDate.getMonth() +
      //   1}-${expriseDate.getDate()}`;
      // const curTime = `${curDate.getFullYear()}-${curDate.getMonth() +
      //   1}-${curDate.getDate()}`;
      const newPrivilegeList = (newMemberCards[0].memberCardPrivileges || []).filter((v: any) => v.isSelected + '' === '1')
      uni.hideLoading()

      avatarUrl.value = data.avatarUrl
      nikeName.value = data.nikeName
      memberCards.value = newMemberCards
      canUpdateMembers.value = newCanUpdateMembers
      memberNumber.value = data.memberNumber
      accountBalance.value = data.accountBalance
      useAmount.value = data.useAmount
      remainDate.value = data.remainDate
      privilegeList.value = newPrivilegeList
      openType.value = newMemberCards[0].openType
      privilege.value = newPrivilegeList[0] || {}
      privilegeIndex.value = 0
      curMember.value = {
        rankName: data.rankName,
        rankCode: data.rankCode,
      }
      memberLevel.value = {
        options: newCanUpdateMembers,
        rankCode: newCanUpdateMembers[0].rankCode,
      }
      selectOption.value = newCanUpdateMembers[0]
    })
    .catch((err) => {
      uni.hideLoading()
      uni.showToast({
        title: err || '获取会员卡失败',
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
    margin-left: 40rpx;
    margin-right: 40rpx;
    padding-bottom: 40rpx;
    padding-top: 40rpx;
    border-bottom: 1rpx solid #f7f5f6;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
}

.pop__conter--phone--text {
  width: 600rpx;
  display: flex;
  align-items: center;
}

.pop__conter--phone--code {
  width: 200rpx;
  height: 50rpx;
  border-radius: 25rpx;
  border: 1rpx solid #e0c286;
  color: #b7b7b7;
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
  width: 700rpx;
  background-color: #fff6e5;
  border-radius: 15rpx;
  padding-bottom: 40rpx;
}

.descConter__top {
  padding-top: 20rpx;
  font-size: 32rpx;
  font-weight: bold;
  background: linear-gradient(to right, #63583a, #ac941f);
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

.imageContainer {
  width: 100%;
  height: 400rpx;
  background-color: #3e3d45;
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

.privilege-img {
  width: 50rpx;
  height: 50rpx;

  &-active {
    width: 60rpx;
    height: 60rpx;
  }
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
  height: 780rpx;
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

.congratulate {
  width: 600rpx;
  height: 650rpx;

  &__text {
    margin-top: 40rpx;
    font-size: 36rpx;
    display: flex;
    justify-content: center;
  }

  &__img {
    margin-top: 40rpx;
    display: flex;
    justify-content: center;
  }
}

.congratulate__sure {
  margin-top: 80rpx;
  display: flex;
  justify-content: center;

  &--btn {
    width: 400rpx;
    height: 60rpx;
    border-radius: 50rpx;
    background: linear-gradient(to right, #ebcc89, #b4893b);
    color: white;
    font-size: 28rpx;
    @include middle-center;
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

.imageContainer__swiperItem__paymount {
  // box-shadow: -4rpx 4rpx 4rpx #888888;
  font-size: 30rpx;
  position: absolute;
  bottom: 40rpx;
  left: 32rpx;
  padding: 12rpx 36rpx;
  border-radius: 80rpx;
  // background: linear-gradient(to right, rgb(202,191,212), rgb(138, 142,145));
  background-color: #fff;
  color: #666;
}

.can__use {
  position: absolute;
  font-size: 24rpx;
  color: #999;
}
</style>
