<template>
  <view>
    <UPopup v-model:show="isshow" @close="cancel">
      <view class="popup">
        <image :src="image" />
        <view class="close" @tap="cancel" />
        <view class="btn" @tap="topage" />
      </view>
    </UPopup>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { getRecyclingSend } from '@/api/modules/global'
import { useSettingStore } from '@/store/modules/setting'
import { useUserStore } from '@/store/modules/user'
import { APP_LAUNCH } from '@/utils/emitNameConfig'

// 定义props
defineProps({
  decoretionProperties: {
    type: Object,
    default: () => {},
  },
})

// 响应式变量
const isshow = ref(false)
const image = ref('')

const appLaunchInit = () => {
  const openrecyling = useSettingStore().recyclingsetting.recyclingState
  uni.$on(APP_LAUNCH, async () => {
    const res = await getRecyclingSend()
    isshow.value = res && openrecyling
    image.value = useSettingStore().recyclingsetting.homePromptBgImg
  })
}

onBeforeUnmount(() => {
  uni.$off(APP_LAUNCH)
})

const cancel = () => {
  isshow.value = false
}

const topage = () => {
  isshow.value = false
  uni.navigateTo({
    url: '/subcontract/pages/mysend/mysend',
  })
}

// 生命周期钩子
onMounted(() => {
  image.value = useSettingStore().recyclingsetting.homePromptBgImg || ''
  if (useUserStore().userInfo.token) {
    isshow.value = false
  } else {
    appLaunchInit()
  }
})
</script>

<style lang="scss" scoped>
.popup {
  width: 610rpx;
  height: 550rpx;
  position: relative;

  image {
    width: 100%;
    height: 100%;
  }

  .close {
    width: 60rpx;
    height: 60rpx;
    // background-color: rgba(0,0,0,0.3);
    position: absolute;
    top: 0;
    right: 0;
  }

  .btn {
    width: 420rpx;
    height: 70rpx;
    // background-color: rgba(0,0,0,0.3);
    position: absolute;
    bottom: 40rpx;
    right: 0;
    left: 0;
    margin: 0 auto;
  }
}
</style>
