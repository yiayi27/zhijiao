<script lang="ts" setup>
import type { FormInstance } from 'element-plus'
import { addCoupon, couponDetail, editCoupon } from '@/api/couponActivity/coupon'
import { upLoad } from '@/api/index'
import { mapToRemoveAttrs } from '@/libs/base'
import { Plus } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { ElMessageBox } from 'element-plus'

import { onBeforeRouteLeave, useRoute, useRouter } from 'vue-router'
import ApointGoods from '../../common/ApointGoods.vue'
import AddcouponComp from './AddcouponComp.vue'

defineOptions({
  name: 'AddCouponActivity',
})

// 定义类型
interface CouponDateValue {
  startTime: string
  endTime: string
  effectiveDays: number
}

interface CouponFormData {
  name: string
  useDateValue: CouponDateValue
  baseImg: string
  type: any[]
}

interface CouponContentItem {
  status?: number
  id?: string
  name?: string
  startTime?: string
  endTime?: string
  effectiveDays?: number
  recycleImg?: string
  recycleActivity?: number
}

// 路由和组件引用
const router = useRouter()
const route = useRoute()
const fromRef = ref<FormInstance>()
const addComp = ref()
const apointComp = ref()

// 常量
const actType = 'AddActivity'

// 响应式数据
/** 优惠券使用时间 */
const timeRadio = ref('setTime')

/** 禁用状态 */
const disType = ref(false)

/** 商品选择类型 */
const goodType = ref(0)

/** 是否参与回收返券 */
const recycleActivity = ref(1)

/** 返券图片 */
const recycleImg = ref('https://oss-tencent.bgniao.cn/api/recycle_discount_coupon.png')

const ruleForm = reactive<CouponFormData>({
  name: '',
  useDateValue: {
    startTime: '',
    endTime: '',
    effectiveDays: 1,
  },
  baseImg: '',
  type: [],
})

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

const rules = {
  name: [{ required: true, message: '请输入优惠券名称', trigger: 'blur' }],
  useDateValue: [
    {
      validator: validatePass,
      required: true,
      trigger: 'blur',
    },
  ],
  actRules: [{ required: true, trigger: 'blur' }],
}

/** 退出弹窗状态 */
const saveFlag = ref(false)

/** 优惠券信息 */
const couponContent = ref<CouponContentItem>({})

// 表单验证规则
function validatePass(rule: unknown, value: CouponDateValue, callback: (error?: Error) => void) {
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
        if (couponContent.value.status !== 1) {
          callback(new Error('开始时间不能小于当前时间'))
        }
      }
    }
  } else {
    const regs = /^\+?(\d+)$/
    if (!regs.test(String(value.effectiveDays))) {
      callback(new Error('有效期请输入整数'))
    } else if (Number(value.effectiveDays) <= 0) {
      callback(new Error('有效期不能小于1'))
    } else {
      callback()
    }
  }
  callback()
}

// 生命周期钩子
onMounted(() => {
  if (route.query.id) {
    getCouponDetail()
  }
})

// 路由离开钩子
onBeforeRouteLeave((to, from, next) => {
  if (saveFlag.value) {
    next()
    return
  }
  ElMessageBox.confirm(`确定要退出${route.query.id ? '编辑' : '发布'}优惠券活动?退出后，未保存的信息将不会保留!`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    addComp.value.clearValidate()
    next()
  })
})

/**
 * 优惠券详情
 */
function getCouponDetail() {
  const id = route.query.id as string
  if (!id) return

  couponDetail(id, {}).then((res) => {
    const result = res.data.couponVo
    const productVos = res.data.discountProductVos
    disType.value = result.status === 1
    ruleForm.name = result.name
    timeRadio.value = result.startTime ? 'setTime' : 'immediate'
    ruleForm.useDateValue.effectiveDays = result.effectiveDays
    if (result.startTime) {
      ruleForm.useDateValue.startTime = result.startTime
      ruleForm.useDateValue.endTime = result.endTime
    }
    couponContent.value = { ...result, productVos }
    recycleImg.value = result.recycleImg || recycleImg.value
    recycleActivity.value = result.recycleActivity === 1 ? 2 : 1
  })
}

/**
 * 选择时间类型
 */
function chooseTimeType() {
  // 共用一个字段 避免时间格式问题 先清空操作
  ruleForm.useDateValue = {
    startTime: '',
    endTime: '',
    effectiveDays: 1,
  }
}

/**
 * 商品选择类型
 */
function changeModeList(e: any) {
  if (disType.value && e === 2) {
    recycleActivity.value = 1
    ElMessage.error("已发布优惠券不能修改为'参与回收返券'")
  } else {
    recycleActivity.value = e
  }
}

interface UploadFile {
  raw: File
  size: number
}

async function uploadProductImg(file: any) {
  recycleImg.value = await upLoadFile(file)
}

