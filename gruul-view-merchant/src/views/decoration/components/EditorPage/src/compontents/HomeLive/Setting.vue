<script setup lang="ts">
import HomeLive from './HomeLive'
import { useVModel } from '@vueuse/core'

/** 属性定义 */
const props = defineProps({
  formData: {
    type: Object as () => typeof HomeLive,
    required: true,
  },
})
const emit = defineEmits(['update:formData'])
const formData = useVModel(props, 'formData', emit)
</script>

<template>
  <!-- 直播 -->
  <el-form class="spellpage" :model="formData" label-width="100px">
    <el-form-item label="列表标题">
      <el-input v-model="formData.title" link placeholder="请输入列表标题" show-word-limit maxlength="14" />
    </el-form-item>
    <el-form-item label="展示直播数量">
      <el-input-number v-model="formData.count" :min="0" step-strictly :step="1" :controls="false" class="w-[80px]" />

      <span class="ml-[8px]">根据直播列表置顶排序展示</span>
    </el-form-item>
    <el-form-item label="列表样式">
      <el-radio-group v-model="formData.listStyle">
        <el-radio :value="1"> 大图模式 </el-radio>
        <el-radio :value="2"> 一行两个 </el-radio>
        <el-radio :value="3"> 横向滑动 </el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item label="页面边距">
      <el-slider v-model="formData.pagePadding" :show-tooltip="false" :show-input="true" :max="30" />
    </el-form-item>
    <el-form-item label="上下间距">
      <el-slider v-model="formData.goodsPadding" :show-tooltip="false" :show-input="true" :max="30" />
    </el-form-item>
    <el-form-item label="图片倒角">
      <el-radio-group v-model="formData.doodsAngle">
        <el-radio :value="2"> 直角 </el-radio>
        <el-radio :value="1"> 圆角 </el-radio>
      </el-radio-group>
    </el-form-item>
  </el-form>
</template>

<style lang="scss">
@import '@/assets/styles/decoration/spellGroup';
</style>
