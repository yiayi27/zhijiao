import type { App } from 'vue'
import { MCard } from './card'
import { MDragList } from './drag-list'
import { MTable, MTableColumn } from './table'

// 组件配置
interface ComponentConfig {
  name: string
  component: any
}

// 组件映射表
const componentMap: ComponentConfig[] = [
  { name: 'MCard', component: MCard },
  { name: 'MTable', component: MTable },
  { name: 'MTableColumn', component: MTableColumn },
  { name: 'MDragList', component: MDragList },
]

// 安装函数 - 提供类型安全
function install(app: App): void {
  componentMap.forEach(({ name, component }) => {
    app.component(name, component)
  })
}

// 默认导出
export default {
  install,
}
