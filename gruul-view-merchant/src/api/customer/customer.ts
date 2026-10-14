import api from '@/libs/request'

/**
 * 获取用户列表
 */
export function getCustomerList(data: Obj) {
  return api.get(`/account/mini-account/list`, data)
}

/**
 * 批量黑名单
 */
export function batchSetBlackList(data: Obj) {
  return api.put(`/account/mini-account/set/blacklist`, data)
}

/**
 * 获取黑名单用户列表
 */
export function getBlackList(data: Obj) {
  return api.get(`/account/mini-account/blacklist`, data)
}

/**
 * 查询所有标签
 */
export function getAllTags(data: Obj = {}) {
  return api.get(`/account/mini-account-tag`, data)
}

/**
 * 设置用户标签(添加|删除|设置)
 */
export function editTags(data: Obj) {
  return api.post(`/account/mini-account-tag`, data, {
    headers: {
      'Content-Type': 'application/json',
    },
  })
}

/**
 * 赠送优惠券
 * @param ids 客户id
 * @param data 优惠券数据
 */
export function giveCoupon(ids: string[], data: Obj) {
  const idString = ids.join(',')
  return api.post(`/discount/manager/coupon/give/${idString}`, data, {
    headers: {
      'Content-Type': 'application/json',
    },
  })
}

/**
 * 获取会员卡等级
 */
export function getAllTagsVipLevel() {
  return api.get(`/account/manager/use/memberCardRank`)
}
