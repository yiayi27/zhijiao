<script setup lang="ts">
import { addFull, fullActivityDet, updateFullActivity } from '@/api/fullReduce/fullActive'
import { mapToRemoveAttrs } from '@/libs/base'
import ApointGoods from '@/views/marketing/common/ApointGoods.vue'
import dayjs from 'dayjs'
import { ElMessageBox } from 'element-plus'

import { onBeforeRouteLeave, useRoute, useRouter } from 'vue-router'
import ActRuleAdd from './ActRuleAdd.vue'

defineOptions({
  name: 'FullRedAdd',
})

const router = useRouter()
const route = useRoute()
const actType = 'FullReduceAdd'

const fromRef = ref()
const actRule = ref()
const apointComp = ref()

/** 可使用优惠券选项 */
const applyList = [
  {
    label: '会员价',
    value: '0',
  },
  {
    label: '优惠券',
    value: '1',
  },
]

const ruleForm = reactive({
  name: '',
  useDateValue: {
    startTime: '',
    endTime: '',
  },
  applyType: [] as string[],
  deduction: false,
})

const rules = {
  name: [{ required: true, message: '请输入活动名称', trigger: 'blur' }],
  useDateValue: [
    {
      validator: validatePass,
      required: true,
      trigger: 'blur',
    },
  ],
  actRules: [{ required: true, trigger: 'blur' }],
}

// 开始日期的禁用规则
function startTimeDisabledDate(time: Date): boolean {
  const isBeforeCurrent = time.getTime() < dayjs().startOf('day').valueOf()
  // 如果已经选择了结束时间，则禁用所有大于结束时间的日期
  if (ruleForm.useDateValue.endTime) {
    return time.getTime() > dayjs(ruleForm.useDateValue.endTime).valueOf() || isBeforeCurrent
  }
  return isBeforeCurrent
}

// 结束日期的禁用规则
function endTimeDisabledDate(time: Date): boolean {
  const isBeforeCurrent = time.getTime() < dayjs().startOf('day').valueOf()
  // 如果已经选择了开始时间，则禁用所有小于开始时间的日期
  if (ruleForm.useDateValue.startTime) {
    return time.getTime() < dayjs(ruleForm.useDateValue.startTime).valueOf()
  }
  return isBeforeCurrent
}

function validatePass(_rule: any, value: any, callback: (error?: Error) => void) {
  if (value.startTime && value.endTime) {
    const startTime = dayjs(value.startTime).valueOf()
    const endTime = dayjs(value.endTime).valueOf()

    if (startTime > endTime) {
      callback(new Error('结束时间不能小于开始时间'))
    } else {
      callback()
    }
  } else {
    callback(new Error('请输入活动时间'))
  }
}

/** 禁用状态 */
const idDisabled = ref(false)

/**
 * 编辑时获取的满减详情
 */
const fullContent = ref({})

/** 退出弹窗状态 */
const saveFlag = ref(false)

/** 是否编辑商品 */
const isEdit = ref(false)

onMounted(() => {
  isEdit.value = !!route.query.id
  route.meta.title = route.query.id ? '编辑满减活动' : '新增满减活动'

  if (route.query.id) {
    getFullDetail()
  }
})

/**
 * 获取满减活动详情
 */
function getFullDetail() {
  const id = route.query.id as string
  fullActivityDet(id, {}).then((res) => {
    const productVos = res.data.discountProductVos
    const result = res.data.fullScaleVo
    const disType = result.status === 1
    idDisabled.value = disType
    ruleForm.name = result.name
    ruleForm.deduction = result.deductionType === '0'
    ruleForm.applyType = result.applyType?.split(',') || []
    ruleForm.useDateValue.startTime = result.startTime
    ruleForm.useDateValue.endTime = result.endTime
    fullContent.value = { ...result, disType, productVos }
  })
}

/**
 * 验证表单
 */
function formValid() {
  return fromRef.value.validate()
}

/**
 * 保存满减活动
 */
