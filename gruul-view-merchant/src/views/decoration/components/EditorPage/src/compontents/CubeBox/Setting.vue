<script setup lang="ts">
import { upLoad } from '@/api/index'
import LinkSelect from '@/components/LinkSelect'
import { useDecorationStore } from '@/store/modules/decoration'
import { CircleCloseFilled, Plus } from '@element-plus/icons-vue'
import { storeToRefs } from 'pinia'

import templateList from './template'

interface SubEntry {
  x: number
  y: number
  width: number
  height: number
  img?: string
  link?: any
  linkName?: string
  [key: string]: any
}

interface FormData {
  showMethod: number
  layoutWidth: number
  layoutHeight: number
  subEntry: SubEntry[]
  [key: string]: any
}

interface TemplateItem {
  layoutWidth: number
  layoutHeight: number
  subEntry: SubEntry[]
  img: string
  title: string
  showMethod?: number
  pageMargin?: number
  width?: number
  [key: string]: any
}

interface CubeListItem {
  style: {
    width: string
    height: string
  }
  coordinates: string
  selected: boolean
  finish: number
  start: number
}

const props = defineProps({
  formData: {
    type: Object,
    required: true,
  },
})

const emit = defineEmits(['update:formData'])

const activeItem = computed({
  get: () => props.formData as FormData,
  set: (value) => emit('update:formData', value),
})

const decorationStore = useDecorationStore()
const { activeComIndex } = storeToRefs(decorationStore)

// 模版选择list
const template1 = ref<TemplateItem[]>(templateList as TemplateItem[])
const showCubeList = ref<CubeListItem[][]>([])
const showCubeListWrap = ref<any[]>([])
const activeShowCubeListWrap = ref(0)
const mouseOverSeleted = ref<any>(null)
const isSeletedMidu = ref(false)

const coordinates = reactive({
  rowStart: 0, // x 行
  columnStart: 0, // y 列
})

const ranksNumber = reactive({
  rowCount: 1,
  columnCount: 1,
})

const styleWidth = ref(0) // 初始化后单个模块的宽度
const styleHeight = ref(0) // 初始化后单个模块的高度

const options = ref([
  // 尺寸属性
  { value: '4x4', label: '4x4' },
  { value: '5x5', label: '5x5' },
  { value: '6x6', label: '6x6' },
  { value: '7x7', label: '7x7' },
])

const miduValue = ref('4x4') // 初始化属性

// 切换相同组件清除缓存
watch(activeComIndex, () => {
  resetCube()
})

// 清除缓存
function resetCube() {
  mouseOverSeleted.value = null
  activeShowCubeListWrap.value = 0
  showCubeListWrap.value = []
  showCubeList.value = []
  isSeletedMidu.value = false
  coordinates.rowStart = 0
  coordinates.columnStart = 0
  ranksNumber.rowCount = 0
  ranksNumber.columnCount = 0
}

/**
 * 选择模块属性
 */
function changeTemplate(item: TemplateItem, index: number): void {
  mouseOverSeleted.value = null
  activeShowCubeListWrap.value = 0
  showCubeListWrap.value = []
  showCubeList.value = []
  isSeletedMidu.value = false
  activeItem.value.showMethod = index
  if (index === 7) {
    // 自定义魔方
    miduValue.value = '4x4'
    onChangeMidu()
  } else {
    activeItem.value.layoutWidth = item.layoutWidth
    activeItem.value.layoutHeight = item.layoutHeight
    if (!activeItem.value.subEntry.length) {
      activeItem.value.subEntry = item.subEntry
    } else {
      const dist = item.subEntry.length > activeItem.value.subEntry.length ? activeItem.value.subEntry.length : item.subEntry.length
      for (let i = 0; i < dist; i++) {
        const item1 = item.subEntry[i]
        if (item1) {
          item1.img = activeItem.value.subEntry[i]?.img || ''
          item1.link = activeItem.value.subEntry[i]?.link
          item1.linkName = activeItem.value.subEntry[i]?.linkName
        }
      }
      activeItem.value.subEntry = item.subEntry
    }
  }
}

/**
 * 初始化魔方
 */
onMounted(() => {
  if (activeItem.value.showMethod === 7) {
    miduValue.value = `${activeItem.value.layoutWidth}x${activeItem.value.layoutHeight}`
  }
  drawCube()
})

/**
 * 选择魔方密度转变
 */
function onChangeMidu() {
  const arr = miduValue.value.split('x')
  activeItem.value.layoutWidth = Number(arr[0])
  activeItem.value.layoutHeight = Number(arr[1])
  activeItem.value.subEntry = []
  mouseOverSeleted.value = null
  activeShowCubeListWrap.value = 0
  showCubeListWrap.value = []
  showCubeList.value = []
  isSeletedMidu.value = false
  drawCube(true)
}

