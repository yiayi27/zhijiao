<script setup lang="ts">
import { agree, disagree, getAfterDetail, getNegotiateList } from '@/api/order/afterSale'
import { ElMessageBox } from 'element-plus'

import {
  getAfterName,
  getAfterStatusName,
  isExchange,
  isReturnAndrefund,
  isWaitConfirm,
  isWaitReview,
  isWaitSendRefund,
} from '../../common/afterSale'

interface AfterInfo {
  id: string | number
  orderId: string | number
  type?: string
  status?: string
  deadline?: string
  item?: any
  [key: string]: any
}

interface NegotiateItem {
  id: string | number
  image?: string[]
  userName?: string
  updateTime?: string
  info?: string
  userAvatarUrl?: string
  [key: string]: any
}

const props = defineProps({
  detail: {
    type: Object,
    default: () => ({}),
  },
  afterInfo: {
    type: Object,
    default: () => ({}),
  },
})

const emit = defineEmits(['reset'])

const negotiateList = ref<NegotiateItem[]>([])
const afterDetail = ref<AfterInfo>({
  id: '',
  orderId: '',
})
const loading = ref(false)
const contain = ref<'fill' | 'contain' | 'cover' | 'none' | 'scale-down'>('contain')

watch(
  () => props.afterInfo as AfterInfo,
  ({ id, orderId }) => {
    if (!id) return

    getAfterDetail(String(id)).then((res) => {
      afterDetail.value = res.data
    })

    getNegotiateList(String(orderId)).then((res) => {
      res.data.forEach((item: NegotiateItem) => {
        if (item.image && typeof item.image === 'string') {
          item.image = (item.image as string).split(',')
        }
      })
      negotiateList.value = res.data
    })
  },
  { immediate: true },
)

function setLoading(status: boolean): void {
  loading.value = status
}

function onAgree(): void {
  setLoading(true)
  agree(props.afterInfo.id)
    .then(() => {
      emit('reset', afterDetail.value.item)
    })
    .finally(() => setLoading(false))
}

function send(): void {
  disagree({
    afsId: afterDetail.value.id,
    refusalReason: '已发货',
  }).then(() => {
    emit('reset', afterDetail.value.item)
  })
}

function onDisagree(): void {
  ElMessageBox.prompt('拒绝原因', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputType: 'textarea',
    inputPattern: /^\s*\S[\s\S]*$/,
    inputErrorMessage: '请输入拒绝原因',
  }).then(({ value }) => {
    setLoading(true)
    disagree({
      afsId: afterDetail.value.id,
      refusalReason: value,
    })
      .then(() => {
        emit('reset', afterDetail.value.item)
      })
      .finally(() => setLoading(false))
  })
}

function checkTime(i: number): number {
  if (i < 10) {
    return Number(`0${i}`)
  }
  return i
}

function computedResidualTime(time: string): string {
  const ts = new Date(time).getTime() - new Date().getTime() // 计算剩余的毫秒数
  const dd = checkTime(Number.parseInt(String(ts / 1000 / 60 / 60 / 24), 10)) // 计算剩余的天数
  const hh = checkTime(Number.parseInt(String((ts / 1000 / 60 / 60) % 24), 10)) // 计算剩余的小时数
  const mm = checkTime(Number.parseInt(String((ts / 1000 / 60) % 60), 10)) // 计算剩余的分钟数
  const ss = checkTime(Number.parseInt(String((ts / 1000) % 60), 10)) // 计算剩余的秒数

  return `${dd}天${hh}时${mm}分${ss}秒`
}
</script>

