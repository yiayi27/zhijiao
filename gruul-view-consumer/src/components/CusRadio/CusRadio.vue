<template>
  <view :class="[value ? 'choosed' : 'radio']" @click="chooseHandle">
    <uv-icon v-if="value" name="checkbox-mark" color="#fff" size="16" />
  </view>
</template>

<script setup lang="ts">
// 定义Props
const props = defineProps({
  value: {
    type: Boolean,
    default: false,
  },
  index: {
    type: Number,
    default: 0,
  },
})

// 定义emit
const emit = defineEmits(['change'])

// 方法
const chooseHandle = () => {
  emit('change', { index: props.index })
}
</script>

<style lang="scss" scoped>
.radio {
  width: 36rpx;
  height: 36rpx;
  border-radius: 50%;
  border: 1rpx solid;
  border-color: #c8c9cc;
}
.choosed {
  width: 36rpx;
  height: 36rpx;
  border-radius: 50%;
  background: #ff4444;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
