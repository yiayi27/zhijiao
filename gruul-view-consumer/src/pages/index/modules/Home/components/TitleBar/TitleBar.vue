<template>
  <view
    v-if="decoretionProperties?.titleName"
    :style="
      'color:' +
      (decoretionProperties?.color || '') +
      ';background-color:' +
      (decoretionProperties?.backgroundColor || '') +
      ';text-align:center; font-size: 28rpx;font-weight: 800;position:relative'
    "
  >
    <uv-cell
      v-if="decoretionProperties?.showStyle === 'is-style-one'"
      class="spellpre__header"
      :style="'color: ' + (decoretionProperties?.color || '')"
      center
      :title="decoretionProperties?.titleName || ''"
    >
    </uv-cell>
    <view v-else class="spellpre__header" :style="'color: ' + (decoretionProperties?.color || '')" @tap.stop="handleClick">
      <text class="spellpre__header--title">{{ decoretionProperties?.titleName }}</text>
      <text class="spellpre__header--more">查看更多 ></text>
      <view class="add__line" />
    </view>
    <uv-button v-if="decoretionProperties?.type === 0 && decoretionProperties?.id === 8" open-type="contact" class="contact-style" />
  </view>
</template>

<script setup lang="ts">
import { navtofun } from '@/utils/navtofun'

// 定义标题栏属性接口
interface TitleBarProperties {
  color?: string
  backgroundColor?: string
  showStyle?: string
  titleName?: string
  type?: number
  id?: number
  pathLink?: string
  append?: string
}

// 导入或定义ItemLink接口
interface ItemLink {
  type: number
  url: string
  append: string
  id: number
  name?: string
}

// 定义props
const props = defineProps<{
  decoretionProperties?: TitleBarProperties
}>()

// 方法定义
const handleClick = () => {
  if (!props.decoretionProperties) return

  const { pathLink, append, id, type } = props.decoretionProperties

  // 确保必要的属性存在
  if (type === undefined || !pathLink || append === undefined || id === undefined) {
    return
  }

  const itemLink: ItemLink = {
    type,
    url: pathLink,
    append,
    id,
  }

  navtofun(itemLink)
}
</script>

<style lang="scss" scoped>
@include b(spellpre) {
  box-sizing: border-box;
  background-color: #fff;

  @include e(header) {
    color: #333;
    height: 80rpx;
    line-height: 80rpx;
    position: relative;

    @include m(title) {
      float: left;
      font-size: 36rpx;
      font-weight: 700;
      margin-left: 40rpx;
    }

    @include m(more) {
      font-size: 28rpx;
      font-weight: 400;
      float: right;
      margin-right: 40rpx;
    }

    .add__line {
      height: 100%;
      width: 8rpx;
      background-color: red;
      box-sizing: border-box;
      position: absolute;
      left: 0;
      top: 0;
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
</style>
