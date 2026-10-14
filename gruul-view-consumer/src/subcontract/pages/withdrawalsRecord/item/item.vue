<template>
  <view class="withdrawals--record--item">
    <view class="withdrawals--record__date">{{ date }}</view>
    <view class="withdrawals--record__list">
      <block v-for="(item, index) in list" :key="index">
        <view class="withdrawals--record__item" :data-item="item" @tap="pushDetail">
          <view class="item--time">{{ item.time }}</view>
          <view class="item--type">{{ item.channel ? getChannel(item.channel) : '' }}</view>
          <view class="item--info">
            <view class="item__amount">{{ item.amount }}元</view>
            <view :class="'item__status ' + (item.auditStatus ? getAuditStatusColor(item.auditStatus.toString()) : '')">
              {{ item.auditStatus ? getAuditStatusName(item.auditStatus.toString()) : '' }}
            </view>
          </view>
        </view>
        <view class="withdrawals--line" />
      </block>
    </view>
  </view>
</template>
<script setup lang="ts">
import { useHelper } from './composables/useHelper'
import type { WithdrawalItem } from '@/composables/mixins/useWithdrawals'

// Props definition with types
defineProps({
  date: {
    type: String,
    required: true,
  },
  list: {
    type: Array as () => WithdrawalItem[],
    required: true,
  },
})

const { getChannel, getAuditStatusName, getAuditStatusColor } = useHelper()

// Navigate to detail page
const pushDetail = (event: { currentTarget: { dataset: { item: WithdrawalItem } } }) => {
  const { item } = event.currentTarget.dataset
  uni.navigateTo({
    url: `/subcontract/pages/commissionRecordDetail/commissionRecordDetail?detail=${encodeURIComponent(JSON.stringify(item))}`,
  })
}
</script>
<style lang="scss" scoped>
.withdrawals--record--item {
  width: 100%;
  height: auto;
}

.withdrawals--record__date {
  width: 100%;
  height: auto;
  padding: 30rpx 20rpx;
  box-sizing: border-box;
  position: relative;
  background: rgba(156, 157, 156, 0.11);
}

.withdrawals--record__list {
  background: #fff;
}

.withdrawals--record__item {
  display: flex;
  align-content: center;
  align-items: center;
  padding: 25rpx 20rpx;
  box-sizing: border-box;
}

.item--time {
  width: 213rpx;
  margin-right: 70rpx;
}

// .item--type {
//   margin-right: 310rpx;
// }

.item--info {
  margin-left: auto;
  text-align: center;
}

.item__status {
  font-size: 24rpx;
}

.item__status.success {
  color: #575757;
}

.item__status.fail {
  color: #fe4e63;
}

.item__status.processing {
  color: #09bb07;
}

.withdrawals--line {
  width: calc(100% - 40rpx);
  border-bottom: 1px solid #e8e8e8;
  margin: 0 auto;
}
</style>
