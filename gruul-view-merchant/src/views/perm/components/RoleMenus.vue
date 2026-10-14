<script lang="ts" setup>
import routes from '@/router/routes'
import { getAllMenu } from '@/store/modules/menu'
import { cloneDeep } from 'lodash-es'

const props = defineProps({
  modelValue: {
    type: Array,
    default() {
      return []
    },
  },
})
const emits = defineEmits(['update:modelValue'])
const menuTree = ref()
const menus = ref<any>([])
const showMenus = computed(() => {
  const value = menus.value
  return !!value && value.length > 0
})
function checkChanged() {
  const checkedMenuIds = menuTree.value.getCheckedKeys(true)
  emits('update:modelValue', checkedMenuIds)
}
watch(
  () => props.modelValue,
  (value) => {
    menuTree.value.setCheckedKeys(value)
  },
)

onMounted(() => {
  menus.value = getAllMenu(cloneDeep(routes as any))
  nextTick(() => {
    menuTree.value.setCheckedKeys(props.modelValue)
  })
})
</script>

<template>
  <div style="width: 100%; height: 200px; overflow-y: auto">
    <el-tree v-show="showMenus" ref="menuTree" :data="menus" show-checkbox node-key="value" @check="checkChanged" />
    <el-skeleton v-if="!showMenus" animated />
  </div>
</template>

<style lang="scss" scoped>
:deep(.el-tree-node__content) {
  height: 40px;
}
</style>
