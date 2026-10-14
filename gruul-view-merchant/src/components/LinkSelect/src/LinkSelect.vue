<script setup lang="ts">
import type { LinkSelectItem, LinkSelectProps } from '@/components/LinkSelect/type'
import { asyncComponents, getDefaultLinkData, getRadioOption, getTabOptions, typeNameMap } from '@/components/LinkSelect/constants'
import { LINK_TYPE_CONFIG, LinkType, SelectType, TAB_CONFIG } from '@/components/LinkSelect/enums'
import { useDecorationStore } from '@/store/modules/decoration'
import { useVModel } from '@vueuse/core'
import { cloneDeep } from 'lodash-es'

import AppView from './components/AppView.vue'
import WebView from './components/WebView.vue'

// 定义componentRef的类型接口
interface ComponentRef {
  selectId: string | number | null
  [key: string]: any
}

const props = withDefaults(defineProps<LinkSelectProps>(), {
  inner: false,
  noProTab: false,
  spellTab: true,
  limitProTab: true,
  getFrom: 'none',
  showClear: false,
  selectType: SelectType.Radio,
  link: () => getDefaultLinkData(),
})

const emit = defineEmits<{
  'update:link': [value: LinkSelectItem]
  select: [value: LinkSelectItem]
}>()

const radio = ref(LINK_TYPE_CONFIG.SYSTEM)
const dialogVisible = ref(false)
const currentComponent = ref(TAB_CONFIG.FUNCTION_PAGE)
const secShow = ref(true)
const componentRef = ref<ComponentRef | null>(null)
const decorationStore = useDecorationStore()

const linkValue = useVModel(props, 'link', emit)

// 单选按钮选项
const radioOptions = computed(() => getRadioOption())

// 标签页选项
const tabOptions = computed(() =>
  getTabOptions().filter((item) => {
    return item.show(props, secShow.value)
  }),
)

// 监听链接值变化
watch(
  () => linkValue.value,
  (v) => {
    if ((v && typeof v.type === 'string') || typeof v.type === 'number') {
      const typeName = typeNameMap[v.type]?.name
      if (typeName) {
        currentComponent.value = typeName
      }
    }
  },
)

const dialogLink = ref<LinkSelectItem>(getDefaultLinkData())

// 更新链接
function onUpdateLink(v: LinkSelectItem) {
  dialogLink.value = v
}
// 显示对话框
function handleShow() {
  dialogVisible.value = true
  dialogLink.value = cloneDeep(linkValue.value || getDefaultLinkData()) as LinkSelectItem

  // 清除上次选中状态
  if (props.showClear) {
    nextTick(() => {
      if (componentRef.value?.selectId) {
        componentRef.value.selectId = null
      }
    })
  }
}

// 关闭对话框
function handleClose() {
  dialogVisible.value = false
  if (linkValue.value) {
    emit('select', linkValue.value)
    decorationStore.setLinkForm(props.getFrom)
    Object.assign(linkValue.value, dialogLink.value)
  }
  dialogLink.value = getDefaultLinkData()
}

// 组件挂载时
onMounted(() => {
  if (props.link?.type === LinkType.MiniProgram) {
    radio.value = LINK_TYPE_CONFIG.MINI_PROGRAM
  } else {
    radio.value = props.link?.type === LinkType.CustomLink ? LINK_TYPE_CONFIG.CUSTOM : LINK_TYPE_CONFIG.SYSTEM
  }
})
</script>

<template>
  <!-- 链接选择器 -->
  <div>
    <div class="mb18">
      <!-- 单选按钮/下拉选择区域 -->
      <slot name="radio">
        <el-radio-group v-if="selectType === SelectType.Radio" v-model="radio" class="line">
          <el-radio v-for="option in radioOptions" :key="option.value" :value="option.value">
            {{ option.label }}
          </el-radio>
        </el-radio-group>
        <el-select v-if="selectType === SelectType.Select" v-model="radio">
          <el-option v-for="option in radioOptions" :key="option.value" :label="option.label" :value="option.value" />
        </el-select>
      </slot>
    </div>

    <!-- 按钮区域 -->
    <slot name="input">
      <div @click="handleShow">
        <slot name="button">
          <el-button v-if="radio === LINK_TYPE_CONFIG.SYSTEM" type="primary"> 添加链接 </el-button>
        </slot>
      </div>
    </slot>

    <!-- 自定义链接和小程序组件 -->
    <WebView v-if="radio === LINK_TYPE_CONFIG.CUSTOM" v-model:link="linkValue" />
    <AppView v-if="radio === LINK_TYPE_CONFIG.MINI_PROGRAM" v-model:link="linkValue" />

    <!-- 链接选择器对话框 -->
    <el-dialog v-model="dialogVisible" title="链接选择器" :append-to-body="inner" :modal-append-to-body="true">
      <div style="margin: 0 20px">
        <el-tabs v-model="currentComponent">
          <el-tab-pane v-for="tab in tabOptions" :key="tab.name" :label="tab.label" :name="tab.name" />
        </el-tabs>
        <component
          :is="asyncComponents[currentComponent]"
          ref="componentRef"
          :link="dialogLink"
          :visible="dialogVisible"
          :no-pro-tab="noProTab"
          :limit-pro-tab="limitProTab"
          @update:link="onUpdateLink"
        />
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="handleClose">确 定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.mb18 {
  margin-bottom: 18px;
}

.line {
  display: flex;
  margin-top: 9px;
}

:deep(.el-radio) {
  margin-right: 18px;
}

:deep(.el-dialog) {
  width: 880px;
}
</style>
