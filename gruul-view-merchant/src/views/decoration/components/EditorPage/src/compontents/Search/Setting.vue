<script setup lang="ts">
import { CirclePlus, Remove } from '@element-plus/icons-vue'

import { useVModel } from '@vueuse/core'

// 定义props和emit
const props = defineProps({
  formData: {
    type: Object,
    default: () => null,
  },
})

const emit = defineEmits(['update:formData'])

const formData = useVModel(props, 'formData', emit)

// 样式选项
const showStyles = ref([
  {
    label: '样式一',
    value: 'is-style-one',
  },
  {
    label: '样式二',
    value: 'is-style-two',
  },
  {
    label: '样式三',
    value: 'is-style-three',
  },
])

// 添加热词方法
function add() {
  if (formData.value.hotWord.length === 10) {
    ElMessage.warning('搜索热词最多为10个！')
    return
  }
  formData.value.hotWord.push('')
}

// 删除热词方法
function remove(i: number) {
  if (formData.value.hotWord.length === 1) {
    ElMessage.warning('搜索热词最少为1个！')
    return
  }
  formData.value.hotWord.splice(i, 1)
}
</script>

<template>
  <!-- 搜索 -->
  <el-form :model="formData" label-width="100px">
    <el-form-item label="选中样式">
      <el-radio-group v-model="formData.showStyle">
        <el-radio v-for="item in showStyles" :key="item.value" :value="item.value">
          {{ item.label }}
        </el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item label="预设关键字">
      <el-input v-model="formData.keyWord" :maxlength="8" placeholder="最多显示8个字" />
    </el-form-item>
    <el-form-item label="热门搜索">
      <div v-for="(item, i) in formData.hotWord" :key="i" class="item">
        <el-icon class="icon" color="ff0000" @click="remove(i)">
          <Remove />
        </el-icon>
        <el-input v-model="formData.hotWord[i]" :maxlength="8" placeholder="预设搜索热词, 最多显示8个字" class="mx-[10px]" />
        <el-icon class="icon" @click="add">
          <CirclePlus />
        </el-icon>
      </div>
      <div>{{ `搜索热词关键字(${formData.hotWord.length}/10)` }}</div>
    </el-form-item>
  </el-form>
</template>

<style lang="scss" scoped>
:deep(.el-input) {
  display: inline;
}

.item {
  display: flex;
  margin-bottom: 5px;
}
</style>
