<template>
  <view>
    <view v-if="memberCards[currentIndex].state === 2" class="expire">你的会员卡已到期，无法享受更多优惠。</view>
    <view class="topBack">
      <view />
      <view>
        <view class="topBack__balance">
          <view>账户余额(元)</view>
          <view @click="checkDetail">余额明细</view>
        </view>
        <view class="topBack__balance">
          <view style="font-size: 70rpx">{{ accountBalance }}</view>
          <view v-if="chargeSwitch === 1" style="line-height: 55rpx" @click="checkRechage">
            <image class="topBack__balance--rechange" :src="ptypeMapIcon[10 as keyof typeof ptypeMapIcon]" />
            充值
          </view>
        </view>
        <swiper
          class="imageContainer"
          circular
          indicator-dots="true"
          indicator-color="#a39f99"
          indicator-active-color="#f49641"
          @change="handleChange"
        >
          <block v-for="(item, index) in memberCards" :key="index">
            <swiper-item class="imageContainer__swiperItem">
              <view
                :style="{ backgroundPosition: 'center', backgroundImage: `url(${item.backGroundPicUrl || defaultImg})` }"
                :class="['topBack__member', item.state === 1 && index === newIndex && 'use_member', item.state === 2 && 'over_member']"
              >
                <view>
                  <view class="topBack__member-name">{{ item.title }}</view>
                  <view class="topBack__member-level">{{ item.rankName }}LV{{ item.rankCode }}</view>
                </view>
                <view style="position: relative">
                  <view class="topBack__member-time" @click="onValidTime">
                    有效期{{ ' ' }}{{ item.memberExpireTime }}>
                    <text>NO.{{ item.memberNumber }}</text>
                  </view>
                  <view
                    v-if="(openType === 1 || openType === 2) && memberCards[currentIndex].isOpen === 1"
                    class="topBack__member-renew"
                    @click="renew"
                  >
                    立即续费
                  </view>
                  <view v-if="openType !== 1 && openType !== 2" class="topBack__member-empty" />
                </view>
              </view>
            </swiper-item>
          </block>
        </swiper>
      </view>
    </view>
    <view
      v-if="(openType === 1 || openType === 2) && memberCards[currentIndex].mostRankCode === 2 && memberCards[currentIndex].state !== 2"
      class="level"
    >
      <image class="level__btn" src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/api/upgrade.jpg" @click="onGoUpdate" />
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
              :class="'privilege-img' + (privilegeIndex === index ? '-active' : '')"
            />
          </view>
          <view style="width: 100%; text-align: center">{{ item.privilegeName }}</view>
        </view>
      </view>
      <view class="privilege__desc">
        <view style="font-weight: bold">{{ privilege.privilegeName }}</view>
        <view v-if="privilege.ptype === '1'" style="margin-top: 20rpx">
          {{ `${privilege?.illustrate ?? ''}${privilege.value ? `(折扣${privilege.value / 10}折)` : ''}` }}
        </view>
        <view v-if="privilege.ptype === '3'" style="margin-top: 20rpx">
          {{ privilege.illustrate ? (privilege.value ? privilege.illustrate + '(加' + privilege.value + '倍)' : '') : '暂无特殊优待' }}
        </view>
        <view v-if="privilege.ptype !== '3' && privilege.ptype !== '1'" style="margin-top: 20rpx">
          {{ privilege.illustrate || '暂无特殊优待' }}
        </view>
      </view>
      <UPopup v-model:show="payTypeFlag" mode="bottom" @close="onClose">
        <view class="payConter" @click="hidePayDrop">
          <view class="payConter__top">
            <uv-icon name="arrow-left" style="margin-right: 40rpx" @click="onClose" />
            会员续费
          </view>
          <view class="payConter__openingMode">
            <view>会员卡名称</view>
            <view>{{ memberCards[currentIndex].title }}</view>
          </view>
          <view class="payConter__openingMode">
            <view>会员等级</view>
            <view>{{ memberCards[currentIndex].rankName }}</view>
          </view>
          <view class="payConter__openingMode">
            <view>剩余时间</view>
            <view>{{ memberCards[currentIndex].remainDate }}天</view>
          </view>
          <view v-if="openType === 1" class="payConter__openingMode">
            <view>价格</view>
            <view> ¥{{ memberCards[currentIndex].openValue }}／{{ memberCards[currentIndex].useInDate }}天 </view>
          </view>
          <view v-if="openType === 2" class="payConter__openingMode">
            <view>积分</view>
            <view> {{ memberCards[currentIndex].openValue }}积分／{{ memberCards[currentIndex].useInDate }}天 </view>
          </view>
          <view class="payConter__openingMode">
            <view>续费后时长</view>
            <view>{{ allDate || 365 }}天</view>
          </view>
          <view v-if="openType === 1" class="payConter__openingMode">
            <view>支付方式</view>
            <view style="position: relative" @click.stop="onShowPay">
              {{ payLabel }}
              <uv-icon name="arrow-down" style="margin-left: 10rpx" />
              <view v-if="showPayDrop" class="custom__drop--down">
                <view
                  v-for="(item, index) in payLevel.options"
                  :key="index"
                  :class="['member-item', item.id === payLevel.id && 'member-item-active']"
                  :data-id="item.id"
                  @click.stop="onPaySelect"
                >
                  {{ item.label }}
                </view>
              </view>
            </view>
          </view>
          <view v-if="openType === 2" class="payConter__openingMode">
            <view>支付方式</view>
            <view>账户积分</view>
          </view>
          <view v-if="openType === 1" style="display: flex; justify-content: center">
            <text v-if="payLevel.id === 2" class="can__use">当前可用余额{{ accountUseBalance }}</text>
            <view
              :class="['payConter__btn', memberCards[currentIndex].openValue > accountUseBalance && payLevel.id === 2 && 'payConter__btn-no']"
              @click="paySure"
            >
              {{ memberCards[currentIndex].openValue > accountUseBalance && payLevel.id === 2 ? '余额不足' : '确认付款' }}
            </view>
          </view>
          <view v-if="openType === 2" style="display: flex; justify-content: center">
            <text class="can__use">当前可用积分{{ useAmount }}</text>
            <view :class="['payConter__btn', memberCards[currentIndex].openValue > useAmount && 'payConter__btn-no']" @click="paySure">
              {{ memberCards[currentIndex].openValue > useAmount ? '积分不足' : '确认兑换' }}
            </view>
          </view>
        </view>
      </UPopup>
      <UPopup v-model:show="showValidTime" :customStyle="{ top: '40%' }" @close="onClose">
        <view class="descConter">
          <view class="descConter__top">你正在享受笔歌会员卡会员权益</view>
          <view class="descConter__conter">
            <view class="descConter__conter__title" style="margin-bottom: 30rpx">有效期计算方法</view>
            <view class="descConter__conter_con">
              如同时拥有多种不同身份的会员卡，会依次消耗最高等级会员卡至最低等级会员卡的有效天数，享受对应的权益。
            </view>
            <view class="descConter__conter_con">
              在购买/兑换会员卡后，若账号内仍有剩余未过期的会员，将会先立即生效最高等级会员权益，剩余的会员到期时间将顺延，待高等级会员过期时继续依次生效。
            </view>
            <view class="descConter__conter_con">购买/兑换的会员卡与累计消费和购买指定商品获得的会员卡不能同时享用</view>
          </view>
          <view class="descConter__conter_bottom">
            <view @click="onClose">知道了</view>
            <view @click="onGoLogs">会员卡记录</view>
          </view>
        </view>
      </UPopup>
    </view>
    <UPopup v-model:show="openFlag" :customStyle="{ top: '40%' }" round="25" @close="onClose">
      <view class="congratulate">
        <view class="congratulate__text">恭喜!</view>
        <view class="congratulate__text" style="margin-bottom: 60rpx"> 您的{{ newCardInfo.rankName }}开通成功 </view>
        <view class="congratulate__img" style="display: none">
          <image :src="newCardInfo.backGroundPicUrl || defaultImg" style="width: 400rpx; height: 200rpx" />
        </view>
        <view
          :style="{ backgroundSize: '100% 280rpx', background: 'url(' + (newCardInfo.backGroundPicUrl || defaultImg) + ')' }"
          class="topBack__member"
        >
          <view>
            <view class="topBack__member-name-modal">{{ newCardInfo.title }}</view>
            <view class="topBack__member-level">{{ newCardInfo.rankName }}</view>
          </view>
          <view style="position: relative; height: 80rpx">
            <view class="topBack__member-time-modal" @click="onValidTime">
              有效期{{ ' ' }}{{ newCardInfo.memberExpireTime }}
              <text>NO.{{ newCardInfo.memberNumber }}</text>
            </view>
            <view class="topBack__member-empty-modal" />
          </view>
        </view>
        <view class="congratulate__sure">
          <view class="congratulate__sure--btn" @click="onClose">确 定</view>
        </view>
      </view>
    </UPopup>
  </view>
