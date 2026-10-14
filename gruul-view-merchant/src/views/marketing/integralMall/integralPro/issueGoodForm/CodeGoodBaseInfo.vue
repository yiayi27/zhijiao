<script setup lang="ts">
import { getAllAttsList, getLogistics } from '@/api/good/goods'
import { upLoad } from '@/api/index'
import { storage } from '@/libs/storage'
import LogModel from '@/views/logistics/common/LogModel.vue'
import { CircleClose, Plus } from '@element-plus/icons-vue'

import { useRouter } from 'vue-router'
import type { FormInstance, UploadFile, UploadFiles } from 'element-plus'

interface BaseModel {
  integralGoodName: string
  guidePrice: number
  inventory: number
  integralPrice: number
  attributeId: string
  attributeName: string
  interalGoodsAttibutes: Array<{
    content: string
    name: string
    id: string
    integralGoodsId: string
    value: string
  }>
  pic: string
  albumPics: string
  freightTemplateId: string
  sales: number
  weight: number
  id?: string
}

interface AttributeTemplate {
  id: string
  name: string
  attributeTemplates: Array<{
    content: string
    name: string
    id: string
  }>
}

interface LogisticsTemplate {
  id: string
  name: string
}

interface ProductImage {
  name: string
  url: string
  img?: string
}

const props = defineProps({
  formModel: {
    type: Object as () => Partial<BaseModel>,
    default: () => ({}),
  },
})

const emit = defineEmits(['saveFlag'])

// 组件引用
const router = useRouter()
const formRef = ref<FormInstance>()

// 当前步骤模板
const baseModel = reactive<BaseModel>({
  integralGoodName: '',
  guidePrice: 0,
  inventory: 0,
  integralPrice: 0,
  attributeId: '',
  attributeName: '',
  interalGoodsAttibutes: [],
  pic: '',
  albumPics: '',
  freightTemplateId: '',
  sales: 0,
  weight: 0,
})

// // 配送设置内容
// const rections = reactive({
//   autoShippingState: '', // 设置新增提货点是否自动设置送货上门,0false=禁用,1true=启用
//   goodPointIdsPercentage: '',
// })

// 属性模板数组
const temAttsList = ref<AttributeTemplate[]>([])

// 物流模板数组
const logModelOptions = ref<LogisticsTemplate[]>([])

// 上传商品图片
const productImgList = ref<ProductImage[]>([])

// 上传图片下标
const productImgIndex = ref(0)

// 重量信息
const isWeight = ref(false)

function weightVlidator(_rule: any, value: any, callback: any) {
  const regs = /^\d+.?\d{0,2}$/
  if (Number(value) === 0) {
    callback(new Error('重量不能为0'))
  }
  if (!regs.test(value)) {
    callback(new Error('重量输入有误,数字最多保留两位小数'))
  } else {
    callback()
  }
}

// 基本信息验证
const formRules = {
  integralGoodName: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  inventory: [{ required: true, message: '请输入商品库存', trigger: 'blur' }],
  integralPrice: [{ required: true, message: '请输入商品积分价', trigger: 'blur' }],
  pic: [
    {
      required: true,
      validator: (_rule: any, value: any, callback: any) => {
        if (!value) {
          callback(new Error('请上传商品主图'))
        } else {
          callback()
        }
      },
      trigger: 'change',
    },
  ],
  freightTemplateId: {
    required: true,
    message: '请选择运费模板',
    trigger: 'change',
  },
  weight: [
    { required: true, message: '请输入商品重量', trigger: 'blur' },
    { validator: weightVlidator, trigger: 'blur' },
  ],
}

watch(
  () => props.formModel,
  (nVal) => {
    if (nVal) {
      Object.assign(baseModel, nVal)
      productImgList.value = getProductImgList(nVal.albumPics || '')
    }
  },
  { deep: true, immediate: true },
)

/**
 * 获取表单数据
 */
async function getFormModel() {
  try {
    await validate()
  } catch (error) {
    console.error(error)
    return Promise.reject(false)
  }
}

/**
 * 对整个表单进行校验
 */
function validate() {
  return formRef.value?.validate()
}

/**
 * 获取物流模板
 */
async function getLogis() {
  const res = await getLogistics({})
  res.data.unshift({
    id: '0',
    name: '商家包邮',
  })
  logModelOptions.value = res.data
  baseModel.freightTemplateId = baseModel.id ? baseModel.freightTemplateId : '0'
}

/**
 * 选择物流模板
 */
