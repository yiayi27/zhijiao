<script setup lang="ts">
import MContent from '@/components/MContainer/MContent.vue'
import PageManage from '@/components/PageManage.vue'
import { useVModel } from '@vueuse/core'

defineOptions({
  name: 'MContainer',
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
  showSameSlot: {
    type: Boolean,
    default: false,
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
  <div class="m__container">
    <div v-if="$slots.top" class="m__container--top">
      <slot name="top" />
    </div>

    <div v-if="$slots.tabs" class="m__container--tabs">
      <slot name="tabs" />
    </div>

    <div v-if="$slots.form && ($slots.content || showSameSlot)" class="m__container--form">
      <slot name="form" />
    </div>

    <div v-if="$slots.section && ($slots.content || showSameSlot)" class="m__container--section">
      <slot name="section" />
    </div>

    <slot name="content">
      <MContent v-if="$slots.form || $slots.section || $slots.table || $slots.pagination || $slots['pagination--left']" v-bind="$attrs">
        <template v-if="$slots.form" #form>
          <slot name="form" />
        </template>

        <template v-if="$slots.section" #section>
          <slot name="section" />
        </template>

        <template v-if="$slots.table" #table>
          <slot name="table" />
        </template>

        <template v-if="showPagination || $slots.pagination" #pagination>
          <div class="pagination-area">
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
        </template>

        <template #other />
      </MContent>
    </slot>

    <slot name="default" />
  </div>
</template>

<style lang="scss" scoped>
.m {
  &__container {
    width: 100%;
    height: 100%;
    position: relative;
    display: flex;
    flex-direction: column;

    &--form,
    &--section,
    &--top {
      margin-bottom: 10px;
    }

    :deep() {
      .m__content {
        overflow: hidden;
      }

      .m__table--head {
        position: sticky;
        top: 0;
        z-index: 99;
      }

      > .el-table {
        height: 100%;
      }
    }
  }
}
</style>
