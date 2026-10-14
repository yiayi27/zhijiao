<template>
  <view>
    <view class="assess__list">
      <view class="assess__list--button" :class="{ active: isCurrent('0', query.type) }" data-type="0" @tap="onTypeClick">
        全部
        <view class="assess__list--button--num">({{ evaluateOverview.all || 0 }})</view>
      </view>
      <view class="assess__list--button" :class="{ active: isCurrent('2', query.type) }" data-type="2" @tap="onTypeClick">
        有内容
        <view class="assess__list--button--num">({{ evaluateOverview.hasContent || 0 }})</view>
      </view>
      <view class="assess__list--button" :class="{ active: isCurrent('1', query.type) }" data-type="1" @tap="onTypeClick">
        有图片
        <view class="assess__list--button--num">({{ evaluateOverview.hasPicture || 0 }})</view>
      </view>
    </view>
    <block v-if="evaluateList.length > 0">
      <view v-for="(item, index) in evaluateList" :key="index" class="review">
        <view>
          <view style="display: flex">
            <image :src="item.userAvatarUrl" class="review__img" />
            <view>
              <view class="review__content__rank">
                {{ item.userName }}
                <!-- <view class="review__content__rank--vip" wx:if="{{item.vipType}}">
                    {{item.vip}}
                </view> -->
              </view>
              <view class="review__content__time">
                {{ item.createTime }}
                <view class="review__content__time--para">{{ item.specs }}</view>
              </view>
            </view>
          </view>
          <view class="review__content">
            <view class="review__content--rev">{{ item.comment }}</view>
            <view class="review__content__shopImg">
              <view v-for="(img, index2) in item.picture" :key="index2">
                <image :src="img" class="review__content__shopImg--item" :data-item="img" @tap="handleSeePhoto" />
              </view>
            </view>
          </view>
        </view>
        <view v-if="item.reply" class="review__reply">
          <view class="review__reply__top">
            <view class="review__reply__top--service">掌柜回复：</view>
            <view class="review__reply__top--time">{{ item.replyTime }}</view>
          </view>
          <view style="margin-top: 20rpx">{{ item.reply }}</view>
        </view>
      </view>
    </block>
    <view v-else :style="'height:' + height + 'px'" class="noReviews">
      <view class="noReviews__center">
        <image src="https://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/api/comment.png" class="noReviews__center--img" />
        <view class="noReviews__center--text">暂无评价内容~</view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onBeforeUnmount, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { fetchProductEvaluate, getProductEvaluateOverview } from '@/api/modules/goods'
import { useHelper } from './composables/useHelper'
import { $Popup } from '@/utils/helper'
import { APP_LAUNCH } from '@/utils/emitNameConfig'

// 定义接口
interface EvaluateItem {
  userAvatarUrl: string
  userName: string
  createTime: string
  specs: string
  comment: string
  picture: string | string[]
  reply?: string
  replyTime?: string
  vipType?: string
  vip?: string
}

interface PhotoEvent {
  currentTarget: {
    dataset: {
      item: string
    }
  }
}

interface TypeClickEvent {
  currentTarget: {
    dataset: {
      type: string
    }
  }
}

interface EvaluateResponseData {
  current: number
  pages: number
  list: EvaluateItem[]
}

interface SystemInfoSuccess {
  windowHeight: number
  [key: string]: any
}

// 响应式数据
const { isCurrent } = useHelper()
const evaluateList = ref<EvaluateItem[]>([])
const evaluateOverview = ref<Obj>({})
const query = ref({
  current: 1,
  type: '0',
  keyword: '',
  productId: '',
  size: 10,
})
const isLast = ref(false)
const height = ref(0)
const options = ref({
  id: '',
})

// 方法
const handleSeePhoto = (e: PhotoEvent) => {
  const item = e.currentTarget.dataset.item
  uni.previewImage({
    current: item,
    urls: [item],
  })
}

