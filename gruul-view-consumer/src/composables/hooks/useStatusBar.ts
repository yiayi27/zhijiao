import { useSettingStore } from '@/store/modules/setting'
import { ref } from 'vue'

export const useStatusBar = () => {
  // 状态栏高度
  const statusBarHeight = ref(0)
  // 导航栏高度(不包含状态栏)
  const navBarHeight = ref(45)

  uni.getSystemInfo({
    success: (res) => {
      statusBarHeight.value = res.statusBarHeight ?? 0
      if (res.system.indexOf('iOS') > -1) {
        navBarHeight.value = 50
      } else {
        navBarHeight.value = 45
      }
      // #ifdef MP-WEIXIN
      let custom = uni.getMenuButtonBoundingClientRect()
      navBarHeight.value = (custom.top - statusBarHeight.value) * 2 + custom.height
      // #endif
      useSettingStore().setNavigationBarHeight(statusBarHeight.value + navBarHeight.value)
    },
  })
  return { statusBarHeight, navBarHeight }
}
