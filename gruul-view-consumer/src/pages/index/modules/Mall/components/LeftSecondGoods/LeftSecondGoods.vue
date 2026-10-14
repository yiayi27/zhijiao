<template>
  <scroll-view :class="'top__biggoods-page ' + getBigGoodsPageClass" :scroll-x="false" scroll-y>
    <!-- 列表模式 -->
    <view
      v-for="(item, index) in goodsList"
      v-show="formData.listStyle === 2"
      :key="index"
      class="spellpre__goods spellpre__goodsL"
      :style="'padding: ' + (index === 0 ? '20rpx 0 ' + formData.goodsPadding + 'px 0' : formData.goodsPadding + 'px 0')"
    >
      <view :class="'spellpre__goods--box ' + getGoodsBoxClass" :data-id="item.id" @tap.stop="gotoDetail">
        <view class="spellpre__goodsL--content">
          <view class="spellpre__goodsL--icon">
            <view v-if="!item.inventory" class="no__goods">
              <text>已售罄</text>
              <text>Sold out</text>
            </view>
            <view class="ipic">
              <image :src="item.img" mode="widthFix" alt :lazy-load="true" />
            </view>
          </view>
          <view class="spellpre__goodsL--info">
            <view :class="'spellpre__goodsL--name ' + getGoodsNameClass">{{ item.name }}</view>
            <view class="spellpre__goodsL--buy" style="display: flex; justify-content: space-between">
              <view class="spellpre__goodsL--firPrinum">
                {{ '¥' + (item.actPrice !== '0.00' ? item.actPrice : item.price) }}
                <view v-if="item.price !== '0.00'" class="spellpre__goodsL--firLinePrinum"> ¥{{ item.price }} </view>
              </view>
              <view v-if="formData.cartButton === 3" class="spellpre__goods--cart spellpre__goods--cart1">
                <image
                  src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20200710/810700b67e264a7b82bba347528b9eb0.png"
                  alt
                />
              </view>
              <view v-if="formData.cartButton === 4" class="spellpre__goods--cart spellpre__goods--cart2">
                <image
                  src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20200710/3de00f3d8099459c83771b1f4decb197.png"
                  alt
                />
              </view>
              <view v-if="formData.cartButton === 1" class="spellpre__goods--cart spellpre__goods--cart3"> 立即购买 </view>
              <view v-if="formData.cartButton === 2" class="spellpre__goods--cart spellpre__goods--cart4"> 立即购买 </view>
            </view>
          </view>
        </view>
      </view>
    </view>
    <!-- 大图模式 -->
    <view
      v-for="(item, index) in goodsList"
      v-show="formData.listStyle === 1"
      :key="index"
      class="spellpre__goods spellpre__goodsB"
      :style="'padding: ' + (index === 0 ? '20rpx 0 ' + formData.goodsPadding + 'px 0' : formData.goodsPadding + 'px 0')"
    >
      <view :class="'spellpre__goods--box ' + getGoodsBoxClass" :data-id="item.id" @tap.stop="gotoDetail">
        <view class="spellpre__goodsB--icon">
          <view v-if="!item.inventory" class="no__goods">
            <text>已售罄</text>
            <text>Sold out</text>
          </view>
          <image :src="item.img" mode="aspectFill" alt :lazy-load="true" />
        </view>
        <view class="detail__box">
          <view :class="'spellpre__goodsB--name ' + getGoodsNameClass">{{ item.name }}</view>
          <view class="spellpre__goodsB--buy">
            <view class="spellpre__goodsB--prinum"> ¥{{ item.actPrice !== '0.00' ? item.actPrice : item.price }} </view>
            <view v-if="item.price !== '0.00'" class="spellpre__goodsL--linePrinum"> ¥{{ item.price }} </view>
            <view v-if="formData.cartButton === 3" class="spellpre__goods--cart spellpre__goods--cart1">
              <image
                src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20200710/810700b67e264a7b82bba347528b9eb0.png"
                alt
              />
            </view>
            <view v-if="formData.cartButton === 4" class="spellpre__goods--cart spellpre__goods--cart2">
              <image
                src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20200710/3de00f3d8099459c83771b1f4decb197.png"
                alt
              />
            </view>
            <view v-if="formData.cartButton === 1" class="spellpre__goods--cart spellpre__goods--cart3"> 立即购买 </view>
            <view v-if="formData.cartButton === 2" class="spellpre__goods--cart spellpre__goods--cart4"> 立即购买 </view>
          </view>
        </view>
      </view>
    </view>
  </scroll-view>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'

