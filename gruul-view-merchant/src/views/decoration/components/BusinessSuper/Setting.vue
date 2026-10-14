<script setup lang="ts">
import type { SwiperListItem, Navigation, Category as CategoryType, Chart } from './BusinessSuper'
import BusinessSuper from './BusinessSuper'
import { getList } from '@/api/good/goods'
import { upLoad } from '@/api/index'
import LinkSelect from '@/components/LinkSelect'
import { useDecorationStore } from '@/store/modules/decoration'
import { Plus, Remove } from '@element-plus/icons-vue'
import { useVModel } from '@vueuse/core'
import { storeToRefs } from 'pinia'

import { bus, FirstCategory } from './BusinessSuper'
import Category from './components/Category.vue'
import { ElMessage } from 'element-plus'

// 为mitt定义事件类型
interface Events {
  firstCategoryId: string
  selectedFirstCategory: void
}

// 类型定义
interface LocalChart extends Chart {
  sortId?: number
  category: CategoryType
}

// 增强Navigation类型，明确定义class和chartList属性
interface EnhancedNavigation extends Navigation {
  class: string[]
  chartList: LocalChart[]
}

interface UploadFile {
  raw: File
  size: number
  type?: string
}

// 增强的FirstCategory类型，处理navigationStyle可能缺失的情况
type EnhancedFirstCategory = Omit<FirstCategory, 'navigation' | 'chartList'> & {
  navigation: EnhancedNavigation[]
  navigationStyle: number
  classChart: LocalChart[]
}

// 对BusinessSuper类型进行扩展
type ExtendedBusinessSuper = typeof BusinessSuper & {
  [key: string]: any
}

// Props
const props = defineProps({
  formData: {
    type: Object as PropType<ExtendedBusinessSuper>,
    required: true,
  },
})

const emit = defineEmits(['update:formData'])
const formData = useVModel(props, 'formData', emit)

// Refs
const CategoryRef = ref<InstanceType<typeof Category> | null>(null)

// Store
const decorationStore = useDecorationStore()
const { saleMode } = storeToRefs(decorationStore)

// 状态
const currentSwiperItem = ref<SwiperListItem>({} as SwiperListItem)

/** 分类图片对象 */
const currentChartItem = ref<LocalChart>({
  img: '',
  title: '',
  category: {
    id: '',
    name: '',
    parentId: '',
    productNumber: 0,
    showCategoryVos: [],
  },
})

/** 当前编辑的一级分类对象 */
const currentFirstCategory = ref<FirstCategory>(new FirstCategory())

/** 拖动的组件的下角标 */
const dragStarIndex = ref(-1)

/** 类目拖动下角标 */
const classDragIndex = ref(-1)

/** 添加导航拖动下角标 */
const navigationDrag = ref(-1)

/** 分类图拖动下角标 */
const chartDragIndex = ref(-1)

/** 选择商品分类 */
const dialogShow = ref(false)

/** 剩余的二级分类 */
const surplusClass = ref<CategoryType[]>([])

/** 商品加载数据参数 */
const goodsParam = ref({
  /** 分类ID */
  showCategoryId: '',
  /** 页码 */
  current: 1,
  /** 大小 */
  size: 10,
})

// 监听属性变化
watch(saleMode, () => {
  filterCategory()
})

const isShowNav = computed(() => {
  const { selectMode, selectType } = formData.value
  const { navigation = [] } = currentFirstCategory.value
  return selectMode === 2 && selectType === 2 && navigation.length
})

/**
 * 是否是侧边栏、分类列表
 */
const isSideClassification = computed(() => {
  const { selectMode = 0, selectType = 0 } = formData.value
  return selectMode === 2 && selectType === 2
})

/**
 * 获取当前可选择到二级分类
 */
function getCanSelectClass(nav: Navigation): LocalChart[] {
  const { classChart = [], navigation = [] } = currentFirstCategory.value
  // 安全地访问 navigation.find()
  const foundNav = navigation.find((i) => i.navType === 1)
  const temp = foundNav?.class ? [...foundNav.class] : []

  if (nav.class) {
    temp.push(...nav.class)
  }

  return (classChart as LocalChart[]).filter((k) => temp.includes(k.title))
}

