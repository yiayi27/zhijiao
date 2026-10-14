<script setup lang="ts">
import MContainer from '@/components/MContainer/MContainer.vue'

import { useRouter } from 'vue-router'
import DialogSetting from './common/DialogSetting.vue'
import NormalSetting from './common/NormalSetting.vue'
import OrderSetting from './common/OrderSetting.vue'

defineOptions({
  name: 'RecoveryIndex',
})

const router = useRouter()
const clickType = ref('0')

onMounted(() => {
  init()
})

/**
 * 初始化信息
 */
function init() {}
</script>

<template>
  <MContainer>
    <template #tabs>
      <el-tabs v-model="clickType">
        <el-tab-pane name="0" label="通用设置" />
        <el-tab-pane name="2" label="弹窗设置" />
        <el-tab-pane name="3" label="回收订单" />
      </el-tabs>
    </template>
    <template #content>
      <NormalSetting v-if="clickType === '0'" />
      <DialogSetting v-if="clickType === '2'" />
      <OrderSetting v-if="clickType === '3'" />
    </template>
  </MContainer>
</template>

<style lang="scss" scoped>
.pageTop {
  display: flex;
  height: 50px;
  align-items: center;

  &__botton {
    padding: 10px 20px;
    border-radius: 50px;
    font-size: 13px;
    font-weight: bold;
    margin-right: 20px;
    cursor: pointer;
  }
}
</style>
