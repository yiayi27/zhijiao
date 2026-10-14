<script setup lang="ts">
import { upLoad } from '@/api'
import { getMemberCard, getMemberCardPrivilege, saveMemberCard, updateMemberCardInfoStatus } from '@/api/customer/member'
import BaseTable from '@/components/BaseTable/index.vue'
import { commonMemberList } from '@/libs/const'
import { advanceGetImage, readFile } from '@/libs/voidImg'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { cloneDeep } from 'lodash-es'

import SelectGoods from '../dialog/SelectGoods.vue'

// 定义接口类型
interface FormItem {
  formId: string | number
  formName: string
  formPattern: number
  isRequired: number
  tips: string
  id?: string | number // 添加可选的id属性
}

interface PrivilegeItem {
  id: string | number
  privilegeName: string
  isSelected: number
  ptype: number | string
  value?: number
  open?: number
}

interface MemberCardItem {
  rankCode?: string
  rankName?: string
  isOpen: number
  style: string
  backGroundPicUrl?: string
  memberPrivilegeVos?: PrivilegeItem[]
  payAmount?: number
  integralCount?: number
  consumeCount?: number
  productCount?: number
  memberCardGoodsVo?: {
    goodsIds: string
  }
  rowIndex?: number
  [key: string]: any
}

interface FormData {
  title: string
  openType: number
  integralVos: MemberCardItem[] | null
  consumeVos: MemberCardItem[] | null
  assignProductVos: MemberCardItem[] | null
  paymentVos?: MemberCardItem[]
  memberRegisterFormVos: FormItem[]
  useInDate: number
  useAmount: null | number
  [key: string]: any
}

interface CustomFormData {
  formName: string
  formPattern: number
  isRequired: number
  tips: string
}

interface CardStyleData {
  style: string
  img?: string
  cusImg?: string
  [key: string]: any
}

const goodsDialogRef = ref<InstanceType<typeof SelectGoods> | null>(null)
const formRef = ref<any>(null)
const coustomFormRef = ref<any>(null)

// 默认注册表单状态
const coustomForm = reactive<CustomFormData>({
  formName: '',
  formPattern: 0,
  isRequired: 2,
  tips: '请输入',
})

const currentForm = ref<FormItem | null>(null)

// 当前编辑的卡片行数据
const currentCardData = reactive<CardStyleData>({
  style: '1',
})

// 当前编辑的卡片行
const currentCardindex = ref(0)

// 当前操作的表单
const currentIndex = ref(0)

// 当前处理商品的索引
let currentHandelGoodIndex = 0

// 注册表单的校验规则
const coustomRules = {
  formName: [
    {
      required: true,
      message: '表单名称不可为空',
      // trigger: "blur",
    },
  ],
  formPattern: [
    {
      required: true,
      message: '表单类型不可为空',
      // trigger: "change",
    },
  ],
}

// 注册表单modal显示
const formDialogVisible = ref(false)

// 会员卡设置modal显示
const cardDialogVisible = ref(false)

// 权益modal显示
const requityVisible = ref(false)

// 表单格式选项
const formOptions = [
  {
    label: '电话',
    value: 3,
  },
  {
    label: '身份证',
    value: 1,
  },
  {
    label: '文本',
    value: 2,
  },
]

// 开卡方式MAP
const radioMap: Record<number, string> = {
  1: '付费金额',
  2: '积分',
  3: '累计消费',
  4: '选择商品',
}

// 会员卡取值map
const tableMap: Record<number, string> = {
  1: 'paymentVos',
  2: 'integralVos',
  3: 'consumeVos',
  4: 'assignProductVos',
}

// 选中的行
const selectRow = reactive<MemberCardItem>({
  isOpen: 1,
  style: '1',
})
const selectRowChecked = ref<Array<string | number>>([])
const allIds = ref<string[]>([])

