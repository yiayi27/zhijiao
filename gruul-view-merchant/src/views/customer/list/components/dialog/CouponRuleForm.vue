<script setup lang="ts">
import BaseTable from '@/components/BaseTable/index.vue'
import type { ElForm } from 'element-plus'

// 定义类型接口
interface CouponFormType {
  couponType: number | null
  discount: string
  conditionAmount: string
  discountAmount: string
}

const emit = defineEmits(['update'])

const couponRef = ref<InstanceType<typeof ElForm> | null>(null)

const couponList = reactive<CouponFormType[]>([
  {
    couponType: null,
    discount: '',
    conditionAmount: '',
    discountAmount: '',
  },
])

watch(
  couponList,
  (newList) => {
    emit('update', newList)
  },
  { deep: true },
)

/**
 * 对整个表单进行校验
 */
function validate(): Promise<boolean> {
  if (!couponRef.value) {
    return Promise.reject(new Error('表单未初始化'))
  }
  return couponRef.value.validate()
}

/**
 * 移除表单项的校验结果
 */
function clearValidate(): void {
  couponList[0] = {
    couponType: 0,
    discount: '',
    conditionAmount: '',
    discountAmount: '',
  }
}

/** 折扣验证 */
function discountValid(_rule: unknown, value: any, callback: (error?: Error) => void): void {
  const regs = /^\d+.?\d?$/
  if (value > 9) {
    callback(new Error('折扣范围为1-9'))
  } else if (value <= 0) {
    callback(new Error('折扣为正整数'))
  } else if (!regs.test(value)) {
    callback(new Error('保留一位小数'))
  } else {
    callback()
  }
}

/** 金额验证 */
function discountAmountValid(_rule: unknown, value: any, callback: (error?: Error) => void): void {
  const regs = /^\+?(\d+)$/
  if (!regs.test(value)) {
    callback(new Error('金额请输入整数'))
  } else if (value <= 0) {
    callback(new Error('金额不能小于1'))
  } else if (couponList[0].couponType === 3 && Number(couponList[0].conditionAmount) < Number(couponList[0].discountAmount)) {
    callback(new Error('优惠金额有误'))
    ElMessage.error('优惠金额不能大于满足条件')
  } else {
    callback()
  }
}

/** 条件金额 */
function conditionAmountValid(_rule: unknown, value: any, callback: (error?: Error) => void): void {
  const regs = /^\+?(\d+)$/
  if (!regs.test(value)) {
    callback(new Error('满足金额有误'))
  } else if (value <= 0) {
    callback(new Error('金额不能小于1'))
  } else {
    callback()
  }
}

const couponRules = {
  discount: [
    { required: true, message: '折扣不能为空', trigger: 'blur' },
    { validator: discountValid, trigger: 'blur' },
  ],
  discountAmount: [
    { required: true, message: '金额不能为空', trigger: 'blur' },
    { validator: discountAmountValid, trigger: 'blur' },
  ],
  conditionAmount: [
    { required: true, message: '条件不能为空', trigger: 'blur' },
    { validator: conditionAmountValid, trigger: 'blur' },
  ],
}

defineExpose({
  validate,
  clearValidate,
})
</script>

<template>
  <el-form ref="couponRef" :model="couponList[0]" :rules="couponRules" label-width="0" class="w-full">
    <BaseTable :data="couponList">
      <el-table-column label="选择优惠券类型">
        <template #default="scope">
          <el-select v-model="scope.row.couponType" clearable placeholder="请选择优惠券类型" class="w-[200px]">
            <el-option label="无门槛折扣券" :value="0" />
            <el-option label="满折劵" :value="2" />
            <el-option label="满减券" :value="3" />
            <el-option label="无门槛现金劵" :value="1" />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column label="优惠券规则">
        <template #default="scope">
          <div v-if="scope.row.couponType === 0">
            <el-form-item prop="discount">
              <el-input-number v-model="scope.row.discount" :min="0" :max="10" :controls="false" style="width: 80px" />
              折，无门槛使用
            </el-form-item>
          </div>
          <div v-if="scope.row.couponType === 1">
            <el-form-item prop="discountAmount">
              <el-input-number v-model="scope.row.discountAmount" :min="1" :controls="false" type="number" style="width: 90px" />
              元，无门槛使用
            </el-form-item>
          </div>
          <div v-if="scope.row.couponType === 2" class="flex items-center">
            <el-form-item prop="discount">
              <el-input-number v-model="scope.row.discount" :min="0" :max="10" :controls="false" type="number" style="width: 90px" />
              折，满
            </el-form-item>
            <el-form-item prop="conditionAmount">
              <el-input-number v-model="scope.row.conditionAmount" :min="0" :controls="false" type="number" style="width: 90px" />
              元使用
            </el-form-item>
          </div>
          <div v-if="scope.row.couponType === 3" class="flex items-center">
            <el-form-item prop="discountAmount">
              <el-input-number v-model="scope.row.discountAmount" :min="1" :controls="false" type="number" style="width: 90px" />
              元，满
            </el-form-item>
            <el-form-item prop="conditionAmount">
              <el-input-number v-model="scope.row.conditionAmount" :min="0" :controls="false" type="number" style="width: 90px" />
              元使用
            </el-form-item>
          </div>
        </template>
      </el-table-column>
    </BaseTable>
  </el-form>
</template>

<style lang="scss" scoped>
.ruleStyle {
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
