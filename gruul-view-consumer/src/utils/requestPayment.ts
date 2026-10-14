import { cloneDeep } from 'lodash-es'

/**
 * 请求微信支付
 * @param orderInfo 订单信息
 * @param success 成功回调
 * @param fail 失败回调
 */
export const requestPayment = (orderInfo: any, success: () => void, fail: (e: any) => void) => {
  if (!orderInfo) {
    fail('订单信息为空')
    return
  }
  // 处理下packageValue 和 package
  orderInfo.package = orderInfo.package ?? orderInfo.packageValue
  orderInfo.packageValue = void 0
  // 处理下appId
  orderInfo.appid = orderInfo.appid ?? orderInfo.appId
  orderInfo.appId = void 0
  // 处理下timeStamp
  orderInfo.timeStamp = String(orderInfo.timeStamp)
  // 深拷贝订单信息
  const orderInfoClone = cloneDeep(orderInfo)
  // 得到全部小写的订单信息
  const orderInfoLower = Object.fromEntries(Object.entries(orderInfoClone).map(([key, value]) => [key.toLowerCase(), value]))
  uni.requestPayment({ provider: 'wxpay', orderInfo: orderInfoLower, ...orderInfo, success, fail })
}
