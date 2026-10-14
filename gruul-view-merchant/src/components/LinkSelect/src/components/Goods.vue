<script setup lang="ts">
import type { GoodsProps, LinkSelectItem } from '@/components/LinkSelect/type'
import { getCouponsGoods } from '@/api/couponActivity/coupon'
import { getAllRegionList, getEffectiveCategory } from '@/api/good/goods'
import BaseTable from '@/components/BaseTable/index.vue'
import { typeNameMap } from '@/components/LinkSelect/constants'

import { LinkType, TAB_CONFIG } from '@/components/LinkSelect/enums'
import PageManage from '@/components/PageManage.vue'
import { ElMessage } from 'element-plus'

interface RegionItem {
  id: string | number
  modeName: string
  index?: number
}

interface CategoryItem {
  id: string
  name: string
  showCategoryVos: CategoryItem[]
  index?: number
}

const props = defineProps<GoodsProps>()
const emit = defineEmits(['update:link'])

const selectId = ref<string | number>()
const loading = ref(false)
const pageSize = ref(10)
const pageNum = ref(1)
const total = ref(0)
const showVal = ref('')
const showSecVal = ref('')
const saleMode = ref<string | number>('')
const showList = ref<RegionItem[]>([])
const showSecList = ref<CategoryItem[]>([])
const tableData = ref<LinkSelectItem[]>([])

// 搜索参数
const search = ref({
  goodsName: '',
  showCategoryId: '',
  saleMode: '',
  minPrice: '',
  maxPrice: '',
})

// 判断是否为相同类型
function sameJudge(): boolean {
  return props.link?.type !== undefined && typeof props.link.type === 'number' && typeNameMap[props.link.type]?.name === TAB_CONFIG.GOODS
}

// 选择处理
function selectHandle() {
  const currentItem = tableData.value.find((item) => item.id === selectId.value)
  if (currentItem && props.link) {
    emit('update:link', { ...props.link, ...currentItem })
  }
}

// 获取表格数据
async function getTableData() {
  const param = {
    current: pageNum.value,
    size: pageSize.value,
    ...search.value,
  }

  if (Number(param.maxPrice) < Number(param.minPrice)) {
    ElMessage({
      showClose: true,
      message: '请填写正确价格区间',
      type: 'warning',
    })
    return
  }

  loading.value = true

  try {
    const { data } = await getCouponsGoods(param)

    if (data) {
      const goodsList = data.list || []
      tableData.value = goodsList.map((item: any) => ({
        id: Number(item.id),
        type: LinkType.Goods,
        name: item.name,
        url: '/subcontract/pages/detail/detail',
        append: '',
        limitType: item.limitType,
        maxPrice: item.maxPrice,
        minPrice: item.minPrice,
      }))

      total.value = data.total

      // 初始相同设为选中状态
      if (sameJudge() && props.link?.id) {
        selectId.value = Number(props.link.id)
      }
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('获取商品数据失败')
  } finally {
    loading.value = false
  }
}

// 搜索
function searchClick() {
  pageNum.value = 1
  getTableData()
}

// 清除筛选
function onClear() {
  showVal.value = ''
  showSecVal.value = ''
  search.value = {
    goodsName: '',
    showCategoryId: '',
    saleMode: '',
    minPrice: '',
    maxPrice: '',
  }
  getTableData()
}

// 选择专区
function selectTem(item: RegionItem) {
  saleMode.value = item.id
  search.value.saleMode = String(saleMode.value)
  search.value.showCategoryId = ''
  showSecVal.value = ''

  // 获取分类数据
  getEffectiveCategory(item.id).then((res) => {
    if (res.code === 200) {
      showSecList.value = res.data
    }
  })
}

// 选择分类
function selectGood(item: CategoryItem) {
  search.value.showCategoryId = item.id
  search.value.saleMode = String(saleMode.value)
  getTableData()
}

// 分页处理
function handleSizeChange(val: number) {
  pageNum.value = 1
  pageSize.value = val
  getTableData()
}

function handleCurrentChange(val: number) {
  pageNum.value = val
  getTableData()
}

onMounted(async () => {
  getTableData()

  try {
    const res = await getAllRegionList({})
    showList.value = res.data
    if (res.data.length > 0) {
      saleMode.value = res.data[0].id
    }
  } catch (error) {
    console.error(error)
  }
})

// 监听父弹窗显隐
watch(
  () => props.visible,
  (newVal) => {
    if (newVal) {
      getTableData()
    }
  },
)
</script>

<template>
  <!-- 商超商品 -->
  <div class="link-good">
    <div class="search-wrap">
      <el-button @click="onClear"> 刷新 </el-button>
      <el-select v-model="showVal" style="width: 120px" placeholder="全部专区" clearable @clear="onClear">
        <el-option-group v-for="group in showList" :key="group.index">
          <el-option :label="group.modeName" :value="group.modeName" @click="selectTem(group)" />
        </el-option-group>
      </el-select>
      <el-select v-model="showSecVal" placeholder="请选择分类" style="width: 120px">
        <el-option-group v-for="group in showSecList" :key="group.index">
          <el-option-group :key="group.id" :label="group.name" :value="group.name" />
          <el-option
            v-for="item in group.showCategoryVos"
            :key="item.name"
            :label="item.name"
            :value="item.name"
            style="margin-left: 20px"
            @click="selectGood(item)"
          />
        </el-option-group>
      </el-select>
      <div class="demo-input-suffix">
        价格：
        <el-input v-model="search.minPrice" placeholder="最小值" class="mr10" type="number" />
        <span>-</span>
        <el-input v-model="search.maxPrice" placeholder="最大值" class="ml10" type="number" />
      </div>
      <el-input v-model="search.goodsName" placeholder="请输入关键词" class="search-wrap-input">
        <template #append>
          <el-button icon="Search" @click="searchClick" />
        </template>
      </el-input>
    </div>
    <BaseTable v-loading="loading" :data="tableData" height="369">
      <el-table-column label="商品名称" prop="name" />
      <el-table-column label="操作" width="100px">
        <template #default="scope">
          <el-radio v-model="selectId" :value="scope.row.id" @change="selectHandle">{{ scope.row.id }}</el-radio>
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
.demo-input-suffix .el-input {
  width: 98px !important;
}
.ml10 {
  margin-left: 10px;
}
.mr10 {
  margin-right: 10px;
}
.search-wrap {
  display: flex;
  justify-content: space-between;
  justify-items: center;
}
.search-wrap-input {
  width: 180px;
}
</style>
