<template>
  <scroll-view :class="'top__biggoods-page ' + BigGoodsPageClass" scroll-x="false" scroll-y="true" @scrolltolower="onReachscrollBottom">
    <!-- 列表模式 -->
    <block v-if="formData.listStyle === 2">
      <view
        v-for="(item, index) in goodsList"
        :key="index"
        class="spellpre__goods spellpre__goodsL"
        :style="'padding: ' + (index === 0 ? '20rpx 0 ' + formData.goodsPadding + 'px 0' : formData.goodsPadding + 'px 0')"
      >
        <view :class="'spellpre__goods--box ' + GoodsBoxClass" :data-id="item.id" @tap.stop="gotoDetail">
          <view class="spellpre__goodsL--content">
            <view class="spellpre__goodsL--icon">
              <view v-if="!item.inventory" class="no__goods">
                <text>已售罄</text>
                <text>Sold out</text>
              </view>
              <view class="ipic">
                <image :src="item.img" mode="aspectFill" alt :lazy-load="true" />
                <!-- <image src="{{ item.img }}" mode="widthFix" alt=""></image> -->
              </view>
            </view>
            <view class="spellpre__goodsL--info">
              <view :class="'spellpre__goodsL--name ' + GoodsNameClass">{{ item.name }}</view>
              <view class="spellpre__goodsL--buy">
                <view class="spellpre__goodsL--prinum">
                  {{ '¥' + (item.actPrice !== '0.00' ? item.actPrice : item.price) }}
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
                <view class="spellpre__goodsL--guaid">{{ '¥' + item.price }}</view>
              </view>
            </view>
          </view>
          <view v-if="false" class="spellpre__goods--footer spellpre__goodsL--footer">
            <view class="timebox">
              <view class="tip">{{ item.preheatStatus === 1 ? '距开始' : '距结束' }}</view>
              <view class="time_flex">
                <view>{{ endList[index].list[0][0] }}</view>
                <view>{{ endList[index].list[0][1] }}</view>
                :
                <view>{{ endList[index].list[1][0] }}</view>
                <view>{{ endList[index].list[1][1] }}</view>
                :
                <view>{{ endList[index].list[2][0] }}</view>
                <view>{{ endList[index].list[2][1] }}</view>
              </view>
            </view>
            <view class="time-info">已售{{ item.soldCount }}/还剩{{ item.inventory }}</view>
            <view class="user__icon">
              <view v-for="(attr, idx) in userAttrs[index]" :key="idx" :class="'icon_' + (idx + 1)">
                <image v-if="attr" :src="attr" mode="aspectFit" />
              </view>
            </view>
          </view>
        </view>
      </view>
    </block>
    <!-- 大图模式 -->
    <block v-if="formData.listStyle === 1">
      <view
        v-for="(item, index) in goodsList"
        :key="index"
        class="spellpre__goods spellpre__goodsB"
        :style="'padding: ' + (index === 0 ? '20rpx 0 ' + formData.goodsPadding + 'px 0' : formData.goodsPadding + 'px 0')"
      >
        <view :class="'spellpre__goods--box ' + GoodsBoxClass" :data-id="item.id" @tap.stop="gotoDetail">
          <view class="spellpre__goodsB--icon">
            <view v-if="!item.inventory" class="no__goods">
              <text>已售罄</text>
              <text>Sold out</text>
            </view>
            <image :src="item.img" mode="aspectFill" alt :lazy-load="true" />
          </view>
          <view class="detail__box">
            <view :class="'spellpre__goodsB--name ' + GoodsNameClass">{{ item.name }}</view>
            <view class="spellpre__goodsB--buy">
              <view class="spellpre__goodsB--prinum"> ¥{{ item.actPrice !== '0.00' ? item.actPrice : item.price }} </view>
              <view class="spellpre__goodsB--guaid">¥{{ item.price }}</view>
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
            <view v-if="false" class="spellpre__goods--footer spellpre__goodsB--info">
              <view class="timebox">
                <view class="tip">{{ item.preheatStatus === 1 ? '距开始' : '距结束' }}</view>
                <view class="time_flex">
                  <view>{{ endList[index].list[0][0] }}</view>
                  <view>{{ endList[index].list[0][1] }}</view>
                  :
                  <view>{{ endList[index].list[1][0] }}</view>
                  <view>{{ endList[index].list[1][1] }}</view>
                  :
                  <view>{{ endList[index].list[2][0] }}</view>
                  <view>{{ endList[index].list[2][1] }}</view>
                </view>
              </view>
              <view class="time-info">已售{{ item.soldCount }}/还剩{{ item.inventory }}</view>
              <view class="user__icon">
                <view v-for="(attr, idx) in userAttrs[index]" :key="idx" :class="'icon_' + (idx + 1)">
                  <image v-if="attr" :src="attr" mode="aspectFit" />
                </view>
              </view>
            </view>
          </view>
        </view>
      </view>
    </block>
  </scroll-view>
