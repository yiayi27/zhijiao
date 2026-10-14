<script setup lang="ts">
import { GoodDel, GoodUpDown, updateDominoState, updateGoodsApart } from '@/api/good/goods'
import MContent from '@/components/MContainer/MContent.vue'
import PageManage from '@/components/PageManage.vue'
import { ElMessageBox } from 'element-plus'

import { useRouter } from 'vue-router'
import SetClassify from './components/goodsComp/SetClassify.vue'
import GoodsList from './components/GoodsList.vue'
import Search from './components/Search.vue'

// 定义Props
interface Props {
  saleMode?: string
}

const props = defineProps<Props>()

// 定义ref
const searchRef = ref()
const goodsListRef = ref()
const setClassRef = ref()

// 定义路由
const router = useRouter()

// 定义响应式状态
const changeId = ref('')
const goodIds = ref<number[]>([])
const showGetList = ref<any[]>([])
const searchType = ref<Record<string, any>>({})
const pageSize = ref(0)
const pageNum = ref(0)
const total = ref(0)
const areaList = ref<any[]>([])
const dialogArea = ref(false)

// 监听saleMode变化
watch(
  () => props.saleMode,
  () => {
    changeId.value = props.saleMode || ''
  },
)

// 组件挂载时
onMounted(() => {
  changeId.value = props.saleMode || ''
})

/**
 * 发布商品
 */
function publishGoods() {
  router.push({
    name: 'AddGood',
    query: { saleMode: changeId.value },
  })
}

/** 根据选择批量操作 */
async function commandVal(val: string, _type = false) {
  if (goodIds.value.length === 0) {
    ElMessage.error('请先选择商品')
    return
  }

  // 使用Record<string, Function>类型定义actionMap
  const actionMap: Record<string, () => Promise<void>> = {
    '1': () => transApart(val),
    '2': () => transApart(val),
    '3': () => {
      getGoodArea()
      return Promise.resolve()
    },
    '4': () => domino(val),
    '5': () => domino(val),
    '6': () => upDownGoods(val),
    '7': () => upDownGoods(val),
    '8': () => delByIds(),
    default: () => transApart(val),
  }

  const action = actionMap[val] || actionMap.default
  await action()
}

/**
 * 批量转移专区
 */
async function transApart(val: string) {
  if (!goodsListRef.value?.regionList) return

  const selectItem = goodsListRef.value.regionList.find((item: any) => item.id === val)
  if (!selectItem) return

  const res = await updateGoodsApart(val, goodIds.value)

  if (res.code === 200) {
    ElMessage.success(`商品已${selectItem.text}`)
    getGoodList()
  }
}

/**
 * 获取投放区域
 */
function getGoodArea() {
  let a: string[] = []

  if (!goodsListRef.value || !goodsListRef.value.goodList) return

  goodIds.value.forEach((item) => {
    goodsListRef.value.goodList.forEach((v: any) => {
      if (item === v.id) {
        const b = v.launchArea ? v.launchArea.split(',') : []
        a = Array.from(new Set(a.concat(b)))
      }
    })
  })

  areaList.value.forEach((e) => {
    e.list.forEach((i: any) => {
      i.list.forEach((c: any) => {
        c.isChosen = 0
        a.forEach((t) => {
          if (t === c.pointId) {
            c.isChosen = 1
          }
        })
      })
    })
  })

  dialogArea.value = true
}

/**
 * 批量删除
 */
async function delByIds() {
  await ElMessageBox.confirm('确定要删除选中商品吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })

  const { code } = await GoodDel(goodIds.value.join(','), '0', {})

  if (code === 200) {
    ElMessage.success('删除成功')
    getGoodList()
  }
}

/**
 * 批量上下架操作
 */
async function upDownGoods(val: string) {
  if (!goodsListRef.value || !goodsListRef.value.goodList) return

  const list = goodsListRef.value.goodList
  const hasNoType = list.some((item: any) => item.productShowCategorys.length === 0)

  if (hasNoType) {
    ElMessage.error('选择商品中有未编辑分类商品，请先编辑后在进行操作')
    return
  }

  const { code } = await GoodUpDown(val === '6' ? '1' : '0', '0', goodIds.value)

  if (code === 200) {
    ElMessage.success(`商品已${val === '6' ? '上架' : '下架'}`)
    getGoodList()
  }
}

/**
 * 批量接龙操作
 */
async function domino(val: string) {
  const { code } = await updateDominoState(val === '4' ? '1' : '0', goodIds.value)

  if (code === 200) {
    ElMessage.success(`商品已${val === '4' ? '参加' : '取消'}接龙活动`)
    getGoodList()
  }
}

/**
 * 获取选中商品ids数组
 */
function getGoodId(data: number[]) {
  goodIds.value = data
}

/** 展示分类获取已选择的分类  */
function getShowProList(data: any) {
  showGetList.value = data || []
  total.value = goodsListRef.value.total
  pageSize.value = goodsListRef.value.searchType.size
  pageNum.value = goodsListRef.value.searchType.current
}

/**
 * 获取商品列表
 */
function getGoodList() {
  if (!goodsListRef.value) return

  goodsListRef.value.searchType = Object.assign(goodsListRef.value.searchType, searchType.value)
  goodsListRef.value.getProduct()
}

/**
 * 获取搜索条件
 */
function getSearch(data: any) {
  searchType.value = data
  searchType.value.current = 1
  getGoodList()
}

/**
 * @method handleSizeChange
 * @description 每页 条
 */
function handleSizeChange(val: number) {
  if (!goodsListRef.value) return

  goodsListRef.value.searchType.size = val
  goodsListRef.value.getProduct()
}

/**
 * @method handleCurrentChange
 * @description 当前页
 */
function handleCurrentChange(val: number) {
  if (!goodsListRef.value) return

  goodsListRef.value.searchType.current = val
  goodsListRef.value.getProduct()
}
</script>

<template>
  <MContent>
    <template #form>
      <Search ref="searchRef" :change-id="changeId" @search-by="getSearch" />
    </template>

    <template #section>
      <div class="topLine">
        <div class="topLine__left">
          <el-button type="primary" @click="publishGoods"> 发布商品 </el-button>
          <!-- 设置分类 -->
          <SetClassify
            ref="setClassRef"
            :good-ids="goodIds"
            :show-get-list="showGetList"
            style="margin-left: 20px"
            :is-item="false"
            :is-value="false"
            :sale-mode="saleMode"
            @command="commandVal"
            @change-ids="getGoodList"
          >
            设置分类
          </SetClassify>
        </div>
      </div>
    </template>

    <template #table>
      <GoodsList v-if="changeId" ref="goodsListRef" :change-id="changeId" @good-id="getGoodId" @get-show-pro-list="getShowProList" />
    </template>

    <template #pagination>
      <PageManage
        :page-size="pageSize"
        :page-num="pageNum"
        :total="total"
        style="margin-top: 0px"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
      >
        <template #pagination--left>
          <!-- 设置分类 -->
          <SetClassify
            ref="setClassRef"
            :good-ids="goodIds"
            :show-get-list="showGetList"
            :is-item="false"
            @command="commandVal"
            @change-ids="getGoodList"
          />
        </template>
      </PageManage>
    </template>
  </MContent>
</template>

<style lang="scss" scoped>
.topLine {
  display: flex;
  justify-content: space-between;
  align-items: center;

  &__left {
    display: flex;
  }

  &__right {
    width: 450px;
    display: flex;
    justify-content: space-around;
  }
}
</style>
