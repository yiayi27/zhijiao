<script setup lang="ts">
import { useRoute } from 'vue-router'

defineOptions({
  name: 'BusinessCenter',
})

const route = useRoute()
const comName = ref<keyof typeof asyncComponentMap>('Account')

// 使用defineAsyncComponent定义异步组件
const asyncComponentMap = {
  Account: defineAsyncComponent(() => import('@/views/businessCenter/Account/Account.vue')),
}

onMounted(() => {
  if (route.query.tabName) {
    comName.value = route.query.tabName as keyof typeof asyncComponentMap
  }
})

function handleClick() {
  history.pushState({}, '', `${location.origin}${location.pathname}?tabName=${comName.value}`)
}
</script>

<template>
  <div class="setting">
    <el-tabs v-model="comName" @tab-change="handleClick">
      <el-tab-pane label="账户管理" name="Account" />
    </el-tabs>
    <component :is="asyncComponentMap[comName]" />
  </div>
</template>

<style lang="scss" scoped>
@import '@/assets/styles/businessCenter/businessCenter.scss';
</style>