onMounted(() => {
  /** 监听一级分类选择切换 */
  ;(bus as unknown as { on<K extends keyof Events>(type: K, handler: (event: Events[K]) => void): void }).on('firstCategoryId', (data: string) => {
    curruntCateIdChange(data)
    reasetGoodsParam({
      showCategoryId: data === '-1' ? '' : Number(data),
    })
  })
  filterCategory()
})

/**
 * 初始化数据，需要过滤掉不可用掉一级分类
 */
async function filterCategory(): Promise<void> {
  const param = {
    saleMode: saleMode.value,
  }
  const res = await getList(param)
  const list = res.data.list || []
  const { firstCatList } = formData.value

  const value: FirstCategory[] = []
  if (!list.length) {
    formData.value.currentFirstCategory = currentFirstCategory.value
  }

  firstCatList.forEach((i) => {
    const category = list.find((k: any) => k.id === i.category?.id)
    if (category) {
      i.category = category
      value.push(i)
    }
  })

  formData.value.firstCatList = value
  fitlerClassSed()
}

/**
 * 过滤分类导航中选择的不可用的二级分类
 */
function fitlerClassSed(): void {
  const { firstCatList } = formData.value

  firstCatList.forEach((i) => {
    const showCategoryVos = i.category?.showCategoryVos || []
    const { classChart, navigation } = i

    if (Array.isArray(classChart)) {
      for (let d = classChart.length - 1; d >= 0; d--) {
        const k = classChart[d]
        if (!k.category) continue

        const tm = showCategoryVos.find((l: CategoryType) => l.id === k.category.id)
        if (!tm) {
          classChart.splice(d, 1)
        } else {
          k.category = tm
        }
      }
    }

    if (Array.isArray(navigation)) {
      navigation.forEach((k) => {
        if (Array.isArray(k.class)) {
          for (let m = k.class.length - 1; m >= 0; m--) {
            const j = k.class[m]
            const idx = showCategoryVos.findIndex((t: CategoryType) => t.id === j)
            if (idx !== -1) {
              if (Array.isArray(k.chartList) && k.chartList[m]) {
                const categoryMatch = showCategoryVos.find((t: CategoryType) => t.id === j)
                if (categoryMatch) {
                  k.chartList[m].category = categoryMatch
                }
              }
            } else {
              k.class.splice(m, 1)
              if (Array.isArray(k.chartList)) {
                k.chartList.splice(m, 1)
              }
            }
          }
        }
      })
    }
  })

  if (firstCatList.length) {
    formData.value.currentFirstCategory = currentFirstCategory.value = firstCatList[0]
  } else {
    formData.value.currentFirstCategory = currentFirstCategory.value
  }
}

/**
 * 重置加载商品参数
 */
function reasetGoodsParam(data: Record<string, any>): void {
  Object.assign(goodsParam.value, {
    showCategoryId: '',
    current: 1,
    size: 10,
    ...data,
  })
}

/**
 * 一级分类选择切换
 */
function curruntCateIdChange(v: string): void {
  if (v && v !== '-1') {
    const found = formData.value.firstCatList.find((i) => i.category && i.category.id === v)
    if (found) {
      formData.value.currentFirstCategory = currentFirstCategory.value = found
      nextTick(() => {
        changeNav()
      })
    }
  }
}

/**
 * 切换一级分类
 */
function changeNav(): void {
  if (currentFirstCategory.value.category && currentFirstCategory.value.category.showCategoryVos) {
    surplusClass.value = JSON.parse(JSON.stringify(currentFirstCategory.value.category.showCategoryVos))
  } else {
    surplusClass.value = []
  }
}

/**
 * 选择商品分组
 */
function chooseCategory(): void {
  dialogShow.value = true
}

/**
 * 选择一级分类
 */
