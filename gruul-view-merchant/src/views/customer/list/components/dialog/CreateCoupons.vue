<script setup lang="ts">
import CouponRuleForm from '@/views/customer/list/components/dialog/CouponRuleForm.vue'
import ApointGoods from '@/views/marketing/common/ApointGoods.vue'
import dayjs from 'dayjs'
import { ElMessageBox } from 'element-plus'

import { GiveCoupon } from '../../formModel/GiveCoupon'

const emit = defineEmits(['createCoupons'])

const dialogVisible = defineModel<boolean>('visible', { required: true })

const couponCreateFormRef = ref()
const CouponRuleFormRef = ref()
const apointCompRef = ref()

const actType = 'AddActivity'

/** 优惠券信息 */
const couponContent = ref({})

/** 用户输入表单数据 */
const couponCreateForm = reactive({
  effectiveType: 0,
  couponList: [
    {
      couponType: '',
      discount: '',
      conditionAmount: '',
      discountAmount: '',
    },
  ],
  startTime: '',
  endTime: '',
  effectiveDays: 1,
  productType: 0,
  couponProducts: [],
})

// 开始日期的禁用规则
function startTimeDisabledDate(time: Date) {
  const isBeforeCurrent = time.getTime() < dayjs().startOf('day').valueOf()
  // 如果已经选择了结束时间，则禁用所有大于结束时间的日期
  if (couponCreateForm.endTime) {
    return time.getTime() > dayjs(couponCreateForm.endTime).valueOf() || isBeforeCurrent
  }
  return isBeforeCurrent
}

// 结束日期的禁用规则
function endTimeDisabledDate(time: Date) {
  const isBeforeCurrent = time.getTime() < dayjs().startOf('day').valueOf()
  // 如果已经选择了开始时间，则禁用所有小于开始时间的日期
  if (couponCreateForm.startTime) {
    return time.getTime() < dayjs(couponCreateForm.startTime).valueOf()
  }
  return isBeforeCurrent
}

function validatePass(_rule: any, value: string, callback: (error?: Error) => void) {
  const start = dayjs(couponCreateForm.startTime).valueOf()
  const end = dayjs(value).valueOf()
  const now = dayjs().startOf('day').valueOf()
  if (!couponCreateForm.startTime || !value) {
    callback(new Error('请输入活动时间'))
  } else {
    if (start > end) {
      callback(new Error('结束时间不能小于开始时间'))
    }
    if (start < now) {
      callback(new Error('开始时间不能小于当前时间'))
    }
  }
  callback()
}

function validateday(rule: any, value: string | number, callback: (error?: Error) => void) {
  const regs = /^\+?(\d+)$/
  if (typeof value === 'string' && !regs.test(value)) {
    callback(new Error('有效期请输入整数'))
  } else if (Number(value) <= 0) {
    callback(new Error('有效期不能小于1'))
  } else {
    callback()
  }
}

const couponCreateRules = {
  effectiveType: [{ required: true, message: '请选择有效时间', trigger: 'change' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, validator: validatePass, trigger: 'change' }],
  effectiveDays: [{ required: true, validator: validateday, trigger: 'change' }],
  productType: [{ required: true, message: '请选择适用商品', trigger: 'change' }],
}

watch(
  () => dialogVisible.value,
  (v) => {
    if (v) {
      Object.assign(couponCreateForm, {
        effectiveType: 0,
        couponList: [
          {
            couponType: '',
            discount: '',
            conditionAmount: '',
            discountAmount: '',
          },
        ],
        startTime: '',
        endTime: '',
        effectiveDays: 1,
        productType: 0,
        couponProducts: [],
      })
    } else {
      CouponRuleFormRef.value?.clearValidate()
    }
  },
)

function couponListHandle(couponList: any[]) {
  couponCreateForm.couponList = couponList
}

/**
 * 对整个表单进行校验
 */
function formValidate() {
  return couponCreateFormRef.value.validate()
}

