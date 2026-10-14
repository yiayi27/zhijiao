import type StoreNavigationItemTs from './StoreNavigationItemTs'

/**
 * 店铺导航
 */
export default {
  storeNavigations: Array.from({ length: 4 })
    .fill('')
    .map(() => getDefaultStoreNavigations()) as StoreNavigationItemTs[],
}

function getDefaultStoreNavigations() {
  return {
    /** 导航名称 */
    navName: '导航名称',
    /** 字体颜色 */
    fontColor: '#333333',
    /** 图标 */
    navIcon: 'https://qiniu-app.qtshe.com/u391.png',
    /** 跳转路径 */
    pathLink: '',
    /** 链接跳转地址 */
    linkUrl: '',
    /** 链接名称 */
    linkName: '',
    /** home功能页面会有此参数 */
    append: '',
    /** 链接类型5为自定义页面 */
    type: '',
    /** 链接类型5为自定义页面 */
    id: '',
  }
}
