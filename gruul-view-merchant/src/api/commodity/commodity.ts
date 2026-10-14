import api from '@/libs/request'

/** 获取商品基金配置 */
export function getCommoditySet(data: Obj) {
  return api.get(`/recycling/recycling-setting/info`, data)
}

/** 设置商品基金 */
export function saveCommoditySet(data: Obj) {
  return api.put(`/recycling/recycling-setting/fund/modify`, data)
}

/** 获取基金详情设置 */
export function getCommodityDet(data: Obj = {}) {
  return api.get(`/recycling/recycling-ass-order-account/statistical`, data)
}

/** 获取用户基金列表详情 */
export function getUserCommodityList(data: Obj) {
  return api.get(`/recycling/recycling-ass-order-account/list`, data)
}
