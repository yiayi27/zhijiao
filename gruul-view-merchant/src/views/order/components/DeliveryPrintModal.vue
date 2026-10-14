<script setup lang="ts">
interface FormData {
  deliverCode: string
  expressId: string
  orderIds: string[]
  printCode: string
  shopId: string
}

interface LogisticsCompanyItem {
  code: string
  name: string
  isDefault?: number
  logisticsExpressAddressVos?: LogisticsAddressItem[]
  [key: string]: any
}

interface LogisticsAddressItem {
  code: string
  address: string
  expressId: string
  [key: string]: any
}

interface LogisticsPrinterItem {
  deviceKey: string
  deviceName: string
  [key: string]: any
}

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: true,
  },
  currentOrder: {
    type: Object,
    default: () => ({}),
  },
  logisticsCompany: {
    type: Array as () => LogisticsCompanyItem[],
    default: () => [],
  },
  logisticsPrinterVos: {
    type: Array as () => LogisticsPrinterItem[],
    default: () => [],
  },
})

const emit = defineEmits(['update:modelValue', 'onConfirm'])

const form = ref<FormData>({
  deliverCode: '',
  expressId: '',
  orderIds: [],
  printCode: '',
  shopId: '',
})

const logisticsAddressVos = computed(() => {
  try {
    const foundCompany = props.logisticsCompany.find((item) => item.code === form.value.deliverCode)
    return foundCompany?.logisticsExpressAddressVos || []
  } catch (err) {
    console.error(err)
    return []
  }
})

/** 模态框显示隐藏 */
const visible = computed({
  get: () => props.modelValue,
  set: (v) => {
    emit('update:modelValue', v)
  },
})

/** 默认物流公司 */
const defLogisticsCompany = computed(() => {
  const foundCompany = props.logisticsCompany.find((item) => item.isDefault === 1)
  return foundCompany || ({} as LogisticsCompanyItem)
})

/** 监听弹窗变化 派发对应事件 */
watch(
  () => visible.value,
  (v) => {
    if (v) {
      form.value.deliverCode = defLogisticsCompany.value.code || ''
    } else {
      nextTick(() => {
        resetForm()
      })
    }
  },
)

/** 物流公司变化 设置发货地址列表 */
watch(
  () => form.value.deliverCode,
  () => {
    form.value.expressId = ''
  },
)

function toggleVisible(): void {
  visible.value = !visible.value
}

function resetForm(): void {
  form.value = {
    deliverCode: '',
    expressId: '',
    orderIds: [],
    printCode: '',
    shopId: '',
  }
}

/** 确认发货 */
function onConfirm(): void {
  emit('onConfirm', form.value)
}
</script>

<template>
  <el-dialog v-model="visible" title="商品发货" class="send" width="450px">
    <el-row :gutter="10" class="send__field">
      <el-col :span="24">
        <el-form ref="form" :model="form" label-width="80px">
          <el-form-item label="打印机" required>
            <el-select v-model="form.printCode" placeholder="选择打印机" style="width: 100%">
              <el-option v-for="item of logisticsPrinterVos" :key="item.deviceKey" :label="item.deviceName" :value="item.deviceKey" />
            </el-select>
          </el-form-item>
          <el-form-item label="物流公司" required>
            <el-select v-model="form.deliverCode" placeholder="选择物流公司" style="width: 100%">
              <el-option v-for="item of logisticsCompany" :key="item.code" :label="item.name" :value="item.code" />
            </el-select>
          </el-form-item>
          <el-form-item label="发货地址" required>
            <el-select v-model="form.expressId" :disabled="!logisticsAddressVos.length" placeholder="选择发货地址" style="width: 100%">
              <el-option v-for="item of logisticsAddressVos" :key="item.code" :label="item.address" :value="item.expressId" />
            </el-select>
          </el-form-item>
        </el-form>
      </el-col>
    </el-row>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="toggleVisible">取 消</el-button>
        <el-button type="primary" @click="onConfirm">确 定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped>
.send {
  &__field {
    font-size: 13px;

    .el-form-item {
      margin-bottom: 10px;
    }
  }
}
</style>
