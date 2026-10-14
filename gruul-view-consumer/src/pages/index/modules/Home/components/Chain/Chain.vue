<template>
  <view v-if="checkValue && hasChain" style="margin-bottom: 20rpx" @tap="jumpChainPage">
    <view class="chainTop">
      <view class="chainTop__content">
        <view class="line">
          <view class="circle" />
          <view class="lineOne" />
          <view class="LineTwo" />
        </view>
        <span style="font-size: 32rpx">接龙活动</span>
        <view class="line">
          <view class="circle" />
          <view class="lineOne" />
          <view class="LineTwo" />
        </view>
      </view>
    </view>
    <view class="chainDesc">
      <view class="chainDesc__top">
        <span class="title">{{ activityDetail.name }}</span>
        <span v-if="countDetail.status === 'ended'" class="day">
          {{ activityDetail.stopTime }}
        </span>
      </view>
      <view class="chainDesc__desc">{{ activityDetail.content }}</view>
      <view class="chainDesc__img">
        <image :src="activityDetail.coverPoster" alt style="width: 100%; height: 340rpx" :lazy-load="true" />
      </view>
      <view v-if="activityDetail.solitaireRecords?.length > 0" class="chainDesc__list">
        <view v-for="(item, index) in activityDetail.solitaireRecords" :key="index">
          <view class="chainDesc__list--content">
            <view class="line">
              <view style="display: flex; width: 160rpx; align-items: center; justify-content: space-between">
                <view class="contentId">{{ item.sign }}.</view>
                <image :src="item.buyerIcon" alt class="img" :lazy-load="true" />
              </view>
              <view style="color: #6b6b6b; width: 160rpx; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; margin-left: 20rpx">
                {{ item.buyerName }}
              </view>
            </view>
            <view style="display: flex; width: 45%; align-items: center; justify-content: space-between">
              <view style="color: #6b6b6b">{{ item.createTime }}</view>
              <view style="color: #6b6b6b; display: flex">
                <view style="color: #6b6b6b; width: 160rpx; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; margin-left: 20rpx">
                  {{ item.productName }}
                </view>
                + {{ item.productQuantity }}
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>
    <view class="chainBottom">
      <view class="chainBottom__left">
        <span>有{{ activityDetail.views }}人看过</span>
        <span style="margin-left: 10rpx">{{ activityDetail.userRecords }}人参与</span>
      </view>
      <view v-if="countDetail.status !== 'ended'" class="time__count">
        <view class="time__count--text">
          <text>还剩</text>
        </view>
        <uv-count-down :time="remainTime" @change="handleChange">
          <view style="display: flex; align-items: center">
            <view class="time">{{ timeData.hours[0] }}</view>
            <view class="time">{{ timeData.hours[1] }}</view>
            <view v-if="timeData.hours.length > 2" class="time">{{ timeData.hours[2] }}</view>
            <view class="colon">:</view>
            <view class="time">{{ timeData.minutes[0] }}</view>
            <view class="time">{{ timeData.minutes[1] }}</view>
            <view class="colon">:</view>
            <view class="time">{{ timeData.seconds[0] }}</view>
            <view class="time">{{ timeData.seconds[1] }}</view>
          </view>
        </uv-count-down>
      </view>
      <view v-if="countDetail.status === 'aboutToBegin'" class="chainBottom__status">距开始</view>
      <view v-if="countDetail.status === 'beginning'" class="chainBottom__status">进行中</view>
      <view v-if="countDetail.status === 'ended'" class="chainBottom__status" style="background-color: #cccccc"> 已结束 </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, watch, reactive } from 'vue'
import { getActivityList, getActivitySetting as fetchActivitySetting } from '@/api/modules/chain'
import DateUtil from '@/mixins/date'
import { LOGIN_OPEN } from '@/utils/emitNameConfig'
import { useUserStore } from '@/store/modules/user'
import { storeToRefs } from 'pinia'

const { isLogin } = storeToRefs(useUserStore())

// 定义props
const props = defineProps({
  decoretionProperties: {
    type: Object,
    default: () => {},
  },
})

// 响应式变量
const formData = ref<Obj>({})
const checkValue = ref(false)
const hasChain = ref(true)
const activityDetail = ref<Obj>({})
const countDetail = reactive({
  status: '',
  toBeginTime: 0,
  toEndTime: 0,
})
const timeData = ref({
  hours: ['0', '0'],
  minutes: ['0', '0'],
  seconds: ['0', '0'],
})
const showType = ref(false)
const remainTime = ref(0)
const showCountDown = ref(false)

