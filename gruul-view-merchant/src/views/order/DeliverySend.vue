<script setup lang="ts">
import { bulkShipment, getLogisticsCompany as fetchLogisticsCompany, setDefaultAddress } from '@/api/logistics/logistics'
import { logisticsSend, printDeliver, searchLogistics } from '@/api/order'
import { ElMessageBox, ElMessage } from 'element-plus'
import { cloneDeep } from 'lodash-es'

import { useRoute, useRouter } from 'vue-router'
import { getAfterNameExport, getAfterStatusNameExport } from './common/order'

defineOptions({
  name: 'OrderDeliverySend',
})

interface LogisticsCompany {
  code: string
  name: string
  isDefault: number
  logisticsCompanyId: string | number
  logisticsExpressAddressVos?: LogisticsAddress[]
  [key: string]: any
}

interface LogisticsAddress {
  expressId: string | number
  code: string
  address: string
  [key: string]: any
}

interface LogisticsPrinter {
  deviceKey: string
  deviceName: string
  [key: string]: any
}

interface LogisticsQuery {
  type: '手动发货' | '打印快递单并发货' | '无需物流'
  printCode: string
  deliverCode: string
  expressId: string
  [key: string]: any
}

interface OrderItem {
  productPic: string
  productName: string
  productPrice: number
  productQuantity: number
  specs?: string
  [key: string]: any
}

interface TableItem {
  orderId: string
  createTime: string
  userAvatarUrl?: string
  userName?: string
  itemVoList: OrderItem[]
  localLogisticsBatchDeliverDtos: {
    deliverCode: string
    deliverName: string
    logisticsCode: string
    orderId: string
    [key: string]: any
  }
  [key: string]: any
}

interface ColumnItem {
  label: string
  coustomStyle?: string
  width?: number
}

const router = useRouter()
const route = useRoute()

/** 物流公司列表 */
const logisticsCompany = ref<LogisticsCompany[]>([])
/** 表格数据 */
const tableData = ref<TableItem[]>([])
/** 打印机 */
const logisticsPrinterVos = ref<LogisticsPrinter[]>([])
/** 打印快递单并发货form */
const lQuery = ref<LogisticsQuery>({
  type: '手动发货',
  printCode: '',
  deliverCode: '',
  expressId: '',
})

/** 是否为手动发货 */
const isManual = computed<boolean>(() => {
  return lQuery.value.type === '手动发货'
})

/** 处理物流公司列表，增加默认标记 */
const company = computed<LogisticsCompany[]>(() => {
  return cloneDeep(logisticsCompany.value).map((item) => {
    if (item.isDefault === 1) {
      item.name = item.name + (isManual.value ? '' : '(默认)')
    }
    return item
  })
})

/** 物流公司map */
const logisticsCompanyMap = computed<Record<string, string>>(() => {
  const map: Record<string, string> = {}
  logisticsCompany.value.forEach((item) => {
    map[item.code] = item.name
  })
  return map
})

/** 是否为无需物流发货 */
const isNotLogistics = computed<boolean>(() => {
  return lQuery.value.type === '无需物流'
})

/** 表头 */
const columns = computed<ColumnItem[]>(() => {
  const cols: ColumnItem[] = [
    {
      label: '商品',
      coustomStyle: 'text-align: left;',
    },
    {
      label: '客户',
      coustomStyle: 'text-align: left;',
      width: 245,
    },
  ]

  if (isManual.value) {
    return [
      ...cols,
      {
        label: '操作',
        width: 255,
      },
    ]
  }
  return cols
})

/** 默认物流公司 */
const defLogisticsCompany = computed<LogisticsCompany | undefined>(() => {
  return logisticsCompany.value.find((item) => item.isDefault === 1)
})

/** 设置默认按钮状态 */
const deliverCodeBtnStatus = computed<boolean>(() => {
  return !lQuery.value.deliverCode || (defLogisticsCompany.value !== undefined && defLogisticsCompany.value.code === lQuery.value.deliverCode)
})