async function upLoadFile(file: UploadFile): Promise<string> {
  const whiteList = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif']
  const isLt2M = file.size < 2 * 1024 * 1024
  if (!whiteList.includes(file.raw.type)) {
    ElMessage.error('上传文件只能是 GIF、JPG或PNG 格式!')
    return recycleImg.value
  }
  if (!isLt2M) {
    ElMessage.error('上传文件大小不能超过 2MB!')
    return recycleImg.value
  }
  const res = await upLoad({
    file: file.raw,
  })
  return res.data
}

/**
 * 验证表单
 */
function formValid() {
  return new Promise((resolve) => {
    ;(fromRef.value as FormInstance).validate((valid) => {
      resolve(valid)
    })
  })
}

interface ProductItem {
  id: string
  [key: string]: any
}

/**
 * 保存发布优惠券
 */
async function save() {
  const valid = await formValid()
  const checkValid = checkSecond()

  if (!valid || !checkValid) {
    return
  }

  mapToRemoveAttrs(apointComp.value.pointGoodsList, [
    {
      key: 'id',
      value: (item: ProductItem) => {
        return { productId: item.id }
      },
    },
  ])

  const param = {
    // 基本信息
    id: route.query.id ? (couponContent.value as any).id : '',
    name: ruleForm.name,
    giveType: 0,
    ...(timeRadio.value === 'immediate'
      ? {
          effectiveDays: ruleForm.useDateValue.effectiveDays,
          startTime: '',
          endTime: '',
          effectiveType: 1,
        }
      : {
          effectiveDays: 1,
          startTime: dayjs(ruleForm.useDateValue.startTime).startOf('day').format('YYYY-MM-DD HH:mm:ss'),
          endTime: dayjs(ruleForm.useDateValue.endTime).endOf('day').format('YYYY-MM-DD HH:mm:ss'),
          effectiveType: 0,
        }),
    productType: apointComp.value.goodRadio,
    couponProducts: mapToRemoveAttrs(apointComp.value.pointGoodsList, [{ key: 'id', value: (item: ProductItem) => ({ productId: item.id }) }]),
    recycleActivity: recycleActivity.value === 1 ? 0 : 1,
    recycleImg: recycleImg.value,
    ...addComp.value.couponList[0],
  }
  await getRuleModel()

  if (route.query.id) {
    edit(param)
    return
  }

  if (recycleActivity.value === 2 && addComp.value.couponList[0].couponType !== 0) {
    ElMessage.error('只有无门槛折扣券参与回收返券功能，请重新编辑')
    return
  }

  await addCoupon(param).then(() => {
    saveFlag.value = true
    ElMessage.success('发布成功')
    router.push({ name: 'CouponIndex' })
  })
}

async function getRuleModel() {
  try {
    await addComp.value.validate()
  } catch (error) {
    return Promise.reject(error)
  }
}

/**
 * 二次验证条件
 */
function checkSecond() {
  if (ruleForm.name === '') {
    ElMessage.error('请输入优惠券名称')
    return false
  }

  if (timeRadio.value === 'setTime') {
    const start = dayjs(ruleForm.useDateValue.startTime).valueOf()
    const end = dayjs(ruleForm.useDateValue.endTime).valueOf()
    const now = dayjs().startOf('day').valueOf()

    if (couponContent.value.status === 1) {
      if (start > end) {
        return false
      }
    } else if (start > end || start < now) {
      return false
    }
  } else if (!Number(ruleForm.useDateValue.effectiveDays)) {
    ElMessage.error('请输入大于0的有效时间')
    return false
  }

  if (apointComp.value.goodRadio !== 0 && apointComp.value.pointGoodsList.length === 0) {
    ElMessage.error('请选择指定商品')
    return false
  }
  return true
}

/**
 * 编辑优惠券
 */
function edit(param: Record<string, any>) {
  editCoupon(param).then((res) => {
    if (res.code === 200) {
      saveFlag.value = true
      ElMessage.success('编辑成功')
      router.push({ name: 'CouponIndex' })
    }
  })
}
</script>

