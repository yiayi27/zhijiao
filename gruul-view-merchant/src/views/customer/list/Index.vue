<script setup lang="ts">
import { getAllTags, getCustomerList } from '@/api/customer/customer'
import { getMemberCardRankList } from '@/api/customer/member'
import MContainer from '@/components/MContainer/MContainer.vue'
import PageManage from '@/components/PageManage.vue'
import SetDrop from '@/components/SetDrop/SetDrop.vue'
import BalanceDetail from '@/views/customer/list/components/dialog/BalanceDetail.vue'
import SetLabel from '@/views/customer/list/components/dialog/SetTags.vue'
import WithdrawalDetail from '@/views/customer/list/components/dialog/WithdrawalDetail.vue'

import { CaretBottom } from '@element-plus/icons-vue'

import { useRouter } from 'vue-router'
import BlackList from '../common/SetBlackList.vue'
import ChargeIntegral from './components/dialog/ChargeIntegral.vue'
import GiveCoupons from './components/dialog/GiveCoupons.vue'
import IntegralDetail from './components/dialog/IntegralDetail.vue'

// 定义接口和类型
interface CustomerDataForm {
  nikeName: string
  becomeMemberTime: string[]
  orderSuccessTime: string[]
  memberNumber: string
  rankCode: Long
  sortType: number
  tagId: Long
}

interface UserTag {
  tagId: Long
  tagName: string
}

interface CustomerRecord {
  userId: Long
  nikeName: string
  avatarUrl: string
  phone?: string
  firstLoginTime?: string
  memberNumber?: string
  memberRegisterTime?: string
  becomeMemberDayNumber?: number
  orderCount?: number
  consumeTotleMoney?: number
  firstNickName?: string
  firstPhone?: string
  supplyBonus?: number
  firstDistributionAmount?: number
  orderLastDealTime?: string
  userTagVos?: UserTag[]
  state?: number
  [key: string]: any
}

interface SortOption {
  value: number
  label: string
}

interface DropdownItem {
  command: string
  disabled: boolean
  show: boolean
  text: string
}

interface RankOption {
  rankName: string
  rankCode: Long
}

defineOptions({
  name: 'CustomerList',
})

const router = useRouter()

// Refs
const dataFormRef = ref()
const customerListRef = ref()
const blackListDialogRef = ref()
const labelDialogRef = ref()

// Reactive data
const dataForm = reactive<CustomerDataForm>({
  nikeName: '',
  becomeMemberTime: [],
  orderSuccessTime: [],
  memberNumber: '',
  rankCode: '',
  sortType: 1,
  tagId: '',
})

const dataList = ref<CustomerRecord[]>([])
const allTagList = ref<UserTag[]>([])
const selectionList = ref<CustomerRecord[]>([])
const multipleSelection = ref<CustomerRecord[]>([])
const couponVisible = ref(false)
const chargeVisible = ref(false)
const detailVisible = ref(false)
const balanceVisible = ref(false)
const WithdrawalVisible = ref(false)
const blackListVisible = ref(false)
const labelVisible = ref(false)
const rankOptions = ref<RankOption[]>([])

// Pagination
const pageSize = ref(10)
const pageNum = ref(1)
const total = ref(0)

const sortOptions: SortOption[] = [
  {
    value: 1,
    label: '按交易总额降序',
  },
  {
    value: 2,
    label: '按交易总额升序',
  },
  {
    value: 3,
    label: '按成为会员时间降序',
  },
  {
    value: 4,
    label: '按成为会员时间升序',
  },
]

/** 赠送优惠券下拉菜单 */
const couponDropList: DropdownItem[] = [
  {
    command: '设置标签',
    disabled: false,
    show: true,
    text: '设置标签',
  },
  {
    command: '积分充值',
    disabled: false,
    show: true,
    text: '积分充值',
  },
  // {
  //   command: "设为会员",
  //   disabled: false,
  //   show: true,
  //   text: "设为会员",
  // },
  {
    command: '加入黑名单',
    disabled: false,
    show: true,
    text: '加入黑名单',
  },
]

