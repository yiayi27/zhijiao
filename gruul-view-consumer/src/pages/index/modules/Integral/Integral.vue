<template>
  <view style="position: absolute; width: 100%; padding-bottom: 100rpx">
    <block data-src="../../../integralShop/integralShop.wxml">
      <!-- 用户信息 -->
      <image
        v-if="singIn && shop"
        src="https://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/api/integral_Shop.png"
        style="width: 100%; height: 580rpx; position: relative"
      />
      <view v-else style="width: 100%; height: 160rpx; position: relative; background-color: #f05342" />
      <view class="userMsg">
        <view class="userMsg__icon">
          <view class="userMsg__icon--button" @tap="gotoBalance">
            <MIcon name="iconjifen1" size="36rpx" style="margin-right: 10rpx" />
            积分值
            <span class="userMsg__icon--button--num">{{ integral }}</span>
          </view>
          <view v-if="stepNumber" class="userMsg__icon--button" @tap="gotoChange">
            <MIcon name="iconpaobu1" size="36rpx" style="margin-right: 10rpx" />
            今日步数
            <span class="userMsg__icon--button--num">{{ todayStep }}</span>
          </view>
        </view>
      </view>
      <!-- 签到 -->
      <view v-if="singIn && shop" class="sign">
        <view class="sign__card">
          <view class="sign__card--top">
            <view style="font-size: 28rpx">
              已连续签到
              <span style="color: #ec564a">{{ runningDays - 1 }}</span>
              天
            </view>
          </view>
          <view class="sign__card--content">
            <view v-for="(item, index) in signArr" :key="index" class="form">
              <view v-if="item.type" class="sign__card--content--circle" style="background-color: #fcecec; color: #f35334; font-size: 28rpx">
                +{{ item.value }}
              </view>
              <view v-else-if="!item.type" class="sign__card--content--circle" style="background-color: #f7f7f7">
                <div v-if="item.isAdd" class="tag">
                  <em />
                  <view class="tag__num">+{{ item.addCode }}</view>
                </div>
                <image
                  v-if="item.isAdd"
                  src="https://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/api/rectangle5.png"
                  style="width: 56rpx; height: 48rpx"
                />
                <span v-else class="sign__card--content--circle--values" style="color: #a4a4a4; font-size: 28rpx"> +{{ item.value }} </span>
              </view>
              <view class="sign__card--content--day" :style="'color: ' + (!item.type ? '#A4A4A4' : ' #3E3E3E') + ';'"> {{ index + 1 }}天 </view>
            </view>
          </view>
          <view class="sign__card--signBtn">
            <view style="display: flex">
              <view :class="!signInState ? 'sign__card--top--sign' : 'sign__card--top--nosign'" @tap="todaySignOpt">
                {{ signInState ? '已签到' : '签到领积分' }}
              </view>
              <view style="position: absolute; right: 60rpx; top: 50rpx" @tap="gotoRule" />
            </view>
          </view>
        </view>
      </view>
      <view v-if="couponList.length > 0" class="titleShop">
        <view class="titleShop__top">
          <view class="titleShop__top--line" />
          优惠套餐
        </view>
        <view class="couponList">
          <view v-for="(coupon, index) in couponList" :key="index" class="couponList__box">
            <view class="couponList__box--itemTop">
              <view class="couponList__box--itemTop--act">
                <span v-if="coupon.couponType === 0 || coupon.couponType === 2" style="display: flex; align-items: flex-end; font-size: 48rpx">
                  {{ coupon.discount }}
                  <view style="font-size: 32rpx; margin-bottom: 6rpx; margin-left: 1px">折</view>
                </span>
                <span v-else style="display: flex; align-items: flex-end; font-size: 48rpx">
                  <view style="font-size: 32rpx; margin-bottom: 6rpx; margin-left: 1px">￥</view>
                  {{ coupon.discountAmount }}
                </span>
              </view>
              <view class="couponList__box--itemTop--desc">
                <view v-if="coupon.couponType === 0 || coupon.couponType === 1"> 无门槛可用 </view>
                <view v-if="coupon.couponType === 2 || coupon.couponType === 3"> 满{{ coupon.conditionAmount }}可用 </view>
              </view>
            </view>
            <view class="couponList__box--itemBottom">
              <view class="couponList__box--itemTop--act" style="color: #2b2b2b; font-size: 30rpx">
                {{ coupon.integral }}
                <image src="https://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/api/gold.png" style="width: 34rpx; height: 34rpx" />
              </view>
              <view class="couponList__box--itemTop--desc" style="top: 62rpx" :data-coupon="coupon" @tap="changeCoupon">
                <view class="changeBtn">{{ coupon.totalAvailable === 0 ? '已兑完' : '兑换' }}</view>
              </view>
            </view>
          </view>
        </view>
      </view>
      <view v-if="goodsList.length > 0" class="goodShop" :style="'margin-top:' + (couponList.length > 0 ? '175' : '0') + 'px'">
        <view class="titleShop__top" style="padding: 20rpx">
          <view class="titleShop__top--line" />
          实物商品
        </view>
        <view class="goodsList">
          <view v-for="(good, index) in goodsList" :key="index" class="goodsList__item" :data-good="good" @tap="gotoDetail">
            <view class="goodsList__item--img">
              <image :src="good.pic" style="width: 340rpx; height: 340rpx" />
              <view v-if="good.inventory === 0" class="goodsList__soldout">
                <view>已售罄</view>
                <view>SoldOut</view>
              </view>
            </view>
            <view class="goodsList__item--shopName">{{ good.integralGoodName }}</view>
            <view class="goodsList__item--shopPrice">
              <view class="goodsList__item--shopPrice--price">
                <span>{{ good.integralPrice }}积分</span>
              </view>
              <view class="goodsList__item--shopPrice--right">已有{{ good.sales }}人兑换</view>
            </view>
            <view v-if="good.guidePrice !== 0" class="goodsList__item--shopBottom">
              市场价
              <span style="text-decoration: line-through; margin-left: 8rpx">￥{{ good.guidePrice }}</span>
            </view>
          </view>
        </view>
      </view>
      <view v-if="goodsList.length === 0" class="shopNoCar" :style="'margin-top:' + (couponList.length > 0 ? '175' : '0') + 'px'">
        <image src="https://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/api/noCar.png" class="shop__noCar" />
        <view class="shop__noCar--text">暂无商品~</view>
      </view>
    </block>
  </view>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useIntegral } from '@/composables/mixins/useIntegral'
