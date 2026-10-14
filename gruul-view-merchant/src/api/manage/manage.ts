import api from '@/libs/request'

/**
 * 店铺实时概况
 */
export function getManageOverView(data: Obj) {
  return api.get('/finance/manage/overview', data)
}

/**
 * 数据概况订单数据
 */
export function getManageOrder() {
  return api.get('/order/manage/overview', {})
}

/**
 * 获取访客数据
 */
export function getManageAccount() {
  return api.get('/datasets/shopCaller/get/info', null)
}

/**
 * 获取交易量排行数据
 */
export function getAccountRank() {
  return api.get('/datasets/dc-shop-ranking/get/rankingsInfo', null)
}

/**
 * 获取交易额排行数据
 */
export function getAccountGVMRank() {
  return api.get('/datasets/dc-shop-ranking/get/rankingsGVMInfo', null)
}
