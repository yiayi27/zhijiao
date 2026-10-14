<script setup lang="ts">
import { integralGoodLogMsg } from '@/api/integral/integral'

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

const logDetail = ref<any[]>([])
const hasLogistics = ref(false)

const acitve = computed(() => {
  return logDetail.value.length - 1
})

// 监听detail变化
watch(
  () => props.detail,
  () => {
    getMsg()
  },
)

// 监听visible变化
watch(
  () => props.visible,
  (newVal) => {
    if (newVal) {
      getMsg()
    }
  },
)

onMounted(() => {
  getMsg()
})

function getMsg() {
  let shipperType = 'sf'
  if (props.detail.deliveryCompany === '顺丰快递') {
    shipperType = 'sf'
  }
  if (props.detail.deliveryCompany === '中通快递') {
    shipperType = 'zt'
  }
  if (props.detail.deliveryCompany === '圆通快递') {
    shipperType = 'yt'
  }
  if (props.detail.deliveryCompany === '韵达快递') {
    shipperType = 'yd'
  }
  if (props.detail.deliveryCompany === '百世快递') {
    shipperType = 'ht'
  }
  const param = {
    shipperType,
    waybillNo: props.detail.deliverySn,
  }
  integralGoodLogMsg(param).then((res) => {
    const result = JSON.parse(res.data)
    if (result.code === 201101) {
      hasLogistics.value = false
    } else {
      hasLogistics.value = true
      logDetail.value = result
    }
  })
}
</script>

<template>
  <div class="digBox">
    <div class="digBox__orderMsg">
      <div style="font-weight: bold">物流信息</div>
      <div class="digBox__orderMsg--line">
        <div style="margin-top: 10px">收货地址：{{ detail.address }}</div>
        <div style="margin-top: 10px">物流公司：{{ detail.deliveryCompany }}</div>
        <div style="margin-top: 10px">物流单号：{{ detail.deliverySn }}</div>
      </div>
    </div>
    <div class="digBox__orderMsg">
      <div v-if="!hasLogistics" class="digBox__orderMsg--orderType">暂无物流详情信息</div>
      <div v-else>
        <span style="font-weight: bold">物流详情</span>
        <el-steps direction="vertical" :active="acitve" style="margin-top: 20px">
          <el-step v-for="(item, i) of logDetail" :key="i">
            <template #icon>
              {{ i + 1 }}
            </template>
            <template #title>
              {{ item.context }}
            </template>
            <template #description>
              {{ item.time }}
            </template>
          </el-step>
        </el-steps>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.digBox {
  padding: 20px;

  &__orderMsg {
    margin-top: 20px;
    color: #8893a7;
    &--orderType {
      background-color: #e9f3fb;
      margin-top: 20px;
      padding: 10px;
      display: flex;
      align-items: center;
      font-weight: bold;
      color: #697992;
    }
    &--line {
      margin-top: 10px;
      padding-bottom: 40px;
      border-bottom: 1px solid #e4e7ed;
    }
  }
}
</style>