</template>

<script setup lang="ts">
import { ref, watch, nextTick } from 'vue'
import { getGoodsList } from '@/api/modules/goods'

// 定义props
const props = defineProps<{
  propData: Obj
}>()

// 响应式变量
const formData = ref<Obj>({})
const GoodsNameClass = ref('')
const GoodsBoxClass = ref('')
const BigGoodsPageClass = ref('')
const goodsList = ref<any[]>([])
const endList = ref<any[]>([])
const userAttrs = ref<any[]>([])
const searchForm = ref({
  // 获取商品时搜索对象
  current: 1,
  name: '',
  showCategoryId: '',
  size: 10,
})
const hasMore = ref(true)
const isFirst = ref(true)

// 监听propData变化
watch(
  () => props.propData,
  () => {
    getFormData()
  },
  { deep: true, immediate: true },
)

// 获取表单数据
const getFormData = () => {
  if (props.propData) {
    formData.value = props.propData
    const showCategoryId = formData.value.currentFirstCategory && !isFirst.value ? formData.value.currentFirstCategory.category.id : ''
    searchForm.value = {
      current: 1,
      name: '',
      showCategoryId,
      size: 10,
    }
    hasMore.value = true
    goodsList.value = []
    endList.value = []
    userAttrs.value = []
    nextTick(() => {
      isFirst.value = false
      getInfo()
    })
  }
}

// 获取信息
const getInfo = () => {
  getAllList()
  getGoodsNameClass()
  getGoodsBoxClass()
  getBigGoodsPageClass()
}

// 滚动到底部
const onReachscrollBottom = () => {
  getInfo()
}

// 获取商品盒子样式类
const getGoodsBoxClass = () => {
  // const { goodsStyle = 1, doodsAngle = 1 } = this.data.formData;
  const goodsStyle = 1
  const doodsAngle = 2
  // 商品样式 1无底白边 2卡片投影 3描边白底
  const gstyles = ['spellpre__goods--boxWB', 'spellpre__goods--boxCP', 'spellpre__goods--boxSW']
  // 图片倒角 1直角 2圆角
  const astyles = ['spellpre__goods--corners', 'spellpre__goods--angle']
  const gs = gstyles[+goodsStyle - 1]
  const as = astyles[+doodsAngle - 1]
  GoodsBoxClass.value = `${gs} ${as}`
}

// 获取商品名称样式类
const getGoodsNameClass = () => {
  const { textStyle = 1 } = formData.value
  // 1常规体 2加粗体
  const styles = ['spellpre__goods--nameF', 'spellpre__goods--nameB']
  GoodsNameClass.value = styles[+textStyle - 1]
}

// 获取大商品页面样式类
const getBigGoodsPageClass = () => {
  // 1顶部模式 2侧边栏模式 1大图样式 2列表样式
  const { selectMode, listStyle } = formData.value
  const list: string[] = []
  if (selectMode === 1) {
    list.push('page__goods-addPadding')
  }
  if (selectMode === 2 && listStyle === 2) {
    list.push('left__Lgoods-style')
  }
  BigGoodsPageClass.value = `${list.join(' ')}`
}

