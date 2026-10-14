export interface ICoupon {
  id: string
  createTime: string
  name: string
  effectiveType: number
  effectiveDays: number
  startTime: string
  endTime: string
  couponType: number
  productType: number
  discount: number
  discountAmount: number
  conditionAmount: number
  issueQuantity: number
  limitQuantity: number
  useStatus: number
  status: number
  couponProducts: IProcuct[] | null
  canUse: boolean
  isChoose: boolean
}

export interface IAdress {
  tenantId: string
  createTime: string
  updateTime: string
  deleted: boolean
  id: string
  userName: string
  phone: string
  postCode: string
  // 是否为默认地址 0-非默认 1-默认
  isDefault: number
  province: string
  city: string
  county: string
  detailInfo: string
  userId: string
  location: string
  lngLat: string
}

export interface IProcuct {
  // 配送方式
  distributionMode: 0 | 1
  productId: string
  productPic: string
  productName: string
  productSn: string
  productPrice: number
  productOriginalPrice: number
  productQuantity: number
  productSkuId: string
  productSkuCode: any
  productSkuPic: string
  specs: string
  status: number
  giftIntegration: number
  memberType: number
  rebateType: number
  memberPrices: {
    id: string
    productId: string
    skuId: string
    memberLevelId: string
    memberType: any
    memberPrice: number
    memberLevelName: string
  }[]
  skuRebates: {
    id: number
    productId: number
    rebatePrice: number
    skuId: number
  }[]
}

export interface IFull {
  fullScaleId: number
  price: number
  /** 是否和会员价共用 */
  canUseMemberPrice: boolean
  /** 是否和积分共用 */
  useIntegral: boolean
}

export interface IRebateSet {
  tenantId: string
  createTime: string
  updateTime: string
  deleted: boolean
  id: string
  shopId: string
  rebateRatio: number
  balanceUseCondition: string
  rebateCondition: string
}

export interface SubmitOptions {
  id: string
  solitaireActivityId: string
  type: string
  items: string
}
