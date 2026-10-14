import api from '@/libs/request'

/** 上传接口 */
export function upLoad(data: { file: any }) {
  const formData = new FormData()
  formData.append('file', data.file)
  return api.post(`oss/upload`, formData)
}

/**
 * 商城信息
 */
export function getShopMsg(data: Obj) {
  return api.get(`/integral/manager/shop/get/shopId`, data)
}
