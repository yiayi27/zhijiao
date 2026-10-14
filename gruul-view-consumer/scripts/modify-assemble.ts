import { resolve } from 'path'
import modifyAppId from './modify-appId'
import modifyLiveId from './modify-live-id'
import { loadEnv, type Plugin } from 'vite'
import fs from 'fs'
import prettier from 'prettier'

export default function modifyAssemble(mode: string): Plugin[] {
  // 执行修改操作
  const baseUrl = process.cwd()
  const platform = process.env.UNI_PLATFORM
  const pagesPath = resolve(__dirname, '../src/pages.json')
  const environmentObj = loadEnv(mode, baseUrl) as unknown as ImportMetaEnv

  if (platform === 'mp-weixin') {
    // 修改 appid
    modifyAppId(environmentObj)
    // 修改 wx 直播 id
    modifyLiveId(pagesPath, environmentObj)
  }

  // 使用 prettier 格式化 manifest.json
  formatManifest()

  // 返回空数组作为插件
  return []
}

async function formatManifest() {
  const manifestPath = resolve(__dirname, '../src/manifest.json')
  const manifest = fs.readFileSync(manifestPath, 'utf-8')
  const formattedManifest = await prettier.format(manifest, {
    parser: 'json',
  })
  fs.writeFileSync(manifestPath, formattedManifest, {
    encoding: 'utf-8',
  })
}
