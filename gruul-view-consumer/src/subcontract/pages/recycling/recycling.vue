<template>
  <view>
    <view v-if="swiperList.length" class="homeSwiper" style="margin: 30rpx auto">
      <swiper
        class="homeSwiper-swiper"
        :indicator-dots="false"
        :autoplay="true"
        :circular="false"
        :vertical="false"
        :interval="2000"
        :duration="500"
        :previous-margin="0 + 'px'"
        :next-margin="0 + 'px'"
        style="height: 300rpx"
        @change="onChange"
      >
        <block v-for="(item, index) in swiperList" :key="index">
          <swiper-item class="homeSwiper-swiper__item" :data-index="index">
            <view class="homeSwiper-swiper__image" style="padding: 0rpx 30rpx">
              <image
                :data-index="index"
                :data-key="item"
                class="skeleton--animate homeSwiper-swiper--angle2 homeSwiper-swiper__boxNM homeSwiper-swiper__angle"
                :src="item"
                mode="scaleToFill"
                :lazy-load="true"
              />
              <!-- 骨架屏，体验有问题，暂时注释 -->
              <!-- <skeleton height="{{height/2}}" loaded="{{item.loaded}}" skeletonKey="{{item.img}}">
          </skeleton> -->
            </view>
          </swiper-item>
        </block>
      </swiper>
      <view class="homeSwiper-swiper__indicator homeSwiper-swiper__icat1">
        <text v-for="(ix, idx) in swiperList.length" :key="idx" :class="curent === idx ? 'homeSwiper-swiper__icat1--active' : ''" />
      </view>
    </view>
    <view class="title">
      <view class="line" />
      <view class="text">图片上传</view>
    </view>
    <view class="photo_list">
      <view v-for="(item, index) in originPhotos" :key="index" class="item">
        <image :src="item" mode="aspectFill" class="photo" />
        <view class="item_btn" :data-index="index" @tap="handleDelPhoto">删除</view>
      </view>
      <view v-if="originPhotos.length < 3" class="item" @tap="handleTakePhoto">
        <image src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/api/fd4ad3fe6e3548ab286614cb6591e4b.png" mode="aspectFill" />
      </view>
    </view>
    <view class="title">
      <view class="line" />
      <view class="text">活动说明</view>
    </view>
    <view class="preInstructions">
      <text class="text">{{ recyclingsetting.activityDesc }}</text>
    </view>
    <view class="bottom" :style="'background-image:url(' + recyclingsetting.submitImg + ')'" @tap="tijiao" />
    <UPopup v-model:show="showCoupon" mode="center" custom-style="background-color: transparent" @close="onClose">
      <view class="card">
        <image :src="couponimg" />
        <view class="text">
          恭喜您获得
          <text>{{ originPhotos.length }}</text>
          张优惠券
        </view>
        <view class="btn" @tap.stop="backHome" />
      </view>
      >
    </UPopup>
    <UPopup v-model:show="isshow" mode="center" custom-style="background-color:transparent;" @close="cancel">
      <view class="popup">
        <image :src="homePromptBgImg" />
        <view class="close" @tap="cancel" />
        <view class="btn" @tap="topage" />
      </view>
    </UPopup>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { uploadFile } from '@/utils/upload'
import { addNewRecycling } from '@/api/modules/global'
import { navtofun } from '@/utils/navtofun'
import { isArray } from 'lodash-es'
import { useSettingStore } from '@/store/modules/setting'
import { $Popup } from '@/utils/helper'

// 定义接口
interface SwiperChangeEvent {
  detail: {
    current: number
  }
}

interface DelPhotoEvent {
  currentTarget: {
    dataset: {
      index: number
    }
  }
}

// 响应式数据
const recyclingsetting = ref<Obj>({})
const swiperList = ref<string[]>([])
const curent = ref(0)
const photos = ref<string[]>([])
const showCoupon = ref(false)
const originPhotos = ref<string[]>([])
const couponimg = ref('')
const isshow = ref(false)
const homePromptBgImg = ref('')
const isUploading = ref(false)

