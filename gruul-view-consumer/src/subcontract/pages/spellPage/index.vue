<template>
  <view class="spellPage--preview">
    <!-- 显示内容页面 -->
    <view v-if="hasInfo" class="show__content-info">
      <!-- 搜索 -->
      <search-plugin :chanel="12" />
      <!-- 内容显示区 -->
      <view class="show__con-box">
        <!-- 顶部模式 -->
        <view v-if="formData.selectMode === 1" class="con__box" style="height: 100%">
          <!-- 头部tab bar -->
          <top-modehead :propData="formData" @changeCategoryId="changeCategoryId" />
          <view class="viewiding__line" />
          <!-- 商品组件 -->
          <top-biggoods :propData="formData" />
        </view>
        <!-- 侧边栏模式 -->
        <view v-if="formData.selectMode === 2" class="con__box left-con__box">
          <!-- 侧边tab bar -->
          <view class="left__tabbar-box">
            <left-tabbar :propData="formData" @changeCategoryId="changeCategoryId" />
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
            <view v-if="upperNextText" class="left__biggoods-upper">
              <image
                src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20201125/05433c5d8fb7483f91b49576ce7ee618.png"
                mode="aspectFit"
                lazy-load="false"
              />
              <text>下拉浏览{{ upperNextText }}</text>
            </view>
            <view class="right__box" :style="getRightBoxStyle">
              <!-- 海报 -->
              <swiper-list :propData="formData" />
              <!-- 商品组件 -->
              <left-biggoods v-if="formData.selectType === 1" :propData="formData" @getNoList="getNoList" />
              <!-- 分类导航 -->
              <classified-goods v-if="formData.selectType === 2" :propData="formData" />
            </view>
            <!-- 底部上拉加载下一个分类页 -->
            <view v-show="isShowLoadNext" class="left__biggoods-more">
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
import { ref, nextTick } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getAssembly } from '@/api/modules/decoration'
import { getEffectiveCategory } from '@/api/modules/goods'
import searchPlugin from './components/search-plugin/search-plugin.vue'
import topModehead from './components/top-modehead/top-modehead.vue'
import topBiggoods from './components/top-biggoods/top-biggoods.vue'
import leftTabbar from './components/left-tabbar/left-tabbar.vue'
import swiperList from './components/swiper-list/swiper-list.vue'
import leftBiggoods from './components/left-biggoods/left-biggoods.vue'
import classifiedGoods from './components/classified-goods/classified-goods.vue'
import { useSettingStore } from '@/store/modules/setting'

// 响应式数据
const isFirst = ref(true)
const formData = ref<Obj>({})
const getRightBoxStyle = ref('')
const currentFirstCategory = ref<Obj>({})
const hasInfo = ref(false)
const isShowLoadNext = ref(false)
const upperNextText = ref('')
const nextCategoryName = ref('')
const isComeBottom = ref(false)
const scrollTopValue = ref(0)
const saleMode = ref('')
const triggeredStatus = ref(false)
const freshing = ref(false)
const safeHight = ref(0)

// 获取屏幕高度信息
const getHeight = () => {
  const sysinfo = uni.getSystemInfoSync()
  const statusHeight = sysinfo.statusBarHeight || 0
  const isiOS = sysinfo.system.indexOf('iOS') > -1
  const navHeight = !isiOS ? 48 : 44
  const safeHeight = sysinfo.screenHeight - (sysinfo.safeArea?.bottom || 0)
  safeHight.value = statusHeight + navHeight + safeHeight - 5
}

// 获取页面组件数据
const getPageAssembly = async () => {
  // const data = this.store.settingStore.businessSuper as any;
  // const formData = data.formData;
  let formDataValue = ''
  const templateId = useSettingStore().templateId
  await useSettingStore().getPageType(saleMode.value)
  const param = {
    templateId,
    modelId: saleMode.value ? saleMode.value : useSettingStore().saleMode,
  }
  getAssembly(param).then((res: any) => {
    if (res === '无此专区商品信息') {
      return
    }
    const properties = JSON.parse(res[0].properties) || ''
    formDataValue = properties.formData
    dealManage(formDataValue)
  })
}

// 处理没有商品列表的情况
const getNoList = (e: any) => {
  triggeredStatus.value = false
  freshing.value = false
  hasInfo.value = Boolean(e)
}

// 处理管理数据
const dealManage = (formDataInput: any) => {
  filterCategory(formDataInput).then(() => {
    if (formDataInput.selectMode === 2 && isFirst.value && formDataInput.firstCatList.length) {
      formDataInput.currentFirstCategory = formDataInput.firstCatList[0]
    }
    formDataInput.saleMode = saleMode.value || useSettingStore().saleMode
    formData.value = formDataInput
    isFirst.value = false
    hasInfo.value = Boolean(formDataInput.firstCatList.length)
    nextTick(() => {
      RightBoxStyle()
    })
  })
}

