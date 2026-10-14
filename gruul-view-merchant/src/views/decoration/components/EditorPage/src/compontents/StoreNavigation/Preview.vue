<script setup lang="ts">
import StoreNavigation from './StoreNavigation'

defineProps<{
  formData: typeof StoreNavigation
}>()
</script>

<template>
  <!-- 店铺导航 -->
  <div>
    <ul class="storeNavigation-item">
      <li
        v-for="(storeNavigationItem, index) in formData.storeNavigations"
        :key="index"
        :style="{
          width: formData.storeNavigations.length % 5 === 0 || formData.storeNavigations.length > 8 ? '20%' : '25%',
        }"
      >
        <img class="storeNavigation-item__img" :src="storeNavigationItem.navIcon" />
        <div :style="{ color: storeNavigationItem.fontColor }">
          {{ storeNavigationItem.navName }}
        </div>
      </li>
    </ul>
  </div>
</template>

<style lang="scss" scoped>
@import '@/assets/styles/decoration/storeNavigation';
</style>
