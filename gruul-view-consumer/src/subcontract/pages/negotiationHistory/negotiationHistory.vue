<template>
  <view class="negotiation--history">
    <view v-for="(item, index) in list" :key="index" class="negotiation--history__item">
      <view class="item--user">
        <view class="item--user__l">
          <image :src="item.userAvatarUrl || 'https://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/api/head_portrait.jpg'" />
        </view>
        <view class="item--user__r">
          <view class="user--name">{{ item.userName }}</view>
          <view class="user--time">{{ item.updateTime }}</view>
        </view>
      </view>
      <view class="item--message">
        {{ item.info }}
        <view v-if="item.image" class="item--images">
          <image v-for="(it, index2) in item.image" :key="index2" mode="aspectFill" :src="it" />
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getNegotiateHistory } from '@/api/modules/afterSale'
import { $Popup } from '@/utils/helper'

const list = ref<any[]>([])

onLoad(({ id: orderId, type, isGroupLookMember }: any) => {
  let data: any = {
    orderId,
  }
  if (!isGroupLookMember) {
    data.type = type
  }
  getNegotiateHistory(data)
    .then((res) => {
      res.forEach((item: any) => {
        if (item.image) {
          item.image = item.image.split(',')
        }
      })
      list.value = res
    })
    .catch(() => {
      $Popup.toast('协商历史获取失败')
    })
})
</script>

<style lang="scss" scoped>
page {
  background: #f4f4f4;
}

.negotiation--history,
.negotiation--history__item,
.item--user {
  width: 100%;
  height: auto;
}

.negotiation--history__item {
  box-sizing: border-box;
  padding: 25rpx;
  background: #fff;
  margin-bottom: 20rpx;
}

.item--user {
  display: flex;
  align-items: center;
}

.item--user image {
  width: 67rpx;
  height: 67rpx;
}

.item--user__l {
  width: 67rpx;
  height: 67rpx;
  margin-right: 15rpx;
}

// .user--name {}

.user--time {
  margin-top: 5rpx;
  font-size: 22rpx;
  color: #919191;
}

.item--message {
  margin-top: 37rpx;
  word-break: break-all;
}

.item--images {
  width: 100%;
  display: flex;
  justify-content: flex-start;
  margin: 20rpx 0;
}

.item--images image {
  width: 174rpx;
  height: 174rpx;
  margin-right: 10rpx;
}
</style>
