<script setup lang="ts">
import { editGood, putWayGood, updateGood } from '@/api/good/goods'
import { mapToRemoveAttrs } from '@/libs/base'
import { NEW_GOOD_SELL_INFO_MODEL, NEW_GOOD_SHOP_INFO_MODEL } from '@/libs/enum/storage'
import { storage } from '@/libs/storage'
import { useGlobalStore } from '@/store/modules/global'
import { ElMessageBox } from 'element-plus'

import { useRoute, useRouter } from 'vue-router'

// 异步加载组件
const NewGoodBaseInfo = defineAsyncComponent(() => import('./NewGoodBaseInfo.vue'))
const NewGoodSellInfo = defineAsyncComponent(() => import('./NewGoodSellInfo.vue'))
const NewGoodDetailInfo = defineAsyncComponent(() => import('./NewGoodDetailInfo.vue'))

// 组件映射表
const componentMap = {
  NewGoodBaseInfo,
  NewGoodSellInfo,
  NewGoodDetailInfo,
}

// 路由相关
const router = useRouter()
const route = useRoute()
const globalStore = useGlobalStore()
const isMall = computed(() => globalStore.setting?.isMall || false)
const loading = ref(false)

// 组件引用
const componentRef = ref<any>(null)

// 定义类型
interface StepIndicator {
  /** 上一步 */
  prev: string
  /** 下一步 */
  next: string
  /** 当前进度 */
  setpIndex: number
  cacheKey?: string
}

// 定义SkuStock接口
interface SkuStock {
  specs: string
  originalPrice: number
  price: number
  stock: string
  perLimit: string
  pic: string
  sale: string
  weight: string
  id?: string
  lowStock?: string
  memberPrices?: Array<{ memberPrice: number }>
  skuRebates?: Array<{ rebatePrice: number }>
  productId?: string
  skuCode?: string
  memberType?: number
  rebateType?: number
}

const cacheInfo = ref<any>({})
const currentStep = ref<keyof typeof componentMap>('NewGoodBaseInfo')
const goodDetail = ref<any>(null)
const logisticsId = ref('')
const isWeight = ref(false)
const checkFlag = ref(true)

// 步骤配置
const stepIndicator = ref<{
  [key: string]: StepIndicator
}>({
  NewGoodBaseInfo: {
    prev: '',
    next: 'NewGoodSellInfo',
    setpIndex: 0,
  },
  NewGoodSellInfo: {
    prev: 'NewGoodBaseInfo',
    next: 'NewGoodDetailInfo',
    setpIndex: 1,
    cacheKey: NEW_GOOD_SELL_INFO_MODEL,
  },
  NewGoodDetailInfo: {
    prev: 'NewGoodSellInfo',
    next: '',
    setpIndex: 2,
    cacheKey: NEW_GOOD_SHOP_INFO_MODEL,
  },
})

// 商品表单模型
const goodFormModel = ref({
  /** 是否新增区域默认投放  0否 1 是 */
  addLaunchArea: 1,
  /** 商品图片 */
  albumPics: '',
  /** 模板属性列表 */
  attribute: '',
  /** 商品属性模板id */
  attributeId: '',
  /** 团长佣金 */
  commanderCommission: '',
  /** 佣金类型状态 (0–金额，1–比例) */
  commissionType: 0,
  /** 商品详情 */
  // detail: "",
  /** 配送方式 */
  distributionMode: 0,
  /** 商品分销设置 */
  distributionSets: [],
  /** 参与接龙 0–未参加，1–已参加 */
  dominoState: 2,
  /** 运费模板ID */
  freightTemplateId: '',
  /** 商品赠送积分 */
  giftPoint: '',
  id: null,
  /** 投放小区ID */
  launchArea: '',
  /** 限购类型(默认统一规格，0–统一规格，1–统一限购，2–规格限购) */
  limitType: 0,
  /** 商品名称 */
  name: '',
  /** 卖点描述 */
  saleDescribe: '',
  /** 规格是否展开 */
  // openSpecs: "",
  /** 展示图片 */
  pic: '',
  /** 模板属性列表 */
  productAttributes: [],
  /** 展示分类模板 */
  productShowCategorys: [],
  /** 货号 */
  productSn: '',
  /** 供应商ID */
  providerId: '',
  /** 初始销量 */
  sale: '',
  /** 销售专区 0商超 1拼团 2秒杀 */
  saleMode: '0',
  /** 销售名称 导入用 */
  saleName: '',
  /** 评分 */
  // score:""
  /** 产品服务 */
  serviceIds: '',
  /** 商品库存 */
  skuStocks: [] as SkuStock[],
  /** 分拣品类ID */
  sortingCategoryId: '',
  /** 状态(默认上架，0–下架（仓库中），1–上架，2–已售完) */
  status: 1,
  /** 商品重量 */
  unit: '克',
  /** 商品视频 */
  videoUrl: '',
  /** 商品长度 */
  weight: '',
  /** 分佣类型 */
  useDistribution: 1,
  /** 佣金数值 */
  distributionAmount: '',
  // 商品多规格信息
  productSpecs: [],
})

