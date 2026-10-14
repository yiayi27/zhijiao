export const useHelper = () => {
  const getChannel = (key: string) => {
    return {
      WECAHT: '微信',
      ALIPAY: '支付宝',
      BANK: '银行卡',
      OFFLINE: '线下结算',
    }[key]
  }

  const getAuditStatusName = (key: string) => {
    return {
      PENDING_PARTNER: '待审核',
      PENDING_MAIN: '待审核',
      PASSED: '已成功',
      REJECTED: '已拒绝',
    }[key]
  }

  const getAuditStatusColor = (key: string) => {
    return {
      PENDING_PARTNER: 'processing',
      PENDING_MAIN: 'processing',
      PASSED: 'success',
      REJECTED: 'fail',
    }[key]
  }
  return {
    getChannel,
    getAuditStatusName,
    getAuditStatusColor,
  }
}
