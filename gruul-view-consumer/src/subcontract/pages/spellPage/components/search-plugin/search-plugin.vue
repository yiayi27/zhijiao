<template>
  <view class="page">
    <view class="page__box van-search">
      <view class="page__header" @tap="toPop">
        <uv-icon name="search" class="page__header__icon" />
        {{ queryName || '请输入关键字搜索' }}
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
// 定义props
const props = defineProps<{
  queryName?: string
  chanel?: number
  pageType?: number
}>()

// 方法定义
const toPop = () => {
  if (props.chanel !== undefined && [12].includes(props.chanel)) {
    let chanelValue = props.chanel
    switch (chanelValue) {
      case 12:
        chanelValue = 2
        break
      default:
        break
    }

    uni.navigateTo({
      url: `/subcontract/pages/Search/Search?chanel=${chanelValue}`,
    })
  } else {
    uni.navigateBack({})
  }
}
</script>

<style lang="scss" scoped>
.page {
  &__header {
    border-radius: 80rpx;
    height: 80rpx;
    display: inline-block;
    display: flex;
    align-items: center;
    background-color: #f8f8f8;
    font-size: 28rpx;
    width: 100%;

    &__icon {
      font-size: 28rpx;
      margin: 0 40rpx;
    }
  }

  &__box {
    background: #fff;
    height: 120rpx;
    width: 100%;
    padding: 10rpx 20rpx;
  }

  .page__pop--search {
    color: #fff;
    font-weight: 800;
    margin-right: 20rpx;
    background: #fe5468;
    padding: 0 40rpx;
    border-radius: 40rpx;
  }

  .page__item {
    width: 33%;
    padding: 14rpx 20rpx;
    float: left;
    text-align: center;

    &--word {
      display: block;
      width: 80%;
      white-space: nowrap;
      text-overflow: ellipsis;
      overflow: hidden;
      border-radius: 40rpx;
      background: #f5f5f5;
      font-size: 28rpx;
      padding: 20rpx 20rpx;
      text-align: center;
    }
  }
}
</style>
