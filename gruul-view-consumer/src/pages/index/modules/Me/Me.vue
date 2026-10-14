<template>
  <view class="me_container">
    <view class="box">
      <view class="user--container" :style="headStyleConfig.headBackGround">
        <view :class="'user--layout ' + (userCenter.hideCartInlet ? '' : 'hideQrcode')">
          <view class="user--layout__info">
            <image v-if="isLogin" :src="userInfo.avatarUrl" class="user--layout__image" mode="aspectFill" @click="navSetUser" />
            <image v-else src="https://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/api/head_portrait.jpg" class="user--layout__image" />
            <view class="user--layout__container">
              <view class="user--layout__nikeName">
                <view v-if="isLogin" class="user--layout__text">
                  {{ userInfo.nikeName }}
                </view>
                <view v-else class="user--layout__text" @click="authLoad">登录 / 注册</view>
              </view>
              <view v-if="member.status === 1" class="user--layout__member" @click="pageLink('/pages/memberVip/memberVip')">
                <MIcon name="iconhuiyuan1" class="icon radius--inner__icon" size="22rpx" />
                <text>{{ member.rankName || '' }}</text>
              </view>
            </view>
          </view>
          <view v-if="userInfo.nikeName" class="user--layout__icon">
            <MIcon
              name="iconcode"
              size="50rpx"
              style="
                width: 90rpx;
                height: 90rpx;
                background-color: rgba(0, 0, 0, 0.08);
                border-radius: 100rpx;
                color: rgba(255, 255, 255, 0.9);
                display: flex;
                align-items: center;
                justify-content: center;
              "
              @click="openQrcode"
            />
            <view class="user--layout__icon__text">分销码</view>
          </view>
        </view>
        <view class="content">
          <view class="content__all">
            <view v-if="userInfo.nikeName" class="content__all--item" @click="balanceJumpHandle">
              <view style="font-weight: bold; font-size: 32rpx">
                {{ member.supplyAndRebateBonus ? member.supplyAndRebateBonus : 0 }}
              </view>
              <view style="margin-top: 6rpx; font-size: 26rpx">余额</view>
            </view>
            <view>|</view>
            <view class="content__all--item" @click="gotoIntegralShop">
              <view style="font-weight: bold; font-size: 32rpx">{{ integral }}</view>
              <view style="margin-top: 6rpx; font-size: 26rpx">积分</view>
            </view>
            <view>|</view>
            <view class="content__all--item" @click="gotoCollect">
              <view style="font-weight: bold; font-size: 32rpx">{{ collectNum }}</view>
              <view style="margin-top: 6rpx; font-size: 26rpx">收藏</view>
            </view>
            <view>|</view>
            <view class="content__all--item" @click="gotoFooter">
              <view style="font-weight: bold; font-size: 32rpx">{{ footNum }}</view>
              <view style="margin-top: 6rpx; font-size: 26rpx">足迹</view>
            </view>
          </view>
        </view>
        <view v-if="userCenter.hideCartInlet && member.status === 2" class="radius">
          <view class="radius--inner" :style="'background-color: ' + headStyleConfig.cardColor">
            <view class="radius--inner__item" :style="'color: ' + headStyleConfig.textColor">
              尊享会员
              <text class="radius__inner--split" :style="'color: ' + headStyleConfig.textColor">|</text>
              {{ userCenter.getCartText }}
            </view>
            <view
              class="radius--inner__btn"
              :style="'background-color: ' + headStyleConfig.textColor + ';color: ' + headStyleConfig.cardColor + ';'"
              @click="pageLink('/pages/member/member')"
            >
              <text class="radius--inner__text">立即开通</text>
            </view>
          </view>
        </view>
      </view>
      <view class="order">
        <view class="order__all">
          <view class="order__all--me">我的订单</view>
          <view class="order__all--check" @click="goOrder">
            <text>查看全部订单</text>
            <uv-icon name="arrow-right" class="order__all--arrow" size="12" color="rgba(69, 64, 60, 0.6)" top="1" />
          </view>
        </view>
        <view class="order__quick">
          <view class="cover--group__cell" @click.stop="coverOrderClick('/pages/order/order?type=0')">
            <view class="order__quick--item">
              <view class="order__quick--badge">
                <view v-if="orderBadgeInfo?.waitForPay !== '0'" class="order__quick--number">
                  {{ orderBadgeInfo?.waitForPay }}
                </view>
                <image class="order__quick--image" :src="orderInfo?.waitIcon?.url" />
              </view>
              <view class="order__quick--text">{{ orderInfo?.waitIcon?.name }}</view>
            </view>
          </view>
          <view class="cover--group__cell" @click.stop="coverOrderClick('/pages/order/order?type=1')">
            <view class="order__quick--item">
              <view class="order__quick--badge">
                <view v-if="orderBadgeInfo?.withDelivery !== '0'" class="order__quick--number">
                  {{ orderBadgeInfo?.withDelivery }}
                </view>
                <image class="order__quick--image" :src="orderInfo?.waitDelivered?.url" />
              </view>
              <view class="order__quick--text">{{ orderInfo?.waitDelivered?.name }}</view>
            </view>
          </view>
          <view class="cover--group__cell" @click.stop="coverOrderClick('/pages/order/order?type=3')">
            <view class="order__quick--item">
              <view class="order__quick--badge">
                <view v-if="orderBadgeInfo?.waitForPickup !== '0'" class="order__quick--number">
                  {{ orderBadgeInfo?.waitForPickup }}
                </view>
                <image class="order__quick--image" :src="orderInfo?.waitPickingIcon?.url" />
              </view>
              <view class="order__quick--text">{{ orderInfo?.waitPickingIcon?.name }}</view>
            </view>
          </view>
          <view class="cover--group__cell" @click.stop="coverOrderClick('/pages/order/order?type=6')">
            <view class="order__quick--item">
              <view class="order__quick--badge">
                <view v-if="orderBadgeInfo?.afsOrder !== '0'" class="order__quick--number">
                  {{ orderBadgeInfo?.afsOrder }}
                </view>
                <image class="order__quick--image" :src="orderInfo?.afterSaleIcon?.url" />
              </view>
              <view class="order__quick--text">{{ orderInfo?.afterSaleIcon?.name }}</view>
            </view>
          </view>
          <view v-if="hasOpenEvaluate" class="cover--group__cell" @click.stop="coverOrderClick('/pages/evaluationCenter/evaluationCenter')">
            <view class="order__quick--item">
              <view class="order__quick--badge">
                <image class="order__quick--image" :src="orderInfo?.evaluateIcon?.url" />
              </view>
              <view class="order__quick--text">{{ orderInfo?.evaluateIcon?.name }}</view>
            </view>
          </view>
        </view>
      </view>
    </view>
    <view class="cover--container">
      <view v-if="userCenter.menuStyle === 1" class="cover--group">
        <view
          v-for="(item, index) in expandMenu"
          :key="index"
          :class="'cover--group__expand ' + (item.splitFlag ? 'splitFlag' : '')"
          @click.stop="linkClick(item.linkSelectItem)"
        >
          <block v-if="item.hideMenu">
            <uv-button v-if="item.menuName === '客服'" open-type="contact" class="contact__button cover--group__cell">
              <view class="group__cell--left">
                <image class="cell--left--image" :src="item.menuIconUrl" />
                <text class="cell--left--text">{{ item.menuName }}</text>
              </view>
              <uv-icon class="group__cell--right" name="arrow-right" size="12" color="rgba(69, 64, 60, 0.6)" top="1" />
            </uv-button>
            <view
              v-else-if="!item.splitFlag"
              class="cover--group__cell"
              :class="{
                nextIsSplitFlag: expandMenu[index + 1]?.splitFlag,
              }"
            >
              <view class="group__cell--left">
                <image class="cell--left--image" :src="item.menuIconUrl" />
                <text class="cell--left--text">{{ item.menuName }}</text>
              </view>
              <uv-icon class="group__cell--right" name="arrow-right" size="12" color="rgba(69, 64, 60, 0.6)" top="1" />
            </view>
            <view v-else class="cover--group__cell"></view>
          </block>
        </view>
        <view class="cover--group__expand">
          <view v-if="recyclingsetting.recyclingState" class="cover--group__cell" @click.stop="linkClick(recyclingLink)">
            <view class="group__cell--left">
              <image class="cell--left--image" src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/api/mysend.png" />
              <text class="cell--left--text">我的寄出</text>
            </view>
            <uv-icon class="group__cell--right" name="arrow-right" size="12" color="rgba(69, 64, 60, 0.6)" top="1" />
          </view>
          <view v-if="recyclingsetting.goodsFundState" class="cover--group__cell" @click.stop="linkClick(fundLink)">
            <view class="group__cell--left">
              <image class="cell--left--image" src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/api/myfund.png" />
              <text class="cell--left--text">我的基金</text>
            </view>
            <uv-icon class="group__cell--right" name="arrow-right" size="12" color="rgba(69, 64, 60, 0.6)" top="1" />
          </view>
        </view>
      </view>
      <view v-if="userCenter.menuStyle === 2" class="grid">
        <view class="user__menu--grid">
          <view>
            <view class="grid__name">我的工具</view>
            <view class="grid__wrap">
              <view v-for="(supItem, supIndex) in gridMenu" :key="supIndex" class="grid__navigator" @click.stop="linkClick(supItem.linkSelectItem)">
                <block v-if="supItem.hideMenu">
                  <uv-button
                    v-if="supItem.menuName === '客服'"
                    open-type="contact"
                    :customStyle="{
                      border: 'none',
                      display: 'flex',
                      flexDirection: 'column',
                      justifyContent: 'center',
                      height: '118rpx',
                    }"
                    class="grid__item"
                  >
                    <image class="grid__navigator--image" style="transform: translateY(-5rpx)" :src="supItem.menuIconUrl" />
                    <view class="grid__navigator--right">
                      <text class="grid__navigator--name" style="transform: translateY(5rpx)">{{ supItem.menuName }}</text>
                    </view>
                  </uv-button>
                  <view v-if="supItem.menuName !== '客服'" class="grid__item">
                    <image class="grid__navigator--image" :src="supItem.menuIconUrl" />
                    <view class="grid__navigator--right">
                      <text class="grid__navigator--name">{{ supItem.menuName }}</text>
                    </view>
                  </view>
                </block>
              </view>
              <view v-if="recyclingsetting.recyclingState" class="grid__navigator">
                <view class="grid__item" @click.stop="linkClick(recyclingLink)">
                  <image class="grid__navigator--image" src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/api/mysend.png" />
                  <view class="grid__navigator--right">
                    <text class="grid__navigator--name">我的寄出</text>
                  </view>
                </view>
              </view>
              <view v-if="recyclingsetting.goodsFundState" class="grid__navigator">
                <view class="grid__item" @click.stop="linkClick(fundLink)">
                  <image class="grid__navigator--image" src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/api/myfund.png" />
                  <view class="grid__navigator--right">
                    <text class="grid__navigator--name">我的基金</text>
                  </view>
                </view>
              </view>
            </view>
          </view>
        </view>
      </view>
      <view class="version__text" @click="pageLink('/pages/systemBanner/systemBanner')">
        <image v-if="!!shopInfo.miniBottomLog" class="miniBottomLog" :src="!!shopInfo.miniBottomLog ? shopInfo.miniBottomLog : ''" />
        smart shop商城系统 v{{ APP_VERSION }}
      </view>
      <UPopup v-model:show="qrcodeShow" mode="center" custom-style="background: rgba(255,255,255,0);width:590rpx;" @close="onClose">
        <view
          style="
            background-image: url('https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20211110/70436e0bdb7d43dda409a6ead4e63d9f.png');
            background-repeat: no-repeat;
            background-size: 100% auto;
            width: 100%;
            height: 1049rpx;
            position: relative;
          "
        >
          <image :src="codeImage" style="position: absolute; top: 774rpx; left: 82rpx; width: 173rpx; height: 173rpx" />
        </view>
      </UPopup>
    </view>
    <!-- 页面蒙版 用于新用户登录操作 勿删 -->
    <view
      v-if="!isLogin"
      class="loadMen"
      :style="{
        height: `${screenHeight}px`,
      }"
      @click="authLoad"
    />
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'
import type { MenuList, CustomStyle } from './userCenter'
import { userMsgGet, userIdGet } from '@/api/modules/user'
import { getImgCode } from '@/api/modules/goods'
import { navtofun } from '@/utils/navtofun'
import MIcon from '@/components/MIcon/MIcon.vue'
import { useSettingStore } from '@/store/modules/setting'
import { useUserStore } from '@/store/modules/user'
import { useShopsetStore } from '@/store/modules/shopset'
import { storeToRefs } from 'pinia'
import { APP_LAUNCH, LOGIN_SUCCESS, LOGIN_OPEN } from '@/utils/emitNameConfig'
import useScreenHeight from '@/composables/hooks/useScreenHeight'

