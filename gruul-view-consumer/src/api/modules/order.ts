import api from '@/utils/request'

/**
 * 获取订单列表
 * @param query
 */
export const getOrders = (query: any) => {
  return api.get('/order/mini/search', query)
}

/**
 * 付款
 * @param orderId
 */
export const payment = (orderId: Long, userBalance: any = false) => {
  return api.put(`/order/mini/pay/${orderId}?userBalance=${userBalance}`)
}

/**
 * 确认收货
 * @param orderId
 */
export const receipt = (orderId: string) => {
  return api.put(`/order/mini/receipt/${orderId}`)
}

/**
 * 取消订单
 * @param orderId
 */
export const cancel = (orderId: string) => {
  return api.put(`/order/mini/cancel/${orderId}`)
}

/**
 * 获取订单详情
 * @param orderId
 */
export const getOrderDetail = (orderId: string) => {
  return api.get(`/order/mini/info/${orderId}`)
}

/**
 * 获取评价列表
 * @param query
 */
export const getEvaluateList = (query: any) => {
  return api.get('/order/mini/evaluate', query)
}

/**
 * 评价订单
 * @param body
 */
export const evaluate = (body: any) => {
  return api.post('/order/mini/evaluate', body)
}

/**
 * 获取结算页面
 * @param form
 */
export const getConfirmOrder = (form: any) => {
  return api.post(`/order/mini/confirm_order`, form)
}

/**
 * 创建订单
 * @param form
 */
export const createOrder = (form: any) => {
  return api.post(`/order/mini/create`, form)
}

/**
 * 创建订单创建状态
 * @param form
 * res -1->秒杀失败;0->排队中;order->成功;
 */
export const getOrderStatus = (form: any) => {
  return api.post(`/order/mini/result`, form)
}

/**
 * 模拟支付
 */
export const mockPay = (orderId: any) => {
  return api.put(`/order/mini/pay/notify/${orderId}`)
}

/**
 * 获取运费
 */
export const getFreightAmount = (query: any) => {
  return api.post(`/order/mini/freightAmount`, query)
}

/**
 * 获取物流信息
 */
export const getdeliveryInfo = (form: any) => {
  return api.get('/order/manage/traces', form)
}

/**
 * 获取物流信息
 */
export const getSenddelivery = (form: any) => {
  return api.get('/integral/manager/integralOrder/traces/info', form)
}

/**
 * 更新订单地址
 * @param form
 */
export const updateAddress = (form: any) => {
  return api.put('/order/mini/receiver/address', form)
}
