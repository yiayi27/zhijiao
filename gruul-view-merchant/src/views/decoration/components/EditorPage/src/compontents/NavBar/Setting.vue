<script setup lang="ts">
import NavBar from './NavBar'
import { getDefaultMenuItem } from '@/views/decoration/components/EditorPage/src/compontents/NavBar/NavBarItem'
import { Warning } from '@element-plus/icons-vue'
import { useVModel } from '@vueuse/core'

import { getDefaultIconList } from './NavBar'
import NavBarItem from './NavBarItem.vue'

const props = defineProps({
  formData: {
    type: Object as PropType<typeof NavBar>,
    required: true,
  },
})
const emit = defineEmits(['update:formData'])
const formData = useVModel(props, 'formData', emit)

const notice = ref(false)

/** 拖动的组件的下角标 */
const dragStarIndex = ref(-1)

/** 新增导航 */
function addNav() {
  formData.value.codeStyle = 1
  if (formData.value.menuList.length <= 4) {
    const defaultTab = getDefaultMenuItem()
    formData.value.menuList.push(defaultTab)
    sortMenu()
  } else {
    ElMessage({
      message: '底部导航最多4个',
      type: 'warning',
    })
  }
}

/** 删除底部导航tab */
function delect(index: number) {
  formData.value.codeStyle = 1
  formData.value.menuList.splice(index, 1)
  sortMenu()
}

/**  选择默认图标 */
function setDefIcon(index: number) {
  const icons = getDefaultIconList()
  formData.value.menuList[index].defIcon = icons[index].def
  formData.value.menuList[index].actIcon = icons[index].act
}

/** 底部导航排序 */
function sortMenu() {
  formData.value.menuList.forEach((item, index) => {
    item.sortIndex = index
  })
}

/** 设为首页 */
function setHomeTab(homeIndex: number) {
  formData.value.menuList.forEach((item, index) => {
    if (homeIndex === index) {
      item.isHome = true
    } else {
      item.isHome = false
    }
  })
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
  const temp = formData.value.menuList.splice(dragStarIndex.value, 1, formData.value.menuList[i])
  formData.value.menuList.splice(i, 1, ...temp)
}
</script>

<template>
  <!-- 导航 -->
  <el-form :model="formData" label-width="100px">
    <div class="setmenu__form--item" style="padding-top: 10px; position: relative">
      <label class="el-form-item__label">选择样式:</label>
      <el-radio-group v-model="formData.codeStyle">
        <el-radio :value="1"> 普通样式 </el-radio>
        <el-radio :disabled="(formData.menuList.length & 1) === 0" :value="2">
          中间大图样式
          <el-icon class="pointer" @mouseover="notice = true" @mouseout="notice = false">
            <Warning />
          </el-icon>
        </el-radio>
      </el-radio-group>

      <span v-if="notice" style="position: absolute; top: 40px; left: 50px">该模式适合单数导航，最多可添加5个，最少3个</span>
    </div>

    <div class="setmenu__form--item" style="padding-top: 0px">
      <label class="el-form-item__label">添加导航:</label>
      <span style="color: #9797a1; font-size: 12px">最多添加5个导航，鼠标拖拽调整导航顺序</span>
    </div>
    <div class="setmenu__form--item" style="padding-top: 0px">
      <label class="el-form-item__label">未选中颜色:</label>
      <el-color-picker v-model="formData.defaultColor" />
    </div>
    <div class="setmenu__form--item" style="padding-top: 0px">
      <label class="el-form-item__label">已选择颜色:</label>
      <el-color-picker v-model="formData.selectColor" />
    </div>

    <!-- 导航item -->
    <div
      v-for="(item, index) in formData.menuList"
      :key="index"
      :draggable="true"
      @dragstart="handleDragstart(index)"
      @dragover="handleDragover"
      @drop="handleDrop(index)"
    >
      <NavBarItem
        :item-index="index"
        :form-data="item"
        @on-set-home-tab="setHomeTab(index)"
        @on-delect="delect(index)"
        @set-def-icon="setDefIcon(index)"
      />
    </div>

    <!-- 添加导航按钮 -->
    <el-button v-if="formData.menuList.length <= 4" style="margin-top: 10px" @click="addNav()"> 添加导航 </el-button>
  </el-form>
</template>

<style lang="scss" scoped>
@import '@/assets/styles/decoration/navBar';
</style>