const screenHeight = useScreenHeight()

const { isLogin, userInfo } = storeToRefs(useUserStore())
// 组件名称
defineOptions({
  name: 'Me',
})

// 响应式状态
const recyclingsetting = ref<Obj>({})
const userCenter = ref({
  /** 自定义风格样式,json存储 */
  customStyle: '',
  /** 领卡文案 */
  getCartText: '',
  /** 头部风格 1-系统风格 2-自定义风格 */
  headStyle: 1,
  /** 非会员隐藏领卡入口 0-隐藏 1-显示 */
  hideCartInlet: 0,
  /** 用户中心数据id */
  id: 0,
  /** 菜单选择样式 1-展开式 2-折叠式 */
  menuStyle: 1,
  /** 菜单列表 */
  menuVos: [] as MenuList[],
  /** 订单icon信息 json存储 */
  orderInfo: '',
})

const customStyle = ref<CustomStyle>({
  backgroundImage: '',
  /** 卡面颜色 */
  cardColor: '',
  /** 文字颜色 */
  textColor: '',
})

const orderInfo = ref(
  {} as {
    waitIcon: {
      name: string
      url: string
    }
    waitDelivered: {
      name: string
      url: string
    }
    waitPickingIcon: {
      name: string
      url: string
    }
    afterSaleIcon: {
      name: string
      url: string
    }
    evaluateIcon: {
      name: string
      url: string
    }
  },
)
const expandMenu = ref<any[]>([])
const gridMenu = ref<any[]>([])
const qrcodeShow = ref(false)
const headStyleConfig = ref({
  headBackGround: {},
  cardColor: '#45403C',
  textColor: '#E4CB98',
})
const scrollHeight = ref(0)
const collectNum = ref(0)
const footNum = ref(0)
const integral = ref(0)
const shopOpen = ref(false)
const hasOpenEvaluate = ref(true)
const memberInfo = ref<any[]>([])
const member = ref({
  status: 3,
  rankName: '',
  supplyAndRebateBonus: 0,
})
const infoExtends = ref({
  shippingPointInfoVo: {
    pointId: '',
  },
})
const orderBadgeInfo = ref({
  /** 代付款数目 */
  waitForPay: '',
  /** 配送中数目 */
  shipped: '',
  /** 待收货数目 */
  waitForPickup: '',
  /** 待发货数目 */
  withDelivery: '',
  afsOrder: '',
})
const APP_VERSION = import.meta.env.VITE_APP_APP_SHOW_VERSION
const shopInfo = ref<Obj>({})
const packageFunctionVo = ref({
  area: false,
  copyright: false,
  headOffice: false,
  live: false,
  point: false,
})
const codeImage = ref('')
const recyclingLink = ref({
  name: '我的寄出',
  url: '/pages/mysend/mysend',
})
const fundLink = ref({
  name: '我的基金',
  url: '/pages/myfund/myfund',
})

