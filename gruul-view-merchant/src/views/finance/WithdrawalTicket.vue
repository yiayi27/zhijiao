<script setup lang="ts">
import { bulkNote as apiBulkNote, getWithdrawalList as apiGetWithdrawalList, pass as apiPass, reject as apiReject } from '@/api/finance/index'
import MContainer from '@/components/MContainer/MContainer.vue'
import RemarkModal from '@/views/order/components/RemarkModal.vue'
import dayjs from 'dayjs'
import { ElLoading, ElMessageBox } from 'element-plus'

import { useRoute } from 'vue-router'

defineOptions({
  name: 'FinanceWithdrawalTicket',
})

const route = useRoute()
const formRef = ref()

// 计算开始时间和结束时间
const startTime = dayjs().subtract(7, 'day').format('YYYY-MM-DD')
const endTime = dayjs().add(7, 'day').format('YYYY-MM-DD')

// 响应式数据
const total = ref(0)
const list = ref([])
const tableCheckedItem = ref([])
const createTime = ref([startTime, endTime])
const remarklVisible = ref(false)
const currentList = ref<WithdrawalItem[]>([])

// 表单数据
const form = reactive({
  // ASS, AREA_ASS, POINT
  accountType: '',
  auditStatus: 'PENDING_PARTNER',
  id: '',
  keyword: '',
  name: '',
  applyTime: '',
  startTime,
  endTime,
  current: 1,
  size: 10,
})

const options = [
  {
    label: '普通会员',
    value: '',
  },
]

// 计算属性
const isPending = computed(() => {
  return form.auditStatus === 'PENDING_PARTNER'
})

const columns = computed(() => {
  return [
    {
      label: '基本信息',
      width: 100,
    },
    {
      label: '账户信息',
      width: 90,
      coustomStyle: 'text-align: left;',
    },
    {
      label: '提现信息',
      width: 100,
    },
    {
      label: '结算方式',
      width: 60,
    },
    {
      label: '提现方式',
      width: 60,
    },
    {
      label: '状态',
      width: 60,
    },
  ].concat(
    isPending.value
      ? [
          {
            label: '操作',
            width: 160,
          },
        ]
      : [],
  )
})

// 监听属性变化
watch(
  () => form.size,
  () => {
    getWithdrawalList()
  },
)

watch(
  () => form.current,
  () => {
    getWithdrawalList()
  },
)

watch(
  () => form.auditStatus,
  () => {
    getWithdrawalList()
  },
)

watch(
  () => form.accountType,
  () => {
    getWithdrawalList()
  },
)

// 定义类型接口
interface WithdrawalItem {
  id: string
  userName: string
  userAvatarUrl: string
  phone: string
  channel: ChannelType
  amount: number
  account?: string
  bankName?: string
  createTime: string
  updateTime?: string
  auditStatus: AuditStatusType
  note?: string
  [key: string]: any
}

type ChannelType = 'WECAHT' | 'ALIPAY' | 'BANK' | 'OFFLINE'
type AuditStatusType = 'PENDING_PARTNER' | 'PENDING_MAIN' | 'PASSED' | 'REJECTED'

// 获取工单列表
function getWithdrawalList() {
  apiGetWithdrawalList({ ...form, ...route.query }).then((res) => {
    const { list: resList, total: resTotal, ...other } = res.data
    list.value = resList
    total.value = resTotal
    Object.assign(form, other)
  })
}

// 选择时间
function chooseTime(data: [string, string] | null) {
  form.startTime = data?.[0] ?? ''
  form.endTime = data?.[1] ?? ''
  getWithdrawalList()
}

// 批量备注请求
function bulkNoteRequest(data: { note: string; over: boolean }) {
  apiBulkNote(
    currentList.value.map((item: any) => item.id),
    data.note,
    data.over,
  ).then(() => {
    getWithdrawalList()
    toggleRemarklVisible()
    ElMessage.success('操作成功')
  })
}

// 批量备注
function bulkNote(noteList: WithdrawalItem[]) {
  currentList.value = noteList
  toggleRemarklVisible()
}

// 拒绝
function reject(row: WithdrawalItem) {
  ElMessageBox.prompt('', '拒绝说明', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputType: 'textarea',
    inputPattern: /\S+/,
    inputErrorMessage: '拒绝说明不可为空',
  }).then((res) => {
    apiReject({ id: row.id, reason: res.value }).then(() => {
      getWithdrawalList()
      ElMessage.success('操作成功')
    })
  })
}