// 默认表单内容
const formData = reactive<FormData>({
  title: '',
  openType: 1,
  integralVos: null,
  consumeVos: null,
  assignProductVos: null,
  memberRegisterFormVos: [
    {
      id: '1',
      formId: '1',
      formName: '手机号',
      formPattern: 3,
      isRequired: 1,
      tips: '请输入手机号',
    },
    {
      id: '2',
      formId: '2',
      formName: '身份证',
      formPattern: 1,
      isRequired: 2,
      tips: '请输入身份证号',
    },
  ],
  useInDate: 365,
  useAmount: null,
})

// 记录前一次的开卡方式
const preOpenType = ref(2)

// 校验规则
const formRules = {
  title: [{ required: true, message: '请输入会员卡名称', trigger: 'blur' }],
  openType: [{ required: true, message: '请选择开卡方式', trigger: 'blur' }],
  useInDate: [{ required: true, message: '请输入有效期', trigger: 'blur' }],
}

// 选中的商品
const pointGoodsList = ref<{ id: string }[]>([])

// 权益列表
const requities = ref<PrivilegeItem[]>([])

// 注册表单默认数据
const forms = ref<FormItem[]>([
  {
    formId: 1,
    formName: '手机号',
    formPattern: 3,
    isRequired: 1,
    tips: '请输入手机号',
  },
])

const isInit = ref(false)

// 默认的系统会员头像
const sysImgs = [
  'https://oss-tencent.bgniao.cn/api/general_card.png',
  'https://oss-tencent.bgniao.cn/api/silver_card.png',
  'https://oss-tencent.bgniao.cn/api/gold_card.png',
  'https://oss-tencent.bgniao.cn/api/platinum_card.png',
  'https://oss-tencent.bgniao.cn/api/supremacy_card.png',
]

// 输入框递增校验
function comprePre(index: number, row: MemberCardItem, type: string): void {
  const tableData = formData[tableMap[formData.openType]] as MemberCardItem[]

  if (index !== 0 && Number.parseInt(String(tableData[index - 1][type])) > Number.parseInt(String(row[type]))) {
    ElMessage.error('不能小于前一项')
  }
}

onMounted(() => {
  getMemberCard({}).then((res) => {
    res.data.useInDate = Number(res.data.useInDate)
    const newData = cloneDeep(res.data)
    const openType = newData.openType || 1
    preOpenType.value = openType
    const memberCardList = newData[tableMap[openType]] || commonMemberList
    newData[tableMap[openType]] = getDefaultImg(memberCardList)
    Object.assign(formData, newData)
    isInit.value = res.data?.memberRegisterFormVos?.length > 0
    if (isInit.value) {
      forms.value = res.data.memberRegisterFormVos
    }
  })
  getMemberCardPrivilege({}).then((res) => {
    requities.value = res.data
  })
})

function getDefaultImg(arr: MemberCardItem[]): MemberCardItem[] {
  return arr.map((v, i) => {
    v.backGroundPicUrl = v.backGroundPicUrl || sysImgs[i]
    v.style = v.style ? `${v.style}` : '1'
    return v
  })
}

// 开卡方式change事件
function onOpenTypeChange(val: any): void {
  formData[tableMap[val]] = formData[tableMap[preOpenType.value]] ? [...(formData[tableMap[preOpenType.value]] as MemberCardItem[])] : null
  preOpenType.value = val
}

// 点击背景设置的图片
function imgClick(item: string): void {
  currentCardData.img = item
  Object.assign(currentCardData, cloneDeep(currentCardData))
}

// 监听对话框可见性变化
watch(formDialogVisible, (v) => {
  if (!v) {
    currentForm.value = null
    resetCoustomForm()
  }
})

watch(cardDialogVisible, (v) => {
  if (!v) {
    Object.keys(currentCardData).forEach((key) => {
      if (key !== 'style') {
        delete currentCardData[key]
      }
    })
  }
})

