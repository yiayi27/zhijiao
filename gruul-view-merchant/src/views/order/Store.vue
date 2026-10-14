<script setup lang="ts">
import { getMemberOrderInfo, remarks as remarksApi } from '@/api/customer/member'
import MContainer from '@/components/MContainer/MContainer.vue'
import PageManage from '@/components/PageManage.vue'
import { Search } from '@element-plus/icons-vue'
import dayjs from 'dayjs'

import RemarkModal from './components/RemarkModal.vue'

defineOptions({
  name: 'OrderStore',
})

// 定义类型接口
interface FilterParams {
  page: number
  becomeMemberTime: string[]
  searchContent: string
  [key: string]: any
}

interface OrderItem {
  id: string
  orderId: string
  note?: string
  avatarUrl: string
  nikeName: string
  rankName: string
  payAmount: number
  payTime: string
  usableAmount: number
}

// 过滤条件
const filter = ref<FilterParams>({
  page: 1,
  becomeMemberTime: [],
  searchContent: '',
})

// 表格数据
const dataList = ref<OrderItem[]>([])

// 分页条数
const pageSize = ref(20)

// 页码
const pageNum = ref(1)

// 总数
const total = ref(1)

// 备注visible
const remarklVisible = ref(false)

/** 当前选中的订单 */
const currentOrder = ref<OrderItem>()

/** 选择的订单ID组 */
const orderIds = ref<string[]>([])

onMounted(() => {
  // 设置默认为今天的日期范围
  filter.value.becomeMemberTime = [dayjs().startOf('day').format('YYYY-MM-DD HH:mm:ss'), dayjs().endOf('day').format('YYYY-MM-DD HH:mm:ss')]
  getDataList()
})

function getDataList() {
  // 创建请求参数对象
  const requestParams: Record<string, any> = {
    ...filter.value,
    size: pageSize.value,
  }

  // 处理日期转换
  requestParams.startTime = filter.value.becomeMemberTime?.[0]
  requestParams.endTime = filter.value.becomeMemberTime?.[1]

  // 移除不需要发送的参数
  delete requestParams.becomeMemberTime

  // 发送请求
  getMemberOrderInfo(requestParams).then(({ data }) => {
    dataList.value = data.list || []
    total.value = data.total || 0
    pageNum.value = data.current || 1
  })
}

function toggleRemarklVisible() {
  remarklVisible.value = !remarklVisible.value
}

interface RemarkForm {
  note: string
  over?: boolean
}

// 打开描述modal
function triggerRemark(orderData: OrderItem) {
  currentOrder.value = orderData
  orderIds.value = [orderData.id]
  toggleRemarklVisible()
}

/**
 * 备注
 * @param form 表单数据
 */
function handleRemarks(form: RemarkForm) {
  if (!form || !orderIds.value.length) return

  // 处理表单数据
  const remarkData = { ...form }
  if (currentOrder.value?.note && !form.over) {
    remarkData.note = `${currentOrder.value.note}\n${form.note}`
  }

  // 发送备注请求
  remarksApi({ orderIds: orderIds.value, ...remarkData }).then(() => {
    getDataList()
    toggleRemarklVisible()
    ElMessage.success('备注成功')
  })
}

function handleCurrentChange(val: number) {
  pageNum.value = val
  filter.value.page = val
  getDataList()
}

function handleSizeChange(val: number) {
  pageSize.value = val
  getDataList()
}
</script>

<template>
  <MContainer>
    <template #form>
      <div class="filter">
        <div>
          付款时间&nbsp;
          <el-date-picker
            v-model="filter.becomeMemberTime"
            type="datetimerange"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD HH:mm:ss"
            @change="getDataList"
          />
        </div>
        <div>
          <el-input v-model="filter.searchContent" placeholder="订单编号微信昵称搜索" class="input-with-select">
            <template #append>
              <el-button :icon="Search" @click="getDataList" />
            </template>
          </el-input>
        </div>
      </div>
    </template>

    <template #table>
      <m-table :data="dataList" need-hover-border class="customerList">
        <template #header="{ row }">
          <div style="width: 100%; position: relative">
            <span style="margin-right: 50px">订单编号：{{ row.orderId }}</span>
            <span>
              <el-popover placement="bottom-start" width="200" trigger="hover" :disabled="!row.note">
                <template #default>
                  <div>备注：{{ row.note }}</div>
                </template>
                <template #reference>
                  <i class="iconfont iconPC-beizhu" :class="{ red: row.note }" @click="triggerRemark(row)" />
                </template>
              </el-popover>
            </span>
          </div>
        </template>
        <m-table-column v-slot="{ row }" label="客户信息" width="240" height="80">
          <div class="info">
            <img class="info__img" :src="row.avatarUrl" />
            <div class="info__msg">
              <div class="info__msg--text">
                {{ row.nikeName }}
              </div>
            </div>
          </div>
        </m-table-column>
        <m-table-column label="会员等级名称" prop="rankName" />
        <m-table-column prop="payAmount" label="充值金额" />
        <m-table-column prop="payTime" label="付款时间" />
        <m-table-column v-slot="{ row }" prop="usableAmount" label="可用余额"> ¥{{ row.usableAmount }} </m-table-column>
      </m-table>
    </template>

    <template #pagination>
      <PageManage
        v-model:page-num="pageNum"
        :page-size="pageSize"
        :total="total"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
      />
    </template>

    <RemarkModal
      v-model="remarklVisible"
      title="备注"
      :current-order="currentOrder"
      :order-ids="orderIds"
      @on-ok="handleRemarks"
      @on-cancel="toggleRemarklVisible"
    />
  </MContainer>
</template>

<style lang="scss" scoped>
@import '../../assets/styles/cutomer/customer';
@import '@/assets/styles/order/order.scss';

.filter {
  display: flex;
  justify-content: space-between;
}

.customerList {
  :deep(th) {
    text-align: left;
  }

  :deep(.m__table--body .body--content td .item__content) {
    justify-content: left;
  }
}
</style>
