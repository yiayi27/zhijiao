<script setup lang="ts">
import HomeSwiperFormData from './HomeSwiper'
import { useVModel } from '@vueuse/core'

/** 属性定义 */
const props = defineProps({
  formData: {
    type: Object as () => typeof HomeSwiperFormData,
    required: true,
  },
})

/** 双向绑定 */
const emit = defineEmits(['update:formData'])

const formModel = useVModel(props, 'formData', emit)

/** 轮播序列号 */
const curent = ref(0)

/**
 * 轮播切换
 */
function onChange(e: number) {
  curent.value = e
}

/**
 * 获取图片样式
 */
const getImageClass = computed(() => {
  const { imageStyle, imageAngle } = formModel.value
  // 1常规 2投影
  const styles = ['homeSwiper-swiper__boxNM', 'homeSwiper-swiper__boxCP']
  // 图片倒角 1直角 2圆角
  const angles = ['homeSwiper-swiper__corners', 'homeSwiper-swiper__angle']
  const gs = styles[+imageStyle - 1]
  const as = angles[+imageAngle - 1]
  return `${gs} ${as}`
})
</script>

<template>
  <div class="homeSwiper" :style="{ height: `${formModel.height / 2}px` }">
    <van-swipe v-if="formModel.swiperList?.length" class="homeSwiper-swiper" :show-indicators="false" :autoplay="3000" @change="onChange">
      <van-swipe-item v-for="(item, index) in formModel.swiperList" :key="index" class="homeSwiper-swiper__item">
        <div class="homeSwiper-swiper__image" :style="{ padding: `0px ${formModel.padding}px` }">
          <van-image width="100%" height="100%" :src="item.img" class="homeSwiper-swiper__imgItem" :class="[getImageClass]" contain />
        </div>
        <div v-if="formModel.indicator === 1" class="homeSwiper-swiper__indicator homeSwiper-swiper__icat1">
          <span v-for="(ix, idx) in formModel.swiperList.length" :key="ix" :class="[curent === idx ? 'homeSwiper-swiper__icat1--active' : '']" />
        </div>
        <div v-if="formModel.indicator === 3" class="homeSwiper-swiper__indicator homeSwiper-swiper__icat3">
          <span v-for="(ix, idx) in formModel.swiperList.length" :key="ix" :class="[curent === idx ? 'homeSwiper-swiper__icat1--active' : '']" />
        </div>
      </van-swipe-item>
    </van-swipe>
    <div v-if="!formModel.swiperList?.length" class="homeSwiper-swiper__nullImg">
      <img src="https://qiniu-app.qtshe.com/u391.png" alt="" />
    </div>
    <div
      v-if="formModel.indicator === 2"
      class="homeSwiper-swiper__indicator homeSwiper-swiper__icat2"
      :style="{ right: `${formModel.padding + 5}px` }"
    >
      <span>{{ `${curent + 1}/${formModel.swiperList.length}` }}</span>
    </div>
  </div>
</template>

<style lang="scss">
.homeSwiper {
  background-color: #fff;
  position: relative;
}

.homeSwiper-swiper {
  height: 100%;

  &__item {
    height: 100%;
  }

  &__image {
    width: 100%;
    height: 100%;
    position: relative;
  }

  &--custom {
    border-radius: 0;
  }

  &--fillet {
    border-radius: 10px;
    overflow: hidden;
  }

  &__imgItem {
    box-sizing: border-box;
    padding-top: 2px;

    img {
      height: calc(100% - 2px);
    }
  }

  &__nullImg {
    box-sizing: border-box;
    overflow: hidden;
    height: 100%;
    background-color: rgba(233, 247, 253, 1);
    display: flex;
    justify-content: center;
    align-items: center;
    img {
      display: inline-block;
      width: 44px;
      height: 46px;
    }
  }

  &__boxNM {
    img {
      box-shadow: none;
    }
  }

  &__boxCP {
    img {
      box-shadow: 0 0 4px rgba(0, 0, 0, 0.2);
    }
  }

  &__corners {
    img {
      border-radius: 0px;
    }
  }

  &__angle {
    img {
      border-radius: 7px;
    }
  }

  &__indicator {
    position: absolute;
    display: flex;
    align-items: center;
    justify-content: center;
    bottom: 10px;
    // border: 1px solid red;
    height: 20px;
    width: 100%;
    box-sizing: border-box;
  }

  &__icat1 {
    span {
      display: inline-block;
      width: 8px;
      height: 8px;
      background-color: #ebedf0;
      opacity: 0.3;
      margin-right: 6px;
      border-radius: 50%;
    }

    &--active {
      opacity: 0.8 !important;
      background-color: #fff !important;
    }
  }

  &__icat2 {
    position: absolute;
    right: 0;
    bottom: 10px;
    display: block;

    span {
      float: right;
      box-sizing: border-box;
      padding: 5px 12px;
      background-color: rgba(0, 0, 0, 0.2);
      color: #fff;
      font-size: 13px;
      border-radius: 16px;
    }
  }

  &__icat3 {
    span {
      display: inline-block;
      width: 22px;
      height: 3px;
      background-color: #ebedf0;
      opacity: 0.3;
      margin-right: 6px;
      border-radius: 4px;
    }

    &--active {
      opacity: 0.8 !important;
      background-color: #fff !important;
    }
  }
}
</style>
