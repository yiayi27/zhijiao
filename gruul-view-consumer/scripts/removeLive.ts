import fs from 'fs'
import path from 'path'
import { fileURLToPath } from 'node:url'
import { stripJsonComments } from '../src/utils/jsonUtils'
import prettier from 'prettier'

const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)

interface AppJson {
  plugins?: any
  subPackages: {
    pages: string[]
    [key: string]: any
  }[]
  [key: string]: any
}

interface ComponentJson {
  usingComponents: {
    [key: string]: any
    live?: string
    'home-live'?: string
  }
  [key: string]: any
}

function removeDir(dir: string): void {
  let files = fs.readdirSync(dir)
  for (let i = 0; i < files.length; i++) {
    let newPath = path.join(dir, files[i])
    let stat = fs.statSync(newPath)
    if (stat.isDirectory()) {
      //如果是文件夹就递归下去
      removeDir(newPath)
    } else {
      //删除文件
      fs.unlinkSync(newPath)
    }
  }
  fs.rmdirSync(dir) //如果文件夹是空的，就将自己删除掉
}

// 判断文件是否存在
const exists = (filePath1: string): boolean => fs.existsSync(filePath1)

const indexPath = path.resolve(__dirname, '../dist/build/mp-weixin/pages/index/index.json')
const filePath = path.resolve(__dirname, '../dist/build/mp-weixin/app.json')
const homePath = path.resolve(__dirname, '../dist/build/mp-weixin/pages/index/modules/home/home.json')
const subcontractLive = path.resolve(__dirname, '../dist/build/mp-weixin/subcontract/pages/live')
const modulesLive = path.resolve(__dirname, '../dist/build/mp-weixin/pages/index/modules/live')
const modulesHomeLive = path.resolve(__dirname, '../dist/build/mp-weixin/pages/index/modules/home/components/home-live')

const indexPathDev = path.resolve(__dirname, '../dist/dev/mp-weixin/pages/index/index.json')
const filePathDev = path.resolve(__dirname, '../dist/dev/mp-weixin/app.json')
const homePathDev = path.resolve(__dirname, '../dist/dev/mp-weixin/pages/index/modules/home/home.json')
const subcontractLiveDev = path.resolve(__dirname, '../dist/dev/mp-weixin/subcontract/pages/live')
const modulesLiveDev = path.resolve(__dirname, '../dist/dev/mp-weixin/pages/index/modules/live')
const modulesHomeLiveDev = path.resolve(__dirname, '../dist/dev/mp-weixin/pages/index/modules/home/components/home-live')

const isFileExists: boolean =
  exists(filePath) && exists(homePath) && exists(indexPath) && exists(subcontractLive) && exists(modulesLive) && exists(modulesHomeLive)
const isFileExistsDev: boolean =
  exists(filePathDev) &&
  exists(homePathDev) &&
  exists(indexPathDev) &&
  exists(subcontractLiveDev) &&
  exists(modulesLiveDev) &&
  exists(modulesHomeLiveDev)

const removeLidt = async (): Promise<void> => {
  if (isFileExists) {
    const fileContent = fs.readFileSync(filePath).toString()
    const data = JSON.parse(stripJsonComments(fileContent)) as AppJson

    const homeContent = fs.readFileSync(homePath).toString()
    const homeData = JSON.parse(stripJsonComments(homeContent)) as ComponentJson

    const indexDataContent = fs.readFileSync(indexPath).toString()
    const indexData = JSON.parse(stripJsonComments(indexDataContent)) as ComponentJson

    delete indexData.usingComponents.live
    delete homeData.usingComponents['home-live']

    delete data.plugins
    const index = data.subPackages[1].pages.findIndex((page) => page.includes('pages/live/live'))

    data.subPackages[1].pages.splice(index, 1)

    // 使用 Prettier 格式化 JSON
    const prettierConfig = await prettier.resolveConfig(process.cwd())
    const formattedAppJson = await prettier.format(JSON.stringify(data), {
      ...prettierConfig,
      parser: 'json',
    })
    const formattedHomeJson = await prettier.format(JSON.stringify(homeData), {
      ...prettierConfig,
      parser: 'json',
    })
    const formattedIndexJson = await prettier.format(JSON.stringify(indexData), {
      ...prettierConfig,
      parser: 'json',
    })

    fs.writeFileSync(filePath, formattedAppJson)
    fs.writeFileSync(indexPath, formattedIndexJson)
    fs.writeFileSync(homePath, formattedHomeJson)

    removeDir(subcontractLive)
    removeDir(modulesLive)
    removeDir(modulesHomeLive)
    console.log('移除生产环境小程序直播插件成功')
  }
  if (isFileExistsDev) {
    const fileContent = fs.readFileSync(filePathDev).toString()
    const data = JSON.parse(stripJsonComments(fileContent)) as AppJson

    const homeContent = fs.readFileSync(homePathDev).toString()
    const homeData = JSON.parse(stripJsonComments(homeContent)) as ComponentJson

    const indexDataContent = fs.readFileSync(indexPathDev).toString()
    const indexData = JSON.parse(stripJsonComments(indexDataContent)) as ComponentJson

    delete indexData.usingComponents.live
    delete homeData.usingComponents['home-live']

    delete data.plugins
    const index = data.subPackages[1].pages.findIndex((page) => page.includes('pages/live/live'))

    data.subPackages[1].pages.splice(index, 1)

    // 使用 Prettier 格式化 JSON
    const prettierConfig = await prettier.resolveConfig(process.cwd())
    const formattedAppJson = await prettier.format(JSON.stringify(data), {
      ...prettierConfig,
      parser: 'json',
    })
    const formattedHomeJson = await prettier.format(JSON.stringify(homeData), {
      ...prettierConfig,
      parser: 'json',
    })
    const formattedIndexJson = await prettier.format(JSON.stringify(indexData), {
      ...prettierConfig,
      parser: 'json',
    })

    fs.writeFileSync(filePathDev, formattedAppJson)
    fs.writeFileSync(indexPathDev, formattedIndexJson)
    fs.writeFileSync(homePathDev, formattedHomeJson)

    removeDir(subcontractLiveDev)
    removeDir(modulesLiveDev)
    removeDir(modulesHomeLiveDev)
    console.log('移除开发环境小程序直播插件成功')
  }
}

removeLidt()
