<script setup lang="ts">
import { upLoad } from '@/api/index'
import { getRecoveryCoupon, saveRecoverySet } from '@/api/recovery/recovery'

import { useGlobalStore } from '@/store/modules/global'
import { CircleClose, Plus } from '@element-plus/icons-vue'

import { useRouter } from 'vue-router'

// 为上传文件定义接口类型
interface UploadFile {
  raw: File
  size: number
  type?: string
  [key: string]: any
}

interface ProductImage {
  img: string
}

interface ButtonStyle {
  btnName: string
  color: string
  sort: number
}

const router = useRouter()
const globalstore = useGlobalStore()

const form = reactive({
  id: null,
  radio: 2,
  radio2: 1,
  btnStyles: [
    {
      btnName: '旧衣回收',
      color: '#409EFF',
      sort: 1,
    },
    {
      btnName: '加入购物车',
      color: '#869926',
      sort: 2,
    },
    {
      btnName: '立即购买',
      color: '#C62D65',
      sort: 3,
    },
  ] as ButtonStyle[],
  productImgList: [] as ProductImage[],
  selectType: 0,
  inputContent: '1、一次性最多上传3张图片。 2、每上传一张图片可获得一张超值优惠券。 3、优惠券可在平台商品通用。',
  btnImg: '',
})

/** 上传图片下标 */
const productImgIndex = ref(0)

const activeTypeList = ref([
  {
    id: 1,
    label: '旧衣回收专用券',
  },
])

onMounted(() => {
  init()
})

/**
 * 提交按钮上传
 */
async function uploadBtnImg(file: any): Promise<void> {
  form.btnImg = await upLoadFile(file)
  globalstore.getShopInfo()
}

async function upLoadFile(file: UploadFile): Promise<string> {
  const whiteList = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif']
  const isLt2M = file.size < 2 * 1024 * 1024
  if (!whiteList.includes(file.raw.type)) {
    ElMessage.error('上传文件只能是 GIF、JPG或PNG 格式!')
    return ''
  }
  if (!isLt2M) {
    ElMessage.error('上传文件大小不能超过 2MB!')
    return ''
  }
  const res = await upLoad({
    file: file.raw,
  })
  return res.data
}

/**
 * @method uploadProductImg
 * @description 修改商品主图
 */
async function uploadProductImg(file: any): Promise<void> {
  const whiteList = ['image/jpeg', 'image/jpg', 'image/png']

  const isLt1M = file.size < 1 * 1024 * 1024
  if (!whiteList.includes(file.raw.type)) {
    ElMessage.error('上传文件只能是 JPG或PNG 格式!')
    return
  }
  if (!isLt1M) {
    ElMessage.error('上传文件大小不能超过 1MB!')
    return
  }

  const res = await upLoad({
    file: file.raw,
  })
  form.productImgList[productImgIndex.value].img = res.data
}

/**
 * 上传图片
 */
async function addUploadProductImg(file: any): Promise<void> {
  const whiteList = ['image/jpeg', 'image/jpg', 'image/png']

  const isLt1M = file.size < 1 * 1024 * 1024
  if (!whiteList.includes(file.raw.type)) {
    ElMessage.error('上传文件只能是 JPG或PNG 格式!')
    return
  }
  if (!isLt1M) {
    ElMessage.error('上传文件大小不能超过 1MB!')
    return
  }

  const res = await upLoad({
    file: file.raw,
  })
  form.productImgList.push({
    img: res.data,
  })
}

/**
 * 删除以选中图片
 */
function cancelImg(item: ProductImage, index: number): void {
  form.productImgList.splice(index, 1)
}

function gotoCoupon() {
  router.push({
    name: 'CouponIndex',
  })
}

/**
 * 初始化信息
 */
async function init() {
  await globalstore.getShopInfo()
  getCouponList()
}

/**
 * 获取关联优惠券
 */
