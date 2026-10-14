<script setup lang="ts">
import { couponDetail, couponList, delCoupons, offCoupons } from '@/api/couponActivity/coupon'
import BaseTable from '@/components/BaseTable/index.vue'
import { ElMessageBox } from 'element-plus'

import { useRouter } from 'vue-router'
import EndContent from '../common/EndContent.vue'

// 定义优惠券接口
interface CouponItem {
  id: string
  name: string
  couponType: number
  discount?: number
  discountAmount?: number
  conditionAmount?: number
  hasGet: number
  hasUse: number
  issueQuantity: number
  status: number
  [key: string]: any
}

// 定义事件
const emit = defineEmits(['getSelect', 'subTotal'])

// 路由
const router = useRouter()

/** 优惠券数组 */
const couponData = ref<CouponItem[]>([])

/** 优惠券类型 */
const couponTypeList = ['无门槛折扣券', '无门槛现金券', '折扣券', '满减券']

/** 优惠券状态 */
const couponStatusList = ['未开始', '进行中', '已结束']

/** 状态颜色显示 */
const couponStatusColorList = ['#333333', '#333333', '#d5d5d5']

/** 选择优惠券 */
const selectList = ref<string[]>([])

/** 搜索条件 */
const search = ref({
  current: 1,
  size: 10,
  couponType: '', // 优惠券类型
  startTime: '', // 开始时间
  endTime: '', // 结束时间
  status: '', // 优惠券状态
  giveType: 0,
})

/** 查看结束活动弹窗 */
const endVisible = ref(false)

/** 选中优惠券 */
const chooseItem = ref<any>({})

onMounted(() => {
  getCouponList()
})

function getCouponList() {
  couponList(search.value).then((res) => {
    couponData.value = res.data.list
    emit('subTotal', res.data.total)
  })
}

/**
 * 选择优惠券
 */
function handleSelectionChange(val: CouponItem[]) {
  selectList.value = []
  val.forEach((item) => {
    selectList.value.push(item.id)
  })
  emit('getSelect', selectList.value)
}

/**
 * 删除优惠券
 */
function delCoupon(item: CouponItem | string[]) {
  ElMessageBox.confirm('确认删除选中优惠券吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    let ids: string[] = []
    if ('id' in item) {
      ids.push(item.id)
    } else {
      ids = item
    }
    delCoupons(ids.join(','), {}).then((res) => {
      if (res.code === 200) {
        ElMessage.success('删除成功')
        getCouponList()
      }
    })
  })
}

/**
 * 查看关闭优惠券
 */
function showCoupon(item: CouponItem) {
  endVisible.value = true
  couponDetail(item.id, {}).then((res) => {
    const discountProductVos = res.data.discountProductVos
    chooseItem.value = { ...res.data.couponVo, discountProductVos }
  })
}

/**
 * 关闭优惠券
 */
function offCoupon(item: CouponItem | string[]) {
  ElMessageBox.confirm('确认关闭选中优惠券吗？关闭后无法恢复', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    let ids: string[] = []
    if ('id' in item) {
      ids.push(item.id)
    } else {
      ids = item
    }
    offCoupons(2, ids).then((res) => {
      if (res.code === 200) {
        ElMessage.success('关闭成功')
        getCouponList()
      }
    })
  })
}

/**
 * 编辑优惠券
 */
function editCoupon(item: CouponItem) {
  router.push({ name: 'AddCouponActivity', query: { id: item.id } })
}

// 暴露方法给父组件
defineExpose({
  search,
  getCouponList,
  delCoupon,
})
</script>

<template>
  <BaseTable :data="couponData" tooltip-effect="dark" :row-style="{ height: '50px' }" @selection-change="handleSelectionChange">
    <template #empty>
      <div class="emptyLine">暂无数据~</div>
    </template>
    <el-table-column type="selection" width="55" />
    <el-table-column label="名称" width="150">
      <template #default="scope">
        {{ scope.row.name }}
      </template>
    </el-table-column>
    <el-table-column label="类型" width="150">
      <template #default="scope">
        {{ couponTypeList[scope.row.couponType] }}
      </template>
    </el-table-column>
    <el-table-column label="规则" width="200">
      <template #default="scope">
        <div v-if="scope.row.couponType === 0">{{ scope.row.discount }}折,无门槛使用</div>
        <div v-if="scope.row.couponType === 1">{{ scope.row.discountAmount }}元,无门槛使用</div>
        <div v-if="scope.row.couponType === 2">{{ scope.row.discount }}折,满{{ scope.row.conditionAmount }}元使用</div>
        <div v-if="scope.row.couponType === 3">{{ scope.row.discountAmount }}元,满{{ scope.row.conditionAmount }}元使用</div>
      </template>
    </el-table-column>
    <el-table-column label="已领取/剩余" width="140">
      <template #default="scope">
        {{ scope.row.hasGet }} /
        {{ scope.row.issueQuantity - scope.row.hasGet }}
      </template>
    </el-table-column>
    <el-table-column label="已使用">
      <template #default="scope">
        {{ scope.row.hasUse }}
      </template>
    </el-table-column>
    <el-table-column label="状态" show-overflow-tooltip>
      <template #default="scope">
        <span :style="{ color: couponStatusColorList[scope.row.status] }">{{ couponStatusList[scope.row.status] }}</span>
      </template>
    </el-table-column>
    <el-table-column label="操作">
      <template #default="scope">
        <div class="dealWay">
          <span v-if="scope.row.status !== 2" style="color: #2d8cf0" @click="editCoupon(scope.row)">编辑</span>
          <span v-else style="color: #2d8cf0" @click="showCoupon(scope.row)">查看</span>
          <span v-if="scope.row.status === 1" style="color: #fa4e74" @click="offCoupon(scope.row)">关闭</span>
          <span v-else style="color: #fa4e74" @click="delCoupon(scope.row)">删除</span>
        </div>
      </template>
    </el-table-column>
  </BaseTable>
  <el-dialog v-model="endVisible" width="40%">
    <EndContent :choose-item="chooseItem" />
  </el-dialog>
</template>

<style lang="scss" scoped>
.dealWay {
  display: flex;
  cursor: pointer;
  width: 60px;
  justify-content: space-between;
}

.emptyLine {
  width: 100%;
  height: 80px;
  background-color: white;
  display: flex;
  -webkit-box-align: center;
  -ms-flex-align: center;
  align-items: center;
  -webkit-box-pack: center;
  justify-content: center;
  margin-left: -15px;
  font-size: 14px;
  color: #b3b3b3;
}
</style>
