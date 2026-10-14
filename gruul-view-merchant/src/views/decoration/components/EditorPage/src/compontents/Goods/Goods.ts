export interface CategoryItem {
  id: string
  name: string
  modeName: string
  saleMode?: string
  productNumber?: number
  showCategoryVos?: CategoryItem[]
}
interface IShowContent {
  /** 商品名称 */
  nameShow: boolean
  /** 商品价格 */
  priceShow: boolean
  /** 购物按钮 */
  buttonShow: boolean
  /** 购物按钮样式 */
  buttonStyle: number
  /** 购物车按钮文案 */
  buttonText: string
  /** 商品角标 */
  tagShow: boolean
  /** 商品角标样式 */
  tagStyle: number
  pointShow?: boolean
}

interface GoodsItem {
  id: number
  /** 商品名称 */
  name: string
  /** 商品图片 */
  img: string
  /** 结束时间 */
  endTime: string
  /** 商品实售价 */
  price: number
  /** 商品指导价 */
  guide: number
  /** 已售量 */
  soldCount: number
  /** 库存 */
  inventory: number
  /** 提货时间 */
  deliveryTime: string
}

/**
 * 商品
 */
export default {
  /** 1展示分类  2不展示分类 */
  ponentType: 1,

  /** 商品 */
  goods: [] as any[],

  /** 列表样式 */
  listStyle: 'goods-style--one',

  /** 页面边距 */
  pagePadding: 17,

  /** 类目样式 1新  2下划线 */
  titleStyle: 1,

  /** 商品间距 */
  goodPadding: 12,

  /** 商品样式 */
  goodsStyle: 'is-none',

  /** 图片倒角 */
  angle: 'is-straight',

  /** 文本样式 */
  textStyle: 'is-normal',

  /** 一级类目 */
  firstCatList: [] as CategoryItem[],

  /** 当前分类ID */
  currentCategoryId: '',

  /** 显示内容 */
  showContent: {
    nameShow: true,
    priceShow: true,
    buttonShow: true,
    buttonStyle: 1,
    buttonText: '',
    tagShow: true,
    tagStyle: 1,
  } as IShowContent,

  /** 商品对象 */
  goodsItem: {
    id: 1,
    img: 'https://qiniu-app.qtshe.com/u391.png',
    name: '商品名称',
    price: '99.90',
  } as any,

  /** 商品数量 */
  goodsCount: 1,

  /** 商品临时对象 */
  goodsTemp: {
    id: 1,
    name: '商品名称',
    img: 'https://qiniu-app.qtshe.com/u391.png',
    endTime: '20:15:14',
    price: 99,
    guide: 219,
    soldCount: 10,
    inventory: 120,
    deliveryTime: '06月24日 14:00',
  } as GoodsItem,
}
