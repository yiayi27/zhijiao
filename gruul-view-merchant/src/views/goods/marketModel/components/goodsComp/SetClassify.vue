<script setup lang="ts">
import { getAllRegionList, updateGoodShow } from '@/api/good/goods'
import SetDrop from '@/components/SetDrop/SetDrop.vue' // 编辑商品分类

import GoodCategory from './GoodCategory.vue'

// Props定义
const props = defineProps({
  isItem: {
    type: Boolean,
    default: false,
  },
  goodIds: {
    type: Array as () => number[],
    default: () => [],
  },
  showGetList: {
    type: Array,
    default: () => [],
  },
  saleMode: {
    type: String,
    default: '',
  },
})

// Emits定义
const emit = defineEmits(['click', 'command', 'handleItemCommand', 'changeIds', 'getSelectedGoods'])

// 响应式数据
const popVisible = ref(false)
const regionList = ref<any[]>([])
const goodCate = ref<any>(null)

// 下拉菜单列表
const defaultDropList = [
  {
    text: '批量上架',
    command: '6',
    disabled: false,
  },
  {
    text: '批量下架',
    command: '7',
    disabled: false,
  },
  {
    text: '删除',
    command: '8',
    disabled: false,
  },
]

const dropdownList = computed(() => {
  const drop = regionList.value.filter((v) => v.id !== props.saleMode)
  return [...drop, ...defaultDropList]
})

function getClassify() {
  getAllRegionList({}).then((res) => {
    regionList.value = res.data.map((item: any) => {
      return {
        ...item,
        text: `移至${item.modeName}`,
        show: true,
        disabled: false,
        command: item.id,
      }
    })
  })
}

/**
 * 点击按钮
 */
function handleClick(e: MouseEvent) {
  emit('click', e)
}

/**
 * 点击的下拉项
 */
function handleCommand(command: string) {
  emit('command', command)
}

function getNewCateList(list: any[]) {
  if (props.goodIds.length === 0) {
    ElMessage.error('请先选择商品')
    return
  }
  const temList: any[] = list.filter((v) => v.showCategoryVos.length > 0)
  updateGoodShow(props.goodIds.join(','), JSON.parse(JSON.stringify(temList).replace(/showCategoryVos/g, 'productShowCategorySeconds'))).then(
    (res) => {
      if (res.code === 200) {
        ElMessage.success(`编辑成功`)
        popVisible.value = false
        emit('changeIds')
      }
    },
  )
}

onMounted(() => {
  getClassify()
})
</script>

<template>
  <SetDrop :dropdown-list="dropdownList" @command="handleCommand" @set-click="handleClick">
    <el-popover v-if="!isItem" v-model:visible="popVisible" placement="bottom" width="260" trigger="hover">
      <GoodCategory
        ref="goodCate"
        cate-type="2"
        :pop-visible="popVisible"
        :show-get-list="showGetList"
        :sale-mode="saleMode"
        :ids-list="goodIds"
        style="width: 260px"
        @get-new-cate-list="getNewCateList"
        @ch-pop-visible="popVisible = false"
      />
      <template #reference>
        <span>
          <slot>设置分类</slot>
        </span>
      </template>
    </el-popover>
    <el-popover v-if="isItem" v-model:visible="popVisible" placement="bottom" width="260" trigger="click">
      <template #reference>
        <span>
          <slot>编辑</slot>
        </span>
      </template>
    </el-popover>
  </SetDrop>
</template>