import MIcon from '@/components/MIcon/MIcon.vue'

// 从useIntegral钩子中解构出所需的状态和方法
const {
  // 状态
  couponList,
  todayStep,
  goodsList,
  runningDays,
  signInState,
  signArr,
  integral,
  shop,
  singIn,
  stepNumber,

  // 方法
  getShopType,
  todaySignOpt,
  getUser,
  getCoupon,
  changeCoupon,
  gotoDetail,
  gotoChange,
  gotoBalance,
  gotoRule,
} = useIntegral()

// 生命周期钩子
onMounted(() => {
  uni.setNavigationBarTitle({
    title: '积分商城',
  })

  getShopType()
  getUser()
  getCoupon()
})
</script>

<style lang="scss" scoped>
@import '@/assets/styles/var.scss';

page {
  background-color: #f6f6f6;
}

.null {
  width: 100%;
  text-align: center;
}

@include b(userMsg) {
  padding: 40rpx 0 40rpx 40rpx;
  position: absolute;
  top: 0;
  width: 100%;

  @include e(icon) {
    color: white;
    padding-right: 40rpx;
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 40rpx;

    @include m(button) {
      display: flex;
      align-items: flex-end;
      font-size: 28rpx;

      @include m(num) {
        margin-left: 20rpx;
        font-size: 28rpx;
        font-weight: bold;
      }
    }
  }

  @include e(user) {
    display: flex;
    justify-content: space-between;

    @include m(set) {
      display: flex;
      align-items: center;
      color: white;

      @include m(url) {
        width: 150rpx;
        height: 150rpx;
        border-radius: 100rpx;
        background-color: white;
        @include flex(center, center);
        margin-right: 40rpx;
      }
    }

    @include m(rules) {
      width: 180rpx;
      height: 70rpx;
      border-top-left-radius: 100rpx;
      border-bottom-left-radius: 100rpx;
      background-color: #929cfe;
      color: white;
      @include flex(center, center);
      margin-top: 10rpx;
    }
  }

  @include e(img) {
    width: 130rpx;
    height: 130rpx;
    border-radius: 100rpx;
  }
}

