<template>
  <view class="stepperBox">
    <uv-number-box v-model="num" integer :longPress="false" :min="1" :max="maxNum" @change="processInputValue" @overlimit="overLimit">
      <template #minus>
        <uv-icon v-if="num <= 1" name="trash-fill" size="46rpx" color="#fd435b" @click="delGood" />
        <uv-icon v-else name="minus-circle" size="46rpx" color="#fd435b"> </uv-icon>
      </template>
      <template #plus>
        <uv-icon name="plus-circle-fill" size="46rpx" color="#fd435b" />
      </template>
    </uv-number-box>
  </view>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import type { GoodDetail } from '@/pages/index/modules/ShopCar/types'

// 定义props
const props = defineProps({
  good: {
    type: Object as () => GoodDetail,
    default: () => ({}),
  },
  index: {
    type: Number,
    default: 0,
  },
})

// 定义emit
const emit = defineEmits(['changeNum', 'delGood'])
const maxNum = defineModel<number>('maxNum', { required: true, default: 99 })

// 响应式数据
const num = defineModel<number>('num', { required: true, default: 0 })

const stockNum = ref<number>(0)
const perLimit = ref<number>(0)

// 方法
const goodChange = () => {
  // 实现初始化逻辑
  if (props.good) {
    // 添加空值检查
    if (props.good.skuStocks && Array.isArray(props.good.skuStocks)) {
      props.good.skuStocks.forEach((item) => {
        if (item.id === props.good.skuId) {
          maxNum.value = !item.perLimit ? item.stock : item.perLimit < item.stock ? item.perLimit : item.stock
          perLimit.value = !item.perLimit ? item.stock : item.perLimit
          stockNum.value = item.stock
        }
      })
    } else if (props.good.checkStock) {
      // 如果没有skuStocks，但有checkStock，使用checkStock
      stockNum.value = props.good.checkStock
      perLimit.value = props.good.perLimit || 0
      maxNum.value = !props.good.perLimit
        ? props.good.checkStock
        : props.good.perLimit < props.good.checkStock
          ? props.good.perLimit
          : props.good.checkStock
    }
  }
}

// 监听props变化
watch(
  () => props.good,
  () => {
    goodChange()
  },
  { immediate: true },
)

// 输入处理方法
const processInputValue = (inputValue: { name: string; value: number }) => {
  num.value = inputValue.value
  changeNum(num.value)
}

const changeNum = (newNum: number) => {
  let index = props.index
  emit('changeNum', {
    num: Number(newNum) <= 0 ? 1 : newNum,
    index,
  })
}

const delGood = () => {
  emit('delGood', props.index) // 传递索引给父组件
}

const overLimit = (e: any) => {
  if (e === 'plus') {
    uni.showToast({
      title: `最多只能购买${maxNum.value}件`,
      icon: 'none',
    })
  }
}
</script>

<style lang="scss" scoped>
.stepperBox {
  :deep(.uv-number-box__slot) {
    width: 50rpx;
  }
}
</style>