// 计算属性
const currentStepIndicator = computed<any>(() => stepIndicator.value[currentStep.value])
const setpIndex = computed(() => currentStepIndicator.value.setpIndex)
const prevStep = computed(() => currentStepIndicator.value.prev)
const nextStep = computed(() => currentStepIndicator.value.next)
const isCsv = computed(() => route.query.from === 'csv')
const saleMode = computed(() => route.query.saleMode || '')

/**
 * 上一步
 */
async function handlePrev() {
  nextTick(() => {
    document.querySelector('.goodForm')?.scrollTo(0, 0)
  })
  const res = await componentRef.value.getNewForm()
  // Object.assign(cacheInfo.value, res)
  cacheInfo.value[currentStepIndicator.value.cacheKey] = res

  if (prevStep.value) {
    currentStep.value = prevStep.value
  }
}

/**
 * 下一步
 */
async function handleNext() {
  nextTick(() => {
    document.querySelector('.goodForm')?.scrollTo(0, 0)
  })

  if (nextStep.value) {
    await getGoodFormModel()
    if (checkFlag.value) {
      currentStep.value = nextStep.value
    }
  }
}

/**
 * 获取商品详情
 */
async function getGoodDetail() {
  if (isMall.value) {
    goodFormModel.value.distributionMode = 1
  }

  const goodId = route.query.id || route.params.id
  if (!goodId) return

  const { data } = await editGood(goodId as string)
  goodDetail.value = data

  if (typeof goodId === 'string') {
    goodFormModel.value.id = goodId as any
  }
}

async function getGoodFormModel() {
  const res = await componentRef.value.getFormModel()
  Object.assign(goodFormModel.value, res)
  const model = goodFormModel.value

  if (model.productSpecs.length > 0) {
    checkSku(model.skuStocks)
    if (goodDetail.value?.id) {
      checkSetting()
    }
  } else {
    checkFlag.value = true
  }
}

/**
 * 规格内容的二次验证
 */
function checkSku(skuStock: SkuStock[]) {
  checkFlag.value = true
  if (skuStock.length === 0) throw new Error('请添加规格值')

  skuStock.forEach((item) => {
    if (prevStep.value === 'NewGoodBaseInfo') {
      checkInputField(item)

      if (Number(item.price) === 0) {
        checkFlag.value = false
        throw new Error('实售价不能为0')
      }
    }

    if (String(item.price).split('.')[1]) {
      const length = String(item.price).split('.')[1].length
      if (length > 2) {
        checkFlag.value = false
        throw new Error('实售价最多保留两位小数')
      }
    }

    if (!item.pic && currentStep.value === 'NewGoodSellInfo') {
      checkFlag.value = false
      throw new Error('请上传SKU图片')
    }
  })

  if (checkFlag.value) {
    checkStock(skuStock)
  }
}

function checkStock(skuStock: SkuStock[]) {
  const regs = /^0*[1-9]\d*$/

  if (currentStep.value === 'NewGoodSellInfo' && goodFormModel.value.limitType === 1) {
    const hasEmptyPic = skuStock.some((item) => !item.pic)
    if (hasEmptyPic) {
      ElMessage.error('请输入规格sku图')
      checkFlag.value = false
      return
    }
  }

  if (prevStep.value === 'NewGoodBaseInfo') {
    if (isWeight.value && skuStock.some((v) => Number(v.weight) === 0 || v.weight === '')) {
      ElMessage.error('请输入规格重量')
      checkFlag.value = false
      return
    }

    if (skuStock.some((item) => item.stock === '0' || !regs.test(item.stock))) {
      ElMessage.error('请输入大于0库存')
      checkFlag.value = false
    }
  }
}

/**
 * 编辑时实售价需大于会员价与返利价格
 */
