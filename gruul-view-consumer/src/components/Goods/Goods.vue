<template>
  <view>
    <view class="goods">
      <view class="goods__image">
        <view class="goods__image--tag" />
        <image :src="goodsData.productSkuPic || goodsData.productPic || goodsData.picture || goodsData.img" mode="aspectFill" />
      </view>
      <!-- <image style="width: 400rpx; height: 400rpx; background-color: #eeeeee;" mode="{{item.mode}}" src="{{src}}"></image> -->
      <view class="goods__info">
        <view class="goods--name">{{ goodsData.productName || goodsData.goodName }}</view>
        <view class="goods--spec">{{ goodsData.specs }}</view>
        <view class="goods--specification">
          <text class="goods__price">￥{{ goodsData.productPrice || goodsData.price }}</text>
          <text>x{{ goodsData.productQuantity || goodsData.num }}</text>
        </view>
      </view>
    </view>
    <view class="goods__status">
      <slot />
    </view>
  </view>
</template>

<script setup lang="ts">
// 定义接口
interface GoodsData {
  productSkuPic?: string
  productPic?: string
  picture?: string
  img?: string
  productName?: string
  goodName?: string
  specs?: string
  productPrice?: Long
  price?: Long
  productQuantity?: Long
  num?: Long
  [key: string]: any
}

// 定义Props
defineProps({
  goodsData: {
    type: Object as () => GoodsData,
    default: () => ({}),
  },
})
</script>

<style lang="scss" scoped>
@import '@/assets/styles/var.scss';
.goods {
  @include flex(space-between, flex-start);
  margin-bottom: 20rpx;
  .goods__image {
    width: 200rpx;
    height: 200rpx;
    background: #ccc;
    margin-right: 20rpx;
    flex: 1 0 150rpx;
    position: relative;

    &--tag {
      position: absolute;
      left: 0;
      top: 0;
    }

    image {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }

  .goods__info {
    @include flex(space-between, flex-start);

    flex-direction: column;
    width: 500rpx;
    height: 200rpx;

    .goods--spec {
      font-size: 24rpx;
      color: #c7c7c7;
    }

    .goods--specification {
      @include flex(space-between, flex-start);
      width: 100%;
      color: #9c9c9c;
      margin-top: auto;
      .goods__price {
        font-size: 28rpx;
        color: #000;
      }
    }
  }
}
</style>