// 权益设置modal确认
function onRequityOk(): boolean | void {
  if (selectRow.memberPrivilegeVos?.every((v) => v.isSelected === 0)) {
    ElMessage.error('至少选择一项权益')
    return false
  }
  if (selectRow.memberPrivilegeVos?.[1]?.isSelected === 1 && !selectRow.memberPrivilegeVos?.[1]?.value) {
    ElMessage.error('请输入权益值')
    return false
  } else if (selectRow.memberPrivilegeVos?.[2]?.isSelected === 1 && !selectRow.memberPrivilegeVos?.[2]?.value) {
    ElMessage.error('请输入权益值')
    return false
  }
  const rowIndex = selectRow.rowIndex || 0
  let isRequired = true
  let isValidDisValue = true
  let isNumber = true
  let isValidIntValue = true
  let compareDisValue = true
  let compareIntValue = true

  selectRow.memberPrivilegeVos?.forEach((item) => {
    if ((item.ptype === 1 && item.isSelected === 1) || (item.ptype === 3 && item.isSelected === 1)) {
      if (!item.value) {
        isRequired = false
      }
      if (Number.isNaN(Number(item.value))) {
        isNumber = false
      }
    }
    if (item.ptype === 1 && item.isSelected === 1 && !Number.isNaN(Number(item.value))) {
      if (Number(item.value) > 99 || Number(item.value) < 10 || String(item.value).includes('.')) {
        isValidDisValue = false
      }
      for (let i = 0; i <= 4; i++) {
        if (!getPrivilegeValue(i, 1)) {
          break
        }
        if (i < rowIndex) {
          if (Number(item.value) > Number(getPrivilegeValue(i, 1))) {
            compareDisValue = false
          }
        }
        if (i > rowIndex) {
          if (Number(item.value) < Number(getPrivilegeValue(i, 1))) {
            compareDisValue = false
          }
        }
      }
    }
    if (item.ptype === 3 && item.isSelected === 1 && !Number.isNaN(Number(item.value))) {
      const valArr = String(item.value).split('.')
      if ((valArr[1] && valArr[1].length > 1) || Number(item.value) > 50 || Number(item.value) < 2) {
        isValidIntValue = false
      }

      for (let i = 0; i <= 4; i++) {
        if (!getPrivilegeValue(i, 3)) {
          break
        }
        if (i < rowIndex) {
          if (Number(item.value) < Number(getPrivilegeValue(i, 3))) {
            compareIntValue = false
          }
        }
        if (i > rowIndex) {
          if (Number(item.value) > Number(getPrivilegeValue(i, 3))) {
            compareIntValue = false
          }
        }
      }
    }
  })
  if (!isRequired) {
    ElMessage.error('输入框值必填')
    return false
  }

  if (!isNumber) {
    ElMessage.error('不能输入非数字字符')
    return false
  }

  if (!isValidDisValue) {
    ElMessage.error('折扣值必须为10-99整数')
    return false
  }

  if (!isValidIntValue) {
    ElMessage.error('积分只允许2-50且保留小数点后一位的数值')
    return false
  }

  if (!compareDisValue) {
    ElMessage.error('折扣值需小于等于低等级折扣权益值且大于等于高等级折扣权益值')
    return false
  }

  if (!compareIntValue) {
    ElMessage.error('积分值需大于等于低等级积分权益值且小于等于高等级积分权益值')
    return false
  }
  const newFormData = cloneDeep(formData)
  if (newFormData[tableMap[newFormData.openType]]) {
    ;(newFormData[tableMap[newFormData.openType]] as MemberCardItem[])[rowIndex] = cloneDeep(selectRow)
  }
  Object.assign(formData, newFormData)
  requityVisible.value = false
}

// 获取权益值
function getPrivilegeValue(index: number, pType: number): string | number | null {
  const tableData = formData[tableMap[formData.openType]] as MemberCardItem[]
  if (!tableData || !tableData[index] || tableData[index].isOpen === 0) {
    return null
  }
  const privileges = tableData[index].memberPrivilegeVos
  let value = null
  if (privileges) {
    privileges.forEach((el) => {
      if (el.ptype === pType) {
        value = el.value
      }
    })
  }
  return value
}

