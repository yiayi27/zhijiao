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

const activeId = ref('-1')

// 创建一个单独的方法来更新activeId
function updateActiveId() {
  // 查找选中的分类
  const selectedCategory = formData.value.firstCatList.find((i) => i.isSelected)
  activeId.value = selectedCategory?.category?.id ?? '-1'
}

// 计算属性不应有副作用，只返回列表数据
const getHeadList = computed(() => {
  const list = formData.value.firstCatList.map((i) => {
    return {
      id: i.category?.id || '',
      name: i.category?.name || '',
    }
  })
  return [
    {
      id: '-1',
      name: '全部',
    },
    ...list,
  ]
})

// 监听firstCatList的变化，更新activeId
watch(
  () => formData.value.firstCatList,
  () => {
    updateActiveId()
  },
  { deep: true },
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

/**
 * 修改当前激活tab
 */
function changeActiveItem(item: (typeof getHeadList.value)[number]) {
  formData.value.firstCatList.forEach((i) => {
    i.isSelected = i.category?.id === item.id
  })
  activeId.value = item.id
  bus.emit('firstCategoryId', item.id)
}

/**
 * 选择完成一级分类需要初始化当前编辑的一级分类
 */
function initCurruntFirstCategory() {
  const { selectMode } = formData.value
  if (selectMode !== 1) return
  const item = formData.value.firstCatList.find((i) => i.isSelected)
  if (!item) {
    activeId.value = '-1'
  }
}

onMounted(() => {
  if (formData.value.firstCatList.length && formData.value.firstCatList.find((i) => i.isSelected)) {
    formData.value.firstCatList[0].isSelected = true
  }
  updateActiveId()
  bus.on('selectedFirstCategory', () => {
    initCurruntFirstCategory()
  })
})
</script>

<template>
  <div class="top__mode-page">
    <span
      v-for="(item, idx) in getHeadList"
      :key="idx"
      class="tab__item"
      :style="item.id === activeId ? styleType.style1 : styleType.style2"
      @click="changeActiveItem(item)"
    >
      {{ item.name }}
    </span>
  </div>
</template>

<style lang="scss" scoped>
.top__mode-page {
  width: 100%;
  height: 50px;
  overflow-x: scroll;
  white-space: nowrap;
  box-sizing: border-box;
  padding: 0px 5px;

  .tab__item {
    display: inline-block;
    padding: 6px 16px;
    border-radius: 40px;
    font-size: 15px;
    color: #333;
    background-color: #fff;
    cursor: pointer;
    user-select: none;
    margin-top: 10px;
  }

  .tab__active {
    background-color: rgba(253, 78, 100, 1);
    color: #fff;
  }
}
</style>
