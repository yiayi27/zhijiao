<script lang="ts" setup>
// 使用defineAsyncComponent异步加载组件
const componentsMap = {
  Buyer: defineAsyncComponent(() => import('@/views/setting/common/buyer/Buyer.vue')),
  Merchant: defineAsyncComponent(() => import('@/views/setting/common/merchant/Merchant.vue')),
  Exception: defineAsyncComponent(() => import('@/components/Exception.vue')),
}

const activeName = ref<keyof typeof componentsMap>('Buyer')
</script>

<template>
  <div class="message">
    <div class="message__info">
      <p>使用须知:</p>
      <p>1、使用商家通知需先绑定认证服务号，并于服务号后台开通"模板消息"功能并并从"模板库"中添加相应模板ID至"我的模板"中；</p>
      <p>2、使用买家通知消息需先绑定小程序，并从"模板库"中添加相应模板ID至"我的模板"中；</p>
      <p>
        3、每个认证服务号或小程序最多可同时启用25个模板ID（公众号相同的模板ID计为1个；小程序相同的模板ID且字段及顺序相同时计为1个）；公众号或小程序已添加的模板一旦达到25个，可能无法正常推送消息，此时请商家进入微信公众号后台删除部分不启用的模板。
      </p>
    </div>
    <el-tabs v-model="activeName" class="message-tabs">
      <el-tab-pane label="买家通知" name="Buyer" class="Buyer-tab" />
      <el-tab-pane label="云短信" name="Exception" class="Merchant-tab" />
    </el-tabs>
    <component :is="componentsMap[activeName]" />
  </div>
</template>

<style lang="scss" scoped>
@import '@/assets/styles/message/message.scss';
</style>
