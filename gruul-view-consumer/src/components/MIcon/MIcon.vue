<template>
  <view @click="clickHandler">
    <text :class="'icon iconfont ' + [name]" :style="'font-size:' + size + '; color:' + color" />
  </view>
</template>

<script setup lang="ts">
// 定义props
defineProps({
  name: {
    type: String,
    default: '',
  },
  size: {
    type: String,
    default: '32rpx',
  },
  color: {
    type: String,
    default: '',
  },
})

// 定义emit
const emit = defineEmits(['click'])

// 方法
const clickHandler = () => {
  emit('click', {})
}
</script>

<style lang="scss">
@import '@/assets/styles/font/iconfont.scss';
</style>
