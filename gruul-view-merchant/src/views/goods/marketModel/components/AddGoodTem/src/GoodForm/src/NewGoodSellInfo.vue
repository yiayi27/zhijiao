<script setup lang="ts">
import type { NormType } from '../../../../GoodsNorms/goodNormType'
import { NEW_GOOD_SELL_INFO_MODEL } from '@/libs/enum/storage'
import { storage } from '@/libs/storage'
import GoodsNorms from '@/views/goods/marketModel/components/GoodsNorms/GoodsNorms.vue'
import GoodsNormsDetail from '@/views/goods/marketModel/components/GoodsNorms/GoodsNormsDetail.vue'

import { onMounted, reactive, ref, watch } from 'vue'

export interface SkuStockItem {
  specs: string
  originalPrice: Long
  price: Long
  stock: number
  perLimit: number
  pic: string
  sale: string
  weight: Long
  id: string
  lowStock: string | number
  memberPrices: any[]
  skuRebates: any[]
  productId?: string
  skuCode: string
  productSpecNames?: any[] | { name: string }
  version?: string
  memberType?: any
  rebateType?: any
  inputValue?: string
  inputVisble?: boolean
}

// Props定义
const props = defineProps({
  goodDetail: {
    type: Object,
    default: () => ({}),
  },
  goodFormModel: {
    type: Object,
    default: () => ({}),
  },
  logisticsId: {
    type: String,
    default: '',
  },
  isWeight: {
    type: Boolean,
    default: false,
  },
})

// 表单校验器
function checkPriceField(message: string) {
  return (rule: any, value: any, callback: any) => {
    if (!value) {
      return callback(new Error(message))
    }
    const priceReg = /(^[1-9]\d*(\.\d{1,2})?$)|(^0(\.\d{1,2})?$)/

    if (!priceReg.test(value)) {
      return callback(new Error('请输入正确的价格'))
    }
    callback()
  }
}

function checkNumberField(message?: string) {
  return (rule: any, value: any, callback: any) => {
    if (!value) {
      return message ? callback(new Error(message)) : callback()
    }

    if (!Number.isSafeInteger(value)) {
      return callback(new Error('请输入正确的数字'))
    }
    callback()
  }
}

// 响应式数据
const formRef = ref()
const goodNormDetailRef = ref()
const showAttributes = ref(false)
const formModel = reactive({} as any)
const limitType = ref(0)
const unifyLimitNum = ref('0')
const deliveryTime = ref(0)
const memoSpecList = ref([])

// 表单验证规则
const formRules = reactive({
  originalPrice: [
    {
      required: true,
      message: '请输入商品指导价',
      trigger: 'blur',
      validator: checkPriceField('请输入商品指导价'),
    },
  ],
  price: [
    {
      required: true,
      message: '请输入商品实售价',
      trigger: 'blur',
      validator: checkPriceField('请输入商品实售价'),
    },
  ],
  stock: [{ required: true, message: '请输入商品库存', trigger: 'blur' }],
  checkNumberField: [{ trigger: 'blur', validator: checkNumberField() }],
  weight: [{ required: true, message: '请输入商品重量', trigger: 'blur' }],
  dominoState: [{ required: true, message: '请选择是否开启接龙', trigger: 'blur' }],
})

// 服务保障列表
const serviceAssuranceList = reactive([
  {
    name: '全场包邮',
    state: false,
    text: '所有商品均无条件包邮',
  },
  {
    name: '7天退换',
    state: false,
    text: '商家承诺7天无理由退换货',
  },
  {
    name: '48小时发货',
    state: false,
    text: '商家承诺订单在48小时内发布',
  },
  {
    name: '假一赔十',
    state: false,
    text: '若收到商品是假冒品牌，可获得十倍赔偿',
  },
  {
    name: '正品保证',
    state: false,
    text: '商家承诺商品正品质量',
  },
])

watch(
  () => formModel.skuStocks,
  () => {
    showAttributes.value = formModel.skuStocks.length > 0
  },
  { deep: true },
)

