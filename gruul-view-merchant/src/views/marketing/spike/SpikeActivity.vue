<script setup lang="ts">
const descriptionVisible = ref(false)
const value = ref('')
const date = ref('')

const options = [
  {
    value: '选项1',
    label: '开始',
  },
  {
    value: '选项2',
    label: '未开始',
  },
]

function toggleDescriptionVisible() {
  descriptionVisible.value = !descriptionVisible.value
}
</script>

<template>
  <div class="spike__activity">
    <div class="spike__activity--form">
      <el-button class="form--item" type="primary" @click="$router.push({ name: 'SpikeAdd' })"> 新增秒杀活动 </el-button>
      <el-select v-model="value" class="form--item" placeholder="请选择">
        <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
      <el-date-picker v-model="date" style="width: 150px" class="form--item" type="date" placeholder="选择日期" />
      <el-link class="form--item" underline="never" @click="toggleDescriptionVisible"> 活动说明 </el-link>
    </div>
    <div class="spike__activity--list">
      <m-card v-for="i of 4" :key="i">
        <div class="activity--item">
          <div class="item--l">
            <div>
              <span>活动时间：2019-05-22 14:26:36 至 2019-05-22 16:26:36</span>
              <span class="status">未开始</span>
            </div>
            <div>活动商品：8 件</div>
            <div class="statistics">
              <span>活动浏览量：0</span>
              <span>支付单数：0</span>
              <span>应收金额：0.00</span>
            </div>
          </div>
          <div class="item--r">
            <el-button-group>
              <el-button @click="$router.push({ name: 'SpikeUpdate' })"> 编辑活动 </el-button>
              <el-button>关闭活动</el-button>
            </el-button-group>
          </div>
        </div>
      </m-card>
    </div>
    <el-dialog v-model="descriptionVisible" title="提示">
      限时秒杀是超级棱镜平台推出的一款抢购类营销工具，商家可通过该工具设置优惠价售卖指定商品，营造紧张的抢购氛围，促进店铺整体销售。
      <br />一、适用场景 <br />提升小程序活跃度，引导线上用户成交转化。 <br />二、活动规则
      <br />1.在同一时间范围内未参加优享套餐、团购活动的所有上架商品均可参加此活动。
      <br />2.活动时间只能为整时，如1小时、2小时、3小时等，多个秒杀活动不能同时进行。
      <br />3.本活动与已设置为可叠加的非秒杀类活动叠加进行，可能造成商家财产损失，请谨慎选择。 <br />4.未开始的活动可以编辑、关闭和删除。
      <br />5.进行中的活动不可编辑，但可以关闭并删除。 <br />6.已关闭的活动可以编辑重新发布，也可以删除。
      <br />7.关闭、删除活动或从活动中移出商品，已经生成的订单不受影响。
      <br />8.删除活动，将同时删除活动中的所有统计数据；从活动中移出商品，将同时删除该商品在活动中的统计数据。 <br />三、使用方法
      <br />1.新增限时秒杀活动：填写活动名称、简介和备注，设置活动封面、时间范围、未付款订单关闭时间，添加商品并设定优惠价，选择优惠叠加。
      <br />2.活动开启后，进入预热阶段，用户可在小程序端看到活动开始倒计时，但不能下单购买。
      <br />3.倒计时结束，小程序前端限时秒杀模块进入抢购倒计时，用户可以在倒计时结束前下单购买活动中的商品，商家可以在活动列表中实时查看该活动的交易信息。
      <br />4.活动结束，商家根据后台的显示的订单信息发货。
      <br />
    </el-dialog>
  </div>
</template>

<style lang="scss"></style>
