<script setup lang="ts">
import { useGlobalStore } from '@/store/modules/global'
import zhCn from 'element-plus/es/locale/lang/zh-cn'

// 获取全局store
const globalStore = useGlobalStore()
// 组件挂载时执行
onMounted(() => {
  globalStore.initSocket()
})
</script>

<template>
  <el-config-provider :locale="zhCn">
    <router-view />
  </el-config-provider>
</template>

<style lang="scss">
/* 全局样式可以放在这里 */

#nprogress .bar {
  background: #409eff !important;
}
</style>