</template>
<script setup lang="ts">
import { ref } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import { memberCenter, updateMemberExpireTime } from '@/api/modules/member'
import { ptypeMapIcon } from '@/utils/const'
import { requestPayment } from '@/utils/requestPayment'

// 响应式数据
const privilegeList = ref<any[]>([])
const privilegeIndex = ref(0)
const openType = ref(1)
const openFlag = ref(false)
const currentIndex = ref(0)
const privilege = ref<Obj>({})
const defaultImg = ref('https://images.unsplash.com/photo-1551446591-142875a901a1?w=640')
const payLabel = ref('微信支付')
const showPayDrop = ref(false)
const payTypeFlag = ref(false)
const showValidTime = ref(false)
const accountBalance = ref(0)
const accountUseBalance = ref(0)
const useAmount = ref(0)
const memberCards = ref<any[]>([])
const leftDate = ref(0)
const payLevel = ref({
  options: [
    {
      id: 1,
      label: '微信付款',
    },
    {
      id: 2,
      label: '余额支付',
    },
  ],
  id: 1,
})
const params = ref<Obj>({})
const newCardInfo = ref<Obj>({})
const chargeSwitch = ref<number>(0)
const newIndex = ref(0)
const allDate = ref<any>('')

// 方法定义
const fetchMemberCenter = () => {
  uni.showLoading({
    title: '加载中',
  })
  memberCenter(null)
    .then((data) => {
      let privilegeTemp: any = null
      let newCardIndex = 0
      const newMemberCards = data.memberCardInfoCenterVos || []
      const curMemberCard = newMemberCards[0]
      const newPrivilegeList = (newMemberCards[0].memberCardPrivileges || []).filter((v: any) => v.isSelected + '' === '1')
      const curPri = newMemberCards[0].memberCardPrivileges
      for (let i = 0; i < curPri.length; i++) {
        if (curPri[i].isSelected !== 0) {
          privilegeTemp = curPri[i]
          break
        }
      }

      newMemberCards.forEach((el: any, i: number) => {
        if (i === 0) return
        if (new Date(newMemberCards[newCardIndex].memberRegisterTime) < new Date(el.memberRegisterTime)) {
          newCardIndex = i
        }
      })

      uni.hideLoading()

      memberCards.value = newMemberCards
      newCardInfo.value = params.value.cardId ? newMemberCards.find((v: any) => v.memberCardInfoId + '' === params.value.cardId + '') : {}
      accountBalance.value = data.accountBalance
      accountUseBalance.value = data.accountUseBalance
      privilegeList.value = newPrivilegeList
      privilege.value = privilegeTemp || {}
      privilegeIndex.value = 0
      leftDate.value = parseInt(curMemberCard.remainDate)
      useAmount.value = data.useAmount
      openType.value = data.openType
      chargeSwitch.value = data.state
      newIndex.value = newCardIndex
    })
    .catch((err) => {
      uni.hideLoading()
      uni.showToast({
        title: err || '获取会员失败',
        icon: 'none',
        duration: 2000,
      })
    })
}

