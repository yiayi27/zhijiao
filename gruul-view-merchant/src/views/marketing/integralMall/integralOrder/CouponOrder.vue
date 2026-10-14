<script setup lang="ts">
import { integralCouponOrder, remarkIntegralCouponOrder } from '@/api/integral/integral'
import MContent from '@/components/MContainer/MContent.vue'
import PageManage from '@/components/PageManage.vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'

import Remark from '../common/Remark.vue'
import CouponTable from './components/CouponTable.vue'

// 接口定义
interface CouponItem {
  id: string | number
  couponType: number
  [key: string]: any
}

interface RemarkParam {
  backRemark: string
  isCoverage: boolean
  [key: string]: any
}

// 数据
const couponTableRef = ref()
const searchText = ref('')
const pageSize = ref(10)
const pageNum = ref(1)
const total = ref(0)
const someRemark = ref(false)
const selectList = ref<(string | number)[]>([])
const couponOrder = ref<CouponItem[]>([])

// 在组件挂载时执行
onMounted(() => {
  getCouponOrder()
})

/**
 * 获得积分优惠券
 */
async function getCouponOrder() {
  const param = {
    current: pageNum.value,
    size: pageSize.value,
    wxuserName: searchText.value,
    orderType: 1,
  }
  const res = await integralCouponOrder(param)
  res.data.list.forEach((item: CouponItem) => {
    item.couponType += 1
  })
  couponOrder.value = res.data.list
  total.value = res.data.total
}

/**
 * 搜索功能
 */
function searchBy() {
  pageNum.value = 1
  getCouponOrder()
}

/**
 * 批量选择订单
 */
function remarkSome() {
  const list = couponTableRef.value?.couponListRef?.getCheckedItem()
  selectList.value = list.map((item: CouponItem) => {
    return item.id
  })
  if (selectList.value.length === 0) {
    ElMessage.error('请先选择备注订单')
    return
  }
  someRemark.value = true
}

/**
 * 批量备注
 */
function remarkSure(param: RemarkParam) {
  const ids = selectList.value
  remarkIntegralCouponOrder({
    ids,
    backRemark: param.backRemark,
    isCoverage: param.isCoverage,
  }).then(() => {
    ElMessage.success('备注成功')
    someRemark.value = false
    pageNum.value = 1
    getCouponOrder()
  })
}

/**
 * 关闭弹窗
 */
function closeHandle() {
  ElMessageBox.confirm('确定退出批量备注窗口？退出后，选中订单备注信息不会保留？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    someRemark.value = false
  })
}

/**
 * @method handleSizeChange
 * @description 每页 条
 */
function handleSizeChange(val: number) {
  pageSize.value = val
  pageNum.value = 1
  getCouponOrder()
}

/**
 * @method handleCurrentChange
 * @description 当前页
 */
function handleCurrentChange(val: number) {
  pageNum.value = val
  getCouponOrder()
}
</script>

<template>
  <MContent>
    <template #section>
      <div class="orderTop">
        <el-button type="primary" @click="remarkSome"> 批量备注 </el-button>
        <el-dialog v-model="someRemark" width="30%" :before-close="closeHandle">
          <Remark :select-list="selectList" @remark-sure="remarkSure" />
        </el-dialog>
        <div style="display: flex">
          <el-input
            v-model="searchText"
            class="filter__right--input"
            clearable
            style="width: 200px"
            placeholder="输入微信昵称搜索"
            @keyup.enter="searchBy"
          >
            <template #append>
              <el-button :icon="Search" @click="searchBy" />
            </template>
          </el-input>
        </div>
      </div>
    </template>
    <template #table>
      <CouponTable ref="couponTableRef" :coupon-order="couponOrder" @get-coupon-order="getCouponOrder" />
    </template>

    <template #pagination>
      <PageManage
        :page-size="pageSize"
        :page-num="pageNum"
        :total="total"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
      >
        <template #pagination--left>
          <el-button type="primary"> 批量备注 </el-button>
        </template>
      </PageManage>
    </template>
  </MContent>
</template>

<style lang="scss" scoped>
.orderTop {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search {
  width: 32px;
  height: 32px;
  display: flex;
  justify-content: center;
  align-items: center;
  border: 1px solid #dcdfe6;
  border-left: none;
  border-radius: 4px;
  cursor: pointer;
}
</style>
