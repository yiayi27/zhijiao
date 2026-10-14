<script setup lang="ts">
import MContainer from '@/components/MContainer/MContainer.vue'

import detCommodity from './common/detCommodity.vue'
import setCommodity from './common/setCommodity.vue'

defineOptions({
  name: 'CommodityIndex',
})

const clickType = ref(1)

onMounted(() => {
  init()
})

/**
 * 初始化信息
 */
function init() {}

/**
 * 顶部点击事件
 */
function clickTop(type: number) {
  clickType.value = type
}

// 导出方法供父组件使用
defineExpose({
  init,
  clickTop,
})
</script>

<template>
  <MContainer>
    <template #tabs>
      <el-tabs v-model="clickType">
        <el-tab-pane label="通用设置" :name="1" />
        <el-tab-pane label="基金详情" :name="0" />
      </el-tabs>
    </template>

    <template #content>
      <setCommodity v-if="clickType" />
      <detCommodity v-if="!clickType" />
    </template>
  </MContainer>
</template>

<style lang="scss" scoped>
.pageTop {
  display: flex;
  height: 50px;
  align-items: center;
  border-bottom: 1px solid #f0f0f0;

  &__botton {
    padding: 10px 20px;
    border-radius: 50px;
    font-size: 13px;
    font-weight: bold;
    margin-right: 20px;
    cursor: pointer;
  }
}
</style>