// 方法定义
const getActivitySetting = () => {
  fetchActivitySetting({}).then((res: any) => {
    checkValue.value = Boolean(res.activityState)
    if (checkValue.value) {
      getActivity()
    }
  })
}

const formatNumber = (n: any): string => {
  n = n.toString()
  return n[1] ? n : '0' + n
}

const getCountDownInfo = (item: any) => {
  const dateNow = Date.now()
  const activityBeginTime = new Date(item.startTime.replace(/-/g, '/')).getTime()
  const activityEndTime = new Date(item.endTime.replace(/-/g, '/')).getTime()
  const aboutToBegin = dateNow < activityBeginTime // 将要开始
  const isBegin = dateNow >= activityBeginTime && dateNow < activityEndTime // 已开始
  const isEnd = dateNow >= activityEndTime // 已结束
  let toBeginTime = activityBeginTime - dateNow
  let toEndTime = activityEndTime - dateNow
  let remainTimeValue = 0
  let status = ''

  if (aboutToBegin) {
    status = 'aboutToBegin'
    remainTimeValue = toBeginTime
  }
  if (isBegin) {
    status = 'beginning'
    toBeginTime = 0
    remainTimeValue = toEndTime
  }
  if (isEnd) {
    status = 'ended'
    toEndTime = 0
    remainTimeValue = 0
    timeData.value = {
      hours: ['0', '0'],
      minutes: ['0', '0'],
      seconds: ['0', '0'],
    }
  }

  return {
    status,
    remainTime: remainTimeValue,
    toBeginTime,
    toEndTime,
  }
}

const dealTime = (options: any) => {
  if (options.status === 'aboutToBegin') {
    countDetail.status = 'aboutToBegin'
    countDetail.toBeginTime = 0
    countDetail.toEndTime = options.toEndTime - options.remainTime
  }

  if (options.status === 'beginning') {
    countDetail.status = 'beginning'
    countDetail.toBeginTime = 0
    countDetail.toEndTime = 0
  }

  if (options.status === 'ended') {
    countDetail.status = 'ended'
    countDetail.toBeginTime = 0
    countDetail.toEndTime = 0
  }

  if (options.timeData) {
    timeData.value = {
      hours: options.timeData.hours ? options.timeData.hours.split('') : ['0', '0'],
      minutes: options.timeData.minutes ? options.timeData.minutes.split('') : ['0', '0'],
      seconds: options.timeData.seconds ? options.timeData.seconds.split('') : ['0', '0'],
    }
  }
  remainTime.value = options.remainTime
  showCountDown.value = true
}

const getConstData = (res: any) => {
  const now = Date.now()
  const end = Date.parse(res.endTime.replace(/-/g, '/'))
  const start = Date.parse(res.startTime.replace(/-/g, '/'))
  const showTime = new DateUtil('').getHMSs(start, res.preheatHour * 60 * 60 * 1000)
  const show = Date.parse(showTime.replace(/-/g, '/'))
  return {
    now,
    end,
    show,
  }
}

const dealSolitaire = (res: any) => {
  res.solitaireRecords.forEach((item: any, index: number) => {
    item.createTime =
      Number(new DateUtil('').getD(new Date())) - Number(new DateUtil('').getD(item.payTime)) > 0
        ? `${Number(new DateUtil('').getD(new Date())) - Number(new DateUtil('').getD(item.payTime))}天前`
        : new DateUtil('').getHM(item.payTime)
    item.sign = res.orderRecords > 4 ? res.orderRecords - index : (res.solitaireRecords?.length ?? 0) - index
  })
}

const getActivity = () => {
  getActivityList({})
    .then((res: any) => {
      const countDownInfo = getCountDownInfo(res)
      dealTime(countDownInfo)
      if (res.solitaireRecords) dealSolitaire(res)
      const { now, end, show } = getConstData(res)
      if (now > end) {
        const culTime = now - end
        const h = culTime / 3600000
        const m = (culTime % (1000 * 60 * 60)) / (1000 * 60)
        if (h > 24) {
          const now = new Date().getTime()
          const stop = new Date(res.endTime.replace(/-/g, '/')).getTime()
          let day = ((now - stop) / 1000 / 24 / 60 / 60) as any
          day = day.toFixed(0)
          res.stopTime = `${day}天前`
        } else if (h > 1) {
          res.stopTime = `${h.toFixed(0)}小时前`
        } else if (m > 0) {
          res.stopTime = `${m.toFixed(0)}分钟前`
        }
      }
      activityDetail.value = res
      showType.value = now > show ? true : false
    })
    .catch(() => {
      hasChain.value = false
    })
}

