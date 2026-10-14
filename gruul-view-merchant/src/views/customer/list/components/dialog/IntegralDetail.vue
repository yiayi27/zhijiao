<script setup lang="ts">
import { getIntegralDetail } from '@/api/integral/integral'
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
  endTime: '',
  keyword: '',
  startTime: '',
  userId: '',
})

// 积分消费说明
const integralTypeList = [
  '兑换积分商品',
  '购买商品',
  '积分抵扣',
  '每日首次登录',
  '每日首次分享',
  '签到',
  '每日步数达标',
  '商家充值',
  '商家扣除',
  '积分清零',
  '归还积分',
  '兑换会员卡',
]

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
      queryForm.userId = String(props.checkedItem[0].userId)
      queryForm.startTime = dayjs().subtract(30, 'day').startOf('day').format('YYYY-MM-DD HH:mm:ss')
      queryForm.endTime = dayjs().endOf('day').format('YYYY-MM-DD HH:mm:ss')
      getDataList()
    }
  },
)

async function getDataList() {
  const form = {
    size: pageSize.value,
    current: pageNum.value,
  }
  Object.assign(form, queryForm)
  const data = await getIntegralDetail(form)
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
  return await ElMessageBox.confirm('确定退出积分明细页面？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
}
</script>

<template>
  <el-dialog
    v-model="dialogVisible"
    :before-close="beforeCloseHandle"
    class="dialog"
    :title="`${checkedItem[0] ? checkedItem[0].nikeName : ''}的积分明细`"
  >
    <el-row class="queryTime">
      <el-date-picker v-model="queryForm.startTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="开始日期" />
      <span style="padding: 0 10px">至</span>
      <el-date-picker v-model="queryForm.endTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="结束日期" @keyup="getDataList" />
      <el-button style="margin-left: 10px" @click="getDataList"> 查询 </el-button>
    </el-row>
    <BaseTable :data="dataList">
      <!-- <el-table-column prop="integralDetailId" label="交易说明">
      </el-table-column> -->
      <el-table-column prop="createTime" label="交易时间" />
      <el-table-column prop="integral" label="交易积分">
        <template #default="scope">
          <span v-if="scope.row.integral > 0" style="color: red">+{{ scope.row.integral }}</span>
          <span v-if="scope.row.integral <= 0" style="color: red">{{ scope.row.integral }}</span>
          <span v-if="scope.row.detailName">{{ scope.row.detailName }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="gainIntegralType" label="交易说明">
        <template #default="scope">
          <span>{{ integralTypeList[scope.row.gainIntegralType - 1] }}</span>
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
