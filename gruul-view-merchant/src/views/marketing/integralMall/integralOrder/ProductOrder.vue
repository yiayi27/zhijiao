<script setup lang="ts">
import { integralOrder, integralRemarkOrder } from '@/api/integral/integral'
import MContent from '@/components/MContainer/MContent.vue'
import PageManage from '@/components/PageManage.vue'
import SetDrop from '@/components/SetDrop/SetDrop.vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'

import { useRouter } from 'vue-router'
import Remark from '../common/Remark.vue'
import ProductTable from './components/product/ProductTable.vue'
import SelectShop from './components/product/SelectShop.vue'

const router = useRouter()

// 数据
const proList = ref<Obj[]>([])
const searchText = ref('')
const integralGoodName = ref('')
const prizeOrderId = ref('')
const wxuserName = ref('')
const prizeOrderStatus = ref('-1')
const pageSize = ref(10)
const pageNum = ref(1)
const total = ref(0)
const searchVal = ref('1')
const someRemark = ref(false)
const selectList = ref<Obj[]>([])
const productTableRef = ref()

// 不同条件搜索
const searchtList = [
  {
    value: '1',
    label: '积分商品名称',
  },
  {
    value: '2',
    label: '积分订单ID',
  },
  {
    value: '3',
    label: '微信昵称',
  },
]

// 下拉选项
const dropdownList = ref([
  {
    text: '批量备注',
    command: '1',
    show: true,
  },
])

onMounted(() => {
  getProOrder()
})

/**
 * 获得积分商品
 */
async function getProOrder() {
  const param = {
    current: pageNum.value,
    size: pageSize.value,
    wxuserName: wxuserName.value,
    orderType: 0,
    integralGoodName: integralGoodName.value,
    prizeOrderId: prizeOrderId.value,
    prizeOrderStatus: prizeOrderStatus.value,
  }
  const res = await integralOrder(param)
  proList.value = res.data.list
  total.value = res.data.total
}

/**
 * 批量选择订单
 */
function remark() {
  if (!productTableRef.value || !productTableRef.value?.proListRef) {
    ElMessage.error('请先选择订单')
    return
  }

  try {
    const list = productTableRef.value?.proListRef?.getCheckedItem()
    selectList.value = list.map((item: Obj) => item.prizeOrderId)

    if (selectList.value.length === 0) {
      ElMessage.error('请先选择备注订单')
      return
    }
    someRemark.value = true
  } catch (error) {
    console.error('获取选中项失败', error)
    ElMessage.error('操作失败')
  }
}

/**
 * 批量备注
 */
function remarkSure(param: Obj) {
  const integralOrders = selectList.value
  integralRemarkOrder({
    integralOrders,
    backRemark: param.backRemark,
    isCoverage: param.isCoverage,
  }).then((res) => {
    if (res.code === 200) {
      ElMessage.success('备注成功')
      someRemark.value = false
      pageNum.value = 1
      getProOrder()
    }
  })
}

/**
 * 关闭弹窗
 */
function closeHandle() {
  ElMessageBox.confirm('确定退出批量备注窗口？退出后，选中订单备注信息不会保留？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    someRemark.value = false
  })
}

/**
 * 批量发货
 */
function command() {
  if (!productTableRef.value || !productTableRef.value?.proListRef) {
    ElMessage.error('请先选择订单')
    return
  }

  try {
    const list = productTableRef.value?.proListRef?.getCheckedItem()
    if (list.length === 0) {
      ElMessage.error('请选择发货订单')
      return
    }

    const orderList: Obj[] = []
    list.forEach((item: Obj) => {
      if (item.prizeOrderStatus === 0) {
        orderList.push(item)
      }
    })

    if (orderList.length === 0) {
      ElMessage.warning('暂无待发货订单')
      return
    }

    // 使用storage存储订单数据
    import('@/libs/storage').then(({ storage }) => {
      storage.set('INTEGRAL_DELIVERY_ORDERS', orderList)
      router.push({
        name: 'DeliverGoods',
      })
    })
  } catch (error) {
    console.error('获取选中项失败', error)
    ElMessage.error('操作失败')
  }
}

/**
 * 搜索功能
 */
function searchBy() {
  pageNum.value = 1
  integralGoodName.value = searchVal.value === '1' ? searchText.value : ''
  prizeOrderId.value = searchVal.value === '2' ? searchText.value : ''
  wxuserName.value = searchVal.value === '3' ? searchText.value : ''
  getProOrder()
}

/**
 * 下拉选择状态
 */
function changeSel(val: string) {
  switch (val) {
    case '0':
      prizeOrderStatus.value = '-1'
      break
    case '1':
      prizeOrderStatus.value = '0'
      break
    case '2':
      prizeOrderStatus.value = '1'
      break
    case '3':
      prizeOrderStatus.value = '2'
      break
  }
  pageNum.value = 1
  getProOrder()
}

/**
 * @method handleSizeChange
 * @description 每页 条
 */
function handleSizeChange(val: number) {
  pageSize.value = val
  pageNum.value = 1
  getProOrder()
}

/**
 * @method handleCurrentChange
 * @description 当前页
 */
function handleCurrentChange(val: number) {
  pageNum.value = val
  getProOrder()
}
</script>

<template>
  <MContent>
    <template #section>
      <div class="ProOrder">
        <div style="display: flex">
          <SetDrop set-name="批量发货" :dropdown-list="dropdownList" @set-click="command" @command="remark" />
          <SelectShop style="margin-left: 20px" @change-sel="changeSel" />
        </div>
        <el-dialog v-model="someRemark" width="30%" :before-close="closeHandle">
          <Remark :select-list="selectList" @remark-sure="remarkSure" />
        </el-dialog>
        <div class="ProOrder__sel">
          <div style="display: flex; margin-left: 40px">
            <el-input
              v-model="searchText"
              class="filter__right--input"
              clearable
              style="width: 320px"
              placeholder="请输入搜索内容"
              @keyup.enter="searchBy"
            >
              <template #prepend>
                <el-select v-model="searchVal" placeholder="搜索条件" style="width: 125px">
                  <el-option v-for="item in searchtList" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
              </template>
              <template #append>
                <el-button :icon="Search" @click="searchBy" />
              </template>
            </el-input>
          </div>
        </div>
      </div>
    </template>
    <template #table>
      <ProductTable ref="productTableRef" :pro-list="proList" @get-pro-order="getProOrder" />
    </template>
    <template #pagination>
      <PageManage
        :page-size="pageSize"
        :page-num="pageNum"
        :total="total"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
      >
        <template #pagination--left>
          <SetDrop set-name="批量发货" :dropdown-list="dropdownList" style="margin-top: 20px" @set-click="command" @command="remark" />
        </template>
      </PageManage>
    </template>
  </MContent>
</template>

<style lang="scss" scoped>
.ProOrder {
  display: flex;
  justify-content: space-between;

  &__sel {
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-weight: bold;
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
}
</style>
