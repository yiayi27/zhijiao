import type { App } from 'vue'
import LinkSelect from './src/LinkSelect.vue'

// 注册组件
LinkSelect.install = (app: App) => {
  app.component(LinkSelect.name || 'LinkSelect', LinkSelect)
}

export default LinkSelect
