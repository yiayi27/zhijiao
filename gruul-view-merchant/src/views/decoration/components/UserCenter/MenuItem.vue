<script lang="ts" setup>
import { ArrowRight } from '@element-plus/icons-vue'

defineProps({
  imgUrl: {
    type: String,
    default: '',
  },
  rightIcon: {
    type: String,
    default: ArrowRight,
  },
})
</script>

<template>
  <div class="item">
    <img v-if="imgUrl !== ''" :src="imgUrl" class="item__img" />
    <span class="item__text">
      <slot />
    </span>
    <el-icon class="el-collapse-item__arrow">
      <component :is="rightIcon" />
    </el-icon>
  </div>
</template>

<style lang="scss">
@import '../../../../assets/styles/decoration/menuitem';
</style>
