export interface User {
  nikeName: string
  userId: string
}

interface CouponProduct {
  couponId: number
  id: number
  productId: number
}

export interface IGiveCoupon {
  /** 满足条件金额 */
  conditionAmount: number
  /** 优惠劵关联商品信息 */
  couponProducts: CouponProduct[]
  /** 优惠劵类型： 0-无门槛折扣劵 1-无门槛现金劵 2-满折劵 3-满减劵 */
  couponType: number
  /** 优惠折扣（不能大于10） */
  discount: number
  /** 优惠金额 */
  discountAmount: number
  /** 有效天数 */
  effectiveDays: number
  /** 有效类型： 0-固定时间 1-领劵立即生效 */
  effectiveType: number
  /** 结束时间 */
  endTime: string
  /** 赠送类型： 0-全民活动 1-赠予客户 2-积分优惠劵 */
  giveType: number
  id: string
  /** 所需积分 */
  integral: number
  /** 发行数量 */
  issueQuantity: number
  /** 限领(张/人) */
  limitQuantity: number
  /** 优惠劵名称 */
  name: string
  /** 适用商品类型： 0-全部商品参与 1-指定商品参与 2-指定商品不参与 */
  productType: number
  /** 开始时间 */
  startTime: string
  /** 满足条件金额 */
  status: number
  [key: string]: any
}

export class Coupon implements NullCheck<IGiveCoupon> {
  conditionAmount = null

  couponProducts = []

  couponType = null

  discount = null

  discountAmount = null

  effectiveDays: number | null = null

  effectiveType: number | null = null

  endTime = ''

  giveType = null

  id = null

  integral = null

  issueQuantity = null

  limitQuantity = null

  name = ''

  productType = null

  startTime = ''

  status = null
}