function checkSetting() {
  if (currentStep.value === 'NewGoodSellInfo') {
    const skuList = goodFormModel.value.skuStocks

    skuList.forEach((skuItem) => {
      if (skuItem.memberType === 1) {
        const vipList = skuItem.memberPrices?.map((item) => item.memberPrice) || []
        const maxPrice = vipList.length > 0 ? Math.max(...vipList) : 0

        if (Number(skuItem.price) < maxPrice) {
          ElMessageBox.confirm('您已设置会员价,实售价不得低于最低档会员价', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
          })
          checkFlag.value = false
          return
        }
      }

      if (skuItem.rebateType === 1) {
        const rebList = skuItem.skuRebates?.map((item) => item.rebatePrice) || []
        const maxPrice = rebList.length > 0 ? Math.max(...rebList) : 0

        if (Number(skuItem.price) < maxPrice) {
          ElMessageBox.confirm('您已设置返利,返利价格不能高于实售价', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
          })
          checkFlag.value = false
        }
      }
    })
  }
}

// 校验输入
function checkInputField(skuStockItem: SkuStock) {
  if (!checkFieldReg(skuStockItem.originalPrice) && skuStockItem.originalPrice !== 0) {
    throw new Error('请输入正确的指导价')
  }
  if (!checkFieldReg(skuStockItem.price) && skuStockItem.price !== 0) {
    throw new Error('请输入正确的实售价')
  }
  if (!checkFieldReg(skuStockItem.stock) && Number(skuStockItem.stock) !== 0) {
    throw new Error('请输入正确的库存')
  }
  if (!checkFieldReg(skuStockItem.perLimit) && Number(skuStockItem.perLimit) !== 0) {
    throw new Error('请输入正确的限购数量')
  }
  if (!checkFieldReg(skuStockItem.weight) && Number(skuStockItem.weight) !== 0) {
    throw new Error('请输入正确的重量')
  }
}

// 检验输入值规格
function checkFieldReg(value: any): boolean {
  const priceReg = /(^[1-9]\d*(\.\d{1,2})?$)|(^0(\.\d{1,2})?$)/
  return !!value && priceReg.test(value)
}

/**
 * 上架
 */
async function putAway() {
  await getGoodFormModel()
  storage.delete('formModel')

  const productAttributes = mapToRemoveAttrs(goodFormModel.value.productAttributes, ['id'])
  const productShowCategorys = mapToRemoveAttrs(goodFormModel.value.productShowCategorys, ['id', 'productShowCategorySeconds.id'])
  const query = {
    ...goodFormModel.value,
    status: 1,
    place: 0,
    productAttributes,
    productShowCategorys,
  }

  const hasMemberType3 = query.skuStocks.some((item: SkuStock) => item.memberType === 3)
  if (hasMemberType3) {
    query.skuStocks = query.skuStocks.map((item: SkuStock) => ({
      ...item,
      memberType: 3,
    }))
  }

  loading.value = true
  const fn = query.id ? updateGood : putWayGood
  await fn(query).finally(() => {
    loading.value = false
  })

  ElMessage.success(`${query.id ? '编辑成功' : '上架成功'}`)
  await router.replace({
    name: isCsv.value ? 'csvList' : 'GoodsList',
    query: {
      saleMode: (isCsv.value ? '' : saleMode.value) as string,
      saveFlag: 1,
    },
  })
}

/**
 * 取消上架
 */
function cancelputAway() {
  router.push({ name: 'GoodsList' })
}

// 生命周期钩子
onMounted(() => {
  getGoodDetail()
})
</script>

<template>
  <div class="goodForm">
    <el-steps :active="setpIndex" simple>
      <el-step title="1.编辑基本信息" icon="none" />
      <el-step title="2.编辑销售信息" icon="none" />
      <el-step title="3.编辑商品信息" icon="none" />
    </el-steps>
    <!--  -->
    <keep-alive>
      <component
        :is="componentMap[currentStep]"
        ref="componentRef"
        v-model:is-weight="isWeight"
        v-model:logistics-id="logisticsId"
        :good-detail="goodDetail"
        :good-form-model="goodFormModel"
        :is-csv="isCsv"
        :sale-mode="saleMode"
        :cache-info="cacheInfo"
      />
    </keep-alive>
    <!--  -->
    <div class="next">
      <el-button v-if="prevStep !== ''" type="primary" @click="handlePrev"> 上一步 </el-button>
      <el-button v-if="nextStep !== ''" type="primary" @click="handleNext"> 下一步 </el-button>
      <template v-if="currentStep === 'NewGoodDetailInfo'">
        <el-button type="primary" :loading="loading" @click="putAway"> 上架 </el-button>
        <el-button @click="cancelputAway"> 取消 </el-button>
      </template>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@import '@/assets/styles/goods/index.scss';
</style>