const handleChange = (e: any) => {
  if (remainTime.value === 0) return
  const { days, hours, minutes, seconds } = e
  const h = hours + days * 24
  const hoursStr = formatNumber(h)
  const minutesStr = formatNumber(minutes)
  const secondsStr = formatNumber(seconds)

  timeData.value = {
    hours: hoursStr.split(''),
    minutes: minutesStr.split(''),
    seconds: secondsStr.split(''),
  }

  if (h === 0 && minutes === 0 && seconds === 0) {
    getActivity()
  }
}

const jumpChainPage = () => {
  if (!isLogin.value) {
    uni.$emit(LOGIN_OPEN, true)
    return
  }
  const id = activityDetail.value.id
  uni.navigateTo({
    url: `/subcontract/pages/chainDetail/chainDetail?id=${id}`,
  })
}

// 监听props变化
watch(
  () => props.decoretionProperties,
  (newVal) => {
    if (newVal) {
      formData.value = newVal as Obj
      getActivitySetting()
    }
  },
  {
    immediate: true,
  },
)
</script>

<style lang="scss" scoped>
.line {
  display: flex;
  align-items: center;
}

.circle {
  width: 16rpx;
  height: 16rpx;
  background-color: #e75e75;
  border-radius: 100rpx;
}

.lineOne {
  width: 16rpx;
  height: 40rpx;
  border-radius: 20rpx;
  background-color: #eeb84e;
  transform: skewX(-35deg);
  margin-left: 16rpx;
}

.LineTwo {
  width: 16rpx;
  height: 32rpx;
  border-radius: 20rpx;
  background-color: #e78159;
  transform: skewX(-35deg);
  margin-left: 16rpx;
}

@include b(chainTop) {
  display: flex;
  justify-content: center;
  padding-top: 40rpx;
  background-color: white;

  @include e(content) {
    display: flex;
    align-items: center;
    width: 360rpx;
    justify-content: space-between;
  }
}

@include b(chainDesc) {
  padding: 40rpx 40rpx 0 40rpx;
  background-color: white;

  @include e(top) {
    display: flex;
    justify-content: space-between;

    .title {
      font-size: 36rpx;
      font-weight: 700;
    }

    .name {
      font-size: 36rpx;
      font-weight: bold;
    }

    .day {
      font-size: 28rpx;
      color: #bcbcbc;
    }
  }

  @include e(desc) {
    margin-top: 20rpx;
    color: #868686;
  }

  @include e(img) {
    width: 100%;
    margin-top: 20rpx;
  }

  @include e(list) {
    margin-top: 20rpx;

    @include m(content) {
      display: flex;
      flex-wrap: wrap;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 10rpx;
      font-size: 26rpx;
      width: 100%;

      .line {
        display: flex;
        align-items: center;
        width: 50%;

        .contentId {
          width: 40rpx;
          display: flex;
          justify-content: flex-end;
        }

        .img {
          width: 80rpx;
          height: 80rpx;
          border-radius: 100rpx;
        }
      }
    }
  }
}

@include b(chainBottom) {
  display: flex;
  padding: 20rpx 40rpx 20rpx 40rpx;
  margin-top: -10rpx;
  font-size: 24rpx;
  justify-content: space-between;
  align-items: center;
  color: #a1a1a1;
  background-color: white;

  @include e(left) {
    // width: 280rpx;
    display: flex;
    justify-content: flex-start;
  }

  @include e(status) {
    width: 120rpx;
    height: 48rpx;
    display: flex;
    justify-content: center;
    align-items: center;
    border-radius: 100rpx;
    font-size: 24rpx;
    color: white;
    background-color: #f73119;
  }
}

.time__count {
  display: flex;
  align-items: center;
}

.time__count--text {
  text-align: center;
  margin: 0 8rpx;
  color: #a1a1a1;
}

.colon {
  margin-left: 4rpx;
  margin-right: 8rpx;
  margin-bottom: 4rpx;
}

.time {
  width: 20rpx;
  height: 30rpx;
  margin-right: 4rpx;
  background-color: #000000;
  text-align: center;
  font-size: 22rpx;
  font-family: HiraginoSansGB-W3;
  font-weight: normal;
  color: #fff;
  line-height: 42rpx;
  border-radius: 6rpx;
  display: flex;
  justify-content: center;
  align-items: center;
  padding-bottom: 1px;
}
</style>
