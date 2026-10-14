import api from '@/utils/request'

// 获取会员卡信息
export const getMemberCardInfo = (data: any) => {
  return api.get('/account/api/getMemberCardInfo', { ...data })
}

// 获取注册表单
export const getRegisterForm = (data: any) => {
  return api.get('/account/api/getRegisterForm', { ...data })
}

// 确认付款
export const confirmPayment = (data: any) => {
  return api.post('/account/api/confirmPayment', { ...data })
}

// 发送验证码
export const sendCode = (data: any) => {
  return api.get('/account/api/sendCode', { ...data })
}

// 表单提交
export const updateRegisterData = (data: any) => {
  return api.post('/account/api/updateRegisterData', { ...data })
}

// 会员中心数据
export const memberCenter = (data: any) => {
  return api.get('/account/api/memberCenter', { ...data })
}

// 会员卡记录
export const getMemberCardRecord = (data: any) => {
  return api.get('/account/api/getMemberCardRecord', { ...data })
}

// 充值规则
export const getMemberPayRule = (data: any) => {
  return api.get('/account/api/getMemberPayRule', { ...data })
}

// 余额重置
export const memberPay = (data: any) => {
  return api.post('/account/api/memberPay', { ...data })
}
// 余额重置(非会员)
export const userPay = (data: any) => {
  return api.post('/account/api/userPay', { ...data })
}

// 会员续费
export const updateMemberExpireTime = (data: any) => {
  return api.post('/account/api/updateMemberExpireTime', { ...data })
}

// 会员升级
export const upgradeMemberRank = (data: any) => {
  return api.post('/account/api/upgradeMemberRank', { ...data })
}

// 会员升级界面信息
export const getMemberCardUpgrade = (data: any) => {
  return api.get('/account/api/getMemberCardUpgrade', { ...data })
}

// 余额明细
export const getMemberBalanceRecord = (data: any) => {
  return api.get('/account/api/getMemberBalanceRecord', { ...data })
}

// 删除余额明细
export const deleteMemberBalanceRecord = (id: any) => {
  return api.delete(`/account/api/memberBalanceRecord/${id}`, {})
}
