<template>
  <view class="nav">
    <uv-status-bar :customStyle="containerStyle" />
    <view class="navbar" :style="'height:' + navBarHeight + 'px;' + containerStyle">
      <view v-if="backIcon" class="back-icon" @tap="back">
        <image :src="backIcon" />
      </view>
      <view v-if="homeIcon" class="home-icon" @tap="home">
        <image :src="homeIcon" />
      </view>
      <view v-if="titleImg" class="nav-icon">
        <image :src="titleImg" :style="iconStyle" />
      </view>
      <view v-if="titleText && !titleImg" class="nav-title">
        <text :style="textStyle">{{ titleText }}</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useStatusBar } from '@/composables/hooks/useStatusBar'

defineOptions({
  options: {
    virtualHost: true,
  },
})
// 定义Props
const props = defineProps({
  background: {
    type: String,
    default: '#ffffff',
  },
  color: {
    type: String,
    default: 'rgba(0, 0, 0, 1)',
  },
  titleText: {
    type: String,
    default: '导航栏',
  },
  titleImg: {
    type: String,
    default: '',
  },
  backIcon: {
    type: String,
    default: '',
  },
  homeIcon: {
    type: String,
    default: '',
  },
  fontSize: {
    type: Number,
    default: 16,
  },
  iconHeight: {
    type: Number,
    default: 19,
  },
  iconWidth: {
    type: Number,
    default: 58,
  },
})

// 定义emit
const emit = defineEmits(['back', 'home'])

// 响应式数据
const { statusBarHeight, navBarHeight } = useStatusBar()
const containerStyle = ref('')
const textStyle = ref('')
const iconStyle = ref('')

// 生命周期
onMounted(() => {
  setStyle()
})

const setStyle = () => {
  containerStyle.value = `background:${props.background}`
  textStyle.value = `color:${props.color};font-size:${props.fontSize}px`
  iconStyle.value = `width:${props.iconWidth}px;height:${props.iconHeight}px`
}

const back = () => {
  uni.navigateBack({
    delta: 1,
  })

  emit('back', {
    back: 1,
  })
}

const home = () => {
  emit('home', {})
}
</script>

<style lang="scss" scoped>
.nav {
  position: fixed;
  width: 100%;
  top: 0;
  left: 0;
  z-index: 99;
}

.navbar {
  position: relative;
  transition: all 300ms linear 0s;
}

.back-icon,
.home-icon {
  width: 56rpx;
  height: 100%;
  position: absolute;
  transform: translateY(-50%);
  top: 50%;
  display: flex;
}

.back-icon {
  left: 32rpx;
}

.home-icon {
  left: 88rpx;
}

.back-icon image {
  width: 56rpx;
  height: 56rpx;
  margin: auto;
}

.home-icon image {
  width: 40rpx;
  height: 40rpx;
  margin: auto;
}

.nav-title,
.nav-icon {
  position: absolute;
  transform: translate(-50%, -50%);
  font-size: 28rpx;
  left: 50%;
  top: 50%;
  color: #000000;
  transition: all 1s ease 0s;
  font-weight: bold;
}
</style>
