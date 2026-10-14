<script setup lang="ts">
import type { SwiperListItem } from './GoodSwiper'
import { upLoad } from '@/api/index'
import LinkSelect from '@/components/LinkSelect'
import { getDefualtSwiperItem } from '@/views/decoration/components/EditorPage/src/compontents/HomeSwiper/HomeSwiper'

import { Plus, Remove } from '@element-plus/icons-vue'
import { useVModel } from '@vueuse/core'

/** 轮播图 */

// 定义props和emit
const props = defineProps({
  formData: {
    type: Object,
    default: () => ({}),
  },
})

const emit = defineEmits(['update:formData'])
const formModel = useVModel(props, 'formData', emit)

// 用于保存当前选择的轮播项
const currentSwiperItem = ref<SwiperListItem>({} as SwiperListItem)

// 拖动的组件的下角标
const dragStarIndex = ref(-1)

/**
 * 选择图片上传
 */
async function handleChange(file: any) {
  const img = await upLoadFile(file)
  if (img) {
    const swiperListItem: SwiperListItem = getDefualtSwiperItem({ img })
    formModel.value.swiperList.push(swiperListItem)
  }
}

/**
 * 替换轮播图片
 */
async function handleItemChange(file: any) {
  const img = await upLoadFile(file)
  if (img) {
    currentSwiperItem.value.img = img
  }
}

async function handlebtnImgChange(file: any) {
  formModel.value.btnImg = await upLoadFile(file)
  emit('update:formData', formModel.value)
}

/**
 * 点击获取需要编辑的轮播项
 */
function swiperItemClick(item: SwiperListItem) {
  currentSwiperItem.value = item
}

async function upLoadFile(file: any) {
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
function deleteSwiperItem(idx: number) {
  formModel.value.swiperList.splice(idx, 1)
  emit('update:formData', formModel.value)
}

/**
 * 开始拖动，记录拖动的组件下角标
 */
function handleDragstart(i: number) {
  dragStarIndex.value = i
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
function handleDrop(i: number) {
  if (dragStarIndex.value === i) {
    return false
  }

  const temp = formModel.value.swiperList.splice(dragStarIndex.value, 1, formModel.value.swiperList[i])
  formModel.value.swiperList.splice(i, 1, ...temp)
}
</script>

<template>
  <!-- 轮播图 -->
  <div class="GoodSwiperForm">
    <el-form label-position="left">
      <el-form-item label="">
        <el-radio-group v-model="formModel.radio">
          <el-radio :value="1"> 基础样式 </el-radio>
          <el-radio :value="2"> 叠层样式 </el-radio>
          <el-radio :value="3"> 突出样式 </el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>

    <el-form v-if="formModel.radio === 2" class="GoodSwiperForm-item" style="flex-wrap: wrap" label-width="70px">
      <el-form-item label="底图标题">
        <el-input v-model="formModel.btmInput" style="width: 220px" />
      </el-form-item>
      <el-form-item label="图片上传">
        <el-upload class="GoodSwiperForm-item__uploader" :show-file-list="false" :auto-upload="false" :on-change="handlebtnImgChange">
          <img v-if="formModel.btnImg" :src="formModel.btnImg" class="GoodSwiperForm-item__img" />
          <el-icon v-else class="GoodSwiperForm-item__plus">
            <Plus />
          </el-icon>
        </el-upload>
      </el-form-item>
      <!-- <el-form-item label="导航链接">
        <LinkSelect :link="formModel.btnlink" />
      </el-form-item> -->
    </el-form>

    <el-form label-position="left" label-width="70px">
      <el-form-item label="添加图片">
        <span style="color: #999">最多添加5个广告，鼠标拖拽调整广告顺序</span>
      </el-form-item>
    </el-form>
    <div
      v-for="(item, index) in formModel.swiperList"
      :key="index"
      class="GoodSwiperForm-item"
      :draggable="true"
      @click.capture="swiperItemClick(item)"
      @dragstart="handleDragstart(index)"
      @dragover="handleDragover"
      @drop="handleDrop(index)"
    >
      <el-icon class="remove__swiper--item" @click="deleteSwiperItem(index)">
        <Remove />
      </el-icon>
      <div class="GoodSwiperForm-item__right">
        <el-form :model="item" label-width="70px">
          <el-form-item label="图片标题">
            <el-input v-model="item.title" placeholder="图片标题" />
          </el-form-item>
          <el-form-item label="图片上传">
            <el-upload class="GoodSwiperForm-item__uploader" :show-file-list="false" :auto-upload="false" :on-change="handleItemChange">
              <img v-if="item.img" :src="item.img" class="GoodSwiperForm-item__img" />
              <el-icon v-else class="GoodSwiperForm-item__plus">
                <Plus />
              </el-icon>
            </el-upload>
          </el-form-item>
          <el-form-item label="导航链接">
            <LinkSelect v-model:link="item.link" />
          </el-form-item>
          <el-form-item v-if="item.link.type !== 6">
            <span style="color: #9797a1">{{ item.link.name }}</span>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <el-upload v-if="formModel.swiperList" class="GoodSwiperForm-add" multiple :auto-upload="false" :show-file-list="false" :on-change="handleChange">
      <span>+ 添加背景图</span>
      <p style="margin-top: 5px">建议宽度{{ formModel.radio === 3 ? 350 : 750 }}像素</p>
    </el-upload>

    <div class="GoodSwiperForm-form">
      <el-form label-position="left" :model="formModel" label-width="70px">
        <el-form-item v-if="formModel.radio === 1" label="边距">
          <el-slider v-model="formModel.padding" :show-tooltip="false" :show-input="true" :max="30" />
        </el-form-item>
        <el-form-item v-if="formModel.radio === 2" label="侧边距">
          <el-slider v-model="formModel.sidePadding" :show-tooltip="false" :show-input="true" :max="50" />
        </el-form-item>
        <el-form-item label="高度">
          <el-slider v-model="formModel.height" :show-tooltip="false" :show-input="true" :max="1000" :min="140" />
        </el-form-item>
        <el-form-item v-if="formModel.radio === 2" label="轮播间距">
          <el-slider v-model="formModel.margin" :show-tooltip="false" :show-input="true" :max="750" :min="0" />
        </el-form-item>
        <el-form-item v-if="formModel.radio === 3 || formModel.radio === 1" label="轮播时间">
          <el-slider v-model="formModel.interval" :show-tooltip="false" :show-input="true" :max="10" :min="1" />
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
        <el-form-item v-if="formModel.radio === 1" label="指示器">
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
.GoodSwiperForm-item {
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

.GoodSwiperForm-add {
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

.GoodSwiperForm-form {
  border: 1px solid #e4e4e4;
  padding: 10px;
}
</style>