// 监听activeItem变化
watch(
  () => activeItem.value,
  () => {
    drawCube()
  },
  { deep: true },
)

/**
 * 魔方密度选择后产生相对应魔方数据
 */
function drawCube(isChangeMidu = false) {
  if (activeItem.value) {
    const perviewLayoutWidth = 322
    if (showCubeList.value.length && activeItem.value.showMethod === 7 && !isChangeMidu) {
      // 拦截密度选择截取
    } else {
      showCubeList.value = []
      const item = activeItem.value
      const layoutWidth = item.layoutWidth
      const layoutHeight = item.layoutHeight
      // 画魔方

      const width = Math.ceil(perviewLayoutWidth / layoutWidth)
      const height = layoutHeight !== 1 ? Math.ceil(perviewLayoutWidth / layoutHeight) : width
      for (let i = 0; i < layoutWidth; i++) {
        const ul: CubeListItem[] = []
        for (let j = 0; j < layoutHeight; j++) {
          const li: CubeListItem = {
            style: {
              width: `${width}px`,
              height: `${height}px`,
            },
            coordinates: `${i}:${j}`,
            selected: false,
            finish: 0,
            start: 0,
          }
          ul.push(li)
        }
        showCubeList.value.push(ul)
      }
      styleWidth.value = width
      styleHeight.value = height
    }
    drawCubeWrap(styleWidth.value, styleHeight.value)
  }
}

/**
 * 魔方密度选择后产生后针对对应魔方数据绘画出对应的魔方
 */
function drawCubeWrap(divWidth: number, divHeight: number): void {
  const item = activeItem.value
  const subEntry = item.subEntry
  const showMethod = activeItem.value.showMethod
  showCubeListWrap.value = []
  if (subEntry.length) {
    for (let i = 0; i < subEntry.length; i++) {
      const coverDiv: {
        top: string
        left: string
        width: string
        height: string
        paddingTop: string
        img: string
        text?: string
      } = {
        top: `${subEntry[i].y * divHeight}px`,
        left: `${subEntry[i].x * divWidth}px`,
        width: `${divWidth * subEntry[i].width}px`,
        height: `${divHeight * subEntry[i].height}px`,
        paddingTop: `${(divHeight * subEntry[i].height) / 2}px`,
        img: subEntry[i].img || '',
      }
      showCubeList.value[subEntry[i].x][subEntry[i].y].selected = true
      showCubeList.value[subEntry[i].x][subEntry[i].y].finish = 1
      for (let j = 1; j < subEntry[i].width; j++) {
        showCubeList.value[subEntry[i].x + j][subEntry[i].y].selected = true
        showCubeList.value[subEntry[i].x + j][subEntry[i].y].finish = 1
      }
      for (let l = 1; l < subEntry[i].height; l++) {
        showCubeList.value[subEntry[i].x][subEntry[i].y + l].selected = true
        showCubeList.value[subEntry[i].x][subEntry[i].y + l].finish = 1
      }
      if (showMethod === 0 || showMethod === 1 || showMethod === 2) {
        coverDiv.text = `宽度${Math.ceil(750 / item.layoutWidth)}像素`
      } else {
        coverDiv.text = `${Math.ceil((750 / item.layoutWidth) * subEntry[i].width)}像素${Math.ceil(
          (750 / item.layoutHeight) * subEntry[i].height,
        )}或同等比例`
      }
      showCubeListWrap.value.push(coverDiv)
    }
  }
}

/**
 * 魔方密度选择后产生后针对魔方绘制画出对应的魔方
 */
function onShowCubeListWrap(index: number): void {
  activeShowCubeListWrap.value = index
}

/**
 * 点击任意方块度时候判断该方块是起始位置还是结束位置
 */
