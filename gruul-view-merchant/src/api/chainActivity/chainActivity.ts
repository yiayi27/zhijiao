import api from '@/libs/request'

/**
 * 发布接龙活动
 */
export function addActivity(data: Obj) {
  return api.post(`/solitaire/manager/activity/add`, data)
}

/**
 * 获取接龙数据
 */
export function getActivityList(data: PageParam) {
  return api.get(`/solitaire/manager/activity/list`, data)
}

/**
 * 关闭接龙活动
 */
export function closeActivityById(id: string, data: Obj) {
  return api.put(`/solitaire/manager/activity/changeStatus/${id}`, data)
}

/**
 * 删除接龙活动
 */
export function delActivityById(id: string, data: Obj) {
  return api.delete(`/solitaire/manager/activity/delete/${id}`, data)
}

/**
 * 获取单个接龙活动信息
 */
export function getActivityById(id: string, data: Obj) {
  return api.get(`/solitaire/manager/activity/get/${id}`, data)
}

/**
 * 编辑单个接龙活动信息
 */
export function editActivityById(data: Obj) {
  return api.put(`/solitaire/manager/activity/update`, data)
}

/**
 * 转发当前接龙信息
 */
export function transFormActivityById(activityId: string, data: Obj) {
  return api.get(`/solitaire/api/weChat/current/activity/product/${activityId}`, data)
}

/**
 * 获取接龙设置信息
 */
export function getActivitySetting(data: Obj) {
  return api.get(`/solitaire/manager/info/get`, data)
}

/**
 * 手动控制关闭接龙状态
 */
export function changeActivitySetting(data: Obj) {
  return api.post(`/solitaire/manager/info/save`, data)
}
