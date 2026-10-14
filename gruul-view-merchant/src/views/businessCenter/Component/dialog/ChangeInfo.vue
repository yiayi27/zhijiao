<script setup lang="ts">
import { getCode, verifyTheCode } from '@/api/sign'
import ScanQrCode from '@/components/ScanQrCode/ScanQrCode.vue'

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

/** 设为团长表单数据 */
const queryForm = reactive({
  phone: null,
  code: null,
  buttonName: '获取验证码',
  isDisabled: false,
  time: 60,
})

watch(
  () => dialogVisible.value,
  (newVal) => {
    emit('update:visible', newVal)
    if (newVal) {
      queryForm.phone = props.userInfo.phone
    }
  },
)

const validate = ref(false)

// 获取验证码
async function getVertifyCode(_formName: string, type: number) {
  const response = await getCode({
    phone: queryForm.phone,
    type,
  })
  const { code } = response
  if (code === 200) {
    ElMessage({
      type: 'success',
      message: '验证码已发送',
    })
  }
  queryForm.isDisabled = true
  const interval = window.setInterval(() => {
    queryForm.buttonName = `${queryForm.time}秒后重新发送`
    --queryForm.time
    if (queryForm.time < 0) {
      queryForm.buttonName = '重新发送'
      queryForm.time = 60
      queryForm.isDisabled = false
      window.clearInterval(interval)
    }
  }, 1000)
}

async function comfirmClickHandle() {
  const response = await verifyTheCode({
    phone: queryForm.phone,
    code: queryForm.code,
    type: 1005,
  })
  const { code } = response
  if (code === 200) {
    validate.value = true
  } else {
    ElMessage({
      type: 'warning',
      message: '请重新验证信息！',
    })
  }
}

/**
 * 关闭弹窗
 */
async function closeHandle() {
  dialogVisible.value = false
}
</script>

<template>
  <el-dialog v-model="dialogVisible" class="dialog" width="600px" title="换绑微信">
    <div v-if="!validate" class="verify-account">
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
            <el-button :disabled="queryForm.isDisabled" type="primary" @click="getVertifyCode('queryForm', 1005)">
              {{ queryForm.buttonName }}
            </el-button>
          </div>
        </el-form-item>
      </el-form>
    </div>
    <div v-if="validate" class="scan-code">
      <ScanQrCode redirect-url="/business" scenes="AccountSwitching" />
    </div>
    <template #footer>
      <span v-if="!validate" class="dialog--footer">
        <el-button @click="closeHandle">取 消</el-button>
        <el-button type="primary" @click="comfirmClickHandle">下一步</el-button>
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
</style>
