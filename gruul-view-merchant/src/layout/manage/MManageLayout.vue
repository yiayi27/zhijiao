<script setup lang="ts">
import { useRoute } from 'vue-router'

defineOptions({
  name: 'MManageLayout',
})
const route = useRoute()
const noPadding = computed(() => route.meta && route.meta.noPadding)
</script>

<template>
  <div class="admin">
    <div class="admin__header">
      <slot name="logo" />
      <slot name="user" />
    </div>
    <div class="admin__content admin__center">
      <div class="admin__main">
        <div class="admin__main--content" :class="noPadding && 'no--padding'">
          <slot name="content" />
          <slot name="footer" />
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@import '@/assets/styles/console/m-layout.scss';
</style>
