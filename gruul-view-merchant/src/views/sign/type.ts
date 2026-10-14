export enum LoginType {
  MOBILE_PASSWORD = 'mobile_password', // 手机号密码登录
  SMS_CODE = 'sms_code', // 短信验证码登录
  WECHAT_OPEN_SCAN = 'wechat_open_scan', // 开放平台扫码登录
  SWITCH_SHOP = 'switch_shop',
  REFRESH_TOKEN = 'refresh_token',
}

export enum RedirectType {
  LOGIN = 'LOGIN',
  BINDING_WX = 'BINDING_WX',
}
