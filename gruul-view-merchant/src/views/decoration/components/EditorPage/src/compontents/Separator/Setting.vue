<script setup lang="ts">
import { useVModel } from '@vueuse/core'

import Separator from './Separator'

const props = defineProps({
  formData: {
    type: Object as PropType<typeof Separator>,
    default: () => Separator,
  },
})

const emit = defineEmits(['update:formData'])
const formData = useVModel(props, 'formData', emit)

const borderStyles = ref([
  {
    label: '实线',
    value: 'solid',
  },
  {
    label: '虚线',
    value: 'dashed',
  },
  {
    label: '点线',
    value: 'dotted',
  },
])

/**
 * 颜色选择切换
 */
function colorSelectChange() {
  if (!formData.value.borderColor) {
    formData.value.borderColor = '#EEEEEE'
  }
}
</script>

<template>
  <!-- 分隔符 -->
  <el-form :model="formData">
    <el-form-item label="颜色">
      <el-color-picker v-model="formData.borderColor" @change="colorSelectChange" />
    </el-form-item>
    <el-form-item label="边距">
      <el-radio-group v-model="formData.hasMargin">
        <el-radio :value="false"> 无边距 </el-radio>
        <el-radio :value="true"> 左右留边 </el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item label="样式">
      <el-radio-group v-model="formData.borderStyle">
        <el-radio v-for="item in borderStyles" :key="item.value" :value="item.value">
          {{ item.label }}
        </el-radio>
      </el-radio-group>
    </el-form-item>
  </el-form>
</template>
