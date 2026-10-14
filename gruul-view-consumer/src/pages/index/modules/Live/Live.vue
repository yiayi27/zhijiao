<template>
  <view>
    <view>
      <view class="live-list" style="padding: 0">
        <block v-for="(item, index) in liveList" :key="index">
          <view class="live-list-item">
            <image :data-count="item.viewCount" :data-room_id="item.roomId" :data-id="item.id" :src="item.shareImg" @tap.stop="onGotoLive" />
            <view class="live-item-head">
              <span v-if="item.status === 101" class="head-live">
                <image src="https://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/Live/live.png" />
                直播中
              </span>
              <span v-if="item.status === 102" class="head-no-live">未开始</span>
              <span v-if="item.status === 103" class="head-no-live head-done-live">已结束</span>
              <span v-if="item.status === 104" class="head-no-live head-done-live">禁播</span>
              <span v-if="item.status === 105" class="head-no-live head-done-live">暂停中</span>
              <span v-if="item.status === 106" class="head-no-live head-done-live">异常</span>
              <span v-if="item.status === 107" class="head-no-live head-done-live">已过期</span>
              <span class="head-text">{{ item.viewCount || 0 }}{{ ' ' }}观看</span>
            </view>
            <view style="background: rgba(0, 0, 0, 0.05); height: 130rpx; width: 100%; bottom: 0; position: absolute">
              <view :data-count="item.viewCount" :data-room_id="item.roomId" :data-id="item.id" class="live-item-center" @tap.stop="onGotoLive">
                {{ item.liveName }}
              </view>
              <view class="live-item-bottom">
                <view :data-count="item.viewCount" :data-room_id="item.roomId" :data-id="item.id" class="live-anchor" @tap.stop="onGotoLive">
                  {{ item.anchor }}
                </view>
                <view class="live-like">
                  <image v-if="item.clickLike" class="like-gif" src="https://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/Live/dong_live.gif" />
                  <image
                    :data-count="item.likeCount"
                    :data-room_id="item.roomId"
                    :data-id="item.id"
                    src="https://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/Live/like.png"
                    @tap.stop="onClickLike"
                  />
                  {{ item.likeCount || 0 }}
                </view>
              </view>
            </view>
          </view>
        </block>
        <Loading v-if="isFetch" />
        <view v-if="liveList.length === 0 && !isFetch" class="no-live-data">暂无直播!</view>
      </view>
      <uv-loading-page loading-text="loading..." :loading="loading" bg-color="rgba(0,0,0,0.4)" />
      <block v-if="!loading && total !== 0">
        <view v-if="liveList.length >= total" class="live-tip">到底了~</view>
      </block>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted } from 'vue'
import { onReachBottom } from '@dcloudio/uni-app'
import { useLive } from '@/composables/mixins/useLive'

// 从useLive钩子中解构出所需的状态和方法
const {
  // 状态
  liveList,
  current,
  total,
  isFetch,
  loading,
  timer,

  // 方法
  onGotoLive,
  onClickLike,
  fetchLiveList,
} = useLive()

// 生命周期钩子
onMounted(() => {
  uni.setNavigationBarTitle({
    title: '直播',
  })

  fetchLiveList(1)
})

// 页面触底事件处理
onReachBottom(() => {
  if (liveList.value.length < total.value) {
    fetchLiveList(current.value + 1, true)
  }
})

// 页面卸载时清除定时器
onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
})
</script>

<style lang="scss" scoped>
@include b(live-loading) {
  width: 100%;
  height: 100rpx;
}

@include b(live-tip) {
  height: 100rpx;
  line-height: 100rpx;
  text-align: center;
  color: #b0b0b0;
  font-size: 24rpx;

  @include e(loadMore) {
    background-color: #ffffff;
    color: #333333;
  }
}

@include b(live-list) {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  padding: 0 24rpx;
}

@include b(live-list-item) {
  width: 49%;
  height: 380rpx;
  margin-bottom: 14rpx;
  position: relative;

  > image {
    width: 100%;
    height: 100%;
    background-color: #666;
    object-fit: cover;
  }
}

@include b(live-item-head) {
  position: absolute;
  top: 8rpx;
  left: 16rpx;

  image {
    width: 90rpx;
    height: 30rpx;
    vertical-align: middle;
  }
}

@include b(live-item-bottom) {
  position: absolute;
  bottom: 16rpx;
  padding: 0 16rpx;
  display: flex;
  justify-content: space-between;
  line-height: 40rpx;
  font-size: 24rpx;
  width: 100%;
  height: 40rpx;
  color: #fff;

  image {
    width: 30rpx;
    height: 30rpx;
    vertical-align: middle;
  }
}

@include b(live-like) {
  position: relative;
}

@include b(like-gif) {
  width: 400rpx !important;
  height: 200rpx !important;
  position: absolute;
  bottom: 30rpx;
  right: -100rpx;
}

@include b(head-text) {
  display: inline-block;
  vertical-align: middle;
  line-height: 30rpx;
  font-size: 22rpx;
  padding: 0 12rpx;
  color: #fff;
  background-color: rgba(0, 0, 0, 0.1);
}

@include b(live-item-center) {
  color: #fff;
  font-size: 24rpx;
  bottom: 90rpx;
  left: 16rpx;
  position: absolute;
}

@include b(head-live) {
  background-image: url(https://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/Live/dong.png);
  overflow: hidden;
  background-position: center;
  display: inline-block;
  vertical-align: middle;
  position: relative;
  font-size: 22rpx;
  line-height: 30rpx;
  color: #fff;
  padding: 0 12rpx;

  image {
    width: 24rpx;
    height: 22rpx;
    vertical-align: middle;
  }
}

@include b(head-no-live) {
  display: inline-block;
  vertical-align: middle;
  position: relative;
  font-size: 22rpx;
  line-height: 30rpx;
  color: #fff;
  padding: 0 12rpx;
  padding-left: 24rpx;
  background-color: rgba(0, 226, 100, 1);

  &::before {
    content: '';
    width: 8rpx;
    height: 8rpx;
    border-radius: 8rpx;
    display: block;
    position: absolute;
    left: 8rpx;
    top: 50%;
    margin-top: -4rpx;
    background-color: #fff;
  }
}

.head-done-live {
  background-color: #666;
}

@include b(no-live-data) {
  text-align: center;
  line-height: 400rpx;
  width: 100%;
}
</style>
