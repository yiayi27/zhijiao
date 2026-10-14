import api from '@/libs/request'

/*
 * 获取省市区
 */
export function getArea(data: Obj) {
  return api.get('/account/mini-account-address/area', data)
}

/**
 * 获取分享设置
 */
export function getShareSetting() {
  return api.get('/order/manage/share/setting', null)
}

/**
 * 获取引导页设置
 */
export function getGuide() {
  return api.get('/shops/shop/guide/page/get', null)
}

export function getGuideSetting() {
  return api.get('/shops/shop/guide/switch/page/get', null)
}

export function setGuideSetting(status: boolean) {
  return api.get('/shops/shop/guide/switch/page/update', { status })
}

export function getDefGuideSetting() {
  return api.get('/shops/shop/guide/page/getDefault', null)
}

export function setGuide(data: Obj) {
  return api.post('shops/shop/guide/page/guidePage/update', data)
}
