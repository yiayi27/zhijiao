<script setup lang="ts">
import { getMemberCardInfoSwitch, getMemberPayRule, saveMemberPayRule, setMemberCardInfoStatus } from '@/api/customer/member'

// 定义会员卡规则项接口
interface PayRuleItem {
  state: number
  payAmount: number
  discountAmount: number
  [key: string]: any
}

// 定义会员卡信息类型
interface MemberInfo {
  memberModel?: number
  state?: number
  [key: string]: any
}

// 由于父组件可能有memberSwitch属性，这里使用any类型避免类型检查
const instance = getCurrentInstance()
const proxy = instance?.proxy as any

// 储值功能开关
const mSwitch = ref(1)

// 储值表单字段
const formData = reactive({
  state: 0,
  ruleList: [{ state: 1, payAmount: 0, discountAmount: 0 }] as PayRuleItem[],
})

const formRef = ref(null)

onMounted(() => {
  if (proxy?.$parent?.memberSwitch === 0) {
    mSwitch.value = 0
  }
  fetchMemberPayRule()
  getMemberCardInfoSwitch({} as any).then((res) => {
    const memberSwitchObj = (res.data || []).filter((v: MemberInfo) => v.memberModel === 1)[0]
    mSwitch.value = memberSwitchObj ? memberSwitchObj.state || 0 : 0
  })
})

// 获取会员储值数据
function fetchMemberPayRule() {
  getMemberPayRule({} as any).then(({ data }) => {
    if (data && data.length) {
      const newFormData = {
        state: data && data.length === 1 && data[0].state === 0 ? 0 : 1,
        ruleList: data,
      }
      Object.assign(formData, newFormData)
    }
  })
}

// 新增有优惠下的规则
function onAddRule() {
  if (formData.ruleList && formData.ruleList.length === 10) {
    ElMessage.error('储值规则最多只能10条')
    return false
  }
  const newRuleList = [...formData.ruleList, { state: 1, payAmount: 0, discountAmount: 0 }]
  formData.ruleList = newRuleList
}

// 删除储值规则
function onRemove(index: number) {
  formData.ruleList.splice(index, 1)
}

// 储值开关变化
function handelSwitch(v: any) {
  if (v === 0) {
    setMemberCardInfoStatus({
      memberCardInfoSwitch: 0,
      memberModel: 1,
    }).catch(() => {
      mSwitch.value = 1
    })
  } else {
    setMemberCardInfoStatus({
      memberCardInfoSwitch: 1,
      memberModel: 1,
    }).catch(() => {
      mSwitch.value = 0
    })
  }
}

// 保存储值管理
function onSave() {
  let isValue = true
  if (formData.state === 1) {
    formData.ruleList.forEach((v) => {
      if (!v.payAmount || !v.discountAmount) {
        isValue = false
      }
    })
    if (!isValue) {
      ElMessage.error('请填写完成储值规则!')
      return false
    }
  }
  saveMemberPayRule(
    formData.state
      ? formData.ruleList.map((v) => {
          v.state = 1
          return v
        })
      : [{ state: 0 }],
  ).then((res) => {
    if (res.code === 200) {
      ElMessage({
        type: 'success',
        message: '保存成功',
      })
    }
  })
}
</script>

<template>
  <div class="flex-1 overflow-auto">
    <div class="smember">
      <div class="smember__tip">会员卡储值功能帮助商家快速回笼资金，充实资金链。</div>
      <div class="smember__switch">
        <el-switch v-model="mSwitch" :active-value="1" :inactive-value="0" @change="handelSwitch" />
      </div>
    </div>
    <el-form ref="formRef" :model="formData" label-width="100px">
      <el-form-item label="是否有优惠" required>
        <el-radio-group v-model="formData.state">
          <el-radio :value="1"> 有优惠 </el-radio>
          <el-radio :value="0"> 无优惠 </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item v-if="formData.state === 1" label="储值规则" required>
        <div v-for="(item, index) in formData.ruleList" :key="index" style="margin-bottom: 12px">
          充&nbsp;
          <el-input-number v-model="item.payAmount" :min="0" :controls="false" style="width: 120px" />&nbsp;元, 送&nbsp;
          <el-input-number v-model="item.discountAmount" :min="0" :controls="false" style="width: 120px" />&nbsp;元 &nbsp;&nbsp;
          <el-button v-if="index + 1 < formData.ruleList.length" link @click="onRemove(index)"> 删除 </el-button>
          <el-button v-else link @click="onAddRule"> 添加 </el-button>
        </div>
      </el-form-item>
    </el-form>
    <br />
    <br />
    <br />
    <el-button type="primary" @click="onSave"> 保存 </el-button>
  </div>
</template>

<style lang="scss" scoped>
.smember {
  padding: 12px 24px;
  margin-bottom: 30px;
  overflow: hidden;
  background: rgb(244, 252, 255);
  > div {
    float: left;
  }

  &__tip {
    width: 80%;
    > span {
      display: inline-block;
      margin-top: 12px;
      color: red;
    }
  }

  &__switch {
    width: 15%;
    margin-left: 5%;
  }
}
</style>
