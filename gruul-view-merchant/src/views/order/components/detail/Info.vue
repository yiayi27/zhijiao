<script setup lang="ts">
import { getArea } from '@/api/global'
import { updateAddress } from '@/api/order'
import { ElMessageBox, ElMessage } from 'element-plus'

import { getDeliveryTypeName, getOrderStatusName, isClose } from '../../common/order'
import type { TableColumn } from '@/components/medusa-core/packages/table/MTable.vue'

export interface OrderDetail {
  id: string
  status: string
  note?: string
  userName?: string
  orderDelivery?: OrderDelivery
  customForm?: string
  privileges?: string
  payType?: string
  createTime?: string
  payTime?: string
  commentTime?: string
  closeTime?: string
  totalAmount?: number
  integrationAmount?: number
  fullScaleAmount?: number
  couponAmount?: number
  memberAmount?: number
  payAmount?: number
  freightAmount?: number
  userOrderDistributionVo?: {
    firstNikeName?: string
    firstPhone?: string
    firstDistributionAmount?: number
  }
  orderItemList?: OrderItem[]
  [key: string]: any
}

interface OrderItem {
  productPic: string
  productName: string
  productPrice: number
  productQuantity: number
  specs?: string
  [key: string]: any
}

interface OrderDelivery {
  receiverName: string
  receiverPhone: string
  receiverProvince: string
  receiverCity: string
  receiverRegion: string
  receiverDetailAddress: string
  receiverPostCode?: string
  deliveryType?: string
  deliveryTime?: string
  receiveTime?: string
  [key: string]: any
}

interface AreaItem {
  value: string | number
  label: string
  localValue?: string
  [key: string]: any
}

interface FormItem {
  key: string
  value: string
  type: string
  time?: string
  [key: string]: any
}

