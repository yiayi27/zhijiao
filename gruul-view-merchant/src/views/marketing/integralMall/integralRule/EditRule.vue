<script setup lang="ts">
import { integralRuleUpdate } from '@/api/integral/integral'
import { ElMessageBox } from 'element-plus'

import { onBeforeRouteLeave, useRouter } from 'vue-router'

// 定义类型
interface RuleParameter {
  value: number
  parameterId?: string | number
}

interface RuleItem {
  integralRuleParameterDtos: RuleParameter[]
  integralRuleId?: string | number
  open: boolean
  integralShopId?: string | number
  ruleType: number
  integralRuleInfo?: string
  integralValidity?: string | number
  useRuleIntroduce?: string
}

interface RuleModel {
  useRuleIntroduce: string
  integralValidity: number
  integralRuleInfo: string
}

const props = defineProps({
  comName: {
    type: String,
    default: '',
  },
  getList: {
    type: Array as PropType<RuleItem[]>,
    default: () => [],
  },
  ruleOption: {
    type: Object as PropType<RuleModel>,
    default: () => ({
      useRuleIntroduce: '',
      integralValidity: '',
      integralRuleInfo: '',
    }),
  },
})
const emit = defineEmits(['setComName'])
const router = useRouter()
// 获取父组件传递的数据
const ruleModel = ref<RuleModel>({ ...props.ruleOption })
const list = ref<RuleItem[]>([])

watchEffect(() => {
  list.value = [...props.getList]
})
const saveFlag = ref(false)

/** 验证规则 */
const regs = /^\+?[1-9]\d*$/

function vaildInput(e: Event) {
  const target = e.target as HTMLInputElement
  if (!regs.test(target.value)) {
    target.value = ''
  }
}

function conVaildInput(e: Event) {
  const target = e.target as HTMLInputElement
  if (!regs.test(target.value)) {
    target.value = ''
  }
  if (Number(target.value) > 7) {
    ElMessage.error('连续签到天数不可大于7天')
    target.value = ''
  }
}

/**
 * 保存编辑
 */
function saveRule() {
  let valid = true
  list.value.forEach((item) => {
    item.integralRuleInfo = ruleModel.value.integralRuleInfo
    item.integralValidity = ruleModel.value.integralValidity
    item.useRuleIntroduce = ruleModel.value.useRuleIntroduce
    if (item.open) {
      item.integralRuleParameterDtos.forEach((v) => {
        if (!v.value) {
          valid = false
        }
      })
    }
  })
  // if (list.value.length === 0) {
  //   list.value.push({
  //     integralRuleInfo: ruleModel.value.integralRuleInfo,
  //     integralValidity: ruleModel.value.integralValidity,
  //     useRuleIntroduce: ruleModel.value.useRuleIntroduce,
  //     // TODOS: list.value.length===0生成初始数据
  //     integralRuleParameterDtos: [
  //       { value: 0 },
  //       { value: 0 },
  //       { value: 0 },
  //       { value: 0 },
  //       { value: 0 },
  //     ],
  //     open: false,
  //     ruleType: 0,
  //   })
  // }

  const integralTime = regs.test(String(ruleModel.value.integralValidity))
  if (!integralTime) {
    ElMessage.error('请输入整数的积分有效期')
    return
  }
  if (!valid) {
    ElMessage.error('请为选中规则输入内容')
    return
  }

  integralRuleUpdate(list.value).then(() => {
    ElMessage.success('编辑成功')
    saveFlag.value = true
    emit('setComName', 'RuleContent')
  })
}

/**
 * 取消保存
 */
function cancelRule() {
  ElMessageBox.confirm('确定取消编辑积分规则页面吗？取消后，未保存的信息将不保留', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    emit('setComName', 'RuleContent')
  })
}

// 路由离开守卫
onBeforeRouteLeave((to, from, next) => {
  if (saveFlag.value) {
    next()
    return
  }

  ElMessageBox.confirm(`确定要退出${router.currentRoute.value.params.id ? '编辑' : '发布'}积分商品页面?退出后，未保存的信息将不会保留!`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    next()
  })
})
</script>

