export const useOrder = () => {
  /** 待付款 */
  const isPendingPayment = (status: string) => {
    return status === 'WAIT_FOR_PAY'
  }

  /** 待发货状态 */
  const isWaitDelivery = (status: string) => {
    return status === 'WAIT_FOR_SEND'
  }

  /** 待提货状态 */
  const isWaitTake = (status: string) => {
    return status === 'WAIT_FOR_PICKUP'
  }

  /** 待评价状态 */
  const isWaitComment = (status: string) => {
    return status === 'WAIT_FOR_COMMENT'
  }

  /** 已关闭状态 */
  const isClose = (status: string) => {
    const closeStatus = ['BUYER_PAY_TIMEOUT_CLOSE', 'BUYER_CANCEL_CLOSE', 'SELLER_CANCEL_CLOSE', 'EXCHANGE_SUCCESS_CLOSE']
    return closeStatus.indexOf(status) !== -1
  }

  /** 支付类型 */
  const getPayTypeName = (key: string) => {
    return {
      UNPAID: '未支付',
      BALANCE: '余额支付',
      WECHAT: '微信支付',
      FRIEND: '好友代付',
      FREE: '无需支付',
    }[key]
  }

  /** 订单状态 */
  const getOrderStatusName = (key: string) => {
    return {
      WAIT_FOR_PAY: '待买家付款',
      WAIT_FOR_SEND: '买家已付款',
      WAIT_FOR_PICKUP: '待收货',
      WAIT_FOR_COMMENT: '交易成功',
      COMPLETE: '交易成功',
      REFUNDED: '交易关闭',
      PART_REFUNDED: '部分退款',
      BUYER_PAY_TIMEOUT_CLOSE: '交易关闭',
      BUYER_CANCEL_CLOSE: '交易关闭',
      SELLER_CANCEL_CLOSE: '交易关闭',
      EXCHANGE_SUCCESS_CLOSE: '交易关闭',
    }[key]
  }

  /** 订单状态 */
  const getOrderStatusTips = (key: string) => {
    return {
      WAIT_FOR_PAY: '待买家付款',
      WAIT_FOR_SEND: '买家已付款',
      WAIT_FOR_PICKUP: '待收货',
      WAIT_FOR_COMMENT: '交易成功',
      COMPLETE: '交易成功',
      REFUNDED: '交易关闭',
      PART_REFUNDED: '部分退款',
      BUYER_PAY_TIMEOUT_CLOSE: '因支付超时交易关闭',
      BUYER_CANCEL_CLOSE: '因买家取消订单交易关闭',
      SELLER_CANCEL_CLOSE: '因卖家取消订单交易关闭',
      EXCHANGE_SUCCESS_CLOSE: '因换货成功交易关闭',
    }[key]
  }

  /** 是否为排除不显示的状态 */
  const isExcludeStatus = (key: string) => {
    // "WAIT_FOR_SEND",
    return ['WAIT_FOR_COMMENT'].indexOf(key) !== -1
  }

  const excludeActive = (activeArr: number[], active: Long) => {
    return activeArr.indexOf(+active) !== -1
  }

  /** 是否订单所有商品正在售后 */
  const isafs = (itemVoList: any[]) => {
    const noafsList = itemVoList.filter((item) => {
      return !item.afs || item.afs.status === 'CLOSE'
    })
    return noafsList.length > 0
  }

  // /** 配送类型 */
  const getDeliveryTypeName = (key: string) => {
    return {
      LOGISTICS: '物流配送',
    }[key]
  }

  const getOrderTypeName = (key: string) => {
    return {
      EXCHANGE: '换货单',
    }[key]
  }
  return {
    getOrderTypeName,
    isPendingPayment,
    getOrderStatusName,
    isWaitDelivery,
    isWaitTake,
    isClose,
    getDeliveryTypeName,
    isWaitComment,
    getPayTypeName,
    isExcludeStatus,
    excludeActive,
    getOrderStatusTips,
    isafs,
  }
}
