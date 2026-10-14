<script setup lang="ts">
import { sendintegralGoodByPrint, sendOneintegralGood } from '@/api/integral/integral'
import {
  getLogisticsCompany as getLogisticsCompanyApi,
  importAllOrder,
  sendNoLogistic,
  setDefaultAddress as setDefaultAddressApi,
} from '@/api/logistics/logistics'
import { ElMessageBox } from 'element-plus'
import { storage } from '@/libs/storage'

import { useRoute, useRouter } from 'vue-router'

defineOptions({
  name: 'DeliverGoods',
})

// 定义接口
interface OrderItem {
  prizeOrderId: string
  createTime?: string
  deliveryCompany?: string
  deliverySn?: string
  deliveComp?: string
  [key: string]: any
}

interface LogisticsCompany {
  code: string
  name: string
  isDefault: number
  logisticsCompanyId?: string
  prizeOrderId?: string
  logisticsExpressAddressVos: any[]
  deliveComp?: string
  [key: string]: any
}

interface LogisticsPrinter {
  deviceKey: string
  deviceName: string
  [key: string]: any
}

const router = useRouter()
const route = useRoute()

// 表单数据
const lQuery = reactive({
  type: '手动发货',
  printCode: '',
  deliverCode: '',
  expressId: '',
})

// 数据
const logisticsCompany = ref<LogisticsCompany[]>([])
const orderList = ref<OrderItem[]>([])
const logisticsPrinterVos = ref<LogisticsPrinter[]>([])

// 计算属性
const isManual = computed(() => {
  return lQuery.type === '手动发货'
})

/**
 * 是否为无需物流发货
 */
const isNotLogistics = computed(() => {
  return lQuery.type === '无需物流'
})

/** 默认物流公司 */
const defLogisticsCompany = computed<LogisticsCompany>(() => {
  return (
    logisticsCompany.value.find((item) => item.isDefault === 1) || {
      code: '',
      name: '',
      isDefault: 0,
      logisticsExpressAddressVos: [],
    }
  )
})

/** 设置默认按钮状态 */
const deliverCodeBtnStatus = computed(() => {
  return !lQuery.deliverCode || (defLogisticsCompany.value && defLogisticsCompany.value.code === lQuery.deliverCode)
})

const logisticsAddressVos = computed(() => {
  try {
    const company = logisticsCompany.value.find((item) => item.code === lQuery.deliverCode)
    return company ? company.logisticsExpressAddressVos : []
  } catch (err) {
    console.error(err)
    return []
  }
})

// 监听物流公司变化
watch(
  () => lQuery.deliverCode,
  (newValue) => {
    if (newValue) {
      orderList.value = orderList.value.map((item) => {
        item.deliveryCompany = newValue
        return item
      })
    }
  },
)

// 组件挂载时执行
onMounted(() => {
  // 从storage中获取订单数据
  const savedOrders = storage.get('INTEGRAL_DELIVERY_ORDERS')
  if (savedOrders && savedOrders.length > 0) {
    orderList.value = savedOrders
    // 获取数据后清除storage中的数据，避免重复使用
    storage.delete('INTEGRAL_DELIVERY_ORDERS')
  }
  getLogisticsCompany()
})

/**
 * 获取物流公司与打印机
 */
function getLogisticsCompany() {
  getLogisticsCompanyApi({}).then((res) => {
    logisticsCompany.value = res.data.logisticsCompanyVos
    logisticsPrinterVos.value = res.data.LogisticsPrinterVos
    logisticsCompany.value.forEach((item) => {
      if (item.isDefault === 1) {
        lQuery.deliverCode = item.code
        item.name = `${item.name}(默认)`
      }
    })
  })
}

/** 设置默认发货公司 */
function setDefaultAddress() {
  const company = logisticsCompany.value.find((item) => item.code === lQuery.deliverCode)

  if (!company || !company.logisticsCompanyId) {
    ElMessage.warning('未找到物流公司信息')
    return
  }

  setDefaultAddressApi(company.logisticsCompanyId).then(() => {
    getLogisticsCompany()
    ElMessage.success('设置成功')
  })
}

