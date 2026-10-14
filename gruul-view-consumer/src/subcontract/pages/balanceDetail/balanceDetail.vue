<template>
  <view id="wrap" style="width: 100%; height: 100%" @tap="hideDelete">
    <view class="topText" @tap="onShowDeal">
      {{ selectLabel }}交易类型
      <uv-icon name="arrow-down" />
    </view>
    <view id="fix-top" :class="'filter-head ' + tabFix">
      <picker :end="expriseData" mode="date" fields="month" @change="dateChange">
        <view style="display: flex">
          {{ selectDate }}
          <uv-icon name="arrow-down" />
        </view>
      </picker>
      <view>支出：¥{{ outBalance }}{{ ' ' }}收入¥{{ inBalance }}</view>
    </view>
    <view v-if="!!tabFix" style="height: 170rpx; width: 100%" />
    <view v-for="(item, index) in list" :key="index" :class="'recode re-' + tabFix" :data-id="item.id" @longpress="showDelete">
      <view class="recode__conter">
        <view>{{ item.detailName }}</view>
        <view class="recode__conter--time">
          {{ item.createTime }}
          <text v-if="delId === item.id" class="delete-record" @tap.stop="deleteRecord">删除</text>
        </view>
      </view>
      <view :class="(item.type === 1 ? ' ' : 'recode__money--add') + ' recode__money '">
        {{ item.changeType === 1 ? '-' : '+' }}{{ item.useBalance }}
      </view>
    </view>
    <view v-if="isShowEmptyBottom" style="height: 200rpx; width: 100%" />
    <view v-if="!list.length" class="empty-data">暂无数据</view>
    <uv-loading-page loading-text="loading..." :loading="loading && list.length < total" bg-color="rgba(0,0,0,0.4)" />
    <block v-if="loading">
      <view v-if="list.length >= total" class="live-tip">到底了~</view>
    </block>
    <UPopup v-model:show="showDealType" mode="bottom" @close="onClose">
      <view class="deaConter" @tap="onClose">
        <view class="deaConter__top">
          <uv-icon name="arrow-left" style="margin-right: 40rpx" @tap="onClose" />
          选择交易类型
        </view>
        <view class="deaConter__content">
          <view
            v-for="(item, index) in dealTypes"
            :key="index"
            :class="'deaConter__content-item ' + (selectDeal === item.value ? 'deaConter__content-avtive' : '')"
            :data-val="item.value"
            :data-label="item.label"
            @tap.stop="onDealSelect"
          >
            {{ item.label }}
          </view>
        </view>
      </view>
    </UPopup>
    <uv-modal title="删除" :show="dialogShow" showCancelButton @confirm="confirmInfo" @cancel="onCloseDislog">
      <view class="confirmText">删除后，该条交易记录将从列表移除并无法找到</view>
    </uv-modal>
  </view>
</template>

<script setup lang="ts">
import { ref, nextTick, onMounted } from 'vue'
import { onLoad, onPageScroll, onReachBottom } from '@dcloudio/uni-app'
import { getMemberBalanceRecord, deleteMemberBalanceRecord } from '@/api/modules/member'
import { $Popup } from '@/utils/helper'

// 响应式状态
const showDealType = ref(false)
const selectDeal = ref(-1)
const selectLabel = ref('全部')
const tabFix = ref('')
const loading = ref(false)
const total = ref(0)
const tabTop = ref(0)
const wrapHeight = ref(0)
const isShowEmptyBottom = ref(false)
const selectDate = ref(`${new Date().getFullYear()}年${new Date().getMonth() + 1}月`)
const expriseData = ref(`${new Date().getFullYear()}-${new Date().getMonth() + 1}`)
const page = ref(1)
const list = ref<any[]>([])
const delId = ref('')
const show = ref(false)
const dialogShow = ref(false)
const outBalance = ref<number>(0)
const inBalance = ref<number>(0)
const dealTypes = [
  {
    label: '全部',
    value: -1,
  },
  {
    label: '用户消费',
    value: 0,
  },
  {
    label: '退款成功',
    value: 3,
  },
  {
    label: '微信充值',
    value: 1,
  },
  {
    label: '消费返利',
    value: 2,
  },
  {
    label: '分销佣金',
    value: 4,
  },
  {
    label: '提现',
    value: 5,
  },
]

// 方法定义
const fetchList = () => {
  uni.showLoading({
    title: '加载中',
  })

  let date: string | string[] = selectDate.value
  const dateParts = date.split('年')
  const yearMonth = `${dateParts[0]}${dateParts[1].split('月')[0]}`
  const params: any = {
    page: 1,
    size: 20,
    yearMonth,
  }

  if (selectDeal.value !== -1) {
    params.dealType = selectDeal.value
  }

  getMemberBalanceRecord(params).then((data) => {
    uni.hideLoading()
    list.value = data.list
    inBalance.value = data.inBalance
    outBalance.value = data.outBalance
    page.value = data.pages
    total.value = data.total
    loading.value = false
    showDealType.value = false
    nextTick(() => {
      const query = uni.createSelectorQuery()
      query
        .select('#wrap')
        .boundingClientRect((res: any) => {
          wrapHeight.value = res?.height || 0
        })
        .exec()
    })
  })
}

