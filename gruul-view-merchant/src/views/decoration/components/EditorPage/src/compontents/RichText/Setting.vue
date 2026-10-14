<script setup lang="ts">
import RichText from '@/views/decoration/components/EditorPage/src/compontents/RichText/RichText'

import RichEditor from '@/components/RichEditor/RichEditor.vue'
import { useVModel } from '@vueuse/core'

const props = defineProps({
  formData: {
    type: Object as PropType<typeof RichText>,
    required: true,
  },
})

const emit = defineEmits(['update:formData'])
const formData = useVModel(props, 'formData', emit)
</script>

<template>
  <RichEditor v-model:text="formData.text" />
</template>

<style lang="scss" scope></style>