// 改变会员状态
function handelChangeStatus(rowData: MemberCardItem, index: number): void {
  const newFormData = cloneDeep(formData)
  const currentMap = {
    1: {
      targetStatus: 0,
      tips: '停用等级小程序端将不会显示该等级，确定停用吗？',
    },
    0: {
      targetStatus: 1,
      tips: '启用等级后将正常使用该等级权益，确定启用吗？',
    },
  }[rowData.isOpen]

  if (!currentMap) return

  ElMessageBox.confirm(currentMap.tips, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      if (!isInit.value) {
        await updateMemberCardInfoStatus([
          {
            rankCode: rowData.rankCode,
            memberCardInfoSwitch: currentMap.targetStatus,
          },
        ])
      }
      if (newFormData[tableMap[newFormData.openType]]) {
        ;(newFormData[tableMap[newFormData.openType]] as MemberCardItem[])[index].isOpen = currentMap.targetStatus
      }
      Object.assign(formData, newFormData)
    })
    .catch(() => {
      // Cancelled
    })
}

// 设置会员等级
function handelLevelSet(rowData: MemberCardItem, index: number): void {
  // radioStyle = rowData.style ? rowData.style + '' : "1";
  currentCardData.style = rowData.style ? `${rowData.style}` : '1'
  currentCardData.img = rowData.style === '1' ? rowData.backGroundPicUrl : sysImgs[index]
  currentCardData.cusImg = rowData.style === '2' ? rowData.backGroundPicUrl : ''
  currentCardindex.value = index
  cardDialogVisible.value = true
}

// 确定设置会员
function onCardOk(): void {
  const newFormData = cloneDeep(formData)
  if (newFormData[tableMap[newFormData.openType]]) {
    ;(newFormData[tableMap[newFormData.openType]] as MemberCardItem[])[currentCardindex.value].style = currentCardData.style
    if (currentCardData.style === '1') {
      ;(newFormData[tableMap[newFormData.openType]] as MemberCardItem[])[currentCardindex.value].backGroundPicUrl = currentCardData.img
    } else {
      ;(newFormData[tableMap[newFormData.openType]] as MemberCardItem[])[currentCardindex.value].backGroundPicUrl = currentCardData.cusImg
    }
  }
  Object.assign(formData, newFormData)
  cardDialogVisible.value = false
}

// 选中的商品
function onSelectGoods(index: number): void {
  let allIdsStr = ''
  currentHandelGoodIndex = index
  if (formData.assignProductVos) {
    formData.assignProductVos.forEach((item, i) => {
      if (index !== i) {
        allIdsStr += item.memberCardGoodsVo ? `,${item.memberCardGoodsVo.goodsIds}` || '' : ''
      }
    })
  }

  const ids =
    formData.assignProductVos && formData.assignProductVos[index] && formData.assignProductVos[index].memberCardGoodsVo
      ? formData.assignProductVos[index].memberCardGoodsVo.goodsIds
      : ''

  allIds.value = allIdsStr.split(',').filter(Boolean)
  pointGoodsList.value = ids
    .split(',')
    .map((v) => ({ id: v }))
    .filter((v) => !!v.id)
  if (goodsDialogRef.value) {
    goodsDialogRef.value.init()
  }
}

// 商品modal确认
function onSureGoods(pointGoodIds: string[]): void {
  const newFormData = cloneDeep(formData)
  if (newFormData.assignProductVos) {
    newFormData.assignProductVos[currentHandelGoodIndex].memberCardGoodsVo = {
      goodsIds: pointGoodIds.join(','),
    }
    newFormData.assignProductVos[currentHandelGoodIndex].productCount = pointGoodIds.length
  }
  Object.assign(formData, newFormData)
}

// 修改权益
function onChangeRequity(row: MemberCardItem, index: number): void {
  requityVisible.value = true
  Object.assign(selectRow, cloneDeep(row))
  selectRow.rowIndex = index
  if (!selectRow.memberPrivilegeVos || !selectRow.memberPrivilegeVos.length) {
    selectRow.memberPrivilegeVos = cloneDeep(requities.value)
  }
  selectRowChecked.value = (selectRow.memberPrivilegeVos || []).filter((v) => v.isSelected === 1).map((v) => v.id)
}

