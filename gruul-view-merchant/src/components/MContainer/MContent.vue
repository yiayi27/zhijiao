<script setup lang="ts">
import PageManage from '@/components/PageManage.vue'
import { useVModel } from '@vueuse/core'

defineOptions({
  name: 'MContent',
})

const props = defineProps({
  showPagination: {
    type: [Boolean, String, Number],
    default: false,
  },
  total: {
    type: Number,
    default: 0,
  },
  size: {
    type: Number,
    default: 10,
  },
  current: {
    type: Number,
    default: 1,
  },
})

const emit = defineEmits(['update:total', 'update:size', 'update:current', 'handleSizeChange', 'handleCurrentChange'])

const pageTotal = useVModel(props, 'total', emit)
const pageSize = useVModel(props, 'size', emit)
const pageCurrent = useVModel(props, 'current', emit)

function handleSizeChange(val: number) {
  emit('handleSizeChange', val)
}

function handleCurrentChange(val: number) {
  emit('handleCurrentChange', val)
}
</script>

<template>
  <div class="m__content">
    <div v-if="$slots.form" class="m__content--form">
      <slot name="form" />
    </div>

    <div v-if="$slots.section" class="m__content--section">
      <slot name="section" />
    </div>

    <div v-if="Object.keys($slots).length !== 0 && $slots.table" class="m__content--table">
      <slot name="table" />
    </div>

    <div v-if="showPagination || $slots.pagination" class="m__content--pagination">
      <slot name="pagination">
        <PageManage
          v-model:page-size="pageSize"
          v-model:page-num="pageCurrent"
          v-model:total="pageTotal"
          @handle-size-change="handleSizeChange"
          @handle-current-change="handleCurrentChange"
        >
          <template #pagination--left>
            <slot name="pagination--left" />
          </template>
        </PageManage>
      </slot>
    </div>

    <slot name="other" />

    <slot name="default" />
  </div>
</template>

<style lang="scss">
.m {
  &__content {
    flex: 1;
    width: 100%;
    height: 100%;
    position: relative;
    display: flex;
    flex-direction: column;

    &--form {
      margin-bottom: 10px;
    }

    &--section {
      margin-bottom: 10px;
    }

    &--table {
      flex: 1;
      overflow: auto;
      display: flex;
      flex-direction: column;
      margin-bottom: 10px;
    }
  }
}
</style>
