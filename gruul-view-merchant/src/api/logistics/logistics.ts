import api from '@/libs/request'

/**
 * 获取运费模板
 */
export function getLogistModel(data: Obj) {
  return api.get('logistics/get/template/list', data)
}

/**
 * 删除运费模板
 */
export function delLogistModel(id: string, data: Obj) {
  return api.delete(`logistics/delete/template/info/${id}`, data)
}

/**
 * 新增单个模板信息
 */
export function addLogistModel(data: Obj) {
  return api.post('logistics/set/template/info', data)
}

/**
 * 获取地址信息
 */
export function getShopAddress(data: Obj) {
  return api.get('logistics/get/address/list', data)
}

/**
 * 获取所有地址信息
 */
export function getAllShopAddress(data: Obj) {
  return api.get('logistics/get/all/address', data)
}

/**
 * 设置收货退货地址
 */
export function setShopAddress(id: Long, type: number, data: Obj) {
  return api.put(`logistics/set/def/address/${id}/${type}`, data)
}

/**
 * 删除
 */
export function delShopAddress(id: string, data: Obj) {
  return api.delete(`logistics/del/address/${id}`, data)
}

/**
 * 添加地址信息
 */
export function addShopAddress(data: Obj) {
  return api.post('logistics/set/address', data)
}

/**
 * 获取物流公司
 */
export function getLogisticsCompany(data: Obj = {}) {
  return api.get('logistics/list/company', data)
}

/**
 * 批量发货
 */
export function bulkShipment(data: Obj) {
  return api.post('logistics/batch/deliver/goods', data)
}

/**
 * 获取电子面单打印机列表
 */
export function getSheetList(data: Obj) {
  return api.get('logistics/get/express/print/list', data)
}

/**
 * 新增/修改电子面单打印机
 */
export function addSheetMach(data: Obj) {
  return api.post('logistics/set/express/print/info', data)
}

/**
 * 启用/停用电子面单打印机
 */
export function sheetLink(id: string, type: string, data: Obj) {
  return api.put(`logistics/set/express/print/status/${id}/${type}`, data)
}

/**
 * 删除电子面单打印机
 */
export function delSheetMach(id: string, data: Obj) {
  return api.delete(`logistics/del/express/print/${id}`, data)
}

/**
 * 获取物流公司地址
 */
export function getLogAddress(data: Obj) {
  return api.get(`logistics/get/express/list`, data)
}

/**
 * 开通物流地址
 */
export function openLogAddress(data: Obj) {
  return api.post(`logistics/set/express/info`, data)
}

/**
 * 删除物流公司地址
 */
export function delCompony(id: string, data: Obj) {
  return api.delete(`logistics/del/express/${id}`, data)
}

/**
 * 设置默认发货公司
 */
export function setDefaultAddress(logisticsCompanyId: string) {
  return api.get(`logistics/set/default`, {
    logisticsCompanyId,
  })
}

/**
 * 导入所有代发货订单
 */
export function importAllOrder(data: Obj) {
  return api.get(`integral/manager/integralOrder/get/allWaitForSend`, data)
}

/**
 * 积分订单无需物流发货
 */
export function sendNoLogistic(data: Obj) {
  return api.put(`integral/manager/integralOrder/noLogistics`, data)
}
