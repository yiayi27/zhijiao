<template>
  <!-- 魔方组件 -->
  <view class="rc-design-react-preview rc-design-component-default-preview">
    <view v-if="showCubeListWrap.length > 0" class="cap-cube-wrap">
      <view class="cap-cube" :style="pageMarginStyle.style">
        <view
          v-for="(item, index) in showCubeListWrap"
          :key="index"
          class="cap-cube__item"
          :style="item.style"
          :data-index="index"
          @tap.stop="handleTap"
        >
          <!-- <image class="cap-cube__table-image cap-cube__table-image--invisible" src="{{item.img}}" wx:if="{{item.img}}" mode="aspectFill" /> -->
          <uv-button v-if="subEntry[index].link.type === 0 && subEntry[index].link.id === 8" open-type="contact" class="contact-style" />
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { navtofun } from '@/utils/navtofun'

interface LinkSelectItem {
  id: number
  /** 链接类型 0 -功能页面 FunctionPage 1-商超商品 Goods 2 -展示分类 DisplayClassify 3-自定义页面 CustomPage 4 活动营销 ActivityMarket */
  type: number
  /** 链接名称 */
  name: string
  /** 链接地址 */
  url: string
  /** 附加参数 */
  append: string
}

interface IBanners {
  x: number
  y: number
  width: number
  height: number
  img: string
  link: LinkSelectItem
  linkName: string
}

interface CubeItem {
  top: string
  left: string
  width: string
  height: string
  paddingTop: string
  img: string
  borderWidth: string
  style: string
}

// 定义props
const props = defineProps({
  decoretionProperties: {
    type: Object,
    default: () => {},
  },
})

// 响应式变量
const borderWidth = ref(0)
const layoutWidth = ref(0)
const layoutHeight = ref(0)
const showMethod = ref(0)
const pageMargin = ref(0)
const width = ref(0)
const subEntry = ref<IBanners[]>([])
const showCubeListWrap = ref<CubeItem[]>([])
const pageMarginStyle = ref<Obj>({
  style: `height:400rpx`,
})

// 方法定义
const getProperties = (formdata: any) => {
  borderWidth.value = formdata.borderWidth
  layoutWidth.value = formdata.layoutWidth
  layoutHeight.value = formdata.layoutHeight
  showMethod.value = formdata.showMethod
  pageMargin.value = formdata.pageMargin
  width.value = formdata.width
  subEntry.value = formdata.subEntry
  showCubeListWrap.value = formdata.showCubeListWrap
  pageMarginStyle.value = formdata.pageMarginStyle
}

const drawCubeWrap = (divWidth: number, divHeight: number, wrapWith: number) => {
  const subEntryValue = subEntry.value
  const showCubeListWrapValue: CubeItem[] = []
  let maxY = 0,
    maxIndex = 0,
    maxHeight = 0
  if (subEntryValue.length) {
    for (let i = 0; i < subEntryValue.length; i++) {
      const a = subEntryValue[i]
      const coverDiv: CubeItem = {
        top: a.y * divHeight + 'px',
        left: a.x * divWidth + pageMargin.value + 'px',
        width: divWidth * a.width - borderWidth.value + 'px',
        height: divHeight * a.height - borderWidth.value + 'px',
        paddingTop: (divHeight * a.height) / 2 + 'px',
        img: a[`img`] ? a[`img`] : '',
        borderWidth: borderWidth.value / 2 + 'px',
        style: `width:${divWidth * a.width - borderWidth.value}px;height:${divHeight * a.height - borderWidth.value}px;top:${
          a.y * divHeight
        }px;left:${a.x * divWidth + pageMargin.value}px;magin:${borderWidth.value}px;background-image:url(${a[`img`]});`,
      }

      if (maxY <= a.y) {
        maxY = a.y
        maxIndex = i
      }
      showCubeListWrapValue.push(coverDiv)
    }
    maxHeight = maxY + subEntryValue[maxIndex].height < layoutHeight.value ? maxY + subEntryValue[maxIndex].height : layoutHeight.value
    showCubeListWrap.value = showCubeListWrapValue
    pageMarginStyle.value = {
      width: wrapWith,
      height: divHeight * maxHeight,
      margin: `-${borderWidth.value / 2}px`,
      style: `width:${wrapWith}px;height:${divHeight * maxHeight - borderWidth.value}px;`,
    }
  }
}

const drawCube = async () => {
  uni.getSystemInfo({
    success: (res) => {
      const perviewLayoutWidth = res.windowWidth
      const layoutWidthValue = layoutWidth.value
      const layoutHeightValue = layoutHeight.value
      const wrapWith = perviewLayoutWidth + borderWidth.value - pageMargin.value * 2
      const styleWidth = wrapWith / layoutWidthValue
      const styleHeight = layoutHeightValue !== 1 ? perviewLayoutWidth / layoutHeightValue : styleWidth
      drawCubeWrap(styleWidth, styleHeight, wrapWith)
    },
  })
}

const handleTap = (e: { currentTarget: { dataset: { index: number } } }) => {
  const index: number = e.currentTarget.dataset.index
  const currentSwiperItem = subEntry.value[index]
  const itemLink = currentSwiperItem.link
  navtofun(itemLink)
}

// 监听props变化
watch(
  () => props.decoretionProperties,
  (newVal) => {
    if (newVal) {
      getProperties(newVal)
      drawCube()
    }
  },
  {
    immediate: true,
    deep: true,
  },
)
</script>

<style lang="scss" scoped>
.rc-design-react-preview {
  position: relative;
  width: 100%;
  overflow: hidden;
  background: #fff;
}

.cap-cube__item {
  position: absolute;
  background-repeat: no-repeat;
  background-size: cover;
  background-position: 50%;
  overflow: hidden;
}

image {
  width: 100%;
}

.cap-cube {
  position: relative;
}

@include b(contact-style) {
  position: relative;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
}
</style>
