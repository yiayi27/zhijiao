<script setup lang="ts">
const props = defineProps({
  detail: {
    type: Object,
    default: () => ({}),
  },
  visible: {
    type: Boolean,
    default: false,
  },
})

// 使用map和defineAsyncComponent异步加载组件
const componentMap = {
  ProDetail: defineAsyncComponent(() => import('./dialog/ProDetail.vue')),
  Delivery: defineAsyncComponent(() => import('./dialog/Delivery.vue')),
}

// 弹窗标签
const activeName = ref<keyof typeof componentMap>('ProDetail')

// 监听visible变化
watch(
  () => props.visible,
  (newVal) => {
    if (newVal) {
      activeName.value = 'ProDetail'
    }
  },
)

function handleClick(tab: { name: keyof typeof componentMap }) {
  activeName.value = tab.name
}
</script>

<template>
  <div>
    <el-tabs v-model="activeName" @tab-click="(e: any) => handleClick(e)">
      <el-tab-pane label="订单信息" name="ProDetail" />
      <el-tab-pane label="物流信息" name="Delivery" />
    </el-tabs>
    <component :is="componentMap[activeName]" :detail="detail" :visible="visible" />
  </div>
</template>

<style lang="scss" scoped></style>
