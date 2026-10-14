import { LOGIN_OPEN } from './emitNameConfig'
import { doPostLogout } from '@/api/modules/sign'

export default (errRes: { code: number; msg: string }) => {
  switch (errRes.code) {
    case 300007:
      return errRes.msg
      break
    case 3:
      // uni.clearStorageSync();
      // 延迟打开登陆弹窗
      uni.$emit(LOGIN_OPEN, true)
      return errRes.msg
      // uni.reLaunch({ url: "/pages/index/index" });
      break
    case 4:
      // token 过期
      uni.$emit(LOGIN_OPEN, true)
      return errRes.msg
      break
    case 7:
      uni.showToast({
        title: '账号异地登录',
        icon: 'none',
        duration: 1500,
        success: () => {
          doPostLogout()
          uni.reLaunch({
            url: '/pages/index/index',
          })
        },
      })

      break
    default:
      return errRes.msg
  }
}
