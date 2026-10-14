import Bxios, { type Result, Platform } from './bxios'
import { LOGIN_OPEN } from './emitNameConfig'
import errorHandler from './errorHandler'
import { useUserStore } from '@/store/modules/user'
import storage from './storage'
import { GrantType } from '@/api/modules/sign/type'
import { doPostLogout } from '@/api/modules/sign'
import { useRequestStore } from '@/store/modules/request'

type Method = 'get' | 'post' | 'put' | 'delete'
const TOKEN_OVERDUE = [2, 3, 5, 6]
let requests: any[] = []

const assmbleUrl = ['/assemble/activity/ass/list/product_show_category']
const whiteList = ['/platform/login?grant_type=refresh_token']

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

const bxios = new Bxios({ baseUrl: isDev && isH5 ? '/api' : import.meta.env.VITE_APP_BASE_URL })

bxios.interceptors.request.use(
  (config) => {
    const { token } = useUserStore().userInfo
    Object.assign(config.header, {
      Authorization: config.needToken ? (token ?? '') : void 0,
      'Shop-Id': import.meta.env.VITE_APP_SHOP_ID,
      version: import.meta.env.VITE_APP_APP_REQUEST_VERSION,
      Platform: platform,
      'Client-Type': 'CONSUMER',
    })
    // 如果正在刷新中
    if (useRequestStore().isRefreshing && !whiteList.includes(config.url)) {
      return new Promise((resolve) => {
        requests.push((token: string) => {
          config.header.Authorization = token
          resolve(config)
        })
      })
    }
    return Promise.resolve(config)
  },
  (err) => {
    return Promise.reject(err)
  },
)

bxios.interceptors.response.use(
  async ({ data, config }: Result) => {
    if (config && assmbleUrl.find((i) => config.url.indexOf(i) !== -1)) {
      return { data }
    }
    if (data?.code === 200) {
      return data.data
    }

    if (data?.code === 4) {
      // 无感刷新
      if (!useRequestStore().isRefreshing) {
        useRequestStore().setRefreshState(true)
        const refresh_token = storage.get('getLoginData')?.refreshToken || ''
        if (!refresh_token) {
          useRequestStore().setRefreshState(false)
          doPostLogout()
          uni.$emit(LOGIN_OPEN, true)
          return Promise.reject(data)
        }
        try {
          const loginData = await useUserStore().login({ GrantType: GrantType.REFRESH_TOKEN, form: { value: refresh_token } })
          useRequestStore().setRefreshState(false)
          const newToken = loginData?.value as string
          if (!newToken) {
            doPostLogout()
            uni.$emit(LOGIN_OPEN, true)
            return Promise.reject(loginData)
          }
          requests.forEach((cb: (t: string) => any) => {
            cb(newToken)
          })
          requests = []
          config.header.Authorization = newToken
          // config.method转小写
          const method = config.method.toLowerCase() as Method
          return Promise.resolve(bxios[method](config.url, config.data, config))
        } catch (error) {
          console.log('error', error)
          useRequestStore().setRefreshState(false)
          doPostLogout()
          uni.$emit(LOGIN_OPEN, true)
          return Promise.reject(error)
        } finally {
          useRequestStore().setRefreshState(false)
        }
      }
      return new Promise((resolve) => {
        requests.push((token: string) => {
          config.header.Authorization = token
          // config.method转小写
          const method = config.method.toLowerCase() as Method
          resolve(bxios[method](config.url, config.data, config))
        })
      })
    }
    if (TOKEN_OVERDUE.includes(data?.code)) {
      doPostLogout()
      uni.$emit(LOGIN_OPEN, true)
      return Promise.reject(data)
    }
    return Promise.reject(errorHandler(data))
  },
  (err) => {
    return Promise.reject(err)
  },
)

export default bxios
