<script setup lang="ts">
import type { FormInstance } from 'element-plus'

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
})

// 表单ref
const sheetRef = ref<FormInstance>()

// 表单数据
const sheetForm = reactive({
  deviceType: '打印机',
  deviceModel: '快递100',
  deviceName: '',
  deviceNo: '',
  deviceKey: '',
  status: '1',
  id: '',
  kdHundredTempId: '',
})

// 表单校验规则
const sheetRules = {
  deviceType: [{ required: true, message: '请选择设备类型', trigger: 'change' }],
  deviceModel: [{ required: true, message: '请选择设备型号', trigger: 'change' }],
  deviceName: [{ required: true, message: '设备名称不能为空', trigger: 'blur' }],
  deviceNo: [{ required: true, message: '打印机机身号不能为空', trigger: 'blur' }],
  deviceKey: [{ required: true, message: '打印机key不能为空', trigger: 'blur' }],
  kdHundredTempId: [{ required: true, message: '模板id不能为空', trigger: 'blur' }],
}

// 设备数组
const sheetList = reactive([
  {
    value: '打印机',
    label: '打印机',
  },
])

// 设备型号
const valueList = reactive([
  {
    value: '选项一',
    label: '快递100',
  },
])

// 监听对话框变化
watch(
  () => props.addDialog,
  () => {
    if (props.editCurrent.id) {
      editData()
    } else {
      initData()
    }
  },
)

// 生命周期挂载
onMounted(() => {
  if (props.editCurrent.id) {
    editData()
  } else {
    initData()
  }
})

// 初始化数据
function initData() {
  Object.assign(sheetForm, {
    deviceType: '打印机',
    deviceModel: '快递100',
    deviceName: '',
    deviceNo: '',
    deviceKey: '',
    status: '1',
    id: '',
    kdHundredTemplId: '',
  })
}

// 编辑时处理数据
function editData() {
  Object.assign(sheetForm, JSON.parse(JSON.stringify(props.editCurrent)))
  // 在Vue 3中，不能直接修改父组件的属性，需要通过emit事件
  // 这里的$parent.editCurrent = {} 需要通过其他方式实现
}

// 对外暴露方法
defineExpose({
  // 对整个表单进行校验
  validate: () => {
    return sheetRef.value?.validate()
  },

  // 移除表单项的校验结果
  clearValidate: () => {
    sheetRef.value?.clearValidate()
  },

  sheetForm,
})
</script>

<template>
  <el-form ref="sheetRef" :model="sheetForm" :rules="sheetRules" label-width="100px" class="demo-ruleForm">
    <el-form-item label="设备类型" prop="deviceType">
      <el-select v-model="sheetForm.deviceType" placeholder="选择设备" style="width: 250px">
        <el-option v-for="item in sheetList" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
    </el-form-item>
    <el-form-item label="设备型号" prop="deviceModel">
      <el-select v-model="sheetForm.deviceModel" placeholder="选择设备" style="width: 250px">
        <el-option v-for="item in valueList" :key="item.value" :label="item.label" :value="item.label" />
      </el-select>
    </el-form-item>
    <el-form-item label="设置名称" prop="deviceName" style="width: 500px">
      <el-input v-model="sheetForm.deviceName" style="width: 250px" placeholder="填写设备备注名称" />
    </el-form-item>
    <el-form-item label="打印机身号" prop="deviceNo">
      <el-input v-model="sheetForm.deviceNo" style="width: 250px" placeholder="填写打印机机身号" />
    </el-form-item>
    <el-form-item label="打印机KEY" prop="deviceKey">
      <el-input v-model="sheetForm.deviceKey" style="width: 250px" placeholder="填写打印机Key" />
    </el-form-item>
    <el-form-item label="模板ID" prop="kdHundredTemplId">
      <el-input v-model="sheetForm.kdHundredTempId" style="width: 250px" placeholder="填写模板ID" />
    </el-form-item>
  </el-form>
</template>

<style lang="scss" scoped>
.demo-ruleForm {
  padding-left: 30px;
}
</style>
