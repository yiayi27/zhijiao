<template>
  <view>
    <view v-if="list.length > 0" class="collectBox">
      <view class="collectBox__listBox">
        <view
          v-for="(item, index) in list"
          :key="index"
          class="collectBox__listBox--in"
          :data-index="index"
          :data-item="item"
          @longpress="deleteCollect"
          @tap="toProductDetail"
        >
          <image :src="item.productPic" lazy-load mode="aspectFill" />
          <view v-if="item.status !== 1" class="collectBox__listBox--in--imgUseless">失效</view>
          <view class="collectBox__listBox--in--content">
            <view class="product__name" :style="'color:' + (item.status !== 1 ? '#ccc' : '')">
              {{ item.productName }}
            </view>
            <view v-if="item.status === 1" class="price__line">
              <view class="product__price">
                <span>{{ item.intPrice }}</span>
                <span style="font-weight: weight; font-size: 26rpx">.{{ item.smaPrice }}</span>
              </view>
              <view class="product__oriPrice">{{ item.originalPrice }}</view>
            </view>
            <view v-else class="price__useless">失效</view>
          </view>
          <view v-if="item.longType" class="longDelate">
            <view class="longDelate__centerDelete" :data-index="index" @tap.stop="deleteSure"> 删除 </view>
          </view>
        </view>
      </view>
      <view class="tipNoMore">-------没有更多了-------</view>
    </view>
    <view v-else :style="'height:' + height + 'px'" class="noReviews">
      <view class="noReviews__center">
        <image src="https://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/api/collect.png" class="noReviews__center--img" />
        <view class="noReviews__center--text">暂无收藏~</view>
      </view>
      <view class="noCollect__button" @tap="gotoIndex">去逛一逛</view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { userCollectList, userDelCollect } from '@/api/modules/goods'
import { useSettingStore } from '@/store/modules/setting'
import { $Popup } from '@/utils/helper'

// 响应式变量
const list = ref<any[]>([])
const height = ref(0)

// 方法定义
const getCollList = () => {
  userCollectList({}).then((res) => {
    res.forEach((item: any) => {
      item.originalPrice = item.originalPrice.toFixed(2)
      item.longType = false
      item.productPrice = item.productPrice.toFixed(2)
      item.intPrice = item.productPrice.split('.')[0]
      item.smaPrice = item.productPrice.split('.')[1]
    })
    list.value = res
  })
}

const deleteCollect = (e: { currentTarget: { dataset: { index: number } } }) => {
  const index = e.currentTarget.dataset.index
  const currentList = list.value
  currentList.forEach((v) => {
    v.longType = false
  })
  list.value = currentList
  currentList[index].longType = true
  list.value[index].longType = currentList[index].longType
}

const toProductDetail = (e: { currentTarget: { dataset: { index: number } } }) => {
  const currentList = list.value
  const index = e.currentTarget.dataset.index
  const type = currentList.some((v) => {
    return v.longType === true
  })
  if (!type) {
    uni.navigateTo({
      url: '/subcontract/pages/detail/detail?id=' + currentList[index].productId,
    })
    return
  }
  currentList.forEach((item) => (item.longType = false))
  list.value = currentList
}

const deleteSure = (e: { currentTarget: { dataset: { index: number } } }) => {
  const index = e.currentTarget.dataset.index
  const currentList = list.value
  const param = {
    originalPrice: currentList[index].originalPrice,
    productId: currentList[index].productId,
    productName: currentList[index].productName,
    productPic: currentList[index].productPic,
    productPrice: currentList[index].productPrice,
    status: currentList[index].status,
  }

  $Popup
    .modal({
      title: '提示',
      content: '确认删除此收藏商品吗',
    })
    .then(() => {
      userDelCollect(param).then(() => {
        uni.showToast({
          title: '删除成功',
          icon: 'none',
        })
        getCollList()
      })
    })
}

const gotoIndex = () => {
  useSettingStore().backHome()
}

// 生命周期钩子
onShow(() => {
  getCollList()
  uni.getSystemInfo({
    success: (res) => {
      height.value = res.windowHeight
    },
  })
})
</script>

<style lang="scss" scoped>
page {
  background-color: #f4f4f4;
}

.collectBox {
  padding: 20rpx;
  background-color: #f4f4f4;
  height: 100%;

  .collectBox__listBox {
    display: flex;
    justify-content: space-between;
    flex-wrap: wrap;

    .collectBox__listBox--in {
      width: 345rpx;
      margin-bottom: 16rpx;
      position: relative;
    }
    image {
      width: 345rpx;
      height: 345rpx;
    }

    .collectBox__listBox--in--imgUseless {
      // width: 340rpx;
      width: 100%;
      height: 50rpx;
      position: absolute;
      bottom: 134rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      font-size: 26rpx;
      background-color: #f9b752;
      opacity: 0.5;
    }

    .collectBox__listBox--in--content {
      width: 100%;
      background-color: white;
      margin-top: -11rpx;
      padding: 20rpx;
      border-bottom-right-radius: 18rpx;
      border-bottom-left-radius: 18rpx;
    }
  }
}

.product__name {
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  font-size: 26rpx;
  height: 36rpx;
}

.price__line {
  display: flex;
  align-items: flex-end;
  margin-top: 10rpx;
}

.price__useless {
  width: 80rpx;
  height: 40rpx;
  border-radius: 100rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #f6f6f6;
  font-size: 26rpx;
  margin-top: 28rpx;
  background-color: #d3d3d3;
}

.product__price {
  margin-top: 10rpx;
  font-size: 34rpx;
  color: #fa5555;
  margin-right: 8rpx;
  font-weight: bold;
}

.product__price::before {
  margin-top: 10rpx;
  content: '￥';
  font-size: 26rpx;
}

.product__oriPrice {
  margin-top: 10rpx;
  font-size: 24rpx;
  color: #ccc;
  font-weight: bold;
  text-decoration: line-through;
  margin-left: 10rpx;
}

.product__oriPrice::before {
  margin-top: 10rpx;
  font-size: 24rpx;
  content: '￥';
}

.longDelate {
  position: absolute;
  width: 80rpx;
  height: 100rpx;
  background-color: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  top: 0;
  width: 349rpx;
  height: 100%;
  border-radius: 18rpx;
}

.longDelate__centerDelete {
  display: flex;
  justify-content: center;
  width: 140rpx;
  height: 140rpx;
  background-color: #fea00b;
  border-radius: 70rpx;
  color: white;
  align-items: center;
}

.tipNoMore {
  display: flex;
  justify-content: center;
  align-items: center;
  color: #d0d2d6;
  font-size: 24rpx;
}

.noCollect {
  display: flex;
  align-items: center;
  justify-content: center;
  // height: 1000rpx;
  width: 100%;
  flex-wrap: wrap;
}

.noCollect__button {
  height: 80rpx;
  width: 240rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #e4322c;
  border-radius: 100rpx;
  border: 1px solid #e4322c;
  font-size: 28rpx;
  margin-top: -220rpx;
}

.shop__noCar {
  margin-top: 140rpx;
  width: 69%;
}

.noReviews {
  display: flex;
  width: 100%;
  justify-content: center;
  background-color: white;
  flex-wrap: wrap;

  &__center {
    display: flex;
    justify-content: center;
    width: 590rpx;
    flex-wrap: wrap;
    height: 680rpx;

    &--img {
      margin-top: 100rpx;
    }

    &--text {
      margin-top: 20rpx;
      font-size: 32rpx;
    }
  }
}
</style>