function selectedCategory(list: CategoryType[] = []): void {
  const ls: Array<FirstCategory> = [...formData.value.firstCatList]
  list.forEach((i) => {
    const temp = ls.find((t) => t.category && t.category.id === i.id)
    if (!temp) {
      const first: FirstCategory = {
        category: i,
        isSelected: false,
        classChart: [],
        swiperList: [],
        navigation: [],
        navigationStyle: 1,
      } as FirstCategory
      ls.push(first)
    }
  })
  formData.value.firstCatList = ls
  ;(bus as unknown as { emit<K extends keyof Events>(type: K, event?: Events[K]): void }).emit('selectedFirstCategory')
}

/**
 * 删除一级分类
 */
function handleGoodGroupDel(id: string): void {
  const firstCatList = formData.value.firstCatList
  const index = firstCatList.findIndex((item) => item.category && item.category.id === id)
  const categoryId = currentFirstCategory.value.category?.id

  firstCatList.splice(index, 1)
  if (id === categoryId) {
    // 创建一个符合FirstCategory要求的对象
    const emptyCategory: EnhancedFirstCategory = {
      category: {} as CategoryType,
      isSelected: false,
      classChart: [],
      swiperList: [],
      navigation: [],
      navigationStyle: 1,
    }

    formData.value.currentFirstCategory = currentFirstCategory.value = emptyCategory as FirstCategory
    ;(bus as unknown as { emit<K extends keyof Events>(type: K, event?: Events[K]): void }).emit('selectedFirstCategory')
  }
}

/**
 * 被放置的容器触发事件，交换两个组件的位置
 */
function handleDropClass(i: number): false | void {
  if (classDragIndex.value === i) {
    return false
  }
  const temp = formData.value.firstCatList.splice(classDragIndex.value, 1, formData.value.firstCatList[i])
  formData.value.firstCatList.splice(i, 1, ...temp)
}

/**
 * 阻止默认行为，否则drop事件不会触发
 */
function handleDragoverClass(e: DragEvent): void {
  e.preventDefault()
}

/**
 * 开始拖动，记录拖动的组件下角标
 */
function handleDragClass(i: number): void {
  classDragIndex.value = i
}

/**
 * 选择图片上传
 */
async function handleChange(file: any): Promise<void> {
  if (file.size > 1024 * 1024 * 2) {
    ElMessage.error('文件过大，请压缩重试！')
    return
  }

  if (!(currentFirstCategory.value.category && currentFirstCategory.value.category.id)) {
    ElMessage.error('当前没有一级类目，请选择！')
    return
  }

  if (!checkoutSwiper()) {
    ElMessage.error('请填写完整再添加！')
    return
  }

  const img = await upLoadFile(file)
  if (!img) return
  const swiperListItem: SwiperListItem = {
    title: currentFirstCategory.value.category?.name || '',
    img,
    link: {
      id: 0,
      type: 0,
      name: '',
      url: '',
      append: '',
    },
    linkName: '',
  }
  if (Array.isArray(currentFirstCategory.value.swiperList)) {
    currentFirstCategory.value.swiperList.push(swiperListItem)
  }
}

/**
 * 检测上一个海报是否已经填写完整
 */
function checkoutSwiper(): boolean {
  const swiperList = currentFirstCategory.value.swiperList || []
  let flag = true
  swiperList.forEach((i) => {
    if (!i.img) flag = false
  })
  return flag
}

/**
 * 替换轮播图片
 */
async function handleItemChange(file: any): Promise<void> {
  const img = await upLoadFile(file)
  if (!img) return
  currentSwiperItem.value.img = img
}

/**
 * 点击获取需要编辑的轮播项
 */
function swiperItemClick(e: SwiperListItem): void {
  currentSwiperItem.value = e
}

