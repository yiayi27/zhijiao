<script setup lang="ts">
import { upLoad } from '@/api'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

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
})

const emit = defineEmits(['update:imgUrl'])

const uploadFile = ref(null)

// 处理双向绑定
const imageUrl = ref(props.imgUrl)

// 监听imageUrl变化并向父组件更新值
function updateImageUrl(val: string) {
  imageUrl.value = val
  emit('update:imgUrl', val)
}

/**
 * 描述 文件上传
 */
async function uploadImage(file: any) {
  const res = await upLoad({
    file: file.file,
  })
  updateImageUrl(res.data)
}

function beforeAvatarUpload(file: any) {
  const isType = props.typeList.some((type) => {
    return file.type.includes(type)
  })
  const isSize = file.size / 1024 / 1024 < props.size

  if (!isType) {
    ElMessage.error('上传图片的格式不正确，请重新上传！')
  }
  if (!isSize) {
    const sizeText = props.size >= 1 ? `${props.size}M` : `${props.size * 100}kb`
    ElMessage.error(`上传图片不被不能超过${sizeText}，请重新上传！`)
  }
  return isType && isSize
}

// 当点击恢复默认按钮时
function handleRestoreDefault() {
  updateImageUrl(props.defaultIcon)
}
</script>

<template>
  <div class="upload-container-nav">
    <el-upload
      ref="uploadFile"
      action=""
      class="avatar-uploader"
      :auto-upload="true"
      :show-file-list="false"
      :before-upload="beforeAvatarUpload"
      :http-request="uploadImage"
    >
      <img v-if="imageUrl" :src="imageUrl" class="avatar" />
      <el-icon v-else class="avatar-uploader-icon">
        <Plus />
      </el-icon>
    </el-upload>
    <el-button v-if="defaultIcon !== ''" @click="handleRestoreDefault"> 恢复默认 </el-button>
  </div>
</template>

<style lang="scss" scoped>
.upload-container-nav {
  .avatar-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
  }

  .avatar-uploader .el-upload:hover {
    border-color: #409eff;
  }

  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 60px;
    height: 60px;
    line-height: 60px;
    text-align: center;
  }

  .avatar {
    width: 60px;
    height: 60px;
    display: block;
  }

  .upload-container {
    width: 60px;
    margin-right: 20px;
    display: flex;
    align-items: flex-end;

    .el-button {
      margin-left: 15px;
    }
  }
}
</style>
