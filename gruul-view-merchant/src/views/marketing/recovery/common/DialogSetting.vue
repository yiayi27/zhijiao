<script setup lang="ts">
import { upLoad } from '@/api/index'
import { getDialogImg } from '@/api/recovery/recovery'
import { useGlobalStore } from '@/store/modules/global'
import { Plus } from '@element-plus/icons-vue'

const form = reactive({
  id: null,
  baseImg: '',
})

onMounted(() => {
  init()
})

// 为 file 参数定义接口类型
interface UploadFile {
  raw: File
  size: number
  [key: string]: any
}

async function uploadBaseImg(file: any) {
  form.baseImg = await upLoadFile(file)
}

async function upLoadFile(file: UploadFile) {
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

/**
 * 初始化信息
 */
const globalStore = useGlobalStore()
function init() {
  const shopRecInfo = globalStore.recover
  form.id = shopRecInfo.id
  form.baseImg = shopRecInfo.homePromptBgImg
}

/**
 * 提交保存
 */
async function sureSubmit() {
  const param = {
    homePromptBgImg: form.baseImg,
    id: form.id,
  }

  await getDialogImg(param)
  ElMessage.success('保存成功')
}
</script>

<template>
  <div class="flex-1 overflow-auto">
    <div class="setPage__line--title newSet">首页提示弹窗</div>
    <div class="setPage__line newSet">
      <el-upload
        class="setPage__line--upload"
        style="width: 230px; height: 250px"
        :auto-upload="false"
        :on-change="uploadBaseImg"
        :show-file-list="false"
      >
        <img v-if="form.baseImg" :src="form.baseImg" style="width: 220px; height: 240px" />
        <el-icon v-else>
          <Plus />
        </el-icon>
        <div class="closeArea">关闭</div>
        <div class="clickArea">按钮区域</div>
      </el-upload>
      <div class="setPage__line--tip">
        <div class="tipSet">
          <el-upload :auto-upload="false" :on-change="uploadBaseImg" :show-file-list="false">
            <p>上传样式</p>
          </el-upload>

          <div>建议尺寸：610 * 550px</div>
        </div>
        <div class="redTip">设计图片上传时，请将点击按钮尽量对其按钮位置</div>
      </div>
    </div>
    <div style="width: 88px; margin: 60px 10px 10px 10px">
      <div class="pageBotton" @click="sureSubmit">保存</div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.setPage {
  &__line {
    display: flex;
    margin-left: 80px;
    height: 60px;
    align-items: center;

    &--title {
      margin-right: 40px;
      font-size: 14px;
      font-weight: bold;
      color: #586884;
    }

    &--putBox {
      width: 400px;
      margin-top: 90px;
      margin-left: 15px;
    }

    &--con {
      display: flex;
      align-items: center;
      p {
        margin-right: 20px;
        color: #586884;
      }
    }

    &--upload {
      display: flex;
      justify-content: center;
      align-items: center;
      padding: 10px;
      border: 1px dashed #c3c3c3;
      border-radius: 10px;
      position: relative;

      img {
        width: 160px;
        height: 280px;
      }

      .closeArea {
        position: absolute;
        display: flex;
        justify-content: center;
        align-items: center;
        color: white;
        font-size: 12px;
        right: 0px;
        top: 0px;
        height: 28px;
        width: 50px;
        background-color: rgba(0, 0, 0, 0.5);
      }

      .clickArea {
        position: absolute;
        display: flex;
        justify-content: center;
        align-items: center;
        color: white;
        font-size: 12px;
        left: 0px;
        bottom: 25px;
        height: 30px;
        width: 100%;
        background-color: rgba(0, 0, 0, 0.5);
      }
    }

    &--tip {
      display: flex;
      flex-wrap: wrap;
      align-items: center;
      margin: 40px 0px 0px 40px;
      width: 350px;

      .tipSet {
        display: flex;
        justify-content: center;
        align-items: center;
        p {
          color: #2e99f3;
          font-size: 14px;
          margin-right: 10px;
          cursor: pointer;
        }
        div {
          color: #cfcfcf;
          font-size: 14px;
        }
      }

      .redTip {
        margin-top: 20px;
        font-size: 14px;
        color: #ff3b4b;
      }
    }
  }
}

.goodEnter {
  margin: 10px 0px 20px 130px;

  &__poster {
    border: 1px dashed #e5e5e5;
    width: 260px;
    height: 120px;
  }

  &__type {
    display: flex;
    margin-top: 30px;
    align-items: center;

    &--title {
      font-size: 14px;
      font-weight: bold;
      color: #586884;
      margin-right: 40px;
    }

    &--input {
      display: flex;

      .content {
        display: flex;
        justify-content: center;
        align-items: center;
        margin-right: 20px;
      }

      .tip {
        font-size: 14px;
        color: #cfcfcf;
        margin-left: 15px;
      }
    }
  }
}

.newSet {
  margin-top: 20px;
  display: flex;
  border: none;
  height: auto;
  align-items: flex-start;
}

.popTip {
  border: none;
  margin-top: 30px;
}

.setTip {
  display: flex;
  flex-wrap: wrap;
  height: 60px;

  &__tipTwo {
    color: #ff3b4b;
    font-size: 14px;
    margin-left: 40px;
  }
}

.gotoBtn {
  margin-left: 40px;
  padding: 10px 20px;
  border-radius: 50px;
  font-size: 13px;
  font-weight: bold;
  margin-right: 20px;
  cursor: pointer;
  background-color: #2e99f3;
  color: white;
}

.pageBotton {
  padding: 10px 20px;
  border-radius: 50px;
  font-size: 13px;
  font-weight: bold;
  margin-right: 20px;
  cursor: pointer;
  background-color: #2e99f3;
  color: white;
}
</style>
