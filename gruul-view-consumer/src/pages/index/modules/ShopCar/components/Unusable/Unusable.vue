<template>
  <view v-if="(shopLoseCarList || []).length > 0" class="shop">
    <view class="shop__top" style="border-radius: 0">
      <text style="font-size: 30rpx">失效商品</text>
      <text style="font-size: 30rpx; color: #ff4444" @tap="emptyUseless">清空失效商品</text>
    </view>
    <view v-for="(good, index) in shopLoseCarList || []" :key="index" class="shop__goods">
      <view class="shop__goods--container" style="height: 280rpx">
        <uv-checkbox disabled="true" style="margin-right: 20rpx" activeColor="#FF4444" />
        <view class="shop__goods--container--unable">
          <view v-if="good.status === 2" class="soldOut">
            <view>已售罄</view>
            <view>SoldOut</view>
          </view>
          <view v-if="good.status === 0" class="soldOut">
            <view>已下架</view>
            <view>DropOff</view>
          </view>
          <view v-if="good.status === 1" class="soldOut">
            <view>活动结束</view>
            <view>ActiveEnd</view>
          </view>
        </view>
        <!-- 失效商品 -->
        <!-- name="{{good.productName}} " img="{{good.pic}}" goodId="{{good.productId}}" skuName="{{good.skuName}}" type="{{good.status}}" -->
        <Goods :good="good" />
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import Goods from '../Goods/Goods.vue'

// 定义props和emits
defineProps<{
  type?: number
  shopLoseCarList?: any[]
}>()

const emit = defineEmits(['emptyUseless'])

// 方法定义
const emptyUseless = () => {
  emit('emptyUseless')
}
</script>

<style lang="scss" scoped>
@include b(shop) {
  height: 100%;
  background-color: white;
  margin: 0 20rpx;
  border-radius: 20rpx;

  @include e(top) {
    width: 100%;
    height: 88rpx;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 20rpx;
    font-size: 30rpx;
  }

  @include e(goods) {
    width: 100%;
    // padding: 20rpx;
    padding-top: 4rpx;
    border-radius: 40rpx;
    padding-bottom: 20rpx;

    @include m(container) {
      display: -webkit-box;
      display: flex;
      -webkit-box-pack: justify;
      justify-content: space-between;
      -webkit-box-align: center;
      align-items: center;
      width: 100%;
      border-top: 1px solid #fafafa;
      padding: 20rpx;
      background-color: white;

      @include m(unable) {
        width: 160rpx;
        height: 160rpx;
        border-radius: 100rpx;
        background-color: rgba(0, 0, 0, 0.3);
        position: absolute;
        left: 112rpx;
        @include flex(center, center);
        border: 4rpx solid white;
      }
    }
  }
}

.soldOut {
  color: white;
  font-size: 26rpx;
  display: flex;
  flex-wrap: wrap;
  padding: 8rpx;
  align-items: center;
  justify-content: center;
}
</style>