<template>
  <div v-if="afterDetail" class="after">
    <div class="after__status">
      <div class="flex">
        <div>
          当前订单状态:{{ getAfterName(afterDetail.type || '') }}
          {{ getAfterStatusName(afterDetail.status || '') }}
        </div>

        <div v-if="afterDetail.deadline">剩余 {{ computedResidualTime(afterDetail.deadline) }}</div>
      </div>

      <template v-if="afterDetail.status === 'WAIT_FOR_BUSINESS_APPROVED'">
        <div class="tips mt-[10px]">如您逾期未响应申请，视作同意买家申请，系统会自动退款给买家</div>
      </template>

      <!-- <div>无售后记录</div> -->
    </div>
    <div class="after__btns">
      <template v-if="isWaitSendRefund(afterDetail.type || '', afterDetail.status || '', detail.status)">
        <el-button :disabled="loading" type="primary" @click="send"> 发货 </el-button>
        <el-button :disabled="loading" @click="onAgree"> 同意退款 </el-button>
      </template>

      <template v-else-if="isWaitReview(afterDetail.type || '', afterDetail.status || '', detail.status)">
        <el-button :disabled="loading" type="primary" @click="onDisagree"> 拒绝退款 </el-button>
        <el-button :disabled="loading" @click="onAgree"> 同意退款 </el-button>
      </template>

      <template v-else-if="isExchange(afterDetail.type || '', afterDetail.status || '')">
        <el-button :disabled="loading" type="primary" @click="onAgree"> 同意换货 </el-button>
        <el-button :disabled="loading" @click="onDisagree"> 拒绝换货 </el-button>
      </template>

      <template v-else-if="isReturnAndrefund(afterDetail.type || '', afterDetail.status || '')">
        <el-button :disabled="loading" type="primary" @click="onAgree"> 同意退货退款 </el-button>
        <el-button :disabled="loading" @click="onDisagree"> 拒绝退货退款 </el-button>
      </template>

      <template v-else-if="isWaitConfirm(afterDetail.type || '', afterDetail.status || '')">
        <el-button :disabled="loading" type="primary" @click="onAgree"> 确认收到退货 </el-button>
        <el-button :disabled="loading" @click="onDisagree"> 未收到退货 </el-button>
      </template>
    </div>

    <div v-if="negotiateList.length" class="negotiate">
      <div class="negotiate--title">
        <h1>协商历史</h1>
      </div>
      <div v-for="item of negotiateList" :key="item.id" class="negotiate--item">
        <div
          class="negotiate--item__head"
          :style="{
            backgroundImage: `url(${item.userAvatarUrl})`,
          }"
        />
        <div class="negotiate--item__content">
          <div class="content--name">
            <span>{{ item.userName }}</span> <span>{{ item.updateTime }}</span>
          </div>
          <div class="content--text">
            {{ item.info }}
          </div>
          <div v-if="item.image" class="content--images">
            <el-image
              v-for="sitem of item.image"
              :key="sitem"
              style="width: 100px; height: 100px; margin-right: 10px"
              :src="sitem"
              :preview-src-list="item.image"
              :fit="contain"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.after {
  &__status {
    width: 100%;
    padding: 20px 10px;
    background: #e8f4fe;
    border: 1px solid #88e2ff;

    .flex {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .tips {
      font-size: 12px;
      margin-top: 5px;
    }
  }

  &__btns {
    margin-top: 15px;
  }

  .negotiate {
    .negotiate--title {
      h1 {
        font-size: 16px;
        padding: 10px 0;
        border-bottom: 1px solid rgb(233, 233, 233);
        margin-bottom: 20px;
      }
    }

    &--item {
      display: flex;
      margin: 20px 0 10px;
      padding-bottom: 20px;
      border-bottom: 1px solid rgb(233, 233, 233);

      &__head {
        width: 40px;
        height: 40px;
        background: #ccc;
        margin-right: 10px;
        background-size: cover;
      }

      &__content {
        width: 100%;

        .content--name {
          display: flex;
          justify-content: space-between;
          margin-bottom: 5px;
          font-size: 14px;
        }

        .content--images {
          margin-top: 10px;
          display: flex;
          justify-content: flex-start;
        }
      }
    }
  }
}

.modal-after_btns {
  margin: 15px 0;

  .ivu-btn-default {
    margin-left: 10px;
  }
}
</style>
