<script setup lang="ts">
import { getAllAttsList, getAllCategory, getAllRegionList, getAllSupList, getLogistics } from '@/api/good/goods'
import { upLoad } from '@/api/index'
import { NEW_GOOD_BASE_INFO_MODEL, NEW_GOOD_SELL_INFO_MODEL, NEW_GOOD_SHOP_INFO_MODEL } from '@/libs/enum/storage'
import { storage } from '@/libs/storage'
import { advanceGetImage, readFile } from '@/libs/voidImg'
import LogModel from '@/views/logistics/common/LogModel.vue'

import { CircleClose, Plus } from '@element-plus/icons-vue'

import { onBeforeRouteLeave, useRouter } from 'vue-router'

// Props 定义
const props = defineProps({
  goodDetail: {
    type: Object,
    default: () => null,
  },
  goodFormModel: {
    type: Object,
    default: () => ({}),
  },
  isCsv: {
    type: Boolean,
    default: false,
  },
  saleMode: {
    type: String,
    default: '',
  },
  isWeight: {
    type: Boolean,
    default: false,
  },
  logisticsId: {
    type: String,
    default: '',
  },
  cacheInfo: {
    type: Object,
    default: () => ({}),
  },
})

// Emits 定义
const emit = defineEmits(['update:isWeight', 'update:logisticsId'])

// Refs 和响应式状态
const formRef = ref()
const logRef = ref()
const router = useRouter()

// 响应式数据
const formModel = reactive<any>({})
const widePic = ref('')
const productImgIndex = ref(0)
const showSale = ref(false)
const saleApartList = ref<any[]>([])
const temShowList = ref<any[]>([])
const temSupList = ref<any[]>([])
const temAttsList = ref<any[]>([])
const logModel = ref<any[]>([])
const productImgList = ref<any[]>([])

/** 基本信息验证 */
const formRules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  saleName: [{ required: true, message: '请选择销售专区', trigger: 'blur' }],
  showCategoryIds: [{ required: true, message: '请选择分类', trigger: 'blur' }],
  pic: [
    {
      required: true,
      validator: (rule: any, value: any, callback: any) => {
        if (!value) {
          callback(new Error('请上传商品主图'))
        } else {
          callback()
        }
      },
      trigger: 'change',
    },
  ],
  distributionMode: [{ required: true, message: '请选择配送方式', trigger: 'change' }],
  freightTemplateId: [{ required: true, message: '请选择物流模板', trigger: 'change' }],
}

// 计算属性
const isWeight = computed({
  get: () => props.isWeight,
  set: (val) => emit('update:isWeight', val),
})

const logisticsId = computed({
  get: () => props.logisticsId,
  set: (val) => emit('update:logisticsId', val),
})

// 监听 saleName 变化
watch(
  () => formModel.saleName,
  () => {
    const dropItem = saleApartList.value.find((item) => item.modeName === formModel.saleName)
    if (dropItem) {
      formModel.saleMode = dropItem.id
      getTemShowList() // 展示分类
    }
  },
)

// 监听 goodDetail 变化
watch(
  () => props.goodDetail,
  (nVal) => {
    if (nVal) {
      setFormModel(nVal)
    }
  },
  { immediate: true },
)

// 组件挂载时
onMounted(() => {
  getTemShowList() // 展示分类
  getTemSupList() // 供应商模板
  getTemAttsList() // 属性模板
  getLogis() //  物流模板

  if (!props.goodDetail) {
    Object.assign(formModel, props.goodFormModel)
    formModel.saleMode = props.saleMode
  }

  const cacheModel = storage.get(NEW_GOOD_BASE_INFO_MODEL)
  if (cacheModel) {
    setFormModel(cacheModel, true)
  }
})

/**
 * 点击下拉销售区域
 */
function selectMode() {
  temShowList.value = []
  formModel.showCategoryIds = ''
}

/**
 * 还原表单数据
 */