const dateChange = (e: any) => {
  const value: string[] = e.detail.value.split('-')
  selectDate.value = `${value[0]}年${value[1]}月`
  nextTick(() => {
    fetchList()
  })
}

const onShowDeal = () => {
  showDealType.value = true
}

const onClose = () => {
  showDealType.value = false
}

const onDealSelect = (e: any) => {
  const val = e.currentTarget.dataset.val
  const label = e.currentTarget.dataset.label
  selectDeal.value = val
  selectLabel.value = label
  nextTick(() => {
    fetchList()
  })
}

const showDelete = (e: any) => {
  if (e.currentTarget.dataset.id) {
    delId.value = e.currentTarget.dataset.id
    show.value = true
  }
}

const hideDelete = () => {
  delId.value = ''
}

const deleteRecord = () => {
  dialogShow.value = true
}

const confirmInfo = async () => {
  try {
    await deleteMemberBalanceRecord(delId.value)
    fetchList()
    delId.value = ''
    dialogShow.value = false
  } catch (err) {
    delId.value = ''
    dialogShow.value = false
    $Popup.toast(err as string)
  }
}

const onCloseDislog = () => {
  dialogShow.value = false
}

// 生命周期钩子
onLoad(() => {
  fetchList()
})

onMounted(() => {
  const query = uni.createSelectorQuery()
  query
    .select('#fix-top')
    .boundingClientRect((res: any) => {
      tabTop.value = res?.top || 0
    })
    .exec()
})

onPageScroll((e: any) => {
  const height = wrapHeight.value
  // 处理抖动情况
  if (height - uni.getSystemInfoSync().windowHeight <= tabTop.value + 12 && height - uni.getSystemInfoSync().windowHeight > 0) {
    tabFix.value = ''
    return false
  }
  //tab的吸顶效果
  if (e.scrollTop >= tabTop.value) {
    if (tabFix.value) {
      return
    } else {
      tabFix.value = 'Fixed'
    }
  } else {
    tabFix.value = ''
  }
  return false
})

onReachBottom(() => {
  if (list.value.length < total.value) {
    loading.value = true
    nextTick(() => {
      fetchList()
    })
  }
})
</script>

<style lang="scss" scoped>
.topText {
  @include flex();
  padding: 40rpx 0;
  background-color: #ededed;
  font-size: 30rpx;
  text-align: center;
  color: rgba(191, 158, 103, 1);
}

.addText {
  margin-bottom: 85rpx;
}

.recode {
  padding: 40rpx;
  border-bottom: 1rpx solid #f2f2f2;
  font-size: 26rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #ffffff;

  .recode__conter--time {
    color: #9e9e9e;
    margin-top: 20rpx;
  }
}

.recode__money {
  font-weight: bold;
  font-size: 36rpx;
}

.recode__money--add {
  color: #ff9900;
}

.deaConter__top {
  @include flex(flex-start);
  border-bottom: 1rpx solid #aaa;
  line-height: 90rpx;
  padding: 0 28rpx;
  font-size: 30rpx;
}

.deaConter__content {
  padding: 0 24rpx 30rpx;
  overflow: hidden;
}

.deaConter__content-item {
  text-align: center;
  font-size: 26rpx;
  padding: 26rpx 0;
  width: 31%;
  margin-top: 30rpx;
  float: left;
  border: 1rpx solid #bbb;

  &:nth-child(3n-1) {
    margin: 30rpx 3.5% 0;
  }
}

.deaConter__content-avtive {
  background-color: rgba(191, 158, 103, 1);
  color: #fff;
}

@include b(live-loading) {
  width: 100%;
  height: 100rpx;
}

@include b(live-tip) {
  height: 100rpx;
  line-height: 100rpx;
  text-align: center;
  color: #b0b0b0;
  font-size: 24rpx;

  @include e(loadMore) {
    background-color: #ffffff;
    color: #333333;
  }
}

.filter-head {
  display: flex;
  padding: 40rpx;
  border-bottom: 40rpx solid #ededed;
  justify-content: space-between;
  background-color: #fff;

  picker {
    font-size: 36rpx;
    color: #bf9e67;
  }

  > view {
    line-height: 50rpx;
    font-size: 32rpx;
  }
}

.empty-data {
  font-size: 32rpx;
  line-height: 300rpx;
  text-align: center;
  color: #aaa;
}

.Fixed {
  background-color: #eee;
  width: 100%;
  position: fixed;
  top: 0;
  margin-top: 0;
  z-index: 99999;
  border-bottom: 0;
}

// .re-Fixed {
//     margin-top: 160rpx;
// }

.delete-record {
  padding: 10rpx 20rpx;
  background-color: #9e9e9e;
  color: #fff;
  z-index: 2;
}

.overlay {
  transition-duration: 300ms;
  z-index: 1;
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: red;
}

.confirmText {
  padding: 30rpx 40rpx;
}
</style>
