import { defineConfig, loadEnv, type ConfigEnv } from 'vite'
import uni from '@dcloudio/vite-plugin-uni'
import modifyAssemble from './scripts/modify-assemble'
import path from 'path'
import fs from 'fs'

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
export default defineConfig(({ mode }: ConfigEnv) => {
  const env = loadEnv(mode, process.cwd(), '') || {}
  return {
    base: '/h5',
    server: {
      hmr: true,
      port: 7865,
      host: true,
      https: {
        cert: fs.readFileSync(path.join(__dirname, '/cert.crt')),
        key: fs.readFileSync(path.join(__dirname, '/cert.key')),
      },
      proxy: {
        '^/api': {
          target: env.VITE_APP_BASE_URL, // 后台服务器的源
          changeOrigin: true, // 修改源
          rewrite: (path) => path.replace(/^\/api/, ''),
        },
        '^/oss/upload': {
          target: env.VITE_APP_UPLOAD_URL, // 后台服务器的源
          changeOrigin: true, // 修改源
          rewrite: (path) => path.replace(/^\/oss\/upload/, ''),
        },
        '^/proxy/img': {
          target: env.VITE_APP_OSS, // 后台服务器的源
          changeOrigin: true, // 修改源
          rewrite: (path) => path.replace(/^\/proxy\/img/, ''),
        },
      },
    },
    resolve: {
      alias: {
        '@': path.resolve(__dirname, './src'),
      },
    },
    plugins: [uni(), modifyAssemble(mode)],
    css: {
      preprocessorOptions: {
        scss: {
          additionalData: `@import "@/assets/styles/global.scss";`,
        },
      },
    },
    optimizeDeps: {
      include: ['@dcloudio/uni-app', 'lodash-es', 'md5'],
    },
    build: {
      rollupOptions: {
        output: {
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
      emptyOutDir: true,
      sourcemap: mode !== 'production',
      minify: 'terser' as const,
      terserOptions: {
        compress: {
          drop_console: env.VITE_DROP_CONSOLE === 'true',
          drop_debugger: env.VITE_DROP_DEBUGGER === 'true',
        },
      },
    },
  }
})
