<script setup lang="ts">
import { setDefaultAddress } from '@/api/logistics/logistics'

interface LogisticsPrinterVo {
  id: string
  deviceKey: string
  deviceName: string
}

interface LogisticsCompanyItem {
  code: string
  name: string
  isDefault: number
  logisticsCompanyId: string
  logisticsExpressAddressVos: LogisticsAddressVo[]
}

interface LogisticsAddressVo {
  code: string
  address: string
  expressId: string
}

interface OrderItem {
  orderId: string
  createTime: string
  receiverName: string
  receiverPhone: string
  receiverProvince: string
  receiverCity: string
  receiverRegion: string
  receiverDetailAddress: string
}

interface FormData {
  deliverCode: string
  expressId: string
  orderIds: string[]
  printCode: string
  shopId: string
  logisticsExpressPrintId: string
}

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  },
  currentOrder: {
    type: Object as () => OrderItem,
    default: () => ({}),
  },
  logisticsCompany: {
    type: Array as () => LogisticsCompanyItem[],
    default: () => [],
  },
  logisticsPrinterVos: {
    type: Array as () => LogisticsPrinterVo[],
    default: () => [],
  },
})

const emit = defineEmits(['update:modelValue', 'reset', 'onConfirm'])

// 使用响应式引用而不是计算属性来直接控制对话框
const dialogVisible = ref(props.modelValue)
const formRef = ref<HTMLFormElement | null>(null)

// 创建一个响应式表单对象
const form = ref<FormData>({
  deliverCode: '',
  expressId: '',
  orderIds: [],
  printCode: '',
  shopId: '',
  logisticsExpressPrintId: '',
})

// 监听父组件传入的modelValue变化
watch(
  () => props.modelValue,
  (newVal) => {
    dialogVisible.value = newVal
    if (newVal) {
      initForm()
    }
  },
  { immediate: true },
)

// 监听本地对话框状态变化，通知父组件
watch(dialogVisible, (newVal) => {
  emit('update:modelValue', newVal)
  if (!newVal) {
    resetForm()
  }
})

const company = computed(() => {
  return props.logisticsCompany.map((item: LogisticsCompanyItem) => {
    const newItem = { ...item }
    if (newItem.isDefault === 1) {
      newItem.name = `${newItem.name}(默认)`
    }
    return newItem
  })
})

const logisticsAddressVos = computed(() => {
  try {
    const found = props.logisticsCompany.find((item: LogisticsCompanyItem) => item.code === form.value.deliverCode)

    return found?.logisticsExpressAddressVos || []
  } catch (err) {
    console.error('获取发货地址列表失败', err)
    return []
  }
})

/** 默认物流公司 */
const defLogisticsCompany = computed(() => {
  return (
    props.logisticsCompany.find((item: LogisticsCompanyItem) => item.isDefault === 1) || {
      code: '',
    }
  )
})

const deliverCodeBtnStatus = computed(() => {
  return !form.value.deliverCode || (defLogisticsCompany.value && defLogisticsCompany.value.code === form.value.deliverCode)
})

// 初始化表单
function initForm(): void {
  const defaultCode = defLogisticsCompany.value?.code || ''

  form.value = {
    deliverCode: defaultCode,
    expressId: '',
    orderIds: [],
    printCode: '',
    shopId: '',
    logisticsExpressPrintId: '',
  }
}

// 重置表单
function resetForm(): void {
  form.value = {
    deliverCode: '',
    expressId: '',
    orderIds: [],
    printCode: '',
    shopId: '',
    logisticsExpressPrintId: '',
  }
}

// 关闭对话框
function closeDialog(): void {
  dialogVisible.value = false
}

/** 设置默认发货公司 */
function handleSetDefaultAddress(): void {
  if (!form.value.deliverCode) {
    ElMessage.warning('请先选择物流公司')
    return
  }

  const found = props.logisticsCompany.find((item: LogisticsCompanyItem) => item.code === form.value.deliverCode)
  if (!found) {
    ElMessage.warning('未找到物流公司信息')
    return
  }
  const logisticsCompanyId = found.logisticsCompanyId

  setDefaultAddress(logisticsCompanyId).then(() => {
    emit('reset')
    ElMessage.success('设置成功')
  })
}

function onConfirm(): void {
  if (!form.value.deliverCode) {
    ElMessage.warning('请选择物流公司')
    return
  }

  if (!form.value.expressId) {
    ElMessage.warning('请选择发货地址')
    return
  }

  if (!form.value.printCode) {
    ElMessage.warning('请选择打印机')
    return
  }

  const formData = {
    ...form.value,
    orderIds: [props.currentOrder.orderId],
  }

  emit('onConfirm', formData)
  closeDialog()
}

function changePrintCode(val: string): void {
  const found = props.logisticsPrinterVos.find((item: LogisticsPrinterVo) => item.deviceKey === val)
  form.value.logisticsExpressPrintId = found?.id ?? ''
}
</script>

<template>
  <el-dialog v-model="dialogVisible" title="商品发货" class="send" width="550px">
    <el-row :gutter="10" class="send__field">
      <el-col v-if="currentOrder" :span="24">
        <el-form label-width="80px">
          <el-form-item label="订单号">
            {{ currentOrder.orderId }}
          </el-form-item>
          <el-form-item label="创建时间">
            {{ currentOrder.createTime }}
          </el-form-item>
          <el-form-item label="地址">
            {{ currentOrder.receiverName }},{{ currentOrder.receiverPhone }}
            {{ currentOrder.receiverProvince }}
            {{ currentOrder.receiverCity }}
            {{ currentOrder.receiverRegion }}
            {{ currentOrder.receiverDetailAddress }}
          </el-form-item>
        </el-form>
      </el-col>
      <!-- <el-col :span="24">
        创建时间： 创建时间
      </el-col>
      <el-col :span="24">
        地址：地址
      </el-col> -->
      <el-col :span="24">
        <el-form ref="formRef" :model="form" label-width="80px">
          <el-form-item label="打印机" required>
            <el-select v-model="form.printCode" placeholder="选择打印机" style="width: 370px" @change="changePrintCode">
              <el-option v-for="item in logisticsPrinterVos" :key="item.id" :label="item.deviceName" :value="item.deviceKey" />
            </el-select>
          </el-form-item>
          <el-form-item label="物流公司" required>
            <el-select v-model="form.deliverCode" placeholder="选择物流公司" style="width: 370px">
              <el-option v-for="item in company" :key="item.code" :label="item.name" :value="item.code">
                {{ item.name }}
              </el-option>
            </el-select>
            <el-link type="primary" underline="never" style="margin-left: 4px" :disabled="deliverCodeBtnStatus" @click="handleSetDefaultAddress">
              设为默认
            </el-link>
          </el-form-item>
          <el-form-item label="发货地址" required>
            <el-select v-model="form.expressId" :disabled="!logisticsAddressVos.length" placeholder="选择发货地址" style="width: 370px">
              <el-option v-for="item in logisticsAddressVos" :key="item.expressId" :label="item.address" :value="item.expressId" />
            </el-select>
          </el-form-item>
        </el-form>
      </el-col>
    </el-row>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="closeDialog">取 消</el-button>
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
