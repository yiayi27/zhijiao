<template>
  <view>
    <view v-if="!hasChainAct" class="no-live-data">暂无接龙活动，敬请期待~</view>
    <view v-else>
      <block data-src="../../../chainDetail/chainDetail.wxml">
        <view class="chainBox" :style="'padding-bottom:' + (bottomType ? '60' : '80') + 'px'">
          <view class="chainTop">
            <view class="chainTop__content">
              <block data-src="./preImg.wxml">
                <view class="line">
                  <view class="circle" />
                  <view class="lineOne" />
                  <view class="LineTwo" />
                </view>
              </block>
              <span style="font-size: 32rpx">接龙活动</span>
              <block data-src="./preImg.wxml">
                <view class="line">
                  <view class="circle" />
                  <view class="lineOne" />
                  <view class="LineTwo" />
                </view>
              </block>
            </view>
          </view>
          <view class="chainDesc">
            <view class="chainDesc__top">
              <span class="name">{{ activityDetail.name }}</span>
              <span v-if="countDetail.status === 'ended'" class="day">
                {{ activityDetail.stopTime }}
              </span>
            </view>
            <view class="chainDesc__desc">{{ activityDetail.content }}</view>
            <view class="chainDesc__img">
              <image :src="activityDetail.coverPoster" alt style="width: 100%; height: 340rpx" />
            </view>
          </view>
          <view class="chainBottom">
            <view class="chainBottom__left">
              <span>有{{ activityDetail.views }}人看过</span>
              <span>{{ activityDetail.userRecords }}人参与</span>
            </view>
            <view v-if="countDetail.status !== 'ended'" class="time__count">
              <view class="time__count--text">
                <text>还剩</text>
              </view>
              <uv-count-down :time="remainTime" @change="handleChange">
                <view style="display: flex; align-items: center">
                  <view class="time">{{ timeData.hours[0] }}</view>
                  <view class="time">{{ timeData.hours[1] }}</view>
                  <view v-if="timeData.hours.length > 2" class="time">{{ timeData.hours[2] }}</view>
                  <view class="colon">:</view>
                  <view class="time">{{ timeData.minutes[0] }}</view>
                  <view class="time">{{ timeData.minutes[1] }}</view>
                  <view class="colon">:</view>
                  <view class="time">{{ timeData.seconds[0] }}</view>
                  <view class="time">{{ timeData.seconds[1] }}</view>
                </view>
              </uv-count-down>
            </view>
            <view v-if="countDetail.status === 'aboutToBegin'" class="chainBottom__status">距开始</view>
            <view v-if="countDetail.status === 'beginning'" class="chainBottom__status">距结束</view>
            <view v-if="countDetail.status === 'ended'" class="chainBottom__status" style="background-color: #cccccc"> 已结束 </view>
          </view>
          <view class="listBox" :style="'margin-top:' + (appMode === 'MALL' ? '40rpx' : '')">
            <view v-for="(item, index) in activityList" :key="index" style="background-color: white" :data-item="item" @tap.stop="jumpDetail">
              <view class="listBox__line">
                <view class="listBox__line--image">
                  <image :src="item.pic" alt style="width: 100%; height: 100%" />
                  <view v-if="item.allStock === 0" class="unableIcon">
                    <view class="soldOut">
                      <view>已售罄</view>
                      <view>SoldOut</view>
                    </view>
                  </view>
                </view>
                <view class="listBox__content">
                  <view class="listBox__content--name">{{ item.name }}</view>
                  <view v-if="item.skuStocks.length > 1" class="listBox__content--skuName" :data-item="item" @tap.stop="clickSku">
                    {{ item.actStocks.specs }}
                    <uv-icon name="arrow-down" />
                  </view>
                  <view class="listBox__content--limit" :style="'margin-top:' + (item.skuStocks.length > 1 ? '0rpx' : '80rpx')">
                    <view v-if="item.actStocks.perLimit > 0" class="border"> 限购{{ item.actStocks.perLimit }}件 </view>
                    <view class="border">已售{{ item.actStocks.sale }} / 剩余{{ item.actStocks.stock }}件</view>
                  </view>
                  <view class="listBox__content__price">
                    <view style="display: flex; align-items: flex-end">
                      <view class="price">{{ item.actStocks.price }}</view>
                      <view class="originPrice">{{ item.actStocks.originalPrice }}</view>
                    </view>
                    <view style="display: flex; align-items: center; width: 140rpx; justify-content: space-between">
                      <MIcon name="icongouwuche-jian" size="46rpx" :data-item="item" @tap.stop="decrease" />
                      <view>{{ item.actStocks.goodNum }}</view>
                      <MIcon name="icongouwuche-jia" size="46rpx" style="color: #fc425a" :data-item="item" @tap.stop="increase" />
                    </view>
                  </view>
                </view>
              </view>
            </view>
            <view v-if="activityList.length === 0" class="shopNoCar">
              <!-- <image src="https://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/api/noCar.png" class="shop__noCar" /> -->
              <view class="shop__noCar--text">暂无商品~</view>
            </view>
          </view>
          <!-- <view class="center--middle" style="font-size:28rpx" bind:tap="clickCopy">点击复制链接 参与接龙</view> -->
          <view v-if="!hasListMore">
            <block data-src="./buyRecoder.wxml">
              <view v-if="buyQuery.length > 0" class="recodeList">
                <view class="recodeList__title">购买记录</view>
                <view v-for="(listItem, index) in buyQuery" :key="index">
                  <view class="recodeList__list--item">
                    <view style="display: flex; align-items: center; justify-content: space-between">
                      <view class="index">{{ index + 1 }}.</view>
                      <view class="imgBox">
                        <image :src="listItem.userAvatarUrl" alt style="width: 120rpx; height: 120rpx" />
                      </view>
                      <view>{{ listItem.userName }}</view>
                      <view class="right">
                        <view>{{ listItem.payTime }}</view>
                        <view class="join">我参与的</view>
                      </view>
                    </view>
                    <view class="goodLink">
                      <view style="display: flex; margin-left: 40rpx; padding-top: 32rpx; width: 120rpx">产品:</view>
                      <view class="recodeList__list">
                        <view v-for="(item, index2) in listItem.itemVoList" :key="index2" style="margin: 10rpx 0rpx">
                          <view class="goodName">{{ index + 1 }}. {{ item.productName }}</view>
                        </view>
                      </view>
                    </view>
                    <view class="goodLink">
                      <view style="width: 140rpx; display: flex; margin-left: 40rpx">金额:</view>
                      <view style="width: 500rpx">￥{{ listItem.payAmount }}</view>
                    </view>
                    <view class="goodLink">
                      <view style="width: 140rpx; display: flex; margin-left: 40rpx">联系人:</view>
                      <view style="width: 500rpx">
                        {{ listItem.receiverName }} {{ listItem.receiverPhone }} {{ listItem.receiverDetailAddress }}
                      </view>
                    </view>
                  </view>
                </view>
              </view>
            </block>
            <block data-src="./chainRecoder.wxml">
              <view v-if="joinList.length > 0" class="JoinList">
                <view class="JoinList__title">接龙记录</view>
                <view v-for="(joinItem, index) in joinList" :key="index" class="listContent">
                  <view class="listContent__top">
                    <view class="index">{{ index + 1 }}.</view>
                    <image :src="joinItem.userAvatarUrl" alt class="image" />
                    <view style="margin-right: 20rpx; width: 140rpx; white-space: nowrap; overflow: hidden; text-overflow: ellipsis">
                      {{ joinItem.userName }}
                    </view>
                    <view style="font-size: 24rpx">({{ joinItem.payTime }})</view>
                  </view>
                  <view class="listContent__goodList">
                    <view v-for="(item, index2) in joinItem.itemVoList" :key="index2">
                      <view class="listContent__goodList--box">
                        <view>{{ item.productName }}</view>
                        <view>*{{ item.productQuantity }}</view>
                      </view>
                    </view>
                  </view>
                </view>
                <view class="center--middle" @tap="showMore">{{ hasMore ? '查看更多...' : '没有更多了' }}</view>
              </view>
            </block>
          </view>
          <view class="bottom" :style="'bottom:' + (bottomType ? '0' : bottomHeight) + 'px'">
            <view class="bottom__left">
              <view class="bottom__left--tip">合计:</view>
              <view class="bottom__left--price">￥{{ totalPrice }}</view>
            </view>
            <view class="bottom__account" :style="'background-color:' + (countDetail.status === 'beginning' ? '#fd435b' : '#CCCCCC')" @tap="submit">
              结算({{ goodNum }})
            </view>
          </view>
          <UPopup v-model:show="showVisible" mode="bottom" :customStyle="{ height: '800rpx', flex: 'auto' }" @close="onClose">
            <!-- 弹窗选择规格 -->
            <block data-src="./skuPage.wxml">
              <view class="skuPop">
                <view class="skuPop__top">
                  <image :src="currentItem.actStocks.pic" alt style="width: 240rpx; height: 240rpx" />
                  <view class="skuPop__top--content">
                    <view style="display: flex; width: 100%; align-items: flex-end">
                      <view class="price">{{ currentItem.actStocks.price }}</view>
                      <view class="originalPrice">{{ currentItem.actStocks.originalPrice }}</view>
                    </view>
                    <view class="skuContent">当前所选规格：{{ currentItem.actStocks.specs }}</view>
                  </view>
                </view>
                <view class="skuPop__text">规格</view>
                <view class="skuPop__popList">
                  <view v-for="(item, index) in currentItem.skuStocks" :key="index">
                    <view
                      v-if="item.stock === 0"
                      class="skuPop__popList--item"
                      style="background-color: #f2f2f2; color: #cdcfd0; border: '2rpx solid #F2F2F2'"
                      :data-skuItem="item"
                      @tap="selectSku"
                    >
                      {{ item.specs }}
                    </view>
                    <view
                      v-else
                      class="skuPop__popList--item"
                      :style="
                        'color:' +
                        (item.isCheck && item.stock > 0 ? 'red' : '') +
                        '; background-color:' +
                        (item.isCheck ? '#F6EBEC' : '#F2F2F2') +
                        '; border:' +
                        (item.isCheck ? '2rpx solid red' : '2rpx solid #F2F2F2')
                      "
                      :data-skuItem="item"
                      @tap="selectSku"
                    >
                      {{ item.specs }}
                    </view>
                  </view>
                </view>
              </view>
              <view class="center--middle">
                <view class="sureButton" @tap="onClose">确 定</view>
              </view>
            </block>
          </UPopup>
        </view>
      </block>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useChain } from '@/composables/mixins/useChain'
