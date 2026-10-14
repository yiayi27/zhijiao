<template>
  <view class="code">
    <image :src="codeImage" class="image" />
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { userIdGet } from '@/api/modules/user'
import { getImgCode } from '@/api/modules/goods'

const codeImage = ref('')

userIdGet({}).then((id) => {
  const wxaGetwxacode = {
    path: `pages/index/index?userId=${id}`,
    width: 0,
  }

  getImgCode(wxaGetwxacode).then((res) => {
    codeImage.value = res
  })
})
</script>

<style lang="scss" scoped>
.code {
  background-image: url('https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20211110/70436e0bdb7d43dda409a6ead4e63d9f.png');
  background-repeat: no-repeat;
  background-size: 100% auto;
  width: 100%;
  height: 1334rpx;
  position: relative;
}
.image {
  position: absolute;
  top: 983rpx;
  left: 104rpx;
  width: 222rpx;
  height: 222rpx;
}
</style>
