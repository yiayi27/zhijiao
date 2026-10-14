import api from '@/libs/request'
import type { PayFormType } from './type'

/**
 * 获取支付配置
 */
export function getPaymentConfig(data: { code: string; type: string }) {
  return api.get(`/payment/multi/merchant`, data)
}

/**
 * @description: 保存支付配置
 */
export function savePaymentConfig(data: Partial<Record<PayFormType, string>>) {
  return api.post(`/payment/multi/merchant`, data)
}

/**
 * 上传支付证书
 */
export function uploadCertificate(data: { file: any }) {
  const formData = new FormData()
  formData.append('file', data.file)
  return api.post(`/payment/multi/merchant/wechat/upload/cert`, formData)
}

/**
 * 根据租户id获取店铺基本信息
 */
export async function getShopInfo() {
  const response = await api.get(`/platform/shop/info`, {})
  const { data } = response
  return data
}

/**
 * 控制台店铺设置(修改店铺信息)
 * @param shopId
 */
export function updateShopInfo(shopId: string, data: Obj) {
  return api.put(`/platform/shop/console/update/${shopId}`, data, {
    headers: {
      'Content-Type': 'application/json',
    },
  })
}
