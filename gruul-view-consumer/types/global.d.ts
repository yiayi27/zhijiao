export {}
declare global {
  export interface ImportMetaEnv {
    VITE_APP_APP_REQUEST_VERSION: string
    VITE_APP_APP_SHOW_VERSION: string
    VITE_APP_BASE_URL: string
    VITE_APP_UPLOAD_URL: string
    VITE_APP_TENAN_ID: string
    VITE_APP_ID: string
    VITE_APP_PLAT: string
  }
  export type Long = number | string
  export type Obj = Record<string, any>
}

// 确保这些类型在TypeScript中可用
export type Long = number | string
export type Obj = Record<string, any>

// lodash-es 模块声明，将 debounce 方法标记为已废弃
declare module 'lodash-es' {
  export * from '@types/lodash-es'

  /**
   * @deprecated 此方法在微信小程序中会报错,请自行实现 debounce 方法
   * 禁止使用lodash-es的防抖
   * 推荐使用src/utils/index.ts中的debounce方法
   */
  export function debounce() {}
}