function getCouponList(): void {
  const shopRecInfo = globalstore.recover
  const list: string[] = shopRecInfo.pageCarousels ? shopRecInfo.pageCarousels.split(',') : []
  form.id = shopRecInfo.id
  form.radio = shopRecInfo.recyclingState ? 1 : 2
  form.radio2 = shopRecInfo.entranceState ? 1 : 2
  form.btnStyles = shopRecInfo.btnStyles ? shopRecInfo.btnStyles : form.btnStyles
  form.productImgList = []
  list.forEach((item: string) => {
    form.productImgList.push({
      img: item,
    })
  })
  getRecoveryCoupon({}).then((res) => {
    activeTypeList.value = res.data
  })
  form.selectType = shopRecInfo.assCouponId
  form.inputContent = shopRecInfo.activityDesc
  form.btnImg = shopRecInfo.submitImg
}

/**
 * 提交保存
 */
function saveClick(): void {
  const list: string[] = []
  form.productImgList.forEach((item: ProductImage) => {
    list.push(item.img)
  })
  const assCouponId = form.selectType || ''
  if (assCouponId === '' && form.radio === 1) {
    ElMessage.error('请选择优惠券')
    return
  }
  const btnStyles = form.btnStyles
  for (const i in btnStyles) {
    if (!btnStyles[i].btnName) {
      form.btnStyles = [
        {
          btnName: '旧衣回收',
          color: '#409EFF',
          sort: 1,
        },
        {
          btnName: '加入购物车',
          color: '#869926',
          sort: 2,
        },
        {
          btnName: '立即购买',
          color: '#C62D65',
          sort: 3,
        },
      ]
    }
  }

  const param = {
    recyclingState: form.radio === 1,
    entranceState: form.radio2 === 1,
    btnStyles: form.btnStyles,
    pageCarousels: list.join(','),
    activityDesc: form.inputContent,
    submitImg: form.btnImg,
    id: form.id,
    assCouponId,
  }

  saveRecoverySet(param).then(() => {
    ElMessage.success('保存成功')
  })
}

function changeredio(e: any): void {
  if (e === 2) {
    form.radio2 = 2
  }
}
</script>

