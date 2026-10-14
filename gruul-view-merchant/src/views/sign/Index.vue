<script setup lang="ts">
import { getSystemConfig } from '@/api/sign'
import config from '@/config'
import SignInByPhone from '@/views/sign/components/SignInByPhone.vue'
import SignInByQrCode from '@/views/sign/components/SignInByQrCode.vue'

import { useRoute } from 'vue-router'

defineOptions({
  name: 'SignIndex',
})

const route = useRoute()
const currenType = ref('SignInByPhone')

onMounted(() => {
  if (route.query.type) {
    changeLoginType(route.query.type as string)
  }
})

function changeLoginType(type: string) {
  currenType.value = type
}

async function linkTo() {
  const response = await getSystemConfig()
  const { code, data } = response
  if (code === 200) {
    if (data.systemConfig && data.systemConfig.consoleLog) {
      window.open(`${data.systemConfig.consoleLog}`, '_top')
    }
  }
}

function goPolice() {
  window.open(`http://www.beian.gov.cn/portal/registerSystemInfo?recordcode=33020502000496`)
}
</script>

<template>
  <div class="SignFlowHomepage">
    <img :src="config.logo" class="SignFlowHomepage__logo pointer" @click="linkTo" />
    <div class="SignFlowHomepage__content">
      <div class="Card SignContainer">
        <img class="SignContainer__img" :src="config.loginImage" />
        <div class="SignContainer__content">
          <SignInByPhone v-if="currenType === 'SignInByPhone'" @change="changeLoginType" />
          <SignInByQrCode v-if="currenType === 'SignInByQrCode'" @change="changeLoginType" />
        </div>
      </div>
    </div>

    <footer class="SignFlowHomepage__footer">
      <p class="medusa__rights">Copyright © 2020 启山智软. All Rights Reserved 宁波启山科技有限公司版权所有</p>
      <p class="flex justify-center items-center">
        <img style="width: 14px; height: 14px" src="https://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/gruul/20200321/gongan.jpg" />
        <span class="pointer" @click="goPolice">浙公网安备33020502000496号</span>
      </p>
    </footer>
  </div>
</template>

<style lang="scss" scoped>
@import '@/assets/styles/sign/sign';
</style>