/**
 * 导入所有待发货订单
 */
function handleImportAll() {
  importAllOrder({}).then((res) => {
    orderList.value = res.data
    if (defLogisticsCompany.value && defLogisticsCompany.value.code) {
      orderList.value.forEach((item) => {
        item.deliveryCompany = defLogisticsCompany.value.code
      })
    }
  })
}

/**
 * 移除订单
 */
function handleRemove(index: number) {
  orderList.value.splice(index, 1)
}

/**
 * 选择单个物流公司
 */
function changeOneComp(row: OrderItem) {
  logisticsCompany.value.forEach((item) => {
    if (item.prizeOrderId === row.prizeOrderId) {
      item.deliveComp = row.deliveComp
    }
  })
}

/**
 * 输入单个运单号码
 */
function inputOneComp(row: OrderItem) {
  orderList.value.forEach((item) => {
    if (item.prizeOrderId === row.prizeOrderId) {
      item.deliverySn = row.deliverySn
    }
  })
}

/**
 * 批量发货
 */
function sendSome() {
  if (lQuery.type === '打印快递单并发货') {
    sendByPrint()
    return
  }
  if (lQuery.type === '无需物流') {
    sendNo()
    return
  }

  // 检查是否所有订单都填写了物流信息
  const allHaveDelivery = orderList.value.every((item) => {
    return item.deliveryCompany && item.deliverySn
  })

  if (!allHaveDelivery) {
    ElMessage.error('请填写物流信息')
    return
  }

  // 准备发送数据
  const sendData = orderList.value.map((item) => {
    const { createTime, ...rest } = item
    return rest
  })

  sendOneintegralGood(sendData).then(() => {
    ElMessage.success('发货成功')
    isBack()
  })
}

/**
 * 打印发货
 */
function sendByPrint() {
  if (!lQuery.deliverCode || !lQuery.printCode || !lQuery.expressId) {
    ElMessage.error('请填写打印信息')
    return
  }

  const logisticsPrintDeliverDto: {
    orderIds: string[]
    isIntegralOrder: number
    [key: string]: any
  } = {
    orderIds: [],
    ...lQuery,
    isIntegralOrder: -1,
  }

  orderList.value.forEach((item) => {
    if (item.prizeOrderId) {
      logisticsPrintDeliverDto.orderIds.push(item.prizeOrderId)
    }
  })

  sendintegralGoodByPrint(logisticsPrintDeliverDto).then(() => {
    ElMessage.success('发货成功')
    isBack()
  })
}

/**
 * 无需物流发货
 */
function sendNo() {
  const integralOrders = orderList.value.filter((item) => item.prizeOrderId).map((item) => item.prizeOrderId)

  if (integralOrders.length === 0) {
    ElMessage.warning('没有可发货的订单')
    return
  }

  sendNoLogistic(integralOrders).then(() => {
    ElMessage.success('发货成功')
    isBack()
  })
}

function isBack() {
  router.push({
    name: 'IntegralMallIndex',
    params: { type: 'deliver' },
  })
}

/**
 * 取消发货
 */
function back() {
  ElMessageBox.confirm('你确定要退出批量发货页面?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    router.push({
      name: 'IntegralMallIndex',
      params: { type: 'deliver' },
    })
  })
}
</script>

