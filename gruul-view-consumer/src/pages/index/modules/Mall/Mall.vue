<template>
  <view
    class="businessSuper--preview"
    :style="{
      height,
    }"
  >
    <!-- 显示内容页面 -->
    <view v-if="hasInfo" class="show__content-info">
      <!-- 搜索 -->
      <SearchPlugin />
      <!-- 内容显示区 -->
      <view class="show__con-box">
        <!-- 顶部模式 -->
        <view v-if="formData.selectMode === 1" class="con__box" style="height: 100%">
          <!-- 头部tab bar -->
          <TopModehead :propData="formData" @changeCategoryId="changeCategoryId" />
          <view class="viewiding__line" />
          <!-- 商品组件 -->
          <TopBigGoods :propData="formData" />
        </view>
        <!-- 侧边栏模式 -->
        <view v-if="formData.selectMode === 2" class="con__box left-con__box">
          <!-- 侧边tab bar -->
          <view class="left__tabbar-box">
            <LeftTabbar :propData="formData" :canClick="canClick" @changeCategoryId="changeCategoryId" />
          </view>
          <scroll-view
            scroll-y="true"
            class="left__biggoods-box"
            :refresher-enabled="true"
            :scroll-anchoring="true"
            :refresher-triggered="triggeredStatus"
            refresher-default-style="black"
            :refresher-threshold="10"
            @refresherpulling="refresherpullingPulling"
            @touchstart="scrollStart"
            @touchend="scrollEnd"
            @scroll="scrollBindScroll"
            @refresherrefresh="refresherrefreshPull"
            @refresherrestore="refresherrestoreEnd"
          >
            <!-- 底部上拉加载下一个分类页 -->
            <view v-if="upperNextText !== ''" class="left__biggoods-upper">
              <image
                src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20201125/05433c5d8fb7483f91b49576ce7ee618.png"
                mode="aspectFit"
                lazy-load="false"
              />
              <text>下拉浏览{{ upperNextText }}</text>
            </view>
            <view id="leftBiggoods" class="right__box" :style="getRightBoxStyle">
              <!-- 海报 -->
              <SwiperList :propData="formData" />
              <!-- 商品组件 -->
              <LeftBigGoods v-if="formData.selectType === 1" :propData="formData" @getNoList="getNoList" />
              <!-- 分类导航 -->
              <ClassifiedGoods v-if="formData.selectType === 2" :propData="formData" />
            </view>
            <!-- 底部上拉加载下一个分类页 -->
            <view v-show="isShowLoadNext" id="moreTip" class="left__biggoods-more">
              <image
                src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20201117/26690c0bd517470d9dad39938555d93b.png"
                mode="aspectFit"
                lazy-load="false"
              />
              <text>上拉浏览{{ nextCategoryName }}</text>
            </view>
          </scroll-view>
        </view>
      </view>
    </view>
    <!-- 空白页面 -->
    <view v-if="!hasInfo" class="shopNoCar">
      <image src="https://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/api/noCar.png" class="shop__noCar" />
      <view class="shop__noCar--text">暂无商品~</view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import SearchPlugin from './components/SearchPlugin/SearchPlugin.vue'
import TopModehead from './components/TopModehead/TopModehead.vue'
import TopBigGoods from './components/TopBigGoods/TopBigGoods.vue'
import LeftTabbar from './components/LeftTabbar/LeftTabbar.vue'
import SwiperList from './components/SwiperList/SwiperList.vue'
import LeftBigGoods from './components/LeftBigGoods/LeftBigGoods.vue'
import ClassifiedGoods from './components/ClassifiedGoods/ClassifiedGoods.vue'
import { getEffectiveCategory } from '@/api/modules/goods'
import { getAssembly } from '@/api/modules/decoration'
import { useSettingStore } from '@/store/modules/setting'
import useScreenHeight from '@/composables/hooks/useScreenHeight'
import { storeToRefs } from 'pinia'
import type { Category, CategoryVo, FirstCategory, FormData, Navigation } from './types'