// 方法定义
const init = async () => {
  try {
    const windowHeight = uni.getSystemInfoSync().windowHeight
    const windowWidth = uni.getSystemInfoSync().windowWidth
    // 是否开启评价
    hasOpenEvaluate.value = useSettingStore().shopSetting.hasOpenEvaluate
    scrollHeight.value = (windowHeight * 750) / windowWidth
    // 获取缓存初次渲染
    updateAllData(useSettingStore().userCenter)
    // 获取最新个人中心数据并缓存
    getData()
  } catch (err) {
    console.log(err)
  }
}

const getData = async () => {
  await useSettingStore().getAllUserCenterData()
  const userCenterData = useSettingStore().userCenter
  await getUserMsg()
  updateAllData(userCenterData)
  recyclingsetting.value = useSettingStore().recyclingsetting
}

const getUserMsg = async () => {
  const res = await userMsgGet({
    infoLevel: 2,
  })

  if (res.infoExtends) {
    infoExtends.value = res.infoExtends
    // 改变会员状态
    const memberInfoData = res.infoExtends.memberInfo ? res.infoExtends.memberInfo : []
    if (memberInfoData && memberInfoData.length > 0) {
      member.value = {
        status: memberInfoData[0].status,
        rankName: memberInfoData[0].rankName,
        supplyAndRebateBonus: memberInfoData[0].supplyAndRebateBonus,
      }
    }
  }
}

