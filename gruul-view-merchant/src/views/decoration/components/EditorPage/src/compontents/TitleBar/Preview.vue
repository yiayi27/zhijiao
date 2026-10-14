<script setup lang="ts">
import TitleBar from './TitleBar'

const props = defineProps({
  formData: {
    type: Object as () => typeof TitleBar,
    required: true,
  },
})

const inlineStyle = computed(() => {
  const { backgroundColor } = props.formData
  return {
    'background-color': backgroundColor,
  }
})
</script>

<template>
  <!-- 标题栏 -->
  <div class="titleBar" :style="inlineStyle">
    <div
      v-if="formData.showStyle === 'is-style-one'"
      class="titleBar__item"
      :class="formData.showStyle"
      :style="{ color: formData.color, fontWeight: 600 }"
    >
      {{ formData.titleName }}
    </div>
    <div v-if="formData.showStyle === 'is-style-two'" class="titleBar__item" :class="formData.showStyle" :style="{ color: formData.color }">
      <div style="font-weight: 600">
        {{ formData.titleName }}
      </div>
      <div>查看更多></div>
    </div>
  </div>
</template>