async function setFormModel(nVal: any, isCache = false) {
  if (!nVal) return

  const res = await getAllRegionList({})
  saleApartList.value = res.data
  widePic.value = nVal.widePic
  showSale.value = props.isCsv

  Object.assign(formModel, nVal)
  productImgList.value = getProductImgList(nVal.albumPics)

  if (!isCache) {
    formModel.showCategoryIds = getShowCategoryIds(nVal.productShowCategorys)
  }

  if (props.isCsv) {
    formModel.distributionMode = 1
    formModel.freightTemplateId = '0'
    formModel.saleName = saleApartList.value?.[0]?.modeName

    // const dropItem = saleApartList.value.find((item) => item.modeName === formModel.saleName)
    // if (dropItem) {
    //   formModel.saleMode = dropItem.id
    //   getTemShowList() // 展示分类
    // }
  }
}

/**
 * 对整个表单进行校验
 */
const validate = async () => await formRef.value.validate()

/**
 * 获取表单数据
 */
async function getFormModel() {
  try {
    setAlbumPics()
    await validate()
    setProviderName()
    const productShowCategorys = getProductShowCategorys()

    return {
      ...formModel,
      productShowCategorys,
    }
  } catch (error) {
    console.error(error)
    return Promise.reject(false)
  }
}

/**
 * 移除表单项的校验结果
 */
const clearValidate = () => formRef.value.clearValidate()

/**
 * 获取展示分类
 */
async function getTemShowList() {
  try {
    const { data } = await getAllCategory({ saleMode: props.saleMode })
    temShowList.value = JSON.parse(JSON.stringify(data)) || []
  } catch (error) {
    console.error('获取展示分类失败:', error)
    temShowList.value = []
  }
}

/**
 * 获取供应商
 */
async function getTemSupList() {
  try {
    const { data } = await getAllSupList({})
    temSupList.value = data || []
  } catch (error) {
    console.error('获取供应商失败:', error)
    temSupList.value = []
  }
}

/**
 * 设置供应商名称
 */
function setProviderName() {
  const providerId = formModel.providerId
  const currentTempSup = temSupList.value.find((item) => item.id === providerId)
  formModel.providerName = currentTempSup?.name || ''
}

/**
 * 获取展示分类
 */
function getProductShowCategorys() {
  const vals = formModel.showCategoryIds
  const productShowCategorys: any = []

  temShowList.value.forEach((item) => {
    if (item.showCategoryVos) {
      const productShowCategorySeconds = item.showCategoryVos.filter((child: any) => vals.includes(child.showCategoryId))

      if (productShowCategorySeconds.length) {
        productShowCategorys.push({
          id: item.id,
          name: item.name,
          parentId: item.parentId,
          showCategoryId: item.showCategoryId,
          productShowCategorySeconds: productShowCategorySeconds.map((seconds: any) => ({ ...seconds })),
        })
      }
    }
  })

  return productShowCategorys
}

/**
 * 还原展示分类id
 */
function getShowCategoryIds(showCategorys: any[] = []) {
  let showCategoryId: any[] = []

  showCategorys.forEach((item) => {
    if (item.productShowCategorySeconds) {
      const ids = item.productShowCategorySeconds.map((child: any) => child.showCategoryId)
      showCategoryId = [...showCategoryId, ...ids]
    }
  })

  return showCategoryId
}

/**
 * 获取商品属性
 */
async function getTemAttsList() {
  try {
    const { data } = await getAllAttsList({})
    temAttsList.value = data || []
  } catch (error) {
    console.error('获取商品属性失败:', error)
    temAttsList.value = []
  }
}

/**
 * 选择商品属性
 */
function selectTemAttsList(temAttsId: number) {
  const currenttemAtts = temAttsList.value.find((item) => item.id === temAttsId)

  if (currenttemAtts) {
    formModel.attribute = currenttemAtts.name
    formModel.productAttributes = currenttemAtts.attributeTemplates.map((item: any) => ({
      ...item,
      value: item.content,
    }))
  }
}

/**
 * 添加商品属性
 */
function addTemAtts() {
  formModel.productAttributes = formModel.productAttributes || []
  formModel.productAttributes.push({
    content: '',
    name: '',
    id: '',
    parentId: '',
    value: '',
  })
}

/**
 * 跳转分类/供货商/属性模板
 */
