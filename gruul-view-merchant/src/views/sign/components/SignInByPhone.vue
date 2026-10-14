<script setup lang="ts">
import type { FormInstance } from 'element-plus'
import { getCode, toGetAccountVerifyEnabled, unionAccountLogin } from '@/api/sign'
import { isDev, modifySignRefresh, modifySignStatus } from '@/libs'
import { MOBILE, PASSWORD, REMEMBER_ME } from '@/libs/enum/storage'
import { storage } from '@/libs/storage'
import { phoneReg } from '@/libs/validate'
import { LoginType } from '@/views/sign/type'

import { useRouter } from 'vue-router'

defineOptions({
  name: 'SignIn',
})

const emit = defineEmits(['change'])
const router = useRouter()

// 表单引用
const signFormRef = ref<FormInstance>()

// 密码输入框类型 true "password" false "text"
const inputType = ref(true)

const signForm = reactive({
  // 登录类型
  type: LoginType.MOBILE_PASSWORD,
  // 登录账号
  mobile: '',
  // 密码
  password: '',
  // 验证码
  code: '',
})

const countDown = reactive({
  // 按钮名称
  buttonName: '获取验证码',
  // 是否禁用
  isDisabled: false,
  // 倒计时时间
  time: 60,
})

const rememberMe = ref(false)
const loading = ref(false)

function validPhone(rule: any, value: string, callback: any) {
  if (!value) {
    callback(new Error('请输入手机号码'))
  } else if (!phoneReg.test(value)) {
    callback(new Error('请输入正确的11位手机号码'))
  } else {
    callback()
  }
}

const signRules = reactive({
  mobile: [{ required: true, validator: validPhone, trigger: 'blur' }],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
  password: [{ required: true, message: '请输入登录密码', trigger: 'blur' }],
})

function switchStatus(status: LoginType) {
  signForm.type = status
}

function changeSignType(value: string) {
  emit('change', value)
}

// 获取验证码
async function getVertifyCode(formName: string, type: number) {
  const res = await getCode({ phone: signForm.mobile, type })
  if (isDev) {
    signForm.code = res.data
  }

  ElMessage({
    type: 'success',
    message: '验证码已发送',
  })

  countDown.isDisabled = true
  const interval = window.setInterval(() => {
    countDown.buttonName = `${countDown.time}秒后重新发送`
    --countDown.time
    if (countDown.time < 0) {
      countDown.buttonName = '重新发送'
      countDown.time = 60
      countDown.isDisabled = false
      window.clearInterval(interval)
    }
  }, 1000)
}

async function submitForm() {
  if (!signFormRef.value) {
    return
  }

  await signFormRef.value.validate()
  loading.value = true
  const response = await unionAccountLogin(signForm, { grant_type: signForm.type }).finally(() => {
    loading.value = false
  })
  const { data } = response
  modifySignStatus(data.value)
  modifySignRefresh(data.refreshToken.value)
  setLoginStorage()
  await router.replace('/console')
}

function setLoginStorage() {
  storage.set(REMEMBER_ME, rememberMe.value, null)
  if (rememberMe.value) {
    storage.set(MOBILE, signForm.mobile, null)
    if (signForm.type === LoginType.MOBILE_PASSWORD) {
      storage.set(PASSWORD, signForm.password, null)
    }
  } else {
    storage.delete(MOBILE)
    storage.delete(PASSWORD)
  }
}

// 忘记密码
function forgotPassword() {
  router.push({ path: '/changepass', query: { type: 'forget' } })
}

function getLoginStorage() {
  const rememberMe = storage.get(REMEMBER_ME, false)
  const mobile = storage.get(MOBILE)
  const password = storage.get(PASSWORD)

  return {
    rememberMe,
    mobile,
    password,
  }
}

const scanEnabled = ref(false)
async function getScanVerifyEnabled() {
  const res = await toGetAccountVerifyEnabled()
  scanEnabled.value = res.data
}