// 过滤分类
const filterCategory = (formDataInput: any) => {
  return new Promise((resolve, reject) => {
    getEffectiveCategory(saleMode.value || useSettingStore().saleMode)
      .then((res) => {
        const list = res
        const { firstCatList } = formDataInput
        const value: any[] = []
        firstCatList.forEach((i: any) => {
          const category = list.find((k: any) => k.id === i.category.id)
          if (category) {
            i.category = category
            value.push(i)
          }
        })
        formDataInput.firstCatList = value
        fitlerClassSed(formDataInput)
        resolve('')
      })
      .catch((e) => {
        console.log(e)
        reject()
      })
  })
}

// 过滤分类
const fitlerClassSed = (formDataInput: any) => {
  const { firstCatList, selectMode, selectType } = formDataInput
  const value: any[] = []
  firstCatList.forEach((i: any) => {
    const { showCategoryVos = [] } = i.category
    const { navigation, swiperList } = i
    navigation.forEach((k: any, f: number) => {
      if (!k.class.length) {
        navigation.splice(f, 1)
      } else {
        k.class.forEach((j: any, m: number) => {
          const idx = showCategoryVos.findIndex((t: any) => t.id === j)
          if (idx !== -1) {
            k.chartList[m].category = showCategoryVos.find((t: any) => t.id === j)
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
  formDataInput.firstCatList = [...value]
}

// 处理右侧盒子样式
const RightBoxStyle = () => {
  const getCategoryVo = getCategoryVoFunc(formData.value)
  const style = `width: calc(100% - ${formData.value.pagePadding * 2}px); margin-left: ${formData.value.pagePadding}px;`
  getRightBoxStyle.value = `${style} padding-bottom: ${getCategoryVo.hasNextCatgory ? '100rpx' : '0'}`
  isShowLoadNext.value = getCategoryVo.hasNextCatgory
  nextCategoryName.value = getCategoryVo.name || ''
}

// 改变分类ID
const changeCategoryId = (e: any) => {
  const id = e
  const formDataValue = formData.value
  const currentFirstCategoryValue = formDataValue.firstCatList.find((i: any) => i.category.id === id) || ''
  formDataValue.currentFirstCategory = currentFirstCategoryValue
  currentFirstCategory.value = currentFirstCategoryValue
  formData.value = formDataValue
  triggeredStatus.value = false
  refresherpullingPulling()
  RightBoxStyle()
}

// 获取分类信息
const getCategoryVoFunc = (formDataInput: any) => {
  const { category } = formDataInput.currentFirstCategory || {}
  const firstCatList = formDataInput.firstCatList || []
  const id = category ? category.id : ''
  const index = firstCatList.findIndex((i: any) => i.category.id === id)
  const hasNextCatgory = index !== -1 && index !== firstCatList.length - 1
  const hasUpperCatgory = index !== -1 && index !== 0
  const nextCategory = hasNextCatgory ? firstCatList[index + 1].category : {}
  const upperCatgory = hasUpperCatgory ? firstCatList[index - 1].category : {}
  return {
    ...nextCategory,
    hasNextCatgory,
    hasUpperCatgory,
    upperCatgory,
  }
}

// 滚动开始
const scrollStart = (e: any) => {
  scrollTopValue.value = e.changedTouches[0].clientY
  isComeBottom.value = false
}

// 滚动结束
const scrollEnd = (e: any) => {
  const scrollTopValueDiff = e.changedTouches[0].clientY - scrollTopValue.value
  if (!isComeBottom.value && scrollTopValueDiff < 0) {
    const categoryVo = getCategoryVoFunc(formData.value)
    if (categoryVo.hasNextCatgory) {
      changeCategoryId(categoryVo.id)
    }
  }
}

// 滚动绑定
const scrollBindScroll = () => {
  isComeBottom.value = true
}

// 刷新拉取
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
    nextTick(() => {
      triggeredStatus.value = false
      freshing.value = false
    })
  }
}

// 刷新恢复结束
const refresherrestoreEnd = () => {
  upperNextText.value = ''
}

// 刷新拉取中
const refresherpullingPulling = () => {
  const getCategoryVo = getCategoryVoFunc(formData.value)
  if (getCategoryVo.hasUpperCatgory) {
    upperNextText.value = getCategoryVo.upperCatgory.name
  } else {
    upperNextText.value = ''
  }
  if (getCategoryVo.hasNextCatgory) {
    nextCategoryName.value = getCategoryVo.name
  } else {
    nextCategoryName.value = ''
  }
}

// 生命周期
onLoad((options: any = {}) => {
  if (options && options.titleName) {
    uni.setNavigationBarTitle({
      title: `${options.titleName}专区`,
    })
  }
  saleMode.value = options?.id || ''
  isFirst.value = true
  getPageAssembly()
  getHeight()
})
</script>

<style lang="scss" scoped>
.spellPage--preview {
  width: 100vw;
  height: 100vh;
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
    height: 100vh;
    background-color: #ffffff;
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
