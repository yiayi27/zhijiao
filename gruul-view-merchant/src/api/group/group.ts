import api from '@/libs/request'

/**
 *
 * 获取装修自定义页面
 */
export function getPageList(data: Obj) {
  return api.get('/shops/renovation/template/page/list', data)
}
/**
 * 页面获取装修组件
 */
export function getAssembly(data: Obj) {
  return api.post('/shops/renovation/template/page/assembly/list', data)
}
/**
 * 新增装修自定义页面
 */
export function addPage(data: Obj) {
  return api.put('/shops/renovation/template/page/save', data)
}
/**
 * 页面新增装修组件
 */
export function addAssembly(data: Obj) {
  return api.post('/shops/renovation/template/page/assembly/add', data)
}

/**
 * 删除装修自定义页面
 */
export function delPage(data: Obj) {
  return api.get('/shops/renovation/template/page/delete', data)
}
/**
 * 获取是否页面已添加内容
 */
export function getJudgeAssembly(data: Obj) {
  return api.get('/shops/renovation/template/page/modelId', data)
}
