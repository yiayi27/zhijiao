<script setup lang="ts">
import type { FunctionPageProps, LinkSelectItem } from '@/components/LinkSelect/type'
import { getTemplate } from '@/api/decoration/decoration'
import { getPageList } from '@/api/group/group'
import BaseTable from '@/components/BaseTable/index.vue'
import { FUNCTION_PAGE_DATA, typeNameMap } from '@/components/LinkSelect/constants'
import { LinkType, TAB_CONFIG } from '@/components/LinkSelect/enums'

const props = defineProps<FunctionPageProps>()
const emit = defineEmits(['update:link'])

// 组件名称
const name = TAB_CONFIG.FUNCTION_PAGE
// 选中ID
const selectId = ref<string | number>()
// 表格数据
const tableData = ref<LinkSelectItem[]>([...FUNCTION_PAGE_DATA])

// 初始化数据
function initData() {
  if (props.noProTab) {
    tableData.value = FUNCTION_PAGE_DATA.filter((item) => item.append !== 'mall' && item.append !== 'service')
  } else if (props.limitProTab) {
    tableData.value = FUNCTION_PAGE_DATA.filter((item) => item.append !== 'mall')
  } else {
    tableData.value = [...FUNCTION_PAGE_DATA]
  }
}

// 选择处理
function selectHandle() {
  // 找到选中的项目
  const currentItem = tableData.value.find((item) => item.id === selectId.value)
  if (currentItem && props.link) {
    emit('update:link', { ...props.link, ...currentItem })
  }
}

// 判断是否为相同类型
function sameJudge(): boolean {
  return (
    props.link !== undefined && props.link.type !== undefined && typeof props.link.type === 'number' && typeNameMap[props.link.type]?.name === name
  )
}

// 初始化选中状态
function initSelection() {
  if (sameJudge() && props.link?.id) {
    selectId.value = props.link.id
  }
}

// 组件挂载时
onMounted(() => {
  initData()
  initSelection()
})

// 监听父弹窗显隐
watch(
  () => props.visible,
  (newVal) => {
    if (newVal) {
      initSelection()
    }
  },
)
</script>

<template>
  <!-- 功能页面 -->
  <div>
    <BaseTable :data="tableData" height="369">
      <el-table-column label="页面名称" prop="name" />
      <el-table-column label="操作" width="100px">
        <template #default="scope">
          <el-radio v-model="selectId" :value="scope.row.id" @change="selectHandle">{{ scope.row.id }} </el-radio>
        </template>
      </el-table-column>
    </BaseTable>
  </div>
</template>
