<template>
  <view>
    <UPopup v-model:show="shoptype" mode="center" :customStyle="{ height: '44%', width: '75%' }" @close="close">
      <view class="shopContent">
        <image class="shopContent__img" src="https://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/api/proofing.png" />
        <view v-if="shopStatus === 5" class="shopContent__explain">店铺已打烊，于明日{{ shopTime }}正常营业</view>
        <view v-if="shopStatus === 4" class="shopContent__explain">程序系统维护中</view>
        <view v-if="shopStatus === 3" class="shopContent__explain">店铺已打烊，于明日{{ shopTime }}正常营业</view>
        <!-- 1部署中 2正常 ，3已打烊，4禁用, 5时间到了关门 -->
      </view>
    </UPopup>
  </view>
</template>

<script setup lang="ts">
// 使用defineModel来处理shoptype的双向绑定
const shoptype = defineModel<boolean>('shoptype', { default: false })

// 定义其他props
defineProps({
  shopTime: {
    type: String,
    default: '',
  },
  shopStatus: {
    type: [Number, String],
    default: 0,
  },
})

// 处理关闭事件
const close = () => {
  shoptype.value = false
}
</script>

<style>
.shopContent {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.shopContent__img {
  width: 200rpx;
  height: 200rpx;
  margin-bottom: 30rpx;
}

.shopContent__explain {
  font-size: 30rpx;
}
</style>
