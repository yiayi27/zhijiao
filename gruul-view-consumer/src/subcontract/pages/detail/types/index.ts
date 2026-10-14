// 类型定义
export interface interCountDetail {
  status: string
  toBeginTime: number
  toEndTime: number
  remainTime: number
}

export interface interTimeData {
  hours: string
  minutes: string
  seconds: string
}

// 添加Point接口定义
export interface Point {
  x: number
  y: number
}

// 扩展评价项目类型定义
export interface EvaluateItem {
  userName: string
  id?: Long
  userAvatarUrl?: string
  createTime?: string
  specs?: string
  comment?: string
  reply?: string
  replyTime?: string
}

export interface GoodDetailType {
  imgUrls: any[]
  skuStocks: any[]
  productAttributes: any[]
  actPrice?: string
  price?: string
  openSpecs?: boolean
  name?: string
  saleDescribe?: string
  goodTime?: string
  sale?: number
  stock?: number
  goodType?: number
  maxNum?: number
  isCollect?: boolean
  giftPoint?: number
  serviceIds?: string
  status?: number
  id?: Long
  memPrice?: string
  savePrice?: string
  rebPrice?: string
  detail?: string
  albumPics?: string
  launchArea?: string
  activityStartTime?: string
  activityEndTime?: string
  activityId?: string
  productId?: Long
  productName?: string
  skuId?: Long
  pic?: string
  saleMode?: string
  productSn?: string
  goodsNumber?: number
  // 添加Good类型需要的属性
  integralPrice?: Long
  guidePrice?: Long
  integralGoodName?: string
  [key: string]: any
}
