<template>
  <view>
    <view class="shop__car--top" :style="{ top: navigationBarHeight }">
      <view
        class="shop__car--top--container"
        :style="'border-radius:0;justify-content:' + (shopCarList.length > 0 || shopLoseCarList.length > 0) ? 'space-between' : 'center'"
      >
        <view v-if="shopCarList.length > 0 || shopLoseCarList.length > 0" style="font-size: 28rpx" @tap="getCoupon"> 领券 </view>
        <view v-if="shopCarList.length > 0 || shopLoseCarList.length > 0" style="font-size: 28rpx">
          <view v-if="!editFlag" @tap="eidtList">编辑</view>
          <view v-else @tap="eidtFinish">完成</view>
        </view>
      </view>
    </view>
    <Pop v-if="popType === 'coupon'" v-model:showType="showType" :couponList="couponList" @myCouponGet="myCouponGet" />
    <Sku
      v-else
      v-model:showType="showType"
      v-model:goodDetail="goodDetail"
      :couponList="couponList"
      :norms="norms"
      @addShopCar="addShopCar"
      @checkNorms="checkNorms"
    />
    <view v-if="shopCarList.length > 0 || shopLoseCarList.length > 0" class="shop__car">
      <view class="shop__car--goods">
        <view
          v-for="(good, goodIndex) in shopCarList"
          :key="goodIndex"
          class="shop__car--container"
          style="height: 280rpx; box-shadow: 0 0 12rpx #ebebeb"
        >
          <view v-if="good.status === 1" class="shop__car--container--item">
            <CusRadio :value="good.isCheck" :index="goodIndex" style="margin-right: 20rpx" @change="onChange" />
            <!-- 加入购物车商品 -->
            <Goods :good="good" :index="goodIndex" :checkStock="good.checkStock" @getSku="getSku">
              <Stepper
                v-if="good.status === 1"
                v-model:maxNum="good.maxNum"
                v-model:num="good.goodsNumber"
                :index="goodIndex"
                :good="good"
                @changeNum="changeNum"
                @delGood="delGood"
              />
            </Goods>
          </view>
        </view>
      </view>
      <!-- 购物车失效商品 -->
      <Unusable :shopLoseCarList="shopLoseCarList" @emptyUseless="emptyUseless" />
      <view class="pageBottom">
        <view class="pageBottom__check">
          <uv-checkbox-group v-model="selectAll">
            <uv-checkbox
              shape="circle"
              activeColor="#FF4444"
              style="font-size: 26rpx"
              :disabled="shopCarList.length === 0 && shopLoseCarList.length > 0 ? true : false"
              :name="true"
              label="全选"
              @change="chooseAll"
            />
          </uv-checkbox-group>
          <view v-if="!editFlag" view class="pageBottom__check--allPrice">
            <span style="font-size: 30rpx">合计:</span>
            <span class="pageBottom__check--allPrice--price">￥{{ allPrice }}</span>
          </view>
        </view>
        <view v-if="!editFlag" class="pageBottom__button" @tap="submit"> 结算({{ selectList.length }}) </view>
        <view v-else style="display: flex">
          <view class="moveCollect" @tap="addCollect">移入收藏夹</view>
          <view class="pageBottom__delBtn" @tap="deleteButton">删除</view>
        </view>
      </view>
      <!-- 弹窗显示优惠券或规格 -->
    </view>
    <view
      v-else
      class="shopNoCar_container"
      :style="{
        height: `calc(${screenHeight}px - 96rpx)`,
      }"
    >
      <view class="shopNoCar">
        <image src="https://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/api/noCar.png" class="shop__noCar" />
        <view class="shop__noCar--text">你的购物车空空如也</view>
        <view class="shop__noCar--buyButton" @tap="gotoHome">去下单</view>
      </view>
    </view>
  </view>