<template>
  <div class="setPage flex-1 overflow-auto">
    <div class="setPage__line" style="border-bottom: 1px solid #f0f0f0">
      <div class="setPage__line--title">回收返券</div>
      <div>
        <el-radio-group v-model="form.radio" @change="changeredio">
          <el-radio :value="1"> 开启 </el-radio>
          <el-radio :value="2"> 关闭 </el-radio>
        </el-radio-group>
      </div>
    </div>

    <div v-if="form.radio === 1" class="setPage__line">
      <div class="setPage__line--title">商品详情入口</div>
      <el-radio-group v-model="form.radio2">
        <el-radio :value="1"> 开启 </el-radio>
        <el-radio :value="2"> 关闭 </el-radio>
      </el-radio-group>
    </div>

    <div v-if="form.radio2 === 1 && form.radio === 1" class="goodEnter">
      <div class="goodEnter__poster">
        <img src="../../../../../src/assets/images/shopCar.png" />
      </div>
      <div class="goodEnter__type">
        <div class="goodEnter__type--title">按钮名称</div>
        <div class="goodEnter__type--input">
          <div class="content">
            <el-input v-model="form.btnStyles[0].btnName" style="width: 100px" maxlength="4" />
            <el-color-picker v-model="form.btnStyles[0].color" style="margin-left: 15px" />
            <div class="tip">4字以内</div>
          </div>
          <div class="content">
            <el-input v-model="form.btnStyles[1].btnName" style="width: 100px" maxlength="5" disabled />
            <el-color-picker v-model="form.btnStyles[1].color" style="margin-left: 15px" />
            <!-- <div class="tip">5字以内</div> -->
          </div>
          <div class="content">
            <el-input v-model="form.btnStyles[2].btnName" style="width: 100px" maxlength="4" disabled />
            <el-color-picker v-model="form.btnStyles[2].color" style="margin-left: 15px" />
            <!-- <div class="tip">4字以内</div> -->
          </div>
        </div>
      </div>
    </div>

    <div v-if="form.radio === 1" class="setPage__line">
      <div class="setPage__line--title">启用优惠券</div>
      <div class="setPage__line--con">
        <el-select v-model="form.selectType" class="w-[200px]">
          <el-option v-for="item in activeTypeList" :key="item.id" :label="item.label" :value="item.id" />
        </el-select>

        <div class="gotoBtn" @click="gotoCoupon">前往设置优惠券</div>
      </div>
    </div>

    <div v-if="form.radio === 1" class="setPage__line newSet">
      <div class="setPage__line--title">页面轮播图</div>
      <m-draggable v-model="form.productImgList" itemKey="img" style="margin-left: 10px; display: flex; flex-wrap: wrap">
        <template #item="{ element, index }">
          <div style="position: relative; margin-right: 10px" draggable="true">
            <el-upload class="uploaderImg" :auto-upload="false" :on-change="uploadProductImg" :show-file-list="false">
              <img v-if="element.img" :src="element.img" @click="productImgIndex = index" />
            </el-upload>

            <el-icon v-if="element.img" class="iconCloseStyle" @click="cancelImg(element, index)">
              <CircleClose />
            </el-icon>
          </div>
        </template>
      </m-draggable>
    </div>
    <div v-if="form.radio === 1" class="setPage__line--tip" style="margin-left: 110px; margin-top: 20px">
      <el-upload
        v-if="form.productImgList.length < 16"
        class="iconStyle"
        :auto-upload="false"
        :on-change="addUploadProductImg"
        :show-file-list="false"
      >
        <el-icon><Plus /></el-icon>
      </el-upload>
      <div>建议尺寸：690 * 300px</div>
    </div>

    <div v-if="form.radio === 1" class="setPage__line" style="margin-top: 20px">
      <div class="setPage__line--title">活动说明</div>
      <el-input v-model="form.inputContent" class="setPage__line--putBox" type="textarea" :rows="5" />
    </div>

    <div v-if="form.radio === 1" class="setPage__line" style="margin-top: 100px">
      <div class="setPage__line--title">提交按钮</div>
      <div class="setPage__line--tip">
        <el-upload class="uploadClass" :auto-upload="false" :on-change="uploadBtnImg" :show-file-list="false">
          <img v-if="form.btnImg" :src="form.btnImg" />
          <el-icon v-else>
            <Plus />
          </el-icon>
        </el-upload>
        <div>建议尺寸：750 * 98px</div>
      </div>
    </div>

    <div style="width: 88px; margin: 60px 10px 10px 10px">
      <div class="pageBotton" @click="saveClick">保存</div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.setPage {
  &__line {
    display: flex;
    padding-left: 10px;
    height: 60px;
    align-items: center;

    &--title {
      width: 100px;
      font-size: 14px;
      font-weight: bold;
      color: #586884;
    }

    &--putBox {
      width: 400px;
      margin-top: 90px;
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
      width: 220px;
      height: 100px;
      display: flex;
      justify-content: center;
      align-items: center;
      padding: 10px;
      border: 1px dashed #c3c3c3;
    }

    &--tip {
      display: flex;
      align-items: center;

      .uploadClass {
        border: 1px dashed #c3c3c3;
        width: 100px;
        height: 60px;
        border-radius: 10px;
        display: flex;
        justify-content: center;
        align-items: center;
        margin-right: 20px;
      }

      img {
        width: 100px;
        height: 60px;
        padding: 5px;
        border-radius: 10px;
      }

      i {
        color: #7f7f7f;
        font-size: 13px;
      }

      div {
        color: #cfcfcf;
        font-size: 14px;
      }
    }
  }
}

.goodEnter {
  margin: 10px 0px 20px 110px;

  &__poster {
    border: 1px dashed #e5e5e5;
    width: 280px;
    height: 140px;
    display: flex;
    justify-content: center;
    align-items: center;

    img {
      width: 270px;
      height: 130px;
    }
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

.imgList {
  width: 500px;
  display: flex;
  flex-wrap: wrap;
}

.iconCloseStyle {
  position: absolute;
  right: 8px;
  top: -5px;
  font-size: 18px;
  cursor: pointer;
  color: #c3c3c3;
}

.iconStyle {
  width: 50px;
  height: 50px;
  display: flex;
  justify-content: center;
  align-items: center;
  border: 1px dashed #c3c3c3;
  margin-right: 10px;
}

.uploaderImg {
  width: 130px;
  height: 100px;
  display: flex;
  justify-content: center;
  align-items: center;
  border: 1px dashed #c3c3c3;
  margin-right: 20px;

  img {
    width: 120px;
    height: 90px;
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
