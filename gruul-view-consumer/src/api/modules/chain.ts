import api from '@/utils/request'

// 获取接龙开启关闭开关
export const getActivitySetting = (data: any) => {
  return api.get(`/solitaire/manager/info/get`, data, { cache: true })
}

// 查看接龙活动信息
export const getActivityList = (data: any) => {
  return api.get('/solitaire/api/weChat/current/activity', data, {
    cache: true,
  })
}

// 接龙活动关闭
export const closeActivitySetting = (data: any) => {
  return api.post(`/solitaire/manager/info/save`, data)
}

// 查看接龙活动详情
export const getActivityListDetail = (activityId: any, data: any) => {
  return api.get(`/solitaire/api/weChat/get/activity/${activityId}`, data)
}

// 获取商品详情
export const getListDetail = (data: any) => {
  return api.get(`/goods/api/product/get/solitaire/activity/product/by/param`, data)
}

// 获取用户下的订单
export const getUserRecord = (data: any) => {
  return api.get('/order/mini/solitaire/my', data)
}

// 获取接龙记录
export const getChainRecord = (data: any) => {
  return api.get('/order/mini/solitaire/buyer', data)
}

/**
 * 复制h5链接
 */
export const transFormActivityById = (activityId: any, data: any) => {
  return api.get(`/solitaire/api/weChat/current/activity/product/${activityId}`, data)
}
