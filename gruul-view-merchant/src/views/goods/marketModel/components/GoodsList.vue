<script setup lang="ts">
import {
  getAllRegionList,
  getImgCode,
  getProList,
  GoodDel,
  GoodUpDown,
  putGoodMemberPrice,
  putRebatePrice,
  updateGood,
  updateGoodsApart,
} from '@/api/good/goods'
import GoodNameEdit from '@/components/GoodNameEdit.vue'
import GoodPriceEdit from '@/components/GoodPriceEdit.vue'
import SetDrop from '@/components/SetDrop/SetDrop.vue'
import { ArrowDown } from '@element-plus/icons-vue'
import { useClipboard } from '@vueuse/core'
import { ElLoading, ElMessage, ElMessageBox } from 'element-plus'

import { useRouter } from 'vue-router'
import Distribution from './Distribution.vue'
import StockWarn from './goodsComp/StockWarn.vue'
import MemberSet from './MemberSet.vue'

interface RegionItem {
  id: string | number
  modeName: string
  text: string
  show: boolean
  disabled: boolean
  command: string | number
  isCustom: boolean
  modeType?: string
}

interface SkuStockItem {
  id: string | number
  stock: number
  price: string | number
  memberPrices: Array<{
    memberPrice: string | number
  }>
  memberType?: string
  skuRebates?: Array<{
    rebatePrice: string | number
  }>
  rebateType?: string
}

interface GoodItem {
  id: string | number
  name: string
  pic: string
  status: number
  saleMode: string | number
  skuStocks: SkuStockItem[]
  stock?: number
  providerName?: string
  createTime?: string
  score?: number | string
  productShowCategorys: any[]

  [key: string]: any // 添加索引签名，允许任意属性
}

interface DropdownItem {
  text: string
  command: string
  show: boolean
  disabled: boolean
}

interface SearchParams {
  current: number
  size: number
  saleMode: string
  [key: string]: any
}

const props = defineProps({
  changeId: {
    type: [String, Number],
    default: '',
  },
})

const emit = defineEmits(['goodId', 'getShowProList'])

const memberSetRef = ref<InstanceType<typeof MemberSet> | null>(null)
const rebatesSetRef = ref<InstanceType<typeof Distribution> | null>(null)
const router = useRouter()

// 响应式状态
const saleMode = ref('')
const goodList = ref<GoodItem[]>([])
const hasList = ref(false)
const loading = ref(false)
const checkAll = ref(false)
const isIndeterminate = ref(false)
const tableCheckedItem = ref<GoodItem[]>([])
const currentGood = ref<GoodItem | null>(null)
const total = ref(0)
const searchType = reactive<SearchParams>({
  current: 1,
  size: 10,
  saleMode: '',
})
const dialogVisible = ref(false)
const memberFlag = ref(false)
const rebatesFlag = ref(false)
const codeImg = ref('')
const regionList = ref<RegionItem[]>([])
const currentRegion = ref('')

/**
 * 获取商品列表查询参数
 */
function getProListParams(): SearchParams {
  searchType.saleMode = saleMode.value
  return searchType
}

/**
 * 获取商品库存
 */
function getGoodStock(skuStocks: SkuStockItem[] = []): number {
  let stock = 0
  skuStocks.forEach((v) => {
    stock += v.stock
  })
  return stock
}

/**
 * 获取商品列表
 */
async function getProduct(): Promise<void> {
  const param = getProListParams()
  loading.value = true

  const res = await getProList(param).finally(() => {
    loading.value = false
  })
  const productList = res.data.list
  total.value = res.data.total
  hasList.value = res.data.list.length === 0
  productList.forEach((item: GoodItem) => {
    item.stock = getGoodStock(item.skuStocks)
  })
  goodList.value = productList
  checkAll.value = false
  handleCheckAllChange()

  emit('getShowProList', goodList.value)
}

// 设置获取商品列表接口参数
function getList(param: { regionId: string }): void {
  searchType.saleMode = param.regionId
  getProduct()
}

