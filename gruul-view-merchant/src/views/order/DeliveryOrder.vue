<script setup lang="ts">
import { getLogisticsCompany } from '@/api/logistics/logistics'
import { close, getLogisticsWait, getOrderDetail, getOrders, printDeliver, remarks } from '@/api/order'
import MContainer from '@/components/MContainer/MContainer.vue'
import DeliveryPrintModal from '@/views/order/components/DeliveryPrintModal.vue'
import DeliverySendModal from '@/views/order/components/DeliverySendModal.vue'
import OrderDetailModal from '@/views/order/components/detail/Index.vue'
import RemarkModal from '@/views/order/components/RemarkModal.vue'
import { ArrowDown } from '@element-plus/icons-vue'

import { ElMessage, ElMessageBox } from 'element-plus'
import { cloneDeep, debounce } from 'lodash-es'

import { useRoute, useRouter } from 'vue-router'
import { deliveryOrderQuery, filterEmpty, getAfterNameExport, getAfterStatusNameExport, isClose } from './common/order'
import DeliveryOrderFrom from './components/DeliveryOrderFrom.vue'
import DeliveryOrderTable from './components/DeliveryOrderTable.vue'

// 确保与组件内部使用的类型匹配
interface OrderItem {
  orderId: string
  id: string | number
  status: string
  close: boolean
  createTime: string
  receiverName: string
  receiverPhone: string
  receiverProvince: string
  receiverCity: string
  receiverRegion: string
  receiverDetailAddress: string
  [key: string]: any
}

interface OptionItem {
  label: string
  value: string
}

interface PrintDeliverData {
  orderIds: string[]
  [key: string]: any
}

interface RemarkForm {
  remark: string
  orderIds?: string
  [key: string]: any
}

interface OrderDetail {
  id: string
  status: string
  [key: string]: any
}

defineOptions({
  name: 'OrderDelivery',
})

const router = useRouter()
const route = useRoute()

// 查询条件
const query = ref(cloneDeep(deliveryOrderQuery))

// 操作栏选项
const defaultContorlOption: OptionItem[] = [
  {
    label: '批量备注',
    value: 'remark',
  },
]

// 待收货操作选项
const waitContorlOption: OptionItem[] = [
  {
    label: '批量备注',
    value: 'remark',
  },
]

// 待付款操作选项
const waitPayContorlOption: OptionItem[] = [
  {
    label: '批量备注',
    value: 'remark',
  },
  {
    label: '批量关闭',
    value: 'close',
  },
]

// tab[0]选项
const quicklyOption = ref<OptionItem[]>([
  { label: '近一个月订单', value: '0' },
  { label: '近三个月订单', value: '1' },
  { label: '全部订单', value: '2' },
])

// 表格数据
const data = ref<any[]>([])

// 选择的订单ID组
const orderIds = ref<string[]>([])

// 订单详情modal
const detailVisible = ref(false)

// 留言modal
const remarklVisible = ref(false)

// 发货modal显示隐藏
const sendModalVisible = ref(false)

// 打印modal显示隐藏
const printModalVisible = ref(false)

// 订单详情
const orderDetail = ref<OrderDetail>({
  id: '',
  status: '',
})

// 表格选中条目
const checkedItem = ref<any[]>([])

// 当前选中的订单
const currentOrder = ref<OrderItem | undefined>()

// 详情是否显示tab
const needTabs = ref(false)

// 显示类型，主要用去驱动详情modal的ui变化
const detailType = ref('1')

// 物流公司
const logisticsCompany = ref<any[]>([])

// 打印机列表
const logisticsPrinterVos = ref<any[]>([])

// 订单列表
const orders = computed<any[]>(() => {
  return data.value.map((item) => {
    item.close = isClose(item.status)
    return item
  })
})

// 表格选中条目ID组
const selectedIds = computed(() => {
  return checkedItem.value.map((item) => item.orderId)
})

