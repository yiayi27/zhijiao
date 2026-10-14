<script setup lang="ts">
import fullReduction from '@/assets/images/fullReduction.jpg'
import MContainer from '@/components/MContainer/MContainer.vue'

defineOptions({
  name: 'FullIndex',
})

const componentMap = {
  ActivityLists: defineAsyncComponent(() => import('./ActivityLists/Index.vue')),
  ActivityExplain: defineAsyncComponent(() => import('./ActivityExplain.vue')),
}

const comName = ref<keyof typeof componentMap>('ActivityLists')
</script>

<template>
  <MContainer>
    <template #top>
      <img :src="fullReduction" alt="" style="width: 100%; height: 260px" />
    </template>

    <template #tabs>
      <el-tabs v-model="comName">
        <el-tab-pane label="所有活动" name="ActivityLists" />
        <el-tab-pane label="活动说明" name="ActivityExplain" />
      </el-tabs>
    </template>

    <template #content>
      <component :is="componentMap[comName]" />
    </template>
  </MContainer>
</template>

<style lang="scss" scoped>
:deep(.el-tabs__nav-wrap)::after {
  content: '';
  position: absolute;
  left: 0;
  bottom: 0;
  width: 100%;
  height: 2px;
  background-color: #f5f5f5;
  z-index: 1;
}
</style>
