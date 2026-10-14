<script setup lang="ts">
import { getWithdrawalDetail as apiGetWithdrawalDetail, getFinTrade, getWaterRecode } from '@/api/finance/index'
import { getOrderDetail as apiGetOrderDetail } from '@/api/order'
import BaseTable from '@/components/BaseTable/index.vue'
import MContainer from '@/components/MContainer/MContainer.vue'
import PageManage from '@/components/PageManage.vue'
import dayjs from 'dayjs'

import FnanceMainModal from './components/FnanceMainModal.vue'

defineOptions({
  name: 'FinanceIndex',
})

// 定义交易记录接口
interface TradeRecord {
  id: string
  sn: string
  sourceId: string
  type: string
  userName: string
  userAvatarUrl: string
  amount: number | string
  capitalFlows: string
  createTime: string
  [key: string]: any
}

// 响应式数据
const active = ref('1')
const visible = ref(false)
const currentRow = ref<TradeRecord>({} as TradeRecord)
const total = ref(0)
const detail = ref<any>({})
const tableData = ref<TradeRecord[]>([])
const detailIsOrder = ref(false)

const currentDate = dayjs().format('YYYY-MM-DD')

// 搜索条件
const query = reactive({
  type: '',
  flowType: -1, // -1->全部;0->收入;1->支出
  startTime: currentDate,
  endTime: currentDate,
  current: 1,
  size: 10,
})

// 对账单概况
const account = reactive({
  expensesAmount: '', // 支出金额
  expensesNumber: '', // 支出次数
  incomeAmount: '', // 收入金额
  incomeNumber: '', // 收入次数
})

const createTime = ref([currentDate, currentDate])

const options = [
  { label: '全部', value: '' },
  { label: '商品交易', value: 'TRADE' },
  { label: '退款', value: 'RETURN' },
  { label: '普通会员余额提现', value: 'DISTRIBUTOR_FOR_USER' },
  { label: '消费返利', value: 'REBATE' },
]

// 计算属性
const title = computed(() => {
  const obj: Record<string, string> = {}
  options.forEach((item) => {
    obj[item.value] = item.label
  })
  return obj[currentRow.value.type] || '详情'
})

// 请求是否为详情请求
function getDetailIsOrder(row: TradeRecord): boolean {
  return row.type === 'TRADE' || row.type === 'RETURN' || row.type === 'REBATE'
}

// 查看详情
async function lookDetail(row: TradeRecord) {
  currentRow.value = row
  detailIsOrder.value = getDetailIsOrder(row)
  if (detailIsOrder.value) {
    await getOrderDetail(row.sourceId)
  } else {
    await getWithdrawalDetail(row.sourceId)
  }
  toggleVisible()
}

// 切换详情弹窗
function toggleVisible() {
  visible.value = !visible.value
}

// 初始化获取数据
function init() {
  query.current = 1
  getTrade()
  getRecord()
}

// 切换顶部状态
function clickTab(e: any) {
  Object.assign(query, {
    flowType: Number(e.props.name) - 2,
    type: '',
  })
  init()
}

// 选择时间
function chooseTime(data: string[] | null) {
  query.startTime = data?.[0] || currentDate
  query.endTime = data?.[1] || currentDate
  query.current = 1
  getTrade()
  getRecord()
}

// 下拉框选择
function selectStatus(type: string) {
  query.current = 1
  Object.assign(query, { type, current: 1 })
  getRecord()
}

// 获取订单详情
async function getOrderDetail(id: string) {
  const res = await apiGetOrderDetail(id)
  detail.value = res.data
}

// 获取提现详情
async function getWithdrawalDetail(id: string | number) {
  const res = await apiGetWithdrawalDetail({ id })
  detail.value = res.data
}

// 获取对账单详情
function getTrade() {
  getFinTrade(query).then((res) => {
    Object.assign(account, res.data)
  })
}

// 获取对账单流水
function getRecord() {
  getWaterRecode(query).then((res) => {
    tableData.value = res.data.list
    total.value = res.data.total
  })
}

