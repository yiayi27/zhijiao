<script setup lang="ts">
import BaseTable from '@/components/BaseTable/index.vue'

// 定义类型接口
interface CouponItem {
  couponType: number
  discount: number // 优惠折扣
  discountAmount: number | undefined // 优惠金额
  conditionAmount: number // 满足条件金额
  issueQuantity: number // 发行数量
  limitQuantity: number // 限领张数
}

interface ValidationRule {
  required?: boolean
  message?: string
  trigger: string
  validator?: (rule: unknown, value: any, callback: (error?: Error) => void) => void
}

interface CouponType {
  id: number
  couponType: string
}

// Props 定义
const props = defineProps({
  couponContent: {
    type: Object,
    default: () => ({}),
  },
  goodType: {
    type: Number,
    default: 0,
  },
})

// 可选择优惠券列表常量
const couponTypes: CouponType[] = [
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

// 组件引用
const couponRef = ref()

// 响应式数据
/** 禁用状态 */
const disType = ref(false)

/** 验证状态 */
const validType = ref(false)

const showType = ref(false)

const couponList = ref<CouponItem[]>([
  {
    couponType: 0,
    discount: 1, // 优惠折扣
    discountAmount: undefined, // 优惠金额
    conditionAmount: 0, // 满足条件金额
    issueQuantity: 0, // 发行数量
    limitQuantity: 1, // 限领张数
  },
])

/** 可选择优惠券列表 */
const modelList = ref<CouponType[]>(couponTypes)

// 监听属性
watch(
  () => props.couponContent,
  () => {
    getCoupon()
  },
  { deep: true },
)

watch(
  () => props.goodType,
  () => {
    setModeType()
  },
)

// 折扣验证
function discountValid(_rule: unknown, value: any, callback: (error?: Error) => void) {
  const regs = /^\d+.?\d?$/
  if (value > 10) {
    callback(new Error('折扣范围为0-10'))
  } else if (value <= 0) {
    callback(new Error('折扣为正数'))
  } else if (!regs.test(value)) {
    callback(new Error('保留一位小数'))
  } else {
    callback()
  }
}

// 金额验证
function discountAmountValid(_rule: unknown, value: any, callback: (error?: Error) => void) {
  const regs = /^\+?(\d+)$/
  if (!regs.test(value)) {
    callback(new Error('金额请输入整数'))
  } else if (value <= 0) {
    callback(new Error('金额不能小于1'))
  } else if (couponList.value[0].couponType === 3 && Number(couponList.value[0].conditionAmount) < Number(couponList.value[0].discountAmount)) {
    callback(new Error('优惠金额有误'))
    ElMessage.error('优惠金额不能大于满足条件')
  } else {
    callback()
  }
}

// 条件金额验证
function conditionAmountValid(_rule: unknown, value: any, callback: (error?: Error) => void) {
  const regs = /^\+?(\d+)$/
  if (!regs.test(value)) {
    callback(new Error('满足金额有误'))
  } else if (value <= 0) {
    callback(new Error('金额不能小于1'))
  } else {
    callback()
  }
}

// 发行量验证
function issueQuantityVlidator(_rule: unknown, value: any, callback: (error?: Error) => void) {
  const regs = /^\+?(\d+)$/
  if (!regs.test(value)) {
    callback(new Error('请输入整数'))
  } else {
    callback()
  }
}

// 表单验证规则
const couponRules: Record<string, ValidationRule[]> = {
  discount: [
    {
      required: true,
      message: '折扣不能为空',
      trigger: 'blur',
    },
    {
      validator: discountValid,
      trigger: 'blur',
    },
  ],
  discountAmount: [
    {
      required: true,
      message: '金额不能为空',
      trigger: 'blur',
    },
    {
      validator: discountAmountValid,
      trigger: 'blur',
    },
  ],
  conditionAmount: [
    {
      required: true,
      message: '条件不能为空',
      trigger: 'blur',
    },
    {
      validator: conditionAmountValid,
      trigger: 'blur',
    },
  ],
  issueQuantity: [
    {
      required: true,
      message: '发行量不能为空',
      trigger: 'blur',
    },
    {
      validator: issueQuantityVlidator,
      trigger: 'blur',
    },
  ],
  limitQuantity: [
    {
      required: true,
      message: '限领量不能为空',
      trigger: 'blur',
    },
    {
      validator: issueQuantityVlidator,
      trigger: 'blur',
    },
  ],
}

/**
 * 设置模式类型
 */
function setModeType() {
  if (props.goodType === 2) {
    couponList.value[0].couponType = 0
    modelList.value = [
      {
        id: 0,
        couponType: '无门槛折扣劵',
      },
    ]
  } else {
    modelList.value = couponTypes
  }
}

/**
 * 编辑获得优惠券信息
 */
function getCoupon() {
  const temOption = JSON.parse(JSON.stringify(props.couponContent))
  disType.value = temOption.status === 1
  couponList.value[0].couponType = temOption.couponType
  couponList.value[0].discount = Number(temOption.discount)
  couponList.value[0].discountAmount = Number(temOption.discountAmount)
  couponList.value[0].conditionAmount = Number(temOption.conditionAmount)
  couponList.value[0].issueQuantity = Number(temOption.issueQuantity)
  couponList.value[0].limitQuantity = Number(temOption.limitQuantity)
  validType.value = true
}

/**
 * 选择优惠券
 */
function selectType(e: number) {
  couponList.value[0].couponType = e
  couponList.value[0].discount = 1
  couponList.value[0].discountAmount = 0
  couponList.value[0].conditionAmount = 0
  couponList.value[0].issueQuantity = 0
  couponList.value[0].limitQuantity = 1
  clearValidate()
}

/**
 * 对整个表单进行校验
 */
function validate() {
  return couponRef.value.validate()
}

/**
 * 移除表单项的校验结果
 */
function clearValidate() {
  couponRef.value.clearValidate()
}

// 暴露组件方法
defineExpose({
  validate,
  clearValidate,
  couponList,
})
</script>

<template>
  <div class="addCompent">
    <el-form ref="couponRef" :model="couponList[0]" :rules="couponRules" class="demo-ruleForm">
      <BaseTable :data="couponList" border>
        <el-table-column label="选择优惠券类型" width="180" align="center">
          <template #default="scope">
            <el-select v-model="scope.row.couponType" placeholder="请选择" :disabled="disType" @change="selectType">
              <el-option v-for="item in modelList" :key="item.id" :label="item.couponType" :value="item.id" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="优惠券规则" width="300" align="center">
          <template #default="scope">
            <div v-if="scope.row.couponType === 0" class="ruleStyle">
              <el-form-item prop="discount">
                <el-input-number
                  v-model="scope.row.discount"
                  :min="0"
                  :max="9.9"
                  :step="0.1"
                  :controls="false"
                  step-strictly
                  class="ruleStyle__firIpt"
                  :disabled="disType"
                />
                折，无门槛使用
              </el-form-item>
            </div>
            <div v-if="scope.row.couponType === 1" class="ruleStyle">
              <el-form-item prop="discountAmount">
                <el-input-number v-model="scope.row.discountAmount" :min="1" :controls="false" class="ruleStyle__firIpt" :disabled="disType" />
                元，无门槛使用
              </el-form-item>
            </div>
            <div v-if="scope.row.couponType === 2" class="ruleStyle">
              满
              <el-form-item prop="conditionAmount">
                <el-input-number v-model="scope.row.conditionAmount" :min="0" :controls="false" class="ruleStyle__firIpt" :disabled="disType" />
              </el-form-item>
              元，打
              <el-form-item prop="discount">
                <el-input-number
                  v-model="scope.row.discount"
                  :min="0"
                  :max="9.9"
                  :step="0.1"
                  :controls="false"
                  class="ruleStyle__secIpt"
                  style="width: 80px"
                  :disabled="disType"
                />
              </el-form-item>
              折
            </div>
            <div v-if="scope.row.couponType === 3" class="ruleStyle">
              满
              <el-form-item prop="conditionAmount">
                <el-input-number v-model="scope.row.conditionAmount" :min="0" :controls="false" class="ruleStyle__firIpt" :disabled="disType" />
              </el-form-item>
              元，减
              <el-form-item prop="discountAmount">
                <el-input-number
                  v-model="scope.row.discountAmount"
                  :min="1"
                  :controls="false"
                  class="ruleStyle__secIpt"
                  :disabled="disType"
                  style="width: 80px"
                />
              </el-form-item>
              元
            </div>
          </template>
        </el-table-column>
        <el-table-column label="发行量(张)" align="center">
          <template #default="scope">
            <el-form-item prop="issueQuantity">
              <el-input-number
                v-model="scope.row.issueQuantity"
                :min="0"
                :step="1"
                step-strictly
                :controls="false"
                style="width: 80px"
                :disabled="disType"
              />
            </el-form-item>
          </template>
        </el-table-column>
        <el-table-column label="限领(张/人)" align="center">
          <template #default="scope">
            <el-form-item prop="limitQuantity">
              <el-input-number
                v-model="scope.row.limitQuantity"
                :min="0"
                step-strictly
                :step="1"
                :controls="false"
                style="width: 80px"
                :disabled="disType"
              />
            </el-form-item>
          </template>
        </el-table-column>
      </BaseTable>
    </el-form>
    <div v-if="showType" class="expText">折扣输入有误</div>
  </div>
</template>

<style lang="scss" scoped>
.addCompent {
  margin-top: 10px;
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
    width: 80px;
    margin: 0px 5px;
  }
}

.expText {
  font-size: 13px;
  color: rgb(250, 100, 101);
  margin-left: 225px;
}
</style>