async function confirmClickHandle() {
  await formValidate()
  await CouponRuleFormRef.value.validate()
  emitCreateCoupons()
  dialogVisible.value = false
  apointCompRef.value.pointGoodsList = []
  apointCompRef.value.goodRadio = 0
}

/**
 * 触发更新菜单事件
 */
function emitCreateCoupons() {
  const coupons = couponCreateForm.couponList.map((item) => {
    const coupon = new GiveCoupon()
    coupon.effectiveType = couponCreateForm.effectiveType
    coupon.effectiveDays = Number(couponCreateForm.effectiveDays) || 1
    coupon.startTime = couponCreateForm.startTime ? dayjs(couponCreateForm.startTime).startOf('day').format('YYYY-MM-DD HH:mm:ss') : ''
    coupon.endTime = couponCreateForm.endTime ? dayjs(couponCreateForm.endTime).endOf('day').format('YYYY-MM-DD HH:mm:ss') : ''
    Object.assign(coupon, item)
    coupon.productType = apointCompRef.value.goodRadio
    coupon.couponProducts = apointCompRef.value.pointGoodsList.map((item: any) => {
      return {
        productId: item.id,
      }
    })
    return coupon
  })

  emit('createCoupons', coupons)
  return coupons
}

/**
 * 关闭弹窗
 */
async function closeHandle() {
  const result = await confirmHandle()
  if (result === 'confirm') {
    dialogVisible.value = false
  }
}

async function beforeCloseHandle(done: Fn) {
  const result = await confirmHandle()
  if (result === 'confirm') {
    done()
  }
}

/**
 * 关闭确认
 */
async function confirmHandle() {
  return await ElMessageBox.confirm('确定退出创建优惠券页面？退出后，赠送优惠券信息将不保留！', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
}
</script>

<template>
  <el-dialog v-model="dialogVisible" title="新增优惠券" width="800px" :before-close="beforeCloseHandle" append-to-body>
    <div class="content">
      <el-form ref="couponCreateFormRef" :model="couponCreateForm" :rules="couponCreateRules" class="couponCreateForm">
        <el-form-item label="有效时间" prop="effectiveType">
          <el-radio-group v-model="couponCreateForm.effectiveType">
            <el-radio :value="0"> 固定时间 </el-radio>
            <el-radio :value="1"> 领券立即生效 </el-radio>
          </el-radio-group>
        </el-form-item>
        <div v-if="couponCreateForm.effectiveType === 0" class="couponTime">
          <el-form-item prop="startTime">
            <el-date-picker
              v-model="couponCreateForm.startTime"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              placeholder="开始日期"
              :disabled-date="startTimeDisabledDate"
            />
          </el-form-item>
          <el-form-item prop="endTime" class="couponEndTime">
            <el-date-picker
              v-model="couponCreateForm.endTime"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              placeholder="结束日期"
              :disabled-date="endTimeDisabledDate"
            />
          </el-form-item>
        </div>
        <el-form-item v-if="couponCreateForm.effectiveType === 1" prop="effectiveDays">
          <span
            >领券当日起
            <el-input-number v-model="couponCreateForm.effectiveDays" :min="1" step-strictly :step="1" :controls="false" style="width: 150px" />
            天内可用
          </span>
        </el-form-item>
        <el-form-item label="活动规则" label-position="top">
          <CouponRuleForm ref="CouponRuleFormRef" @update="couponListHandle" />
        </el-form-item>
        <el-form-item label="商品选择" prop="actRules">
          <ApointGoods ref="apointCompRef" :act-type="actType" :content="couponContent" />
        </el-form-item>
      </el-form>
    </div>
    <template #footer>
      <div class="dialog--footer">
        <el-button @click="closeHandle"> 取 消 </el-button>
        <el-button type="primary" @click="confirmClickHandle"> 确 定 </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<style scoped lang="scss">
.couponCreateForm {
  margin-left: 40px;

  .couponTime {
    display: flex;
    justify-content: flex-start;

    :deep(.couponEndTime .el-form-item__content) {
      margin-left: 40px;
    }
  }
}
</style>
