import api from '@/libs/request'

/**
 * 获取对账单
 */
export function getFinTrade(data: Obj) {
  return api.get('/finance/manage/trade', data)
}

/**
 * 获取对账单流水
 */
export function getWaterRecode(data: Obj) {
  return api.get('/finance/manage/trade/detail', data)
}
/*
 * 获取提现列表
 * auditStatus 0->合伙人待审核；1->总店待审核(审核中)；2->已通过；3->已拒绝
 */
export function getWithdrawalList(data: Obj) {
  return api.get('/finance/manage/withdrawal', data)
}

/**
 * 获取单条提现详情
 */
export function getWithdrawalDetail(data: Obj) {
  return api.get(`/finance/manage/withdrawal/info/${data.id}`, data)
}

/**
 * 通过提现
 */
export function pass(data: Obj) {
  return api.put('/finance/manage/withdrawal/pass', null, { params: data })
}

/**
 * 拒绝提现
 */
export function reject(data: Obj) {
  return api.put('/finance/manage/withdrawal/reject', null, { params: data })
}

/**
 * 批量备注
 */
export function bulkNote(ids: any, note: any, over: any) {
  return api.put('/finance/manage/withdrawal/note', ids, {
    params: { note, over },
  })
}

/**
 * 获取激励
 */
export function getIncentiveOrder(data: Obj) {
  return api.get('order/manage/incentive/search', data)
}
