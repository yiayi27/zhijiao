import api from '@/libs/request'

/**
 * pc获取租户所有买家消息或商家消息
 * @param type 获取类型1-买家消息 2-商家消息
 */
export function getSubMessage(type: string) {
  return api.get(`/platform/min-subscriberi-message/pc/${type}`, {})
}

/**
 * pc获取租户所有买家消息
 */
export function getBuyerMessage() {
  return api.get(`/platform/shop-message/buyer/notify`, {})
}

/**
 * 更新商家消息使用状态
 * @param id
 */
export function updateMerchantState(id: string, data: Obj) {
  return api.put(`/platform/min-subscriberi-message/${id}`, data, {
    headers: {
      'Content-Type': 'application/json',
    },
  })
}

/**
 * 修改消息开启状态
 */
export function updateMessageState(data: Obj) {
  return api.put(`/platform/shop-message/state`, data, {
    headers: {
      'Content-Type': 'application/json',
    },
  })
}