/** 默认发货地址 */
const logisticsAddressVos = computed<LogisticsAddress[]>(() => {
  try {
    const company = logisticsCompany.value.find((item) => item.code === lQuery.value.deliverCode)
    if (company && company.logisticsExpressAddressVos) {
      return company.logisticsExpressAddressVos
    }
    return []
  } catch (err) {
    console.error(err)
    return []
  }
})

/** 监听deliverCode变化，批量设置列表deliverCode */
watch(
  () => lQuery.value.deliverCode,
  (v) => {
    tableData.value = tableData.value.map((item) => {
      item.localLogisticsBatchDeliverDtos.deliverCode = v
      return item
    })
  },
)

/** 监听物流公司列表变化，设置form中的默认物流公司 */
watch(
  () => logisticsCompany.value,
  (v) => {
    if (v.length && defLogisticsCompany.value && defLogisticsCompany.value.code) {
      lQuery.value.deliverCode = defLogisticsCompany.value.code
    }
  },
  { immediate: true },
)

onMounted(() => {
  searchDeliveryOrders()
  getLogisticsCompany()
})

onBeforeUnmount(() => {
  window.onresize = null
})

/** 设置默认发货公司 */
function onSetDefaultAddress(): void {
  const company = logisticsCompany.value.find((item) => item.code === lQuery.value.deliverCode)
  if (!company) return

  setDefaultAddress(String(company.logisticsCompanyId)).then(() => {
    getLogisticsCompany()
    ElMessage.success('设置成功')
  })
}

/** 移除 */
function handleRemove(index: number): void {
  tableData.value.splice(index, 1)
}

/** 导入所有未发货订单 */
function handleImportAll(): void {
  searchDeliveryOrders(
    {
      orderIds: '',
    },
    () => {
      ElMessage.success('导入成功')
    },
  )
}

/** 查询订单 */
function searchDeliveryOrders(
  query: { orderIds: string | string[] | null | undefined } = {
    orderIds: route.query.orderIds as string | string[] | undefined,
  },
  cb: () => void = () => {},
): void {
  searchLogistics(query).then((res) => {
    res.data.forEach((item: TableItem) => {
      item.itemVoList = item.itemVoList.filter((i: any) => {
        return !i.afs || (i.afs && !(getAfterNameExport(i.afs.type) !== '' && getAfterStatusNameExport(i.afs.status) === '成功'))
      })
    })
    tableData.value = addLogisticsData(res.data)
    cb()
  })
}

/**
 * 获取物流公司与打印机
 */
function getLogisticsCompany(): void {
  fetchLogisticsCompany().then((res) => {
    logisticsCompany.value = res.data.logisticsCompanyVos
    logisticsPrinterVos.value = res.data.LogisticsPrinterVos
  })
}

/** 处理条漫物流公司变化 */
function onItemChange(value: string, key: string, index: number): void {
  tableData.value[index].localLogisticsBatchDeliverDtos[key] = value
}

/**
 * 添加物流的默认数据
 * @param arr 订单列表
 */
function addLogisticsData(arr: any[]): TableItem[] {
  return arr.map((item) => {
    item.localLogisticsBatchDeliverDtos = {
      deliverCode: lQuery.value.deliverCode,
      deliverName: '',
      logisticsCode: '',
      orderId: item.orderId,
    }
    return item
  })
}

/** 取消批量发货 */
function cancel(): void {
  router.back()
}

/** 确认按钮点击事件 */
function confirm(): void {
  if (isManual.value) return manualSend()
  if (isNotLogistics.value) return logisticsSendFunc()
  printSend()
}

function goBack(): void {
  router.replace('/order/delivery')
}

/** 打印发货 */
function printSend(): void {
  const data = Object.assign({}, lQuery.value, {
    orderIds: tableData.value.map((item) => item.orderId),
  })

  printDeliver(data).then(() => {
    ElMessage.success('发货成功')
    goBack()
  })
}

/** 无需物流发货 */
function logisticsSendFunc(): void {
  const data = tableData.value.map((item) => item.orderId).join(',')

  logisticsSend(data).then(() => {
    ElMessage.success('发货成功')
    goBack()
  })
}

