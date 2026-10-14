<script setup lang="ts">
import { cloneVNode } from 'vue'

defineProps({
  row: {
    type: Object,
    default: () => ({}),
  },
  selection: {
    type: Boolean,
    default: false,
  },
  rowKey: {
    type: Function,
    default: () => {},
  },
})

const emit = defineEmits(['change'])

function onChange(val: any) {
  emit('change', val)
}

const slots = useSlots()
const defaultSlot = slots.default?.() || []

/**
 * 处理并返回修改后的节点
 * @returns 处理后的节点数组
 */
function modifiedNodes(): VNode[] {
  const result: VNode[] = []

  /**
   * 处理节点及其子节点
   * @param vnodes - 要处理的节点数组
   */
  const processNodes = (vnodes: VNode[]) => {
    for (const vnode of vnodes) {
      // 处理 Fragment 节点（多根节点）
      if (vnode.type === Symbol.for('v-fgt') && vnode.children) {
        // 只有当children是数组时才进行处理
        if (Array.isArray(vnode.children)) {
          // 将Fragment的子节点作为VNode[]处理
          const childNodes = vnode.children.filter((child) => typeof child === 'object' && child !== null) as VNode[]

          if (childNodes.length > 0) {
            processNodes(childNodes)
          }
        }
        continue
      }

      // 将普通节点添加到结果中
      if (vnode.type !== Symbol.for('v-cmt')) {
        result.push(cloneVNode(vnode))
      }
    }
  }

  // 处理默认插槽中的节点
  if (defaultSlot.length > 0) {
    processNodes(defaultSlot)
  }

  return result
}
</script>

<template>
  <tr class="body--content">
    <template v-for="(node, index) in modifiedNodes()" :key="`bodyContent${rowKey ? rowKey(row) : index}${index}`">
      <td class="m__table--item">
        <el-checkbox
          v-if="index === 0 && selection"
          :key="`ChildrenCheckbox${rowKey ? rowKey(row) : index}${index}`"
          :model-value="row.checked"
          @change="onChange"
        />
        <component :is="node" :index="index" :row="row" :selection="selection" />
      </td>
    </template>
  </tr>
</template>
