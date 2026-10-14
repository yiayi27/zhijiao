<script setup lang="ts">
import { getIncentiveOrder } from '@/api/finance/index'
import PageManage from '@/components/PageManage.vue'
import { getDeliverOrderStatusName, isClose, strFilter } from '@/views/order/common/order'

const props = defineProps({
  currentData: {
    type: Object,
    default: () => ({}),
  },
})

const orders = ref([])

const form = reactive({
  current: 1,
  size: 10,
  deliverType: '102',
  total: 0,
})

const options = [
  {
    label: '快递订单',
    value: '102',
  },
]

const columns = [
  {
    label: '商品',
    width: 270,
    coustomStyle: 'text-align: left;',
  },
  {
    label: '客户',
    width: 220,
    coustomStyle: 'text-align: left;',
  },
  {
    label: '交易额',
    width: 114,
  },
  {
    label: '订单状态',
    width: 115,
  },
]

function getOrderStatusName(status: string) {
  return getDeliverOrderStatusName(status)
}

function getOrder() {
  getIncentiveOrder(Object.assign({}, props.currentData, form)).then((res: any) => {
    const { list, ...other } = res.data
    Object.assign(form, other)
    orders.value = list
  })
}

watch(
  () => form.deliverType,
  () => {
    getOrder()
  },
)

watch(
  () => form.current,
  () => {
    getOrder()
  },
)

watch(
  () => form.size,
  () => {
    getOrder()
  },
)

onMounted(() => {
  getOrder()
})
</script>

<template>
  <div style="width: 100%">
    <div class="form">
      <el-select v-model="form.deliverType" class="form--select">
        <el-option v-for="item of options" :key="item.value" :value="item.value" :label="item.label" />
      </el-select>
    </div>

    <m-table :data="orders" :selection="false" :custom="true" :columns="columns" :need-hover-border="true" multiple-key="itemVoList">
      <template #header="{ row }">
        <div class="order--header" :class="isClose(row.status) && 'order__close'">
          <span style="margin-right: 50px">订单号：{{ row.orderId }}</span>
          <span style="margin-right: 50px"> 创建时间：{{ row.createTime }} </span>
        </div>
      </template>

      <template #custom-body="{ row }">
        <template v-for="(goods, i) of row.itemVoList" :key="i">
          <tr class="body--content">
            <td>
              <div class="table__goods">
                <div class="table__goods--image">
                  <img :src="goods.productPic" :alt="goods.productName" />
                </div>
                <div class="table__goods--info">
                  <div class="goods--name">
                    {{ strFilter(goods.productName) }}
                  </div>
                  <div class="goods--specs">
                    <span class="l">{{ goods.specs }}</span>
                  </div>
                  <div class="goods--price">
                    <span> ￥{{ goods.productPrice.toFixed(2) }} </span>
                    <span class="specs">
                      <span class="r">×{{ goods.productQuantity }}</span>
                    </span>
                  </div>
                </div>
              </div>
            </td>

            <td v-if="i === 0" :rowspan="row.itemVoList.length">
              <div class="table__user">
                <div class="user--name">
                  <span>{{ row.userName }}</span> <br />
                  ({{ row.receiverName }},{{ row.receiverPhone }})
                </div>
              </div>
            </td>

            <td v-if="i === 0" :rowspan="row.itemVoList.length" class="text--center">￥{{ row.payAmount }}</td>

            <td v-if="i === 0" :rowspan="row.itemVoList.length" class="text--center">
              <template v-if="row.deliveryType === 'SHIPPED'"> 付款成功 </template>
              <template v-else>
                {{ getOrderStatusName(row.status) }}
              </template>
            </td>
          </tr>
        </template>
      </template>
    </m-table>
    <div class="order__control--bottom form" style="width: 100%">
      <PageManage v-model:page-size="form.size" v-model:page-num="form.current" v-model:total="form.total" />
    </div>
  </div>
</template>

<style lang="scss" scoped>
@import '@/assets/styles/order/order.scss';

.form {
  display: flex;
  justify-content: flex-end;
  width: 100%;

  .form--select {
    margin-left: auto;
    margin-bottom: 10px;
  }
}

.fixed {
  position: fixed;
  bottom: 0;
}
</style>
