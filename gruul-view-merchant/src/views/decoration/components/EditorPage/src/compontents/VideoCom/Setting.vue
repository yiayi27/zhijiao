<script setup lang="ts">
import VideoCom from './VideoCom'
import { upLoad } from '@/api/index'
import { Plus } from '@element-plus/icons-vue'
import { useVModel } from '@vueuse/core'
import { ElLoading, ElMessage } from 'element-plus'

const props = defineProps({
  formData: {
    type: Object as PropType<typeof VideoCom>,
    default: () => VideoCom,
  },
})
const emit = defineEmits(['update:formData'])
const upLoadiVideo = useVModel(props, 'formData', emit)
const linkString = ref('')

function gotoVideo() {
  window.open(upLoadiVideo.value.video, '_blank')
}

onMounted(() => {
  if (upLoadiVideo.value.video) {
    linkString.value = String(upLoadiVideo.value.video)
  }
})

/**
 * 上传修改视频
 */
async function handleChange(e: any) {
  const loadingInstance = ElLoading.service({ target: '.homeSwiperForm-form' })
  const file = e.raw
  const fileSize = file.size / 1024 / 1024 < 10
  if (!['video/mp4', 'video/ogg', 'video/flv', 'video/avi', 'video/wmv', 'video/rmvb', 'video/mov'].includes(file.type)) {
    ElMessage.error('请上传正确的视频格式')
    return false
  }
  if (!fileSize) {
    ElMessage.error('视频大小不能超过10MB')
    return false
  }

  upLoad({
    file,
  })
    .then((res) => {
      upLoadiVideo.value.video = res.data
      linkString.value = String(res.data)
    })
    .finally(() => {
      loadingInstance.close()
    })
}

/**
 * 上传修改图片
 */
async function handleImgChange(file: any) {
  const img = await upLoadFile(file)
  upLoadiVideo.value.poster = img
}

async function upLoadFile(file: { raw: File; size: number }) {
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
    <div class="homeSwiperForm-form">
      <el-form label-position="left" :model="upLoadiVideo" label-width="70px">
        <el-form-item label="视频来源">
          <el-radio-group v-model="upLoadiVideo.radio">
            <el-radio :value="1"> 本地视频 </el-radio>
            <el-radio :value="2"> 外部视频 </el-radio>
          </el-radio-group>
          <el-upload v-if="upLoadiVideo.radio === 1 && !upLoadiVideo.video" :show-file-list="false" :auto-upload="false" :on-change="handleChange">
            <div class="uploadBox">
              <el-icon><Plus /></el-icon>
            </div>
          </el-upload>

          <div v-else-if="upLoadiVideo.radio === 1 && upLoadiVideo.video" style="display: flex">
            <div class="uploadLink" style="width: 240px" @click="gotoVideo">
              {{ linkString }}
            </div>
            <el-upload :show-file-list="false" :auto-upload="false" :on-change="handleChange">
              <div class="modify">修改</div>
            </el-upload>
          </div>

          <div v-else-if="upLoadiVideo.radio === 2">
            <el-input v-model="upLoadiVideo.videoLink" class="uploadInput" />
          </div>
        </el-form-item>
      </el-form>
      <el-form v-if="upLoadiVideo.video || upLoadiVideo.radio === 2" label-position="left" :model="upLoadiVideo" label-width="70px">
        <el-form-item label="封面图">
          <el-upload :show-file-list="false" :auto-upload="false" :on-change="handleImgChange">
            <div v-if="!upLoadiVideo.poster" class="imgContent">
              <el-icon><Plus /></el-icon>
            </div>
            <div v-if="upLoadiVideo.poster" class="imgContent">
              <img :src="upLoadiVideo.poster" style="width: 200px; height: 120px" />
            </div>
          </el-upload>
        </el-form-item>
      </el-form>

      <el-form v-if="upLoadiVideo.video || upLoadiVideo.radio === 2" label-position="left" :model="upLoadiVideo" label-width="70px">
        <el-form-item label="播放比例">
          <el-radio-group v-model="upLoadiVideo.radioBL">
            <el-radio :value="1"> 16:9 </el-radio>
            <el-radio :value="2"> 1:1 </el-radio>
            <el-radio :value="3"> 自适应高度 </el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <el-form v-if="upLoadiVideo.video || upLoadiVideo.radio === 2" label-position="left" :model="upLoadiVideo" label-width="70px">
        <el-form-item label="视频填充 ">
          <el-radio-group v-model="upLoadiVideo.radioTC">
            <el-radio :value="1"> 填充 </el-radio>
            <el-radio :value="2"> 周边留白 </el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.uploadBox {
  width: 200px;
  height: 80px;
  display: flex;
  justify-content: center;
  align-items: center;
  border: 1px dashed #e4e4e4;
  margin-top: 20px;
}

.homeSwiperForm-form {
  border: 1px solid #e4e4e4;
  padding: 10px;
}

.uploadLink {
  color: #409eff;
  font-size: 13px;
  display: inline-block;
  flex-wrap: nowrap;
  width: 250px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  cursor: pointer;
}

.uploadInput {
  width: 250px;
  height: 60px;
  margin-top: 20px;
}

.modify {
  cursor: pointer;
  color: #409eff;
  font-size: 13px;
}

.imgContent {
  width: 200px;
  height: 120px;
  border: 1px dashed #e4e4e4;
  margin-top: 10px;
  display: flex;
  justify-content: center;
  align-items: center;
  line-height: 120px;
}
</style>

<style lang="scss" scoped>
.uploadBox {
  width: 200px;
  height: 80px;
  display: flex;
  justify-content: center;
  align-items: center;
  border: 1px dashed #e4e4e4;
  margin-top: 20px;
}

.homeSwiperForm-form {
  border: 1px solid #e4e4e4;
  padding: 10px;
}

.uploadLink {
  color: #409eff;
  font-size: 13px;
  display: inline-block;
  flex-wrap: nowrap;
  width: 250px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  cursor: pointer;
}

.uploadInput {
  width: 250px;
  height: 60px;
  margin-top: 20px;
}

.modify {
  cursor: pointer;
  color: #409eff;
  font-size: 13px;
}

.imgContent {
  width: 200px;
  height: 120px;
  border: 1px dashed #e4e4e4;
  margin-top: 10px;
  display: flex;
  justify-content: center;
  align-items: center;
  line-height: 120px;
}
</style>