const props = defineProps({
  detail: {
    type: Object as () => OrderDetail,
    default: () => ({}),
  },
  isGroup: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['reset'])

const forms = computed<FormItem[]>(() => {
  if (!props.detail.customForm) {
    return []
  }
  return JSON.parse(props.detail.customForm)
})

const list = computed<OrderDetail[]>(() => {
  return [props.detail]
})

const changeBtnVisible = computed<boolean>(() => {
  return props.detail.status === 'WAIT_FOR_SEND' || props.detail.status === 'WAIT_FOR_PAY'
})

const addressForm = reactive<OrderDelivery>({
  orderId: null,
  receiverDetailAddress: '',
  receiverName: '',
  receiverPhone: '',
  receiverPostCode: '',
  receiverProvince: '',
  receiverCity: '',
  receiverRegion: '',
})

const areaData = reactive({
  receiverProvince: '' as string,
  receiverCity: '' as string,
  receiverRegion: '' as string,
  receiverPostCode: '',
})

const province = ref<AreaItem[]>([])
const city = ref<AreaItem[]>([])
const region = ref<AreaItem[]>([])
const visible = ref<boolean>(false)

const columns: TableColumn[] = [
  {
    label: '商品',
    width: 270,
    coustomStyle: 'text-align: left;',
  },
  {
    label: '单价&数量 ',
  },
  {
    label: '总价',
  },
  {
    label: '配送方式',
  },
  {
    label: '优惠',
  },
  {
    label: '一级佣金',
  },
  {
    label: '实收款',
  },
]

watch(
  () => props.detail,
  (newVal) => {
    if (newVal.orderDelivery) {
      Object.assign(addressForm, newVal.orderDelivery)
      getDefauleAddressData(addressForm)
    }
  },
  { deep: true },
)

function getDefauleAddressData({
  receiverProvince,
  receiverCity,
  receiverRegion,
}: {
  receiverProvince?: string
  receiverCity?: string
  receiverRegion?: string
}): void {
  if (!receiverProvince || !receiverCity || !receiverRegion) return

  onGetArea({ type: 1 }, 'province', (province: AreaItem[]) => {
    const currentProvince = province.find((item) => {
      return item.label === receiverProvince
    })

    if (!currentProvince) return

    onGetArea({ type: 2, id: currentProvince.value }, 'city', (city: AreaItem[]) => {
      const currentCity = city.find((item) => (item.label === receiverCity && receiverCity === '北京市' ? '市辖区' : receiverCity))

      if (!currentCity) return

      onGetArea({ type: 3, id: currentCity.value }, 'region', (_region: AreaItem[]) => {
        // const currentRegion = region.find(item => item.label === receiverRegion)
        Object.assign(areaData, {
          receiverProvince,
          receiverCity,
          receiverRegion,
        })
      })
    })
  })
}

function getVipData(): string {
  if (!props.detail.privileges) return ''
  const map: Record<string, string> = {
    // 0: "自定义权益",
    // 1: "商品权益",
    2: '包邮权益',
    // 3: "积分倍数权益",
    // 4: "专属客服权益",
    // 5: "优先发货",
  }
  let p = ''
  for (const key of props.detail.privileges.split(',')) {
    p += map[key] ? `<p>${map[key]}</p>` : ''
  }
  return p
  //  0-自定义权益 1-商品权益 2-包邮权益 3-积分倍数权益 4-专属客服权益 5-优先发货
}

function getPayType(key?: string): string {
  if (!key) return ''
  return (
    {
      UNPAID: '未支付',
      BALANCE: '余额支付',
      WECHAT: '微信支付',
      ALIPAY: '支付宝支付',
      FRIEND: '好友代付',
    }[key] || ''
  )
}

function onGetArea(form: { type: number; id?: string | number }, key: 'province' | 'city' | 'region', cb?: (data: AreaItem[]) => void): void {
  if (!changeBtnVisible.value) return
  getArea(form).then((res) => {
    if (key === 'province') {
      province.value = res.data.map((item: AreaItem) => {
        item.localValue = item.label
        return item
      })
    } else if (key === 'city') {
      city.value = res.data.map((item: AreaItem) => {
        item.localValue = item.label
        return item
      })
    } else if (key === 'region') {
      region.value = res.data.map((item: AreaItem) => {
        item.localValue = item.label
        return item
      })
    }
    cb?.(res.data)
  })
}

function onUpdateAddress(): void {
  const currentForm = addressForm
  const phoneReg = /^1(3\d|4\d|5\d|6\d|7\d|8\d|9[1,89])\d{8}$/
  if (!phoneReg.test(currentForm.receiverPhone)) {
    ElMessage.warning('请输入正确的手机号码')
    return
  }

  updateAddress({
    ...addressForm,
    orderId: props.detail.id,
    ...areaData,
  }).then(() => {
    ElMessage.success('更新成功')
    emit('reset', props.detail)
    toggleVisible()
  })
}

function lookImage(url: string): void {
  ElMessageBox.alert(`<img src="${url}" style="width:375px" />`, '', {
    dangerouslyUseHTMLString: true,
  })
}

function isDateItem(type: string): boolean {
  return ['date', 'datetime', 'time'].includes(type)
}

function toggleVisible(): void {
  visible.value = !visible.value
}

function provinceChange(v: string): void {
  const currentProvince = province.value.find((item) => {
    return item.label === v
  })
  if (!currentProvince) return

  onGetArea({ type: 2, id: currentProvince.value }, 'city', (list) => {
    if (list.length > 0) {
      areaData.receiverCity = list[0].localValue ?? ''
      cityChange(list[0].localValue ?? '')
    }
  })
}

function cityChange(v: string): void {
  const currentCity = city.value.find((item) => {
    return item.label === v
  })
  if (!currentCity) return

  onGetArea({ type: 3, id: currentCity.value }, 'region', (list) => {
    if (list.length > 0) {
      areaData.receiverRegion = list[0].localValue || ''
    }
  })
}

function regionChange(v: string): void {
  const currentRegion = region.value.find((item) => {
    return item.label === v
  })
  if (!currentRegion) return

  areaData.receiverRegion = currentRegion.localValue || ''
  areaData.receiverPostCode = String(currentRegion.value)
}
</script>

<template>
  <div v-if="Object.keys(detail).length" class="detail">
    <div class="detail__status">
      当前订单状态：{{ getOrderStatusName(detail.status) }}
      <p v-if="detail.note">
        <el-popover placement="bottom-start" width="200" trigger="hover" :disabled="!detail.note">
          <div>备注：{{ detail.note }}</div>
          <template #reference>
            <i class="iconfont iconPC-beizhu" />
          </template>
        </el-popover>
        <span style="margin-left: -5px">{{ detail.note }}</span>
      </p>
      <p v-if="changeBtnVisible">
        <!-- <el-button class="detail__btn--mini" type="primary" size="mini" round
          >发货</el-button
        > -->
        <el-popover v-model:visible="visible" placement="bottom" width="500" trigger="click">
          <el-form v-if="addressForm" :model="addressForm" label-width="90px" style="margin-top: 20px">
            <el-form-item label="收货地址：">
              <el-row class="w-full">
                <el-col :span="8" class="pr-[10px]">
                  <el-select v-model="areaData.receiverProvince" placeholder="请选择" @change="provinceChange">
                    <el-option v-for="item in province" :key="item.value" :label="item.label" :value="item.localValue ?? ''" />
                  </el-select>
                </el-col>
                <el-col :span="8" class="pr-[10px]">
                  <el-select v-model="areaData.receiverCity" placeholder="请选择" @change="cityChange">
                    <el-option v-for="item in city" :key="item.value" :label="item.label" :value="item.localValue ?? ''" />
                  </el-select>
                </el-col>
                <el-col :span="8">
                  <el-select v-model="areaData.receiverRegion" placeholder="请选择" @change="regionChange">
                    <el-option v-for="item in region" :key="item.value" :label="item.label" :value="item.localValue ?? ''" />
                  </el-select>
                </el-col>
                <el-col :span="24" style="margin-top: 10px">
                  <el-input v-model="addressForm.receiverDetailAddress" />
                </el-col>
              </el-row>
              <!-- <el-input v-model="addressForm.name"></el-input> -->
            </el-form-item>
            <el-form-item label="买家姓名：">
              <el-input v-model="addressForm.receiverName" />
            </el-form-item>

            <el-form-item label="手机号码：">
              <el-input v-model="addressForm.receiverPhone" />
            </el-form-item>
          </el-form>
          <div class="form__col text-right">
            <el-button style="margin-left: auto" class="detail__btn--small" round @click="toggleVisible"> 取消 </el-button>
            <el-button type="primary" class="detail__btn--small" round @click="onUpdateAddress"> 确认 </el-button>
          </div>
          <template #reference>
            <el-button class="detail__btn--mini" round> 修改地址 </el-button>
          </template>
        </el-popover>
      </p>
    </div>
    <div class="detail__field">
      <el-row :gutter="10">
        <el-col :span="24">
          <b>买家信息</b>
        </el-col>
        <el-col :span="8"> 微信昵称：{{ detail.userName }} </el-col>
        <template v-if="detail.orderDelivery">
          <el-col :span="8"> 买家姓名：{{ detail.orderDelivery.receiverName }} </el-col>
          <el-col :span="8"> 买家手机号： {{ detail.orderDelivery.receiverPhone }} </el-col>
          <el-col :span="24">
            收货地址：
            {{ detail.orderDelivery.receiverProvince }}
            {{ detail.orderDelivery.receiverCity }}
            {{ detail.orderDelivery.receiverRegion }}
            {{ detail.orderDelivery.receiverDetailAddress }}
          </el-col>
        </template>
        <template v-for="(item, index) of forms">
          <el-col v-if="item.value" :key="index" :span="8">
            {{ item.key }}：
            <template v-if="item.type === 'image'">
              <span class="image--btn" @click="lookImage(item.value)">查看大图</span>
            </template>
            <template v-else-if="isDateItem(item.type)">
              {{ item.time }}
            </template>
            <template v-else>
              {{ item.value }}
            </template>
          </el-col>
        </template>
        <!-- <el-col :span="24"
                v-if=" detail.userNote">
          备注：{{ detail.userNote}}
        </el-col> -->
      </el-row>
      <el-divider />
      <el-row :gutter="10">
        <el-col :span="24">
          <b>订单信息</b>
        </el-col>
        <el-col :span="8"> 订单编号：{{ detail.id }} </el-col>
        <el-col :span="8"> 创建时间：{{ detail.createTime }} </el-col>
        <el-col :span="8"> 支付方式：{{ getPayType(detail.payType) }} </el-col>
        <el-col v-if="detail.payTime" :span="8"> 付款时间：{{ detail.payTime }} </el-col>
        <el-col v-if="detail.orderDelivery && detail.orderDelivery.deliveryTime" :span="8">
          发货时间：{{ detail.orderDelivery.deliveryTime }}
        </el-col>
        <el-col v-if="detail.orderDelivery && detail.orderDelivery.receiveTime" :span="8"> 收货时间：{{ detail.orderDelivery.receiveTime }} </el-col>
        <el-col v-if="detail.commentTime" :span="8"> 评价时间：{{ detail.commentTime }} </el-col>
        <el-col v-if="detail.closeTime" :span="8"> 关闭时间：{{ detail.closeTime }} </el-col>
      </el-row>
      <el-divider />
      <el-row :gutter="10">
        <el-col :span="24">
          <b>分销信息</b>
        </el-col>
        <el-col :span="8"> 一级分销：{{ detail.userOrderDistributionVo?.firstNikeName || '-' }} </el-col>
        <el-col :span="8"> 手机号：{{ detail.userOrderDistributionVo?.firstPhone || '-' }} </el-col>
        <el-col :span="8"> 一级佣金：{{ detail.userOrderDistributionVo?.firstDistributionAmount || '-' }} </el-col>
      </el-row>
    </div>
    <m-table
      :data="list"
      :single="true"
      :custom="true"
      table-header-class="detail__table--header"
      :columns="columns"
      multiple-key="orderItemList"
      style="margin-top: 20px"
    >
      <template #custom-body="{ row }">
        <template v-for="(goods, i) of row.orderItemList" :key="i">
          <tr class="body--content">
            <td>
              <div class="table__goods">
                <div class="table__goods--image">
                  <img :src="goods.productPic" :alt="goods.productName" />
                </div>
                <div class="table__goods--info">
                  <div class="goods--name">
                    {{ goods.productName }}
                  </div>
                  <div class="goods--specs">
                    <span class="l">{{ goods.specs }}</span>
                  </div>
                </div>
              </div>
            </td>

            <td style="border-right: 1px solid #d8eaf9">
              <div>
                <div>单价： ￥{{ goods.productPrice }}</div>
                <div>
                  数量：
                  <span>*{{ goods.productQuantity }}</span>
                </div>
              </div>
            </td>

            <td v-if="i === 0" class="tc" :rowspan="row.orderItemList.length">￥{{ detail.totalAmount }}</td>

            <td v-if="i === 0" class="tc" :rowspan="row.orderItemList.length">
              {{ getDeliveryTypeName(row.orderDelivery.deliveryType) }}
            </td>

            <td v-if="i === 0" class="tc" :rowspan="row.orderItemList.length">
              <p>积分抵扣:{{ detail.integrationAmount || '--' }}</p>
              <p>满减优惠:{{ detail.fullScaleAmount || '--' }}</p>
              <p>优惠券优惠:{{ detail.couponAmount || '--' }}</p>
              <span v-dompurify-html="getVipData()" />
              <p>会员优惠{{ detail.memberAmount || '--' }}</p>
            </td>
            <td v-if="i === 0" class="tc" :rowspan="row.orderItemList.length">
              {{ row.userOrderDistributionVo?.firstNikeName ? row.userOrderDistributionVo.firstDistributionAmount : '-'
              }}{{ row.userOrderDistributionVo?.firstNikeName ? '元' : '' }}
            </td>
            <!-- <td>
              {{ detail.totalAmount }}元
            </td> -->
            <td v-if="i === 0" class="tc" :rowspan="row.orderItemList.length">
              ￥{{ detail.payAmount }} <br />
              <span v-if="detail.privileges && detail.privileges.includes('2')">免配送费</span>
              <span v-else>(含配送:￥{{ detail.freightAmount }})</span>
            </td>
          </tr>
        </template>
      </template>
    </m-table>
    <div class="detail__price">
      {{ detail.status === 'WAIT_FOR_PAY' || isClose(detail.status) ? '待付款' : '实收款' }}
      ： <b>{{ detail.payAmount }}</b> 元
    </div>
  </div>
</template>

<style lang="scss" scoped>
@import '@/assets/styles/order/detail.scss';

.iconPC-beizhu {
  color: red;
  font-size: 22px;
  position: relative;
  bottom: -3px;
  left: -5px;
}

.image--btn {
  cursor: pointer;
  color: rgb(23, 143, 241);
}

.detail__btn--mini {
  padding: 3px 5px;
  margin-top: 10px;
}

.detail__btn--small {
  padding: 10px 18px;
  margin-top: 10px;
}
</style>