// 暴露方法给父组件
defineExpose({
  searchType,
  total,
  tableCheckedItem,
  getProduct,
  getList,
  goodList,
  regionList,
})

// 计算属性
const itemDropList = computed(() => {
  return (row: GoodItem): DropdownItem[] => {
    /** 操作项下拉菜单 */
    return [
      {
        text: '商品码',
        command: '3',
        show: true,
        disabled: false,
      },
      {
        text: '删除',
        command: '4',
        show: true,
        disabled: false,
      },
      {
        text: '复制链接',
        command: '5',
        show: true,
        disabled: false,
      },
      {
        text: '上架',
        command: '6',
        show: row.status === 0,
        disabled: false,
      },
      {
        text: '下架',
        command: '7',
        show: row.status === 1,
        disabled: false,
      },
      {
        text: '会员价',
        command: '8',
        show: true,
        disabled: false,
      },
      {
        text: '返利设置',
        command: '9',
        show: true,
        disabled: false,
      },
    ]
  }
})

// 监听 goodIds 变化
watch(
  () => tableCheckedItem.value,
  (val) => {
    emit(
      'goodId',
      val.map((item) => item.id),
    )
  },
  { deep: true },
)

// 监听 changeId 变化
watch(
  () => props.changeId,
  () => {
    searchType.current = 1
    searchType.size = 10
    searchType.saleMode = ''
    saleMode.value = props.changeId as string
    getProduct()
    getApartList()
  },
)

onMounted(() => {
  saleMode.value = props.changeId as string
  getProduct()
  getApartList()
})

/**
 * 获取所有专区
 */
function getApartList(): void {
  getAllRegionList({}).then((res) => {
    res.data.forEach((item: RegionItem) => {
      item.text = `移至${item.modeName}`
      item.show = true
      item.disabled = false
      item.command = item.id
      item.isCustom = false
      if (item.id === saleMode.value) {
        item.isCustom = true
        currentRegion.value = item.modeName
      }
    })

    regionList.value = res.data
  })
}

/**
 * 全选|取消全选 商品
 */
function handleCheckAllChange(): void {
  // goodIds.value = checkAll.value ? goodList.value.map(item => item.id as number) : []
  isIndeterminate.value = false
}

/**
 * 转移专区
 */
function transWay(val: string | number, row: GoodItem): void {
  const selectItem = regionList.value.filter((item) => item.id === val)
  if (selectItem.length === 0) return

  updateGoodsApart(String(val), [row.id]).then((res) => {
    if (res.code === 200) {
      ElMessage.success(`商品已${selectItem[0].text}`)
      getProduct()
    }
  })
}

/**
 * 获取下拉框
 */
const { copy, isSupported } = useClipboard()
function getDropdown(val: string, row: GoodItem): void {
  if (Number(val) > 9) {
    transWay(val, row)
    return
  }
  switch (val) {
    case '2':
      updateGoodsApart('2', [row.id]).then((res) => {
        if (res.code === 200) {
          ElMessage.success(`商品已移至商超专区`)
          getProduct()
        }
      })
      break
    case '3':
      dealShopCode(row.id)
      break
    case '4':
      delProduct(row.id)
      break
    case '5':
      if (row.status === 0) {
        ElMessage.error('请先上架商品')
        return
      }
      if (isSupported) {
        copy(`subcontract/pages/detail/detail.html?id=${row.id}`)
        ElMessage.success('复制成功')
      } else {
        ElMessage.error('当前浏览器不支持复制')
      }

      break
    case '6':
    case '7':
      changeStatus(row)
      break
    case '8':
      currentGood.value = row
      memberFlag.value = true
      break
    case '9':
      currentGood.value = row
      rebatesFlag.value = true
      break
  }
}

/**
 * 会员弹框确认
 */
