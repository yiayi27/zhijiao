<script setup lang="ts">
import BaseTable from '@/components/BaseTable/index.vue'
import { useVModel } from '@vueuse/core'
import Sortable from 'sortablejs'

import { useRouter } from 'vue-router'

// 定义类型接口
interface RegionItem {
  id: string
  modeName: string
  productNumber: number
  modeType: string

  [key: string]: any
}

// 定义props
const props = defineProps({
  regionList: {
    type: Array as () => RegionItem[],
    default: () => [],
  },
})
// 定义emits
const emit = defineEmits(['sort', 'editFucntion', 'deleteRegionFunction', 'update:regionList'])
const regionList = useVModel(props, 'regionList', emit)

const router = useRouter()

// 表格引用
const multipleTable = ref(null)

function rowClassName({ rowIndex }: { rowIndex: number }) {
  // 为除了第一行外的所有行添加额外样式
  return 'el-table__row__drag'
}

onMounted(() => {
  const table: HTMLElement | null = document.querySelector('.el-table__body-wrapper tbody')
  if (table) {
    Sortable.create(table, {
      animation: 150,
      draggable: '.draggable-table .el-table__row__drag',
      onEnd: (ent: { oldIndex?: number; newIndex?: number }) => {
        const oldIndex = ent.oldIndex || 0
        const newIndex = ent.newIndex || 0
        const length = regionList.value.length
        if (length > 0) {
          const currRow = regionList.value.splice(oldIndex, 1)[0]
          regionList.value.splice(newIndex, 0, currRow)
        }
        sort()
      },
    })
  }
})

/**
 * 排序功能
 */
function sort() {
  const list = JSON.parse(JSON.stringify(regionList.value))
  emit('sort', list)
}

/**
 * 设置分类
 */
function gotoClassSet(item: RegionItem) {
  const id = item.id
  router.push({
    name: 'GoodClass',
    query: { options: 'classSet', id },
  })
}

/**
 * 编辑专区弹窗
 */
function editDailog(item: RegionItem) {
  const newItem = JSON.parse(JSON.stringify(item))
  emit('editFucntion', newItem)
}

/**
 * 删除专区
 */
function deleteRegion(item: RegionItem) {
  emit('deleteRegionFunction', item)
}
</script>

<template>
  <BaseTable
    ref="multipleTable"
    class="draggable-table"
    :data="regionList"
    tooltip-effect="dark"
    style="width: 100%"
    :row-class-name="rowClassName"
    row-key="id"
    :row-style="{ height: '50px', cursor: 'move' }"
  >
    <el-table-column label="专区名称" width="300">
      <template #default="scope">
        <span style="color: #2d8cf0">{{ scope.row.modeName }}</span>
      </template>
    </el-table-column>
    <el-table-column label="商品数量" width="300">
      <template #default="scope">
        <div style="width: 50px; display: flex; justify-content: center">
          {{ scope.row.productNumber }}
        </div>
      </template>
    </el-table-column>
    <el-table-column label="操作">
      <template #default="scope">
        <div class="dealWay">
          <span style="color: #2d8cf0" @click="gotoClassSet(scope.row)">设置分类</span>
          <span style="color: #2d8cf0" @click="editDailog(scope.row)">编辑</span>
          <span style="color: #fa4e74" @click="deleteRegion(scope.row)">删除</span>
        </div>
      </template>
    </el-table-column>
  </BaseTable>
</template>

<style lang="scss" scoped>
.dealWay {
  display: flex;
  cursor: pointer;
  width: 150px;
  justify-content: space-between;
}
</style>