function selectLogModel(modelOption: LogisticsTemplate) {
  if (modelOption.id === '0') {
    isWeight.value = false
  }
}

/**
 * 获取商品属性
 */
async function getTemAttsList() {
  const { data } = await getAllAttsList({})
  temAttsList.value = data || []
}

/**
 * 选择商品属性
 */
function selectTemAttsList(temAttsId: string) {
  const currenttemAtts = temAttsList.value.find((item) => item.id === temAttsId)
  if (currenttemAtts && currenttemAtts.attributeTemplates) {
    baseModel.attributeName = currenttemAtts.name
    baseModel.interalGoodsAttibutes = currenttemAtts.attributeTemplates.map((item) => {
      return {
        ...item,
        value: item.content,
        integralGoodsId: '',
      }
    })
  }
}

/**
 * 添加商品属性
 */
function addTemAtts() {
  const obj = {
    content: '',
    name: '',
    id: '',
    integralGoodsId: '',
    value: '',
  }
  baseModel.interalGoodsAttibutes.push(obj)
}

/**
 * 删除商品属性
 */
function delAttr(index: number) {
  baseModel.interalGoodsAttibutes.splice(index, 1)
}

/**
 * 前往设置商品属性
 */
function jumpClass(type: number) {
  let location = {}
  storage.set('baseModel', JSON.stringify(baseModel))
  emit('saveFlag', true)

  switch (type) {
    case 1:
      location = {
        name: 'AttributeTemple',
        query: { options: 'pick' },
      }
      break
    case 2:
      location = {
        name: 'LogisticsIndex',
        query: { options: 'log' },
      }
      break
  }
  router.push(location)
}

/**
 * 获取商品图片列表
 */
function getProductImgList(albumPics: string) {
  if (!albumPics) return []
  return albumPics.split(',').map((item) => {
    return {
      name: item,
      url: item,
      img: item,
    }
  })
}

/**
 * @method uploadProductImg
 * @description 修改商品主图
 */
async function uploadProductImg(file: any) {
  const whiteList = ['image/jpeg', 'image/jpg', 'image/png']

  const isLt1M = file.size < 2 * 1024 * 1024
  if (!whiteList.includes(file.raw.type)) {
    ElMessage.error('上传文件只能是 JPG或PNG 格式!')
    return
  }
  if (!isLt1M) {
    ElMessage.error('上传文件大小不能超过 2MB!')
    return
  }
  const res = await upLoad({
    file: file.raw,
  })
  if (productImgList.value[productImgIndex.value]) {
    productImgList.value[productImgIndex.value].img = res.data
  }
  baseModel.albumPics = productImgList.value
    .filter((item) => item.img !== '')
    .map((item) => item.img)
    .join(',')
  baseModel.pic = productImgList.value[0]?.img || ''
  formRef.value?.clearValidate('pic')
}

/**
 * 上传商品图片
 */
async function addUploadProductImg(uploadFile: UploadFile, uploadFiles: UploadFiles) {
  if (!uploadFile.raw) return
  const whiteList = ['image/jpeg', 'image/jpg', 'image/png']

  const isLt1M = uploadFile.raw.size < 1 * 1024 * 1024
  if (!whiteList.includes(uploadFile.raw.type)) {
    ElMessage.error('上传文件只能是 JPG或PNG 格式!')
    return
  }
  if (!isLt1M) {
    ElMessage.error('上传文件大小不能超过 1MB!')
    return
  }
  const res = await upLoad({ file: uploadFile.raw })
  productImgList.value.push({
    name: res.data,
    url: res.data,
    img: res.data,
  })
  baseModel.albumPics = productImgList.value
    .filter((item) => item.img !== '')
    .map((item) => item.img)
    .join(',')
  baseModel.pic = productImgList.value[0]?.img || ''
  formRef.value?.clearValidate('pic')
}

/**
 * 删除已选中图片
 */
function cancelImg(item: ProductImage, index: number) {
  productImgList.value.splice(index, 1)
  if (productImgList.value.length === 0) {
    baseModel.pic = ''
  }
}

/**
 * 获取模板信息
 */
function getModelOption(modelOption: any) {
  isWeight.value = modelOption?.valuationModel === 2
}

onMounted(() => {
  getTemAttsList()
  getLogis() //  物流模板

  // 从localStorage获取
  const savedModel = storage.get('baseModel')
  if (savedModel) {
    Object.assign(baseModel, JSON.parse(savedModel))
  }

  if (baseModel.albumPics) {
    productImgList.value = getProductImgList(baseModel.albumPics)
  }
})

