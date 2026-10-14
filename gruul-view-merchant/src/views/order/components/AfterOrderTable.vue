<script setup lang="ts">
import { getAfterName, getAfterStatusName } from '../common/afterSale'
import { getDeliveryTypeName, getOrderStatusName, isClose, strFilter } from '../common/order'
import { ElMessage } from 'element-plus'

export interface TableItem {
  status: string
  type: string
  no: string
  createTime: string
  note?: string
  productPic: string
  productName: string
  specs: string
  productPrice: number
  productQuantity: number
  userName: string
  receiverName: string
  receiverPhone: string
  receiverProvince: string
  receiverCity: string
  receiverRegion: string
  receiverDetailAddress: string
  deliveryType: string
  refundAmount: number
  orderStatus: string
  id: string
  orderId: string
  close?: boolean
  [key: string]: any
}

const props = defineProps({
  /** 表格数据 */
  data: {
    type: Array as () => TableItem[],
    default: () => [],
  },
  /** 查询条件 */
  query: {
    type: Object,
    default: () => ({}),
  },
  /** 父级已选条码 */
  checkedItem: {
    type: Array,
    default: () => [],
  },
})

const emit = defineEmits(['update:query', 'update:checked-item', 'tableFunction'])

/** 已选表格选项 */
const tableCheckedItem = computed({
  get: () => props.checkedItem || [],
  set: (v) => {
    emit('update:checked-item', v)
  },
})

/** 表头 */
const columns = computed(() => {
  const list = [
    {
      label: '商品',
      width: 200,
      coustomStyle: 'text-align: left;',
    },
    {
      label: '客户',
      width: 200,
      coustomStyle: 'text-align: left;',
    },
    {
      label: '地址',
      width: 112,
    },
    {
      label: '交易额',
      width: 94,
    },
    {
      label: '订单状态',
      width: 115,
    },
    {
      label: '操作',
      width: 140,
    },
  ]
  return list
})

/** 触发父级方法 */
function emitFun(name: string, data?: TableItem | null, ignore: boolean = false): void {
  // 阻止未选中元素的批量操作
  if (!data && !props.checkedItem.length && !ignore) {
    ElMessage.info('请先选择条目')
    return
  }
  emit('tableFunction', name, data)
}

// 导出函数以供模板使用
const getOrderStatusNameExport = getOrderStatusName
const getDeliveryTypeNameExport = getDeliveryTypeName
const getAfterStatusNameExport = getAfterStatusName
const getAfterNameExport = getAfterName
</script>

<template>
  <m-table v-model:checked-item="tableCheckedItem" :data="data" :selection="true" :custom="true" :columns="columns" :need-hover-border="true">
    <template #header="{ row }">
      <div class="order--header" :class="isClose(row.status) && 'order__close'">
        <el-tag class="header__tag"> {{ getAfterNameExport(row.type) }}单 </el-tag>
        <span style="margin-right: 50px">订单号：{{ row.no }}</span>
        <span style="margin-right: 50px"> 创建时间：{{ row.createTime }} </span
        ><span>
          <el-popover placement="bottom-start" width="200" trigger="hover" :disabled="!row.note">
            <div>备注：{{ row.note }}</div>
            <template #reference>
              <i class="iconfont iconPC-beizhu" :class="{ red: row.note }" @click="emitFun('remark', row)" />
            </template>
          </el-popover>
        </span>
      </div>
    </template>

    <template #custom-body="{ row }">
      <tr class="body--content">
        <td>
          <div class="table__goods">
            <div class="table__goods--image">
              <img :src="row.productPic" :alt="row.productName" />
            </div>
            <div class="table__goods--info">
              <div class="goods--name">
                {{ strFilter(row.productName) }}
              </div>
              <div class="goods--specs">
                <span class="l">{{ row.specs }}</span>
              </div>
              <div class="goods--price">
                <span> ￥{{ row.productPrice.toFixed(2) }} </span>
                <span class="specs">
                  <span class="r">×{{ row.productQuantity }}</span>
                </span>
              </div>
            </div>
          </div>
        </td>

        <td>
          <div class="table__user">
            <div class="user--name">
              <span>{{ row.userName }}</span> <br />
              ({{ row.receiverName }},{{ row.receiverPhone }})
            </div>
          </div>
        </td>

        <td>
          <div>
            {{ getDeliveryTypeNameExport(row.deliveryType) }}， {{ row.receiverName }},{{ row.receiverPhone }}
            {{ row.receiverProvince }}
            {{ row.receiverCity }}
            {{ row.receiverRegion }}
            {{ row.receiverDetailAddress }}
          </div>
        </td>

        <td class="text--center">￥{{ row.refundAmount }}</td>

        <td class="text--center">
          <p class="after__status">
            {{ getOrderStatusNameExport(row.orderStatus) }}
          </p>
          <p>{{ getAfterNameExport(row.type) }} {{ getAfterStatusNameExport(row.status) }}</p>
        </td>

        <td>
          <template v-if="row.status === 'WAIT_FOR_BUSINESS_APPROVED'">
            <el-button-group class="fix">
              <el-button plain type="primary" @click="emitFun('review', row)"> 审核 </el-button>
              <el-button plain class="dropdown__fix more" type="primary">
                <el-dropdown trigger="hover">
                  <span class="dropdown__fix--more">...</span>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item @click="emitFun('detail', row)"> 查看详情 </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </el-button>
            </el-button-group>
          </template>
          <template v-else>
            <el-button plain type="primary" @click="emitFun('detail', row)"> 查看详情 </el-button>
          </template>
        </td>
      </tr>
    </template>
  </m-table>
</template>

<style scoped>
.header__tag {
  border-radius: 0;
  margin-right: 10px;
}

.after__status {
  color: orange;
  margin-top: 10px;
}
</style>