// 每页条数变化
function handleSizeChange(val: number) {
  query.current = 1
  query.size = val
  getRecord()
}

// 当前页变化
function handleCurrentChange(val: number) {
  query.current = val
  getRecord()
}

// 生命周期钩子
onMounted(() => {
  init()
})
</script>

<template>
  <MContainer class="finance">
    <template #top>
      <m-card class="finance__sts">
        <div class="left">
          收入（{{ account.incomeNumber }}笔）
          <br />
          <span>+{{ account.incomeAmount }}元</span>
        </div>
        <div class="right">
          支出（{{ account.expensesNumber }}笔）
          <br />
          <span>-{{ account.expensesAmount }}元</span>
        </div>
      </m-card>
    </template>
    <template #tabs>
      <el-tabs v-model="active" @tab-click="clickTab">
        <el-tab-pane label="全部" name="1" />
        <el-tab-pane label="收入" name="2" />
        <el-tab-pane label="支出" name="3" />
      </el-tabs>
    </template>
    <template #form>
      <div class="finance__form">
        <el-date-picker
          v-model="createTime"
          style="margin-right: 15px"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
          @change="chooseTime"
        />
        <el-select v-model="query.type" placeholder="请选择" class="w-[200px]" @change="selectStatus">
          <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </div>
    </template>

    <template #table>
      <BaseTable :data="tableData" style="width: 100%">
        <el-table-column label="交易流水号" width="180">
          <template #default="{ row }">
            <div>{{ row.sn }}</div>
          </template>
        </el-table-column>
        <el-table-column label="对方信息" width="180">
          <template #default="{ row }">
            <div class="userMsg">
              <img :src="row.userAvatarUrl" alt="" />
              <div>{{ row.userName }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="交易类型">
          <template #default="{ row }">
            <template v-if="row">
              <div v-if="row.type === 'TRADE'">商品交易</div>
              <div v-if="row.type === 'RETURN'">退款</div>
              <div v-if="row.type === 'REBATE'">消费返利</div>
              <div v-if="row.type === 'DISTRIBUTOR_FOR_USER'">余额提现</div>
            </template>
          </template>
        </el-table-column>
        <el-table-column label="收支金额（元）">
          <template #default="{ row }">
            <div>{{ row.capitalFlows === 'IN' ? '+' : '-' }}{{ row.amount }}</div>
          </template>
        </el-table-column>
        <el-table-column label="交易时间">
          <template #default="{ row }">
            <div>{{ row.createTime }}</div>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-link type="primary" underline="never" @click="lookDetail(row)"> 详情 </el-link>
          </template>
        </el-table-column>
      </BaseTable>
    </template>
    <template #pagination>
      <PageManage
        :page-size="query.size"
        :page-num="query.current"
        :total="total"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
      />
    </template>

    <FnanceMainModal v-model:visible="visible" :detail="detail" :detail-is-order="detailIsOrder" :title="title" />
  </MContainer>
</template>

<style lang="scss" scoped>
.finance {
  &__sts {
    display: flex;
    justify-content: flex-start;

    font-size: 14px;
    color: #666666;
    .left {
      span {
        color: #4bb1a6;
        font-size: 18px;
        margin-top: 10px;
      }
    }
    .right {
      margin-left: 300px;

      span {
        font-size: 18px;
        color: #ff0000;
        margin-top: 10px;
      }
    }
  }

  .userMsg {
    display: flex;
    align-items: center;
  }

  .userMsg > img {
    width: 50px;
    height: 50px;
    margin-right: 20px;
  }

  .el-range-separator {
    padding: 0 20px !important;
  }
}

.emptyLine {
  width: 100%;
  height: 80px;
  background-color: white;
  display: flex;
  -webkit-box-align: center;
  -ms-flex-align: center;
  align-items: center;
  -webkit-box-pack: center;
  justify-content: center;
  margin-left: -15px;
  font-size: 14px;
  color: #b3b3b3;
}
</style>