const checkDetail = () => {
  uni.navigateTo({
    url: '/subcontract/pages/balanceDetail/balanceDetail',
  })
}

const checkRechage = () => {
  uni.navigateTo({
    url: '/subcontract/pages/rechage/rechage',
  })
}

const onShowPay = () => {
  showPayDrop.value = true
}

const hidePayDrop = () => {
  showPayDrop.value = false
}

const paySure = () => {
  const memberCard: any = memberCards.value[currentIndex.value]
  if (openType.value === 1 && memberCard.openValue > accountUseBalance.value && payLevel.value.id === 2) {
    uni.showToast({
      title: '积分不足',
      icon: 'none',
      duration: 1500,
    })
    return null
  }
  if (openType.value === 2 && memberCard.openValue > useAmount.value) {
    uni.showToast({
      title: '余额不足',
      icon: 'none',
      duration: 1500,
    })
    return null
  }
  updateMemberExpireTime({
    availableAmount: memberCard.openValue,
    memberNumber: memberCard.memberNumber,
    openType: openType.value,
    paymentType: openType.value === 1 ? payLevel.value.id : undefined,
    rankCode: memberCard.rankCode,
    rankName: memberCard.rankName,
    useAmount: openType.value === 1 ? accountUseBalance.value : useAmount.value,
    memberExpireTime: memberCard.memberExpireTime,
    date: memberCard.useInDate,
  })
    .then((res) => {
      if (openType.value === 2 || (openType.value === 1 && payLevel.value.id === 2)) {
        onClose()
        fetchMemberCenter()
        uni.showToast({
          title: '续费成功',
          icon: 'success',
          duration: 2000,
        })
        return false
      }

      if (payLevel.value.id === 1) {
        requestPayment(
          res.wxResult,
          () => {
            payTypeFlag.value = false
            fetchMemberCenter()
            uni.showToast({
              title: '续费成功',
              icon: 'success',
              duration: 2000,
            })
            onClose()
            fetchMemberCenter()
          },
          (e: any) => {
            console.log(e)
            const isCancel = (msg: string) => msg.includes('cancel')
            if (!isCancel(e.errMsg)) {
              uni.showToast({
                title: '续费失败',
                icon: 'none',
                duration: 2000,
              })
            }
          },
        )
      }
      return false
    })
    .catch((err) => {
      uni.showToast({
        title: err || '续费失败',
        icon: 'none',
        duration: 2000,
      })
    })
  return null
}

