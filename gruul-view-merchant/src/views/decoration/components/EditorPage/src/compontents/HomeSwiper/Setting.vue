<script setup lang="ts">
import HomeSwiperFormData, { type SwiperListItem } from './HomeSwiper'

import { upLoad } from '@/api/index'
import LinkSelect from '@/components/LinkSelect'
import { Plus, Remove } from '@element-plus/icons-vue'
import { useVModel } from '@vueuse/core'

import { getDefualtSwiperItem } from './HomeSwiper'

const props = defineProps({
  formData: {
    type: Object as () => typeof HomeSwiperFormData,
    required: true,
  },
})

const emit = defineEmits(['update:formData'])
const formModel = useVModel(props, 'formData', emit)

/** 当前选中的轮播项 */
const currentSwiperItem = ref<SwiperListItem>({} as SwiperListItem)

/** 拖动的组件的索引 */
const dragStartIndex = ref(-1)

/**
 * 选择图片上传
 */
async function handleChange(file: any) {
  const img = await uploadFile(file)
  if (img) {
    const swiperListItem = getDefualtSwiperItem({ img })
    formModel.value.swiperList.push(swiperListItem)
  }
}

/**
 * 替换轮播图片
 */
async function handleItemChange(file: any) {
  const img = await uploadFile(file)
  if (img) {
    currentSwiperItem.value.img = img
  }
}

/**
 * 点击获取需要编辑的轮播项
 */
function handleSwiperItemClick(item: SwiperListItem) {
  currentSwiperItem.value = item
}

/**
 * 上传文件
 */
async function uploadFile(file: any) {
  try {
    const whiteList = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif']
    const isLt2M = file.size < 2 * 1024 * 1024

    if (!whiteList.includes(file.raw.type)) {
      ElMessage.error('上传文件只能是 GIF、JPG或PNG 格式!')
      return null
    }

    if (!isLt2M) {
      ElMessage.error('上传文件大小不能超过 2MB!')
      return null
    }

    const res = await upLoad({
      file: file.raw,
    })
    return res.data
  } catch (error) {
    console.error(error)
    return null
  }
}

/**
 * 删除轮播组件中的图片
 */
function deleteSwiperItem(index: number) {
  formModel.value.swiperList.splice(index, 1)
}

/**
 * 开始拖动，记录拖动的组件索引
 */
function handleDragstart(index: number) {
  dragStartIndex.value = index
}

/**
 * 阻止默认行为，否则drop事件不会触发
 */
function handleDragover(e: DragEvent) {
  e.preventDefault()
}

/**
 * 被放置的容器触发事件，交换两个组件的位置
 */
function handleDrop(index: number) {
  if (dragStartIndex.value === index) {
    return
  }

  const temp = formModel.value.swiperList.splice(dragStartIndex.value, 1, formModel.value.swiperList[index])
  formModel.value.swiperList.splice(index, 1, ...temp)
}
</script>

<template>
  <!-- 轮播图 -->
  <div class="homeSwiperForm">
    <el-form label-position="left" label-width="70px">
      <el-form-item label="添加图片">
        <span style="color: #999">最多添加5个广告，鼠标拖拽调整广告顺序</span>
      </el-form-item>
    </el-form>
    <div
      v-for="(item, index) in formModel.swiperList"
      :key="index"
      class="homeSwiperForm-item"
      draggable
      @click.capture="handleSwiperItemClick(item)"
      @dragstart="handleDragstart(index)"
      @dragover="handleDragover"
      @drop="handleDrop(index)"
    >
      <el-icon class="remove__swiper--item" @click="deleteSwiperItem(index)">
        <Remove />
      </el-icon>
      <div class="homeSwiperForm-item__right">
        <el-form :model="item" label-width="70px">
          <el-form-item label="图片标题">
            <el-input v-model="item.title" placeholder="图片标题" />
          </el-form-item>
          <el-form-item label="图片上传">
            <el-upload class="homeSwiperForm-item__uploader" action="" :show-file-list="false" :auto-upload="false" :on-change="handleItemChange">
              <img v-if="item.img" :src="item.img" class="homeSwiperForm-item__img" />
              <el-icon v-else class="homeSwiperForm-item__plus">
                <Plus />
              </el-icon>
            </el-upload>
          </el-form-item>
          <LinkSelect v-model:link="item.link" />
          <el-form-item v-if="item.link && item.link.type !== 6">
            <span style="color: #9797a1">{{ item.link.name }}</span>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <el-upload
      v-if="formModel.swiperList && formModel.swiperList.length < 5"
      class="homeSwiperForm-add"
      action=""
      multiple
      :auto-upload="false"
      :show-file-list="false"
      :on-change="handleChange"
    >
      <span>+ 添加背景图</span>
      <p style="margin-top: 5px">建议宽度750像素</p>
    </el-upload>

    <div class="homeSwiperForm-form">
      <el-form label-position="left" :model="formModel" label-width="70px">
        <el-form-item label="边距">
          <el-slider v-model="formModel.padding" :show-tooltip="false" :show-input="true" :max="30" />
        </el-form-item>
        <el-form-item label="高度">
          <el-slider v-model="formModel.height" :show-tooltip="false" :show-input="true" :max="360" :min="140" />
        </el-form-item>
        <el-form-item label="图片样式">
          <el-radio-group v-model="formModel.imageStyle">
            <el-radio :value="1"> 常规 </el-radio>
            <el-radio :value="2"> 投影 </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="图片倒角">
          <el-radio-group v-model="formModel.imageAngle">
            <el-radio :value="1"> 直角 </el-radio>
            <el-radio :value="2"> 圆角 </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="指示器">
          <el-radio-group v-model="formModel.indicator">
            <el-radio :value="1"> 样式一 </el-radio>
            <el-radio :value="2"> 样式二 </el-radio>
            <el-radio :value="3"> 样式三 </el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<style lang="scss">
.homeSwiperForm-item {
  position: relative;
  display: flex;
  border: 1px solid #e4e4e4;
  padding: 10px;
  margin-bottom: 10px;
  &__right {
    flex: 1;
    padding-left: 10px;
  }
  &__uploader {
    width: 80px;
    height: 80px;
    display: block;
    .el-upload {
      border: 1px dashed #d9d9d9;
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
    }
    .el-upload:hover {
      border-color: #409eff;
    }
  }

  &__img {
    width: 80px;
    height: 80px;
    display: block;
  }

  &__plus {
    font-size: 28px;
    color: #8c939d;
    width: 80px;
    height: 80px;
    line-height: 80px;
    text-align: center;
  }

  .remove__swiper--item {
    display: none;
  }

  &:hover {
    .remove__swiper--item {
      display: block;
      position: absolute;
      font-size: 20px;
      right: -8px;
      top: -8px;
      width: 20px;
      height: 20px;
      z-index: 20;
      color: #e4e4e4;
      background-color: #fff;
      cursor: pointer;
    }
  }
}

.homeSwiperForm-add {
  width: 100%;
  height: 100px;
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

.homeSwiperForm-form {
  border: 1px solid #e4e4e4;
  padding: 10px;
}
</style>
