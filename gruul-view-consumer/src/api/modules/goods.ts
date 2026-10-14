import api from '@/utils/request'

// 查看商超商品列表
export const getGoodsList = (data: any, params: any) => {
  return api.get('/goods/api/product/supermarket/list', { ...data, ...params }, { cache: true })
}

// 查看优惠券可用商品列表
export const getCouponGoodsList = (id: any, params: any) => {
  return api.get(`/discount/api/member/coupon/alive/product/list/${id}`, { ...params }, { cache: true })
}

// 查看有效的一级商超分类及二级分类和商品
export const getEffectCategoryGoods = (data: any, param: any) => {
  return api.get(`/goods/api/product/get/alive/product/list/by/category/${data}`, { ...param }, { cache: true })
}

// 查看商品列表
export const searchGoodsList = (data: any) => {
  return api.get('/goods/api/product/index/list', { ...data }, { cache: true })
}

// 查看商品会员价与返利对象
export const getRebateInSet = (data: any) => {
  return api.get(`/goods/manager/rebate/set/get`, data)
}

// 查看商品详情
export const getGoodDetail = (id: Long, data: any) => {
  return api.get(`/goods/api/product/get/${id}`, data)
}

/**
 * 获取商超有效展示分类  0-商超 1-拼团
 */
export const getEffectiveCategory = (data: any) => {
  return api.get(`/goods/api/show/category/get/${data}`, {})
}

/**
 * 获取所有展示分类
 */
export const getAllCategory = (data: any) => {
  return api.get('/goods/api/show/category/get/all', data)
}

// 查看商品参与优惠券活动
export const getCouponList = (data: any) => {
  return api.get(`/goods/api/product/get/coupon`, data)
}

// 查看商品参与满减活动
export const getFullList = (data: any) => {
  return api.get(`/goods/api/product/get/full/scale`, data)
}

// 获取商品评价
export const fetchProductEvaluate = (data: any) => {
  return api.get(`/order/mini/product/evaluate`, data)
}

// 获取商品概况
export const getProductEvaluateOverview = (data: any) => {
  return api.get(`/order/mini/product/evaluate/overview`, data)
}

// 商品是否被收藏
export const checkIsCollect = (data: any) => {
  return api.get(`/account/api/collect/findAccountIsCollect`, data)
}

// 收藏商品
export const userAddCollect = (data: any) => {
  return api.post(`/account/api/collect/plus`, data)
}

// 删除用户收藏
export const userDelCollect = (data: any) => {
  return api.post(`/account/api/collect/delAccountCollect`, data)
}

// 获取收藏列表
export const userCollectList = (data: any) => {
  return api.get(`/account/api/collect/getUserCollectInfo`, data)
}

// 添加用户足迹
export const userFooterAdd = (data: any) => {
  return api.post(`/account/api/FootMark/add/FootMarkInfo`, data)
}

// 查看用户足迹列表
export const userFooterList = (data: any) => {
  return api.get(`/account/api/FootMark/getUserFootMarkInfo`, data)
}

// 删除用户足迹商品
export const userFooterDel = (type: any, data: any) => {
  return api.put(`/account/api/FootMark/del/FootMarkInfo/${type}`, data)
}

/**
 * 获取商品码
 */
export const getImgCode = (data: any) => {
  return api.post('/platform/mini-info/wxa/getwxacode', data)
}

/**
 * 过滤不可用的商品
 */
export const discountList = (data: any, params: any) => {
  return api.get(`/goods/api/product/alive/product/list/${data}`, params)
}

/**
 * 查询商品是否参与了接龙活动
 */
export const joinChain = (productId: any, params: any) => {
  return api.get(`/solitaire/api/weChat/get/activity/by/product/${productId}`, params)
}