const getProductEvaluateList = (productId: string) => {
  fetchProductEvaluate({
    ...query.value,
    productId,
  })
    .then((res: any) => {
      const { current, pages, list } = res as EvaluateResponseData

      // 将字符串转为图片
      const formattedList = list.map((item: EvaluateItem) => {
        item.picture = handlePicture(item.picture)
        return item
      })

      evaluateList.value = current > 1 ? [...evaluateList.value, ...formattedList] : formattedList

      /** 如果为最后一页设置到底状态 */
      if (current === pages) {
        isLast.value = true
      }
    })
    .catch((err: any) => {
      $Popup.toast(err || '评价列表获取失败')
    })
}

const getProductEvaluate = (productId: string) => {
  getProductEvaluateList(productId)

  getProductEvaluateOverview({
    productId,
  })
    .then((res: any) => {
      evaluateOverview.value = res
    })
    .catch((err: any) => {
      $Popup.toast(err || '评价概况获取失败')
    })
}

const onTypeClick = (e: TypeClickEvent) => {
  const type = e.currentTarget.dataset.type
  query.value.type = type
  getProductEvaluateList(options.value.id)
}

const handlePicture = (pictures: string | string[]): string[] => {
  return typeof pictures === 'string' ? (pictures ? pictures.split(',') : []) : pictures
}

// 生命周期钩子
onLoad((opts: { id?: string } = {}) => {
  const id = opts.id || ''
  options.value = { id }

  getProductEvaluate(id)

  uni.$on(APP_LAUNCH, () => {
    getProductEvaluate(id)
  })

  uni.getSystemInfo({
    success: (res: SystemInfoSuccess) => {
      height.value = res.windowHeight
    },
  })
})

onBeforeUnmount(() => {
  uni.$off(APP_LAUNCH)
})
</script>

<style lang="scss" scoped>
@mixin center-middle {
  display: flex;
  align-items: center;
  justify-content: center;
}

page {
  background-color: #f2f1f2;
}

.assess__list {
  display: flex;
  justify-content: flex-start;
  font-size: 30rpx;
  padding: 20rpx 30rpx;
  flex-wrap: wrap;
  margin-bottom: 20rpx;
  background: #fff;

  .assess__list--button {
    // width: 220rpx;
    // height: 80rpx;
    padding: 12rpx 30rpx;
    background-color: #f9f2ef;
    border-radius: 50rpx;
    font-size: 24rpx;
    margin-right: 20rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    height: 62rpx;

    .assess__list--button--num {
      font-size: 26rpx;
    }

    &.active {
      background: #fd000c;
      color: #fff;
    }
  }
}

.review {
  padding: 40rpx;
  margin-bottom: 20rpx;
  background: #fff;
}

.review__img {
  width: 80rpx;
  height: 80rpx;
  border-radius: 100%;
  margin-right: 20rpx;
}

.review__content {
  width: 500rpx;
  font-size: 26rpx;
  color: #878787;

  &__rank {
    display: flex;
    align-items: center;
    color: #000;
    margin-bottom: 18rpx;
    font-size: 26rpx;

    &--vip {
      margin-left: 20rpx;
      width: 160rpx;
      height: 40rpx;
      border-radius: 50rpx;
      background-color: #dfd2b5;
      color: white;
      display: flex;
      justify-content: center;
      align-items: center;
    }
  }

  &__time {
    display: flex;
    margin-top: 10rpx;
    color: #b1afaf;
    font-size: 24rpx;

    &--para {
      margin-left: 20rpx;
    }
  }

  &--rev {
    margin-top: 24rpx;
    color: #333333;
  }

  &__shopImg {
    display: flex;
    margin-top: 27rpx;

    &--item {
      display: flex;
      width: 160rpx;
      height: 160rpx;
      margin-right: 20rpx;
    }
  }
}

.review__reply {
  padding: 30rpx;
  border-radius: 10rpx;
  margin-top: 10rpx;
  font-size: 28rpx;
  background-color: #f2f2f2;

  &__top {
    display: flex;
    justify-content: space-between;

    &--service {
      color: #4a4a4a;
      font-weight: bold;
    }

    &--time {
      color: #c4c4c4;
    }
  }
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
      margin-top: 40rpx;
      font-size: 32rpx;
    }
  }
}
</style>
