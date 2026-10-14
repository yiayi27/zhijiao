<script setup lang="ts">
import { useDecorationStore } from '@/store/modules/decoration'
import { Edit } from '@element-plus/icons-vue'

import { storeToRefs } from 'pinia'

// 定义props
const props = defineProps({
  goodName: {
    type: String,
    required: true,
  },
  good: {
    type: Object,
    required: true,
  },
})

// 定义emit
const emit = defineEmits(['change'])

// 获取store
const decorationStore = useDecorationStore()
const { activePage } = storeToRefs(decorationStore)

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
    <span v-if="!inputVisible" class="goodNameEdit__text flex items-center" @click="showInput">
      {{ goodName }}
      <el-icon color="#2d8cf0" :class="{ active1: activePage && activePage.id === good.id }">
        <Edit />
      </el-icon>
    </span>
    <el-input
      v-if="inputVisible"
      ref="textareaInput"
      v-model="editName"
      style="width: 180px"
      maxlength="30"
      placeholder="请输入商品名称"
      @blur="hindInpt"
    />
  </div>
</template>

<style lang="scss">
.goodNameEdit {
  width: 170px;
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
.active1 {
  display: inline-block !important;
}
</style>
