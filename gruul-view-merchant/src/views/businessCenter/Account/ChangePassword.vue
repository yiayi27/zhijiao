<script setup lang="ts">
import { changePassword, forgetPassword, getCode, getSystemConfig, verifyTheCode } from '@/api/sign'
import config from '@/config'
import { logout } from '@/libs'
import { useUserStore } from '@/store/modules/user'
import { Lock } from '@element-plus/icons-vue'

import { useRoute } from 'vue-router'

defineOptions({
  name: 'ChangePass',
})

const route = useRoute()
const userStore = useUserStore()

/** 身份验证状态值 */
const validate = ref(false)

// 手机表单
const phoneForm = reactive({
  phone: userStore.iphone,
  code: null,
  certificate: null,
  buttonName: '获取验证码',
  isDisabled: false,
  time: 60,
})

// 密码表单
const passForm = reactive({
  password: '',
  dbpass: '',
})

const type = ref('')

onMounted(() => {
  if (route.query.type && route.query.type === 'forget') {
    type.value = 'forget'
  } else {
    type.value = ''
  }
  validate.value = false
  phoneForm.phone = userStore.iphone
  phoneForm.code = null
  phoneForm.certificate = null
  phoneForm.buttonName = '获取验证码'
  phoneForm.isDisabled = false
  phoneForm.time = 60
  passForm.password = ''
  passForm.dbpass = ''
})

function onChangePassword() {
  if (type.value === 'forget') {
    forgetSubmit()
  } else {
    restPass()
  }
}

// 用户修改密码
async function restPass() {
  const form = {
    phone: phoneForm.phone,
    certificate: phoneForm.certificate,
    passwd: passForm.dbpass,
  }
  const response = await changePassword(form)
  const { code } = response
  if (code === 200) {
    ElMessage({
      type: 'success',
      message: '更改密码成功！',
    })
    await goBack()
  }
}

// 忘记密码表单提交
async function forgetSubmit() {
  const form = {
    phone: phoneForm.phone,
    certificate: phoneForm.certificate,
    passwd: passForm.dbpass,
  }
  const response = await forgetPassword(form)
  const { code } = response
  if (code === 200) {
    ElMessage({
      type: 'success',
      message: '更改密码成功！',
    })
    goBack()
  }
}

// 获取验证码
async function getVertifyCode(_formName: string, type: number) {
  const response = await getCode({
    phone: phoneForm.phone,
    type,
  })
  const { code } = response
  if (code === 200) {
    ElMessage({
      type: 'success',
      message: '验证码已发送',
    })
  }
  phoneForm.isDisabled = true
  const interval = window.setInterval(() => {
    phoneForm.buttonName = `${phoneForm.time}秒后重新发送`
    --phoneForm.time
    if (phoneForm.time < 0) {
      phoneForm.buttonName = '重新发送'
      phoneForm.time = 60
      phoneForm.isDisabled = false
      window.clearInterval(interval)
    }
  }, 1000)
}

async function nextClickHandle() {
  const form = {
    phone: phoneForm.phone,
    code: phoneForm.code,
    type: 1004,
  }
  const response = await verifyTheCode(form)
  const { code, data } = response
  if (code === 200) {
    phoneForm.certificate = data
    validate.value = true
  }
}

const resetDisabled = computed(() => {
  let isValidate = false
  // 忘记密码并且填入新密码
  if (type.value === 'forget' && passForm.dbpass) {
    isValidate = true
  }
  // 修改密码
  if (!passForm.password || !passForm.dbpass) {
    isValidate = true
  }
  if (passForm.password !== passForm.dbpass) {
    isValidate = true
  }
  return isValidate
})

function checkPassword() {
  if (type.value === 'forget' && !passForm.dbpass) {
    ElMessage({
      type: 'warning',
      message: '请输入新密码',
    })
    return
  }
  if (passForm.password !== passForm.dbpass) {
    ElMessage({
      type: 'warning',
      message: '请输入相同的密码',
    })
  }
}

async function goBack() {
  logout()

  const response = await getSystemConfig()
  const { code, data } = response
  if (code === 200 && data.systemConfig && data.systemConfig.consoleUrl) {
    window.open(`${data.systemConfig.consoleUrl}`, '_top')
  }
}
</script>

<template>
  <div class="change-password">
    <div class="logo" @click="goBack">
      <img class="pointer" :src="config.logo" />
    </div>
    <div class="resetBox">
      <div>
        <div class="middle">
          <el-icon><Lock /></el-icon>
          <span v-if="type === 'forget'">忘记密码</span>
          <span v-else>修改密码</span>
        </div>
        <div class="text-info">请输入您的登录手机号，以进行密码重设</div>
        <div v-if="!validate" class="resetPass">
          <el-form :model="phoneForm">
            <el-form-item>
              <el-input v-model="phoneForm.phone" :maxlength="11" placeholder="请输入您的手机号" :disabled="type !== 'forget'" />
            </el-form-item>
            <el-form-item>
              <div class="phoneCode w-full">
                <el-input v-model="phoneForm.code" class="phoneCode--item" placeholder="验证码" />
                <el-button class="phoneCode--item" :disabled="phoneForm.isDisabled" type="primary" @click="getVertifyCode('phoneForm', 1004)">
                  {{ phoneForm.buttonName }}
                </el-button>
              </div>
            </el-form-item>
            <el-form-item style="margin-top: 50px">
              <el-button type="primary" style="width: 100%; height: 40px" @click="nextClickHandle"> 下一步 </el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
      <div v-if="validate" class="resetPass">
        <el-form :model="passForm">
          <el-form-item label="输入新密码" prop="password">
            <el-input v-model="passForm.password" type="password" placeholder="请输入新密码" />
          </el-form-item>
          <el-form-item label="再次输入新密码" prop="dbpass">
            <el-input v-model="passForm.dbpass" type="password" placeholder="请输入新密码" @blur="checkPassword" />
          </el-form-item>
          <el-form-item style="margin-top: 50px">
            <el-button type="primary" :disabled="resetDisabled" style="width: 100%; height: 40px" @click="onChangePassword"> 确定 </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
    <div class="text-center copyright">Copyright © MEDUSA 2017. All Rights Rrserved.宁波美杜莎企业孵化器管理有限公司 版权所有</div>
  </div>
</template>

<style lang="scss" scoped>
.change-password {
  background-color: #eef1f6;
  color: #515a6e;
}

.resetBox {
  width: 80%;
  height: 445px;
  margin: 0 auto;
  background-color: #fff;
  padding: 50px 40px 0;

  .middle {
    font-size: 28px;
    padding-bottom: 15px;
    border-bottom: 1px solid #ededed;

    i {
      color: #409eff;
    }

    span {
      color: #303030;
      padding: 0 15px;
    }
  }

  .text-info {
    padding-top: 15px;
    font-size: 14px;
    color: #606266;
  }
}

.logo {
  width: 184px;
  margin: 0 auto 40px;
  padding-top: 128px;

  .pointer {
    width: 100%;
    height: auto;
  }
}

.resetPass {
  width: 420px;
  margin: 0 auto;

  .phoneCode {
    display: flex;
    justify-content: space-between;

    .phoneCode--item {
      flex: 0 0 49%;
    }
  }
}

.copyright {
  padding: 200px 0 100px;
  text-align: center;
}
</style>
