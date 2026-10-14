<script setup lang="ts">
import { useVModel } from '@vueuse/core'

// 定义props
const props = defineProps({
  pageSize: {
    type: Number,
    default: 20,
  },
  pageNum: {
    type: Number,
    default: 1,
  },
  pageSizes: {
    type: Array,
    default: () => [10, 20, 50, 100],
  },
  total: {
    type: Number,
    default: 0,
  },
  layout: {
    type: String,
    default: 'total, prev, pager, next, sizes',
  },
})

// 定义emit
const emit = defineEmits(['handleSizeChange', 'handleCurrentChange', 'update:total', 'update:size', 'update:current'])

// 计算属性
const _pageSize = useVModel(props, 'pageSize', emit)
const _pageNum = useVModel(props, 'pageNum', emit)
const _total = useVModel(props, 'total', emit)
const _pageSizes = computed(() => props.pageSizes as number[])

// 事件处理函数
function handleSizeChange(val: number) {
  emit('handleSizeChange', val)
}

function handleCurrentChange(val: number) {
  emit('handleCurrentChange', val)
}
</script>

<template>
  <div class="pagination">
    <div class="pagination__left">
      <slot name="pagination--left" />
    </div>

    <el-pagination
      v-model:page-size="_pageSize"
      v-model:current-page="_pageNum"
      v-model:total="_total"
      :page-sizes="_pageSizes"
      :layout="layout"
      background
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<style lang="scss" scoped>
.pagination {
  display: flex;
  justify-content: space-between;
  //margin-top: 20px;
}
</style>