// 获取所有列表
const getAllList = async () => {
  if (!hasMore.value) return
  const param = {
    saleMode: formData.value.saleMode,
  }
  const searchFormValue = {
    ...searchForm.value,
    ...param,
  }
  let list = []
  let res: any = {}
  res = await getGoodsList(param, searchFormValue)
  list = res.list
  // if (!this.data.searchForm.showCategoryId) {
  //   res = await getGoodsList(param, searchForm);
  //   list = res.list;
  // } else {
  //   res = await getEffectCategoryGoods(
  //     this.data.searchForm.showCategoryId,
  //     searchForm,
  //   );
  //   res.forEach(sd => {
  //     list.push(...sd.apiAliveProductVos);
  //   });
  // }
  const data: any = []
  list.forEach((item: any) => {
    item.hasTimeTips = item.goodTime && item.goodTime.includes(':')
    item.goodTime = item.hasTimeTips ? item.goodTime.substr(5) : item.goodTime
    data.push({
      id: Number(item.id),
      name: item.name,
      img: returnGoodsImg(item),
      actPrice: item.minPrice,
      price: item.maxPrice,
      soldCount: item.sale,
      inventory: item.inventory,
      hasTimeTips: item.hasTimeTips,
    })
  })
  const formDataValue = formData.value
  if (formDataValue.count > data.length) {
    formDataValue.count = data.length
  }
  goodsList.value = goodsList.value.concat(data)
  formData.value = formDataValue
  nextTick(() => {
    dealSearchParam(res)
  })
}

// 处理搜索参数
const dealSearchParam = (res: any) => {
  const hasMoreValue = goodsList.value.length < res.total
  const searchFormValue = searchForm.value
  if (hasMoreValue) {
    searchFormValue.current += 1
  }
  hasMore.value = hasMoreValue
  searchForm.value = searchFormValue
}

// 返回商品图片
const returnGoodsImg = (item: any) => {
  const { widePic, pic } = item
  let url = pic
  if (formData.value.listStyle === 1) {
    url = widePic ? widePic.split(',')[0] : pic
  }
  return url
}

// 跳转到详情
const gotoDetail = (e: any) => {
  const goodId = e.currentTarget.dataset.id
  uni.navigateTo({
    url: `/subcontract/pages/detail/detail?id=${goodId}`,
  })
}
</script>

