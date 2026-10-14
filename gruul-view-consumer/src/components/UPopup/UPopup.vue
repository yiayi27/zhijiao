<script setup lang="ts">
import { ref, watch } from 'vue'

// 只定义不在原组件中的属性
const props = defineProps({
  show: {
    type: Boolean,
    default: false,
  },
  position: {
    type: String,
    default: 'bottom',
  },
  // 其他所有uv-popup的属性将通过v-bind透传
  mode: {
    type: String,
    default: 'bottom',
  },
})

const emit = defineEmits(['update:show', 'change', 'maskClick'])

const popupRef = ref()

// 监听show变化控制弹窗
watch(
  () => props.show,
  (val) => {
    if (val) {
      popupRef.value?.open()
    } else {
      popupRef.value?.close()
    }
  },
  { immediate: true },
)

// 处理弹窗状态变化
const handleChange = (e: { show: boolean; type?: string }) => {
  emit('update:show', e.show)
  emit('change', e)
}

// 处理蒙层点击
const handleMaskClick = () => {
  emit('maskClick')
}
</script>

<template>
  <uv-popup ref="popupRef" v-bind="{ ...props }" @change="handleChange" @maskClick="handleMaskClick">
    <slot />
    <uv-safe-bottom v-if="props.mode === 'bottom'" />
  </uv-popup>
</template>

<style lang="scss" scoped></style>
