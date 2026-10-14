<script setup lang="ts">
import AreaSelect from './AreaSelect.vue'

interface ShippingModelDTO {
  region: any[]
  area: string
  id: string
  logisticsId: string
  firstPiece: number // 首件
  firstWeight: number // 首重
  firstAmount: number // 首费
  secondPiece: number // 续件
  secondWeight: number // 续重
  secondAmount: number // 续费
  valuationModel: string // 计价方式
}

interface IncludePostageDTO {
  id: string
  area: string
  region: any[]
  value: string
  pieceNum: number // 包邮件数
  amountNum: number // 包邮金额
  weight: number // 包邮重量
  type: string // 0= 按件数包邮,1=按重量包邮,2=按金额包邮,3=件数+金额 4=重量+金额
}

interface FormData {
  id: string
  name: string
  valuationModel: string
  choiceConditionPostage: boolean
  logisticsShippingModelDtos: ShippingModelDTO[]
  logisticsIncludePostageDtos: IncludePostageDTO[]
}

const props = defineProps({
  editCurrent: {
    type: Object,
    default: () => ({}),
  },
  logDialog: {
    type: Boolean,
    default: false,
  },
})

const areaRef = ref<any[]>([])
const freeAreaRef = ref<any[]>([])

/** 点击的数组定位 */
const modelIndex = ref(0)

/** 点击时所已经选择的地址 */
const modelRegion = ref<any[]>([])

/** 是否点击包邮 */
const freeArea = ref(false)

/** 表单数据 */
const modelValidateForm = reactive<FormData>({
  id: '',
  name: '',
  valuationModel: '1',
  /** 是否选择包邮区域 */
  choiceConditionPostage: false,
  logisticsShippingModelDtos: [
    {
      region: [],
      area: '',
      id: '',
      logisticsId: '',
      firstPiece: 0, // 首件
      firstWeight: 0, // 首重
      firstAmount: 0, // 首费
      secondPiece: 0, // 续件
      secondWeight: 0, // 续重
      secondAmount: 0, // 续费
      valuationModel: '', // 计价方式
    },
  ],

  logisticsIncludePostageDtos: [
    {
      id: '',
      area: '',
      region: [],
      value: '1',
      pieceNum: 0, // 包邮件数
      amountNum: 0, // 包邮金额
      weight: 0, // 包邮重量
      type: '', // 0= 按件数包邮,1=按重量包邮,2=按金额包邮,3=件数+金额 4=重量+金额
    },
  ],
})

/** 包邮选项 */
const selectOptions = ref([
  {
    value: '1',
    label: '件数',
    show: false,
  },
  {
    value: '2',
    label: '金额',
  },
  {
    value: '3',
    label: '件数+金额',
  },
])

/** 地址选择弹窗 */
const areaDialog = ref(false)

const areaRules = {
  firstPiece: [
    { required: true, message: '首件不能为空', trigger: 'blur' },
    { validator: pieceVlidator, trigger: 'blur' },
  ],
  firstWeight: [
    { required: true, message: '首重不能为空', trigger: 'blur' },
    { validator: weightVlidator, trigger: 'blur' },
  ],
  firstAmount: [
    { required: true, message: '首费不能为空', trigger: 'blur' },
    { validator: amountVlidator, trigger: 'blur' },
  ],
  secondPiece: [
    { required: true, message: '续件不能为空', trigger: 'blur' },
    { validator: pieceVlidator, trigger: 'blur' },
  ],
  secondWeight: [
    { required: true, message: '续重不能为空', trigger: 'blur' },
    { validator: weightVlidator, trigger: 'blur' },
  ],
  secondAmount: [
    { required: true, message: '续费不能为空', trigger: 'blur' },
    { validator: amountVlidator, trigger: 'blur' },
  ],
}

const freeAreaRules = {
  pieceNum: [{ validator: FreeFirstVlidator, trigger: 'blur' }],
  weight: [{ validator: FreeFirstVlidator, trigger: 'blur' }],
  amountNum: [
    { required: true, message: '费用不能为空', trigger: 'blur' },
    { validator: amountVlidator, trigger: 'blur' },
  ],
}

