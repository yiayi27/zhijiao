<script setup lang="ts">
import StoreNavigation from './StoreNavigation'
import { useVModel } from '@vueuse/core'

import StoreNavigationItemFrom from './StoreNavigationItem.vue'
import StoreNavigationItemTs from './StoreNavigationItemTs'

const props = defineProps<{
  formData: typeof StoreNavigation
}>()

const emit = defineEmits(['update:formData'])
const formData = useVModel(props, 'formData', emit)

/** 拖动的组件的下角标 */
const dragStarIndex = ref(-1)

/**
 * 添加导航
 */
function handelAddNav() {
  if (formData.value.storeNavigations.length >= 10) {
    ElMessage.warning('最多可添加10个')
    return
  }
  const itemTs: StoreNavigationItemTs = new StoreNavigationItemTs()
  formData.value.storeNavigations.push(itemTs)
}

/** 删除底部导航tab */
function delect(index: number) {
  // if (props.formData.storeNavigations.length <= 4) {
  //   ElMessage.warning("最少添加4个");
  //   return;
  // }
  formData.value.storeNavigations.splice(index, 1)
  // sortMenu();
}

/**
 * 开始拖动，记录拖动的组件下角标
 */
function handleDragstart(i: number) {
  dragStarIndex.value = i
}

/**
 * 阻止默认行为，否则drop事件不会触发
 */
function handleDragover(e: DragEvent) {
  e.preventDefault()
}

/**
 * 被放置的容器触发事件，交换两个组件的位置
 */
function handleDrop(i: number) {
  if (dragStarIndex.value === i) {
    return false
  }
  const temp = formData.value.storeNavigations.splice(dragStarIndex.value, 1, formData.value.storeNavigations[i])
  formData.value.storeNavigations.splice(i, 1, ...temp)
}
</script>

<template>
  <!-- 店铺导航 -->
  <div>
    <el-form :model="formData" label-width="80px">
      <el-form-item label="店铺导航"> 最多可添加10个，最少可添加4个 </el-form-item>
    </el-form>
    <div
      v-for="(storeNavigationItem, index) in formData.storeNavigations"
      :key="index"
      :draggable="true"
      @dragstart="handleDragstart(index)"
      @dragover="handleDragover"
      @drop="handleDrop(index)"
    >
      <StoreNavigationItemFrom :key="index" :item-index="index" :form-data="storeNavigationItem" @on-delect="delect(index)" />
    </div>

    <el-row v-if="formData.storeNavigations.length < 10" type="flex" align="middle" justify="space-between">
      <el-button @click="handelAddNav"> 添加导航 </el-button>
      <!-- <div>上下拖动可排序</div> -->
    </el-row>
  </div>
</template>
