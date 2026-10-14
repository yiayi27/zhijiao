import api from '@/libs/request'

/** 获取弹窗图片 */
export function getDialogImg(data: Obj) {
  return api.put(`/recycling/recycling-setting/popup/window`, data)
}

/**
 * 获取关联优惠券
 */
export function getRecoveryCoupon(data: Obj) {
  return api.get(`/discount/manager/coupon/recycle/list`, data)
}

/** 回收返券通用设置 */
export function saveRecoverySet(data: Obj) {
  return api.put(`/recycling/recycling-setting/coupon/modify`, data)
}

/** 获取相关订单 */
export function getRecoveryOrderList(data: Obj) {
  return api.get(`/recycling/recycling-order/pc/list`, data)
}
