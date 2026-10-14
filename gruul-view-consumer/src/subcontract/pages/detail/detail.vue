<template>
  <!-- #ifndef H5 -->
  <page-meta :page-style="`overflow:${show || buyShow || shareVisible ? 'hidden' : 'visible'}`"></page-meta>
  <!-- #endif -->
  <view>
    <view class="share_canvas_container">
      <uv-modal ref="modalRef" zIndex="99" title="" :showCancelButton="false" :showConfirmButton="false">
        <view style="position: fixed; top: -100000px; left: -100000px">
          <canvas style="width: 1120rpx; height: 1760rpx; opacity: 0" canvas-id="cardCanvas" class="share--canvas"></canvas>
        </view>
        <view v-if="showAll" style="padding: 25px">
          <view class="icon--end">
            <view class="icon--end__content">
              <MIcon name="icontijiaodingdan-youhuiquan-guanbi" size="40rpx" color="#D1D1D1" @click="hiddenCrad" />
            </view>
          </view>
          <img class="share--image" mode="aspectFill" :src="cardImgUrl" />
          <view class="downButton" style="margin-top: 40rpx; margin-left: 40rpx" @tap.stop="savePhoto">
            <view class="downButton--content">
              <view style="font-size: 30rpx">保存至本地</view>
            </view>
          </view>
        </view>
      </uv-modal>
    </view>

    <swiper
      :autoplay="autoplay"
      indicator-color="white"
      :interval="3000"
      :duration="300"
      circular="true"
      class="swiper"
      current="0"
      @change="onSlideChange"
    >
      <swiper-item v-if="goodsVideoUrl">
        <video
          id="myVideo"
          :src="goodsVideoUrl"
          controls="true"
          :poster="videoImg"
          style="width: 100%; height: 90%"
          @tap.stop="bofang"
          @ended="endbofng"
        />
      </swiper-item>
      <block v-for="(item, index) in goodDetail.imgUrls" :key="index">
        <swiper-item>
          <image mode="aspectFill" :src="item" class="slide-image swiper-item" />
        </swiper-item>
      </block>
    </swiper>

    <view class="currentStyle">
      <view class="currentStyle__content">
        <view class="currentStyle__content--curr">
          {{ current }} / {{ goodsVideoUrl ? goodDetail.imgUrls.length + 1 : goodDetail.imgUrls.length }}
        </view>
      </view>
    </view>
    <view v-if="hasJoinType" class="fenx" @click="goIntoChain">
      <MIcon name="iconshangpinxiangqing-fenxiang" size="32rpx" style="margin-right: 10rpx" />
      接龙分享
    </view>
    <!-- 引入backAct部分 -->
    <block>
      <view class="backAct">
        <image
          v-if="showCountDown"
          src="https://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/api/priceBackground.png"
          style="width: 100%; height: 120rpx; z-index: 10"
        />
        <view v-else style="width: 100%; height: 120rpx; background-color: #ff0c19; z-index: 10" />
        <view style="position: absolute; display: flex; justify-content: space-between; width: 100%; align-items: center; z-index: 11">
          <view class="bothNone__price">
            <view class="bothNone__price--actPrice">
              <view style="font-size: 34rpx; margin-bottom: 4rpx">￥</view>
              {{ goodDetail.actPrice !== '0.00' ? goodDetail.actPrice : goodDetail.price }}
            </view>
            <view v-if="goodDetail.actPrice !== '0.00'" class="through">￥{{ goodDetail.price }}</view>
          </view>
          <view v-if="showCountDown" style="width: 240rpx">
            <view class="time__count">
              <view class="time__count--text">
                <text>{{ status[countDetail.status] }}还剩</text>
              </view>
              <view style="margin-left: -24rpx">
                <uv-count-down :time="remainTime" @change="handleChange" @finish="handleFinish">
                  <view class="center--middle" style="padding-bottom: 8rpx">
                    <view v-if="timeData.hours.length === 3" class="time__count--time">
                      {{ timeData.hours[0] }}{{ timeData.hours[1] }}{{ timeData.hours[2] }}
                    </view>
                    <view v-if="timeData.hours.length === 2" class="time__count--time"> {{ timeData.hours[0] }}{{ timeData.hours[1] }} </view>
                    <view class="time__count--colon">:</view>
                    <view class="time__count--time"> {{ timeData.minutes[0] }}{{ timeData.minutes[1] }} </view>
                    <view class="time__count--colon">:</view>
                    <view class="time__count--time"> {{ timeData.seconds[0] }}{{ timeData.seconds[1] }} </view>
                  </view>
                </uv-count-down>
              </view>
            </view>
          </view>
        </view>
      </view>
      <view style="background-color: white; width: 100%; display: flex; padding: 20rpx 20rpx 0 20rpx">
        <view v-if="goodDetail.skuStocks[0]?.memPrice && memState === 1" style="display: flex; margin-right: 40rpx">
          <view v-if="goodDetail.skuStocks[0]?.memPrice && memState === 1" class="member__text--vip center--middle" style="margin-right: 4rpx">
            <MIcon name="iconhuiyuan" size="22rpx" />
            会员
          </view>
          <view v-if="goodDetail.memPrice && memState === 1 && openMember"> ￥{{ goodDetail.memPrice }} </view>
        </view>
        <view style="display: flex">
          <view v-if="isRebate && goodDetail.rebPrice && goodDetail.rebPrice !== '0.00'" class="reWard" style="margin-right: 40rpx">
            返利￥{{ goodDetail.rebPrice }}
          </view>
        </view>
      </view>
      <view v-if="openMember && goodDetail.memPrice" style="padding: 20rpx 30rpx 0 30rpx; background-color: white">
        <view v-if="memState !== 1" class="member">
          <view style="display: flex; align-items: center">
            <view class="member__text--vip center--middle">
              <MIcon name="iconhuiyuan" size="22rpx" />
              会员
            </view>
            <view v-if="goodDetail.memPrice">购买会员卡预计可省￥{{ goodDetail.savePrice }}</view>
          </view>
          <view class="member__open center--middle" @click="openMemberHandle">
            <view>立即开通 ></view>
          </view>
        </view>
      </view>
    </block>
    <view class="product">
      <view v-if="!goodDetail.openSpecs && goodDetail.skuStocks.length > 1" class="product__box">
        <view v-for="(item, index) in goodDetail.skuStocks" :key="index" class="product__box--list" @click="submit">
          <view
            class="item__book--text center--middle"
            :style="
              'color:' +
              (item.getType && item.stock > 0 ? 'red' : '') +
              ';border:1rpx solid ' +
              (item.getType && item.stock > 0 ? 'red' : '#81817F') +
              ';background-color:' +
              (item.stock === 0 ? '#F3F2F1' : '')
            "
          >
            {{ item.specs }}
          </view>
        </view>
      </view>
      <view class="product__name" style="font-family: '微软雅黑'">{{ goodDetail.name }}</view>
      <view v-if="goodDetail.saleDescribe" class="product__saleDescribe">
        <view class="product__saleDescribe--text" @longpress="longCopy">
          {{ goodDetail.saleDescribe }}
        </view>
      </view>
      <view v-if="goodDetail.goodTime" class="product__description">
        <text class="product__description--title">发货</text>
        <text class="product__description--desc">{{ goodDetail.goodTime }}</text>
      </view>
      <view class="product__temp">
        <view class="product__description">
          <text class="product__description--title">销量</text>
          <view class="product__description--desc">
            已售
            <text class="product__description--content">{{ goodDetail.sale }}</text>
            件,
          </view>
          <view class="product__description--desc">
            仅剩
            <text class="product__description--content">{{ goodDetail.stock }}</text>
            件
            <span v-if="goodDetail.goodType === 1">,</span>
          </view>
          <view v-if="goodDetail.goodType === 1" class="product__description--desc">
            限购
            <text class="product__description--content">{{ goodDetail.maxNum }}</text>
            件
          </view>
        </view>
        <view class="product__share" @click="addCollect">
          <MIcon v-if="!goodDetail.isCollect" name="iconshoucang" size="40rpx" style="color: #7c7c7c" />
          <MIcon v-else name="iconshoucang1" size="40rpx" style="color: #ea0000" />
          <view :style="'font-size:28rpx; color:' + (!goodDetail.isCollect ? '#7C7C7C' : '#EA0000') + ';margin-left:10rpx'">
            {{ !goodDetail.isCollect ? '收藏' : '已收藏' }}
          </view>
        </view>
      </view>
    </view>
    <view v-if="true" class="product__shareFloat" @click="showShare">
      <view style="width: 50rpx; display: flex; flex-wrap: wrap; justify-content: center; align-content: center">
        <view class="iconfont iconshangpinxiangqing-fenxiang share__floatIcon" />
        <view style="font-size: 20rpx">分享</view>
      </view>
    </view>
    <view class="cell">
      <uv-cell-group v-if="goodDetail.giftPoint && goodDetail.giftPoint > 0">
        <uv-cell icon="gift-fill" :title="'购买可获取' + goodDetail.giftPoint + '积分'" :titleStyle="{ fontSize: '26rpx', color: '#8a8a8a' }" />
      </uv-cell-group>
      <view v-if="couponList.length > 0" class="cell__line" @click="getCoupon('coupon')">
        <text class="cell__title">优惠</text>
        <view class="cell__title--right" style="width: 600rpx; margin-right: 20rpx">
          <view class="cell__title--right--max">
            <view v-for="(item, index) in couponList" :key="index">
              <view v-if="item.couponType === 0" class="cell__title--right--max--coupon"> 无门槛{{ item.discount }}折 </view>
              <view v-if="item.couponType === 1" class="cell__title--right--max--coupon"> 无门槛减{{ item.discountAmount }}元 </view>
              <view v-if="item.couponType === 2" class="cell__title--right--max--coupon"> 满{{ item.conditionAmount }}打{{ item.discount }}折 </view>
              <view v-if="item.couponType === 3" class="cell__title--right--max--coupon">
                满{{ item.conditionAmount }}减{{ item.discountAmount }}元
              </view>
            </view>
          </view>
        </view>
        <uv-icon name="arrow-right" style="font-size: 32rpx; font-weight: bold" />
      </view>
      <view v-if="goodDetail.serviceIds" class="cell__line">
        <text class="cell__title">服务</text>
        <view class="cell__title--right" style="padding-left: 16rpx">{{ goodDetail.serviceIds }}</view>
      </view>
      <view v-if="showFullScaleRules.length > 0" class="cell__line" @click="getCoupon('reduce')">
        <text class="cell__title">满减</text>
        <view class="cell__title--right">
          <view class="cell__title--right--max">
            <view v-for="(item, index) in fullScaleRules" :key="index">
              <view v-if="item.ruleType === 0" style="display: flex">
                满{{ item.conditionAmount }}减{{ item.discountAmount }}元
                <view v-if="fullScaleRules.length !== index + 1" style="margin-right: 20rpx">,</view>
              </view>
              <view v-if="item.ruleType === 1" style="display: flex">
                满{{ item.conditionAmount }}打{{ item.discount }}折
                <view v-if="fullScaleRules.length !== index + 1" style="margin-right: 20rpx">,</view>
              </view>
            </view>
          </view>
        </view>
      </view>
      <view v-if="goodDetail.openSpecs && goodDetail.skuStocks.length > 1" class="cell__line" @click="submit">
        <text class="cell__title">选择</text>
        <view class="cell__title--right" style="padding-left: 16rpx">
          <view v-if="getNorms.specs" style="margin-right: 20rpx; color: #000"> 已选：{{ getNorms.specs }} </view>
          <view v-else style="margin-right: 20rpx">请选择规格</view>
          <uv-icon name="arrow-right" style="font-size: 32rpx; font-weight: bold" />
        </view>
      </view>
      <view v-if="goodDetail.productAttributes.length > 0" class="cell__line" @click="getCoupon('para')">
        <text class="cell__title">参数</text>
        <view class="cell__title--right" style="padding-left: 16rpx">
          <view style="margin-right: 20rpx; color: #b1afaf">查看商品参数</view>
          <uv-icon name="arrow-right" style="font-size: 32rpx; font-weight: bold" />
        </view>
      </view>
      <UPopup v-model:show="show" mode="bottom" :customStyle="{ flex: 'auto' }" round="20" @close="onClose">
        <!-- 优惠券弹窗内容 -->
        <view v-if="couponShow" style="height: 800rpx">
          <view class="cell__pop--reduce--text">店铺优惠</view>
          <MIcon
            name="iconshangpinxiangqing-guanbi"
            size="32rpx"
            style="position: fixed; margin-top: 30rpx; right: 30rpx; z-index: 101"
            @click="onClose"
          />
          <view class="coupPage">
            <view v-for="(coupon, index) in couponList" v-show="couponList.length > 0" :key="index" class="coupon">
              <Coupon v-if="coupon.totalAvailable > 0" :coupon="coupon" state="noUse" @myCouponGet="myCouponGet" />
            </view>
          </view>
        </view>
        <!-- 满减弹窗内容 -->
        <view v-if="reduceShow" class="cell__pop--reduce">
          <view class="cell__pop--reduce--text">满就减活动</view>
          <view class="cell__pop--reduce--list">
            <view v-for="(item, index) in fullScaleRules" :key="index" class="list--value">
              <view v-if="item.ruleType === 0"> 满{{ item.conditionAmount }}减{{ item.discountAmount }}元 </view>
              <view v-if="item.ruleType === 1">满{{ item.conditionAmount }}打{{ item.discount }}折</view>
            </view>
          </view>
        </view>
        <!-- 商品参数弹窗内容 -->
        <view v-if="paraShow" class="popup__attr" style="padding-bottom: 100rpx">
          <view class="cell__pop--reduce--text" style="font-weight: normal">商品参数</view>
          <view v-for="(item, index) in goodDetail.productAttributes" :key="index" :style="'padding-top:' + (index === 0 ? '100rpx' : '')">
            <view class="popup__attr--item">
              <view class="popup__attr--item--left">{{ item.name }}</view>
              <view class="popup__attr--item--right">{{ item.value }}</view>
            </view>
          </view>
          <view class="center--middle" @click="onClose">
            <view class="popup__back">
              <view class="popup__attr--finish">完成</view>
            </view>
          </view>
        </view>
      </UPopup>
    </view>
    <view v-if="hasOpenEvaluate" class="assess">
      <view class="assess__top">
        <view class="assess__top--left">评价({{ evaluateOverview.total }})</view>
        <view class="assess__top--right" @click="gotoReviews">
          <view style="color: #fc425a">好评{{ evaluateOverview.praiseRate }}%</view>
          <uv-icon name="arrow-right" style="font-size: 32rpx; font-weight: bold" />
        </view>
      </view>
      <view v-if="evaluateItem?.id" class="assess__list" @click="gotoReviews">
        <view class="assess__list--button center--middle">
          全部
          <view class="assess__list--button--num">({{ evaluateOverview.all || 0 }})</view>
        </view>
        <view class="assess__list--button center--middle">
          有内容
          <view class="assess__list--button--num">({{ evaluateOverview.hasContent || 0 }})</view>
        </view>
        <view class="assess__list--button center--middle">
          有图片
          <view class="assess__list--button--num">({{ evaluateOverview.hasPicture || 0 }})</view>
        </view>
      </view>
      <view v-if="evaluateItem?.userName" class="assess__user">
        <view style="display: flex; align-items: center">
          <view class="assess__user--left">
            <view class="avatar">
              <image :src="evaluateItem.userAvatarUrl" />
            </view>
          </view>
          <view class="assess__user--right">
            <view class="name">{{ evaluateItem.userName }}</view>
            <view>
              {{ evaluateItem.createTime }}
              <block v-if="evaluateItem.specs">规格：{{ evaluateItem.specs }}</block>
            </view>
          </view>
        </view>
        <view class="assess__bottom">{{ evaluateItem.comment }}</view>
        <!-- <view class="assets__reply" wx:if="{{evaluateItem.reply}}">
                <view class="assess__reply__top">
                    <view class="assess__reply__top--service">掌柜回复：</view>
                    <view class="assess_reply__top--time">{{item.replyTime}}</view>
                </view>
                <view style="margin-top:20rpx">{{evaluateItem.reply}}</view>
            </view> -->
      </view>
    </view>
    <view class="shop--content">— 商品详情 —</view>
    <view class="shop--english">COMMODITY DETAILS</view>
    <!-- 商品详情富文本 -->
    <view class="goods__text">
      <uv-parse :content="goodDetail.detail" lazyLoad></uv-parse>
      <image
        v-if="!!shopInfo.miniBottomLog"
        class="miniBottomLog"
        :src="!!shopInfo.miniBottomLog ? shopInfo.miniBottomLog : ''"
        @click="pageLink('/pages/systemBanner/systemBanner')"
      />
      <Sign :text="'smart shop商城系统 v' + APP_VERSION" @click="pageLink('/pages/systemBanner/systemBanner')" />
    </view>
    <uv-safe-bottom></uv-safe-bottom>
    <!-- 底部按钮标签 -->
    <view v-if="recyclingsetting.entranceState" class="detail__bottom">
      <!-- iconweibiaoti12 -->
      <view class="detail__img" style="width: 100rpx" @click="showmenu">
        <image src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20210512/ce7fd458032a4a69a361ef9e0a0b8d25.png" />
      </view>
      <view v-if="menu" class="detail__menu" style="width: 100rpx; padding: 20rpx 0rpx; box-shadow: 6rpx 0rpx 10rpx 0rpx rgba(0, 0, 0, 0.1)">
        <view class="detail__img detail__margin" @click="returnHome">
          <MIcon name="iconshouye" size="52rpx" />
          <text class="detail__img--icon">店铺</text>
        </view>
        <uv-button open-type="contact" class="detail__img detail__margin">
          <MIcon name="iconxinbaniconshangchuan-" size="44rpx" />
          <text class="detail__img--icon">客服</text>
        </uv-button>
        <view
          v-if="goodDetail.status === 1 || goodDetail.status === 3"
          class="detail__img detail__margin"
          style="position: relative"
          @click="gotoCar"
        >
          <MIcon name="icon3" size="44rpx" />
          <text class="detail__img--icon">购物车</text>
          <view v-if="shopCarNum > 0" class="tip" :style="'width:' + (shopCarNum > 9 ? 54 : 36) + 'rpx; font-size:22rpx;'">
            {{ shopCarNum > 99 ? '99+' : shopCarNum }}
          </view>
        </view>
      </view>
      <view class="buy__btn" :style="'background-color:' + recyclingsetting.btnStyles[0].color + ';width:189rpx'" @click="torecycling">
        {{ recyclingsetting.btnStyles[0].btnName }}
      </view>
      <view
        v-if="goodDetail.status === 1 || goodDetail.status === 3"
        class="buy__btn"
        :style="'background-color:' + recyclingsetting.btnStyles[1].color + ';width:231rpx'"
        @click="submit"
      >
        加入购物车
      </view>
      <view v-if="goodDetail.status === 0" class="buy__btn" style="background-color: #e5e5e5; width: 400rpx"> 商品已下架 </view>
      <view v-if="goodDetail.status === 3" class="buy__btn" style="background-color: #6ae086"> 即将开始 </view>
      <view
        v-if="goodDetail.status === 1"
        class="buy__btn"
        :style="'background-color:' + recyclingsetting.btnStyles[2].color + ';width:231rpx'"
        @click="submit"
      >
        立即购买
      </view>
      <view v-if="goodDetail.status === 2" class="buy__btn" style="background-color: #e5e5e5; width: 400rpx"> 商品已售罄 </view>
      <view v-if="goodDetail.status === 4" class="buy__btn" style="background-color: #e5e5e5; width: 400rpx"> 活动已结束 </view>
    </view>
    <view v-else class="detail__bottom">
      <view class="detail__img" @click="returnHome">
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
      >
        <MIcon name="iconxinbaniconshangchuan-" size="50rpx" />
        <text class="detail__img--icon">客服</text>
      </uv-button>

      <view v-if="goodDetail.status === 1 || goodDetail.status === 3" class="detail__img" style="position: relative" @click="gotoCar">
        <MIcon name="icon3" size="44rpx" />
        <text class="detail__img--icon">购物车</text>
        <view v-if="shopCarNum > 0" class="tip" :style="'width:' + (shopCarNum > 9 ? 54 : 36) + 'rpx; font-size:22rpx;'">
          {{ shopCarNum > 99 ? '99+' : shopCarNum }}
        </view>
      </view>
      <view v-if="goodDetail.status === 1 || goodDetail.status === 3" class="buy__btn" @click="submit"> 加入购物车 </view>
      <view v-if="goodDetail.status === 0" class="buy__btn" style="background-color: #e5e5e5; width: 400rpx"> 商品已下架 </view>
      <view v-if="goodDetail.status === 3" class="buy__btn" style="background-color: #6ae086"> 即将开始 </view>
      <view v-if="goodDetail.status === 1" class="buy__btn" style="background-color: #ff0c19" @click="submit"> 立即购买 </view>
      <view v-if="goodDetail.status === 2" class="buy__btn" style="background-color: #e5e5e5; width: 400rpx"> 商品已售罄 </view>
      <view v-if="goodDetail.status === 4" class="buy__btn" style="background-color: #e5e5e5; width: 400rpx"> 活动已结束 </view>
    </view>
    <!-- 规格弹窗 -->
    <UPopup v-model:show="buyShow" mode="bottom" :overlay="true" round="20" @close="onClose">
      <SelectNorms
        v-if="buyShow"
        v-model:goodDetail="goodDetail"
        :norms="norms"
        :state="state"
        :getNorms="getNorms"
        @onClose="onClose"
        @addShopCar="addShopCar"
        @checkNorms="checkNorms"
      />
    </UPopup>
    <!-- 生成卡片分享 -->
    <UPopup v-model:show="shareVisible" mode="bottom" @close="onClose">
      <view class="share">分享</view>
      <MIcon
        name="iconshangpinxiangqing-guanbi"
        size="40rpx"
        style="position: fixed; margin-top: -44rpx; right: 30rpx; z-index: 101"
        @click="onClose"
      />
      <view v-if="clickShare">
        <view class="shareWay">
          <!-- #ifdef MP-WEIXIN -->
          <view class="shareWay__side">
            <uv-button
              open-type="share"
              style="border-radius: 100rpx; display: flex; align-items: center"
              :customStyle="{
                border: 'none',
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'center',
                height: '110rpx',
              }"
              @click="onClose"
            >
              <image src="https://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/api/WeChat.png" />
            </uv-button>
            <view class="shareWay__side--text">微信好友</view>
          </view>
          <!-- #endif -->
          <view class="shareWay__side" @click="shareBrCard">
            <image src="https://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/api/poster.png" />
            <view class="shareWay__side--text">生成海报</view>
          </view>
        </view>
        <view class="shareTip">
          <view class="shareTip__tip">和好友分享你发现的好物</view>
        </view>
      </view>
      <view v-else style="display: flex; flex-wrap: wrap; justify-content: center">
        <view class="screenshot">好商品就要分享 点击按钮即可分享</view>
        <uv-button open-type="share" class="screenshotBtn" @click="onClose">分享给您的好友</uv-button>
      </view>
    </UPopup>
    <!-- 授权弹窗 -->
    <Auth v-model:authType="authType" />
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onBeforeUnmount, nextTick, getCurrentInstance } from 'vue'
import { onReady, onShow, onLoad, onShareAppMessage, onShareTimeline } from '@dcloudio/uni-app'
import {
  getGoodDetail,
  getCouponList,
  getFullList,
  fetchProductEvaluate,
  getProductEvaluateOverview,
  checkIsCollect,
  userAddCollect,
  userDelCollect,
  getImgCode,
  userFooterAdd,
  getRebateInSet,
  joinChain,
} from '@/api/modules/goods'
import { getMemberCardInfo } from '@/api/modules/member'
import { getCarsList } from '@/api/modules/shopCar'
import { shareGetIntegral, getIntegralRule } from '@/api/modules/integral'
import { getOneCoupon } from '@/api/modules/coupon'
import { addCars } from '@/api/modules/shopCar'
import { base64src } from '@/utils/base64src'
import Sign from '@/components/Sign/Sign.vue'
import SelectNorms, { type Norms } from '@/components/SelectNorms/SelectNorms.vue'
import Coupon from '@/components/CouponCom/Coupon/Coupon.vue'
import MIcon from '@/components/MIcon/MIcon.vue'
import Auth from '@/components/Auth/Auth.vue'
import canvasUrl from './canvas'
import { useSettingStore } from '@/store/modules/setting'
import { useUserStore } from '@/store/modules/user'
import { useShopsetStore } from '@/store/modules/shopset'
import { $Popup } from '@/utils/helper'
import url from './components/animation'
import { APP_LAUNCH } from '@/utils/emitNameConfig'
import { storeToRefs } from 'pinia'
import type { EvaluateItem, GoodDetailType, interCountDetail, interTimeData, Point } from './types'

