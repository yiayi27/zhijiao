<script setup lang="ts">
import { delIntegralGoods, editIntegralGoodsDetail, getIntegralGoods, upDownIntegralGoods } from '@/api/integral/integral'
import GoodNameEdit from '@/components/GoodNameEdit.vue' // 编辑商品名称
import IntegralCode from '@/components/IntegralCode.vue' // 编辑商品积分价格
import IntegralStock from '@/components/IntegralStock.vue' // 编辑商品库存
import SetDrop from '@/components/SetDrop/SetDrop.vue'
import { Warning } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'

import { useRouter } from 'vue-router'

// 类型定义
interface SearchObj {
  pageNum: number
  pageSize: number
  status: string
  integralGoodName: string
  productSn: string
}

interface CurrentGood {
  id: string
  integralGoodName: string
  integralPrice: number
  inventory: number
  status: number
  productSn: string
  pic: string
  createTime: string
}

// 定义props
const props = defineProps({
  searchObj: {
    type: Object as () => SearchObj,
    required: true,
  },
})

// 定义事件
const emit = defineEmits(['getTotal'])

const router = useRouter()
const productListRef = ref(null)
const proList = ref([])

// 选中商品
const currentGood = ref<CurrentGood>({
  id: '',
  integralGoodName: '',
  integralPrice: 0,
  inventory: 0,
  status: 0,
  productSn: '',
  pic: '',
  createTime: '',
})

// // 配送设置内容
// const rections = reactive({
//   autoShippingState: '', // 设置新增提货点是否自动设置送货上门,0false=禁用,1true=启用
//   goodPointIdsPercentage: '',
// })

// 下拉商品列表
const dropdownShopList = [
  {
    text: '删除',
    command: '1',
    show: true,
    disabled: false,
  },
]

onMounted(() => {
  getList()
})

async function getList() {
  const param = {
    current: props.searchObj.pageNum,
    size: props.searchObj.pageSize,
    status: props.searchObj.status,
    integralGoodName: props.searchObj.integralGoodName,
    productSn: props.searchObj.productSn,
  }
  const res = await getIntegralGoods(param)
  proList.value = res.data.list
  emit('getTotal', res.data.total)
}

// 查看详情
function show(row: any) {
  router.push({
    name: 'IssueGoodAdd',
    query: {
      id: row.id,
    },
  })
}

function getItem(row: CurrentGood) {
  currentGood.value = row
}

// 更新商品名称
async function updateGoodName(newName: string) {
  const param = {
    ...currentGood.value,
    integralGoodName: newName,
  }
  await updateGoodInfo(param)
  if (currentGood.value) {
    currentGood.value.integralGoodName = newName
  }
}

// 更新积分价格
async function updateIntegralPrice(newPrice: number) {
  const param = {
    ...currentGood.value,
    integralPrice: String(newPrice),
  }
  await updateGoodInfo(param)
  if (currentGood.value) {
    currentGood.value.integralPrice = newPrice
  }
}

// 更新积分库存
async function updateGoodStock(newStock: number) {
  const param = {
    ...currentGood.value,
    inventory: String(newStock),
  }
  await updateGoodInfo(param)
  if (currentGood.value) {
    currentGood.value.inventory = newStock
  }
}

// 更新商品信息
async function updateGoodInfo(param: any) {
  const { code } = await editIntegralGoodsDetail(param)
  if (code === 200) {
    ElMessage.success('修改成功')
    return Promise.resolve(true)
  }
  return Promise.reject(false)
}

// 上下架商品
function changeStatus(item: CurrentGood) {
  if (!item.id) return

  const id: string[] = []
  id.push(item.id)
  upDownIntegralGoods(String(item.status === 1 ? 0 : 1), id).then((res) => {
    if (res.code === 200) {
      ElMessage.success(`商品已${item.status === 0 ? '上架' : '下架'}`)
      getList()
    }
  })
}

