<script setup lang="ts">
import { addActivity, editActivityById, getActivityById } from '@/api/chainActivity/chainActivity'
import { upLoad } from '@/api/index'
import { advanceGetImage, readFile } from '@/libs/voidImg'
import { Plus } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { ElMessageBox } from 'element-plus'

import { onBeforeRouteLeave, useRoute, useRouter } from 'vue-router'
import ApointGoods from '../common/ApointGoods.vue'

defineOptions({
  name: 'AddChainActivity',
})

const router = useRouter()
const route = useRoute()

// refs
const fromRef = ref()
const apointComp = ref()

// data
const actType = 'AddChainActivity'

const ruleForm = reactive({
  name: '',
  useDateValue: {
    startTime: '',
    endTime: '',
  },
  preTime: 0,
  img: '',
  desc: '',
  status: 0,
  id: '',
})

/** 编辑室获取到接龙详情 */
const activityContent = ref({})

/** 开始时间是否可选 */
const isChecked = ref(false)

/** 是否有退出提示 */
const saveFlag = ref(false)

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

// 表单验证规则
const rules = {
  name: [{ required: true, message: '请输入活动名称', trigger: 'blur' }],
  useDateValue: [
    {
      validator: validatePass,
      required: true,
      trigger: 'blur',
    },
  ],
  preTime: [{ required: true, message: '请输入预热时间', trigger: 'blur' }],
  img: [{ required: true, message: '请上传封面海报', trigger: 'blur' }],
  desc: [{ required: true, message: '活动描述不能为空', trigger: 'blur' }],
}

// 生命周期函数
onMounted(() => {
  if (route.query.activityId) {
    getDeatil(route.query.activityId as string)
  }
})

// 路由离开钩子
onBeforeRouteLeave((to, from, next) => {
  if (saveFlag.value) {
    next()
    return
  }
  ElMessageBox.confirm(`确定要退出${route.query.activityId ? '编辑' : '发布'}接龙活动?退出后，未保存的信息将不会保留!`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    next()
  })
})

// 方法定义
function validatePass(_rule: unknown, value: any, callback: (error?: Error) => void) {
  const start = dayjs(value.startTime).valueOf()
  const end = dayjs(value.endTime).valueOf()
  const now = dayjs().startOf('day').valueOf()
  if (!value.endTime || !value.startTime) {
    callback(new Error('请输入活动时间'))
  } else {
    if (start > end) {
      callback(new Error('结束时间不能小于开始时间'))
    }
    if (start < now) {
      if (!route.query.activityId) {
        callback(new Error('开始时间不能小于当前时间'))
      }
    }
  }
  callback()
}

/**
 * 编辑时获取数据
 */
function getDeatil(id: string) {
  getActivityById(id, {}).then((res) => {
    ruleForm.name = res.data.solitaireActivityVo.name
    ruleForm.preTime = res.data.solitaireActivityVo.preheatHour
    ruleForm.img = res.data.solitaireActivityVo.coverPoster
    ruleForm.desc = res.data.solitaireActivityVo.content
    ruleForm.status = res.data.solitaireActivityVo.status
    isChecked.value = res.data.solitaireActivityVo.status === 1
    ruleForm.id = res.data.solitaireActivityVo.id
    ruleForm.useDateValue.startTime = res.data.solitaireActivityVo.startTime
    ruleForm.useDateValue.endTime = res.data.solitaireActivityVo.endTime

    activityContent.value = res.data
  })
}

/**
 * 上传海报
 */
async function addUploadBigProductImg(file: any) {
  const whiteList = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif']

  const isLt1M = file.size < 1 * 1024 * 1024
  if (!whiteList.includes(file.raw.type)) {
    ElMessage.error('上传文件只能是 JPG或PNG 格式!')
    return
  }
  if (!isLt1M) {
    ElMessage.error('上传文件大小不能超过 1MB!')
    return
  }

  const imgFile = await readFile(file.raw)
  const newImg = await advanceGetImage(imgFile)

  if (newImg.image.width !== 750 || newImg.image.height !== 375) {
    ElMessage.error('海报图片长宽应为750 * 375')
    return
  }

  const res = await upLoad({
    file: file.raw,
  })
  ruleForm.img = res.data
}

async function save() {
  await formValid()

  const check = checkSecond()
  if (!check) {
    return
  }

  let list: any[] = []
  if (apointComp.value.goodRadio === 1) {
    list = apointComp.value.joinList
  } else if (apointComp.value.goodRadio === 2) {
    list = apointComp.value.unJoinList
  }

  if (list.length > 0) {
    list.forEach((item: any) => {
      item.productId = item.id
    })
  }

  if (apointComp.value.goodRadio === 1 || apointComp.value.goodRadio === 2) {
    if (list.length === 0) {
      ElMessage.error('请选择商品')
      return
    }
  }

  const param = {
    content: ruleForm.desc,
    coverPoster: ruleForm.img,
    name: ruleForm.name,
    preheatHour: ruleForm.preTime,
    productType: apointComp.value.goodRadio,
    id: ruleForm.id,
    activityProducts: list,
    startTime: ruleForm.useDateValue.startTime,
    endTime: ruleForm.useDateValue.endTime,
    status: ruleForm.status,
  }

  if (route.query.activityId && param.status !== 2) {
    editActivity(param)
    return
  }

  await addActivity({ ...param, id: param.status === 2 ? null : param.id })
  ElMessage.success('发布成功')
  saveFlag.value = true
  router.push({ name: 'ChainIndex' })
}

