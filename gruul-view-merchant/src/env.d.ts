/// <reference types="vite/client" />
interface ImportMetaEnv {
  readonly VITE_APP_MODE: string
  readonly VITE_APP_APP_VERSION: string
  readonly VITE_APP_BASEPATH: string
  readonly VITE_APP_BASEURL: string
  readonly VITE_APP_UPLOADURL: string
  readonly VITE_APP_DOMAIN: string
  readonly VITE_APP_APPID: string
  readonly VITE_APP_WS: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
