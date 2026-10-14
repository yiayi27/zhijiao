export interface SkuStock {
  id: Long
  specs: string
  perLimit: number
  stock: number
  getType?: boolean
  originalPrice?: number
  price?: Long
  pointId?: number
  pic?: string
  [key: string]: any
}

export interface GoodDetail {
  skuStocks: SkuStock[]
  goodsNumber: number
  pic?: string
  skuId?: Long
  skuName?: string
  productId?: Long
  productName?: string
  actPrice?: Long
  status?: number
  maxNum: number
  [key: string]: any
}

export interface ShopCarItem extends GoodDetail {
  isCheck: boolean
  itemIndex: number
  skuList: (string | number)[]
  // 其他可能的属性
}