// 表单验证方法
function pieceVlidator(rule: any, value: string, callback: (error?: Error) => void): void {
  const regs = /^\+?(\d+)$/
  if (!regs.test(value)) {
    callback(new Error('件数请输入整数'))
  } else {
    callback()
  }
}

function weightVlidator(rule: any, value: string, callback: (error?: Error) => void): void {
  const regs = /^\d+.?\d{0,2}$/
  if (!regs.test(value)) {
    callback(new Error('重量输入有误,数字最多保留两位小数'))
  } else {
    callback()
  }
}

function amountVlidator(rule: any, value: string, callback: (error?: Error) => void): void {
  const regs = /^\d+.?\d{0,2}$/
  if (!regs.test(value)) {
    callback(new Error('费用输入有误,数字最多保留两位小数'))
  } else {
    callback()
  }
}

/** 包邮验证 */
function FreeFirstVlidator(rule: any, value: string, callback: (error?: Error) => void): void {
  const regs = /^\+?(\d+)$/
  const regWeight = /^\d+.?\d{0,2}$/
  const valuationModel = modelValidateForm.valuationModel
  if (value === '') {
    callback(new Error(`${valuationModel === '1' ? '件数' : '重量'}不能为空`))
  } else if (!regs.test(value) && valuationModel === '1') {
    callback(new Error(`件数请输入整数`))
  } else if (!regWeight.test(value) && valuationModel === '2') {
    callback(new Error(`重量输入有误,数字最多保留两位小数`))
  } else {
    callback()
  }
}

/** 运费模板选择 */
function selectRadio(e: string | number | boolean | undefined, idEdit: boolean = false): void {
  modelValidateForm.valuationModel = String(e) || ''
  const tonOptions = [
    {
      value: '1',
      label: '重量',
    },
    {
      value: '2',
      label: '金额',
    },
    {
      value: '3',
      label: '重量+金额',
    },
  ]
  const numOptions = [
    {
      value: '1',
      label: '件数',
    },
    {
      value: '2',
      label: '金额',
    },
    {
      value: '3',
      label: '件数+金额',
    },
  ]
  selectOptions.value = String(e) === '1' ? numOptions : tonOptions
  if (!idEdit) {
    modelValidateForm.logisticsIncludePostageDtos.forEach((item) => {
      item.amountNum = 0
      item.pieceNum = 0
      item.weight = 0
    })
  }
}

function initModel() {
  Object.assign(modelValidateForm, {
    id: '',
    name: '',
    valuationModel: '1',
    /** 是否选择包邮区域 */
    choiceConditionPostage: false,
    logisticsShippingModelDtos: [
      {
        region: [],
        area: '',
        id: '',
        logisticsId: '',
        firstPiece: 0, // 首件
        firstWeight: 0, // 首重
        firstAmount: 0, // 首费
        secondPiece: 0, // 续件
        secondWeight: 0, // 续重
        secondAmount: 0, // 续费
        valuationModel: '', // 计价方式
      },
    ],

    logisticsIncludePostageDtos: [
      {
        id: '',
        area: '',
        region: [],
        value: '1',
        pieceNum: 0, // 包邮件数
        amountNum: 0, // 包邮金额
        weight: 0, // 包邮重量
        type: '', // 0= 按件数包邮,1=按重量包邮,2=按金额包邮,3=件数+金额 4=重量+金额
      },
    ],
  })
}

// 监听props变化
watch(
  () => props.editCurrent,
  (newVal) => {
    if (newVal.id) {
      editModelOption()
    } else {
      initModel()
    }
  },
  { deep: true },
)

watch(
  () => props.logDialog,
  () => {
    if (props.editCurrent.id) {
      editModelOption()
    }
  },
)

onMounted(() => {
  if (props.editCurrent.id) {
    editModelOption()
  }
})

function selectValue() {
  clearValidate()
}

/**
 * 对选择区域表单进行校验
 */
function validate() {
  const arr = areaRef.value
  return Promise.all(
    arr.map(async (item) => {
      return await item.validate()
    }),
  )
}

async function getFormModel() {
  try {
    await validate()
  } catch (error) {
    console.error(error)
    return Promise.reject(false)
  }
}

/**
 * 清除验证
 */