// 方法
const onChange = (e: SwiperChangeEvent) => {
  curent.value = e.detail.current
}

const tijiao = () => {
  if (originPhotos.value.length === 0) {
    $Popup.toast('请上传图片')
    return
  }

  const articleImgs = originPhotos.value.join(',')
  addNewRecycling({
    articleImgs,
  })
    .then((res: string) => {
      couponimg.value = res
      showCoupon.value = true
    })
    .catch((err: any) => {
      if (err === '10086') {
        isshow.value = true
      } else {
        $Popup.toast(err)
      }
    })
}

const onClose = () => {
  showCoupon.value = false
}

const backHome = () => {
  const item = {
    append: 'mall',
    id: +useSettingStore().currentPageId,
    linkUrl: '/pages/index/index',
    name: 'mall',
    sortIndex: 1,
    text: '商超',
    type: 2,
    url: '/pages/index/index',
    selectedIconPath: true,
  }
  item.append = item.name
  item.url = item.linkUrl
  uni.navigateBack({})
  navtofun(item)
}

const handleTakePhoto = () => {
  if (photos.value.length >= 3) {
    return $Popup.toast('已达到上传上限')
  }

  uni.chooseImage({
    count: 3 - originPhotos.value.length,
    sizeType: ['original', 'compressed'],
    success: (res: any) => {
      const tempFilePaths = res.tempFilePaths
      if (isArray(tempFilePaths)) {
        for (const i in tempFilePaths) {
          photos.value.push(tempFilePaths[i])
          uploadPhoto(tempFilePaths[i])
        }
      }
    },
  })
}

const uploadPhoto = (tempFilePath: string) => {
  isUploading.value = true

  uni.showLoading({
    title: '上传中...',
  })

  uploadFile(tempFilePath)
    .then((data: any) => {
      originPhotos.value.push(data)
      isUploading.value = false
      uni.hideLoading()
    })
    .catch(() => {
      isUploading.value = false
      uni.hideLoading()
      $Popup.toast('上传失败')
    })
}

const handleDelPhoto = (e: DelPhotoEvent) => {
  const index = e.currentTarget.dataset.index
  photos.value.splice(index, 1)
  originPhotos.value.splice(index, 1)
}

const cancel = () => {
  isshow.value = false
}

const topage = () => {
  isshow.value = false
  uni.redirectTo({
    url: '/subcontract/pages/mysend/mysend',
  })
}

// 生命周期钩子
onLoad(async () => {
  await useSettingStore().getRecyclingSetting()
  const { pageCarousels, homePromptBgImg: bgImg } = useSettingStore().recyclingsetting

  if (pageCarousels) {
    swiperList.value = pageCarousels.split(',')
  }

  recyclingsetting.value = useSettingStore().recyclingsetting
  homePromptBgImg.value = bgImg
})
</script>

<style lang="scss" scoped>
@include b(homeSwiper) {
  background-color: #ffffff;
  position: relative;
}