const updateAllData = (userCenterData: any) => {
  // 我的页面配置数据
  getUserCenterSetting(userCenterData.accountCenterVo)
  // 用户足迹数量
  footNum.value = userCenterData.accountFootMarkCount
  // 是否开启积分商城
  shopOpen.value = !!userCenterData.shopFunctionInfoVo?.shop
  // 获取用户积分值
  integral.value = userCenterData.userInfoVo?.integral ? userCenterData.userInfoVo.integral : 0
  // 用户收藏数量
  collectNum.value = userCenterData.collectCount
  // 数量标记数据
  orderBadgeInfo.value = userCenterData.orderOverviewVo
  // 会员信息
  memberInfo.value = userCenterData.memberInfo
  shopInfo.value = useShopsetStore().shopInfo
  packageFunctionVo.value = userCenterData.packageFunctionVo
}

const pageLink = (url: string) => {
  uni.navigateTo({
    url: `/subcontract${url}`,
  })
}

const gotoFooter = () => {
  uni.navigateTo({
    url: '/subcontract/pages/footer/footer',
  })
}

const gotoCollect = () => {
  uni.navigateTo({
    url: '/subcontract/pages/collect/collect',
  })
}

const gotoIntegralShop = () => {
  if (!shopOpen.value) {
    uni.showToast({
      title: '积分商城暂未开放',
      icon: 'none',
    })
    return
  }
  uni.navigateTo({
    url: '/subcontract/pages/integralShop/integralShop',
  })
}

