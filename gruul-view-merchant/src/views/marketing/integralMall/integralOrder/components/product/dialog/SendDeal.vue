<script setup lang="ts">
import { getLogisticsCompany, setDefaultAddress } from '@/api/logistics/logistics'

const props = defineProps({
  detail: {
    type: Object,
    default: () => ({}),
  },
  visible: {
    type: Boolean,
    default: false,
  },
})

const formRef = ref()
const form = reactive({
  deliverCode: '',
  expressId: '',
  orderIds: [] as string[],
  printCode: '',
})

interface LogisticsCompany {
  code: string
  name: string
  isDefault: number
  logisticsCompanyId: string
  logisticsExpressAddressVos: any[]
}

const logisticsCompany = ref<LogisticsCompany[]>([])
const logisticsPrinterVos = ref<any[]>([])
const deliveComp = ref('') // 物流公司

// 监听visible变化
watch(
  () => props.visible,
  (newVal) => {
    if (newVal) {
      getCompany()
    }
  },
)

// 监听logisticsCompany变化
watch(
  () => logisticsCompany.value,
  (newVal) => {
    newVal.forEach((item) => {
      if (item.isDefault === 1) {
        item.name = `${item.name}(默认)`
      }
    })
  },
)

// 物流公司变化 设置发货地址列表
watch(
  () => form.deliverCode,
  () => {
    form.expressId = ''
  },
)

// 计算物流地址列表
const logisticsAddressVos = computed(() => {
  try {
    const company = logisticsCompany.value.find((item) => item.code === form.deliverCode)
    if (company) {
      return company.logisticsExpressAddressVos
    }
    return []
  } catch (err) {
    console.error(err)
    return []
  }
})

// 默认物流公司
const defLogisticsCompany = computed(() => {
  return logisticsCompany.value.find((item) => item.isDefault === 1) || undefined
})

// 计算物流公司按钮状态
const deliverCodeBtnStatus = computed(() => {
  const defComp = defLogisticsCompany.value as LogisticsCompany | undefined
  return !form.deliverCode || (defComp && defComp.code === form.deliverCode)
})

onMounted(() => {
  getCompany()
})

/**
 * 获取物流公司
 */
async function getCompany() {
  const res = await getLogisticsCompany({})
  logisticsCompany.value = res.data.logisticsCompanyVos
  logisticsPrinterVos.value = res.data.LogisticsPrinterVos
  logisticsCompany.value.forEach((item) => {
    if (item.isDefault === 1) {
      form.deliverCode = item.code
    }
  })
}

/** 设置默认发货公司 */
function setDefaultAddressHandler() {
  const company = logisticsCompany.value.find((item) => item.code === form.deliverCode)
  if (company) {
    setDefaultAddress(company.logisticsCompanyId).then(() => {
      getCompany()
      ElMessage.success('设置成功')
    })
  }
}

/**
 * 选择物流公司
 */
function changeSel(e: string) {
  deliveComp.value = e
}

// 导出变量和方法
defineExpose({
  form,
  logisticsCompany,
  logisticsPrinterVos,
  logisticsAddressVos,
  getCompany,
  setDefaultAddress: setDefaultAddressHandler,
  changeSel,
  formRef,
})
</script>

<template>
  <div class="sendDig">
    <div class="sendDig__line">
      <span class="sendDig__line--right">订单号</span>
      <span class="sendDig__line--left">{{ detail.prizeOrderId }}</span>
    </div>
    <div class="sendDig__line">
      <span class="sendDig__line--right">创建时间</span>
      <span class="sendDig__line--left">{{ detail.createTime }}</span>
    </div>
    <div class="sendDig__line">
      <span class="sendDig__line--right">地址</span>
      <span class="sendDig__line--left"> {{ detail.receiverName }},{{ detail.telPhone }},{{ detail.address }} </span>
    </div>
    <el-form ref="formRef" :model="form" label-width="92px">
      <el-form-item label="打印机" required>
        <el-select v-model="form.printCode" placeholder="选择打印机" style="width: 370px; margin-left: 25px">
          <el-option v-for="item of logisticsPrinterVos" :key="item.deviceKey" :label="item.deviceName" :value="item.deviceKey" />
        </el-select>
      </el-form-item>
      <el-form-item label="物流公司" required>
        <el-select v-model="form.deliverCode" placeholder="选择物流公司" style="width: 370px; margin-left: 25px">
          <el-option v-for="item of logisticsCompany" :key="item.code" :label="item.name" :value="item.code" />
        </el-select>
        <el-link type="primary" underline="never" style="margin-left: 4px" :disabled="deliverCodeBtnStatus" @click="setDefaultAddressHandler">
          设为默认
        </el-link>
      </el-form-item>
      <el-form-item label="发货地址" required>
        <el-select
          v-model="form.expressId"
          :disabled="!logisticsAddressVos.length"
          placeholder="选择发货地址"
          style="width: 370px; margin-left: 25px"
        >
          <el-option v-for="item of logisticsAddressVos" :key="item.code" :label="item.address" :value="item.expressId" />
        </el-select>
      </el-form-item>
    </el-form>
  </div>
</template>

<style lang="scss" scoped>
.sendDig {
  padding: 20px;
  &__line {
    display: flex;
    width: 100%;
    align-items: center;
    margin-bottom: 20px;
    &--right {
      display: flex;
      width: 80px;
      justify-content: flex-end;
    }
    &--left {
      margin-left: 40px;
    }
  }
}
</style>
