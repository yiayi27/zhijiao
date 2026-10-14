<script setup lang="ts">
import { getCommodityDet, getUserCommodityList } from '@/api/commodity/commodity'
import MContent from '@/components/MContainer/MContent.vue'
import PageManage from '@/components/PageManage.vue'

const form = reactive({
  pageSize: 20,
  pageNum: 1,
  input: '',
  getTypeValue: 1,
})

const total = ref(0)

const activeTypeList = [
  {
    id: 1,
    label: '按基金升序',
  },
  {
    id: 2,
    label: '按基金降序',
  },
  {
    id: 3,
    label: '按最近交易时间升序',
  },
  {
    id: 4,
    label: '按最近交易时间降序',
  },
]

const tableData = ref<any[]>([])
const commodityDetail = ref<any>({})
onMounted(() => {
  init()
})

/**
 * 初始化信息
 */
async function init() {
  getCommodity()
  getUserContent()
}

async function getCommodity() {
  const res = await getCommodityDet()
  commodityDetail.value = res.data
}

/**
 * 获取基金列表详情
 */
async function getUserContent() {
  const param = {
    nickName: form.input,
    page: form.pageNum,
    size: form.pageSize,
    sortType: form.getTypeValue,
  }

  const res = (await getUserCommodityList(param)) as any
  tableData.value = res.data ? res.data : []
  total.value = Number(res.totalCount)
}

/**
 * 下拉框筛选
 */
function selectFun(e: number) {
  form.getTypeValue = e
  getUserContent()
}

/**
 * @method handleSizeChange
 * @description 每页 条
 */
function handleSizeChange(val: number) {
  form.pageSize = val
  getUserContent()
}

/**
 * @method handleCurrentChange
 * @description 当前页
 */
function handleCurrentChange(val: number) {
  form.pageNum = val
  getUserContent()
}

// 导出方法供父组件使用
defineExpose({
  init,
  getUserContent,
  selectFun,
  handleSizeChange,
  handleCurrentChange,
})
</script>

<template>
  <MContent class="detPage">
    <template #form>
      <div class="detPage__search">
        <div class="detPage__search--left">
          <div class="nikeName">微信昵称</div>
          <el-input v-model="form.input" style="width: 120px" type="text" />
          <div class="searchBtn" @click="getUserContent">搜索</div>
        </div>

        <el-space>
          <div class="flex items-center">
            <div class="detPage__line--title">基金总额</div>
            <div class="detPage__line--con">{{ commodityDetail.totalFundQuota || 0 }}元</div>
            <div class="detPage__line--title">订单总数</div>
            <div class="detPage__line--con">{{ commodityDetail.totalOrderNumber || 0 }}单</div>
          </div>
          <el-select v-model="form.getTypeValue" placeholder="按基金升序" class="w-[200px]" @change="selectFun">
            <el-option v-for="item in activeTypeList" :key="item.id" :label="item.label" :value="item.id" />
          </el-select>
        </el-space>
      </div>
    </template>

    <template #table>
      <div class="detPage__table">
        <div class="detPage__table--title">
          <div class="content" style="width: 30%">微信昵称</div>
          <div class="content" style="width: 20%">交易总额</div>
          <div class="content" style="width: 10%">订单数</div>
          <div class="content" style="width: 20%">最近交易时间</div>
          <div class="content" style="width: 10%">基金值</div>
        </div>
        <div v-for="(item, index) in tableData" :key="index" class="detPage__table--list">
          <div class="content imgBox">
            <div style="width: 60px; height: 60px">
              <img style="width: 60px; height: 60px" :src="item.avatarUrl" alt="" />
            </div>
            <p style="margin-left: 20px; width: 150px">
              {{ item.nickName }}
            </p>
          </div>
          <div class="content" style="width: 20%">
            {{ item.totalAmount }}
          </div>
          <div class="content" style="width: 10%">
            {{ item.orderNumber }}
          </div>
          <div class="content" style="width: 20%">
            {{ item.lastPayTime }}
          </div>
          <div class="content" style="width: 10%">
            {{ item.totalFundQuota }}
          </div>
        </div>
        <div v-if="tableData.length === 0" class="detPage__table--noList">暂无数据</div>
      </div>
    </template>

    <template #pagination>
      <PageManage
        :page-size="form.pageSize"
        :page-num="form.pageNum"
        :total="total"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
      />
    </template>
  </MContent>
</template>

<style lang="scss" scoped>
.detPage {
  &__line {
    display: flex;
    padding-left: 10px;
    height: 40px;
    border-bottom: 1px solid #f0f0f0;
    align-items: center;

    &--title {
      margin-right: 10px;
      font-size: 14px;
      font-weight: bold;
      color: #586884;
    }

    &--con {
      margin-right: 20px;
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
      }

      .imgBox {
        display: flex;
        justify-content: center;
        align-items: center;
        width: 30%;
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

.setImgBox {
  width: 150px;
  display: flex;
  align-items: center;

  p {
    margin-left: 20px;
  }
}
</style>
