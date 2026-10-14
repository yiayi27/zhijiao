<script setup lang="ts">
import { ArrowDown } from '@element-plus/icons-vue'

import SupProduct from './SupProduct.vue'

// 定义props
const props = defineProps({
  status: {
    type: Number,
    default: null,
  },
  sup: {
    type: Object,
    default: () => null,
  },
})

// 定义emits
const emit = defineEmits(['useSup', 'delSupplier'])

// 响应式状态
const dialogVisible = ref(false)

/** 启用/禁用供应商 */
function useSup() {
  emit('useSup', props.status, props.sup)
}

/** 删除供应商 */
function delSupplier() {
  emit('delSupplier', props.status, props.sup)
}
</script>

<template>
  <div>
    <el-dropdown style="margin-left: 10px" placement="bottom-start">
      <span style="cursor: pointer">
        <el-button text>
          更多<el-icon class="ml-[5px]" size="8"><ArrowDown /></el-icon>
        </el-button>
      </span>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item>
            <span @click="dialogVisible = true">查看</span>
          </el-dropdown-item>
          <el-dropdown-item>
            <span @click="useSup">{{ status === 1 ? '禁用' : '启用' }}</span>
          </el-dropdown-item>
          <el-dropdown-item>
            <span style="color: #fa6465" @click="delSupplier">删除</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
    <el-dialog v-model="dialogVisible" width="750px">
      <template #header>
        <div class="diaTitle">查看商品</div>
      </template>
      <SupProduct :sup-product="sup" />
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
.diaTitle {
  display: flex;
  justify-content: flex-start;
  font-size: 16px;
}
</style>
