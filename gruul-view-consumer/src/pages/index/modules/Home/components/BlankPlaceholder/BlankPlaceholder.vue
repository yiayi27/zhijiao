<template>
  <!-- 空白占位 -->
  <view :style="inlineStyle" />
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'

interface BlankHeight {
  /** 空白高度 */
  height: number
}

// 定义props
const props = defineProps({
  decoretionProperties: {
    type: Object,
    default: () => ({
      height: 0,
    }),
  },
})

// 响应式变量
const formData = ref<BlankHeight>({
  height: 0,
})
const inlineStyle = ref('')

// 方法定义
const setInlineStyle = () => {
  const { height } = formData.value
  inlineStyle.value = `height:${height}px;background-color:transparent;`
}

// 监听props变化
watch(
  () => props.decoretionProperties,
  (newVal) => {
    formData.value = newVal as BlankHeight
    setInlineStyle()
  },
)

// 组件挂载
onMounted(() => {
  formData.value = props.decoretionProperties as BlankHeight
  setInlineStyle()
})
</script>

<style></style>
