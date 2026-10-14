<script setup lang="ts">
import type { DisplayClassIfyProps, LinkSelectItem } from '@/components/LinkSelect/type'
import { getRegionList } from '@/api/good/goods'
import BaseTable from '@/components/BaseTable/index.vue'
import { typeNameMap } from '@/components/LinkSelect/constants'
import PageManage from '@/components/PageManage.vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

interface RegionItem {
  id: string | number
  modeName: string
}

const props = defineProps<DisplayClassIfyProps>()
const emit = defineEmits(['update:link'])

const name = 'DisplayClassIfy'
const selectId = ref<number>()
const loading = ref(false)
const tableData = ref<LinkSelectItem[]>([])
const pageNum = ref(1)
const pageSize = ref(20)
const total = ref(0)
const searchName = ref('')
const list = ref<RegionItem[]>([])

// 判断是否为相同类型
function sameJudge(): boolean {
  return (
    props.link?.type !== undefined &&
    typeof props.link.type === 'number' &&
    typeNameMap[props.link.type] &&
    typeNameMap[props.link.type].name === name
  )
}

// 选择处理
function selectHandle() {
  const currentItem = tableData.value.find((item) => item.id === selectId.value)

  if (currentItem && props.link) {
    emit('update:link', { ...props.link, ...currentItem })
  }
}

// 获取所有专区
async function getRegionListData() {
  loading.value = true

  const param = {
    modeName: searchName.value,
    current: pageNum.value,
    size: pageSize.value,
  }

  try {
    const res = await getRegionList(param)

    list.value = res.data.list || []
    total.value = res.data.total || 0
    tableData.value = list.value.map((item) => {
      return {
        id: Number(item.id),
        type: 2,
        name: item.modeName,
        url: '/pages/index/index',
        append: 'mall',
      }
    })

    // 初始相同设为选中状态
    if (sameJudge() && props.link?.id) {
      selectId.value = Number(props.link.id)
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('获取专区数据失败')
  } finally {
    loading.value = false
  }
}

// 搜索
function handleSearch() {
  pageNum.value = 1
  getRegionListData()
}

// 分页处理
function handleSizeChange(val: number) {
  pageNum.value = 1
  pageSize.value = val
  getRegionListData()
}

function handleCurrentChange(val: number) {
  pageNum.value = val
  getRegionListData()
}

onMounted(() => {
  getRegionListData()
})

// 监听父弹窗显隐
watch(
  () => props.visible,
  (newVal) => {
    if (newVal) {
      getRegionListData()
    }
  },
)
</script>

<template>
  <!-- 商品专区 -->
  <div class="display-classify">
    <div class="search-box">
      <el-input v-model="searchName" placeholder="请输入关键词" class="search-input">
        <template #suffix>
          <el-icon class="el-input__icon" @click="handleSearch">
            <Search />
          </el-icon>
        </template>
      </el-input>
    </div>
    <BaseTable v-loading="loading" :data="tableData" height="369">
      <el-table-column label="专区名称" prop="name" />
      <el-table-column label="操作" width="100px">
        <template #default="scope">
          <el-radio v-model="selectId" :value="scope.row.id" @change="selectHandle">{{ scope.row.id }} </el-radio>
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

<style scoped>
.display-classify {
  width: 100%;
}
.search-box {
  margin-bottom: 20px;
}
.search-input {
  width: 220px;
}
</style>
