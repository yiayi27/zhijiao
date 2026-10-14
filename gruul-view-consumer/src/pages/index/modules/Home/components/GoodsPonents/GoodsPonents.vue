<template>
  <view>
    <!-- 商品组件 -->
    <scroll-view
      v-if="formData.ponentType === 1 && formData.firstCatList && formData.firstCatList.length > 0"
      class="scroll-view_HPage"
      scroll-x="true"
    >
      <view
        v-for="(item, idx) in formData.firstCatList || []"
        :key="idx"
        :data-id="item.id"
        :data-mode="item.saleMode"
        :class="['tab__itam', currentCategoryId === item.id ? 'active' : '']"
        @tap.stop="activeClass"
      >
        <view v-if="formData.titleStyle === 2" class="text new__active-class">
          {{ item.name }}
        </view>
        <view v-if="formData.titleStyle === 1" class="text">{{ item.name }}</view>
        <text v-if="formData.titleStyle === 1" class="line" />
      </view>
    </scroll-view>
    <view
      v-if="
        (formData.ponentType === 1 && formData.firstCatList && formData.firstCatList.length > 0) || (formData.ponentType === 2 && goodsList.length)
      "
      :class="['goods', formData.listStyle]"
      :style="{ padding: `0 ${formData.pagePadding * 2}rpx` }"
    >
      <view
        v-for="(item, idx) in goodsList"
        :key="idx"
        :data-id="item.id"
        :data-img="item.img"
        :class="['goods-item', GoodsBoxClass]"
        :style="[idx === goodsList.length - 1 ? GoodsLastStyle : GoodsItemStyle]"
        @tap.stop="gotoDetail"
      >
        <view v-if="formData.showContent.tagShow" :class="['goods-item__coner ', GoodsCornerMark.class]">
          <image :src="GoodsCornerMark.url" mode="aspectFill" :lazy-load="true" />
        </view>
        <view class="goods-item__img">
          <view v-if="!item.inventory" class="no__goods">
            <text>已售罄</text>
            <text>Sold out</text>
          </view>
          <image :src="item.img" alt :lazy-load="true" mode="aspectFill" style="height: 100%" />
        </view>
        <view :class="['goods-item__foot', formData.listStyle !== 'goods-style--three' ? 'add__f-p' : '']">
          <view v-if="formData.showContent.nameShow" :class="['goods-item__name', GoodsNameClass]">
            {{ item.name }}
          </view>
          <view v-if="formData.showContent.priceShow || formData.showContent.buttonShow" class="goods-item__bottom">
            <view class="goods-item__price">
              <view v-if="formData.showContent.priceShow" class="price__one">
                {{ '￥' + item.price }}
                <text class="price__two">{{ '￥' + item.maxPrice }}</text>
              </view>
            </view>
            <view
              v-if="formData.showContent.buttonShow && formData.showContent.buttonStyle === 1"
              :class="['goods-item__cart', 'goods-item__cart1', ClassBugCard3]"
            >
              <image
                src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20200710/810700b67e264a7b82bba347528b9eb0.png"
                mode="aspectFit"
                :lazy-load="true"
              />
            </view>
            <view
              v-if="formData.showContent.buttonShow && formData.showContent.buttonStyle === 2"
              :class="['goods-item__cart', 'goods-item__cart2', ClassBugCard3]"
            >
              <image
                src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20200710/3de00f3d8099459c83771b1f4decb197.png"
                mode="aspectFit"
                :lazy-load="true"
              />
            </view>
            <view v-if="formData.showContent.buttonShow && formData.showContent.buttonStyle === 3" class="goods-item__cart goods-item__cart3">
              {{ formData.showContent.buttonText || '立即购买' }}
            </view>
            <view v-if="formData.showContent.buttonShow && formData.showContent.buttonStyle === 4" class="goods-item__cart goods-item__cart4">
              {{ formData.showContent.buttonText || '立即购买' }}
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, watch, nextTick } from 'vue'
import { discountList, getAllCategory } from '@/api/modules/goods'
import storage from '@/utils/storage'
import { isEqual } from 'lodash-es'
import { getEffectCategoryGoods } from '@/api/modules/goods'
import md5 from 'md5'

