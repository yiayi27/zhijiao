<script setup lang="ts">
import Separator from './Separator'

const props = defineProps({
  formData: {
    type: Object as PropType<typeof Separator>,
    default: () => Separator,
  },
})

const inlineBox = computed(() => {
  const { hasMargin } = props.formData
  return `box-sizing: border-box; padding: 10px ${hasMargin ? 15 : 0}px`
})

const inlineStyle = computed(() => {
  const { borderColor, borderStyle } = props.formData
  return `border-bottom:1px ${borderStyle} ${borderColor};`
})
</script>

<template>
  <!-- 分隔符 -->
  <div :style="inlineBox">
    <div :style="inlineStyle" />
  </div>
</template>
