<script lang="ts" setup>
import { upLoad } from '@/api'
import { Plus } from '@element-plus/icons-vue'

const props = defineProps({
  imgUrl: {
    type: String,
    default: '',
  },
  typeList: {
    type: Array,
    default: () => ['image'],
  },
  size: {
    type: Number,
    default: 100,
  },
  defaultIcon: {
    type: String,
    default: '',
  },
  avatarHeight: {
    type: Number,
    default: 120,
  },
  disabled: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['update:imgUrl'])
const uploadFile = ref(null)
const imageUrl = ref(props.imgUrl)

watch(
  () => props.imgUrl,
  (newVal) => {
    imageUrl.value = newVal
  },
)

watch(imageUrl, (newVal) => {
  emit('update:imgUrl', newVal)
})

/**
 * 描述 文件上传
 */
async function uploadImage(file: any) {
  const res = await upLoad({
    file: file.file,
  })
  imageUrl.value = res.data
}

/**
 * 文件上传之前校验
 */
function beforeAvatarUpload(file: any) {
  const isType = props.typeList.some((type) => {
    return file.type.includes(type)
  })
  const isSize = file.size / 1024 / 1024 < props.size

  if (!isType) {
    // Vue 3中，需要从app实例或者全局属性中获取$message
    // 这里假设您已经配置了Element Plus
    ElMessage.error('上传图片的格式不正确，请重新上传！')
  }
  if (!isSize) {
    const sizeText = props.size < 1 ? `${props.size * 1024}kb` : `${props.size}M`
    ElMessage.error(`上传图片不能超过${sizeText}！`)
  }
  return isType && isSize
}
</script>

<template>
  <div class="upload-container">
    <el-upload
      ref="uploadFile"
      action=""
      class="avatar-uploader"
      :class="{ gray: disabled }"
      :disabled="disabled"
      :auto-upload="true"
      :show-file-list="false"
      :before-upload="beforeAvatarUpload"
      :http-request="uploadImage"
    >
      <img v-if="imageUrl" :src="imageUrl" class="avatar" :height="avatarHeight" alt="img" />
      <el-icon v-else class="avatar-uploader-icon">
        <Plus />
      </el-icon>
      <template #tip>
        <slot name="el-upload__tip" />
      </template>
    </el-upload>
    <el-button v-if="defaultIcon !== ''" @click="imageUrl = defaultIcon"> 恢复默认 </el-button>
  </div>
</template>

<style lang="scss" scoped>
.avatar-uploader :deep(.el-upload) {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.avatar-uploader :deep(.el-upload):hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  line-height: 120px;
  text-align: center;
}

.avatar {
  width: 120px;
  display: block;
}

.upload-container {
  display: flex;
  align-items: flex-end;

  .el-button {
    margin-left: 15px;
  }
}

.gray {
  opacity: 0.5;
}

.gray.avatar-uploader .el-upload {
  cursor: not-allowed;
}
</style>
