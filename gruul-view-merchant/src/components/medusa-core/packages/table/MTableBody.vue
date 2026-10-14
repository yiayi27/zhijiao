<script setup lang="ts">
import ElTableEmpty from '@/components/ElTableEmpty/index.vue'
import { cloneDeep } from 'lodash-es'

import MTableRow from './MTableRow.vue'
import type { TableColumn } from './MTable.vue'

defineOptions({
  name: 'MTableBody',
})

// 定义组件属性
const props = withDefaults(defineProps<TableBodyProps>(), {
  columns: () => [],
  data: () => [],
  selection: false,
  custom: false,
  tableHeaderClass: '',
  rowHeaderClass: '',
  needHoverBorder: false,
  needBorder: false,
})

// 定义事件
const emit = defineEmits(['onCheck'])

// 定义列的接口

interface TableBodyProps {
  columns?: TableColumn[]
  data?: any[]
  selection?: boolean
  custom?: boolean
  tableHeaderClass?: string
  rowHeaderClass?: string
  needHoverBorder?: boolean
  needBorder?: boolean
  rowKey: (row: Obj) => string | number
}

// 表格数据响应式
const tableData = ref<any[]>([])

// 全选计算属性
const checkAll = computed({
  get: () => tableData.value.length && tableData.value.filter((item) => item.checked).length === tableData.value.length,
  set: (v: boolean) => {
    onCheckAllChange(v)
    emit('onCheck', getCheckedItem())
  },
})

// 半选状态计算属性
const isIndeterminate = computed(() => {
  const len = tableData.value.filter((item) => item.checked).length
  return len !== 0 && len !== tableData.value.length
})

// 监听数据变化
watch(
  () => props.data,
  (v) => {
    tableData.value = cloneDeep(v).map((row) => ({ ...row, checked: false }))
  },
  { deep: true, immediate: true },
)

// 获取选中项方法
function getCheckedItem() {
  return tableData.value.filter((item) => item.checked)
}

// 全选事件处理
function onCheckAllChange(v: boolean) {
  tableData.value = tableData.value.map((item) => ({ ...item, checked: v }))
}

// 单项选择事件处理
function onItemCheckChange(checked: boolean, index: number) {
  tableData.value[index].checked = checked
  emit('onCheck', getCheckedItem())
}

// 暴露方法给父组件
defineExpose({
  getCheckedItem,
})
</script>

<template>
  <table class="m__table" cellspacing="0" cellpadding="0">
    <colgroup>
      <col v-for="(item, index) in columns" :key="index" :width="item.width" />
    </colgroup>

    <thead class="m__table--head" :class="[tableHeaderClass, $slots.header && 'padding']">
      <tr>
        <th v-for="(item, i) in columns" :key="i" :style="item.coustomStyle" :class="[{ 'flex items-center': i === 0 }]">
          <el-checkbox v-if="i === 0 && selection" v-model="checkAll" :indeterminate="isIndeterminate" />
          {{ item.label }}
        </th>
      </tr>
    </thead>

    <tbody v-if="!tableData.length" class="m__table--empty">
      <tr>
        <td class="empty__td" :colspan="columns.length" align="center">
          <ElTableEmpty />
        </td>
      </tr>
    </tbody>

    <tbody
      v-for="(row, index) in tableData"
      :key="rowKey(row)"
      class="m__table--body"
      :class="[custom ? 'custom' : 'default', needBorder && 'need--border', needHoverBorder && row.close ? 'hover--class' : 'ordinary--class']"
    >
      <tr v-if="$slots.header">
        <td :colspan="columns.length">
          <div class="body--header" :class="[rowHeaderClass, { close: row.close }]">
            <el-checkbox
              v-if="selection"
              :key="`HeaderCheckbox${rowKey(row)}${index}`"
              :model-value="row.checked"
              @change="(val: any) => onItemCheckChange(val, index)"
            />
            <slot name="header" :row="row" :index="index" />
          </div>
        </td>
      </tr>

      <template v-if="custom">
        <slot name="custom-body" :row="row" :index="index" />
      </template>

      <MTableRow v-else :row="row" :row-key="rowKey" :selection="selection" @change="(val) => onItemCheckChange(val, index)">
        <slot name="default" :row="row" />
      </MTableRow>
    </tbody>
  </table>
</template>
