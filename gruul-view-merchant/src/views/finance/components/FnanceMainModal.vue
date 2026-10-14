<script setup lang="ts">
import OrderInfo, { type OrderDetail } from '@/views/order/components/detail/Info.vue'

import FnanceMainModalTable from './FnanceMainModalTable.vue'
import OrderTable from './OrderTable.vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false,
  },
  currentData: {
    type: Object,
    default: () => ({}),
  },
  detail: {
    type: Object as PropType<OrderDetail>,
    default: () => ({}),
  },
  detailIsOrder: {
    type: Boolean,
    default: false,
  },
  title: {
    type: String,
    default: '',
  },
})

const emit = defineEmits(['update:visible'])

const dialogVisible = computed({
  get: () => props.visible,
  set: (value) => emit('update:visible', value),
})

function toggleVisible() {
  dialogVisible.value = !dialogVisible.value
}
</script>

<template>
  <el-dialog v-model="dialogVisible" class="fnance__modal" :title="title || '详情'" width="850px">
    <template v-if="currentData.type === 'INCENTIVE'">
      <OrderTable :current-data="currentData" />
    </template>
    <template v-if="currentData.type === 'MONTHLY' || currentData.type === 'WITHDRAWAL'" />
    <template v-else>
      <FnanceMainModalTable v-if="!detailIsOrder" :detail="detail" />
      <OrderInfo v-else :detail="detail" />
    </template>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="toggleVisible">取 消</el-button>
        <el-button type="primary" @click="toggleVisible">确 定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped></style>
