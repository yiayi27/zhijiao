export interface CustomizedPages {
  /** 页面id */
  id: number
  /** 商品名称 */
  name: string
  /** 是否是首页 */
  isHome: boolean

  [key: string]: any
}

export interface CategoryPage extends CustomizedPages {
  /** 页面类型  2商超分类页 */
  type: string
}