const authLoad = () => {
  if (!isLogin.value) {
    uni.$emit(LOGIN_OPEN, true)
  }
}

const getUserCenterSetting = (userCenterVo: any) => {
  if (!userCenterVo) {
    return
  }
  const expandMenuValue = userCenterVo.menuVos
    .filter((item: any) => {
      // 过滤出展开式菜单
      return item.styleType === 1
    })
    .map((mapItem: any) => {
      if (typeof mapItem.linkSelectItem === 'string') {
        mapItem.linkSelectItem = JSON.parse(mapItem.linkSelectItem)
      }
      return mapItem
    })

  const gridMenuValue = userCenterVo.menuVos
    .filter((item: any) => {
      // 过滤出展开式菜单
      return item.styleType === 2 && item.hideMenu
    })
    .map((mapItem: any) => {
      if (typeof mapItem.linkSelectItem === 'string') {
        mapItem.linkSelectItem = JSON.parse(mapItem.linkSelectItem)
      }
      return mapItem
    })

  userCenter.value = userCenterVo
  orderInfo.value = JSON.parse(userCenterVo.orderInfo)
  customStyle.value = JSON.parse(userCenterVo.customStyle)
  expandMenu.value = expandMenuValue
  // #ifdef MP-WEIXIN
  gridMenu.value = gridMenuValue
  // #endif
  // #ifndef MP-WEIXIN
  gridMenu.value = gridMenuValue.filter((item: any) => item.menuName !== '客服')
  // #endif

  // 系统风格
  if (userCenter.value.headStyle === 1) {
    headStyleConfig.value.headBackGround = `background-color: #FE4E63`
  }
  if (userCenter.value.headStyle === 2) {
    headStyleConfig.value.headBackGround = `background-image: url(${customStyle.value.backgroundImage})`
    headStyleConfig.value.cardColor = customStyle.value.cardColor
    headStyleConfig.value.textColor = customStyle.value.textColor
  }
}

const onClose = () => {
  qrcodeShow.value = false
}

const openQrcode = () => {
  userIdGet({}).then((id) => {
    const wxaGetwxacode = {
      path: `pages/index/index?userId=${id}`,
      width: 0,
    }

    getImgCode(wxaGetwxacode).then((res) => {
      codeImage.value = res
      qrcodeShow.value = true
    })
  })
}

const goOrder = () => {
  uni.navigateTo({
    url: '/subcontract/pages/order/order',
  })
}

const linkClick = async (link: any) => {
  if (link.append && link.append === 'shopCar') {
    navtofun(link)
    return
  }
  if (link.type && link.type === 7) {
    uni.navigateToMiniProgram({
      appId: link.url,
      envVersion: 'release',
    })
    return
  }
  if (link.url === '/pages/mysend/mysend') {
    uni.navigateTo({
      url: '/subcontract/pages/mysend/mysend',
    })
    return
  }
  if ((link.type && (link.type === 5 || link.type === 6)) || link.append === 'mall') {
    navtofun(link)
    return
  }
  linkLogcal(link)
}

