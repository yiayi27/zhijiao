<script setup lang="ts">
import type { IComponent } from './interfaces/component'
import { useDecorationStore } from '@/store/modules/decoration'

defineProps({
  currentComponent: {
    type: Object as () => IComponent | null,
    default: null,
  },
})

const emit = defineEmits(['change'])

/** 当前选中tab */
const nowTab = ref(0)

const decorationStore = useDecorationStore()

/**
 * 切换左侧tab
 */
function selectTab(num: number) {
  nowTab.value = num
  decorationStore.setIsUserCenter(num)
  emit('change', nowTab.value)
}
</script>

<template>
  <!-- 组件、控件、页面菜单页面 -->
  <div class="editor_tab">
    <div class="left_menu">
      <div class="tab_item" :class="[nowTab === 0 ? 'tab_item_active' : '']" @click="selectTab(0)">
        <svg class="tab--icon" aria-hidden="true">
          <use :xlink:href="nowTab === 0 ? '#iconzujian' : '#iconzujianhui'" />
        </svg>
        <div class="tab_item_title">组件</div>
        <div class="decorator" />
      </div>
      <div class="tab_item" :class="[nowTab === 1 ? 'tab_item_active' : '']" @click="selectTab(1)">
        <svg class="tab--icon" aria-hidden="true">
          <use :xlink:href="nowTab === 1 ? '#iconkongjian' : '#iconkongjianhui'" />
        </svg>
        <div class="tab_item_title">控件</div>
        <div class="decorator" />
      </div>
      <div class="tab_item" :class="[nowTab === 2 ? 'tab_item_active' : '']" @click="selectTab(2)">
        <svg class="tab--icon" aria-hidden="true">
          <use :xlink:href="nowTab === 2 ? '#iconyemian' : '#iconyemianhui'" />
        </svg>
        <div class="tab_item_title">页面</div>
        <div class="decorator" />
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@import '@/assets/styles/decoration/editorPage';
</style>
