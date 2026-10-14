<template>
  <view>
    <view v-for="(item, index) in list" :key="index" class="recode">
      <view class="recode__conter">
        <view v-if="item.gainIntegralType === 1">兑换积分商品</view>
        <view v-if="item.gainIntegralType === 2">购买商品</view>
        <view v-if="item.gainIntegralType === 3">积分抵扣</view>
        <view v-if="item.gainIntegralType === 4">每日首次登录</view>
        <view v-if="item.gainIntegralType === 5">每日首次分享</view>
        <view v-if="item.gainIntegralType === 6">签到</view>
        <view v-if="item.gainIntegralType === 7">每日步数达标</view>
        <view v-if="item.gainIntegralType === 8">商家充值</view>
        <view v-if="item.gainIntegralType === 9">商家扣除</view>
        <view v-if="item.gainIntegralType === 10">积分清零</view>
        <view v-if="item.gainIntegralType === 11">归还积分</view>
        <view v-if="item.gainIntegralType === 12">兑换会员卡</view>
        <view :class="(item.type === 'min' ? ' ' : 'recode__money--add') + ' recode__money '">
          {{ item.integral > 0 ? '+' : '' }}{{ item.integral }}
          <span v-if="item.detailName" style="font-size: 26rpx">({{ item.detailName }})</span>
        </view>
      </view>
      <view class="recode__conter" style="color: #9e9e9e">
        <view class="recode__conter--time">{{ item.createTime }}</view>
        <view>当前用户剩余积分：{{ item.nowTheIntegral }}</view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad, onReachBottom } from '@dcloudio/uni-app'
import { getIntegralDetail } from '@/api/modules/integral'

// 响应式变量
const list = ref<any[]>([])
const query = ref({
  current: 1,
  size: 10,
})

// 方法定义
const getDetail = () => {
  const param = query.value

  getIntegralDetail(param).then((res) => {
    if (!res.list) return
    list.value = list.value.concat(res.list)
  })
}

// UniApp 生命周期钩子
onLoad(() => {
  getDetail()
})

onReachBottom(() => {
  query.value.current++
  getDetail()
})
</script>

<style lang="scss" scoped>
.recode {
  padding: 40rpx;
  border-bottom: 1rpx solid #f2f2f2;
  font-size: 26rpx;
  background: #fff;

  .recode__conter {
    margin-top: 20rpx;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
}

.recode__money {
  font-weight: bold;
  font-size: 36rpx;
}

.recode__money--add {
  color: #ff9900;
}
</style>
