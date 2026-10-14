<script setup lang="ts">
import { cancelChoice, getEvaluateList, getManageSetting, reply, setChoice, setManageSetting } from '@/api/order'
import MContainer from '@/components/MContainer/MContainer.vue'
import { ElMessageBox } from 'element-plus'
import { cloneDeep } from 'lodash-es'

import { useRoute } from 'vue-router'
import { evaluationQuery, filterEmpty } from './common/order'
import EvaluationForm from './components/EvaluationForm.vue'

defineOptions({
  name: 'OrderEvaluation',
})

const route = useRoute()
const value = ref(true)
const data = ref([])
const total = ref(0)

// 查询条件
const query = ref(cloneDeep(evaluationQuery))

// 表格列定义
const columns = computed(() => [
  {
    label: '评价人',
    width: 200,
  },
  {
    label: '产品信息',
    width: 130,
  },
  {
    label: '商品评分',
  },
  {
    label: '评分内容',
    width: 250,
  },
  {
    label: '操作',
    width: 120,
  },
])

// 监听查询条件变化
watch(
  () => query.value,
  (newValue) => {
    onGetEvaluateList(newValue)
  },
  { deep: true },
)

// 获取评价列表
function onGetEvaluateList(queryParams = {}) {
  const params = Object.assign({}, query.value, queryParams)

  getEvaluateList(params).then((res) => {
    const { list, total: totalCount } = res.data
    total.value = totalCount
    data.value = list
  })
}

// 设置精选
function onSetChoice(id: Long) {
  setChoice([id]).then(() => {
    onGetEvaluateList()
    ElMessage.success('设置成功')
  })
}

// 取消精选
function onCancelChoice(id: Long) {
  cancelChoice([id]).then(() => {
    onGetEvaluateList()
    ElMessage.success('设置成功')
  })
}

// 回复订单
function onReply(id: Long) {
  ElMessageBox.prompt('', '商家回复', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputType: 'textarea',
    inputPattern: /^\s*\S[\s\S]*$/,
    inputErrorMessage: '回复不可为空',
  }).then((res: any) => {
    reply({ reply: res.value, id }).then(() => {
      onGetEvaluateList()
      ElMessage.success('回复成功')
    })
  })
}

// 设置管理配置
function onSetManageSetting(openEvaluate: boolean) {
  setManageSetting({ openEvaluate })
}

// 获取管理配置
function onGetManageSetting() {
  getManageSetting().then((res) => {
    value.value = res.data
  })
}

// 初始加载
onMounted(() => {
  onGetManageSetting()
  const queryParams = filterEmpty(Object.assign(cloneDeep(evaluationQuery), route.query))
  onGetEvaluateList(queryParams)
})
</script>

<template>
  <MContainer v-model:size="query.size" v-model:current="query.current" v-model:total="total" show-pagination class="evaluation">
    <template #form>
      <m-card class="evaluation__switch" hide-text="展开搜索条件" show-text="收起搜索条件">
        <span>关闭后小程序端将不显示评价模块</span>
        <el-switch v-model="value" @change="(e: any) => onSetManageSetting(e)" />
      </m-card>
      <EvaluationForm v-model="query" />
    </template>
    <template #table>
      <m-table :data="data" :custom="true" :columns="columns" :need-border="true">
        <template #header="{ row }">
          <div style="width: 100%">
            <span style="margin-right: 50px">订单号：{{ row.orderId }}</span>
            <span style="margin-right: 50px">评价时间：{{ row.createTime }}</span>
          </div>
        </template>
        <template #custom-body="{ row }">
          <template v-for="(goods, i) of row.itemList" :key="i">
            <tr class="body--content">
              <td>
                <div class="tw">
                  <img class="user--avatar" :src="row.userAvatarUrl" />
                  {{ row.userName }}
                </div>
              </td>
              <td>
                <div class="name">
                  {{ goods.productName }}
                </div>
              </td>
              <td class="text--center">
                <template v-if="goods.rate">
                  <i v-for="star of goods.rate" :key="star" style="color: #ffd21f" class="iconfont iconpingjiaxingxing" />
                </template>
                <template v-else> 0 </template>
              </td>
              <td>
                <div style="line-height: 19px">
                  {{ goods.comment }}
                </div>
                <div v-if="goods.picture" class="evaluation__table--imgs">
                  <img v-for="(img, index) of goods.picture.split(',')" :key="index" :src="img" alt="" />
                </div>
                <div v-if="goods.reply" class="evaluation__table--reply">
                  <div style="line-height: 18px">商家回复：{{ goods.reply }}</div>
                </div>
              </td>
              <td>
                <div class="btn-container">
                  <el-button v-if="!goods.choice" link @click="onSetChoice(goods.id)"> 精选 </el-button>
                  <el-button v-else link @click="onCancelChoice(goods.id)"> 取消精选 </el-button>
                </div>
                <div class="btn-container">
                  <el-button v-if="!goods.reply" link @click="onReply(goods.id)"> 回复 </el-button>
                </div>
              </td>
            </tr>
          </template>
        </template>
      </m-table>
    </template>
  </MContainer>
</template>

<style lang="scss" scoped>
@import '../../assets/styles/order/evaluation.scss';
</style>
