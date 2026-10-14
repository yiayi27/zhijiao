<template>
  <view class="container">
    <web-view :src="webSrc" @error="onEerror" />
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'

const webSrc = ref('')

onLoad((option: any) => {
  if (option.src) {
    webSrc.value = option.src
  }
})

const onEerror = (e: any) => {
  if (e.src) {
    uni.showToast({
      title: '页面解析失败',
    })
  }
}
</script>

<style lang="scss" scoped>
.container {
  width: 100%;
  height: 100%;
  overflow-x: hidden;
}
</style>
