import api from '@/utils/request'

// 获取积分商城的开启关闭
export const integralShopType = (data: any) => {
  return api.get(`/integral/api/shop/functionInfo`, data)
}

// 获取积分优惠券列表
export const getIntegralCoupon = (data: any) => {
  return api.get(`/discount/api/coupon/integral/list`, data)
}

// 进行积分优惠券兑换
export const changeIntegralCoupon = (id: any, data: any) => {
  return api.post(`/discount/api/member/coupon/exchangeCoupon/${id}`, data)
}

// 获取积分商品列表
export const getIntegralGoods = (data: any) => {
  return api.get(`/integral/api/get/integralGoods`, data)
}

// 获取积分商品详情
export const getIntegralGoodsDetail = (id: Long, data: any) => {
  return api.get(`/integral/api/get/${id}`, data)
}

// 获取积分明细
export const getIntegralDetail = (data: any) => {
  return api.get(`/integral/detail/get`, data)
}

// 获取积分规则
export const getIntegralRule = (data: any) => {
  return api.get(`/integral/rule/get`, data, { cache: true })
}

// 获取积分抵扣规则
export const getLessRule = (data: any) => {
  return api.get(`/order/manage/setting`, data)
}

// 获取用户积分值信息
export const getUserIntegral = (data: any) => {
  return api.post(`/integral/detail/get/userInfo`, data)
}

// 商品分享获得积分
export const shareGetIntegral = (data: any) => {
  return api.post(`/integral/detail/save/share`, data)
}

// 提交积分商品订单
export const getPrizeOrder = (data: any) => {
  return api.post(`/integral/manager/integralOrder/save`, data)
}

// 用户收货地址
export const getAllAddress = (data: any) => {
  return api.get(`/integral/manager/integralOrder/get/address`, data)
}

// 获取用户邮费
export const getLogistFee = (data: any) => {
  return api.get(`/integral/manager/integralOrder/get/FreightAmount`, data)
}

// 用户支付
export const payOrder = (prizeOrderId: any, data: any) => {
  return api.put(`/integral/manager/integralOrder/payIntegralOrder/${prizeOrderId}`, data)
}

// 支付返回后端
export const payOrderCallBack = (data: any) => {
  return api.get(`/integral/manager/integralOrder/done`, data)
}

// 获取商家设置签到天数
export const signSetDay = (data: any) => {
  return api.get(`/integral/singIn/get/singInTotalDays`, data)
}

// 获取用户签到信息
export const userSignMsg = (data: any) => {
  return api.get(`/integral/singIn/getUserInfo`, data)
}

// 生产用户签到规则
export const setSignRule = (data: any) => {
  return api.post(`/integral/singIn/default`, data)
}

// 点击签到
export const todaySign = (data: any) => {
  return api.post(`/integral/singIn/updateStatus`, data)
}

// 获取用户步数
export const userWalk = (data: any) => {
  return api.post(`/integral/stepNumber/decrypt`, data)
}

// 获取步数兑换规则
export const stepChangeRule = (data: any) => {
  return api.get(`/integral/stepNumber/conversionInfo`, data)
}

// 进行步数兑换
export const changeSteps = (data: any) => {
  return api.get(`/integral/stepNumber/conversion`, data)
}

// 用户登录获取
export const loginGetInt = (data: any) => {
  return api.post(`/integral/enter/updateStatus`, data)
}
