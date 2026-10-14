import api from '@/libs/request'

/**
 * 获取展示分类列表
 */
export function getProList(data: Obj) {
  return api.get('/goods/manager/product/list', data)
}

/**
 * 获取属性列表
 */
export function getAttsList(data: Obj) {
  return api.get('/goods/manager/attribute/template/list', data)
}

/**
 * 获取所有属性列表
 */
export function getAllAttsList(data: Obj) {
  return api.get('/goods/manager/attribute/template/get/all', data)
}
/**
 * 删除属性列表
 */
export function delAttr(ids: string, data: Obj) {
  return api.delete(`/goods/manager/attribute/template/delete/${ids}`, data)
}
/**
 * 删除子属性模板
 */
export function delAttrSon(ids: string, data: Obj) {
  return api.delete(`/goods/manager/attribute/template/delete/child/${ids}`, data)
}

/**
 * 添加属性列表
 */
export function addAttrList(data: Obj) {
  return api.post('/goods/manager/attribute/template/save', data)
}

/**
 * 编辑属性列表
 */
export function updateAttrList(data: Obj) {
  return api.put('/goods/manager/attribute/template/update', data)
}

/**
 * 获取供应商列表
 */
export function getSupList(data: Obj) {
  return api.get('/goods/manager/supplier/list', data)
}

/**
 * 获取所有供应商
 */
export function getAllSupList(data: Obj) {
  return api.get('/goods/manager/supplier/get/all', data)
}

/**
 * 删除供应商
 */
export function delSup(ids: string, data: Obj) {
  return api.delete(`/goods/manager/supplier/delete/${ids}`, data)
}

/**
 * 添加供应商
 */
export function addSup(data: Obj) {
  return api.post('/goods/manager/supplier/save', data)
}

/**
 * 修改供应商
 */
export function updateSup(data: Obj) {
  return api.put('/goods/manager/supplier/update', data)
}

/** 供应商审核 */
export function dealApplySup(data: Obj) {
  return api.put(`/goods/manager/supplier/check`, data)
}

/**
 * 查看供应商名下商品
 */
export function ShowSupPro(data: Obj) {
  return api.get('/goods/manager/supplier/product', data)
}

/**
 * 获取展示分类列表
 */
export function getList(data: Obj) {
  return api.get('/goods/manager/show/category/list', data)
}

/**
 * 获取所有展示分类
 */
export function getAllCategory(data: Obj) {
  return api.get('/goods/manager/show/category/get/all', data)
}

/**
 * 获取拼团/商超有效展示分类 0-商超 1-拼团
 */
export function getEffectiveCategory(data: Long) {
  return api.get(`/goods/api/show/category/get/${data}`, {})
  // return api.get(`/goods/manager/show/category/get/${data}`, {});
}

/**
 * 删除展示分类
 */
export function delLevel(id: string, data: Obj) {
  return api.delete(`/goods/manager/show/category/delete/${id}`, data)
}

/**
 * 添加展示分类
 */
export function addList(data: Obj) {
  return api.post('/goods/manager/show/category/save', data)
}

/**
 * 获取物流模板
 */
export function getLogistics(data: Obj) {
  return api.get('/goods/manager/freight/template/list/by/logistics', data)
}

/**
 * 获取物流模板详情
 */
export function getLogisticsContent(freightTemplateId: string, data: Obj) {
  return api.get(`/goods/manager/freight/template/info/by/logistics/${freightTemplateId}`, data)
}

/**
 * 二级分类保存
 */
export function addSecondList(data: Obj) {
  return api.post('/goods/manager/show/category/save/second', data)
}

/**
 * 分类编辑保存
 */
export function updateList(data: Obj) {
  return api.put('/goods/manager/show/category/update', data)
}

/**
 * 接龙状态变更
 */
export function updateDominoState(state: string, data: Obj) {
  return api.put(`/goods/manager/product/updateDominoState/${state}`, data)
}

/**
 * 商品专区变更
 */
export function updateGoodsApart(saleMode: string, data: Long[]) {
  return api.put(`/goods/manager/product/updateSaleMode/${saleMode}/0`, data)
}

/**
 * 发布商品
 */
export function putWayGood(data: Obj) {
  return api.post('/goods/manager/product/issue', data)
}

/**
 * 编辑商品
 */
export function editGood(id: string, data: Obj = {}) {
  return api.get(`/goods/manager/product/get/${id}`, data)
}

/**
 * 编辑发布商品
 */
export function updateGood(data: Obj) {
  return api.put('/goods/manager/product/update', data)
}

/**
 * 编辑展示分类
 */
export function updateGoodShow(ids: string, data: Obj) {
  return api.post(`/goods/manager/product/updateProductShowCategory/${ids}`, data)
}

/**
 * 商品上下架
 */
export function GoodUpDown(status: string, mode: string, data: Obj) {
  return api.put(`/goods/manager/product/updateStatus/${status}/${mode}`, data)
}

/**
 * 商品删除
 */
export function GoodDel(ids: string, mode: string, data: Obj) {
  return api.delete(`/goods/manager/product/delete/${ids}/${mode}`, data)
}

/**
 * 分类排序
 */
export function Sort(data: Obj) {
  return api.put('/goods/manager/show/category/sort', data)
}

/**
 * 获取商品码
 */
export function getImgCode(data: Obj) {
  return api.post('/platform/mini-info/wxa/getwxacode', data)
}

/**
 * 商品编辑会员价格
 */
export function putGoodMemberPrice(id: string, data: Obj) {
  return api.put(`/goods/manager/product/update/member/price/${id}`, data)
}

/**
 * 商品编辑返利价格
 */
export function putRebatePrice(id: string, data: Obj) {
  return api.put(`/goods/manager/product/update/sku/rebate/${id}`, data)
}

/**
 * 过滤不可用的商品
 */
export function discountList(data: string | string[]) {
  return api.get(`/goods/manager/product/alive/product/list/${data}`, {})
}

/**
 * 通用设置
 */
export function getRebateInSet(data: Obj) {
  return api.get(`/goods/manager/rebate/set/get`, data)
}

/**
 * 保存通用设置
 */
export function putRebateInSet(data: Obj) {
  return api.put(`/goods/manager/rebate/set/save`, data)
}

/**
 * 获取默认专区
 */
export function getRegionList(data: Obj) {
  return api.get(`/goods/manager/sale/mode/list`, data)
}

/**
 * 删除专区
 */
export function delRegionItem(id: string, data: Obj) {
  return api.delete(`/goods/manager/sale/mode/delete/${id}`, data)
}

/**
 * 获取所有专区
 */
export function getAllRegionList(data: Obj) {
  return api.get(`/goods/manager/sale/mode/get/all`, data)
}

/**
 * 专区排序
 */
export function sortRegionList(data: Obj) {
  return api.put(`/goods/manager/sale/mode/sort`, data)
}

/**
 * 专区编辑
 */
export function editRegionList(data: Obj) {
  return api.put(`/goods/manager/sale/mode/update`, data)
}

/**
 * 专区新增
 */
export function addRegionList(data: Obj) {
  return api.post(`/goods/manager/sale/mode/save`, data)
}
