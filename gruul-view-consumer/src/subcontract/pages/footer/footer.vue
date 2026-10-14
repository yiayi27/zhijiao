<template>
  <view>
    <uv-checkbox-group v-model="selectValue" class="box" @change="onChange">
      <view v-if="list.length > 0" class="footerBox">
        <view v-for="(item, listIndex) in list" :key="listIndex" :data-index="listIndex" :data-list="list">
          <view class="listTime">
            <view v-if="isDeal" class="listDateCheck">
              <!-- <uv-checkbox v-model="item.isDataCheck" @change="dataOnChange" activeColor="#FD4D63" iconSize="36rpx"
              :data-index="listIndex"></uv-checkbox> -->
              <!-- <uv-checkbox shape="circle" :name="{index:listIndex}" @change="dataOnChange"  iconSize="36rpx" activeColor="#FD4D63"></uv-checkbox> -->
              <!-- <view @click="dataOnChange" :data-index="listIndex">111</view> -->
            </view>
            {{ item.date }}
          </view>
          <view class="box">
            <view
              v-for="(good, index) in item.shopList"
              :key="index"
              class="shopList"
              :data-index="listIndex"
              :data-shopindex="index"
              style="position: relative"
              @longpress="deleteFootprint"
              @tap.stop="toProductDetail"
            >
              <view v-if="isDeal" class="listCheck">
                <uv-checkbox shape="circle" :name="good.footmarkId" iconSize="36rpx" activeColor="#FD4D63" />
              </view>
              <view style="width: 204rpx; height: 204rpx; padding-left: 20rpx">
                <image :src="good.productPic" lazy-load mode="aspectFill" style="width: 204rpx; height: 204rpx" />
                <view v-if="good.status === 0" class="listContent__imgUseless">失效</view>
              </view>
              <view v-if="good.isDelete" class="longDelate">
                <view class="longDelate__centerDelete" @tap.stop="deleteSure"> 删除 </view>
              </view>
              <view class="listContent">
                <view class="product__name">{{ good.productName }}</view>
                <view class="price__line">
                  <view class="product__price">
                    <span>{{ good.intPrice }}</span>
                    <span style="font-weight: weight; font-size: 30rpx">.{{ good.smaPrice }}</span>
                  </view>
                  <view v-if="good.productPrice !== '0.00'" class="product__oriPrice">
                    {{ good.originalPrice }}
                  </view>
                </view>
              </view>
            </view>
          </view>
        </view>
      </view>
    </uv-checkbox-group>
    <view v-if="list.length > 0" style="height: 180rpx" class="tipNoMore">-------没有更多了-------</view>
    <view v-if="list.length > 0" class="footerBox__bottom">
      <view v-if="!isDeal" class="footerBox__bottom--choose">
        <view @tap="cleanAll">一键清空</view>
        <view @tap="editAll">编辑</view>
      </view>
      <view v-else class="footerBox__bottom--choose">
        <view v-if="!isAllCheck" data-type="true" @tap.stop="checkAllDeal">全选</view>
        <view v-if="isAllCheck" data-type="false" @tap.stop="cancelCheckAllDeal">取消全选</view>
        <view @tap.stop="cancelDeal">取消</view>
        <view style="color: #fe4e63" @tap.stop="checkAllDel">删除</view>
      </view>
    </view>
    <view v-if="list.length === 0" :style="'height:' + height + 'px'" class="noReviews">
      <view class="noReviews__center">
        <image src="https://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/api/footmark.png" class="noReviews__center--img" />
        <view class="noReviews__center--text">暂无足迹~</view>
      </view>
      <view class="noCollect__button" @tap="gotoIndex">去逛一逛</view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue'
import { onShow, onReachBottom } from '@dcloudio/uni-app'
import { userFooterList, userFooterDel } from '@/api/modules/goods'
import { useSettingStore } from '@/store/modules/setting'

// 响应式状态
const list = ref<any[]>([])
const isDeal = ref(false)
const isAllCheck = ref(false)
const query = ref({
  current: 1,
  size: 10,
})
const hasMore = ref(false)
const height = ref(0)
const selectValue = ref<any[]>([])

