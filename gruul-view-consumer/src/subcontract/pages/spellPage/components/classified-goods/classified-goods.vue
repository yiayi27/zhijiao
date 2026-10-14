<template>
  <view class="classified__goods-page">
    <view v-for="(nav, index) in navigation" :key="index">
      <block v-if="nav.name">
        <view
          v-if="formData.currentFirstCategory.navigationStyle === 1 && (navigation.length > 1 || (navigation.length === 1 && nav.navType !== 1))"
          class="navi__box1"
        >
          <text class="nav_b" />
          <text class="nav_p">{{ nav.name }}</text>
          <text class="nav_b" />
        </view>
        <view
          v-if="formData.currentFirstCategory.navigationStyle === 2 && (navigation.length > 1 || (navigation.length === 1 && nav.navType !== 1))"
          class="navi__box2"
        >
          <text class="nav_b2" />
          <text class="nav_p2">{{ nav.name }}</text>
        </view>
        <view
          v-for="(item, idx) in nav.chartList"
          :key="idx"
          :class="'class-item ' + (idx % 3 === 0 ? 'remove__margin' : '')"
          :data-item="item"
          @tap="showSecondCategory"
        >
          <image :src="item.img" mode="scaleToFill" lazy-load="true" />
          <text class="item_p">{{ item.category.name }}</text>
        </view>
      </block>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

// 定义props
const props = defineProps<{
  propData: Obj
}>()

// 响应式变量
const navigation = ref<any[]>([])
const formData = ref<Obj>({})

// 监听props变化
watch(
  () => props.propData,
  () => {
    getFormData()
  },
  { deep: true, immediate: true },
)

// 方法定义
const getFormData = () => {
  const { currentFirstCategory = {} } = props.propData as any
  let { navigation: nav = [] } = currentFirstCategory

  if (nav.length > 1) {
    const temp = nav.findIndex((i: any) => i.navType === 1)
    if (temp !== -1) {
      const list = [...nav]
      list.push(...list.splice(temp, 1))
      nav = [...list]
    }
  }

  formData.value = props.propData
  navigation.value = nav
}

const showSecondCategory = (e: any) => {
  const data = e.currentTarget.dataset.item
  uni.navigateTo({
    url: `/subcontract/pages/spellPage/second-catgoods/second-catgoods?chanel=11&categoryId=${data.category.id}&cartButton=${formData.value.cartButton}`,
  })
}
</script>

<style lang="scss" scoped>
.classified__goods-page {
  width: 100%;
  box-sizing: border-box;

  .class-item {
    width: 142rpx;
    height: 222rpx;
    display: inline-block;
    margin-left: calc((100% - 430rpx) / 2);
    margin-bottom: 16rpx;
    vertical-align: top;

    image {
      display: block;
      width: 100%;
      height: 142rpx;
      border-radius: 6rpx;
    }

    .item_p {
      display: block;
      height: 80rpx;
      line-height: 80rpx;
      width: 100%;
      color: #333;
      text-align: center;
    }
  }

  .remove__margin {
    margin-left: 0;
  }

  .navi__box1 {
    height: 100rpx;
    text-align: center;

    text {
      display: inline-block;
    }

    .nav_p {
      height: 100rpx;
      line-height: 80rpx;
      padding: 0 30rpx;
      vertical-align: middle;
    }

    .nav_b {
      width: 104rpx;
      height: 1px;
      background-color: rgba(236, 236, 236, 1);
      vertical-align: super;
    }
  }

  .navi__box2 {
    height: 100rpx;
    position: relative;

    .nav_b2 {
      height: 26rpx;
      left: 0;
      top: 28rpx;
      width: 4rpx;
      background-color: #333;
      position: absolute;
    }

    .nav_p2 {
      display: inline-block;
      height: 100rpx;
      line-height: 80rpx;
      padding-left: 30rpx;
    }
  }
}
</style>