function memberSure(): void {
  if (!memberSetRef.value || !currentGood.value) return

  const skuStockDtos = memberSetRef.value.goodQuery.skuStocks
  const chooseRadio = memberSetRef.value.chooseRadio
  skuStockDtos.map((item: SkuStockItem) => {
    item.memberType = chooseRadio
    return item
  })
  const id = memberSetRef.value.goodQuery.id
  let isPass = true
  if (chooseRadio === '0' || chooseRadio === '3') {
    skuStockDtos.forEach((item: SkuStockItem) => {
      item.memberPrices = []
    })
    putGoodMemberPrice(id, skuStockDtos).then(() => {
      memberFlag.value = false
      getProduct()
      ElMessage.success('设置成功')
    })
    return
  }
  const reg = /^(([1-9]\d*)|((0\.\d{0,2}|[1-9]\d*\.\d{0,2})))$/

  if (chooseRadio === '2') {
    isPass = memberPercentValid(skuStockDtos)
  } else {
    const hasHigh = skuStockDtos.some((skuItem: SkuStockItem) => {
      return skuItem.memberPrices.some(() => {
        return Number(skuItem.memberPrices[0].memberPrice) > Number(skuItem.price)
      })
    })
    if (hasHigh) {
      ElMessage.error('会员价格不能高于实售价')
      isPass = false
      return
    }

    const hasZero = skuStockDtos.some((skuItem: SkuStockItem) => {
      return skuItem.memberPrices.some((memItem: { memberPrice: string | number }) => {
        return Number(memItem.memberPrice) === 0
      })
    })
    if (hasZero) {
      ElMessage.error('会员价不能为0')
      isPass = false
      return
    }

    const hasRule = skuStockDtos.some((skuItem: SkuStockItem) => {
      return skuItem.memberPrices.some((memItem: { memberPrice: string | number }, index: number) => {
        if (index !== skuItem.memberPrices.length - 1) {
          return Number(skuItem.memberPrices[index].memberPrice) <= Number(skuItem.memberPrices[index + 1].memberPrice)
        }
        return false
      })
    })
    if (hasRule) {
      ElMessage.error('会员优惠递增')
      isPass = false
      return
    }

    const hasNaN = skuStockDtos.some((skuItem: SkuStockItem) => {
      return skuItem.memberPrices.some((memItem: { memberPrice: string | number }) => {
        return reg.test(String(memItem.memberPrice)) === false
      })
    })
    if (hasNaN) {
      ElMessage.error('会员价格输入有误')
      isPass = false
      return
    }
  }

  if (!isPass) return
  putGoodMemberPrice(id, skuStockDtos).then(() => {
    ElMessage.success('设置成功')
    memberFlag.value = false
    getProduct()
  })
}

/**
 * 会员百分比验证
 */
function memberPercentValid(skuStockDtos: SkuStockItem[]): boolean {
  const reg = /^(([1-9]\d*)|((0\.\d{0,2}|[1-9]\d*\.\d{0,2})))$/
  const hasNum = skuStockDtos.some((skuItem) => {
    return skuItem.memberPrices.some((memItem) => {
      return Number(memItem.memberPrice) <= 0 || Number(memItem.memberPrice) >= 100
    })
  })
  if (hasNum) {
    ElMessage.error('会员比例设置在0-100之间')
    return false
  }

  const hasRule = skuStockDtos.some((skuItem) => {
    return skuItem.memberPrices.some((memItem, index) => {
      if (index !== skuItem.memberPrices.length - 1) {
        return Number(skuItem.memberPrices[index].memberPrice) <= Number(skuItem.memberPrices[index + 1].memberPrice)
      }
      return false
    })
  })
  if (hasRule) {
    ElMessage.error('会员优惠递增')
    return false
  }

  const hasNaN = skuStockDtos.some((skuItem) => {
    return skuItem.memberPrices.some((memItem) => {
      return reg.test(String(memItem.memberPrice)) === false
    })
  })
  if (hasNaN) {
    ElMessage.error('会员价格输入有误')
    return false
  }
  return true
}

