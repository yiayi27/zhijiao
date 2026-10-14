<script setup lang="ts">
import { getSubMessage, updateMerchantState } from '@/api/message/message'

import { ArrowRight } from '@element-plus/icons-vue'

// 定义接口
interface MiniSubscriberMsg {
  id: string
  msgTitle: string
  templateId: string
  sendRule: string
  useStatus: number
  exampleJson: string
}

interface MessageItem {
  id: string
  messageType: number
  msgTitle: string
  miniSubscriberiMsgVos: MiniSubscriberMsg[]
}

// 状态
const dataList = ref<MessageItem[]>([])

// 计算属性 - 解析JSON
function exampleJsonFn(exampleJson: string) {
  return JSON.parse(exampleJson)
}

// 生命周期挂载
onMounted(() => {
  getDataList()
})

// 获取数据列表
async function getDataList() {
  // 使用字符串类型的参数
  const response = await getSubMessage('2')
  const { data } = response
  dataList.value = data
}

// 更新状态
async function useStatusChange(id: string, useStatus: number) {
  // 使用字符串类型的id
  await updateMerchantState(id, {
    useStatus,
  })
  await getDataList()
}
</script>

<template>
  <div>
    <table>
      <thead>
        <tr>
          <th style="width: 90px">消息类别</th>
          <th>消息标题</th>
          <th>模板ID</th>
          <th>推送规则</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <template v-for="supItem in dataList" :key="supItem.id">
          <tr v-for="(subItem, subIndex) in supItem.miniSubscriberiMsgVos" :key="subItem.id" class="message__tr">
            <td v-if="subIndex === 0" :rowspan="supItem.miniSubscriberiMsgVos.length" class="message__first__tr">
              <span v-if="supItem.messageType === 1">订单消息</span>
              <span v-if="supItem.messageType === 2">售后消息</span>
              <span v-if="supItem.messageType === 3">用户消息</span>
              <span v-if="supItem.messageType === 4">营销活动</span>
            </td>
            <td>{{ subItem.msgTitle }}</td>
            <td>{{ subItem.templateId }}</td>
            <td>{{ subItem.sendRule }}</td>
            <td class="message__useStatus">
              <el-switch
                v-model="subItem.useStatus"
                style="margin-right: 15px"
                active-color="#409EFF"
                inactive-color="#909399"
                :active-value="1"
                :inactive-value="0"
                @change="(e: any) => useStatusChange(subItem.id, e)"
              />
              <el-popover trigger="click">
                <template #default>
                  <el-card shadow="never" class="message__card">
                    <template #header>
                      <div class="message__clearfix">
                        <div class="message__card--title">
                          {{ exampleJsonFn(subItem.exampleJson).title }}
                        </div>
                        <div v-for="(item, idx) in exampleJsonFn(subItem.exampleJson).items" :key="idx">
                          <span>{{ item.k }}：</span>
                          <span>{{ item.v }}</span>
                        </div>
                      </div>
                    </template>
                    <div class="message__card--detail">
                      <span>详情</span>
                      <el-icon><ArrowRight /></el-icon>
                    </div>
                  </el-card>
                </template>
                <template #reference>
                  <el-button link> 预览 </el-button>
                </template>
              </el-popover>
            </td>
          </tr>
        </template>
      </tbody>
    </table>
  </div>
</template>

<style lang="scss" scoped></style>
