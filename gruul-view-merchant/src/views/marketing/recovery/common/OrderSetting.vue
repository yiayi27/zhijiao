<script setup lang="ts">
import { integralGoodLogMsg } from '@/api/integral/integral'

import { getRecoveryOrderList } from '@/api/recovery/recovery'
import MContent from '@/components/MContainer/MContent.vue'
import PageManage from '@/components/PageManage.vue'
import { ArrowLeft, ArrowRight, Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

// 定义接口
interface RecoveryOrderItem {
  avatarUrl?: string
  nickName?: string
  articleImgs?: string
  proofImgs?: string
  articleImgsList: string[]
  proofImgsList: string[]
  sendOut?: string
  shipmentName?: string
  shipmentNumber?: string
  [key: string]: any
}

interface SearchOption {
  value: string
  label: string
}

interface FormData {
  size: number
  page: number
  nickName: string
  shipmentNumber: string
}

interface ImageItem {
  img: string
}

const dialogVisible = ref(false)
const temImgList = ref<ImageItem[]>([])
const temImg = ref('')
const temObj = ref<RecoveryOrderItem>({} as RecoveryOrderItem)

/** 选择查看的下标 */
const index = ref(0)

/** 是否有物流信息 */
const hasLogistics = ref(false)
const logDetail = ref<any[]>([])
const logVisible = ref(false)

const form = reactive<FormData>({
  size: 20,
  page: 1,
  nickName: '',
  shipmentNumber: '',
})

/** 搜索内容 */
const searchText = ref('')
const searchVal = ref('1')

/** 不同条件搜索 */
const searchtList: SearchOption[] = [
  {
    value: '1',
    label: '微信昵称',
  },
  {
    value: '2',
    label: '物流单号',
  },
]

const total = ref(0)
const tableData = ref<RecoveryOrderItem[]>([])

const acitve = computed((): number => {
  return logDetail.value.length - 1
})

onMounted(() => {
  init()
})

/**
 * 搜索功能
 */
function searchBy(): void {
  init()
}

/**
 * 查看回收图
 */
function showReImg(item: RecoveryOrderItem, type: number): void {
  index.value = 0

  temImgList.value = []
  if (type === 1) {
    item.articleImgsList.forEach((imgItem: string) => {
      temImgList.value.push({
        img: imgItem,
      })
    })
    if (item.articleImgsList.length > 0) {
      dialogVisible.value = true
    }
  }
  if (type === 2) {
    item.proofImgsList.forEach((imgItem: string) => {
      temImgList.value.push({
        img: imgItem,
      })
    })
    if (item.proofImgsList.length > 0) {
      dialogVisible.value = true
    }
  }
  temImg.value = temImgList.value[0]?.img || ''
}

function perShow(): void {
  if (index.value === 0) {
    ElMessage.info('没有更多了')
    return
  }
  index.value--
  temImg.value = temImgList.value[index.value].img
}

function nextShow(): void {
  if (index.value === temImgList.value.length - 1) {
    ElMessage.info('没有更多了')
    return
  }
  index.value++
  temImg.value = temImgList.value[index.value].img
}

/**
 * 初始化信息
 */
function init(): void {
  if (searchVal.value === '') {
    ElMessage.error('请选择搜索类型')
    return
  }
  if (searchVal.value === '1') {
    form.nickName = searchText.value
    form.shipmentNumber = ''
  } else {
    form.nickName = ''
    form.shipmentNumber = searchText.value
  }
  getRecoveryOrderList(form).then((res: any) => {
    if (res.data) {
      res.data.forEach((item: RecoveryOrderItem) => {
        const proofImgs = item.proofImgs || ''
        const articleImgs = item.articleImgs || ''
        item.proofImgsList = !proofImgs ? [] : proofImgs.split(',')
        item.articleImgsList = !articleImgs ? [] : articleImgs.split(',')
      })
      tableData.value = res.data
      total.value = Number(res.totalCount)
    }
  })
}

/**
 * 查看物流
 */
function showLogitic(item: RecoveryOrderItem): void {
  temImg.value = item.articleImgsList[0]
  temObj.value = item
  logVisible.value = true
  let shipperType = 'sf'
  if (item.shipmentName === '顺丰快递') {
    shipperType = 'sf'
  }
  if (item.shipmentName === '中通快递') {
    shipperType = 'zt'
  }
  if (item.shipmentName === '圆通快递') {
    shipperType = 'yt'
  }
  if (item.shipmentName === '韵达快递') {
    shipperType = 'yd'
  }
  if (item.shipmentName === '百世快递') {
    shipperType = 'ht'
  }

  const param = {
    shipperType,
    waybillNo: item.shipmentNumber,
  }

  integralGoodLogMsg(param).then((res) => {
    const result = JSON.parse(res.data)
    if (result.code === 201101) {
      hasLogistics.value = false
    } else {
      hasLogistics.value = true
      logDetail.value = result
    }
  })
}

/**
 * @method handleSizeChange
 * @description 每页 条
 */
function handleSizeChange(val: number): void {
  form.size = val
  init()
}

/**
 * @method handleCurrentChange
 * @description 当前页
 */
function handleCurrentChange(val: number): void {
  form.page = val
  init()
}
</script>

<template>
  <MContent>
    <template #form>
      <div class="detPage__search">
        <div class="detPage__search--left">
          <el-input v-model="searchText" class="filter__right--input" clearable style="width: 320px" placeholder="输入关键词" @keyup.enter="searchBy">
            <template #prepend>
              <el-select v-model="searchVal" placeholder="搜索条件" class="w-[100px]">
                <el-option v-for="item in searchtList" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </template>
            <template #append>
              <el-button :icon="Search" @click="init" />
            </template>
          </el-input>
        </div>
      </div>
    </template>

    <template #table>
      <div class="detPage__table">
        <div class="detPage__table--title">
          <div class="content" style="width: 30%">微信昵称</div>
          <div class="content" style="width: 20%">回收图数</div>
          <div class="content" style="width: 10%">凭证图数</div>
          <div class="content" style="width: 20%">寄出时间</div>
          <div class="content" style="width: 10%">操作</div>
        </div>
        <div v-if="tableData.length !== 0">
          <div v-for="(item, tIndex) in tableData" :key="tIndex" class="detPage__table--list">
            <div class="content imgBox">
              <div style="width: 60px; height: 60px">
                <img style="width: 60px; height: 60px" :src="item.avatarUrl" alt="" />
              </div>
              <p style="margin-left: 20px; width: 150px">
                {{ item.nickName }}
              </p>
            </div>
            <div class="content" style="width: 20%">
              <div class="reImg" @click="showReImg(item, 1)">
                {{ item.articleImgsList.length }}
              </div>
            </div>
            <div class="content" style="width: 10%">
              <div class="reImg" @click="showReImg(item, 2)">
                {{ item.proofImgsList.length }}
              </div>
            </div>
            <div class="content" style="width: 20%">
              {{ item.sendOut }}
            </div>
            <div class="content" style="width: 10%">
              <div class="showLog" @click="showLogitic(item)">查看物流</div>
            </div>
          </div>
        </div>
        <div v-if="tableData.length === 0" class="detPage__table--noList">暂无数据</div>
      </div>
    </template>
    <el-dialog v-model="dialogVisible" width="600px">
      <div class="diaBox">
        <div class="diaBox__iStyle" @click="perShow">
          <el-icon class="iconStyle">
            <ArrowLeft />
          </el-icon>
        </div>
        <img :src="temImg" alt="" style="width: 240px; height: 300px" />
        <div class="diaBox__iStyle" @click="nextShow">
          <el-icon class="iconStyle">
            <ArrowRight />
          </el-icon>
        </div>
      </div>
      <div class="center">{{ index + 1 }}/{{ temImgList.length }}</div>
    </el-dialog>

    <el-dialog v-model="logVisible" width="500px" style="top: -10%">
      <div class="diaImgBox">
        <img :src="temImg" alt="" />
        <div class="diaImgBox__right">
          <div>{{ temObj.shipmentName }}</div>
          <div class="diaImgBox__right--number">物流单号: {{ temObj.shipmentNumber }}</div>
        </div>
      </div>

      <div class="digBox__orderMsg">
        <div v-if="!hasLogistics" class="digBox__orderMsg--orderType">暂无物流详情信息</div>
        <div v-else>
          <span style="font-weight: bold">物流详情</span>
          <el-steps direction="vertical" :active="acitve" style="margin-top: 20px">
            <el-step v-for="(item, i) of logDetail" :key="i">
              <template #icon>
                <div>{{ logDetail.length - i }}</div>
              </template>
              <template #title>
                <div>{{ item.context }}</div>
              </template>
              <template #description>
                <div>{{ item.time }}</div>
              </template>
            </el-step>
          </el-steps>
        </div>
      </div>
    </el-dialog>

    <PageManage
      :page-size="form.size"
      :page-num="form.page"
      :total="total"
      @handle-size-change="handleSizeChange"
      @handle-current-change="handleCurrentChange"
    />
  </MContent>
</template>

<style lang="scss" scoped>
.detPage {
  &__line {
    display: flex;
    padding-left: 10px;
    height: 60px;
    border-bottom: 1px solid #f0f0f0;
    align-items: center;

    &--title {
      margin-right: 40px;
      font-size: 14px;
      font-weight: bold;
      color: #586884;
    }

    &--con {
      margin-right: 40px;
    }
  }

  &__search {
    display: flex;
    justify-content: space-between;
    align-items: center;

    &--left {
      display: flex;
      align-items: center;

      .nikeName {
        color: #586884;
        margin-right: 20px;
        margin-left: 10px;
      }

      .searchBtn {
        padding: 8px 15px;
        border-radius: 50px;
        color: white;
        font-size: 14px;
        background-color: #2d99f3;
        margin-left: 40px;
        cursor: pointer;
      }
    }
  }

  &__table {
    &--title {
      height: 60px;
      background-color: #f5f6fa;
      color: #586884;
      display: flex;
      align-items: center;

      .content {
        display: flex;
        justify-content: center;
      }
    }

    &--list {
      display: flex;
      width: 100%;
      height: 80px;
      align-items: center;
      border-bottom: 1px solid #f5f6fa;
      color: #586884;

      .content {
        display: flex;
        justify-content: center;

        .reImg {
          color: #2d99f3;
          cursor: pointer;
        }

        .showLog {
          padding: 8px 10px;
          border-radius: 50px;
          background-color: #2d99f3;
          color: white;
          font-size: 12px;
          cursor: pointer;
        }
      }

      .imgBox {
        display: flex;
        justify-content: flex-start;
        align-items: center;
        flex-wrap: wrap;
        width: 30%;
        padding: 0 20px;
      }
    }

    &--noList {
      width: 100%;
      margin-top: 20px;
      padding-bottom: 20px;
      border-bottom: 1px solid #dcdfe6;
      color: #dcdfe6;
      display: flex;
      justify-content: center;
    }
  }
}

.diaBox {
  display: flex;
  padding: 0px 20px 10px 20px;
  justify-content: space-between;
  align-items: center;

  &__iStyle {
    width: 30px;
    height: 30px;
    border-radius: 50px;
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: #cccccc;
    cursor: pointer;
  }
}

.iconStyle {
  color: white;
}

.center {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.digBox {
  padding: 20px;

  &__orderMsg {
    margin-top: 10px;
    color: #8893a7;
    height: 480px;
    overflow: auto;

    &--orderType {
      background-color: #e9f3fb;
      margin-top: 20px;
      padding: 10px;
      display: flex;
      align-items: center;
      font-weight: bold;
      color: #697992;
    }

    &--line {
      margin-top: 10px;
      padding-bottom: 40px;
      border-bottom: 1px solid #e4e7ed;
    }
  }
}

.diaImgBox {
  border-bottom: 14px solid #f0f0f0;
  padding-bottom: 20px;
  display: flex;

  img {
    width: 100px;
    height: 80px;
  }

  &__right {
    margin-left: 20px;
    font-weight: bold;
    margin-top: 30px;

    &--number {
      margin-top: 15px;
    }
  }
}

:deep(.el-step.is-vertical .el-step__title) {
  font-size: 14px;
}

:deep(.el-dialog__header) {
  padding: 0px;
}
</style>