<template>
  <div>
    <div class="title">基本信息</div>
    <el-form ref="fromRef" :model="ruleForm" :rules="rules" class="demo-ruleForm" label-position="left" label-width="120">
      <el-form-item label="优惠券名称" prop="name">
        <el-input v-model="ruleForm.name" label-width="80px" maxlength="5" placeholder="请输入优惠券名称" style="width: 460px" />
        <span class="remark">优惠券名称不超过5个字</span>
      </el-form-item>
      <el-form-item label="有效时间" prop="useDateValue">
        <el-radio v-model="timeRadio" :disabled="disType" value="setTime" @change="chooseTimeType"> 固定时间 </el-radio>
        <el-radio v-model="timeRadio" :disabled="disType" value="immediate" @change="chooseTimeType"> 领券立即生效 </el-radio>
        <div>
          <span v-if="timeRadio === 'setTime'">
            <el-date-picker
              v-model="ruleForm.useDateValue.startTime"
              :disabled="disType"
              :disabled-date="startTimeDisabledDate"
              placeholder="开始日期"
              style="width: 220px; margin-left: 15px"
              type="date"
              value-format="YYYY-MM-DD"
            />
            <span style="margin-left: 10px; color: #ccc">至</span>
            <el-date-picker
              v-model="ruleForm.useDateValue.endTime"
              :disabled-date="endTimeDisabledDate"
              placeholder="结束日期"
              style="width: 220px; margin-left: 10px"
              type="date"
              value-format="YYYY-MM-DD"
            />
          </span>
          <span v-else>
            领券当日起
            <el-input-number
              v-model="ruleForm.useDateValue.effectiveDays"
              :controls="false"
              :disabled="disType"
              :min="1"
              :step="1"
              step-strictly
              style="width: 80px; margin: 0 10px"
            />天内可用
          </span>
        </div>
      </el-form-item>
      <el-form-item label="活动规则">
        <AddcouponComp ref="addComp" :coupon-content="couponContent" :good-type="goodType" />
      </el-form-item>
      <el-form-item label="商品选择">
        <ApointGoods ref="apointComp" :act-type="actType" :content="couponContent" />
      </el-form-item>
      <el-form-item label="规则配置">
        <el-radio-group v-model="recycleActivity" @change="changeModeList">
          <el-radio :value="1"> 不参与回收返券 </el-radio>
          <el-radio :value="2"> 参与回收返券 </el-radio>
        </el-radio-group>
        <div v-if="recycleActivity === 2" class="couponUpload">
          <div class="couponUpload__baseUpload">
            <el-upload :auto-upload="false" :on-change="uploadProductImg" :show-file-list="false" class="couponUpload__baseUpload--inUpload">
              <img v-if="recycleImg" :src="recycleImg" style="width: 320px; height: 400px" />
              <el-icon v-else>
                <Plus />
              </el-icon>
            </el-upload>
            <div class="position1">发券字段位置</div>
            <div class="position2">按钮位置</div>
          </div>

          <div class="couponUploadTip">
            <div class="couponUploadTip__tip" style="height: 40px; margin-top: 20px">
              <el-upload :auto-upload="false" :on-change="uploadProductImg" :show-file-list="false">
                <p>上传样式</p>
              </el-upload>
              <div>建议尺寸：750 * 1350px</div>
            </div>
            <div class="couponUploadTip__tipTwo">设计图片上传时，请将点击按钮尽量对其按钮位置，并在字段位置留空</div>
          </div>
        </div>
      </el-form-item>
    </el-form>

    <div class="pageButtom">
      <el-button @click="router.push({ name: 'CouponIndex' })"> 取消 </el-button>
      <el-button type="primary" @click="save"> 保存 </el-button>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.title {
  margin: 10px 0px;
  font-weight: bold;
  font-size: 14px;
  margin-bottom: 20px;
  margin-left: 10px;
}

.demo-ruleForm {
  margin-left: 15px;
}

.remark {
  margin-left: 20px;
  color: #9b9b9b;
}

:deep(.el-radio) {
  margin-right: 20px;
}

.pageButtom {
  margin-top: 80px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.couponUpload {
  display: flex;

  &__baseUpload {
    position: relative;
    border: 2px dashed #e6e6e6;
    width: 330px;
    height: 400px;
    border-radius: 10px;
    padding: 10px;
    margin-left: 20px;
    margin-top: 40px;
    display: flex;

    &--inUpload {
      display: flex;
      justify-content: center;
      align-items: center;
      height: 100%;
      width: 100%;
    }
  }
}

.position1 {
  position: absolute;
  display: flex;
  justify-content: center;
  width: 80%;
  background-color: rgba(0, 0, 0, 0.8);
  color: white;
  top: 80px;
  left: 34px;
}

.position2 {
  position: absolute;
  display: flex;
  justify-content: center;
  align-items: center;
  width: 80%;
  height: 50px;
  background-color: rgba(0, 0, 0, 0.8);
  color: white;
  bottom: 40px;
  left: 34px;
}

.couponUploadTip {
  display: flex;
  flex-wrap: wrap;
  height: 60px;
  margin-top: 50px;

  &__tip {
    display: flex;
    margin: 80px 0px 0px 40px;

    p {
      color: #2e99f3;
      font-size: 14px;
      margin-right: 30px;
    }

    div {
      color: #cfcfcf;
      font-size: 14px;
    }
  }

  &__tipTwo {
    color: #ff3b4b;
    font-size: 14px;
    margin-left: 40px;
  }
}
</style>
