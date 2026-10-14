<script setup lang="ts">
import MContainer from '@/components/MContainer/MContainer.vue'

import { useRoute, useRouter } from 'vue-router'
import CategoryTem from './components/CategoryTem.vue'

defineOptions({
  name: 'AttributeTemple',
})

// 响应式状态
const pageType = ref('')
const getSelectData = ref()

// 路由
const router = useRouter()
const route = useRoute()

/**
 * 返回上一页
 */
function back() {
  router.go(-1)
}

// 组件挂载
onMounted(() => {
  pageType.value = route.query.options as string
})

/**
 * 添加操作
 */
function add() {
  if (!getSelectData.value) return

  getSelectData.value.dialogVisible = true
}
</script>

<template>
  <MContainer show-same-slot>
    <template #section>
      <div class="top--bottom">
        <el-button v-if="pageType" @click="back"> 返回发布商品 </el-button>
        <el-button type="primary" @click="add"> 新增 </el-button>
      </div>
    </template>

    <template #content>
      <CategoryTem ref="getSelectData" />
    </template>
  </MContainer>
</template>

<style lang="scss" scoped>
@import '../../../assets/styles/goods/index.scss';
</style>
