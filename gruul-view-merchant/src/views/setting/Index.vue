<script setup lang="ts">
import MContainer from '@/components/MContainer/MContainer.vue'

import { useRoute } from 'vue-router'

defineOptions({
  name: 'SettingIndex',
})

// 路由
const route = useRoute()

// 使用defineAsyncComponent和映射表管理组件
const componentMap = {
  Store: defineAsyncComponent(() => import('@/views/setting/modules/Store.vue')),
  Order: defineAsyncComponent(() => import('./modules/Order.vue')),
  Rebate: defineAsyncComponent(() => import('./modules/Rebate.vue')),
  PrintSetting: defineAsyncComponent(() => import('./modules/PrintSetting.vue')),
  NoteSetting: defineAsyncComponent(() => import('./modules/NoteSetting.vue')),
  Payment: defineAsyncComponent(() => import('@/views/setting/modules/Payment.vue')),
  OSSSettings: defineAsyncComponent(() => import('./modules/OSSSettings.vue')),
}

// 状态
const comName = ref<keyof typeof componentMap>('Store')
const tabs = ref()

// 生命周期钩子
onMounted(() => {
  if (route.query.tabName) {
    comName.value = route.query.tabName as keyof typeof componentMap
  }

  if (route.params.options === '1') {
    comName.value = 'Rebate'
  }
})

// Tab点击处理
function handleClick() {
  history.pushState({}, '', `${location.origin}${location.pathname}?tabName=${comName.value}`)
}
</script>

<template>
  <MContainer class="setting">
    <el-tabs ref="tabs" v-model="comName" @tab-change="handleClick">
      <el-tab-pane label="店铺设置" name="Store" />
      <el-tab-pane label="交易设置" name="Order" />
      <el-tab-pane label="返利设置" name="Rebate" />
      <el-tab-pane label="支付配置" name="Payment" />
      <el-tab-pane label="打印设置" name="PrintSetting" />
      <el-tab-pane label="消息设置" name="NoteSetting" />
      <el-tab-pane label="OSS设置" name="OSSSettings" />
    </el-tabs>
    <div class="flex-1 overflow-auto">
      <component :is="componentMap[comName]" />
    </div>
  </MContainer>
</template>

<style lang="scss" scoped>
:deep(.tip) {
  vertical-align: center;
  background-color: rgba(246, 248, 250, 1);
  padding: 15px 15px 15px 30px;
  margin-bottom: 30px;

  .tip__title {
    margin-left: 12px;
    color: #586884;
    font-weight: 700;
  }

  .tip__lump {
    display: inline-block;
    width: 3px;
    height: 12px;
    background-color: rgba(255, 153, 0, 1);
  }
}
</style>