// 权益change
function privilegeChange(s: any[]): void {
  if (selectRow.memberPrivilegeVos) {
    selectRow.memberPrivilegeVos = selectRow.memberPrivilegeVos.map((v) => {
      v.isSelected = 0
      if ((s || []).includes(v.id)) {
        v.isSelected = 1
      }
      return v
    })
  }
}

/**
 * 上传大图片
 */
async function addUploadBigProductImg(file: any): Promise<void> {
  const whiteList = ['image/jpeg', 'image/jpg', 'image/png']

  const isLt1M = file.size < 1024 * 1024
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
  if (newImg.image.width !== 682 || newImg.image.height !== 370) {
    ElMessage.error('宽屏图片长宽应为682*370')
    return
  }

  const res = await upLoad({
    file: file.raw,
  })
  currentCardData.cusImg = res.data
}

// 重置注册表单
function resetCoustomForm(): void {
  Object.assign(coustomForm, {
    formName: '',
    formPattern: null,
    isRequired: 2,
    tips: '请输入',
  })
}

// 表单格式映射
function getFormName(key: number): string {
  const data = formOptions.find((item) => item.value === key)
  return data ? data.label : ''
}

function toggleDialogVisible(): void {
  formDialogVisible.value = !formDialogVisible.value
}

// 其定义表单modal确认
async function onConfirm(): Promise<void> {
  try {
    await formValidate(coustomFormRef.value)
    coustomFormRef.value.validate((valid: boolean) => {
      if (!valid) {
        return ElMessage.info('请填写正确的表单')
      }
      if (!currentForm.value) {
        // 确保formPattern不为空
        const formPattern = coustomForm.formPattern || 2 // 使用默认值2 (文本)
        forms.value.push({ ...coustomForm, formId: forms.value.length + 1, formPattern })
      } else {
        // 确保formPattern不为空
        const formPattern = coustomForm.formPattern || 2 // 使用默认值2 (文本)
        forms.value[currentIndex.value] = cloneDeep({
          ...coustomForm,
          formId: `${currentIndex.value + 1}`,
          formPattern,
        })
      }

      toggleDialogVisible()
    })
  } catch (error) {
    console.error(error)
  }
}

// 注册表单列表删除
function handleDel(item: FormItem, i: number): void {
  forms.value.splice(i, 1)
}

// 注册表单列表编辑
function handleEdit(item: FormItem, i: number): void {
  currentForm.value = cloneDeep(item)
  Object.assign(coustomForm, currentForm.value)
  currentIndex.value = i
  toggleDialogVisible()
}

/**
 * 对整个表单进行校验
 */
function formValidate(formType: any): Promise<boolean> {
  return formType.validate()
}

// 会员卡信息保存
async function onSaveCard(): Promise<void> {
  try {
    await formValidate(formRef.value)
    const tableData = formData[tableMap[formData.openType]] as MemberCardItem[]
    let isPass = true
    let isSort = true
    const valueMap: Record<number, string> = {
      1: 'payAmount',
      2: 'integralCount',
      3: 'consumeCount',
      4: 'productCount',
    }
    if (tableData) {
      tableData.forEach((v) => {
        if (v.isOpen === 1) {
          if (!v.rankName || !v[valueMap[formData.openType]]) {
            isPass = false
          }
        }
      })
    }
    if (!isPass) {
      ElMessage.error('请填写完整会员卡数据')
      return
    }

    // 处理文本框数值是否递增
    if (isPass && formData.openType !== 4 && tableData) {
      for (let i = 1; i < tableData.length; i++) {
        if (tableData[i].isOpen === 1) {
          if (Number(tableData[i][valueMap[formData.openType]]) < Number(tableData[i - 1][valueMap[formData.openType]])) {
            isSort = false
          }
        }
      }
      if (!isSort) {
        ElMessage.error('会员卡值金额或积分需递增')
        return
      }
    }

    if (tableData) {
      const privilegeNumList = tableData
        .filter((i) => i.isOpen)
        .map((item) => {
          return (item.memberPrivilegeVos?.filter((v: PrivilegeItem) => v.isSelected === 1) || []).length
        })

      let isUp = true
      privilegeNumList.forEach((number, index, array) => {
        const next = array[index + 1]
        if (next !== undefined && number > next) {
          isUp = false
          ElMessage.error('存在高等级会员权益比低等级会员权益少')
        }
      })

      if (isPass && isSort && isUp) {
        const res = await saveMemberCard({
          ...formData,
          memberRegisterFormVos: forms.value,
        })

        if (res.code === 200) {
          ElMessage({
            type: 'success',
            message: '保存成功',
          })
        }
      }
    }
  } catch (error) {
    console.error(error)
  }
}
</script>

