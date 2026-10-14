<script setup lang="ts">
import BusinessSuper from './BusinessSuper'
import { Search } from '@element-plus/icons-vue'
import { useVModel } from '@vueuse/core'

import ClassificationList from './components/ClassificationList.vue'
import LeftBigGoods from './components/LeftBigGoods.vue'
import LeftTabbar from './components/LeftTabbar.vue'
import SearchPage from './components/SearchPage.vue'
import SecondCatGoods from './components/SecondCatGoods.vue'
import SwiperList from './components/SwiperList.vue'
import TopBigGoods from './components/TopBigGoods.vue'
import TopModeHead from './components/TopModeHead.vue'

// 定义props
const props = defineProps<{
  formData: typeof BusinessSuper
}>()
const emit = defineEmits(['update:formData'])
const formData = useVModel(props, 'formData', emit)

const backStr = ref('<')

/**
 * 动态设置页面样式
 */
const getPageStyle = computed(() => {
  return { padding: `0px ${formData.value.pagePadding}px` }
})

/**
 * 获取当前显示的展示分类，并判断是否还有下一个
 */
const getCategoryVo = computed(() => {
  const { category = {} as any } = formData.value.currentFirstCategory
  const firstCatList: any[] = formData.value.firstCatList || []
  const index = firstCatList.findIndex((i) => i.category.id === category.id)
  const hasNextCatgory = index !== -1 && index !== firstCatList.length - 1
  const hasUpperCatgory = index !== -1 && index !== 0
  const nextCategory: any = hasNextCatgory ? firstCatList[index + 1].category : {}
  const upperCatgory = hasUpperCatgory ? firstCatList[index - 1].category : {}

  // 确保包含name属性，修复类型错误
  return {
    hasNextCatgory,
    hasUpperCatgory,
    upperCatgory,
    name: nextCategory.name || '',
    ...nextCategory,
  }
})

/**
 * 搜索页面返回
 */
function gotoBack() {
  formData.value.isSearch = 1
}

/**
 * 修改搜索状态
 */
function changeSearch() {
  // formData.value.isSearch = 2;
}
</script>

<template>
  <div class="editor__preview_new spellPage--preview" style="border: 1px solid #dcdfe6 !important">
    <div class="header__box">
      <span v-if="[2, 3].includes(formData.isSearch)" class="back" @click="gotoBack">{{ backStr }}</span>
      分类
      <span class="tis">
        <i />
        <i />
        <i />
      </span>
    </div>

    <!-- 显示搜索状态 -->
    <div v-if="formData.isSearch === 2">
      <SearchPage :form-data="formData" />
    </div>

    <!-- 显示内容页面 -->
    <div v-if="formData.isSearch === 1" class="show__content-info">
      <!-- 搜索 -->
      <div class="search__box" @click="changeSearch">
        <div class="search-icon">
          <el-icon><Search /></el-icon>
        </div>
      </div>
      <!-- 内容显示区 -->
      <div class="show__con-box">
        <!-- 顶部模式 -->
        <div v-if="formData.selectMode === 1" class="con__box" style="height: 100%">
          <TopModeHead :form-data="formData" />
          <div class="dividing__line" />
          <TopBigGoods :form-data="formData" />
        </div>
        <!-- 侧边栏模式 -->
        <div v-if="formData.selectMode === 2" class="con__box left-con__box">
          <div class="left__tabbar-box">
            <LeftTabbar v-model:form-data="formData" />
          </div>
          <div class="left__biggoods-box" :style="getPageStyle">
            <div
              class="right__box"
              :style="{
                paddingBottom: !getCategoryVo.hasNextCatgory ? '0px' : '50px',
              }"
            >
              <SwiperList v-if="formData.currentFirstCategory.swiperList && formData.currentFirstCategory.swiperList.length" :form-data="formData" />
              <LeftBigGoods v-if="formData.selectType === 1" :form-data="formData" />
              <ClassificationList v-if="formData.selectType === 2" :form-data="formData" />
            </div>
            <div v-if="getCategoryVo.hasNextCatgory && formData.selectType === 1" class="left__biggoods-more">
              <img src="https://oss-tencent.bgniao.cn//gruul/20201117/26690c0bd517470d9dad39938555d93b.png" alt="" />
              <span>上拉浏览{{ getCategoryVo.name }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="formData.isSearch === 3" class="show__content-info">
      <SecondCatGoods :form-data="formData" />
    </div>
  </div>
</template>

<style lang="scss">
@import '@/assets/styles/decoration/businessSuper';
</style>