@include b(sign) {
  @include e(card) {
    width: 94%;
    padding: 20rpx 30rpx;
    position: absolute;
    background-color: white;
    top: 120rpx;
    height: 354rpx;
    left: 3%;
    border-radius: 20rpx;
    box-shadow:
      30rpx 20rpx 30rpx -30rpx rgba(0, 0, 0, 0.4),
      -30rpx 20rpx 30rpx -30rpx rgba(0, 0, 0, 0.4);

    @include m(top) {
      display: flex;
      justify-content: space-between;

      @include m(sign) {
        width: 440rpx;
        height: 80rpx;
        border-radius: 100rpx;
        @include flex(center, center);
        color: #ffffff;
        border: 1px solid #e9554e;
        margin-top: 22rpx;
        background-color: #e9554e;
        box-shadow: 10rpx 10rpx 20rpx -8rpx #ea3323;
        background: linear-gradient(#f6522b, #e85857);
        font-size: 32rpx;
      }

      @include m(nosign) {
        width: 440rpx;
        height: 80rpx;
        border-radius: 100rpx;
        @include flex(center, center);
        color: #ffffff;
        border: 1px solid #a4a4a4;
        margin-top: 22rpx;
        background-color: #a4a4a4;
        box-shadow: 10rpx 10rpx 20rpx -8rpx #a4a4a4;
        background: linear-gradient(#a4a4a4, #a4a4a4);
        font-size: 32rpx;
      }
    }

    @include m(content) {
      margin-top: 1px;
      display: flex;
      justify-content: space-around;

      @include m(circle) {
        width: 84rpx;
        height: 84rpx;
        @include flex(center, center);
        border-radius: 100rpx;
        position: relative;

        @include m(values) {
          position: absolute;
          display: flex;
          justify-content: center;
        }
      }

      @include m(day) {
        margin-top: -30rpx;
        font-size: 28rpx;
      }
    }

    @include m(signBtn) {
      width: 100%;
      display: flex;
      justify-content: center;
      align-items: center;
      position: relative;
    }
  }
}

.form {
  height: 160rpx;
  width: 90rpx;
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  align-items: center;
}

@include b(title) {
  margin: 30rpx 20rpx 0 20rpx;
  background-color: white;
  // padding-top: 20rpx;
  border-top-left-radius: 30rpx;
  border-top-right-radius: 30rpx;

  @include e(item) {
    @include flex(center, center);
    font-weight: bold;
    font-size: 28rpx;
  }

  @include e(desc) {
    @include flex(center, center);
    font-size: 26rpx;
    color: #bebebe;
    margin-top: 8rpx;
  }

  @include e(icon) {
    display: flex;
    align-items: center;
    font-size: 34rpx;
    margin-top: 8rpx;
    font-weight: bold;
    padding-left: 20rpx;
  }
}

.tag {
  border: 1rpx solid #ee5544;
  position: relative;
  background-color: #ee5544;
  border-radius: 100rpx;
  position: absolute;
  top: -54rpx;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 0 6rpx;

  .tag__num {
    color: white;
    font-size: 28rpx;
  }
}

.tag em {
  display: block;
  border-width: 10rpx;
  position: absolute;
  bottom: -19rpx;
  left: 25rpx;
  border-style: solid dashed dashed;
  border-color: #ee5544 transparent transparent;
  font-size: 0;
  line-height: 0;
}

@include b(titleShop) {
  font-size: 34rpx;
  font-weight: bold;
  padding: 20rpx;
  background-color: white;
  border-radius: 30rpx;
  margin-top: -40rpx;
  z-index: 100;
  position: absolute;
  width: 100%;

  @include e(top) {
    display: flex;
    align-items: center;
    font-size: 32rpx;

    @include m(line) {
      width: 10rpx;
      height: 34rpx;
      background-color: #e9554e;
      margin-right: 16rpx;
      border-radius: 100rpx;
    }
  }
}

@include b(couponList) {
  margin: 30rpx 0 20rpx 0;
  display: flex;
  overflow: auto;

  @include e(box) {
    margin-right: 52rpx;

    @include m(itemTop) {
      width: 172rpx;
      height: 114rpx;
      background-color: #fa7e69;
      border-top-left-radius: 40rpx;
      border-top-right-radius: 40rpx;
      position: relative;

      @include m(act) {
        display: flex;
        width: 100%;
        justify-content: center;
        align-items: center;
        color: white;
        font-weight: normal;
        top: 10rpx;
        position: absolute;
      }

      @include m(desc) {
        display: flex;
        width: 100%;
        justify-content: center;
        color: white;
        font-weight: normal;
        top: 68rpx;
        position: absolute;
        font-size: 22rpx;
      }
    }

    @include m(itemBottom) {
      width: 172rpx;
      height: 114rpx;
      background-color: #fdd8d6;
      border-bottom-left-radius: 40rpx;
      border-bottom-right-radius: 40rpx;
      position: relative;
    }
  }
}

.changeBtn {
  width: 90rpx;
  height: 42rpx;
  background-color: #ef5541;
  display: flex;
  @include flex(center, center);
  font-size: 24rpx;
  border-radius: 100rpx;
}

.couponList::-webkit-scrollbar {
  display: none;
}

.couponList__box--itemTop::before {
  width: 32rpx;
  height: 32rpx;
  border-radius: 100rpx;
  background-color: white;
  content: '';
  position: absolute;
  top: 100rpx;
  z-index: 50;
  left: -20rpx;
}

.couponList__box--itemTop::after {
  width: 32rpx;
  height: 32rpx;
  border-radius: 100rpx;
  background-color: white;
  content: '';
  position: absolute;
  top: 100rpx;
  z-index: 50;
  right: -20rpx;
}

.goodShop {
  font-size: 34rpx;
  font-weight: bold;
  background-color: #f6f6f6;
  border-radius: 30rpx;
}

@include b(goodsList) {
  margin: 0 16rpx;
  flex-wrap: wrap;
  display: flex;
  justify-content: space-between;
  padding-bottom: 10rpx;
  margin-bottom: 20rpx;
  border-bottom-left-radius: 30rpx;
  border-bottom-right-radius: 30rpx;

  @include e(item) {
    width: 49.2%;
    // height: 400rpx;
    background-color: white;
    margin-bottom: 14rpx;
    border-radius: 20rpx;
    position: relative;

    @include m(img) {
      margin-top: 20rpx;
      @include flex(center, center);
    }

    @include m(shopName) {
      padding: 0 20rpx;
      display: flex;
      height: 96rpx;
      flex-wrap: wrap;
      padding-top: 20rpx;
      display: -webkit-box;
      overflow: hidden;
      text-overflow: ellipsis;
      word-wrap: break-word;
      white-space: normal !important;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      font-size: 28rpx;
      font-weight: normal;
    }

    @include m(shopPrice) {
      margin-top: 10rpx;
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 0 20rpx;
      margin-bottom: 20rpx;

      @include m(price) {
        display: flex;
        font-size: 26rpx;
        color: #f3573a;
      }

      @include m(right) {
        display: flex;
        font-size: 24rpx;
        color: #aeaeae;
        font-weight: normal;
      }

      @include m(button) {
        width: 150rpx;
        height: 50rpx;
        @include flex(center);
        color: white;
        font-weight: bold;
        background-color: #ff4d63;
        border-radius: 10rpx;
        font-size: 24rpx;
      }
    }

    @include m(shopBottom) {
      padding: 0 20rpx;
      display: flex;
      margin-top: 10rpx;
      margin-bottom: 10rpx;
      color: #c8c8c8;
      align-items: center;
      font-weight: normal;
      font-size: 24rpx;
    }
  }

  @include e(soldout) {
    position: absolute;
    width: 200rpx;
    height: 200rpx;
    top: 180rpx;
    left: 180rpx;
    transform: translate(-50%, -50%);
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: #ffffff;
    font-size: 26rpx;
    border-radius: 100rpx;
    background-color: rgba(0, 0, 0, 0.5);
    border: 4rpx solid white;
  }
}

.shopNoCar {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  width: 100%;
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
</style>