async function upLoadFile(file: UploadFile): Promise<string | void> {
  const whiteList = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif']
  const isLt2M = file.size < 2 * 1024 * 1024

  if (!whiteList.includes(file.raw.type)) {
    ElMessage.error('上传文件只能是 JPG或PNG 格式!')
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

/**
 * 删除轮播组件中的图片
 */
function deleteSwiperItem(idx: number): void {
  if (Array.isArray(currentFirstCategory.value.swiperList)) {
    currentFirstCategory.value.swiperList.splice(idx, 1)
  }
}

/**
 * 开始拖动，记录拖动的组件下角标
 */
function handleDragstart(i: number): void {
  dragStarIndex.value = i
}

/**
 * 阻止默认行为，否则drop事件不会触发
 */
function handleDragover(e: DragEvent): void {
  e.preventDefault()
}

/**
 * 被放置的容器触发事件，交换两个组件的位置
 */
function handleDrop(i: number): false | void {
  if (dragStarIndex.value === i) {
    return false
  }

  if (Array.isArray(currentFirstCategory.value.swiperList)) {
    const temp = currentFirstCategory.value.swiperList.splice(dragStarIndex.value, 1, currentFirstCategory.value.swiperList[i])
    currentFirstCategory.value.swiperList.splice(i, 1, ...temp)
  }
}

/**
 * 开始拖动，记录拖动的组件下角标
 */
function navigationDragstart(i: number): void {
  navigationDrag.value = i
}

/**
 * 被放置的容器触发事件，交换两个组件的位置
 */
function navigationDrop(i: number): false | void {
  if (navigationDrag.value === i) {
    return false
  }

  if (Array.isArray(currentFirstCategory.value.navigation)) {
    const temp = currentFirstCategory.value.navigation.splice(navigationDrag.value, 1, currentFirstCategory.value.navigation[i])
    currentFirstCategory.value.navigation.splice(i, 1, ...temp)
  }
}

/**
 * 删除轮播组件中的图片
 */
function deletenavigationItem(idx: number): void {
  if (Array.isArray(currentFirstCategory.value.navigation)) {
    currentFirstCategory.value.navigation.splice(idx, 1)
  }
}

/**
 * 添加导航
 */
function addNavigation(): void {
  const list = currentFirstCategory.value.navigation || []

  if (list.length) {
    list.push({
      name: '',
      class: [],
      chartList: [],
      navType: 2,
    } as EnhancedNavigation)
  } else {
    list.push({
      name: '其他',
      class: [],
      chartList: [],
      navType: 1,
    } as EnhancedNavigation)
  }
}

/**
 * 导航添加分类图
 */
function navSelectClass(nav: Navigation): void {
  const list: LocalChart[] = []
  const temp = currentFirstCategory.value.classChart || []

  if (Array.isArray(nav.class)) {
    nav.class.forEach((e) => {
      const i = temp.find((k) => k.category.id === e)
      if (i) list.push(i)
    })
  }

  nav.chartList = list
  resetOtherNavigation()
  sortNavigationByClass()
}

/**
 * 重置其他导航数据
 */
function resetOtherNavigation(): void {
  const { navigation = [], classChart = [] } = currentFirstCategory.value
  const ids: string[] = []

  if (Array.isArray(navigation)) {
    navigation.forEach((i) => {
      if (i.navType !== 1 && Array.isArray(i.class)) {
        i.class.forEach((c) => ids.push(c))
      }
    })

    const temp = navigation.find((i) => i.navType === 1)
    if (temp) {
      temp.chartList = classChart.filter((k) => !ids.includes(k.title))
      temp.class = []
      if (Array.isArray(temp.chartList)) {
        temp.chartList.forEach((k) => {
          temp.class.push(k.title)
        })
      }
    }
  }
}

/**
 * 对导航信息中的分类图进行排序
 */
function sortNavigationByClass(): void {
  const { navigation = [], classChart = [] } = currentFirstCategory.value

  if (Array.isArray(navigation)) {
    navigation.forEach((i) => {
      if (Array.isArray(i.chartList)) {
        i.chartList.forEach((k: any) => {
          k.sortId = classChart.findIndex((i) => i.category.id === k.category.id)
        })

        i.chartList.sort((a: any, b: any) => (a.sortId > b.sortId ? 1 : -1))
      }

      if (Array.isArray(i.chartList)) {
        i.class = i.chartList.reduce((a: string[], b: any) => {
          a.push(b.category.id)
          return a
        }, [])
      }
    })
  }
}

/**
 * 添加分类图 被放置的容器触发事件，交换两个组件的位置
 */
function chartDrop(i: number): false | void {
  if (chartDragIndex.value === i) {
    return false
  }

  if (Array.isArray(currentFirstCategory.value.classChart)) {
    const temp = currentFirstCategory.value.classChart.splice(chartDragIndex.value, 1, currentFirstCategory.value.classChart[i])
    currentFirstCategory.value.classChart.splice(i, 1, ...temp)
  }
  sortNavigationByClass()
}

/**
 * 添加分类图 开始拖动，记录拖动的组件下角标
 */
function chartDragstart(i: number): void {
  chartDragIndex.value = i
}

/**
 * 添加分类图 删除轮播组件中的图片
 */
function deleteChartItem(idx: number): void {
  if (!Array.isArray(currentFirstCategory.value.classChart)) return

  const classChart = currentFirstCategory.value.classChart.splice(idx, 1)[0] as any

  const { navigation = [] } = currentFirstCategory.value

  if (Array.isArray(navigation)) {
    navigation.forEach((i) => {
      if (Array.isArray(i.chartList) && Array.isArray(i.class)) {
        const tIdx = i.chartList.findIndex((k) => k.title === classChart.title)
        const cIdx = i.class.findIndex((k) => k === classChart.title)

        if (cIdx !== -1) {
          i.chartList.splice(tIdx, 1)
          i.class.splice(cIdx, 1)
        }
      }
    })
  }
}

/**
 * 添加分类图 点击获取需要编辑的轮播项
 */
function chartItemClick(e: LocalChart): void {
  currentChartItem.value = e
}

/**
 * 添加分类图片
 */
async function handleChartChange(file: any): Promise<void> {
  const img = await upLoadFile(file)
  if (!img) return

  currentChartItem.value.img = img

  // 检测是不是修改
  const navigations = currentFirstCategory.value.navigation || []
  let flag = true
  let temp: EnhancedNavigation | undefined = undefined
  let index = 0

  if (Array.isArray(navigations)) {
    navigations.forEach((i) => {
      // 断言i为EnhancedNavigation类型
      const nav = i as EnhancedNavigation
      if (Array.isArray(nav.class)) {
        const idx = nav.class.findIndex((s) => s === (currentChartItem.value as any).category.id)

        if (idx !== -1) {
          flag = false
          temp = nav
          index = idx
        }
      }
    })
  }

  // 确保temp不为undefined且有chartList属性
  if (!flag && temp) {
    const typedTemp = temp as EnhancedNavigation
    if (Array.isArray(typedTemp.chartList)) {
      // 如果不是新增则修改对应值
      typedTemp.chartList.splice(index, 1, currentChartItem.value)
    }
  }

  const idx = formData.value.firstCatList.findIndex((i) => i.category && i.category.id === currentFirstCategory.value.category?.id)

  if (idx !== -1) {
    formData.value.firstCatList.splice(idx, 1, currentFirstCategory.value)
  }
}

/**
 * 添加分类图 选择图片上传
 */
async function chartHandleChange(file: any): Promise<void> {
  if (file.size > 1024 * 1024 * 2) {
    ElMessage.error('文件过大，请压缩重试！')
    return
  }

  if (!(currentFirstCategory.value.category && currentFirstCategory.value.category.id)) {
    ElMessage.error('请添加一级分类！')
    return
  }

  if (!isShowAddClass()) {
    ElMessage.error('已无可添加的二级分类，请先添加二级分类！')
    return
  }

  if (!checkLastClassChart()) {
    ElMessage.error('请将上一个分类图填写完整！')
    return
  }

  const img = await upLoadFile(file)
  if (!img) return

  const temp: LocalChart = {
    title: '',
    img,
    category: {
      id: '',
      name: '',
      parentId: '',
      productNumber: 0,
      showCategoryVos: [],
    } as CategoryType,
  }

  if (Array.isArray(currentFirstCategory.value.classChart)) {
    currentFirstCategory.value.classChart.push(temp)
  }
}

/**
 * 判断是否还显示添加分类图按钮
 * 如果当前一级分类下面的二级分类均已被分类图选了，就不再显示添加分类图按钮
 */
function isShowAddClass(): boolean {
  const showCategoryVos = currentFirstCategory.value.category?.showCategoryVos || []
  const classChart = currentFirstCategory.value.classChart || []
  let flag = false

  for (const i of showCategoryVos) {
    if (!classChart.find((k) => k.title === i.id)) {
      flag = true
      break
    }
  }

  return flag
}

/**
 * 判断最后一个二级分类图是否已经选择二级分类
 * 如果未选择，则不允许添加新分类图
 */
function checkLastClassChart(): boolean {
  const classChart = currentFirstCategory.value.classChart || []
  let flag = true

  if (classChart.length && !classChart[classChart.length - 1].title) flag = false

  return flag
}

/**
 * 添加分类图 选择二级分类
 */
function classSelectChange(item: LocalChart): void {
  const navigations = currentFirstCategory.value.navigation || []
  const isAlter = Boolean(item.category.id)
  let temp: EnhancedNavigation | undefined = undefined
  let index = 0

  // 如果是修改
  if (isAlter && Array.isArray(navigations)) {
    navigations.forEach((i) => {
      // 断言i为EnhancedNavigation类型
      const nav = i as EnhancedNavigation
      if (Array.isArray(nav.class)) {
        const idx = nav.class.findIndex((id) => id === item.category.id)
        if (idx !== -1) {
          temp = nav
          index = idx
        }
      }
    })
  }

  if (currentFirstCategory.value.category && Array.isArray(currentFirstCategory.value.category.showCategoryVos)) {
    item.category =
      currentFirstCategory.value.category.showCategoryVos.find((i) => i.id === item.title) ||
      ({
        id: '',
        name: '',
        parentId: '',
        productNumber: 0,
        showCategoryVos: [],
      } as CategoryType)
  }

  // 添加的分类图默认添加到导航里面去
  if (Array.isArray(navigations) && navigations.length === 0) {
    navigations.push({
      name: '其他',
      class: [],
      chartList: [],
      navType: 1,
    } as EnhancedNavigation)
  }

  if (!isAlter && Array.isArray(navigations) && navigations.length > 0) {
    // 断言为EnhancedNavigation类型
    const firstNav = navigations[0] as EnhancedNavigation
    if (Array.isArray(firstNav.class) && Array.isArray(firstNav.chartList)) {
      firstNav.class.push(item.category.id)
      firstNav.chartList.push(item)
    }
  } else if (temp) {
    const typedTemp = temp as EnhancedNavigation
    if (Array.isArray(typedTemp.class) && Array.isArray(typedTemp.chartList)) {
      // 如果不是新增则修改对应值
      typedTemp.class.splice(index, 1, item.category.id)
      typedTemp.chartList.splice(index, 1, item)
    }
  }

  const idx = formData.value.firstCatList.findIndex((i) => i.category && i.category.id === currentFirstCategory.value.category?.id)

  if (idx !== -1) {
    formData.value.firstCatList.splice(idx, 1, currentFirstCategory.value)
  }
}

/**
 * 颜色选择切换
 */
function colorSelectChange(name: string): void {
  const type = formData.value[name]
  const color: Record<string, string> = {
    fontColor: '#FFFFFF',
    fontBg: '#FD4E64',
    fontNotColor: '#000000',
    fontNotBg: '#FFFFFF',
  }

  if (!type) {
    formData.value[name] = color[name]
  }
}
</script>

<template>
  <div class="spellPage--setting">
    <el-form :model="formData" label-width="80px" label-position="left">
      <el-form-item label="选择模式">
        <el-radio-group v-model="formData.selectMode">
          <el-radio :value="1"> 顶部模式 </el-radio>
          <el-radio :value="2"> 侧边栏模式 </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item v-if="formData.selectMode === 2" label="选择样式">
        <el-radio-group v-model="formData.selectType">
          <el-radio :value="1"> 商品列表 </el-radio>
          <el-radio :value="2"> 分类列表 </el-radio>
        </el-radio-group>
      </el-form-item>
      <div class="class__box">
        <div class="select__box">
          <el-button @click="chooseCategory"> 选择展示分类 </el-button>
          <span class="tips">已选分类（上下拖动可排序）</span>
        </div>
        <div
          v-for="(group, index) in formData.firstCatList"
          :key="index"
          class="list__item"
          :draggable="true"
          @dragstart="handleDragClass(index)"
          @dragover="handleDragoverClass"
          @drop="handleDropClass(index)"
        >
          {{ group.category?.name }}
          <span class="click__btn" @click="handleGoodGroupDel(group.category?.id || '')">删除</span>
        </div>
      </div>

      <!-- 添加分类图片 -->
      <div v-if="formData.selectMode === 2 && formData.selectType === 2" class="poster__box">
        <div class="tips" style="height: auto">
          <span style="color: #666">添加分类图片:</span>
          鼠标拖拽调整顺序，小程序端分类按顺序显示
        </div>
        <div
          v-for="(item, index) in currentFirstCategory.classChart"
          :key="index"
          class="homeSwiperForm-item"
          style="background-color: #fff"
          :draggable="true"
          @click.capture="chartItemClick(item)"
          @dragstart="chartDragstart(index)"
          @dragover="handleDragover"
          @drop="chartDrop(index)"
        >
          <el-icon class="remove__swiper--item" @click="deleteChartItem(index)">
            <Remove />
          </el-icon>

          <el-upload class="homeSwiperForm-item__uploader" :show-file-list="false" :auto-upload="false" :on-change="handleChartChange">
            <img v-if="item.img" :src="item.img" class="homeSwiperForm-item__img" />
            <el-icon v-else class="homeSwiperForm-item__plus">
              <Plus />
            </el-icon>
          </el-upload>
          <div class="homeSwiperForm-item__right" style="padding-left: 20px; padding-top: 23px">
            <el-form :model="item" label-width="70px">
              <el-form-item label="选择分类">
                <el-select v-model="item.title" placeholder="请选择" @change="classSelectChange(item)">
                  <el-option
                    v-for="(lable, inx) in currentFirstCategory.category?.showCategoryVos || []"
                    :key="inx"
                    :hidden="
                      !!(
                        currentFirstCategory.classChart &&
                        currentFirstCategory.classChart.find((t) => t.category && t.category.id === lable.id && lable.id !== item.title)
                      )
                    "
                    :label="lable.name"
                    :value="lable.id"
                  />
                </el-select>
              </el-form-item>
            </el-form>
          </div>
        </div>

        <el-upload
          class="homeSwiperForm-add"
          style="background-color: #fff"
          multiple
          :auto-upload="false"
          :show-file-list="false"
          :on-change="chartHandleChange"
        >
          <span>+ 添加一个分类图</span>
          <p style="margin-top: 5px">建议宽度177像素</p>
        </el-upload>
      </div>

      <!-- 添加海报 -->
      <div v-if="formData.selectMode === 2" class="poster__box">
        <div class="tips" style="height: auto">
          <span style="color: #666">添加海报:</span>
          鼠标拖拽调整广告顺序，小程序端海报按顺序显示
        </div>
        <div
          v-for="(item, index) in currentFirstCategory.swiperList"
          :key="index"
          class="homeSwiperForm-item"
          style="background-color: #fff"
          :draggable="true"
          @click.capture="swiperItemClick(item)"
          @dragstart="handleDragstart(index)"
          @dragover="handleDragover"
          @drop="handleDrop(index)"
        >
          <el-icon class="remove__swiper--item" @click="deleteSwiperItem(index)">
            <Remove />
          </el-icon>

          <el-upload class="homeSwiperForm-item__uploader" :show-file-list="false" :auto-upload="false" :on-change="handleItemChange">
            <img v-if="item.img" :src="item.img" class="homeSwiperForm-item__img" />
            <el-icon v-else class="homeSwiperForm-item__plus">
              <Plus />
            </el-icon>
          </el-upload>
          <div class="homeSwiperForm-item__right">
            <el-form :model="item" label-width="70px">
              <el-form-item label="分类名称">
                {{ item.title }}
              </el-form-item>
              <el-form-item label="跳转路径">
                <LinkSelect :link="item.link" :inner="false" :select-type="2" />
              </el-form-item>
              <el-form-item label="">
                <span style="color: #9797a1">{{ item.link.name }}</span>
              </el-form-item>
            </el-form>
          </div>
        </div>

        <el-upload
          v-if="currentFirstCategory.swiperList && currentFirstCategory.swiperList.length < 5"
          class="homeSwiperForm-add"
          style="background-color: #fff"
          multiple
          :auto-upload="false"
          :show-file-list="false"
          :on-change="handleChange"
        >
          <span>+ 添加背景图</span>
          <p style="margin-top: 5px">建议宽度500像素</p>
        </el-upload>
      </div>

      <!-- 导航信息 -->
      <div v-if="isShowNav" class="navigation__tips">
        <div class="tips" style="height: auto">
          <span style="color: #666">导航信息:</span>
          鼠标拖拽调整顺序，小程序端分类按顺序显示
        </div>
      </div>
      <div v-if="isShowNav" class="navigation__box">
        <div v-for="(nav, iex) in currentFirstCategory.navigation" :key="iex">
          <div
            v-if="nav.navType !== 1"
            class="homeSwiperForm-item"
            style="background-color: #fff; display: block"
            :draggable="true"
            @dragstart="navigationDragstart(iex)"
            @dragover="handleDragover"
            @drop="navigationDrop(iex)"
          >
            <el-icon class="remove__swiper--item" style="z-index: 11" @click="deletenavigationItem(iex)">
              <Remove />
            </el-icon>

            <el-form-item label="导航名称">
              <el-input v-model="nav.name" placeholder="请输入内容" />
            </el-form-item>
            <el-form-item label="选择分类">
              <el-select v-model="nav.class" multiple placeholder="请选择" style="width: 100%" @change="navSelectClass(nav)">
                <el-option v-for="(cla, p) in getCanSelectClass(nav)" :key="p" :label="cla.category.name" :value="cla.category.id" />
              </el-select>
            </el-form-item>
          </div>
        </div>
        <el-form-item v-if="currentFirstCategory.navigation && currentFirstCategory.navigation.length" label="导航样式">
          <el-radio-group v-model="currentFirstCategory.navigationStyle">
            <el-radio :value="1"> 样式一 </el-radio>
            <el-radio :value="2"> 样式二 </el-radio>
          </el-radio-group>
        </el-form-item>
        <div class="add__navigation">
          <span @click="addNavigation">添加导航</span>
        </div>
      </div>

      <div class="content__box">
        <el-form-item v-if="formData.selectMode === 1 || formData.selectType === 1" label="列表样式">
          <el-radio-group v-model="formData.listStyle">
            <el-radio :value="1"> 大图样式 </el-radio>
            <el-radio :value="2"> 列表样式 </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="!isSideClassification" label="购买按钮">
          <el-radio-group v-model="formData.cartButton">
            <el-radio :value="3"> 样式一 </el-radio>
            <el-radio :value="4"> 样式二 </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="!isSideClassification" label="商品边距" class="slider__box">
          <el-slider v-model="formData.goodsPadding" :show-tooltip="false" :show-input="true" :max="30" />
        </el-form-item>
        <el-form-item label="字体选中">
          <div class="color__select">
            <span class="color__tips">{{ formData.fontColor }}</span>
            <el-color-picker v-model="formData.fontColor" @change="colorSelectChange('fontColor')" />
          </div>
        </el-form-item>
        <el-form-item label="背景选中">
          <div class="color__select">
            <span class="color__tips">{{ formData.fontBg }}</span>
            <el-color-picker v-model="formData.fontBg" @change="colorSelectChange('fontBg')" />
          </div>
        </el-form-item>
        <el-form-item label="字体未选">
          <div class="color__select">
            <span class="color__tips">{{ formData.fontNotColor }}</span>
            <el-color-picker v-model="formData.fontNotColor" @change="colorSelectChange('fontNotColor')" />
          </div>
        </el-form-item>
        <el-form-item label="背景未选">
          <div class="color__select">
            <span class="color__tips">{{ formData.fontNotBg }}</span>
            <el-color-picker v-model="formData.fontNotBg" @change="colorSelectChange('fontNotBg')" />
          </div>
        </el-form-item>
      </div>
    </el-form>
    <Category v-if="dialogShow" ref="CategoryRef" v-model:dialog-show="dialogShow" :form-data="formData" @choose="selectedCategory" />
  </div>
</template>

<style lang="scss">
@import '@/assets/styles/decoration/businessSuper';
</style>
