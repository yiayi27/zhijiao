<script setup lang="ts">
import type { IComponent } from './interfaces/component'
import { useDecorationStore } from '@/store/modules/decoration'
import { Rank } from '@element-plus/icons-vue'
import { storeToRefs } from 'pinia'

import EdtiorFormData from './compontents/index/formModel'
import preview from './compontents/index/preview'

const emit = defineEmits(['change', 'addchange'])
const components = defineModel<IComponent[]>('components', { required: true })

const delFlag = ref(-1)
const curreentFlag = ref(-1)
const myScrollbar = ref<any>(null)

const decorationStore = useDecorationStore()
const { activePageType, liveItem } = storeToRefs(decorationStore)

watch(
  () => components.value,
  (v: IComponent[]) => {
    if (Array.isArray(v) && v.length === 1) {
      const name = v[0].value
      // 如果是拼团分类页要默认选中
      if (name === 'SpellPage' || name === 'BusinessSuper') {
        handleCurrentComponent(v[0], 0)
      }
    }
  },
  {
    deep: true,
  },
)

const iscomponentList = computed(() => {
  return components.value.length > 0 && (components.value[0].value === 'SpellPage' || components.value[0].value === 'BusinessSuper')
})

watch(
  () => liveItem.value,
  (val) => {
    if (val && val.name) {
      components.value = val.components as IComponent[]
    }
  },
)

onMounted(() => {
  nextTick(() => {
    onWrapSroll()
  })
})

function componentAdd(e: any) {
  const index = e.newDraggableIndex
  const com = components.value[index]
  let FormData: any = EdtiorFormData[com.value as keyof typeof EdtiorFormData]
  emit(
    'addchange',
    {
      ...com,
      id: Date.now(),
      formData: FormData,
    },
    index,
  )
}

/**
 * 监听组件设置切换
 */
function handleCurrentComponent(currentComponent: IComponent, i: number): IComponent {
  curreentFlag.value = i
  decorationStore.setActiveComIndex(i)
  emit('change', currentComponent)
  return currentComponent
}

function onWrapSroll() {
  if (!myScrollbar.value || !myScrollbar.value.wrap) return
  const scrollbarEl = myScrollbar.value.wrap
  scrollbarEl.onscroll = function () {
    const popover = document.querySelector('.el-popover')
    if (popover) {
      popover.remove()
    }
  }
}

/**
 * 设置当前选中的组件
 */
function setCurrentFlag(i: number) {
  curreentFlag.value = i
}

/**
 * 删除当前组件
 */
function delCurrentCom(i: number) {
  if (components.value.length === 1) {
    components.value = []
    emit('change', null)
  } else {
    components.value.splice(i, 1)
    const index = components.value.length - 1
    handleCurrentComponent(components.value[index], index)
  }
}

defineExpose({
  setCurrentFlag,
})
</script>

<template>
  <!-- 预览编辑页面 -->
  <div class="editor__preview">
    <div style="overflow: hidden; height: 100%">
      <el-scrollbar
        ref="myScrollbar"
        :class="[components && components.length === 1 && components[0].value === 'NavBar' ? 'preNavBar' : '']"
        style="height: 100%"
        :noresize="true"
      >
        <m-draggable
          v-model:list="components"
          :sort="true"
          :group="iscomponentList ? 'nocomponent' : 'component'"
          animation="500"
          :scroll="true"
          item-key="id"
          style="height: 100%"
          @add="componentAdd"
        >
          <template #item="{ element: component, index: i }">
            <div
              :id="`editor-preview-com-${i}`"
              style="width: 375px"
              class="component--item"
              :class="[
                component.value !== undefined && delFlag === i && component.value !== 'SpellPage' && component.value !== 'BusinessSuper'
                  ? 'iscur__component--item'
                  : '',
                curreentFlag === i && component.value !== 'SpellPage' && component.value !== 'BusinessSuper' ? 'select__component--item' : '',
              ]"
              @click="handleCurrentComponent(component, i)"
              @mouseover="delFlag = i"
              @mouseleave="delFlag = -1"
            >
              <div
                v-show="activePageType !== 'bussiness' && delFlag === i && component.value !== 'SpellPage' && component.value !== 'BusinessSuper'"
                class="component--item__tan"
              >
                <div class="component--item__text" @click.stop="handleCurrentComponent(component, i)">编辑</div>
                <div class="component--item__text ml-[5px]" @click.stop="delCurrentCom(i)">删除</div>
              </div>
              <div v-show="delFlag === i" class="component--item__draghelp">
                <el-icon>
                  <Rank />
                </el-icon>
              </div>
              <component :is="preview[component.value as keyof typeof preview]" :form-data="component.formData" />
            </div>
          </template>
        </m-draggable>
      </el-scrollbar>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@import '@/assets/styles/decoration/editorPage';
:deep(.el-scrollbar__view) {
  height: 100%;
}
</style>