// Computed properties
const dropdownList = computed(() => {
  return (row: CustomerRecord) => {
    /** 操作项下拉菜单 */
    const drownList: DropdownItem[] = [
      {
        command: '余额明细',
        disabled: false,
        show: true,
        text: '余额明细',
      },
      {
        command: '提现明细',
        disabled: false,
        show: true,
        text: '提现明细',
      },
      {
        command: '积分充值',
        disabled: false,
        show: true,
        text: '积分充值',
      },
      {
        command: '积分明细',
        disabled: false,
        show: true,
        text: '积分明细',
      },
      {
        command: '加入黑名单',
        disabled: false,
        show: true,
        text: '加入黑名单',
      },
    ]
    return row.state === 2
      ? [
          {
            command: '加入会员',
            disabled: false,
            show: true,
            text: '加入会员',
          },
          ...drownList,
        ]
      : drownList
  }
})

// Methods
/**
 * 获取用户列表
 */
function getDataList() {
  const form = dataForm
  const param = {
    page: pageNum.value,
    size: pageSize.value,
    sort: form.sortType,
    memberNumber: form.memberNumber,
    rankCode: form.rankCode,
    nikeName: form.nikeName,
    tagId: form.tagId,
    becomeMemberStartTime: form.becomeMemberTime?.[0],
    becomeMemberEndTime: form.becomeMemberTime?.[1],
    orderSuccessStartTime: form.orderSuccessTime?.[0],
    orderSuccessEndTime: form.orderSuccessTime?.[1],
  }
  getCustomerList(param).then((res) => {
    dataList.value = res.data.list
    pageSize.value = res.data.size
    pageNum.value = res.data.current
    total.value = res.data.total
  })
  getTags()
}

/**
 * @method getTags
 * @description 获取所有标签
 */
function getTags() {
  getAllTags().then((res) => {
    allTagList.value = res.data
  })
}

function batchCouponClick() {
  if (multipleSelection.value.length > 0) {
    couponVisible.value = true
    selectionList.value = multipleSelection.value
  } else {
    ElMessage.warning('至少选择一个客户')
  }
}

function batchCouponCommand(command: string) {
  if (multipleSelection.value.length > 0) {
    switch (command) {
      case '设置标签':
        labelVisible.value = true
        nextTick(() => {
          labelDialogRef.value.init(multipleSelection.value)
        })
        break
      case '加入黑名单':
        blackListVisible.value = true
        nextTick(() => {
          blackListDialogRef.value.init(multipleSelection.value, 1)
        })
        break
      case '积分充值':
        chargeVisible.value = true
        selectionList.value = multipleSelection.value
        break
    }
  } else {
    ElMessage.info('请至少选择一个客户')
  }
}

function itemClick(row: CustomerRecord) {
  couponVisible.value = true
  selectionList.value = [row]
}

function itemCommand(command: string, row: CustomerRecord) {
  switch (command) {
    case '加入会员':
      router.push({
        name: 'SettingMember',
        query: {
          id: row.userId,
          nikeName: row.nikeName,
          img: row.avatarUrl,
        },
      })
      break
    case '加入黑名单':
      blackListVisible.value = true
      nextTick(() => {
        blackListDialogRef.value.init([row], 1)
      })
      break
    case '积分充值':
      chargeVisible.value = true
      selectionList.value = [row]
      break
    case '积分明细':
      detailVisible.value = true
      selectionList.value = [row]
      break
    case '余额明细':
      balanceVisible.value = true
      selectionList.value = [row]
      break
    case '提现明细':
      WithdrawalVisible.value = true
      selectionList.value = [row]
      break
  }
}

function onSetLabel(row: CustomerRecord) {
  labelVisible.value = true
  nextTick(() => {
    labelDialogRef.value.init([row])
  })
}

/**
 * @method handleSizeChange
 * @description 每页 条
 */
