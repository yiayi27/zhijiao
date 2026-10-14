<script setup lang="ts">
import { dropdownHack } from '@/libs'

import { useRoute } from 'vue-router'

defineOptions({
  name: 'MLayout',
})

// 响应式状态
const route = useRoute()
const minHeight = ref(780)

// 计算属性
const noPadding = computed(() => route.meta?.noPadding)

// 生命周期钩子
onMounted(() => {
  nextTick(() => {
    dropdownHack()
  })

  minHeight.value = document.documentElement.clientHeight - 84
})

// 监听路由变化
watch(
  () => route.name,
  () => {
    const scrollWrap = document.querySelector('.el-scrollbar__wrap')
    if (scrollWrap) {
      scrollWrap.scrollTop = 0
    }
  },
)
</script>

<template>
  <div class="admin">
    <div class="admin__header">
      <div class="admin__center admin__center--flex">
        <slot name="logo" />
        <slot name="user" />
      </div>
    </div>
    <div class="admin__center admin__main--breadcrumb-wrap">
      <div class="admin__main--breadcrumb">
        <slot name="breadcrumb" />
      </div>
    </div>
    <div class="admin__center_wrap">
      <!--      <el-scrollbar :noresize="false" view-style="width:100vw;" class="h100 pb-[10px]"> -->
      <div class="admin__content admin__center h-full">
        <div class="admin__aside">
          <slot name="aside" />
        </div>
        <el-main class="admin__main">
          <div class="admin__main--content h-full overflow-auto" :class="{ 'no--padding': noPadding }">
            <slot name="view" />
          </div>
        </el-main>
      </div>
      <!--      </el-scrollbar> -->
    </div>
    <slot name="notify" />
  </div>
</template>

<style lang="scss" scoped>
@import 'styles/m-layout.scss';

.side-nav-wrap-main {
  ::-webkit-scrollbar {
    display: none;
  }
}
</style>
