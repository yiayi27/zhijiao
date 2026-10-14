<template>
  <view class="second__cat-page">
    <view class="header__box">
      <view class="search__box">
        <search-plugin :chanel="chanel" style="flex: 1" :queryName="queryName" />
        <view class="icon__box" @tap="changeStatus">
          <image
            class="icon__image"
            src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20200626/26bffe66093f4086b501a2fce01e9388.png"
            alt
          />
          <view class="change__item">{{ secondCatGoodsStyle === 1 ? '矩形' : '列表' }}</view>
        </view>
      </view>
    </view>
    <view class="tab__con">
      <view v-for="(i, d) in tabList" :key="d" :class="'tab__item ' + (activeTab === d + 1 ? 'active__tab' : '')" @click="changeActiveTab(d + 1)">
        <view>{{ i }}</view>
        <view v-if="d === 3" class="price__icon">
          <uv-icon size="12" :class="'price__icon_up '" :color="isPrice === 'asc' ? '#FC425A' : '#000'" name="arrow-up" />
          <uv-icon size="12" :class="'price__icon_down '" :color="isPrice === 'desc' ? '#FC425A' : '#000'" name="arrow-down" />
        </view>
      </view>
    </view>
    <view v-if="goodsList.length" class="list__page">
      <second-goodslist v-if="secondCatGoodsStyle === 1" :goodsList="goodsList" :cartButton="cartButton" @loadMored="loadMored" />
      <second-goodsdetail v-if="secondCatGoodsStyle === 2" :goodsList="goodsList" :cartButton="cartButton" @loadMored="loadMored" />
    </view>
    <view v-if="!goodsList.length" class="shopNoCar">
      <image src="https://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/api/noCar.png" class="shop__noCar" />
      <view class="shop__noCar--text">暂无商品~</view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getGoodsList, searchGoodsList as searchGoodsListApi, getCouponGoodsList } from '@/api/modules/goods'
import SecondGoodslist from '../components/second-goodslist/second-goodslist.vue'
import SecondGoodsdetail from '../components/second-goodsdetail/second-goodsdetail.vue'
import SearchPlugin from '../components/search-plugin/search-plugin.vue'

// 响应式变量
const activeTab = ref(1)
const tabList = ref(['综合', '销量', '新品', '价格'])
const secondCatGoodsStyle = ref(1)
const showCategoryId = ref('')
const cartButton = ref(3)
const searchForm = ref({
  // 获取商品时搜索对象
  current: 1,
  size: 10,
})
const hasMore = ref(true)
const goodsList = ref<any[]>([])
const chanel = ref(0)
const queryName = ref('')
const isPrice = ref('')
const couponId = ref('')

// 初始化数据
const initData = () => {
  if (chanel.value === 2 || chanel.value === 12) {
    // 2商超分类页 12商超分类页二级分类导航
    getGooodsList()
  } else if (chanel.value === 4) {
    // 4首页搜索组件
    searchGoodsList()
  } else if (couponId.value) {
    // 优惠券商品
    searchCouponGoodsList()
  }
}

// 切换显示模式
const changeStatus = () => {
  secondCatGoodsStyle.value = secondCatGoodsStyle.value === 1 ? 2 : 1
}

// 切换活动标签
const changeActiveTab = (activeTabValue: number) => {
  let isPriceValue = isPrice.value
  if (activeTabValue === 4) isPriceValue = isPrice.value === 'asc' ? 'desc' : 'asc'
  else isPriceValue = ''
  if (activeTabValue === 3) isPriceValue = 'desc'
  goodsList.value = []
  hasMore.value = true
  searchForm.value = {
    // 获取商品时搜索对象
    current: 1,
    size: 10,
  }
  activeTab.value = activeTabValue
  isPrice.value = isPriceValue
  nextTick(() => {
    initData()
  })
}

// 加载更多
const loadMored = () => {
  initData()
}

// 获取商品列表
const getGooodsList = () => {
  if (!hasMore.value) return
  const searchFormValue = {
    ...searchForm.value,
    type: activeTab.value,
    name: queryName.value,
    showCategoryId: showCategoryId.value,
    sort: isPrice.value,
  }
  getGoodsList(searchFormValue, {}).then((res: any) => {
    const goodsListValue: any = []
    res.list.forEach((item: any) => {
      goodsListValue.push({
        id: Number(item.id),
        name: item.name,
        img: returnGoodsImg(item),
        actPrice: item.minPrice,
        price: item.maxPrice,
        soldCount: item.sale,
        inventory: item.inventory,
        deliveryTime: item.goodTime,
        hasTimeTips: item.goodTime && item.goodTime.includes(':'),
      })
    })
    goodsList.value = goodsList.value.concat(goodsListValue)
    nextTick(() => {
      dealSearchParam(res)
    })
  })
}

