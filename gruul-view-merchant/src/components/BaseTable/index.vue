<script setup lang="ts">
import type { TableInstance } from 'element-plus'
import ElTableEmpty from '@/components/ElTableEmpty/index.vue'
import TableProps from 'element-plus/es/components/table/src/table/defaults'

defineOptions({
  name: 'BaseTable',
})

const props = defineProps({
  ...TableProps,
  isFull: {
    type: Boolean,
    default: true,
  },
})
const refElTable = ref()
const attr = useAttrs()
const getProps = computed(() => ({ ...attr, ...props }))

defineExpose<TableInstance>(
  new Proxy(
    {},
    {
      get: (_target, prop) => refElTable.value?.[prop],
      has: (_target, prop) => prop in (refElTable.value || {}),
    },
  ) as TableInstance,
)
</script>

<template>
  <div class="base-table" :class="{ 'flex flex-col flex-1 h-full': isFull }">
    <el-table ref="refElTable" v-bind="getProps" :class="{ 'flex-1': isFull }">
      <template #empty>
        <slot name="empty">
          <ElTableEmpty />
        </slot>
      </template>

      <template v-for="name in Object.keys($slots)" :key="name" #[name]="data">
        <slot v-bind="data" :name="name" />
      </template>
    </el-table>
  </div>
</template>

<style scoped lang="scss"></style>
