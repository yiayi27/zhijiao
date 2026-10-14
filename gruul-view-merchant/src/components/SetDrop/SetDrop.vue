<script setup lang="ts">
import { MoreFilled } from '@element-plus/icons-vue'

defineOptions({
  name: 'SetDrop',
})

defineProps({
  setName: {
    type: String,
    default: '编辑',
  },
  dropdownList: {
    type: Array as () => DropdownItem[],
    required: true,
  },
  isStyle: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['command', 'setClick'])

interface DropdownItem {
  command: string | number
  text: string
  disabled?: boolean
  show?: boolean
}

/**
 * 点击的下拉项
 */
function handleCommand(val: string | number) {
  emit('command', val)
}

/**
 * 点击的设置按钮
 */
function handleClick() {
  emit('setClick')
}
</script>

<template>
  <div class="wrap">
    <div class="wrap__set" @click="handleClick">
      <slot>
        {{ setName || '编辑' }}
      </slot>
    </div>
    <el-dropdown trigger="click" @command="handleCommand">
      <div :class="isStyle ? 'wrap__set--newSelect' : 'wrap__set--select'">
        <div class="wrap__set--apart">|</div>
        <el-icon><MoreFilled /></el-icon>
      </div>
      <template #dropdown>
        <el-dropdown-menu>
          <template v-for="(item, index) in dropdownList" :key="index">
            <el-dropdown-item v-if="item?.show !== false" :disabled="!!item?.disabled" :command="item?.command">
              {{ item?.text }}
            </el-dropdown-item>
          </template>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<style lang="scss" scoped>
.container {
  display: inline-block;
}

.wrap {
  display: flex;
  align-items: center;
  line-height: 30px;
  overflow: hidden;
  border-radius: 50px;
  background-color: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
  border: 1px solid var(--el-color-primary-light-9);
  padding: 0 6px;

  &__set {
    padding: 0 5px;

    &:hover {
      cursor: pointer;
    }

    &--apart {
      margin-top: -2px;
      margin-right: 8px;
    }

    &--newSelect,
    &--select {
      width: 32px;
      display: flex;
      justify-content: center;
      cursor: pointer;
      color: var(--el-color-primary);
      font-weight: bold;
    }
  }
}
</style>
