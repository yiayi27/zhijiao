<template>
  <view>
    <view :class="{ home: true, tripList_root: showModalView }">
      <view v-for="(item, index) in components" :key="index">
        <!-- 魔方组件 -->
        <CubeBox v-if="item.value === 'CubeBox'" :decoretionProperties="item.formData" />
        <!-- 店铺导航组件 -->
        <ShopNav v-if="item.value === 'StoreNavigation'" :navList="item.formData.storeNavigations" @AssembleReload="AssembleReload" />
        <!-- 优惠券组件 -->
        <CouponComponent v-if="item.value === 'Coupon'" :decoretionProperties="item.formData" @stopRoll="stopRoll" />
        <!-- 轮播图组件 -->
        <HomeSwiper v-if="item.value === 'HomeSwiper' && item.formData.swiperList.length" :decoretionProperties="item.formData" />
        <!-- 分隔符组件 -->
        <HomeSeparator v-if="item.value === 'Separator'" :decoretionProperties="item.formData" />
        <!-- 空白占位组件 -->
        <BlankPlaceholder v-if="item.value === 'BlankPlaceholder'" :decoretionProperties="item.formData" />
        <!-- 图片组件 -->
        <ImageCom v-if="item.value === 'ImageCom'" :decoretionProperties="item.formData" />
        <!-- 商品组件 -->
        <GoodsPonents v-if="item.value === 'Goods'" ref="GroupGoods" :decoretionProperties="item.formData" />
        <!-- 搜索组件 -->
        <SearchComp v-if="item.value === 'Search'" :decoretionProperties="item.formData" @search="search" />
        <!-- 标题组件 -->
        <TitleBar v-if="item.value === 'TitleBar'" :decoretionProperties="item.formData" />
        <!-- 接龙组件 -->
        <Chain v-if="item.value === 'Chain'" :decoretionProperties="item.formData" />
        <!-- 富文本组件 -->
        <RichText v-if="item.value === 'RichText'" :decoretionProperties="item.formData" />
        <!-- 直播组件 -->
        <!-- #ifdef MP-WEIXIN -->
        <HomeLive v-if="item.value === 'HomeLive'" :decoretionProperties="item.formData" />
        <!-- #endif -->
        <!-- 商品轮播图 -->
        <GoodsSwiper v-if="item.value === 'GoodSwiper'" :decoretionProperties="item.formData" />
        <!-- 视频组件 -->
        <VideoCom v-if="item.value === 'VideoCom'" :decoretionProperties="item.formData" />
      </view>

      <ShopManage v-model:shoptype="shoptype" :shopMsg="shopMsg" :shopTime="shopTime" :shopStatus="shopStatus" style="z-index: 1000000" />
    </view>
    <HomeCoupon v-show="controlCoupon && !isCustom" :animation="animation" @stopRoll="stopRoll" />
    <HomeRecycling v-if="isLogin" catchtouchmove />
  </view>
</template>

<script setup lang="ts">
import { getAssembly } from '@/api/modules/decoration'
import CubeBox from './components/CubeBox/CubeBox.vue'
import ShopNav from './components/ShopNav/ShopNav.vue'
import CouponComponent from './components/CouponComponent/CouponComponent.vue'
import HomeSwiper from './components/HomeSwiper/HomeSwiper.vue'
import HomeSeparator from './components/HomeSeparator/HomeSeparator.vue'
import BlankPlaceholder from './components/BlankPlaceholder/BlankPlaceholder.vue'
import ImageCom from './components/ImageCom/ImageCom.vue'
import GoodsPonents from './components/GoodsPonents/GoodsPonents.vue'
import SearchComp from './components/SearchComp/SearchComp.vue'
import TitleBar from './components/TitleBar/TitleBar.vue'
import Chain from './components/Chain/Chain.vue'
import RichText from './components/RichText/RichText.vue'
// #ifdef MP-WEIXIN
import HomeLive from './components/HomeLive/HomeLive.vue'
// #endif
import GoodsSwiper from './components/GoodsSwiper/GoodsSwiper.vue'
import VideoCom from './components/VideoCom/VideoCom.vue'
import ShopManage from '@/components/ShopManage/ShopManage.vue'
import HomeCoupon from './components/HomeCoupon/HomeCoupon.vue'
import HomeRecycling from './components/HomeRecycling/HomeRecycling.vue'
import { handUserid } from '@/api/modules/distribution'
import { useSettingStore } from '@/store/modules/setting'
import { useShopsetStore, type ShopInfo } from '@/store/modules/shopset'
import { $Popup } from '@/utils/helper'
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useUserStore } from '@/store/modules/user'
import { storeToRefs } from 'pinia'
import { APP_LAUNCH } from '@/utils/emitNameConfig'

const { isLogin } = storeToRefs(useUserStore())
const { shopInfo } = storeToRefs(useShopsetStore())

// 定义props
const props = defineProps({
  hasInCustom: {
    type: Boolean,
    default: false,
  },
  isCustom: {
    type: Boolean,
    default: false,
  },
  currentTab: {
    type: Object,
    default: () => ({}),
  },

  userid: {
    type: String,
    default: '',
  },
})