function clearValidate() {
  areaRef.value.forEach((item) => {
    item.clearValidate()
  })

  freeAreaRef.value.forEach((item) => {
    item.clearValidate()
  })
}

/**
 * 对包邮区域表单进行校验
 */
function freeValidate() {
  const arr = freeAreaRef.value
  return Promise.all(
    arr.map(async (item) => {
      return await item.validate()
    }),
  )
}

async function getFreeFormModel() {
  try {
    await freeValidate()
  } catch (error) {
    console.error(error)
    return Promise.reject(false)
  }
}

/**
 * 编辑时初始化数据
 */
function editModelOption() {
  const editCurrent = JSON.parse(JSON.stringify(props.editCurrent))
  modelValidateForm.choiceConditionPostage = !!editCurrent.choiceConditionPostage
  modelValidateForm.valuationModel = String(editCurrent.valuationModel)
  modelValidateForm.name = editCurrent.name
  modelValidateForm.id = editCurrent.id
  editCurrent.logisticsShippingModelVos.forEach((item: Obj) => {
    item.valuationModel = String(item.valuationModel)
    item.region = typeof item.region === 'object' ? item.region : JSON.parse(item.region)
    // 确保数值字段为数字类型
    item.firstPiece = Number(item.firstPiece || 0)
    item.firstWeight = Number(item.firstWeight || 0)
    item.firstAmount = Number(item.firstAmount || 0)
    item.secondPiece = Number(item.secondPiece || 0)
    item.secondWeight = Number(item.secondWeight || 0)
    item.secondAmount = Number(item.secondAmount || 0)
  })

  editCurrent.logisticsIncludePostageVos.forEach((item: Obj) => {
    item.value = getType([item.type, editCurrent.valuationModel])
    item.region = typeof item.region === 'object' ? item.region : JSON.parse(item.region)
    // 确保数值字段为数字类型
    item.pieceNum = Number(item.pieceNum || 0)
    item.amountNum = Number(item.amountNum || 0)
    item.weight = Number(item.weight || 0)
  })
  modelValidateForm.logisticsShippingModelDtos = editCurrent.logisticsShippingModelVos
  modelValidateForm.logisticsIncludePostageDtos = editCurrent.logisticsIncludePostageVos
  selectRadio(String(modelValidateForm.valuationModel), true)
}

/** 逆向返回选中包邮类型 */
function getType(arr: [number | string, number | string]): string {
  const types = [
    [[0, 1], '1'],
    [[2, 1], '2'],
    [[3, 1], '3'],
    [[1, 2], '1'],
    [[2, 2], '2'],
    [[4, 2], '3'],
  ]
  const logType = new Map()
  types.forEach(([key, value]) => {
    logType.set(JSON.stringify(key), value)
  })
  return logType.get(JSON.stringify(arr)) || ''
}

/**
 * 编辑或新增地址
 */
function updateArea(item: any, index: number): void {
  modelIndex.value = index
  modelRegion.value = modelValidateForm.logisticsShippingModelDtos[modelIndex.value].region
  areaDialog.value = true
  freeArea.value = false
}

/**
 * 编辑或新增包邮地址
 */
function updateFreeArea(item: any, index: number): void {
  modelIndex.value = index
  modelRegion.value = modelValidateForm.logisticsIncludePostageDtos[index].region
  areaDialog.value = true
  freeArea.value = true
}

/** 选择包邮区域 */
function showCheck() {
  if (modelValidateForm.logisticsIncludePostageDtos.length === 0) {
    addnewFreeArea()
  }
  selectRadio(String(modelValidateForm.valuationModel))
}

/**
 * 添加新的区域
 */
function addnewArea() {
  modelValidateForm.logisticsShippingModelDtos.push({
    region: [],
    area: '',
    id: '',
    firstPiece: 0, // 首件
    firstWeight: 0, // 首重
    logisticsId: '',
    firstAmount: 0, // 首费
    secondPiece: 0, // 续件
    secondWeight: 0, // 续重
    secondAmount: 0, // 续费
    valuationModel: '', // 计价方式
  })
}

/**
 * 删除新区域
 */