function jumpClass(type: number) {
  setAlbumPics()
  let location: { name?: string; query?: any; params?: any } = {}

  // 设置跳转位置
  const commonSaleModeData = {
    saleMode: props.saleMode || formModel.saleMode,
  }

  switch (type) {
    case 1:
      location = {
        name: 'GoodClass',
        query: {
          options: '1',
          ...commonSaleModeData,
        },
        params: {
          options: '1',
          ...commonSaleModeData,
        },
      }
      break
    case 2:
      location = {
        name: 'GoodClass',
        query: {
          options: '2',
          ...commonSaleModeData,
        },
        params: {
          options: '2',
          ...commonSaleModeData,
        },
      }
      break
    case 3:
      location = {
        name: 'SupplierManage',
        query: { options: 'sup' },
        params: { options: 'sup' },
      }
      break
    case 4:
      location = {
        name: 'AttributeTemple',
        query: { options: 'pick' },
        params: { options: 'sup' },
      }
      break
    case 5:
      location = {
        name: 'LogisticsIndex',
        query: { options: 'log' },
        params: { options: 'sup' },
      }
      break
  }

  router.push({
    ...location,
    query: {
      ...location?.query,
      saveFlag: 1,
    },
  })
}

// 图片上传通用验证
function validateImage(file: any) {
  const whiteList = ['image/jpeg', 'image/jpg', 'image/png']
  const isLt1M = file.size < 1 * 1024 * 1024

  if (!whiteList.includes(file.raw.type)) {
    ElMessage.error('上传文件只能是 JPG或PNG 格式!')
    return false
  }

  if (!isLt1M) {
    ElMessage.error('上传文件大小不能超过 1MB!')
    return false
  }

  return true
}

/**
 * 修改商品主图
 */
async function uploadProductImg(file: any) {
  if (!validateImage(file)) return

  const res = await upLoad({ file: file.raw })
  productImgList.value[productImgIndex.value].img = res.data
  formRef.value.clearValidate('pic')
}

/**
 * 上传主图
 */
async function addUploadProductImg(file: any) {
  if (!validateImage(file)) return

  const res = await upLoad({ file: file.raw })
  productImgList.value.push({ img: res.data })
}

/**
 * 上传大图片
 */
async function addUploadBigProductImg(file: any) {
  if (!validateImage(file)) return

  const imgFile = await readFile(file.raw)
  const newImg = await advanceGetImage(imgFile)

  if (newImg.image.width !== 750 || newImg.image.height !== 350) {
    ElMessage.error('宽屏图片长宽应为750 * 350')
    return
  }

  const res = await upLoad({ file: file.raw })
  widePic.value = res.data
  formModel.widePic = res.data
}

/**
 * 删除图片
 */
function cancelImg(_: any, index: number) {
  productImgList.value.splice(index, 1)
  if (productImgList.value.length === 0) {
    formModel.pic = ''
  }
}

/**
 * 设置商品图片
 */
function setAlbumPics() {
  if (productImgList.value.length > 0) {
    formModel.pic = productImgList.value[0].img
    formModel.albumPics = productImgList.value
      .filter((item) => item.img !== '')
      .map((item) => item.img)
      .join(',')
  }
}

/**
 * 还原商品图片
 */
function getProductImgList(albumPics: any) {
  if (albumPics) {
    return albumPics.split(',').map((item: string) => ({ img: item }))
  }
  return []
}

/**
 * 上传视频
 */
async function uploadProductVideo(file: any) {
  const whiteList = ['video/mp4']
  const isLt = file.size < 5 * 1024 * 1024

  if (!whiteList.includes(file.raw.type)) {
    ElMessage.error('上传视频只能是 mp4 格式!')
    return
  }

  if (!isLt) {
    ElMessage.error('上传视频大小不超过5M!')
    return
  }

  const res = await upLoad({ file: file.raw })
  formModel.videoUrl = res.data
}

/**
 * 删除视频
 */
function deleteVideoUrl() {
  formModel.videoUrl = ''
}

/**
 * 获取物流模板
 */
