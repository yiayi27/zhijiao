<template>
  <view>
    <div v-if="dataList.length" class="swiper__list-page">
      <swiper
        class="homeSwiper-swiper"
        :indicator-dots="true"
        indicator-color="#fff"
        :autoplay="true"
        :circular="false"
        :vertical="false"
        :interval="3000"
        :duration="500"
        :previous-margin="0 + 'px'"
        :next-margin="0 + 'px'"
        style="height: 220rpx"
      >
        <block v-for="(item, index) in dataList" :key="index">
          <swiper-item class="homeSwiper-swiper__item" :data-index="index" @tap.stop="handleTap">
            <view class="homeSwiper-swiper__image" style="padding: 4rpx 0">
              <image class="homeSwiper-swiper--angle2 homeSwiper-swiper__angle" :src="item.img" mode="scaleToFill" lazy-load="true" />
            </view>
          </swiper-item>
        </block>
      </swiper>
    </div>
  </view>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { navtofun } from '@/utils/navtofun'

// 定义Props
const props = defineProps<{
  propData: Obj
}>()

// 响应式数据
const dataList = ref<any[]>([])
const formData = ref<Obj>({})
const isFirst = ref(true)

// 方法
const getFormData = () => {
  if (props.propData) {
    dataList.value = []
    formData.value = props.propData
    if (!formData.value.firstCatList?.length) return
    const data = isFirst.value ? formData.value.firstCatList[0].swiperList : formData.value.currentFirstCategory.swiperList
    dataList.value = data
    isFirst.value = false
  }
}

const handleTap = (e: any) => {
  const index: number = e.currentTarget.dataset.index
  const currentSwiperItem = dataList.value[index]
  const itemLink = currentSwiperItem.link
  navtofun(itemLink, 'poster')
}

// 监听属性
watch(
  () => props.propData,
  () => {
    getFormData()
  },
  { deep: true },
)

// 生命周期
onMounted(() => {
  getFormData()
})
</script>

<style lang="scss" scoped>
.swiper__list-page {
  @include b(homeSwiper) {
    background-color: #ffffff;
    position: relative;
  }

  @include b(homeSwiper-swiper) {
    height: 100%;
    margin-bottom: 60rpx;

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
}
</style>