function deleteNewArea(index: number, type: number): void {
  if (type === 0) {
    if (modelValidateForm.logisticsShippingModelDtos.length === 1) {
      ElMessage.error('至少保留一个配送区域')
      return
    }
    modelValidateForm.logisticsShippingModelDtos.splice(index, 1)
  } else {
    if (modelValidateForm.logisticsIncludePostageDtos.length === 1) {
      ElMessage.error('至少保留一个包邮区域')
      return
    }
    modelValidateForm.logisticsIncludePostageDtos.splice(index, 1)
  }
}

function addnewFreeArea() {
  modelValidateForm.logisticsIncludePostageDtos.push({
    id: '',
    area: '',
    region: [],
    value: '1',
    pieceNum: 0, // 包邮件数
    amountNum: 0, // 包邮金额
    weight: 0, // 包邮重量
    type: '', // 0= 按件数包邮,1=按重量包邮,2=按金额包邮,3=件数+金额 4=重量+金额
  })
}

/**
 * 获取选择后的地址
 */
function getConList(region: any[], conList: any[], modelIndex: number, type: boolean): void {
  if (!type) {
    dealLogArea(region, conList, modelIndex)
  } else {
    dealFreeLogArea(region, conList, modelIndex)
  }
}

/** 配送区域 */
function dealLogArea(region: any[], conList: any[], modelIndex: number): void {
  const area: string[] = []
  modelValidateForm.logisticsShippingModelDtos.forEach((item, index) => {
    if (index === modelIndex) {
      item.region = region
      conList.forEach((v) => {
        if (v.cityList && v.cityList.length > 0 && v.cityList.length !== v.hasNum) {
          v.newlabel = `${v.label} (${v.cityList.length} / ${v.hasNum})`
        } else {
          v.newlabel = v.label
        }
        area.push(v.newlabel)
        item.area = area.join(',')
      })
    }
  })
}

/** 包邮区域 */
function dealFreeLogArea(region: any[], conList: any[], modelIndex: number): void {
  const area: string[] = []
  modelValidateForm.logisticsIncludePostageDtos.forEach((item, index) => {
    if (index === modelIndex) {
      item.region = region
      conList.forEach((v) => {
        if (v.cityList && v.cityList.length > 0 && v.cityList.length !== v.hasNum) {
          v.newlabel = `${v.label} (${v.cityList.length} / ${v.hasNum})`
        } else {
          v.newlabel = v.label
        }
        area.push(v.newlabel)
        item.area = area.join(',')
      })
    }
  })
}

// 向父组件暴露方法 (可以通过ref调用)
defineExpose({
  getFormModel,
  getFreeFormModel,
  initModel,
  modelValidateForm,
})
</script>

