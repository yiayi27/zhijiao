<template>
  <view :class="' ' + (goodDetail.skuStocks.length > 1 ? 'specific' : 'specific2')">
    <view class="specific__info">
      <image :src="normsData.pic" class="specific__info--image" />
      <view class="specific__info--product">
        <view style="display: flex; align-items: flex-end">
          <view class="product__price">
            <span>{{ normsData.intPrice }}</span>
            <span style="font-weight: weight; font-size: 26rpx">.{{ normsData.smaPrice }}</span>
          </view>
          <view v-if="normsData.memPrice" style="margin-left: 40rpx; display: flex; font-weight: bold; color: #fa5555; font-size: 26rpx">
            会员价:
            <view class="product__memPrice">{{ normsData.memPrice }}</view>
          </view>
        </view>
        <view class="product__line">
          <view v-if="goodDetail.skuStocks.length > 1" class="product__stock">
            <span v-if="normsData.specs">已选：{{ normsData.specs }}</span>
            <span v-else>请先选择规格</span>
          </view>
          <view class="product__max">
            <span v-if="goodDetail.skuStocks.length > 1 && normsData.perLimit"> 限购{{ normsData.perLimit }}件 </span>
            <span v-if="goodDetail.skuStocks.length === 1 && goodDetail.skuStocks[0].perLimit !== 0">
              限购{{ goodDetail.skuStocks[0].perLimit }}件
            </span>
          </view>
        </view>
      </view>
      <MIcon name="iconshangpinxiangqing-guanbi" size="38rpx" style="position: absolute; top: 30rpx; right: 30rpx" @click="onClose" />
    </view>
    <view v-if="goodDetail.skuStocks.length > 1" class="select">
      <view class="select__title">规格</view>
      <scroll-view scroll-y style="height: 400rpx; overflow: hidden">
        <view class="select__list">
          <view v-for="(item, index) in goodDetail.skuStocks" :key="index" class="select__list--value" @click="checkNorms(item)">
            <view v-if="item.stock === 0" class="select__list--value--info center--middle" style="background-color: #f2f2f2; color: #cdcfd0">
              {{ item.specs }}
            </view>
            <view
              v-else
              class="select__list--value--info center--middle"
              :style="
                'color:' +
                (item.getType && item.stock > 0 ? 'red' : '') +
                ';border:1px solid ' +
                (item.getType ? 'red' : '#F2F2F2') +
                ';background-color:' +
                (item.getType ? '#F6EBEC' : '#F2F2F2')
              "
            >
              {{ item.specs }}
            </view>
          </view>
        </view>
      </scroll-view>
    </view>
    <view class="btn" :style="'bottom:' + (goodDetail.skuStocks.length > 1 ? '70' : '65') + 'px'">
      <view class="select__title">数量</view>
      <uv-number-box v-model="numValue" integer min="1" :max="normsData.maxNum" step="1" input-width="120rpx" @change="onChange" />
    </view>
    <view v-if="shopCar" class="poup">
      <view class="poup__btn" style="width: 100%; background-color: #fa5555" @click="addShopCar()">加入购物车</view>
    </view>
    <view v-else class="poup">
      <view class="poup__btn" @click="addShopCar()">加入购物车</view>
      <view class="poup__btn" style="background-color: #fa5555" @click="addShopCar(1)"> 立即购买 </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, type PropType, watchEffect, watch } from 'vue'
import MIcon from '@/components/MIcon/MIcon.vue'
import { useSettingStore } from '@/store/modules/setting'