const screenHeight = useScreenHeight()
const { navigationBarHeight } = storeToRefs(useSettingStore())
const height = computed(() => {
  return `${screenHeight.value - navigationBarHeight.value - uni.upx2px(100)}px`
})

// 响应式变量
const isFirst = ref(true)
const formData = ref<FormData>({} as FormData)
const getRightBoxStyle = ref('')
const currentFirstCategory = ref<Obj>({})
const hasInfo = ref(false)
const isShowLoadNext = ref(false)
const upperNextText = ref('')
const nextCategoryName = ref('')
const isComeBottom = ref(true)
const scrollTopValue = ref(0)
const triggeredStatus = ref(false)
const freshing = ref(false)
const canClick = ref(false)
const safeHight = ref(0)

// 方法定义
const getHeight = () => {
  const sysinfo = uni.getSystemInfoSync()
  const statusBarHeight = sysinfo.statusBarHeight || 0
  const isIOS = sysinfo.system ? sysinfo.system.indexOf('iOS') > -1 : false
  const navHeight = isIOS ? 44 : 48

  safeHight.value = statusBarHeight + navHeight + (sysinfo.safeArea ? sysinfo.screenHeight - sysinfo.safeArea.bottom : 0) - 5
}

const getPageAssembly = async () => {
  let formDataValue = ''
  const btmList = useSettingStore().tabBar.list
  const templateId = useSettingStore().templateId
  const modelItem = btmList.find((item) => item.name === 'mall')
  if (!modelItem) {
    return
  }
  const pageId = useSettingStore().pageList.find((item) => item.pageName === '商超分类页')?.id ?? ''
  useSettingStore().setSaleMode(modelItem.id)
  useSettingStore().getPageType(modelItem.id)
  console.log('modelItem', modelItem)
  const param = {
    templateId,
    modelId: modelItem.id,
    pageId,
  }
  console.log('pageList', useSettingStore().pageList)

  uni.setNavigationBarTitle({
    title: `${modelItem.linkName}`,
  })

  const res = await getAssembly(param)
  if (res.length > 0) {
    const properties = JSON.parse(res[0].properties)
    formDataValue = properties.formData
    dealManage(formDataValue, modelItem.id)
  }
}

const dealManage = (formDataValue: any, id: Long) => {
  filterCategory(formDataValue, id).then(() => {
    if (formDataValue.selectMode === 2 && isFirst.value && formDataValue.firstCatList.length) {
      formDataValue.currentFirstCategory = formDataValue.firstCatList[0]
    }
    formData.value = formDataValue
    // formData: data.formData,
    isFirst.value = false
    console.log('formDataValue', formDataValue)
    hasInfo.value = Boolean(formDataValue.firstCatList.length)
    console.log('hasInfo', hasInfo.value)
    getRightBoxStyleHandle()
  })
}

const getNoList = (e: boolean) => {
  triggeredStatus.value = false
  freshing.value = false
  hasInfo.value = Boolean(e)
}

const filterCategory = (formDataValue: any, id: Long) => {
  return new Promise<string>((resolve, reject) => {
    getEffectiveCategory(id)
      .then((res) => {
        const list = res

        const { firstCatList } = formDataValue
        console.log('firstCatList', firstCatList)
        console.log('list', list)
        const value: FirstCategory[] = []

        firstCatList.forEach((i: FirstCategory) => {
          const category = list.find((k: Category) => k.id === i.category.id)
          if (category) {
            i.category = category
            value.push(i)
          }
        })

        formDataValue.firstCatList = value
        fitlerClassSed(formDataValue)
        resolve('输出')
      })
      .catch((err) => {
        reject(err)
      })
  })
}

