<script setup lang="ts">
import type { GiveCoupon } from '@/views/customer/list/formModel/GiveCoupon'
import { CircleCheckFilled } from '@element-plus/icons-vue'

const props = defineProps({
  setting: {
    type: Object as () => GiveCoupon,
    required: true,
  },
  selected: {
    type: Boolean,
    required: true,
  },
})

const emit = defineEmits(['update:selected'])

const isSelected = computed({
  get: () => props.selected,
  set: (value) => emit('update:selected', value),
})

const colorMap = [
  ['#ffbf00', '#ff9600'],
  ['#de4f3b', '#ff6c5c'],
  ['#4bc8ff', '#41a9f9'],
  ['#05af8f', '#01d5af'],
]

const productTypeMap = ['全部商品', '指定商品', '指定商品不参与']

const startTime = computed(() => {
  const nowDate = new Date()
  const nowY = nowDate.getFullYear()
  const nowM = nowDate.getMonth() + 1
  const nowD = nowDate.getDate()
  return `${nowY}.${nowM < 10 ? `0${nowM}` : nowM}.${nowD < 10 ? `0${nowD}` : nowD}`
})

const endTime = computed(() => {
  const endDate = new Date()
  endDate.setDate(endDate.getDate() + Number(props.setting.effectiveDays || 0))
  const lastY = endDate.getFullYear()
  const lastM = endDate.getMonth() + 1
  const lastD = endDate.getDate()
  return `${lastY}.${lastM < 10 ? `0${lastM}` : lastM}.${lastD < 10 ? `0${lastD}` : lastD}`
})

function formatTime(time: string | null | undefined): string {
  if (time) {
    const data = time.split(' ')[0]
    return data.split('-').join('.')
  }
  return ''
}
</script>

<template>
  <div class="coupon">
    <div class="box--coupon">
      <div class="box--coupon__scope" :style="{ color: colorMap[setting.couponType || 0][0] }">
        {{ productTypeMap[setting.productType || 0] }}
      </div>
      <div
        v-if="setting.couponType === 0 || setting.couponType === 1"
        class="box--coupon__rule"
        :style="{ color: colorMap[setting.couponType || 0][0] }"
      >
        无门槛使用
      </div>
      <div
        v-if="setting.couponType === 2 || setting.couponType === 3"
        class="box--coupon__rule"
        :style="{ color: colorMap[setting.couponType || 0][0] }"
      >
        满{{ setting.conditionAmount }} 元可使用
      </div>
      <div v-if="setting.effectiveType === 0" class="box--coupon__date">
        <span class="box--coupon__title">有效期</span>
        <span class="box--coupon__start">{{ formatTime(setting.startTime) }}</span>
        <span>-</span>
        <span class="box--coupon__end">{{ formatTime(setting.endTime) }}</span>
      </div>
      <div v-if="setting.effectiveType === 1" class="box--coupon__date">
        <span class="box--coupon__title">有效期</span>
        <span class="box--coupon__start">{{ startTime }}</span>
        <span>-</span>
        <span class="box--coupon__end">{{ endTime }}</span>
      </div>
    </div>
    <div
      class="radius--coupon"
      :style="{
        backgroundImage: `radial-gradient(rgba(0, 0, 0, 0) 0,rgba(255, 0, 0, 0) 3px,${colorMap[setting.couponType || 0][0]} 3px)`,
      }"
    >
      <div class="radius--coupon__before" :style="{ background: colorMap[setting.couponType || 0][0] }" />
      <div class="radius--coupon__top" />
      <div class="radius--coupon__bottom" />
      <span v-if="setting.couponType === 1 || setting.couponType === 3" class="radius--coupon__value">
        {{ setting.discountAmount }}<span class="radius--coupon__value--unit">元</span>
        <span class="radius--coupon__value--type" :style="{ backgroundColor: colorMap[setting.couponType || 0][1] }">现金券</span>
      </span>
      <span v-if="setting.couponType === 0 || setting.couponType === 2" class="radius--coupon__value">
        {{ setting.discount }}<span class="radius--coupon__value--unit">折</span>
        <span class="radius--coupon__value--type" :style="{ backgroundColor: colorMap[setting.couponType || 0][1] }">折扣券</span>
      </span>
      <div class="radius--coupon__use">立即使用</div>
    </div>
    <div v-if="isSelected" :class="{ 'shade--coupon': isSelected }">
      <el-icon class="coupon--success">
        <CircleCheckFilled />
      </el-icon>
    </div>
  </div>
</template>

<style scoped lang="scss">
.coupon {
  color: black;
  display: flex;
  position: relative;

  .box--coupon {
    width: 200px;
    height: 90px;
    padding: 8px;
    position: relative;
    flex: 0 0 200px;

    &__scope {
      font-size: 20px;
      font-weight: bolder;
    }

    &__rule {
      margin-top: 5px;
    }

    &__date {
      margin-top: 5px;
      font-size: 12px;
      color: #595959;
    }
  }

  .radius--coupon {
    width: 120px;
    height: 90px;
    position: relative;
    overflow: hidden;
    z-index: 1;
    background-size: 10px 10px;
    background-position: 5px 0;

    &__before {
      position: absolute;
      top: 0;
      bottom: 0;
      left: 6px;
      right: 6px;
    }

    &__top {
      background: white;
      width: 20px;
      height: 20px;
      border-radius: 50%;
      position: absolute;
      left: -10px;
      top: -10px;
    }

    &__bottom {
      background: white;
      width: 20px;
      height: 20px;
      border-radius: 50%;
      position: absolute;
      left: -10px;
      bottom: -10px;
    }

    &__value {
      position: absolute;
      font-size: 50px;
      color: white;
      left: 10px;
      top: 10px;

      &--unit {
        font-size: 12px;
        position: absolute;
        right: -15px;
        top: 10px;
      }

      &--type {
        font-size: 12px;
        position: absolute;
        right: -45px;
        bottom: 25px;
        padding: 2px;
      }
    }

    &__use {
      position: absolute;
      bottom: 8px;
      left: 35px;
      font-size: 12px;
      color: white;
      text-align: center;
      border: 1px solid white;
      border-radius: 6px;
      padding: 2px;
    }
  }

  .shade--coupon {
    position: absolute;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.4);
    z-index: 2;

    .coupon--success {
      position: absolute;
      color: #ffffff;
      font-size: 40px;
      right: 5px;
      bottom: 10px;
      z-index: 3;
    }
  }
}
</style>
