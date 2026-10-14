<script setup lang="ts">
import { getdeliveryInfo } from '@/api/order/index'

interface DeliveryInfo {
  deliverySn?: string
  deliveryCompany?: string
  deliveryCode?: string
  receiverPhone?: string
  receiverProvince?: string
  receiverCity?: string
  receiverRegion?: string
  receiverDetailAddress?: string
  [key: string]: any
}

interface DeliveryDetailItem {
  context: string
  time: string
  [key: string]: any
}

const props = defineProps({
  deliveryInfo: {
    type: Object as () => DeliveryInfo,
    default: () => ({}),
  },
})

const info = ref<DeliveryDetailItem[]>([])

const acitve = computed<number>(() => {
  return 0
})

function getDetail(v: DeliveryInfo): void {
  if (!v) return

  getdeliveryInfo({
    tracesId: v.deliverySn,
    companyName: v.deliveryCompany,
    deliveryCode: v.deliveryCode,
    mobile: v.receiverPhone,
  })
    .then((res) => {
      info.value = res.data || []
    })
    .catch(() => {
      info.value = []
    })
}

watch(
  () => props.deliveryInfo,
  (v) => {
    if (v) {
      getDetail(v)
    } else {
      info.value = []
    }
  },
  { immediate: true },
)

onMounted(() => {
  getDetail(props.deliveryInfo)
})
</script>

<template>
  <div class="delivery">
    <el-row :gutter="10" class="delivery__field">
      <el-col :span="24">
        <b>物流信息</b>
      </el-col>
      <el-col :span="24">
        收货地址： {{ deliveryInfo.receiverProvince }}
        {{ deliveryInfo.receiverCity }}
        {{ deliveryInfo.receiverRegion }}
        {{ deliveryInfo.receiverDetailAddress }}
      </el-col>
      <el-col :span="24"> 物流公司：{{ deliveryInfo.deliveryCompany }} </el-col>
      <el-col :span="24"> 物流单号：{{ deliveryInfo.deliverySn }} </el-col>
    </el-row>
    <div class="delivery__detail">
      <b>物流详情</b>
      <div v-if="!info.length" class="empty">暂无物流详情信息</div>
      <el-steps v-else direction="vertical" :active="acitve">
        <el-step v-for="(item, i) in info" :key="i">
          <template #icon>
            {{ info.length - i }}
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
</template>

<style lang="scss" scoped>
.delivery {
  &__field {
    font-size: 13px;
    border-bottom: 1px solid #ccc;

    .el-col-24 {
      margin-bottom: 10px;
    }
  }

  &__detail {
    margin-top: 15px;

    .el-steps {
      margin-top: 10px;
    }

    .el-step {
      min-height: 80px;
    }

    .empty {
      width: 100%;
      height: 34px;
      line-height: 34px;
      background-color: rgba(233, 243, 251, 1);
      box-shadow: none;
      color: #586884;
      text-align: left;
      margin-top: 10px;
      padding-left: 10px;
    }
  }
}
</style>
