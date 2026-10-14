<template>
  <view v-if="swiperList.length" class="homeSwiper">
    <block v-if="radio === 1">
      <swiper
        class="homeSwiper-swiper"
        :indicator-dots="false"
        autoplay
        :circular="false"
        :vertical="false"
        :interval="interval * 1000"
        :duration="500"
        :previous-margin="0 + 'px'"
        :next-margin="0 + 'px'"
        :style="{ height: `${height}rpx` }"
        @change="onChange"
      >
        <block v-for="(item, index) in swiperList" :key="index">
          <swiper-item class="homeSwiper-swiper__item" :data-index="index" @tap.stop="handleTap">
            <view class="homeSwiper-swiper__image" :style="{ padding: `4rpx ${padding}rpx` }">
              <image
                :data-index="index"
                :data-key="item.img"
                :class="'skeleton--animate ' + ('homeSwiper-swiper--angle' + imageAngle) + ' ' + getImageClass"
                :src="item.img"
                mode="aspectFill"
                :lazy-load="true"
              />
              <!-- 骨架屏，体验有问题，暂时注释 -->
              <!-- <skeleton height="{{height/2}}" loaded="{{item.loaded}}" skeletonKey="{{item.img}}">
          </skeleton> -->
              <uv-button v-if="item.link.type === 0 && item.link.id === 8" open-type="contact" class="contact-style" />
            </view>
          </swiper-item>
        </block>
      </swiper>
    </block>
    <block v-if="radio === 2">
      <view class="kuang" :style="{ height: `${height}rpx`, padding: `${sidePadding}rpx` }">
        <scroll-view
          class="homeSwiper-swiper"
          :enable-flex="true"
          :scroll-x="true"
          :enhanced="true"
          :show-scrollbar="false"
          :style="{ backgroundImage: `url(${btnImg})` }"
          @change="onChange"
        >
          <!-- <view class="homeSwiper-swiper__view"> -->
          <block v-for="(item, index) in swiperList" :key="index">
            <view class="homeSwiper-swiper__item" :data-index="index" :style="{ width: '750rpx', marginRight: `${margin}rpx` }" @tap.stop="handleTap">
              <view class="homeSwiper-swiper__image" :style="{ padding: `0 ${padding}rpx` }">
                <image
                  :data-index="index"
                  :data-key="item.img"
                  :class="'skeleton--animate ' + ('homeSwiper-swiper--angle' + imageAngle) + ' ' + getImageClass"
                  :src="item.img"
                  mode="aspectFill"
                  :lazy-load="true"
                />
                <uv-button v-if="item.link.type === 0 && item.link.id === 8" open-type="contact" class="contact-style" />
              </view>
            </view>
          </block>
          <view style="width: 375rpx; height: 100%; display: inline-block" />
          <!-- </view> -->
        </scroll-view>
      </view>
    </block>
    <block v-if="radio === 3">
      <swiper
        class="homeSwiper-swiper"
        :indicator-dots="false"
        :autoplay="true"
        :circular="true"
        :vertical="false"
        :interval="interval * 1000"
        :duration="500"
        :previous-margin="200 + 'rpx'"
        :next-margin="200 + 'rpx'"
        :style="{ height: `${height}rpx` }"
        @change="onChange"
      >
        <block v-for="(item, index) in swiperList" :key="index">
          <swiper-item class="homeSwiper-swiper__item" :data-index="index" @tap.stop="handleTap">
            <view class="homeSwiper-swiper__image" :style="{ transform: curent === index ? 'scale(1, 1)' : 'scale(0.8, 0.8)', margin: 'auto' }">
              <image
                :data-index="index"
                :data-key="item.img"
                :class="'skeleton--animate ' + ('homeSwiper-swiper--angle' + imageAngle) + ' ' + getImageClass"
                :src="item.img"
                mode="aspectFill"
                :lazy-load="true"
              />
              <!-- 骨架屏，体验有问题，暂时注释 -->
              <!-- <skeleton height="{{height/2}}" loaded="{{item.loaded}}" skeletonKey="{{item.img}}">
          </skeleton> -->
              <uv-button v-if="item.link.type === 0 && item.link.id === 8" open-type="contact" class="contact-style" />
            </view>
          </swiper-item>
        </block>
      </swiper>
    </block>
    <view v-if="radio === 3" />
    <view v-else-if="radio === 2" />
    <view v-else-if="indicator === 1" class="homeSwiper-swiper__indicator homeSwiper-swiper__icat1">
      <text v-for="(ix, idx) in swiperList.length" :key="idx" :class="[curent === idx ? 'homeSwiper-swiper__icat1--active' : '']" />
    </view>
    <view v-else-if="indicator === 3" class="homeSwiper-swiper__indicator homeSwiper-swiper__icat3">
      <text v-for="(ix, idx) in swiperList.length" :key="idx" :class="[curent === idx ? 'homeSwiper-swiper__icat1--active' : '']" />
    </view>
    <view v-else-if="indicator === 2" class="homeSwiper-swiper__indicator homeSwiper-swiper__icat2" :style="{ right: `${padding + 10}rpx` }">
      <text>{{ curent + 1 }}/{{ swiperList.length }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import { navtofun } from '@/utils/navtofun'

// 定义类型
interface LinkSelectItem {
  id: number
  /** 链接类型 0 -功能页面 FunctionPage 1-商超商品 Goods 2 -展示分类 DisplayClassify 3-自定义页面 CustomPage 4 活动营销 ActivityMarket */
  type: number
  /** 链接名称 */
  name: string
  /** 链接地址 */
  url: string
  /** 附加参数 */
  append: string
}

interface SwiperListItem {
  title: string
  img: string
  link: LinkSelectItem
}

// 定义props
const props = defineProps({
  decoretionProperties: {
    type: Object,
    default: () => {},
  },
})

// 响应式变量
const type = ref('')
const swiperList = ref<SwiperListItem[]>([])
const padding = ref(10)
const imageStyle = ref(1)
const imageAngle = ref(1)
const indicator = ref(1)
const curent = ref(0)
const height = ref(185)
const radio = ref(1)
const btnImg = ref('')
const interval = ref(1)
const sidePadding = ref(10)
const margin = ref(5)

// 计算属性
const getImageClass = computed(() => {
  // 1常规 2投影
  const styles = ['homeSwiper-swiper__boxNM', 'homeSwiper-swiper__boxCP']
  // 图片倒角 1直角 2圆角
  const angles = ['homeSwiper-swiper__corners', 'homeSwiper-swiper__angle']
  const gs = styles[+imageStyle.value - 1]
  const as = angles[+imageAngle.value - 1]
  return `${gs} ${as}`
})

// 方法定义
const getProperties = (formdata: any) => {
  type.value = formdata.type
  swiperList.value = formdata.swiperList
  padding.value = formdata.padding
  imageStyle.value = formdata.imageStyle
  imageAngle.value = formdata.imageAngle
  indicator.value = formdata.indicator
  height.value = formdata.height
  radio.value = formdata.radio
  btnImg.value = formdata.btnImg
  interval.value = formdata.interval
  sidePadding.value = formdata.sidePadding
  margin.value = formdata.margin
}

const handleTap = (e: { currentTarget: { dataset: { index: number } } }) => {
  const index: number = e.currentTarget.dataset.index
  const currentSwiperItem = swiperList.value[index]
  const itemLink = currentSwiperItem.link
  navtofun(itemLink)
}

const onChange = (e: { detail: { current: number } }) => {
  curent.value = e.detail.current
}

// 监听props变化
watch(
  () => props.decoretionProperties,
  (newVal) => {
    if (newVal) {
      getProperties(newVal)
    }
  },
  {
    deep: true,
    immediate: true,
  },
)
</script>

<style lang="scss" scoped>
@include b(homeSwiper) {
  background-color: #ffffff;
  position: relative;
}

@include b(homeSwiper-swiper) {
  height: 100%;
  white-space: nowrap;
  background-size: 100% 100%;

  @include e(view) {
    height: 100%;
    display: flex;
    white-space: nowrap;
  }

  @include e(item) {
    height: 100%;
    display: inline-block;
  }

  @include e(image) {
    width: 100%;
    height: 100%;
    box-sizing: border-box;
    padding-top: 4rpx;
    transition: transform 0.5s;

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

.kuang {
  width: 100%;
}
</style>