function handleSizeChange(val: number) {
  pageSize.value = val
  pageNum.value = 1
  getDataList()
}

/**
 * @method handleCurrentChange
 * @description 当前页
 */
function handleCurrentChange(val: number) {
  pageNum.value = val
  getDataList()
}

// 等级下拉列表数据请求
function getRank() {
  getMemberCardRankList().then((res) => {
    if (res.code === 200) {
      rankOptions.value = res.data || []
    }
  })
}

// Lifecycle hooks
onMounted(() => {
  getDataList()
  getRank()
})
</script>

<template>
  <MContainer class="customer">
    <template #form>
      <m-card class="form" :need-toggle="true">
        <el-form ref="dataFormRef" class="customer__dataForm" :model="dataForm" label-width="100px">
          <el-row :gutter="40">
            <el-col :span="10">
              <el-form-item label="微信昵称">
                <el-input v-model="dataForm.nikeName" clearable placeholder="请输入会员昵称" @keyup.enter="getDataList" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="标  签">
                <el-select v-model="dataForm.tagId" placeholder="请选择标签" style="width: 256px" clearable>
                  <el-option label="全部" value="" />
                  <el-option v-for="tag in allTagList" :key="tag.tagId" :label="tag.tagName" :value="tag.tagId" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="40">
            <el-col :span="10">
              <el-form-item label="上次交易时间">
                <el-date-picker
                  v-model="dataForm.orderSuccessTime"
                  value-format="YYYY-MM-DD"
                  style="width: 256px"
                  type="daterange"
                  range-separator="-"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                />
              </el-form-item>
            </el-col>
            <el-col :span="10">
              <el-form-item label="成为会员时间">
                <el-date-picker
                  v-model="dataForm.becomeMemberTime"
                  type="daterange"
                  value-format="YYYY-MM-DD"
                  style="width: 256px"
                  range-separator="-"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="40">
            <el-col :span="10">
              <el-form-item label="会员等级">
                <el-select v-model="dataForm.rankCode" style="width: 256px" placeholder="选择会员等级">
                  <el-option label="全部" value="" />
                  <el-option
                    v-for="(item, index) in rankOptions"
                    :key="index"
                    :label="`${item.rankName}(LV${item.rankCode})`"
                    :value="item.rankCode"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="会员卡号">
                <el-input v-model="dataForm.memberNumber" style="width: 256px" clearable placeholder="请输入会员卡号" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-button type="primary" style="margin-left: 100px" @click="handleCurrentChange(1)"> 搜索 </el-button>
        </el-form>
      </m-card>
    </template>
    <template #section>
      <div class="customer__filterForm flex justify-between">
        <div>
          <SetDrop set-name="赠送优惠券" :dropdown-list="couponDropList" @set-click="batchCouponClick" @command="batchCouponCommand" />
        </div>
        <div>
          <el-select v-model="dataForm.sortType" placeholder="请选择排序方式" class="w-[200px]" @change="handleCurrentChange(1)">
            <el-option v-for="item in sortOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </div>
      </div>
    </template>
    <template #table>
      <m-table
        ref="customerListRef"
        v-model:checked-item="multipleSelection"
        :data="dataList"
        :selection="true"
        need-hover-border
        class="customerList"
        :row-key="(row: Obj) => String(row.userId)"
      >
        <template #header="{ row }: { row: Obj }">
          <div v-if="!!row.memberNumber" style="width: 100%">
            <span style="margin-right: 50px">会员卡号：{{ row.memberNumber }}</span>
            <span>成为会员时间:{{ row.memberRegisterTime }}</span>
            <span>(已成为会员{{ row.becomeMemberDayNumber }}天)</span>
            <span style="padding-left: 15px">成为用户时间:{{ row.firstLoginTime }}</span>
          </div>
          <div v-else style="width: 100%">
            <span>成为用户时间:{{ row.firstLoginTime }}</span>
          </div>
        </template>
        <m-table-column prop="userName" label="客户信息" :shows-slection="true" width="200">
          <template #default="{ row }: { row: CustomerRecord }">
            <div class="info">
              <img class="info__img" :src="row.avatarUrl" />
              <div class="info__msg">
                <div class="info__msg--text">
                  <div class="truncate">
                    {{ row.nikeName }}
                  </div>
                  <div v-if="row.phone">({{ row.phone }})</div>
                </div>
                <div class="info__msg--tags flex items-center">
                  <span v-if="row.userTagVos && row.userTagVos?.length > 0" class="pointer" @click="onSetLabel(row)">
                    <el-button link class="p-0">
                      {{
                        row.userTagVos
                          .map((tag: UserTag) => tag.tagName)
                          .slice(0, 2)
                          .join(' ; ')
                      }}
                    </el-button>
                    <span class="text-[12px]">等共{{ row.userTagVos?.length || 0 }}个标签</span>
                  </span>
                  <el-button v-if="row.userTagVos === null || row.userTagVos?.length === 0" link @click="onSetLabel(row)"> 请选择所属标签 </el-button>
                  <el-icon v-if="!row.userTagVos?.length" class="pointer inline-block" @click="onSetLabel(row)">
                    <CaretBottom />
                  </el-icon>
                </div>
              </div>
            </div>
          </template>
        </m-table-column>
        <m-table-column prop="userName" label="分销订单">
          <template #default="{ row }">
            <span>{{ row.orderCount }}</span>
          </template>
        </m-table-column>
        <m-table-column prop="userName" label="交易总额">
          <template #default="{ row }">
            <span>{{ row.consumeTotleMoney }}</span>
          </template>
        </m-table-column>
        <m-table-column prop="userName" label="所属分销商" width="120">
          <template #default="{ row }">
            <div>
              <span>{{ row.firstNickName }}</span
              ><br />
              {{ row.firstPhone }}
            </div>
          </template>
        </m-table-column>
        <m-table-column prop="userName" label="可用余额" width="150">
          <template #default="{ row }">
            <div>
              <span>当前余额：{{ row.supplyBonus }}</span
              ><br />
              累计佣金：{{ row.firstDistributionAmount }}
            </div>
          </template>
        </m-table-column>
        <m-table-column prop="userName" label="上次交易时间" width="160">
          <template #default="{ row }">
            <span>{{ row.orderLastDealTime }}</span>
          </template>
        </m-table-column>
        <m-table-column prop="userName" label="操作" width="150">
          <template #default="{ row }">
            <SetDrop set-name="赠送优惠券" :dropdown-list="dropdownList(row)" @set-click="itemClick(row)" @command="itemCommand($event, row)" />
          </template>
        </m-table-column>
      </m-table>
    </template>

    <template #pagination>
      <PageManage
        :page-size="pageSize"
        :page-num="pageNum"
        :total="total"
        class="PageManage"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
      />
    </template>

    <GiveCoupons v-model:visible="couponVisible" :checked-item="selectionList" @refresh-data-list="getDataList()" />
    <ChargeIntegral v-model:visible="chargeVisible" :checked-item="selectionList as any[]" @refresh-data-list="getDataList()" />
    <IntegralDetail v-model:visible="detailVisible" :checked-item="selectionList" @refresh-data-list="getDataList()" />
    <BalanceDetail v-model:visible="balanceVisible" :checked-item="selectionList" @refresh-data-list="getDataList()" />
    <WithdrawalDetail v-model:visible="WithdrawalVisible" :checked-item="selectionList" @refresh-data-list="getDataList()" />
    <BlackList v-if="blackListVisible" ref="blackListDialogRef" @refresh-data-list="getDataList" />
    <SetLabel v-if="labelVisible" ref="labelDialogRef" :all-tags="allTagList" @refresh-data-list="getDataList()" />
  </MContainer>
</template>

<style lang="scss" scoped>
@import '../../../assets/styles/cutomer/customer';
</style>
