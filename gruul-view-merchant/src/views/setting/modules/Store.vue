<script lang="ts" setup>
import { getShopInfo, updateShopInfo } from '@/api/businessCenter/setting'
import config from '@/config'
import { useUserStore } from '@/store/modules/user'
import UploadFile from '@/views/decoration/components/UserCenter/UploadFile.vue'
import Share from '@/views/setting/modules/Share.vue'

const queryFormRef = ref()

/** 业务基础库表单 */
const queryForm = reactive({
  businessHours: ['00:00:00', '23:59:59'],
  dueTime: '',
  isDue: 0,
  level: 0,
  logoUrl: '',
  miniBottomLog: '',
  orderSource: 0,
  packageName: '',
  platformShopId: 0,
  shopName: '',
  shopPhone: '',
  status: 0,
  templateName: '',
  appid: '',
  appSecret: '',
})

const queryRules = {
  shopName: [{ required: true, message: '请填写店铺名称', trigger: 'blur' }],
  businessHours: [{ required: true, message: '请填写营业时间', trigger: 'blur' }],
  shopPhone: [{ required: true, validator: validPhone, trigger: 'blur' }],
  miniBottomLog: [{ required: true, message: '请上传底部版权标志', trigger: 'change' }],
  logoUrl: [{ required: true, message: '请上传店铺logo', trigger: 'change' }],
  appid: [{ required: true, message: '请填写小程序appid', trigger: 'blur' }],
  appSecret: [{ required: true, message: '请填写小程序app密钥', trigger: 'blur' }],
}

onMounted(() => {
  getInfo()
})

async function getInfo() {
  const response = await getShopInfo()
  Object.assign(queryForm, {
    ...response,
    miniBottomLog: response.miniBottomLog || config.logo,
    logoUrl:
      response.logoUrl || 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20201114/55f9da2cc5254da2a90433e0acd77f66.jpg',
    businessHours: response.businessHours ? JSON.parse(response.businessHours) : ['00:00:00', '23:59:59'],
  })
}

const userStore = useUserStore()

async function confirmClickHandle() {
  await queryFormRef.value.validate()
  const platformShopId = userStore.shopInfo?.platformShopId || ''
  // let businessHours = ['00:00:00', '23:59:59']
  // if (typeof queryForm.businessHours !== 'string') {
  //   businessHours = JSON.stringify(queryForm.businessHours)
  // }
  const form = {
    shopName: queryForm.shopName,
    businessHours: JSON.stringify(queryForm.businessHours),
    shopPhone: queryForm.shopPhone,
    miniBottomLog: queryForm.miniBottomLog,
    logoUrl: queryForm.logoUrl,
    appid: queryForm.appid,
    appSecret: queryForm.appSecret,
  }
  const response = await updateShopInfo(platformShopId, form)
  const { code } = response
  if (code === 200) {
    ElMessage.success('修改成功')
    await getShopInfo()
  }
}

function validPhone(_rule: any, value: string, callback: any) {
  if (!value) {
    callback(new Error('请输入电话号码'))
  } else if (!isValidPhone(value)) {
    callback(new Error('请输入正确的11位手机号码'))
  } else {
    callback()
  }
}

function isValidPhone(str: string) {
  const reg = /^1(3\d|4\d|5\d|6\d|7\d|8\d|9[1,89])\d{8}$/
  return reg.test(str)
}
</script>

<template>
  <div class="stroe">
    <el-form ref="queryFormRef" :model="queryForm" :rules="queryRules" class="queryForm" label-position="left" label-width="120px">
      <div class="store__tip">
        <div class="store__tip--lump orange" />
        <span class="store__tip--title">店铺设置</span>
      </div>
      <el-form-item label="小程序Appid" prop="appid">
        <el-input v-model="queryForm.appid" placeholder="请填写小程序appid" style="width: 350px" />
      </el-form-item>
      <el-form-item label="小程序密钥" prop="appSecret">
        <el-input v-model="queryForm.appSecret" placeholder="请填写小程序App密钥" style="width: 350px" type="password" />
      </el-form-item>
      <el-form-item label="店铺logo" prop="logoUrl">
        <UploadFile
          v-model:img-url="queryForm.logoUrl"
          :avatar-height="60"
          :size="1"
          default-icon="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20201114/55f9da2cc5254da2a90433e0acd77f66.jpg"
        />
      </el-form-item>
      <el-form-item label="店铺名称" prop="shopName">
        <el-input v-model="queryForm.shopName" placeholder="请填写店铺名称" style="width: 350px" />
      </el-form-item>
      <el-form-item label="营业时间" prop="businessHours">
        <div>
          <el-time-picker
            v-model="queryForm.businessHours"
            end-placeholder="结束时间"
            is-range
            placeholder="选择营业时间"
            range-separator="-"
            start-placeholder="开始时间"
            value-format="HH:mm:ss"
          />
        </div>
      </el-form-item>
      <el-form-item label="店铺电话" prop="shopPhone">
        <el-input v-model="queryForm.shopPhone" placeholder="请填写店铺电话" style="width: 350px" />
      </el-form-item>
      <div class="store__tip">
        <div class="store__tip--lump green" />
        <span class="store__tip--title">版权设置</span>
      </div>
      <el-form-item label="店铺底部logo" prop="miniBottomLog">
        <UploadFile v-model:img-url="queryForm.miniBottomLog" :avatar-height="60" :default-icon="config.logo" :size="1">
          <template #el-upload__tip>
            <div class="el-upload__tip">尺寸建议330*90的PNG图片，大小1M以下</div>
          </template>
        </UploadFile>
      </el-form-item>
    </el-form>
    <Share @submit="confirmClickHandle" />
  </div>
</template>

<style lang="scss" scoped>
.stroe {
  font-size: 13px;

  .store__tip {
    vertical-align: center;
    background-color: rgba(246, 248, 250, 1);
    padding: 15px 15px 15px 30px;
    margin-bottom: 30px;

    .store__tip--title {
      margin-left: 12px;
      color: #586884;
      font-weight: 700;
    }

    .store__tip--text {
      padding-left: 15px;
      color: rgba(102, 102, 102, 0.458);
    }
  }
}

.store__tip--lump {
  display: inline-block;
  width: 3px;
  height: 12px;
  background-color: rgba(255, 153, 0, 1);

  &.green {
    background-color: rgb(0, 255, 8);
  }
}
</style>