<template>
  <div style="padding: 0px 20px">
    <div class="nameInput">
      <div style="margin-right: 20px; font-size: 13px"><span style="color: red">*</span>模板名称：</div>
      <el-input v-model="modelValidateForm.name" style="width: 280px" maxlength="25" />
      <span class="nameInput__text">模板名称最多25个字</span>
    </div>
    <div class="radioChoise">
      <div style="margin-right: 20px; font-size: 13px"><span style="color: red">*</span>计费方式：</div>
      <el-radio v-model="modelValidateForm.valuationModel" value="1" @change="selectRadio">
        <span style="font-size: 13px">按件数</span>
      </el-radio>
      <el-radio v-model="modelValidateForm.valuationModel" value="2" @change="selectRadio">
        <span style="font-size: 13px">按重量</span>
      </el-radio>
      <span class="radioChoise__text">指定区域城市运费包邮可在运费填写为0</span>
    </div>
    <div class="lineStyle">
      <div class="lineStyle__left">选择区域</div>
      <div class="lineStyle__right">
        <span>
          {{ modelValidateForm.valuationModel === '1' ? '首件数(件)' : '首重量(kg)' }}
        </span>
        <span style="margin-left: 80px">首费(元)</span>
        <span style="margin-left: 80px">
          {{ modelValidateForm.valuationModel === '1' ? '续件数(件)' : '续重量(kg)' }}
        </span>
        <span style="margin-left: 80px">续费(元)</span>
        <span style="margin-left: 80px">操作</span>
      </div>
    </div>
    <div v-for="(item, index) in modelValidateForm.logisticsShippingModelDtos" :key="index">
      <div class="lineStyle" style="padding: 10px 0px">
        <div class="lineStyle__left middleCenter">
          {{ item.area }}
          <el-button link style="margin-left: 10px" @click="updateArea(item, index)">
            {{ item.area ? '编辑' : '添加' }}
          </el-button>
        </div>
        <div class="lineStyle__right">
          <el-form ref="areaRef" :model="item" :rules="areaRules" class="demo-ruleForm">
            <div style="display: flex">
              <div>
                <el-form-item v-if="modelValidateForm.valuationModel === '1'" prop="firstPiece">
                  <el-input-number v-model="item.firstPiece" :min="0" :controls="false" style="width: 80px" />
                </el-form-item>
                <el-form-item v-if="modelValidateForm.valuationModel === '2'" prop="firstWeight">
                  <el-input-number v-model="item.firstWeight" :min="0" :controls="false" style="width: 80px" />
                </el-form-item>
              </div>
              <div style="margin-left: 65px">
                <el-form-item prop="firstAmount">
                  <el-input-number v-model="item.firstAmount" :min="0" :controls="false" style="width: 80px" />
                </el-form-item>
              </div>
              <div style="margin-left: 50px">
                <el-form-item v-if="modelValidateForm.valuationModel === '1'" prop="secondPiece">
                  <el-input-number v-model="item.secondPiece" :min="0" :controls="false" style="width: 80px" />
                </el-form-item>
                <el-form-item v-if="modelValidateForm.valuationModel === '2'" prop="secondWeight">
                  <el-input-number v-model="item.secondWeight" :min="0" :controls="false" style="width: 80px" />
                </el-form-item>
              </div>
              <div style="margin-left: 55px">
                <el-form-item prop="secondAmount">
                  <el-input-number v-model="item.secondAmount" :min="0" :controls="false" style="width: 80px" />
                </el-form-item>
              </div>
            </div>
          </el-form>
          <div
            class="lineStyle__right--deal"
            :style="{
              'justify-content': modelValidateForm.logisticsShippingModelDtos.length === index + 1 ? 'space-between' : '',
            }"
          >
            <span v-if="modelValidateForm.logisticsShippingModelDtos.length === index + 1" style="color: #409eff; cursor: pointer" @click="addnewArea"
              >添加</span
            >
            <span style="color: rgb(250, 100, 101); cursor: pointer" @click="deleteNewArea(index, 0)">删除</span>
          </div>
          <AreaSelect v-model:visible="areaDialog" :model-index="modelIndex" :get-region="modelRegion" @get-con-list="getConList" />
        </div>
      </div>
    </div>
    <el-checkbox v-model="modelValidateForm.choiceConditionPostage" style="margin-top: 20px" @change="showCheck">
      <div class="checkArea">指定包邮条件</div>
    </el-checkbox>
    <div v-if="modelValidateForm.choiceConditionPostage">
      <div class="lineStyle">
        <div class="lineStyle__left">
          <span>选择区域</span>
        </div>
        <div class="lineStyle__right" style="justify-content: space-between; padding: 0px 80px 0px 0px">
          <span>设置包邮条件</span>
          <span>操作</span>
        </div>
      </div>
      <div v-for="(item, index) in modelValidateForm.logisticsIncludePostageDtos" :key="index">
        <div class="areaLineStyle">
          <div class="areaLineStyle__left middleCenter">
            {{ item.area }}
            <el-button link style="margin-left: 10px" @click="updateFreeArea(item, index)">
              {{ item.area ? '编辑' : '添加' }}
            </el-button>
          </div>
          <div class="areaLineStyle__right">
            <el-form ref="freeAreaRef" :model="item" :rules="freeAreaRules" class="demo-ruleForm">
              <div style="display: flex; padding-top: 15px">
                <el-select
                  v-model="item.value"
                  placeholder="请选择"
                  style="width: 120px; margin-right: 20px; margin-left: 15px"
                  @change="selectValue"
                >
                  <el-option v-for="option in selectOptions" :key="option.value" :label="option.label" :value="option.value" />
                </el-select>
                <el-form-item v-if="item.value === '1'" prop="pieceNum">
                  <span
                    >在
                    <el-input-number v-model="item.pieceNum" :min="0" :controls="false" class="areaLineStyle__right--input" style="width: 80px" />
                    <span> {{ modelValidateForm.valuationModel === '1' ? '件' : 'kg' }} </span>以上包邮
                  </span>
                </el-form-item>
                <el-form-item v-if="item.value === '2'" prop="amountNum">
                  <span
                    >满
                    <el-input-number
                      v-model="item.amountNum"
                      :min="0"
                      :controls="false"
                      class="areaLineStyle__right--input"
                      style="width: 100px"
                    />元以上包邮
                  </span>
                </el-form-item>
                <el-form-item v-if="item.value === '3'" style="width: 450px">
                  <div style="display: flex">
                    在
                    <el-form-item v-if="modelValidateForm.valuationModel === '1'" prop="pieceNum">
                      <el-input-number v-model="item.pieceNum" :min="0" :controls="false" class="areaLineStyle__right--input" style="width: 80px" />
                    </el-form-item>
                    <el-form-item v-if="modelValidateForm.valuationModel === '2'" prop="weight">
                      <el-input-number v-model="item.weight" :min="0" :controls="false" class="areaLineStyle__right--input" style="width: 100px" />
                    </el-form-item>
                    <div style="display: flex">
                      {{ modelValidateForm.valuationModel === '1' ? '件' : 'kg' }}
                    </div>
                    以上,满
                    <el-form-item prop="amountNum">
                      <el-input-number v-model="item.amountNum" :min="0" :controls="false" class="areaLineStyle__right--input" style="width: 100px" />
                      元以上包邮
                    </el-form-item>
                  </div>
                </el-form-item>
              </div>
            </el-form>
            <div
              class="areaLineStyle__right--deal"
              :style="{
                'justify-content': modelValidateForm.logisticsIncludePostageDtos.length === index + 1 ? 'space-between' : '',
              }"
            >
              <span
                v-if="modelValidateForm.logisticsIncludePostageDtos.length === index + 1"
                style="color: #409eff; cursor: pointer"
                @click="addnewFreeArea"
                >添加</span
              >
              <span style="color: rgb(250, 100, 101); cursor: pointer" @click="deleteNewArea(index, 1)">删除</span>
            </div>
            <AreaSelect
              v-model:visible="areaDialog"
              :model-index="modelIndex"
              :get-region="modelRegion"
              :free-area="freeArea"
              :model-validate-form="modelValidateForm"
              @get-con-list="getConList"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.nameInput {
  display: flex;
  align-items: center;

  &__text {
    font-size: 12px;
    color: #c7c7c7;
    margin-left: 30px;
  }
}

