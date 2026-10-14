import api from '@/libs/request'

/**
 * 获取直播列表
 */
export function getLiveList(data: Obj) {
  return api.get('/live/manager/get/List', data)
}

/**
 * 更新直播信息
 */
export function updateLanuch(data: Obj) {
  return api.get('/live/manager/update/launchArea', data)
}

export function reFreshList(data: Obj) {
  return api.get('/live/manager/get/weChatList', data)
}

/**
 * 获取商品码
 */
export function getImgCode(data: Obj) {
  return api.post('/platform/mini-info/wxa/getwxacode', data)
}
