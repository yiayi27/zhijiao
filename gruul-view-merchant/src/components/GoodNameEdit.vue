<script setup lang="ts">
import { Edit } from '@element-plus/icons-vue'

// 定义props
const props = defineProps({
  goodName: {
    type: String,
    required: true,
  },
})

// 定义事件
const emit = defineEmits(['change'])

// 响应式状态
const editName = ref('')
const inputVisible = ref(false)
const textareaInput = ref<HTMLTextAreaElement | null>(null)

/**
 * 显示输入框
 */
function showInput() {
  inputVisible.value = true
  editName.value = props.goodName
  nextTick(() => {
    if (textareaInput.value) {
      textareaInput.value.focus()
    }
  })
}

/**
 * 隐藏输入框
 */
function hindInpt() {
  inputVisible.value = false
  const currentEditName = editName.value
  if (props.goodName === currentEditName) {
    return
  }
  if (currentEditName !== '') {
    emit('change', currentEditName)
  } else {
    ElMessage.warning('请填写商品名')
  }
}
</script>

<template>
  <!-- 编辑商品名称 -->
  <div class="goodNameEdit">
    <span v-if="!inputVisible" class="goodNameEdit__text" @click="showInput">
      <div class="goodNameEdit__text--name">{{ goodName }}</div>
      <el-icon color="#2d8cf0"><Edit /></el-icon>
    </span>
    <el-input
      v-if="inputVisible"
      ref="textareaInput"
      v-model="editName"
      type="textarea"
      :rows="1"
      style="width: 120px"
      maxlength="30"
      placeholder="请输入商品名称"
      @blur="hindInpt"
    />
  </div>
</template>

<style lang="scss">
.goodNameEdit {
  // width: 108px;
  //width: 120px;
  &__text {
    cursor: pointer;
    display: flex;
    &--name {
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
    }
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