// 搜索商品列表
const searchGoodsList = () => {
  if (!hasMore.value) return
  const searchFormValue = {
    ...searchForm.value,
    type: activeTab.value,
    name: queryName.value,
    sort: isPrice.value,
  }
  searchGoodsListApi(searchFormValue).then((res: any) => {
    const goodsListValue: any = []
    res.list.forEach((item: any) => {
      goodsListValue.push({
        id: Number(item.id),
        name: item.name,
        img: returnGoodsImg(item),
        actPrice: item.minPrice,
        price: item.maxPrice,
        soldCount: item.sale,
        inventory: item.inventory,
        deliveryTime: item.goodTime,
        hasTimeTips: item.goodTime && item.goodTime.includes(':'),
      })
    })
    goodsList.value = goodsList.value.concat(goodsListValue)
    nextTick(() => {
      dealSearchParam(res)
    })
  })
}

// 搜索优惠券商品列表
const searchCouponGoodsList = () => {
  if (!hasMore.value) return
  const searchFormValue = {
    ...searchForm.value,
    type: activeTab.value,
    keyword: queryName.value,
    sort: isPrice.value ? isPrice.value : 'asc',
  }
  getCouponGoodsList(couponId.value, searchFormValue).then((res: any) => {
    const goodsListValue: any = []
    res.list.forEach((item: any) => {
      goodsListValue.push({
        id: Number(item.id),
        name: item.name,
        img: returnGoodsImg(item),
        actPrice: item.minPrice,
        price: item.maxPrice,
        soldCount: item.sale,
        inventory: item.inventory,
        deliveryTime: item.goodTime,
        hasTimeTips: item.goodTime && item.goodTime.includes(':'),
      })
    })
    goodsList.value = goodsList.value.concat(goodsListValue)
    nextTick(() => {
      dealSearchParam(res)
    })
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
  if (secondCatGoodsStyle.value === 2) {
    url = widePic ? widePic.split(',')[0] : pic
  }
  return url
}

// 生命周期
onLoad((option: any = {}) => {
  if (option && option.couponId) {
    couponId.value = option.couponId
  }
  chanel.value = Number(option?.chanel || 0)
  showCategoryId.value = option?.categoryId || ''
  cartButton.value = option?.cartButton || 0
  queryName.value = option?.queryName || ''
  nextTick(() => {
    initData()
  })
})
</script>

<style lang="scss" scoped>
.second__cat-page {
  min-height: 100vh;
  background-color: #f2f2f2;

  .header__box {
    box-sizing: border-box;

    .search__box {
      height: 120rpx;
      display: flex;
      padding: 0 20rpx;
      background-color: #fff;

      .input__iocn {
        flex: 1;
        height: 60rpx;
        background-color: rgba(228, 228, 228, 1);
        border-radius: 40rpx;
        margin-top: 6rpx;
        cursor: pointer;
      }

      .icon__box {
        width: 100rpx;
        height: 100%;
        position: relative;
        cursor: pointer;
        padding-top: 4rpx;

        .icon__image {
          float: right;
          width: 50rpx;
          height: 50rpx;
          margin-right: 10rpx;
        }

        .change__item {
          width: 120rpx;
          line-height: 36rpx;
          height: 36rpx;
          text-align: right;
          position: absolute;
          top: 60rpx;
          right: 8rpx;
          font-size: 24rpx;
        }
      }
    }
  }

  .tab__con {
    display: flex;
    background-color: #fff;

    .tab__item {
      flex: 1;
      @include flex();
      flex-wrap: nowrap;
      text-align: center;
      font-size: 28rpx;
      cursor: pointer;
      height: 80rpx;
      align-items: center;
    }

    .active__tab {
      color: #fc425a;
    }
  }

  .list__page {
    height: calc(100% - 160rpx);
  }
}

.price__icon {
  width: 50rpx;
  position: relative;
  margin-left: 10rpx;
  display: flex;
  flex-direction: column;

  .price__icon_up,
  .price__icon_down {
    font-size: 22rpx;
    color: #ccc;
    transform: translateY(1.25px);
  }

  .active__icon {
    color: #fc425a;
  }
}

.shopNoCar {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  width: 100%;
  height: calc(100vh - 200rpx);
  background-color: #fff;
  position: relative;
}

.shop__noCar {
  margin-top: 140rpx;
  width: 69%;
}

.shop__noCar--text {
  display: block;
  justify-content: center;
  color: #acacac;
  width: 100%;
  text-align: center;
  position: absolute;
  top: 640rpx;
}
</style>
