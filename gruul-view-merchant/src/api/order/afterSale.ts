import api from '@/libs/request'

/**
 * 获取商品列表
 */
export function getAfterList(data: Obj) {
  return api.get('/afs/manage/list', data)
}

/** 获取售后详情 */
export function getAfterDetail(afsId: string) {
  return api.get('/afs/manage/info', { afsId })
}

/**
 * 获取协商历史
 * @param orderId
 */
export function getNegotiateList(orderId: string) {
  return api.get('/afs/negotiate/history', { orderId })
}

/**
 * 备注
 */
export function note(data: Obj) {
  return api.post('/afs/manage/note', data)
}

/**
 * 同意申请
 * @param afsId
 */
export function agree(afsId: any) {
  return api.put('/afs/manage/seller/approve', null, { params: { afsId } })
}

/**
 * 拒绝申请
 * @param data { afsId: string, refusalReason: string }
 */
export function disagree(data: Obj) {
  return api.post('/afs/manage/seller/refusal', data, { params: data })
}
