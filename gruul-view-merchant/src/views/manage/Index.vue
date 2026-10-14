<script setup lang="ts">
import { getManageAccount, getManageOrder, getManageOverView } from '@/api/manage/manage'
import dayjs from 'dayjs'
import * as echarts from 'echarts'

import Account from './components/Account.vue'
import OrderMsg from './components/OrderMsg.vue'

defineOptions({
  name: 'OverViewIndex',
})

// 定义接口
interface TradeData {
  transactionVolume: number
  turnover: number
  views: number
}

interface TradeLineItem {
  date: string
  transactionVolume: number
  turnover: number
}

interface DayQueryData {
  trade: TradeData
  updateTime: string
  yesterdayTrade: TradeData
  tradeLineVos: TradeLineItem[]
}

// 数据响应式定义
const dateValue = ref<string[]>([])
const showYes = ref(true)

// 实时概况数据
const dayQuery = reactive<DayQueryData>({
  trade: {
    transactionVolume: 0,
    turnover: 0,
    views: 0,
  },
  updateTime: '',
  yesterdayTrade: {
    transactionVolume: 0,
    turnover: 0,
    views: 0,
  },
  tradeLineVos: [],
})

// 交易概况数据
const orderQuery = reactive({
  shipped: 0,
  waitForPay: 0,
  waitForPickup: 0,
  waitForSend: 0,
})

// 访客统计数据
const accountQuery = reactive({
  pv: 0,
  totalUv: 0,
  uv: 0,
})

// 绘制图表
function drawLine() {
  const dateList: string[] = []
  const transactionVolumeList: number[] = []
  const turnoverList: number[] = []
  const tradeLineVos = dayQuery.tradeLineVos

  tradeLineVos.forEach((item) => {
    const date = dayjs(item.date).format('MM-DD')
    dateList.push(date)
    transactionVolumeList.push(item.transactionVolume)
    turnoverList.push(item.turnover)
  })

  // 基于准备好的dom，初始化echarts实例
  const chartElement = document.getElementById('main')
  if (!chartElement) return

  const myChart = echarts.init(chartElement as HTMLDivElement)

  // 绘制图表
  myChart.setOption({
    backgroundColor: 'white',
    tooltip: {
      trigger: 'axis',
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true,
    },
    xAxis: {
      boundaryGap: false,
      data: dateList,
    },
    yAxis: {
      axisTick: {
        show: false,
      },
      splitLine: {
        // 网格线
        lineStyle: {
          type: 'dashed', // 设置网格线类型 dotted：虚线   solid:实线
        },
        show: true, // 隐藏或显示
      },
    },
    series: [
      {
        name: '交易额',
        type: 'line',
        smooth: true,
        areaStyle: {
          color: '#FFF9F5',
        },
        data: turnoverList,
        itemStyle: {
          normal: { color: '#FD7C25' },
        },
      },
      {
        name: '交易量',
        type: 'line',
        smooth: true,
        areaStyle: {
          color: '#F4F2F5',
        },
        data: transactionVolumeList,
        itemStyle: {
          normal: { color: '#0C67F5' },
        },
      },
    ],
  })
}

// 选择时间
function selectTime(e: any) {
  let date: string[] = []
  if (e) {
    date = [dayjs(e[0]).format('YYYY-MM-DD'), dayjs(e[1]).format('YYYY-MM-DD')]
    showYes.value = false
  } else {
    dateValue.value = []
    showYes.value = true
  }
  getOverView(date)
}

// 获取订单数据
function getOrder() {
  getManageOrder().then((res) => {
    Object.assign(orderQuery, res.data)
  })
}

// 获取数据概况
function getOverView(date = dateValue.value) {
  const param = {
    startTime: date.length === 0 ? '' : date[0],
    endTime: date.length === 0 ? '' : date[1],
  }
  const noData: TradeData = {
    transactionVolume: 0,
    turnover: 0,
    views: 0,
  }
  getManageOverView(param).then((res) => {
    dayQuery.trade = res.data.trade
    dayQuery.tradeLineVos = res.data.tradeLineVos
    dayQuery.updateTime = res.data.updateTime
    dayQuery.yesterdayTrade = res.data.yesterdayTrade ? res.data.yesterdayTrade : noData
    drawLine()
  })
}

// 获取访客数据
function getAccount() {
  getManageAccount().then((res) => {
    Object.assign(accountQuery, res.data)
  })
}

// 初始化信息
function init() {
  getOrder()
  getOverView()
  getAccount()
}

// 组件挂载时初始化
onMounted(() => {
  init()
})
</script>

