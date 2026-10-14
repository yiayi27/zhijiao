<script setup lang="ts">
import type { ActivityMarketProps, LinkSelectItem } from '@/components/LinkSelect/type'
import BaseTable from '@/components/BaseTable/index.vue'
import { ACTIVITY_MARKET_DATA, typeNameMap } from '@/components/LinkSelect/constants'
import { TAB_CONFIG } from '@/components/LinkSelect/enums'

const props = defineProps<ActivityMarketProps>()
const emit = defineEmits(['update:link'])

const name = TAB_CONFIG.ACTIVITY_MARKET
const selectId = ref<Long>('')
const tableData = ref<LinkSelectItem[]>(ACTIVITY_MARKET_DATA)

// 判断是否为相同类型
function sameJudge(): boolean {
  return props.link?.type !== undefined && typeNameMap[props.link.type!]?.name === name
}

// 选择处理
function selectHandle() {
  const currentItem = tableData.value.find((item) => item.id === selectId.value)
  if (currentItem && props.link) {
    emit('update:link', { ...props.link, ...currentItem })
  }
}

// 初始化选中状态
function initSelection() {
  if (sameJudge() && props.link?.id) {
    selectId.value = props.link.id
  }
}

onMounted(() => {
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
  <!-- 活动营销 -->
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
