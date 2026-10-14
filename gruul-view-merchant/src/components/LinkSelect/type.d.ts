import type { LinkType, SelectType } from './enums'

/**
 * 链接选择器组件属性
 */
export interface LinkSelectProps {
  /** 是否内嵌模式 */
  inner?: boolean
  /** 底部导航去掉商超 */
  noProTab?: boolean
  /** 分类页海报去掉单独分类选择 */
  spellTab?: boolean
  /** 自定义底部导航选择范围限制 */
  limitProTab?: boolean
  /** 来源标识 */
  getFrom?: string
  /** 是否显示清除按钮 */
  showClear?: boolean
  /** 选择器样式 1:radio单选 2:select下拉 */
  selectType?: SelectType
  /** 链接类型 */
  type?: LinkType
  /** 链接数据 */
  link?: LinkSelectItem
}

/**
 * 链接选择项数据结构
 */
export interface LinkSelectItem {
  /** 链接ID */
  id: number | string | null | undefined
  /** 链接类型 */
  type: number | string | null | undefined
  /** 链接名称 */
  name: string
  /** 链接地址 */
  url: string
  /** 附加参数 */
  append?: string
  /** 限制类型 */
  limitType?: number
  /** 最高价格 */
  maxPrice?: number
  /** 最低价格 */
  minPrice?: number
  /** 来源标识 */
  getFrom?: string
}

/**
 * 活动营销组件属性
 */
export interface ActivityMarketProps {
  /** 链接数据 */
  link?: LinkSelectItem
  /** 是否可见 */
  visible?: boolean
}

/**
 * 小程序组件属性
 */
export interface AppViewProps {
  /** 链接数据 */
  link?: LinkSelectItem
}

/**
 * 自定义页面组件属性
 */
export interface CustomPageProps {
  /** 链接数据 */
  link?: LinkSelectItem
  /** 是否可见 */
  visible?: boolean
}

/**
 * 展示分类组件属性
 */
export interface DisplayClassIfyProps {
  /** 链接数据 */
  link?: LinkSelectItem
  /** 是否可见 */
  visible?: boolean
}

/**
 * 功能页面组件属性
 */
export interface FunctionPageProps {
  /** 链接数据 */
  link?: LinkSelectItem
  /** 是否可见 */
  visible?: boolean
  /** 底部导航去客服 */
  noProTab?: boolean
  /** 底部导航限制可选范围 */
  limitProTab?: boolean
}

/**
 * 商品组件属性
 */
export interface GoodsProps {
  /** 链接数据 */
  link?: LinkSelectItem
  /** 是否可见 */
  visible?: boolean
}

/**
 * 自定义链接组件属性
 */
export interface WebViewProps {
  /** 链接数据 */
  link?: LinkSelectItem
}

/**
 * 组件事件
 */
export interface ComponentEmits {
  (e: 'update:link', value: LinkSelectItem): void
}

/**
 * 类型名称映射项
 */
export interface TypeNameMapItem {
  /** 显示文本 */
  text: string
  /** 组件名称 */
  name: string
  /** 是否展示 */
  show: (prop?: any, secShow?: boolean) => boolean
}

/**
 * 类型名称映射
 */
export interface TypeNameMap {
  [key: string | number]: TypeNameMapItem
}
