<script lang="ts" setup>
import { getOrderDetail } from '@/api/order'
import { getAfterList, note } from '@/api/order/afterSale'

import MContainer from '@/components/MContainer/MContainer.vue'

import { useRoute } from 'vue-router'
import { filterEmpty, isClose } from './common/order'
import OrderForm from './components/AfterOrderForm.vue'
import OrderTable, { type TableItem } from './components/AfterOrderTable.vue'
import OrderDetailModal from './components/detail/Index.vue'
import RemarkModal from './components/RemarkModal.vue'

defineOptions({
  name: 'OrderAfterSale',
})

interface OrderData {
  id: string
  status: string
  orderId: string

  [key: string]: any
}

// 定义订单详情接口
interface OrderDetail {
  id: string
  status: string
  [key: string]: any
}

interface OrderQuery {
  orderStatus?: string
  area?: string
  assName?: string
  current?: number
  deliverType?: string
  endTime?: string
  startTime?: string
  keyword?: string
  lineId?: string
  orderId?: string
  pointName?: string
  productName?: string
  receiverName?: string
  size?: number
  status?: string
  quicklyDate?: string
  total?: number
  goodsName?: string
  groupLeaderName?: string
  userName?: string
  payTime?: string
  deliverySn?: string
  remarkType?: string
  sendBillType?: string
}

const route = useRoute()

// 查询条件
const query = ref<OrderQuery>({
  orderStatus: '',
  area: '',
  assName: '',
  current: 1,
  deliverType: '102',
  endTime: '',
  startTime: '',
  keyword: '',
  lineId: '',
  orderId: '',
  pointName: '',
  productName: '',
  receiverName: '',
  size: 20,
  status: '-1',
  quicklyDate: '',
})

// 表格数据
const data = ref<OrderData[]>([])

// 选择的订单ID组
const orderIds = ref<string[]>([])

// 订单详情modal
const detailVisible = ref(false)

// 留言modal
const remarklVisible = ref(false)

const detailType = ref('4')

// 订单详情
const orderDetail = ref<OrderDetail>({
  id: '',
  status: '',
})

// 售后详情
const afterInfo = ref({})

// 表格选中条目
const checkedItem = ref<OrderData[]>([])

// 当前选中的订单
const currentOrder = ref<OrderData | Record<string, any>>({})

// 移除发货单显示隐藏
const removeVisible = ref(false)

// 计算属性
const orders = computed<TableItem[]>(() => {
  return data.value.map((item) => {
    return {
      ...item,
      close: isClose(item.status),
    } as unknown as TableItem
  })
})

const selectedIds = computed(() => {
  return checkedItem.value.map((item) => item.id)
})

const sendBillVisible = computed(() => {
  return query.value.orderStatus === '-1' || query.value.orderStatus === '1'
})

// 方法
async function onGetAfterList(q: OrderQuery) {
  const res = await getAfterList(q)
  const { list, ...other } = res.data
  Object.assign(query.value, other)
  data.value = list
  // 滚动到顶部
  document.querySelector('.admin__main--content')?.scrollTo(0, 0)
  // 关闭弹窗
  if (!detailVisible.value) return
  toggleDetailModal()
}

async function handleSeeDetail(orderData: OrderData) {
  const res = await getOrderDetail(orderData.orderId || orderData.id)
  afterInfo.value = orderData
  orderDetail.value = {
    id: res.data.id || '',
    status: res.data.status || '',
    ...res.data,
  }
  toggleDetailModal()
}

async function handleRemarks(form?: any) {
  await note({ afsIds: orderIds.value, ...form })
  await onGetAfterList({ ...query.value, ...route.query })
  toggleRemarklVisible()
  ElMessage.success('备注成功')
}

function tableFunction(name: string, data: OrderData): void {
  switch (name) {
    case 'remark':
      triggerRemark(data)
      break
    case 'detail':
      detailType.value = '1'
      handleSeeDetail(data)
      break
    case 'remove':
      handleRemove(data)
      break
    case 'goodsRemove':
      toggleRemoveVisible()
      break
    case 'review':
      handleReview(data)
      break
  }
}

function handleReview(row: OrderData) {
  detailType.value = '4'
  handleSeeDetail(row)
}

function handleTabClick({ name: orderStatus }: { name: string }) {
  checkedItem.value = []
  Object.assign(query.value, {
    orderStatus,
    current: 1,
    deliverId: '',
  })
}

function triggerRemark(orderData?: OrderData) {
  currentOrder.value = orderData || {}
  // 判断对象是否为空
  orderIds.value = Object.keys(currentOrder.value).length === 0 ? [...selectedIds.value] : [currentOrder.value.id]
  toggleRemarklVisible()
}

function toggleDetailModal() {
  detailVisible.value = !detailVisible.value
  if (!detailVisible.value) {
    detailType.value = '4'
  }
}

function toggleRemarklVisible() {
  remarklVisible.value = !remarklVisible.value
}

function toggleRemoveVisible() {
  removeVisible.value = !removeVisible.value
}

// 处理移除
function handleRemove(_data: OrderData) {
  // 实现移除逻辑
}

// 监听器
watch(
  () => query.value,
  (v) => {
    onGetAfterList(v)
  },
  { deep: true },
)

// 生命周期钩子
onMounted(() => {
  query.value.deliverType = '102'
  onGetAfterList(filterEmpty(query.value))
})
</script>

<template>
  <MContainer v-model:current="query.current" v-model:size="query.size" v-model:total="query.total" class="order" show-pagination>
    <template #tabs>
      <!--      <el-tabs v-model="query.deliverType" @tab-click="handleAreaTabClick"> -->
      <!--        <el-tab-pane label="物流售后" name="102" /> -->
      <!--      </el-tabs> -->
      <el-tabs v-model="query.status" @tab-click="(e: any) => handleTabClick(e)">
        <el-tab-pane label="全部" name="-1" />
        <el-tab-pane label="待处理" name="0" />
        <el-tab-pane label="处理中" name="1" />
        <el-tab-pane label="已完成" name="2" />
        <el-tab-pane label="已关闭" name="3" />
      </el-tabs>
    </template>

    <template #section>
      <el-button :disabled="!checkedItem.length" plain type="primary" @click="triggerRemark()"> 批量备注 </el-button>
    </template>

    <template #form>
      <OrderForm v-model="query" />
    </template>

    <template #table>
      <OrderTable
        v-model:checked-item="checkedItem"
        v-model:query="query"
        :data="orders"
        :send-bill-visible="sendBillVisible"
        @table-function="tableFunction"
      />
    </template>

    <template #pagination--left>
      <el-button :disabled="!checkedItem.length" plain type="primary" @click="triggerRemark()"> 批量备注 </el-button>
    </template>

    <OrderDetailModal
      v-model="detailVisible"
      :after-info="afterInfo"
      :detail="orderDetail"
      :is-after="true"
      :is-group="true"
      :type="detailType"
      @reset="() => onGetAfterList(query)"
    />

    <RemarkModal
      v-model="remarklVisible"
      :current-order="currentOrder"
      :order-ids="orderIds"
      @on-ok="handleRemarks"
      @on-cancel="toggleRemarklVisible"
    />
  </MContainer>
</template>

<style lang="scss" scoped>
@import '@/assets/styles/order/order.scss';
</style>
