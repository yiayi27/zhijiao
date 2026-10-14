<template>
  <view class="ruleBox">
    <view class="ruleBox__item">
      <view class="title">积分获取规则</view>
      <view v-for="(item, index) in getList" :key="index" class="line">
        <div v-if="item.ruleType === 0 && item.open">每日首次分享获得{{ item.integralRuleParameterDtos[0].value }}点积分。</div>
        <div v-if="item.ruleType === 1 && item.open">
          每日首次登录获得{{ item.integralRuleParameterDtos[0].value }}点积分, 第{{ item.integralRuleParameterDtos[1].value }}天连续登录将额外获赠{{
            item.integralRuleParameterDtos[2].value
          }}点积分； 第{{ item.integralRuleParameterDtos[3].value }}天连续登录将额外获得{{ item.integralRuleParameterDtos[4].value }}点积分。
        </div>
        <div v-if="item.ruleType === 2 && item.open">
          每日首次签到获得{{ item.integralRuleParameterDtos[0].value }}点积分, 第{{ item.integralRuleParameterDtos[1].value }}天连续签到将额外获赠{{
            item.integralRuleParameterDtos[2].value
          }}点积分； 第{{ item.integralRuleParameterDtos[3].value }}天连续签到将额外获得{{ item.integralRuleParameterDtos[4].value }}点积分。
        </div>
        <div v-if="item.ruleType === 3 && item.open">
          每到达{{ item.integralRuleParameterDtos[0].value }}步数可获得{{ item.integralRuleParameterDtos[1].value }}点积分。
        </div>
      </view>
    </view>
    <view class="ruleBox__item">
      <view class="title">积分抵扣规则</view>
      <view class="line">单笔消费积分抵用 = 积分*{{ ratio }}%（使用额度需不高于单笔订单总额的{{ ceiling }}%）</view>
    </view>
    <view class="ruleBox__item">
      <view class="title">积分使用规则</view>
      <view class="line">{{ ruleOption.useRuleIntroduce }}</view>
    </view>
    <view class="ruleBox__item">
      <view class="title">积分有效期</view>
      <view class="line">积分有效期为{{ ruleOption.integralValidity }}个月</view>
    </view>
    <view class="ruleBox__item">
      <view class="title">积分值规则</view>
      <view class="line">{{ ruleOption.integralRuleInfo }}</view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getIntegralRule, getLessRule } from '@/api/modules/integral'

const ruleOption = ref({
  useRuleIntroduce: '',
  integralValidity: '',
  integralRuleInfo: '',
})
const getList = ref<any[]>([])
const ceiling = ref('')
const ratio = ref('')

onLoad(() => {
  getRule()
})

const getRule = () => {
  const ruleOptionValue = {
    useRuleIntroduce: '',
    integralValidity: '',
    integralRuleInfo: '',
  }
  const getListValue: any[] = []

  getIntegralRule({}).then((res) => {
    res.forEach((item: any) => {
      ruleOptionValue.useRuleIntroduce = item.useRuleIntroduce
      ruleOptionValue.integralValidity = item.integralValidity
      ruleOptionValue.integralRuleInfo = item.integralRuleInfo
      getListValue.push({
        integralRuleParameterDtos: item.integralRuleParameterDtos,
        integralRuleId: item.integralRuleId,
        open: item.open,
        integralShopId: item.integralShopId,
        ruleType: item.ruleType,
      })
    })
    ruleOption.value = ruleOptionValue
    getList.value = getListValue
  })

  getLessRule({}).then((res) => {
    ceiling.value = res.ceiling
    ratio.value = res.ratio
  })
}
</script>

<style lang="scss" scoped>
page {
  min-height: 100%;
  background-color: white;
}

.ruleBox {
  padding: 40rpx 60rpx;
  font-size: 26rpx;
  background-color: #fff;
}

.ruleBox__item {
  margin-bottom: 60rpx;
}

.title {
  border-left: 2rpx solid #7b7b7b;
  padding-left: 20rpx;
  color: #7b7b7b;
  margin-bottom: 20rpx;
}

.line {
  line-height: 50rpx;
}
</style>