// 定义类型
interface IShowContent {
  /** 商品名称 */
  nameShow: boolean
  /** 商品价格 */
  priceShow: boolean
  /** 购物按钮 */
  buttonShow: boolean
  /** 购物按钮样式 */
  buttonStyle: number
  /** 购物车按钮文案 */
  buttonText: string
  /** 商品角标 */
  tagShow: boolean
  /** 商品角标样式 */
  tagStyle: number
}

interface Goods {
  /** 1展示分类  2不展示分类 */
  ponentType: number
  /** 商品 */
  goods: any[]
  /** 类目样式 1新  2下划线 */
  titleStyle: number
  /** 列表样式 */
  listStyle: string
  /** 页面边距 */
  pagePadding: number
  /** 商品间距 */
  goodPadding: number
  /** 商品样式 */
  goodsStyle: string
  /** 图片倒角 */
  angle: string
  /** 文本样式 */
  textStyle: string
  /** 显示内容 */
  showContent: IShowContent
  /** 商品对象 */
  goodsItem: any
  /** 商品数量 */
  goodsCount: number
  firstCatList?: Obj[]
}

interface IGoodsCornerMark {
  url: string
  class: string
}

interface IStyleObject {
  margin?: string
  width?: string
}

// 定义props
const props = defineProps({
  decoretionProperties: {
    type: Object,
    default: () => {},
  },
})

// 响应式变量
const formData = ref<Goods>({
  ponentType: 0,
  goods: [],
  titleStyle: 0,
  listStyle: '',
  pagePadding: 0,
  goodPadding: 0,
  goodsStyle: '',
  angle: '',
  textStyle: '',
  showContent: {
    nameShow: false,
    priceShow: false,
    buttonShow: false,
    buttonStyle: 0,
    buttonText: '',
    tagShow: false,
    tagStyle: 0,
  },
  goodsItem: null,
  goodsCount: 0,
})
const currentCategoryId = ref('-1')
const GoodsCornerMark = ref<IGoodsCornerMark>({
  url: '',
  class: '',
})
const goodsList = ref<any[]>([])
const GoodsNameClass = ref('')
const GoodsBoxClass = ref('')
const ClassBugCard3 = ref('')
const GoodsItemStyle = ref<IStyleObject>({})
const GoodsLastStyle = ref<IStyleObject>({})
const saleMode = ref('')

// 方法定义
const getGoodsNameClass = (fData: any) => {
  const { textStyle } = fData
  GoodsNameClass.value = textStyle
}

const getGoodsBoxClass = (fData: any) => {
  const { goodsStyle, angle } = fData
  GoodsBoxClass.value = `${goodsStyle} ${angle}`
}

const getClassBugCard3 = (fData: any) => {
  const {
    listStyle,
    showContent: { buttonStyle },
  } = fData
  let classBugCard3 = ''

  if (buttonStyle === 1 && listStyle === 'goods-style--four') {
    classBugCard3 = 'goods-buy__cart3'
  }
  if (buttonStyle === 2 && listStyle === 'goods-style--four') {
    classBugCard3 = 'goods-buy__cart3 buy-icon2'
  }

  ClassBugCard3.value = classBugCard3
}

const getGoodsItemStyle = (fData: any) => {
  const { listStyle, goodPadding } = fData
  let margin = ''
  let width = ''

  if (listStyle === 'goods-style--four') {
    margin = `0 ${goodPadding}px 0 0`
  } else {
    margin = `0 0 ${goodPadding}px 0`
  }
  if (listStyle === 'goods-style--two') {
    width = `calc(50% - ${goodPadding / 2}px)`
  } else {
    width = ''
  }
  GoodsItemStyle.value = {
    margin,
    width,
  }
}

