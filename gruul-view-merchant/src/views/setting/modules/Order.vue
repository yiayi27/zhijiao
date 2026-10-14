<script setup lang="ts">
import { getOrderManageSetting, setOrderManageSetting } from '@/api/order/index'
import BaseTable from '@/components/BaseTable/index.vue'
import { useGlobalStore } from '@/store/modules/global'

import dayjs from 'dayjs'
import { cloneDeep } from 'lodash-es'

// 定义表单项类型
interface CustomFormItem {
  key: string
  type: string
  required: boolean
  placeholder: string
  [key: string]: any
}

const globalStore = useGlobalStore()

// 表单引用
const formRef = ref()
const coustomFormRef = ref()

// 数据
const coustomForm = reactive<CustomFormItem>({
  key: '',
  type: '',
  required: false,
  placeholder: '请输入',
})

const coustomRules = {
  key: [
    {
      required: true,
      message: '表单名称不可为空',
      trigger: 'change',
    },
  ],
  type: [
    {
      required: true,
      message: '表单类型不可为空',
      trigger: 'change',
    },
  ],
  placeholder: [
    {
      required: true,
      message: '表单提示语不可为空',
      trigger: 'change',
    },
  ],
}

const currentForm = ref<CustomFormItem | null>(null)
const currentIndex = ref(0)
const dialogVisible = ref(false)

const formOptions = [
  {
    label: '电话',
    value: 'phone',
  },
  {
    label: '身份证',
    value: 'id',
  },
  {
    label: '文本',
    value: 'text',
  },
  {
    label: '数字',
    value: 'number',
  },
  {
    label: '图片',
    value: 'image',
  },
  {
    label: '日期',
    value: 'date',
  },
  {
    label: '时间',
    value: 'time',
  },
  {
    label: '日期时间',
    value: 'datetime',
  },
]

const time = reactive({
  day: 0,
  hour: 0,
  min: 15,
})

const form = reactive({
  ceiling: 0,
  customFrom: '',
  id: 0,
  kdHundredSecret: '',
  kdHundredKey: '',
  kdHundredCustomer: '',
  orderNotify: true,
  paymentModel: [1],
  ratio: 0,
  shopId: '',
  userReturnOvertime: 0,
  merchantConfirmOvertime: 0,
  type: '',
  // 关闭时间
  normalOrderOvertime: 15,
  finishOvertime: 0,
  confirmOvertime: 0,
})

const rules = {
  userReturnOvertime: [
    {
      required: true,
      message: '申请售后时间不可为空',
      trigger: 'change',
    },
  ],
  merchantConfirmOvertime: [{ required: true, message: '待审核时间不可为空', trigger: 'change' }],
  settlementDay: [{ required: true, message: '佣金奖励时间不可为空', trigger: 'change' }],
  paymentModel: [
    {
      required: true,
      message: '至少选择一种支付方式',
      trigger: 'change',
    },
  ],
  finishOvertime: [
    {
      required: true,
      message: '不可为空',
      trigger: 'change',
    },
    {
      required: true,
      max: 30,
      message: '不可大于30',
      trigger: 'change',
    },
  ],
  confirmOvertime: [
    {
      required: true,
      message: '不可为空',
      trigger: 'change',
    },
    {
      required: true,
      max: 30,
      message: '不可大于30',
      trigger: 'change',
    },
  ],
}

const forms = ref<CustomFormItem[]>([])

// 监听对话框状态
watch(dialogVisible, (v) => {
  if (!v) {
    currentForm.value = null
    resetCoustomForm()
  }
})

// 生命周期钩子
onMounted(() => {
  getOrderManageSetting().then((res) => {
    res.data.paymentModel = res.data.paymentModel.split(',')
    forms.value = JSON.parse(res.data.customFrom)
    handleNormalOrderOvertime(res.data.normalOrderOvertime)
    Object.assign(form, res.data)
  })
})

