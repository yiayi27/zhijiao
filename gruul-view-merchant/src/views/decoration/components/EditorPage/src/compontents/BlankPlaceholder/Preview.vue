<script setup lang="ts">
import BlankPlaceholder from './BlankPlaceholder'

const props = defineProps({
  formData: {
    type: Object as PropType<typeof BlankPlaceholder>,
    required: true,
  },
})

const inlineStyle = computed(() => {
  return `height:${props.formData.height}px`
})
</script>

<template>
  <!-- 空白占位 -->
  <div :style="inlineStyle" />
</template>
