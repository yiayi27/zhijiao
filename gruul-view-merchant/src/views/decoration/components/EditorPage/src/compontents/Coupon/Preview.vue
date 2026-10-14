<script setup lang="ts">
const props = defineProps({
  formData: {
    type: Object,
    default: () => null,
  },
})

const emit = defineEmits(['update:formData'])

const formModel = computed({
  get: () => props.formData,
  set: (value) => emit('update:formData', value),
})

/** 样式图片 */
const couponList = ref([
  {
    count: 5,
    type: '元',
    rule: '满5元可使用',
    color: 'bg_orange',
  },
  {
    count: 5,
    type: '元',
    rule: '无门槛使用',
    color: 'bg_red',
  },
  {
    count: 5,
    type: '折',
    rule: '满5元可使用',
    color: 'bg_green',
  },
  {
    count: 5,
    type: '折',
    rule: '无门槛使用',
    color: 'bg_blue',
  },
])
</script>

<template>
  <div class="Coupon">
    <div v-for="(item, index) in couponList" :key="index" class="boxCoupon" :class="[item.color ? item.color : '']">
      <div class="boxCoupon_top">
        <span class="coupon_title">5</span>
        {{ item.type }}
      </div>
      <div class="coupon_bottom">
        {{ item.rule }}
      </div>
      <div v-if="formModel.style === 'style2'" class="left_icon" />
      <div v-if="formModel.style === 'style2'" class="right_icon" />
    </div>
  </div>
</template>

<style lang="scss" scoped>
div::-webkit-scrollbar {
  width: 0px;
  height: 0px;
}
.Coupon {
  height: 80px;
  width: 100%;
  background-color: #ffffff;
  margin: 5px 0;
  overflow-x: auto;
  white-space: nowrap;
}
.boxCoupon {
  margin-left: 5px;
  width: 150px;
  height: 75px;
  display: inline-block;
  background-color: rgba(246, 164, 44, 1);
  overflow: hidden;
  position: relative;
}
.boxCoupon_top {
  font-size: 14px;
  text-align: center;
  color: #fff;
  .coupon_title {
    font-size: 36px;
  }
}
.coupon_bottom {
  font-size: 14px;
  text-align: center;
  color: #fff;
}
.left_icon {
  width: 6px;
  height: 75px;
  position: absolute;
  background-image: url('../../../../../../../assets/images/decoration/coupon_right.png');
  background-size: 100%;
  left: 0;
  top: 0;
}
.right_icon {
  width: 6px;
  height: 75px;
  position: absolute;
  background-image: url('../../../../../../../assets/images/decoration/coupon_left.png');
  background-size: 100%;
  right: 0;
  top: 0;
}

.bg_orange {
  background-color: rgba(246, 164, 44, 1);
}

.bg_red {
  background-color: rgba(252, 92, 123, 1);
}
.bg_green {
  background-color: rgba(31, 222, 148, 1);
}
.bg_blue {
  background-color: rgba(116, 211, 215, 1);
}
.bg_gray {
  background-color: #ccc;
}
</style>
