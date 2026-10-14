/**
 * 链接类型枚举
 */
export enum LinkType {
  /** 功能页面 */
  FunctionPage = 0,
  /** 商超商品 */
  Goods = 1,
  /** 展示分类 */
  DisplayClassify = 2,
  /** 自定义页面 */
  CustomPage = 5,
  /** 活动营销 */
  ActivityMarket = 4,
  /** 自定义链接 */
  CustomLink = 6,
  /** 小程序 */
  MiniProgram = 7,
}

/**
 * 选择器样式类型
 */
export enum SelectType {
  /** 单选按钮样式 */
  Radio = 1,
  /** 下拉选择样式 */
  Select = 2,
}

/**
 * 链接类型配置
 */
export const LINK_TYPE_CONFIG = {
  /** 系统链接 */
  SYSTEM: 1,
  /** 自定义链接 */
  CUSTOM: 2,
  /** 小程序 */
  MINI_PROGRAM: 3,
}

/**
 * 标签页配置
 */
export const TAB_CONFIG = {
  /** 功能页面 */
  FUNCTION_PAGE: 'FunctionPage',
  /** 商品 */
  GOODS: 'Goods',
  /** 商品专区 */
  DISPLAY_CLASSIFY: 'DisplayClassIfy',
  /** 活动营销 */
  ACTIVITY_MARKET: 'ActivityMarket',
  /** 自定义页面 */
  CUSTOM_PAGE: 'CustomPage',
}
