<script setup lang="ts">
const intro = '满减优惠是给商家提供的一个店铺营销工具，通过这个营销工具可以让商家的店铺促销活动更加丰富。'

const sence = ['拉新促活，提高线上交易客单价。']

const activeRules = [
  '本活动与其他类型活动叠加进行，可能给商家造成财产损失，请谨慎选择。',
  '参加满减活动的商品仅在活动时间内可以使用。',
  '选择下单时可同时使用的优惠类型，直接勾选即可。',
  '活动时间内的满减活动可以编辑和关闭。',
  '已结束的活动可以查看和删除。',
  '删除和关闭活动对已经生成的订单不受影响。',
  '删除活动时，活动列表中的相应统计数据将一并删除。',
]

const useWays = [
  '新增满减活动：填写活动名称，设置有效时间、选择满减类型，设置满减类型规则，添加商品和优惠叠加方式。',
  '活动开启后，满减活动投放至小程序前端。',
  '商家可在活动列表中查看活动期内满减活动成交的相关统计数据。',
]
</script>

<template>
  <div class="box flex-1 overflow-auto">
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
  margin-left: 10px;
}

.content {
  margin-top: 10px;
}
.title {
  font-weight: bold;
  margin-top: 40px;
}
</style>
