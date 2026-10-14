<template>
  <view>
    <canvas canvas-id="shareCanvas" class="share--card" style="width: 1120rpx; height: 1760rpx" />
    <canvas canvas-id="cardCanvas" class="share--canvas" style="width: 1120rpx; height: 1760rpx" />
    <uv-modal ref="modalRef" zIndex="99" title="" :showCancelButton="false" :showConfirmButton="false">
      <view v-if="showAll" style="padding: 25px">
        <view class="icon--end">
          <view class="icon--end__content" style="right: 15rpx">
            <MIcon name="icontijiaodingdan-youhuiquan-guanbi" size="40rpx" color="#D1D1D1" @tap="hiddenCrad" />
          </view>
        </view>
        <img style="width: 560rpx; height: 880rpx" :src="cardImgUrl" />
        <view class="downButton" style="margin-top: 40rpx; margin-left: 40rpx" @tap.stop="savePhoto">
          <view class="downButton--content">
            <view style="font-size: 30rpx">保存至本地</view>
          </view>
        </view>
      </view>
    </uv-modal>

    <swiper autoplay :interval="3000" :duration="300" circular class="swiper" :current="0" @change="onSlideChange">
      <block v-for="(item, index) in goodDetail.imgUrls" :key="index">
        <swiper-item>
          <image style="width: 100%; height: 100%" :src="item" class="swiper--item" mode="aspectFill" />
        </swiper-item>
      </block>
    </swiper>
    <view class="currentStyle" style="transform: translateY(-40rpx)">
      <view class="currentStyle__content" style="top: unset">
        <view class="currentStyle__content--curr">{{ current }} / {{ goodDetail?.imgUrls?.length }}</view>
      </view>
    </view>
    <view class="integral" style="transform: translateY(-40rpx); z-index: 30; position: absolute">
      <view class="hasBack__price">
        <view class="hasBack__price--actPrice">
          {{ goodDetail.integralPrice }}
          <view style="font-size: 34rpx; margin-bottom: 4rpx; font-weight: normal; margin-left: 10rpx">积分</view>
        </view>
        <view v-if="goodDetail.guidePrice" class="through" style="color: white"> 市场价{{ goodDetail.guidePrice }} </view>
      </view>
    </view>
    <view class="product">
      <view class="product__title">
        <view class="product__title--name">{{ goodDetail.integralGoodName }}</view>
      </view>
      <view v-if="true" class="product__shareFloat" @click="showShare">
        <view style="width: 50rpx; display: flex; flex-wrap: wrap; justify-content: center; align-content: center">
          <view class="iconfont iconshangpinxiangqing-fenxiang share__floatIcon" />
          <view style="font-size: 20rpx">分享</view>
        </view>
      </view>
      <view class="product__description">
        <text class="product__description--title">销量</text>
        <view class="product__description--desc">
          已售
          <text class="product__description--content">{{ goodDetail.sales }}</text>
          件,
        </view>
        <view class="product__description--desc">
          仅剩
          <text class="product__description--content">{{ goodDetail.inventory }}</text>
          件
        </view>
      </view>
    </view>
    <view class="cell">
      <view class="cell__line" data-mode="para" @tap="getPop">
        <text class="cell__title">产品参数</text>
        <view class="cell__title--right">
          <view style="margin-right: 20rpx">查看产品参数</view>
          <uv-icon name="arrow-right" style="font-size: 32rpx; font-weight: bold" />
        </view>
      </view>
      <UPopup v-model:show="show" mode="bottom" :overlay="true" :customStyle="{ height: '50%' }" round="20" @close="onClose">
        <view class="popup__attr" style="padding-bottom: 100rpx">
          <view class="cell__pop--reduce--text">商品参数</view>
          <view v-for="(item, index) in goodDetail.interalGoodsAttibutes" :key="index" :style="'padding-top:' + (index === 0 ? '100rpx' : '')">
            <view class="popup__attr--item">
              <view class="popup__attr--item--left">{{ item.name }}</view>
              <view class="popup__attr--item--right">{{ item.value }}</view>
            </view>
          </view>
          <view class="center--middle" @tap="onClose">
            <view class="popup__attr--finish">完成</view>
          </view>
        </view>
      </UPopup>
    </view>
    <view class="shop--content">— 商品详情 —</view>
    <view class="shop--english">COMMODITY DETAILS</view>
    <!-- 商品详情富文本 -->
    <view class="goods__text" @tap="pageLink('/pages/systemBanner/systemBanner')">
      <uv-parse :content="goodDetail.detail" lazyLoad></uv-parse>
      <image v-if="!!shopInfo.miniBottomLog" class="miniBottomLog" :src="!!shopInfo.miniBottomLog ? shopInfo.miniBottomLog : ''" />
      <Sign :text="'smart shop商城系统 v' + APP_VERSION" />
    </view>
    <!-- 底部按钮标签 -->
    <view class="detail__bottom">
      <view class="detail__img" @tap="returnHome">
        <MIcon name="iconshouye" size="52rpx" />
        <text class="detail__img--icon">店铺</text>
      </view>
      <uv-button
        open-type="contact"
        :customStyle="{
          border: 'none',
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'center',
          height: '99rpx',
        }"
        class="detail__img"
      >
        <MIcon name="iconxinbaniconshangchuan-" size="44rpx" />
        <text class="detail__img--icon">客服</text>
      </uv-button>
      <view v-if="goodDetail.status === 0" class="noBuy__btn">商品已下架</view>
      <view v-if="goodDetail.inventory === 0 && goodDetail.status === 1" class="noBuy__btn">商品已售罄</view>
      <view v-if="goodDetail.inventory !== 0 && goodDetail.status === 1" class="buy__btn" @tap="gotoSubmit"> 立即兑换 </view>
    </view>
    <UPopup v-model:show="shareVisible" mode="bottom" closeable @close="onClose">
      <view class="share">分享</view>
      <view class="shareWay">
        <!-- #ifdef MP-WEIXIN -->
        <view class="shareWay__side">
          <uv-button
            open-type="share"
            style="border-radius: 100rpx; display: flex; justify-content: center; align-items: center"
            :customStyle="{
              border: 'none',
              display: 'flex',
              flexDirection: 'column',
              justifyContent: 'center',
              height: '110rpx',
            }"
            @tap="onClose"
          >
            <image src="https://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/api/WeChat.png" />
          </uv-button>
          <view class="shareWay__side--text">微信好友</view>
        </view>
        <!-- #endif -->
        <view class="shareWay__side" @tap="shareBrCard">
          <image src="https://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/api/poster.png" />
          <view class="shareWay__side--text">生成海报</view>
        </view>
      </view>
      <view class="shareTip">
        <view class="shareTip__tip">好物推荐，和好友一起分享你发现的好物</view>
      </view>
    </UPopup>
  </view>
