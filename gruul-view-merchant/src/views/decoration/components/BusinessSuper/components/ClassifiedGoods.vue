<script setup lang="ts">
import BusinessSuper from './../BusinessSuper'

import type { Chart } from './../BusinessSuper'

const props = defineProps({
  formData: {
    type: Object as () => typeof BusinessSuper,
    required: true,
  },
})

const isShowPage = ref(true)

const getValue = computed(() => {
  const { navigation = [] } = props.formData.currentFirstCategory
  return JSON.parse(JSON.stringify(navigation))
})

/**
 * 获取当前的分类导航
 * 如果长度大于2则将其他调到最后一个
 */
const navigationList = computed(() => {
  let { navigation = [] } = props.formData.currentFirstCategory
  navigation = JSON.parse(JSON.stringify(navigation))
  if (navigation.length > 1) {
    const temp = navigation.splice(0, 1)
    navigation.push(...temp)
  }
  return navigation
})

/**
 * 判断是否显示标题
 * 如果长度小于2，说明没有或者只有一个其他导航，则不显示
 */
const isShowTitle = computed(() => {
  const navigation = navigationList.value || []
  return navigation.length > 1
})

watch(
  getValue,
  (v) => {
    if (v) {
      isShowPage.value = false
      setTimeout(() => {
        isShowPage.value = true
      }, 0)
    }
  },
  { deep: true },
)

/**
 * 显示二级分类页面
 */
function showSecondCategory(item: Chart) {
  console.log(item)
  // props.formData.isSearch = 3;
  // props.formData.secondCategoryId = item.title;
}
</script>

<template>
  <div v-if="isShowPage" class="classified__goods-page">
    <div v-for="(nav, inx) in navigationList.reverse()" v-show="nav.name" :key="inx">
      <div v-if="formData.currentFirstCategory.navigationStyle === 1 && isShowTitle && nav.class.length" class="navi__box1">
        <b />
        <span>{{ nav.name }}</span>
        <b />
      </div>
      <div v-if="formData.currentFirstCategory.navigationStyle === 2 && isShowTitle && nav.class.length" class="navi__box2">
        <b />
        <span>{{ nav.name }}</span>
      </div>
      <div
        v-for="(item, idx) in nav.chartList"
        :key="idx"
        class="class-item"
        :class="[idx % 3 === 0 ? 'remove__margin' : '']"
        @click="showSecondCategory(item)"
      >
        <img :src="item.img" />
        <span>{{ item.category.name }}</span>
      </div>
    </div>
  </div>
</template>

<style lang="scss">
.classified__goods-page {
  .class-item {
    width: 71px;
    height: 111px;
    display: inline-block;
    margin-left: calc((100% - 215px) / 2);
    margin-bottom: 8px;
    vertical-align: top;

    img {
      display: block;
      width: 100%;
      height: 71px;
      border-radius: 3px;
    }

    span {
      display: block;
      height: 40px;
      line-height: 40px;
      width: 100%;
      color: #333;
      text-align: center;
    }
  }

  .remove__margin {
    margin-left: 0px;
  }

  .navi__box1 {
    height: 50px;
    text-align: center;

    span,
    b {
      display: inline-block;
    }

    span {
      height: 50px;
      line-height: 40px;
      padding: 0 15px;
      vertical-align: middle;
    }

    b {
      width: 52px;
      height: 1px;
      background-color: rgba(236, 236, 236, 1);
      vertical-align: super;
    }
  }

  .navi__box2 {
    height: 50px;
    position: relative;

    b {
      height: 13px;
      left: 0;
      top: 14px;
      width: 2px;
      background-color: #333;
      position: absolute;
    }

    span {
      display: inline-block;
      height: 50px;
      line-height: 40px;
      padding-left: 15px;
    }
  }
}
</style>
