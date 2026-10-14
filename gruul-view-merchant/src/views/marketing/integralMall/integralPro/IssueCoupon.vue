<script setup lang="ts">
import { addCoupon, couponDetail, editCoupon } from '@/api/couponActivity/coupon'
import { mapToRemoveAttrs } from '@/libs/base'
import dayjs from 'dayjs'
import { ElMessageBox } from 'element-plus'

import { onBeforeRouteLeave, useRoute, useRouter } from 'vue-router'
import ApointGoods from '../../common/ApointGoods.vue'
import AddCouponTable from './issueCouponForm/AddCouponTable.vue'

defineOptions({
  name: 'IssueCouponAdd',
})

const route = useRoute()
const router = useRouter()

const fromRef = ref()
const addComp = ref()
const apointComp = ref()

// 优惠券使用时间
const timeRadio = ref('setTime')

// 禁用状态
const disType = ref(false)

// nameid 用name 字段控制
const nameId = ref('')

// 定义 ruleForm 类型
interface RuleForm {
  useDateValue: {
    startTime: string
    endTime: string
    effectiveDays: number
  }
  type: any[]
}

// 定义 couponContent 类型
interface CouponContent {
  id?: string
  name?: string
  status?: number
  startTime?: string
  endTime?: string
  effectiveDays?: number
  productVos?: any[]
  [key: string]: any
}

const ruleForm = reactive<RuleForm>({
  useDateValue: {
    startTime: '',
    endTime: '',
    effectiveDays: 0,
  },
  type: [],
})

// 优惠券信息
const couponContent = ref<CouponContent>({})

// 退出弹窗状态
const saveFlag = ref(false)

// activity类型
const actType = 'AddActivity'

// 开始日期的禁用规则
function startTimeDisabledDate(time: any) {
  const isBeforeCurrent = time.getTime() < dayjs().startOf('day').valueOf()
  if (ruleForm.useDateValue.endTime) {
    return time.getTime() > dayjs(ruleForm.useDateValue.endTime).valueOf() || isBeforeCurrent
  }
  return isBeforeCurrent
}

// 结束日期的禁用规则
function endTimeDisabledDate(time: any) {
  const isBeforeCurrent = time.getTime() < dayjs().startOf('day').valueOf()
  if (ruleForm.useDateValue.startTime) {
    return time.getTime() < dayjs(ruleForm.useDateValue.startTime).valueOf()
  }
  return isBeforeCurrent
}

function validatePass(rule: any, value: any, callback: any) {
  const start = dayjs(value.startTime).valueOf()
  const end = dayjs(value.endTime).valueOf()
  const now = dayjs().startOf('day').valueOf()
  if (timeRadio.value === 'setTime') {
    if (!value.endTime || !value.startTime) {
      callback(new Error('请输入活动时间'))
    } else {
      if (start > end) {
        callback(new Error('结束时间不能小于开始时间'))
      }
      if (start < now) {
        callback(new Error('开始时间不能小于当前时间'))
      }
    }
  } else if (!value.effectiveDays) {
    callback(new Error('请输入活动时间'))
  }
}

const rules = {
  useDateValue: [
    {
      validator: validatePass,
      required: true,
      trigger: 'blur',
    },
  ],
  actRules: [{ required: true, trigger: 'blur' }],
}

/**
 * 选择时间类型
 */
function chooseTimeType() {
  // 共用一个字段 避免时间格式问题 先清空操作
  ruleForm.useDateValue = {
    startTime: '',
    endTime: '',
    effectiveDays: 0,
  }
}

onMounted(() => {
  if (route.query.id) {
    getCouponDetail()
  }
})

/**
 * 优惠券详情
 */
function getCouponDetail() {
  couponDetail(route.query.id as string, {}).then((res) => {
    const result = res.data.couponVo
    const productVos = res.data.discountProductVos
    disType.value = result.status === 1
    timeRadio.value = result.startTime ? 'setTime' : 'immediate'
    ruleForm.useDateValue.effectiveDays = result.effectiveDays
    nameId.value = result.name
    if (result.startTime) {
      ruleForm.useDateValue.startTime = result.startTime
      ruleForm.useDateValue.endTime = result.endTime
    }
    couponContent.value = { ...result, productVos }
  })
}

/**
 * 保存发布优惠券
 */
async function save() {
  const check = checkSecond()
  if (!check) {
    return
  }

  const param = dealParam()
  if (Number(param.integral) === 0) {
    ElMessage.error('积分值不能小于0')
    return
  }

  if (route.query.id) {
    edit(param)
    return
  }

  addCoupon(param).then(() => {
    saveFlag.value = true
    ElMessage.success('发布成功')
    router.push({
      name: 'IntegralMallIndex',
      query: { comName: 'CouponList' },
    })
  })
}

/**
 * 处理请求参数
 */
