import type { AxiosError, AxiosRequestConfig, AxiosResponse } from 'axios'
import { unionAccountLogin } from '@/api/sign'
import { isDev, logout, modifySignRefresh, modifySignStatus } from '@/libs'
import Axios from '@/libs/axios'
import { ACCESS_TOKEN, REFRESH_TOKEN } from '@/libs/enum/storage'
import { storage } from '@/libs/storage'
import { useUserStoreWithOut } from '@/store/modules/user'
import { LoginType } from '@/views/sign/type'

// 重试队列
let requests: any = []

// 是否正在刷新的标记
let isRefreshing = false
// 常量集中管理
const EXCLUDE_URLS = new Set([
  '/shipping/get/deliverClass/list/export',
  '/shipping/get/deliver/list/export',
  '/shipping/get/deliverPoint/list/export',
  '/assemble/activity/ass/list/product_show_category',
  '/shipping/set/shipper/pointInfo',
])

const CONTENT_TYPES = {
  FORM_DATA: 'application/x-www-form-urlencoded',
  JSON: 'application/json',
} as const

const ApiService = createApiService()

// 创建axios实例
function createApiService() {
  const api = new Axios({
    baseURL: isDev ? '/api/' : import.meta.env.VITE_APP_BASEURL,
    headers: {
      'Content-Type': CONTENT_TYPES.JSON,
      Accept: CONTENT_TYPES.JSON,
      Platform: 'PC',
      'Client-Type': 'SHOP',
      'Shop-Id': '0',
    },
  })

  // 请求拦截器
  api.instance.interceptors.request.use(configureRequestHeaders)

  // 响应拦截器
  api.instance.interceptors.response.use(handleSuccessResponse, handleErrorResponse)

  return api
}

// 请求头配置函数
function configureRequestHeaders(config: AxiosRequestConfig) {
  const userStore = useUserStoreWithOut()
  const headers = {
    ...config.headers,
    version: import.meta.env.VITE_APP_APP_VERSION,
  }

  if (userStore.token || storage.get(ACCESS_TOKEN)) {
    headers.Authorization = userStore.token || storage.get(ACCESS_TOKEN)
  }

  if (headers.type === 'formData') {
    headers['Content-Type'] = CONTENT_TYPES.FORM_DATA
  }

  if (userStore.currentShopId) {
    headers['Shop-Id'] = userStore.currentShopId
  }

  return { ...config, headers }
}

// 成功响应处理
function handleSuccessResponse(response: AxiosResponse) {
  const { config, data, status } = response

  if (EXCLUDE_URLS.has(config.url as string)) {
    return data
  }

  // token失效
  if ([4].includes(data.code)) {
    if (!isRefreshing) {
      isRefreshing = true
      refreshingFn()
    }
    return new Promise((resole) => {
      requests.push((token: string) => {
        response.headers.Authorization = token
        resole(ApiService.instance(response.config))
      })
    })
  }

  if (data.code !== 200 || status !== 200) {
    const message = createBusinessError(data)
    ElMessage.error(message)
    if ([2, 3, 5, 6].includes(data.code)) {
      handleSessionExpiration()
    }
    return Promise.reject(message)
  }

  return data
}

// 错误响应处理
function handleErrorResponse(error: AxiosError) {
  if (error.response) {
    const { status, data } = error.response
    const errorMessage = data?.message || `HTTP Error ${status}`
    return Promise.reject(new Error(errorMessage))
  }
  return Promise.reject(error)
}

// 返回指定错误格式
function createBusinessError(data: { msg?: string; message?: string }): string {
  return data.msg || data.message || '未知错误，请稍后再试'
}

// 处理token过期
async function handleSessionExpiration() {
  logout()
  window.location.href = '/sign'
  // await router.replace('/sign')
}

async function refreshingFn() {
  const userStore = useUserStoreWithOut()
  const refresh_token = userStore.refreshToken || storage.get(REFRESH_TOKEN)
  try {
    const { data, code } = await unionAccountLogin({ value: refresh_token }, { grant_type: LoginType.REFRESH_TOKEN })
    // 刷新 token 不可用 直接重新登录
    if (code !== 200) {
      handleSessionExpiration()
      return
    }
    modifySignStatus(data.value)
    modifySignRefresh(data.refreshToken.value)
    requests.forEach((cd: (t: string) => any) => cd(data.value))
    requests = []
    isRefreshing = false
  } finally {
    isRefreshing = false
  }
}

export default ApiService