// 快速日期名称
const quicklyName = computed(() => {
  const foundItem = quicklyOption.value.find((item) => item.value === query.value.quicklyDate)
  return foundItem ? foundItem.label : ''
})

// 获取订单列表（防抖处理）
const getOrdersDebounced = debounce((q: any, cb: () => void = () => {}) => {
  getOrders(q).then((res) => {
    const { list, ...other } = res.data
    Object.assign(query.value, other)
    data.value = list
    if (cb) {
      cb()
    }
  })
}, 50)

// 监听query变化
watch(
  () => query.value,
  (newVal) => {
    getOrdersDebounced(filterEmpty(filterEmpty({ ...route.query, ...newVal })))
  },
  { deep: true },
)

// 组件挂载时获取物流公司数据
onMounted(() => {
  onGetLogisticsCompany()
})

// 获取物流公司及打印机
function onGetLogisticsCompany() {
  getLogisticsCompany().then((res) => {
    logisticsCompany.value = res.data.logisticsCompanyVos
    logisticsPrinterVos.value = res.data.LogisticsPrinterVos
  })
}

// 确认批量发货
function confirm(data: PrintDeliverData) {
  if (!data.orderIds.length) {
    data.orderIds = checkedItem.value.map((item) => item.orderId)
  }

  printDeliver(data).then(() => {
    ElMessage.success('发货成功')
    getOrdersDebounced(Object.assign({}, query.value, route.query))
    hideModal()
  })
}

// 获取按钮选项
function getControlOption(type: string): OptionItem[] {
  return (
    {
      '-1': waitContorlOption,
      '0': waitPayContorlOption,
      '1': waitContorlOption,
    }[type] || defaultContorlOption
  )
}

// 监听table传来的事件
function tableFunction(name: string, data: any | null, isLogistics?: boolean) {
  switch (name) {
    case 'remark':
      return triggerRemark(data)
    case 'close':
      return handleClose(data)
    case 'detail':
      return handleSeeDetail(data, isLogistics)
    case 'print':
      return togglePrintisible()
    case 'send':
      send(data)
  }
}

// 备注
function triggerRemark(orderData: any | null) {
  orderIds.value = orderData ? [orderData.orderId] : [...selectedIds.value]
  toggleRemarklVisible()
}

// 发货
async function send(data: any | null) {
  const { data: len } = await getLogisticsWait()

  if (!len) {
    return ElMessage.info('当前暂无待发货订单')
  }

  const selectedOrderIds = checkedItem.value.map((item) => item.orderId).join(',')
  const waitLen = orders.value.filter((item) => {
    if (!selectedOrderIds.includes(item.orderId)) {
      return false
    }
    const itemVoList = item.itemVoList.filter(
      (i: any) => !i.afs || (i.afs && !(getAfterNameExport(i.afs.type) !== '' && getAfterStatusNameExport(i.afs.status) === '成功')),
    )
    console.log(itemVoList)
    if (!itemVoList.length) {
      return false
    }
    if (item.status === 'WAIT_FOR_SEND') {
      return true
    }
  }).length

  if (!waitLen && !data) {
    return ElMessage.info('您没有选择待发货订单')
  }
  if (data) {
    toggleSendVisible(data)
  } else {
    router.push(`/order/delivery/send?orderIds=${selectedOrderIds}`)
  }
}

// 查看详情
async function handleSeeDetail(orderData: any | null, isLogistics?: boolean) {
  if (isLogistics) {
    needTabs.value = true
    detailType.value = '2'
  } else {
    detailType.value = '1'
    needTabs.value = false
  }

  if (orderData?.orderId) {
    toggleDetailModal()
  }

  getOrderDetail(orderData?.orderId || orderData?.id?.toString() || '').then((res) => {
    orderDetail.value = {
      id: String(res.data.id || ''),
      status: res.data.status || '',
      ...res.data,
    }
  })
}

