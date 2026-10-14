<script setup lang="ts">
import { useVModel } from '@vueuse/core'

import AfterSaleInfo from './AfterSaleInfo.vue'
import Delivery from './Delivery.vue'
import Info, { type OrderDetail } from './Info.vue'

const props = defineProps({
  detail: {
    type: Object as () => OrderDetail,
    default: () => ({
      id: '',
      status: '',
    }),
  },
  afterInfo: {
    type: Object,
    default: () => ({}),
  },
  needTabs: {
    type: Boolean,
    default: true,
  },
  isDelivery: {
    type: Boolean,
    default: false,
  },
  isAfter: {
    type: Boolean,
    default: false,
  },
  modelValue: {
    type: Boolean,
    default: false,
  },
  type: {
    type: String,
    default: '1',
  },
  isGroup: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  reset: [data: any]
}>()

const deliveryVisible = computed(() => {
  return props.detail && props.detail.orderDelivery?.deliverySn
})

const visible = useVModel(props, 'modelValue', emit)

function reset(data: any): void {
  emit('reset', data)
}
</script>

<template>
  <el-dialog v-model="visible" class="detail__modal">
    <el-tabs v-if="needTabs" :model-value="type">
      <el-tab-pane v-if="detail" label="订单信息" name="1">
        <Info :detail="detail" @reset="reset" />
      </el-tab-pane>
      <el-tab-pane v-if="deliveryVisible && detail" label="物流信息" name="2">
        <Delivery :delivery-info="detail.orderDelivery" />
      </el-tab-pane>
      <el-tab-pane v-if="isAfter" label="售后信息" name="4">
        <AfterSaleInfo :detail="detail" :after-info="afterInfo" @reset="reset" />
      </el-tab-pane>
    </el-tabs>
    <Info v-else :detail="detail" />
  </el-dialog>
</template>

<style lang="scss" scoped>
.detail__modal {
  .el-dialog__body {
    padding: 10px 20px 30px;
  }
}
</style>
