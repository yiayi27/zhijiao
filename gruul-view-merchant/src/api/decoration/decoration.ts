import api from '@/libs/request'

/**
 * 获取展示分类列表
 */
export function getAllCategory(data: Obj) {
  return api.get('/goods/api/show/category/get/all', data)
}

/**
 * 获取用户中心配置
 */
export function getUserCenterConfig() {
  return api.get(`/shops/account-center`)
}

/**
 * 设置用户中心配置
 */
export function setUserCenterConfig(data: Obj) {
  return api.put(`/shops/account-center`, data, {
    headers: {
      'Content-Type': 'application/json',
    },
  })
}

/**
 * 批量新增控件
 */
export function addControlMore(params: Obj) {
  return api.post('/shops/renovation/plugins/add', params)
}
/**
 * 获取商铺装修模板列表
 */
export function getTemplate(params: Obj) {
  return api.post('/shops/renovation/template/list', params)
}

/** 新增控件 */
export function addControl(params: Obj) {
  return api.post('/shops/renovation/plugin/add', params)
}

/** 获取装修控件列表 */
export function getControl(params: Obj) {
  return api.post('/shops/renovation/plugin/list', params)
}
