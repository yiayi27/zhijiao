<template>
  <view class="goods">
    <image :src="good.pic" mode="aspectFill" class="goods__image" alt @tap.stop="gotoDeatil" />
    <view :class="'goods__info ' + (good.status === 1 && checkStock !== 0 ? '' : 'padding')">
      <view :class="' ' + (good.status === 1 && checkStock !== 0 ? 'goods--hasName' : 'goods--name')" @tap.stop="gotoDeatil">
        {{ good.productName }}
      </view>
      <view v-if="good.skuName !== '' && checkStock !== 0" class="goods--num" @tap.stop="getSku">
        <view class="skuName">
          {{ good.skuName }}
        </view>
        <uv-icon name="arrow-down" class="goods--icon" size="14" />
      </view>
      <view v-if="good.skuName !== '' && checkStock === 0" class="goods--noNum">
        {{ good.skuName }}
      </view>
      <view v-if="good.status === 1 && checkStock !== 0" class="goods--specification">
        <span class="goods--price">
          <view class="product__price">
            <span>{{ intPrice }}</span>
            <span style="font-weight: weight; font-size: 26rpx">.{{ smaPrice }}</span>
          </view>
        </span>
        <slot />
      </view>
      <view v-if="good.status === 1 && checkStock === 0 && good.skuStocks.length > 1">
        <view class="goods--reTry">
          请重新选择规格
          <view class="button" @tap.stop="getSku">重选</view>
        </view>
        <view v-if="good.status !== 1">
          <view class="soldOut">
            <view>{{ good.status === 2 ? '已售罄' : '已下架' }}</view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

// 定义props和emits
const props = defineProps<{
  good: Obj
  checkStock?: number
}>()

const emit = defineEmits(['getSku'])

// 响应式变量
const intPrice = ref('')
const smaPrice = ref('')

// 方法定义
const goodChange = () => {
  if (props.good) {
    const good = props.good
    let intPriceValue = ''
    let smaPriceValue = ''
    if (good.actPrice) {
      intPriceValue = good.actPrice.toFixed(2).split('.')[0]
      smaPriceValue = good.actPrice.toFixed(2).split('.')[1]
      intPrice.value = intPriceValue
      smaPrice.value = smaPriceValue
    }
  }
}

const gotoDeatil = () => {
  const good = props.good
  uni.navigateTo({
    url: `/subcontract/pages/detail/detail?id=${good.productId}`,
  })
}

const getSku = () => {
  const good = props.good
  const options = {
    id: good.productId,
    skuId: good.skuId,
  }
  emit('getSku', options)
}

// 监听good变化
watch(
  () => props.good,
  () => {
    goodChange()
  },
  { immediate: true },
)
</script>

<style lang="scss" scoped>
@import '@/assets/styles/var.scss';

.goods {
  @include flex(space-between, flex-start);
  $s: 190rpx;

  .goods__image {
    width: $s;
    height: $s;
    flex: 1 0 $s;
    margin-right: 20rpx;
    border-radius: 20rpx;
  }

  .goods--hasName {
    font-size: 26rpx;
    font-weight: bold;
    color: #535353;
  }

  .goods--name {
    font-size: 26rpx;
    font-weight: bold;
    color: #9c9c9c;
  }

  .goods__info {
    @include flex(space-between, space-between);
    flex-direction: column;
    width: 440rpx;
    padding-right: 48rpx;
    height: $s;
    .goods--specification {
      @include flex(space-between, flex-start);

      width: 100%;
      color: #9c9c9c;
    }

    .goods--price {
      .important {
        font-size: 16.1rpx;
        color: #ff4444;
        font-weight: bold;
      }
    }

    .goods--num {
      font-size: 22rpx;
      color: #333333;
      background: #ececec;
      display: flex;
      align-items: flex-end;
      border-radius: 100rpx;
      border: 1px solid $main-border-color;
      padding: 10rpx 20rpx 10rpx 20rpx;
      align-self: baseline;
    }

    .goods--noNum {
      display: flex;
      width: 180rpx;
      font-size: 24rpx;
      color: #9c9c9c;
    }

    .goods--reTry {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-top: 10rpx;
      color: #333333;
      font-size: 28rpx;

      .button {
        width: 120rpx;
        height: 50rpx;
        @include flex(center, center);
        border: 1px solid #f84642;
        color: #f84642;
        border-radius: 80rpx;
      }
    }
  }
}

.padding {
  padding-bottom: 60rpx;
}

.soldOut {
  color: black;
  margin-top: 112rpx;
  font-size: 30rpx;
}

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
</style>
