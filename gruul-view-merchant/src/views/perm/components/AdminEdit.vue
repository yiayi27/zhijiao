<script lang="ts" setup>
import { useVModel } from '@vueuse/core'
import { ElMessage } from 'element-plus'
import { toPostShopAdminAdd, toPostShopAdminAvailable, toPostShopAdminEdit } from '@/api/perm'
import RoleMenus from '@/views/perm/components/RoleMenus.vue'

type FormInstanceType = {
  validate: (callback: (valid: boolean) => void) => void
  clearValidate: () => void
  resetFields: () => void
}

const props = defineProps({
  visible: {
    type: Boolean,
    default: false,
  },
  data: {
    type: Object,
    default: () => ({}),
  },
})

const emit = defineEmits(['update:visible', 'success', 'close'])
const visible = useVModel(props, 'visible', emit)

function getDefaultForm() {
  return {
    nickname: null,
    password: null,
    confirmPassword: null,
    mobile: null,
    perms: [] as string[],
  }
}

const passwordPattern = '(?![A-Z]+$)(?![a-z]+$)(?!\\d+$)(?![\\W_]+$)\\S{6,20}'
const formRef = ref<FormInstanceType | null>(null)
const form = ref(getDefaultForm())
const loading = ref(false)

watch(
  () => props.data,
  (newValue: Record<string, any> | undefined) => {
    if (!newValue) {
      return
    }

    Object.keys(form.value).forEach((item) => {
      if (newValue[item]) {
        // 使用类型断言避免索引签名错误
        ;(form.value as any)[item] = newValue[item]
      }
    })
  },
  { deep: true, immediate: true },
)

enum MobileState {
  PASSWORD = 'PASSWORD',
  NO_PASSWORD = 'NO_PASSWORD',
  BOUNDED = 'BOUNDED',
}

const loadingMobile = ref(false)
const mobileState = ref(MobileState.NO_PASSWORD)
const rules = ref({
  nickname: [
    { required: true, message: '请输入用户昵称' },
    { min: 1, max: 20, message: '昵称限制在1-20个字符之间' },
  ],
  password: [
    { required: true, message: '请输入密码' },
    { min: 6, max: 20, message: '长度限制在6-20个字符之间' },
    { pattern: passwordPattern, message: '密码需要在大写字母、小写字母、数字、字符之间任选其二' },
  ],
  confirmPassword: [
    { required: true, message: '请输入确认密码' },
    { min: 6, max: 20, message: '长度限制在6-20个字符之间' },
    { pattern: passwordPattern, message: '密码需要在大写字母、小写字母、数字、特殊字符之间任选其二' },
    {
      validator: (_rule: any, _value: any, callback: (error?: Error) => void) => {
        if (form.value.confirmPassword !== form.value.password) {
          callback(new Error('确认密码和密码不一致'))
          return
        }
        callback()
      },
      trigger: 'blur',
    },
  ],
  mobile: [
    { required: true, message: '请输入用户手机号', trigger: 'blur' },
    {
      pattern: '((?:0|86|\\+86)?1[3-9]\\d{9})|((010|02\\d|0[3-9]\\d{2})-?(\\d{6,8}))',
      message: '请输入正确的手机号',
    },
    {
      validator: async (_rule: any, mobile: string, callback: (error?: Error) => void) => {
        if (!props.data?.mobile) {
          await getMobileState(mobile)
          if (mobileState.value === MobileState.BOUNDED) {
            callback(new Error('该手机号已被绑定'))
            return
          }
        }
        callback()
      },
      trigger: 'blur',
    },
  ],
})

async function getMobileState(mobile: string) {
  loadingMobile.value = true
  const res = await toPostShopAdminAvailable({ mobile }).finally(() => {
    loadingMobile.value = false
  })
  mobileState.value = res.data
}

function saveData() {
  formRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      loading.value = true
      const fn = props.data?.mobile ? toPostShopAdminEdit : toPostShopAdminAdd
      await fn(form.value).finally(() => {
        loading.value = false
      })
      ElMessage.success('已保存')
      emit('success')
      handleClose()
    }
  })
}

const showPassword = computed(() => !props.data?.mobile && mobileState.value === MobileState.PASSWORD)

async function handleClose() {
  formRef.value?.clearValidate()
  formRef.value?.resetFields()
  form.value = getDefaultForm()
  visible.value = false
  emit('close')
}
</script>

<template>
  <el-dialog v-model="visible" title="自定义管理员" width="500px" :before-close="handleClose">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="昵称" prop="nickname">
        <el-input v-model="form.nickname" :clearable="true" maxlength="20" placeholder="请输入昵称" />
      </el-form-item>
      <el-form-item label="手机号" prop="mobile">
        <el-input v-model="form.mobile" :clearable="true" maxlength="11" placeholder="请输入手机号" :disabled="!!data?.mobile" />
      </el-form-item>
      <el-form-item v-if="showPassword" label="密码" prop="password">
        <el-input
          v-model="form.password"
          autocomplete="new-password"
          clearable
          maxlength="20"
          show-password
          type="password"
          placeholder="请输入密码"
        />
      </el-form-item>
      <el-form-item v-if="showPassword" label="确认密码" prop="confirmPassword">
        <el-input v-model="form.confirmPassword" clearable maxlength="20" show-password type="password" placeholder="请输入确认密码" />
      </el-form-item>
      <el-form-item label="授权菜单" prop="menuIds">
        <RoleMenus v-model="form.perms" />
      </el-form-item>
    </el-form>
    <template #footer>
      <span>
        <el-button :disabled="loading" @click="handleClose">取消</el-button>
        <el-button v-loading="loading" type="primary" @click="saveData">保存</el-button>
      </span>
    </template>
  </el-dialog>
</template>
