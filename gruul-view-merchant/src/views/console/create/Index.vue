<script setup lang="ts">
import { createShop } from '@/api/console'
import { upLoad } from '@/api/index'
import config from '@/config'
import { Picture } from '@element-plus/icons-vue'

import { useRoute, useRouter } from 'vue-router'

defineOptions({
  name: 'ConsoleCreate',
})

const router = useRouter()
const route = useRoute()

const createFormRef = ref<HTMLFormElement>()
const loading = ref(false)

const createForm = reactive({
  agree: false,
  logoUrl: config.logo,
  shopName: '',
})

const createRules = {
  shopName: [{ required: true, message: '请输入店铺名称', trigger: 'blur' }],
  logoUrl: [{ required: true, message: '请上传logo', trigger: 'blur' }],
  type: [{ required: true, message: '请选择店铺类型', trigger: 'blur' }],
}

onMounted(async () => {
  if (route.query.form) {
    const form = JSON.parse(String(route.query.form))
    Object.assign(createForm, form)
  }
})

/**
 * 描述 文件上传
 */
async function uploadAvatar(file: any) {
  const response = await upLoad({
    file: file.file,
  })
  const { data } = response
  createForm.logoUrl = data
}

function beforeAvatarUpload(file: File) {
  const typeList = ['image']
  const isType = typeList.some((type) => {
    return file.type.includes(type)
  })
  const isSize = file.size / 1024 / 1024 < 2

  if (!isType) {
    ElMessage.error('上传图片的格式不正确，请重新上传！')
  }
  if (!isSize) {
    ElMessage.error(`上传图片不被不能超过2M，请重新上传！`)
  }
  return isType && isSize
}

function getShopName() {
  let charLength = 0
  const shopName = createForm.shopName
  for (let i = 0; i < shopName.length; i++) {
    const a = shopName.charAt(i)
    if (a.match(/[\u4e00-\u9fa5]|[^\u0020-\u007f]/gi) !== null) {
      if (charLength + 2 >= 16) {
        createForm.shopName = shopName.substr(0, i + 1)
        return
      }
      charLength += 2
    } else {
      if (charLength + 1 >= 16) {
        createForm.shopName = shopName.substr(0, i + 1)
        return
      }
      charLength += 1
    }
  }
}

async function onCreateShop() {
  await createFormRef.value?.validate()
  if (!createForm.agree) {
    ElMessage.warning('请确定已同意协议')
    return
  }

  loading.value = true
  await createShop({
    logoUrl: createForm.logoUrl,
    shopName: createForm.shopName,
  }).finally(() => {
    loading.value = false
  })
  ElMessage.success('创建店铺成功')
  onBack()
}

function goOrder() {
  window.open(`${location.origin}/console/order?shopName=${createForm.shopName}`)
}

function goRegister() {
  window.open(`${location.origin}/console/register?shopName=${createForm.shopName}`)
}

function onBack() {
  router.replace('/console')
}
</script>

<template>
  <div class="create">
    <el-form ref="createFormRef" :model="createForm" :rules="createRules" label-position="left" label-width="85px" class="createForm">
      <el-form-item label="店铺logo" prop="logoUrl">
        <div class="create__avatar">
          <el-image :src="createForm.logoUrl" class="create__avatar--image">
            <template #placeholder>
              <div class="image-slot">
                <el-icon><Picture /></el-icon>
              </div>
            </template>
            <template #error>
              <div class="image-slot">
                <el-icon><Picture /></el-icon>
              </div>
            </template>
          </el-image>
          <el-upload
            class="create__avatar--uploader"
            action=""
            :auto-upload="true"
            :show-file-list="false"
            :before-upload="beforeAvatarUpload"
            :http-request="uploadAvatar"
          >
            <span class="primary">上传</span>
          </el-upload>
        </div>
      </el-form-item>
      <el-form-item label="店铺名称" prop="shopName">
        <div class="create__ShopName">
          <el-input v-model="createForm.shopName" class="create__ShopName--input" placeholder="请输入店铺名称, 最多16个字符" @input="getShopName" />
        </div>
      </el-form-item>
      <el-form-item prop="agree">
        <el-checkbox v-model="createForm.agree" class="mr-[10px]" />
        <span
          >我已阅读并同意<span class="primary pointer" @click="goOrder">{{ `《${createForm.shopName || 'xxx'}软件订购及服务协议》` }}</span
          >和 <span class="primary pointer" @click="goRegister">{{ `《${createForm.shopName || 'xxx'}支付开户及服务协议》` }}</span></span
        >
      </el-form-item>
      <el-form-item>
        <el-button @click="onBack"> 取消 </el-button>
        <el-button type="primary" @click="onCreateShop"> 创建店铺 </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<style lang="scss" scoped>
@import '@/assets/styles/console/create.scss';
</style>