// 暴露方法给父组件
defineExpose({
  getFormModel,
  validate,
  baseModel,
  isWeight,
})
</script>

<template>
  <div>
    <el-form ref="formRef" :model="baseModel" :rules="formRules" label-width="100px">
      <div class="navLine">基本信息</div>
      <div class="baseMsg">
        <el-form-item label="商品名称" prop="integralGoodName">
          <el-input v-model="baseModel.integralGoodName" style="width: 520px" placeholder="请输入商品名称" maxlength="30" />
        </el-form-item>
        <el-form-item label="市场价">
          <el-input-number v-model="baseModel.guidePrice" :min="0" :controls="false" style="width: 520px" placeholder="请输入市场价" />
        </el-form-item>
        <el-form-item label="库存" prop="inventory">
          <el-input-number
            v-model="baseModel.inventory"
            :min="0"
            step-strictly
            :step="1"
            :controls="false"
            style="width: 520px"
            placeholder="请输入商品库存"
          />
        </el-form-item>
        <el-form-item label="积分" prop="integralPrice">
          <el-input-number
            v-model="baseModel.integralPrice"
            :min="0"
            step-strictly
            :step="1"
            :controls="false"
            style="width: 520px"
            placeholder="请输入商品积分价"
          />
        </el-form-item>
        <el-form-item label="商品属性">
          <el-select v-model="baseModel.attributeId" placeholder="请选择属性模板" style="width: 450px" @change="selectTemAttsList">
            <el-option v-for="item in temAttsList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
          <el-button link class="ml-20" @click="jumpClass(1)"> 前往设置 </el-button>
          <div class="valueName">
            <div class="flex between">
              <div>属性名称</div>
              <div>属性值</div>
              <div>操作</div>
            </div>
            <div v-if="baseModel.interalGoodsAttibutes.length === 0" class="valueName__noList">暂无数据~</div>
            <div v-for="(item, index) in baseModel.interalGoodsAttibutes" v-else :key="index" class="valueName__List">
              <el-input v-model="item.name" style="width: 150px" maxlength="10" />
              <el-input v-model="item.value" style="width: 150px" maxlength="20" />
              <div style="width: 30px" @click="delAttr(index)">删除</div>
            </div>
          </div>
        </el-form-item>
        <el-button type="primary" plain style="margin-left: 100px; margin-bottom: 20px" @click="addTemAtts"> + 添加属性 </el-button>
        <el-form-item label="商品主图" prop="pic">
          <div>
            <div class="text-info fs-12" style="color: rgba(69, 64, 60, 0.6)">
              尺寸建议750×1000（长图模式）或750x750（正方形模式）像素以上，大小1M以下，最多6张 (可拖拽图片调整顺序 )
            </div>
            <div class="product" style="display: flex">
              <m-draggable v-model="productImgList" item-key="img">
                <template #item="{ element, index }">
                  <div style="position: relative; margin-right: 20px" class="product__productImg" draggable="true">
                    <el-upload
                      class="productImg__uploader product__productImg"
                      :auto-upload="false"
                      :on-change="uploadProductImg"
                      :show-file-list="false"
                    >
                      <img v-if="element.img" :src="element.img" class="product__productImg" @click="productImgIndex = index" />
                    </el-upload>
                    <div v-if="index === 0" class="productImg-text">封面图</div>
                    <el-icon v-if="element.img" class="productImg-icon" @click="cancelImg(element, index)">
                      <CircleClose />
                    </el-icon>
                  </div>
                </template>
              </m-draggable>
              <el-upload
                v-if="productImgList.length < 6"
                class="productImg__uploader product__productImg"
                :auto-upload="false"
                :on-change="addUploadProductImg"
                :show-file-list="false"
              >
                <el-icon class="product__productImg--uploader--icon">
                  <Plus />
                </el-icon>
                <div class="product__productImg--uploader--add">添加图片</div>
              </el-upload>
            </div>
          </div>
        </el-form-item>
      </div>
      <div class="navLine">其他信息</div>
      <el-form-item label="运费模板" prop="freightTemplateId">
        <div class="w-full">
          <div class="flex items-center">
            <div class="w-[200px]">
              <el-select v-model="baseModel.freightTemplateId" placeholder="请选择运费模板" @change="selectLogModel">
                <el-option v-for="item in logModelOptions" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </div>

            <el-button link class="ml-[20px]" @click="jumpClass(2)"> 前往设置 </el-button>
          </div>

          <template v-if="baseModel.freightTemplateId !== '0'">
            <LogModel :template-id="baseModel.freightTemplateId" @get-model-option="getModelOption" />
          </template>
        </div>
      </el-form-item>

      <el-form-item label="初始销量">
        <el-input-number v-model="baseModel.sales" style="width: 520px" :controls="false" placeholder="0" :min="0" step-strictly :step="1" />
      </el-form-item>
      <el-form-item v-if="isWeight" label="商品重量" prop="weight">
        <el-input-number v-model="baseModel.weight" :min="0" :controls="false" style="width: 520px" placeholder="0" />
      </el-form-item>
    </el-form>
  </div>
