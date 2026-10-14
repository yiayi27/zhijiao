<script setup lang="ts">
import HomeLive from './HomeLive'
import { ArrowRight } from '@element-plus/icons-vue'

/** 属性定义 */
const props = defineProps({
  formData: {
    type: Object as () => typeof HomeLive,
    required: true,
  },
})

/**
 * 动态设置页面样式
 */
const getPageStyle = computed(() => {
  return { padding: `0px ${props.formData.pagePadding}px` }
})

/**
 * 动态设置style
 */
const getGoodsStyle = computed(() => {
  const margin = `${+props.formData.goodsPadding / 2}px 0px`
  if (props.formData.doodsAngle === 2) {
    return { margin }
  } else {
    return { margin, borderRadius: '8px', overflow: 'hidden' }
  }
})
</script>

<template>
  <div class="spellpre" :style="getPageStyle">
    <div class="spellpre__header" :style="getPageStyle">
      <span class="spellpre__header--title">{{ formData.title }}</span>
      <span class="spellpre__header--more"
        >查看更多
        <el-icon><ArrowRight /></el-icon>
      </span>
    </div>
    <div class="spellpre__headtemp" />
    <div
      class="liveList"
      :class="{
        liveListBig: formData.listStyle === 1,
        liveListScroll: formData.listStyle === 3,
      }"
    >
      <div
        v-for="(_item, index) in +formData.count"
        :key="index"
        class="liveListItem"
        :class="{
          liveItemBig: formData.listStyle === 1,
          liveItemScroll: formData.listStyle === 3,
        }"
        :style="getGoodsStyle"
      >
        <img src="https://dss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=2203762303,14815638&fm=26&gp=0.jpg" />
        <div class="live-item-head">
          <span class="head-no-live">未开始</span>
          <span class="head-text">101观看2</span>
        </div>
        <div>
          <div class="live-item-center">直播标题</div>
          <div class="live-item-bottom">
            <div class="live-anchor">昵称</div>
            <div class="live-like">
              <img src="http://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/Live/like.png" />
              97
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss">
@import './index.scss';
</style>
