<script setup lang="ts">
import { batchSetBlackList, getBlackList } from '@/api/customer/customer'
import MContainer from '@/components/MContainer/MContainer.vue'
import PageManage from '@/components/PageManage.vue'
import SetDrop from '@/components/SetDrop/SetDrop.vue'
import SetBlackList from '@/views/customer/common/SetBlackList.vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'

defineOptions({
  name: 'Blacklist',
})

// 定义类型
interface UserRecord {
  userId: string | number
  avatarUrl: string
  nikeName: string
  phone: string
  consumeTotleMoney?: number
  consumeNum?: number
  supplyBonus?: number
  integral?: number
  blacklistType: number[]
  [key: string]: any
}

interface DropdownItem {
  command: string
  disabled: boolean
  show: boolean
  text: string
}

interface PermissionOption {
  value: string | number
  label: string
}

interface DataForm {
  permission: Long
  fuzzy: string
}

interface BlacklistParams {
  option: number
  rejectInteger: any[]
  userIds: (string | number)[]
}

// Refs
const setBlackRef = ref()
const blackList = ref()

// Reactive data
const dataList = ref<UserRecord[]>([])
const dataForm = reactive<DataForm>({
  permission: '',
  fuzzy: '',
})
const multipleSelection = ref<UserRecord[]>([])
const blackVisible = ref(false)

// Constants
const dropdownList: DropdownItem[] = [
  {
    command: 'delete',
    disabled: false,
    show: true,
    text: '移除',
  },
]

const permissionOptions: PermissionOption[] = [
  {
    value: '',
    label: '全部权限',
  },
  {
    value: 1,
    label: '下单',
  },
  {
    value: 2,
    label: '评论',
  },
]

// Pagination
const pageSize = ref(10)
const pageNum = ref(1)
const total = ref(0)

// Methods
/**
 * 获取黑名单列表
 * @param pageNumVal 页码
 */
async function getDataList(pageNumVal: number): Promise<void> {
  pageNum.value = pageNumVal
  const param = {
    page: pageNum.value,
    size: pageSize.value,
    permission: dataForm.permission,
    fuzzy: dataForm.fuzzy,
  }
  const res = await getBlackList(param)
  dataList.value = res.data.list
  pageSize.value = res.data.size
  pageNum.value = res.data.current
  total.value = res.data.total
}

/**
 * 移除黑名单通用方法
 * @param userIds 要移除的用户ID数组
 */
function removeFromBlacklist(userIds: (string | number)[]): void {
  if (userIds.length === 0) {
    return
  }

  const params: BlacklistParams = {
    option: 2,
    rejectInteger: [],
    userIds,
  }

  ElMessageBox.confirm('黑名单移除后将同时移除所有限制行为，确定移除吗？', '黑名单移除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  }).then(() => {
    batchSetBlackList(params).then(() => {
      ElMessage({
        message: '已成功移除黑名单',
        type: 'success',
      })
      getDataList(1)
    })
  })
}

/**
 * 批量删除黑名单
 */
function batchDelete(): void {
  const userIds = multipleSelection.value.map((item) => item.userId)
  removeFromBlacklist(userIds)
}

/**
 * 打开设置黑名单对话框
 * @param row 用户记录
 */
function onSetBlackList(row: UserRecord): void {
  blackVisible.value = true
  nextTick(() => {
    setBlackRef.value.init([row], 3)
  })
}

/**
 * 处理下拉菜单命令
 * @param command 命令
 * @param row 用户记录
 */
function handleCommand(command: string, row: UserRecord): void {
  if (command === 'delete') {
    removeFromBlacklist([row.userId])
  }
}

/**
 * 获取黑名单类型描述
 * @param list 类型列表
 * @returns 类型描述字符串
 */
function getBlackType(list: number[]): string {
  return list.map((i) => (i === 1 ? '下单' : '评论')).join(',')
}

/**
 * 每页条数变化处理
 * @param val 每页条数
 */
function handleSizeChange(val: number): void {
  pageSize.value = val
  getDataList(1)
}

/**
 * 当前页变化处理
 * @param val 当前页
 */
function handleCurrentChange(val: number): void {
  getDataList(val)
}

// Lifecycle hooks
onMounted(() => {
  getDataList(1)
})
</script>

<template>
  <MContainer class="blacklist">
    <template #section>
      <el-row class="filter" type="flex" justify="space-between">
        <el-button :disabled="multipleSelection.length === 0" @click="batchDelete"> 批量移除 </el-button>
        <div class="filter__right">
          <el-select v-model="dataForm.permission" class="filter__right--select" placeholder="全部权限" style="width: 150px" @change="getDataList(1)">
            <el-option v-for="item in permissionOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
          <el-input
            v-model="dataForm.fuzzy"
            class="filter__right--input"
            clearable
            style="width: 200px"
            placeholder="输入关键词"
            @keyup.enter="getDataList(1)"
          >
            <template #append>
              <el-button :icon="Search" @click="getDataList(1)" />
            </template>
          </el-input>
        </div>
      </el-row>
    </template>
    <template #table>
      <m-table ref="blackList" v-model:checked-item="multipleSelection" :data="dataList" :selection="true" need-hover-border class="blackList--table">
        <!-- <template v-slot:header="{ row }">
          <div style="width:100%" v-if="!!row.memberNumber">
            <span style="margin-right:50px"
              >会员卡号：{{ row.memberNumber }}</span
            >
            <span>成为会员时间{{ row.memberRegisterTime }}</span>
            <span>(已成为会员{{ row.becomeMemberDayNumber }}天)</span>
          </div>
          <div style="width:100%" v-else>
            <span>成为用户时间{{ row.firstLoginTime }}</span>
          </div>
        </template> -->
        <m-table-column v-slot="{ row }" label="客户信息" width="240" height="80">
          <div class="info">
            <img class="info__img" style="flex: unset" :src="row.avatarUrl" alt="" />
            <div class="info__msg">
              <div class="info__msg--name">
                {{ row.nikeName }}
              </div>
              <div class="info__msg--phone">
                {{ row.phone }}
              </div>
            </div>
          </div>
        </m-table-column>
        <m-table-column prop="consumeTotleMoney" label="消费金额" />
        <m-table-column label="消费次数" prop="consumeNum" />
        <m-table-column prop="supplyBonus" label="可用余额" />
        <m-table-column prop="integral" label="可用积分" />
        <m-table-column v-slot="{ row }" label="限制权限">
          {{ getBlackType(row.blacklistType) }}
        </m-table-column>
        <m-table-column v-slot="{ row }" label="操作">
          <SetDrop :dropdown-list="dropdownList" @set-click="onSetBlackList(row)" @command="handleCommand($event, row)" />
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
      >
        <template #pagination--left>
          <el-button class="blacklist__footer--batchDel" :disabled="multipleSelection.length === 0" @click="batchDelete"> 批量移除 </el-button>
        </template>
      </PageManage>
    </template>
    <SetBlackList v-if="blackVisible" ref="setBlackRef" @refresh-data-list="getDataList(1)" />
  </MContainer>
</template>

<style lang="scss" scoped>
@import '../../../assets/styles/cutomer/blacklist';
</style>