// 方法
const getFooterList = (customQuery = {}, type = false) => {
  if (hasMore.value) return

  userFooterList(Object.assign({}, query.value, customQuery)).then((res) => {
    const currentList = list.value
    const newList: any[] = []
    let dates: string[] = []

    if (res.length > 0) {
      res.forEach((v: any) => {
        const vList = JSON.parse(v)
        vList.forEach((vItem: any) => {
          vItem.originalPrice = vItem.originalPrice.toFixed(2)
          vItem.productPrice = vItem.productPrice.toFixed(2)
          vItem.intPrice = vItem.productPrice !== '0.00' ? vItem.productPrice.split('.')[0] : vItem.originalPrice.split('.')[0]
          vItem.smaPrice = vItem.productPrice !== '0.00' ? vItem.productPrice.split('.')[1] : vItem.originalPrice.split('.')[1]
        })
        newList.push(...vList)
      })
    }

    hasMore.value = res.length !== query.value.size

    dates = [
      ...new Set(
        newList.map((v) => {
          return v.time
        }),
      ),
    ]

    if (currentList.length > 0 && currentList[currentList.length - 1].date === dates[0]) {
      currentList[currentList.length - 1].shopList = currentList[currentList.length - 1].shopList.concat(
        newList.filter((v) => {
          return v.time === dates[0]
        }),
      )
      dates.shift()
    }

    dates.forEach((v) => {
      currentList.push({
        date: v,
        isDataCheck: false,
        shopList: newList.filter((m) => {
          return m.time === v
        }),
      })
    })

    if (type && isAllCheck.value && isDeal.value) {
      currentList.forEach((item) => {
        item.isDataCheck = true
        item.shopList.forEach((v: any) => {
          v.isCheck = true
        })
      })
    }

    list.value = currentList
  })
}

const onChange = () => {
  nextTick(() => {
    checkout()
  })
}

const checkout = () => {
  const listLength = list.value
    .map((item) => {
      return item.shopList.length
    })
    .reduce((total, current) => {
      return total + current
    }, 0)

  const selectLength = selectValue.value.length
  isAllCheck.value = selectLength === listLength
}

const deleteFootprint = (e: any) => {
  const index = e.currentTarget.dataset.index
  const shopindex = e.currentTarget.dataset.shopindex
  const currentList = list.value

  currentList.forEach((v) => {
    v.shopList.forEach((item: any) => {
      item.isDelete = false
    })
  })
  list.value = currentList

  if (isDeal.value) {
    return
  }

  currentList[index].shopList[shopindex].isDelete = true
}

const toProductDetail = (e: any) => {
  const index = e.currentTarget.dataset.index
  const shopindex = e.currentTarget.dataset.shopindex
  const currentList = list.value

  currentList.forEach((item) => {
    item.shopList.forEach((v: any) => {
      v.isDelete = false
    })
  })

  list.value = currentList
  const id = list.value[index].shopList[shopindex].productId

  if (!isDeal.value) {
    uni.navigateTo({
      url: '/subcontract/pages/detail/detail?id=' + id,
    })
  }
}

const deleteSure = () => {
  // 实现删除确认逻辑
  // const index = e.currentTarget.dataset.index;
  // const shopindex = e.currentTarget.dataset.shopindex;
  // const list = this.list;
  // footmarkIds.push(list[index].shopList[shopindex].footmarkId);
  // uni.showModal({
  //   title:"提示",
  //   content:'确认删除此商品记录吗',
  //   success:()=>{
  //     userFooterDel(0, footmarkIds).then(() => {
  //       const query = {
  //         current: 1,
  //         size: this.query.size * this.query.current,
  //       };
  //       this.setData({
  //         list: [],
  //         hasMore: false,
  //       });
  //       this.getFooterList(query);
  //       uni.showToast({
  //         title: "删除成功",
  //         icon: "none"
  //       });
  //     });
  //   },
  //   fail:()=>{
  //   // list[index].shopList[shopindex].isDelete = false;
  //   //   this.setData({
  //   //     list,
  //   //   });
  //   }
  // })
}

const cleanAll = () => {
  uni.showModal({
    title: '提示',
    content: '确认清空商品浏览记录吗',
    success: (res) => {
      if (res.confirm) {
        userFooterDel(1, [-1]).then(() => {
          list.value = []
        })
      }
    },
  })
}

const editAll = () => {
  const currentList = list.value

  currentList.forEach((item) => {
    item.shopList.forEach((v: any) => {
      v.isDelete = false
    })
  })

  list.value = currentList
  isDeal.value = true
}

const checkAllDeal = () => {
  const arr = list.value
  const tempArr: any[] = []

  for (let i = 0; i < arr.length; i++) {
    for (let j = 0; j < arr[i].shopList.length; j++) {
      tempArr.push(arr[i].shopList[j].footmarkId)
    }
  }

  isAllCheck.value = true
  selectValue.value = tempArr
}

const cancelCheckAllDeal = () => {
  isAllCheck.value = false
  selectValue.value = []
}

const cancelDeal = () => {
  selectValue.value = []
  isDeal.value = false
  isAllCheck.value = false
}

