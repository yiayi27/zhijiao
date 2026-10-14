<script lang="ts" setup>
import { delCoupons, offCoupons } from '@/api/couponActivity/coupon'
import SetDrop from '@/components/SetDrop/SetDrop.vue'
import { ElMessageBox } from 'element-plus'

import { useRouter } from 'vue-router'
import CouponType from '../../common/CouponType.vue'
import type { CouponItem } from '../CouponList.vue'

// 定义下拉菜单项类型
interface DropdownItem {
  text: string
  show: boolean
  command: string
}

const props = defineProps<{
  couponTable: CouponItem[]
}>()

const emit = defineEmits(['refresh'])

const router = useRouter()
const couponList = ref<any>(null)
const tableList = ref<CouponItem[]>([])

/** 编辑下拉 */
const dropdownCoupList = ref<DropdownItem[]>([
  {
    text: '删除',
    show: true,
    command: '1',
  },
])

function getCoupon(): void {
  tableList.value = JSON.parse(JSON.stringify(props.couponTable))
  tableList.value.forEach((item: CouponItem) => {
    item.effectiveStartTime = item?.effectiveStartTime?.replace(/-/g, '.') || ''
    item.effectiveEndTime = item?.effectiveEndTime?.replace(/-/g, '.') || ''
  })
}

watch(
  () => props.couponTable,
  () => {
    getCoupon()
  },
  { deep: true },
)

/**
 * 上下架商品
 */
function changeStatus(item: CouponItem): void {
  if (item.startTime) {
    const now = Date.now()
    const end = Date.parse(item.endTime || '')
    if (end < now && item.status === 2) {
      ElMessage.error('优惠券结束需大于当前时间，请重新编辑优惠券')
      return
    }
  }
  const id: string[] = []
  id.push(item.id)
  offCoupons(item.status === 2 ? 1 : 2, id).then((res) => {
    if (res.code === 200) {
      ElMessage.success(`优惠券已${item.status === 2 ? '上架' : '下架'}`)
      emit('refresh')
    }
  })
}

/** 编辑 */
function editGoods(item: CouponItem): void {
  router.push({ name: 'IssueCouponAdd', query: { id: item.id } })
}

/** 编辑下拉 */
function handleCommand(e: any, item: CouponItem): void {
  ElMessageBox.confirm('确认删除选中优惠券吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    const ids: string[] = []
    ids.push(item.id)
    delCoupons(ids.join(','), {}).then((res) => {
      if (res.code === 200) {
        ElMessage.success('删除成功')
        emit('refresh')
      }
    })
  })
}

// 向外暴露方法供父组件调用
defineExpose({
  getCoupon,
  couponList,
})
</script>

<template>
  <div>
    <m-table ref="couponList" :data="tableList" :selection="true">
      <template #header="{ row }">
        <div style="width: 100%">
          <span style="margin-right: 50px">商品ID：{{ row.name }}</span>
          <span style="margin-right: 50px">创建时间：{{ row.createTime }}</span>
        </div>
      </template>
      <m-table-column label="商品" width="300">
        <template #default="{ row }">
          <div style="display: flex">
            <CouponType :coupon="row" />
            <div class="couponTent">
              <div style="margin-bottom: 10px">
                {{ row.productType === 0 ? '全店商品' : '指定商品' }}
              </div>
              <div v-if="row.couponType === 1">{{ row.discount }}折,无门槛使用</div>
              <div v-if="row.couponType === 2">{{ row.discountAmount }}元,无门槛使用</div>
              <div v-if="row.couponType === 3">{{ row.discount }}折,满{{ row.conditionAmount }}元使用</div>
              <div v-if="row.couponType === 4">{{ row.discountAmount }}元,满{{ row.conditionAmount }}元使用</div>
            </div>
          </div>
        </template>
      </m-table-column>
      <m-table-column label="有效期" width="180">
        <template #default="{ row }">
          <div class="middleCenter">
            <div class="box--coupon__date">
              <div v-if="row.effectiveDays > 0">领取后{{ row.effectiveDays }}天内有效</div>
              <div v-else>
                <span class="box--coupon__start">{{ row.effectiveStartTime }}</span>
                <span>-</span>
                <span class="box--coupon__end">{{ row.effectiveEndTime }}</span>
              </div>
            </div>
          </div>
        </template>
      </m-table-column>
      <m-table-column label="积分价">
        <template #default="{ row }">
          <div class="middleCenter">
            {{ row.integral }}
          </div>
        </template>
      </m-table-column>
      <m-table-column label="发行量(张)">
        <template #default="{ row }">
          <div class="middleCenter">
            {{ row.issueQuantity }}
          </div>
        </template>
      </m-table-column>
      <m-table-column label="状态">
        <template #default="{ row }">
          <div class="middleCenter">
            <div class="upDown">
              <span
                class="upDown--goodUp"
                :style="{
                  'background-color': row.status === 2 ? '#626C72' : '#5CB95C',
                }"
                >{{ row.status === 2 ? '已下架' : '已上架' }}</span
              >|
              <span class="upDown--goodDown" @click="changeStatus(row)">{{ row.status === 2 ? '上架' : '下架' }}</span>
            </div>
          </div>
        </template>
      </m-table-column>
      <m-table-column label="操作">
        <template #default="{ row }">
          <div class="middleCenter">
            <SetDrop set-name="编辑" :dropdown-list="dropdownCoupList" @set-click="editGoods(row)" @command="handleCommand($event, row)" />
          </div>
        </template>
      </m-table-column>
    </m-table>
  </div>
</template>

<style lang="scss" scoped>
.setClassify {
  border: none;
  display: flex;
  justify-content: center;
  align-items: center;
  &__del {
    width: 55px;
    height: 30px;
    display: flex;
    justify-content: center;
    align-items: center;
    cursor: pointer;
  }
  &__drop {
    width: 40px;
    height: 30px;
    border: 1px solid #e0e0e0;
    display: flex;
    justify-content: center;
    align-items: center;
    cursor: pointer;
  }
}

.couponTent {
  font-weight: bold;
  margin-left: 15px;
  font-size: 10px;
  margin-top: 10px;
}

.middleCenter {
  display: flex;
  justify-content: center;
  align-items: center;
}

.upDown {
  display: flex;
  align-items: center;
  justify-content: center;

  &--goodUp {
    display: flex;
    width: 40px;
    height: 20px;
    justify-content: center;
    align-items: center;
    border-radius: 4px;
    color: white;
    margin-right: 10px;
  }

  &--goodDown {
    margin-left: 10px;
    color: #2d8cf0;
    cursor: pointer;
  }
}
</style>