// 方法
function verification() {
  const rules: Record<string, string> = {
    userReturnOvertime: '申请售后时间不可为空',
    merchantConfirmOvertime: '待审核时间不可为空',
  }
  const item = Object.keys(form).find((key) => {
    if (rules[key] && !form[key as keyof typeof form] && form[key as keyof typeof form] !== 0) {
      ElMessage.warning(rules[key])
      return true
    }
    return false
  })
  return item
}

function timeUpdate() {
  const { day, hour, min } = time
  form.normalOrderOvertime = dayjs
    .duration({
      days: day || 0,
      hours: hour || 0,
      minutes: min || 0,
    })
    .asMinutes()
}

/** 处理过期时间 */
function handleNormalOrderOvertime(timeValue: number) {
  Object.assign(time, diffTime(0, timeValue))
}

function diffTime(startDate: number, endDate: number) {
  const startDateObj = dayjs(startDate * 60 * 1000)
  const endDateObj = dayjs(endDate * 60 * 1000)

  // 计算时间差
  const diff = endDateObj.diff(startDateObj)
  const durationObj = dayjs.duration(diff)

  // 获取天、小时、分钟
  const day = Math.floor(durationObj.asDays())
  const hour = durationObj.hours()
  const min = durationObj.minutes()

  return {
    day: day || 0,
    hour: hour || 0,
    min: min || 0,
  }
}

function getFormName(key: string) {
  const data = formOptions.find((item) => item.value === key)
  return data && data.label
}

function resetCoustomForm() {
  Object.assign(coustomForm, {
    key: '',
    type: '',
    required: false,
    placeholder: '请输入',
  })
}

function toggleDialogVisible() {
  dialogVisible.value = !dialogVisible.value
}

function setOrderSetting() {
  if (verification()) return

  const gd = ((day) => (day > 30 ? 30 : day) * 24 * 60)(30)
  if (gd <= form.normalOrderOvertime) {
    return ElMessage.info('超时时间不可大于30天')
  }
  if (form.normalOrderOvertime < 15) {
    return ElMessage.info('超时时间不可小于15分钟')
  }

  formRef.value.validate((valid: boolean) => {
    if (!valid) {
      return ElMessage.info('请填写正确的表单')
    }

    setOrderManageSetting({
      ...form,
      paymentModel: form.paymentModel.join(','),
      customFrom: JSON.stringify(forms.value),
    }).then(() => {
      ElMessage.success('保存成功')
      globalStore.getShopSetting()
    })
  })
}

function onConfirm() {
  coustomFormRef.value.validate((valid: boolean) => {
    if (!valid) {
      return ElMessage.info('请填写正确的表单')
    }
    if (!currentForm.value) {
      forms.value.push(cloneDeep(coustomForm))
    } else {
      forms.value[currentIndex.value] = cloneDeep(coustomForm)
    }

    toggleDialogVisible()
  })
}

function handleDel(item: any, i: number) {
  forms.value.splice(i, 1)
}

function handleEdit(item: any, i: number) {
  currentForm.value = cloneDeep(item)
  Object.assign(coustomForm, currentForm.value)
  currentIndex.value = i
  toggleDialogVisible()
}
</script>

