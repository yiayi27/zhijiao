<script setup lang="ts">
import MContent from '@/components/MContainer/MContent.vue'
import PageManage from '@/components/PageManage.vue'

import {
  getAfterNameExport as getAfterName,
  getAfterStatusNameExport as getAfterStatusName,
  getDeliverOrderStatusName,
  isClose,
  strFilter,
} from '../common/order'

interface OrderItem {
  id: string | number
  orderId: string
  status: string
  type?: string
  deliveryType?: string
  itemVoList?: any[]
  note?: string
  privileges?: string[]
  solitaireActivityId?: string | number
  createTime?: string
  userName?: string
  receiverName?: string
  receiverPhone?: string
  payAmount?: number
  deliverySn?: string
  [key: string]: any
}

interface ControlOption {
  label: string
  value: string
}

interface ColumnItem {
  label: string
  width: number
  coustomStyle?: string
}

const props = defineProps({
  /** 表格数据 */
  data: {
    type: Array as () => OrderItem[],
    default: () => [],
  },
  /** 表格内查询按钮 */
  controlOption: {
    type: Array as () => ControlOption[],
    default: () => [],
  },
  /** 查询条件 */
  query: {
    type: Object,
    default: () => ({}),
  },
  /** 父级已选条码 */
  checkedItem: {
    type: Array as () => OrderItem[],
    default: () => [],
  },
})

const emit = defineEmits(['update:query', 'update:checked-item', 'tableFunction'])

/** 本地form 主要用户页码切换 */
const form = computed({
  get: () => props.query,
  set: (v) => {
    emit('update:query', v)
  },
})

/** 已选表格选项 */
const tableCheckedItem = computed({
  get: () => props.checkedItem || [],
  set: (v) => {
    emit('update:checked-item', v)
  },
})

/** 表头 */
const columns: ColumnItem[] = [
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
  // {
  //   label: "地址",
  //   width: 160,
  // },
  {
    label: '交易额',
    width: 114,
  },
  {
    label: '订单状态',
    width: 115,
  },
  {
    label: '操作',
    width: 142,
  },
]

/** 触发父级方法 */
function emitFun(name: string, data?: OrderItem, status?: boolean): void {
  // 阻止未选中元素的批量操作
  if (!data && !props.checkedItem.length) {
    ElMessage.info('请先选择条目')
    return
  }
  emit('tableFunction', name, data, status)
}

/** 物流按钮显示隐藏 */
function logisticsBtnVisible(item: OrderItem): boolean {
  const other = ['WAIT_FOR_PAY', 'WAIT_FOR_SEND', 'COMPLETE']
  return !isClose(item.status) && !other.includes(item.status) && item.deliveryType === 'LOGISTICS'
}
</script>

