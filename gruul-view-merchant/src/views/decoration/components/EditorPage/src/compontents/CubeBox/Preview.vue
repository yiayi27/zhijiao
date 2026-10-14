<script setup lang="ts">
// 定义类型接口
interface CubeFormData {
  subEntry: Array<{
    img?: string
    x: number
    y: number
    width: number
    height: number
  }>
  layoutWidth: number
  layoutHeight: number
  borderWidth: number
  pageMargin: number
}

interface CubeCoverDiv {
  top: string
  left: string
  width: string
  height: string
  paddingTop: string
  img: string
  borderWidth: string
}

const props = defineProps({
  formData: {
    type: Object as () => CubeFormData,
    default: () => ({
      subEntry: [],
      layoutWidth: 0,
      layoutHeight: 0,
      borderWidth: 0,
      pageMargin: 0,
    }),
  },
})

const pageMarginStyle = reactive({
  width: 0,
  height: 0,
  margin: 0,
})

const showCubeListWrap = ref<CubeCoverDiv[]>([])
const showCubeList = ref(false)

onMounted(() => {
  if (props.formData) {
    showCubeList.value = showTxt()
    if (showCubeList.value) {
      drawCube()
    }
  }
})

function showTxt() {
  return !!props.formData.subEntry.filter((item) => !!item.img).length
}

watch(
  () => props.formData,
  () => {
    drawCube()
  },
  { deep: true },
)

function drawCube() {
  if (props.formData) {
    showCubeList.value = showTxt()
    const perviewLayoutWidth = 370
    const item = props.formData
    const layoutWidth = item.layoutWidth
    const layoutHeight = item.layoutHeight
    // perviewLayoutWidth = perviewLayoutWidth - item.pageMargin * 2;
    const wrapWith = perviewLayoutWidth + item.borderWidth - item.pageMargin * 2
    const styleWidth = wrapWith / layoutWidth
    const styleHeight = layoutHeight !== 1 ? perviewLayoutWidth / layoutHeight : styleWidth
    drawCubeWrap(styleWidth, styleHeight, wrapWith)
  }
}

/**
 * 根据每个对应数据绘画出各个位置
 */
function drawCubeWrap(divWidth: number, divHeight: number, wrapWith: number) {
  const item = props.formData
  const subEntry = item.subEntry
  showCubeListWrap.value = []
  let maxY = 0
  let maxIndex = 0
  let maxHeght = 0
  if (subEntry.length) {
    for (let i = 0; i < subEntry.length; i++) {
      const a = subEntry[i]
      const coverDiv: CubeCoverDiv = {
        top: `${a.y * divHeight}px`,
        left: `${a.x * divWidth + item.pageMargin}px`,
        width: `${divWidth * a.width - item.borderWidth}px`,
        height: `${divHeight * a.height - item.borderWidth}px`,
        paddingTop: `${(divHeight * a.height) / 2}px`,
        img: a.img ? a.img : '',
        borderWidth: `${item.borderWidth / 2}px`,
      }
      if (maxY <= a.y) {
        maxY = a.y
        maxIndex = i
      }
      showCubeListWrap.value.push(coverDiv)
    }

    maxHeght = maxY + subEntry[maxIndex].height < item.layoutHeight ? maxY + subEntry[maxIndex].height : item.layoutHeight
    pageMarginStyle.width = wrapWith
    pageMarginStyle.height = divHeight * maxHeght
    pageMarginStyle.margin = -item.borderWidth / 2
  }
}
</script>

<template>
  <div class="rc-design-react-preview rc-design-component-default-preview">
    <div v-if="!showCubeList" class="rc-design-component-default-preview__text">点击编辑魔方</div>
    <div v-else class="cap-cube-wrap">
      <div
        class="cap-cube"
        :style="{
          width: `${pageMarginStyle.width}px`,
          height: `${pageMarginStyle.height}px`,
          margin: `${pageMarginStyle.margin}px`,
        }"
      >
        <div
          v-for="(item, index) in showCubeListWrap"
          :key="index"
          class="cap-cube__item"
          :style="{
            width: item.width,
            height: item.height,
            top: item.top,
            left: item.left,
            backgroundImage: `url(${item.img})`,
            margin: item.borderWidth,
          }"
        >
          <img v-if="item.img" class="cap-cube__table-image cap-cube__table-image--invisible" :src="item.img" />
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scope>
@import '@/assets/styles/decoration/cubeBox.scss';
</style>
