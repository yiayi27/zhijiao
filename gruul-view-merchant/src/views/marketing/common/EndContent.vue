<script setup lang="ts">
// 定义接口描述 itemDetail 可能包含的字段
interface ItemDetail {
  name?: string
  startTime?: string
  endTime?: string
  effectiveDays?: number
  deductionType?: string
  applyType?: string
  productType?: number
  discountProductVos?: any[]
  fullScaleRules?: any[]
  couponType?: number
  discount?: number
  discountAmount?: number
  conditionAmount?: number
  issueQuantity?: number
  limitQuantity?: number
  [key: string]: any
}

// Props 定义
const props = defineProps({
  chooseItem: {
    type: Object,
    default: () => ({}),
  },
})

const itemDetail = ref<ItemDetail>({})

onMounted(() => {
  getShowItem()
})

function getShowItem() {
  itemDetail.value = JSON.parse(JSON.stringify(props.chooseItem))

  if (typeof itemDetail.value.deductionType === 'string') {
    itemDetail.value.deductionType = itemDetail.value.deductionType === '0' ? '积分' : '无'
  }

  if (itemDetail.value.applyType && typeof itemDetail.value.applyType === 'string') {
    itemDetail.value.applyType =
      itemDetail.value.applyType.length > 0 ? itemDetail.value.applyType.replace('0', '会员价').replace('1', '优惠券') : '无'
  }
}

// 监听 chooseItem 属性变化
watch(
  () => props.chooseItem,
  () => {
    getShowItem()
  },
  { deep: true },
)
</script>

<template>
  <div>
    <div class="digLine">
      <div class="digLine__left">活动名称:</div>
      <div>{{ itemDetail.name }}</div>
    </div>
    <div class="digLine">
      <div class="digLine__left">有效时间:</div>
      <div v-if="itemDetail.startTime">{{ itemDetail.startTime }} 至 {{ itemDetail.endTime }}</div>
      <div v-else>领券次日起{{ itemDetail.effectiveDays }}天内可用</div>
    </div>
    <div class="digLine">
      <div class="digLine__left">可用抵扣:</div>
      <div class="digLine__right">
        {{ itemDetail.deductionType }}
      </div>
    </div>
    <!-- <div class="digLine">
      <div class="digLine__left">使用优惠:</div>
      <div class="digLine__right">{{itemDetail.applyType}}</div>
    </div> -->
    <div class="digLine">
      <div class="digLine__left">选择商品:</div>
      <span v-if="itemDetail.productType === 0">全场商品</span>
      <span v-else>
        <div>以下商品{{ itemDetail.productType === 1 ? '参与' : '不参与' }}此场活动</div>
        <div class="digLine__left--activity">
          <div v-for="(item, index) in itemDetail.discountProductVos" :key="index">
            <div class="content">
              <img :src="item.pic" class="content__img" />
              <div class="content__detail">
                <div style="height: 300px; height: 40px">{{ item.name }}</div>
                <div style="color: #fe7781">
                  <div v-if="item.limitType === 1">￥{{ item.minPrice }}~￥{{ item.maxPrice }}</div>
                  <div v-if="item.limitType === 0">
                    <span v-if="item.minPrice > 0">￥{{ item.minPrice }}</span>
                    <span v-else>￥{{ item.maxPrice }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </span>
    </div>
    <div class="digLine">
      <div class="digLine__left">活动规则:</div>
      <div v-if="itemDetail.fullScaleRules" class="digLine__right">
        <div v-for="(item, index) in itemDetail.fullScaleRules" :key="index">
          <span style="color: red">
            <div v-if="item.ruleType === 0">满{{ item.conditionAmount }}元减{{ item.discountAmount }}元</div>
            <div v-if="item.ruleType === 1">满{{ item.conditionAmount }}元打{{ item.discount }}折</div>
          </span>
        </div>
      </div>
      <div v-else class="digLine__right">
        <span style="color: red">
          <div v-if="itemDetail.couponType === 0">{{ itemDetail.discount }}折,无门槛使用</div>
          <div v-if="itemDetail.couponType === 1">{{ itemDetail.discountAmount }}元,无门槛使用</div>
          <div v-if="itemDetail.couponType === 2">{{ itemDetail.discount }}折,满{{ itemDetail.conditionAmount }}元使用</div>
          <div v-if="itemDetail.couponType === 3">{{ itemDetail.discountAmount }}元,满{{ itemDetail.conditionAmount }}元使用</div>
        </span>
        <div style="margin-top: 10px">发行量{{ itemDetail.issueQuantity }}张</div>
        <div style="margin-top: 10px">限领{{ itemDetail.limitQuantity }}张/人</div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.digLine {
  display: flex;
  margin-bottom: 20px;
  &__left {
    width: 200px;
    &--activity {
      width: 400px;
      height: 160px;
      overflow: auto;
      overflow-x: hidden;
      margin: 10px 0px;
    }
  }
  &__right {
    flex-wrap: wrap;
  }
}

.digLine__left--activity::-webkit-scrollbar {
  display: none;
}

.content {
  height: 80px;
  padding: 10px;
  display: flex;
  border-bottom: 1px solid #eaedf2;
}

.content__img {
  width: 60px;
  height: 60px;
}

.content__detail {
  margin-left: 30px;
}
</style>
