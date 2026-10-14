<template>
  <view class="record--detail">
    <view class="record--detail__top">
      <image src="https://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/images/m-icon.png" class="detail--m__icon" />
      <view class="detail--type">余额提现-{{ getChannel(detail.channel) }}</view>
      <view class="detail--amount">{{ detail.amount }}</view>
    </view>
    <view class="detail--cell">
      <uv-cell-group>
        <uv-cell title="申请状态" :value="getAuditStatusName(detail.auditStatus)" :border="false" />
        <uv-cell title="申请时间" :value="detail.createTime" :border="false" />
        <uv-cell v-if="detail.reason" title="原因说明" :value="detail.reason" :border="false" />
        <uv-cell
          v-if="detail.auditStatus !== 'PASSED' && detail.auditStatus !== 'PENDING_PARTNER' && detail.updateTime"
          title="审核时间"
          :value="detail.updateTime"
          :border="false"
        />
        <uv-cell v-if="detail.arriveTime" title="到账时间" :value="detail.arriveTime" :border="false" />
        <uv-cell title="提现方式" :value="getChannel(detail.channel)" :border="false" />
        <uv-cell v-if="detail.sn" title="交易订单" :value="detail.sn" :border="false" value-class="order--num" />
      </uv-cell-group>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { useHelper } from '../withdrawalsRecord/item/composables/useHelper'

// 辅助方法
const { getChannel, getAuditStatusName } = useHelper()

// 响应式变量
const detail = ref<Obj>({})

// UniApp 生命周期钩子
onLoad((options) => {
  if (options && options.detail) {
    detail.value = JSON.parse(decodeURIComponent(options.detail))
  }
})
</script>

<style lang="scss" scoped>
page {
  background: #fff;
}

.record--detail {
  width: 100%;
  height: 100vh;

  background: white;
}

.detail--m__icon {
  width: 189rpx;
  height: 123rpx;
  margin: 72rpx 0 32rpx;
}

.record--detail__top {
  width: 100%;
  display: flex;
  align-content: center;
  flex-direction: column;
  align-items: center;
}

.detail--type {
  font-size: 35rpx;
  margin-bottom: 51rpx;
}

.detail--amount {
  font-size: 58rpx;
  font-weight: bold;
}

.detail--cell {
  margin-top: 117rpx;
}

.order--num {
  flex: 2 1 240rpx !important;
}
</style>
