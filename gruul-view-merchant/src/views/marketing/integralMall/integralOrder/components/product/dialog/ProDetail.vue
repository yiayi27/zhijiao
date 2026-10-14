<script setup lang="ts">
import { integralOrderById, updateIntegralOrderById } from '@/api/integral/integral'
import address from '@/views/goods/common/city.js'

interface AddressItem {
  value: string | number
  label: string
}

interface OrderDetail {
  prizeOrderId?: string | number
  prizeOrderStatus?: number
  integralName?: string
  allAddress?: string
  address?: string
  provinceCode?: string
  cityCode?: string
  areaCode?: string
  receiverName?: string
  telPhone?: string
  buyersNote?: string
  backRemark?: string
  [key: string]: any
}

const props = defineProps({
  detail: {
    type: Object,
    default: () => ({}),
  },
  visible: {
    type: Boolean,
    default: false,
  },
})

const popVisiable = ref(false)
const orderDetail = reactive<OrderDetail>({})
const popDetail = reactive<OrderDetail>({}) // 修改地址对象
const addressProv = ref<AddressItem[]>(address.getProvince() as AddressItem[]) // 省数组
const addressCity = ref<AddressItem[]>([]) // 市数组
const addressArea = ref<AddressItem[]>([]) // 区数组
const row = reactive({ note: '' }) // 模板中使用的row对象

// 监听visible变化
watch(
  () => props.visible,
  (newVal) => {
    if (newVal) {
      getDetail()
    }
  },
)

onMounted(() => {
  getDetail()
})

function getDetail() {
  const param = {
    orderId: props.detail.prizeOrderId,
  }
  integralOrderById(param).then((res) => {
    Object.assign(orderDetail, res.data)
    orderDetail.integralName = res.data.shoppingInfVos[0].integralGoodName
    orderDetail.allAddress = res.data.address
    orderDetail.address = res.data.address.split(' ')[3]
    addressCity.value = address.getCity(props.detail.provinceCode) as AddressItem[]
    addressArea.value = address.getArea(props.detail.provinceCode, props.detail.cityCode) as AddressItem[]
    Object.assign(popDetail, JSON.parse(JSON.stringify(orderDetail)))
  })
}

/**
 * 选择省市区
 */
function selectChang(value: string | number, type: string): void {
  if (type === 'prov' && value) {
    addressCity.value = address.getCity(value) as AddressItem[]
    addressArea.value = []
    popDetail.cityCode = ''
    popDetail.areaCode = ''
  }
  if (type === 'city' && value) {
    addressArea.value = address.getArea(popDetail.provinceCode || '', value) as AddressItem[]
    popDetail.areaCode = ''
  }
}

/**
 * 获取省市区所有数据
 */
function getAddressName(list: AddressItem[], value: string | number): string {
  let name = ''
  if (list.length > 0) {
    for (const item of list) {
      if (item.value === value) {
        name = item.label
        break
      }
    }
  }
  return name
}

/**
 * 修改地址后保存
 */
function sureChange() {
  const province = getAddressName(addressProv.value, popDetail.provinceCode || '')
  const city = getAddressName(addressCity.value, popDetail.cityCode || '')
  const country = getAddressName(addressArea.value, popDetail.areaCode || '')
  const phoneReg = /^1([3-9])\d{9}$/

  if (!popDetail.telPhone || !phoneReg.test(popDetail.telPhone)) {
    ElMessage.error('请输入正确手机号码')
    return false
  }

  const param = {
    prizeOrderId: popDetail.prizeOrderId,
    provinceCode: popDetail.provinceCode,
    cityCode: popDetail.cityCode,
    areaCode: popDetail.areaCode,
    receiverName: popDetail.receiverName,
    telPhone: popDetail.telPhone,
    buyersNote: popDetail.buyersNote,
    address: `${province} ${city} ${country} ${popDetail.address}`,
  }
  updateIntegralOrderById(param).then(() => {
    popVisiable.value = false
    ElMessage.success('修改成功')
    getDetail()
  })
}
</script>

