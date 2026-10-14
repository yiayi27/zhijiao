<template>
  <view class="my--setting">
    <view class="info">允许"小程序名称"使用我的地理位置</view>
    <view class="cover--group">
      <view v-if="showAddress" class="cover--group__cell" @tap.stop="toAddress">
        <view class="group__cell--left">
          <MIcon name="icongerenzhongxin-dizhiguanli" class="icon cell--left--icon" />
          <text class="cell--left--text">地址管理</text>
        </view>
        <uv-icon class="group__cell--right" name="arrow-right" />
      </view>
      <view class="cover--group__cell">
        <view class="group__cell--left">
          <MIcon name="icondaohang" class="icon cell--left--icon" />
          <text class="cell--left--text">使用我的地理位置</text>
        </view>
        <uv-switch v-model="status" size="21" @change="onChange" />
      </view>
      <view class="cover--group__cell" @tap="clearStorage">
        <view class="group__cell--left">
          <MIcon name="iconshanchu" class="icon cell--left--icon" />
          <text class="cell--left--text">清除缓存</text>
        </view>
      </view>
    </view>
  </view>
</template>
<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import MIcon from '@/components/MIcon/MIcon.vue'
import { getActivitySetting } from '@/api/modules/chain'
import { getSetting } from '@/utils/helper'
import storage from '@/utils/storage'
import { CHOOSE_ADDRESS } from '@/utils/emitNameConfig'

// 响应式数据
const status = ref(false)
const showAddress = ref(true)
const activity = ref({
  activityState: 0,
  arriveInfo: null,
  arriveInfoSet: 0,
  id: '',
  payOrderInfo: null,
  payOrderInfoSet: 0,
  welcomeInfo: null,
  welcomeInfoSet: 0,
})

// 方法定义
const getActivity = () => {
  getActivitySetting({}).then((res) => {
    activity.value = res
  })
}

const onChange = async () => {
  // 地理位置授权
  const res = (await getSetting()) as any
  if (!res.authSetting['scope.userLocation']) {
    // #ifdef MP-WEIXIN
    uni.openSetting({
      success: (setRes) => {
        if (!setRes.authSetting['scope.userLocation']) {
          status.value = false
        } else {
          status.value = true
        }
      },
    })
    // #endif
    // #ifndef MP-WEIXIN
    navigator.geolocation.getCurrentPosition(
      (res) => {
        console.log(res)
        status.value = false
      },
      (err) => {
        console.log(err)
        status.value = false
      },
      {
        enableHighAccuracy: true,
        timeout: 10000,
        maximumAge: 0,
      },
    )
    // #endif
  } else {
    status.value = true
  }
}

const toAddress = () => {
  uni.navigateTo({
    url: '/subcontract/pages/address/address',
  })
}

const clearStorage = () => {
  storage.removeThis()
  uni.showToast({
    title: '清除成功',
    icon: 'success',
    duration: 2000,
  })
}

// 生命周期
onLoad((options) => {
  getActivity()
  if (options?.type === '1') {
    showAddress.value = false
  } else {
    showAddress.value = true
  }

  getSetting().then((res: any) => {
    if (!res.authSetting['scope.userLocation']) {
      status.value = false
    } else if (res.authSetting['scope.userLocation']) {
      status.value = true
    }
  })
})
</script>
<style lang="scss" scoped>
@mixin bottom-line($weight: 1px, $color: #f8f5f9) {
  border-bottom: $weight solid $color;
}

// 默认字体大小
$default-font-size: 32rpx;
// 字体颜色
$page-text-color: #45403c;

page {
  height: 100%;
}

.my--setting {
  background: #f5f5f5;
  min-height: 100%;
}

.info {
  font-size: 24rpx;
  color: rgba(69, 64, 60, 0.6);
  padding: 16rpx 30rpx;
}

.cover--group {
  border-radius: 10rpx;
  margin-bottom: 15rpx;

  .cover--group__cell {
    @include flex(space-between, center);
    background-color: #ffffff;
    height: 100rpx;
    padding: 30rpx 25rpx 30rpx 30rpx;

    .group__cell--left {
      @include flex(center, center);

      .cell--left--icon {
        width: 45rpx;
        height: 45rpx;
        padding-right: 23rpx;
      }

      .cell--left--text {
        font-size: $default-font-size;
        color: $page-text-color;
      }
    }

    .group__cell--right {
      color: rgba(69, 64, 60, 0.6);
      font-size: 24rpx;
      line-height: 0rpx;
    }
  }

  .cover--group__cell:not(:last-child) {
    // CSS样式
    @include bottom-line;
  }
}

.setting-dialog__tip {
  text-align: center;
  line-height: 88rpx;
}
</style>