const props = defineProps({
  propData: {
    type: Object,
    default: () => ({}),
  },
  dataList: {
    type: Array,
    default: () => [],
  },
})

const formData = ref<Obj>({})
const getGoodsNameClass = ref('')
const getGoodsBoxClass = ref('')
const getBigGoodsPageClass = ref('')
const goodsList = ref<any[]>([])

watch(
  () => props.propData,
  (newValue) => {
    setFormData(newValue)
  },
)

watch(
  () => props.dataList,
  (newValue) => {
    setDataList(newValue)
  },
)

onMounted(() => {
  if (props.propData) {
    setFormData(props.propData)
  }
  if (props.dataList) {
    setDataList(props.dataList)
  }
})

const setFormData = (newValue: any) => {
  if (newValue) {
    formData.value = newValue
    getInfo()
  }
}

const setDataList = (newValue: any[]) => {
  if (newValue) {
    if (!formData.value.firstCatList) return
    getAllList(newValue)
    getInfo()
  }
}

const getInfo = () => {
  getGoodsNameClassHandle()
  getGoodsBoxClassHandle()
  getBigGoodsPageClassHandle()
}

const getGoodsBoxClassHandle = () => {
  // const { goodsStyle = 1, doodsAngle = 1 } = this.data.formData;
  const goodsStyle = 1
  const doodsAngle = 1
  // 商品样式 1无底白边 2卡片投影 3描边白底
  const gstyles = ['spellpre__goods--boxWB', 'spellpre__goods--boxCP', 'spellpre__goods--boxSW']
  // 图片倒角 1直角 2圆角
  const astyles = ['spellpre__goods--corners', 'spellpre__goods--angle']
  const gs = gstyles[+goodsStyle - 1]
  const as = astyles[+doodsAngle - 1]
  getGoodsBoxClass.value = `${gs} ${as}`
}

const getGoodsNameClassHandle = () => {
  const { textStyle = 1 } = formData.value
  // 1常规体 2加粗体
  const styles = ['spellpre__goods--nameF', 'spellpre__goods--nameB']
  getGoodsNameClass.value = styles[+textStyle - 1]
}

const getBigGoodsPageClassHandle = () => {
  // 1顶部模式 2侧边栏模式 1大图样式 2列表样式
  const { selectMode, listStyle } = formData.value
  const list: string[] = []
  if (selectMode === 1) {
    list.push('page__goods-addPadding')
  }
  if (selectMode === 2 && listStyle === 2) {
    list.push('left__Lgoods-style')
  }
  getBigGoodsPageClass.value = `${list.join(' ')}`
}

const getAllList = (list: any[]) => {
  const data: any = []
  list.forEach((item) => {
    item.hasTimeTips = item.goodTime && item.goodTime.includes(':')
    item.goodTime = item.hasTimeTips ? item.goodTime.substr(5) : item.goodTime
    data.push({
      id: Number(item.id),
      name: item.name,
      img: returnGoodsImg(item),
      actPrice: item.minPrice.toFixed(2),
      price: item.maxPrice.toFixed(2),
      soldCount: item.sale,
      inventory: item.inventory,
      hasTimeTips: item.hasTimeTips,
    })
  })
  const formDataValue = formData.value
  if (formDataValue.count > data.length) {
    formDataValue.count = data.length
  }
  const goodsListValue = goodsList.value.concat(data)
  goodsList.value = goodsListValue
  formData.value = formDataValue
}

const returnGoodsImg = (item: any) => {
  const { widePic, pic } = item
  let url = pic
  if (formData.value.listStyle === 1) {
    url = widePic ? widePic.split(',')[0] : pic
  }
  return url
}

const gotoDetail = (e: any) => {
  const goodId = e.currentTarget.dataset.id
  uni.navigateTo({
    url: `/subcontract/pages/detail/detail?id=${goodId}`,
  })
}
</script>
<style lang="scss" scoped>
.top__biggoods-page .spellpre__goods--box {
  padding: 0rpx;
}

