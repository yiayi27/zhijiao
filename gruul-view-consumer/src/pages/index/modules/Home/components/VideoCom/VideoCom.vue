<template>
  <view class="video--com" :style="'padding:' + (radioTC === 2 ? 20 : 0) + 'rpx'">
    <video :src="radio === 1 ? video : videoLink" :style="'width:' + videowidth + 'rpx;height:' + videoheight + 'rpx'" :poster="poster" />
  </view>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

// 定义接口
interface VideoProperties {
  VideoCom?: string
  height?: string
  link?: Obj
  poster?: string
  radio?: number
  radioBL?: number
  radioTC?: number
  video?: string
  videoLink?: string
  width?: string
}

// 定义props
const props = defineProps<{
  decoretionProperties?: VideoProperties
}>()

// 响应式变量
const VideoCom = ref('')
const height = ref('')
const link = ref<Obj>({})
const poster = ref('')
const radio = ref(1)
const radioBL = ref(1)
const radioTC = ref(1)
const video = ref('')
const videoLink = ref('')
const width = ref('')
const videowidth = ref(750)
const videoheight = ref(0)

// 方法定义
const getProperties = (formdata: VideoProperties) => {
  if (!formdata) return

  VideoCom.value = formdata.VideoCom || ''
  height.value = formdata.height || ''
  link.value = formdata.link || {}
  poster.value = formdata.poster || ''
  radio.value = formdata.radio || 1
  radioBL.value = formdata.radioBL || 1
  radioTC.value = formdata.radioTC || 1
  video.value = formdata.video || ''
  videoLink.value = formdata.videoLink || ''
  width.value = formdata.width || ''
  getvideo()
}

const getvideo = () => {
  if (radioTC.value === 2) {
    videowidth.value = 710
  }
  if (radioBL.value === 1) {
    videoheight.value = (videowidth.value / 16) * 9
  }
  if (radioBL.value === 2) {
    videoheight.value = videowidth.value
  }
  if (radioBL.value === 3) {
    const url = radio.value === 1 ? video.value : videoLink.value
    // const getVideoInfo = this.getVideoInfo;
    uni.downloadFile({
      //需要先下载文件获取临时文件路径 单个文件大小不得超过50M
      url: url,
      success: (res1: any) => {
        getVideoInfo(res1.tempFilePath)
      },
    })
  }
}

const getVideoInfo = (url: string) => {
  uni.getVideoInfo({
    src: url,
    success: (res) => {
      const bit = res.height / res.width
      videoheight.value = videowidth.value * bit
    },
    fail(err) {
      console.log(err)
    },
  })
}

// 监听props变化
watch(
  () => props.decoretionProperties,
  (newVal) => {
    if (newVal) {
      getProperties(newVal)
    }
  },
  {
    immediate: true,
    deep: true,
  },
)
</script>

<style lang="scss" scoped>
.video--com {
  width: 100%;
  background-color: #fff;
}
</style>
