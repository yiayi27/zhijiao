<script setup lang="ts">
interface OrderQuery {
  waitForPay: number
  waitForSend: number
  waitForPickup: number
  shipped: number
}

defineProps<{
  orderQuery: OrderQuery
}>()
</script>

<template>
  <div class="orderBox">
    <div class="text">交易概况</div>
    <div class="order">
      <div class="order__content">
        <div class="order__content--text">
          <div class="order__content--text--type">
            <i class="iconfont icondengdaifukuan" style="color: #fd8d42; font-size: 20px; margin-right: 3px" />
            待付款订单
          </div>
          <div class="order__content--text--account">
            {{ orderQuery.waitForPay }}
          </div>
        </div>
      </div>
      <div class="order__content">
        <div class="order__content--text">
          <div class="order__content--text--type">
            <i class="iconfont icondaifahuo" style="color: #fd8d42; font-size: 20px; margin-right: 3px" />
            待发货订单
          </div>
          <div class="order__content--text--account">
            {{ orderQuery.waitForSend }}
          </div>
        </div>
      </div>
      <div class="order__content">
        <div class="order__content--text">
          <div class="order__content--text--type">
            <i class="iconfont icondaishouhuofuben" style="color: #fd8d42; font-size: 20px; margin-right: 3px" />
            待收货订单
          </div>
          <div class="order__content--text--account">
            {{ orderQuery.waitForPickup }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.orderBox {
  background-color: white;
  margin-top: 10px;
  padding: 20px;
}

.text {
  font-size: 14px;
  font-weight: bold;
  color: #5f6779;
}

.order {
  margin-top: 10px;
  display: flex;
  justify-content: space-between;
  background-color: white;

  .order__content {
    width: 24%;
    height: 100px;
    background-color: white;
    display: flex;
    align-items: center;
    justify-content: center;

    .order__content--text {
      display: flex;
      flex-wrap: wrap;
      width: 90px;
      justify-content: center;
      color: #727073;

      .order__content--text--account {
        font-size: 30px;
        font-weight: bold;
        margin-top: 10px;
      }

      .order__content--text--type {
        font-size: 12px;
        margin-top: 5px;
        display: flex;
        align-items: center;
      }
    }
  }
}
</style>
