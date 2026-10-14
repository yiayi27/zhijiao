<script setup lang="ts">
import { Edit } from '@element-plus/icons-vue'

// 定义props
const props = defineProps({
  goodStock: {
    type: Number,
    required: true,
  },
})

// 定义emit
const emit = defineEmits(['change'])

// 响应式状态
const editStock = ref(0)
const inputVisible = ref(false)
const savePriceInput = ref<any>(null)

/**
 * 显示输入框
 */
function showInput() {
  inputVisible.value = true
  editStock.value = props.goodStock
  nextTick(() => {
    if (savePriceInput.value) {
      const inputEl = savePriceInput.value.$el.querySelector('input')
      if (inputEl) {
        inputEl.focus()
      }
    }
  })
}

/**
 * 隐藏输入框
 */
function hindInpt() {
  inputVisible.value = false
  const currentEditStock = editStock.value
  if (props.goodStock === currentEditStock) {
    return
  }
  if (currentEditStock < 0) {
    ElMessage.warning('请正确填写商品库存')
  } else {
    emit('change', currentEditStock)
  }
}
</script>

<template>
  <!-- 编辑积分价格 -->
  <div class="goodStockEdit">
    <span v-if="!inputVisible" class="goodStockEdit__text" @click="showInput">
      {{ goodStock }}
      <el-icon><Edit /></el-icon>
    </span>
    <el-input-number
      v-if="inputVisible"
      ref="savePriceInput"
      v-model="editStock"
      :min="0"
      :step="1"
      step-strictly
      :controls="false"
      style="width: 120px"
      placeholder="请输入商品库存"
      @blur="hindInpt"
    />
  </div>
</template>

<style lang="scss" scoped>
.goodStockEdit {
  &__text {
    cursor: pointer;
  }
  i {
    margin-left: 4px;
    display: none;
  }
  &:hover {
    i {
      display: inline-block;
    }
  }
}
</style>
