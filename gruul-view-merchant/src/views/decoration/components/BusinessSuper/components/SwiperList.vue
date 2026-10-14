<script setup lang="ts">
import BusinessSuper from './../BusinessSuper'
import 'vant/lib/swipe/style'
import 'vant/lib/swipe-item/style'
import 'vant/lib/image/style'

defineProps({
  formData: {
    type: Object as () => typeof BusinessSuper,
    required: true,
  },
})

/** 轮播序列号 */
const curent = ref(0)

/**
 * 轮播切换
 */
function onChange(e: number) {
  curent.value = e
}
</script>

<template>
  <div class="swiper__list-page">
    <van-swipe :show-indicators="false" :autoplay="3000" style="height: 110px" @change="onChange">
      <van-swipe-item v-for="(item, index) in formData.currentFirstCategory.swiperList" :key="index">
        <div class="swiper-con__image">
          <van-image width="100%" height="100%" :src="item.img" class="swiper-con__imgItem swiper-con__boxNM swiper-con__corners" contain />
        </div>
        <div v-if="formData.currentFirstCategory.swiperList" class="swiper-con__indicator swiper-con__icat1">
          <span
            v-for="(ix, idx) in formData.currentFirstCategory.swiperList.length"
            :key="ix"
            :class="[curent === idx ? 'swiper-con__icat1--active' : '']"
          />
        </div>
      </van-swipe-item>
    </van-swipe>
  </div>
</template>

<style lang="scss">
.swiper__list-page {
  margin-bottom: 30px;

  .swiper-con {
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
        border-radius: 4px;
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

  .swiper-con__corners {
    img {
      border-radius: 4px;
    }
  }
}
</style>
