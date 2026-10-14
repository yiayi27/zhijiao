<script setup lang="ts">
import ScanQrCode from '@/components/ScanQrCode/ScanQrCode.vue'
import { RedirectType } from '@/views/sign/type'

// 定义emit
const emit = defineEmits(['change'])

function changeSignType() {
  emit('change', 'SignInByPhone')
}
</script>

<template>
  <div class="SignFlow">
    <div class="SignFlow__tabs">
      <div class="SignFlow__tabs--title">LOG IN</div>
      <div class="SignFlow__tabs--switch" @click="changeSignType">
        <div class="toolTip-right">密码登录</div>
        <i class="iconfont iconhalf_pc" />
      </div>
    </div>
    <div class="SignFlow__QrCode ScanQrCode__container">
      <ScanQrCode redirect-url="/redirect" :scenes="RedirectType.LOGIN" />
    </div>
  </div>
</template>

<style lang="scss" scoped>
@import '@/assets/styles/sign/sign';
</style>