/** 手动发货 */
function manualSend(): void {
  const logisticsBatchDeliverDtos = tableData.value
    .map((item) => item.localLogisticsBatchDeliverDtos)
    .map((item) => {
      item.deliverName = logisticsCompanyMap.value[item.deliverCode]
      return item
    })

  const hasDataLen = logisticsBatchDeliverDtos.filter((item) => !!item.logisticsCode).length

  if (hasDataLen === 0) {
    ElMessage.warning('请填写单号')
    return
  }

  const request = (): Promise<void> =>
    bulkShipment(logisticsBatchDeliverDtos.filter((item) => item.logisticsCode)).then(() => {
      ElMessage.success('发货成功')
      goBack()
    })

  if (hasDataLen !== logisticsBatchDeliverDtos.length) {
    ElMessageBox.confirm('存在订单未填写单号，是否继续', '提示').then(() => {
      request()
    })
    return
  }
  request()
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
                <el-option v-for="item in company" :key="item.code" :label="item.name" :value="item.code">
                  {{ item.name }}
                </el-option>
              </el-select>
              <el-link
                v-if="!isManual"
                type="primary"
                underline="never"
                style="margin-left: 4px"
                :disabled="deliverCodeBtnStatus"
                @click="onSetDefaultAddress"
              >
                设为默认
              </el-link>
            </el-form-item>
            <template v-if="!isManual">
              <el-form-item label="打印机" required>
                <el-select v-model="lQuery.printCode" style="width: 250px" placeholder="选择打印机">
                  <el-option v-for="item in logisticsPrinterVos" :key="item.deviceKey" :label="item.deviceName" :value="item.deviceKey" />
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

    <m-table :data="tableData" custom :columns="columns" style="width: 100%; flex: 1; overflow: scroll">
      <template #header="{ row, index }">
        <div style="width: 100%">
          <span style="margin-right: 50px">订单号：{{ row.orderId }}</span>
          <span style="margin-right: 50px"> 创建时间：{{ row.createTime }} </span>
          <el-link type="primary" style="float: right" underline="never" @click="handleRemove(index)"> 移除 </el-link>
        </div>
      </template>
      <template #custom-body="{ row, index }">
        <template v-for="(goods, i) in row.itemVoList" :key="i">
          <tr class="body--content">
            <td>
              <div class="table__goods">
                <div class="table__goods--image">
                  <img :src="goods.productPic" :alt="goods.productName" />
                </div>
                <div class="table__goods--info">
                  <div class="goods--name">
                    {{ goods.productName }}
                  </div>
                  <div class="goods--specification">
                    <span class="l">{{ goods.specs }}</span>
                    <span class="r">*{{ goods.productQuantity }}</span>
                  </div>
                  <div class="goods--price">￥{{ goods.productPrice.toFixed(2) }}</div>
                </div>
              </div>
            </td>

            <td v-if="i === 0" :rowspan="row.itemVoList.length">
              <div class="table__user">
                <div class="user--head">
                  <img :src="row.userAvatarUrl" :alt="row.userName" />
                </div>
                <div class="user--name">
                  {{ row.userName }}
                </div>
              </div>
            </td>

            <td v-if="i === 0 && isManual" :rowspan="row.itemVoList.length">
              <el-form class="send__form" label-width="80px">
                <el-form-item label="选择物流" required>
                  <el-select
                    v-model="row.localLogisticsBatchDeliverDtos.deliverCode"
                    placeholder="选择物流"
                    style="width: 100%"
                    @input="onItemChange($event, 'deliverCode', index)"
                  >
                    <el-option v-for="item of logisticsCompany" :key="item.code" :label="item.name" :value="item.code" />
                  </el-select>
                </el-form-item>
                <el-form-item label="运单号码" required>
                  <el-input v-model="row.localLogisticsBatchDeliverDtos.logisticsCode" @input="onItemChange($event, 'logisticsCode', index)" />
                </el-form-item>
              </el-form>
            </td>
          </tr>
        </template>
      </template>
    </m-table>
    <div class="next">
      <el-button @click="cancel"> 取消 </el-button>
      <el-button type="primary" style="margin-left: 10px" @click="confirm"> 确认 </el-button>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@import '@/assets/styles/order/order.scss';

.send {
  height: calc(100vh - 15px - 59px);
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

.m__table--body .body--content td:first-child {
  border-right: 1px solid #d8eaf9 !important;
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
