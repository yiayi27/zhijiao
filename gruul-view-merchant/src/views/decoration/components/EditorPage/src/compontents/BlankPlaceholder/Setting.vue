<script setup lang="ts">
import BlankPlaceholder from './BlankPlaceholder'
import { useVModel } from '@vueuse/core'

const props = defineProps({
  formData: {
    type: Object as () => typeof BlankPlaceholder,
    required: true,
  },
})
const emit = defineEmits(['update:formData'])

const formData = useVModel(props, 'formData', emit)
</script>

<template>
  <!-- 空白占位 -->
  <el-form :model="formData" label-width="80px">
    <el-form-item label="空白高度">
      <el-slider v-model="formData.height" :max="100" :min="10" :show-tooltip="false" />

      {{ formData.height }}像素
    </el-form-item>
  </el-form>
</template>