function onSelectMidu(index1: number, index2: number): void {
  // 选择密度
  const selected = showCubeList.value[index1][index2].selected
  if (isSeletedMidu.value && !selected) {
    return
  }
  if (selected) {
    // 再次确认
    const { rowStart, columnStart } = getItemStart()
    mouseOverSeleted.value = null
    isSeletedMidu.value = false
    let rowEnd = index1
    let columnEnd = index2

    // Create local variables to handle modified values
    let startRow = rowStart
    let startCol = columnStart

    if (startRow !== undefined && rowEnd !== undefined && startRow > rowEnd) {
      startRow = rowEnd
      rowEnd = startRow
    }

    // 如果开始列大于结束列，则开始列用结束列，结束列用开始列
    if (startCol !== undefined && columnEnd !== undefined && startCol > columnEnd) {
      startCol = columnEnd
      columnEnd = startCol
    }

    activeItem.value.subEntry.push({
      x: startRow || 0,
      y: startCol || 0,
      width: ranksNumber.rowCount,
      height: ranksNumber.columnCount,
      img: '',
      link: {
        id: null,
        type: null,
        name: '',
        url: '',
        append: '',
      },
      linkName: '',
    })
    showCubeList.value.forEach((item1, index3) => {
      // 所有归零
      item1.forEach((item2, index4) => {
        showCubeList.value[index3][index4].start = 0
      })
    })
    showCubeList.value.forEach((t, i) => {
      if (Array.isArray(t) && t.length) {
        t.forEach((t1, j) => {
          if (t1.selected) {
            showCubeList.value[i][j].finish = 1
          }
        })
      }
    })
  } else {
    mouseOverSeleted.value = null
    showCubeList.value[index1][index2].selected = true
    showCubeList.value[index1][index2].start = 1
    isSeletedMidu.value = true
    coordinates.rowStart = index1
    coordinates.columnStart = index2
  }
}

/**
 * 获取用户点击开始的坐标数据
 */
function getItemStart(): { rowStart: number | undefined; columnStart: number | undefined } {
  let rowStart: number | undefined
  let columnStart: number | undefined
  showCubeList.value.forEach((item1, index3) => {
    item1.forEach((item2, index4) => {
      if (item2.start) {
        rowStart = index3
        columnStart = index4
      }
    })
  })
  return { rowStart, columnStart }
}

/**
 *鼠标在任意方块的上滑动计算方块队列
 */
function onMouseOver(index1: number, index2: number): void {
  showCubeList.value.forEach((item1, index3) => {
    item1.forEach((item2, index4) => {
      if (!item2.finish) {
        showCubeList.value[index3][index4].selected = false
      }
    })
  })
  if (isSeletedMidu.value) {
    // 鼠标移动
    const { rowStart, columnStart } = getItemStart()
    let rowEnd = index1
    let columnEnd = index2

    // Create local variables to handle possibly undefined values
    let startRow = rowStart !== undefined ? rowStart : 0
    let startCol = columnStart !== undefined ? columnStart : 0

    // 如果开始行大于结束行，则开始行用结束行，结束行用开始行
    if (startRow > rowEnd) {
      const temp = startRow
      startRow = rowEnd
      rowEnd = temp
    }
    // 如果开始列大于结束列，则开始列用结束列，结束列用开始列
    if (startCol > columnEnd) {
      const temp = startCol
      startCol = columnEnd
      columnEnd = temp
    }

    let rowCount = 0 // 总行数
    const columnCount: number[] = [] // 总列数
    let isAdd = true

    for (let forRowStart = startRow; forRowStart <= rowEnd; forRowStart++) {
      rowCount++
      // 遍历列
      for (let i = startCol; i <= columnEnd; i++) {
        // 当前行列坐标
        // 检测当前的模块是否被占用，如果被占用，那么整个都不能选择
        if (showCubeList.value[forRowStart]?.[i]?.finish) {
          isAdd = false
          break
        }
        if (!inArray(i, columnCount)) columnCount.push(i)
        if (showCubeList.value[forRowStart]?.[i]) {
          showCubeList.value[forRowStart][i].selected = true
        }
      }
    }

    if (!isAdd) {
      let rowStart1: number | undefined
      let columnStart1: number | undefined
      showCubeList.value.forEach((item1, index1) => {
        item1.forEach((item2, index2) => {
          if (!item2.finish) {
            showCubeList.value[index1][index2].selected = false
          }
          if (item2.start) {
            rowStart1 = index1
            columnStart1 = index2
          }
        })
      })
      if (rowStart1 !== undefined && columnStart1 !== undefined) {
        showCubeList.value[rowStart1][columnStart1].selected = true
      }
    }
    ranksNumber.rowCount = rowCount
    ranksNumber.columnCount = columnCount.length
  }
}

/**
 *点击魔方块的showCubeListWrap下标
 */
function onMouseOverSeleted(index: number) {
  mouseOverSeleted.value = index
}

/**
 * 监听该模快是否被删除
 */
