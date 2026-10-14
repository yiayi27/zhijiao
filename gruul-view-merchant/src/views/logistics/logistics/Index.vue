<script setup lang="ts">
import MContainer from '@/components/MContainer/MContainer.vue'

defineOptions({
  name: 'LogisticsIndex',
})

/** 组件映射 */
const componentMap = {
  LogisticsAttr: defineAsyncComponent(() => import('./components/logisticsAttr/LogisticsAttr.vue')),
  AddressProgram: defineAsyncComponent(() => import('./components/addressProgram/AddressProgram.vue')),
  LogisticsServe: defineAsyncComponent(() => import('./components/logisticsServe/LogisticsServe.vue')),
}

/** 当前tab */
const comName = ref<keyof typeof componentMap>('LogisticsAttr')
</script>

<template>
  <MContainer class="logIndex">
    <template #tabs>
      <el-tabs v-model="comName">
        <el-tab-pane label="运费模板" name="LogisticsAttr" />
        <el-tab-pane label="地址管理" name="AddressProgram" />
        <el-tab-pane label="物流服务" name="LogisticsServe" />
      </el-tabs>
    </template>

    <template #content>
      <component :is="componentMap[comName]" />
    </template>
  </MContainer>
</template>

<style lang="scss" scoped>
.logIndex {
  padding: 0px;
}
</style>
