<script setup lang="ts">
import { getAllTags } from '@/api/customer/customer'
import { getMemberCardRank, getMemberList } from '@/api/customer/member'
import MContent from '@/components/MContainer/MContent.vue'
import PageManage from '@/components/PageManage.vue'
import SetDrop from '@/components/SetDrop/SetDrop.vue'
import MemberRecord from '@/views/customer/list/components/dialog/MemberRecord.vue'
import RenewMember, { type MemberInfo } from '@/views/customer/list/components/dialog/RenewMember.vue'
import SetLabel from '@/views/customer/list/components/dialog/SetTags.vue'

import UpgradeMember from '@/views/customer/list/components/dialog/UpgradeMember.vue'
import { CaretBottom } from '@element-plus/icons-vue'
import dayjs from 'dayjs'

// Interface definitions
interface MemberFilter {
  sortType: number
  tagId: string | number
  rankCode: string | number
  nikeName: string
  becomeMemberTime: string[]
  orderSuccessTime: string[]
  memberNumber: string
}

interface UserTag {
  tagId: string | number
  tagName: string
}

// Ensure UserItem type matches what SetLabel.init expects
interface UserItem {
  userId: string
  nikeName: string
  avatarUrl: string
  phone?: string
  userTagVos?: UserTag[]
  [key: string]: any
}

export interface IMemberRecord {
  userId?: string | number
  nikeName: string
  avatarUrl: string
  phone?: string
  consumeNum?: number
  consumeTotleMoney?: number
  integral?: number
  supplyBonus?: number
  orderLastDealTime?: string
  userTagVos?: UserTag[]
  memberInnerListVos: MemberInfo[]
  [key: string]: any
}

interface DropdownItem {
  command: string
  show: boolean
  disabled: boolean
  text: string
}

interface RankOption {
  id: string | number
  rankName: string
  rankCode: string | number
}

interface SortOption {
  value: number
  label: string
}

const labelDialogRef = ref<InstanceType<typeof SetLabel> | null>(null)
const renewMemberRef = ref<InstanceType<typeof RenewMember> | null>(null)
const upgradeMemberRef = ref<InstanceType<typeof UpgradeMember> | null>(null)
const memberRecordRef = ref<InstanceType<typeof MemberRecord> | null>(null)
const customerListRef = ref(null)

// 过滤条件
const filter = reactive<MemberFilter>({
  sortType: 3,
  tagId: '',
  rankCode: '',
  nikeName: '',
  becomeMemberTime: [],
  orderSuccessTime: [],
  memberNumber: '',
})

// 会员身份数据
const rankOptions = ref<RankOption[]>([])

// 排序方式
const sorts: SortOption[] = [
  { value: 1, label: '按照交易总额降序' },
  { value: 2, label: '按照交易总额升序' },
  { value: 3, label: '按照成为会员时间降序' },
  { value: 4, label: '按照成为会员时间升序' },
]

// 表格数据
const dataList = ref<IMemberRecord[]>([])

// 分页条数
const pageSize = ref(20)

// 页码
const pageNum = ref(1)

// 总数
const total = ref(0)

// 标签
const allTagList = ref<UserTag[]>([])

// 选中的标签
const multipleSelection = ref<IMemberRecord[]>([])

// 设置标签modal的显示
const labelVisible = ref(false)

onMounted(() => {
  getDataList()
  getRankList()
})

// 等级下拉列表数据请求
function getRankList(): void {
  getMemberCardRank({}).then((res) => {
    if (res.code === 200) {
      rankOptions.value = res.data || []
    }
  })
}

/**
 * 获取用户列表
 */
