import { useUserStore } from '@/store/modules/user'
import { Platform } from './bxios'

export const uploadFile = (filePath: any, formData?: any) => {
  const isDev = import.meta.env.VITE_ENV_MODE === 'dev'
  let isH5 = false
  let platform = Platform.H5
  // #ifdef H5
  isH5 = true
  platform = Platform.H5
  // #endif
  // #ifdef MP-WEIXIN
  platform = Platform.MINI_APP
  // #endif
  return new Promise((resolve, reject) => {
    uni.uploadFile({
      url: isDev && isH5 ? '/oss/upload' : import.meta.env.VITE_APP_UPLOAD_URL,
      filePath,
      name: 'file',
      header: {
        'Shop-Id': import.meta.env.VITE_APP_SHOP_ID,
        version: import.meta.env.VITE_APP_APP_REQUEST_VERSION,
        Platform: platform,
        'Client-Type': 'CONSUMER',
        authorization: useUserStore().userInfo.token,
      },
      formData,
      success: ({ data }) => {
        resolve(JSON.parse(data).data)
      },
      fail: (err) => {
        reject(err)
      },
    })
  })
}