<template>
  <MContent>
    <template #section>
      <div class="order__control--top">
        <div class="control--l">
          <el-button-group v-if="controlOption" class="fix">
            <el-button plain type="primary" @click="emitFun('send')"> 批量发货 </el-button>
            <el-button v-if="controlOption && controlOption.length" plain class="dropdown__fix more" type="primary">
              <el-dropdown v-if="controlOption" trigger="click" :disabled="!checkedItem.length">
                <span class="dropdown__fix--more">...</span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item v-for="(item, i) in controlOption" :key="i" @click="emitFun(item.value)">
                      {{ item.label }}
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </el-button>
          </el-button-group>

          <!-- <el-dropdown
            v-if="controlOption"
            :disabled="!checkedItem.length"
            trigger="hover"
            split-button
            type="primary"
          >
            <span @click="emitFun('send')" :disabled="!checkedItem.length"
              >批量发货</span
            >
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item
                v-for="(item, i) of controlOption"
                :key="i"
                @click.native="emitFun(item.value)"
                >{{ item.label }}</el-dropdown-item
              >
            </el-dropdown-menu>
          </el-dropdown> -->
        </div>
        <div class="control--r">
          <slot />
        </div>
      </div>
    </template>

    <template #table>
      <m-table
        v-model:checked-item="tableCheckedItem"
        :data="data"
        :selection="true"
        :custom="true"
        :columns="columns"
        :need-hover-border="true"
        multiple-key="itemVoList"
      >
        <template #header="{ row }">
          <div class="order--header" :class="isClose(row.status) && 'order__close'">
            <el-tag v-if="getAfterName(row.type)" class="header__tag"> {{ getAfterName(row.type) }}单 </el-tag>
            <el-tag v-if="row.privileges && row.privileges.includes('5')" class="header__tag"> 优先发货 </el-tag>
            <el-tag v-if="row.solitaireActivityId" class="header__tag" effect="plain" type="danger"> 接龙活动 </el-tag>
            <span style="margin-right: 50px">订单号：{{ row.orderId }}</span>
            <span style="margin-right: 50px"> 创建时间：{{ row.createTime }} </span>
            <span
              ><el-popover placement="bottom-start" width="200" trigger="hover" :disabled="!row.note">
                <div>备注：{{ row.note }}</div>
                <template #reference>
                  <i class="iconfont iconPC-beizhu" :class="{ red: row.note }" @click="emitFun('remark', row)" />
                </template> </el-popover
            ></span>
          </div>
        </template>

        <template #custom-body="{ row }">
          <template v-for="(goods, i) in row.itemVoList" :key="i">
            <tr class="body--content" :class="[{ 'is--multiple': row.itemVoList.length > 1 }]">
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
                    <div v-if="goods.afs" class="goods--specs" style="color: orange">
                      <span class="l">
                        {{ getAfterName(goods.afs.type) }}
                        {{ getAfterStatusName(goods.afs.status) }}
                      </span>
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

              <!-- <td v-if="i === 0"
                  :rowspan="row.itemVoList.length">
                <div>
                  {{ row.receiverName }},{{ row.receiverPhone }}
                  {{ row.receiverProvince }}
                  {{ row.receiverCity }}
                  {{ row.receiverRegion }}
                  {{ row.receiverDetailAddress }}
                </div>
              </td> -->

              <td v-if="i === 0" :rowspan="row.itemVoList.length" class="text--center">￥{{ row.payAmount }}</td>

              <td v-if="i === 0" :rowspan="row.itemVoList.length" class="text--center">
                <template v-if="row.deliveryType === 'SHIPPED'"> 付款成功 </template>

                <template v-else>
                  {{ getDeliverOrderStatusName(row.status) }}
                </template>

                <div v-if="row.status !== 'WAIT_FOR_PAY'" style="margin-top: 5px">
                  <!-- v-if="row.deliveryType==='WAIT_FOR_SEND'" -->
                  <el-button v-if="row.status === 'WAIT_FOR_SEND'" type="primary" style="margin-bottom: 5px" @click="emitFun('send', row, true)">
                    发货
                  </el-button>
                </div>
              </td>

              <td v-if="i === 0" :rowspan="row.itemVoList.length">
                <el-button-group class="fix">
                  <el-button plain type="primary" @click="emitFun('detail', row)"> 查看详情 </el-button>

                  <el-dropdown v-if="row.status === 'WAIT_FOR_PAY' || (logisticsBtnVisible(row) && row.deliverySn)" trigger="hover">
                    <el-button plain class="dropdown__fix more" type="primary">
                      <span class="dropdown__fix--more">...</span>
                    </el-button>
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item v-if="row.status === 'WAIT_FOR_PAY'" @click="emitFun('close', row)"> 关闭订单 </el-dropdown-item>
                        <el-dropdown-item v-if="logisticsBtnVisible(row) && row.deliverySn" @click="emitFun('detail', row, true)">
                          查看物流
                        </el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                </el-button-group>
              </td>
            </tr>
          </template>
        </template>
      </m-table>
    </template>

    <template #pagination>
      <PageManage v-model:page-size="form.size" v-model:page-num="form.current" v-model:total="form.total">
        <template #pagination--left>
          <el-button-group v-if="controlOption" class="fix">
            <el-button plain type="primary" @click="emitFun('send')"> 批量发货 </el-button>
            <el-button v-if="controlOption && controlOption.length" plain class="dropdown__fix more" type="primary">
              <el-dropdown v-if="controlOption" :disabled="!checkedItem.length" trigger="hover">
                <span class="dropdown__fix--more">...</span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item v-for="(item, i) in controlOption" :key="i" @click="emitFun(item.value)">
                      {{ item.label }}
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </el-button>
          </el-button-group>
        </template>
      </PageManage>
    </template>
  </MContent>
</template>

<style lang="scss" scoped>
.header__tag {
  border-radius: 0;
  margin-right: 10px;
}
</style>
