<script setup lang="ts">
import type { IComponent, IComponentItem } from './interfaces/component'
import { useDecorationStore } from '@/store/modules/decoration'
import { storeToRefs } from 'pinia'

import EdtiorFormData from './compontents/index/formModel'

// 发出事件替代 @Emit
const emit = defineEmits(['change'])
const decorationStore = useDecorationStore()
const { activePageType } = storeToRefs(decorationStore)
const first = ref(true)
const second = ref(true)

/** 普通组件 */
const components = ref<IComponentItem[]>([
  {
    icon: 'lunbotu',
    value: 'HomeSwiper',
    label: '轮播图',
  },
  {
    icon: 'lunbotu',
    value: 'GoodSwiper',
    label: '商品轮播图',
  },
  {
    icon: 'shangpin',
    value: 'Goods',
    label: '商品',
  },
  {
    icon: 'sousuo',
    value: 'Search',
    label: '搜索',
  },
  {
    icon: 'biaotilan',
    value: 'TitleBar',
    label: '标题栏',
  },
  {
    icon: 'zhanweifu',
    value: 'BlankPlaceholder',
    label: '空白占位',
  },
  {
    icon: 'fengefu',
    value: 'Separator',
    label: '分隔符',
  },
  {
    icon: 'dianpudaohang',
    value: 'StoreNavigation',
    label: '店铺导航',
  },
  {
    icon: 'mofang',
    value: 'CubeBox',
    label: '魔方',
  },
  {
    icon: '28fuwenbenkuang',
    value: 'RichText',
    label: '富文本',
  },
  {
    icon: 'tupian',
    value: 'ImageCom',
    label: '图片',
  },
  {
    icon: 'zhibo1',
    value: 'VideoCom',
    label: '视频',
  },
])

/** 营销组件 */
const components2 = ref<IComponentItem[]>([
  {
    icon: 'youhuiquan',
    value: 'Coupon',
    label: '优惠券',
    showType: true,
  },
  {
    icon: 'jielong',
    value: 'Chain',
    label: '接龙',
    showType: true,
  },
  {
    icon: 'zhibo1',
    value: 'HomeLive',
    label: '直播',
    showType: true,
  },
])

const componentsStart = ref<IComponentItem[]>([])
const componentsStart2 = ref<IComponentItem[]>([])

onMounted(() => {
  componentsStart.value = [...components.value]
  componentsStart2.value = [...components2.value]
})

function handleAddComponent(currentComponent: IComponentItem): IComponent | undefined {
  if (activePageType.value === 'bussiness') {
    return undefined
  } else {
    let FormData: any = EdtiorFormData[currentComponent.value as keyof typeof EdtiorFormData]
    const component = { ...currentComponent, id: Date.now(), formData: FormData }
    emit('change', component)
    return component
  }
}

function componentClone(currentComponent: IComponentItem) {
  const FormData = EdtiorFormData[currentComponent.value as keyof typeof EdtiorFormData]
  return {
    ...currentComponent,
    id: Date.now(),
    formData: FormData,
  }
}

function componentAdd() {
  components.value = [...componentsStart.value]
}

function componentAdd2() {
  components2.value = [...componentsStart2.value]
}
</script>

<template>
  <!-- 组件管理页面 -->
  <div class="editor__component editor__component_new">
    <div class="editor_component_wrap mt-[20px]">
      <el-scrollbar style="height: 85%">
        <div class="editor_component_wrap_main">
          <div class="component_title" @click="first = !first">
            <div :class="first ? 'jiantou_bot' : 'jiantou_right'" />
            <div class="component_title_text">基本组件</div>
          </div>
          <m-draggable
            v-show="first"
            v-model="components"
            class="component_box"
            :group="{ name: 'component', pull: 'clone' }"
            :sort="false"
            animation="300"
            itemKey="value"
            :clone="componentClone"
            @add="componentAdd"
          >
            <template #item="{ element: component }">
              <div style="width: 93px">
                <div class="component--item" @click="handleAddComponent(component)">
                  <div class="iconfont component--item--icon" :class="`icon${component.icon}`" />
                  <span>{{ component.label }}</span>
                </div>
              </div>
            </template>
          </m-draggable>
          <div class="component_title" @click="second = !second">
            <div :class="second ? 'jiantou_bot' : 'jiantou_right'" />
            <div class="component_title_text">营销组件</div>
          </div>
          <m-draggable
            v-show="second"
            v-model="components2"
            class="component_box"
            :group="{ name: 'component', pull: 'clone' }"
            :sort="false"
            animation="300"
            itemKey="value"
            :clone="componentClone"
            @add="componentAdd2"
          >
            <template #item="{ element: component }">
              <div style="width: 93px">
                <div class="component--item" @click="handleAddComponent(component)">
                  <div class="iconfont component--item--icon" :class="`icon${component.icon}`" />
                  <span>{{ component.label }}</span>
                </div>
              </div>
            </template>
          </m-draggable>
        </div>
      </el-scrollbar>
    </div>
  </div>
</template>

<style lang="scss">
@import '@/assets/styles/decoration/editorPage';

.component--item--icon {
  font-size: 28px;
  text-align: center;
  margin-bottom: 19px;
}

.editor__component_new .is-horizontal {
  display: none;
  overflow-x: hidden;
}
</style>