function save() {
  formValid()

  const check = checkSecond()
  if (!check) {
    return
  }

  const parma = {
    applyType: ruleForm.applyType.join(','),
    deductionType: ruleForm.deduction ? 0 : '',
    startTime: dayjs(ruleForm.useDateValue.startTime).startOf('day').format('YYYY-MM-DD HH:mm:ss'),
    endTime: dayjs(ruleForm.useDateValue.endTime).endOf('day').format('YYYY-MM-DD HH:mm:ss'),
    name: ruleForm.name,
    fullScaleProducts: mapToRemoveAttrs(apointComp.value.pointGoodsList, [{ key: 'id', value: (item: any) => ({ productId: item.id }) }]),
    fullScaleRules: mapToRemoveAttrs(actRule.value.fullList, ['id']),
    productType: apointComp.value.goodRadio,
    id: route.query.id ? (fullContent.value as any).id : '',
  }
  if (parma.fullScaleRules[0].ruleType === 0) {
    if (Number(parma.fullScaleRules[0].conditionAmount) < Number(parma.fullScaleRules[0].discountAmount)) {
      ElMessage.error('满减金额不能大于满足条件')
      return
    }
  }
  if (route.query.id) {
    editFull(parma)
    return
  }
  addFull(parma).then((res) => {
    if (res.code === 200) {
      saveFlag.value = true
      ElMessage.success('发布成功')
      router.push({ name: 'FullIndex' })
    }
  })
}

/**
 * 二次验证条件
 */
function checkSecond() {
  const start = dayjs(ruleForm.useDateValue.startTime).valueOf()
  const end = dayjs(ruleForm.useDateValue.endTime).valueOf()

  if (start > end) {
    return false
  }

  if (!actRule.value.validType) {
    ElMessage.error('请输入活动规则')
    return false
  }
  if (apointComp.value.goodRadio !== 0 && apointComp.value.pointGoodsList.length === 0) {
    ElMessage.error('请选择指定商品')
    return false
  }
  return true
}

/**
 * 编辑满减活动
 */
function editFull(param: any) {
  updateFullActivity(param).then((res) => {
    if (res.code === 200) {
      ElMessage.success('编辑成功')
      saveFlag.value = true
      router.push({ name: 'FullIndex' })
    }
  })
}

// 路由离开前的确认
onBeforeRouteLeave((to, from, next) => {
  if (saveFlag.value) {
    next()
    return
  }
  ElMessageBox.confirm(`确定要退出${route.query.id ? '编辑' : '发布'}满减活动?退出后，未保存的信息将不会保留!`, '提示', {
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
    <div class="title">{{ isEdit ? '编辑' : '新增' }}满减活动</div>
    <el-form ref="fromRef" :model="ruleForm" :rules="rules" label-width="100px" class="demo-ruleForm">
      <el-form-item label="活动名称" prop="name" width="55">
        <el-input v-model="ruleForm.name" style="width: 473px" placeholder="请输入活动名称" maxlength="5" />
        <span class="remark">活动名称不超过5个字</span>
      </el-form-item>
      <el-form-item label="活动时间" prop="useDateValue">
        <el-date-picker
          v-model="ruleForm.useDateValue.startTime"
          type="date"
          placeholder="开始日期"
          style="width: 220px"
          :disabled="idDisabled"
          value-format="YYYY-MM-DD"
          :disabled-date="startTimeDisabledDate"
        />
        <span style="margin-left: 10px; color: #ccc">至</span>
        <el-date-picker
          v-model="ruleForm.useDateValue.endTime"
          type="date"
          placeholder="结束日期"
          style="width: 220px; margin-left: 10px"
          value-format="YYYY-MM-DD"
          :disabled-date="endTimeDisabledDate"
        />
      </el-form-item>
      <el-form-item label="活动规则" required>
        <ActRuleAdd ref="actRule" :full-content="fullContent" />
      </el-form-item>
      <el-form-item label="商品选择" required>
        <ApointGoods ref="apointComp" :act-type="actType" :content="fullContent" />
      </el-form-item>
      <el-form-item label="可用抵扣">
        <el-checkbox v-model="ruleForm.deduction" label="积分" />
        <span class="remark">仅勾选的抵扣方式，可在下单时间时使用</span>
      </el-form-item>
      <el-form-item label="使用优惠">
        <div style="display: flex">
          <el-checkbox-group v-model="ruleForm.applyType">
            <el-checkbox v-for="(item, index) in applyList" :key="index" :value="item.value">
              {{ item.label }}
            </el-checkbox>
          </el-checkbox-group>
          <span class="remark">买家下单时仅可用与勾选的活动类型同时使用</span>
        </div>
      </el-form-item>
    </el-form>
    <div class="pageButtom">
      <el-button @click="router.push({ name: 'FullIndex' })"> 取消 </el-button>
      <el-button type="primary" @click="save"> 保存 </el-button>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.title {
  margin: 10px 0px;
  margin-left: 10px;
  font-weight: bold;
  font-size: 14px;
  margin-bottom: 20px;
}

.remark {
  margin-left: 20px;
  color: #9b9b9b;
}

.pageButtom {
  margin-top: 80px;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