const checkAllDel = () => {
  const footmarkIds = selectValue.value

  if (!footmarkIds.length) {
    uni.showToast({
      icon: 'none',
      title: '请先选择删除商品',
    })
    return
  }

  uni.showModal({
    title: '提示',
    content: '确认删除选中商品吗',
    success: (res) => {
      if (res.confirm) {
        userFooterDel(0, footmarkIds).then(() => {
          const customQuery = {
            current: 1,
            size: query.value.size * query.value.current,
          }

          list.value = []
          isDeal.value = false
          hasMore.value = false
          getFooterList(customQuery)
        })
      }
    },
  })
}

const gotoIndex = () => {
  useSettingStore().backHome()
}

// 生命周期钩子
onShow(() => {
  getFooterList()
  uni.getSystemInfo({
    success: (res) => {
      height.value = res.windowHeight
    },
  })
})

onReachBottom(() => {
  query.value.current++
  getFooterList({}, true)
})
</script>

<style lang="scss" scoped>
page {
  background-color: #f4f4f4;
}

.listTime {
  height: 70rpx;
  display: flex;
  align-items: center;
  padding-left: 20rpx;
  font-size: 26rpx;
}

.box {
  background-color: white;
  border-radius: 18rpx;
  padding: 10rpx;
}

.shopList {
  height: 242rpx;
  background-color: white;
  display: flex;
  padding: 20rpx 0;
}

.listDateCheck {
  width: 80rpx;
  height: 100%;
  display: flex;
  align-items: center;
}

.listCheck {
  width: 80rpx;
  height: 100%;
  display: flex;
  align-items: center;
  padding-left: 20rpx;
}

.listContent {
  width: 430rpx;
  margin-left: 40rpx;
  display: flex;
  flex-wrap: wrap;
}

.listContent__imgUseless {
  width: 200rpx;
  height: 50rpx;
  position: absolute;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24rpx;
  background-color: #fea00c;
  opacity: 0.8;
}

.longDelate {
  position: absolute;
  width: 80rpx;
  height: 100rpx;
  background-color: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  top: 0;
  width: 100%;
  height: 100%;
}

.longDelate__centerDelete {
  display: flex;
  justify-content: center;
  width: 140rpx;
  height: 140rpx;
  background-color: #fea00b;
  border-radius: 70rpx;
  color: white;
  align-items: center;
}

.product__name {
  width: 430rpx;
  // overflow: hidden;
  // white-space: nowrap;
  // text-overflow: ellipsis;
  font-size: 26rpx;
  display: flex;
  flex-wrap: wrap;
}

.price__line {
  display: flex;
  align-items: flex-end;
}

.product__price {
  margin-top: 10rpx;
  font-size: 40rpx;
  color: #fa5555;
  margin-right: 20rpx;
  font-weight: bold;
}

.product__price::before {
  margin-top: 10rpx;
  content: '￥';
  font-size: 30rpx;
  font-weight: normal;
}

.product__oriPrice {
  margin-top: 10rpx;
  font-size: 24rpx;
  color: #ccc;
  font-weight: bold;
  text-decoration: line-through;
}

.product__oriPrice::before {
  margin-top: 10rpx;
  font-size: 24rpx;
  content: '￥';
}

.footerBox__bottom {
  position: fixed;
  bottom: 0;
  width: 100%;
  height: 100rpx;
  background-color: white;
  display: flex;
  align-items: center;
  justify-content: space-around;
  font-size: 28rpx;
}

.footerBox__bottom--choose {
  display: flex;
  justify-content: space-around;
  width: 100%;
}

.tipNoMore {
  display: flex;
  justify-content: center;
  color: #d0d2d6;
  font-size: 30rpx;
  margin-top: 36rpx;
}

.noCollect {
  display: flex;
  align-items: center;
  justify-content: center;
  // height: 1000rpx;
  width: 100%;
  flex-wrap: wrap;
}

.noCollect__button {
  height: 80rpx;
  width: 240rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #e4322c;
  border-radius: 100rpx;
  border: 1px solid #e4322c;
  font-size: 28rpx;
  margin-top: -220rpx;
}

.shop__noCar {
  margin-top: 140rpx;
  width: 69%;
}

.noReviews {
  display: flex;
  width: 100%;
  justify-content: center;
  background-color: white;
  flex-wrap: wrap;

  &__center {
    display: flex;
    justify-content: center;
    width: 590rpx;
    flex-wrap: wrap;
    height: 680rpx;

    &--img {
      margin-top: 100rpx;
    }

    &--text {
      margin-top: 20rpx;
      font-size: 32rpx;
    }
  }
}
</style>
