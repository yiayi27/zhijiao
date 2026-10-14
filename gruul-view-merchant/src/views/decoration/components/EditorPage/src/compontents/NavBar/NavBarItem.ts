export default class MenuList {
  /** 导航名称 */
  text = '导航名称'

  /** 图标类型 1-系统图标 2-自定义图标 */
  iconType = 1

  /** 图标url */
  iconPath = ''

  /** 选中图标url */
  selectedIconPath = ''

  /** 系统图标 */
  defIcon = ''

  actIcon = ''

  /** 添加链接时的 跳转类型 0 -功能页面 1-商超商品 2 -展示分类 3-自定义页面 4 活动营销 */
  linkSelectItem: any

  /** 链接跳转地址 */
  linkUrl = ''

  /** 链接名称 */
  linkName = ''

  /** append index页面固定页面会有此参数 */
  name = ''

  /** 排序位置 */
  sortIndex: number = 0

  /** 是否为首页 */
  isHome = false

  /** 链接类型5为自定义页面 */
  type: string | number = ''

  id: number | string | null = null

  isAdd = true
}

export function getDefaultMenuItem(def: object = {}): MenuList {
  return {
    text: '名称',
    id: null,
    iconType: 2,
    iconPath: 'https://qiniu-app.qtshe.com/u391.png',
    selectedIconPath: 'https://qiniu-app.qtshe.com/u391.png',
    linkSelectItem: '',
    sortIndex: 0,
    linkUrl: '',
    linkName: '',
    name: '',
    type: '',
    isHome: false,
    /** 系统图标 */
    defIcon: '',
    actIcon: '',
    isAdd: false, // 新增的导航是只能选择上传图片
    ...def,
  }
}
