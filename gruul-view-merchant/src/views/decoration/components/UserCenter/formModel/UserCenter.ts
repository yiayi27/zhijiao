interface IUserCenter {
  codeStyle: number
  customStyle: string
  getCartText: string
  headStyle: number
  hideCartInlet: number
  id: number
  qrcodeVisible: boolean
  menuStyle: number
  menuVos: MenuList[]
  orderInfo: string
}

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
  pid: number | string
  /** 排序位置 */
  sortIndex: number
  /** 分隔 */
  splitFlag: boolean
  /** 菜单样式类别 1-展开式 2-折叠式 */
  styleType: number
  /** 领卡文案 */
  subMenu: SubMenuList[] | null
}

export interface SubMenuList {
  id: number
  menuName: string
  menuIconUrl: string
  defaultIcon: string | null
  styleType: number
  sortIndex: number
  hideMenu: boolean
  allowUse: number
  linkSelectItem: any
  splitFlag: boolean
  subMenu: null
  pid: number | string
}

/**
 * 用户中心配置
 */
export default class UserCenter implements IUserCenter {
  /** 提货码样式,1样式1 2样式2 */
  codeStyle = 1

  /** 提货码显示影藏 */
  qrcodeVisible = true

  /** 自定义风格样式,json存储 */
  customStyle = ''

  /** 领卡文案 */
  getCartText = ''

  /** 头部风格 1-系统风格 2-自定义风格 */
  headStyle = 1

  /** 非会员隐藏领卡入口 0-隐藏 1-显示 */
  hideCartInlet = 0

  /** 用户中心数据id */
  id: number = 0

  /** 菜单选择样式 1-展开式 2-折叠式 */
  menuStyle = 1

  /** 菜单列表 */
  menuVos: MenuList[] = []

  /** 订单icon信息 json存储 */
  orderInfo = ''
}
