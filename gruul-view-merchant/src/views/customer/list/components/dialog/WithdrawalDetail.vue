<script setup lang="ts">
import { getWithdrawalList } from '@/api/finance/index'
import BaseTable from '@/components/BaseTable/index.vue'
// import { getBalanceDetail } from "@/api/customer/member";
import PageManage from '@/components/PageManage.vue'
import dayjs from 'dayjs'
import { ElMessageBox } from 'element-plus'

// 定义用户对象接口
interface UserItem {
  userId: string | number
  nikeName: string
  [key: string]: any
}

const props = defineProps({
  visible: {
    type: Boolean,
    default: false,
  },
  checkedItem: {
    type: Array as () => UserItem[],
    default: () => {
      return []
    },
  },
})

const emit = defineEmits(['update:visible'])

const dialogVisible = ref(props.visible)

/** 分页条数 */
const pageSize = ref(10)

/** 分页页码 */
const pageNum = ref(1)

/** 数据长度 */
const total = ref(0)

const queryForm = reactive({
  yearMonth: '',
})

const dataList = ref([])

const form = reactive({
  // ASS, AREA_ASS, POINT
  accountType: '',
  auditStatus: '',
  id: '',
  keyword: '',
  name: '',
  applyTime: '',
  startTime: '',
  endTime: '',
  current: 1,
  size: 10,
})

watch(
  () => props.visible,
  (newVal) => {
    dialogVisible.value = newVal
  },
)

watch(
  () => dialogVisible.value,
  (v) => {
    emit('update:visible', v)
    if (v) {
      queryForm.yearMonth = dayjs().format('YYYYMM')
      getDataList()
    }
  },
)

function pickTime() {
  pageNum.value = 1
  getDataList()
}

function getDataList() {
  let year: number = Number(queryForm.yearMonth.substring(0, 4))
  let month: number = Number(queryForm.yearMonth.substring(4, 6))

  const startTime = `${year}-${month}-01 00:00:00`
  if (month + 1 > 12) {
    year = year + 1
    month = 0
  }

  const endTime = `${year}-${month + 1}-01 00:00:00`

  getWithdrawalList({ ...form, userId: String(props.checkedItem[0].userId), startTime, endTime }).then((res) => {
    dataList.value = res.data.list
    pageSize.value = res.data.size
    pageNum.value = res.data.current
    total.value = res.data.total
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

async function beforeCloseHandle(done: () => void) {
  const result = await confirmHandle()
  if (result === 'confirm') {
    done()
  }
}

/**
 * 关闭确认
 */
async function confirmHandle() {
  return await ElMessageBox.confirm('确定退出提现明细页面？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
}
</script>

<template>
  <!--  提现明细 -->
  <el-dialog
    v-model="dialogVisible"
    :before-close="beforeCloseHandle"
    class="dialog"
    :title="`${checkedItem[0] ? checkedItem[0].nikeName : ''}的提现明细`"
  >
    <el-row class="queryTime">
      <el-date-picker v-model="queryForm.yearMonth" type="month" value-format="YYYYMM" placeholder="选择月" @change="pickTime" />
    </el-row>
    <BaseTable :data="dataList">
      <el-table-column prop="createTime" label="提现时间" />
      <el-table-column prop="amount" label="提现金额">
        <template #default="scope">
          <!-- <span style="color: red;" v-if="scope.row.amount === 0.3"
            >+{{ scope.row.amount }}</span
          > -->
          <span>-{{ scope.row.amount }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="auditStatus" label="状态">
        <template #default="scopes">
          <span v-if="scopes.row.auditStatus === 'PENDING_PARTNER'">待审核</span>
          <span v-if="scopes.row.auditStatus === 'PASSED'" style="color: green">已到账</span>
          <span v-if="scopes.row.auditStatus === 'REJECTED'" style="color: red">已拒绝</span>
        </template>
      </el-table-column>
    </BaseTable>
    <PageManage
      :page-size="pageSize"
      :page-num="pageNum"
      :total="total"
      @handle-size-change="handleSizeChange"
      @handle-current-change="handleCurrentChange"
    />
  </el-dialog>
</template>

<style scoped lang="scss">
.queryTime {
  float: right;
  margin-bottom: 25px;
}
:deep(.el-dialog__header) {
  border-bottom: 1px solid rgba(128, 128, 128, 0.23);
}
</style>
