/**
 * 简化的 knip 配置文件，避免加载 vite.config.ts
 */
import type { KnipConfig } from 'knip'
import { readFileSync } from 'fs'

// 手动解析 pages.json 来避免使用 with 语法，增加兼容性
const pagesJson = JSON.parse(readFileSync('./src/pages.json', 'utf-8'))

/**
 * 从 pages.json 提取页面路径
 */
const entryPagesList: string[] = ['src/main.ts', 'types/auto-imports.d.ts']

// 处理主包页面
if (pagesJson.pages && Array.isArray(pagesJson.pages)) {
  pagesJson.pages.forEach((page: any) => {
    if (page && page.path) {
      entryPagesList.push(`src/${page.path}.vue`)
    }
  })
}

// 处理分包页面
if (pagesJson.subPackages && Array.isArray(pagesJson.subPackages)) {
  pagesJson.subPackages.forEach((subPackage: any) => {
    if (subPackage && subPackage.root && subPackage.pages && Array.isArray(subPackage.pages)) {
      subPackage.pages.forEach((page: any) => {
        if (page && page.path) {
          entryPagesList.push(`src/${subPackage.root}/${page.path}.vue`)
        }
      })
    }
  })
}

const config: KnipConfig = {
  entry: entryPagesList,
  project: ['src/**/*.ts', 'src/**/*.tsx', 'src/**/*.js', 'src/**/*.jsx', '!src/**/*.d.ts', 'src/**/*.vue', '!src/uni_modules/**/*'],
  ignore: ['vite.config.ts', 'node_modules/**', '.eslintrc.js', '.prettierrc.js'],
  // 显式禁用所有可能尝试加载vite.config.ts的插件
  jest: false,
  vitest: false,
  storybook: false,
  next: false,
  remix: false,
  webpack: false,
  vite: false,
}

export default config
