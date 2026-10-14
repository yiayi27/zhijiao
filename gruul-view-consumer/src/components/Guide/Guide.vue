<template>
  <view>
    <swiper indicator-dots class="guide" easing-function="linear">
      <block v-for="(item, index) in guideList" :key="index">
        <swiper-item class="guide__item">
          <uv-image width="100%" height="100%" :src="item.url" @click="execute(item)" />
        </swiper-item>
      </block>
    </swiper>
    <view class="guide__jump" @click="jump">{{ jumpTime }}s | 跳过</view>
  </view>
</template>

<script setup lang="ts">
import { ref, onBeforeMount, onBeforeUnmount } from 'vue'
import { navtofun } from '@/utils/navtofun'
import { useSettingStore } from '@/store/modules/setting'
import { $Popup } from '@/utils/helper'

// 定义接口
interface GuideItem {
  type: number
  url: string
  link?: string
  appId?: string
  [key: string]: any
}

// 定义Props
defineProps({
  guideList: {
    type: Array as () => GuideItem[],
    default: () => [],
  },
})

// 响应式数据
const jumpTime = ref<number>(3)
let IntervalTime: number | NodeJS.Timeout = 0

// 生命周期
onBeforeMount(() => {
  IntervalTime = setInterval(() => {
    if (jumpTime.value > 0) {
      jumpTime.value = jumpTime.value - 1
    } else {
      jump()
    }
  }, 1000)
})

onBeforeUnmount(() => {
  clearInterval(IntervalTime as NodeJS.Timeout)
})

// 方法
const jump = () => {
  clearInterval(IntervalTime as NodeJS.Timeout)
  useSettingStore().setGuideVisible(false)
}

const execute = (item: GuideItem) => {
  if (item.type === 0 && item.link) {
    if (JSON.parse(item.link).type === 0) {
      try {
        const { url, append, name } = JSON.parse(item.link)

        uni.navigateTo({
          url: `/subcontract${(url[0] === '/' ? '' : '/') + url}?page=${append}`,
          success: () => {
            jump()
            useSettingStore().setIndexTitle(name)
          },
        })
      } catch {
        $Popup.toast('跳转失败')
        uni.reLaunch({
          url: '/pages/index/index',
        })
      }
    } else {
      const itemLink = JSON.parse(item.link)
      jump()
      navtofun(itemLink)
    }
  }

  if (item.type === 1 && item.appId) {
    uni.navigateToMiniProgram({
      appId: item.appId,
    })
  }
}
</script>

<style lang="scss" scoped>
@include b(guide) {
  width: 100vw;
  height: 100vh;
  background: #fff;
  position: fixed;
  z-index: 9999;
  top: 0;
  left: 0;

  @include e(item) {
    image {
      width: 100%;
      height: 100%;
    }
  }

  @include e(jump) {
    position: fixed;
    z-index: 10001;
    right: 20rpx;
    top: 190rpx;
    background: rgba($color: #000000, $alpha: 0.5);
    padding: 10rpx 30rpx;
    color: #fff;
    border-radius: 30rpx;
    font-size: 28rpx;
  }
}
</style>