function delSelected(i: number) {
  const subEntry = activeItem.value.subEntry

  // 使用嵌套循环遍历整个矩形区域的所有单元格
  for (let x = 0; x < subEntry[i].width; x++) {
    for (let y = 0; y < subEntry[i].height; y++) {
      const posX = subEntry[i].x + x
      const posY = subEntry[i].y + y
      showCubeList.value[posX][posY].selected = false
      showCubeList.value[posX][posY].finish = 0
      showCubeList.value[posX][posY].start = 0
    }
  }

  showCubeList.value.forEach((item1, index3) => {
    // 清除选中的
    item1.forEach((item2, index4) => {
      if (!item2.finish) {
        showCubeList.value[index3][index4].selected = false
      }
      showCubeList.value[index3][index4].start = 0
    })
  })
  mouseOverSeleted.value = null
  isSeletedMidu.value = false
  activeShowCubeListWrap.value = 0
  ranksNumber.rowCount = 1
  ranksNumber.columnCount = 1
  activeItem.value.subEntry.splice(i, 1)
}

function inArray(elem: number, arr: number[]): boolean {
  let i = 0
  const n = arr.length
  for (; i < n; ++i) {
    if (arr[i] === elem) return true
  }
  return false
}

/**
 * 选择图片上传
 */
async function handleChange(file: any) {
  const img = await upLoadFile(file)
  const index = activeShowCubeListWrap.value
  activeItem.value.subEntry[index].img = img
  showCubeListWrap.value[index].img = img
}

async function upLoadFile(file: any) {
  const whiteList = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif']
  const isLt2M = file.size < 2 * 1024 * 1024
  if (!whiteList.includes(file.raw.type)) {
    ElMessage.error('上传文件只能是GIF, JPG或PNG 格式!')
    return
  }
  if (!isLt2M) {
    ElMessage.error('上传文件大小不能超过 2MB!')
    return
  }
  const res = await upLoad({
    file: file.raw,
  })
  return res.data
}
</script>