const getGoodsLastStyle = (fData: any) => {
  const { listStyle, goodPadding = 0 } = fData
  let margin = ''
  if (listStyle === 'goods-style--two') {
    margin = `margin: 0 0 ${goodPadding}px 0;`
  } else {
    margin = ''
  }
  GoodsLastStyle.value = {
    margin,
  }
}

const goodsIds = (fData: any): string => {
  const list = Array.isArray(fData.goods) ? fData.goods : []
  return list.map((i: any) => i.id).join(',')
}

const saleModeIds = (fData: any): any[] => {
  const list = Array.isArray(fData.goods) ? fData.goods : []
  return Array.from(new Set(list.map((i: any) => i.saleMode)))
}

const dealGoodsSort = (obj: any, ids: any[] = []) => {
  const temp: any[] = []
  const goods = obj.goodsList
  ids.forEach((i) => {
    const e = goods.find((t: any) => t.id === i)
    if (e) {
      temp.push(e)
    }
  })
  goods.forEach((i: any) => {
    if (!ids.includes(i.id)) {
      temp.push(i)
    }
  })
  obj.goodsList = temp
}

const returnGoodsImg = (item: any): string => {
  const { widePic, pic } = item
  let url = pic
  if (formData.value.listStyle === 'goods-style--one') {
    url = widePic ? widePic.split(',')[0] : pic
  }
  return url
}

const getGoodsCornerMark = (fData = formData.value) => {
  const { tagStyle } = fData.showContent
  // 商品角标 1新品 2热卖 3抢购
  const srcs = [
    'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20201026/37a28e49acb448fc8618d5e72851b027.png',
    'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20201026/1622a28ef72040f9a2f367a2efa7c32d.png',
    'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20201026/0f33444422b14a8e980cc50d0011e2d0.png',
  ]

  const styles = ['goods-item__coner1', 'goods-item__coner2', 'goods-item__coner3']
  GoodsCornerMark.value = {
    url: srcs[+tagStyle - 1],
    class: styles[+tagStyle - 1],
  }
}

const dealGoogsPrice = (minPrice = 0) => {
  let price: any = Number(minPrice)
  if (isNaN(price)) {
    price = 0
  } else if (price >= 10000 && price < 100000000) {
    price = `${parseFloat((price / 10000).toFixed(2))}万`
  } else if (price > 100000000) {
    price = `${parseFloat((price / 100000000).toFixed(2))}亿`
  }
  return price
}

const gotoDetail = (e: any) => {
  const { id, img } = e.currentTarget.dataset
  uni.navigateTo({
    url: `/subcontract/pages/detail/detail?id=${id}`,
  })
}

const getAllList = async () => {
  const ids: any[] = []
  if (currentCategoryId.value === '-1') {
    formData.value.firstCatList?.forEach((i) => {
      if (i.id !== '-1') ids.push(i.id)
    })
  } else {
    ids.push(currentCategoryId.value)
  }
  const param = {
    saleMode: saleMode.value,
  }

  const res = await getEffectCategoryGoods(ids.join(','), param)
  const datas: any[] = []
  res.forEach((k: any) => {
    k.apiAliveProductVos.forEach((sd: any) => {
      if (!datas.find((t) => t.id === sd.id)) datas.push(sd)
    })
  })
  const goodsListValue: any[] = []
  goodsList.value = goodsListValue
  datas.forEach((item) => {
    goodsListValue.push({
      id: Number(item.id),
      name: item.name,
      img: returnGoodsImg(item),
      actPrice: item.maxPrice,
      price: dealGoogsPrice(item.minPrice),
      maxPrice: dealGoogsPrice(item.maxPrice),
      soldCount: item.sale,
      inventory: item.inventory,
      hasTimeTips: item.hasTimeTips,
    })
  })
  goodsList.value = goodsListValue.sort((a, b) => (a.name > b.name ? -1 : 1))
  const currentFormData = formData.value
  getGoodsCornerMark(currentFormData)
  getGoodsNameClass(currentFormData)
  getGoodsBoxClass(currentFormData)
  getClassBugCard3(currentFormData)
  getGoodsItemStyle(currentFormData)
  getGoodsLastStyle(currentFormData)
}