// 定义emits
const emit = defineEmits(['stopShow', 'stopuserid'])

// 定义响应式状态
const showModalView = ref(false)
const components = ref<any[]>([])
const scrollTop = ref(0)
const shoptype = ref(false)
const shopTime = ref('00:00')
const shopStatus = ref(2)
const shopMsg = ref<ShopInfo>({})
const controlCoupon = ref(true)
const animation = ref<any>({})

// 引用
const GroupGoods = ref<any>(null)

// 方法定义
const parseAssembleList = (assembleList: any[]): any[] => {
  const result: any[] = []
  if (assembleList.length > 0) {
    assembleList.forEach((item) => {
      const properties = JSON.parse(item.properties)
      result.push(properties)
    })
  }
  return result
}

const getShopCom = () => {
  if (!shopInfo.value.businessHours) return
  const time = JSON.parse(shopInfo.value.businessHours)
  if (shopInfo.value.status !== 2) {
    shopMsg.value = shopInfo.value
    shoptype.value = true
    shopTime.value = time[0].slice(0, 5)
    shopStatus.value = shopInfo.value.status || 0
  } else {
    shoptype.value = false
  }
}

const reGetPageAssembly = async (id: Long = '') => {
  const pageId = id ? id : useSettingStore().newCurrentPageId
  try {
    const res = await getAssembly({ pageId })
    console.log(res)
    components.value = res ? parseAssembleList(res) : []
    getShopCom()
  } catch (error) {
    $Popup.toast(error as string)
  }
}

const AssembleReload = () => {
  components.value = useSettingStore().components
}
const getPageAssembly = async () => {
  try {
    if (!useSettingStore().hasInCustom) {
      // 初始化通过聚合接口获取到首页数据
      AssembleReload()
    } else {
      console.log(useSettingStore().newCurrentPageId)
      // 除第一次外 进入首页需重新获取接口获取首页数据
      await reGetPageAssembly(useSettingStore().newCurrentPageId)
    }
    getShopCom()
  } catch (error) {
    $Popup.toast(error as string)
  }
}

const getPageSetting = async () => {
  await getPageAssembly()
  animation.value = uni.createAnimation({})
}

const PageScroll = (e: { scrollTop: number }) => {
  scrollTop.value = e.scrollTop
  const animationObj = uni.createAnimation({
    duration: 500,
    timingFunction: 'ease',
  })

  animationObj.translate(50, 0).step()
  animation.value = animationObj.export()

  const timer = setTimeout(() => {
    if (scrollTop.value === e.scrollTop) {
      scrollTop.value = e.scrollTop
      animationObj.translate(0, 0).step()
      animation.value = animationObj.export()
      clearTimeout(timer)
    }
  }, 500)
}

const search = (val: { scope: number; name: { detail: any } }) => {
  if (GroupGoods.value && val.scope === 1) {
    GroupGoods.value?.onSearch(val.name.detail)
  }
}

const resetInfo = () => {
  //TODO: 重置信息
  console.log('TODO: 重置信息')
}

const stopRoll: (e: any) => void = (e) => {
  const type = e
  showModalView.value = type
}

// 生命周期
onMounted(async () => {
  if (props.userid) {
    try {
      const res = await handUserid(props.userid, {})
      emit('stopuserid')
      uni.showToast({
        title: `你已成为${res}下级分销员`,
        icon: 'none',
      })
    } catch (error) {
      emit('stopuserid')
      uni.showToast({
        title: `${error}`,
        icon: 'none',
      })
    }
  }
  getPageSetting()
  const obj: any = uni?.getLaunchOptionsSync?.() ?? {}

  emit('stopShow', {
    type: shopInfo.value.status !== 2 && obj.scene ? 0 : 1,
  })
  PageScroll({
    scrollTop: 10,
  })
  uni.$on(APP_LAUNCH, () => {
    getPageSetting()
  })
})

onBeforeUnmount(() => {
  uni.$off(APP_LAUNCH)
  useSettingStore().setIftransFrom(false)
})

// 暴露方法给父组件调用
defineExpose({
  reGetPageAssembly,
  resetInfo,
  PageScroll,
})
</script>

<style lang="scss" scoped>
.home {
  background-color: #f8f8f8;
}

.icon {
  color: red;
}

.popupSty {
  width: 70%;
  height: 70%;
}

.tripList_root {
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 0;
}

.choiceShop2 {
  position: fixed;
  bottom: 300rpx;
  right: 40rpx;
  width: 90rpx;
  height: 90rpx;
  display: -webkit-box;
  display: flex;
  -webkit-box-align: center;
  align-items: center;
  -webkit-box-pack: center;
  justify-content: center;
  flex-wrap: wrap;
  background-color: rgba(0, 0, 0, 0.5);
  border-radius: 100rpx;
  color: white;
  z-index: 10;

  .choiceWrap {
    width: 50rpx;
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    align-content: center;
  }

  .ft {
    font-size: 20rpx;
    margin-top: -6rpx;
  }
}
</style>