function getDataList(): void {
  const param = {
    page: pageNum.value,
    size: pageSize.value,
    sortType: filter.sortType,
    memberNumber: filter.memberNumber,
    rankCode: filter.rankCode,
    nikeName: filter.nikeName,
    tagId: filter.tagId,
    becomeMemberStartTime: filter.becomeMemberTime?.length ? dayjs(filter.becomeMemberTime[0]).startOf('day').format('YYYY-MM-DD HH:mm:ss') : '',
    becomeMemberEndTime: filter.becomeMemberTime?.length ? dayjs(filter.becomeMemberTime[1]).endOf('day').format('YYYY-MM-DD HH:mm:ss') : '',
    orderSuccessStartTime: filter.orderSuccessTime?.length ? dayjs(filter.orderSuccessTime[0]).startOf('day').format('YYYY-MM-DD HH:mm:ss') : '',
    orderSuccessEndTime: filter.orderSuccessTime?.length ? dayjs(filter.orderSuccessTime[0]).endOf('day').format('YYYY-MM-DD HH:mm:ss') : '',
  }
  getMemberList(param).then((res) => {
    dataList.value = res.data.list
    pageSize.value = res.data.size
    pageNum.value = res.data.current
    total.value = res.data.total
  })
  getTags()
}

// 获取标签总数
function getTags(): void {
  getAllTags().then((res) => {
    allTagList.value = res.data
  })
}

// 表格操作项'...'的点击
function itemClick(row: IMemberRecord): void {
  renewMemberRef.value?.init(row)
}

function handleChange(): void {
  pageNum.value = 1
  getDataList()
}

// 表格分页变化
function handleCurrentChange(val: number): void {
  pageNum.value = val
  getDataList()
}

// 表格尺寸变化
function handleSizeChange(val: number): void {
  pageSize.value = val
  getDataList()
}

// 操作下拉
const dropdownList = computed(() => {
  return (): DropdownItem[] => {
    return [
      {
        command: '升级会员',
        show: true,
        disabled: false,
        text: '升级会员',
      },
      {
        command: '会员记录',
        show: true,
        disabled: false,
        text: '会员记录',
      },
    ]
  }
})

// 表格操作点击后的下拉
function itemCommand(command: string, row: IMemberRecord): void {
  switch (command) {
    case '升级会员':
      upgradeMemberRef.value?.init(row)
      break
    case '会员记录':
      memberRecordRef.value?.init(row)
      break
  }
}

// 将 MemberRecord 转换为 UserItem
function convertToUserItem(record: IMemberRecord): UserItem {
  const { userId, ...rest } = record
  return {
    userId: String(userId),
    ...rest,
  }
}

// 行设置标签
function onSetLabel(row: IMemberRecord): void {
  labelVisible.value = true
  nextTick(() => {
    labelDialogRef.value?.init([convertToUserItem(row)])
  })
}

// 批量设置标签
function onBatchSetLabel(): void {
  if (multipleSelection.value.length > 0) {
    labelVisible.value = true
    nextTick(() => {
      const userItems = multipleSelection.value.map(convertToUserItem)
      labelDialogRef.value?.init(userItems)
    })
  } else {
    ElMessage({
      message: '请选择会员',
      type: 'warning',
    })
  }
}
</script>

