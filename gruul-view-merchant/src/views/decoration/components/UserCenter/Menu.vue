<script lang="ts" setup>
// 没有需要定义的props或方法
</script>

<template>
  <div class="menu">
    <slot />
  </div>
</template>

<style lang="scss" scoped>
.menu {
  border-radius: 10px;
  margin-bottom: 10px;
}
</style>