const onClose = () => {
  payTypeFlag.value = false
  showValidTime.value = false
  openFlag.value = false
}

const onPaySelect = (e: any) => {
  const currentPayLevel = payLevel.value
  currentPayLevel.id = e.currentTarget.dataset.id
  showPayDrop.value = false
  const selectedOption = currentPayLevel.options.find((v) => v.id === currentPayLevel.id)
  payLabel.value = selectedOption?.label || '微信支付'
}

const renew = () => {
  const index = currentIndex.value
  const memberCard: any = memberCards.value[index]
  payTypeFlag.value = true
  allDate.value = leftDate.value + parseInt(memberCard.useInDate)
}

const onValidTime = () => {
  showValidTime.value = true
}

const onGoLogs = () => {
  uni.navigateTo({
    url: '/subcontract/pages/memberCardLog/memberCardLog',
  })
}

const onGoUpdate = () => {
  const curMemberCards = memberCards.value
  const curIndex = currentIndex.value
  const memberCard: any = curMemberCards[curIndex]
  uni.navigateTo({
    url: `/subcontract/pages/memberUpdate/memberUpdate?memberId=${memberCard.id}`,
  })
}

const handleChange = (e: any) => {
  const memberCard: any = memberCards.value[e.detail.current]
  const curMemberCard: any = memberCards.value[e.detail.current]
  const newPrivilegeList = (memberCard.memberCardPrivileges || []).filter((v: any) => v.isSelected + '' === '1')
  currentIndex.value = e.detail.current
  privilegeList.value = newPrivilegeList
  privilege.value = newPrivilegeList[0] || {}
  privilegeIndex.value = 0
  leftDate.value = parseInt(curMemberCard.remainDate)
}

const onPrivilegeTap = (e: any) => {
  const index = e.currentTarget.dataset.index
  privilege.value = privilegeList.value[index]
  privilegeIndex.value = index
}

// 生命周期
onLoad((options) => {
  if (options) {
    openFlag.value = options.cardId ? true : false
    params.value = options
  }
})

onShow(() => {
  fetchMemberCenter()
})
</script>
<style lang="scss" scoped>
@mixin middle-center {
  display: flex;
  justify-content: center;
  align-items: center;
}

.topBack {
  position: relative;
  background-color: #3e3d45;
  height: 550rpx;
  overflow: hidden;
  > view {
    position: absolute;
    width: 100%;
    height: 100%;
    &:first-child {
      width: 200%;
      height: 200%;
      background-color: #fff;
      border-radius: 200%;
      top: 65%;
      left: -50%;
    }
  }
}

.topBack__balance {
  padding-top: 40rpx;
  padding-left: 40rpx;
  padding-right: 40rpx;
  padding-bottom: 20rpx;
  display: flex;
  justify-content: space-between;
  font-size: 30rpx;
  color: white;
  align-items: center;
}

.level {
  margin-top: 50rpx;
  height: 130rpx;
  width: 100%;
  // background-color: #E8E8E8;
  // display: flex;
  // justify-content: center;
  // align-items: center;
}

