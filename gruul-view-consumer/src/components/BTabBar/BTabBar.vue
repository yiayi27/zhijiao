<template>
  <view class="tabbar_container">
    <!-- tabbar默认模式 -->
    <view v-if="tabBar.mode === 'default'" class="tab--bar" :style="{ background: isBlurred ? 'transparent' : tabBar.background }">
      <!-- 毛玻璃效果层 -->
      <view v-if="isBlurred" class="tab--bar__blur"></view>
      <view v-if="isReady" class="tab--bar__border" />
      <view v-for="(item, index) in tabBar.list" :key="index" class="tab--bar__item" style="position: relative" @click="switchTab(item)">
        <image v-if="item.selectedIconPath" :src="currentTab === item.name && currentPageId === item.id ? item.selectedIconPath : item.iconPath" />
        <view :style="{ color: currentTab === item.name && currentPageId === item.id ? tabBar.selectColor : tabBar.defaultColor }">
          {{ item.text }}
        </view>
        <view v-if="item.name === 'shopCar' && shopCarNum! > 0" class="number">
          {{ shopCarNum }}
        </view>
      </view>
    </view>
    <!-- tabbar中按钮模式 -->
    <block v-if="tabBar.mode === 'center'">
      <!-- 毛玻璃效果层 -->
      <view v-if="isBlurred" class="tab--bar__blur"></view>
      <view v-if="centerTabbarItem" class="tab--bar__center" :class="{ 'tab--bar__center--blurred': isBlurred }" @click="switchTab(centerTabbarItem)">
        <image
          class="center_img"
          :src="
            currentTab === centerTabbarItem.name && currentPageId === centerTabbarItem.id
              ? centerTabbarItem.selectedIconPath
              : centerTabbarItem.iconPath
          "
        />
        <view
          :style="{ color: currentTab === centerTabbarItem.name && currentPageId === centerTabbarItem.id ? tabBar.selectColor : tabBar.defaultColor }"
        >
          {{ centerTabbarItem.text }}
        </view>
      </view>
      <view class="tab--bar" :style="{ background: isBlurred ? 'transparent' : tabBar.background }">
        <view class="tab--bar__border" />
        <view v-for="(item, index) in tabBar.list" :key="index" class="tab--bar__item" @click="switchTab(item)">
          <block v-if="index !== centerTabbarItem.centerNum">
            <image
              v-if="item.selectedIconPath"
              :src="currentTab === item.name && currentPageId === item.id ? item.selectedIconPath : item.iconPath"
            />
            <view :style="{ color: currentTab === item.name && currentPageId === item.id ? tabBar.selectColor : tabBar.defaultColor }">
              {{ item.text }}
            </view>
          </block>
          <block v-else>
            <view />
          </block>
        </view>
      </view>
    </block>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useSettingStore } from '@/store/modules/setting'
import { navtofun } from '@/utils/navtofun'

defineOptions({
  options: {
    virtualHost: true,
  },
})
// 定义Props
defineProps({
  currentTab: {
    type: String,
    default: 'home',
  },
  isReady: {
    type: Boolean,
    default: false,
  },
  tabBar: {
    type: Object,
    default: () => ({}),
  },
  shopCarNum: {
    type: Number,
    default: 0,
  },
  shopCarType: {
    type: Number,
    default: 0,
  },
  centerTabbarItem: {
    type: Object,
    default: () => ({}),
  },
  isBlurred: {
    type: Boolean,
    default: false,
  },
})

// 响应式数据
const currentPageId = ref<string | number>('')

// 生命周期
onMounted(() => {
  currentPageId.value = useSettingStore().currentPageId
  uni.$on('onTap', () => {
    currentPageId.value = useSettingStore().currentPageId
  })
})

onUnmounted(() => {
  // 移除事件监听
  uni.$off('onTap')
})

// 方法
const initPageId = () => {
  if (!currentPageId.value) {
    const time = setInterval(() => {
      if (currentPageId.value) {
        clearInterval(time)
      }
      currentPageId.value = useSettingStore().currentPageId
    }, 100)
  }
}

initPageId()

const switchTab = (item: any) => {
  console.log('切换tabbar:', item)
  currentPageId.value = item.id
  item.append = item.name
  item.url = item.linkUrl
  if (!item.name) {
    item.url = `${item.url}?btnShow=true`
    uni.reLaunch({
      url: item.url,
    })

    return
  }
  navtofun(item)
  if (item.type === 5 && item.sortIndex === 0) {
    useSettingStore().setHistoryPage([])
    useSettingStore().setCurrentTab('home')
  }

  uni.pageScrollTo({
    scrollTop: 0,
    duration: 0,
  })

  useSettingStore().setIndexTitle(item.linkName)
  if (item.type === 5 && item.sortIndex === 0) {
    useSettingStore().setIndexTitle(useSettingStore().shopSetting.curShopInfoDto?.shopName ?? '')
  }
}
</script>

<style lang="scss" scoped>
$border-color: rgba(0, 0, 0, 0.33);

.number {
  position: absolute;
  top: 12rpx;
  right: 1;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  min-width: 32rpx;
  padding: 0 6rpx;
  color: #fff;
  font-weight: 500;
  font-family:
    PingFang SC,
    Helvetica Neue,
    Arial,
    sans-serif;
  line-height: 30rpx;
  text-align: center;
  background-color: #ee0a24;
  border: 2rpx solid #fff;
  border-radius: 32rpx;
  -webkit-transform: translate(50%, -50%);
  transform: translate(50%, -50%);
  -webkit-transform-origin: 100%;
  transform-origin: 100%;
  padding-top: 2rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}
.tabbar_container {
  position: fixed;
  width: 100%;
  bottom: 0;
  left: 0;
  z-index: 98;
  background: transparent;
}
.tab--bar {
  width: 100%;
  height: 100rpx;
  background: #ffffff;
  display: flex;
  padding-bottom: constant(safe-area-inset-bottom);
  padding-bottom: env(safe-area-inset-bottom);
  box-sizing: content-box;
  position: relative;
  // border-top: 1px solid #f8f8f8;

  &__blur {
    position: absolute;
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
    backdrop-filter: blur(1px);
    -webkit-backdrop-filter: blur(10px);
    background-color: rgba(255, 255, 255, 0.95);
    z-index: -1;
  }

  &__border {
    position: absolute;
    left: 0;
    top: 0;
    width: 100%;
    height: 2rpx;
    transform: scaleY(0.5);
  }
  &__item {
    flex: 1;
    text-align: center;
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;
    position: relative;
    z-index: 2;
    image {
      width: 40rpx;
      height: 40rpx;
    }
    view {
      font-size: 20rpx;
      margin-top: 10rpx;
    }
  }

  &__center {
    width: 100rpx;
    height: 125rpx;
    padding-bottom: constant(safe-area-inset-bottom);
    padding-bottom: env(safe-area-inset-bottom);
    display: flex;
    justify-content: center;
    flex-direction: column;
    align-items: center;
    position: fixed;
    z-index: 99;
    bottom: 0;
    border-radius: 50rpx 50rpx 0 0;
    left: calc(50% - 50rpx);
    text-align: center;
    font-size: 24rpx;
    line-height: 40rpx;
    background: #fff;
    box-sizing: content-box;
    .center_img {
      padding-top: 4rpx;
      width: 50rpx;
      height: 50rpx;
    }
    view {
      font-size: 20rpx;
      margin-top: 15rpx;
    }

    &--blurred {
      background: rgba(255, 255, 255, 0.7);
      backdrop-filter: blur(10px);
      -webkit-backdrop-filter: blur(10px);
    }
  }
}
</style>
