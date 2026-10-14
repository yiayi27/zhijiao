<script lang="ts" setup>
import type { FormInstance } from 'element-plus'
import { getPaymentConfig, savePaymentConfig, uploadCertificate } from '@/api/businessCenter/setting'

import BaseTable from '@/components/BaseTable/index.vue'
import type { PayFormType } from '@/api/businessCenter/type'

// 定义文件上传请求类型
interface UploadFileRequest {
  file: File
  [key: string]: any
}

// 定义回调函数类型
type ValidatorCallback = (error?: Error) => void

const paymentConfig = ref([])
const dialogVisible = ref(false)
const currentInfo = ref<Partial<Record<PayFormType, string>>>({
  appid: '',
  mchId: '',
  keyPrivate: '',
  keyCert: '',
  keyPublic: '',
})
const currentIsWechat = computed(() => {
  return isWechat(currentInfo.value.payType)
})
const payFormRef = ref<FormInstance>()
const uploadFileRef = ref()
//微信支付公钥模式配置
const wechatKeyPublic = reactive({
  enable: false,
  keyPublicId: '',
  keyPublicText: '',
})
//支付宝证书
const alipayCert = reactive({
  appCertPublicKey: '',
  alipayRootCert: '',
})

const payFormRules = {
  appid: [{ required: true, message: '请填写应用ID', trigger: 'blur' }],
  mchId: [{ required: true, message: '请填写商户号', trigger: 'blur' }],
  keyPrivate: [{ required: true, message: '请填写密钥', trigger: 'blur' }],
  keyCert: [{ required: true, message: '请上传证书', trigger: 'blur' }],
  keyPublic: [{ required: true, message: '请填写公钥', trigger: 'blur' }],
  alipayRootCert: [
    {
      required: true,
      validator(_r: any, _v: any, callback: ValidatorCallback) {
        return callback(alipayCert.alipayRootCert ? undefined : new Error('请上传支付宝根证书'))
      },
      trigger: 'blur',
    },
  ],
  appCertPublicKey: [
    {
      required: true,
      validator(_r: any, _v: any, callback: ValidatorCallback) {
        return callback(alipayCert.appCertPublicKey ? undefined : new Error('请上传应用公钥证书'))
      },
      trigger: 'blur',
    },
  ],
  keyPublicId: [
    {
      required: true,
      validator(_r: any, _v: any, callback: ValidatorCallback) {
        return callback(wechatKeyPublic.keyPublicId ? undefined : new Error('请填写微信支付公钥ID'))
      },
      trigger: 'blur',
    },
  ],
  keyPublicText: [
    {
      required: true,
      validator(_r: any, _v: any, callback: ValidatorCallback) {
        return callback(wechatKeyPublic.keyPublicText ? undefined : new Error('请填写微信支付公钥文本内容'))
      },
      trigger: 'blur',
    },
  ],
}

onMounted(() => {
  getPayConfig()
})

function isWechat(payType: string | undefined) {
  return payType === 'WECHAT'
}

/**
 * 获取支付配置
 */
async function getPayConfig() {
  const response = await getPaymentConfig({
    code: '',
    type: '1',
  })
  const { code, data } = response
  if (code === 200) {
    paymentConfig.value = data
  }
}

/**
 * 设置支付配置
 */
function setPayConfig(info: any) {
  currentInfo.value = info
  if (isWechat(info.payType)) {
    const keyPublic = info.keyPublic
    if (keyPublic) {
      const keyPublicArr = keyPublic.split(',')
      wechatKeyPublic.enable = true
      wechatKeyPublic.keyPublicId = keyPublicArr[0]
      wechatKeyPublic.keyPublicText = keyPublicArr[1]
    }
  } else {
    const cert = info.keyCert
    if (cert) {
      const certArr = cert.split(',')
      alipayCert.appCertPublicKey = certArr[0]
      alipayCert.alipayRootCert = certArr[1]
    }
  }
  dialogVisible.value = true
}

/**
 * 上传支付证书
 */
async function uploadFile(file: UploadFileRequest, success?: (data: string) => void) {
  const response = await uploadCertificate({
    file: file.file,
  })
  const { code, data } = response
  if (code === 200) {
    ElMessage({
      message: '上传成功',
      type: 'success',
    })
    if (success) {
      success(data)
      return
    }
    currentInfo.value.keyCert = data
  }
}

/**
 * @description: 保存支付配置
 */
async function savePayForm() {
  if (!payFormRef.value) return
  await payFormRef.value.validate()
  if (currentInfo.value.payType === 'ALIPAY') {
    const alipayRootCert = alipayCert.alipayRootCert
    currentInfo.value.keyCert = alipayRootCert ? `${alipayCert.appCertPublicKey},${alipayRootCert}` : `${alipayCert.appCertPublicKey}`
  } else {
    currentInfo.value.keyPublic = wechatKeyPublic.enable ? `${wechatKeyPublic.keyPublicId},${wechatKeyPublic.keyPublicText}` : ''
  }
  const { code } = await savePaymentConfig(currentInfo.value)
  if (code === 200) {
    ElMessage({
      message: '保存成功',
      type: 'success',
    })
    dialogVisible.value = false
    getPayConfig()
  }
}
</script>

