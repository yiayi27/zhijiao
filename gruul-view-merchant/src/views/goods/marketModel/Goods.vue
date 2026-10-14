<script setup lang="ts">
import { getAllRegionList } from '@/api/good/goods'
import MContainer from '@/components/MContainer/MContainer.vue'

import { useRoute } from 'vue-router'
import ListApart from './ListApart.vue'

defineOptions({
  name: 'GoodsList',
})

// 定义接口
interface RegionItem {
  id: string
  modeName: string
  [key: string]: any
}

// 定义响应式状态
const list = ref<RegionItem[]>([])
const activeName = ref('')
const chooseId = ref('')
const route = useRoute()
const listApartRef = ref()
const loading = ref(false)

// 初始化
onMounted(() => {
  const fromSaleMode = route.params.saleMode as string
  init(fromSaleMode)
})

/**
 * 获取所有专区
 */
async function init(fromSaleMode?: string) {
  loading.value = true
  const res = await getAllRegionList({}).finally(() => {
    loading.value = false
  })
  list.value = res.data
  activeName.value = res.data[0].modeName
  chooseId.value = res.data[0].id

  if (fromSaleMode) {
    const chooseItem = res.data.filter((item: Obj) => item.id === fromSaleMode)
    if (chooseItem.length > 0) {
      activeName.value = chooseItem[0].modeName
      chooseId.value = chooseItem[0].id
    }
  }
}

/**
 * 顶部专区选择
 */
function handleClick(tab: any) {
  chooseId.value = list.value[tab.index].id
}
</script>

<template>
  <MContainer v-loading="loading">
    <template #tabs>
      <el-tabs v-model="activeName" @tab-click="handleClick">
        <el-tab-pane v-for="(item, index) in list" :id="item.id" :key="index" :label="item.modeName" :name="item.modeName" />
      </el-tabs>
    </template>

    <template #content>
      <ListApart ref="listApartRef" :sale-mode="chooseId" />
    </template>
  </MContainer>
</template>

<style lang="scss" scoped></style>
