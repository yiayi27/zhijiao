<script setup lang="ts">
import { sendintegralGoodByPrint } from '@/api/integral/integral'

import SendDeal from './dialog/SendDeal.vue'

const Delivery = defineAsyncComponent(() => import('./dialog/Delivery.vue'))
const props = defineProps({
  detail: {
    type: Object,
    default: () => ({}),
  },
})

const emit = defineEmits(['getProOrder'])

// 发货弹窗
const sendVisible = ref(false)
// 物流进度
const logVisible = ref(false)
const nextDetail = reactive({})
const sendDealRef = ref()

/**
 * 单个发货
 */
function deliverGood() {
  sendVisible.value = true
}

function closeHandle() {
  sendVisible.value = false
  if (sendDealRef.value) {
    sendDealRef.value.form = {
      deliverCode: '',
      expressId: '',
      orderIds: [],
      printCode: '',
      shopId: '',
    }
  }
}

/**
 * 确认发货
 */
function sendIntegral() {
  if (!sendDealRef.value) {
    ElMessage.error('组件未初始化')
    return
  }

  if (!sendDealRef.value.form.deliverCode || !sendDealRef.value.form.printCode || !sendDealRef.value.form.expressId) {
    ElMessage.error('请填写物流信息')
    return
  }

  // 添加类型断言
  const form = sendDealRef.value.form
  form.isIntegralOrder = -1
  form.orderIds.push(props.detail.prizeOrderId)

  sendintegralGoodByPrint(form).then(() => {
    ElMessage.success('发货成功')
    sendVisible.value = false
    emit('getProOrder')
  })
}

/**
 * 查看物流信息
 */
function showDeliverMsg() {
  Object.assign(nextDetail, JSON.parse(JSON.stringify(props.detail)))
  logVisible.value = true
}
</script>

<template>
  <div>
    <div v-if="detail.prizeOrderStatus === 0">
      <div class="tableCenter">待发货</div>
      <el-button type="primary" size="small" class="bottom" @click="deliverGood"> 发货 </el-button>
      <el-dialog v-model="sendVisible" width="650px" :before-close="closeHandle">
        <template #header>
          <span class="diaTitle">商品发货</span>
        </template>
        <SendDeal ref="sendDealRef" :detail="detail" :visible="sendVisible" />
        <template #footer>
          <span class="diaFooter">
            <el-button @click="closeHandle">取消</el-button>
            <el-button type="primary" @click="sendIntegral">确认</el-button>
          </span>
        </template>
      </el-dialog>
    </div>
    <div v-if="detail.prizeOrderStatus === 1">
      <div class="tableCenter">卖家已发货</div>
      <el-button type="primary" size="small" class="bottom" @click="showDeliverMsg"> 查看物流 </el-button>
      <el-dialog v-model="logVisible" width="30%">
        <Delivery :detail="nextDetail" />
      </el-dialog>
    </div>
    <span v-if="detail.prizeOrderStatus === 2">交易成功</span>
  </div>
</template>

<style lang="scss" scoped>
.diaTitle {
  font-weight: bold;
  font-size: 18px;
}

.diaFooter {
  margin-right: 40px;
}

.bottom {
  margin-top: 5px;
}

.tableCenter {
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
