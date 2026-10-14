import api from '@/utils/request'

// /afs/mini/user/search?

export const getAfterSaleList = (data?: any) => {
  return api.get('/afs/mini/user/search', data)
}

/** 申请售后 */
export const applyAfterSale = (data?: any) => {
  return api.post('/afs/mini/user/apply', data)
}

/**
 * 获取协商历史
 * @param orderId
 */
export const getNegotiateHistory = (data: { orderId: string; type: string }) => {
  return api.get('/afs/negotiate/history', data)
}

/**
 * 获取售后详情
 * @param afsId
 */
export const getAfterDetail = (afsId: string) => {
  return api.get('/afs/mini/user/info', { afsId })
}

/**
 * 用户确认退货
 * @param afsId
 */
export const returnOfGoods = (data: {
  afsId: string
  deliveryCompany?: string
  deliverySn?: string
  deliveryCode?: string
  phone?: string
  reason?: string
}) => {
  return api.put(
    `/afs/mini/user/return?afsId=${data.afsId}&deliverySn=${data.deliverySn}&deliveryCode=${data.deliveryCode}&deliveryCompany=${data.deliveryCompany}&phone=${data.phone}&reason=${data.reason}`,
    data,
  )
}

/**
 * 获取申请次数
 * @param orderId
 */
export const getApplyNumber = (orderId: string) => {
  return api.get('/afs/mini/user/apply/goods/number', { orderId })
}

/**
 * 用户取消申请
 * @param afsId
 */
export const cancel = (afsId: string) => {
  return api.put(`/afs/mini/user/cancel?afsId=${afsId}`, { afsId })
}

/**
 * 获取物流公司
 */
export const getLogisticsCompany = (data: any) => {
  return api.get('/logistics/list/company', data)
}

/**
 * 获取退货地址
 * @param data
 */
export const getReturnAddress = (orderId: string) => {
  return api.get('/afs/mini/user/return/address', { orderId })
}
