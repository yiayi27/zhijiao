<script setup lang="ts">
import { Edit } from '@element-plus/icons-vue'

// 定义props
const props = defineProps({
  goodPrice: {
    type: Number,
    required: true,
  },
})

// 定义emit
const emit = defineEmits(['change'])

// 响应式状态
const editPrice = ref(0)
const inputVisible = ref(false)
const savePriceInput = ref<any>(null)

/**
 * 显示输入框
 */
function showInput() {
  inputVisible.value = true
  editPrice.value = props.goodPrice
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
  const currentEditPrice = editPrice.value
  if (props.goodPrice === currentEditPrice) {
    return
  }
  if (currentEditPrice > 0) {
    emit('change', currentEditPrice)
  } else {
    ElMessage.warning('请正确填写积分价格')
  }
}
</script>

<template>
  <!-- 编辑积分价格 -->
  <div class="goodPriceEdit">
    <span v-if="!inputVisible" class="goodPriceEdit__text" @click="showInput">
      {{ goodPrice }}
      <el-icon><Edit /></el-icon>
    </span>
    <el-input-number
      v-if="inputVisible"
      ref="savePriceInput"
      v-model="editPrice"
      :controls="false"
      style="width: 120px"
      placeholder="请输入积分价格"
      :min="1"
      :step="1"
      step-strictly
      @blur="hindInpt"
    />
  </div>
</template>

<style lang="scss" scoped>
.goodPriceEdit {
  &__text {
    width: 150px;
    cursor: pointer;
    text-align: center;
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