async function getLogis() {
  try {
    const res = await getLogistics({})
    res.data.unshift({
      id: '0',
      name: '商家包邮',
    })
    logModel.value = res.data
  } catch (error) {
    console.error('获取物流模板失败:', error)
    logModel.value = [
      {
        id: '0',
        name: '商家包邮',
      },
    ]
  }
}

/**
 * 切换配送方式
 */
function getLogModel(e: any) {
  isWeight.value = false
  logisticsId.value = ''

  if (e === 1) {
    formModel.freightTemplateId = '0'
  }
}

/**
 * 选择物流模板
 */
function selectLogModel(e: string) {
  formModel.freightTemplateId = e
  logisticsId.value = e
  isWeight.value = e !== '0'
}

/**
 * 获取模板信息
 */
function getModelOption(modelOption: any) {
  isWeight.value = modelOption.valuationModel !== 1
}

onBeforeRouteLeave((to, _from, next) => {
  // 保存当前表单状态
  if (to.query.saveFlag) {
    storage.set(NEW_GOOD_BASE_INFO_MODEL, formModel)
    Object.keys(props.cacheInfo).forEach((cacheKey) => {
      storage.set(cacheKey, props.cacheInfo[cacheKey])
    })
  } else {
    storage.delete(NEW_GOOD_BASE_INFO_MODEL)
    storage.delete(NEW_GOOD_SELL_INFO_MODEL)
    storage.delete(NEW_GOOD_SHOP_INFO_MODEL)
  }

  next()
})

// 暴露方法给父组件使用
defineExpose({
  getFormModel,
  validate,
  clearValidate,
})
</script>