/**
 * 确认返利弹窗
 */
function rebatesSure(): void {
  if (!rebatesSetRef.value || !currentGood.value) return

  const skuStockDtos = rebatesSetRef.value.reList.skuStocks
  const chooseRadio = rebatesSetRef.value.chooseRadio
  skuStockDtos.map((item: SkuStockItem) => {
    item.rebateType = chooseRadio
    return item
  })
  const id = rebatesSetRef.value.reList.id
  let isPass = true
  if (chooseRadio === '0' || chooseRadio === '3') {
    skuStockDtos.forEach((item: SkuStockItem) => {
      item.skuRebates = []
    })
    putRebatePrice(id, skuStockDtos).then(() => {
      rebatesFlag.value = false
      getProduct()
    })

    return
  }
  const reg = /^(([1-9]\d*)|((0\.\d{0,2}|[1-9]\d*\.\d{0,2})))$/
  const hasNaN = skuStockDtos.some((rebItem: SkuStockItem) => {
    return reg.test(String(rebItem.skuRebates?.[0]?.rebatePrice)) === false
  })
  if (hasNaN) {
    ElMessage.error('返利金额设置有误')
    isPass = false
    return
  }
  if (chooseRadio === '1') {
    const hasPrice = skuStockDtos.some((rebItem: SkuStockItem) => {
      return Number(rebItem.skuRebates?.[0]?.rebatePrice) >= Number(rebItem.price)
    })
    if (hasPrice) {
      ElMessage.error('返利价格需低于规格实售价')
      isPass = false
      return
    }
  } else {
    const hasNaN = skuStockDtos.some((rebItem: SkuStockItem) => {
      return Number(rebItem.skuRebates?.[0]?.rebatePrice) >= 100
    })
    if (hasNaN) {
      ElMessage.error('返利金额比例不得大于或等于100')
      isPass = false
      return
    }
  }

  if (!isPass) return
  putRebatePrice(id, skuStockDtos).then(() => {
    ElMessage.success('设置成功')
    rebatesFlag.value = false
    getProduct()
  })
}

/**
 * 获取商品码
 */
function dealShopCode(id: string | number): void {
  const wxaGetwxacode = {
    path: `subcontract/pages/detail/detail?id=${id}`,
    width: 0,
  }
  const loadingInstance = ElLoading.service({ target: '.imgView' })
  getImgCode(wxaGetwxacode)
    .then((res) => {
      codeImg.value = res.data
      dialogVisible.value = true
    })
    .finally(() => {
      loadingInstance.close()
    })
}

/**
 * 删除商品
 */