const linkLogcal = async (link: any) => {
  // 空链接不进行跳转
  if (!link.url) {
    return
  }
  // 积分商城开通
  if (link.url === '/pages/integralShop/integralShop') {
    if (!shopOpen.value) {
      uni.showToast({
        title: '积分商城暂未开放',
        icon: 'none',
      })
      return
    }
  }

  // 回收返券开通
  if (link.url === '/pages/recycling/recycling') {
    const shopOpen = useSettingStore().recyclingsetting.recyclingState
    if (!shopOpen) {
      uni.showToast({
        title: '回收返券暂未开放',
        icon: 'none',
      })
      return
    }
  }

  // 会员中心
  if (link.url === '/pages/member/member') {
    const status = member.value.status
    if (status === 1) {
      uni.navigateTo({
        url: '/subcontract/pages/memberVip/memberVip',
      })
    }
    if (status === 2) {
      uni.navigateTo({
        url: '/subcontract/pages/member/member',
      })
    }
    if (status > 2) {
      uni.showToast({
        title: '该功能暂未开放',
        icon: 'none',
      })
    }
    return
  }
  // 首页跳转
  if (link.url === '/pages/index/index') {
    useSettingStore().setCurrentTab(link.append)
    return
  }
  // 列表和九宫格的设置跳转
  if (link.menuName === '设置' || link.url === '/pages/mySetting/mySetting') {
    if (userCenter.value.menuStyle === 1) {
      await uni.navigateTo({
        url: '/subcontract/pages/mySetting/mySetting?type=1',
      })
    }
    if (userCenter.value.menuStyle === 2) {
      await uni.navigateTo({
        url: '/subcontract/pages/mySetting/mySetting?type=2',
      })
    }
    return
  }
  if (link.type && link.type === 6) {
    await uni.navigateTo({
      url: '/subcontract/pages/linkPge/linkPge?src=' + link.url,
    })
    return
  }
  // 普通链接跳转
  await uni.navigateTo({
    url: `${link.url.includes('subcontract') ? '' : '/subcontract'}${link.url}`,
  })

  linkTypeHandle(link)
}

const linkTypeHandle = async (link: any) => {
  // 分类页
  if (link.type === 2) {
    // currentPageId String类型
    useSettingStore().setCurrentPageId(link.id + '')
    useSettingStore().setCurrentTab(link.append)
    return
  }
  // 商超商品
  if (link.type === 1) {
    // 自定义分类id
    await uni.navigateTo({
      url: '/subcontract' + link.url + '?id=' + link.id,
    })
    return
  }
}

const coverOrderClick = (url: string) => {
  uni.navigateTo({
    url: `/subcontract${url}`,
  })
}

const balanceJumpHandle = async () => {
  const balanceDetail = `/pages/balanceValue/balanceValue`
  uni.navigateTo({
    url: '/subcontract' + balanceDetail,
  })
}

const navSetUser = () => {
  uni.navigateTo({
    url: '/subcontract/pages/userInfo/userInfo',
  })
}

// 生命周期钩子
onMounted(() => {
  uni.setNavigationBarTitle({
    title: '个人中心',
  })
  uni.$on(APP_LAUNCH, () => {
    init()
  })
  uni.$on(LOGIN_SUCCESS, () => {
    init()
  })
  if (!isLogin.value) {
    uni.$emit(LOGIN_OPEN, true)
  } else {
    init()
  }
})

watch(
  () => isLogin.value,
  (val: boolean) => {
    if (val) {
      init()
    } else {
      uni.$emit(LOGIN_OPEN, true)
    }
  },
)

onBeforeUnmount(() => {
  uni.$off(APP_LAUNCH)
  uni.$off(LOGIN_SUCCESS)
})

defineExpose({
  getData,
})
</script>
<style lang="scss" scoped>
@mixin bottom-line($weight: 1px, $color: #f8f5f9) {
  border-bottom: $weight solid $color;
}