// 获取当前组件实例
const instance = getCurrentInstance()

const userStore = useUserStore()
const { isLogin, userInfo } = storeToRefs(userStore)

// 响应式状态
const showFullScaleRules = ref([])
const goodDetail = ref<GoodDetailType>({
  imgUrls: [],
  skuStocks: [],
  productAttributes: [],
})
const norms = ref<Norms>({} as Norms)
const getNorms = ref<Norms>({} as Norms)
const buyShow = ref(false)
const show = ref(false)
const couponShow = ref(false)
const reduceShow = ref(false)
const paraShow = ref(false)
const timeData = ref<interTimeData>({
  hours: '00',
  seconds: '00',
  minutes: '00',
})
const state = ref('')
const shareVisible = ref(false)
const imgUrl = ref('')
const cardImgUrl = ref('')
const modalRef = ref()
const showAll = ref(false)
const goodId = ref('')
const goodsVideoUrl = computed(() => {
  return goodDetail.value.videoUrl
})
const videoImg = ref('')
const autoplay = ref(true)
const current = ref(1)
const videoContext = ref<UniApp.VideoContext | null>(null)
const showCountDown = computed(() => {
  return !!goodDetail.value.activityStartTime
})
const countDetail = ref<interCountDetail>({
  status: '',
  toBeginTime: 0,
  toEndTime: 0,
  remainTime: 0,
})
const remainTime = ref(0)
const status = ref<Record<string, string>>({
  aboutToBegin: '距开始',
  beginning: '距结束',
  ended: '已结束',
})
const fullScaleRules = ref<any[]>([])
const couponList = ref<any[]>([])
const authType = ref(false)
const evaluateItem = ref<EvaluateItem>({ userName: '' })
const evaluateOverview = ref<Obj>({})
const shopCarNum = ref(0)
const clickShare = ref(false)
const openMember = ref(false)
const integralVos = ref<any[]>([])
const memState = ref(0)
const rebateRatio = ref(0)
const isRebate = ref(false)
const hideGoodBox = ref(true)
const feiBox = ref('')
const busPos = ref<Point>({ x: 0, y: 0 })
const finger = ref<Point>({ x: 0, y: 0 })
const hasJoinType = ref(false)
const chainId = ref(null)
const APP_VERSION = ref(import.meta.env.VITE_APP_APP_SHOW_VERSION)
const shopInfo = ref<Obj>({})
const recyclingsetting = ref<Obj>({})
const menu = ref(false)
const options = ref<Obj>({})