</template>
<script setup lang="ts">
import { ref, onMounted, nextTick, onUnmounted } from 'vue'
import { getCarsList, delCarsList, editCarsList, emptyCarsList, changeType } from '@/api/modules/shopCar'
import { userAddCollect } from '@/api/modules/goods'
import { getOneCoupon, getCouponIndex } from '@/api/modules/coupon'
import { getMemberCardInfo } from '@/api/modules/member'
import Goods from './components/Goods/Goods.vue'
import Unusable from './components/Unusable/Unusable.vue'
import Pop from './components/Pop/Pop.vue'
import Sku from './components/Pop/Sku/Sku.vue'
import Stepper from '@/components/Stepper/Stepper.vue'
import CusRadio from '@/components/CusRadio/CusRadio.vue'
import { useSettingStore } from '@/store/modules/setting'
import { useUserStore } from '@/store/modules/user'
import { $Popup } from '@/utils/helper'
import useScreenHeight from '@/composables/hooks/useScreenHeight'
import type { ShopCarItem, SkuStock, GoodDetail } from './types'
import { debounce } from '@/utils'
import { LOGIN_SUCCESS } from '@/utils/emitNameConfig'

const screenHeight = useScreenHeight()

// 定义Props
defineProps({
  navigationBarHeight: {
    type: String,
    default: '',
  },
})

// 定义emit
const emit = defineEmits(['shopCarChange'])

// 响应式数据
const shopCarList = ref<ShopCarItem[]>([])
const temShopList = ref<ShopCarItem[]>([])
const shopLoseCarList = ref<any[]>([])
const editFlag = ref(false)
const selectAll = ref<boolean[]>([])

const selectList = ref<any[]>([])
const showType = ref(false)
const popType = ref('coupon')
const couponList = ref<any[]>([])
const goodDetail = ref<GoodDetail>({} as GoodDetail)
const norms = ref<any>({})
const allPrice = ref('0.00')
const isAddCollect = ref(false)
const canClick = ref(false)

const handleLoginSuccess = () => {
  getList(false)
}
// 生命周期
onMounted(() => {
  getList(false)
  uni.setNavigationBarTitle({
    title: '购物车',
  })
  uni.$on(LOGIN_SUCCESS, handleLoginSuccess)
})

onUnmounted(() => {
  uni.$off(LOGIN_SUCCESS, handleLoginSuccess)
})

// 方法
const addCollect = () => {
  const selectedItems = getSelectList()
  if (selectedItems.length === 0) {
    uni.showToast({
      title: '请选择要加入商品',
      icon: 'none',
    })
    return
  }
  const list: any[] = []
  selectedItems.forEach((item) => {
    item.skuStocks.forEach((v: SkuStock) => {
      if (v.id === item.skuId) {
        item.originalPrice = v.originalPrice
      }
    })
    list.push({
      originalPrice: item.originalPrice,
      productId: item.productId,
      productName: item.productName,
      productPic: item.pic,
      productPrice: item.actPrice,
      status: item.status,
    })
  })
  userAddCollect(list)
    .then(() => {
      uni.showToast({
        title: '收藏成功',
        icon: 'none',
      })
      isAddCollect.value = true
      deleteButton()
    })
    .catch((err) => {
      console.log(err)
    })
}

const submit = () => {
  const items = shopCarList.value
    .filter((item) => item.isCheck)
    .map((item) => {
      return {
        activityId: item.activityId,
        activityProductId: item.activityProductId,
        dominoState: item.dominoState,
        number: item.goodsNumber,
        skuId: item.skuId,
      }
    })
  if (!items.length) {
    uni.showToast({
      title: '请选择商品',
      icon: 'none',
    })
    return
  }
  uni.navigateTo({
    url: `/modules/pages/submit/submit?items=${encodeURIComponent(JSON.stringify(items))}`,
  })
}

