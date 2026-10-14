export interface IShopInfo {
  platformShopId: string
  shopName: string
  logoUrl: string
  miniBottomLog: string
}

export type PayFormType = 'appid' | 'mchId' | 'keyPrivate' | 'keyCert' | 'keyPublic' | 'mchKey' | 'cert' | 'payType'