</template>

<script setup lang="ts">
import { onBeforeUnmount, ref, getCurrentInstance } from 'vue'
import { onLoad, onShow, onShareAppMessage } from '@dcloudio/uni-app'
import { getIntegralGoodsDetail, shareGetIntegral, getIntegralRule } from '@/api/modules/integral'
import { getImgCode } from '@/api/modules/goods'
import { base64src } from '@/utils/base64src'
import page from './canvas'
import Sign from '@/components/Sign/Sign.vue'
import MIcon from '@/components/MIcon/MIcon.vue'
import { useShopsetStore } from '@/store/modules/shopset'
import { useSettingStore } from '@/store/modules/setting'
import { APP_LAUNCH } from '@/utils/emitNameConfig'

// 获取当前组件实例
const instance = getCurrentInstance()

// 响应式状态
const goodDetail = ref<Obj>({})
const show = ref(false)
const paraShow = ref(false)
const shareVisible = ref(false)
const modalRef = ref()
const imgUrl = ref('')
const shareImgUrl = ref('')
const cardImgUrl = ref('')
const current = ref(1)
const APP_VERSION = ref(import.meta.env.VITE_APP_APP_SHOW_VERSION)
const shopInfo = ref<Obj>({})
const showAll = ref(false)

// 方法定义
const onSlideChange = (e: any) => {
  current.value = e.detail.current + 1
}

const getPop = ({
  currentTarget: {
    dataset: { mode },
  },
}: any) => {
  show.value = true
  switch (mode) {
    case 'para':
      paraShow.value = true
      break
    default:
      break
  }
}

const canv = (img: any) => {
  return new Promise((resolve) => {
    let priceHeight = 415
    const lineHeight = 407
    let newWidth = 100
    uni.getSystemInfo({
      success(res) {
        if (res.model.indexOf('iPhone X') > -1) {
          priceHeight = 415
          newWidth = 140
        }
        if (res.model.indexOf('HMA-AL00') > -1) {
          priceHeight = 415
        }
      },
    })
    const canvas = uni.createCanvasContext('shareCanvas', instance)
    canvas.drawImage(img, 0, 0, 560, 500)
    canvas.save()
    canvas.rect(0, 365, 560, 90)
    canvas.setFillStyle('#FF0C19')
    canvas.fill()
    canvas.beginPath()
    canvas.setFontSize(28)
    canvas.setFillStyle('white')
    canvas.fillText(`积分${goodDetail.value.integralPrice}`, 20, priceHeight, 400)
    const leftWidth = canvas.measureText(goodDetail.value.integralPrice) as any
    canvas.setFontSize(24)
    if (goodDetail.value.guidePrice) {
      canvas.fillText(`￥${goodDetail.value.guidePrice}`, newWidth + leftWidth.width, priceHeight, 400)
      const delPriceWidth = canvas.measureText(goodDetail.value.guidePrice) as any
      canvas.moveTo(100 + leftWidth.width, lineHeight)
      canvas.lineTo(100 + leftWidth.width + delPriceWidth.width + 25, lineHeight)
      canvas.setStrokeStyle('white')
      canvas.stroke()
    }
    canvas.draw(true, () => {
      uni.canvasToTempFilePath({
        x: 0,
        y: 0,
        canvasId: 'shareCanvas',
        success: (res) => {
          img = res.tempFilePath
          imgUrl.value = img
          resolve(img)
        },
      })
    })
  })
}