<template>
  <MContent class="mList">
    <template #form>
      <m-card class="form" :need-toggle="true" show-text="收起搜索条件" hide-text="展开搜索条件">
        <div class="lfilter">
          <div>
            <div class="lfilter__item filter__name">
              <span>微信昵称</span>
              <el-input v-model="filter.nikeName" placeholder="输入会员昵称" />
            </div>
            <div class="lfilter__item">
              <span>成为会员时间</span>
              <el-date-picker v-model="filter.becomeMemberTime" type="daterange" start-placeholder="开始日期" end-placeholder="结束日期" />
            </div>
          </div>
          <div>
            <div class="lfilter__item">
              <span>标签</span>
              <el-select v-model="filter.tagId" clearable placeholder="请选择标签">
                <el-option label="全部" value="" />
                <el-option v-for="tag in allTagList" :key="`${tag.tagId}tagId`" :label="tag.tagName" :value="tag.tagId" />
              </el-select>
            </div>
            <div class="lfilter__item">
              <span>上次交易时间</span>
              <el-date-picker v-model="filter.orderSuccessTime" type="daterange" start-placeholder="开始日期" end-placeholder="结束日期" />
            </div>
          </div>
          <div>
            <div class="lfilter__item">
              <span>会员等级</span>
              <el-select v-model="filter.rankCode" placeholder="选择会员等级">
                <el-option label="全部" value="" />
                <el-option v-for="item in rankOptions" :key="item.id" :label="`${item.rankName}(LV${item.rankCode})`" :value="item.rankCode" />
              </el-select>
            </div>
            <!-- <div class="lfilter__item">
              <span>会员卡号</span>
              <el-input
                v-model="filter.memberNumber"
                placeholder="输入会员卡号"
              ></el-input>
            </div> -->
          </div>
          <el-button type="primary" @click="getDataList"> 搜索 </el-button>
        </div>
      </m-card>
    </template>

    <template #section>
      <div class="flex justify-between">
        <el-button @click="onBatchSetLabel"> 设置标签 </el-button>
        <el-select v-model="filter.sortType" class="w-[200px]" @change="handleChange">
          <el-option v-for="item in sorts" :key="`${item.value}sorts`" :label="item.label" :value="item.value" />
        </el-select>
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
      >
        <m-table-column v-slot="{ row }" label="客户信息" width="240" height="80">
          <div class="info">
            <el-popover placement="bottom-start" width="200" trigger="hover" :disabled="!row?.memberInnerListVos?.[0]?.registerData">
              <div v-for="item in JSON.parse(row?.memberInnerListVos?.[0]?.registerData ?? '[]')" :key="item.id">
                {{ item.formName }}:{{ item.value }}
              </div>
              <template #reference>
                <img class="info__img" :src="row.avatarUrl" />
              </template>
            </el-popover>

            <div class="info__msg">
              <div class="info__msg--text">
                {{ row.nikeName }}
                <span v-if="row.phone">({{ row.phone }})</span>
              </div>
              <div class="info__msg--tags" @click="onSetLabel(row)">
                <span v-if="row.userTagVos?.length > 0">
                  <el-button link>
                    {{
                      row.userTagVos
                        .map((tag: UserTag) => tag.tagName)
                        .slice(0, 2)
                        .join(' ; ')
                    }}
                  </el-button>
                  <span>等共{{ row.userTagVos.length }}个标签</span>
                </span>
                <el-button v-if="row.userTagVos === null || row.userTagVos?.length === 0" link> 请选择所属标签 </el-button>
                <el-icon><CaretBottom /></el-icon>
              </div>
            </div>
          </div>
        </m-table-column>
        <m-table-column v-slot="{ row }" label="消费次数">
          <span>{{ row.consumeNum }}</span>
        </m-table-column>
        <m-table-column prop="consumeTotleMoney" label="交易总额" />
        <m-table-column prop="integral" label="可用积分" />
        <m-table-column prop="supplyBonus" label="可用余额" />
        <m-table-column prop="orderLastDealTime" label="上次交易时间" />
        <m-table-column v-slot="{ row }" label="操作" width="180">
          <SetDrop set-name="会员续费" :dropdown-list="dropdownList()" @set-click="itemClick(row)" @command="itemCommand($event, row)" />
        </m-table-column>
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

    <SetLabel v-if="labelVisible" ref="labelDialogRef" :all-tags="allTagList" @refresh-data-list="getDataList" />
    <RenewMember ref="renewMemberRef" @refresh-data-list="getDataList" />
    <UpgradeMember ref="upgradeMemberRef" @refresh-data-list="getDataList" />
    <MemberRecord ref="memberRecordRef" />
  </MContent>
</template>

<style lang="scss" scoped>
@import '../../../../../assets/styles/cutomer/customer';
.lfilter {
  background: rgb(248, 248, 248);
  button {
    margin-left: 100px;
  }
  > div {
    overflow: hidden;
    margin-bottom: 16px;
  }

  &__name {
    // .el-input {
    //   width: 194px;
    // }
  }

  &__item {
    float: left;
    width: 50%;
    .el-input,
    .el-select {
      width: 350px;
    }
    > span {
      display: inline-block;
      width: 100px;
    }
  }
}

.form {
  &.show {
    height: 248px;
  }
}

.mList {
  .el-pagination {
    text-align: right;
  }
}
</style>
