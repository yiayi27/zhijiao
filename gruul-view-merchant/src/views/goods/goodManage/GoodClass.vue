<script setup lang="ts">
import { useRoute } from 'vue-router'

defineOptions({
  name: 'GoodClass',
})

const components = {
  ShowClass: defineAsyncComponent(() => import('./components/ShowClass.vue')),
  PickShow: defineAsyncComponent(() => import('./components/PickShow.vue')),
}

// 响应式状态
const comName = ref<keyof typeof components>('ShowClass')
const showType = ref(false)
const showClassType = ref(false)
const route = useRoute()

// 初始化
onMounted(() => {
  showType.value = false
  showClassType.value = false

  comName.value = route.query.options === '2' ? 'PickShow' : 'ShowClass'
  if (route.query.options) {
    showType.value = true
  }

  if (route.query.options === 'classSet') {
    showType.value = false
  }
})

/**
 * 选择顶部分类
 */
function selectType(tab: any) {
  const index = tab.index
  comName.value = index === '0' ? 'ShowClass' : 'PickShow'
}
</script>

<template>
  <div style="min-height: 800px !important">
    <el-tabs v-if="!showType" v-model="comName" class="top--tag" @tab-click="selectType">
      <el-tab-pane label="展示分类" name="ShowClass" />
    </el-tabs>
    <component :is="components[comName]" />
  </div>
</template>

<style lang="scss" scoped>
:deep(.el-tabs__nav-wrap::after) {
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