// 点击的下拉项
function handleCommand(val: string, row: CurrentGood) {
  ElMessageBox.confirm(`确定删除选中商品？删除后无法恢复`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    if (row.id) {
      delIntegralGoods(row.id, {}).then((res) => {
        if (res.code === 200) {
          getList()
          ElMessage.success('删除成功')
        }
      })
    }
  })
}

// 暴露方法给父组件
defineExpose({
  getList,
  productListRef,
})
</script>

<template>
  <div>
    <m-table ref="productListRef" :data="proList" :selection="true">
      <template #header="{ row }">
        <div style="width: 100%">
          <span style="margin-right: 50px">商品ID：{{ row.productSn }}</span>
          <span style="margin-right: 50px">创建时间：{{ row.createTime }}</span>
        </div>
      </template>
      <m-table-column label="商品名" width="250">
        <template #default="{ row }">
          <div class="shopName">
            <img :src="row.pic" :alt="row.integralGoodName" style="width: 60px; height: 60px" />
            <div class="shopName__name">
              <GoodNameEdit :good-name="row.integralGoodName" @change="updateGoodName" @click="getItem(row)" />
            </div>
          </div>
        </template>
      </m-table-column>
      <m-table-column label="积分">
        <template #default="{ row }">
          <div class="middleCenter">
            <IntegralCode :good-price="Number(row.integralPrice)" @change="updateIntegralPrice" @click="getItem(row)" />
          </div>
        </template>
      </m-table-column>
      <m-table-column label="库存">
        <template #default="{ row }">
          <div class="middleCenter">
            <IntegralStock :good-stock="row.inventory" @change="updateGoodStock" @click="getItem(row)" />
            <el-popover placement="top-start" trigger="hover">
              <div v-if="row.inventory === 0">商品已售罄</div>
              <div v-else>&nbsp;</div>
              <template #reference>
                <el-icon v-if="row.inventory === 0" color="#ff0000" class="ml-[5px]">
                  <Warning />
                </el-icon>
                <span v-else class="placeholder-icon"></span>
              </template>
            </el-popover>
          </div>
        </template>
      </m-table-column>
      <m-table-column label="状态">
        <template #default="{ row }">
          <div class="middleCenter">
            <div class="upDown">
              <span
                class="upDown--goodUp"
                :style="{
                  'background-color': row.status === 1 ? '#5CB95C' : '#626C72',
                }"
                >{{ row.status === 1 ? '已上架' : '已下架' }}</span
              >|
              <span class="upDown--goodDown" @click="changeStatus(row)">{{ row.status === 1 ? '下架' : '上架' }}</span>
            </div>
          </div>
        </template>
      </m-table-column>
      <m-table-column width="120" label="操作">
        <template #default="{ row }">
          <div class="middleCenter">
            <SetDrop set-name="编辑" :dropdown-list="dropdownShopList" @set-click="show(row)" @command="handleCommand($event, row)" />
          </div>
        </template>
      </m-table-column>
    </m-table>
  </div>
</template>

<style lang="scss" scoped>
.shopName {
  display: flex;
  width: 100%;

  &__name {
    margin-left: 20px;
    flex-wrap: wrap;
    padding-top: 10px;
  }
}

.middleCenter {
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
    width: 40px;
    height: 20px;
    justify-content: center;
    align-items: center;
    border-radius: 4px;
    color: white;
    margin-right: 10px;
  }

  &--goodDown {
    margin-left: 10px;
    color: #2d8cf0;
    cursor: pointer;
  }
}

.setClassify {
  border: none;
  display: flex;
  justify-content: center;
  align-items: center;

  &__del {
    width: 80px;
    height: 30px;
    // border: 1px solid #e0e0e0;
    display: flex;
    justify-content: center;
    align-items: center;
    // border-top-left-radius: 5px;
    // border-bottom-left-radius: 5px;
    cursor: pointer;
  }

  &__drop {
    width: 40px;
    height: 30px;
    border: 1px solid #e0e0e0;
    display: flex;
    justify-content: center;
    align-items: center;
    border-top-right-radius: 5px;
    border-bottom-right-radius: 5px;
    cursor: pointer;
  }
}
</style>