@include b(homeSwiper-swiper) {
  height: 100%;

  @include e(item) {
    height: 100%;
  }

  @include e(image) {
    width: 100%;
    height: 100%;
    box-sizing: border-box;
    padding-top: 4rpx;

    image {
      display: block;
      width: 100%;
      height: 100%;
    }
  }

  @include m(angle1) {
    border-radius: 0rpx;
  }

  @include m(angle2) {
    border-radius: 20rpx;
  }

  @include e(boxNM) {
    box-shadow: none;
  }

  @include e(boxCP) {
    box-shadow: 0 0 8rpx rgba(0, 0, 0, 0.2);
  }

  @include e(corners) {
    border-radius: 0;
  }

  @include e(angle) {
    border-radius: 14rpx;
  }

  @include e(indicator) {
    position: absolute;
    display: flex;
    align-items: center;
    justify-content: center;
    bottom: 20rpx;
    // border: 1px solid red;
    height: 40rpx;
    width: 100%;
    box-sizing: border-box;
  }

  @include e(icat1) {
    text {
      display: inline-block;
      width: 16rpx;
      height: 16rpx;
      background-color: #ebedf0;
      opacity: 0.3;
      margin-right: 12rpx;
      border-radius: 50%;
    }

    @include m(active) {
      opacity: 0.8 !important;
      background-color: #fff !important;
    }
  }

  @include e(icat2) {
    position: absolute;
    right: 0;
    bottom: 30rpx;
    display: block;

    text {
      float: right;
      box-sizing: border-box;
      padding: 10rpx 24rpx;
      background-color: rgba(0, 0, 0, 0.2);
      color: #fff;
      font-size: 26rpx;
      border-radius: 32rpx;
    }
  }

  @include e(icat3) {
    text {
      display: inline-block;
      width: 44rpx;
      height: 6rpx;
      background-color: #ebedf0;
      opacity: 0.3;
      margin-right: 12rpx;
      border-radius: 8rpx;
    }

    @include m(active) {
      opacity: 0.8 !important;
      background-color: #fff !important;
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

.title {
  width: 100%;
  height: 60rpx;
  display: flex;
  justify-content: flex-start;
  align-items: center;

  .line {
    width: 20rpx;
    height: 60rpx;
    background-color: #ff5028;
  }

  .text {
    font-size: 28rpx;
    line-height: 60rpx;
    margin-left: 30rpx;
  }
}

.photo_list {
  width: 700rpx;
  margin: 0 auto;
  height: 293rpx;
  display: flex;
  justify-content: flex-start;
  align-items: center;

  .item {
    width: 160rpx;
    height: 233rpx;
    margin: 0 15rpx;
    display: flex;
    flex-direction: column;
    justify-content: flex-start;
    align-items: center;

    image {
      width: 160rpx;
      height: 160rpx;
      border-radius: 20rpx;
    }

    .item_btn {
      width: 110rpx;
      height: 50rpx;
      border-radius: 10rpx;
      background-color: #eeeeee;
      font-size: 26rpx;
      color: #9e9e9e;
      text-align: center;
      line-height: 50rpx;
      margin-top: 20rpx;
    }
  }
}

.preInstructions {
  width: 100%;
  padding: 10rpx 40rpx;

  .text {
    width: 100%;
    height: 42rpx;
    display: 28rpx;
    line-height: 42rpx;
    color: #636363;
  }
}

.bottom {
  width: 100%;
  position: fixed;
  bottom: 0;
  left: 0;
  height: 98rpx;
  // background-color: #FF5028;
  font-size: 28rpx;
  color: #ffffff;
  text-align: center;
  line-height: 98rpx;
  background-size: 100% 100%;
}

.card {
  width: 612rpx;
  height: 695rpx;
  position: relative;

  image {
    width: 100%;
    height: 100%;
  }

  .text {
    width: 100%;
    font-size: 28rpx;
    color: #ffffff;
    position: absolute;
    top: 140rpx;
    left: 0;
    text-align: center;

    text {
      font-size: 55rpx;
      color: #ffe8c5;
    }
  }

  .btn {
    width: 80%;
    height: 80rpx;
    position: absolute;
    left: 0;
    right: 0;
    bottom: 62rpx;
    margin: 0 auto;
    // background: rgba(0,0,0,0.3);
  }
}

.popup {
  width: 610rpx;
  height: 550rpx;
  position: relative;

  image {
    width: 100%;
    height: 100%;
  }

  .close {
    width: 60rpx;
    height: 60rpx;
    // background-color: rgba(0,0,0,0.3);
    position: absolute;
    top: 0;
    right: 0;
  }

  .btn {
    width: 420rpx;
    height: 70rpx;
    // background-color: rgba(0,0,0,0.3);
    position: absolute;
    bottom: 40rpx;
    right: 0;
    left: 0;
    margin: 0 auto;
  }
}
</style>
