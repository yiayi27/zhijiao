import replaceManifestKey from './replaceManifestKey'

export default function modifyAppId(environmentObj: ImportMetaEnv) {
  replaceManifestKey('mp-weixin.appid', `"${environmentObj.VITE_APP_ID}"`)
}
