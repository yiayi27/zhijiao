<script setup lang="ts">
import { integralRule } from '@/api/integral/integral'

// 定义类型
interface RuleOption {
  useRuleIntroduce: string
  integralValidity: string
  integralRuleInfo: string
}

interface RuleParameter {
  value: string | number
  parameterId?: string | number
}

interface RuleItem {
  integralRuleParameterDtos: RuleParameter[]
  integralRuleId: string | number
  open: boolean
  integralShopId: string | number
  ruleType: number
}

const emit = defineEmits(['editRule', 'setComName'])

const ruleOption = ref<RuleOption>({
  useRuleIntroduce: '',
  integralValidity: '',
  integralRuleInfo: '',
})

const getList = ref<RuleItem[]>([])

// /** 规则说明 */
// const getRule = [
//   '每日首次分享获得20点积分',
//   '每日首次登录获得20点积分,第3天联系登录将额外获赠10点积分；第7天连续登录将额外获得20点积分。',
//   ' 每日首次签到获得20点积分,第3天联系签到将额外获赠10点积分；第7天连续签到将额外获得20点积分。',
//   '每到达1000步数可获得20点积分。',
// ]

function editRule() {
  emit('setComName', 'EditRule')
  emit('editRule', ruleOption.value, getList.value)
}

async function getRules() {
  const res = await integralRule({})
  const newList: RuleItem[] = []
  res.data.forEach((item: any) => {
    ruleOption.value.useRuleIntroduce = item.useRuleIntroduce
    ruleOption.value.integralValidity = item.integralValidity
    ruleOption.value.integralRuleInfo = item.integralRuleInfo
    // TODOS: item.integralRuleParameterDtos.length===0生成初始数据
    newList.push({
      integralRuleParameterDtos: item.integralRuleParameterDtos,
      integralRuleId: item.integralRuleId,
      open: item.open,
      integralShopId: item.integralShopId,
      ruleType: item.ruleType,
    })
  })
  getList.value = newList
}

onMounted(() => {
  getRules()
})
</script>

<template>
  <div>
    <el-button type="primary" @click="editRule"> 编辑积分规则 </el-button>
    <div class="integral">
      <div class="integral__title">| 积分使用规则</div>
      <div class="integral__content" style="font-size: 14px">
        {{ ruleOption.useRuleIntroduce }}
      </div>
      <div class="integral__title">| 积分有效期</div>
      <div class="integral__content" style="font-size: 14px">积分有效期为{{ ruleOption.integralValidity }}个月</div>
      <div class="integral__title">| 积分值规则</div>
      <div class="integral__content" style="font-size: 14px">
        {{ ruleOption.integralRuleInfo }}
      </div>
      <div class="integral__title">| 积分获取规则</div>
      <div v-for="(item, index) in getList" :key="index" class="integral__content" style="font-size: 14px">
        <div v-if="item.integralRuleParameterDtos.length">
          <div v-if="item.ruleType === 0 && item.open">每日首次分享获得{{ item.integralRuleParameterDtos[0].value }}点积分</div>
          <div v-if="item.ruleType === 1 && item.open">
            每日首次登录获得{{ item.integralRuleParameterDtos[0].value }}点积分, 第{{ item.integralRuleParameterDtos[1].value }}天连续登录将额外获赠{{
              item.integralRuleParameterDtos[2].value
            }}点积分； 第{{ item.integralRuleParameterDtos[3].value }}天连续登录将额外获得{{ item.integralRuleParameterDtos[4].value }}点积分。
          </div>
          <div v-if="item.ruleType === 2 && item.open">
            每日首次签到获得{{ item.integralRuleParameterDtos[0].value }}点积分, 第{{ item.integralRuleParameterDtos[1].value }}天连续签到将额外获赠{{
              item.integralRuleParameterDtos[2].value
            }}点积分； 第{{ item.integralRuleParameterDtos[3].value }}天连续签到将额外获得{{ item.integralRuleParameterDtos[4].value }}点积分。
          </div>
          <div v-if="item.ruleType === 3 && item.open">
            每到达{{ item.integralRuleParameterDtos[0].value }}步数可获得{{ item.integralRuleParameterDtos[1].value }}点积分。
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.integral {
  font-size: 15px;
  &__title {
    color: #64a9f4;
    font-weight: bold;
    font-size: 14px;
    margin-top: 20px;
  }
  &__content {
    margin-top: 20px;
    padding: 0px 20px;
  }
}
</style>