const fitlerClassSed = (formDataValue: FormData) => {
  const { firstCatList, selectMode, selectType } = formDataValue
  const value: FirstCategory[] = []

  firstCatList.forEach((i: FirstCategory) => {
    const { showCategoryVos = [] } = i.category as Category
    const { navigation, swiperList } = i
    console.log('i', i)
    if (!navigation) {
      return
    }
    navigation.forEach((k: Navigation, f: number) => {
      if (!k.class.length) {
        navigation.splice(f, 1)
      } else {
        k.class.forEach((j: Long, m: number) => {
          const idx = showCategoryVos.findIndex((t: CategoryVo) => t.id === j)
          if (idx !== -1) {
            k.chartList[m].category = showCategoryVos.find((t: CategoryVo) => t.id === j)
          } else {
            k.class.splice(m, 1)
            k.chartList.splice(m, 1)
          }
        })
      }
    })

    if (!(selectType === 2 && selectMode === 2 && !swiperList.length && !navigation.length)) {
      value.push(i)
    }
  })

  formDataValue.firstCatList = [...value]
}

const getRightBoxStyleHandle = () => {
  const getCategoryVo = getCategoryVoFunc(formData.value)
  const style = `width: calc(100% - ${formData.value.pagePadding ? formData.value.pagePadding * 2 : 0}px); margin-left: ${formData.value.pagePadding || 0}px;`
  getRightBoxStyle.value = `${style} padding-bottom: ${getCategoryVo.hasNextCatgory ? '100rpx' : '0rpx'}`
  isShowLoadNext.value = getCategoryVo.hasNextCatgory
  nextCategoryName.value = getCategoryVo.name || ''
}

const changeCategoryId = (e: Long) => {
  const id = e
  let formDataCopy = { ...formData.value }
  const currentFirstCategoryValue = formDataCopy.firstCatList.find((i: FirstCategory) => i.category.id === id) || ({} as FirstCategory)
  formDataCopy.currentFirstCategory = currentFirstCategoryValue
  currentFirstCategory.value = currentFirstCategoryValue
  formData.value = { ...formDataCopy }
  refresherpullingPulling()
  getRightBoxStyleHandle()
}

const getCategoryVoFunc = (formDataValue: FormData) => {
  const currentFirstCategory = formDataValue.currentFirstCategory || ({} as any)
  const category = currentFirstCategory.category || ({} as any)
  const firstCatList = formDataValue.firstCatList || []
  const id = category.id || ''
  const index = firstCatList.findIndex((i: FirstCategory) => i.category.id === id)
  const hasNextCatgory = index !== -1 && index !== firstCatList.length - 1
  const hasUpperCatgory = index !== -1 && index !== 0
  const nextCategory = hasNextCatgory ? firstCatList[index + 1].category || ({} as any) : ({} as any)
  const upperCatgory = hasUpperCatgory ? firstCatList[index - 1].category || ({} as any) : ({} as any)

  return {
    ...nextCategory,
    hasNextCatgory,
    hasUpperCatgory,
    upperCatgory,
    name: nextCategory.name || '',
    id: nextCategory.id || '',
  }
}

const scrollStart = (e: { changedTouches: { clientY: number }[] }) => {
  scrollTopValue.value = e.changedTouches[0].clientY
  isComeBottom.value = false
}

const scrollEnd = (e: { changedTouches: { clientY: number }[] }) => {
  const scrollTopValueDiff = e.changedTouches[0].clientY - scrollTopValue.value
  if (!isComeBottom.value && scrollTopValueDiff < 0) {
    const categoryVo = getCategoryVoFunc(formData.value)
    if (categoryVo.hasNextCatgory) {
      changeCategoryId(categoryVo.id)
    }
  }
}

const scrollBindScroll = () => {
  isComeBottom.value = true
}

const refresherrefreshPull = () => {
  if (freshing.value) return
  freshing.value = true
  if (!triggeredStatus.value) {
    triggeredStatus.value = true
  }
  const getCategoryVo = getCategoryVoFunc(formData.value)
  if (getCategoryVo.hasUpperCatgory) {
    changeCategoryId(getCategoryVo.upperCatgory.id)
  } else {
    upperNextText.value = ''
    setTimeout(() => {
      triggeredStatus.value = false
      freshing.value = false
    }, 0)
  }
}