const showShare = () => {
  shareVisible.value = true
}

const hiddenCrad = () => {
  modalRef.value.close()
}

const shareBrCard = () => {
  const url = page
  showLoad()
  let codeImg = ''
  uni.pageScrollTo({
    scrollTop: 0,
    duration: 200,
  })
  modalRef.value.open()
  shareVisible.value = false
  const wxaGetwxacode = {
    path: `pages/detail/detail?id=${goodDetail.value.id}`,
    width: 0,
  }
  getImgCode(wxaGetwxacode).then((res) => {
    codeImg = res
    base64src(codeImg, (res: string) => {
      codeImg = res
      const proxyImgUrl =
        import.meta.env.MODE === 'development' ? goodDetail.value.pic.replace(import.meta.env.VITE_APP_OSS, '/proxy/img') : goodDetail.value.pic
      uni.downloadFile({
        url: proxyImgUrl,
        success: (v) => {
          // 添加类型断言解决类型不匹配问题
          url.methods.canvCard(goodDetail.value as any, 1, v.tempFilePath, codeImg, instance).then((response: string) => {
            cardImgUrl.value = response
            showAll.value = true
            showLoad()
          })
        },
      })
    })
  })
}

const showLoad = () => {
  if (!cardImgUrl.value) {
    uni.showLoading({
      title: '图片生成中',
    })
  } else {
    uni.hideLoading()
  }
}

const savePhoto = () => {
  // #ifdef MP-WEIXIN
  uni.getSetting({
    success: (res) => {
      if (res.authSetting['scope.writePhotosAlbum'] === false) {
        uni.showModal({
          title: '',
          content: '请在设置中开启保存相册功能后再次保存图片',
          success() {
            uni.openSetting({})
          },
        })
      } else {
        uni.saveImageToPhotosAlbum({
          filePath: cardImgUrl.value,
          success() {
            uni.showToast({
              title: '保存成功',
              icon: 'none',
            })
          },
        })
      }
    },
    fail: (err) => {
      console.log(err)
    },
  })
  // #endif
  // #ifdef H5
  uni.showToast({
    title: '请长按图片保存',
    icon: 'none',
  })
  // #endif
}

const onClose = () => {
  show.value = false
  paraShow.value = false
  shareVisible.value = false
}

const gotoSubmit = () => {
  const temObj = { ...goodDetail.value }
  delete temObj.interalGoodsAttibutes
  delete temObj.detail
  const goodDetailStr = JSON.stringify(temObj)
  uni.navigateTo({
    url: '/modules/pages/submit/integralSubmit/integralSubmit?goodDetail=' + goodDetailStr,
  })
}

const getGood = async (id: any) => {
  const res = await getIntegralGoodsDetail(id, {})
  let goodDetailVal = { ...goodDetail.value }
  goodDetailVal = res
  goodDetailVal.imgUrls = res.albumPics.split(',')
  goodDetailVal.guidePrice = goodDetailVal.guidePrice ? goodDetailVal.guidePrice.toFixed(2) : null
  goodDetailVal.sale = goodDetailVal.sales
  const proxyImgUrl =
    import.meta.env.MODE === 'development' ? goodDetailVal.pic.replace(import.meta.env.VITE_APP_OSS, '/proxy/img') : goodDetailVal.pic
  uni.getImageInfo({
    src: proxyImgUrl,
    success: (res) => {
      canv(res.path).then((res: any) => {
        shareImgUrl.value = res
      })
    },
  })
  goodDetail.value = goodDetailVal
}

const pageLink = (url: string) => {
  uni.navigateTo({
    url: `/subcontract${url}`,
  })
}

const returnHome = () => {
  useSettingStore().backHome()
}

// 生命周期钩子
onLoad((options: any) => {
  if (options?.id) {
    getGood(options.id)
    uni.$on(APP_LAUNCH, () => {
      getGood(options.id)
    })
  }
})

onBeforeUnmount(() => {
  uni.$off(APP_LAUNCH)
})

