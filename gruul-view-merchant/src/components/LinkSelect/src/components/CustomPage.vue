<script setup lang="ts">
import type { ComponentEmits, CustomPageProps, LinkSelectItem } from '@/components/LinkSelect/type'
import { getTemplate } from '@/api/decoration/decoration'
import { getPageList } from '@/api/group/group'
import BaseTable from '@/components/BaseTable/index.vue'

import { typeNameMap } from '@/components/LinkSelect/constants'
import PageManage from '@/components/PageManage.vue'
import { ElMessage } from 'element-plus'

interface TemplateItem {
  id: string
  templateName: string
}

interface PageItem {
  id: string
  pageName: string
  isDef: string
}

// 定义 props
const props = defineProps<CustomPageProps>()

// 定义 emit
const emit = defineEmits<ComponentEmits>()

// 响应式状态
const name = ref('CustomPage')
const selectId = ref<number>()
const templateId = ref('')
const loading = ref(false)
const tableData = ref<LinkSelectItem[]>([])
const pageNum = ref(1)
const pageSize = ref(20)
const total = ref(0)
const pageList = ref<PageItem[]>([])

// 计算属性
const linkSelectItem = computed({
  get: () => props.link,
  set: (value) => {
    if (value) {
      emit('update:link', value)
    }
  },
})

// 监听 visible 变化
watch(
  () => props.visible,
  () => {
    onGetPageList()
  },
)

// 生命周期钩子
onMounted(() => {
  if (sameJudge() && linkSelectItem.value && linkSelectItem.value.id) {
    selectId.value = linkSelectItem.value.id as number
  }
  getTemplateList()
})

// 方法
/** 获取自定义页面数据 */
async function onGetPageList() {
  if (!templateId.value) return

  loading.value = true

  try {
    const res = await getPageList({
      templateId: templateId.value,
      current: pageNum.value,
      size: pageSize.value,
    })

    if (res.code === 200) {
      pageList.value = res.data.list || []
      total.value = res.data.total || 0

      tableData.value = pageList.value.map((item) => ({
        id: Number(item.id),
        type: 5,
        name: item.pageName,
        url: `/pages/customPage/customPage?pageId=${item.id}`,
        append: '',
      }))

      // 初始相同设为选中状态
      if (sameJudge() && linkSelectItem.value && linkSelectItem.value.id) {
        selectId.value = linkSelectItem.value.id as number
      }
    } else {
      ElMessage.warning(`获取数据失败`)
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

/** 获取模板列表 */
async function getTemplateList() {
  try {
    const res = await getTemplate({
      onlineStatus: 1,
      isAll: 0,
    })

    if (res.code === 200) {
      // 获取非默认模板（即为正在使用中的模板）
      const templateList = res.data as TemplateItem[]
      if (templateList.length > 0) {
        // 如果有，即为使用中模板的curd
        templateId.value = templateList[0].id
        onGetPageList()
      }
    } else {
      ElMessage.warning(`获取数据失败`)
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('获取模板数据失败')
  }
}

/** 判断是否为相同类型 */
function sameJudge(): boolean {
  return (
    linkSelectItem.value?.type !== undefined &&
    typeof linkSelectItem.value.type === 'number' &&
    typeNameMap[linkSelectItem.value.type] &&
    typeNameMap[linkSelectItem.value.type].name === name.value
  )
}

/** 处理选择 */
function handleSelect() {
  const currentItem = tableData.value.find((item) => item.id === selectId.value)
  if (currentItem) {
    linkSelectItem.value = { ...linkSelectItem.value, ...currentItem }
  }
}

/** 分页处理 */
function handleSizeChange(val: number) {
  pageNum.value = 1
  pageSize.value = val
  onGetPageList()
}

function handleCurrentChange(val: number) {
  pageNum.value = val
  onGetPageList()
}
</script>

<template>
  <!-- 自定义页面 -->
  <div>
    <BaseTable v-loading="loading" :data="tableData" height="369">
      <el-table-column label="页面名称" prop="name" />
      <el-table-column label="操作" width="100px">
        <template #default="scope">
          <el-radio v-model="selectId" :value="scope.row.id" @change="handleSelect">{{ scope.row.id }} </el-radio>
        </template>
      </el-table-column>
    </BaseTable>
    <PageManage
      :page-size="pageSize"
      :page-num="pageNum"
      :total="total"
      @handle-size-change="handleSizeChange"
      @handle-current-change="handleCurrentChange"
    />
  </div>
</template>
