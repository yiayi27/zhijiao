<script setup lang="ts">
import BaseTable from '@/components/BaseTable/index.vue'

// 定义优惠券项类型
interface CouponItem {
  couponType: number
  discount: string | number
  discountAmount: string | number
  conditionAmount: string | number
  issueQuantity: string | number
  limitQuantity: string | number
  integral: string | number
}

// 定义props
const props = defineProps<{
  couponContent: Record<string, any>
}>()

// 验证状态
const validType = ref<boolean>(false)

const couponList = ref<CouponItem[]>([
  {
    couponType: 0,
    discount: '',
    discountAmount: '',
    conditionAmount: '',
    issueQuantity: '',
    limitQuantity: '',
    integral: '',
  },
])

// 可选择优惠券列表
const modelList = [
  {
    id: 0,
    couponType: '无门槛折扣劵',
  },
  {
    id: 1,
    couponType: '无门槛现金劵',
  },
  {
    id: 2,
    couponType: '满折劵',
  },
  {
    id: 3,
    couponType: '满减劵',
  },
]

watch(
  () => props.couponContent,
  () => {
    getCoupon()
  },
  { deep: true },
)

/**
 * 编辑获得优惠券信息
 */
function getCoupon() {
  const temOption = JSON.parse(JSON.stringify(props.couponContent))
  couponList.value[0].couponType = temOption.couponType
  couponList.value[0].discount = temOption.discount
  couponList.value[0].discountAmount = temOption.discountAmount
  couponList.value[0].conditionAmount = temOption.conditionAmount
  couponList.value[0].issueQuantity = temOption.issueQuantity
  couponList.value[0].limitQuantity = temOption.limitQuantity
  couponList.value[0].integral = temOption.integral
  validType.value = true
}

/**
 * 选择优惠券
 */
function selectType(e: any) {
  couponList.value[0].couponType = e
  couponList.value[0].discount = ''
  couponList.value[0].discountAmount = ''
  couponList.value[0].conditionAmount = ''
  couponList.value[0].issueQuantity = ''
  couponList.value[0].limitQuantity = ''
  couponList.value[0].integral = ''
}

/**
 * 输入折扣验证
 */
function vaildInput(e: any) {
  const regs = /^\d(\.\d)?$/
  validType.value = regs.test(e.target.value)
  if (!regs.test(e.target.value)) {
    e.target.value = ''
    couponList.value[0].discount = ''
  }
}

/**
 * 输入数字验证
 */
function vaildNumInput(e: any) {
  const regs = /^0*[1-9]\d*$/
  validType.value = regs.test(e.target.value)
  if (!regs.test(e.target.value)) {
    e.target.value = ''
  }
}

// 向父组件暴露方法和变量
defineExpose({
  validType,
  couponList,
  getCoupon,
})
</script>

<template>
  <div class="addCompent w-full">
    <BaseTable :data="couponList" border>
      <el-table-column label="选择优惠券类型" width="180">
        <template #default="scope">
          <el-select v-model="scope.row.couponType" placeholder="请选择" @change="selectType">
            <el-option v-for="item in modelList" :key="item.id" :label="item.couponType" :value="item.id" />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column label="优惠券规则" width="250">
        <template #default="scope">
          <div v-if="scope.row.couponType === 0" class="ruleStyle">
            <el-input-number
              v-model="scope.row.discount"
              :min="0"
              :max="10"
              :controls="false"
              class="ruleStyle__firIpt"
              @blur="vaildInput($event)"
            />折，无门槛使用
          </div>
          <div v-if="scope.row.couponType === 1" class="ruleStyle">
            <el-input-number
              v-model="scope.row.discountAmount"
              :min="0"
              :controls="false"
              class="ruleStyle__firIpt"
              @blur="vaildNumInput($event)"
            />元，无门槛使用
          </div>
          <div v-if="scope.row.couponType === 2" class="ruleStyle">
            满
            <el-input-number
              v-model="scope.row.conditionAmount"
              :min="0"
              :controls="false"
              class="ruleStyle__firIpt"
              @blur="vaildNumInput($event)"
            />元，打
            <el-input-number
              v-model="scope.row.discount"
              :min="0"
              :max="10"
              :controls="false"
              class="ruleStyle__secIpt"
              @blur="vaildInput($event)"
            />折
          </div>
          <div v-if="scope.row.couponType === 3" class="ruleStyle">
            满
            <el-input-number
              v-model="scope.row.conditionAmount"
              :min="0"
              :controls="false"
              class="ruleStyle__firIpt"
              @blur="vaildNumInput($event)"
            />元，减
            <el-input-number
              v-model="scope.row.discountAmount"
              :min="0"
              :controls="false"
              class="ruleStyle__secIpt"
              @blur="vaildNumInput($event)"
            />元
          </div>
        </template>
      </el-table-column>
      <el-table-column label="发行量(张)">
        <template #default="scope">
          <el-input-number
            v-model="scope.row.issueQuantity"
            :min="0"
            step-strictly
            :step="1"
            :controls="false"
            style="width: 80px"
            @blur="vaildNumInput($event)"
          />
        </template>
      </el-table-column>
      <el-table-column label="限领(张/人)">
        <template #default="scope">
          <el-input-number
            v-model="scope.row.limitQuantity"
            :min="0"
            :step="1"
            step-strictly
            :controls="false"
            style="width: 80px"
            @blur="vaildNumInput($event)"
          />
        </template>
      </el-table-column>
      <el-table-column label="积分价">
        <template #default="scope">
          <el-input-number
            v-model="scope.row.integral"
            :min="0"
            step-strictly
            :step="1"
            :controls="false"
            style="width: 60px"
            @blur="vaildNumInput($event)"
          />
        </template>
      </el-table-column>
    </BaseTable>
  </div>
</template>

<style lang="scss" scoped>
.addCompent {
  margin-top: 10px;
  margin-bottom: 20px;
}
.ruleStyle {
  display: flex;
  justify-content: center;
  align-items: center;
  &__firIpt {
    width: 80px;
    margin: 0px 5px;
  }
  &__secIpt {
    width: 60px;
    margin: 0px 5px;
  }
}
</style>
