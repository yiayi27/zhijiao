<script setup lang="ts">
/** 轮播图 */
// 定义组件
const props = defineProps({
  formData: {
    type: Object,
    default: () => ({}),
  },
})

const emit = defineEmits(['update:formData'])

// 引用原来的formModel
const formModel = ref(props.formData)

// 监听btnImg的变化
watch(
  () => props.formData.btnImg,
  (newValue) => {
    formModel.value.btnImg = newValue
    emit('update:formData', formModel.value)
  },
)

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
  const styles = ['GoodSwiper-swiper__boxNM', 'GoodSwiper-swiper__boxCP']
  // 图片倒角 1直角 2圆角
  const angles = ['GoodSwiper-swiper__corners', 'GoodSwiper-swiper__angle']
  const gs = styles[+imageStyle - 1]
  const as = angles[+imageAngle - 1]
  return `${gs} ${as}`
})
</script>

<template>
  <div class="GoodSwiper" :style="{ height: `${formModel.height / 2}px` }">
    <van-swipe
      v-if="formModel.swiperList?.length && formModel.radio === 1"
      class="GoodSwiper-swiper"
      :show-indicators="false"
      :autoplay="3000"
      @change="onChange"
    >
      <van-swipe-item v-for="(item, index) in formModel.swiperList" :key="index" class="GoodSwiper-swiper__item">
        <div class="GoodSwiper-swiper__image" :style="{ padding: `0px ${formModel.padding}px` }">
          <van-image width="100%" height="100%" :src="item.img" class="GoodSwiper-swiper__imgItem" :class="[getImageClass]" contain />
        </div>
        <div v-if="formModel.indicator === 1" class="GoodSwiper-swiper__indicator GoodSwiper-swiper__icat1">
          <span v-for="(ix, idx) in formModel.swiperList.length" :key="ix" :class="[curent === idx ? 'GoodSwiper-swiper__icat1--active' : '']" />
        </div>
        <div v-if="formModel.indicator === 3" class="GoodSwiper-swiper__indicator GoodSwiper-swiper__icat3">
          <span v-for="(ix, idx) in formModel.swiperList.length" :key="ix" :class="[curent === idx ? 'GoodSwiper-swiper__icat1--active' : '']" />
        </div>
      </van-swipe-item>
    </van-swipe>
    <div v-else-if="formModel.radio === 2" style="position: relative">
      <img class="btnIgClass" :style="{ height: `${formModel.height / 2}px` }" :src="formModel.btnImg" />
      <div
        style="display: flex; width: 100%; overflow: auto; align-items: center; position: absolute"
        :style="{ padding: `0px ${formModel.sidePadding}px` }"
      >
        <div v-for="(item, index) in formModel.swiperList" :key="index">
          <img
            :style="{
              height: `${formModel.height / 2 - 20}px`,
              width: '150px',
              marginRight: `${formModel.margin}px`,
            }"
            :src="item.img"
            style="margin-left: 10px; margin-top: 10px; display: flex; align-items: center"
            contain
          />
        </div>
        <div v-if="formModel.indicator === 1" class="GoodSwiper-swiper__indicator GoodSwiper-swiper__icat1 fixedPosition">
          <span v-for="(ix, idx) in formModel.swiperList.length" :key="ix" :class="[curent === idx ? 'GoodSwiper-swiper__icat1--active' : '']" />
        </div>
        <div v-if="formModel.indicator === 3" class="GoodSwiper-swiper__indicator GoodSwiper-swiper__icat3 fixedPosition">
          <span v-for="(ix, idx) in formModel.swiperList.length" :key="ix" :class="[curent === idx ? 'GoodSwiper-swiper__icat1--active' : '']" />
        </div>
      </div>
    </div>
    <div v-else-if="formModel.swiperList?.length && formModel.radio === 3" style="position: relative">
      <div style="display: flex; width: 100%; overflow: auto; align-items: center">
        <div v-for="(item, index) in formModel.swiperList" :key="index">
          <img
            :style="{
              height: index === 0 ? `${formModel.height / 2}px` : `${formModel.height / 2 - 20}px`,
              width: '120px',
              opacity: index === 0 ? 1 : 0.65,
            }"
            :src="item.img"
            style="margin-left: 10px"
            contain
          />
        </div>
      </div>
      <div v-if="formModel.indicator === 1" class="GoodSwiper-swiper__indicator GoodSwiper-swiper__icat1">
        <span v-for="(ix, idx) in formModel.swiperList.length" :key="ix" :class="[curent === idx ? 'GoodSwiper-swiper__icat1--active' : '']" />
      </div>
      <div v-if="formModel.indicator === 3" class="GoodSwiper-swiper__indicator GoodSwiper-swiper__icat3">
        <span v-for="(ix, idx) in formModel.swiperList.length" :key="ix" :class="[curent === idx ? 'GoodSwiper-swiper__icat1--active' : '']" />
      </div>
    </div>
    <div v-if="!formModel.swiperList?.length" class="GoodSwiper-swiper__nullImg">
      <img src="https://qiniu-app.qtshe.com/u391.png" />
    </div>
    <div
      v-if="formModel.indicator === 2"
      class="GoodSwiper-swiper__indicator GoodSwiper-swiper__icat2"
      :style="{ right: `${formModel.padding + 5}px` }"
    >
      <span>{{ `${curent + 1}/${formModel.swiperList.length}` }}</span>
    </div>
  </div>
</template>

<style lang="scss">
.GoodSwiper {
  background-color: #fff;
  position: relative;
}

.GoodSwiper-swiper {
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

.btnIgClass {
  width: 100%;
  height: 100px;
  position: absolute;
}

.setBigImg {
  opacity: 1;
  margin-top: -5px;
}

.fixedPosition {
  position: fixed;
  bottom: 40px;
  width: 370px;
  justify-content: center;
}
</style>
