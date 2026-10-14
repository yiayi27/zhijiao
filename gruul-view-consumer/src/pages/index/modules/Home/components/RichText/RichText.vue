<template>
  <!-- 富文本组件 -->
  <view style="width: 100%; overflow: hidden">
    <rich-text :nodes="decoretionProperties.text" />
  </view>
</template>

<script setup lang="ts">
defineOptions({
  name: 'QRichText',
})
// 定义富文本组件接口
interface RichTextComponent {
  text: string
}
defineProps<{
  decoretionProperties: RichTextComponent
}>()
</script>

<style></style>