.radioChoise {
  display: flex;
  align-items: center;
  margin-top: 20px;

  &__text {
    font-size: 12px;
    color: #c7c7c7;
    margin-left: 25px;
  }
}

.middleCenter {
  display: flex;
  align-items: center;
}

.lineStyle {
  display: flex;
  // justify-content: space-between;
  border-bottom: 0.5px solid #dcdfe6;
  padding: 20px 0px;
  font-size: 13px;
  margin-top: 10px;

  &__left {
    width: 130px;
    flex-wrap: wrap;
  }

  &__right {
    width: 750px;
    display: flex;
    margin-left: 20px;
    // justify-content: space-around;

    &--deal {
      margin-left: 50px;
      width: 60px;
      display: flex;
      align-items: center;
    }
  }
}

.checkArea {
  font-size: 13px;
}

:deep(.el-form-item--mini.el-form-item),
:deep(.el-form-item--small.el-form-item) {
  margin-bottom: 10px;
}

.areaLineStyle {
  display: flex;
  justify-content: space-between;
  border-bottom: 0.5px solid #dcdfe6;
  padding: 10px 0px;
  font-size: 13px;
  margin-top: 10px;

  &__left {
    width: 130px;
    flex-wrap: wrap;
  }

  &__right {
    width: 750px;
    display: flex;
    justify-content: space-between;
    align-items: center;

    &--input {
      width: 60px;
      margin: 0 5px;
    }

    &--deal {
      width: 60px;
      margin-right: 50px;
      display: flex;
      align-items: center;
    }
  }
}
</style>
