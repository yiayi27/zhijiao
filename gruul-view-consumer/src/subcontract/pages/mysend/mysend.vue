<template>
  <view class="mysend">
    <uv-tabs
      :list="tablist"
      :current="active"
      itemStyle="font-size:28rpx;lineHeight:88rpx;background:#fff"
      lineWidth="40"
      lineColor="#ee0a24"
      :activeStyle="{
        color: '#303133',
        fontWeight: 'bold',
        transform: 'scale(1.05)',
      }"
      :inactiveStyle="{
        color: '#606266',
        transform: 'scale(1)',
      }"
      customStyle="background:#fff"
      @change="onTabChange"
    />
    <view class="mysend__list">
      <view v-for="(ite, index2) in datalist" :key="index2" class="mysend__list__item">
        <view class="mysend__list__item--time">
          <view class="mysend__list__item--time__time1">
            <image src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20210531/96c4f6e4115845189d778ce7bd26ef1b.png" />
            <view>{{ ite.createDate }}</view>
          </view>
          <view class="mysend__list__item--time__time2">{{ ite.createTime }}</view>
        </view>
        <view class="mysend__list__item--img">
          <view v-for="(items, index) in ite.articleImgs" :key="index" class="mysend__list__item--img__item">
            <image :src="items" />
          </view>
        </view>
        <view class="mysend__list__item--text">
          <view v-if="ite.edStatus === 0" class="mysend__list__item--text__text1"> 等待您的寄出 </view>
          <view v-if="ite.edStatus === 1" class="mysend__list__item--text__text1">您已寄出</view>
          <view v-if="ite.edStatus === 2" class="mysend__list__item--text__text1">交易已取消</view>
          <view class="mysend__list__item--text__text2">
            <view v-if="ite.edStatus === 0" class="btn red" :data-id="ite.id" @tap="topage"> 去寄出 </view>
            <!-- <view class="btn black" wx:if="{{ ite.edStatus==0 }}" bind:tap="cancal" data-id="{{ite.id}}">
                              取消订单
                          </view> -->
            <view v-if="ite.edStatus === 2" class="btn black" :data-id="ite.id" @tap="del"> 删除订单 </view>
            <view v-if="ite.edStatus === 1" class="btn red" :data-item="ite" @tap="tologistics"> 物流详情 </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>
<script setup lang="ts">
import { ref } from 'vue'
import { onShow, onReachBottom } from '@dcloudio/uni-app'
import { getRecyclingList, putCancalRecycling, delRecycling } from '@/api/modules/user'
import { $Popup } from '@/utils/helper'

// 响应式数据
const active = ref(0)
const tablist = ref([
  {
    name: '全部',
    id: 0,
  },
  {
    name: '待寄出',
    id: 1,
  },
  {
    name: '已寄出',
    id: 2,
  },
])
const datalist = ref<any[]>([])
const dataparams = ref({
  orderStatus: 0,
  page: 1,
  size: 10,
})

// 方法定义
const onTabChange = (event: { id: number }) => {
  dataparams.value.page = 1
  dataparams.value.orderStatus = event.id
  active.value = event.id
  getRecyclingList(dataparams.value).then((res) => {
    res.forEach((item: any) => {
      const articleImgs = item.articleImgs.split(',')
      item.articleImgs = articleImgs
    })
    datalist.value = res
  })
}

const topage = (event: { currentTarget: { dataset: { id: string } } }) => {
  const id = event.currentTarget.dataset.id
  uni.navigateTo({
    url: '/subcontract/pages/sendlogistics/sendlogistics?id=' + id,
  })
}

const cancal = (event: { currentTarget: { dataset: { id: string } } }) => {
  const id = event.currentTarget.dataset.id
  putCancalRecycling(id)
    .then(() => {
      $Popup.toast('取消成功')
      dataparams.value.page = 1
      dataparams.value.orderStatus = active.value
      getRecyclingList(dataparams.value).then((res) => {
        res.forEach((item: any) => {
          const articleImgs = item.articleImgs.split(',')
          item.articleImgs = articleImgs
        })
        datalist.value = res
      })
    })
    .catch((err) => {
      console.log(err)
      $Popup.toast('取消失败')
    })
}

