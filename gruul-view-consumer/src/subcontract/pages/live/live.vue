<template>
  <view>
    <view class="live-list">
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
    <uv-loading-page v-if="loading" bg-color="rgba(0,0,0,0.4)" loading-text="loading..." />
    <block v-if="!loading && total !== 0">
      <view v-if="liveList.length >= total" class="live-tip">到底了~</view>
    </block>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad, onUnload, onPullDownRefresh, onReachBottom } from '@dcloudio/uni-app'
import { getLiveList, like, view } from '@/api/modules/live'
import { APP_LAUNCH } from '@/utils/emitNameConfig'

// 声明requirePlugin函数
declare function requirePlugin(/** 需要引入的插件的 alias */ module: string): any
const livePlayer = requirePlugin('live-player-plugin')

// 响应式状态
const liveList = ref<any[]>([])
const current = ref(1)
const total = ref(0)
const isFetch = ref(false)
const loading = ref(false)
const timer = ref<NodeJS.Timeout | number | null>(null)

// 方法定义
const onGotoLive = (e: any) => {
  const roomId = e.currentTarget.dataset.room_id
  const id = e.currentTarget.dataset.id
  let viewCount = e.currentTarget.dataset.count
  try {
    viewCount = parseInt(viewCount, 10) + 1
  } catch (err) {
    viewCount = 1
    console.log(err)
  }
  view({
    id,
    viewCount,
  }).then(() => fetchLiveList())
  uni.navigateTo({
    url: `plugin-private://wx2b03c6e691cd7370/pages/live-player-plugin?room_id=${roomId}`,
  })
}

const onClickLike = (e: any) => {
  const roomId = e.currentTarget.dataset.room_id
  const id = e.currentTarget.dataset.id
  let likeCount = e.currentTarget.dataset.count
  try {
    likeCount = parseInt(likeCount, 10) + 1
  } catch (err) {
    console.log(err)
    likeCount = 1
  }
  const newLiveList = liveList.value
  newLiveList.forEach((v) => {
    if (v.roomId + '' === roomId + '') {
      v.clickLike = true
    }
  })
  liveList.value = newLiveList
  setTimeout(() => {
    newLiveList.forEach((v) => {
      if (v.roomId + '' === roomId + '') {
        v.clickLike = false
        v.likeCount = likeCount
      }
    })
    liveList.value = newLiveList
    like({
      id,
      likeCount,
    })
  }, 400)
}

const fetchLiveList = async (pageNum = 1, isMore = false) => {
  isFetch.value = isMore ? false : true
  loading.value = isMore ? true : false

  const res = await getLiveList({
    current: pageNum,
    size: 20,
  })

  liveList.value = isMore ? [...liveList.value, ...(res.list || [])] : res.list
  current.value = res.current
  total.value = res.total
  isFetch.value = false
  loading.value = false

  setTimeout(() => {
    updateStatus()
    if (timer.value) clearInterval(timer.value as NodeJS.Timeout)
    timer.value = setInterval(updateStatus, 1000 * 60)
  }, 100)
}

const updateStatus = async () => {
  const currentLiveList = [...liveList.value]
  const asyncArrs = currentLiveList.map((v) => {
    return livePlayer.getLiveStatus({
      room_id: v.roomId,
    })
  })

  const arrRes = await Promise.all(asyncArrs)
  const updatedList = currentLiveList.map((v, i) => {
    v.status = arrRes[i].liveStatus
    return v
  })

  liveList.value = updatedList
}

// 生命周期钩子
onLoad(() => {
  fetchLiveList(1)
  uni.$on(APP_LAUNCH, () => {
    fetchLiveList(1)
  })
})

onUnload(() => {
  uni.$off(APP_LAUNCH)
  if (timer.value) clearInterval(timer.value as NodeJS.Timeout)
})

onPullDownRefresh(() => {
  uni.stopPullDownRefresh()
})

onReachBottom(() => {
  if (liveList.value.length < total.value) {
    fetchLiveList(current.value + 1, true)
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