// 计算属性
const isMall = computed(() => {
  return useSettingStore().shopSetting?.appMode || 'MALL'
})

const hasOpenEvaluate = computed(() => {
  return useSettingStore().shopSetting.hasOpenEvaluate
})

// 工具方法
const formatNumber = (n: Long) => {
  const nStr = n.toString()
  return nStr[1] ? nStr : '0' + nStr
}

// 主要方法
const openMemberHandle = () => {
  uni.navigateTo({
    url: '/subcontract/pages/member/member',
  })
}

const longCopy = () => {
  uni.setClipboardData({
    data: goodDetail.value.saleDescribe || '',
    success: () => {
      uni.showToast({
        title: '复制成功',
      })
    },
  })
}

const getCoupon = (mode: string) => {
  couponShow.value = false
  reduceShow.value = false
  paraShow.value = false
  show.value = true
  switch (mode) {
    case 'coupon':
      if (!isLogin.value) {
        authType.value = true
        return
      }
      couponShow.value = true
      break
    case 'reduce':
      reduceShow.value = true
      break
    case 'para':
      paraShow.value = true
      break
    default:
      break
  }
}

const onClose = async () => {
  show.value = false
  buyShow.value = false
  shareVisible.value = false
  await nextTick()
  couponShow.value = false
  reduceShow.value = false
  paraShow.value = false
}