// 挂载后执行
onMounted(() => {
  if (!props.goodDetail) {
    Object.assign(formModel, props.goodFormModel)
  }

  if (storage.get(NEW_GOOD_SELL_INFO_MODEL)) {
    setFormModel(storage.get(NEW_GOOD_SELL_INFO_MODEL))
  } else {
    getModel()
  }
})

// 方法定义
function getModel() {
  if (!props.goodDetail) {
    Object.assign(formModel, JSON.parse(JSON.stringify(props.goodFormModel)))
    formModel.skuStocks = [
      {
        specs: '',
        originalPrice: 0,
        price: 0,
        stock: 0,
        perLimit: 0,
        pic: '',
        sale: '0',
        weight: '0',
        id: '',
        lowStock: '',
        memberPrices: [],
        skuRebates: [],
        productId: '',
        skuCode: '',
        productSpecNames: [],
      },
    ]
  } else {
    setFormModel(props.goodDetail)
  }
}

/**
 * 还原表单数据
 */
function setFormModel(nVal: any) {
  if (Object.keys(nVal).length === 0) return
  Object.assign(formModel, nVal)
  setServiceAssuranceList(nVal.serviceIds)
  if (nVal.productSpecs.length === 0 && nVal.skuStocks.length > 0) {
    const skuStockItem = nVal.skuStocks[0]
    formModel.originalPrice = skuStockItem.originalPrice
    formModel.price = skuStockItem.price
    formModel.stock = skuStockItem.stock
    formModel.perLimit = skuStockItem.perLimit
    formModel.weight = skuStockItem.weight
  }
  memoSpecList.value = nVal.skuStocks
}

function getNewForm() {
  if (formModel.productSpecs.length === 0) {
    const temStock = formModel.skuStocks
    formModel.skuStocks = []
    formModel.skuStocks.push({
      originalPrice: formModel.originalPrice,
      price: formModel.price,
      stock: formModel.stock,
      weight: formModel.weight,
      perLimit: formModel.perLimit,
      id: temStock[0] && temStock[0].id ? temStock[0].id : '',
      lowStock: 0,
      memberPrices: temStock[0] ? temStock[0].memberPrices : [],
      skuRebates: temStock[0] ? temStock[0].skuRebates : [],
      memberType: temStock[0] ? temStock[0].memberType : null,
      rebateType: temStock[0] ? temStock[0].rebateType : null,
      productId: temStock.productId,
    })
  }
  formModel.serviceIds = getServiceIds()
  return JSON.parse(JSON.stringify(formModel))
}

async function getFormModel() {
  try {
    await validate()
    await getSkuStocks()
    const model = formModel

    return {
      unit: '克', // 商品重量
      weight: '0', // 商品长度
      useDistribution: model.useDistribution,
      distributionAmount: model.distributionAmount,
      skuStocks: model.skuStocks,
      giftPoint: model.giftPoint,
      sale: model.sale,
      serviceIds: getServiceIds(),
      dominoState: model.dominoState,
      limitType: model.limitType,
      productSpecs: model.productSpecs,
      version: model.skuStocks[0].version,
    }
  } catch (error) {
    console.error(error)
    return Promise.reject(false)
  }
}

/**
 * 获取商品sku
 */
