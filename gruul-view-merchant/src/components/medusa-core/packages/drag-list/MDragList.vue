<script setup lang="ts">
import '../styles/m-drag.scss'

// 定义组件名称
defineOptions({
  name: 'MDragList',
})

// 定义组件属性
const props = withDefaults(defineProps<DragListProps>(), {
  data: () => [],
  defaultOpen: () => [],
  childKey: '',
  childItemKey: 'id',
  itemKey: 'id',
  options: () => ({
    handle: '.drag-handle', // 父项只能通过handle元素拖拽
  }),
  childOptions: () => ({
    group: { name: 'child', pull: true, put: true }, // 为子项设置独立的组
  }),
})

// 定义事件
const emit = defineEmits<{
  'update:data': [value: any[]]
  'update:defaultOpen': [value: number[]]
  onMove: []
}>()

// 定义类型
interface DragListProps<T = any> {
  data?: T[]
  defaultOpen?: number[]
  childKey?: string
  childItemKey?: string
  itemKey?: string
  options?: Record<string, any>
  childOptions?: Record<string, any>
}

// 标记子项拖拽状态
const childDragging = ref(false)

// 处理双向绑定
const list = computed({
  get: () => props.data,
  set: (value) => emit('update:data', value),
})

const open = computed({
  get: () => props.defaultOpen,
  set: (value) => emit('update:defaultOpen', value),
})

// 为每个子列表创建不同的组名，防止不同父项下的子项互相拖入
function getChildOptions(parentIndex: number) {
  return {
    ...props.childOptions,
    group: {
      name: `child-${parentIndex}`,
      pull: true,
      put: true,
    },
    // 阻止冒泡和捕获
    preventDragParent: true,
  }
}

function onEnd() {
  emit('onMove')
}

// 子项开始拖拽
function onChildDragStart(evt: any) {
  childDragging.value = true

  // 如果有原始事件，阻止冒泡
  if (evt.originalEvent) {
    evt.originalEvent.stopPropagation()
    evt.originalEvent.stopImmediatePropagation()
  }
}

// 子项结束拖拽
function onChildDragEnd(evt: any) {
  childDragging.value = false

  // 如果有原始事件，阻止冒泡
  if (evt.originalEvent) {
    evt.originalEvent.stopPropagation()
    evt.originalEvent.stopImmediatePropagation()
  }
  onEnd()
}
</script>

<template>
  <div class="drag__list">
    <m-draggable v-model="list" :item-key="itemKey" :options="options" @end="onEnd">
      <template #item="{ element, index }">
        <el-collapse :key="index" v-model="open">
          <el-collapse-item :name="index" :disabled="!!(element[childKey] && !element[childKey].length)">
            <template #title>
              <div class="drag__list--item drag-handle">
                <slot name="item" :item-data="element" :index="index" />
              </div>
            </template>
            <m-draggable
              v-model="list[index][childKey]"
              :item-key="childItemKey"
              :options="getChildOptions(index)"
              @start="onChildDragStart"
              @end="onChildDragEnd"
            >
              <template #item="{ element: childData, index: i }">
                <div class="child-item" @mousedown.stop @touchstart.stop>
                  <slot name="child" :item-data="element" :child-data="childData" :parent-index="index" :index="i" />
                </div>
              </template>
            </m-draggable>
          </el-collapse-item>
        </el-collapse>
      </template>
    </m-draggable>
  </div>
</template>

<style lang="scss">
@import '../styles/m-drag.scss';

.drag__list {
  .child-item {
    width: 100%;
    cursor: move;
  }

  .drag-handle {
    cursor: move;
  }
}
</style>
