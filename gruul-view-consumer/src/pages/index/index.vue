<template>
  <view
    class="app_container"
    :style="{
      height: `${screenHeight}px`,
    }"
  >
    <NavigationBar :titleText="indexTitle" background="#ffffff" />
    <view
      v-if="isReady"
      class="container"
      :style="{
        paddingTop: `${navigationBarHeight}px`,
      }"
    >
      <Home v-if="isCurrentTab(currentTab, 'home')" ref="HomeRef" :userid="userId" @stopShow="stopShow" @stopuserid="stopuserid" />
      <Me v-if="isCurrentTab(currentTab, 'me')" ref="MeRef" />
      <Mall v-if="isCurrentTab(currentTab, 'mall')" />
      <ShopCar v-if="isCurrentTab(currentTab, 'shopCar')" :navigationBarHeight="String(navigationBarHeight)" @shopCarChange="shopCarChange" />
      <Integral v-if="isCurrentTab(currentTab, 'integral')" />
      <!-- #ifdef MP-WEIXIN -->
      <Live v-if="isCurrentTab(currentTab, 'live')" />
      <!-- #endif -->
      <Chain v-if="isCurrentTab(currentTab, 'chain')" />
    </view>
    <BTabBar
      v-if="showType && !guideVisible"
      :currentTab="currentTab"
      :isReady="isReady && !guideVisible"
      :tabBar="tabBar"
      :shopCarNum="goodsNumber"
      :shopCarType="1"
      :centerTabbarItem="centerTabbarItem"
      isBlurred
    />
    <Loading v-if="!isReady" />
    <Guide v-if="guideVisible" :guideList="guideList" />
    <Auth v-model:authType="authType" />
  </view>
</template>

<script setup lang="ts">
import Chain from './modules/Chain/Chain.vue'
import NavigationBar from '@/components/NavigationBar/NavigationBar.vue'
import BTabBar from '@/components/BTabBar/BTabBar.vue'
import Loading from '@/components/Loading/Loading.vue'
import Guide from '@/components/Guide/Guide.vue'
import Home from './modules/Home/Home.vue'
import Mall from './modules/Mall/Mall.vue'
import Me from './modules/Me/Me.vue'
import ShopCar from './modules/ShopCar/ShopCar.vue'
// #ifdef MP-WEIXIN
import Live from './modules/Live/Live.vue'
// #endif
import Integral from './modules/Integral/Integral.vue'
import { useMethod } from './composables/useMethod'
import { useSettingStore } from '@/store/modules/setting'
import type { ITabName } from '@/store/modules/setting'
import { useUserStore } from '@/store/modules/user'
import { $Popup } from '@/utils/helper'
import { ref } from 'vue'
import { onLoad, onShow, onShareAppMessage, onShareTimeline, onReachBottom, onPageScroll } from '@dcloudio/uni-app'
import { storeToRefs } from 'pinia'
import useScreenHeight from '@/composables/hooks/useScreenHeight'

const screenHeight = useScreenHeight()
const { isCurrentTab } = useMethod()
const settingStore = useSettingStore()
const { currentTab, isReady, tabBar, centerTabbarItem, guideVisible, guideList, indexTitle, navigationBarHeight } = storeToRefs(settingStore)
const userStore = useUserStore()
const { goodsNumber } = storeToRefs(userStore)

// 基础数据
const authType = ref(false)
const showType = ref(true)
const MeRef = ref<any>(null)
const userId = ref('')
const HomeRef = ref<any>(null)

// 方法定义 - 先定义所有方法再使用
const stopuserid = () => {
  userId.value = ''
}

const stopShow = (e: { type: any }) => {
  showType.value = Boolean(e.type)
}

const resetAssInfo = () => {
  HomeRef.value?.resetInfo()
}

const shopCarChange = () => {
  userStore.getShopCarNum()
}

// uniapp 生命周期
onLoad((options: any) => {
  try {
    if (options.userId) {
      userId.value = options.userId
    }
    if (!settingStore.isReSet && options.page) {
      settingStore.setCurrentTab(options.page as ITabName)
    }
  } catch (err) {
    console.log(err)
    $Popup.toast('首页解析失败')
  }
})

onShareAppMessage(() => {
  return {
    title: settingStore.shopSetting.curShopInfoDto.shopName,
    path: `/pages/index/index?page=home`,
  }
})

onShareTimeline(() => {
  return {
    title: settingStore.shopSetting.curShopInfoDto.shopName,
    path: `/pages/index/index?page=home`,
  }
})

onReachBottom(() => {
  uni.$emit('onReachBottom')
})

onPageScroll((e: any) => {
  HomeRef.value?.PageScroll(e)
})

// onShow生命周期
onShow(async () => {
  /** 有切换自定义页面后 重新获取首页内容  */
  if (HomeRef.value) {
    const homepage = settingStore.pageList.filter((item: { isDef: string }) => {
      return item.isDef === '1'
    })

    HomeRef.value?.reGetPageAssembly(homepage[0].id)
  }
  MeRef.value?.getData()
  uni.$emit('onShow')
  resetAssInfo()
})
</script>

<style lang="scss" scoped>
.app_container {
  .container {
    padding-bottom: 100rpx;
    padding-bottom: calc(100rpx + constant(safe-area-inset-bottom));
    padding-bottom: calc(100rpx + env(safe-area-inset-bottom));
  }
}
</style>