import { getActivityList, getActivitySetting as fetchActivitySetting } from '@/api/modules/chain'
import { useSettingStore } from '@/store/modules/setting'
import { onReachBottom } from '@dcloudio/uni-app'

defineOptions({
  name: 'Chain',
})

// 从useChain中解构出所有需要的状态和方法
const {
  activityDetail,
  activityList,
  countDetail,
  timeData,
  showVisible,
  currentItem,
  totalPrice,
  goodNum,
  buyQuery,
  joinList,
  hasMore,
  hasListMore,
  goodQuery,
  actId,
  hasChainAct,
  bottomType,
  bottomHeight,
  remainTime,

  // 解构方法
  getActivity,
  getPosition,
  getHeight,
  getListDetail,
  clickSku,
  selectSku,
  onClose,
  increase,
  decrease,
  submit,
  showMore,
  handleChange,
  jumpDetail,
} = useChain()

// 本地定义的额外状态
const appMode = ref('')

// 本地方法 - 先声明函数
const getActivityContent = () => {
  getActivityList({})
    .then((res: any) => {
      actId.value = res.id
      getActivity(res.id)
    })
    .catch(() => {
      hasChainAct.value = false
    })
}

const getActivitySetting = () => {
  fetchActivitySetting({}).then((res: any) => {
    hasChainAct.value = Boolean(res.activityState)
    if (hasChainAct.value) {
      getActivityContent()
    }
  })
}