const getList = async (isEmit = true) => {
  if (isEmit) {
    emit('shopCarChange')
  }
  let selectAllValue = false
  let res = await getCarsList()
  const useList: any[] = []
  const noList: any[] = []
  if (!res) {
    shopCarList.value = useList
    temShopList.value = JSON.parse(JSON.stringify(useList))
    shopLoseCarList.value = noList
    allPrice.value = '0.00'
    selectAll.value = []
    return
  }
  res.forEach((item: any, index: number) => {
    item.isCheck = false
    item.itemIndex = index
    item.skuList = []
    item = dealPrice(item)
    const type = item.skuStocks.every((v: any) => {
      return v.stock === 0
    })
    item.status = type ? 2 : item.status
    if (item.deleted === 1) {
      item.status = 2
    }
    item.skuStocks.forEach((v: any, i: number) => {
      item.skuList.push(v.id)
      v.price = v.price === 0 ? v.originalPrice.toFixed(2) : v.price.toFixed(2)
      v.pointId = i
      if (item.skuId === v.id) {
        item.price = v.price
        item.perLimit = v.perLimit
        item.pointId = v.pointId
        item.checkStock = v.stock
        item.pic = item.skuStocks.length > 1 ? v.pic : item.pic
      }
    })
    item.status = item.skuList.indexOf(item.skuId) === -1 ? 0 : item.status
    if (item.activityEndTime) {
      item.status = item.activityStatus === '1' || item.activityStatus === '0' ? 1 : 0
    }
    if (item.status === 1) {
      useList.push(item)
      if (useList.length > 0) {
        useList.forEach((item) => {
          item.isCheck = Boolean(item.selectStatus)
        })
        selectAllValue = useList.every((item) => {
          return item.isCheck === true
        })
      }
    } else {
      noList.push(item)
    }
  })
  shopCarList.value = useList
  temShopList.value = JSON.parse(JSON.stringify(useList))
  shopLoseCarList.value = noList
  allPrice.value = '0.00'
  selectAll.value = [selectAllValue]
  dealSelct()
  getAllPrice()
}

const dealSelct = () => {
  const selectedItems = getSelectList()
  selectList.value = selectedItems
}

const dealPrice = (item: any) => {
  item.skuStocks.forEach((v: any, i: number) => {
    if (v.id === item.skuId) {
      // item.actPrice = item.skuStocks[i].price;
      item.actPrice = item.skuStocks[i].price === 0 ? item.skuStocks[i].originalPrice : item.skuStocks[i].price
      item.skuName = item.skuStocks[i].specs
    }
  })
  return item
}

const onChange = (e: any) => {
  if (canClick.value) {
    // 加载过程中阻止用户再次点击
    return
  }
  const id: any[] = []
  const currentIndex = e.index
  const currentShopCarList = shopCarList.value
  const selectedItems: any[] = []
  const currentItem = currentShopCarList[currentIndex]
  currentItem.isCheck = !currentItem.isCheck
  id.push(currentItem.skuId)
  const selectStatus = currentItem.selectStatus
  canClick.value = true
  uni.showLoading({
    title: '加载中',
  })
  changeType(selectStatus === 1 ? 0 : 1, id).then(() => {
    setTimeout(() => {
      uni.hideLoading()
      canClick.value = false
    }, 100)
  })
  const selectAllValue = currentShopCarList.every((item) => item.isCheck)
  currentShopCarList.forEach((item) => {
    if (item.isCheck) {
      selectedItems.push(item)
    }
  })
  shopCarList.value[currentIndex].isCheck = currentItem.isCheck
  selectAll.value = [selectAllValue]
  selectList.value = selectedItems
  getAllPrice()
}

const getCoupon = async () => {
  const couponListValue: any[] = []
  const res = await getCouponIndex({})
  res.forEach((item: any) => {
    if (item.available > 0 && item.totalAvailable > 0) {
      couponListValue.push(item)
    }
  })
  couponListValue.forEach((item: any, index: number) => {
    item.hasUse = false
    item.index = index
    item.restNum = item.available > item.totalAvailable ? item.totalAvailable : item.available
  })
  if (couponListValue.length === 0) {
    uni.showToast({
      title: '暂无可领取优惠券',
      icon: 'none',
    })
    return
  }
  popType.value = 'coupon'
  couponList.value = couponListValue
  await nextTick()
  showType.value = true
}

