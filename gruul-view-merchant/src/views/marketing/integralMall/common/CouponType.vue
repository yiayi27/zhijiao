<script setup lang="ts">
defineProps({
  coupon: {
    type: Object,
    default: () => ({}),
  },
})

const backColor = ['#ff9600', '#ff6c5c', '#41a9f9', '#01d5af']

const couColor = ['#ffbf00', '#de4f3b', '#4bc8ff', '#05af8f']
</script>

<template>
  <div>
    <div class="coupon">
      <div class="couponLeft">
        <div class="box--coupon">
          <div v-if="coupon.productType >= 0" class="box--coupon__scope">
            {{ coupon.productType === 0 ? '全店商品' : '指定商品' }}
          </div>
          <div v-if="coupon.couponType === 1 || coupon.couponType === 2" class="box--coupon__rule">无门槛使用</div>
          <div v-if="coupon.couponType === 3 || coupon.couponType === 4" class="box--coupon__rule">满{{ coupon.conditionAmount }}可使用</div>
        </div>
      </div>
      <div class="couponRight" :style="{ 'background-color': backColor[coupon.couponType - 1] }">
        <div class="radius--coupon">
          <span class="radius--coupon__value">
            <span v-if="coupon.couponType === 1 || coupon.couponType === 3">{{ coupon.discount }}</span>
            <span v-else>{{ coupon.discountAmount }}</span>
            <span class="radius--coupon__value--unit">{{ coupon.couponType === 1 || coupon.couponType === 3 ? '折' : '元' }}</span>
          </span>
          <div class="radius--coupon__value--type" :style="{ 'background-color': couColor[coupon.couponType - 1] }">
            {{ coupon.couponType === 1 || coupon.couponType === 3 ? '折扣券' : '现金券' }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.coupon {
  display: flex;
}

.couponLeft {
  width: 100px;
  height: 60px;
  position: relative;
  background-image:
    radial-gradient(circle at right top, #fff, #fff 10px, transparent 11px),
    radial-gradient(circle at right bottom, #fff, #fff 10px, transparent 11px);
  background-color: white;
}
.box--coupon {
  width: 100px;
  height: 60px;
  padding: 13px;
  position: relative;
  flex: 0 0 200px;
  // background-color: white;
  &__scope {
    // font-size: 20px;
    font-weight: bolder;
  }
  &__rule {
    margin-top: 5px;
  }
  &__date {
    margin-top: 15px;
    font-size: 11px;
    color: #595959;
  }
}
.couponLeft:before {
  content: '';
  position: absolute;
  top: 0px;
  bottom: 0px;
  left: 90px;
  width: 10px;
  height: 100%;
  background: radial-gradient(circle, #fff, #fff 4px, transparent 5px);
  background-size: 10px 10px;
}
.couponLeft:after {
  content: '';
  position: absolute;
  top: 0px;
  bottom: 0px;
  left: -5px;
  width: 10px;
  height: 100%;
  background: radial-gradient(circle, #fff, #fff 4px, transparent 5px);
  background-size: 10px 10px;
}
.couponRight {
  width: 60px;
  height: 60px;
  position: relative;
  background-image:
    radial-gradient(circle at left top, #fff, #fff 10px, transparent 11px), radial-gradient(circle at left bottom, #fff, #fff 10px, transparent 11px);
  background-color: red;
}
.radius--coupon {
  width: 60px;
  height: 60px;
  position: relative;
  overflow: hidden;
  z-index: 1;
  background-size: 10px 10px;
  background-position: 5px 0px;
  &__value {
    position: absolute;
    font-size: 24px;
    color: white;
    left: 10px;
    top: 5px;
    &--unit {
      font-size: 12px;
      position: absolute;
      right: -15px;
      top: 10px;
    }
    &--type {
      font-size: 12px;
      position: absolute;
      right: 7px;
      bottom: 7px;
      padding: 2px;
      width: 40px;
      height: 16px;
      color: white;
    }
  }
}
.couponRight:before {
  content: '';
  position: absolute;
  top: 0px;
  bottom: 0px;
  left: -5px;
  width: 10px;
  height: 100%;
  background: radial-gradient(circle, #fff, #fff 4px, transparent 5px);
  background-size: 10px 10px;
}
.couponRight:after {
  content: '';
  position: absolute;
  top: 0px;
  bottom: 0px;
  left: 56px;
  width: 10px;
  height: 100%;
  background: radial-gradient(circle, #fff, #fff 4px, transparent 5px);
  background-size: 10px 10px;
}
</style>