const refresherrestoreEnd = () => {
  upperNextText.value = ''
}

const refresherpullingPulling = () => {
  const getCategoryVo = getCategoryVoFunc(formData.value)
  if (getCategoryVo.hasUpperCatgory) {
    upperNextText.value = getCategoryVo.upperCatgory.name || ''
  } else {
    upperNextText.value = ''
  }
  if (getCategoryVo.hasNextCatgory) {
    nextCategoryName.value = getCategoryVo.name || ''
  } else {
    nextCategoryName.value = ''
  }
}

// 初始化
isFirst.value = true
getPageAssembly()
getHeight()
</script>

<style lang="scss" scoped>
.businessSuper--preview {
  background-color: #f2f2f2;

  .show__content-info {
    height: 100%;
    box-sizing: border-box;
    overflow-y: scroll;
  }

  .search__box {
    height: 120rpx;
    box-sizing: border-box;
    padding: 24rpx 40rpx;

    .search-icon {
      height: 70rpx;
      background-color: rgba(242, 242, 242, 1);
      border-radius: 12rpx;

      i {
        float: right;
        margin-top: 20rpx;
        margin-right: 20rpx;
      }
    }
  }

  .show__con-box {
    height: calc(100% - 120rpx);
    box-sizing: border-box;
  }

  .dividing__line {
    width: 100%;
    height: 10rpx;
    background-color: rgba(242, 242, 242, 1);
  }

  .con__box {
    min-height: 100%;
    overflow-x: hidden;
    overflow-y: scroll;
  }

  .left__tabbar-box {
    width: 160rpx;
    height: 100%;
    overflow-x: hidden;
    overflow-y: scroll;
  }

  .left-con__box {
    display: flex;
    height: 100%;
    overflow-x: hidden;
    overflow-y: scroll;
    box-sizing: border-box;
  }

  .left__biggoods-box {
    flex: 1;
    height: 100%;
    overflow-y: scroll;
    overflow-x: hidden;
    background-color: #ffffff;
  }

  .shopNoCar {
    display: flex;
    justify-content: center;
    flex-wrap: wrap;
    width: 100%;
    background-color: #ffffff;
    height: 100%;
  }

  .shop__noCar {
    margin-top: 140rpx;
    width: 69%;
  }

  .shop__noCar--text {
    display: flex;
    justify-content: center;
    color: #acacac;
    width: 100%;
    padding-bottom: 440rpx;
  }

  .left__biggoods-box {
    .right__box {
      box-sizing: border-box;
      min-height: 100%;
      padding-bottom: 100rpx;
    }

    .left__biggoods-more {
      height: 100rpx;
      font-size: 0rpx;
      color: #666;
      text-align: center;
      line-height: 100rpx;
      margin-top: -100rpx;
      width: 100%;
      white-space: nowrap;
      text-overflow: ellipsis;
      overflow: hidden;
      box-sizing: border-box;

      image {
        display: inline-block;
        width: 40rpx;
        height: 36rpx;
        vertical-align: middle;
        margin: 4rpx;
      }

      text {
        display: inline-block;
        font-size: 26rpx;
        vertical-align: middle;
        max-width: calc(100% - 80rpx);
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }

    .left__biggoods-upper {
      height: 100rpx;
      font-size: 0rpx;
      color: #666;
      text-align: center;
      line-height: 100rpx;
      width: 100%;
      white-space: nowrap;
      text-overflow: ellipsis;
      overflow: hidden;
      box-sizing: border-box;

      image {
        display: inline-block;
        width: 40rpx;
        height: 36rpx;
        vertical-align: middle;
        margin: 4rpx;
      }

      text {
        display: inline-block;
        font-size: 26rpx;
        vertical-align: middle;
        max-width: calc(100% - 80rpx);
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
  }
}
</style>
