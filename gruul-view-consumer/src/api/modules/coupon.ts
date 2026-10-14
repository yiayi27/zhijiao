import api from '@/utils/request'

// 查看未领取优惠券
export const getCouponIndex = (data: any) => {
  return api.get('/discount/api/coupon/list', data, { cache: true })
}

// 获取首页优惠券组件数据
export const getCouponCoup = (data: any) => {
  return api.get('/discount/api/coupon/component/list', data)
}

// 点击领取优惠券
export const getOneCoupon = (id: any, data: any) => {
  return api.post(`/discount/api/member/coupon/receiveCoupon/${id}`, data)
}

// 一键领取优惠券
export const getAllCoupon = (ids: any, data: any) => {
  return api.post(`/discount/api/member/coupon/receiveCoupon/some/${ids}`, data)
}

// 用户已领取优惠券
export const getUserCoupon = (data: any) => {
  return api.get(`/discount/api/member/coupon/getByUserId`, data)
}
