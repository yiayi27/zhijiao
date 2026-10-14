<script setup lang="ts">
import { getBuyerMessage, updateMessageState } from '@/api/message/message'

// 定义类型接口
interface MessageItem {
  id: number
  codeOpen: number
  miniOpen: number
  mpOpen: number
  title: string
  codeMsg?: any
  miniMsg?: any
  mpMsg?: any
  [key: string]: any // 添加索引签名允许动态访问属性
}

interface MessageGroup {
  msgTitle: string
  shopMessageDetailVos: MessageItem[]
}

interface FormType {
  codeOpen: number
  id: number
  miniOpen: number
  mpOpen: number
  [key: string]: any // 添加索引签名允许动态访问属性
}

// 状态
const initData = ref<any[]>([])
const dataList = ref<MessageGroup[]>([])

// 生命周期挂载
onMounted(() => {
  getDataList()
})

// 获取消息数据
async function getDataList() {
  const response = await getBuyerMessage()
  const { data } = response
  initData.value = data
  const tempDataList = JSON.parse(JSON.stringify(data))
  dataList.value = tempDataList.map((supItem: any) => {
    if (supItem.shopMessageDetailVos && supItem.shopMessageDetailVos.length > 0) {
      supItem.shopMessageDetailVos = supItem.shopMessageDetailVos.map((subItem: any) => {
        if (subItem.codeMsg && typeof subItem.codeMsg === 'string') {
          subItem.codeMsg = JSON.parse(subItem.codeMsg)
        }
        if (subItem.miniMsg && typeof subItem.miniMsg === 'string') {
          subItem.miniMsg = JSON.parse(subItem.miniMsg)
        }
        if (subItem.mpMsg && typeof subItem.mpMsg === 'string' && subItem.mpMsg !== '{}') {
          subItem.mpMsg = JSON.parse(subItem.mpMsg)
        }
        return subItem
      })
    }
    return supItem
  })
}

// 消息状态变更
async function useStatusChange(subItem: MessageItem, name: string, value: number) {
  try {
    const form: FormType = {
      codeOpen: subItem.codeOpen,
      id: subItem.id,
      miniOpen: subItem.miniOpen,
      mpOpen: subItem.mpOpen,
    }
    form[name] = value
    await updateMessageState(form)
    await getDataList()
  } catch (e) {
    console.error(e)
    subItem[name] = value ? 0 : 1
  }
}
</script>

<template>
  <div>
    <table>
      <thead>
        <tr>
          <th style="width: 90px">消息类别</th>
          <th>消息标题</th>
          <th>小程序模板消息</th>
          <th>短信通知</th>
          <th>公众号模板消息</th>
        </tr>
      </thead>
      <tbody>
        <template v-for="supItem in dataList">
          <tr v-for="(subItem, subIndex) in supItem.shopMessageDetailVos" :key="subItem.id" class="message__tr">
            <td v-if="subIndex === 0" :rowspan="supItem.shopMessageDetailVos.length" class="message__first__tr">
              <span>{{ supItem.msgTitle }}</span>
            </td>
            <td>{{ subItem.title }}</td>
            <td>
              <div class="message__useStatus">
                <el-switch
                  v-model="subItem.miniOpen"
                  style="margin-right: 15px"
                  active-color="#409EFF"
                  inactive-color="#909399"
                  :active-value="1"
                  :inactive-value="0"
                  @change="(e: any) => useStatusChange(subItem, 'miniOpen', e)"
                />
                <el-popover trigger="click">
                  <el-card v-if="subItem.miniMsg" shadow="never" class="message__card">
                    <template #header>
                      <div class="message__clearfix">
                        <div class="message__card--title">
                          {{ subItem.miniMsg.exampleJson.title }}
                        </div>
                        <div v-for="item in subItem.miniMsg.exampleJson.items" :key="item.id">
                          <span>{{ item.k }}：</span>
                          <span>{{ item.v }}</span>
                        </div>
                      </div>
                    </template>
                    <!--                  <div class="message__card&#45;&#45;detail"> -->
                    <!--                    <span>详情</span> -->
                    <!--                    <i class="el-icon-arrow-right"></i> -->
                    <!--                  </div> -->
                  </el-card>
                  <template #reference>
                    <el-button link> 预览 </el-button>
                  </template>
                </el-popover>
              </div>
            </td>
            <td class="flex__center">
              <div class="message__useStatus">
                <el-switch
                  v-model="subItem.codeOpen"
                  disabled
                  style="margin-right: 15px"
                  active-color="#409EFF"
                  inactive-color="#909399"
                  :active-value="1"
                  :inactive-value="0"
                  @change="(e: any) => useStatusChange(subItem, 'codeOpen', e)"
                />
                <el-popover trigger="click">
                  <el-card v-if="subItem.codeMsg" shadow="never" class="message__card">
                    <template #header>
                      <div class="message__clearfix">
                        <div class="message__card--title">
                          {{ subItem.codeMsg.exampleJson.title }}
                        </div>
                        <div v-for="item in subItem.codeMsg.exampleJson.items" :key="item.id">
                          <span>{{ item.k }}：</span>
                          <span>{{ item.v }}</span>
                        </div>
                      </div>
                    </template>
                    <!--                  <div class="message__card&#45;&#45;detail"> -->
                    <!--                    <span>详情</span> -->
                    <!--                    <i class="el-icon-arrow-right"></i> -->
                    <!--                  </div> -->
                  </el-card>
                  <el-card v-else>
                    <div class="message__card--title">暂无消息模板</div>
                  </el-card>
                  <template #reference>
                    <el-button link> 预览 </el-button>
                  </template>
                </el-popover>
              </div>
            </td>
            <td class="flex__center">
              <div class="message__useStatus">
                <el-switch
                  v-model="subItem.mpOpen"
                  disabled
                  style="margin-right: 15px"
                  active-color="#409EFF"
                  inactive-color="#909399"
                  :active-value="1"
                  :inactive-value="0"
                  @change="(e: any) => useStatusChange(subItem, 'mpOpen', e)"
                />
                <el-popover trigger="click">
                  <el-card v-if="subItem.mpMsg && subItem.mpMsg !== '{}'" shadow="never" class="message__card">
                    <template #header>
                      <div class="message__clearfix">
                        <div class="message__card--title">
                          {{ subItem.mpMsg.exampleJson.title }}
                        </div>
                        <div v-for="item in subItem.mpMsg.exampleJson.items" :key="item.id">
                          <span>{{ item.k }}：</span>
                          <span>{{ item.v }}</span>
                        </div>
                      </div>
                    </template>
                    <!--                  <div class="message__card&#45;&#45;detail"> -->
                    <!--                    <span>详情</span> -->
                    <!--                    <i class="el-icon-arrow-right"></i> -->
                    <!--                  </div> -->
                  </el-card>
                  <template #reference>
                    <el-button link> 预览 </el-button>
                  </template>
                </el-popover>
              </div>
            </td>
          </tr>
        </template>
      </tbody>
    </table>
  </div>
</template>

<style lang="scss" scoped></style>
