import api from '@/libs/request'

/**
 * 添加满减活动
 */
export function addFull(data: Obj) {
  return api.post(`/discount/manager/full/scale/add`, data)
}

/**
 * 获取满减活动
 */
export function getFullActivity(data: Obj) {
  return api.get(`/discount/manager/full/scale/list`, data)
}

/**
 * 满减活动详情
 */
export function fullActivityDet(id: string, data: Obj) {
  return api.get(`/discount/manager/full/scale/get/${id}`, data)
}

/**
 * 修改满减活动
 */
export function updateFullActivity(data: Obj) {
  return api.put(`/discount/manager/full/scale/update`, data)
}

/**
 * 删除满减活动
 */
export function delFullActivity(ids: string, data: Obj) {
  return api.delete(`/discount/manager/full/scale/delete/${ids}`, data)
}

/**
 * 关闭活动
 */
export function offFullActivity(status: number, data: Obj) {
  return api.put(`/discount/manager/full/scale/changeStatus/${status}`, data)
}