const getSku = async (e: any) => {
  const userInfo = useUserStore().userInfo
  const userMemberInfo = userInfo.infoExtends.memberInfo[0]
  // 用户会员状态 0未注册 1已注册 2会员到期
  // const state = userMemberInfo.state;
  const memRank = userMemberInfo.rankCode
  // 会员持有等级
  const memberLevel = userMemberInfo.rankCode
  const id = e.skuId
  let normsValue: any = {}

  // 查找商品并确保它是GoodDetail类型
  shopCarList.value.forEach((item) => {
    if (item.skuId === String(id)) {
      // 确保item包含必要的属性
      if (!item.maxNum && item.checkStock) {
        item.maxNum = item.checkStock
      }
      goodDetail.value = item as GoodDetail
    }
  })

  const res = await getMemberCardInfo({})
  const integralVos = res.consumeVos || res.integralVos || res.paymentVos || []
  if (goodDetail.value && goodDetail.value.skuStocks) {
    goodDetail.value.skuStocks.forEach((skuItem: any) => {
      skuItem.getType = false
      skuItem.maxNum = skuItem.perLimit < skuItem.stock ? skuItem.perLimit : skuItem.stock
      if (skuItem.memberType !== 0 && skuItem.memberType !== 3 && skuItem.memberPrices && skuItem.memberPrices.length > 0) {
        skuItem.memberPrices.forEach((memItem: any) => {
          if (memItem.memberLevelId === memberLevel) {
            skuItem.memPrice = skuItem.memberType === 1 ? memItem.memberPrice : (skuItem.price * memItem.memberPrice) / 100
            skuItem.memPrice = typeof skuItem.memPrice === 'number' ? skuItem.memPrice.toFixed(2) : skuItem.memPrice
          }
        })
      }
      if (skuItem.memberType === 3 && integralVos) {
        integralVos.forEach((memItem: any) => {
          if (memItem.rankCode === memRank) {
            const value = memItem.memberPrivilegeVos.filter((inItem: any) => {
              if (inItem.isSelected === 1 && inItem.ptype === '1') {
                return inItem.value
              }
              return false
            })
            if (value.length > 0) {
              skuItem.memPrice = (skuItem.price * value[0].value) / 100
              skuItem.memPrice = typeof skuItem.memPrice === 'number' ? skuItem.memPrice.toFixed(2) : skuItem.memPrice
            }
          }
        })
      }
      if (skuItem.id === String(id)) {
        skuItem.getType = true
        normsValue = skuItem
        if (goodDetail.value.actPrice !== '0.00') {
          const actPrice = typeof goodDetail.value.actPrice === 'number' ? goodDetail.value.actPrice.toFixed(2) : goodDetail.value.actPrice
          const price = typeof goodDetail.value.price === 'number' ? goodDetail.value.price.toFixed(2) : goodDetail.value.price
          normsValue.price = goodDetail.value.actPrice ? actPrice : price
        } else {
          normsValue.price = goodDetail.value.price
        }
      }
    })
  }
  popType.value = 'sku'
  norms.value = normsValue
  await nextTick()
  showType.value = true
}

const checkNorms = (check: any) => {
  if (goodDetail.value?.skuStocks) {
    goodDetail.value.skuStocks.forEach((item: any) => {
      item.getType = false
      if (item.id === check.id) {
        item.getType = true
      }
    })
  }
  norms.value = check
}

const delGood = async (itemIndex: number) => {
  const apiShoppingCartDtos: any[] = []
  apiShoppingCartDtos.push(shopCarList.value[itemIndex])
  $Popup
    .modal({
      title: '删除商品',
      content: '确认删除此商品吗，删除后可再次添加购物车',
    })
    .then(async () => {
      await delCarsList(apiShoppingCartDtos)
      uni.showToast({
        title: '删除成功',
        icon: 'none',
      })
      getList()
    })
}