// 关闭订单
function handleClose(orderData: any | null) {
  ElMessageBox.confirm('确定关闭订单？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    const closeOrderIds = orderData ? [orderData.orderId] : [...selectedIds.value]
    close(closeOrderIds).then(() => {
      getOrdersDebounced(Object.assign({}, query.value, route.query))
      ElMessage.success('关闭成功')
    })
  })
}

// 备注
function handleRemarks(form: RemarkForm) {
  remarks({ ...form, orderIds: orderIds.value.join(',') }).then(() => {
    getOrdersDebounced(Object.assign({}, query.value, route.query))
    toggleRemarklVisible()
    ElMessage.success('备注成功')
  })
}

// 设置快速日期
function setQuicklyDate(v: string) {
  query.value.quicklyDate = v
}

// 切换详情弹窗显示
function toggleDetailModal() {
  detailVisible.value = !detailVisible.value
  if (!detailVisible.value) {
    detailType.value = '1'
  }
}

// 切换备注弹窗显示
function toggleRemarklVisible() {
  remarklVisible.value = !remarklVisible.value
}

// 隐藏所有弹窗
function hideModal() {
  sendModalVisible.value = false
  printModalVisible.value = false
}

// 切换发货弹窗显示
function toggleSendVisible(data: any) {
  currentOrder.value = data
  sendModalVisible.value = !sendModalVisible.value
}

// 切换打印弹窗显示
function togglePrintisible() {
  printModalVisible.value = !printModalVisible.value
}

// 处理标签页点击
function handleTabClick(tab: any) {
  query.value.orderStatus = tab.props.name
}

// 初始化：获取订单数据
getOrdersDebounced(filterEmpty(query.value))
</script>

<template>
  <MContainer class="order" show-same-slot>
    <template #tabs>
      <el-tabs v-model="query.orderStatus" @tab-click="handleTabClick">
        <el-tab-pane name="-1">
          <template #label>
            <span>
              <div class="order__quickly">
                <el-dropdown trigger="click" @click.stop>
                  <div>
                    <span class="name">{{ quicklyName }}</span>
                    <el-icon class="ml-[20px]"><ArrowDown /></el-icon>
                  </div>

                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item v-for="item of quicklyOption" :key="item.value" @click="setQuicklyDate(item.value)">{{
                        item.label
                      }}</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
            </span>
          </template>
        </el-tab-pane>
        <el-tab-pane label="待付款" name="0" />
        <el-tab-pane label="待发货" name="1" />
        <el-tab-pane label="待收货" name="3" />
        <el-tab-pane label="已完成" name="4" />
        <el-tab-pane label="已关闭" name="5" />
      </el-tabs>
    </template>

    <template #form>
      <DeliveryOrderFrom v-model="query" />
    </template>

    <template #content>
      <DeliveryOrderTable
        v-model:query="query"
        v-model:checked-item="checkedItem"
        :data="orders"
        :control-option="getControlOption(query.orderStatus)"
        @table-function="tableFunction"
      />
    </template>

    <OrderDetailModal
      v-model="detailVisible"
      :detail="orderDetail"
      :need-tabs="true"
      :is-delivery="true"
      :type="detailType"
      @reset="handleSeeDetail"
    />

    <RemarkModal
      v-model="remarklVisible"
      title="备注"
      :current-order="currentOrder || undefined"
      :order-ids="orderIds"
      @on-ok="handleRemarks"
      @on-cancel="toggleRemarklVisible"
    />

    <DeliverySendModal
      v-model="sendModalVisible"
      :current-order="currentOrder"
      :logistics-printer-vos="logisticsPrinterVos"
      :logistics-company="logisticsCompany"
      @reset="onGetLogisticsCompany"
      @on-confirm="confirm"
    />

    <DeliveryPrintModal
      v-model="printModalVisible"
      :logistics-printer-vos="logisticsPrinterVos"
      :logistics-company="logisticsCompany"
      @reset="onGetLogisticsCompany"
      @on-confirm="confirm"
    />
  </MContainer>
</template>

<style lang="scss" scoped>
@import '@/assets/styles/order/order.scss';
</style>
