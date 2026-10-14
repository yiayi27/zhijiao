<script setup lang="ts">
import { getMemberCardRank, sendCode, setMember } from '@/api/customer/member'
import { phoneReg } from '@/libs/validate'

import { useRoute, useRouter } from 'vue-router'

// 定义类型接口
interface MemberRank {
  rankCode: string
  rankName: string
  memberPrivilegeVos?: {
    isSelected: number
    privilegeName: string
  }[]
}

defineOptions({
  name: 'SettingMember',
})

const route = useRoute()
const router = useRouter()

// Refs
const formRef = ref()

// 微信昵称
const nikeName = ref('')

// 微信头像
const avatarUrl = ref('')

// 验证码
const formCode = reactive({
  buttonName: '获取验证码',
  isDisabled: false,
  time: 60,
})

// 手机号会员等级
const formData = reactive({
  phone: null as null | string,
  code: '',
  rankCode: '',
})

// 会员下拉选项
const rankOptions = ref<MemberRank[]>([])

// 权益简介
const privilegeInfo = ref('')

// 表单校验规则
const formRules = {
  phone: [{ required: true, validator: validPhone, trigger: 'blur' }],
  code: [{ required: true, message: '输入验证码', trigger: 'blur' }],
  rankCode: [{ required: true, message: '请选择会员等级', trigger: 'change' }],
}

onMounted(() => {
  nikeName.value = route.query.nikeName as string
  avatarUrl.value = route.query.img as string
  getRankList()
})

// 等级下拉列表数据请求
function getRankList() {
  getMemberCardRank({}).then((res) => {
    if (res.code === 200) {
      rankOptions.value = res.data || []
    }
  })
}

function memberChange(val: string) {
  const curMember = rankOptions.value.find((v) => v.rankCode === val) || ({} as MemberRank)
  const privilegList = (curMember.memberPrivilegeVos || []).filter((v) => v.isSelected === 1)
  privilegeInfo.value = `该等级会员卡享受${privilegList.map((v) => v.privilegeName).join('、')}规则名称权益`
}

// 获取验证码
async function getVertifyCode() {
  let valid = '' as any
  formRef.value.validateField('phone', (res: string) => {
    valid = res
  })
  if (valid) return
  const res = await sendCode({
    phone: formData.phone,
  })
  if (res.code === 200) {
    formCode.isDisabled = false
    ElMessage.success('验证码发送成功！')
  }
  formCode.isDisabled = true
  const interval = window.setInterval(() => {
    formCode.buttonName = `${formCode.time}秒后重新发送`
    --formCode.time
    if (formCode.time < 0) {
      formCode.buttonName = '重新发送'
      formCode.time = 60
      formCode.isDisabled = false
      window.clearInterval(interval)
    }
  }, 1000)
}

// 号码校验
function validPhone(rule: any, value: string, callback: (error?: Error) => void) {
  if (!value) {
    callback(new Error('请输入电话号码'))
  } else if (!isvalidPhone(value)) {
    callback(new Error('请输入正确的11位手机号码'))
  } else {
    callback()
  }
}

// 判断是否为有效手机号
function isvalidPhone(str: string) {
  return phoneReg.test(str)
}

// 确认提交
function onOk() {
  formRef.value.validate((valid: boolean) => {
    if (valid) {
      setMember({
        userId: route.query.id,
        rankCode: formData.rankCode,
        phone: Number.parseInt(formData.phone || '0', 10),
        code: Number.parseInt(formData.code, 10),
      }).then(() => {
        ElMessage.success('会员设置成功')
        router.push({
          name: 'MemberList',
        })
      })
    }
  })
}
</script>

<template>
  <div>
    <span>设为会员</span>
    <el-form ref="formRef" :rules="formRules" label-width="120px" :model="formData">
      <div class="navLine">会员信息</div>
      <el-form-item label="微信头像">
        <img :src="avatarUrl" style="width: 40px; height: 40px; border-radius: 3px" />
      </el-form-item>
      <el-form-item label="微信昵称">
        {{ nikeName }}
      </el-form-item>
      <el-form-item prop="phone" label="手机号">
        <el-input v-model="formData.phone" style="width: 304px" />
      </el-form-item>
      <el-form-item prop="code" label="验证码">
        <el-input v-model="formData.code" style="width: 200px; margin-right: 12px" />
        <el-button :disabled="formCode.isDisabled" @click="getVertifyCode()">
          {{ formCode.buttonName }}
        </el-button>
      </el-form-item>
      <div class="navLine">会员卡信息</div>
      <el-form-item label="选择会员等级" prop="rankCode">
        <el-select v-model="formData.rankCode" style="width: 304px" @change="memberChange">
          <el-option v-for="item in rankOptions" :key="item.rankCode" :label="`${item.rankName}(LV${item.rankCode})`" :value="item.rankCode" />
        </el-select>
        <br />
        <!-- <span style="color: #999">{{ privilegeInfo }}</span> -->
      </el-form-item>
    </el-form>
    <div class="pageButtom">
      <el-button @click="router.push({ name: 'CustomerList' })"> 取消 </el-button>
      <el-button type="primary" @click="onOk"> 确定 </el-button>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.navLine {
  margin-top: 40px;
  margin-bottom: 40px;
  height: 40px;
  display: flex;
  -webkit-box-align: center;
  align-items: center;
  background-color: #f8f8f8;
  padding-left: 30px;
  font-weight: bold;
}

.pageButtom {
  margin-top: 80px;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