onShow(() => {
  // #ifdef MP-WEIXIN
  uni.hideHomeButton()
  // #endif
  const pages = getCurrentPages()
  const currPage: any = pages[pages.length - 1]
  shopInfo.value = useShopsetStore().shopInfo
  if (currPage.$vm.id) {
    getGood(currPage.$vm.id)
  }
})

onShareAppMessage(() => {
  getIntegralRule({}).then((res) => {
    if (res[0].open) {
      shareGetIntegral({}).then((res) => {
        if (res) {
          uni.showToast({
            icon: 'none',
            title: `分享成功，获得${res}积分`,
            duration: 2000,
          })
        }
      })
    }
  })
  return {
    imageUrl: imgUrl.value,
    title: goodDetail.value.integralGoodName,
    path: `/subcontract/pages/detail/integralGoods?id=${goodDetail.value.id}&type=1`,
  }
})
</script>

<style lang="scss" scoped>
@import '@/assets/styles/detail/detail.scss';
:deep(.uv-modal__content) {
  padding: 0;
  padding-top: 0 !important;
}
:deep(.uv-line) {
  display: none;
}
.priceStyle {
  margin-top: 40rpx;
  border-top-left-radius: 30rpx;
  border-top-right-radius: 30rpx;
  margin-left: 20rpx;
  margin-right: 20rpx;
  position: relative;
  top: -90rpx;
  background-color: #b61e1e;
  height: 100rpx;
  display: flex;
  justify-content: center;
  align-items: center;

  &__act {
    margin-top: 10rpx;
    margin-left: 30rpx;
    margin-right: 30rpx;
    flex-grow: 1;

    &--price {
      display: flex;
      justify-content: space-between;
      align-items: flex-end;
    }
  }
}

.through {
  font-size: 30rpx;
}

.product {
  position: relative;
  padding-top: calc(116rpx - 40rpx);
}

.product__share {
  position: absolute;
  flex-wrap: wrap;
  width: 80rpx;
  right: 40rpx;
  top: 80rpx;
}

.product__box {
  overflow: hidden;
  margin-top: 20rpx;
}

.product__box--list {
  width: 160rpx;
  .item__book--text {
    width: 160rpx;
  }
}

.product__title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  &--name {
    font-size: 32rpx;
    font-family: HiraginoSansGB-W6;
    line-height: 52rpx;
    color: #1e1c1c;
  }
}

.product__description {
  &--desc {
    font-size: 24rpx;
  }
  &--content {
    margin-left: 10rpx;
    margin-right: 10rpx;
    font-weight: bold;
  }
}

.icon--end__content {
  width: 10rpx;
  right: -10rpx;
}

.cell {
  margin-bottom: 20rpx;
}

.cell__title {
  font-size: 24rpx;
  width: 130rpx;
}

.cell__title--right {
  font-size: 24rpx;
  color: #989898;
  display: flex;
  align-items: center;

  &--max {
    font-weight: bold;
  }
}

.downButton {
  height: 60rpx;
}

.detail__img {
  border-left: 1rpx solid #e5e5e5;
}

.detail__img > image {
  width: 68rpx;
  height: 68rpx;
}

.detail__img:nth-child(2) {
  border-left: 1rpx solid #e5e5e5;
}

.noBuy__btn {
  height: 98rpx;
  background-color: #919191;
  text-align: center;
  line-height: 98rpx;
  font-size: 30rpx;
  font-family: MicrosoftYaHei;
  font-weight: 400;
  color: #fff;
  width: 50%;
}

.buy__btn {
  height: 98rpx;
  background-color: #fe4e63;
  line-height: 98rpx;
  width: 60%;
}

.btn {
  padding-left: 30rpx;
  padding-right: 30rpx;
  padding-top: 20rpx;
  padding-bottom: 20rpx;
  font-size: 36rpx;
  font-weight: bold;
  display: flex;
  justify-content: space-between;
  height: 60rpx;
}

.specific {
  height: 800rpx;
}

.specific__info {
  width: 750rpx;
  height: 200rpx;
  display: flex;
  flex-direction: row;
  padding-left: 30rpx;
  padding-right: 30rpx;
  padding-top: 20rpx;
  padding-bottom: 20rpx;
  box-sizing: border-box;
}

.specific__info--image > image {
  width: 260rpx;
  height: 260rpx;
  border: 5rpx solid #fff;
}

.specific__info--product {
  flex: 1;
  margin-left: 30rpx;

  .product__price {
    font-size: 32rpx;
    font-weight: bold;
    color: #fe4e63;
  }

  .product__max {
    font-size: 32rpx;
    color: #fe4e63;
    margin-top: 20rpx;
  }

  .product__price::before {
    content: '￥';
    font-weight: normal;
  }

  .product__stock {
    margin-top: 40rpx;
    height: 30rpx;
    line-height: 30rpx;
    font-size: 30rpx;
  }
}
</style>
