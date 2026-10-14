<script setup lang="ts">
import { NEW_GOOD_BASE_INFO_MODEL, NEW_GOOD_SELL_INFO_MODEL, NEW_GOOD_SHOP_INFO_MODEL } from '@/libs/enum/storage'
import { storage } from '@/libs/storage'
import { ElMessageBox } from 'element-plus'

import { onBeforeRouteLeave, useRoute } from 'vue-router'
import AddGoodTem from './components/AddGoodTem/index'

defineOptions({
  name: 'AddGood',
})

// 定义响应式状态
const saleMode = ref<string>('')

// 路由钩子
const route = useRoute()

// 初始化
onMounted(() => {
  saleMode.value = route.query.saleMode as string
})

// 路由离开守卫
onBeforeRouteLeave((to, from, next) => {
  if (to.query.saveFlag || to.params.saveFlag) {
    next()
    return
  }

  ElMessageBox.confirm(`确定退出${route.params.id ? '编辑' : '发布'}商品页面?退出后，未保存的信息将不会保留!`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    storage.delete(NEW_GOOD_BASE_INFO_MODEL)
    storage.delete(NEW_GOOD_SELL_INFO_MODEL)
    storage.delete(NEW_GOOD_SHOP_INFO_MODEL)
    next()
  })
})
</script>

<template>
  <div>
    <AddGoodTem v-if="saleMode" />
  </div>
</template>