<template>
  <!-- 魔方 -->
  <div clas="zent-design-editor-item">
    <div class="zent-design-editor__control-group">
      <div class="zent-design-editor__control-group-container">
        <div class="zent-design-editor__control-group-label zent-design-editor__control-group-label--top" title="选择模版">模版</div>
        <div class="zent-design-editor__control-group-control">
          <div>
            <div
              v-for="(item, index4) in template1"
              :key="`template${index4}`"
              class="rc-design-select-templates"
              :class="{ active: index4 === activeItem.showMethod }"
              :data-length="index4 !== 7 && item.subEntry ? item.subEntry.length : 0"
              @click="changeTemplate(item, index4)"
            >
              <div class="rc-design-select-templates__image-block">
                <img :src="item.img" width="94px" height="64px" alt="temp" />
              </div>
              <div class="rc-design-select-templates__title">
                {{ item.title }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-if="activeItem.showMethod === 7" class="zent-design-editor__control-group">
      <div class="zent-design-editor__control-group-container">
        <div class="zent-design-editor__control-group-label">
          <!-- react-text: 2624 -->
          密度:
          <!-- /react-text -->
        </div>
        <div class="zent-design-editor__control-group-control">
          <div class="zent-popover-wrapper zent-select" style="display: inline-block">
            <el-select v-model="miduValue" placeholder="请选择" class="w-[200px]" @change="onChangeMidu">
              <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </div>
        </div>
      </div>
    </div>
    <div class="zent-design-editor__control-group">
      <div class="zent-design-editor__control-group-container">
        <div class="zent-design-editor__control-group-label zent-design-editor__control-group-label--top">
          <!-- react-text: 2907 -->
          布局:
          <!-- /react-text -->
        </div>
        <div class="zent-design-editor__control-group-control">
          <div class="rc-design-component-cube clearfix">
            <ul v-for="(item, index1) in showCubeList" :key="index1" class="cube-row">
              <li
                v-for="(item1, index2) in item"
                :ref="`cube-item-${index1}-${index2}`"
                :key="`cube-item-${index2}`"
                class="cube-item"
                :class="{ 'item-selected': item1.selected }"
                :style="{
                  width: item1.style.width,
                  height: item1.style.height,
                }"
                :data-coordinates="item1.coordinates"
                :data-ref="`cube-item-${index1}-${index2}`"
                :data-selected="item1.selected ? 1 : 0"
                :data-start="item1.start"
                @click.stop="onSelectMidu(index1, index2)"
                @mouseover="onMouseOver(index1, index2)"
              >
                <span v-if="!item1.selected" class="plus-icon" :style="{ 'line-height': item1.style.height }">+</span>
              </li>
            </ul>
            <div
              v-for="(item, index3) in showCubeListWrap"
              :key="`cube-selected-${index3}`"
              class="cube-selected"
              :class="{
                'cube-selected-click': activeShowCubeListWrap === index3,
              }"
              :style="{
                width: item.width,
                height: item.height,
                top: item.top,
                left: item.left,
              }"
              @click.stop="onShowCubeListWrap(index3)"
              @mouseover.stop="onMouseOverSeleted(index3)"
            >
              <el-icon
                v-if="activeItem.showMethod === 7 && mouseOverSeleted === index3 && activeShowCubeListWrap === index3"
                class="rc-design-editor-card-item-delete"
                @click.stop="delSelected(index3)"
              >
                <CircleCloseFilled />
              </el-icon>

              <div v-if="!activeItem.subEntry[index3].img" class="cube-selected-text">
                {{ item.text }}
              </div>
              <img v-else :src="activeItem.subEntry[index3].img" :style="{ width: item.width, height: item.height }" />
              <div
                v-if="activeShowCubeListWrap === index3 && activeItem.subEntry[index3].img"
                class="iconfont iconshangpinxiangqing-baozhuang iconshangpinxiangqing-baozhuang-2"
              />
            </div>
          </div>
          <div class="zent-design-editor__control-group-help-desc">
            {{ activeItem.showMethod === 7 ? '选定布局区域，在下方添加图片，建议添加比例一致的图片' : '选定布局区域，在下方添加图片' }}
          </div>
        </div>
      </div>
    </div>
    <div class="zent-design-editor__control-group">
      <div class="zent-design-editor__control-group-container">
        <div class="zent-design-editor__control-group-label">间隙:</div>
        <div class="zent-design-editor__control-group-control">
          <el-slider v-model="activeItem.borderWidth" show-input class="zent-slider" :max="30" />
        </div>
      </div>
    </div>
    <div class="zent-design-editor__control-group">
      <div class="zent-design-editor__control-group-container">
        <div class="zent-design-editor__control-group-label">边距:</div>
        <div class="zent-design-editor__control-group-control">
          <el-slider v-model="activeItem.pageMargin" show-input class="zent-slider" :max="30" />
        </div>
      </div>
    </div>
    <div v-if="activeItem.subEntry.length > 0" class="rc-design-editor-card-item">
      <div class="rc-design-component-editor_subentry-item clearfix rc-design-common-choice-image-component">
        <!-- <div class="rc-design-common-choice-image-component image-editor">

        </div> -->
        <div class="subentry-item-editor-form-content">
          <div class="zent-design-editor__control-group subentry-control-group">
            <div class="zent-design-editor__control-group-container flex" style="align-items: flex-start">
              <div class="zent-design-editor__control-group-label">图片：</div>
              <div class="zent-design-editor__control-group-control">
                <el-upload class="has-not-choose-image" multiple :auto-upload="false" :show-file-list="false" :on-change="handleChange">
                  <el-icon v-if="!activeItem.subEntry[activeShowCubeListWrap].img" class="plus-icon">
                    <Plus />
                  </el-icon>
                  <div v-if="!activeItem.subEntry[activeShowCubeListWrap].img">添加图片</div>
                  <img
                    v-if="activeItem.subEntry[activeShowCubeListWrap].img"
                    class="thumb-image"
                    :src="activeItem.subEntry[activeShowCubeListWrap].img"
                    alt=""
                    width="80"
                    height="80"
                  />
                  <span v-if="activeItem.subEntry[activeShowCubeListWrap].img" class="modify-image">更换图片</span>
                </el-upload>
              </div>
            </div>
            <label class="zent-design-editor__control-group-container flex" style="align-items: flex-start">
              <div class="zent-design-editor__control-group-label">链接：</div>
              <div class="zent-design-editor__control-group-control">
                <div class="rc-choose-link-menu">
                  <div class="zent-popover-wrapper rc-choose-link-menu-popover-wrapper" style="display: inline-block">
                    <div class="rc-choose-link-menu-trigger">
                      <LinkSelect :link="activeItem.subEntry[activeShowCubeListWrap].link" />
                    </div>
                  </div>
                </div>
              </div>
            </label>
            <label v-if="activeItem.subEntry[activeShowCubeListWrap].link.name" class="zent-design-editor__control-group-container flex">
              <div class="zent-design-editor__control-group-label">页面：</div>
              <div class="zent-design-editor__control-group-control">
                <div class="rc-choose-link-menu">
                  <div class="zent-popover-wrapper rc-choose-link-menu-popover-wrapper" style="display: inline-block">
                    <div class="rc-choose-link-menu-trigger">
                      {{ activeItem.subEntry[activeShowCubeListWrap].link.name }}
                    </div>
                  </div>
                </div>
              </div>
            </label>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scope>
@import '@/assets/styles/decoration/cubeBox.scss';
</style>
