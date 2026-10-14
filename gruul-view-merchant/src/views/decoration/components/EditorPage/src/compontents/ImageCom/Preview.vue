<script setup lang="ts">
import { useVModel } from '@vueuse/core'
import ImageCom from './ImageCom'

const props = defineProps({
  formData: {
    type: Object as PropType<typeof ImageCom>,
    default: () => ImageCom,
  },
})

const emit = defineEmits(['update:formData'])
const formData = useVModel(props, 'formData', emit)

// resize方法
function onResize(_handle: string, x: number, y: number, width: number, height: number) {
  formData.value.top = y
  formData.value.left = x
  formData.value.width = width
  formData.value.height = height
}

function onDrag(x: number, y: number) {
  formData.value.top = y
  formData.value.left = x
}
</script>

<template>
  <div
    class="image-container"
    style="margin-top: 5px; position: relative"
    :style="{ height: `${formData.boxHeight}px` }"
    @mousedown.stop.prevent
    @mousemove.stop.prevent
  >
    <vue-draggable-resizable
      :w="formData.width"
      :h="formData.height"
      :x="formData.left"
      :y="formData.top"
      :parent="true"
      :on-resize="onResize"
      :on-drag="onDrag"
      style="z-index: 10"
    >
      <div class="box" :style="{ width: `${+formData.width}px`, height: `${+formData.height}px` }">
        <img :src="formData.img" class="w-full h-full" />
      </div>
    </vue-draggable-resizable>
  </div>
</template>

<style lang="scss" scoped>
.image-container {
  isolation: isolate;
  overflow: hidden;
}

.box {
  pointer-events: auto;
}
</style>