const del = (event: { currentTarget: { dataset: { id: string } } }) => {
  const id = event.currentTarget.dataset.id
  delRecycling(id)
    .then(() => {
      $Popup.toast('删除成功')
      dataparams.value.page = 1
      dataparams.value.orderStatus = active.value
      getRecyclingList(dataparams.value).then((res) => {
        res.forEach((item: any) => {
          const articleImgs = item.articleImgs.split(',')
          item.articleImgs = articleImgs
        })
        datalist.value = res
      })
    })
    .catch((err) => {
      console.log(err)
      $Popup.toast('删除失败')
    })
}

const tologistics = (event: { currentTarget: { dataset: { item: any } } }) => {
  const item = event.currentTarget.dataset.item
  let shipperType = 'sf'
  if (item.shipmentName === '顺丰快递') {
    shipperType = 'sf'
  }
  if (item.shipmentName === '中通快递') {
    shipperType = 'zt'
  }
  if (item.shipmentName === '圆通快递') {
    shipperType = 'yt'
  }
  if (item.shipmentName === '韵达快递') {
    shipperType = 'yd'
  }
  if (item.shipmentName === '百世快递') {
    shipperType = 'ht'
  }
  const data = {
    image: item.articleImgs[0],
    orderDelivery: {
      shipperType,
      shipmentName: item.shipmentName,
      waybillNo: item.shipmentNumber,
    },
  }
  uni.navigateTo({
    url: `/subcontract/pages/senddelivery/senddelivery?data=${encodeURIComponent(JSON.stringify(data))}`,
  })
}

// 获取回收列表数据
const fetchRecyclingList = () => {
  getRecyclingList(dataparams.value).then((res) => {
    res.forEach((item: any) => {
      const articleImgs = item.articleImgs.split(',')
      item.articleImgs = articleImgs
    })
    datalist.value = res
  })
}

// 生命周期
onShow(() => {
  dataparams.value.page = 1
  fetchRecyclingList()
})

// 上拉加载更多
onReachBottom(() => {
  dataparams.value.page = dataparams.value.page + 1
  getRecyclingList(dataparams.value).then((res) => {
    const list1 = datalist.value
    const list2 = res
    list2.forEach((item: any) => {
      const articleImgs = item.articleImgs.split(',')
      item.articleImgs = articleImgs
    })
    const list = list1.concat(list2)
    datalist.value = list
  })
})
</script>
<style lang="scss" scoped>
page {
  background: #eeeeee;
}
.mysend {
  &__list {
    width: 100%;
    font-weight: 500;
    &__item {
      @include flex(center, center);

      flex-direction: column;
      justify-content: flex-start;
      width: 100%;
      height: 330rpx;
      background-color: #fff;
      padding: 0 28rpx;
      margin-top: 20rpx;
      &--time {
        @include flex(space-between, center);

        width: 100%;
        height: 73rpx;
        font-size: 24rpx;
        color: #797979;
        border-bottom: 2rpx solid #f7f7f9;
        &__time1 {
          @include flex(flex-start, center);

          image {
            width: 26rpx;
            height: 26rpx;
          }
          view {
            margin-left: 20rpx;
          }
        }
        &__time2 {
          font-size: 24rpx;
          color: #797979;
        }
      }
      &--img {
        @include flex(flex-start, center);

        width: 100%;
        height: 158rpx;
        border-bottom: 1rpx solid #f7f7f9;
        &__item {
          width: 145rpx;
          height: 133rpx;
          border-radius: 10rpx;
          margin: 0 9rpx;
          image {
            width: 145rpx;
            height: 133rpx;
            border-radius: 10rpx;
          }
        }
      }
      &--text {
        @include flex(space-between, center);

        width: 100%;
        height: 95rpx;
        &__text1 {
          font-size: 24rpx;
          color: #fb315d;

          background: linear-gradient(0deg, #ff7350 0%, #fdb696 100%);
          -webkit-background-clip: text;
          background-clip: text;
          -webkit-text-fill-color: transparent;
        }
        &__text2 {
          @include flex(space-between, center);
          flex-direction: row-reverse;
          width: 305rpx;
          flex-wrap: wrap;
          .btn {
            width: 140rpx;
            height: 60rpx;
            font-size: 24rpx;
            border-radius: 10rpx;
            text-align: center;
            line-height: 60rpx;
            background: #fff;
          }
          .red {
            border: 2rpx solid #ff7350;

            background: linear-gradient(0deg, #ff7350 0%, #fdb696 100%);
            -webkit-background-clip: text;
            background-clip: text;
            -webkit-text-fill-color: transparent;
          }
          .black {
            border: 2rpx solid #797979;
            color: #797979;
          }
        }
      }
    }
  }
}
</style>