// 页面主色
$page-main-color: #fe4e63;
// 字体颜色
$page-text-color: #45403c;
// 默认边距
$default-side: 18rpx;
// 默认字体大小
$default-font-size: 26rpx;
// 内容字体大小
$content-font-size: 26rpx;
.me_container {
  .loadMen {
    width: 750rpx;
    background-color: rgba(0, 0, 0, 0);
    position: fixed;
    top: 0;
    z-index: 9;
  }

  .box {
    width: 100vw;
    position: relative;
    background-color: #f5f5f5;
    overflow: hidden;

    .user--container {
      background-size: cover;
      background-repeat: no-repeat;
      width: 100vw;
      z-index: 2;

      .user--layout {
        width: 90vw;
        margin: 0 auto;
        padding: 40rpx 0 20rpx 0;
        @include flex(space-between, center);

        .user--layout__info {
          @include flex;
        }

        .user--layout__icon {
          display: flex;
          width: 110rpx;
          flex-wrap: wrap;
          justify-content: center;

          .user--layout__icon__text {
            font-size: 24rpx;
            color: white;
            margin-top: 8rpx;
          }
        }

        .user--layout__container {
          padding-left: 20rpx;
          @include flex(space-between, flex-start);
          flex-direction: column;
        }

        .user--layout__nikeName {
          @include flex;
        }

        .user--layout__image {
          flex: 0 0 118rpx;
          width: 118rpx;
          height: 118rpx;
          border-radius: 50%;
          box-shadow: 0 0 0 6rpx white;
        }

        .user--layout__text {
          vertical-align: middle;
          font-size: 32rpx;
          color: #ffffff;
          padding-left: 20rpx;
        }

        .user--layout__member {
          line-height: 44rpx;
          border: 2rpx solid white;
          border-radius: 22rpx;
          padding: 0 10rpx;
          display: flex;
          justify-content: center;
          align-items: center;

          .icon {
            color: #f3ca7e;
          }

          text {
            font-size: 22rpx;
            color: #ffffff;
          }
        }

        .user--layout__recode {
          display: flex;
          color: white;
          font-size: 26rpx;
        }

        .user--layout__qrcode {
          font-size: 50rpx;
          line-height: 0rpx;
          width: 100rpx;
          height: 100rpx;
          background-color: white;
          opacity: 0.25;
          border-radius: 12rpx;
        }
      }

      .content {
        margin: 20rpx 20rpx 0rpx 20rpx;
        padding: 0rpx 20rpx !important;
        border-radius: 15rpx;
        z-index: 10;
        height: 136rpx;

        .content__all {
          height: 136rpx;
          display: flex;
          justify-content: space-around;
          align-items: center;
          font-size: 26rpx;
          color: #ffffff;

          .content__all--item {
            width: 56rpx;
            display: flex;
            flex-wrap: wrap;
            justify-content: center;
          }
        }
      }

      .user--layout__recode__text {
        width: 60rpx;
        height: 76rpx;
        flex-wrap: wrap;
        display: flex;
        justify-content: center;
      }

      .user--layout__recode__line {
        height: 50rpx;
        background-color: white;
        width: 2rpx;
        margin: 4rpx 20rpx 0 20rpx;
      }

      /* 会员权益 */
      .radius {
        width: 100vw;
        margin: 20rpx auto 0;

        .radius--inner {
          width: calc(100% - 36rpx);
          height: 90rpx;
          border-radius: 10rpx 10rpx 0% 0%;
          margin: 0rpx $default-side;
          padding: 0rpx 30rpx 0rpx 40rpx;
          @include flex(space-between, center);

          .radius--inner__item {
            font-size: $content-font-size;
          }

          .radius--inner__split {
            padding: 0 20rpx;
          }

          .radius--inner__btn {
            border-radius: 80rpx;
            width: 139rpx;
            height: 39rpx;
            font-size: $content-font-size;
            line-height: 39rpx;
            text-align: center;
          }
        }
      }
    }
  }

  .order {
    width: 100vw;
    margin: 0 auto;
    padding: 20rpx 20rpx 0 20rpx !important;

    .order__all {
      @include flex(space-between, center);
      padding: 30rpx 28rpx 30rpx 36rpx;
      border-radius: 15rpx 15rpx 0 0;
      @include bottom-line;
      font-size: $content-font-size;
      background-color: #ffffff;

      .order__all--me {
        color: $page-text-color;
        font-weight: bold;
        font-size: 30rpx;
      }

      .order__all--check {
        @include flex();
        color: rgba(69, 64, 60, 0.6);
        background-color: #ffffff;
      }
    }

    .order__quick {
      @include flex(space-between, center);
      padding: 35rpx 24rpx;
      margin: 0;
      background-color: #ffffff;
      border-radius: 0 0 15rpx 15rpx;
      overflow: hidden;

      .order__quick--item {
        text-align: center;
        position: relative;
        width: 110rpx;

        .order__quick--badge {
          position: relative;
          display: inline-block;

          .order__quick--image {
            margin-bottom: 10rpx;
            width: 50rpx;
            height: 50rpx;
          }

          .order__quick--number {
            position: absolute;
            top: 0;
            right: 0;
            box-sizing: border-box;
            min-width: 32rpx;
            padding: 0 6rpx;
            color: #fff;
            font-weight: 500;
            font-size: 24rpx;
            font-family:
              PingFang SC,
              Helvetica Neue,
              Arial,
              sans-serif;
            line-height: 28rpx;
            text-align: center;
            background-color: #ee0a24;
            border: 2rpx solid #fff;
            border-radius: 32rpx;
            -webkit-transform: translate(50%, -50%);
            transform: translate(50%, -50%);
            -webkit-transform-origin: 100%;
            transform-origin: 100%;
          }
        }

        .order__quick--text {
          font-size: $content-font-size;
          color: rgb(102, 94, 88);
        }
      }
    }
  }

  .cover--container {
    background-color: #f5f5f5;
    padding: $default-side;

    .cover--group {
      border-radius: 10rpx;
      margin-bottom: 15rpx;

      .cover--group__expand:first-child {
        .cover--group__cell {
          border-top-right-radius: 15rpx;
          border-top-left-radius: 15rpx;
          font-family: 'first-child';
        }
      }

      .cover--group__expand:last-child {
        .cover--group__cell {
          border-bottom-right-radius: 15rpx;
          border-bottom-left-radius: 15rpx;
          font-family: 'last-child';
        }
      }

      .cover--group__expand:not(:last-child) {
        .cover--group__cell {
          // CSS样式
          @include bottom-line;
        }
      }

      .cover--group__expand.splitFlag + .cover--group__expand {
        .cover--group__cell {
          border-top-right-radius: 15rpx;
          border-top-left-radius: 15rpx;
          font-family: '+';
        }
      }

      .cover--group__expand.splitFlag {
        .cover--group__cell {
          height: 0;
          padding: 0;
          margin-bottom: 15rpx;
          border-bottom-right-radius: 15rpx;
          border-bottom-left-radius: 15rpx;
          font-family: 'splitFlag';
          .uv-icon {
            display: none;
          }
        }
      }

      .cover--group__cell {
        @include flex(space-between, center);
        background-color: #ffffff;
        padding: 30rpx 25rpx 30rpx 42rpx;

        .group__cell--left {
          @include flex(center, center);

          .cell--left--image {
            width: 38rpx;
            height: 38rpx;
            padding-right: 23rpx;
          }

          .cell--left--text {
            font-size: $default-font-size;
            color: $page-text-color;
          }
        }

        .group__cell--right {
          color: rgba(69, 64, 60, 0.6);
          font-size: 24rpx;
          line-height: 0rpx;
        }
      }

      .cover--group__cell.nextIsSplitFlag {
        border-bottom-right-radius: 15rpx;
        border-bottom-left-radius: 15rpx;
        font-family: 'splitFlag';
      }
    }

    .grid {
      background-color: #ffffff;
      margin-bottom: 30rpx;
      border-radius: 10rpx;
      padding: 4rpx 0;

      .grid__name {
        font-size: 28rpx;
        color: #45403c;
        padding: 30rpx 0 20rpx 38rpx;
        font-weight: bolder;
      }

      .grid__item {
        text-align: center;
      }

      .grid__wrap {
        @include flex(flex-start);
        flex-wrap: wrap;

        .grid__navigator {
          @include flex();
          flex-direction: column;
          flex: 0 0 25%;
          padding: 30rpx 0;

          .grid__navigator--image {
            width: 50rpx;
            height: 50rpx;
          }

          .grid__navigator--right {
            text-align: center;
            padding-top: 20rpx;
            display: flex;
            flex-direction: column;
            justify-content: center;

            .grid__navigator--name {
              font-size: 26rpx;
              color: #58524d;
            }

            .grid__navigator--text {
              font-size: 28rpx;
              color: #b2b2b2;
            }
          }
        }
      }
    }

    .navigator-hover {
      background-color: #f1f2f6;
    }

    .version__text {
      font-size: 26rpx;
      color: #b2b2b2;
      text-align: center;
      padding: 40rpx 0;
    }

    .borderRadius {
      border-radius: 100%;
    }

    .contact__button {
      color: #45403c;
      background-color: transparent;
      font-size: 32rpx;
    }
  }

  .miniBottomLog {
    width: 100rpx;
    margin: 0 auto;
    height: 100rpx;
    display: flex;
  }
}
</style>
