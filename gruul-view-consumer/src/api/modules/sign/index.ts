import api from '@/utils/request'
import { encryptAccept } from '@/utils/rsa'
import { GrantType, GrantTypes, SmsType, type SignResp } from '@/api/modules/sign/type'
import type { CaptchaResponse } from '@/components/SlideCaptcha/Captcha'
import storage from '@/utils/storage'
import { useUserStore } from '@/store/modules/user'

/**
 * 获取滑块验证码
 */
export const doGetCaptchaSlider = () => {
  return api.get<CaptchaResponse>(`/platform/captcha/slider`)
}

/**
 * 获取验证码
 */
export const doPostSmsCode = (data: { phone: string; type: SmsType }) => {
  return api.post<string>(`/platform/send-code`, data)
}

/**
 * login
 * @param {GrantType} grantType 认证方式类型
 * @param data 认证资料
 */
export const doSignByUser = (grantType: GrantType, data: any) => {
  const grant = GrantTypes[grantType]
  //参数加密
  if (grant.encrypt) {
    encryptAccept((encrypt) => {
      grant.encryptFields?.forEach((field) => {
        data[field] = encrypt(data[field])
      })
    })
  }
  return api.post<SignResp>(`/platform/login?grant_type=${grant.value}`, data)
}

/**
 * 退出登录
 */
export const doPostLogout = () => {
  storage.removeThis()
  useUserStore().initUserInfo()
  useUserStore().setGoodsNumber(0)
  return api.post('/platform/logout')
}