// 生命周期
onMounted(() => {
  uni.setNavigationBarTitle({
    title: '接龙',
  })
  getActivitySetting()
  getPosition(0)
  getHeight()
  const settingStore = useSettingStore()
  if (settingStore.shopSetting) {
    const shopSetting = settingStore.shopSetting as ShopSetting
    appMode.value = shopSetting.appMode || ''
  }
})

// 直接使用uni-app的页面生命周期钩子
onReachBottom(() => {
  if (hasListMore.value) {
    goodQuery.page = goodQuery.page + 1
    getListDetail()
  }
})

// 保留一些仍然需要的类型定义
interface ShopSetting {
  appMode?: string
  hasOpenEvaluate: boolean
  rebateSet: Obj
  openMember: boolean
  curShopInfoDto: Record<string, string>
  [key: string]: any
}
</script>

<style lang="scss" scoped>
page {
  background-color: #f8f8f8;
}

.chainBox {
  padding: 20rpx;
  padding-bottom: 120rpx;
}

.line {
  display: flex;
  align-items: center;
}

.circle {
  width: 16rpx;
  height: 16rpx;
  background-color: #e75e75;
  border-radius: 100rpx;
}

.lineOne {
  width: 16rpx;
  height: 40rpx;
  border-radius: 20rpx;
  background-color: #eeb84e;
  transform: skewX(-35deg);
  margin-left: 16rpx;
}

