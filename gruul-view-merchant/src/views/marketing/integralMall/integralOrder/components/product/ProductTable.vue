<script setup lang="ts">
import { integralOrderSure, integralRemarkOrder } from '@/api/integral/integral'
import { ElMessageBox } from 'element-plus'
import { cloneDeep } from 'lodash-es'

import DeliveryCom from './DeliveryCom.vue'
import ProductDetail from './ProductDetail.vue'

const props = defineProps({
  proList: {
    type: Array as PropType<Obj[]>,
    default: () => [],
  },
})

const emit = defineEmits(['getProOrder'])

const proListRef = ref()

// 商品订单列表
const proOrderList = ref<Obj[]>([])

// 查看详情
const detailVisible = ref(false)

// 备注详情
const remarkContent = reactive({
  backRemark: '',
  prizeOrderId: '', // 添加缺失的属性
})

// 点击的详情信息
const detailContent = reactive<Obj>({})

// 弹窗状态
const remarkVisible = ref(false)

// 监听proList变化
watch(
  () => props.proList,
  (newVal) => {
    proOrderList.value = cloneDeep(newVal)
  },
)

/**
 * 单个订单信息设置备注
 */
function remarkOrders(row: any) {
  remarkVisible.value = true
  Object.assign(remarkContent, row)
}

function show(row: any) {
  Object.assign(detailContent, row)
  detailVisible.value = true
}

/**
 * 确认收货
 */
function clickSure(row: any) {
  const orderIds: string[] = []
  orderIds.push(row.prizeOrderId)
  integralOrderSure(orderIds).then(() => {
    ElMessage.success('收货成功')
    emit('getProOrder')
  })
}

/**
 * 获取更新后数据
 */
function beforeCloseHandle() {
  detailVisible.value = false
  if (detailContent.prizeOrderStatus === 0) {
    emit('getProOrder')
  }
}

/**
 * 关闭弹窗
 */
function closeHandle() {
  ElMessageBox.confirm('确定退出备注窗口？退出后，备注信息不会保留？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    remarkVisible.value = false
  })
}

/**
 * 弹框确认
 */
function dialogSure() {
  const integralOrders: string[] = []
  integralOrders.push(remarkContent.prizeOrderId)
  integralRemarkOrder({
    backRemark: remarkContent.backRemark,
    isCoverage: true,
    integralOrders,
  }).then((res) => {
    if (res.code === 200) {
      ElMessage.success('备注成功')
      remarkVisible.value = false
      emit('getProOrder')
    }
  })
}

defineExpose({
  proListRef,
})
</script>

<template>
  <div>
    <m-table ref="proListRef" :data="proOrderList" :selection="true">
      <template #header="{ row }">
        <div style="width: 100%; display: flex; align-items: center; justify-content: space-between">
          <div>
            <span style="margin-right: 50px">订单号：{{ row.prizeOrderId }}</span>
            <span style="margin-right: 50px">创建时间：{{ row.createTime }}</span>
          </div>
          <span style="float: right; margin-right: 10px; cursor: pointer">
            <el-popover placement="bottom-start" width="200" trigger="hover" :disabled="!row.note">
              <template #default>
                <div>备注：{{ row.note }}</div>
              </template>
              <template #reference>
                <i
                  class="iconfont iconPC-beizhu"
                  :style="{ color: row.backRemark ? 'red' : '' }"
                  style="font-size: 24px"
                  @click="remarkOrders(row)"
                />
              </template>
            </el-popover>
          </span>
        </div>
      </template>
      <m-table-column v-slot="{ row }" label="商品" width="240" height="80">
        <div class="product">
          <img :src="row.integralGoodsPic" alt="" />
          <span class="product__name">{{ row.integralGoodName }}</span>
        </div>
      </m-table-column>
      <m-table-column v-slot="{ row }" prop="changeCode" label="积分" width="100">
        <div class="table__type">
          <span>{{ row.price }}</span>
        </div>
      </m-table-column>
      <m-table-column v-slot="{ row }" prop="address" label="地址" width="200">
        <div class="table__type">
          <span>{{ row.address }}</span>
        </div>
      </m-table-column>
      <m-table-column v-slot="{ row }" prop="wxuserName" label="买家" width="120">
        <div class="table__type">
          <span>{{ row.wxuserName }}</span>
        </div>
      </m-table-column>
      <m-table-column v-slot="{ row }" label="订单状态">
        <div class="table__type">
          <DeliveryCom :detail="row" @get-pro-order="emit('getProOrder')" />
        </div>
      </m-table-column>
      <m-table-column v-slot="{ row }" label="实付积分">
        <span class="tableCenter">
          <div class="tableCenter__price">
            {{ row.shoppingInfVos[0].integralPrice }}
          </div>
          <div class="tableCenter__price">(含快递：￥{{ row.shippingFee }})</div>
        </span>
      </m-table-column>
      <m-table-column v-slot="{ row }" label="操作">
        <div class="table__type">
          <el-button link @click="show(row)"> 查看详情 </el-button>
          <div v-if="row.prizeOrderStatus === 1" class="table__type--sureBtn" @click="clickSure(row)">确认收货</div>
        </div>
      </m-table-column>
    </m-table>
    <el-dialog v-model="remarkVisible" width="30%" :before-close="closeHandle">
      <template #header>
        <div style="font-weight: bold">备注</div>
      </template>
      <div>
        <div class="">您已选择1笔订单</div>
        <el-input
          v-model="remarkContent.backRemark"
          style="margin-top: 20px"
          type="textarea"
          :autosize="{ minRows: 4, maxRows: 4 }"
          placeholder="请输入备注"
        />
        <div class="digBottom" style="">
          <el-button @click="closeHandle"> 取 消 </el-button>
          <el-button type="primary" @click="dialogSure"> 确 定 </el-button>
        </div>
      </div>
    </el-dialog>
    <el-dialog v-model="detailVisible" width="750px" :before-close="beforeCloseHandle">
      <ProductDetail :detail="detailContent" :visible="detailVisible" />
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
.product {
  display: flex;
  margin-left: 20px;
  img {
    width: 60px;
    height: 60px;
  }
  &__name {
    margin-left: 20px;
    width: 140px;
  }
}

.table__type {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;

  &--sureBtn {
    font-size: 10px;
    color: #f35353;
    cursor: pointer;
  }
}

.digBottom {
  margin-top: 40px;
  display: flex;
  align-content: center;
  justify-content: center;
}

.tableCenter {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-wrap: wrap;

  &__price {
    width: 100px;
    display: flex;
    justify-content: center;
  }
}
</style>