<template>
  <div class="order order__setting--form">
    <div class="order__tip">
      <div class="order__tip--lump" style="background: #08cc00" />
      <span class="order__tip--title">下单设置</span>
    </div>
    <el-form ref="formRef" :rules="rules" :model="form" label-width="120px">
      <el-form-item label="订单播报" required>
        <el-radio-group v-model="form.orderNotify">
          <el-radio :value="true"> 开启 </el-radio>
          <el-radio :value="false"> 关闭 </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="下单表单" required>
        <div class="w-full pr-[20px]">
          <BaseTable :data="forms" style="width: 100%" border>
            <el-table-column prop="key" label="表单名称" />
            <el-table-column prop="type" label="表单格式">
              <template #default="scope">
                {{ getFormName(scope.row.type) }}
              </template>
            </el-table-column>
            <el-table-column label="必填" prop="required">
              <template #default="scope">
                {{ scope.row.required ? '是' : '否' }}
              </template>
            </el-table-column>
            <el-table-column label="提示语" prop="placeholder" />
            <el-table-column label="操作">
              <template #default="scope">
                <el-button link @click="handleEdit(scope.row, scope.$index)"> 编辑 </el-button>

                <el-button link :disabled="forms.length === 1" @click="handleDel(scope.row, scope.$index)"> 删除 </el-button>
              </template>
            </el-table-column>
          </BaseTable>

          <el-button class="mt-[10px]" link @click="toggleDialogVisible"> 添加 </el-button>
        </div>
      </el-form-item>
    </el-form>

    <div class="order__tip">
      <div class="order__tip--lump" style="background: #08cc00" />
      <span class="order__tip--title">订单设置</span>
    </div>
    <el-form ref="formRef" :rules="rules" :model="form" label-width="120px" class="order__setting--form">
      <el-form-item label="未支付订单" prop="normalOrderOvertime">
        <el-row>
          <el-col :span="12">
            <el-input-number v-model="time.day" class="input--item" :min="0" :max="30" :controls="false" placeholder="天" @change="timeUpdate" />
            天
            <el-input-number v-model="time.hour" class="input--item" :min="0" :max="12" :controls="false" placeholder="小时" @change="timeUpdate" />
            小时
            <el-input-number v-model="time.min" class="input--item" :min="0" :max="60" :controls="false" placeholder="分钟" @change="timeUpdate" />
            分钟后自动关闭
          </el-col>
          <el-col :span="12" class="text-[12px] text-gray-400"> 最长可设置30天，最短可设置15分钟。 </el-col>
        </el-row>
      </el-form-item>
      <el-form-item label="已发货订单" prop="confirmOvertime">
        <el-row>
          <el-col :span="12">
            <el-input-number v-model="form.confirmOvertime" :min="0" step-strictly :step="1" :controls="false" placeholder="请输入已发货订单" />
            天后自动确认收货
          </el-col>
          <el-col :span="12" class="text-[12px] text-gray-400">
            <span>请考虑物流运输时间，最长可设置30天，0代表发货后系统自动确认收货</span>
          </el-col>
        </el-row>
      </el-form-item>

      <el-form-item label="已完成订单" prop="finishOvertime">
        <el-row>
          <el-col :span="12">
            <el-input-number v-model="form.finishOvertime" :min="0" step-strictly :step="1" :controls="false" placeholder="请输入已完成订单" />
            天后自动关闭售后
          </el-col>

          <el-col :span="12" class="text-[12px] text-gray-400">
            <span>填写则买家可在确认收货后进行售后申请，最长可设置30天，0代表确认收货后无法申请售后。</span>
          </el-col>
        </el-row>
      </el-form-item>
    </el-form>

    <div class="order__tip">
      <div class="order__tip--lump" style="background: #ff0200" />
      <span class="order__tip--title">售后设置</span>
    </div>
    <el-form ref="formRef" :rules="rules" :model="form" label-width="120px">
      <el-form-item label="申请售后时间" prop="userReturnOvertime">
        <el-row>
          <el-col :span="12">
            <el-input-number v-model="form.userReturnOvertime" :min="0" :step="1" step-strictly :controls="false" placeholder="请输入申请售后时间" />
            天
          </el-col>
          <el-col :span="12" class="text-[12px] text-gray-400"> 申请售后的时间。 </el-col>
        </el-row>
      </el-form-item>
      <el-form-item label="售后审核时间" prop="merchantConfirmOvertime">
        <el-row>
          <el-col :span="12">
            <el-input-number
              v-model="form.merchantConfirmOvertime"
              :step="1"
              :min="1"
              step-strictly
              :controls="false"
              placeholder="请输入售后审核时间"
            />
            天
          </el-col>
          <el-col :span="12" class="text-[12px] text-gray-400">
            <span>买家申请售后卖家审核时间，温馨提示：默认是3天，设置后以设置时间为准！</span>
          </el-col>
        </el-row>
      </el-form-item>
    </el-form>
    <div class="order__tip">
      <div class="order__tip--lump" style="background: #2e8cf0" />
      <span class="order__tip--title">快递100 设置</span>
    </div>
    <el-form ref="formRef" :rules="rules" :model="form" label-width="120px">
      <el-form-item label="客户号">
        <el-row>
          <el-col :span="11">
            <el-input v-model="form.kdHundredCustomer" placeholder="请输入客户号" />
          </el-col>
        </el-row>
      </el-form-item>
      <el-form-item label="Key">
        <el-row>
          <el-col :span="11">
            <el-input v-model="form.kdHundredKey" placeholder="请输入Key" />
          </el-col>
        </el-row>
      </el-form-item>
      <el-form-item label="Select">
        <el-row>
          <el-col :span="11">
            <el-input v-model="form.kdHundredSecret" placeholder="请输入Select" />
          </el-col>
        </el-row>
      </el-form-item>
    </el-form>

    <div class="order__tip">
      <div class="order__tip--lump" />
      <span class="order__tip--title">积分设置</span>
    </div>
    <el-form ref="formRef" :rules="rules" :model="form" label-width="120px">
      <el-form-item label="积分抵扣">
        单笔消费积分抵用 = 积分*
        <el-input-number v-model="form.ratio" :min="0" :controls="false" class="w-[80px] mx-[10px]" />
        %（使用额度为单笔订单总额的
        <el-input-number v-model="form.ceiling" :min="0" :controls="false" class="w-[80px] mx-[10px]" />
        %)
      </el-form-item>

      <el-form-item label=" ">
        <el-button type="primary" @click="setOrderSetting"> 保存 </el-button>
      </el-form-item>
    </el-form>
    <el-dialog v-model="dialogVisible" title="自定义表单" width="30%">
      <el-form ref="coustomFormRef" :rules="coustomRules" :model="coustomForm" label-width="80px">
        <el-form-item label="表单名称" prop="key">
          <el-input v-model="coustomForm.key" />
        </el-form-item>
        <el-form-item label="表单格式" prop="type">
          <el-select v-model="coustomForm.type" style="width: 100%" placeholder="请选择">
            <el-option v-for="item in formOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="是否必填" prop="required">
          <el-radio-group v-model="coustomForm.required">
            <el-radio :value="true"> 开启 </el-radio>
            <el-radio :value="false"> 关闭 </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="提示语" prop="placeholder">
          <el-input v-model="coustomForm.placeholder" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="toggleDialogVisible">取 消</el-button>
          <el-button type="primary" @click="onConfirm">确 定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
.order__setting--form {
  font-size: 13px;

  .order__tip {
    vertical-align: center;
    background-color: rgba(246, 248, 250, 1);
    padding: 15px 15px 15px 30px;
    margin-bottom: 30px;

    .order__tip--title {
      margin-left: 12px;
      color: #586884;
      font-weight: 700;
    }
  }

  .el-table--fit {
    border: 1px solid #e4e4e4;
    border-bottom: 0px;
    border-radius: 10px;
    margin-right: 20px;
  }

  .el-table td,
  .el-table th {
    padding: 0 !important;
  }

  .el-table th {
    padding: 0 !important;
    background-color: #f2f4f3 !important;
  }

  .el-table__header-wrapper tr {
    background-color: #f2f4f3 !important;
  }

  .el-form {
    margin-bottom: 40px;
  }
}

.order__tip--lump {
  display: inline-block;
  width: 3px;
  height: 12px;
  background-color: rgba(255, 153, 0, 1);
}

.input--item {
  width: 60px;
  margin: 0 3px;
}

.order__setting--form {
  .el-row {
    width: 100%;
  }
}
</style>