// 定义接口
export interface Norms {
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
  getType?: boolean // 是否选中
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

// 定义Props和Model
const props = defineProps({
  buyShow: Boolean,
  norms: {
    type: Object as () => Norms,
    default: () => ({}),
  },
  state: {
    type: String,
    default: '',
  },
  buyType: {
    type: Boolean,
    default: false,
  },
  shopCar: {
    type: Boolean,
    default: false,
  },
  // 已选规格
  getNorms: {
    type: Object as () => Norms,
    default: () => {},
  },
})

// 使用defineModel处理goodDetail
const goodDetail = defineModel<GoodDetail>('goodDetail', {
  default: () => ({}),
})

// 定义emit
const emit = defineEmits(['onClose', 'checkNorms', 'addShopCar'])

// 响应式数据
const numValue = ref<number>(1)
const normsData = ref<Norms>({} as Norms)
watch(
  () => goodDetail.value,
  (val) => {
    if (val.skuStocks.length > 1) {
      // TODOS: 后面可能会有多选逻辑
      val.skuStocks.forEach((item) => {
        if (item.getType) {
          normsData.value = { ...normsData.value, ...item }
        }
      })
    }
  },
  {
    deep: true,
    immediate: true,
  },
)

// 生命周期

onMounted(() => {
  propDataChange()
})

// 方法
const onClose = () => {
  emit('onClose')
}

const propDataChange = () => {
  setnorms(props.norms)
}

const setnorms = (normsDataTemp: Norms) => {
  numValue.value = goodDetail.value.goodsNumber ? goodDetail.value.goodsNumber : 1

  // 格式化价格
  let priceStr = typeof normsDataTemp.price === 'number' ? normsDataTemp.price.toFixed(2) : normsDataTemp.price || '0.00'

  // 拆分整数和小数部分
  const priceParts = String(priceStr).split('.')

  const norms = {
    ...normsDataTemp,
    price: priceStr,
    intPrice: priceParts[0] || '0',
    smaPrice: priceParts[1] || '00',
  }

  if (!norms.maxNum) {
    norms.maxNum = norms.perLimit && norms.stock ? (norms.perLimit < norms.stock ? norms.perLimit : norms.stock) : 0
    norms.maxNum = norms.perLimit === 0 ? norms.stock : norms.maxNum
  }

  Object.assign(normsDataTemp, norms)
}

const checkNorms = (mode: any) => {
  if (mode.stock === 0) {
    uni.showToast({
      title: '此规格暂无库存',
      icon: 'none',
    })

    return
  }

  goodDetail.value.skuStocks.forEach((item) => {
    item.getType = mode.id === item.id
    if (item.getType) {
      normsData.value = { ...normsData.value, ...item }
    }
  })

  numValue.value = 1
  emit('checkNorms', mode)
  setnorms(normsData.value)
}
// 默认选中的规格
const defaultChoose = () => {
  // 找到第一个满足条件的规格(stock>0)
  const defaultNorms = goodDetail.value.skuStocks.find((item) => item.stock > 0)
  if (defaultNorms && !props.getNorms.id) {
    // 如果找到满足条件的规格且没有已选规格，则选中该规格
    checkNorms(defaultNorms)
  } else if (props.getNorms.id) {
    const checkNorm = goodDetail.value.skuStocks.find((item) => item.id === props.getNorms.id)
    if (checkNorm) {
      checkNorms(checkNorm)
    }
  }
}
defaultChoose()

const onChange = (e: any) => {
  if (e === normsData.value.stock) {
    uni.showToast({
      title: '库存不足',
      icon: 'none',
    })
  } else if (e === normsData.value.perLimit) {
    uni.showToast({
      title: '您已达限购数量',
      icon: 'none',
    })
  }
  numValue.value = e
}

const addShopCar = (index?: number) => {
  useSettingStore().setFormShopCar(false)

  if (goodDetail.value.skuStocks.length === 1) {
    soloAdd(index)
    return
  }

  if (!normsData.value.id) {
    uni.showToast({
      title: '请先选择规格',
      icon: 'none',
    })

    return
  }

  if (normsData.value.stock === 0) {
    uni.showToast({
      title: '此商品暂无库存',
      icon: 'none',
    })

    return
  }

  goodDetail.value.goodsNumber = numValue.value
  goodDetail.value.pic = normsData.value.pic
  goodDetail.value.skuId = normsData.value.id
  goodDetail.value.skuName = normsData.value.specs

  emit('addShopCar', {
    goodDetail: goodDetail.value,
    index,
  })
}

const soloAdd = (index?: number) => {
  goodDetail.value.goodsNumber = numValue.value
  goodDetail.value.skuId = goodDetail.value.skuStocks[0].id

  emit('addShopCar', {
    goodDetail: goodDetail.value,
    index,
  })
}
</script>

<style lang="scss" scoped>
.center--middle {
  display: flex;
  justify-content: center;
  align-items: center;
}

.specific {
  height: 920rpx;
}

.specific2 {
  height: 450rpx;
}

.specific__info {
  width: 100%;
  height: 250rpx;
  display: flex;
  flex-direction: row;
  padding-left: 30rpx;
  padding-right: 30rpx;
  box-sizing: border-box;
  align-items: flex-end;
}

.specific__info--image {
  width: 220rpx;
  height: 220rpx;
  // margin-top: 20rpx;
  // border-radius: 20rpx;
}

.specific__info--product {
  flex: 1;
  margin-left: 30rpx;
  margin-bottom: 20rpx;

  .product__price {
    font-size: 34rpx;
    font-weight: bold;
    color: #fa5555;
  }

  .product__price::before {
    content: '￥';
    font-weight: normal;
    font-size: 26rpx;
  }

  .product__memPrice {
    font-weight: bold;
    color: #fa5555;
    font-size: 26rpx;
  }

  .product__memPrice::before {
    content: '￥';
    font-weight: normal;
    font-size: 26rpx;
  }

  .product__line {
    display: flex;
    margin-top: 24rpx;
    align-items: center;
  }

  .product__max {
    font-size: 26rpx;
    color: #fa5555;
  }

  .product__stock {
    height: 30rpx;
    line-height: 30rpx;
    font-size: 26rpx;
    margin-right: 20rpx;
  }
}

.select {
  padding: 40rpx 30rpx 20rpx 40rpx;
  font-size: 30rpx;
}

.select__title {
  font-size: 28rpx;
}

.select__list {
  display: flex;
  flex-wrap: wrap;
  margin-top: 20rpx;
  overflow: auto;
  // height: 320rpx;
}

.select__list::-webkit-scrollbar {
  display: none;
}

.select__list--value {
  margin-bottom: 20rpx;
  margin-top: 20rpx;
  margin-right: 40rpx;

  .select__list--value--info {
    height: 54rpx;
    border-radius: 100rpx;
    font-size: 26rpx;
    padding: 24rpx;
  }
}

.btn {
  padding-left: 30rpx;
  padding-right: 30rpx;
  padding-top: 20rpx;
  margin-bottom: 20rpx;
  padding-bottom: 0;
  padding-bottom: constant(safe-area-inset-bottom);
  padding-bottom: env(safe-area-inset-bottom);
  font-size: 36rpx;
  display: flex;
  justify-content: space-between;
  height: 60rpx;
  position: fixed;
  width: 100%;
  align-items: center;
  bottom: 160rpx;
}

.poup {
  position: fixed;
  width: 100%;
  display: flex;
  bottom: 0;
  padding-bottom: 0;
  padding-bottom: constant(safe-area-inset-bottom);
  padding-bottom: env(safe-area-inset-bottom);

  &__btn {
    width: 50%;
    height: 98rpx;
    background-color: #515151;
    text-align: center;
    line-height: 98rpx;
    font-size: 30rpx;
    font-family: MicrosoftYaHei;
    font-weight: 400;
    color: #fff;
  }
}
</style>
