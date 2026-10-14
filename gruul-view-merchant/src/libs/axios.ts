import type { IBaseResponse } from '@/libs/types'
import type { AxiosInstance, AxiosRequestConfig } from 'axios'
import axios from 'axios'

class HttpRequest {
  public instance: AxiosInstance

  private options: AxiosRequestConfig

  constructor(options: AxiosRequestConfig) {
    this.options = options
    this.instance = axios.create(options)
  }

  get<T = any>(url: string, data: any = {}, config: AxiosRequestConfig = {}) {
    return this.request<T>({
      url,
      method: 'get',
      params: data,
      ...config,
    })
  }

  post<T = any>(url: string, data: any = {}, config: AxiosRequestConfig = {}) {
    return this.request<T>({
      url,
      method: 'post',
      data,
      ...config,
    })
  }

  put<T = any>(url: string, data: any = {}, config: AxiosRequestConfig = {}) {
    return this.request<T>({
      url,
      method: 'put',
      data,
      ...config,
    })
  }

  delete<T = any>(url: string, data: any = {}, config: AxiosRequestConfig = {}) {
    return this.request<T>({
      url,
      method: 'delete',
      params: data,
      ...config,
    })
  }

  patch<T = any>(url: string, data: any = {}, config: AxiosRequestConfig = {}) {
    return this.request<T>({
      url,
      method: 'patch',
      data,
      ...config,
    })
  }

  public interceptors(instance: AxiosInstance) {
    // 请求拦截
    instance.interceptors.request.use(
      (config) => {
        return config
      },
      (error) => {
        return Promise.reject(error)
      },
    )
    // 响应拦截
    instance.interceptors.response.use(
      (res) => {
        return res
      },
      (error) => {
        return Promise.reject(error)
      },
    )
  }

  public request<T = any>(options: AxiosRequestConfig) {
    options = Object.assign({}, this.options, options)
    // this.interceptors(this.instance)
    return this.instance.request<T, IBaseResponse<T>>(options)
  }
}

export default HttpRequest
