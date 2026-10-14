<template>
  <view style="width: 100%; height: 100vh; display: flex; flex-direction: column">
    <view>
      <view class="pop__conter">
        <view class="pop__conter--phone">
          <view class="pop__conter--phone--text">
            <view class="pop__conter--phone-label">
              手机号
              <text>*</text>
            </view>
            <uv-input
              v-model="userRegisterForm.phone"
              :maxlength="11"
              :placeholder="phoneForm.tips || '请输入手机号'"
              style="margin-left: 40rpx"
              @blur="onPhoneBlur"
            />
          </view>
          <view :class="'pop__conter--phone--code ' + (formCode.isDisabled && 'phone__code--none')" @tap="onSendCode">
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
              <text>{{ item.isRequired === 1 ? '*' : '' }}</text>
            </view>
            <uv-input v-model="item.value" :placeholder="item.tips" style="margin-left: 40rpx" />
          </view>
        </view>
      </view>
    </view>
    <view style="display: flex; justify-content: center; position: absolute; bottom: 40rpx; width: 100%">
      <view class="pop__next" @tap="onPassMember">保存并开通会员</view>
    </view>
  </view>
</template>
<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getRegisterForm, sendCode, updateRegisterData } from '@/api/modules/member'
import { $Popup, isPhone } from '@/utils/helper'

// 定义接口
interface RegisterFormItem {
  id: number | null
  formId: Long
  value: string
  formName: string
  formPattern: number
  isRequired: number | null
  tips: string
}

// 响应式数据
const options = ref<Obj>({})
const phoneForm = ref<Obj>({})
const memberRegisterFormVos = ref<RegisterFormItem[]>([
  {
    id: null,
    formId: '',
    value: '',
    formName: '',
    formPattern: 1,
    isRequired: null,
    tips: '',
  },
])
const formCode = ref({
  buttonName: '获取验证码',
  isDisabled: false,
  time: 60,
})
const userRegisterForm = ref({
  phone: null as string | null,
  code: null as string | null,
})
const openFlag = ref(false)
const userInfoShow = ref(false)

// 方法定义
const onPassMember = () => {
  const { phone, code } = userRegisterForm.value
  let cusFormPass = true

  if (!phone) {
    $Popup.alert({
      content: '请填写手机号',
    })
    cusFormPass = false
  }

  if (!code) {
    $Popup.alert({
      content: '请填写验证码',
    })
    cusFormPass = false
  }

  memberRegisterFormVos.value.forEach((memberRegisterForm) => {
    if (memberRegisterForm.isRequired === 1) {
      const pat = /^[1-9]\d{5}(18|19|20|(3\d))\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/
      switch (memberRegisterForm.formPattern) {
        // 身份证校验
        case 1:
          if (!pat.test(memberRegisterForm.value)) {
            $Popup.alert({
              content: '身份证格式错误',
            })
            cusFormPass = false
          }
          break
        // 必填项校验
        case 2:
          if (!memberRegisterForm.value) {
            $Popup.alert({
              content: '请输入完整信息',
            })
            cusFormPass = false
          }
          break
        default:
          break
      }
    }
  })

  // 未通过校验 终止表单提交
  if (!cusFormPass) return null

  const formExdDto = memberRegisterFormVos.value.map((memberRegisterForm) => {
    return {
      id: memberRegisterForm.id,
      formId: memberRegisterForm.formId,
      value: memberRegisterForm.value,
      formName: memberRegisterForm.formName,
    }
  })

  const phoneExdDto = {
    id: phoneForm.value.id,
    formId: phoneForm.value.formId,
    value: phone,
    formName: '手机号',
  }

  updateRegisterData({
    phone,
    code,
    formExdDto: [...formExdDto, phoneExdDto],
  })
    .then(() => {
      openFlag.value = true
      userInfoShow.value = false
      uni.navigateTo({
        url: `/subcontract/pages/memberVip/memberVip?cardId=${options.value.cardId}`,
      })
    })
    .catch((res) => {
      $Popup.toast(res)
    })

  return null
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
        formCode.value.time = 60
        if (timer) clearInterval(timer)
      }
    }, 1000)

    if (phone) {
      sendCode({
        phone,
      }).catch((err) => {
        uni.showToast({
          title: err || '发送失败，请重试',
          icon: 'none',
          duration: 2000,
        })
      })
    }
  }
}

// 生命周期
onLoad((optionsTemp) => {
  getRegisterForm(null)
    .then((data) => {
      if (data) {
        // data列表每个增加一个value字段方便绑定(原本没value不方便)
        data.forEach((element: any) => {
          element.value = ''
        })

        phoneForm.value = data.filter((v: any) => v.formPattern + '' === '3')[0]
        memberRegisterFormVos.value = data.filter((v: any) => v.formId !== 1 && v.formName !== '手机号')
      }
    })
    .catch((err) => {
      $Popup.toast(err)
    })
  options.value = optionsTemp ?? {}
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
  width: 700rpx;
  background-color: #fff6e5;
  border-radius: 15rpx;
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
  margin-top: 10rpx;
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
  height: 640rpx;
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
  color: #bbb;
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
