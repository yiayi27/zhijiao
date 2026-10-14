<script setup lang="ts">
import VideoCom from './VideoCom'

const props = defineProps({
  formData: {
    type: Object as PropType<typeof VideoCom>,
    default: () => VideoCom,
  },
})

const upLoadiVideo = ref(props.formData)
</script>

<template>
  <div class="videoCom" :style="{ padding: upLoadiVideo.radioTC === 2 ? '10px' : '' }">
    <img src="https://qiniu-app.qtshe.com/u391.png" style="width: 100%; height: 200px" />
    <i
      class="iconfont iconzhibo1 videoIcon"
      :style="{ bottom: upLoadiVideo.radioTC === 2 ? '25px' : '10px', left: upLoadiVideo.radioTC === 2 ? '20px' : '5px' }"
    />
  </div>
</template>

<style lang="scss" scoped>
.videoCom {
  position: relative;
}

.videoIcon {
  position: absolute;
  font-size: 26px;
  color: #a1a1a1;
}
</style>
