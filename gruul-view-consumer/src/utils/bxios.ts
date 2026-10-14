type Method = 'GET' | 'POST' | 'PUT' | 'DELETE'

export enum Platform {
  H5 = 'H5',
  MINI_APP = 'MINI_APP',
  PC = 'PC',
}
interface IRequestConfig extends UniApp.RequestOptions {
  [key: string]: any
  timeout?: number
  reqState?: boolean
  header?: any
  data?: string
  method: Method
  dataType?: string
  baseUrl?: string
  url: string
  cancelLast?: boolean
  // 是否传递token 默认传递 true
  needToken?: boolean
}

export interface Result<T = any> {
  data: T
  resHeader: Obj
  config: IRequestConfig
}

type Chain = Array<((value: any) => any | Promise<any>) | undefined>

interface PromiseHandlers<T = any, R = any> {
  fulfilled: (res: T) => Promise<T>
  rejected: (res: R) => Promise<R>
}

export class InterceptorManager<T> {
  private handlers: PromiseHandlers<T>[] = []

  use(fulfilled: PromiseHandlers<T>['fulfilled'], rejected: PromiseHandlers<T>['rejected']): number {
    this.handlers.push({
      fulfilled,
      rejected,
    })
    return this.handlers.length - 1
  }

  forEach(fn: (arg: PromiseHandlers<T>) => void) {
    this.handlers.forEach((h: PromiseHandlers<T>) => fn(h))
  }
}

export class Bxios {
  public interceptors: {
    request: InterceptorManager<IRequestConfig>
    response: InterceptorManager<Result>
  }

  private defaults: IRequestConfig = {
    timeout: 5000,
    reqState: false,
    url: '',
    header: {
      tenantId: '',
    },
    data: '',
    method: 'GET',
    dataType: 'json',
    baseUrl: '',
    needToken: true,
  }

  constructor(defaultConfig: Partial<IRequestConfig> = {}) {
    this.defaults = Object.assign({}, this.defaults, defaultConfig)
    this.interceptors = {
      request: new InterceptorManager<IRequestConfig>(),
      response: new InterceptorManager<Result>(),
    }
  }

  /**
   * @param url 请求地址
   * @param data query
   * @param config 配置 config.cache 开启缓存 duration 缓存时间（ms） 默认 3000
   */
  get<T = any, D = any>(url: string, data?: D, config: Partial<IRequestConfig> = {}): Promise<T> {
    return this.request<T>(
      Object.assign({}, config, {
        header: Object.assign({}, this.defaults.header, config.header),
        method: 'GET',
        data,
        url,
      }),
    )
  }

  post<T = any, D = any>(url: string, data?: D, config: Partial<IRequestConfig> = {}): Promise<T> {
    return this.request<T>(
      Object.assign({}, config, {
        header: Object.assign({}, this.defaults.header, config.header),
        method: 'POST',
        data,
        url,
      }),
    )
  }

  put<T = any, D = any>(url: string, data?: D, config: Partial<IRequestConfig> = {}): Promise<T> {
    return this.request<T>(
      Object.assign({}, config, {
        header: Object.assign({}, this.defaults.header, config.header),
        method: 'PUT',
        data,
        url,
      }),
    )
  }

  delete<T = any, D = any>(url: string, data?: D, config: Partial<IRequestConfig> = {}): Promise<T> {
    return this.request<T>(
      Object.assign({}, config, {
        header: Object.assign({}, this.defaults.header, config.header),
        method: 'DELETE',
        data,
        url,
      }),
    )
  }

  private request<T>(config: IRequestConfig): Promise<T> {
    const chain: Chain = [this.dispatchRequest.bind(this), undefined]
    let promise = Promise.resolve({ ...this.defaults, ...config })

    this.interceptors.request.forEach((interceptor) => {
      chain.unshift(interceptor.fulfilled.bind(this), interceptor.rejected)
    })

    this.interceptors.response.forEach((interceptor) => {
      chain.push(interceptor.fulfilled, interceptor.rejected)
    })

    while (chain.length) {
      promise = promise.then(chain.shift(), chain.shift())
    }

    return promise as unknown as Promise<T>
  }

  private requestTaskMap = new Map<string, any>()

  private createRequestKey(config: IRequestConfig): string {
    return `${config.method}~${config.url}`
  }

  private xhrAdapter<T>(config: IRequestConfig): Promise<Result<T>> {
    if (config.cancelLast) {
      this.requestTaskMap.get(this.createRequestKey(config))?.abort()
    }
    return new Promise((resolve, reject) => {
      const requestTaskTemp = uni.request({
        ...config,
        url: `${this.defaults.baseUrl}${config.url}`,
        success: (res: UniApp.RequestSuccessCallbackResult) => {
          if (res.statusCode === 200) {
            const responseData = res.data as T
            resolve({
              data: responseData,
              resHeader: res.header,
              config: Object.assign({}, this.defaults, config),
            })
          } else {
            const responseData = res.data as any
            reject(responseData.error || responseData)
          }
        },
        fail: (err: UniApp.GeneralCallbackResult) => {
          reject(err)
        },
      })
      if (config.cancelLast) {
        this.requestTaskMap.set(this.createRequestKey(config), requestTaskTemp)
      }
    })
  }

  private async dispatchRequest<T>(config: IRequestConfig): Promise<Result<T>> {
    const adapter = this.xhrAdapter<T>(config)
    try {
      return await adapter
    } catch (reason) {
      return Promise.reject(reason)
    }
  }
}

export default Bxios