<template>
  <div class="order order__setting--form flex-1 overflow-auto">
    <el-form ref="formRef" :rules="formRules" :model="formData" label-width="120px">
      <el-form-item label="会员卡名称" prop="title">
        <el-input v-model="formData.title" style="width: 200px" placeholder="请输入" />
      </el-form-item>
      <el-form-item label="开卡方式" prop="openType">
        <el-radio-group v-model="formData.openType" @change="onOpenTypeChange">
          <el-radio :value="1"> 付费购买 </el-radio>
          <el-radio :value="2"> 积分兑换 </el-radio>
          <!--          <el-radio :value="3">累计消费金额</el-radio> -->
          <!--          <el-radio :value="4">购买指定商品</el-radio> -->
        </el-radio-group>
        <BaseTable :data="formData[tableMap[formData.openType]]" style="width: 100%; margin-top: 12px">
          <el-table-column width="60" label="等级">
            <template #default="scope"> LV{{ scope.$index + 1 }} </template>
          </el-table-column>
          <el-table-column prop="rankName" label="等级名称">
            <template #default="scope">
              <el-input v-model="scope.row.rankName" :disabled="!scope.row.isOpen" />
            </template>
          </el-table-column>
          <el-table-column :label="radioMap[formData.openType]">
            <template #default="scope">
              <el-input-number
                v-if="formData.openType === 1"
                v-model="scope.row.payAmount"
                :min="0"
                :controls="false"
                :disabled="!scope.row.isOpen"
                @blur="comprePre(scope.$index, scope.row, 'payAmount')"
              >
                <template #append> 元 </template>
              </el-input-number>
              <el-input-number
                v-if="formData.openType === 3"
                v-model="scope.row.consumeCount"
                :min="0"
                :controls="false"
                :disabled="!scope.row.isOpen"
                @blur="comprePre(scope.$index, scope.row, 'consumeCount')"
              >
                <template #append> 元 </template>
              </el-input-number>
              <el-input-number
                v-if="formData.openType === 2"
                v-model="scope.row.integralCount"
                :step="1"
                :min="0"
                step-strictly
                :controls="false"
                :disabled="!scope.row.isOpen"
                @blur="comprePre(scope.$index, scope.row, 'integralCount')"
              >
                <template #append> 积分 </template>
              </el-input-number>
              <el-button v-if="formData.openType === 4" link @click="onSelectGoods(scope.$index)">
                {{ scope.row.productCount || '选择商品' }}
              </el-button>
            </template>
          </el-table-column>
          <el-table-column width="300" label="会员权益">
            <template #default="scope">
              {{
                scope.row.memberPrivilegeVos && scope.row.memberPrivilegeVos.filter((v: PrivilegeItem) => v.isSelected === 1).length
                  ? `${scope.row.memberPrivilegeVos
                      .filter((v: PrivilegeItem) => v.isSelected === 1)
                      .slice(0, 2)
                      .map((v: PrivilegeItem) => v.privilegeName)
                      .join('，')}等${(scope.row.memberPrivilegeVos.filter((v: PrivilegeItem) => v.isSelected === 1) || []).length}项权益`
                  : ''
              }}
              <el-button link @click="onChangeRequity(scope.row, scope.$index)">
                {{ scope.row.isOpen ? (!scope.row.memberPrivilegeVos || !scope.row.memberPrivilegeVos.length ? '选择权益' : '修改') : null }}
              </el-button>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150">
            <template #default="scope">
              <el-button link @click="handelLevelSet(scope.row, scope.$index)"> 设置 </el-button>

              <el-button link @click="handelChangeStatus(scope.row, scope.$index)">
                {{ scope.row.isOpen ? '停用' : '启用' }}
              </el-button>
            </template>
          </el-table-column>
        </BaseTable>
        <div v-if="formData.openType === 4" style="color: #999; font-size: 12px">购买指定任一商品即可成为会员</div>
        <!-- <el-button class="setupcon__item3--addnew"
                   link
                   @click="addLevel">新增等级
        </el-button> -->
      </el-form-item>
      <el-form-item label="注册表单" required>
        <BaseTable :data="forms" style="width: 100%">
          <el-table-column prop="formName" label="表单名称" />
          <el-table-column prop="formPattern" label="表单格式">
            <template #default="scope">
              {{ getFormName(scope.row.formPattern) }}
            </template>
          </el-table-column>
          <el-table-column label="必填" prop="isRequired">
            <template #default="scope">
              {{ scope.row.isRequired === 1 ? '是' : '否' }}
            </template>
          </el-table-column>
          <el-table-column label="提示语" prop="tips" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button link :disabled="scope.row.formId === 1" @click="handleEdit(scope.row, scope.$index)"> 编辑 </el-button>

              <el-button link :disabled="scope.row.formId === 1" @click="handleDel(scope.row, scope.$index)"> 删除 </el-button>
            </template>
          </el-table-column>
        </BaseTable>

        <el-button class="setupcon__item3--addnew" link @click="toggleDialogVisible"> 添加 </el-button>
      </el-form-item>
      <el-form-item label="有效期" prop="useInDate">
        <el-input-number v-model="formData.useInDate" :step="1" :min="0" step-strictly :controls="false" style="width: 200px" />&nbsp;&nbsp;天
      </el-form-item>
      <el-form-item label=" ">
        <el-button type="primary" @click="onSaveCard"> 保存 </el-button>
      </el-form-item>
    </el-form>

    <el-dialog v-model="formDialogVisible" title="自定义表单" width="30%">
      <el-form ref="coustomFormRef" :rules="coustomRules" :model="coustomForm" label-width="80px">
        <el-form-item label="表单名称" prop="formName" required>
          <el-input v-model="coustomForm.formName" />
        </el-form-item>
        <el-form-item label="表单格式" prop="formPattern" required>
          <el-select v-model="coustomForm.formPattern" style="width: 100%" placeholder="请选择">
            <el-option v-for="item of formOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="是否必填" prop="isRequired">
          <el-radio-group v-model="coustomForm.isRequired">
            <el-radio :value="1"> 开启 </el-radio>
            <el-radio :value="2"> 关闭 </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="提示语" prop="tips">
          <el-input v-model="coustomForm.tips" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="toggleDialogVisible">取 消</el-button>
          <el-button type="primary" @click="onConfirm">确 定</el-button>
        </span>
      </template>
    </el-dialog>
    <el-dialog v-model="cardDialogVisible" title="背景设置" width="30%">
      <el-form label-position="top" label-width="80px" :model="currentCardData">
        <el-form-item label="">
          <el-radio-group v-model="currentCardData.style">
            <el-radio value="1"> 系统风格 </el-radio>
            <el-radio value="2"> 自定义风格 </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="currentCardData.style === '1'" label="">
          <span v-for="item in sysImgs" :key="item" :class="item === currentCardData.img ? 'sImg aImg' : 'sImg'">
            <img width="40" height="40" :src="item" @click="imgClick(item)" />
          </span>
        </el-form-item>
        <el-form-item v-if="currentCardData.style === '2'" label="">
          <div>
            <div class="product" style="display: flex">
              <el-upload
                class="productImg__uploader product__productImg"
                :auto-upload="false"
                :on-change="addUploadBigProductImg"
                :show-file-list="false"
                style="width: 100px; height: 120px"
              >
                <img v-if="currentCardData.cusImg" :src="currentCardData.cusImg" class="product__productImg" style="width: 100px; height: 120px" />
                <el-icon
                  v-if="!currentCardData.cusImg"
                  class="product__productImg--uploader--icon"
                  style="width: 100px; height: 100px; line-height: 100px; border: 1px dotted #999"
                >
                  <Plus />
                </el-icon>
              </el-upload>
            </div>
            <div class="text-info fs-12" style="color: rgba(69, 64, 60, 0.6)">最佳尺寸682*370，图片大小不超过150kb</div>
          </div>
        </el-form-item>
        <el-form-item label="预览效果">
          <img v-show="currentCardData.img && currentCardData.style === '1'" width="160" :src="currentCardData.img" />
          <img v-show="currentCardData.cusImg && currentCardData.style === '2'" width="160" :src="currentCardData.cusImg" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button type="primary" @click="onCardOk">确 定</el-button>
        </span>
      </template>
    </el-dialog>
    <el-dialog v-model="requityVisible" title="选择权益" width="30%">
      <div>
        <el-checkbox-group v-model="selectRowChecked" class="cCheckbox" :border="false" @change="privilegeChange">
          <el-checkbox v-for="item in selectRow.memberPrivilegeVos" :key="item.id" :class="item.open === 0 && 'no-show'" :value="item.id">
            {{ item.privilegeName }}
            <el-input-number
              v-if="item.ptype === '1' && item.isSelected"
              v-model="item.value"
              :min="10"
              :max="99"
              :controls="false"
              style="width: 200px; vertical-align: middle"
              placeholder="输入10-99之间数字"
            >
              <template #append> % </template>
            </el-input-number>
            <el-input-number
              v-if="item.ptype === '3' && item.isSelected"
              v-model="item.value"
              :min="2"
              :max="50"
              :step="1"
              step-strictly
              :controls="false"
              style="width: 200px; vertical-align: middle"
              placeholder="输入2-50之间数字"
            >
              <template #append> 倍 </template>
            </el-input-number>
          </el-checkbox>
        </el-checkbox-group>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button type="primary" @click="onRequityOk">确 定</el-button>
        </span>
      </template>
    </el-dialog>
    <SelectGoods ref="goodsDialogRef" :point-goods-list="pointGoodsList" :all-ids="allIds" @on-sure-goods="onSureGoods" />
  </div>