const activeClass = (e: any) => {
  const id = e.currentTarget.dataset.id
  const mode = e.currentTarget.dataset.mode
  currentCategoryId.value = id
  saleMode.value = mode
  getAllList()
}

const filterCategory = async (fData: any) => {
  try {
    const res = await getAllCategory({})
    const list = res || []
    const { firstCatList = [] } = fData
    const value: any[] = []
    list.forEach((i: any) => {
      i.productNumber = i.showCategoryVos.reduce((a: number, b: any) => {
        a += Number(b.productNumber)
        return a
      }, 0)
      if (i.productNumber > 50) i.productNumber = 50
    })
    firstCatList.forEach((i: any) => {
      const category = list.find((k: any) => k.id === i.id)
      if (category) {
        category.saleMode = i.saleMode
        value.push(category)
      }
    })

    fData.firstCatList = value
    currentCategoryId.value = value[0].id
    saleMode.value = value[0].saleMode
    formData.value = fData
    getAllList()
  } catch (e) {
    console.log('goods-ponents.vue--lin337', e)
  }
}

const discountListFunc = async (md5Str: string, storageData: any, fData: any) => {
  const ids: string = goodsIds(fData)
  const saleModeList = saleModeIds(fData)
  if (!ids) return
  let list: any[] = []
  const goodsListData: any[] = []
  await nextTick()
  for (let i = 0; i < saleModeList.length; i++) {
    const res = await discountList(ids, {
      saleMode: saleModeList[i],
      launchArea: '',
    })

    list = list.concat(res)
    if (i === saleModeList.length - 1) {
      list.forEach((item) => {
        goodsListData.push({
          id: item.id,
          img: returnGoodsImg(item),
          name: item.name,
          price: item.minPrice,
          maxPrice: item.maxPrice,
          inventory: item.inventory,
        })

        const obj = {
          goodsList: goodsListData,
        }

        dealGoodsSort(obj, ids.split(','))
        goodsList.value = obj.goodsList
      })
      if (list && !isEqual(list, storageData)) {
        storage.set(`discountList-${md5Str}`, list)
      }
    }
  }
}

// Watch props变化
watch(
  () => props.decoretionProperties,
  (newVal) => {
    if (newVal) {
      const propData = JSON.parse(JSON.stringify(newVal)) as any
      const md5Str = md5(propData)
      const storageData = storage.get(`discountList-${md5Str}`)
      if (propData.ponentType === 1) {
        filterCategory(propData)
      } else if (propData.ponentType === 2) {
        discountListFunc(md5Str, storageData, propData)
      }
      getGoodsCornerMark(propData)
      getGoodsNameClass(propData)
      getGoodsBoxClass(propData)
      getClassBugCard3(propData)
      getGoodsItemStyle(propData)
      getGoodsLastStyle(propData)

      formData.value = propData
    }
  },
  { deep: true, immediate: true },
)
</script>

