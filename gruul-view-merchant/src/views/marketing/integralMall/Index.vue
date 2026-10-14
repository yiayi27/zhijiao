<script setup lang="ts">
import { getShopMsg } from '@/api/index'
import { takeOnOff } from '@/api/integral/integral'
import MContainer from '@/components/MContainer/MContainer.vue'

import { useRoute } from 'vue-router'

defineOptions({
  name: 'IntegralMallIndex',
})

// 组件动态导入
const componentMap = {
  ProductList: defineAsyncComponent(() => import('./integralPro/ProductList.vue')),
  CouponList: defineAsyncComponent(() => import('./integralPro/CouponList.vue')),
  ProductOrder: defineAsyncComponent(() => import('./integralOrder/ProductOrder.vue')),
  CouponOrder: defineAsyncComponent(() => import('./integralOrder/CouponOrder.vue')),
  IntegralRule: defineAsyncComponent(() => import('./integralRule/Index.vue')),
  IntegralExp: defineAsyncComponent(() => import('./integralExp/Index.vue')),
}

// 状态定义
const switchCheck = ref(false)
const comName = ref<keyof typeof componentMap>('ProductList')
const route = useRoute()

/**
 * 获取积分商城状态
 */
async function getSwitchCheck() {
  const res = await getShopMsg({})
  switchCheck.value = res.data.type
}

/**
 * 开启/关闭积分商城
 */
async function changeCheck() {
  const param = {
    status: switchCheck.value ? 1 : 0,
  }
  await takeOnOff(param)
  getSwitchCheck()
}

// 生命周期钩子
onMounted(() => {
  getSwitchCheck()
  if (route.params.type === 'deliver') {
    comName.value = 'ProductOrder'
  }
})
</script>

<template>
  <MContainer>
    <template #top>
      <div class="shopSwitch">
        <div class="shopSwitch__text">
          您已开启积分商城功能。开启积分商城后会员可利用在您店铺累积的积分兑换特定商品或优惠券,您需要发布商品或优惠券
          并进行积分价格、可兑换数量、限兑数量的设置。
          <div class="shopSwitch__text--point">注意：会员兑换时，根据商品情况，需要或不需要支付运费</div>
        </div>
        <el-switch v-model="switchCheck" active-color="#3399FF" inactive-color="#DCDFE6" @change="changeCheck" />
      </div>
    </template>

    <template #tabs>
      <el-tabs v-model="comName">
        <el-tab-pane label="积分商品" name="ProductList" />
        <el-tab-pane label="积分优惠券" name="CouponList" />
        <el-tab-pane label="积分商品订单" name="ProductOrder" />
        <el-tab-pane label="积分优惠券订单" name="CouponOrder" />
        <el-tab-pane label="积分规则" name="IntegralRule" />
        <el-tab-pane label="活动说明" name="IntegralExp" />
      </el-tabs>
    </template>

    <template #content>
      <component :is="componentMap[comName]" />
    </template>
  </MContainer>
</template>

<style lang="scss" scoped>
.shopSwitch {
  width: 100%;
  height: 100px;
  padding: 20px;
  background-color: #ebf6ff;
  font-size: 14px;
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
  &__text {
    width: 90%;
    &--point {
      margin-top: 10px;
      color: #ff0000;
    }
  }
}
</style>