.LineTwo {
  width: 16rpx;
  height: 32rpx;
  border-radius: 20rpx;
  background-color: #e78159;
  transform: skewX(-35deg);
  margin-left: 16rpx;
}

@include b(chainTop) {
  display: flex;
  justify-content: center;
  padding-top: 40rpx;
  background-color: white;

  @include e(content) {
    display: flex;
    align-items: center;
    width: 360rpx;
    justify-content: space-between;
  }
}

@include b(chainDesc) {
  padding: 40rpx 20rpx 0rpx 20rpx;
  background-color: white;

  @include e(top) {
    display: flex;
    justify-content: space-between;

    .name {
      font-size: 36rpx;
      font-weight: 700;
    }

    .day {
      font-size: 28rpx;
      color: #bcbcbc;
    }
  }

  @include e(desc) {
    margin-top: 20rpx;
    color: #868686;
  }

  @include e(img) {
    width: 100%;
    margin-top: 20rpx;
  }

  @include e(list) {
    margin-top: 20rpx;

    @include m(content) {
      display: flex;
      flex-wrap: wrap;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 10rpx;
      font-size: 26rpx;

      .line {
        display: flex;
        width: 290rpx;
        justify-content: space-between;
        align-items: center;

        .contentId {
          width: 40rpx;
          display: flex;
          justify-content: flex-end;
        }

        .img {
          width: 80rpx;
          height: 80rpx;
          border-radius: 100rpx;
        }
      }
    }
  }
}

@include b(chainBottom) {
  display: flex;
  padding: 20rpx;
  margin-top: -10rpx;
  font-size: 24rpx;
  justify-content: space-between;
  align-items: center;
  color: #a1a1a1;
  background-color: white;

  @include e(left) {
    width: 280rpx;
    display: flex;
    justify-content: space-between;
  }

  @include e(status) {
    width: 120rpx;
    height: 48rpx;
    display: flex;
    justify-content: center;
    align-items: center;
    border-radius: 100rpx;
    font-size: 24rpx;
    color: white;
    background-color: #f73119;
  }
}

.time__count {
  display: flex;
  align-items: center;
}

.time__count--text {
  text-align: center;
  margin: 0rpx 8rpx;
  color: #a1a1a1;
}

.colon {
  margin-left: 4rpx;
  margin-right: 8rpx;
  margin-bottom: 4rpx;
}

