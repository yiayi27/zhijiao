import api from '@/utils/request'

/**获取分销中心首页信息 */
export const distributionGet = (data: any) => {
  return api.get('/account/mini-account/get/user/distribution/index', data)
}
/**获取分销中心明细信息 */
export const distributionDetailGet = (data: any) => {
  return api.get('/account/mini-account/get/user/distribution/detail', data)
}
/**申请提现 */
export const userMsgUpdate = (data: any) => {
  return api.post('/finance/mini/withdrawal', data)
}
/**绑定当前用户的分销信息 */
export const handUserid = (userId: any, data: any) => {
  return api.post(`/account/mini-account/set/distribution/info/${userId}`, data)
}
