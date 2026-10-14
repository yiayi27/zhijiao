<script setup lang="ts">
import { remarkIntegralCouponOrder } from '@/api/integral/integral'
import { ElMessageBox } from 'element-plus'

import CouponType from '../../common/CouponType.vue'

const props = defineProps({
  couponOrder: {
    type: Array,
    default: () => [],
  },
})

const emit = defineEmits(['getCouponOrder'])

// 数据
const couponListRef = ref()
const orderList = ref([])
const remarkVisible = ref(false)
const remarkContent = reactive<{
  backRemark: string
  id?: number
}>({
  backRemark: '',
})

// 监听props
watch(
  () => props.couponOrder,
  () => {
    orderList.value = JSON.parse(JSON.stringify(props.couponOrder))
  },
  { deep: true },
)

/**
 * 单个订单信息设置备注
 */
function remarkOrders(row: Obj) {
  remarkVisible.value = true
  Object.assign(remarkContent, row)
}

/**
 * 关闭弹窗
 */
function closeHandle() {
  ElMessageBox.confirm('确定退出备注窗口？退出后，备注信息不会保留？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    remarkVisible.value = false
  })
}

function dialogSure() {
  const ids: number[] = []
  const rawRemarkContent = toRaw(remarkContent)
  if (rawRemarkContent.id) {
    ids.push(rawRemarkContent.id)
  }
  remarkIntegralCouponOrder({
    backRemark: rawRemarkContent.backRemark,
    isCoverage: true,
    ids,
  }).then(() => {
    ElMessage.success('备注成功')
    remarkVisible.value = false
    emit('getCouponOrder')
  })
}

defineExpose({
  couponListRef,
})
</script>

<template>
  <div>
    <m-table ref="couponListRef" :data="orderList" :selection="true">
      <template #header="{ row }">
        <div style="width: 100%; display: flex; align-items: center; justify-content: space-between">
          <span style="margin-right: 50px">订单号：{{ row.id }}</span>
          <span style="float: right; margin-right: 10px">
            <el-popover placement="bottom-start" width="200" trigger="hover" :disabled="!row.note">
              <div>备注：{{ row.note }}</div>
              <template #reference>
                <i
                  class="iconfont iconPC-beizhu"
                  :style="{ color: row.backRemark ? 'red' : '' }"
                  style="font-size: 24px"
                  @click="remarkOrders(row)"
                />
              </template>
            </el-popover>
          </span>
        </div>
      </template>
      <m-table-column v-slot="{ row }" label="商品">
        <span class="middleCenter">
          <CouponType :coupon="row" />
        </span>
      </m-table-column>
      <m-table-column v-slot="{ row }" label="买家">
        <span class="middleCenter">{{ row.wxuserName }}</span>
      </m-table-column>
      <m-table-column v-slot="{ row }" label="优惠券有效期">
        <span v-if="row.effectiveDays > 0" class="middleCenter">领取后{{ row.effectiveDays }}天有效</span>
        <span v-else class="middleCenter">{{ row.couponValidity }}</span>
      </m-table-column>
      <m-table-column v-slot="{ row }" label="兑换时间">
        <span class="middleCenter">{{ row.sendTime }}</span>
      </m-table-column>
      <m-table-column v-slot="{ row }" label="实付积分">
        <span class="middleCenter">{{ row.realPayIntegral }}</span>
      </m-table-column>
    </m-table>
    <el-dialog v-model="remarkVisible" width="30%" :before-close="closeHandle">
      <template #header>
        <div style="font-weight: bold">备注</div>
      </template>
      <div>
        <div class="">您已选择1笔订单</div>
        <el-input
          v-model="remarkContent.backRemark"
          style="margin-top: 20px"
          type="textarea"
          :autosize="{ minRows: 4, maxRows: 4 }"
          placeholder="请输入备注"
        />
        <div class="digBottom" style="">
          <el-button @click="closeHandle"> 取 消 </el-button>
          <el-button type="primary" @click="dialogSure"> 确 定 </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
.middleCenter {
  display: flex;
  justify-content: center;
  align-items: center;
}

.digBottom {
  margin-top: 40px;
  display: flex;
  align-content: center;
  justify-content: center;
}
</style>
