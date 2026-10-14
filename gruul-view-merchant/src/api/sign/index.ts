import api from '@/libs/request'

// 获取商家路由配置
export function getAsyncRouterMap() {
  return api.get('/shops/shop/menu/one', {})
}

// 更新商家路由配置
export function upDateAsyncRouterMap(data: Obj) {
  return api.put('/shops/shop/menu/update', data)
}

// 账号校验
export function checkoutAccount(data: Obj) {
  return api.get('/platform/account-info/checkout/account', data)
}

// 发送一次性验证,后端在common中定义校验类型,有效期统一5分钟,使用即失效,60秒可发送一次
export function getCode(data: Obj) {
  return api.post('/platform/send-code', data)
}

// 根据请求token获取当前用户最新的信息
export async function getAccountInfo() {
  return api.get('/platform/account-info/info', {})
}

// 手机号换绑
export function changePhone(data: Obj) {
  return api.put('/platform/account-info/phone/change/tie', data, {
    headers: { 'Content-Type': 'application/json' },
  })
}

// 修改密码
export function changePassword(data: Obj) {
  return api.put('/platform/account-info/pass/change/tie', data, {
    headers: { 'Content-Type': 'application/json' },
  })
}

// 获取指定系统配置
export function getSystemConfig() {
  return api.get(`/platform/system-conf/service/config`, {})
}

/**
 * 商户登录接口
 * @query: tenementLoginDto -->  (必须)
 */
export function unionAccountLogin(data: Obj, params: Obj) {
  return api.post('/platform/login', data, { params })
}

/**
 * 登出
 */
export function unionAccountLogout() {
  return api.post('/platform/logout')
}

/**
 * 扫码成功回调,根据code获取制定场景值
 * @query: code --> 微信扫码返回的codo
 */
export function verifyStateResult(data: Obj) {
  return api.get('/platform/account-info/verify/state/result', data)
}

// 忘记密码 重置密码接口(目前仅限商户账号修改,商户子账号无法修改)
export function forgetPassword(data: Obj) {
  return api.put('/platform/account-info/password-retrieve', data, {
    headers: { 'Content-Type': 'application/json' },
  })
}

// 验证码校验,校验成功返回凭证码
export function verifyTheCode(data: Obj) {
  return api.put('/platform/send-code/verify/code', data, {
    headers: { 'Content-Type': 'application/json' },
  })
}

// 验证码校验,校验成功返回凭证码
export function getOssConfig(data: Obj) {
  return api.get(`/platform/system-conf/current/oss/config`, data)
}

export function modifySystemConfig(data: string) {
  return api.post('/platform/system-conf', data, {
    headers: { 'Content-Type': 'application/json' },
  })
}

// 获取二维码用于开放平台
export function toPostAccountVerify(data: Obj) {
  return api.post('/platform/account-info/pre/account/verify', data)
}

// 开放平台登录是否开启
export function toGetAccountVerifyEnabled() {
  return api.get('/platform/account-info/pre/account/verify/enabled')
}

// 绑定开放平台扫码登录
export function toPostAccountVerifyBind(data: Obj) {
  return api.post('/platform/account-info/pre/account/verify/bind', data)
}

// 退出登录接口
export function toPostLogout() {
  return api.post('/platform/logout')
}