<style lang="scss" scoped>
@include b(goods-item) {
  position: relative;
  background-color: #fff;

  @include when(circle) {
    border-radius: 12rpx;
    overflow: hidden;
  }

  @include when(shadow) {
    // box-shadow: 0 0 8rpx rgba($color: #000000, $alpha: 0.4);
    box-shadow: 0 1px 112rpx 12rpx rgba(109, 109, 109, 0.1);
  }

  @include when(border) {
    border: 1px solid #eeeeee;
  }

  @include e(img) {
    height: 360rpx;
    background-color: #eeeeee;
    overflow: hidden;
    position: relative;

    image {
      height: 360rpx;
      width: 100%;
    }
  }

  @include e(name) {
    overflow: hidden;
    text-overflow: ellipsis;
    padding-top: 10rpx;
    -webkit-line-clamp: 2;
    word-wrap: break-word;
    white-space: normal !important;
    -webkit-box-orient: vertical;
    display: -webkit-box;
    height: 100rpx;
    line-height: 100rpx;
    font-size: 32rpx;

    @include when(bold) {
      font-weight: 800;
    }
  }

  @include e(foot) {
    padding: 0 14rpx;
    position: relative;

    .is-bold {
      font-weight: 800;
    }
  }

  .add__f-p {
    padding-bottom: 30rpx;
  }

  @include e(bottom) {
    display: flex;
    justify-content: space-between;
    height: 70rpx;
    padding: 6rpx 0;
    // padding-top: 20rpx;
    margin-top: 20rpx;
  }

  @include e(price) {
    line-height: 74rpx;
    flex: 1;

    view {
      display: inline-block;
      line-height: 74rpx;
      height: 74rpx;
    }

    .price__one {
      font-size: 36rpx;
      color: #dd3c2b;
      font-weight: bold;
    }

    .price__two {
      text-decoration: line-through;
      color: #bbbbbb;
      font-size: 20rpx;
      margin-left: 16rpx;
    }
  }

  @include e(icon) {
    display: inline-block;
    width: 56rpx;
    height: 56rpx;
    line-height: 56rpx;
    text-align: center;
    background-color: red;
    border-radius: 100%;
    color: #ffffff;
  }

  @include e(cart1) {
    float: right;
    height: 60rpx;
    width: 60rpx;
    background-color: rgba(252, 98, 63, 1);
    box-sizing: border-box;
    border-radius: 50%;
    display: flex;
    justify-content: center;
    align-items: center;
    margin-top: 10rpx;
  }

  @include e(cart) {
    float: right;
    height: 60rpx;
    width: 60rpx;
    box-sizing: border-box;
    border-radius: 50%;
    display: flex;
    justify-content: center;
    align-items: center;
    margin-top: 34rpx;

    image {
      display: inline-block;
      width: 40rpx;
      height: 40rpx;
    }
  }

  @include e(cart1) {
    background-color: #fff;

    image {
      display: inline-block;
      width: 60rpx;
      height: 60rpx;
    }
  }

  @include e(cart2) {
    background: linear-gradient(164deg, rgba(243, 243, 243, 1), rgba(229, 56, 46, 1), rgba(253, 78, 38, 1));
    box-shadow: 0 4rpx 14rpx 0 rgba(255, 14, 0, 0.27);
    border-radius: 50%;
  }

  @include e(cart3) {
    border: 1px solid rgba(252, 98, 63, 1);
    width: auto;
    padding: 0 10rpx;
    color: rgba(252, 98, 63, 1);
    font-size: 24rpx;
    border-radius: 40rpx;
    height: 50rpx;
    line-height: 50rpx;
  }

  @include e(cart4) {
    border: 1px solid rgba(252, 98, 63, 1);
    background-color: rgba(252, 98, 63, 1);
    width: auto;
    padding: 0 10rpx;
    color: #fff;
    font-size: 24rpx;
    border-radius: 40rpx;
    height: 50rpx;
    line-height: 50rpx;
  }

  @include e(coner) {
    position: absolute;
    z-index: 12;

    image,
    text {
      display: block;
      width: 100%;
      height: 100%;
      position: absolute;
    }
  }

  @include e(coner1) {
    left: -1px;
    top: 12rpx;
    width: 76rpx;
    height: 44rpx;
    z-index: 7;
  }

  @include e(coner2) {
    left: 0;
    top: 0;
    width: 76rpx;
    height: 82rpx;
  }

  @include e(coner3) {
    left: 16rpx;
    top: 14rpx;
    width: 84rpx;
    height: 42rpx;
  }

  @include e(delivery) {
    padding-top: 4rpx;

    .i_box {
      border: 1px solid rgba(233, 56, 38, 1);
      border-radius: 34rpx;
      color: #e93826;
      font-size: 22rpx;
      display: inline-block;
      padding: 0 12rpx;
    }

    .base_info {
      padding: 1px 12rpx;
      background-color: #e93826;
      color: #fff;
      border-radius: 34rpx;
      display: none;
    }

    .hasTip {
      padding-left: 0;

      .base_info {
        display: inline-block;
        margin-right: 1px;
      }
    }
  }

  .goods-item__delivery3 {
    transform: scale(0.76);
    position: absolute;
    left: 4rpx;
    top: 100rpx;
  }

  .goods-buy__cart3 {
    width: 34rpx;
    height: 36rpx;
  }

  .buy-icon2 {
    margin-top: 16rpx;

    image {
      width: 38rpx;
      height: 38rpx;
    }
  }
}