.level__btn {
  // width: 700rpx;
  // height: 100rpx;
  // background-color: #F0D58E;
  // border-radius: 50rpx;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
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

.privilege-img {
  width: 50rpx;
  height: 50rpx;
  &-active {
    width: 60rpx;
    height: 60rpx;
  }
}

.payConter {
  height: 820rpx;
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

.expire {
  line-height: 80rpx;
  padding: 0 30rpx;
  background-color: rgba(242, 216, 171, 1);
  font-size: 30rpx;
}

.topBack__member-level {
  margin-top: 8rpx;
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
  background-color: #fff;
  color: rgb(202, 191, 212);
  // background: linear-gradient(to right, rgb(202,191,212), rgb(138, 142,145));
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

.topBack__member-level-modal {
  margin-top: 18rpx;
  padding: 6rpx 12rpx;
  background-color: #fff;
  font-size: 26rpx;
  color: #aaa;
  border-radius: 50rpx;
  display: inline-block;
}

.topBack__member-renew-modal {
  float: right;
  padding: 10rpx 30rpx;
  border-radius: 50rpx;
  background: linear-gradient(to right, rgb(202, 191, 212), rgb(138, 142, 145));
}

.topBack__member-empty-modal {
  float: right;
  padding: 10rpx 30rpx;
  height: 60rpx;
}

.custom__drop--down {
  position: absolute;
  width: 180rpx;
  background: #fff;
  box-shadow: 0 1px 6rpx rgba(0, 0, 0, 0.3);
  padding: 20rpx;
  right: 0;
  z-index: 1000;
}

.member-item {
  margin: 15rpx 0;
  &-active {
    color: #e1b56a;
  }
}

.descConter {
  width: 600rpx;
  background-color: #fff;
  border-radius: 15rpx;
}

.descConter__top {
  padding: 40rpx 0;
  margin: 40rpx;
  border-bottom: 1rpx solid #bbb;
  font-size: 30rpx;
  // font-weight: bold;
  // background: linear-gradient(to right, #63583A, #AC941F);
  // background-clip: text;
  // color: transparent;
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

.descConter__conter_con {
  margin-bottom: 30rpx;
}

.descConter__conter_bottom {
  display: flex;
  font-size: 30rpx;
  line-height: 90rpx;
  border-top: 1rpx solid #999;
  > view {
    flex: 1;
    text-align: center;
    &:last-child {
      color: rgba(235, 200, 98, 1);
      border-left: 1rpx solid #999;
    }
  }
}

.topBack__member {
  // width: 90%;
  // margin-left: 5%;
  height: 280rpx;
  border-radius: 15rpx;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 30rpx;
  background-size: 100% 100%;
  color: white;
  font-size: 30rpx;
  margin: 0 30rpx;
}

.imageContainer {
  position: absolute;
  bottom: 0rpx;
  height: 280rpx;
  width: 100%;
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

.can__use {
  position: absolute;
  font-size: 24rpx;
  color: #999;
}

.congratulate {
  width: 600rpx;
  height: 760rpx;

  &__text {
    margin-top: 30rpx;
    font-size: 34rpx;
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
  padding: 0 30rpx;

  &--btn {
    width: 100%;
    height: 70rpx;
    border-radius: 50rpx;
    background: linear-gradient(to right, #ebcc89, #b4893b);
    color: white;
    font-size: 28rpx;
    @include middle-center;
  }
}

.over_member {
  position: relative;
  &::after {
    content: '已到期';
    width: 110rpx;
    height: 80rpx;
    display: block;
    position: absolute;
    right: 0;
    top: 0;
    background-color: #fff;
    color: rgba($color: #000000, $alpha: 0.8);
    text-align: right;
    line-height: 70rpx;
    border-radius: 0 0 0 100rpx;
    font-size: 28rpx;
  }
}

.use_member {
  position: relative;
  &::after {
    content: '使用中';
    width: 110rpx;
    height: 80rpx;
    display: block;
    position: absolute;
    right: 0;
    top: 0;
    background-color: #fff;
    color: rgba($color: #000000, $alpha: 0.8);
    text-align: right;
    line-height: 70rpx;
    border-radius: 0 0 0 100rpx;
    font-size: 28rpx;
  }
}

.topBack__balance--rechange {
  width: 40rpx;
  height: 40rpx;
  vertical-align: middle;
}
</style>
