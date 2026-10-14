import api from '@/utils/request'

/**
 * 获取订阅模板id
 */
// export const getSubTemplate = () => {
//   return api.get("/platform/min-subscriberi-message/mini", null);
// };

/**
 * 聚合接口
 * 获取评价设置、佣金设置、消息订阅
 */
export const getSetting = () => {
  return api.get('/order/mini/setting', null, { cache: true })
}

/**
 * 获取引导页设置
 */
export const getGuide = () => {
  return api.get('/shops/shop/guide/page/get', null, { cache: true })
}

export const getGuideVisible = () => {
  return api.get('/shops/shop/guide/switch/page/get', null)
}

export const getRecyclingSetting = () => {
  return api.get('/recycling/recycling-setting/info', null)
}

export const getRecyclingSend = () => {
  return api.get('/recycling/recycling-order/exit/wait/send', null)
}

export const addNewRecycling = (data: any) => {
  return api.post('/recycling/recycling-order/new', data)
}
