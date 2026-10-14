<script setup lang="ts">
import { upLoad } from '@/api/index'
import LinkSelect from '@/components/LinkSelect'
import { useVModel } from '@vueuse/core'

import ImageCom from './ImageCom'

const props = defineProps({
  formData: {
    type: Object as PropType<typeof ImageCom>,
    default: () => ImageCom,
  },
})

const emit = defineEmits(['update:formData'])
const formData = useVModel(props, 'formData', emit)

const currentItem = ref<any>({})

/**
 * 选择图片上传
 */
async function handleChange(file: any) {
  const img = await upLoadFile(file)
  formData.value.img = img
}

async function upLoadFile(file: any) {
  const whiteList = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif']
  const isLt2M = file.size < 2 * 1024 * 1024
  if (!whiteList.includes(file.raw.type)) {
    ElMessage.error('上传文件只能是 GIF、JPG或PNG 格式!')
    return
  }
  if (!isLt2M) {
    ElMessage.error('上传文件大小不能超过 2MB!')
    return
  }
  const res = await upLoad({
    file: file.raw,
  })
  return res.data
}
</script>

<template>
  <div>
    <el-upload class="homeSwiperForm-add" multiple :auto-upload="false" :show-file-list="false" :on-change="handleChange">
      <div v-if="formData.img" class="imgContent">
        <img :src="formData.img" style="width: 200px; height: 120px" />
      </div>
      <div v-else>
        <span>请上传图片</span>
        <p style="margin-top: 5px">可在左侧自定义图片大小</p>
      </div>
    </el-upload>

    <div class="homeSwiperForm-form">
      <el-form label-position="left" :model="formData" label-width="70px">
        <el-form-item label="图片上传">
          <el-upload :show-file-list="false" :auto-upload="false" :on-change="handleChange">
            <el-button>点击上传图片</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="导航链接">
          <LinkSelect v-model:link="formData.link" />
        </el-form-item>
        <el-form-item v-if="currentItem.type !== 6">
          <span style="color: #9797a1">{{ currentItem.name || formData.link.name }}</span>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.homeSwiperForm-add {
  margin-top: 20px;
  width: 100%;
  height: 180px;
  border: 1px solid #e4e4e4;
  margin-bottom: 10px;
  .el-upload {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100%;
  }
  span {
    color: #3088f0;
    cursor: pointer;
  }
  p {
    font-size: 12px;
    color: #a7a7a7;
  }
}

.imgContent {
  display: flex;
  justify-content: center;
  align-items: center;
}

.homeSwiperForm-form {
  border: 1px solid #e4e4e4;
  padding: 10px;
}
</style>
