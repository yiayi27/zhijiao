export const ACCESS_TOKEN = 'ACCESS-TOKEN' // 用户token
export const REFRESH_TOKEN = 'REFRESH_TOKEN' // 用户token
export const SHOP_ID = 'SHOP-ID' // 商家信息
export const NEW_GOOD_BASE_INFO_MODEL = 'NEW_GOOD_BASE_INFO_MODEL' // 商品基本信息
export const NEW_GOOD_SELL_INFO_MODEL = 'NEW_GOOD_SELL_INFO_MODEL' // 商家销售, 商品信息
export const NEW_GOOD_SHOP_INFO_MODEL = 'NEW_GOOD_SHOP_INFO_MODEL' // 商家销售, 商品信息
export const REMEMBER_ME = 'REMEMBER_ME' // 记住我
export const MOBILE = 'MOBILE' // 手机号
export const PASSWORD = 'PASSWORD' // 密码
export const PERMISSION = 'PERMISSION' // 权限

type ValueOf<T> = T[keyof T]
export type StorageKeyType = ValueOf<typeof import('@/libs/enum/storage')>