const showShare = () => {
  clickShare.value = true
  shareVisible.value = true
}

const gotoReviews = () => {
  uni.navigateTo({
    url: `/subcontract/pages/reviews/reviews?id=${options.value.id}`,
  })
}

const checkNorms = (check: any) => {
  getNorms.value = check
}

const gotoCar = () => {
  const name = useSettingStore().tabBar.list.filter((item) => item.name === 'shopCar')
  useSettingStore().setIndexTitle(name[0].text)
  useSettingStore().setCurrentPageId(name[0].id)
  uni.reLaunch({
    url: '/pages/index/index?page=shopCar',
  })
}

const returnHome = () => {
  useSettingStore().backHome()
}

const hiddenCrad = () => {
  modalRef.value.close()
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

const addShopCar = async (normData: any) => {
  if (normData.index) {
    gotoSubmit()
    return
  }
  goodDetail.value.productId = goodDetail.value.id
  goodDetail.value.productName = goodDetail.value.name
  const parms = {
    goodsNumber: goodDetail.value.goodsNumber,
    id: '',
    saleMode: goodDetail.value.saleMode,
    productId: goodDetail.value.productId,
    productName: goodDetail.value.productName,
    pic: goodDetail.value.pic,
    productSn: goodDetail.value.productSn,
    skuId: goodDetail.value.skuId,
    skuStocks: goodDetail.value.skuStocks,
    status: goodDetail.value.status,
    activityStartTime: goodDetail.value.activityStartTime,
    activityEndTime: goodDetail.value.activityEndTime,
    activityId: goodDetail.value.activityId,
    selectStatus: 1,
  }

  //把点击每一项的对应的商品图保存下来，就是飞向购物车的图片 页面上隐藏了跳动效果
  feiBox.value = goodDetail.value.pic || ''
  // 如果good_box正在运动
  if (!hideGoodBox.value) return
  //当前点击位置的x，y坐标
  const topPoint: Point = { x: 0, y: 0 }
  const fingerValue = finger.value

  if (fingerValue.y < busPos.value.y) {
    topPoint.y = fingerValue.y - 80
  } else {
    topPoint.y = busPos.value.y - 80
  }

  if (fingerValue.x < busPos.value.x) {
    topPoint.x = Math.abs(fingerValue.x - busPos.value.x) / 2 + fingerValue.x
  } else {
    topPoint.x = busPos.value.x
    fingerValue.x = busPos.value.x
  }

  // 类型断言来解决bezier参数类型问题
  const linePos = url.methods.bezier([fingerValue, topPoint, busPos.value], 30)
  addCars(parms)
    .then(() => {
      uni.showToast({
        title: '添加成功',
        icon: 'none',
      })
      buyShow.value = false
      startAnimation(linePos)
      getShopCarNum()
      userStore.getShopCarNum()
    })
    .catch((res) => {
      uni.showToast({
        title: res,
        icon: 'none',
      })
    })
}

const startAnimation = (linePos: any) => {
  let index = 0
  linePos['bezierPoints'].reverse()
  hideGoodBox.value = false
  const timer = setInterval(() => {
    index++
    if (index >= 28) {
      clearInterval(timer)
      hideGoodBox.value = true
    }
  }, 20)
}

const gotoSubmit = () => {
  const items: any[] = []
  items.push({
    activityId: goodDetail.value.activityId,
    activityProductId: goodDetail.value.activityProductId,
    dominoState: goodDetail.value.dominoState,
    number: goodDetail.value.goodsNumber,
    skuId: goodDetail.value.skuId,
  })
  uni.navigateTo({
    url: `/modules/pages/submit/submit?items=${encodeURIComponent(JSON.stringify(items))}`,
  })
  buyShow.value = false
}

const getData = (options: any) => {
  getMemberByCard(options)
}

const getProductEvaluate = async (productId: any) => {
  try {
    const res = await fetchProductEvaluate({
      productId,
      type: 0,
      size: 10,
      current: 1,
    })
    evaluateItem.value = res.list[0]
  } catch (error) {
    $Popup.toast(error || '评价列表获取失败')
  }

  try {
    const res = await getProductEvaluateOverview({
      productId,
    })
    evaluateOverview.value = res
  } catch (error) {
    $Popup.toast(error || '评价概况获取失败')
  }
}

const getMemberAndReb = async () => {
  if (!isLogin.value) return
  const res = await getRebateInSet({})
  if (!res) return
  const { rebateCondition, rebateRatio: rebateRatioVal } = res
  const userMemberInfo = userInfo.value.infoExtends.memberInfo[0]
  const state = userMemberInfo.state
  rebateRatio.value = rebateRatioVal ? rebateRatioVal : 0
  if (state === 1) {
    isRebate.value = rebateCondition.indexOf(state) !== -1 ? true : false
  } else {
    isRebate.value = rebateCondition.indexOf(0) !== -1 ? true : false
  }
}

const getGood = async (id: any) => {
  const userMemberInfo = userInfo.value.infoExtends.memberInfo[0]
  // 用户会员状态 0未注册 1已注册 2会员到期
  const state = userMemberInfo.state
  const memRank = userMemberInfo.rankCode
  // 会员持有等级
  const memberLevel = userMemberInfo.rankCode
  const openMemberVal = useSettingStore().shopSetting.openMember
  const res = await getGoodDetail(id, {})
  try {
    res.imgUrls = [...(res.albumPics?.split(',') ?? []), ...(res.skuStocks?.map((item: any) => item.pic)?.filter(Boolean) ?? [])]
    res.serviceIds = res.serviceIds?.split(',')?.join(' · ') ?? ''
    res.detail = res.detail.replace(/<img/g, '<img style="width:100%;height:auto;display:block"')
    if (openMemberVal) {
      // 根据会员卡等级 显示商品不同会员价
      const integralItem = integralVos.value.filter((item) => item.rankCode === memRank)
      // 积分显示翻倍
      const integralItemValue = integralItem[0] ? integralItem[0].memberPrivilegeVos.filter((v: any) => v.ptype === '3') : []
      if (integralItemValue.length > 0) {
        res.giftPoint = Number(res.giftPoint) * Number(integralItemValue[0].value)
      }
      if (state !== 1 && integralVos.value) {
        integralVos.value.forEach((item, index) => {
          if (!item.isOpen) {
            integralVos.value.splice(index, integralVos.value.length - index + 1)
          }
        })
        let value: any = null
        integralVos.value.forEach((item) => {
          item.memberPrivilegeVos.forEach((memItem: any) => {
            if (memItem.ptype === '1' && memItem.value) {
              value = memItem.value
            }
          })
        })
        res.skuStocks.forEach((skuItem: any) => {
          if (skuItem.memberType !== 0 && skuItem.memberType !== 3 && skuItem.memberPrices.length > 0) {
            skuItem.memberPrices.forEach((memItem: any) => {
              skuItem.memPrice = skuItem.memberType === 1 ? memItem.memberPrice : (skuItem.price * memItem.memberPrice) / 100
              skuItem.memPrice = skuItem.memPrice.toFixed(2)
            })
          }
          if (skuItem.memberType === 3) {
            skuItem.memPrice = (skuItem.price * value) / 100
            skuItem.memPrice = skuItem.memPrice.toFixed(2)
          }
          // skuItem.memPrice = skuItem.memPrice.toFixed(2);
        })
      }

      // 处理商品返利相关信息
      res.skuStocks.forEach((skuItem: any) => {
        skuItem.skuRebates.forEach((rebItem: any) => {
          if (skuItem.rebateType !== 0 && skuItem.rebateType !== 3 && skuItem.skuRebates.length > 0) {
            skuItem.rebPrice = skuItem.rebateType === 1 ? rebItem.rebatePrice : (skuItem.price * rebItem.rebatePrice) / 100
            skuItem.rebPrice = skuItem.rebPrice.toFixed(2)
          }
        })
        if (skuItem.rebateType === 3) {
          skuItem.rebPrice = (Number(skuItem.price) * rebateRatio.value) / 100
          skuItem.rebPrice = skuItem.rebPrice.toFixed(2)
          const rebPriceList: any[] = []
          res.skuStocks.forEach((item: any) => {
            if (item.rebPrice) {
              rebPriceList.push(item.rebPrice)
            }
          })
        }
        const rebPriceList: any[] = []
        res.skuStocks.forEach((item: any) => {
          if (item.rebPrice) {
            rebPriceList.push(item.rebPrice)
          }
        })
        res.rebPrice = rebPriceList.length > 0 ? Math.min(...rebPriceList).toFixed(2) : null
        if (skuItem.memberType !== 0 && skuItem.memberType !== 3 && skuItem.memberPrices.length > 0) {
          skuItem.memberPrices.forEach((memItem: any) => {
            if (memItem.memberLevelId === memberLevel) {
              skuItem.memPrice = skuItem.memberType === 1 ? memItem.memberPrice : (skuItem.price * memItem.memberPrice) / 100
              skuItem.memPrice = skuItem.memPrice.toFixed(2)
            }
          })
        }
        if (skuItem.memberType === 3 && integralVos.value) {
          const integralVosVal = integralVos.value
          integralVosVal.forEach((memItem) => {
            if (memItem.rankCode === memRank) {
              const value = memItem.memberPrivilegeVos.filter((inItem: any) => {
                if (inItem.isSelected === 1 && inItem.ptype === '1') {
                  return inItem.value
                }
                return false
              })
              if (value.length > 0) {
                skuItem.memPrice = (skuItem.price * value[0].value) / 100
                skuItem.memPrice = skuItem.memPrice.toFixed(2)
              }
            }
          })
        }
        const memPriceList: any[] = []
        res.skuStocks.forEach((item: any) => {
          if (item.memPrice) {
            memPriceList.push(item.memPrice)
          }
        })
        res.memPrice = memPriceList.length > 0 ? Math.min(...memPriceList).toFixed(2) : null
        if (skuItem.memberType === 0) {
          res.memPrice = null
        }
      })
    }
    res.stock = 0
    const type = res.skuStocks.every((v: any) => {
      return v.stock === 0
    })
    res.status = type ? 2 : res.status

    // 单规格商品处理显示价格
    if (res.skuStocks.list === 1) {
      res.price = res.price.toFixed(2)
      res.actPrice = res.originalPrice.toFixed(2)
    } else {
      // 多规格商品处理显示价格
      const priceList = res.skuStocks.map((item: any) => item.originalPrice)
      res.price = Math.min(...priceList).toFixed(2)
      const actPriceList = res.skuStocks.map((item: any) => item.price)
      res.actPrice = Math.min(...actPriceList).toFixed(2)
      res.leadPrice = Math.max(...actPriceList).toFixed(2)
    }
    res.skuStocks.forEach((item: any) => {
      item.price = item.price.toFixed(2)
      res.stock += item.stock
      res.sale += item.sale
    })
    res.savePrice = res.actPrice === '0.00' ? res.price : res.actPrice
    res.savePrice = (Number(res.savePrice) - Number(res.memPrice)).toFixed(2)
    goodDetail.value = res
    goodId.value = id
    clickShare.value = true
    memState.value = state === null ? 0 : state
    openMember.value = openMemberVal
    getCollectType(id)
    await nextTick()
    addFooterAdd()
    getCouponActivity(id)
    getFullActivity(id)
    getProductEvaluate(id)
    getShopCarNum()
    jungeMentHasJoinChain()
  } catch (err) {
    console.log(err)
    // 根据报错查看到商品是哪一相关模块报错
    $Popup
      .alert({
        title: '提示',
        content: '商品已删除，点击确认返回首页',
      })
      .then(() => {
        useSettingStore().backHome()
      })
    return
  }
}

const goIntoChain = () => {
  const id = chainId.value
  uni.navigateTo({
    url: `/subcontract/pages/chainDetail/chainDetail?id=${id}`,
  })
}

const jungeMentHasJoinChain = async () => {
  const res = await joinChain(goodDetail.value.id, {})
  try {
    if (res.code === 200) {
      hasJoinType.value = true
      chainId.value = res
    } else {
      hasJoinType.value = false
    }
  } catch (error) {
    console.log(error)
  }
}

const getMemberByCard = async (options: any) => {
  if (!isLogin.value) {
    getGood(options.id)
    getMemberAndReb()
    return
  }
  const res = await getMemberCardInfo({})
  const tem = res.consumeVos || res.integralVos || res.paymentVos || []
  integralVos.value = tem
  getGood(options.id)
  getMemberAndReb()
}

const pageLink = (url: string) => {
  uni.navigateTo({
    url: `/subcontract${url}`,
  })
}

const onSlideChange = (e: any) => {
  current.value = e.detail.current + 1
  if (current.value !== 1) {
    videoContext.value?.pause()
    autoplay.value = true
  }
}

const bofang = () => {
  autoplay.value = false
}

const endbofng = () => {
  autoplay.value = true
}

const getShopCarNum = async () => {
  if (!isLogin.value) return
  const res = await getCarsList()
  const useList: any[] = []
  const noList: any[] = []
  const list = res
  if (!res || res.length < 1) {
    shopCarNum.value = 0
    return
  }
  // 购物车数量需提出失效商品数量 且数量要计算不同规格的数量总和
  list.forEach((item: any) => {
    item.skuList = []
    item.skuStocks.forEach((v: any) => {
      item.skuList.push(v.id)
    })
    const type = item.skuStocks.every((v: any) => {
      return v.stock === 0
    })
    item.status = type ? 2 : item.status
    if (item.deleted === 1) {
      item.status = 2
    }
    item.status = item.skuList.indexOf(item.skuId) === -1 ? 0 : item.status
    if (item.activityEndTime) {
      item.status = item.activityStatus === '1' || item.activityStatus === '0' ? 1 : 0
    }
    if (item.status === 1) {
      useList.push(item)
    } else {
      noList.push(item)
    }
  })
  const goodsNumber = useList.reduce((preVal, item) => {
    return preVal + item.goodsNumber
  }, 0)
  shopCarNum.value = goodsNumber
}

const addFooterAdd = () => {
  if (!isLogin.value) return
  const datail = goodDetail.value
  if (datail.id) {
    const parma = {
      originalPrice: datail.price,
      productId: datail.id,
      productName: datail.name,
      productPic: datail.pic,
      productPrice: datail.actPrice,
      status: datail.status,
    }
    userFooterAdd(parma)
  }
}

const handleChange = (e: any) => {
  const { days, hours, minutes, seconds } = e.detail
  const h = hours + days * 24
  timeData.value = {
    hours: formatNumber(h),
    minutes: formatNumber(minutes),
    seconds: formatNumber(seconds),
  }
}

const handleFinish = () => {
  const { status, toEndTime, remainTime: remainTimeVal } = countDetail.value
  if (status === 'aboutToBegin') {
    countDetail.value.status = 'beginning'
    countDetail.value.toBeginTime = 0
    countDetail.value.toEndTime = toEndTime - remainTimeVal
    getGood(goodDetail.value.id)
  }
  if (status === 'beginning') {
    countDetail.value.status = 'ended'
    countDetail.value.toBeginTime = 0
    countDetail.value.toEndTime = 0
  }
  if (toEndTime === 0) {
    goodDetail.value.status = 4
  }
}

const getCollectType = async (productId: any) => {
  if (!isLogin.value) return
  const res = await checkIsCollect({ productId })
  goodDetail.value.isCollect = res
}

const addCollect = () => {
  const datail = goodDetail.value
  const parma = {
    originalPrice: datail.price,
    productId: datail.id,
    productName: datail.name,
    productPic: datail.pic,
    productPrice: datail.actPrice,
    status: datail.status,
  }
  const list: any[] = []
  list.push(parma)
  if (datail.isCollect) {
    cancelCollect(parma)
    return
  }
  userAddCollect(list)
    .then(() => {
      uni.showToast({
        title: '收藏成功',
        icon: 'none',
      })
      goodDetail.value.isCollect = true
    })
    .catch((err) => {
      console.log(err)
    })
}

const cancelCollect = (parma: any) => {
  userDelCollect(parma).then(() => {
    uni.showToast({
      title: '取消成功',
      icon: 'none',
    })
    goodDetail.value.isCollect = false
  })
}

const submit = () => {
  // 新用户选择规格需先登录
  if (!isLogin.value) {
    authType.value = true
    return
  }
  let normsVal: Norms = {} as Norms
  const list = goodDetail.value.skuStocks
  if (getNorms.value.id) {
    normsVal = goodDetail.value.skuStocks[0]
    normsVal.pic = list.length === 1 ? goodDetail.value.pic : goodDetail.value.skuStocks[0].pic
    normsVal.price = goodDetail.value.actPrice !== '0.00' ? goodDetail.value.actPrice! : goodDetail.value.price!
    normsVal = getNorms.value
  } else {
    normsVal.pic = goodDetail.value.albumPics?.split(',')[0]
    if (goodDetail.value.actPrice !== '0.00') {
      normsVal.price = goodDetail.value.actPrice ? goodDetail.value.actPrice : goodDetail.value.price!
    } else {
      normsVal.price = goodDetail.value.price!
    }
    const skuStock = goodDetail.value.skuStocks[0]
    normsVal.maxNum = skuStock.perLimit < skuStock.stock ? skuStock.perLimit : skuStock.stock
    normsVal.id = ''
    normsVal.memPrice = goodDetail.value.memPrice && memState.value === 1 ? goodDetail.value.memPrice : ''
  }
  norms.value = normsVal
  buyShow.value = true
}

const getCouponActivity = (id: any) => {
  const couponListVal: any[] = []
  getCouponList({
    productId: id,
  }).then((res) => {
    res.forEach((item: any, index: number) => {
      item.hasUse = false
      item.index = index
      item.restNum = item.available > item.totalAvailable ? item.totalAvailable : item.available
    })
    res.forEach((item: any) => {
      if (item.available > 0 && item.totalAvailable > 0) {
        couponListVal.push(item)
      }
    })
    couponList.value = couponListVal
  })
}

const myCouponGet = (options: any) => {
  getOneCoupon(options.id, {}).then(() => {
    uni.showToast({
      title: '领取成功',
      icon: 'none',
    })
    getCouponActivity(goodId.value)
  })
}

const getFullActivity = async (id: any) => {
  const res = await getFullList({
    productId: id,
  })
  if (res.length === 0) return
  fullScaleRules.value = res[0].fullScaleRules
}

const shareBrCard = () => {
  if (!isLogin.value) {
    authType.value = true
    return
  }
  let codeImg = ''
  showLoad()
  uni.pageScrollTo({
    scrollTop: 0,
    duration: 200,
  })
  modalRef.value.open()
  shareVisible.value = false
  let wxaGetwxacode: Obj = {}
  if (isMall.value === 'MALL') {
    const transData = userInfo.value.transData as any
    transData.goodData.launchArea = goodDetail.value.launchArea
    transData.goodData.id = goodDetail.value.id
    const goodTranObj = `${transData.groupType} ${transData.isGrouper} ${userInfo.value.shopUserId}`
    wxaGetwxacode = {
      path: `subcontract/pages/detail/detail?id=${goodDetail.value.id}&goodTranObj=${goodTranObj}`,
      width: 0,
    }
  } else {
    wxaGetwxacode = {
      path: `subcontract/pages/detail/detail?id=${goodDetail.value.id}`,
      width: 0,
    }
  }
  getImgCode(wxaGetwxacode).then((result) => {
    codeImg = result
    base64src(codeImg, (res: string) => {
      codeImg = res
      if (!goodDetail.value.pic) {
        uni.showToast({
          title: '图片读取失败',
          icon: 'none',
        })
        uni.hideLoading()
        return
      }
      // 处理下开发环境图片跨域问题
      // 使用vite自带的server proxy 代理
      // 将图片路径替换为代理路径
      const proxyImgUrl =
        import.meta.env.MODE === 'development' ? goodDetail.value.pic.replace(import.meta.env.VITE_APP_OSS, '/proxy/img') : goodDetail.value.pic
      uni.downloadFile({
        url: proxyImgUrl,
        success: (v) => {
          // 使用类型断言解决Good类型不匹配问题
          canvasUrl.methods.canvCard(goodDetail.value as any, 0, v.tempFilePath, codeImg, instance).then((response: string) => {
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

const getShareData = () => {
  if (isMall.value === 'MALL') {
    return {
      title: goodDetail.value.name,
      path: `/subcontract/pages/detail/detail?id=${goodDetail.value.id}`,
      imageUrl: imgUrl.value,
    }
  } else {
    let transData = userInfo.value.transData as any
    transData.goodData.launchArea = goodDetail.value.launchArea
    transData.goodData.id = goodDetail.value.id
    const goodTranObj = `${transData.groupType} ${transData.isGrouper} ${userInfo.value.shopUserId}`
    transData = JSON.stringify(transData)
    return {
      title: goodDetail.value.name,
      path: `/subcontract/pages/detail/detail?id=${goodDetail.value.id}&goodTranObj=${goodTranObj}`,
      imageUrl: imgUrl.value,
    }
  }
}

const showmenu = () => {
  menu.value = !menu.value
}

const torecycling = () => {
  uni.navigateTo({
    url: '/subcontract/pages/recycling/recycling',
  })
}

// 生命周期钩子
onReady(() => {
  videoContext.value = uni.createVideoContext('myVideo')
  uni.getSystemInfo({
    success: (res) => {
      //可视窗口宽度
      const ww = res.windowWidth
      //可视窗口高度
      const hh = res.windowHeight
      busPos.value['x'] = ww * 0.5
      busPos.value['y'] = hh * 0.9
      finger.value['x'] = ww * 0.3
      finger.value['y'] = hh * 0.9
    },
  })
})

onLoad((opt: any) => {
  options.value = opt || {}
  if (opt?.goodTranObj) {
    getGoodDetail(opt.id, {}).then(() => {
      const goodData = {}
      const newList = opt.goodTranObj.split(' ')
      const transData = {
        shopUserId: newList[3],
        groupType: newList[1],
        isGrouper: newList[2],
        goodData: goodData,
      }
      opt.transData = JSON.stringify(transData)
      options.value = opt
    })
  }
  if (!opt?.transType) {
    getData(opt)
  }
  uni.$on(APP_LAUNCH, () => {
    getData(opt)
  })
  recyclingsetting.value = useSettingStore().recyclingsetting
})

onBeforeUnmount(() => {
  uni.$off(APP_LAUNCH)
})

onShow(() => {
  // #ifdef MP-WEIXIN
  uni.hideHomeButton()
  // #endif
  shareVisible.value = false
  shopInfo.value = useShopsetStore().shopInfo
  // #ifndef H5
  uni.onUserCaptureScreen(() => {
    clickShare.value = false
    shareVisible.value = true
  })
  // #endif
})

onShareAppMessage(() => {
  getIntegralRule({}).then((res) => {
    if (res[0].open) {
      shareGetIntegral({}).then((res) => {
        if (res) {
          uni.showToast({
            title: `分享成功，获得${res}积分`,
            icon: 'none',
            duration: 5000,
          })
        }
      })
    }
  })
  return getShareData()
})

onShareTimeline(() => {
  const { title, path: query } = getShareData()
  return {
    title,
    query,
    imageUrl: goodDetail.value.imgUrls[0],
  }
})
</script>
<style lang="scss" scoped>
@import '@/assets/styles/detail/detail.scss';
.share_canvas_container {
  :deep(.uv-modal__content) {
    padding: 0;
    padding-top: 0 !important;
  }
  :deep(.uv-line) {
    display: none;
  }
}
</style>