<template>
  <div class="pageRoll">
    <el-form ref="formRef" :model="formModel" :rules="formRules" label-width="100px">
      <div class="navLine">基本信息</div>
      <div class="baseMsg">
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="formModel.name" style="width: 550px" maxlength="30" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="卖点描述">
          <el-input v-model="formModel.saleDescribe" style="width: 550px" maxlength="60" placeholder="请输入60字以内卖点描述" />
        </el-form-item>
        <el-form-item v-if="showSale" label="销售专区" prop="saleName">
          <el-select v-model="formModel.saleName" style="width: 550px" placeholder="请选择销售专区" @change="selectMode">
            <el-option v-for="item in saleApartList" :key="item.id" :label="item.modeName" :value="item.modeName" />
          </el-select>
        </el-form-item>
        <el-form-item label="展示分类" prop="showCategoryIds">
          <el-select v-model="formModel.showCategoryIds" style="width: 550px" multiple placeholder="请选择分类" :popper-append-to-body="false">
            <el-option-group v-for="group in temShowList" :key="group.showCategoryId" :label="group.name">
              <el-option v-for="item in group.showCategoryVos" :key="item.showCategoryId" :label="item.name" :value="item.showCategoryId" />
            </el-option-group>
          </el-select>
          <el-button link class="ml-20" @click="jumpClass(1)"> 前往设置 </el-button>
        </el-form-item>
        <el-form-item label="供应商">
          <el-select v-model="formModel.providerId" style="width: 550px" placeholder="请选择供应商" :popper-append-to-body="false">
            <el-option v-for="item in temSupList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
          <el-button link class="ml-20" @click="jumpClass(3)"> 前往设置 </el-button>
        </el-form-item>
        <el-form-item label="商品属性">
          <el-select
            v-model="formModel.attributeId"
            placeholder="请选择属性模板"
            style="width: 550px"
            :popper-append-to-body="false"
            @change="selectTemAttsList"
          >
            <el-option v-for="item in temAttsList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
          <el-button link class="ml-20" @click="jumpClass(4)"> 前往设置 </el-button>
          <div class="valueName">
            <div class="flex between">
              <div>属性名称</div>
              <div>属性值</div>
              <div>操作</div>
            </div>
            <div v-if="formModel.productAttributes?.length <= 0" class="valueName__noList">暂无数据~</div>
            <div v-for="(item, index) in formModel.productAttributes" v-else :key="index" class="valueName__List">
              <el-input v-model="item.name" style="width: 230px" maxlength="10" />
              <el-input v-model="item.value" style="width: 230px" maxlength="100" />
              <div style="width: 30px" @click="formModel.productAttributes.splice(index, 1)">删除</div>
            </div>
          </div>
        </el-form-item>
        <el-button type="primary" plain style="margin-left: 100px; margin-bottom: 20px" @click="addTemAtts"> + 添加属性 </el-button>
        <el-form-item label="商品大图">
          <div>
            <div class="product" style="display: flex">
              <el-upload
                class="productImg__uploader product__productImg"
                :auto-upload="false"
                :on-change="addUploadBigProductImg"
                action=""
                :show-file-list="false"
                style="width: 250px; height: 120px"
              >
                <img v-if="widePic" :src="widePic" class="product__productImg" style="width: 250px; height: 120px" />
                <template v-else>
                  <el-icon class="product__productImg--uploader--icon" style="width: 250px; line-height: 100px">
                    <Plus />
                  </el-icon>

                  <div class="product__productImg--uploader--add" style="top: 70px; left: 100px">添加图片</div>
                </template>
              </el-upload>
            </div>
            <div class="text-info fs-12" style="color: rgba(69, 64, 60, 0.6)">尺寸建议750x350（长方形模式）像素以上，大小1M以下</div>
          </div>
        </el-form-item>
        <el-form-item label="商品视频">
          <div>
            <div class="productVideo__uploader">
              <el-upload
                class="productImg__uploader product__productVideo"
                :auto-upload="false"
                :on-change="uploadProductVideo"
                action=""
                :show-file-list="false"
              >
                <video v-if="formModel.videoUrl" controls :src="formModel.videoUrl" style="width: 100%; height: auto" />
                <el-icon class="product__productVideo--uploader--icon">
                  <Plus />
                </el-icon>
              </el-upload>
              <el-upload style="margin-left: 20px" :auto-upload="false" action="" :on-change="uploadProductVideo" :show-file-list="false">
                <el-button link> 替换 </el-button>

                <el-button link class="fc-fe5f4b ml-[20px]" @click.stop="deleteVideoUrl"> 删除 </el-button>
              </el-upload>
            </div>
            <div class="text-info fs-12" style="color: rgba(69, 64, 60, 0.6)">请上传5M以内的视图视频</div>
          </div>
        </el-form-item>
        <el-form-item label="商品主图" prop="pic">
          <div>
            <div class="product" style="display: flex">
              <m-draggable v-model="productImgList" itemKey="img">
                <template #item="{ element, index }">
                  <div style="position: relative; margin-right: 20px" class="product__productImg" draggable="true">
                    <el-upload
                      class="productImg__uploader product__productImg"
                      :auto-upload="false"
                      :on-change="uploadProductImg"
                      action=""
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
                action=""
                :show-file-list="false"
              >
                <el-icon class="product__productImg--uploader--icon">
                  <Plus />
                </el-icon>
                <div class="product__productImg--uploader--add">添加图片</div>
              </el-upload>
            </div>
            <div class="text-info fs-12" style="color: rgba(69, 64, 60, 0.6)">
              尺寸建议750x750（正方形模式）像素以上，大小1M以下，最多6张 (可拖拽图片调整顺序 )
            </div>
          </div>
        </el-form-item>
      </div>
      <div class="navLine">物流设置</div>
      <div class="sendMsg">
        <el-form-item label="配送方式" prop="distributionMode">
          <el-radio v-model="formModel.distributionMode" :value="1" @change="getLogModel"> 快递配送 </el-radio>
        </el-form-item>
        <el-form-item v-if="formModel.distributionMode === 1" label="运费设置" prop="freightTemplateId">
          <div class="w-full">
            <el-select
              v-model="formModel.freightTemplateId"
              placeholder="请选择运费模板"
              :popper-append-to-body="false"
              class="w-[200px]"
              @change="selectLogModel"
            >
              <el-option v-for="item in logModel" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
            <el-button link class="ml-20" @click="jumpClass(5)"> 前往设置 </el-button>
            <LogModel ref="logRef" :template-id="formModel.freightTemplateId" class="w-full" @get-model-option="getModelOption" />
          </div>
        </el-form-item>
      </div>
    </el-form>
  </div>
</template>