// 通过
function pass(row: WithdrawalItem) {
  const requestFunc = (data: { id: string; tradeNo?: string; channel?: string }) => {
    const loadingInstance = ElLoading.service({
      lock: true,
      text: 'Loading',
      spinner: 'el-icon-loading',
      background: 'rgba(0, 0, 0, 0.7)',
    })

    apiPass({ ...data, channel: '0' })
      .then(() => {
        getWithdrawalList()
        ElMessage.success('操作成功')
      })
      .finally(() => {
        loadingInstance.close()
      })
  }

  // 如果提现方式不为微信需输入交易流水号
  if (row.channel !== 'WECAHT') {
    ElMessageBox.confirm('确认结算', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
    }).then((res) => {
      requestFunc({ id: row.id, tradeNo: res.value })
    })
    return
  }

  const loadingInstance = ElLoading.service({
    lock: true,
    text: 'Loading',
    spinner: 'el-icon-loading',
    background: 'rgba(0, 0, 0, 0.7)',
  })

  ElMessageBox.confirm('确认结算', '提示')
    .then(() => {
      requestFunc({ id: row.id })
    })
    .catch(() => {
      loadingInstance.close()
    })
}

// 线下结算
function passOffline(row: WithdrawalItem) {
  const requestFunc = (data: { id: string; tradeNo?: string; channel?: string }) => {
    const loadingInstance = ElLoading.service({
      lock: true,
      text: 'Loading',
      spinner: 'el-icon-loading',
      background: 'rgba(0, 0, 0, 0.7)',
    })

    apiPass({ ...data, channel: '3' })
      .then(() => {
        getWithdrawalList()
        ElMessage.success('操作成功')
      })
      .finally(() => {
        loadingInstance.close()
      })
  }

  // 如果提现方式不为微信需输入交易流水号
  if (row.channel !== 'WECAHT') {
    ElMessageBox.confirm('确认结算', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
    }).then((res) => {
      requestFunc({ id: row.id, tradeNo: res.value })
    })
    return
  }

  const loadingInstance = ElLoading.service({
    lock: true,
    text: 'Loading',
    spinner: 'el-icon-loading',
    background: 'rgba(0, 0, 0, 0.7)',
  })

  ElMessageBox.confirm('确认结算', '提示')
    .then(() => {
      requestFunc({ id: row.id })
    })
    .catch(() => {
      loadingInstance.close()
    })
}

// 获取渠道名称
function getChannel(key: ChannelType, needSuff = false) {
  const channelMap: Record<ChannelType, string> = {
    WECAHT: '微信',
    ALIPAY: '支付宝',
    BANK: '银行卡',
    OFFLINE: '线下结算',
  }
  return channelMap[key] + (needSuff ? '号' : '')
}

// 获取渠道名称
function getChanneName(key: ChannelType) {
  const channelMap: Record<ChannelType, string> = {
    WECAHT: '微信钱包',
    ALIPAY: '支付宝',
    BANK: '银行卡',
    OFFLINE: '线下结算',
  }
  return channelMap[key]
}

// 获取渠道值
function getChannelVal(key: ChannelType, item: WithdrawalItem) {
  const channelValMap: Partial<Record<ChannelType, string | undefined>> = {
    WECAHT: item.userName,
    ALIPAY: item.account,
    BANK: item.account,
  }
  return channelValMap[key] || ''
}

// 时间文本
function timeText(key: AuditStatusType) {
  const timeMap: Partial<Record<AuditStatusType, string>> = {
    PASSED: '到账时间',
    REJECTED: '拒绝时间',
  }
  return timeMap[key] || ''
}

// 获取审核状态名称
function getAuditStatusName(key: AuditStatusType) {
  const statusMap: Record<AuditStatusType, string> = {
    PENDING_PARTNER: '待审核',
    PENDING_MAIN: '审核中',
    PASSED: '已成功',
    REJECTED: '已拒绝',
  }
  return statusMap[key]
}

// 切换备注可见性
function toggleRemarklVisible() {
  remarklVisible.value = !remarklVisible.value
}

// 生命周期钩子
onMounted(() => {
  getWithdrawalList()
})
</script>