@include b(goods-style) {
  @include m(two) {
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;

    @include b(goods-item) {
      width: 49%;
    }
  }

  @include m(three) {
    @include b(goods-item) {
      padding: 10rpx 0;
      display: flex;
      width: 100%;

      @include e(img) {
        height: 270rpx;
        width: 270rpx;
        margin-right: 20rpx;
        flex: none;
      }

      @include e(foot) {
        flex: 1;
        min-width: 0;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
      }

      @include e(name) {
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
  }

  @include m(four) {
    white-space: nowrap;
    overflow-x: auto;

    @include b(goods-item) {
      display: inline-block;
      width: 270rpx;

      @include e(img) {
        height: 270rpx;
        width: 270rpx;
        margin-right: 20rpx;
      }

      @include e(foot) {
      }

      @include e(name) {
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
  }
}

@include b(goods) {
  // background-color: #ffffff;
  box-sizing: border-box;
}

.no__goods {
  position: absolute;
  left: 50%;
  top: 50%;
  margin-left: -88rpx;
  margin-top: -88rpx;
  z-index: 11;
  width: 176rpx;
  height: 176rpx;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-direction: column;

  text {
    display: block;
    width: 176rpx;
    text-align: center;
    font-size: 25rpx;
  }
}

.goods-style--one {
  .goods-item__cart {
    margin-top: 5rpx;
  }
}

.goods-style--two {
  .goods-item__bottom {
    height: 70rpx;
  }

  .goods-item__cart {
    margin-top: 5rpx;
  }
}

.goods-style--three {
  .goods-item__cart {
    margin-top: 5rpx;
  }

  .goods-item__foot {
    position: relative;
  }

  .goods-item__delivery {
    position: absolute;
    left: 15rpx;
    top: 105rpx;
  }

  .goods-item__bottom {
    height: 86rpx;
    // padding-top: 6rpx;
  }

  .goods-item__name {
    padding-top: 18rpx;
    // line-height: 45rpx;
  }
}

.goods-style--four {
  .goods-item__bottom {
    height: 80rpx;
    margin-top: 50rpx;
  }

  .goods-item__cart {
    margin-top: 10rpx;
  }

  .goods-buy__cart3 {
    width: 60rpx;
    height: 60rpx;
  }
}

.scroll-view_HPage {
  width: 100%;
  height: 90rpx;
  background-color: #fff;
  margin-bottom: 16rpx;
  white-space: nowrap;

  .tab__itam {
    display: inline-block;
    position: relative;
    height: 80rpx;
    padding: 0rpx 22rpx;
    font-size: 32rpx;
    font-family: Hiragino Sans GB;
    font-weight: normal;
    color: #6b6b6b;
    padding-top: 12rpx;

    .line {
      display: none;
    }

    .text {
      // float: left;
      z-index: 8;
      white-space: nowrap;
    }
  }

  .active {
    font-size: 36rpx;
    font-family: Hiragino Sans GB;
    font-weight: normal;
    color: #000000;

    .new__active-class {
      z-index: 8;
      white-space: nowrap;
      display: inline-block;
      padding-bottom: 4rpx;
      border-bottom: 4rpx solid red;
      font-weight: bold;
    }

    .line {
      display: block;
      float: left;
      width: 100%;
      background: linear-gradient(164deg, #f3f3f3, #e5382e, #fd4e26);
      border-radius: 4rpx;
      margin-top: -20rpx;
      height: 18rpx;
    }
  }
}
</style>