</template>

<style lang="scss" scoped>
@import '@/assets/styles/goods/index.scss';

.pageRoll {
  overflow: auto;
  overflow-x: hidden;
  height: 750px;
}

.pageRoll::-webkit-scrollbar {
  width: 0 !important;
  height: 0 !important;
}

.navLine {
  margin-top: 40px;
  margin-bottom: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  background-color: #f8f8f8;
  padding-left: 30px;
  font-weight: bold;
}

.spec {
  margin-left: 40px;
  margin-bottom: 20px;

  &__left {
    margin-left: 10px;
  }
}

.specitem {
  margin-left: 100px;

  &__inp {
    display: flex;
    align-items: flex-end;
    margin-top: 10px;

    &--inside {
      width: 50px;
      margin: 0px 10px;
    }
  }
}

.dialogTitle {
  display: flex;
  justify-content: center;
  font-weight: bold;
  font-size: 16px;
}

.dialog__line {
  display: flex;
  padding-left: 20px;
  padding-top: 10px;
  justify-content: flex-start;
  align-items: flex-start;

  &__fir {
    width: 100%;
    text-align: right;
    display: flex;
    align-items: center;
  }

  &__sec {
    width: 100%;
    display: flex;
    margin-top: 10px;

    &--head {
      padding: 10px;
      display: flex;
      justify-content: space-between;
      font-size: 12px;
      background-color: #f6f6f6;
    }

    &--body {
      width: 100%;
      display: flex;
      justify-content: space-between;
      align-items: center;
      border-bottom: 1px solid #999;
      padding: 10px;
    }
  }
}

.add--text {
  line-height: 30px;
  cursor: pointer;
  color: #2d8cf0;
  margin-left: 90px;
  margin-top: 10px;
}

.pick--dia {
  display: flex;
  justify-content: space-between;
  padding: 10px 30px;
  background-color: #e9f3fb;
}

.pick--input {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 30px;
}

.valueName {
  margin-top: 10px;
  width: 550px;
  padding: 20px 40px;
  border: 1px solid #d7d7d7;
  border-radius: 10px;

  &__noList {
    display: flex;
    justify-content: center;
    color: #d7d7d7;
    font-size: 15px;
    margin-left: 20px;
    margin-top: 20px;
  }

  &__List {
    display: flex;
    justify-content: space-between;
    margin-bottom: 10px;

    div {
      color: red;
      cursor: pointer;
    }
  }
}

.product {
  margin-top: 10px;
}

.productVideo__uploader {
  display: flex;
  align-items: flex-end;
}

.productImg__uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.productImg__uploader:hover {
  border-color: #409eff;
}

.product__productImg {
  width: 100px;
  height: 100px;
  display: inline-block;
}

.product__productVideo {
  width: 260px;
  height: 120px;
  display: inline-block;
}

.product__productVideo--uploader--icon {
  font-size: 28px;
  color: #8c939d;
  width: 260px;
  height: 120px;
  line-height: 120px;
  text-align: center;
}

.productImg-icon {
  position: absolute;
  right: -8px;
  top: -5px;
  font-size: 20px;
  text-align: center;
  color: #7f7f7f;
  background-color: white;
  border-radius: 50px;
}

.product__productImg--uploader--icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  line-height: 80px;
  text-align: center;
}

.product__productImg--uploader--add {
  position: absolute;
  top: 55px;
  left: 20px;
  color: #7f7f7f;
}

.productImg-uploader-clear {
  position: absolute;
  right: -0.375rem;
  top: -0.375rem;
  font-size: 20px;
  cursor: pointer;
}

.productImg-text {
  position: absolute;
  right: 0;
  bottom: 0;
  font-size: 12px;
  text-align: center;
  width: 100%;
  background-color: rgba(0, 0, 0, 0.3);
  border-radius: 0px 0px 6px 6px;
  color: #ffffff;
}
</style>
