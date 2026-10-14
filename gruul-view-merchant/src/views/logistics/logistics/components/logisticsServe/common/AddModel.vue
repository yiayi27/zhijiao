<script setup>
import { getAllShopAddress } from '@/api/logistics/logistics'

// 定义props
const props = defineProps({
  addDialog: {
    type: Boolean,
    default: false,
  },
  editCurrent: {
    type: Object,
    default: () => ({}),
  },
  chooseCompany: {
    type: Object,
    default: () => ({}),
  },
})

// 定义emit事件
const emit = defineEmits(['update:addDialog', 'routeChange'])

// 快递公司信息
const serveOption = reactive({
  addressId: '',
  customerName: '',
  customerPassword: '',
  linkName: '',
  linkTel: '',
  code: 'sf', // 物流公司code
  status: 1,
  phone: 91199,
  name: '顺丰快递',
  id: '',
})

// 发货地址选择列表
const addList = ref([])

// 监听弹窗显示状态
watch(
  () => props.addDialog,
  (newVal) => {
    if (newVal) {
      getLogAddress()
    }

    if (props.editCurrent.getAddress) {
      dealOption()
    } else {
      init()
    }
  },
)

// 组件挂载时初始化
onMounted(() => {
  getLogAddress()
  if (props.editCurrent.getAddress) {
    dealOption()
  } else {
    init()
  }
})

/**
 * 初始化数据
 */
function init() {
  Object.assign(serveOption, {
    addressId: '',
    customerName: '',
    customerPassword: '',
    linkName: '',
    linkTel: '',
    code: props.chooseCompany.code || 'sf',
    status: 1,
    phone: 91199,
    name: props.chooseCompany.name || '顺丰快递',
    id: '',
  })
}

/**
 * 处理编辑数据
 */
function dealOption() {
  const editCurrent = JSON.parse(JSON.stringify(props.editCurrent))
  serveOption.code = editCurrent.code
  serveOption.customerName = editCurrent.getAddress.customerName
  serveOption.customerPassword = editCurrent.getAddress.customerPassword
  serveOption.linkName = editCurrent.getAddress.linkName
  serveOption.linkTel = editCurrent.getAddress.linkTel
  serveOption.name = editCurrent.name
  serveOption.id = editCurrent.getAddress.cid
  serveOption.addressId = editCurrent.getAddress.id
}

/**
 * 前往地址设置页面
 * 使用emit替代$parent访问
 */
function jumpClick() {
  emit('routeChange', 'AddressProgram')
}

/**
 * 获取所有地址
 */
async function getLogAddress() {
  const res = await getAllShopAddress({})
  addList.value = res.data || []
}

/**
 * 表单验证
 */
function validateForm() {
  if (!serveOption.addressId) {
    return { valid: false, message: '请选择发货地址' }
  }
  if (!serveOption.customerName) {
    return { valid: false, message: '请输入客户号' }
  }
  if (!serveOption.customerPassword) {
    return { valid: false, message: '请输入客户号密码' }
  }
  if (!serveOption.linkName) {
    return { valid: false, message: '请输入负责人姓名' }
  }
  if (!serveOption.linkTel) {
    return { valid: false, message: '请输入联系电话' }
  }

  const phoneReg = /^1[3-9]\d{9}$/
  if (!phoneReg.test(serveOption.linkTel)) {
    return { valid: false, message: '请输入正确的手机号' }
  }

  return { valid: true, data: serveOption }
}

// 暴露方法给父组件
defineExpose({
  validateForm,
  serveOption,
})
</script>

<template>
  <div class="diaLog">
    <div class="diaLog__content">
      <span class="diaLog__content--left">快递公司:</span>
      {{ serveOption.name }}
    </div>
    <div class="diaLog__content">
      <span class="diaLog__content--left"> <span class="star">*</span>发货地址: </span>
      <el-select v-model="serveOption.addressId" placeholder="请选择发货地址" style="width: 250px">
        <el-option v-for="item in addList" :key="item.id" :label="item.address" :value="item.id" />
      </el-select>
      <el-button link style="margin-left: 20px" @click="jumpClick"> 前往设置 </el-button>
    </div>
    <div class="diaLog__content">
      <span class="diaLog__content--left"> <span class="star">*</span>客户号: </span>
      <el-input v-model="serveOption.customerName" placeholder="请输入客户号" style="width: 320px" />
    </div>
    <div class="diaLog__content">
      <span class="diaLog__content--left"> <span class="star">*</span>客户号密码: </span>
      <el-input v-model="serveOption.customerPassword" placeholder="请输入客户号密码" style="width: 320px" />
    </div>
    <div class="diaLog__content">
      <span class="diaLog__content--left"> <span class="star">*</span>负责人: </span>
      <el-input v-model="serveOption.linkName" placeholder="请填写负责人姓名" style="width: 320px" />
    </div>
    <div class="diaLog__content">
      <span class="diaLog__content--left"> <span class="star">*</span>联系电话: </span>
      <el-input v-model="serveOption.linkTel" placeholder="请填写联系电话" style="width: 320px" maxlength="11" />
    </div>
    <!-- <div class="diaLog__agree">
      <el-checkbox v-model="isAgree">
        <span class="diaLog__agree--open">同意快递服务商开通协议</span>
      </el-checkbox>
    </div> -->
  </div>
</template>

<style lang="scss" scoped>
.star {
  color: #f56c84;
  margin-right: 3px;
}

.diaLog {
  &__content {
    margin-bottom: 20px;
    display: flex;
    align-items: center;

    &--left {
      width: 100px;
      display: flex;
      justify-content: flex-end;
      margin-right: 40px;
    }
  }

  &__agree {
    width: 100%;
    display: flex;
    margin-left: 140px;

    &--open {
      color: #1e7fff;
      cursor: pointer;
    }
  }
}
</style>
