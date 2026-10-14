<template>
  <view class="customer">
    <view v-for="(item, index) in customersList" :key="index" class="customer__item">
      <view class="customer__item__upper">
        <image class="customer__item__upper--photo" :src="item.avatarUrl" />

        <view class="information">
          <view class="information__name">
            {{ item.nikeName }}
          </view>
          <view class="information__layout">
            <view class="information__layout--time"> 注册时间：{{ item.createTime }} </view>
            <!-- <view class="information__layout--people">
          推广0人
          </view> -->
          </view>
        </view>
      </view>
      <view class="customer__item__statistics">
        <view class="customer__item__statistics--consumption">消费{{ item.singleUserAmount === null ? 0 : item.singleUserAmount }}元</view>
        <view class="customer__item__statistics--order">{{ item.userOrderCount }}个订单</view>
      </view>
    </view>
    <uv-divider contentPosition="center">没有更多了~</uv-divider>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onReachBottom } from '@dcloudio/uni-app'
import { distributionDetailGet as distributionDetailGetApi } from '@/api/modules/distribution'

const current = ref(1)
const customersList = ref<Obj[]>([])

onReachBottom(() => {
  current.value = ++current.value
  distributionDetailGet()
})

const distributionDetailGet = () => {
  distributionDetailGetApi({
    current: current.value,
  }).then((res) => {
    customersList.value = {
      ...customersList.value,
      ...res.list,
    }
  })
}

distributionDetailGet()
</script>

<style lang="scss" scoped>
@import '@/assets/styles/mixins/mixins.scss';

@include b(customer) {
  // padding-top: 50rpx;
  font-size: 30rpx;
  @include e(item) {
    background: #fff;
    width: 750rpx;
    height: 275rpx;
    padding: 30rpx 30rpx 41rpx;
    margin-top: 10rpx;
  }
}

@include b(customer__item) {
  @include e(upper) {
    border-bottom: 1px solid #eeedee;
    display: flex;
    padding-bottom: 35rpx;
    @include m(photo) {
      width: 101rpx;
      height: 101rpx;
      margin-right: 15rpx;
    }
  }
}

@include b(information) {
  width: 83%;
  @include e(name) {
  }
  @include e(layout) {
    color: #5c5c5c;
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 24rpx;
    @include m(time) {
      font-size: 28rpx;
    }
  }
}

@include b(customer__item) {
  @include e(statistics) {
    display: flex;
    justify-content: space-between;
    margin-top: 38rpx;
  }
}
</style>
