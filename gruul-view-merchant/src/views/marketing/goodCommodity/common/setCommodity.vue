<script setup lang="ts">
import { saveCommoditySet } from '@/api/commodity/commodity'
import { upLoad } from '@/api/index'
import { useGlobalStore } from '@/store/modules/global'

import { Plus } from '@element-plus/icons-vue'

defineOptions({
  name: 'SetCommodity',
})

const form = reactive({
  id: null as any,
  radio: 1,
  input: 0,
  img: '',
  baseImg: '',
})

const globalStore = useGlobalStore()

/**
 * 初始化信息
 */
async function init() {
  const shopRecInfo = await globalStore.getShopInfo()
  form.id = shopRecInfo.id
  form.radio = shopRecInfo.goodsFundState ? 1 : 2
  form.input = shopRecInfo.fundQuota
  form.img = shopRecInfo.pageBgImg
  form.baseImg = shopRecInfo.buyBgImg
}

async function uploadProBaseductImg(file: any) {
  form.baseImg = await upLoadFile(file)
}

async function uploadProductImg(file: any) {
  form.img = await upLoadFile(file)
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

/**
 * 提交保存
 */
async function saveSubmit() {
  if (form.input <= 0) {
    ElMessage.error('请正确填写可获取基金数额')
    return
  }
  const param = {
    buyBgImg: form.baseImg,
    fundQuota: form.input,
    goodsFundState: form.radio === 1,
    pageBgImg: form.img,
    id: form.id,
  }
  saveCommoditySet(param).then(() => {
    globalStore.getShopInfo()
    ElMessage.success('保存成功')
  })
}

onMounted(() => {
  init()
})

// 导出方法供父组件使用
defineExpose({
  init,
  uploadProBaseductImg,
  uploadProductImg,
  upLoadFile,
  saveSubmit,
})
</script>

<template>
  <div class="setPage flex-1 overflow-auto">
    <div class="setPage__line">
      <div class="setPage__line--title">商品基金</div>
      <div>
        <el-radio-group v-model="form.radio">
          <el-radio :value="1"> 开启 </el-radio>
          <el-radio :value="2"> 关闭 </el-radio>
        </el-radio-group>
      </div>
    </div>

    <div class="setPage__line">
      <div class="setPage__line--title">基金设置</div>
      <div class="setPage__line--con">
        <p>每个商品可获得基金</p>
        <el-input v-model="form.input" min="0" max="100" type="number" style="width: 200px">
          <template #append>
            <el-button>%</el-button>
          </template>
        </el-input>
      </div>
    </div>

    <div class="setPage__line newSet">
      <div class="setPage__line--title">页面背景</div>
      <el-upload class="setPage__line--upload" :auto-upload="false" :on-change="uploadProductImg" action="" :show-file-list="false">
        <img v-if="form.img" :src="form.img" />
        <el-icon v-else>
          <Plus />
        </el-icon>
      </el-upload>
      <div class="setPage__line--tip">
        <el-upload :auto-upload="false" :on-change="uploadProductImg" action="" :show-file-list="false">
          <p>上传样式</p>
        </el-upload>
        <div>建议尺寸：750 * 1350px</div>
      </div>
    </div>

    <div class="setPage__line popTip">
      <div class="setPage__line--title">购买成功获取基金弹窗</div>
    </div>
    <div style="display: flex">
      <div class="setPage__baseUpload">
        <el-upload class="setPage__baseUpload--inUpload" :auto-upload="false" :on-change="uploadProBaseductImg" action="" :show-file-list="false">
          <img v-if="form.baseImg" :src="form.baseImg" />
          <el-icon v-else>
            <Plus />
          </el-icon>
        </el-upload>

        <div v-if="form.baseImg" class="setPage__baseUpload--overflow">
          <div class="closeStyle">关闭按钮</div>
          <div class="textStyle">字段位置</div>
          <div class="btnStyle">按钮区域</div>
        </div>
      </div>

      <div class="setTip">
        <div class="setPage__line--tip" style="height: 40px; margin-top: 20px">
          <el-upload :auto-upload="false" :on-change="uploadProBaseductImg" action="" :show-file-list="false">
            <p>上传样式</p>
          </el-upload>
          <div>建议尺寸：750 * 1350px</div>
        </div>
        <div class="setTip__tipTwo">设计图片上传时，请将点击按钮尽量对其按钮位置，并在字段位置留空</div>
      </div>
    </div>
    <div style="width: 88px; margin: 20px 10px">
      <div class="pageBotton" @click="saveSubmit">保存</div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.setPage {
  &__line {
    display: flex;
    padding-left: 10px;
    height: 60px;
    border-bottom: 1px solid #f0f0f0;
    align-items: center;

    &--title {
      margin-right: 40px;
      font-size: 14px;
      font-weight: bold;
      color: #586884;
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
      width: 160px;
      height: 200px;
      display: flex;
      justify-content: center;
      align-items: center;
      background-color: #eef1f6;

      img {
        width: 160px;
        height: 200px;
      }
    }

    &--tip {
      display: flex;
      margin: 80px 0px 0px 40px;
      p {
        color: #2e99f3;
        font-size: 14px;
        margin-right: 30px;
        cursor: pointer;
      }
      div {
        color: #cfcfcf;
        font-size: 14px;
      }
    }
  }

  &__baseUpload {
    position: relative;
    border: 2px dashed #e6e6e6;
    width: 280px;
    height: 310px;
    border-radius: 10px;
    margin-left: 100px;
    display: flex;
    justify-content: center;
    align-items: center;
    overflow: hidden;

    &--inUpload {
      display: flex;
      justify-content: center;
      align-items: center;
      height: 100%;
      width: 100%;
      position: relative;

      img {
        width: 100%;
        height: 100%;
      }
    }

    &--overflow {
      position: absolute;
      top: 10px;
      left: 10px;
      bottom: 10px;
      right: 10px;

      .closeStyle {
        position: absolute;
        right: 2px;
        top: 2px;
        padding: 10px 10px;
        background-color: rgba(0, 0, 0, 0.4);
        color: white;
        font-size: 12px;
      }

      .textStyle {
        position: absolute;
        right: 50px;
        top: 90px;
        padding: 20px 50px;
        background-color: rgba(0, 0, 0, 0.4);
        color: white;
        font-size: 12px;
      }

      .btnStyle {
        position: absolute;
        right: 30px;
        bottom: 12px;
        padding: 10px 50px;
        background-color: rgba(0, 0, 0, 0.4);
        color: white;
        font-size: 12px;
        height: 46px;
        width: 200px;
        display: flex;
        justify-content: center;
        align-items: center;
      }
    }
  }
}

.newSet {
  margin-top: 30px;
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
  flex: 1;

  &__tipTwo {
    color: #ff3b4b;
    font-size: 14px;
    margin-left: 40px;
  }
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
