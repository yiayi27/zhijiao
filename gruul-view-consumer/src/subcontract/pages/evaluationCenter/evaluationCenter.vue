<template>
  <view class="evaluation__center">
    <block v-if="list.length">
      <block v-for="(item, index) in list" :key="index">
        <block v-for="goods in item.itemList" :key="goods.productId">
          <view class="evaluation__center--item" :data-id="goods.productId" @tap="goGoods">
            <view class="item--date">{{ goods.createTime }}</view>
            <view class="item--content">{{ goods.comment }}</view>
            <view class="item--images">
              <view v-for="(img, i) in goods.picture" :key="i" class="image">
                <image :src="img" :data-item="img" @tap.stop="handleSeePhoto" />
              </view>
            </view>
            <view v-if="goods.reply" class="item--reply">
              <view class="imp">商家回复</view>
              {{ goods.reply }}
            </view>
            <Goods :goodsData="goods" />
            <view class="item--score">
              <text>商品评分{{ goods.rate ? goods.rate : '0' }}.0</text>
              <uv-rate :value="Number(goods.rate)" :count="5" readonly activeColor="#ffd21e"></uv-rate>
            </view>
          </view>
        </block>
      </block>
    </block>
    <block v-else>
      <DataEmpty :infoVisible="false" tips="暂无数据" />
    </block>
    <uv-divider v-if="isLast" textPosition="center">到底了</uv-divider>
  </view>
</template>

<script setup lang="ts">
import { ref, onBeforeUnmount } from 'vue'
import { onLoad, onReachBottom } from '@dcloudio/uni-app'
import { getEvaluateList } from '@/api/modules/order'
import { $Popup } from '@/utils/helper'
import { APP_LAUNCH } from '@/utils/emitNameConfig'

const list = ref<any[]>([])
const query = ref({
  keyword: '',
  current: 1,
  size: 10,
})
const isLast = ref(false)

onLoad(() => {
  getEvaluateListData()
  uni.$on(APP_LAUNCH, () => {
    getEvaluateListData()
  })
})

onBeforeUnmount(() => {
  uni.$off(APP_LAUNCH)
})

onReachBottom(() => {
  query.value.current = ++query.value.current
  getEvaluateListData()
})

const transformPics = (arr: any[]) => {
  return arr.map((item) => {
    item.itemList = item.itemList.map((product: any) => {
      if (product.picture) {
        product.picture = product.picture.split(',')
      }
      return product
    })
    return item
  })
}

const handleSeePhoto = ({
  currentTarget: {
    dataset: { item },
  },
}: any) => {
  uni.previewImage({
    current: item,
    urls: [item],
  })
}

const getEvaluateListData = () => {
  getEvaluateList(query.value)
    .then((res) => {
      let { current, pages, list: resList } = res
      resList = transformPics(resList)
      list.value = current > 1 ? [...list.value, ...resList] : resList

      /** 如果为最后一页设置到底状态 */
      if (current === pages) {
        isLast.value = true
      }
    })
    .catch((err) => {
      $Popup.toast(err || '列表获取失败')
    })
}

const goGoods = ({
  currentTarget: {
    dataset: { id },
  },
}: any) => {
  uni.navigateTo({
    url: `/subcontract/pages/detail/detail?id=${id}`,
  })
}
</script>

<style lang="scss" scoped>
page {
  background: $main-background;
}

@include b(evaluation) {
  @include e(center) {
    width: 100%;
    height: 100%;
    background: $main-background;

    @include m(item) {
      width: 100%;
      padding: 20rpx 30rpx;
      background: #fff;
      margin-bottom: 20rpx;

      .item--date {
        font-size: 24rpx;
        color: #ccc;
      }

      .item--content {
        margin: 20rpx 0;
      }

      .item--reply {
        width: 100%;
        height: auto;
        background: rgb(241, 241, 241);
        margin: 20rpx 0;
        padding: 20rpx 20rpx;
        border-radius: 10rpx;

        .imp {
          font-weight: bold;
          margin: 0 0 10rpx;
        }
      }

      .item--images {
        @include flex(flex-start);

        margin-top: 20rpx;
        margin-bottom: 20rpx;

        .image {
          $s: 120rpx;

          width: $s;
          height: $s;
          background: #ccc;
          margin-right: 20rpx;

          image {
            width: 100%;
            height: 100%;
          }
        }
      }

      .item--score {
        @include flex(space-between);
        padding: 30rpx 0;
        border-top: 1px solid $main-border-color;
      }
    }

    @include m(group) {
      @include flex(space-between);
      padding: 30rpx 30rpx;
      background: #fff;

      margin-bottom: 20rpx;
    }
  }
}
</style>
