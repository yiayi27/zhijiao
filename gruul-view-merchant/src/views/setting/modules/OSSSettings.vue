<script setup lang="ts">
import { getOssConfig, modifySystemConfig } from '@/api/sign/index'

// 响应式数据
const dataForm = reactive({
  aliyunAccessKeyId: '',
  aliyunAccessKeySecret: '',
  aliyunBucketName: '',
  aliyunDomain: '',
  aliyunEndPoint: '',
  aliyunPrefix: '',
  qiniuAccessKey: '',
  qiniuBucketName: '',
  qiniuDomain: '',
  qiniuPrefix: '',
  qiniuSecretKey: '',
  qcloudAppId: '',
  qcloudBucketName: '',
  qcloudDomain: '',
  qcloudPrefix: '',
  qcloudRegion: '',
  qcloudSecretId: '',
  qcloudSecretKey: '',
  type: 1,
})

// 声明serviceEditVisible变量（在原代码中使用但未定义）
const serviceEditVisible = ref(false)

// 方法
function getOssData(type: any) {
  getOssConfig({
    type,
  }).then((res) => {
    Object.assign(dataForm, res.data)
  })
}

function saveSystemConfig() {
  if (dataForm.type === 1) {
    const form = {
      type: 1,
      qiniouStorageConfig: {
        qiniuAccessKey: dataForm.qiniuAccessKey,
        qiniuBucketName: dataForm.qiniuBucketName,
        qiniuDomain: dataForm.qiniuDomain,
        qiniuPrefix: dataForm.qiniuPrefix,
        qiniuSecretKey: dataForm.qiniuSecretKey,
      },
    }
    modifySystemConfig(JSON.stringify(form)).then(() => {
      ElMessage({
        type: 'success',
        message: '保存成功!',
      })
    })
  }
  if (dataForm.type === 2) {
    const form = {
      type: 2,
      aliyunStorageConfig: {
        aliyunAccessKeyId: dataForm.aliyunAccessKeyId,
        aliyunAccessKeySecret: dataForm.aliyunAccessKeySecret,
        aliyunBucketName: dataForm.aliyunBucketName,
        aliyunDomain: dataForm.aliyunDomain,
        aliyunEndPoint: dataForm.aliyunEndPoint,
        aliyunPrefix: dataForm.aliyunPrefix,
      },
    }
    modifySystemConfig(JSON.stringify(form)).then(() => {
      ElMessage({
        type: 'success',
        message: '保存成功!',
      })
    })
  }
  if (dataForm.type === 3) {
    const form = {
      type: 3,
      tencentStorageConfig: {
        qcloudAppId: dataForm.qcloudAppId,
        qcloudBucketName: dataForm.qcloudBucketName,
        qcloudDomain: dataForm.qcloudDomain,
        qcloudPrefix: dataForm.qcloudPrefix,
        qcloudRegion: dataForm.qcloudRegion,
        qcloudSecretId: dataForm.qcloudSecretId,
        qcloudSecretKey: dataForm.qcloudSecretKey,
      },
    }
    modifySystemConfig(JSON.stringify(form)).then(() => {
      ElMessage({
        type: 'success',
        message: '保存成功!',
      })
    })
  }
}

// 生命周期钩子
onMounted(() => {
  getOssData(1)
})
</script>

<template>
  <div>
    <el-form :model="dataForm" label-width="80px" label-position="left">
      <el-form-item label="存储类型">
        <el-radio-group v-model="dataForm.type" @change="getOssData">
          <el-radio :value="1"> 七牛云 </el-radio>
          <el-radio :value="2"> 阿里云 </el-radio>
          <el-radio :value="3"> 腾讯云 </el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <el-form :model="dataForm" label-width="120px" label-position="left">
      <div v-if="dataForm.type === 1">
        <el-form-item>
          <span>免费申请（七牛云）10GB存储空间</span>
        </el-form-item>
        <el-form-item label="域名">
          <el-input v-model="dataForm.qiniuDomain" clearable placeholder="七牛云绑定的域名" />
        </el-form-item>
        <el-form-item label="路径前缀">
          <el-input v-model="dataForm.qiniuPrefix" clearable placeholder="不设置默认为空" />
        </el-form-item>
        <el-form-item label="AccessKey">
          <el-input v-model="dataForm.qiniuAccessKey" clearable placeholder="七牛云AccessKey" />
        </el-form-item>
        <el-form-item label="SecreKey">
          <el-input v-model="dataForm.qiniuSecretKey" clearable placeholder="七牛云SecreKey" />
        </el-form-item>
        <el-form-item label="空间名">
          <el-input v-model="dataForm.qiniuBucketName" clearable placeholder="七牛云空间名" />
        </el-form-item>
      </div>
      <div v-if="dataForm.type === 2">
        <el-form-item label="域名">
          <el-input v-model="dataForm.aliyunDomain" clearable placeholder="阿里云绑定的域名" />
        </el-form-item>
        <el-form-item label="路径前缀">
          <el-input v-model="dataForm.aliyunPrefix" clearable placeholder="不设置默认为空" />
        </el-form-item>
        <el-form-item label="EndPoint">
          <el-input v-model="dataForm.aliyunEndPoint" clearable placeholder="阿里云EndPoint" />
        </el-form-item>
        <el-form-item label="AccessKeyld">
          <el-input v-model="dataForm.aliyunAccessKeyId" clearable placeholder="阿里云AccessKeyld" />
        </el-form-item>
        <el-form-item label="AccessKeySecret">
          <el-input v-model="dataForm.aliyunAccessKeySecret" clearable placeholder="阿里云AccessKeySecret" />
        </el-form-item>
        <el-form-item label="BucketName">
          <el-input v-model="dataForm.aliyunBucketName" clearable placeholder="阿里云BucketName" />
        </el-form-item>
      </div>
      <div v-if="dataForm.type === 3">
        <el-form-item label="域名">
          <el-input v-model="dataForm.qcloudDomain" clearable placeholder="腾讯云绑定的域名" />
        </el-form-item>
        <el-form-item label="路径前缀">
          <el-input v-model="dataForm.qcloudPrefix" clearable placeholder="不设置默认为空" />
        </el-form-item>
        <el-form-item label="Appid">
          <el-input v-model="dataForm.qcloudAppId" clearable placeholder="腾讯云Appid" />
        </el-form-item>
        <el-form-item label="Secretld">
          <el-input v-model="dataForm.qcloudSecretId" clearable placeholder="腾讯云Secretld" />
        </el-form-item>
        <el-form-item label="SecretKey">
          <el-input v-model="dataForm.qcloudSecretKey" clearable placeholder="腾讯云SecretKey" />
        </el-form-item>
        <el-form-item label="BucketName">
          <el-input v-model="dataForm.qcloudBucketName" clearable placeholder="腾讯云BucketName" />
        </el-form-item>
        <el-form-item label="Bucket所属地区">
          <el-input v-model="dataForm.qcloudRegion" clearable placeholder="如：sh（可选填，华南：gz华北：tj华东：sh）" />
        </el-form-item>
      </div>
    </el-form>
    <div class="footer-submit">
      <el-button @click="serviceEditVisible = false"> 取 消 </el-button>
      <el-button type="primary" @click="saveSystemConfig"> 确定 </el-button>
    </div>
  </div>
</template>

<style scoped></style>
