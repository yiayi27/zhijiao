import type { LinkSelectItem, TypeNameMap } from './type'
import { LINK_TYPE_CONFIG, LinkType, TAB_CONFIG } from '@/components/LinkSelect/enums'

import { defineAsyncComponent } from 'vue'

// 使用 defineAsyncComponent 懒加载组件
export const asyncComponents = {
  [TAB_CONFIG.FUNCTION_PAGE]: defineAsyncComponent(() => import('@/components/LinkSelect/src/components/FunctionPage.vue')),
  [TAB_CONFIG.GOODS]: defineAsyncComponent(() => import('@/components/LinkSelect/src/components/Goods.vue')),
  [TAB_CONFIG.DISPLAY_CLASSIFY]: defineAsyncComponent(() => import('@/components/LinkSelect/src/components/DisplayClassIfy.vue')),
  [TAB_CONFIG.ACTIVITY_MARKET]: defineAsyncComponent(() => import('@/components/LinkSelect/src/components/ActivityMarket.vue')),
  [TAB_CONFIG.CUSTOM_PAGE]: defineAsyncComponent(() => import('@/components/LinkSelect/src/components/CustomPage.vue')),
}

export const typeNameMap: TypeNameMap = {
  [LinkType.FunctionPage]: {
    text: '功能页面',
    name: TAB_CONFIG.FUNCTION_PAGE,
    show: () => true,
  },
  [LinkType.Goods]: {
    text: '商品',
    name: TAB_CONFIG.GOODS,
    show: (props) => !props.noProTab,
  },
  [LinkType.DisplayClassify]: {
    text: '商品专区',
    name: TAB_CONFIG.DISPLAY_CLASSIFY,
    show: (props, secShow) => props.spellTab && secShow,
  },
  [LinkType.ActivityMarket]: {
    text: '活动营销',
    name: TAB_CONFIG.ACTIVITY_MARKET,
    show: (props) => props.limitProTab,
  },
  [LinkType.CustomPage]: {
    text: '自定义页面',
    name: TAB_CONFIG.CUSTOM_PAGE,
    show: () => true,
  },
}

export function getTabOptions() {
  return Object.values(typeNameMap).map((item) => ({ ...item, label: item.text }))
}

export function getRadioOption() {
  return [
    { label: '系统链接', value: LINK_TYPE_CONFIG.SYSTEM },
    { label: '自定义链接', value: LINK_TYPE_CONFIG.CUSTOM },
    { label: '小程序', value: LINK_TYPE_CONFIG.MINI_PROGRAM },
  ]
}

/**
 * 功能页面默认数据
 */
export const FUNCTION_PAGE_DATA: LinkSelectItem[] = [
  {
    id: 0,
    type: LinkType.FunctionPage,
    name: '店铺主页',
    url: '/pages/index/index',
    append: 'home',
  },
  {
    id: 3,
    type: LinkType.FunctionPage,
    name: '个人中心',
    url: '/pages/index/index',
    append: 'me',
  },
  {
    id: 4,
    type: LinkType.FunctionPage,
    name: '购物车',
    url: '/pages/cart/cart',
    append: 'cart',
  },
  {
    id: 5,
    type: LinkType.FunctionPage,
    name: '商品分类',
    url: '/pages/category/category',
    append: 'category',
  },
  {
    id: 6,
    type: LinkType.FunctionPage,
    name: '商品列表',
    url: '/pages/goodsList/goodsList',
    append: 'goodsList',
  },
  {
    id: 7,
    type: LinkType.FunctionPage,
    name: '订单列表',
    url: '/pages/order/order',
    append: 'order',
  },
  {
    id: 8,
    type: LinkType.FunctionPage,
    name: '客服',
    url: '/pages/service/service',
    append: 'service',
  },
  {
    id: 9,
    type: LinkType.FunctionPage,
    name: '搜索',
    url: '/pages/Search/Search',
    append: 'search',
  },
  {
    id: 10,
    type: LinkType.FunctionPage,
    name: '商城',
    url: '/pages/mall/mall',
    append: 'mall',
  },
]

/**
 * 活动营销默认数据
 */
export const ACTIVITY_MARKET_DATA: LinkSelectItem[] = [
  {
    id: 1,
    type: LinkType.ActivityMarket,
    name: '积分商城',
    url: '/subcontract/pages/integralShop/integralShop',
    append: 'integral',
  },
  {
    id: 3,
    type: LinkType.ActivityMarket,
    name: '领券中心',
    url: '/subcontract/pages/coupon/couponCenter/couponCenter',
    append: '',
  },
  {
    id: 4,
    type: LinkType.ActivityMarket,
    name: '我的优惠券',
    url: '/subcontract/pages/coupon/coupon',
    append: '',
  },
  {
    id: 11,
    type: LinkType.ActivityMarket,
    name: '会员中心',
    url: '/subcontract/pages/member/member',
    append: '',
  },
  {
    id: 12,
    type: LinkType.ActivityMarket,
    name: '直播营销',
    url: '/subcontract/pages/live/live',
    append: 'live',
  },
  {
    id: 13,
    type: LinkType.ActivityMarket,
    name: '社群接龙',
    url: '/subcontract/pages/chainDetail/chainDetail',
    append: 'chain',
  },
  {
    id: 14,
    type: LinkType.ActivityMarket,
    name: '回收返券',
    url: '/subcontract/pages/recycling/recycling',
    append: '',
  },
  {
    id: 15,
    type: LinkType.FunctionPage,
    name: '分销中心',
    url: '/subcontract/pages/distributionCenter/distributionCenter',
    append: '',
  },
]

// 获取默认link链接
export function getDefaultLinkData(data: object = {}): LinkSelectItem {
  return {
    id: '',
    type: '',
    name: '',
    url: '',
    append: '',
    ...data,
  }
}
