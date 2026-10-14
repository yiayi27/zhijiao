<template>
  <view class="myfund" :style="'background:url(' + bgimage + ') no-repeat 100% 100%'">
    <view class="myfund__top">
      <image :src="userInfo.avatarUrl" />
      <view class="myfund__top--text">
        <view class="myfund__top--text__name">{{ userInfo.nikeName }}</view>
        <view class="myfund__top--text__pai"> 您目前位于第{{ funddata.ranking ? funddata.ranking : '--' }}名 </view>
      </view>
    </view>
    <view class="myfund__centen">
      <view class="myfund__centen__item">已存入：{{ funddata.totalFundQuota }}元</view>
      <view class="myfund__centen__xian" />
      <view class="myfund__centen__item">订单数：{{ funddata.totalOrderNumber }}单</view>
    </view>
    <view class="myfund__list">
      <scroll-view
        class="myfund__list--scroll"
        style="height: calc(100vh - 300rpx)"
        :scroll-y="true"
        lower-threshold="10rpx"
        :enhanced="true"
        :show-scrollbar="false"
        @scrolltolower="scrolltolower"
      >
        <view v-for="(item, index) in list" :key="index" :class="'myfund__list--scroll__item ' + (index % 2 > 0 ? 'hui' : '')">
          <view class="item__time">{{ item.orderPayTime }}</view>
          <view class="item__price">您下单{{ item.payAmount }}元</view>
          <view class="item__text" :data-id="item.orderId" @tap="goDetail"> 订单详情 </view>
        </view>
      </scroll-view>
    </view>
  </view>
</template>
<script setup lang="ts">
import { ref } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import { getRecyclingStatistical, getRecyclingOrderlist } from '@/api/modules/user'
import { useUserStore } from '@/store/modules/user'
import { useSettingStore } from '@/store/modules/setting'

// 响应式数据
const bgimage = ref('')
const userInfo = ref<Obj>({})
const list = ref<any[]>([])
const pagedata = ref({
  page: 1,
  size: 20,
})
const funddata = ref<Obj>({})

// 方法定义
const scrolltolower = () => {
  pagedata.value.page = pagedata.value.page + 1
  getRecyclingOrderlist(pagedata.value)
    .then((res) => {
      const list1 = list.value
      const list2 = res
      const newList = list1.concat(list2)
      list.value = newList
    })
    .catch((err) => {
      console.log(err)
    })
}

const goDetail = (event: { currentTarget: { dataset: { id: string } } }) => {
  const id = event.currentTarget.dataset.id
  uni.navigateTo({
    url: `/subcontract/pages/orderDetail/orderDetail?orderId=${id}`,
  })
}

// 生命周期
onLoad(() => {
  userInfo.value = useUserStore().userInfo
  bgimage.value = useSettingStore().recyclingsetting.pageBgImg
})

onShow(() => {
  getRecyclingStatistical()
    .then((res) => {
      funddata.value = res
    })
    .catch((err) => {
      console.log(err)
    })
  pagedata.value.page = 1
  getRecyclingOrderlist(pagedata.value)
    .then((res) => {
      list.value = res
    })
    .catch((err) => {
      console.log(err)
    })
})
</script>
<style lang="scss" scoped>
.myfund {
  width: 100vw;
  height: 100vh;
  background-repeat: no-repeat;
  background-size: 100% auto;
  &__top {
    width: 100%;
    height: 180rpx;

    @include flex(flex-start, center);

    padding: 0 20rpx;
    image {
      width: 114rpx;
      height: 114rpx;
      border-radius: 50%;
    }
    &--text {
      @include flex(center, flex-start);
      flex-direction: column;

      width: 500rpx;
      height: 114rpx;
      margin-left: 20rpx;
      &__name {
        font-size: 34rpx;
        color: #333333;
        font-weight: 500;
        margin-bottom: 10rpx;
      }
      &__pai {
        font-size: 28rpx;
        color: #333333;
        font-weight: 500;
      }
    }
  }
  &__centen {
    width: 710rpx;
    height: 110rpx;
    border-radius: 20rpx;
    box-shadow: 0rpx 0rpx 24rpx 0rpx rgba(70, 178, 255, 0.41);
    background-color: #fff;
    @include flex(center, center);
    margin: 0 auto;
    &__item {
      width: 350rpx;
      font-size: 34rpx;
      color: #333333;
      font-weight: 500;
      text-align: center;
    }
    &_xian {
      height: 80rpx;
      width: 2rpx;
      background-color: #ececec;
    }
  }
  &__list {
    width: 710rpx;
    height: calc(100% - 300rpx);
    margin: 0 auto;
    margin-top: 10rpx;
    &--scroll {
      width: 100%;
      height: 100%;
      &__item {
        width: 100%;
        height: 96rpx;
        @include flex(space-between, center);
        font-size: 28rpx;
        color: #333333;
        // background-color: #fff;
        .item__time {
          width: 300rpx;
        }
        .item__price {
          width: 250rpx;
          text-align: left;
        }
        .item__text {
          width: 150rpx;
          color: #2e99f3;
        }
      }
      .hui {
        // background-color: #F7FBFF;
      }
    }
  }
}
</style>