.top__biggoods-page .businessSuper .el-radio {
  margin-right: 40rpx;
}

.businessSuper__addmart {
  margin-top: 14rpx;
}

.top__biggoods-page .spellpre {
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  background-color: #f8f8f8;
  position: relative;
}

.spellpre__header {
  color: #333;
  height: 100rpx;
  line-height: 100rpx;
  background-color: #fff;
  width: 100%;
  position: absolute;
  left: 0;
  top: 0;
  z-index: 2;
}

.spellpre__header--title {
  font-size: 36rpx;
  font-weight: 700;
}

.spellpre__header--more {
  font-size: 28rpx;
  font-weight: 400;
  float: right;
}

.spellpre__headtemp {
  height: 100rpx;
  line-height: 100rpx;
  width: 100%;
}

.spellpre__goods {
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
}

.spellpre__goods .spellpre__goods--box {
  position: relative;
  background-color: #fff;
  overflow: hidden;
}

.spellpre__goods--boxCP {
  -webkit-box-shadow: 0 0 8rpx rgba(0, 0, 0, 0.2);
  box-shadow: 0 0 8rpx rgba(0, 0, 0, 0.2);
}

.spellpre__goods--boxSW {
  border: 2rpx solid #f8f8f8;
}

.spellpre__goods--angle {
  border-radius: 14rpx;
}

.spellpre__goods--corners {
  border-radius: 0;
}

.spellpre__goods--nameF {
  font-weight: 400;
}

.spellpre__goods--nameB {
  font-weight: 600;
}

.spellpre__goods .no__goods {
  position: absolute;
  left: 50%;
  top: 50%;
  margin-left: -70rpx;
  margin-top: -70rpx;
  z-index: 11;
  width: 140rpx;
  height: 140rpx;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
  display: -webkit-box;
  display: flex;
  -webkit-box-align: center;
  align-items: center;
  -webkit-box-pack: center;
  justify-content: center;
  color: #fff;
  -webkit-box-orient: vertical;
  -webkit-box-direction: normal;
  flex-direction: column;
}

.spellpre__goods .no__goods text {
  display: block;
  width: 176rpx;
  text-align: center;
  font-size: 25rpx;
}

.spellpre__goods--cart1 {
  float: right;
  height: 56rpx;
  width: 56rpx;
  background-color: #fc623f;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  border-radius: 50%;
  display: -webkit-box;
  display: flex;
  -webkit-box-pack: center;
  justify-content: center;
  -webkit-box-align: center;
  align-items: center;
  margin-top: 10rpx;
}

.spellpre__goods--cart1 image {
  display: inline-block;
  width: 38rpx;
  height: 38rpx;
}

.spellpre__goods--cart {
  float: right;
  height: 56rpx;
  width: 56rpx;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  border-radius: 50%;
  display: -webkit-box;
  display: flex;
  -webkit-box-pack: center;
  justify-content: center;
  -webkit-box-align: center;
  align-items: center;
  margin-top: 10rpx;
}

.spellpre__goods--cart1 {
  background-color: #fff;
}

.spellpre__goods--cart1 image {
  display: inline-block;
  width: 60rpx;
  height: 60rpx;
}

