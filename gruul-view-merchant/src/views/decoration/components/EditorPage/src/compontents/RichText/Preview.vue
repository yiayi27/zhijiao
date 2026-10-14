<script setup lang="ts">
import RichText from '@/views/decoration/components/EditorPage/src/compontents/RichText/RichText'

defineProps({
  formData: {
    type: Object as PropType<typeof RichText>,
    default: () => RichText,
  },
})
</script>

<template>
  <!-- 富文本组件 -->
  <div class="rc-design-react-preview rc-design-component-default-preview">
    <div v-if="!formData.text" class="rc-design-component-default-preview__text">编辑文本</div>
    <div v-else v-dompurify-html="formData.text" class="cap-cube-wrap" />
  </div>
</template>

<style lang="scss" scoped>
@import '@/assets/styles/decoration/cubeBox.scss';
.cap-cube-wrap {
  width: 100%;
  overflow: hidden;
  word-wrap: break-word;
}
</style>
