<script lang="ts" setup>
import { couponIntList, delCoupons, offCoupons } from '@/api/couponActivity/coupon'
import MContent from '@/components/MContainer/MContent.vue'
import PageManage from '@/components/PageManage.vue'
import SetDrop from '@/components/SetDrop/SetDrop.vue'
import { Search } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { ElMessageBox } from 'element-plus'

import { useRouter } from 'vue-router'
import SelectCom from './components/SelectCom.vue'
import CouponTable from './couponList/CouponTable.vue'

// 定义优惠券项类型
export interface CouponItem {
  id: string
  name?: string
  status: number
  effectiveStartTime?: string
  effectiveEndTime?: string
  startTime?: string
  endTime?: string
  createTime?: string
  effectiveDays?: number
  couponType?: number
  [key: string]: any
}

// 定义搜索参数类型
interface SearchParams {
  current: number
  size: number
  name: string
  couponType: string
  status: string
  giveType: number
}

// 定义下拉菜单项类型
interface DropdownItem {
  text: string
  command: string
  show: boolean
}

const router = useRouter()
const couTable = ref<any>()
const name = ref('CouponList')
const total = ref(0)

/** 选择优惠券数组 */
const goodIds = ref<string[]>([])

/** 优惠券数组 */
const couponTable = ref<CouponItem[]>([])

/** 搜索条件 */
const search = ref<SearchParams>({
  current: 1,
  size: 10,
  name: '', // 优惠券名称
  couponType: '', // 优惠券类型
  status: '', // 优惠券状态
  giveType: 2,
})

const dropdownCoupList = ref<DropdownItem[]>([
  {
    text: '批量上架',
    command: '1',
    show: true,
  },
  {
    text: '批量下架',
    command: '2',
    show: true,
  },
])

/**
 * 获取积分优惠券列表
 */
function getList(): void {
  couponIntList(search.value).then((res) => {
    res.data.list.forEach((item: CouponItem) => {
      item.couponType! += 1
      if (item.startTime) {
        item.effectiveStartTime = item.startTime
        item.effectiveEndTime = item.endTime
      } else {
        item.effectiveStartTime = item.createTime
        item.effectiveEndTime = dayjs(item.createTime).add(item.effectiveDays!, 'day').format('YYYY-MM-DD')
      }
    })
    couponTable.value = res.data.list
    total.value = res.data.total
  })
}

/**
 * 顶部状态筛选
 */
function changeSel(e: string): void {
  search.value.status = e !== '0' ? e : ''
  getList()
}

/**
 * 搜索功能
 */
function searchBy(): void {
  getList()
}

/**
 * 发布积分优惠券
 */
function issueCoupon(): void {
  router.push({ name: 'IssueCouponAdd' })
}

/**
 * 下拉框选择
 */
function command(e: string): void {
  goodIds.value = []
  const selectedItems = couTable.value?.couponList?.getCheckedItem()
  selectedItems.forEach((item: CouponItem) => {
    goodIds.value.push(item.id)
  })
  if (goodIds.value.length === 0) {
    ElMessage.error('请先选择优惠券')
    return
  }
  offCoupons(e === '1' ? 1 : 2, goodIds.value).then((res) => {
    if (res.code === 200) {
      ElMessage.success(`优惠券已${e === '1' ? '上架' : '下架'}`)
      getList()
    }
  })
}

/**
 * 批量删除
 */
function delGoods(): void {
  const selectedItems = couTable.value?.couponList?.getCheckedItem()
  goodIds.value = selectedItems.map((item: CouponItem) => {
    return item.id
  })
  if (goodIds.value.length === 0) {
    ElMessage.error('请选择要删除优惠券')
    return
  }

  ElMessageBox.confirm('确认删除选中优惠券吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    delCoupons(goodIds.value.join(','), {}).then((res) => {
      if (res.code === 200) {
        ElMessage.success('删除成功')
        getList()
      }
    })
  })
}

/**
 * @method handleSizeChange
 * @description 每页 条
 */
function handleSizeChange(val: number): void {
  search.value.size = val
  getList()
}

/**
 * @method handleCurrentChange
 * @description 当前页
 */
function handleCurrentChange(val: number): void {
  search.value.current = val
  getList()
}

onMounted(() => {
  getList()
})
</script>

<template>
  <MContent>
    <template #section>
      <div class="couponTop">
        <div style="display: flex">
          <el-button type="primary" @click="issueCoupon"> 发布积分优惠券 </el-button>
          <SetDrop set-name="批量删除" :dropdown-list="dropdownCoupList" style="margin-left: 20px" @set-click="delGoods" @command="command" />
        </div>
        <SelectCom :name="name" style="margin-left: 380px" @change-sel="changeSel" />
        <div style="display: flex">
          <el-input
            v-model="search.name"
            class="filter__right--input"
            clearable
            style="width: 200px"
            placeholder="输入优惠券ID"
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
      <CouponTable ref="couTable" :coupon-table="couponTable" @refresh="getList" />
    </template>

    <template #pagination>
      <PageManage
        :page-size="search.size"
        :page-num="search.current"
        :total="total"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
      >
        <template #pagination--left>
          <SetDrop set-name="批量删除" :dropdown-list="dropdownCoupList" @set-click="delGoods" @command="command" />
        </template>
      </PageManage>
    </template>
  </MContent>
</template>

<style lang="scss" scoped>
.couponTop {
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
