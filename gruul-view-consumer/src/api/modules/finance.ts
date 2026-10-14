import api from '@/utils/request'

/**
 * 申请提现的列表
 * @param form
 */
export const getWithdrawalList = (form: {
  // 0->团长;1->区域团长;2->提货点
  accountType: string
  current: number
  size: number
  keyword?: any
  // 对应用户id
  userId: string
  // 0->全部记录；1->到账记录
  type: string
}) => {
  return api.get('/finance/mini/withdrawal', form)
}
