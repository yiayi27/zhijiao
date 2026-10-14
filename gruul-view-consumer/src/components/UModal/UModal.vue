<script setup lang="ts">
import { ref, watch } from 'vue'

// 只定义不在原组件中的属性
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  },
  // 其他所有uv-modal的属性将通过v-bind透传
})

const emit = defineEmits(['update:modelValue', 'confirm', 'cancel'])

const modalRef = ref()

// 监听show变化控制弹窗
watch(
  () => props.modelValue,
  (val) => {
    if (val) {
      modalRef.value?.open()
    } else {
      modalRef.value?.close()
    }
  },
  { immediate: true },
)

// 处理关闭
const handleClose = () => {
  emit('update:modelValue', false)
}

// 处理确认
const handleConfirm = () => {
  emit('confirm')
}

// 处理取消
const handleCancel = () => {
  emit('cancel')
}
</script>

<template>
  <uv-modal ref="modalRef" v-bind="{ ...props }" @close="handleClose" @confirm="handleConfirm" @cancel="handleCancel">
    <slot v-if="$slots.default" />
  </uv-modal>
</template>

<style lang="scss" scoped></style>
