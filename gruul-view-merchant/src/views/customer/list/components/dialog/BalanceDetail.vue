<script setup lang="ts">
import { getBalanceDetail } from '@/api/customer/member'
import BaseTable from '@/components/BaseTable/index.vue'
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

async function getDataList() {
  const form = {
    page: pageNum.value,
    size: pageSize.value,
    yearMonth: queryForm.yearMonth,
    userShopId: props.checkedItem[0].userId,
  }
  const data = await getBalanceDetail(form)
  dataList.value = data.data.list
  pageSize.value = data.data.size
  pageNum.value = data.data.current
  total.value = data.data.total
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
  return await ElMessageBox.confirm('确定退出余额明细页面？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
}
</script>

<template>
  <!--  余额明细 -->
  <el-dialog
    v-model="dialogVisible"
    :before-close="beforeCloseHandle"
    class="dialog"
    :title="`${checkedItem[0] ? checkedItem[0].nikeName : ''}的余额明细`"
  >
    <el-row class="queryTime">
      <el-date-picker v-model="queryForm.yearMonth" type="month" value-format="YYYYMM" placeholder="选择月" @change="pickTime" />
    </el-row>
    <BaseTable :data="dataList">
      <el-table-column prop="detailName" label="交易说明" />
      <el-table-column prop="createTime" label="交易时间" />
      <el-table-column prop="useBalance" label="交易金额（元）">
        <template #default="scope">
          <span v-if="scope.row.changeType === 0" style="color: red">+{{ scope.row.useBalance }}</span>
          <span v-else style="color: red">-{{ scope.row.useBalance }}</span>
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