async function delProduct(ids: string | number): Promise<void> {
  await ElMessageBox.confirm('确定要删除选中商品吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
  const { code } = await GoodDel(String(ids), '0', {})
  if (code === 200) {
    ElMessage.success('删除成功')
    getProduct()
  }
}

/**
 * 设置当前商品
 */
function currentGoodClick(val: GoodItem): void {
  currentGood.value = val
}

/**
 * 更新商品名称
 */
async function updateGoodName(newName: string): Promise<void> {
  if (!currentGood.value) return

  const current = currentGood.value
  const param = {
    ...current,
    name: newName,
  }
  await updateGoodInfo(param)
  current.name = newName
}

/**
 * 更新商品价格
 */
async function updateGoodPrice(prices: Record<string | number, string | number>): Promise<void> {
  if (!currentGood.value) return

  const current = currentGood.value
  const skuStocks = current.skuStocks.map((item) => {
    return {
      ...item,
      price: prices[item.id],
    }
  })
  const param = {
    ...current,
    skuStocks,
  }
  await updateGoodInfo(param)
  current.skuStocks = skuStocks
}

/**
 * 更新商品信息
 */
async function updateGoodInfo(param: any): Promise<boolean> {
  const { code } = await updateGood(param)
  if (code === 200) {
    ElMessage.success('修改成功')
    return Promise.resolve(true)
  }

  return Promise.reject(false)
}

/**
 * 上下架商品
 */
function changeStatus(item: GoodItem): void {
  if (item.productShowCategorys.length === 0) {
    ElMessage.error(`商品${item.status === 0 ? '上架' : '下架'}失败,请重新编辑商品分类信息`)
    return
  }
  const id: Array<string | number> = []
  id.push(item.id)
  GoodUpDown(item.status === 1 ? '0' : '1', String(item.saleMode), id).then((res) => {
    if (res.code === 200) {
      ElMessage.success(`商品已${item.status === 0 ? '上架' : '下架'}`)
      getProduct()
    }
  })
}

/**
 * 编辑商品
 */
function edit(item: GoodItem): void {
  router.push({
    name: 'AddGood',
    query: {
      id: item.id,
      saleMode: item.saleMode,
    },
  })
}
</script>

<template>
  <!-- 商品列表 -->
  <m-table v-model:checked-item="tableCheckedItem" v-loading="loading" :data="goodList" :selection="true" class="imgView">
    <m-table-column prop="userName" label="商品" width="230" align="left">
      <template #default="{ row }">
        <div class="goodList">
          <div style="width: 70px; height: 70px">
            <img :src="row.pic" />
          </div>
          <div class="goodList__msg" @click="currentGoodClick(row)">
            <GoodNameEdit :good-name="row.name" @change="updateGoodName" />

            <GoodPriceEdit :sku-stocks="row.skuStocks" :sku-detail="row" @change-price="updateGoodPrice" />

            <div class="goodList__msg--apply">供应商:{{ row.providerName }}</div>
          </div>
        </div>
      </template>
    </m-table-column>
    <m-table-column prop="userName" label="库存">
      <template #default="{ row }">
        <span class="stockWarn center">
          {{ row.stock }}
          <StockWarn :stock-item="row" />
        </span>
      </template>
    </m-table-column>
    <m-table-column prop="userName" label="创建时间" width="140">
      <template #default="{ row }">
        <span>{{ row.createTime }}</span>
      </template>
    </m-table-column>
    <m-table-column prop="userName" label="评分">
      <template #default="{ row }">
        <span>{{ row.score }}</span>
      </template>
    </m-table-column>
    <m-table-column prop="userName" label="状态">
      <template #default="{ row }">
        <div class="upDown center">
          <span
            class="upDown__goodUp"
            :style="{
              color: row.status === 1 ? '#80C269' : '#F3A07E',
              'background-color': row.status === 1 ? '#F5FAF3' : '#FEF7F4',
            }"
            >{{ row.status === 1 ? '已上架' : '已下架' }}</span
          >
        </div>
      </template>
    </m-table-column>
    <m-table-column prop="userName" label="转移专区">
      <template #default="{ row }">
        <el-dropdown trigger="click" placement="bottom-start" @command="transWay($event, row)">
          <span style="display: flex; font-size: 12px">
            {{ currentRegion }}
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu :class="regionList.length > 4 ? 'commandClass' : ''">
              <el-dropdown-item v-for="item in regionList" :key="item.command" :disabled="item.disabled" :command="item.command">
                {{ item.modeName }}
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </template>
    </m-table-column>
    <m-table-column prop="userName" label="操作">
      <template #default="{ row }">
        <div class="center">
          <SetDrop set-name="编辑" :dropdown-list="itemDropList(row)" @set-click="edit(row)" @command="getDropdown($event, row)" />
          <el-dialog v-model="dialogVisible" title="商品码" width="350px">
            <div style="display: flex; justify-content: center">
              <img :src="codeImg" style="width: 180px; height: 180px" />
            </div>
            <template #footer>
              <span class="dialog-footer">
                <el-button @click="dialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="dialogVisible = false">确 定</el-button>
              </span>
            </template>
          </el-dialog>
        </div>
      </template>
    </m-table-column>
  </m-table>
  <el-dialog v-model="memberFlag" width="900px">
    <template #header>
      <div class="digTitle">会员设置</div>
    </template>
    <MemberSet v-if="currentGood" ref="memberSetRef" :current-good="currentGood" :member-flag="memberFlag" />
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="memberFlag = false">取 消</el-button>
        <el-button type="primary" @click="memberSure">确 定</el-button>
      </span>
    </template>
  </el-dialog>
  <el-dialog v-model="rebatesFlag" width="900px">
    <template #header>
      <div class="digTitle">返利设置</div>
    </template>
    <Distribution v-if="currentGood" ref="rebatesSetRef" :current-good="currentGood" :rebates-flag="rebatesFlag" />
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="rebatesFlag = false">取 消</el-button>
        <el-button type="primary" @click="rebatesSure">确 定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped>