<template>
  <div>
    <div style="background-color: #eef1f6">
      <div style="padding: 10px 0px; background-color: white">
        <div class="chart">
          <div class="chart__top">
            <div class="chart__top--left">
              实时概况
              <span class="chart__top--left--update">更新时间：{{ dayQuery.updateTime }}</span>
            </div>
            <div>
              日期
              <el-date-picker
                v-model="dateValue"
                type="daterange"
                range-separator="-"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                style="width: 230px; margin-left: 20px"
                @change="selectTime"
              />
            </div>
          </div>
          <div class="chart__top">
            <div class="chart__top--account">
              <div class="chart__top--account--content">
                <div class="titles">交易量</div>
                <div class="todayAccount">
                  {{ dayQuery.trade.transactionVolume }}
                  <i
                    v-if="dayQuery.trade.transactionVolume < dayQuery.yesterdayTrade.transactionVolume && dayQuery.yesterdayTrade.transactionVolume"
                    class="iconfont iconxiayi iconClass"
                  />
                  <i
                    v-if="dayQuery.trade.transactionVolume > dayQuery.yesterdayTrade.transactionVolume && dayQuery.yesterdayTrade.transactionVolume"
                    class="iconfont iconshangyi iconClass"
                  />
                </div>
                <div v-if="showYes" class="lastDay">昨日 {{ dayQuery.yesterdayTrade.transactionVolume }}</div>
              </div>
              <div class="chart__top--account--content">
                <div class="titles">交易额(元)</div>
                <div class="todayAccount">
                  {{ dayQuery.trade.turnover }}
                  <i
                    v-if="dayQuery.trade.turnover < dayQuery.yesterdayTrade.turnover && dayQuery.yesterdayTrade.turnover"
                    class="iconfont iconxiayi iconClass"
                  />
                  <i
                    v-if="dayQuery.trade.turnover > dayQuery.yesterdayTrade.turnover && dayQuery.yesterdayTrade.turnover"
                    class="iconfont iconshangyi iconClass"
                  />
                </div>
                <div v-if="showYes" class="lastDay">昨日 {{ dayQuery.yesterdayTrade.turnover }}</div>
              </div>
              <div class="chart__top--account--content">
                <div class="titles">浏览量</div>
                <div class="todayAccount">
                  {{ dayQuery.trade.views ? dayQuery.trade.views : 0 }}
                  <i
                    v-if="dayQuery.trade.views < dayQuery.yesterdayTrade.views && dayQuery.yesterdayTrade.views"
                    class="iconfont iconxiayi iconClass"
                  />
                  <i
                    v-if="dayQuery.trade.views > dayQuery.yesterdayTrade.views && dayQuery.yesterdayTrade.views"
                    class="iconfont iconshangyi iconClass"
                  />
                </div>
                <div v-if="showYes" class="lastDay">
                  昨日
                  {{ dayQuery.yesterdayTrade.views ? dayQuery.yesterdayTrade.views : 0 }}
                </div>
              </div>
            </div>
            <div id="main" style="height: 320px; width: 660px; right: -20px" />
          </div>
        </div>
      </div>
      <OrderMsg :order-query="orderQuery" />
      <Account :account-query="accountQuery" />
      <!-- <Ranking /> -->
    </div>
    <!-- <Exception/> -->
  </div>
</template>

<style lang="scss" scoped>
.chart {
  margin: 0px 20px;
  padding-top: 10px;
  height: 390px;
  background-color: white;

  .chart__top {
    display: flex;
    justify-content: space-between;

    .chart__top--left {
      font-size: 14px;
      font-weight: bold;
      color: #5f6779;

      .chart__top--left--update {
        margin-left: 20px;
        color: #a8adb7;
        font-weight: normal;
      }
    }

    .chart__top--account {
      margin-top: 40px;
      width: 350px;
      height: 280px;
      display: flex;
      flex-wrap: wrap;
      .chart__top--account--content {
        width: 150px;
        height: 120px;
        background-color: #fbfbfb;
        margin: 10px 10px 10px 0px;
        padding-top: 10px;
      }
    }
  }
}

.iconClass {
  margin-left: 5px;
  color: #a1a1a1;
  font-size: 20px;
}

.titles {
  padding-left: 40px;
  width: 150px;
  font-size: 14px;
  color: #969ba7;
  margin-top: 10px;
}

.todayAccount {
  width: 150px;
  padding-left: 40px;
  display: flex;
  font-size: 26px;
  font-weight: bold;
  align-items: center;
  color: #586074;
  margin-top: 12px;
}

.lastDay {
  padding-left: 40px;
  width: 150px;
  display: flex;
  font-size: 12px;
  align-items: center;
  color: #a6abb5;
  margin-bottom: 10px;
  margin-top: 12px;
}
</style>