async function formValid() {
  return await fromRef.value.validate()
}

function checkSecond() {
  const start = dayjs(ruleForm.useDateValue.startTime)
  const end = dayjs(ruleForm.useDateValue.endTime)

  if (end.isAfter(start.add(1, 'month'))) {
    ElMessage.error('活动时间最多持续一个月')
    return false
  }
  if (start > end) {
    return false
  }
  if (!ruleForm.name) {
    return false
  }
  const preTimeReg = /^\+?(\d+)$/
  if (!preTimeReg.test(String(ruleForm.preTime)) || Number(ruleForm.preTime) < 0 || Number(ruleForm.preTime) > 24) {
    ElMessage.error('预热时间需为0-24的整数')
    return false
  }
  if (!ruleForm.img) {
    return false
  }
  if (!ruleForm.desc) {
    return false
  }
  return true
}

function editActivity(param: any) {
  editActivityById(param).then(() => {
    ElMessage.success('编辑成功')
    saveFlag.value = true
    router.push({ name: 'ChainIndex' })
  })
}

/**
 * 设置 预热时间
 */
function shippingKeydown(e: any) {
  if (+e < 0) {
    ruleForm.preTime = 0
  } else {
    ruleForm.preTime = +e
  }
}

/**
 * 取消操作
 */
function cancel() {
  saveFlag.value = false
  router.push({ name: 'ChainIndex' })
}

// 导出组件方法和数据
defineExpose({
  save,
  cancel,
  formValid,
  shippingKeydown,
  ruleForm,
})
</script>

<template>
  <div>
    <div class="title">{{ route.query.activityId ? '编辑' : '新增' }}活动</div>
    <el-form ref="fromRef" :model="ruleForm" :rules="rules" label-width="100px" class="demo-ruleForm">
      <el-form-item label="活动名称" prop="name">
        <el-input v-model="ruleForm.name" maxlength="5" show-word-limit style="width: 320px" />
        <span class="remark">(活动名称不超过5个字)</span>
      </el-form-item>
      <el-form-item label="有效时间" prop="useDateValue" style="margin-top: 30px">
        <el-date-picker
          v-model="ruleForm.useDateValue.startTime"
          type="datetime"
          placeholder="开始日期"
          style="width: 220px"
          :disabled="isChecked"
          value-format="YYYY-MM-DD HH:mm:ss"
          :disabled-date="startTimeDisabledDate"
        />
        <span style="margin-left: 10px; color: #ccc">至</span>
        <el-date-picker
          v-model="ruleForm.useDateValue.endTime"
          type="datetime"
          placeholder="结束日期"
          value-format="YYYY-MM-DD HH:mm:ss"
          :disabled-date="endTimeDisabledDate"
          style="width: 220px; margin-left: 10px"
        />
      </el-form-item>
      <el-form-item label="预热时间" prop="preTime" style="margin-top: 30px">
        <el-input-number v-model="ruleForm.preTime" :min="0" step-strictly :step="1" :controls="false" style="width: 180px" @input="shippingKeydown">
          <template #append> 小时 </template>
        </el-input-number>
        <span class="remark">输入值大于0时该活动在商详页展示活动信息，0为不预热，预热时间最多24小时</span>
      </el-form-item>
      <el-form-item label="商品选择" prop="actRules" style="margin-top: 30px">
        <ApointGoods ref="apointComp" :content="activityContent" :act-type="actType" />
      </el-form-item>
      <el-form-item label="封面海报" prop="img" style="margin-top: 30px">
        <div class="product" style="display: flex">
          <el-upload
            class="productImg__uploader product__productImg"
            :auto-upload="false"
            :on-change="addUploadBigProductImg"
            action=""
            :show-file-list="false"
            style="width: 250px; height: 120px"
          >
            <img v-if="ruleForm.img" :src="ruleForm.img" class="product__productImg" style="width: 250px; height: 120px" />
            <el-icon v-if="!ruleForm.img" class="product__productImg--uploader--icon" style="width: 250px; line-height: 100px">
              <Plus />
            </el-icon>

            <div v-if="!ruleForm.img" class="product__productImg--uploader--add" style="top: 70px; left: 100px">添加图片</div>
          </el-upload>
          <div class="text-info fs-12" style="color: rgba(69, 64, 60, 0.6); display: flex; align-items: flex-end; margin-left: 20px">
            (尺寸建议750x375（长方形模式）像素以上，大小1M以下,图片格式支持 png,jpg,jpeg,gif)
          </div>
        </div>
      </el-form-item>
      <el-form-item label="活动描述" prop="desc" style="margin-top: 30px">
        <el-input v-model="ruleForm.desc" maxlength="15" show-word-limit style="width: 320px" />
        <span class="remark">(活动描述最多15个字)</span>
      </el-form-item>
    </el-form>
    <div class="pageButtom">
      <el-button @click="cancel"> 取消 </el-button>
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

.remark {
  margin-left: 20px;
  color: #9b9b9b;
}

.productImg__uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.productImg__uploader:hover {
  border-color: #409eff;
}

.product__productImg {
  width: 100px;
  height: 100px;
  display: inline-block;
}

.product__productImg--uploader--icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  line-height: 80px;
  text-align: center;
}

.product__productImg--uploader--add {
  position: absolute;
  top: 55px;
  left: 20px;
  color: #7f7f7f;
}

.pageButtom {
  margin-top: 80px;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