.time {
  width: 20rpx;
  height: 30rpx;
  margin-right: 4rpx;
  background-color: #000000;
  text-align: center;
  font-size: 22rpx;
  font-family: HiraginoSansGB-W3;
  font-weight: normal;
  color: #fff;
  line-height: 42rpx;
  border-radius: 6rpx;
  display: flex;
  justify-content: center;
  align-items: center;
  padding-bottom: 2rpx;
}

.selectWay {
  padding: 40rpx 80rpx;
  display: flex;
  width: 100%;
  justify-content: space-around;
  align-items: center;
}

.selectWay__left {
  color: #ff0000;
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  width: 160rpx;
  height: 60rpx;
}

.selectWay__left--line {
  width: 70rpx;
  height: 10rpx;
  border-radius: 100rpx;
  background-color: #ff0000;
  margin-top: 8rpx;
}

.listBox {
  // padding: 20rpx 40rpx;
  // background-color: white;

  &__line {
    display: flex;
    padding: 20rpx 0rpx;
    margin-bottom: 20rpx;
    padding-left: 20rpx;

    &--image {
      width: 220rpx;
      height: 220rpx;
      margin-right: 20rpx;
      position: relative;

      .unableIcon {
        width: 160rpx;
        height: 160rpx;
        border-radius: 100rpx;
        background-color: rgba(0, 0, 0, 0.3);
        position: absolute;
        left: 30rpx;
        @include flex(center, center);
        border: 4rpx solid white;
        top: 40rpx;
        .soldOut {
          color: white;
          font-size: 26rpx;
          display: flex;
          flex-wrap: wrap;
          padding: 8rpx;
          align-items: center;
          justify-content: center;
        }
      }
    }
  }

  &__content {
    display: flex;
    flex-wrap: wrap;
    width: 420rpx;
    align-content: space-between;

    &--name {
      width: 100%;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      font-size: 28rpx;
    }

    &--skuName {
      padding: 2rpx 10rpx;
      background-color: #fdfdfd;
      border: 2rpx solid #e4e4e4;
      display: -webkit-box;
      display: flex;
      justify-content: center;
      -webkit-box-align: center;
      align-items: center;
      font-size: 22rpx;
      margin: 20rpx 0rpx;
      border-radius: 14rpx;
      color: #a2a2a2;
    }

    &--limit {
      width: 100%;
      display: flex;
      justify-content: space-between;

      .border {
        padding: 20rpx;
        background-color: #fff3f3;
        color: #ff5050;
        border-radius: 100rpx;
        height: 40rpx;
        font-size: 22rpx;
        display: flex;
        align-items: center;
        justify-content: center;
      }
    }

    &__price {
      display: flex;
      width: 100%;
      align-items: center;
      justify-content: space-between;
      font-size: 24rpx;

      .price {
        color: #fe7787;
        margin-right: 40rpx;
      }

      .price:before {
        content: '￥';
        color: #fe7787;
      }

      .originPrice {
        color: #a1a1a1;
        text-decoration: line-through;
      }

      .originPrice:before {
        content: '￥';
        color: #a1a1a1;
      }

      .iconClass {
        color: #ff0000;
        font-size: 0.7rpx;
        margin: 0rpx 0.4rpx;
      }
    }
  }
}

.bottom {
  position: fixed;
  bottom: 0rpx;
  height: 100rpx;
  background-color: white;
  width: 100%;
  left: 0rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;

  &__left {
    margin-left: 40rpx;
    display: flex;
    align-items: flex-end;
    font-size: 28rpx;

    &--tip {
      display: flex;
      align-content: flex-end;
    }

    &--price {
      color: #fd435b;
      font-size: 32rpx;
      margin-left: 20rpx;
    }
  }

  &__account {
    width: 200rpx;
    height: 100%;
    background-color: #fd435b;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-size: 36rpx;
  }
}

