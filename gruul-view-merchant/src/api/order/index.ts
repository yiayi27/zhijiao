import api from '@/libs/request'

/**
 * 获取商品列表
 */
export function getOrders(data: Obj) {
  return api.get('/order/manage/search', data)
}

/**
 * 获取商品详情
 * @param orderId 订单id
 */
export function getOrderDetail(orderId: string) {
  return api.get(`/order/manage/info/${orderId}`, null)
}

/**
 * 备注
 */
export function remarks(data: Obj) {
  return api.put(`/order/manage/note`, data)
}

/**
 * 关闭订单
 */
export function close(ids: string[]) {
  return api.put('/order/manage/close', ids)
}

/**
 * 获取评价列表
 */
export function getEvaluateList(query: Obj) {
  return api.get('/order/manage/evaluate/search', query)
}

/**
 * 设置精选
 */
export function setChoice(ids: Long[]) {
  return api.put('/order/manage/choice/evaluate', ids)
}

/**
 * 取消精选
 */
export function cancelChoice(ids: Long[]) {
  return api.put('/order/manage/unChoice/evaluate', ids)
}

/**
 * 回复评价
 */
export function reply(form: Obj) {
  return api.put('/order/manage/reply/evaluate', form, { params: form })
}

/**
 * 获取物流信息
 */
export function getdeliveryInfo(form: Obj) {
  return api.get('/order/manage/traces', form)
}

/**
 * 获取订单设置
 */
export function getOrderManageSetting() {
  return api.get('/order/manage/setting', null)
}

/**
 * 设置订单设置
 */
export function setOrderManageSetting(form: Obj) {
  return api.post('/order/manage/setting', form)
}

/**
 * 获取评价是否开启
 */
export function getManageSetting() {
  return api.get('/order/manage/evaluate/setting', null)
}

/**
 * 设置评价是否开启
 * @param form
 */
export function setManageSetting(form: Obj) {
  return api.put('/order/manage/evaluate/setting', null, { params: form })
}

/**
 * 查询快递订单v2
 * @param query
 */
export function searchLogistics(query: Obj) {
  return api.get('/order/manage/search/logistics', query)
}

/**
 * 打印并发货
 * @param query
 */
export function printDeliver(query: Obj) {
  return api.post('/logistics/normal/print/deliver', query)
}

/**
 * 获取是否存在待发货订单
 */
export function getLogisticsWait() {
  return api.get('/order/manage/logistics/wait/send', null)
}

/**
 * 无需物流发货
 */
export function logisticsSend(orderIds: string) {
  return api.put('/order/manage/logistics/send', { orderIds })
}

/**
 * 更新订单地址
 * @param form
 */
export function updateAddress(form: Obj) {
  return api.put('/order/manage/receiver/address', form)
}
