<script setup lang="ts">
import { delIntegralGoods, upDownIntegralGoods } from '@/api/integral/integral'
import MContent from '@/components/MContainer/MContent.vue'
import PageManage from '@/components/PageManage.vue'
import SetDrop from '@/components/SetDrop/SetDrop.vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'

import { useRouter } from 'vue-router'
import SelectCom from './components/SelectCom.vue'
import ShopList from './productList/ShopList.vue'

// 类型定义
interface SearchObjectType {
  pageSize: number
  pageNum: number
  total: number
  status: string
  integralGoodName: string
  productSn: string
}

interface ShopListInstance {
  getList: () => void
  productListRef: {
    getCheckedItem: () => Array<{ id: string }>
  }
}

const router = useRouter()
const shopListRef = ref<ShopListInstance | null>(null)

// 选择多个小区
const idList = ref<string[]>([])

const searchObj = reactive<SearchObjectType>({
  // 分页条数
  pageSize: 10,
  // 分页页码
  pageNum: 1,
  // 数据长度
  total: 0,
  // 商品状态
  status: '',
  // 商品名称
  integralGoodName: '',
  // 商品货号
  productSn: '',
})

// 选中搜索条件
const searchVal = ref('1')

// 搜索内容
const searchText = ref('')

// 不同条件搜索
const searchtList = [
  {
    value: '1',
    label: '商品名称',
  },
  {
    value: '2',
    label: '商品ID',
  },
]

const mallDropdownCoupList = computed(() => {
  return [
    {
      text: '批量上架',
      command: '1',
      show: true,
    },
    {
      text: '批量下架',
      command: '2',
      show: true,
    },
  ]
})

// 获得总数
function getTotal(total: number) {
  searchObj.total = total
}

/**
 * 顶部状态筛选
 */
function changeSel(e: string) {
  switch (e) {
    case '0':
      searchObj.status = ''
      break
    case '1':
    case '2':
      searchObj.status = e === '1' ? '1' : '0'
      break
  }
  shopListRef.value?.getList()
}

/**
 * 搜索功能
 */
function searchBy() {
  if (searchVal.value === '') {
    ElMessage.error('请选择搜索类型')
    return
  }
  if (searchVal.value === '1') {
    searchObj.integralGoodName = searchText.value
    searchObj.productSn = ''
  } else {
    searchObj.integralGoodName = ''
    searchObj.productSn = searchText.value
  }
  shopListRef.value?.getList()
}

/**
 * 发布积分商品
 */
function issueGood() {
  router.push({ name: 'IssueGoodAdd' })
}

function setClick() {
  dealDelete()
}

/**
 * 下拉框选择
 */
function command(e: string) {
  const list = shopListRef.value?.productListRef?.getCheckedItem() || []
  const currentIdList: string[] = (idList.value = [])
  list.forEach((item: { id: string }) => {
    currentIdList.push(item.id)
  })
  if (currentIdList.length === 0) {
    ElMessage.error('请先选择商品')
    return
  }
  switch (e) {
    case '1':
    case '2':
      ElMessageBox.confirm(`确定${e === '2' ? '下架' : '上架'}选中商品？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
        upDownIntegralGoods(String(e === '2' ? 0 : 1), currentIdList).then((res) => {
          if (res.code === 200) {
            ElMessage.success(`商品已${e === '2' ? '下架' : '上架'}`)
            shopListRef.value?.getList()
          }
        })
      })
      break
    case '3':
      ElMessageBox.confirm(`确定删除选中商品？删除后无法恢复`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
        delIntegralGoods(currentIdList.join(','), {}).then((res) => {
          if (res.code === 200) {
            ElMessage.success('删除成功')
            shopListRef.value?.getList()
          }
        })
      })
      break
  }
}

/**
 * 商城模式下的批量删除
 */
function dealDelete() {
  command('3')
}

/**
 * @method handleSizeChange
 * @description 每页 条
 */
function handleSizeChange(val: number) {
  searchObj.pageSize = val
  shopListRef.value?.getList()
}

/**
 * @method handleCurrentChange
 * @description 当前页
 */
function handleCurrentChange(val: number) {
  searchObj.pageNum = val
  shopListRef.value?.getList()
}
</script>

<template>
  <MContent class="flex-1">
    <template #section>
      <div class="couponTop">
        <div class="flex">
          <el-button type="primary" @click="issueGood"> 发布积分商品 </el-button>
          <SetDrop set-name="批量删除" :dropdown-list="mallDropdownCoupList" style="margin-left: 20px" @set-click="setClick" @command="command" />
        </div>
        <SelectCom style="margin-left: 280px" @change-sel="changeSel" />
        <div class="couponTop__left">
          <el-input v-model="searchText" class="filter__right--input" clearable style="width: 320px" placeholder="输入关键词" @keyup.enter="searchBy">
            <template #prepend>
              <el-select v-model="searchVal" placeholder="搜索条件" style="width: 100px">
                <el-option v-for="item in searchtList" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </template>
            <template #append>
              <el-button :icon="Search" @click="searchBy" />
            </template>
          </el-input>
        </div>
      </div>
    </template>

    <template #table>
      <ShopList ref="shopListRef" :search-obj="searchObj" @get-total="getTotal" />
    </template>

    <template #pagination>
      <PageManage
        :page-size="searchObj.pageSize"
        :page-num="searchObj.pageNum"
        :total="searchObj.total"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
      >
        <template #pagination--left>
          <SetDrop set-name="批量删除" :dropdown-list="mallDropdownCoupList" @set-click="setClick" @command="command" />
        </template>
      </PageManage>
    </template>
  </MContent>
</template>

<style lang="scss" scoped>
.couponTop {
  display: flex;
  justify-content: space-between;
  align-items: center;

  &__left {
    display: flex;
  }
}

.search {
  width: 32px;
  height: 32px;
  display: flex;
  justify-content: center;
  align-items: center;
  border: 1px solid #dcdfe6;
  border-left: none;
  border-radius: 4px;
  cursor: pointer;
}
</style>
