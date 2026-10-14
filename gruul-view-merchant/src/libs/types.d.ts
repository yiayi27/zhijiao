/**
 * @description ajax res基础返回体
 * @export
 * @interface IBaseResponse
 * @template T
 */
export interface IBaseResponse<T = any> {
  code: number
  data: T
  msg?: string
}

export interface QrCodeConfig {
  /** iframe 的 URL 地址 */
  url: string
  /** iframe 的高度 (支持像素或百分比) */
  height: string | number
  /** 容器元素的 ID */
  id: string
}
