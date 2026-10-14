import { payment, receipt, cancel } from '@/api'
import { requestPayment } from '@/utils/requestPayment'

interface OrderPayload {
  id: Long
  type?: string
  disabled?: boolean
}

interface WxPayResult {
  timeStamp: string
  nonceStr: string
  packageValue: string
  signType: string
  paySign: string
  wxResult?: any
  mwebUrl?: string
}

interface AfsType {
  type: string
  status: string
}

type EmitFn = (event: string) => void

export function useOrder(emit: EmitFn | undefined) {
  /**
   * 付款
   * @param {string} id 订单id
   */
  function pay({ id, type }: OrderPayload, callback: (() => void) | null = null) {
    type = type || 'BALANCE'
    payment(String(id), type === 'BALANCE')
      .then((res: WxPayResult) => {
        if (type === 'BALANCE') {
          uni.showToast({
            icon: 'none',
            title: '支付成功',
          })
          if (callback) {
            callback()
          }
          return
        }
        console.log(res)
        res = res.wxResult
        // #ifdef H5
        if (res.mwebUrl) {
          window.location.href = res.mwebUrl
        }
        // #endif
        // #ifdef MP-WEIXIN
        requestPayment(
          res,
          () => {
            if (callback) {
              callback()
            }
            uni.showToast({
              icon: 'none',
              title: '支付成功',
            })
          },
          (e: { errMsg: string }) => {
            console.log(e)
            const isCancel = (msg: string) => msg.includes('cancel')
            if (!isCancel(e.errMsg)) {
              uni.showToast({
                icon: 'none',
                title: '支付失败',
              })
            }
          },
        )
        // #endif
      })
      .catch((err) => {
        uni.showToast({
          icon: 'none',
          title: `${err || '支付失败'}`,
        })
      })
  }

  /**
   * 取消订单
   * @param {string} id 订单id
   */
  function cancelOrder({ id }: OrderPayload, callback: (() => void) | null = null) {
    uni.showModal({
      title: '温馨提示',
      content: '好不容易选好，确认要取消吗？',
      success: (res) => {
        if (res.confirm) {
          cancel(String(id))
            .then(() => {
              uni.showToast({
                icon: 'none',
                title: '取消成功',
              })
              if (callback) {
                callback()
              }
            })
            .catch((err) => {
              uni.showToast({
                icon: 'none',
                title: err || '操作失败',
              })
            })
        }
      },
    })
  }

  /**
   * 确认收货
   * @param {string} id 订单id
   */
  function receiptOrder({ id, disabled }: OrderPayload, callback: (() => void) | null = null) {
    if (disabled) return
    uni.showModal({
      title: '',
      content: '确认收货该订单？',
      success: (res) => {
        if (res.confirm) {
          receipt(String(id))
            .then(() => {
              if (callback) {
                callback()
              }
              uni.showToast({
                icon: 'none',
                title: '收货成功',
                duration: 1000,
                complete: function () {
                  uni.navigateTo({
                    url: `/subcontract/pages/orderEvaluation/orderEvaluation?id=${id}`,
                  })
                },
              })
            })
            .catch((err) => {
              uni.showToast({
                icon: 'none',
                title: `${err || '收货失败'}`,
              })
            })
        } else {
          uni.showToast({
            icon: 'none',
            title: '收货失败',
          })
        }
      },
    })
  }

  /** 前往评价 */
  function goReviews({
    target: {
      dataset: { id },
    },
  }: {
    target: {
      dataset: { id: Long }
    }
  }) {
    uni.navigateTo({
      url: `/subcontract/pages/orderEvaluation/orderEvaluation?id=${id}`,
    })
  }

  /**
   * 操作按钮是否不可用
   * @param afs
   */
  function receiptDisabled(afs: AfsType) {
    // 换货状态
    if (
      ['POINT_EXCHANGE', 'EXCHANGE'].indexOf(afs.type) !== -1 &&
      ['WAIT_FOR_SEND', 'WAIT_FOR_PICKUP', 'WAIT_RECEIPT', 'WAIT_FOR_BUSINESS_RECEIPT'].indexOf(afs.status) !== -1
    ) {
      return true
    }
    return false
  }

  function getHasAfsSendOrder(status: string) {
    return ['WAIT_FOR_SEND'].indexOf(status) !== -1
  }

  /** composable内成功回调 */
  function callbackAction() {
    if (emit) {
      emit('call')
    }
  }

  return {
    pay,
    cancelOrder,
    receiptOrder,
    goReviews,
    receiptDisabled,
    getHasAfsSendOrder,
    callbackAction,
  }
}