<template>
  <div>
    <div class="integral">
      <div class="integral__title">| 积分使用规则</div>
      <div class="integral__content">
        <el-input
          v-model="ruleModel.useRuleIntroduce"
          type="textarea"
          :rows="5"
          placeholder="请输入内容"
          style="width: 600px; font-size: 14px"
          maxlength="255"
        />
      </div>
      <div class="integral__title">| 积分有效期</div>
      <div class="integral__content" style="font-size: 14px">
        积分有效期为
        <el-input-number v-model="ruleModel.integralValidity" :min="0" :step="1" :controls="false" style="width: 50px" />个月
      </div>
      <div class="integral__title">| 积分值规则</div>
      <div class="integral__content">
        <el-input
          v-model="ruleModel.integralRuleInfo"
          type="textarea"
          :rows="3"
          placeholder="请输入内容"
          style="width: 600px; font-size: 14px"
          maxlength="450"
        />
      </div>
      <div class="integral__title">| 积分获取规则</div>
      <div v-for="(item, index) in list" :key="index" class="integral__content">
        <div v-if="item.integralRuleParameterDtos.length">
          <div v-if="item.ruleType === 0" style="font-size: 14px">
            <el-checkbox v-model="item.open" />
            <span style="margin-left: 10px">分享</span>
            <span style="color: #d3d3d3">（限制用户首次分享才可获得积分）</span>
            <div class="integral__content--ruleInput">
              每日首次分享获得
              <el-input-number
                v-model="item.integralRuleParameterDtos[0].value"
                :min="0"
                :step="1"
                :controls="false"
                style="width: 50px"
                @keyup="vaildInput($event)"
              />点积分
            </div>
          </div>
          <div v-if="item.ruleType === 1" style="font-size: 14px">
            <el-checkbox v-model="item.open" />
            <span style="margin-left: 10px">登录</span>
            <span style="color: #d3d3d3">（每日首次登录获得积分，7天中有2个节点可以额外赠送积分，第8天00:00数据清空）</span>
            <div class="integral__content--ruleInput">
              每日首次登录获得
              <el-input-number
                v-model="item.integralRuleParameterDtos[0].value"
                :min="0"
                step-strictly
                :step="1"
                :controls="false"
                style="width: 50px"
                @keyup="vaildInput($event)"
              />点积分,第
              <el-input-number
                v-model="item.integralRuleParameterDtos[1].value"
                :min="0"
                step-strictly
                :step="1"
                :controls="false"
                style="width: 50px"
                @keyup="vaildInput($event)"
              />天连续登录将额外获赠
              <el-input-number
                v-model="item.integralRuleParameterDtos[2].value"
                :min="0"
                :step="1"
                step-strictly
                :controls="false"
                style="width: 50px"
                @keyup="vaildInput($event)"
              />点积分；第
              <el-input-number
                v-model="item.integralRuleParameterDtos[3].value"
                :min="0"
                :step="1"
                step-strictly
                :controls="false"
                style="width: 50px"
                @keyup="conVaildInput($event)"
              />天连续登录将额外获得
              <el-input-number
                v-model="item.integralRuleParameterDtos[4].value"
                :min="0"
                :step="1"
                step-strictly
                :controls="false"
                style="width: 50px"
                @keyup="vaildInput($event)"
              />点积分。
            </div>
          </div>
          <div v-if="item.ruleType === 2" style="font-size: 14px">
            <el-checkbox v-model="item.open" />
            <span style="margin-left: 10px">签到</span>
            <span style="color: #d3d3d3">（每日签到获得积分，7天中有2个节点可以额外赠送积分，第8天00:00数据清空）</span>
            <div class="integral__content--ruleInput">
              每日首次签到获得
              <el-input-number
                v-model="item.integralRuleParameterDtos[0].value"
                :min="0"
                :step="1"
                step-strictly
                :controls="false"
                style="width: 50px"
                @keyup="vaildInput($event)"
              />点积分,第
              <el-input-number
                v-model="item.integralRuleParameterDtos[1].value"
                :min="0"
                :step="1"
                step-strictly
                :controls="false"
                style="width: 50px"
                @keyup="vaildInput($event)"
              />天连续签到将额外获赠
              <el-input-number
                v-model="item.integralRuleParameterDtos[2].value"
                :min="0"
                :step="1"
                step-strictly
                :controls="false"
                style="width: 50px"
                @keyup="vaildInput($event)"
              />点积分；第
              <el-input-number
                v-model="item.integralRuleParameterDtos[3].value"
                :min="0"
                :step="1"
                step-strictly
                :controls="false"
                style="width: 50px"
                @keyup="conVaildInput($event)"
              />天连续签到将额外获得
              <el-input-number
                v-model="item.integralRuleParameterDtos[4].value"
                :min="0"
                :step="1"
                step-strictly
                :controls="false"
                style="width: 50px"
                @keyup="vaildInput($event)"
              />点积分。
            </div>
          </div>
          <div v-if="item.ruleType === 3" style="font-size: 14px">
            <el-checkbox v-model="item.open" />
            <span style="margin-left: 10px">步数</span>
            <span style="color: #d3d3d3">（每天可以兑换多次，兑换只要到达步数都可兑换，超过固定步数额外赠送积分）</span>
            <div class="integral__content--ruleInput">
              每到达
              <el-input-number
                v-model="item.integralRuleParameterDtos[0].value"
                :min="0"
                :step="1"
                step-strictly
                :controls="false"
                style="width: 70px"
                @keyup="vaildInput($event)"
              />步数可获得
              <el-input-number
                v-model="item.integralRuleParameterDtos[1].value"
                :min="0"
                :step="1"
                step-strictly
                :controls="false"
                style="width: 50px"
                @keyup="vaildInput($event)"
              />点积分。
            </div>
          </div>
        </div>
      </div>
      <div class="bottom__btn">
        <el-button type="primary" @click="saveRule"> 保存 </el-button>
        <el-button @click="cancelRule"> 取消 </el-button>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.integral {
  font-size: 15px;
  padding-left: 15px;
  &__title {
    color: #64a9f4;
    font-weight: bold;
    font-size: 14px;
    margin-top: 20px;
  }
  &__content {
    margin-top: 20px;
    padding: 0px 20px;
    &--ruleInput {
      margin-left: 20px;
      margin-top: 10px;
      font-size: 14px;
    }
  }
}

.bottom__btn {
  margin-top: 40px;
}
</style>
