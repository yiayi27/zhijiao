import api from '@/libs/request'

/**
 * 获取会员列表
 */
export function getMemberList(data: Obj) {
  return api.get(`/account/manager/memberList`, data)
}

/**
 * 获取会员等级
 */
export function getMemberCardRankList(data: Obj = {}) {
  return api.get(`/account/manager/use/memberCardRank`, data)
}

/**
 * 获取会员等级
 */
export function getMemberCardRank(data: Obj) {
  return api.get(`/account/manager/use/memberCardRank`, data)
}

/**
 * 设为会员提交
 */
export function setMember(data: Obj) {
  return api.post(`/account/manager/setMember`, data)
}

/**
 * 会员续费
 */
export function expireTime(data: Obj) {
  return api.post(`/account/manager/updateMemberExpireTime`, data)
}

/**
 * 会员升级
 */
export function updateRank(data: Obj) {
  return api.post(`/account/manager/upgradeMemberRank`, data)
}

/**
 * 发送验证码
 */
export function sendCode(data: Obj) {
  return api.get(`/account/manager/sendCode`, data)
}

/**
 * 获取会员卡信息
 */
export function getMemberCard(data: Obj) {
  return api.get(`/account/manager/getMemberCardInfo`, data)
}

/**
 * 会员卡保存
 */
export function saveMemberCard(data: Obj) {
  return api.post(`/account/manager/saveMemberCardInfo`, data, {
    headers: {
      'Content-Type': 'application/json',
    },
  })
}

/**
 * 会员卡状态
 */
export function updateMemberCardInfoStatus(data: Obj) {
  return api.post(`/account/manager/updateMemberCardInfoStatus`, data, {
    headers: {
      'Content-Type': 'application/json',
    },
  })
}

/**
 * 会员功能开关
 */
export function setMemberCardInfoStatus(data: Obj) {
  return api.post(`/account/manager/setMemberCardInfoStatus`, data)
}

/**
 * 获取会员权益
 */
export function getMemberCardPrivilege(data: Obj) {
  return api.get(`/account/manager/getMemberCardPrivilege`, data)
}

/**
 * 关闭开启会员卡权益
 */
export function updateMemberCardPrivilegeStatus(data: Obj) {
  return api.post(`/account/manager/updateMemberCardPrivilegeStatus`, data)
}

/**
 * 添加自定义权益
 */
export function insertMemberCardPrivilege(data: Obj) {
  return api.post(`/account/manager/insertMemberCardPrivilege`, data)
}

/**
 * 添加自定义权益
 */
export function deleteMemberCardPrivilege(data: Obj) {
  return api.post(`/account/manager/deleteMemberCardPrivilege`, data)
}

/**
 * 更新权益
 */
export function updateMemberCardPrivilege(data: Obj) {
  return api.post(`/account/manager/updateMemberCardPrivilege`, data)
}

/**
 * 储值订单
 */
export function getMemberOrderInfo(data: Obj) {
  return api.get(`/account/manager/getMemberOrderInfo`, data)
}

/**
 * 备注
 */
export function remarks(data: Obj) {
  return api.post(`/account/manager/note`, data)
}

/**
 * 保存储值规则
 */
export function saveMemberPayRule(data: Obj) {
  return api.post(`/account/manager/saveMemberPayRule`, data)
}

/**
 * 获取储值规则
 */
export function getMemberPayRule(data: Obj) {
  return api.get(`/account/manager/getMemberPayRule`, data)
}

/**
 * 获取会员功能状态
 */
export function getMemberCardInfoSwitch(data: Obj = {}) {
  return api.get(`/account/manager/getMemberCardInfoSwitch`, data)
}

/**
 * 获取会员更新
 */
export function getMemberCardUpgrade(data: Obj) {
  return api.get(`/account/manager/getMemberCardUpgrade`, data)
}

/**
 * 获取余额明细
 */
export function getBalanceDetail(data: Obj) {
  return api.get(`/account/api/getMemberBalanceRecord`, data)
}

/**
 * 获取会员卡记录
 */
export function getMemberRecord(data: Obj) {
  return api.get(`/account//manager/getMemberRecord`, data)
}