<template>
  <div class="digBox">
    <div class="digBox__orderType">
      <div style="width: 400px">
        当前订单状态：
        <span v-if="orderDetail.prizeOrderStatus === 0">待发货</span>
        <span v-if="orderDetail.prizeOrderStatus === 1">卖家已发货</span>
        <span v-if="orderDetail.prizeOrderStatus === 2">交易成功</span>
      </div>
      <div v-if="orderDetail.backRemark" class="orderRemark">
        <el-popover placement="bottom-start" width="200" trigger="hover" :disabled="!row.note">
          <template #default>
            <div>备注：{{ row.note }}</div>
          </template>
          <template #reference>
            <i class="iconfont iconPC-beizhu" style="font-size: 20px" />
          </template>
        </el-popover>

        {{ orderDetail.backRemark }}
      </div>
      <div v-if="orderDetail.prizeOrderStatus === 0" class="digBox__orderType--send">
        <el-popover :visible="popVisiable" placement="bottom-start" width="500" trigger="click">
          <template #default>
            <div style="display: flex; margin-top: 10px">
              <div style="width: 100px">收货地址：</div>
              <div class="line__input" style="width: 100%; flex-wrap: wrap">
                <div style="width: 100%; display: flex">
                  <el-select
                    v-model="popDetail.provinceCode"
                    placeholder="省份选择"
                    style="margin: 0px 10px 0px 0px"
                    @change="(event: string | number) => selectChang(event, 'prov')"
                  >
                    <el-option v-for="item in addressProv" :key="item.value" :value="item.value" :label="item.label">
                      {{ item.label }}
                    </el-option>
                  </el-select>
                  <el-select
                    v-model="popDetail.cityCode"
                    placeholder="城市选择"
                    not-found-text="请先选择省份"
                    style="margin-right: 10px"
                    @change="(event: string | number) => selectChang(event, 'city')"
                  >
                    <el-option v-for="item in addressCity" :key="item.value" :value="item.value" :label="item.label">
                      {{ item.label }}
                    </el-option>
                  </el-select>
                  <el-select
                    v-model="popDetail.areaCode"
                    placeholder="区县选择"
                    not-found-text="请先选择城市"
                    @change="(event: string | number) => selectChang(event, 'area')"
                  >
                    <el-option v-for="item in addressArea" :key="item.value" :value="item.value" :label="item.label">
                      {{ item.label }}
                    </el-option>
                  </el-select>
                </div>
                <div style="width: 100%; margin-top: 10px">
                  <el-input v-model="popDetail.address" />
                </div>
              </div>
            </div>
            <div class="line">
              <div class="line__input">买家姓名：</div>
              <el-input v-model="popDetail.receiverName" />
            </div>
            <div class="line">
              <div class="line__input">手机号码：</div>
              <el-input v-model="popDetail.telPhone" maxlength="11" />
            </div>
            <div class="lineBottom">
              <el-button @click="popVisiable = false"> 取 消 </el-button>
              <el-button type="primary" @click="sureChange"> 确 认 </el-button>
            </div>
          </template>
          <template #reference>
            <div class="modefyAdd" @click="popVisiable = true">修改收货地址</div>
          </template>
        </el-popover>
      </div>
    </div>
    <div class="digBox__orderMsg">
      <div style="font-weight: bold">买家信息</div>
      <div class="digBox__orderMsg--line">
        <div>微信昵称：{{ orderDetail.wxuserName }}</div>
        <div>买家昵称：{{ orderDetail.receiverName }}</div>
        <div>买家手机号：{{ orderDetail.telPhone }}</div>
      </div>
      <div class="digBox__orderMsg--lineTwo">
        <div>收货地址：{{ orderDetail.allAddress }}</div>
      </div>
    </div>
    <div class="digBox__orderMsgOrd">
      <div style="font-weight: bold">订单信息</div>
      <div class="digBox__orderMsgOrd--line">
        <div>订单编号：{{ orderDetail.prizeOrderId }}</div>
        <div>创建时间：{{ orderDetail.createTime }}</div>
        <div>
          支付方式：
          <span v-if="orderDetail.payType === 100">余额支付</span>
          <span v-else-if="orderDetail.payType === 101">微信支付</span>
          <span v-else>其他支付</span>
        </div>
      </div>
      <div class="digBox__orderMsgOrd--lineTwo">
        <div v-if="orderDetail.sendTime" style="margin-right: 60px">发货时间：{{ orderDetail.sendTime }}</div>
        <div v-if="orderDetail.receiveTime">收货时间：{{ orderDetail.receiveTime }}</div>
      </div>
    </div>
    <div class="digBox__orderLeaveMsg">
      <div style="font-weight: bold">买家留言：{{ orderDetail.buyersNote }}</div>
    </div>
    <div class="digBox__good">
      <div class="digBox__good--title">
        <div style="width: 330px; margin-left: 20px">商品名称</div>
        <div style="width: 100px">数量</div>
        <div style="width: 100px">积分价</div>
        <div style="width: 100px">配送方式</div>
        <div style="width: 100px">运费</div>
        <div style="width: 100px">实收款</div>
      </div>
      <div class="digBox__good--content">
        <div class="digBox__good--content--box" style="width: 350px">
          <img :src="orderDetail.integralGoodsPic" alt="" style="width: 80px; height: 80px" />
          <div style="margin-left: 20px">
            {{ orderDetail.integralName }}
          </div>
        </div>
        <div class="digBox__good--content--box" style="width: 100px">1</div>
        <div class="digBox__good--content--box" style="width: 100px">
          <div>{{ orderDetail.price }}</div>
        </div>
        <div class="digBox__good--content--box" style="width: 100px">
          {{ orderDetail.distriMode === 0 ? '物流配送' : '' }}
        </div>
        <div class="digBox__good--content--box" style="width: 100px">￥{{ orderDetail.shippingFee === 0 ? 0.01 : orderDetail.shippingFee }}</div>
        <div class="digBox__good--content--price">
          <div>{{ orderDetail.expendIntegral }}</div>
          <div>(含快递：￥{{ orderDetail.shippingFee === 0 ? 0.01 : orderDetail.shippingFee }})</div>
        </div>
      </div>
    </div>
    <div class="digBox__bottom">
      <div class="digBox__bottom--title">实收款：</div>
      <div style="font-size: 18px; color: #56a5e2">￥{{ orderDetail.shippingFee === 0 ? 0.01 : orderDetail.shippingFee }}</div>
      <div class="digBox__bottom--title">元</div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.digBox {
  padding: 20px;
  &__orderType {
    border: 1px solid #2d8cf0;
    background-color: #e9f3fb;
    padding: 10px;
    display: flex;
    align-items: center;
    font-weight: bold;
    color: #697992;
    flex-wrap: wrap;

    .orderRemark {
      width: 100%;
      padding: 5px 10px;
      background-color: white;
      font-weight: normal;
      margin-top: 20px;
      font-size: 14px;
      display: flex;
      align-items: center;
    }

    &--send {
      margin-top: 20px;
      display: flex;
      width: 400px;
      .sendBtn {
        width: 46px;
        height: 24px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 12px;
        color: white;
        background-color: #288ff8;
        font-weight: normal;
        cursor: pointer;
      }

      .modefyAdd {
        width: 100px;
        height: 24px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 12px;
        cursor: pointer;
        font-weight: normal;
        background-color: white;
      }
    }
  }
  &__orderMsg {
    margin-top: 20px;
    color: #8893a7;
    &--line {
      margin-top: 10px;
      display: flex;
      justify-content: space-between;
    }
    &--lineTwo {
      margin-top: 10px;
      display: flex;
    }
  }

  &__orderMsgOrd {
    margin-top: 40px;
    border-top: 1px solid #e4e7ed;
    padding-top: 30px;
    color: #8893a7;
    &--line {
      margin-top: 10px;
      display: flex;
      justify-content: space-between;
    }
    &--lineTwo {
      margin-top: 10px;
      display: flex;
    }
  }

  &__orderLeaveMsg {
    margin: 20px 0px;
    border-top: 1px solid #e4e7ed;
    border-bottom: 1px solid #e4e7ed;
    padding: 10px 0px;
    color: #8893a7;
  }

  &__good {
    margin-top: 15px;
    &--title {
      background-color: #d2ecff;
      padding: 10px 0px;
      display: flex;
      align-items: center;
    }
    &--content {
      display: flex;
      &--box {
        height: 100px;
        display: flex;
        align-items: center;
        border: 1px solid #e9f3fb;
        border-right: none;
        padding-left: 10px;
      }
      &--price {
        height: 100px;
        display: flex;
        width: 15%;
        align-items: center;
        border: 1px solid #e9f3fb;
        padding-left: 10px;
      }
    }
  }

  &__bottom {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
    align-items: center;
    &--title {
      font-weight: bold;
      color: #697992;
      margin-left: 10px;
    }
  }
}
.line {
  display: flex;
  align-items: center;
  margin-top: 10px;

  .line__input {
    display: flex;
    width: 100px;
    align-items: center;
  }
}

.lineBottom {
  display: flex;
  align-items: center;
  margin-top: 20px;
  justify-content: flex-end;
  .lineBottom__sure {
    width: 46px;
    height: 24px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 12px;
    color: white;
    background-color: #288ff8;
    font-weight: normal;
    border-radius: 5px;
    cursor: pointer;
    margin-left: 10px;
  }

  .lineBottom__cancel {
    width: 46px;
    height: 24px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 12px;
    cursor: pointer;
    font-weight: normal;
    background-color: white;
    margin-left: 20px;
    border: 1px solid #dcdfe6;
    border-radius: 5px;
  }
}
</style>
