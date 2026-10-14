<template>
  <view class="mLogs">
    <view v-for="(item, index) in list" :key="index" class="mLogs__item">
      <view class="mLogs__item-title">
        <view>{{ item.title }}LV{{ item.rankCode }}</view>
        <view class="mLogs__item-value">
          {{ item.openType === 1 || item.openType === 3 ? '¥' : '' }}{{ item.openValue || '' }}{{ item.openType === 2 ? '积分' : '' }}
        </view>
      </view>
      <view class="mLogs__item-content">
        <view>
          <text>购买会员时间：</text>
          {{ item.memberRegisterTime }}
        </view>
        <view>
          <text>会员有效期：</text>
          {{ item.memberExpireTime }}
        </view>
        <view>
          <text>开卡方式：</text>
          {{ item.openType === 1 ? '付费购买' : '' }}
          {{ item.openType === 2 ? '积分购买' : '' }}
          {{ item.openType === 3 ? '累计消费' : '' }}
          {{ item.openType === 4 ? '购买指定商品' : '' }}
        </view>
        <view>
          <text>卡号：</text>
          {{ item.memberNumber }}
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getMemberCardRecord } from '@/api/modules/member'

const list = ref<any[]>([])

onLoad(() => {
  getMemberCardRecord(null).then((data) => {
    list.value = data
  })
})
</script>

<style lang="scss" scoped>
page {
  background: #eee;
}
.mLogs {
  padding: 18rpx;
  min-height: 100% !important;
  background-color: rgba($color: #000000, $alpha: 0.1);
}

.mLogs__item {
  background-color: #fff;
  margin-bottom: 18rpx;
  border-radius: 8rpx;
}

.mLogs__item-title {
  border-bottom: 1rpx solid #bbb;
  padding: 18rpx;
  display: flex;
  justify-content: space-between;
  font-size: 28rpx;
  line-height: 60rpx;
}

.mLogs__item-value {
  color: #999;
}

.mLogs__item-content {
  padding: 18rpx;
  font-size: 26rpx;
  > view {
    margin-bottom: 16rpx;
    color: #999;
    > text {
      color: #333;
    }
  }
}
</style>
