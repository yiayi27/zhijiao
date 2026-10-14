<script setup lang="ts">
import Exception from '@/components/Exception.vue'
import SpikeActivity from './SpikeActivity.vue'
import SpikeSetting from './SpikeSetting.vue'
defineOptions({
  name: 'SpikeIndex',
})
</script>

<template>
  <div class="spike">
    <!-- <el-tabs v-model="activeName"
             @tab-click="handleTabClick">
      <el-tab-pane label="商品列表"
                   name="goods">商品列表</el-tab-pane>
      <el-tab-pane label="活动列表"
                   name="activity">
        <SpikeActivity />
      </el-tab-pane>
      <el-tab-pane label="规则设置"
                   name="setting">
        <SpikeSetting />
      </el-tab-pane>

    </el-tabs> -->
    <Exception />
  </div>
</template>

<style lang="scss" scoped>
@import '@/assets/styles/marketing/spike.scss';
</style>
