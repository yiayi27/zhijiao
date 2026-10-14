<script setup lang="ts">
import BusinessSuper from './../BusinessSuper'
import { useVModel } from '@vueuse/core'

import { bus } from './../BusinessSuper'

const props = defineProps({
  formData: {
    type: Object as PropType<typeof BusinessSuper>,
    required: true,
  },
})
const emit = defineEmits(['update:formData'])
const formData = useVModel(props, 'formData', emit)

const activeId = ref<string>('-1')

const getHeadList = computed(() => {
  return formData.value.firstCatList.map((i) => {
    return {
      id: i.category?.id,
      name: (i.category?.name || '').substr(0, 4),
      isSelected: i.isSelected,
    }
  })
})

// 监听 firstCatList 变化，更新 activeId
watch(
  () => formData.value.firstCatList,
  (newVal) => {
    const selectedItem = newVal.find((i) => i.isSelected)
    activeId.value = selectedItem?.category?.id ?? '-1'
  },
  { immediate: true, deep: true },
)

const styleType = computed(() => {
  const { fontColor, fontBg, fontNotColor, fontNotBg } = formData.value
  const style1 = {
    color: fontColor,
    backgroundColor: fontBg,
  }
  const style2 = {
    color: fontNotColor,
    backgroundColor: fontNotBg,
  }
  return {
    style1,
    style2,
  }
})

const getCurrentFirstCategory = computed(() => {
  const { currentFirstCategory } = formData.value
  return currentFirstCategory
})

watch(
  getCurrentFirstCategory,
  (v) => {
    if (v && v.category && v.category.id) {
      activeId.value = v.category.id
    }
  },
  { deep: true },
)

/**
 * 修改当前激活tab
 */
function changeActiveItem(item: (typeof getHeadList.value)[number]) {
  formData.value.firstCatList.forEach((i) => {
    i.isSelected = i.category?.id === item.id
  })
  activeId.value = item.id ?? '-1'
  bus.emit('firstCategoryId', item.id)
}

/**
 * 选择完成一级分类需要初始化当前编辑的一级分类
 */
function initCurruntFirstCategory() {
  const { selectMode } = formData.value
  if (selectMode !== 2) return
  const item = formData.value.firstCatList.find((i) => i.isSelected)
  if (item || !formData.value.firstCatList.length) return
  formData.value.firstCatList[0].isSelected = true
  bus.emit('firstCategoryId', formData.value.firstCatList[0].category?.id ?? '-1')
}

onMounted(() => {
  if (formData.value.firstCatList.length && !formData.value.firstCatList.find((i) => i.isSelected)) {
    formData.value.firstCatList[0].isSelected = true
    activeId.value = formData.value.firstCatList[0].category?.id ?? '-1'
    bus.emit('firstCategoryId', activeId.value)
  }
  bus.on('selectedFirstCategory', () => {
    initCurruntFirstCategory()
  })
})
</script>

<template>
  <div class="left__mode-page" :style="styleType.style2">
    <div
      v-for="item in getHeadList"
      :key="item.id"
      class="tab__item"
      :style="item.id === activeId ? styleType.style1 : styleType.style2"
      @click="changeActiveItem(item)"
    >
      <span :style="item.id === activeId ? { backgroundColor: '#FC425A' } : styleType.style2" />
      {{ item.name }}
    </div>
  </div>
</template>

<style lang="scss" scoped>
.left__mode-page {
  width: 100%;
  height: 100%;
  box-sizing: border-box;
  background-color: rgba(242, 242, 242, 1);

  .tab__item {
    display: block;
    height: 40px;
    line-height: 40px;
    font-size: 14px;
    color: #333;
    background-color: rgba(242, 242, 242, 1);
    cursor: pointer;
    user-select: none;
    box-sizing: border-box;
    padding-left: 12px;
    position: relative;
    overflow: hidden;
    box-sizing: border-box;
    padding-right: 6px;

    span {
      width: 4px;
      height: 15px;
      background-color: rgba(242, 242, 242, 1);
      top: 12px;
      position: absolute;
      left: 0;
    }
  }

  .tab__active {
    background-color: #fff;

    span {
      background-color: rgba(252, 66, 90, 1);
    }
  }
}
</style>
