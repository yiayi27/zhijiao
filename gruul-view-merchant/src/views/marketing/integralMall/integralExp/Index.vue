<script setup lang="ts">
const intro =
  '积分商城是超级棱镜平台推出的一款会员奖励类营销工具，商家可通过该工具让用户(会员用户)在消费后获得奖励，从而实现客户关怀，提高客户忠诚度。'

const sence = ['拉动小程序人气，激发购买欲望，实现用户二次消费，增加用户粘性。']

const activeRules = [
  '所有上架商品均可加入本活动，除了商品外，还可发布积分优惠券。',
  '出售中的积分商品可以编辑，可以下架和删除。',
  '仓库中的积分商品可以编辑重新发布、上架或删除。',
  '已售完的积分商品可以编辑重新发布、上架或删除。',
  '删除积分商品或下架积分商品，对已经生成的订单不受影响。',
  '删除积分商品时，列表中的相应统计数据将一并删除。',
]

const useWays = [
  '开启积分商城，并去积分商品处发布新的积分商品，以及设置积分价格、可兑换数量、限兑数量。',
  '积分商城开启后，参与兑换的积分商品即可在小程序端展示出来，供用户进行兑换。',
]
</script>

<template>
  <div class="box">
    <div>
      <div>{{ intro }}</div>
      <div class="title">一、适用场景</div>
      <div v-for="v in sence" :key="v" class="content">
        {{ v }}
      </div>
      <div class="title">二、活动规则</div>
      <div v-for="(v, i) in activeRules" :key="v" class="content">{{ i + 1 }}.{{ v }}</div>
      <div class="title">三、使用方法</div>
      <div v-for="(v, i) in useWays" :key="v" class="content">{{ i + 1 }}.{{ v }}</div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.box {
  margin-top: 10px;
  font-size: 14px;
  padding-left: 15px;
}

.content {
  margin-top: 10px;
}
.title {
  font-weight: bold;
  margin-top: 40px;
}
</style>