const eidtList = () => {
  editFlag.value = !editFlag.value
}

const handleChangeNum = (e: any) => {
  const currentIndex = e.index
  const currentItem = shopCarList.value[currentIndex]
  const oldItem = temShopList.value[currentIndex]
  editShopCar(currentItem, oldItem, 0)
  if (currentItem.isCheck) {
    getAllPrice()
  }
}

const changeNum = debounce(handleChangeNum, 500)

const addShopCar = (e: any) => {
  const currentItem = e.goodDetail
  let oldItem = {}
  shopCarList.value.forEach((item, index) => {
    if (item.itemIndex === currentItem.itemIndex) {
      oldItem = temShopList.value[index]
    }
  })
  editShopCar(currentItem, oldItem, 1)
}

const editShopCar = async (newItem: any, oldItem: any, type: number) => {
  const params = {
    oldApiShoppingCartDto: {
      ...oldItem,
    },
    newApiShoppingCartDto: {
      ...newItem,
    },
  }
  try {
    await editCarsList(params)
    showType.value = false
    emit('shopCarChange')
    if (type) {
      getList()
    }
  } catch (error) {
    uni.showToast({
      icon: 'none',
      title: `${error}`,
    })
  }
}

const emptyUseless = async () => {
  const ids: any[] = []
  shopLoseCarList.value.forEach((item: any) => {
    ids.push(item.skuId)
  })
  $Popup
    .modal({
      title: '清空商品',
      content: '确认清空失效商品吗',
    })
    .then(async () => {
      try {
        await emptyCarsList(ids)
        uni.showToast({
          icon: 'none',
          title: '清空成功',
        })
        getList()
      } catch (error) {
        uni.showToast({
          icon: 'none',
          title: `${error}`,
        })
      }
    })
}

const eidtFinish = () => {
  editFlag.value = false
}

const chooseAll = (n: boolean) => {
  selectList.value = []
  if (shopCarList.value.length === 0) {
    return
  }
  shopCarList.value.forEach((item) => {
    item.isCheck = n
    if (item.isCheck) {
      selectList.value.push(item)
    }
  })
  selectAll.value = [n]
  getAllPrice()
}

const deleteButton = async () => {
  const selectedItems = getSelectList()
  if (selectedItems.length === 0) {
    uni.showToast({
      title: '请先选择要删除商品',
      icon: 'none',
    })
    return
  }
  if (isAddCollect.value) {
    await delCarsList(selectedItems)
    isAddCollect.value = false
    getList()
  } else {
    $Popup
      .modal({
        title: '删除商品',
        content: '确认删除选中商品吗，删除后可再次添加购物车',
      })
      .then(async () => {
        await delCarsList(selectedItems)
        uni.showToast({
          icon: 'none',
          title: '删除成功',
        })
        getList()
        useUserStore().getShopCarNum()
      })
  }
}

const getAllPrice = () => {
  const totalPrice = shopCarList.value.reduce((prev, current: ShopCarItem) => {
    const price = typeof current.price === 'string' ? parseFloat(current.price) : current.price || 0
    const currentPrice = current.isCheck ? price * (current.goodsNumber || 0) : 0
    return prev + currentPrice
  }, 0)
  allPrice.value = totalPrice.toFixed(2)
}

const getSelectList = () => {
  return shopCarList.value.filter((item) => item.isCheck)
}

const myCouponGet = async (options: any) => {
  const currentItem = couponList.value[options.index]
  if (currentItem) {
    try {
      await getOneCoupon(options.id, {})
      currentItem.restNum = currentItem.restNum - 1
      if (currentItem.restNum === 0) {
        currentItem.hasUse = !currentItem.hasUse
      }
      uni.showToast({
        title: '领取成功',
        icon: 'none',
        duration: 500,
      })
    } catch (error) {
      console.log(error)
      uni.showToast({
        title: '优惠券已被领光',
        icon: 'none',
        duration: 1000,
      })
      currentItem.hasUse = !currentItem.hasUse
    }
  }
}

