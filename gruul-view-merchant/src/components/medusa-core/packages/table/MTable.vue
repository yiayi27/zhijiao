<script setup lang="ts">
import MTableBody from './MTableBody.vue'

export interface TableColumn {
  label?: string
  width?: Long
  prop?: string
  coustomStyle?: Record<string, string> | string
}

interface TableProps {
  data?: any[]
  selection?: boolean
  custom?: boolean
  needBorder?: boolean
  columns?: TableColumn[]
  single?: boolean
  tableHeaderClass?: string
  rowHeaderClass?: string
  needHoverBorder?: boolean
  checkedItem?: any
  rowKey?: (row: any) => Long
}

// 定义组件名称
defineOptions({
  name: 'MTable',
})

// 定义组件属性
const props = withDefaults(defineProps<TableProps>(), {
  data: () => [],
  selection: false,
  custom: false,
  needBorder: false,
  columns: () => [],
  single: false,
  tableHeaderClass: '',
  rowHeaderClass: '',
  needHoverBorder: false,
  checkedItem: null,
  rowKey: (row: Obj) => row.id,
})

// 定义emit
const emit = defineEmits<{
  'update:checkedItem': [value: any]
}>()

// 实例引用和插槽
const table = ref()
const slots = useSlots()

// 计算列数据
const getColumns = computed<TableColumn[]>(() => {
  return props.columns.length > 0
    ? props.columns
    : slots
        .default?.()
        ?.filter((component) => !!component.props)
        .map((component) => ({
          label: component.props?.label,
          width: component.props?.width,
          prop: component.props?.prop,
        })) || []
})

// 监听数据变化，更新选中项
watch(
  () => props.data,
  () => {
    nextTick(() => {
      emit('update:checkedItem', getCheckedItem())
    })
  },
  { immediate: true },
)

// 获取选中项
function getCheckedItem() {
  return table.value?.getCheckedItem()
}

// 选中项变化处理
function onCheck(v: any) {
  emit('update:checkedItem', v)
}

// 暴露给父组件的方法
defineExpose({
  getCheckedItem,
})
</script>

<template>
  <div class="m__table--container" :class="[single && 'single']">
    <MTableBody
      ref="table"
      :columns="getColumns"
      :data="data"
      :custom="custom"
      :selection="selection"
      :table-header-class="tableHeaderClass"
      :row-header-class="rowHeaderClass"
      :need-hover-border="needHoverBorder"
      :need-border="needBorder"
      :row-key="rowKey"
      @on-check="onCheck"
    >
      <template v-if="$slots.header" #header="slotProps">
        <slot name="header" v-bind="slotProps" />
      </template>
      <template v-if="$slots['custom-body']" #custom-body="slotProps">
        <slot name="custom-body" v-bind="slotProps" />
      </template>
      <template v-if="$slots.default" #default="slotProps">
        <slot v-bind="slotProps" />
      </template>
    </MTableBody>
  </div>
</template>

<style lang="scss" src="../styles/m-table.scss"></style>
