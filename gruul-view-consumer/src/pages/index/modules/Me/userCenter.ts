export interface MenuList {
  /** 是否可用 0不可用 1-可用 */
  allowUse: number
  /** 默认图标地址 */
  defaultIcon: string
  /** 菜单是否 0隐藏1显示 */
  hideMenu: boolean
  /** 菜单id */
  id: number
  /** 跳转类型 0 -功能页面 1-商超商品 2 -展示分类 3-自定义页面 4 活动营销 */
  linkSelectItem: any
  /** 菜单当前图标url */
  menuIconUrl: string
  /** 菜单名称 */
  menuName: string
  /** 父级id */
  pid: number
  /** 排序位置 */
  sortIndex: number
  /** 分隔 */
  splitFlag: boolean
  /** 菜单样式类别 1-展开式 2-折叠式 */
  styleType: number
  /** 领卡文案 */
  subMenu: SubMenuList[] | null
}

interface SubMenuList {
  id: number
  menuName: string
  menuIconUrl: string
  defaultIcon: string | null
  styleType: number
  sortIndex: number
  hideMenu: boolean
  allowUse: number
  /** 跳转类型 0 -功能页面 1-商超商品 2 -展示分类 3-自定义页面 4 活动营销 */
  linkSelectItem: any
  splitFlag: boolean
  subMenu: null
  pid: number
}

export interface CustomStyle {
  /** 背景图片 */
  backgroundImage: string
  /** 卡面颜色 */
  cardColor: string
  /** 文字颜色 */
  textColor: string
}