<template>
  <MContainer v-model:current="form.current" v-model:size="form.size" :total="total" show-pagination class="ticket">
    <template #form>
      <m-card class="form" hide-text="展开搜索条件" show-text="收起搜索条件" :need-toggle="true">
        <el-form ref="formRef" label-width="80px">
          <el-row>
            <el-col :span="12">
              <el-form-item label="姓名">
                <el-input v-model="form.name" placeholder="请输入姓名" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="ID">
                <el-input v-model="form.id" placeholder="请输入ID" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="申请时间">
                <el-date-picker
                  v-model="createTime"
                  style="width: 390px"
                  type="daterange"
                  range-separator="-"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  value-format="YYYY-MM-DD"
                  @change="chooseTime"
                />
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item>
                <el-button type="primary" @click="getWithdrawalList"> 搜索 </el-button>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </m-card>
    </template>

    <template #tabs>
      <el-tabs v-model="form.auditStatus">
        <el-tab-pane label="待审核" name="PENDING_PARTNER" />
        <!-- <el-tab-pane label="审核中" name="PENDING_MAIN"> </el-tab-pane> -->
        <el-tab-pane label="已打款" name="PASSED" />
        <el-tab-pane label="已拒绝" name="REJECTED" />
      </el-tabs>
    </template>

    <template #section>
      <div class="ticket__form">
        <div>
          <el-button :disabled="!tableCheckedItem.length" @click="bulkNote(tableCheckedItem)"> 批量备注 </el-button>
        </div>
        <div class="w-[200px]">
          <el-select v-model="form.accountType" clearable placeholder="请选择" :empty-values="[null, undefined]">
            <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </div>
      </div>
    </template>

    <template #table>
      <m-table v-model:checked-item="tableCheckedItem" class="table" :data="list" :selection="true" :columns="columns" :custom="true">
        <template #header="{ row }">
          <div style="width: 100%">
            <span class="mr-50">ID：{{ row.id }}</span>
            <span class="mr-50">申请时间：{{ row.createTime }}</span>
            <span v-if="row.updateTime && timeText(row.auditStatus)" class="mr-50">{{ timeText(row.auditStatus) }}：{{ row.updateTime }}</span>

            <span style="float: right" :class="{ 'has-data': row.note }" @click="bulkNote([row])"><i class="iconfont iconPC-beizhu" /></span>
          </div>
        </template>
        <template #custom-body="{ row }">
          <tr>
            <td>
              <div class="info">
                <img class="info--l" :src="row.userAvatarUrl" style="margin-right: 10px" />
                <div class="info--r">
                  {{ row.userName }}
                  <br />
                  {{ row.phone }}
                </div>
              </div>
            </td>
            <td style="width: 10px">
              <span v-if="row.channel === 'BANK'">开户行：{{ row.bankName }} </span>
              <br />
              <div v-if="row.channel === 'OFFLINE'">
                {{ getChannel(row.channel) }}
              </div>
              <div v-if="row.channel === 'WECAHT'">
                {{ getChannel(row.channel, true) }}：
                {{ getChannelVal(row.channel, row) }}
              </div>
            </td>
            <td class="tc">
              申请提现金额：{{ row.amount }} <br />
              <!-- 入股费：10 <br>
            实际打款：10 <br> -->
            </td>
            <td class="tc">
              {{ row.channel === 'WECAHT' ? '自动打款' : '手动打款' }}
            </td>
            <td class="tc">
              {{ getChanneName(row.channel) }}
            </td>
            <td class="tc">
              {{ getAuditStatusName(row.auditStatus) }}
            </td>
            <td v-if="isPending">
              <el-button type="primary" round @click="pass(row)"> 结算 </el-button>
              <el-button type="primary" round @click="passOffline(row)"> 线下结算 </el-button>
              <el-button type="danger" plain round @click="reject(row)"> 拒绝 </el-button>
            </td>
          </tr>
        </template>
      </m-table>
    </template>

    <RemarkModal
      v-model="remarklVisible"
      :current-order="currentList[0]"
      :title="currentList.length === 1 ? '备注' : '批量备注'"
      @on-ok="bulkNoteRequest"
      @on-cancel="toggleRemarklVisible"
    />
  </MContainer>
</template>

<style lang="scss" scoped>
.ticket {
  .info {
    display: flex;
    justify-content: flex-start;
    align-items: center;

    .info--l {
      width: 50px;
    }
  }

  &__form {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin: 0 0 10px;
  }

  .table {
    margin-bottom: 10px;
  }

  .mr-50 {
    margin-right: 50px;
  }

  .has-data {
    color: red;
  }

  .tc {
    text-align: center;
  }
}
</style>
