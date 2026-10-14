import api from '@/utils/request'

// 小程序登录
// export const login = (data: any) => {
//   return api.get("/account/mini-account/login", data);
// };

export const login = (data: any) => {
  return api.get(`/platform/multi/auth/wechat/mini/login/${data}`, data)
}

// 更新用户信息
export const userMsgUpdate = (data: any) => {
  return api.post('/account/mini-account', data)
}
// 解密获取用户手机号
export const getPhone = (data: any) => {
  return api.post('/account/mini-account/decode/phone/info', data)
}

/** 获取用户信息 */
export const userMsgGet = (data: any) => {
  return api.get('/account/mini-account', data)
}

/** 获取当前用户id */
export const userIdGet = (data: any) => {
  return api.get('/account/mini-account/getUserId', data)
}

/**
 * 统计用户访问数据
 */
export const accountUserLogin = (data: any) => {
  return api.post('/datasets/shopCaller/add/info', data)
}

/**
 * 获取店铺信息
 */
export const getShopManage = (data: any) => {
  return api.get(`/platform/shop/info`, data, { cache: true })
}

export const putCancalRecycling = (id: any) => {
  return api.put(`/recycling/recycling-order/cancel/${id}`, null)
}

export const getRecyclingList = (params: any) => {
  return api.get('/recycling/recycling-order/list', params)
}

export const delRecycling = (id: any) => {
  return api.delete(`/recycling/recycling-order/del/${id}`, null)
}

export const sendRecycling = (data: any) => {
  return api.put('/recycling/recycling-order/send', data)
}

export const getRecyclingStatistical = () => {
  return api.get('/recycling/recycling-ass-order-account/me/statistical', null)
}

export const getRecyclingOrderlist = (params: any) => {
  return api.get('/recycling/recycling-ass-order/list', params)
}
// 获取loginid
export const getLoginId = (param: any) => {
  return api.post('/platform/multi/auth/wechat/mini/mobile/login', param)
}
// 登录统一接口
export const uniformLogin = (param: any) => {
  return api.post(`/multi-client-account/multi/account/login/${param}`)
}
// 保存用户信息
export const saveUserInfo = (params: { nickname: string; avatar: string }) => {
  return api.post('/platform/account-info/update/data', params)
}
/**获取第三方tenantId */
export const getTenantId = () => {
  return import.meta.env.VITE_APP_TENAN_ID
  // return api.get("/platform/mini-info/get/tenantId",params)
}
