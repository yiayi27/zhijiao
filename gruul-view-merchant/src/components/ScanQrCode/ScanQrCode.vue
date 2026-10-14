<script setup lang="ts">
import { toPostAccountVerify } from '@/api/sign'
import { createQrCode } from '@/libs'

// 定义props
const props = defineProps({
  id: {
    type: String,
    default: 'qrcode',
  },
  height: {
    type: String,
    default: '400px',
  },
  redirectUrl: {
    type: String,
    required: true,
  },
  scenes: {
    type: String,
    required: true,
  },
})

const loading = ref(false)

// 获取完整URL并创建二维码
async function getCompleteUrl() {
  loading.value = true
  const response = await toPostAccountVerify({
    redirectUrl: props.redirectUrl,
    scenes: props.scenes,
  }).finally(() => {
    loading.value = false
  })

  const { data } = response
  await nextTick()
  createQrCode({
    id: props.id,
    url: data,
    height: props.height,
  })
}

// 组件挂载时执行
onMounted(() => {
  getCompleteUrl()
})
</script>

<template>
  <div :id="id" />
</template>
