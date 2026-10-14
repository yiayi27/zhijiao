<template>
  <view class="withdrawals--record">
    <item v-for="[key, items] in Object.entries(group)" :key="key" :date="key" :list="items" />
    <block v-if="list.length === 0">
      <DataEmpty :infoVisible="false" tips="暂无任何记录~" />
    </block>
  </view>
</template>
<script setup lang="ts">
import { onLoad } from '@dcloudio/uni-app'
import { useWithdrawals } from '@/composables/mixins/useWithdrawals'
import item from './item/item.vue'
import DataEmpty from '@/components/DataEmpty/DataEmpty.vue'
import { userIdGet } from '@/api/modules/user'

// 从useWithdrawals钩子中解构出所需的状态和方法
const { group, list, getWithdrawalList } = useWithdrawals()

// 生命周期钩子
onLoad((query?: Record<string, string>) => {
  const accountType = query?.accountType || 'USER'
  const type = Number(query?.type || 0)

  userIdGet({}).then((id) => {
    getWithdrawalList({ accountType, type, userId: id }, false)
  })
})
</script>
<style lang="scss" scoped>
.withdrawals--record {
  width: 100%;
  height: auto;
  font-size: 30rpx;
}
</style>