<template>
  <div>
    <BaseTable :data="paymentConfig" :row-style="{ height: '80px' }">
      <el-table-column align="left" label="渠道名称">
        <template #default="scope">
          {{ isWechat(scope.row.payType) ? '微信支付' : '支付宝支付' }}
        </template>
      </el-table-column>
      <el-table-column align="right" label="操作">
        <template #default="scope">
          <el-button @click="setPayConfig(scope.row)"> 配置</el-button>
        </template>
      </el-table-column>
    </BaseTable>
    <el-dialog v-model="dialogVisible" center width="600px">
      <template #header>
        <div>
          {{ currentIsWechat ? '微信支付' : '支付宝支付' }}
        </div>
      </template>
      <el-form ref="payFormRef" :model="currentInfo" :rules="payFormRules" label-width="120px">
        <el-form-item label="appId" prop="appid">
          <el-input
            v-model="currentInfo.appid"
            :disabled="currentIsWechat"
            :placeholder="currentIsWechat ? '请在店铺设置里配置APPID' : '请输入应用ID'"
          />
        </el-form-item>
        <template v-if="currentIsWechat">
          <el-form-item label="商户号" prop="mchId">
            <el-input v-model="currentInfo.mchId" placeholder="请输入商户号" />
          </el-form-item>
          <el-form-item label="APIv3密钥" prop="keyPrivate">
            <el-input v-model="currentInfo.keyPrivate" placeholder="商户APIv3密钥" />
          </el-form-item>
          <el-form-item label="公钥模式" prop="enable">
            <el-switch v-model="wechatKeyPublic.enable" />
            <el-link href="https://pay.weixin.qq.com/doc/v3/merchant/4012153196" style="padding-left: 8px" target="_blank">
              微信支付新增了对公钥模式的支持。
            </el-link>
          </el-form-item>
          <el-form-item v-if="wechatKeyPublic.enable" label="公钥ID" prop="keyPublicId">
            <el-input v-model="wechatKeyPublic.keyPublicId" placeholder="请填写微信支付公钥ID" />
          </el-form-item>
          <el-form-item v-if="wechatKeyPublic.enable" label="公钥" prop="keyPublicText">
            <el-input
              v-model="wechatKeyPublic.keyPublicText"
              :rows="2"
              placeholder="请填写微信支付公钥文本内容（支付公钥下载并打开，复制内容）"
              type="textarea"
            />
          </el-form-item>
          <el-form-item label="支付证书" prop="keyCert">
            <el-upload
              ref="uploadFileRef"
              :auto-upload="true"
              :http-request="(file: UploadFileRequest) => uploadFile(file)"
              :show-file-list="false"
              accept=".p12"
              action=""
            >
              <el-button v-if="currentInfo.keyCert" link style="color: #67c23a"> 已上传</el-button>
              <el-button v-else link> 未上传</el-button>
              <el-button type="primary"> 上传</el-button>
              <template #tip>
                <div class="el-upload__tip">微信商户平台，微信支付API证书</div>
              </template>
            </el-upload>
          </el-form-item>
        </template>
        <template v-else>
          <!-- 支付宝 -->
          <el-form-item label="支付宝私钥" prop="keyPrivate">
            <el-input v-model="currentInfo.keyPrivate" :rows="2" placeholder="请输入支付宝私钥" type="textarea" />
          </el-form-item>
          <el-form-item label="支付宝公钥证书" prop="keyPublic">
            <el-upload
              ref="uploadFileRef"
              :auto-upload="true"
              :http-request="(file: UploadFileRequest) => uploadFile(file, (data) => (currentInfo.keyPublic = data))"
              :show-file-list="false"
              accept=".crt"
              action=""
            >
              <el-button v-if="currentInfo.keyPublic" link style="color: #67c23a"> 已上传 </el-button>
              <el-button v-else link> 未上传</el-button>
              <el-button type="primary"> 上传</el-button>
              <template #tip>
                <div class="el-upload__tip">支付宝公钥证书</div>
              </template>
            </el-upload>
          </el-form-item>
          <el-form-item label="支付宝根证书" prop="alipayRootCert">
            <el-upload
              ref="uploadFileRef"
              :auto-upload="true"
              :http-request="(file: UploadFileRequest) => uploadFile(file, (data) => (alipayCert.alipayRootCert = data))"
              :show-file-list="false"
              accept=".crt"
              action=""
            >
              <el-button v-if="alipayCert.alipayRootCert" link style="color: #67c23a"> 已上传 </el-button>
              <el-button v-else link> 未上传</el-button>
              <el-button type="primary"> 上传</el-button>
              <template #tip>
                <div class="el-upload__tip">支付宝根证书</div>
              </template>
            </el-upload>
          </el-form-item>
          <el-form-item label="应用公钥证书" prop="appCertPublicKey">
            <el-upload
              ref="uploadFileRef"
              :auto-upload="true"
              :http-request="(file: UploadFileRequest) => uploadFile(file, (data) => (alipayCert.appCertPublicKey = data))"
              :show-file-list="false"
              accept=".crt"
              action=""
            >
              <el-button v-if="alipayCert.appCertPublicKey" link style="color: #67c23a"> 已上传 </el-button>
              <el-button v-else link> 未上传</el-button>
              <el-button type="primary"> 上传</el-button>
              <template #tip>
                <div class="el-upload__tip">应用公钥证书</div>
              </template>
            </el-upload>
          </el-form-item>
        </template>
      </el-form>
      <template #footer>
        <div>
          <el-button @click="dialogVisible = false"> 取 消</el-button>
          <el-button type="primary" @click="savePayForm"> 保 存</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
.pay__table {
  height: 60px;
  background: red;
}
</style>
