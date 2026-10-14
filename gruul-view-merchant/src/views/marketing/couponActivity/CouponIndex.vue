<script setup lang="ts">
import { delCoupons as apiDelCoupons, couponDetail, couponList, offCoupons } from '@/api/couponActivity/coupon'
import MContainer from '@/components/MContainer/MContainer.vue'
import PageManage from '@/components/PageManage.vue'
import { ElMessageBox } from 'element-plus'

import { useRouter } from 'vue-router'
import CouponListTable from './CouponList.vue'

defineOptions({
  name: 'CouponIndex',
})

// 路由
const router = useRouter()

// 组件引用
const couponListRef = ref()

/** 搜索类型 */
const activeStateList = [
  {
    id: '3',
    label: '全部状态',
  },
  {
    id: '0',
    label: '未开始',
  },
  {
    id: '1',
    label: '进行中',
  },
  {
    id: '2',
    label: '已结束',
  },
]

/** 当前选中类型 */
const getStateValue = ref('')

/** 搜索活动状态 */
const activeTypeList = [
  {
    id: '5',
    label: '全部类型',
  },
  {
    id: '0',
    label: '无门槛折扣券',
  },
  {
    id: '1',
    label: '无门槛现金券',
  },
  {
    id: '2',
    label: '满折券',
  },
  {
    id: '3',
    label: '满减券',
  },
]

/** 当前选中状态 */
const getTypeValue = ref('')

/** 固定时间 */
const useDateValue = ref('')

const pageSize = ref(10)
const pageNum = ref(1)
const total = ref(0)

/** 优惠券数组 */
const couponData = ref([])

// /** 优惠券类型 */
// const couponTypeList = ['无门槛折扣券', '无门槛现金券', '折扣券', '满减券']
//
// /** 优惠券状态 */
// const couponStatusList = ['未开始', '进行中', '已结束']
//
// /** 状态颜色显示 */
// const couponStatusColorList = ['#333333', '#333333', '#d5d5d5']

/** 选择优惠券 */
const selectList = ref<number[]>([])

/** 搜索条件 */
const search = reactive({
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
const chooseItem = ref({})

onMounted(() => {
  getCouponList()
})

function getCouponList() {
  couponList(search).then((res) => {
    couponData.value = res.data.list
    subTotal(res.data.total)
  })
}

/**
 * 选择优惠券
 */
function handleSelectionChange(val: any[]) {
  selectList.value = []
  val.forEach((item) => {
    selectList.value.push(item.id)
  })
  getSelect(selectList.value)
}

/**
 * 删除优惠券
 */
function delCoupon(item: any) {
  ElMessageBox.confirm('确认删除选中优惠券吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    const ids: string[] = item.id ? [item.id] : item
    apiDelCoupons(ids.join(','), {}).then((res) => {
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
function showCoupon(item: any) {
  endVisible.value = true
  couponDetail(item.id, {}).then((res) => {
    const discountProductVos = res.data.discountProductVos
    chooseItem.value = { ...res.data.couponVo, discountProductVos }
  })
}

/**
 * 关闭优惠券
 */
function offCoupon(item: any) {
  ElMessageBox.confirm('确认关闭选中优惠券吗？关闭后无法恢复', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    let ids: any[] = []
    if (item.id) {
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
function editCoupon(item: any) {
  router.push({ name: 'AddCouponActivity', params: { id: item.id } })
}

/**
 * 根据时间搜索
 */
function searchByInput(e: any) {
  pageNum.value = couponListRef.value.search.current = 1
  couponListRef.value.search.startTime = e?.[0]
  couponListRef.value.search.endTime = e?.[1]
  couponListRef.value.getCouponList()
}

/**
 * 选择优惠券状态
 */
function selectStatus(e: string) {
  pageNum.value = couponListRef.value.search.current = 1
  couponListRef.value.search.status = e !== '3' ? e : ''
  couponListRef.value.getCouponList()
}

/**
 * 选择优惠券类型
 */
function selecType(e: string) {
  pageNum.value = couponListRef.value.search.current = 1
  couponListRef.value.search.couponType = e !== '5' ? e : ''
  couponListRef.value.getCouponList()
}

/** 子组件选择优惠券 */
function getSelect(itemList: number[]) {
  selectList.value = itemList
}

/**
 * 批量删除优惠券
 */
function delCoupons() {
  couponListRef.value.delCoupon(selectList.value)
}

/**
 * 获取总数
 */
function subTotal(value: number) {
  total.value = value
}

/**
 * @method handleSizeChange
 * @description 每页 条
 */
function handleSizeChange(val: number) {
  pageNum.value = couponListRef.value.search.current = 1
  pageSize.value = couponListRef.value.search.size = val
  couponListRef.value.getCouponList()
}

/**
 * @method handleCurrentChange
 * @description 当前页
 */
function handleCurrentChange(val: number) {
  pageNum.value = couponListRef.value.search.current = val
  couponListRef.value.getCouponList()
}

// 将方法和属性暴露给模板
defineExpose({
  showCoupon,
  editCoupon,
  offCoupon,
  delCoupon,
  handleSelectionChange,
})
</script>

<template>
  <MContainer>
    <template #section>
      <div class="activeTop">
        <el-button type="primary" @click="router.push({ name: 'AddCouponActivity' })"> 新增优惠券 </el-button>
        <el-button :disabled="selectList.length === 0" @click="delCoupons"> 批量删除 </el-button>
        <el-select v-model="getStateValue" placeholder="全部状态" class="activeTop__sel" @change="selectStatus">
          <el-option v-for="item in activeStateList" :key="item.id" :label="item.label" :value="item.id" />
        </el-select>
        <el-select v-model="getTypeValue" placeholder="全部类型" class="activeTop__sel" @change="selecType">
          <el-option v-for="item in activeTypeList" :key="item.id" :label="item.label" :value="item.id" />
        </el-select>
        <el-date-picker
          v-model="useDateValue"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          style="margin-left: 150px; width: 390px"
          value-format="YYYY-MM-DD"
          @change="searchByInput"
        />
      </div>
    </template>

    <template #table>
      <CouponListTable ref="couponListRef" @get-select="getSelect" @sub-total="subTotal" />
    </template>

    <template #pagination>
      <PageManage
        :page-size="pageSize"
        :page-num="pageNum"
        :total="total"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
      />
    </template>
  </MContainer>
</template>

<style lang="scss" scoped>
.activeTop {
  display: flex;

  &__sel {
    margin-left: 10px;
    width: 120px;
  }
}

.serachBtn {
  width: 32px;
  height: 32px;
  display: flex;
  justify-content: center;
  align-items: center;
  border: 1px solid #dcdfe6;
  background-color: white;
  cursor: pointer;
  border-left: none;
  border-radius: 4px;
}
</style>
