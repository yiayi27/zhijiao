<script setup lang="ts">
import NavBar from './NavBar'

defineProps({
  formData: {
    type: Object as PropType<typeof NavBar>,
    required: true,
  },
})
</script>

<template>
  <div class="bottom_bar">
    <div
      v-for="(item, index) in formData.menuList"
      :key="index"
      :class="
        formData.codeStyle === 2 &&
        (formData.menuList.length === 1 || (formData.menuList.length === 3 && index === 1) || (formData.menuList.length === 5 && index === 2))
          ? 'bottom_bar_item_big'
          : 'bottom_bar_item'
      "
    >
      <img v-if="item.iconType === 1" class="bottom_bar_item_icon" :src="item.defIcon" />
      <img v-else class="bottom_bar_item_icon" :src="item.iconPath" />
      <span class="bottom_bar_item_text">{{ item.text }}</span>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@import '@/assets/styles/decoration/navBar';
</style>
