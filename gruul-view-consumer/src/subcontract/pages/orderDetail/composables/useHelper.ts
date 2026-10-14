export const useHelper = () => {
  /** 序列化地址 */
  const getAddress = (orderDelivery: any) => {
    orderDelivery = orderDelivery || {}
    return orderDelivery.receiverProvince + orderDelivery.receiverCity + orderDelivery.receiverRegion + orderDelivery.receiverDetailAddress
  }

  /** 能更改地址的状态 */
  const canUpdateAddress = (status: string) => {
    // status === "WAIT_FOR_PAY" ||
    return status === 'WAIT_FOR_SEND'
  }

  /** 状态icon */
  const getStatusIcon = (status: string) => {
    return {
      // 待付款
      WAIT_FOR_PAY: 'icondingdanxiangqing-daifukuan',
      // 待发货
      WAIT_FOR_SEND: 'icondingdanxiangqing-daifahuo',
      // 待收货
      WAIT_FOR_PICKUP: 'icondingdanxiangqing-daitihuo',
      // 已完成
      COMPLETE: 'icondingdanxiangqing-yiwancheng',
      // 待评价
      WAIT_FOR_COMMENT: 'icondingdanxiangqing-yiwancheng',
      // 已关闭
      BUYER_PAY_TIMEOUT_CLOSE: 'icondingdanxiangqing-yiguanbi',
      BUYER_CANCEL_CLOSE: 'icondingdanxiangqing-yiguanbi',
      SELLER_CANCEL_CLOSE: 'icondingdanxiangqing-yiguanbi',
      EXCHANGE_SUCCESS_CLOSE: 'icondingdanxiangqing-yiguanbi',
    }[status]
  }
  return {
    getAddress,
    canUpdateAddress,
    getStatusIcon,
  }
}
