export interface IFromItem {
  key: string
  value: any
  type: string
  required: boolean
  time?: string
}

export interface OrderItemWithAfs {
  productSkuId: string
  applyNum: number
  afs?: {
    id: string
    type: string
    status: string
  }
  [key: string]: any
}

export interface OrderDetail {
  id: string
  status: string
  type: string
  payType: string
  expireTime?: number
  userNote?: string
  couponAmount?: number
  fullScaleAmount?: number
  memberAmount?: number
  integrationAmount?: number
  freightAmount?: number
  freightAmountstr?: string
  payAmount?: number
  createTime?: string
  payTime?: string
  closeTime?: string
  commentTime?: string
  customForm?: string
  expire?: boolean
  openEvaluate?: boolean
  rBtnStatus?: boolean
  privileges?: string[]
  orderItemList: OrderItemWithAfs[]
  orderDelivery: {
    deliveryType: string
    receiverName: string
    receiverPhone: string
    deliveryTime?: string
    receiveTime?: string
    deliverySn?: string
    deliveryCompany?: string
    deliveryCode?: string
    [key: string]: any
  }
  [key: string]: any
}