.skuPop {
  padding: 40rpx;

  &__top {
    display: flex;
    align-items: flex-end;

    &--content {
      margin-left: 20rpx;
      display: flex;
      flex-wrap: wrap;
      padding-bottom: 20rpx;
      font-size: 30rpx;
      width: 460rpx;

      .price {
        color: #fa5555;
        font-weight: bold;
        margin-bottom: 10rpx;
      }

      .price:before {
        content: '￥';
      }

      .originalPrice {
        color: #bbbbbb;
        margin-bottom: 10rpx;
        margin-left: 28rpx;
        font-size: 26rpx;
        text-decoration: line-through;
      }

      .originalPrice:before {
        content: '￥';
      }

      .skuContent {
        font-size: 28rpx;
      }
    }
  }

  &__text {
    margin: 30rpx 0rpx;
    font-size: 32rpx;
  }

  &__popList {
    height: 280rpx;
    overflow: auto;
    display: flex;
    flex-wrap: wrap;

    &--item {
      padding: 10rpx 20rpx;
      border: 2rpx solid #f2f2f2;
      margin-right: 40rpx;
      border-radius: 160rpx;
      font-size: 26rpx;
      margin-bottom: 40rpx;
    }
  }
}

.skuPop__popList::-webkit-scrollbar {
  display: none;
}

.recodeList {
  background-color: white;
  padding: 40rpx;

  &__title {
    display: flex;
    justify-content: center;
    font-size: 28rpx;
    font-weight: bold;
  }

  &__list {
    padding-top: 20rpx;
    margin-left: 30rpx;
    width: 500rpx;

    &--item {
      border-bottom: 2rpx solid #f8f8f8;
      margin-bottom: 40rpx;
      padding-bottom: 40rpx;
      font-size: 28rpx;

      .index {
        font-size: 24rpx;
      }

      .imgBox {
        width: 130rpx;
        height: 130rpx;
        margin: 0rpx 20rpx;
      }

      .right {
        display: flex;
        justify-content: flex-end;
        width: 360rpx;
        color: #bababa;

        .join {
          padding-left: 50rpx;
          color: #17d145;
        }
      }

      .goodName {
        font-size: 28rpx;
      }

      .goodLink {
        margin: 20rpx 0rpx;
        font-size: 28rpx;
        display: flex;
      }
    }
  }
}

.JoinList {
  background-color: white;
  padding: 40rpx;
  margin-top: 40rpx;

  &__title {
    display: flex;
    justify-content: center;
    font-size: 28rpx;
    font-weight: bold;
    margin-bottom: 40rpx;
  }
}

.listContent {
  margin-bottom: 40rpx;

  &__top {
    display: flex;
    align-items: center;
    border: 2rpx solid #e4e4e4;
    padding: 36rpx;
    font-size: 28rpx;

    .index {
      font-size: 26rpx;
      margin-right: 20rpx;
    }

    .image {
      width: 100rpx;
      height: 100rpx;
      margin-right: 20rpx;
    }
  }

  &__goodList {
    border: 2rpx solid #e4e4e4;
    border-top: none;

    &--box {
      display: flex;
      justify-content: space-between;
      padding: 20rpx 40rpx;
      font-size: 28rpx;
    }
  }
}

.center--middle {
  display: flex;
  justify-content: center;
  align-items: center;
}

.sureButton {
  background-color: #ff0000;
  display: flex;
  position: fixed;
  bottom: 24rpx;
  width: 160rpx;
  height: 60rpx;
  justify-content: center;
  align-items: center;
  color: white;
  border-radius: 100rpx;
  font-size: 30rpx;
}

.shopNoCar {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  width: 100%;
  height: 10vh;
  background-color: #ffffff;
}

.shop__noCar {
  margin-top: 140rpx;
  width: 69%;
}

.shop__noCar--text {
  display: flex;
  justify-content: center;
  color: #acacac;
  width: 100%;
  padding-bottom: 440rpx;
}

.no-live-data {
  text-align: center;
  line-height: 1200rpx;
  width: 100%;
}
</style>
