<script setup lang="ts">
import { GrantType, GrantTypes, SmsType } from '@/api/modules/sign/type'
import { isPhone } from '@/utils/helper'
import { ref } from 'vue'
import QCountDown from '@/components/QCountDown/QCountDown.vue'
import { doPostSmsCode } from '@/api/modules/sign'
import { onLoad } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/modules/user'
import SliderCaptcha from '@/components/SlideCaptcha/SliderCaptcha.vue'
import useScreenHeight from '@/composables/hooks/useScreenHeight'

const screenHeight = useScreenHeight()

const tel = ref('')
const code = ref('')
const inputRef = ref()
const hasCountDown = ref(false)
const countDown = ref(66000)
const countDownRef = ref()
const showSliderCaptcha = ref(false)

let route = '/'
onLoad((res) => {
  if (res) {
    route = res.route ? res.route : route
    console.log('decodeURIComponent(route)', decodeURIComponent(route))
  }
})

const getCodeHandle = async () => {
  if (!isPhone(tel.value)) {
    uni.showToast({
      title: '手机号有误',
      icon: 'none',
    })
    return
  }
  if (GrantTypes[GrantType.SMS_CODE].encrypt) {
    showSliderCaptcha.value = true
  } else {
    hasCountDown.value = false
    const res = await doPostSmsCode({ phone: tel.value, type: SmsType.LOGIN })
    code.value = res
  }
}
const countFinishHandle = () => {
  hasCountDown.value = false
  countDownRef.value.reset()
}
const signHandle = async () => {
  if (!code.value) {
    uni.showToast({
      icon: 'none',
      title: '请填写验证码',
    })
    return
  }

  try {
    await useUserStore().login({
      GrantType: GrantType.SMS_CODE,
      form: {
        mobile: tel.value,
        code: code.value,
      },
      type: 1,
    })
    uni.redirectTo({ url: decodeURIComponent(route) })
  } catch (e) {
    console.log(e)
    uni.showToast({
      title: '登录失败,请重试',
      icon: 'none',
    })
  }
}

const success = (e: string) => {
  hasCountDown.value = true
  countDownRef.value.start()
  showSliderCaptcha.value = false
  code.value = e
}
</script>

<template>
  <view
    class="login-container"
    :style="{
      height: `${screenHeight}px`,
    }"
  >
    <view class="login-card">
      <view class="login-header">
        <view class="login-title">账号登录</view>
        <view class="login-subtitle">安全、快捷的手机号登录</view>
      </view>

      <view class="login-form">
        <view class="form-item">
          <view class="form-item-input">
            <uv-input v-model="tel" type="number" font-size="14" placeholder="请输入手机号" maxlength="11" border="none"></uv-input>
          </view>
        </view>

        <view class="form-item">
          <view class="form-item-input">
            <uv-input ref="inputRef" v-model="code" type="number" font-size="14" placeholder="请输入验证码" maxlength="6" border="none"></uv-input>
          </view>
          <view v-show="!hasCountDown" class="code-btn" @click="getCodeHandle">获取验证码</view>
          <view v-show="hasCountDown" class="code-countdown">
            <QCountDown ref="countDownRef" :timestamp="countDown" format="mm:ss" @end="countFinishHandle" />
          </view>
        </view>

        <view class="form-submit">
          <uv-button
            class="submit-btn"
            throttleTime="300"
            color="rgba(255, 255, 255, 0.3)"
            type="primary"
            shape="circle"
            size="large"
            @click="signHandle"
            >立即登录</uv-button
          >
        </view>
      </view>

      <view class="login-footer">
        <view class="footer-text">登录即表示您同意《用户协议》和《隐私政策》</view>
      </view>
    </view>

    <SliderCaptcha
      v-model="showSliderCaptcha"
      :getForm="() => tel"
      :doSubmit="(request) => doPostSmsCode(request.form)"
      :scale="2"
      @success="success"
      @focus="() => inputRef.onFocus()"
    />
  </view>
</template>

<style lang="scss" scoped>
.login-container {
  position: relative;
  width: 100%;
  background: linear-gradient(to bottom, #5fdaff, #7b8cfe, #815eff, #66d4ff);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  overflow: hidden;
}

.login-card {
  width: 90%;
  max-width: 650rpx;
  padding: 60rpx 40rpx;
  border-radius: 16rpx;
  background-color: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(30rpx);
  -webkit-backdrop-filter: blur(30rpx);
  box-shadow: 0 8rpx 32rpx rgba(255, 102, 102, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.3);
  display: flex;
  flex-direction: column;
  z-index: 10;
}

.login-header {
  margin-bottom: 60rpx;
  text-align: center;

  .login-title {
    font-size: 42rpx;
    font-weight: 500;
    color: white;
    margin-bottom: 20rpx;
  }

  .login-subtitle {
    font-size: 26rpx;
    color: rgba(255, 255, 255, 0.8);
  }
}

.login-form {
  width: 100%;

  .form-item {
    display: flex;
    align-items: center;
    margin-bottom: 30rpx;
    background-color: rgba(255, 255, 255, 0.2);
    border-radius: 40rpx;
    height: 90rpx;
    padding: 0 10rpx 0 30rpx;
    border: 1px solid rgba(255, 255, 255, 0.3);

    .form-item-input {
      flex: 1;
    }

    .code-btn {
      padding: 14rpx 30rpx;
      font-size: 24rpx;
      background-color: white;
      color: #635eff;
      border-radius: 30rpx;
      margin-left: 10rpx;
      font-weight: 500;
    }

    .code-countdown {
      font-size: 24rpx;
      color: white;
      padding: 0 20rpx;
    }
  }

  .form-submit {
    margin-top: 50rpx;

    .submit-btn {
      height: 90rpx;
      background-color: rgba(255, 255, 255, 0.3);
      border-radius: 200rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      font-size: 30rpx;
      font-weight: 500;
      letter-spacing: 5rpx;
      border: 1px solid rgba(255, 255, 255, 0.5);
    }
  }
}

.login-footer {
  margin-top: 60rpx;
  text-align: center;

  .footer-text {
    font-size: 22rpx;
    color: rgba(255, 255, 255, 0.8);
  }
}

/* 修改输入框文字颜色 */
:deep(.uni-input-input),
:deep(.uni-input-placeholder) {
  color: white !important;
}
:deep(.uni-input-placeholder) {
  color: rgba(255, 255, 255, 0.7) !important;
}
</style>
