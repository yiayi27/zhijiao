<template>
  <UPopup v-model:show="showType" mode="bottom" :customStyle="{ height: '60%' }" @close="onClose">
    <SelectNorms
      v-model:goodDetail="goodDetail"
      :norms="norms"
      :getNorms="norms"
      :buyType="true"
      :shopCar="true"
      @onClose="onClose"
      @addShopCar="addShopCar"
      @checkNorms="checkNorms"
    />
  </UPopup>
</template>

<script setup lang="ts">
import SelectNorms from '@/components/SelectNorms/SelectNorms.vue'
import UPopup from '@/components/UPopup/UPopup.vue'
import { useSettingStore } from '@/store/modules/setting'

// 定义接口
interface Norms {
  id?: Long
  pic?: string
  price: Long
  intPrice?: string
  smaPrice?: string
  maxNum?: number
  perLimit?: number
  stock?: number
  specs?: string
  memPrice?: Long
  [key: string]: any
}

interface SkuStock {
  id: Long
  specs: string
  perLimit: number
  stock: number
  getType?: boolean
  [key: string]: any
}

interface GoodDetail {
  skuStocks: SkuStock[]
  goodsNumber?: number
  pic?: string
  skuId?: Long
  skuName?: string
  [key: string]: any
}

// 使用defineModel处理双向绑定
const showType = defineModel<boolean>('showType', { default: false })

const goodDetail = defineModel<GoodDetail>('goodDetail', { default: () => ({}) })

// 定义props
defineProps({
  norms: {
    type: Object as () => Norms,
    default: () => ({}),
  },
})

// 定义emit
const emit = defineEmits(['close', 'addShopCar', 'checkNorms'])

// 方法
const onClose = () => {
  useSettingStore().setFormShopCar(false)
  showType.value = false
}

const addShopCar = (data: any) => {
  emit('addShopCar', data)
}

const checkNorms = (check: any) => {
  emit('checkNorms', check)
}
</script>

<style lang="scss" scoped>
.border {
  background: #f0f0f0;

  &__title {
    background-color: white;
    height: 100rpx;
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 36rpx;
    font-weight: bold;
    margin-bottom: 40rpx;
  }
}

.coupon {
  color: black;
  display: flex;
  padding: 10rpx 40rpx;
}
</style>
