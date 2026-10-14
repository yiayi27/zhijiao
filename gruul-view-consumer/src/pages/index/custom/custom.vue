<template>
  <Home ref="HomeRef" isCustom />
</template>

<script setup lang="ts">
import Home from '../modules/Home/Home.vue'
import { getDecorationAll } from '@/api/modules/decoration'
import { useSettingStore } from '@/store/modules/setting'
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'

// 组件引用
const HomeRef = ref<any>(null)

// 方法定义
const parseAssembleList = (pageList: any[]) => {
  let assembleList: any[] = []
  const nid = useSettingStore().newCurrentPageId
  assembleList = pageList.filter((item) => Number(item.id) === Number(nid))

  uni.setNavigationBarTitle({
    title: `${assembleList[0].pageName}`,
  })
}

const getDecorationAllList = async () => {
  try {
    const data = await getDecorationAll()
    const pages = data.pages
    parseAssembleList(pages)
  } catch (e) {
    uni.showToast({
      title: `${e}`,
      icon: 'none',
    })
  }
}

// 生命周期
onLoad(() => {
  getDecorationAllList()
  useSettingStore().setHasInCustom(true)
})
</script>

<style></style>
