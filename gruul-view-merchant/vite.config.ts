import fs from 'node:fs'
import process from 'node:process'
import { fileURLToPath, URL } from 'node:url'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import IconsResolver from 'unplugin-icons/resolver'
import AutoImport from 'unplugin-auto-import/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import Components from 'unplugin-vue-components/vite'
import { defineConfig, loadEnv } from 'vite'
import path from 'node:path'
import tailwindcss from '@tailwindcss/vite'

const __dirname = fileURLToPath(new URL('.', import.meta.url))

// 项目路径中不能包含空格和中文字符
// 中文正则
const reg = /[\u4e00-\u9fa5]/
if (__dirname.includes(' ') || reg.test(__dirname)) {
  // 终止程序
  throw new Error(`项目路径:
        ${__dirname}
        中不能包含 [空格] 和 [中文]
        请修改后重试`)
}

const optimizeDepsElementPlusIncludes = ['element-plus/es']
fs.readdirSync('node_modules/element-plus/es/components').forEach((dirname) => {
  try {
    fs.accessSync(`node_modules/element-plus/es/components/${dirname}/style/css.mjs`)
    optimizeDepsElementPlusIncludes.push(`element-plus/es/components/${dirname}/style/css`)
    optimizeDepsElementPlusIncludes.push(`element-plus/es/components/${dirname}/style/index`)
  } catch (error) {
    return ''
  }
})

const pathSrc = path.resolve(__dirname, 'src')

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '') || {}
  return {
    base: env.VITE_APP_BASEPATH,
    plugins: [
      tailwindcss(),
      vue(),
      AutoImport({
        include: [/\.[tj]sx?$/, /\.ts$/, /\.vue$/],
        // 自动导入 Vue 相关函数，
        imports: ['vue', 'vue-router'],
        // 自动导入 Element Plus 相关函数，如：ElMessage, ElMessageBox... (带样式)
        resolvers: [
          ElementPlusResolver(),
          IconsResolver({
            prefix: 'Icon',
          }),
        ],
        dts: path.resolve(pathSrc, '../types/auto-imports.d.ts'),
        eslintrc: {
          enabled: true,
          filepath: './types/.eslintrc-auto-import.json',
          globalsPropValue: true,
        },
      }),
      Components({
        resolvers: [ElementPlusResolver({ importStyle: 'sass' })],
        dirs: ['src/components'],
        dts: path.resolve(pathSrc, '../types/components.d.ts'),
      }),
      vueJsx(),
    ],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url)),
      },
    },
    server: {
      port: 5001,
      host: true,
      proxy: {
        // 开发环境统一使用 vite server 代理请求 防止跨域, 这样可以在开发环境模拟 https
        '^/api': {
          target: env.VITE_APP_BASEURL, // 后台服务器的源
          changeOrigin: true, // 修改源
          ws: true,
          rewrite: (path) => path.replace(/^\/api/, ''),
        },
      },
    },
    css: {
      preprocessorOptions: {
        scss: {
          additionalData: `@use "@/assets/styles/variables.scss" as *;`,
          javascriptEnabled: true,
        },
      },
    },
    optimizeDeps: {
      include: optimizeDepsElementPlusIncludes,
    },
    build: {
      rollupOptions: {
        output: {
          manualChunks: {
            // 'element-plus': ['element-plus'], // 禁止对 element-plus 进行代码分割 会报错
            vander: ['vue', 'vue-router'],
            'lodash-es': ['lodash-es'],
            axios: ['axios'],
            pinia: ['pinia'],
            '@wangeditor/editor': ['@wangeditor/editor'],
            '@wangeditor/editor-for-vue': ['@wangeditor/editor-for-vue'],
            '@element-plus/icons-vue': ['@element-plus/icons-vue'],
            echarts: ['echarts'],
            howler: ['howler'],
          },
          //入口文件输出配置
          entryFileNames: `assets/js/[name]-[hash].js`,
          //代码分割后的文件输出配置
          chunkFileNames: `assets/js/[name]-[hash].js`,
          //静态资源输出配置
          assetFileNames(assetInfo) {
            //css文件单独输出到css文件夹
            if (assetInfo?.name?.endsWith('.css')) {
              return `assets/css/[name]-[hash].css`
            }
            //图片文件单独输出到img文件夹
            else if (['.png', '.jpg', '.jpeg', '.gif', '.svg', '.webp'].some((ext) => assetInfo?.name?.endsWith(ext))) {
              return `assets/img/[name]-[hash].[ext]`
            }
            //字体文件单独输出到font文件夹
            else if (['.woff2', '.woff', '.ttf'].some((ext) => assetInfo?.name?.endsWith(ext))) {
              return `assets/font/[name]-[hash].[ext]`
            }
            //媒体文件单独输出到media文件夹
            else if (['.mp3', '.mp4', '.avi'].some((ext) => assetInfo?.name?.endsWith(ext))) {
              return `assets/media/[name]-[hash].[ext]`
            }
            //其他资源输出到assets文件夹
            else {
              return `assets/[name]-[hash].[ext]`
            }
          },
        },
      },
      minify: 'terser',
      chunkSizeWarningLimit: 1000, // 提高超大静态资源警告大小
      terserOptions: {
        // 生产环境清除console和debugger
        compress: {
          drop_console: mode === 'production',
          drop_debugger: mode === 'production',
        },
      },
    },
  }
})
