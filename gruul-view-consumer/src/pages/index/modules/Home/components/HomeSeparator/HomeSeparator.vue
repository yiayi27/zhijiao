<template>
  <!-- 分隔符 -->
  <view :style="inlineBox">
    <view :style="inlineStyle" />
  </view>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

// 定义分隔符接口
interface ISeparator {
  /** 颜色 */
  borderColor: string
  /** 边距 */
  hasMargin: boolean
  /** 样式 solid dashed dotted */
  borderStyle: string
}

// 定义props
const props = defineProps<{
  decoretionProperties?: ISeparator
}>()

// 响应式变量
const formData = ref<ISeparator>({
  borderColor: '',
  hasMargin: false,
  borderStyle: 'solid',
})
const inlineBox = ref('')
const inlineStyle = ref('')

// 方法定义
const getinlineBox = () => {
  const { hasMargin } = formData.value
  inlineBox.value = `box-sizing: 'border-box'; padding: 20rpx ${hasMargin ? 15 : 0}px;`
}

const getinlineStyle = () => {
  const { borderColor, borderStyle } = formData.value
  inlineStyle.value = `border-bottom: 1px ${borderStyle} ${borderColor};`
}

// 初始化
const initComponent = () => {
  if (props.decoretionProperties) {
    formData.value = props.decoretionProperties
    getinlineBox()
    getinlineStyle()
  }
}

// 监听props变化
watch(
  () => props.decoretionProperties,
  () => {
    initComponent()
  },
  {
    immediate: true,
    deep: true,
  },
)
</script>

<style></style>