.spellpre__goods--cart2 {
  background: -webkit-linear-gradient(286deg, #f3f3f3, #e5382e, #fd4e26);
  background: linear-gradient(164deg, #f3f3f3, #e5382e, #fd4e26);
  -webkit-box-shadow: 0rpx 4rpx 14rpx 0rpx rgba(255, 14, 0, 0.27);
  box-shadow: 0rpx 4rpx 14rpx 0rpx rgba(255, 14, 0, 0.27);
  border-radius: 50%;
}

.spellpre__goods--cart2 image {
  display: inline-block;
  width: 40rpx;
  height: 40rpx;
}

.spellpre__goods--cart3 {
  border: 2rpx solid #fc623f;
  width: 160rpx;
  color: #fc623f;
  font-size: 28rpx;
  border-radius: 40rpx;
  height: 60rpx;
  line-height: 60rpx;
}

.spellpre__goods--cart4 {
  border: 2rpx solid #fc623f;
  background-color: #fc623f;
  width: 160rpx;
  color: #fff;
  font-size: 28rpx;
  border-radius: 40rpx;
  height: 60rpx;
  line-height: 60rpx;
}

.spellpre__goods--coner {
  position: absolute;
}

.spellpre__goods--coner image,
.spellpre__goods--coner text {
  display: block;
  width: 100%;
  height: 100%;
  position: absolute;
}

.spellpre__goods--coner1 {
  left: -4rpx;
  top: 20rpx;
  width: 104rpx;
  height: 48rpx;
}

.spellpre__goods--coner2 {
  left: 20rpx;
  top: 0rpx;
  width: 96rpx;
  height: 52rpx;
}

.spellpre__goods--coner2 text {
  color: white;
  font-size: 24rpx;
  text-align: center;
  line-height: 48rpx;
}

.spellpre__goods--coner3 {
  left: 0;
  top: 0;
  width: 96rpx;
  height: 80rpx;
}

.spellpre__goods--delivery view {
  color: #fa6454;
  font-size: 24rpx;
  font-weight: 400;
  display: inline-block;
  background-color: rgba(250, 91, 74, 0.2);
  border-radius: 34rpx;
  padding: 4rpx 16rpx;
}

.spellpre__goods--footer .timebox {
  display: inline-block;
  height: 48rpx;
}

.spellpre__goods--footer .tip {
  display: inline-block;
  height: 48rpx;
  line-height: 48rpx;
  color: #8b8b8b;
  font-size: 26rpx;
  margin-right: 8rpx;
  margin-top: 2rpx;
}

.spellpre__goods .time_flex {
  width: 220rpx;
  height: 48rpx;
  display: inline-block;
  color: #2f2f2f;
  font-size: 18rpx;
  vertical-align: inherit;
}

.spellpre__goods .time_flex view {
  font-size: 26rpx;
  display: inline-block;
  width: 24rpx;
  height: 30rpx;
  line-height: 30rpx;
  text-align: center;
  color: #fff;
  background-color: #2f2f2f;
  border-radius: 6rpx;
  margin: 0rpx 2rpx;
  vertical-align: text-bottom;
}

.spellpre__goods .time-info {
  float: right;
  height: 48rpx;
  line-height: 48rpx;
  color: #a3a3a3;
  font-size: 26rpx;
  margin-left: 16rpx;
}

.spellpre__goods .user__icon {
  float: right;
  height: 48rpx;
  width: 120rpx;
  position: relative;
  margin-top: 2rpx;
}

.spellpre__goods .user__icon view {
  display: inline-block;
  width: 48rpx;
  height: 48rpx;
  border-radius: 50%;
  border: 2rpx solid #fff;
  background-color: #d1d1d1;
  top: 0;
  position: absolute;
}

.spellpre__goods .user__icon view image {
  width: 48rpx;
  height: 48rpx;
  border-radius: 50%;
}

.spellpre__goods .user__icon .icon_1 {
  right: 0;
  z-index: 1;
}

.spellpre__goods .user__icon .icon_2 {
  right: 24rpx;
  z-index: 2;
}

.spellpre__goods .user__icon .icon_3 {
  right: 48rpx;
  z-index: 3;
}

.spellpre__goodsL {
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
}

.spellpre__goodsL .spellpre__goods--box {
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  padding: 20rpx;
}

.spellpre__goodsL--content {
  display: -webkit-box;
  display: flex;
}

.spellpre__goodsL--box {
  display: -webkit-box;
  display: flex;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  overflow: hidden;
  position: relative;
}

.spellpre__goodsL--icon {
  width: 276rpx;
  height: 276rpx;
  position: relative;
}

.spellpre__goodsL--icon .ipic {
  display: inline-block;
  width: 276rpx;
  height: 276rpx;
  overflow: hidden;
  border-radius: 6rpx;
}

.spellpre__goodsL--icon .ipic image {
  min-height: 138rpx;
  width: 100%;
}

.spellpre__goodsL .spellpre__goods--cart {
  margin-top: 24rpx;
}

.spellpre__goodsL .spellpre__goods--coner1 {
  left: 20rpx;
  top: 40rpx;
}

.spellpre__goodsL .spellpre__goods--coner2 {
  left: 20rpx;
  top: 20rpx;
}

.spellpre__goodsL .spellpre__goods--coner3 {
  left: 20rpx;
  top: 20rpx;
}

.spellpre__goodsL--info {
  -webkit-box-flex: 1;
  flex: 1;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  margin-left: 20rpx;
  position: relative;
}

.spellpre__goodsL--name {
  text-overflow: -o-ellipsis-lastline;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  font-size: 30rpx;
  line-height: 40rpx;
  color: #0e0e0e;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  padding: 16rpx 0;
  max-height: 96rpx;
}

.spellpre__goodsL--buy {
  width: 100%;
  height: 130rpx;
  position: absolute;
  bottom: 0;
  left: 0;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  padding-top: 30rpx;
}

.spellpre__goodsL--firPrinum {
  font-size: 36rpx;
  color: #ec342c;
  font-weight: 600;
  display: -webkit-box;
  display: flex;
  flex-wrap: wrap;
  width: 104rpx;
}

.spellpre__goodsL--firLinePrinum {
  color: #aaaaaa;
  text-decoration: line-through;
  display: -webkit-box;
  display: flex;
  width: 100%;
  font-size: 28rpx;
  font-weight: normal;
  margin-top: 10rpx;
}

.spellpre__goodsL--prinum {
  display: inline-block;
  font-size: 44rpx;
  font-weight: 400;
  color: #ec342c;
  font-weight: 600;
  padding-top: 32rpx;
}

.spellpre__goodsL--linePrinum {
  font-size: 28rpx;
  color: #aaaaaa;
  display: inline-block;
  margin-left: 28rpx;
  height: 80rpx;
  line-height: 80rpx;
  text-decoration: line-through;
}

.spellpre__goodsL--guaid {
  font-size: 28rpx;
  color: #aaaaaa;
  text-decoration: line-through;
}

.spellpre__goodsL--footer {
  margin-top: 24rpx;
  height: 40rpx;
  line-height: 40rpx;
  position: relative;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
}

.spellpre__goodsL--footer .tip {
  vertical-align: middle;
}

.spellpre__goodsB {
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
}

.spellpre__goodsB--box {
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  overflow: hidden;
  position: relative;
}

.spellpre__goodsB--icon {
  width: 100%;
  position: relative;
}

.spellpre__goodsB--icon image {
  width: 100%;
  height: 350rpx;
}

.spellpre__goodsB .spellpre__goods--delivery {
  padding-top: 0rpx;
}

.spellpre__goodsB .detail__box {
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  padding: 0 16rpx;
}

.spellpre__goodsB--name {
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
  font-size: 30rpx;
  line-height: 62rpx;
  height: 62rpx;
  color: #0e0e0e;
}

.spellpre__goodsB--buy {
  width: 100%;
  height: 80rpx;
  line-height: 80rpx;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
}

.spellpre__goodsB--prinum {
  display: inline-block;
  font-size: 44rpx;
  font-weight: 500;
  color: #ec342c;
  height: 80rpx;
  line-height: 80rpx;
  font-weight: 600;
}

.spellpre__goodsB--guaid {
  font-size: 28rpx;
  color: #aaaaaa;
  display: inline-block;
  margin-left: 10rpx;
  height: 80rpx;
  line-height: 80rpx;
  text-decoration: line-through;
}

.spellpre__goodsB--info {
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  height: 70rpx;
  position: relative;
}

.page__goods-addPadding {
  padding: 0rpx 20rpx;
  height: calc(100% - 110rpx);
  overflow-y: scroll;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
}

.top__biggoods-page.left__Lgoods-style .spellpre__goodsL--icon,
.top__biggoods-page.left__Lgoods-style .ipic {
  width: 180rpx;
  height: 180rpx;
}

.top__biggoods-page.left__Lgoods-style .spellpre__goodsL--content {
  width: 100%;
  overflow: hidden;
}

.top__biggoods-page.left__Lgoods-style .spellpre__goodsL--info {
  width: calc(100% - 200rpx);
}

.top__biggoods-page.left__Lgoods-style .spellpre__goods--delivery {
  display: none;
}

.top__biggoods-page.left__Lgoods-style .spellpre__goodsL--name {
  padding: 8rpx 0;
  max-height: 44rpx;
  white-space: nowrap;
}
</style>