onMounted(() => {
  getScanVerifyEnabled()
  const { mobile, password, rememberMe: rm } = getLoginStorage()
  rememberMe.value = rm as boolean
  signForm.mobile = mobile || '13800138000'
  signForm.password = password || 'admin123'
})
</script>

<template>
  <div v-loading="loading" class="SignFlow">
    <div class="SignFlow__tabs">
      <div class="SignFlow__tabs--title">LOG IN</div>
      <div v-if="scanEnabled" class="SignFlow__tabs--switch" @click="changeSignType('SignInByQrCode')">
        <div class="toolTip-right">扫码登录</div>
        <i class="iconfont iconQR_Code" />
      </div>
    </div>
    <el-form
      ref="signFormRef"
      :model="signForm"
      :rules="signRules"
      hide-required-asterisk
      label-position="left"
      label-width="85px"
      class="SignInForm"
    >
      <div class="SignInForm__tabs">
        <div
          class="SignInForm__tab"
          :class="{ 'SignInForm__tab--active': signForm.type === LoginType.MOBILE_PASSWORD }"
          @click="switchStatus(LoginType.MOBILE_PASSWORD)"
        >
          密码登录
        </div>
        <div
          class="SignInForm__tab"
          :class="{ 'SignInForm__tab--active': signForm.type === LoginType.SMS_CODE }"
          @click="switchStatus(LoginType.SMS_CODE)"
        >
          验证码登录
        </div>
      </div>
      <el-form-item label="手机号码" prop="mobile">
        <el-input v-model="signForm.mobile" auto-complete="off" placeholder="请输入手机号" @keyup.enter="submitForm" />
      </el-form-item>
      <el-form-item v-if="signForm.type === LoginType.MOBILE_PASSWORD" label="登录密码" prop="password">
        <div class="SignInForm__password flex w-full">
          <el-input
            v-model="signForm.password"
            :type="inputType ? 'password' : 'text'"
            placeholder="请输入密码"
            auto-complete="off"
            class="flex-1"
            @keyup.enter="submitForm"
          />
          <span class="ml-[8px] cursor-pointer" @click="inputType = !inputType">
            <i v-if="inputType" class="iconfont iconeye" />
            <i v-if="!inputType" class="iconfont iconicon_password_eye_on" />
          </span>
        </div>
      </el-form-item>
      <el-form-item v-if="signForm.type === LoginType.SMS_CODE" label="验证码" prop="code">
        <div class="SignInForm__code w-full flex items-center">
          <el-input v-model="signForm.code" placeholder="输入验证码" @keyup.enter="submitForm" />
          <el-button type="primary" plain round class="mb-[8px] flex-1" :disabled="countDown.isDisabled" @click="getVertifyCode('signForm', 1001)">
            {{ countDown.buttonName }}
          </el-button>
        </div>
      </el-form-item>
      <div class="SignInForm__option">
        <el-checkbox v-model="rememberMe"> 记住密码 </el-checkbox>
        <el-button link @click="forgotPassword"> 忘记密码 ? </el-button>
      </div>
      <el-button class="SignInForm__button" type="primary" @click="submitForm"> 登录 </el-button>
    </el-form>
  </div>
</template>

<style lang="scss" scoped>
@import '@/assets/styles/sign/sign';

// 覆盖样式
:deep(.el-input__wrapper) {
  box-shadow: none;
}

:deep(.el-form-item) {
  border-bottom: 1px solid #f8f5f9;
}

:deep(.el-checkbox__inner) {
  width: 12px;
  height: 12px;
}

:deep(.el-checkbox__inner::after) {
  height: 6px;
  left: 3px;
  top: 0;
}

:deep(.el-checkbox__label) {
  color: #909399;
  padding-left: 6px;
  font-size: 12px;
}

:deep(.SignInForm__option .el-button--text) {
  color: #909399;
}
</style>