function getSkuStocks() {
  const isMultiSpec = formModel.productSpecs.length
  const currentLimitType = limitType.value

  if (!isMultiSpec) {
    const skuItem = {
      specs: '',
      originalPrice: formModel.originalPrice,
      price: formModel.price,
      stock: formModel.stock,
      perLimit: formModel.perLimit,
      pic: formModel.pic,
      id: formModel.skuStocks[0] && formModel.skuStocks[0].id ? formModel.skuStocks[0].id : '',
      lowStock: formModel.lowStock,
      memberPrices: formModel.skuStocks[0] ? formModel.skuStocks[0].memberPrices : [],
      skuRebates: formModel.skuStocks[0] ? formModel.skuStocks[0].skuRebates : [],
      memberType: formModel.skuStocks[0] ? formModel.skuStocks[0].memberType : null,
      rebateType: formModel.skuStocks[0] ? formModel.skuStocks[0].rebateType : null,
      productId: formModel.productId,
      skuCode: '',
      weight: formModel.weight,
      version: formModel.skuStocks.length > 0 ? formModel.skuStocks[0].version : '',
    }
    formModel.skuStocks = [skuItem]
  } else {
    formModel.skuStocks = goodNormDetailRef.value.getNormList()
  }

  if (!currentLimitType) {
    const isUnifyLimit = formModel.limitType === 1 // 是否为统一限购
    formModel.skuStocks = formModel.skuStocks.map((item: SkuStockItem) => {
      return {
        ...item,
        perLimit: isUnifyLimit ? unifyLimitNum.value : item.perLimit,
      }
    })
  }
}

/**
 * 对整个表单进行校验
 */
function validate() {
  return formModel.productSpecs.length === 0 && formRef.value.validate()
}

/**
 * 获取服务保障
 */
function getServiceIds(): string {
  return serviceAssuranceList
    .filter((item) => item.state)
    .map((item) => item.name)
    .join(',')
}

/**
 * 还原服务保障
 */
function setServiceAssuranceList(serviceIds = '') {
  if (serviceIds) {
    const serviceIdArr = serviceIds.split(',')
    const isInclueDelivery = serviceIdArr.find((item) => {
      return item.includes('发货')
    })
    serviceAssuranceList.forEach((item) => {
      if (item.name.includes('发货') && isInclueDelivery) {
        const deliveryNumber = Number(isInclueDelivery?.match(/\d/g)?.join('') || 0)
        deliveryTime.value = deliveryNumber
        item.name = `${deliveryNumber}天发货`
        item.state = true
      } else {
        item.state = serviceIds.includes(item.name)
      }
    })
  }
}

/**
 * 添加规格
 */
function addSku(productSpecNames: any) {
  return {
    specs: '',
    originalPrice: 0,
    price: 0,
    stock: 0,
    perLimit: 0,
    pic: '',
    sale: 0,
    weight: 0,
    id: '',
    lowStock: '',
    memberPrices: [],
    skuRebates: [],
    productId: '',
    skuCode: '',
    productSpecNames,
    version: '',
    inputValue: '',
    inputVisble: false,
  }
}

// 规格组合
function caleDescartes(list: NormType[]) {
  let assemble: any = []
  list = list.filter((item) => item.children.length > 0)
  assemble = list.map((item) => {
    return item.children
  })

  if (assemble.length <= 1) {
    assemble = assemble[0] ?? []
  } else {
    // 获取组合数据 笛卡尔基
    assemble = assemble.reduce(
      (total: any[], currentValue: any[]) => {
        return total.flatMap((t) => currentValue.map((cv) => (Array.isArray(t) ? [...t, cv] : [t, cv])))
      },
      [[]],
    )
  }

  // 获取stock缓存
  const stockMap = new Map(formModel.skuStocks.map((item: SkuStockItem) => [JSON.stringify(item.productSpecNames), item]))
  return assemble.map((item: any) => {
    const productSpecNames: string[] = Array.isArray(item) ? item.map((child: any) => child.name) : [item.name]

    return stockMap.get(JSON.stringify(productSpecNames)) ?? addSku(productSpecNames)
  })
}

// 改变规格值
async function changeClassHandle(e: any) {
  const { type, list } = e
  // 存储列表输入值
  if (goodNormDetailRef.value) {
    if (type === 'addSpecValue' || type === 'delSpecValue' || type === 'sortSpecValue') {
      memoSpecList.value = goodNormDetailRef.value.getNormList()
    } else {
      memoSpecList.value = []
    }
  }
  formModel.productSpecs = list
  formModel.skuStocks = caleDescartes(list)
}

// 改变规格列表
function changeNormList(e: any) {
  formModel.skuStocks = e
}

