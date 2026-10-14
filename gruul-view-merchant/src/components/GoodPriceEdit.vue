<script setup lang="ts">
import { Edit } from '@element-plus/icons-vue'

// 定义SKU库存的接口
interface SkuStock {
  id: string | number
  specs: string
  price: number
  originalPrice: number
}

// 定义props
const props = defineProps({
  skuStocks: {
    type: Array as () => SkuStock[],
    default: () => [],
  },
  skuDetail: {
    type: Object,
    default: () => ({}),
  },
})

// 定义事件
const emit = defineEmits(['changePrice'])

// 响应式状态
const inputVisible = ref(false)
const popoverVisible = ref(false)
const minPrice = ref<number | string>(0)
const maxPrice = ref<number | string>(0)
const editPrice = ref(0)
const skuStocksEdit = ref<SkuStock[]>([])
const savePriceInput = ref<HTMLElement | null>(null)

// 计算属性
const isOnePrice = computed(() => {
  return props.skuStocks.length === 1
})

// 监听props变化
watch(
  () => props.skuStocks,
  () => {
    initPrice()
  },
  { deep: true },
)

// 监听popover显示状态
watch(popoverVisible, (nVal) => {
  if (nVal) {
    setSkuStocksEdit()
  }
})

// 组件挂载时初始化
onMounted(() => {
  initPrice()
})

/**
 * 初始化价格
 */
function initPrice() {
  const skuStocks = props.skuStocks
  if (skuStocks.length === 1) {
    minPrice.value = skuStocks[0].price === 0 ? skuStocks[0].originalPrice : skuStocks[0].price
    minPrice.value = minPrice.value ? Number(minPrice.value).toFixed(2) : 0
  }

  if (skuStocks.length > 1) {
    const priceList = skuStocks.map((item: SkuStock) => (item.price !== 0 ? item.price : item.originalPrice))
    minPrice.value = Math.min(...priceList)
    maxPrice.value = Math.max(...priceList)
    minPrice.value = minPrice.value ? Number(minPrice.value).toFixed(2) : 0
    maxPrice.value = maxPrice.value ? Number(maxPrice.value).toFixed(2) : 0
  }
}

/**
 * 设置需要编辑的规格价格
 */
function setSkuStocksEdit() {
  skuStocksEdit.value = props.skuStocks.map((item: SkuStock) => {
    return {
      id: item.id,
      specs: item.specs,
      price: item.price,
      originalPrice: item.originalPrice,
    }
  })
}

/**
 * 显示输入框
 */
function showInput() {
  inputVisible.value = true
  editPrice.value = Number(minPrice.value)
  setSkuStocksEdit()
  nextTick(() => {
    if (savePriceInput.value) {
      const inputEl = savePriceInput.value.querySelector('input')
      if (inputEl) {
        inputEl.focus()
      }
    }
  })
}

/**
 * 隐藏输入框
 */
function hindInpt() {
  inputVisible.value = false
  const currentEditPrice = editPrice.value
  const currentSkuStocksEdit = skuStocksEdit.value

  if (minPrice.value === currentEditPrice) {
    return
  }
  if (!validatePrice(currentEditPrice)) {
    ElMessage.warning('请填写正确的价格')
    return
  }
  if (currentEditPrice > currentSkuStocksEdit[0].originalPrice) {
    ElMessage.warning('修改价格不能高于指导价')
    return
  }
  currentSkuStocksEdit[0].price = currentEditPrice
  changePrice()
}

/**
 * 提交商品规格价格
 */
function submitSkuStockPrice() {
  const currentSkuStocksEdit = skuStocksEdit.value

  const validFlag = currentSkuStocksEdit.every((item) => {
    return validatePrice(item.price)
  })

  if (!validFlag) {
    ElMessage.warning('请填写正确的价格')
    return
  }

  const validprice = currentSkuStocksEdit.every((item) => {
    return item.price <= item.originalPrice
  })

  if (!validprice) {
    ElMessage.warning('修改价格不能高于指导价')
    return
  }

  changePrice()
  popoverVisible.value = false
}

/**
 * 通知父组件价格变化
 */
function changePrice() {
  const currentSkuStocksEdit = skuStocksEdit.value
  const temp: Record<string, number> = {}
  currentSkuStocksEdit.forEach((item) => {
    temp[String(item.id)] = Number.parseFloat(String(item.price))
  })
  emit('changePrice', temp)
}

/**
 * 校验价格是否正确
 */
function validatePrice(price: number | string) {
  const reg = /(^[1-9](\d+)?(\.\d{1,2})?$)|(^(0)$)|(^\d\.\d(\d)?$)/
  return reg.test(String(price))
}
</script>

<template>
  <!-- 编辑商品价格 -->
  <div class="goodPriceEdit">
    <div v-if="isOnePrice">
      <span v-if="!inputVisible" @click="showInput">
        <span class="goodPriceEdit__price">￥{{ minPrice }}</span>
        <el-icon class="edit-icon" color="#2d8cf0"><Edit /></el-icon>
      </span>
      <el-input-number
        v-if="inputVisible"
        ref="savePriceInput"
        v-model="editPrice"
        style="width: 120px"
        placeholder="请输入价格"
        :min="0"
        :controls="false"
        @blur="hindInpt"
      />
    </div>
    <div v-else>
      <el-popover v-model:visible="popoverVisible" placement="bottom" trigger="click">
        <div v-for="item in skuStocksEdit" :key="item.id">
          <div style="margin-bottom: 10px">
            <span>{{ item.specs }}：</span>
            <el-input-number v-model="item.price" style="width: 120px" placeholder="请输入价格" :min="0" :controls="false" />
          </div>
        </div>
        <div class="pop--button">
          <el-button type="primary" @click="submitSkuStockPrice"> 确认 </el-button>
          <el-button @click="popoverVisible = false"> 取消 </el-button>
        </div>
        <template #reference>
          <span>
            <span class="goodPriceEdit__price">￥{{ minPrice }}~￥{{ maxPrice }}</span>
            <el-icon class="edit-icon" color="#2d8cf0"><Edit /></el-icon>
          </span>
        </template>
      </el-popover>
    </div>
  </div>
</template>

<style lang="scss">
.goodPriceEdit {
  //width: 150px;
  cursor: pointer;
  &__price {
    color: #ff7417;
  }
  .edit-icon {
    margin-left: 4px;
    display: none;
  }
  &:hover {
    .edit-icon {
      display: inline-block;
    }
  }
}
</style>
