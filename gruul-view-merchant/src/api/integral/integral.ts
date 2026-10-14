import api from '@/libs/request'

/**
 * 获取积分商品
 */
export function getIntegralGoods(data: Obj) {
  return api.get('/integral/manager/get/integralGoods/List', data)
}

/**
 * 发布积分商品
 */
export function putIntegralGoods(data: Obj) {
  return api.post(`/integral/manager/issue/integralGoods`, data)
}

/**
 * 删除积分商品
 * @param ids
 */
export function delIntegralGoods(ids: string, data: Obj) {
  return api.delete(`/integral/manager/delete/batch/integralGoods/${ids}`, data)
}

/**
 * 获取积分商品详情
 */
export function getIntegralGoodsDetail(data: Obj) {
  return api.get(`/integral/manager/get/integralGoodsInfo`, data)
}

/**
 * 编辑积分商品详情
 */
export function editIntegralGoodsDetail(data: Obj) {
  return api.put(`/integral/manager/update/integralGoods`, data)
}

/**
 * 上下架积分商品
 * @param status
 */
export function upDownIntegralGoods(status: string, data: Obj) {
  return api.put(`/integral/manager/update/batchIntegralGoods/${status}`, data)
}

/**
 * 控制积分商城开启关闭状态
 */
export function takeOnOff(data: Obj) {
  return api.get(`/integral/manager/shop/update/status`, data)
}

/**
 * 获得积分规则
 */
export function integralRule(data: Obj) {
  return api.get(`integral/rule/get`, data)
}

/**
 * 积分规则修改
 */
export function integralRuleUpdate(data: Obj) {
  return api.put(`integral/rule/update`, data)
}

/**
 * 查询积分优惠券订单
 */
export function integralCouponOrder(data: Obj) {
  return api.get(`integral/manager/integralCouponOrder/get/integralCouponOrder`, data)
}

/**
 * 积分订单批量备注功能
 */
export function remarkIntegralCouponOrder(data: Obj) {
  return api.put(`integral/manager/integralCouponOrder/update/orderRemark`, data.ids, {
    params: data,
  })
}

/**
 * 查询积分订单
 */
export function integralOrder(data: Obj) {
  return api.get(`integral/manager/integralOrder/get/integralOrder`, data)
}

/**
 * 查询单个积分订单
 */
export function integralOrderById(data: Obj) {
  return api.get(`integral/manager/integralOrder/get/integralOrderInfo`, data)
}

/**
 * 修改单个积分订单
 */
export function updateIntegralOrderById(data: Obj) {
  return api.put(`integral/manager/integralOrder/update/ReceivingInfo`, data)
}

/**
 * 积分订单批量备注
 */
export function integralRemarkOrder(data: Obj) {
  return api.put(`integral/manager/integralOrder/update/orderRemark`, data.integralOrders, {
    params: data,
  })
}

/**
 * 积分订单确认收货
 */
export function integralOrderSure(data: Obj) {
  return api.put(`integral/manager/integralOrder/receive`, data)
}

/**
 * 积分订单发货
 */
export function sendOneintegralGood(data: Obj) {
  return api.put(`integral/manager/integralOrder/orderDelivery`, data)
}

/**
 * 积分订单打印发货
 */
export function sendintegralGoodByPrint(data: Obj) {
  return api.post(`logistics/print/deliver/integralOrder`, data)
}

/**
 * 积分订单物流信息
 */
export function integralGoodLogMsg(data: Obj) {
  return api.get(`integral/manager/integralOrder/traces/info`, data)
}

/**
 *
 * pc端积分明细获取
 */
export function getIntegralDetail(data: Obj) {
  return api.get(`integral/detail/gain/detail`, data)
}

/**
 * 积分修改
 */
export function changeIntegral(data: Obj) {
  return api.put(`integral/detail/change`, data.userIds, {
    params: data,
  })
}
