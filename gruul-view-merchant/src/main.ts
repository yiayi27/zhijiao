import { setupPlugin } from '@/libs/plugin'
import { setupStore } from '@/store'
import dayjs from 'dayjs'
import duration from 'dayjs/plugin/duration'
import timezone from 'dayjs/plugin/timezone'
import utc from 'dayjs/plugin/utc'
import { createApp } from 'vue'
import App from './App.vue'
import router, { setupRouter } from './router'
// import { installPlugin } from "@/libs/plugin"
import '@/assets/styles/tailwind.css'
import '@/assets/styles/index.scss'
import '@/assets/styles/font/iconfont.css'
import '@/assets/styles/font/iconfont.js'
import '@/assets/styles/font/icon.css'

dayjs.extend(utc)
dayjs.extend(timezone)
dayjs.extend(duration)

async function bootstrap() {
  const app = createApp(App)

  setupPlugin(app)

  setupStore(app)

  setupRouter(app)

  await router.isReady()

  app.mount('#app')
}

void bootstrap()