</template>

<style lang="scss" scoped>
.sImg {
  display: inline-block;
  padding: 6px;
  width: 60px;
  height: 60px;
  margin-right: 12px;
  border: 1px solid #999;
  > img {
    cursor: pointer;
  }
}

.aImg {
  border: 1px solid #409eff;
}

.cTable {
  border: 1px solid #999;
  border-radius: 4px;
  width: 100%;
  padding: 0 12px;

  &__row {
    border-bottom: 1px solid #999;
    margin: 9px 0;
    &:last-child {
      border-bottom: none;
    }
    &:first-child {
      padding-bottom: 9px;
    }

    &--col {
      float: left;
      :deep(.el-button) {
        display: block;
        // padding: 9px 0;
        padding: 0;
        font-size: 14px;
        margin: 0 0 9px;
      }
      > span {
        display: block;
        // padding: 9px 0;
        margin-bottom: 9px;
      }
      > i {
        display: inline-block;
        font-style: normal;
        vertical-align: middle;
      }
    }
  }
}

.cCheckbox {
  > label {
    display: block;
    line-height: 40px;
    height: 40px;
    // vertical-align: middle;
  }
}

.order__setting--form {
  font-size: 13px;

  .el-table .cell {
    padding: 10px;
  }

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

.no-show {
  display: none !important;
}
</style>
