<script setup lang="ts">
import { changePhone, checkoutAccount, getCode, verifyTheCode } from '@/api/sign'
import { InfoFilled } from '@element-plus/icons-vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false,
  },
  userInfo: {
    type: Object,
    default: () => ({}),
  },
})

const emit = defineEmits(['update:visible', 'refreshDataList'])

const dialogVisible = ref(props.visible)

watch(
  () => props.visible,
  (newVal) => {
    dialogVisible.value = newVal
  },
)

/** 身份验证表单 */
const queryForm = reactive({
  phone: null,
  code: null,
  oneCertificate: null,
  buttonName: '获取验证码',
  isDisabled: false,
  time: 60,
})

watch(
  () => dialogVisible.value,
  (newVal) => {
    emit('update:visible', newVal)
    if (newVal) {
      initStatus()
      queryForm.phone = props.userInfo.phone
    }
  },
)

/** 手机换绑表单 */
const phoneForm = reactive({
  phone: '',
  code: null,
  twoCertificate: null,
  buttonName: '获取验证码',
  isDisabled: false,
  time: 60,
})

/** 身份验证状态值 */
const validate = ref(false)

function initStatus() {
  Object.assign(queryForm, {
    phone: null,
    code: null,
    oneCertificate: null,
    buttonName: '获取验证码',
    isDisabled: false,
    time: 60,
  })
  Object.assign(phoneForm, {
    phone: null,
    code: null,
    twoCertificate: null,
    buttonName: '获取验证码',
    isDisabled: false,
    time: 60,
  })
  validate.value = false
}

function isvalidPhone(str: string) {
  const reg = /^1(3\d|4\d|5\d|6\d|7\d|8\d|9[1,89])\d{8}$/
  return reg.test(str)
}

async function checkPhoneAccount(formName: string, showMessage = true) {
  const form = formName === 'queryForm' ? queryForm : phoneForm

  if (!isvalidPhone(form.phone!)) {
    ElMessage({
      type: 'warning',
      message: '请输入正确的手机号',
    })
    return false
  }

  try {
    const res = await checkoutAccount({
      phone: form.phone,
      // 账号存在 500 账号不存在 200
      type: 2,
    })
    if (res.code === 200) {
      if (formName === 'queryForm') {
        form.isDisabled = true
        return false
      }
      if (formName === 'phoneForm') {
        form.isDisabled = false
        return true
      }
    }
    return true
  } catch (e) {
    if (showMessage) {
      ElMessage({
        type: 'warning',
        message: e as string,
      })
    }
    if (formName === 'queryForm') {
      form.isDisabled = false
      return true
    }
    if (formName === 'phoneForm') {
      form.isDisabled = true
      return false
    }
    return false
  }
}

// 获取验证码
async function getVertifyCode(formName: string, type: number) {
  const form = formName === 'queryForm' ? queryForm : phoneForm

  const status = await checkPhoneAccount(formName, false)
  if (!status) {
    return
  }

  const response = await getCode({
    phone: form.phone,
    type,
  })

  const { code } = response
  if (code === 200) {
    ElMessage({
      type: 'success',
      message: '验证码已发送',
    })
  } else {
    ElMessage({
      message: '获取失败,请重新获取',
      type: 'warning',
      center: true,
    })
  }

  form.isDisabled = true
  const interval = window.setInterval(() => {
    form.buttonName = `${form.time}秒后重新发送`
    --form.time
    if (form.time < 0) {
      form.buttonName = '重新发送'
      form.time = 60
      form.isDisabled = false
      window.clearInterval(interval)
    }
  }, 1000)
}

async function nextClickHandle() {
  const form = {
    phone: queryForm.phone,
    code: queryForm.code,
    type: 1003,
  }
  const response = await verifyTheCode(form)
  const { code, data } = response
  if (code === 200) {
    queryForm.oneCertificate = data
    validate.value = true
  }
}

async function comfirmClickHandle() {
  const phoneFormData = {
    phone: phoneForm.phone,
    code: phoneForm.code,
    type: 1003,
  }
  const phoneResult = await verifyTheCode(phoneFormData)
  const { code, data } = phoneResult
  if (code === 200) {
    phoneForm.twoCertificate = data
  }
  const form = {
    oldPhone: queryForm.phone,
    oneCertificate: queryForm.oneCertificate,
    newPhone: phoneForm.phone,
    twoCertificate: phoneForm.twoCertificate,
  }
  const res = await changePhone(form)
  if (res.code === 200) {
    ElMessage({
      type: 'success',
      message: '换绑手机成功！',
    })
    dialogVisible.value = false
    emit('refreshDataList')
  }
}

/**
 * 关闭弹窗
 */
function closeHandle() {
  dialogVisible.value = false
}
</script>

<template>
  <el-dialog v-model="dialogVisible" class="dialog" :width="validate ? '400px' : '600px'" title="换绑手机号">
    <div v-if="!validate" class="verify-phone">
      <el-tag class="tag">
        <el-icon><InfoFilled /></el-icon>
        为了您的账号安全，进行敏感操作前须先验证身份。
      </el-tag>
      <el-form :model="queryForm" class="queryForm" label-width="120px" label-position="left">
        <el-form-item label="验证方式"> 手机验证 </el-form-item>
        <el-form-item label="绑定手机">
          {{ queryForm.phone }}
        </el-form-item>
        <el-form-item label="验证码">
          <div class="phoneCode">
            <el-input v-model="queryForm.code" style="width: 120px; margin-right: 15px" placeholder="6位数字验证码" />
            <el-button :disabled="queryForm.isDisabled" type="primary" @click="getVertifyCode('queryForm', 1003)">
              {{ queryForm.buttonName }}
            </el-button>
          </div>
        </el-form-item>
      </el-form>
    </div>
    <div v-if="validate" class="change-phone">
      <div style="margin-bottom: 20px">
        <el-input v-model="phoneForm.phone" placeholder="请输入手机号" @blur="checkPhoneAccount('phoneForm')" />
      </div>
      <div class="phoneCode">
        <el-input v-model="phoneForm.code" placeholder="请输入验证码" />
        <el-button :disabled="phoneForm.isDisabled" type="primary" @click="getVertifyCode('phoneForm', 1003)">
          {{ phoneForm.buttonName }}
        </el-button>
      </div>
    </div>
    <template #footer>
      <span class="dialog--footer">
        <el-button @click="closeHandle">取 消</el-button>
        <el-button v-if="!validate" type="primary" @click="nextClickHandle">下一步</el-button>
        <el-button v-if="validate" type="primary" @click="comfirmClickHandle">确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<style scoped lang="scss">
.el-tag {
  display: block;
  padding: 15px;
  height: auto;
  margin-bottom: 20px;
}

.phoneCode {
  display: flex;

  .el-button {
    margin-left: 15px;
  }
}
</style>