<style lang="scss" scoped>
// 分类页
.top__biggoods-page {
  .spellpre__goods--box {
    padding: 0;
  }

  @include b(spellpage) {
    .el-radio {
      margin-right: 40rpx;
    }

    @include e(addmart) {
      margin-top: 14rpx;
    }
  }

  @include b(spellpre) {
    box-sizing: border-box;
    background-color: #f8f8f8;
    position: relative;

    @include e(header) {
      color: #333;
      height: 100rpx;
      line-height: 100rpx;
      background-color: #fff;
      width: 100%;
      position: absolute;
      left: 0;
      top: 0;
      z-index: 2;

      @include m(title) {
        font-size: 36rpx;
        font-weight: 700;
      }

      @include m(more) {
        font-size: 28rpx;
        font-weight: 400;
        float: right;
      }
    }

    @include e(headtemp) {
      height: 100rpx;
      line-height: 100rpx;
      width: 100%;
    }

    @include e(goods) {
      box-sizing: border-box;

      .spellpre__goods--box {
        position: relative;
        background-color: #fff;
        overflow: hidden;
      }

      @include m(boxCP) {
        box-shadow: 0 0 8rpx rgba(0, 0, 0, 0.2);
      }

      @include m(boxSW) {
        border: 1px solid #f8f8f8;
      }

      @include m(angle) {
        border-radius: 14rpx;
      }

      @include m(corners) {
        border-radius: 0;
      }

      @include m(nameF) {
        font-weight: 400;
      }

      @include m(nameB) {
        font-weight: 600;
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

      @include m(cart1) {
        float: right;
        height: 56rpx;
        width: 56rpx;
        background-color: rgba(252, 98, 63, 1);
        box-sizing: border-box;
        border-radius: 50%;
        display: flex;
        justify-content: center;
        align-items: center;
        margin-top: 10rpx;

        image {
          display: inline-block;
          width: 38rpx;
          height: 38rpx;
        }
      }

      @include m(cart) {
        float: right;
        height: 56rpx;
        width: 56rpx;
        box-sizing: border-box;
        border-radius: 50%;
        display: flex;
        justify-content: center;
        align-items: center;
        margin-top: 10rpx;
      }

      @include m(cart1) {
        background-color: #fff;

        image {
          display: inline-block;
          width: 60rpx;
          height: 60rpx;
        }
      }

      @include m(cart2) {
        background: linear-gradient(164deg, rgba(243, 243, 243, 1), rgba(229, 56, 46, 1), rgba(253, 78, 38, 1));
        box-shadow: 0 4rpx 14rpx 0 rgba(255, 14, 0, 0.27);
        border-radius: 50%;

        image {
          display: inline-block;
          width: 40rpx;
          height: 40rpx;
        }
      }

      @include m(cart3) {
        border: 1px solid rgba(252, 98, 63, 1);
        width: 160rpx;
        color: rgba(252, 98, 63, 1);
        font-size: 28rpx;
        border-radius: 40rpx;
        height: 60rpx;
        line-height: 60rpx;
      }

      @include m(cart4) {
        border: 1px solid rgba(252, 98, 63, 1);
        background-color: rgba(252, 98, 63, 1);
        width: 160rpx;
        color: #fff;
        font-size: 28rpx;
        border-radius: 40rpx;
        height: 60rpx;
        line-height: 60rpx;
      }

      @include m(coner) {
        position: absolute;

        image,
        text {
          display: block;
          width: 100%;
          height: 100%;
          position: absolute;
        }
      }

      @include m(coner1) {
        left: -4rpx;
        top: 20rpx;
        width: 104rpx;
        height: 48rpx;
      }

      @include m(coner2) {
        left: 20rpx;
        top: 0;
        width: 96rpx;
        height: 52rpx;

        text {
          color: white;
          font-size: 24rpx;
          text-align: center;
          line-height: 48rpx;
        }
      }

      @include m(coner3) {
        left: 0;
        top: 0;
        width: 96rpx;
        height: 80rpx;
      }

      @include m(delivery) {
        view {
          color: #fa6454;
          font-size: 24rpx;
          font-weight: 400;
          display: inline-block;
          background-color: rgba(250, 91, 74, 0.2);
          border-radius: 34rpx;
          padding: 4rpx 16rpx;
        }
      }

      @include m(footer) {
        .timebox {
          display: inline-block;
          height: 48rpx;
        }

        .tip {
          display: inline-block;
          height: 48rpx;
          line-height: 48rpx;
          color: #8b8b8b;
          font-size: 26rpx;
          margin-right: 8rpx;
          margin-top: 1px;
        }
      }

      .time_flex {
        width: 220rpx;
        height: 48rpx;
        display: inline-block;
        color: #2f2f2f;
        font-size: 18rpx;
        vertical-align: inherit;

        view {
          font-size: 26rpx;
          display: inline-block;
          width: 24rpx;
          height: 30rpx;
          line-height: 30rpx;
          text-align: center;
          color: #fff;
          background-color: #2f2f2f;
          border-radius: 6rpx;
          margin: 0 1px;
          vertical-align: text-bottom;
        }
      }

      .time-info {
        float: right;
        height: 48rpx;
        line-height: 48rpx;
        color: #a3a3a3;
        font-size: 26rpx;
        margin-left: 16rpx;
      }

      .user__icon {
        float: right;
        height: 48rpx;
        width: 120rpx;
        position: relative;
        margin-top: 1px;

        view {
          display: inline-block;
          width: 48rpx;
          height: 48rpx;
          border-radius: 50%;
          border: 1px solid rgba(255, 255, 255, 1);
          background-color: #d1d1d1;
          top: 0;
          position: absolute;

          image {
            width: 48rpx;
            height: 48rpx;
            border-radius: 50%;
          }
        }

        .icon_1 {
          right: 0;
          z-index: 1;
        }

        .icon_2 {
          right: 24rpx;
          z-index: 2;
        }

        .icon_3 {
          right: 48rpx;
          z-index: 3;
        }
      }
    }

    @include e(goodsL) {
      box-sizing: border-box;

      .spellpre__goods--box {
        box-sizing: border-box;
        padding: 20rpx;
      }

      @include m(content) {
        display: flex;
      }

      @include m(box) {
        display: flex;
        box-sizing: border-box;
        overflow: hidden;
        position: relative;
      }

      @include m(icon) {
        width: 276rpx;
        height: 276rpx;
        position: relative;

        .ipic {
          display: inline-block;
          width: 276rpx;
          height: 276rpx;
          overflow: hidden;
          border-radius: 6rpx;

          image {
            min-height: 138rpx;
            width: 100%;
          }
        }
      }

      .spellpre__goods--cart {
        margin-top: 24rpx;
      }

      .spellpre__goods--coner1 {
        left: 20rpx;
        top: 40rpx;
      }

      .spellpre__goods--coner2 {
        left: 20rpx;
        top: 20rpx;
      }

      .spellpre__goods--coner3 {
        left: 20rpx;
        top: 20rpx;
      }

      @include m(info) {
        flex: 1;
        box-sizing: border-box;
        margin-left: 20rpx;
        position: relative;
      }

      @include m(name) {
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
        box-sizing: border-box;
        padding: 16rpx 0;
        max-height: 96rpx;
      }

      @include m(buy) {
        width: 100%;
        height: 130rpx;
        position: absolute;
        bottom: 0;
        left: 0;
        box-sizing: border-box;
        padding-top: 40rpx;
      }

      @include m(prinum) {
        display: inline-block;
        font-size: 44rpx;
        font-weight: 400;
        color: #ec342c;
        font-weight: 600;
      }

      @include m(guaid) {
        font-size: 28rpx;
        color: #aaaaaa;
        text-decoration: line-through;
      }

      @include m(footer) {
        margin-top: 24rpx;
        height: 40rpx;
        line-height: 40rpx;
        position: relative;
        box-sizing: border-box;

        .tip {
          vertical-align: middle;
        }
      }
    }

    @include e(goodsB) {
      box-sizing: border-box;

      @include m(box) {
        box-sizing: border-box;
        overflow: hidden;
        position: relative;
      }

      @include m(icon) {
        width: 100%;
        position: relative;

        image {
          width: 100%;
          height: 350rpx;
        }
      }

      .spellpre__goods--delivery {
        padding-top: 0;
      }

      .detail__box {
        box-sizing: border-box;
        padding: 0 16rpx;
      }

      @include m(name) {
        text-overflow: ellipsis;
        overflow: hidden;
        white-space: nowrap;
        font-size: 30rpx;
        line-height: 62rpx;
        height: 62rpx;
        color: #0e0e0e;
      }

      @include m(buy) {
        width: 100%;
        height: 80rpx;
        line-height: 80rpx;
        box-sizing: border-box;
      }

      @include m(prinum) {
        display: inline-block;
        font-size: 44rpx;
        font-weight: 500;
        color: #ec342c;
        height: 80rpx;
        line-height: 80rpx;
        font-weight: 600;
      }

      @include m(guaid) {
        font-size: 28rpx;
        color: #aaaaaa;
        display: inline-block;
        margin-left: 10rpx;
        height: 80rpx;
        line-height: 80rpx;
        text-decoration: line-through;
      }

      @include m(info) {
        box-sizing: border-box;
        height: 70rpx;
        position: relative;
      }
    }
  }
}

.page__goods-addPadding {
  padding: 0 20rpx;
  height: calc(100% - 110rpx);
  overflow-y: scroll;
  box-sizing: border-box;
}

.top__biggoods-page.left__Lgoods-style {
  .spellpre__goodsL--icon,
  .ipic {
    width: 180rpx;
    height: 180rpx;
  }

  .spellpre__goodsL--content {
    width: 100%;
    overflow-x: hidden;
  }

  .spellpre__goodsL--info {
    width: calc(100% - 200rpx);
  }

  .spellpre__goods--delivery {
    display: none;
  }

  .spellpre__goodsL--name {
    padding: 8rpx 0;
    max-height: 44rpx;
    white-space: nowrap;
  }
}
</style>
