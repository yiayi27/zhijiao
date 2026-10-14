export const useAfterSale = () => {
  /** 申请售后选项 */
  const afterActions = (type: string, status?: string, isLogistics?: boolean) => {
    let user = [
      {
        name: '换货',
        id: 'EXCHANGE',
      },
      {
        name: '退款',
        id: 'REFUND',
      },
      {
        name: '退货退款',
        id: 'RETURN_REFUND',
      },
    ]

    if (isLogistics) {
      user.splice(0, 1)
    }
    /** 如果类型为用户，并且订单为已付款只有一种状态 */
    if ((type === 'user' && status === 'WAIT_FOR_SEND') || !status) {
      user = [
        {
          name: '退款',
          id: 'REFUND',
        },
      ]
    }
    /** 如果类型为用户，并且订单为已付款只有一种状态 */
    if (type === 'user' && status === 'WAIT_FOR_PICKUP') {
      user.splice(2, 1)
    }

    return {
      user,
    }[type || 'user']
  }

  /** 申请类型名称 */
  const getAfterTypeName = (type: string) => {
    return (
      {
        REFUND: '退款',
        EXCHANGE: '重新发货',
        RETURN_REFUND: '退货退款',
        // RETURN_EXCHANGE: "换货",
      }[type] || ''
    )
  }

  /** 申请状态名称 */
  const getAfterStatusName = (type: string) => {
    return (
      {
        WAIT_FOR_BUSINESS_APPROVED: '待商家审核',
        // 待用户退货
        WAIT_FOR_RETURN: '待退货',
        WAIT_FOR_BUSINESS_RECEIPT: '等待商家确认收货',
        WAIT_FOR_SEND: '待发货',
        WAIT_FOR_PICKUP: '待收货',
        SHIPPED: '配送中',
        WAIT_RECEIPT: '待签收',
        SUCCESS: '成功',
        CLOSE: '关闭',
      }[type] || ''
    )
  }

  const userApplyVisible = (afs: any) => {
    return !afs || !afs.status
  }

  /**
   * 申请售后按钮是否显示
   * @param status 订单状态
   */
  const userApplyBtnVisible = (status: string, afs: any) => {
    if (afs) {
      if (['POINT_EXCHANGE', 'POINT_REFUND'].indexOf(afs.type) !== -1 && afs.status !== 'CLOSE') return false
      if (['SUCCESS', 'BUYER_CANCEL_CLOSE'].indexOf(afs.status) !== -1) return true
    }

    return !(
      ['SHIPPED', 'COMPLETE', 'WAIT_FOR_PAY', 'BUYER_PAY_TIMEOUT_CLOSE', 'BUYER_CANCEL_CLOSE', 'REFUND', 'POINT_REFUND'].indexOf(status) !== -1
    )
  }

  const applyNumNotExceeded = (afs: any, applyNum: number) => {
    if (applyNum < 3) return true

    if (applyNum === 3 && afs) {
      if (['SUCCESS', 'CLOSE'].indexOf(afs.status) !== -1) return false
      return true
    } else {
      return false
    }
  }

  /**
   * 确认收货按钮是否disabled
   * @param afs 售后对象
   */
  const receiptDisabled = (afs: any) => {
    if (['POINT_EXCHANGE', 'EXCHANGE'].indexOf(afs.type) !== -1 && ['WAIT_FOR_SEND', 'WAIT_FOR_PICKUP', 'WAIT_RECEIPT'].indexOf(afs.status) !== -1) {
      return true
    }

    return false
  }

  /** 类型是否为退款 */
  const isRefund = (status: string) => {
    return status === 'REFUND' || status === 'POINT_REFUND' || status === 'RETURN_REFUND'
  }

  /** 状态是否为撤销 */
  const isRevoke = (afs: any = {}) => {
    return afs.status === 'CLOSE' && afs.closeType === 'USER_CANCEL'
  }

  /** 状态是否为待退货 */
  const isWaitReturn = (type: string, status?: string) => {
    return (type === 'EXCHANGE' || type === 'RETURN_REFUND') && status === 'WAIT_FOR_POINT_RECEIPT'
  }

  /** 是否为待审核 */
  const isWaitApproved = (status: string) => {
    return status === 'WAIT_FOR_BUSINESS_APPROVED'
  }

  /** 时间是否显示 */
  const timeVisible = (status: string) => {
    return ['WAIT_FOR_BUSINESS_APPROVED', 'WAIT_FOR_RETURN', 'WAIT_FOR_POINT_RECEIPT', 'WAIT_FOR_BUSINESS_RECEIPT'].indexOf(status) !== -1
  }

  /** 获取关闭原因 */
  const getCloseType = (type: string) => {
    return {
      USER_CANCEL: '用户撤销',
      RE_EXCHANGE: '重新申请换货',
      SELLER_REFUSE: '卖家拒绝',
    }[type]
  }

  const orderItemStatusVisible = (status: string) => {
    return !(['SUCCESS', 'CLOSE'].indexOf(status) !== -1)
  }

  return {
    applyNumNotExceeded,
    isRevoke,
    receiptDisabled,
    userApplyBtnVisible,
    timeVisible,
    orderItemStatusVisible,
    getCloseType,
    isWaitApproved,
    isWaitReturn,
    isRefund,
    userApplyVisible,
    getAfterStatusName,
    getAfterTypeName,
    afterActions,
  }
}
