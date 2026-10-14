<script setup lang="ts">
import { editIntegralGoodsDetail, getIntegralGoodsDetail, putIntegralGoods } from '@/api/integral/integral'
import { mapToRemoveAttrs } from '@/libs/base'
import { storage } from '@/libs/storage'
import { ElMessageBox } from 'element-plus'

import { onBeforeRouteLeave, useRoute, useRouter } from 'vue-router'

interface FormModel {
  id: string
  integralGoodName: string
  guidePrice: number | null
  inventory: number | null
  integralPrice: number | null
  attributeId: string
  attributeName: string
  interalGoodsAttibutes: any[]
  pic: string
  albumPics: string
  freightTemplateId: string
  integralFreightTemplateDtos: any[]
  sales: string
  detail: string
  status: string
  weight: string
}

defineOptions({
  name: 'IssueGoodAdd',
})

// 使用异步组件定义
const componentsMap = {
  CodeGoodBaseInfo: defineAsyncComponent(() => import('./issueGoodForm/CodeGoodBaseInfo.vue')),
  CodeGoodSellInfo: defineAsyncComponent(() => import('./issueGoodForm/CodeGoodSellInfo.vue')),
}

const route = useRoute()
const router = useRouter()

// 步骤
const active = ref(1)

// 商品id
const goodId = ref('')

// 是否需要退出提示
const saveFlag = ref(false)

// 当前步骤
const currentStep = ref<keyof typeof componentsMap>('CodeGoodBaseInfo')

const componentRef = ref()

const formModel = reactive<FormModel>({
  // 商品id
  id: '',
  // 商品名称
  integralGoodName: '',
  // 市场价
  guidePrice: null,
  // 库存
  inventory: null,
  // 积分
  integralPrice: null,
  // 商品属性模板id
  attributeId: '',
  // 模板属性
  attributeName: '',
  // 模板属性列表
  interalGoodsAttibutes: [],
  // 商品主图
  pic: '',
  // 商品列表
  albumPics: '',
  // 运费模板ID
  freightTemplateId: '',
  // 运费模板信息
  integralFreightTemplateDtos: [],
  // 初始销量
  sales: '',
  // 积分商品详情
  detail: '',
  // 状态
  status: '',
  // 重量
  weight: '',
})

onMounted(() => {
  getDetail()
})

async function next() {
  const res = await getGoodFormModel()
  if (currentStep.value === 'CodeGoodBaseInfo') {
    const data = componentRef.value.baseModel
    Object.assign(formModel, { ...data })
    if (!componentRef.value.isWeight) {
      formModel.weight = ''
    }
  }
  if (res) {
    if (Number(formModel.integralPrice) <= 0) {
      ElMessage.error('积分价不能小于0')
      return
    }
    active.value = 2
    currentStep.value = 'CodeGoodSellInfo'
  }
}

/**
 * 验证表单
 */
async function getGoodFormModel() {
  try {
    await componentRef.value.getFormModel()
    return true
  } catch (error) {
    console.error(error)
    ElMessage.warning('请将信息填写完整')
    return false
  }
}

function setSaveFlag(flag: boolean) {
  saveFlag.value = flag
}

/**
 * 发布积分商品
 */
async function putWayCode() {
  storage.delete('baseModel')
  const res = componentRef.value.getFormModel()
  formModel.detail = res.detail
  saveFlag.value = true
  const integralGoodsAttibuteDtos = mapToRemoveAttrs<Record<string, any>>(formModel.interalGoodsAttibutes, ['id'])
  const query = {
    ...formModel,
    detail: res.detail,
    integralGoodsAttibuteDtos,
  }

  const fn = formModel.id ? editIntegralGoodsDetail : putIntegralGoods
  await fn(query)
  getBackIndex()
}

/**
 * 返回发布页面
 */
function getBackIndex() {
  ElMessage.success(`${formModel.id ? '编辑成功' : '上架成功'}`)
  router.push({
    name: 'IntegralMallIndex',
    query: { comName: 'ProductList' },
  })
}

/**
 * 取消发布
 */
function cancel() {
  router.push({
    name: 'IntegralMallIndex',
    query: { comName: 'ProductList' },
  })
}

/**
 * 编辑获取积分商品
 */
async function getDetail() {
  goodId.value = route.query.id as string
  if (goodId.value) {
    const param = {
      id: goodId.value,
    }
    const res = await getIntegralGoodsDetail(param)
    Object.assign(formModel, res.data)
  }
}

// 路由离开前确认
onBeforeRouteLeave((to, from, next) => {
  if (saveFlag.value) {
    next()
    return
  }
  ElMessageBox.confirm(`确定要退出${route.query.id ? '编辑' : '发布'}积分商品页面?退出后，未保存的信息将不会保留!`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    storage.delete('baseModel')
    next()
  })
})
</script>

<template>
  <div>
    <el-steps :active="active" simple>
      <el-step title="1.编辑基本信息" icon="none" />
      <el-step title="2.编辑商品信息" icon="none" />
    </el-steps>
    <keep-alive>
      <component :is="componentsMap[currentStep]" ref="componentRef" :form-model="formModel" @save-flag="setSaveFlag" />
    </keep-alive>
    <div class="pageButtom">
      <el-button v-if="active === 1" type="primary" @click="next"> 下一步 </el-button>
      <div v-else>
        <el-button
          type="primary"
          @click="
            () => {
              active = 1
              currentStep = 'CodeGoodBaseInfo'
            }
          "
        >
          上一步
        </el-button>
        <el-button type="primary" @click="putWayCode"> 上架 </el-button>
        <el-button @click="cancel"> 取消 </el-button>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.pageButtom {
  margin-top: 40px;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
