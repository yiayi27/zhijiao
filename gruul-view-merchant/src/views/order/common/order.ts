import { fromPairs } from 'lodash-es'

/** 订单状态 */
export function getOrderStatusName(key: string) {
  return {
    WAIT_FOR_PAY: '待付款',
    WAIT_FOR_SEND: '待发货',
    WAIT_FOR_COMMENT: '交易完成',
    WAIT_FOR_PICKUP: '等待客户提货',
    COMPLETE: ' 已完成',
    REFUNDED: '已退款',
    PART_REFUNDED: '已完成',
    BUYER_PAY_TIMEOUT_CLOSE: '超时关闭',
    BUYER_CANCEL_CLOSE: '客户关闭',
    SELLER_CANCEL_CLOSE: '卖家关闭',
    EXCHANGE_SUCCESS_CLOSE: '换货成功',
    /**
     * 换货关闭
     */
    EXCHANGE_CANCEL_CLOSE: '关闭',
  }[key]
}

/** 订单状态 */
export function getDeliverOrderStatusName(key: string) {
  return {
    WAIT_FOR_PAY: '待付款',
    WAIT_FOR_SEND: '待发货',
    WAIT_FOR_PICKUP: '待收货',
    WAIT_FOR_COMMENT: '交易完成',
    COMPLETE: ' 已完成',
    REFUNDED: '已退款',
    PART_REFUNDED: '已完成',
    BUYER_PAY_TIMEOUT_CLOSE: '超时关闭',
    BUYER_CANCEL_CLOSE: '客户关闭',
    SELLER_CANCEL_CLOSE: '卖家关闭',
    EXCHANGE_SUCCESS_CLOSE: '换货成功',
    /**
     * 换货关闭
     */
    EXCHANGE_CANCEL_CLOSE: '关闭',
  }[key]
}

/** 是否为关闭状态 */
export function isClose(v: string) {
  return ['BUYER_PAY_TIMEOUT_CLOSE', 'BUYER_CANCEL_CLOSE', 'SELLER_CANCEL_CLOSE', 'EXCHANGE_SUCCESS_CLOSE'].includes(v)
}

/** 配送类型 */
export function getDeliveryTypeName(key: string) {
  return {
    LOGISTICS: '物流配送',
  }[key]
}

/** 过滤空 */
export function filterEmpty<T extends Record<string, any>>(data: T): Partial<T> {
  return fromPairs(
    Object.entries(data).filter(([, value]) => {
      return !!value
    }),
  ) as Partial<T>
}

/** 获取售后单名称 */
export function getAfterNameExport(type: string) {
  return (
    {
      REFUND: '退款',
      POINT_REFUND: '退款',
      POINT_EXCHANGE: '补货',
      EXCHANGE: '重新发货',
      RETURN_REFUND: '退货退款',
      RETURN_EXCHANGE: '退货换货',
    }[type] || ''
  )
}

/** 获取售后单状态名称 */
export function getAfterStatusNameExport(status: string) {
  return (
    {
      WAIT_FOR_BUSINESS_APPROVED: '待商家审核',
      WAIT_FOR_CUSTOMER_DELIVERY: '待买家退货',
      WAIT_FOR_BUSINESS_RECEIPT: '待商家收货',
      WAIT_FOR_BUSINESS_DELIVERY: '待商家发货',
      WAIT_FOR_CUSTOMER_RECEIPT: '待买家收货',
      WAIT_FOR_REFUND: '待退款',
      SUCCESS: '成功',
      BUSINESS_REFUSE: '商家拒绝',
      CUSTOMER_CANCEL: '买家撤销',
    }[status] || ''
  )
}

/** 物流订单查询条件 */
export const deliveryOrderQuery = {
  current: 1,
  deliverType: '102',
  goodsName: '',
  groupLeaderName: '',
  keyword: '',
  lineId: '',
  userName: '',
  orderId: '',
  // -1：所有订单, 0.待付款（待买家付款）, 1.待发货（买家已付款）, 2.已发货（卖家已发货）,  4.已完成（用户已经签收）, 5.已关闭
  orderStatus: '-1',
  payTime: '',
  // 近一个月->0; 近三个月->1; 全部->2;
  quicklyDate: '0',
  receiverName: '',
  size: 20,
  deliverySn: '',
  remarkType: '0',
  area: '',
  total: 0,
}

/**
 * 订单评价查询条件
 */
export const evaluationQuery = {
  current: 1,
  goodsName: '',
  orderId: '',
  payTime: '',
  rate: '',
  total: 0,
  size: 20,
}

export function strFilter(str: string) {
  if (str.length <= 10) return str
  return `${str.substring(0, 10)}...`
}
