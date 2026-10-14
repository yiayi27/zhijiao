<template>
  <view class="boxContent" :style="'height:' + imageContent.boxHeight + 'px'">
    <image
      :data-item="imageContent"
      :src="imageContent.img"
      :style="
        'width:' +
        imageContent.width +
        ';height:' +
        imageContent.height +
        ';margin-left:' +
        imageContent.newLeft +
        'px;margin-top:' +
        imageContent.top +
        'px'
      "
      @tap="clickImg"
    />
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { navtofun } from '@/utils/navtofun'

// 定义图片内容接口
interface ImageContent extends Obj {
  boxHeight?: number
  img?: string
  width?: string
  height?: string
  newLeft?: Long
  top?: number
  left?: number
  link?: any
}

// 定义props
const props = defineProps<{
  decoretionProperties?: ImageContent
}>()

// 响应式变量
const imageContent = ref<ImageContent>({})

// 方法定义
const getImageCom = () => {
  uni.getSystemInfo({
    success: (res) => {
      try {
        if (!props.decoretionProperties) return

        let pWidth = 0
        pWidth = res.windowWidth
        const decoretionPropertiesCopy = { ...props.decoretionProperties }
        const width = decoretionPropertiesCopy.width

        if (width && typeof width === 'string') {
          const match = width.match(/(\S*)px/)
          if (match && match[1]) {
            const imgWidth = Number(match[1])
            const newLeft = ((decoretionPropertiesCopy.left || 0) * (pWidth - imgWidth)) / (371 - imgWidth)
            decoretionPropertiesCopy.newLeft = newLeft.toFixed(0)

            if (decoretionPropertiesCopy.width === '742rpx') {
              decoretionPropertiesCopy.width = '100%'
            }

            imageContent.value = decoretionPropertiesCopy
          }
        }
      } catch (err) {
        console.log(err)
      }
    },
  })
}

const clickImg = (e: { currentTarget: { dataset: { item: ImageContent } } }) => {
  const item = e.currentTarget.dataset.item
  const itemLink = item.link
  navtofun(itemLink)
}

// 生命周期钩子
onMounted(() => {
  getImageCom()
})
</script>

<style lang="scss" scoped>
.boxContent {
  // width: 100%;
  background-color: white;
}
</style>