<template>
  <div class="send">
    <div class="send__search">
      <div>
        <el-form :model="lQuery" label-width="80px">
          <el-form-item label="发货方式" required>
            <el-radio-group v-model="lQuery.type">
              <el-radio value="手动发货">手动发货</el-radio>
              <el-radio value="打印快递单并发货">打印快递单并发货</el-radio>
              <el-radio value="无需物流">无需物流</el-radio>
            </el-radio-group>
          </el-form-item>
          <template v-if="!isNotLogistics">
            <el-form-item label="物流公司" required>
              <el-select v-model="lQuery.deliverCode" style="width: 250px" placeholder="请选择">
                <el-option v-for="item of logisticsCompany" :key="item.code" :label="item.name" :value="item.code">
                  {{ item.name }}
                </el-option>
              </el-select>
              <el-link
                v-if="!isManual"
                type="primary"
                underline="never"
                style="margin-left: 4px"
                :disabled="deliverCodeBtnStatus"
                @click="setDefaultAddress"
              >
                设为默认
              </el-link>
            </el-form-item>
            <template v-if="!isManual">
              <el-form-item label="打印机" required>
                <el-select v-model="lQuery.printCode" style="width: 250px" placeholder="选择打印机">
                  <el-option v-for="item of logisticsPrinterVos" :key="item.deviceKey" :label="item.deviceName" :value="item.deviceKey" />
                </el-select>
              </el-form-item>
              <el-form-item label="发货地址" required>
                <el-select v-model="lQuery.expressId" :disabled="!logisticsAddressVos.length" placeholder="选择发货地址" style="width: 250px">
                  <el-option v-for="item of logisticsAddressVos" :key="item.code" :label="item.address" :value="item.expressId" />
                </el-select>
              </el-form-item>
            </template>
          </template>
        </el-form>
      </div>
      <div>
        <el-link type="primary" underline="never" @click="handleImportAll"> 导入全部待发货订单 </el-link>
      </div>
    </div>
    <m-table v-show="lQuery.type === '手动发货'" :data="orderList" style="margin-top: 20px; width: 100%; flex: 1; overflow: scroll">
      <template #header="{ row, index }">
        <div style="width: 100%">
          <span style="margin-right: 50px">订单号：{{ row.prizeOrderId }}</span>
          <span style="margin-right: 50px">创建时间：{{ row.createTime }}</span>
          <el-link type="primary" style="float: right" underline="never" @click="handleRemove(index)"> 移除 </el-link>
        </div>
      </template>
      <m-table-column label="商品">
        <template #default="{ row }">
          <div class="shopName">
            <div style="width: 60px; height: 60px">
              <img :src="row.shoppingInfVos[0].pic" alt="" style="width: 60px; height: 60px" />
            </div>
            <div class="shopName__name">
              <div>{{ row.shoppingInfVos[0].integralGoodName }}</div>
              <div>{{ row.shoppingInfVos[0].integralPrice }}</div>
              <div>X1</div>
            </div>
          </div>
        </template>
      </m-table-column>
      <m-table-column label="客户">
        <template #default="{ row }">
          <div class="shopName" style="padding-left: 120px">
            <img :src="row.memberAvatarUrl" alt="" style="width: 60px; height: 60px" />
            <div class="shopName__name">
              {{ row.wxuserName }}
            </div>
          </div>
        </template>
      </m-table-column>
      <m-table-column label="地址">
        <template #default="{ row }">
          <div class="middleCenter">
            <div style="padding-left: 30px">物流配送</div>
            <div style="padding-left: 30px">{{ row.receiverName }},{{ row.telPhone }}</div>
            <div style="padding-left: 30px">
              {{ row.address }}
            </div>
          </div>
        </template>
      </m-table-column>
      <m-table-column label="操作" width="310px">
        <template #default="{ row }">
          <div class="sendDig">
            <div class="sendDig__line">
              <div style="width: 55px">*选择物流</div>
              <el-select
                v-model="row.deliveryCompany"
                placeholder="请选择物流公司"
                style="width: 180px; margin-left: 20px"
                @change="changeOneComp(row)"
              >
                <el-option v-for="item in logisticsCompany" :key="item.code" :label="item.name" :value="item.code" />
              </el-select>
            </div>
            <div class="sendDig__line">
              <div style="width: 55px">*运单号码</div>
              <el-input v-model="row.deliverySn" placeholder="请输入运单号码" style="width: 180px; margin-left: 20px" @change="inputOneComp(row)" />
            </div>
          </div>
        </template>
      </m-table-column>
    </m-table>
    <m-table v-show="lQuery.type !== '手动发货'" :data="orderList" style="margin-top: 20px; width: 100%; flex: 1; overflow: scroll">
      <template #header="{ row, index }">
        <div style="width: 100%">
          <span style="margin-right: 50px">订单号：{{ row.prizeOrderId }}</span>
          <span style="margin-right: 50px">创建时间：{{ row.createTime }}</span>
          <el-link type="primary" style="float: right" underline="never" @click="handleRemove(index)"> 移除 </el-link>
        </div>
      </template>
      <m-table-column label="商品">
        <template #default="{ row }">
          <div class="shopName">
            <div style="width: 60px; height: 60px">
              <img :src="row.shoppingInfVos[0].pic" alt="" style="width: 60px; height: 60px" />
            </div>
            <div class="shopName__name">
              <div>{{ row.shoppingInfVos[0].integralGoodName }}</div>
              <div>{{ row.shoppingInfVos[0].integralPrice }}</div>
              <div>X1</div>
            </div>
          </div>
        </template>
      </m-table-column>
      <m-table-column label="客户">
        <template #default="{ row }">
          <div class="shopName" style="padding-left: 120px">
            <img :src="row.memberAvatarUrl" alt="" style="width: 60px; height: 60px" />
            <div class="shopName__name">
              {{ row.wxuserName }}
            </div>
          </div>
        </template>
      </m-table-column>
      <m-table-column label="地址">
        <template #default="{ row }">
          <div class="middleCenter">
            <div style="padding-left: 120px">物流配送</div>
            <div style="padding-left: 120px">{{ row.receiverName }},{{ row.telPhone }}</div>
            <div style="padding-left: 120px">
              {{ row.address }}
            </div>
          </div>
        </template>
      </m-table-column>
    </m-table>
    <div class="next">
      <el-button @click="back"> 取消 </el-button>
      <el-button type="primary" @click="sendSome"> 发货 </el-button>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.send {
  height: calc(100vh - 15px - 89px);
  padding: 10px 15px;
  display: flex;
  flex-direction: column;
  :deep(.m__table--head) {
    position: sticky;
    top: 0;
    z-index: 1;
  }

  :deep(.el-input-group__prepend .el-select) {
    min-width: 100px;
  }

  :deep(.el-pagination__sizes) {
    position: relative !important;
    top: -5px !important;
  }

  &__search {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  &__form {
    .el-form-item {
      margin: 5px 0;
    }
  }

  .send__bottom {
    display: flex;
    justify-content: center;
    align-items: center;

    width: 1010px;
    padding: 15px 0px;
    box-shadow: 0 0px 10px 0px #d5d5d5;
    position: fixed;
    // bottom: -20px;
    // left: -15px;
    z-index: 9;
    bottom: 10px;
    background: #fff;
  }
}

.shopName {
  display: flex;
  justify-content: center;
  align-items: center;

  &__name {
    width: 110px;
    margin-left: 20px;
    padding-top: 10px;
  }
}

.middleCenter {
  //   display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
}

.upDown {
  display: flex;
  align-items: center;
  justify-content: center;

  &--goodUp {
    display: flex;
    width: 40px;
    height: 20px;
    justify-content: center;
    align-items: center;
    border-radius: 4px;
    color: white;
    margin-right: 10px;
  }

  &--goodDown {
    margin-left: 10px;
    color: #2d8cf0;
    cursor: pointer;
  }
}

.sendDig {
  padding: 20px;
  &__line {
    width: 100%;
    margin-bottom: 20px;
    display: flex;
    align-items: center;
  }
}

.next {
  padding-top: 20px;
  height: 80px;
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 0px 10px 0px #d5d5d5;
  margin-top: auto;
}
</style>
