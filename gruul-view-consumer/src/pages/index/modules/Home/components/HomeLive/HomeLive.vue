<template>
  <!-- 直播组件 -->
  <view v-if="liveList.length !== 0 || isFetch" style="background-color: white">
    <view class="spellpre__header">
      <text class="spellpre__header--title">{{ decoretionProperties?.title }}</text>
      <text class="spellpre__header--more" @tap.stop="goLiveList">查看更多 ></text>
    </view>
    <uv-button v-if="decoretionProperties?.type === 0 && decoretionProperties?.id === 8" open-type="contact" class="contact-style" />
    <view>
      <view
        :class="
          'live-list ' +
          (decoretionProperties?.listStyle === 1 ? 'liveListBig' : '') +
          ' ' +
          (decoretionProperties?.listStyle === 3 ? 'liveListScroll' : '')
        "
        :style="'padding:0 ' + (decoretionProperties?.pagePadding + 'px')"
      >
        <block v-for="(item, idx) in liveList" :key="idx">
          <view
            v-if="decoretionProperties?.count !== undefined && idx < decoretionProperties.count"
            :class="
              'live-list-item ' +
              (decoretionProperties?.listStyle === 1 ? 'liveItemBig' : '') +
              ' ' +
              (decoretionProperties?.listStyle === 3 ? 'liveItemScroll' : '')
            "
            :style="
              'margin:' +
              (decoretionProperties?.goodsPadding + 'px') +
              ' 0;' +
              (decoretionProperties?.doodsAngle === 1 && 'border-radius:16rpx;overflow:hidden')
            "
          >
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
                  />
                  {{ item.likeCount || 0 }}
                </view>
              </view>
            </view>
          </view>
        </block>
        <Loading v-if="isFetch" />
        <view v-if="liveList.length === 0 && !isFetch" class="no-live-data">暂无数据</view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { cloneDeep } from 'lodash-es'
import { view, getLiveList } from '@/api/modules/live'

// 引入直播插件
declare function requirePlugin(
  /** 需要引入的插件的 alias */
  module: string,
): any
const livePlayer = requirePlugin('live-player-plugin')

// 定义props
interface DecoretionProperties {
  title?: string
  type?: number
  id?: number
  listStyle?: number
  pagePadding?: number
  goodsPadding?: number
  doodsAngle?: number
  count?: number
}

defineProps<{
  decoretionProperties: DecoretionProperties
}>()

// 定义响应式变量
const liveList = ref<any[]>([])
const current = ref(1)
const total = ref(0)
const isFetch = ref(false)
const loading = ref(false)
const timer = ref<NodeJS.Timeout | null>(null)

// 方法定义
const goLiveList = () => {
  uni.navigateTo({
    url: '/subcontract/pages/live/live',
  })
}

const onGotoLive = (e: { currentTarget: { dataset: { room_id: string; id: string; count: string } } }) => {
  const roomId = e.currentTarget.dataset.room_id
  const id = e.currentTarget.dataset.id
  let viewCount = e.currentTarget.dataset.count
  try {
    viewCount = String(parseInt(viewCount, 10) + 1)
  } catch (err) {
    console.log(err)
    viewCount = '1'
  }
  view({
    id,
    viewCount,
  }).then(() => fetchLiveList())
  uni.navigateTo({
    url: `plugin-private://wx2b03c6e691cd7370/pages/live-player-plugin?room_id=${roomId}`,
  })
}

const fetchLiveList = async (currentPage = 1, isMore = false) => {
  isFetch.value = !isMore
  loading.value = isMore
  const res = await getLiveList({
    current: currentPage,
    size: 20,
  })

  const list = res.list.filter((item: any) => item.firstFlag === 1)
  liveList.value = list
  current.value = res.current
  total.value = res.total
  isFetch.value = false
  loading.value = false
  setTimeout(() => {
    updateStatus()
    if (timer.value) clearInterval(timer.value)
    timer.value = setInterval(() => updateStatus(), 1000 * 60)
  }, 100)
}

const updateStatus = async () => {
  const liveListCopy = cloneDeep(liveList.value)
  const asyncArrs: any[] = liveListCopy.map((v) => {
    return livePlayer.getLiveStatus({
      room_id: v.roomId,
    })
  })
  const arrRes = await Promise.all(asyncArrs)
  liveList.value = cloneDeep(
    liveListCopy.map((v, i) => {
      v.status = arrRes[i].liveStatus
      return v
    }),
  )
}

// 生命周期钩子
onMounted(() => {
  fetchLiveList(1)
})
</script>

<style lang="scss" scoped>
@include b(spellpre) {
  box-sizing: border-box;
  background-color: #fff;

  @include e(header) {
    color: #333;
    height: 80rpx;
    line-height: 80rpx;
    background-color: #fff;

    @include m(title) {
      float: left;
      font-size: 36rpx;
      font-weight: 700;
      margin-left: 40rpx;
    }

    @include m(more) {
      font-size: 28rpx;
      font-weight: 400;
      float: right;
      margin-right: 40rpx;
    }
  }
}

@include b(contact-style) {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
}

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

@include b(liveListScroll) {
  overflow-x: auto !important;
  flex-wrap: nowrap !important;
}

@include b(liveItemScroll) {
  flex-shrink: 0;
  margin-right: 20rpx !important;
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

@include b(liveItemBig) {
  width: 100% !important;
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