.page--top {
  margin-top: 20px;
}

.user__list {
  border-collapse: collapse;

  thead tr td {
    // border-bottom: 1px solid #eee;
    // border-top: 1px solid #eee;
    padding: 10px 5px;
    background-color: #f6f8fa;

    text-align: center;

    &:first-child {
      width: 1%;
    }

    &:nth-child(2) {
      width: 50px;
      text-align: left !important;
    }

    &:last-child {
      width: 90px;
    }
  }

  tbody tr:nth-child(2) td {
    background-color: #ecf6ff;
    height: 40px;
    padding: 5px 5px;
    // border-right: 1px solid #ecf6ff;
    // border-left: 1px solid #ecf6ff;
    font-size: 12px !important;

    .checkItem {
      margin-left: 15px;
    }
  }

  tbody tr:nth-child(3) td {
    // border-bottom: 1px solid #ecf6ff;
    padding: 15px 9px;
    text-align: center;
    height: 75px;
    border-right: 1px solid #ecf6ff;

    &:first-child {
      // border-left: 1px solid #ecf6ff;
    }

    &:nth-child(2) {
      height: 100px;
      display: flex;
      align-items: center;
      flex-wrap: wrap;
      justify-content: center;
    }
  }

  &__good {
    width: 280px;
    display: flex;
    text-align: justify;
    padding-right: 20px;

    img {
      width: 70px;
      height: 70px;
    }

    &--msg {
      margin-left: 10px;
      display: flex;
      flex-wrap: wrap;
      align-content: space-between;

      &--price {
        color: #ff7417;
        margin-top: 10px;
      }
    }
  }

  .stockWarn {
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .upDown {
    display: flex;
    align-items: center;
    justify-content: center;

    &--goodUp {
      display: flex;
      width: 50px;
      height: 30px;
      justify-content: center;
      align-items: center;
      border-radius: 50px;
      color: white;
    }

    &--goodDown {
      margin-left: 10px;
      color: #2d8cf0;
      cursor: pointer;
    }
  }
}

.mouseEnter {
  // background-color: red;
  border: 1px solid #ecf6ff;
}

.mouseEnter:hover {
  // background-color: green;
  border: 1px solid #d7e0e8;
}

.pop--button {
  display: flex;
  justify-content: flex-end;
  margin-right: 10px;
}

.goodList {
  display: flex;
  text-align: justify;

  img {
    width: 70px;
    height: 70px;
  }

  &__msg {
    margin-left: 10px;
    padding: 8px 0px;
    flex: 1;

    &--apply {
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
    }
  }
}

.upDown {
  display: flex;
  align-items: center;
  justify-content: center;

  &__goodUp {
    display: flex;
    width: 50px;
    height: 20px;
    justify-content: center;
    align-items: center;
    border-radius: 4px;
    color: white;
    margin-right: 10px;
  }

  &__goodDown {
    margin-left: 10px;
    color: #2d8cf0;
    cursor: pointer;
  }
}

.commandClass {
  height: 150px;
  overflow: overlay;
}

.center {
  display: flex;
  justify-content: center;
}

.digTitle {
  font-size: 17px;
  font-weight: bold;
}
</style>
