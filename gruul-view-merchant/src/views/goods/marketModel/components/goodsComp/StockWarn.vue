<script setup lang="ts">
import { Warning } from '@element-plus/icons-vue'

// 定义props
const props = defineProps({
  stockItem: {
    type: Object,
    required: true,
  },
})

// 商品信息计算属性
const stock = computed(() => props.stockItem)

// 部分商品是否售罄
const someStock = ref(false)

// 更新库存状态
function updatedStock() {
  someStock.value = stock.value.skuStocks.some((item: Obj) => {
    return item.stock === 0
  })
}

// 监听stockItem变化
watch(
  () => props.stockItem,
  () => {
    updatedStock()
  },
)

// 组件挂载时初始化
onMounted(() => {
  someStock.value = false
  updatedStock()
})
</script>

<template>
  <div>
    <el-popover v-if="stock.stock === 0 || someStock" placement="top-start" trigger="hover">
      <div>
        <template v-if="stock.stock === 0"> 商品已售罄 </template>
        <template v-else-if="someStock && stock.skuStocks.length > 1 && stock.stock !== 0"> 部分商品已售罄 </template>
      </div>

      <template #reference>
        <el-icon class="ml-[5px]" color="#FF0000">
          <Warning />
        </el-icon>
      </template>
    </el-popover>
  </div>
</template>

<style lang="scss" scoped></style>
