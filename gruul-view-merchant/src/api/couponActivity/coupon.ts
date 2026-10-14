import api from '@/libs/request'

/**
 * 添加优惠券
 */
export function addCoupon(data: Obj) {
  return api.post(`/discount/manager/coupon/add`, data)
}

/**
 * 编辑优惠券
 */
export function editCoupon(data: Obj) {
  return api.put(`/discount/manager/coupon/update`, data)
}

/**
 * 获取积分优惠券列表
 */
export function couponIntList(data: Obj) {
  return api.get(`/discount/manager/coupon/integral/list`, data)
}

/**
 * 获取优惠券列表
 */
export function couponList(data: Obj) {
  return api.get(`/discount/manager/coupon/list`, data)
}

/**
 * 查看优惠券详情
 */
export function couponDetail(id: string, data: Obj) {
  return api.get(`/discount/manager/coupon/get/${id}`, data)
}

/**
 * 批量删除优惠券
 */
export function delCoupons(ids: string, data: Obj) {
  return api.delete(`/discount/manager/coupon/delete/${ids}`, data)
}

/**
 * 关闭优惠券
 */
export function offCoupons(status: number, data: Obj) {
  return api.put(`/discount/manager/coupon/changeStatus/${status}`, data)
}

/**
 * 获取优惠券商品
 */
export function getCouponsGoods(data: Obj) {
  return api.get(`/goods/manager/product/discount/list`, data)
}