function dealParam() {
  const isImmediate = timeRadio.value === 'immediate'
  const { startTime, endTime } = ruleForm.useDateValue
  const timeParams = isImmediate
    ? {
        effectiveDays: ruleForm.useDateValue.effectiveDays,
        startTime: '',
        endTime: '',
        effectiveType: 1,
      }
    : {
        effectiveDays: '',
        startTime: dayjs(startTime).startOf('day').format('YYYY-MM-DD HH:mm:ss'),
        endTime: dayjs(endTime).endOf('day').format('YYYY-MM-DD HH:mm:ss'),
        effectiveType: 0,
      }

  return {
    id: route.query.id ? couponContent.value.id : '',
    name: nameId.value,
    giveType: 2,
    productType: apointComp.value.goodRadio,
    couponProducts: mapToRemoveAttrs(apointComp.value.pointGoodsList, [{ key: 'id', value: (item: any) => ({ productId: item.id }) }]),
    ...addComp.value.couponList[0],
    ...timeParams,
  }
}

/**
 * 二次验证条件
 */
function checkSecond() {
  if (timeRadio.value === 'setTime') {
    if (!ruleForm.useDateValue.startTime || !ruleForm.useDateValue.endTime) {
      ElMessage.error('请输入有效时间')
      return false
    }
    const start = dayjs(ruleForm.useDateValue.startTime).valueOf()
    const end = dayjs(ruleForm.useDateValue.endTime).valueOf()
    const now = dayjs().startOf('day').valueOf()
    if (start > end || start < now) {
      return false
    }
  } else if (!Number(ruleForm.useDateValue.effectiveDays)) {
    ElMessage.error('请输入大于0的有效时间')
    return false
  }

  if (!addComp.value.validType) {
    ElMessage.error('请输入活动规则')
    return false
  }
  if (apointComp.value.goodRadio !== 0 && apointComp.value.pointGoodsList.length === 0) {
    ElMessage.error('请选择指定商品')
    return false
  }
  return true
}

// /**
//  * 验证表单
//  */
// function formValid() {
//   return fromRef.value.validate()
// }

/**
 * 编辑优惠券
 */
function edit(param: any) {
  editCoupon(param).then((res) => {
    if (res.code === 200) {
      saveFlag.value = true
      ElMessage.success('编辑成功')
      router.push({
        name: 'IntegralMallIndex',
        query: { comName: 'CouponList' },
      })
    }
  })
}

// 路由离开前确认
onBeforeRouteLeave((to, from, next) => {
  if (saveFlag.value) {
    next()
    return
  }
  ElMessageBox.confirm(`确定要退出${route.query.id ? '编辑' : '发布'}积分优惠券页面?退出后，未保存的信息将不会保留!`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    next()
  })
})
</script>

<template>
  <div>
    <div class="title">发布积分优惠券</div>
    <el-form ref="fromRef" :model="ruleForm" :rules="rules" class="demo-ruleForm">
      <el-form-item label="有效时间" prop="useDateValue">
        <div>
          <el-radio v-model="timeRadio" value="setTime" :disabled="disType" @change="chooseTimeType"> 固定时间 </el-radio>
          <el-radio v-model="timeRadio" value="immediate" :disabled="disType" @change="chooseTimeType"> 领券立即生效 </el-radio>
          <div class="mt-[10px]">
            <span v-if="timeRadio === 'setTime'">
              <el-date-picker
                v-model="ruleForm.useDateValue.startTime"
                type="date"
                placeholder="开始日期"
                style="width: 200px"
                :disabled="disType"
                value-format="YYYY-MM-DD"
                :disabled-date="startTimeDisabledDate"
              />
              <el-date-picker
                v-model="ruleForm.useDateValue.endTime"
                type="date"
                placeholder="结束日期"
                style="width: 200px; margin-left: 10px"
                value-format="YYYY-MM-DD"
                :disabled-date="endTimeDisabledDate"
              />
            </span>
            <span v-else
              >领券当日起
              <el-input-number
                v-model="ruleForm.useDateValue.effectiveDays"
                :min="0"
                :controls="false"
                style="width: 80px; margin: 10px 0px"
                :disabled="disType"
              />天内可用
            </span>
          </div>
        </div>
      </el-form-item>
      <el-form-item label="活动规则" required>
        <AddCouponTable ref="addComp" :coupon-content="couponContent" />
      </el-form-item>
      <el-form-item label="商品选择" required>
        <ApointGoods ref="apointComp" :act-type="actType" :content="couponContent" />
      </el-form-item>
    </el-form>
    <div class="pageButtom">
      <el-button @click="$router.push({ name: 'IntegralMallIndex' })"> 取消 </el-button>
      <el-button type="primary" @click="save"> 保存 </el-button>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.title {
  margin: 10px 0px;
  font-weight: bold;
  font-size: 14px;
  margin-bottom: 40px;
}

.remark {
  margin-left: 40px;
  color: #9b9b9b;
}

.pageButtom {
  margin-top: 80px;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