// 导出组件方法，使父组件可以调用
defineExpose({
  getFormModel,
  getNewForm,
})
</script>

<template>
  <div class="pageRoll">
    <el-form ref="formRef" :model="formModel" :rules="formRules" label-width="100px">
      <div class="navLine">规格设置</div>
      <GoodsNorms :class-arr="formModel.productSpecs" @change-class="changeClassHandle" />
      <GoodsNormsDetail
        v-if="formModel.productSpecs && formModel.productSpecs.length > 0"
        ref="goodNormDetailRef"
        :list="formModel.skuStocks"
        :class-arr="formModel.productSpecs"
        :memo-spec-list="memoSpecList"
        @change-norm-list="changeNormList"
      />
      <div v-else>
        <el-form-item label="指导价" prop="originalPrice" style="margin-top: 10px">
          <el-input-number v-model="formModel.originalPrice" :min="0" :controls="false" placeholder="0" style="width: 500px" class="input_number" />
        </el-form-item>
        <el-form-item label="实售价" prop="price">
          <el-input-number v-model="formModel.price" :min="0" :controls="false" placeholder="0" style="width: 500px" class="input_number" />
        </el-form-item>
        <el-form-item label="商品库存" prop="stock">
          <el-input-number
            v-model="formModel.stock"
            :step="1"
            placeholder="0"
            style="width: 500px"
            :min="0"
            step-strictly
            :controls="false"
            :precision="0"
            class="input_number"
          />
        </el-form-item>
        <el-form-item label="商品限购">
          <el-input-number
            v-model="formModel.perLimit"
            :step="1"
            placeholder="0"
            style="width: 500px"
            :min="0"
            step-strictly
            :controls="false"
            :precision="0"
            class="input_number"
          />
        </el-form-item>
        <el-form-item v-if="isWeight" label="商品重量" prop="weight">
          <el-input-number v-model="formModel.weight" :min="0" :controls="false" placeholder="0" style="width: 500px" />
        </el-form-item>
      </div>
      <el-form-item label="商品积分">
        <el-input-number
          v-model="formModel.giftPoint"
          :step="1"
          placeholder="0"
          style="width: 500px"
          :min="0"
          step-strictly
          :controls="false"
          :precision="0"
          class="input_number"
        />
      </el-form-item>

      <div class="navLine">分佣设置</div>

      <el-form-item label="分佣类型" style="margin-top: 25px">
        <el-radio v-model="formModel.useDistribution" :value="1"> 固定金额 </el-radio>
        <el-radio v-model="formModel.useDistribution" :value="2"> 百分比 </el-radio>
      </el-form-item>

      <el-form-item label="佣金规则" style="margin-top: 25px">
        <el-input-number
          v-model="formModel.distributionAmount"
          :step="1"
          placeholder="0"
          style="width: 500px"
          :min="0"
          step-strictly
          :controls="false"
          class="input_number mr-[10px]"
        />
        {{ formModel.useDistribution === 1 ? '元' : '%' }}
      </el-form-item>

      <div class="navLine">其他信息</div>
      <el-form-item label="初始销量">
        <el-input-number
          v-model="formModel.sale"
          :step="1"
          placeholder="0"
          style="width: 500px"
          :min="0"
          step-strictly
          :controls="false"
          class="input_number"
        />
      </el-form-item>
      <el-form-item label="服务保障" class="service__guarantee--form">
        <div v-for="item in serviceAssuranceList" :key="item.name">
          <el-checkbox v-model="item.state">
            <div class="serveMsg">
              <span style="width: 120px">{{ item.name }}</span>
              <span style="color: #c6c6c6">{{ item.text }}</span>
            </div>
          </el-checkbox>
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>

<style lang="scss" scoped>
@import '@/assets/styles/goods/index.scss';
@import '@/assets/styles/goods/goodMultiSpec.scss';

.input_number {
  .el-input__inner {
    text-align: left;
  }
}
</style>
