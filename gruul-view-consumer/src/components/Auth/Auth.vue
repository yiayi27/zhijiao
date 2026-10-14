<template>
  <!-- #ifdef MP-WEIXIN -->
  <view>
    <UPopup v-model:show="authType" mode="bottom" :customStyle="{ height: '400rpx', width: '100%', flex: 'auto' }" @close="cancel">
      <view class="authContent">
        <view class="authContent__top">授权手机号可继续当前操作</view>
        <button open-type="getPhoneNumber" class="authContent__button" @getphonenumber="getphonenumber">
          <MIcon name="icon75" size="44rpx" style="margin-right: 10rpx; margin-top: 4rpx" />授权手机号
        </button>
        <view class="authContent__noContent" @tap="cancel">暂不授权</view>
      </view>
    </UPopup>
  </view>
  <!-- #endif -->
  <!-- #ifndef MP-WEIXIN -->
  <UModal
    v-model="authType"
    show-cancel-button
    :title-style="{ color: 'rgba(16, 16, 16, 1)', fontSize: '36rpx' }"
    confirm-color="#FA3534"
    confirm-text="手机登录"
    :confirm-style="{ borderLeft: '1px solid #f3f3f3' }"
    @cancel="cancel"
    @confirm="handleNavToSign"
  >
    <view class="login-box"> 登录后使用完整功能 </view>
  </UModal>
  <!-- #endif -->
</template>

<script setup lang="ts">
import { nextTick, onMounted } from 'vue'
import { useSettingStore } from '@/store/modules/setting'
import { useUserStore } from '@/store/modules/user'
import { $Popup } from '@/utils/helper'
import MIcon from '@/components/MIcon/MIcon.vue'
import { LOGIN_SUCCESS, LOGIN_OPEN } from '@/utils/emitNameConfig'
import { GrantType } from '@/api/modules/sign/type'
import { onShow, onUnload } from '@dcloudio/uni-app'

// 使用defineModel替代authType prop和update:authType事件
const authType = defineModel<boolean>('authType', { default: false })

// 定义emit
const emit = defineEmits(['cancel'])

/**
 * 处理LOGIN_OPEN事件回调
 * 必须使用单独的回调函数,不然uni.$off会引起移除事件错乱
 * uni.$off(eventName)如果只提供了事件名（eventName），则移除该事件名对应的所有监听器；
 * uni.$off(eventName,callback)如果同时提供了事件与回调，则只移除这个事件回调的监听器
 * @param val 事件值
 */
const handleCallBack = (val: boolean) => {
  authType.value = val
}

// 生命周期钩子
onMounted(() => {
  uni.$on(LOGIN_OPEN, handleCallBack)
})

onUnload(() => {
  uni.$off(LOGIN_OPEN, handleCallBack)
})

// 方法
const cancel = () => {
  uni.$emit(LOGIN_OPEN, false)
}

// #ifdef MP-WEIXIN
const getphonenumber = async (e: any) => {
  uni.$emit(LOGIN_OPEN, false)
  const detail = e.detail
  if (detail.errMsg !== 'getPhoneNumber:ok') {
    //用户决绝授权
    console.log('用户拒绝')
    //拒绝授权后弹出一些提示
    return
  }
  try {
    await useUserStore().login({
      GrantType: GrantType.WECHAT_CODE,
      form: { mobileCode: detail.code },
      type: 1,
    })
    // 微信小程序手机登录成功后，触发AUTH_LOGIN事件
    uni.$emit(LOGIN_SUCCESS)
    emit('cancel')
  } catch {
    $Popup.toast('授权失败')
  }
}
// #endif
// #ifndef MP-WEIXIN
const handleNavToSign = () => {
  let routes = getCurrentPages() as any
  var page = routes[routes.length - 1].$page
  const { fullPath } = page
  uni.navigateTo({
    url: `/pages/sign/SmsAuth?route=${encodeURIComponent(fullPath)}`,
  })
}
// #endif
</script>

<style lang="scss" scoped>
@include b(authContent) {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;

  @include e(top) {
    height: 84rpx;
    width: 100%;
    display: flex;
    justify-content: center;
    padding-top: 40rpx;
    font-size: 30rpx;
  }

  @include e(button) {
    border-radius: 100rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-top: 60rpx;
    width: 80%;
    height: 90rpx;
    background-color: #fb5362;
    color: white;
    font-size: 30rpx;
  }

  @include e(noContent) {
    font-size: 30rpx;
    width: 400rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-top: 40rpx;
  }
}
</style>