const gotoHome = () => {
  useSettingStore().backHome()
}
</script>
<style lang="scss" scoped>
@include b(shop) {
  $p-l: 20rpx;

  @include e(car) {
    background: $main-background;
    padding-bottom: 94rpx;
    @include m(control) {
      background: #fff;
      font-size: 28rpx;
      padding: 0 $p-l 0;
      margin-bottom: -1px;
    }

    @include m(container) {
      @include flex(space-between);
      padding-top: 20rpx;
      width: 100%;
      border-bottom: 1px solid $main-border-color;
      padding-bottom: 20rpx;
      background-color: white;
      border-radius: 20rpx;
      margin-bottom: 20rpx;

      @include m(item) {
        display: flex;
        align-items: center;
        padding-left: 20rpx;
      }
    }

    @include m(unable) {
      width: 180rpx;
      height: 180rpx;
      border-radius: 100rpx;
      background-color: rgba(0, 0, 0, 0.3);
      position: absolute;
      left: 84rpx;
      @include flex(center, center);
      border: 4rpx solid white;
    }

    @include m(goods) {
      width: 100%;
      padding: 20rpx;
      padding-top: 110rpx;
      padding-bottom: 12rpx;
    }

    .clear {
      color: $main-light-color;
    }
  }
}

.topSelect {
  display: flex;
  font-size: 28rpx;
}

.topSelect__left {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 160rpx;
  height: 70rpx;
  border: 1px solid #ff4444;
  border-top-left-radius: 100rpx;
  border-bottom-left-radius: 100rpx;
  border-right: none;
}

.topSelect__right {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 160rpx;
  height: 70rpx;
  border: 1px solid #ff4444;
  border-top-right-radius: 100rpx;
  border-bottom-right-radius: 100rpx;
  border-left: none;
}

.shop__car--top {
  position: fixed;
  width: 100%;
  z-index: 10;
}

.shop__car--top--container {
  padding: 20rpx 40rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid $main-border-color;
  background-color: white;
  height: 90rpx;
}

.shopNoCar_container {
  background-color: white;
  .shopNoCar {
    display: flex;
    justify-content: center;
    flex-wrap: wrap;
    width: 100%;
  }
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
}

.shop__noCar--buyButton {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 240rpx;
  height: 80rpx;
  border-radius: 100rpx;
  color: #e4322c;
  border: 1px solid #e4322c;
  margin-top: 40rpx;
  font-size: 28rpx;
}

.submit {
  bottom: 80rpx;
}

@include b(pageBottom) {
  position: fixed;
  bottom: 100rpx;
  bottom: calc(100rpx + env(safe-area-inset-bottom));
  bottom: calc(100rpx + constant(safe-area-inset-bottom));
  z-index: 10;
  width: 100%;
  height: 94rpx;
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-left: 20rpx;

  @include e(check) {
    width: 62%;
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 34rpx;
    height: 100%;

    @include m(allPrice) {
      height: 100%;
      display: flex;
      align-items: center;

      @include m(price) {
        margin-left: 20rpx;
        color: #ff4444;
        font-size: 30rpx;
        font-weight: bold;
      }
    }
  }

  @include e(button) {
    width: 34%;
    height: 100%;
    background-color: #ff4444;
    @include flex(center, center);
    color: white;
    font-size: 34rpx;
  }

  @include e(delBtn) {
    width: 120rpx;
    height: 54rpx;
    display: -webkit-box;
    display: flex;
    -webkit-box-pack: center;
    justify-content: center;
    -webkit-box-align: center;
    align-items: center;
    font-size: 28rpx;
    border: 1px solid #ff4444;
    color: #ff4444;
    border-radius: 100rpx;
    margin-right: 40rpx;
  }
}

.moveCollect {
  margin-right: 40rpx;
  width: 150rpx;
  height: 54rpx;
  border: 1px solid #ff4444;
  color: #ff4444;
  border-radius: 100rpx;
  font-size: 24rpx;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
