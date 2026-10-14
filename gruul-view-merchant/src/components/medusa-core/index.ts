import type { App } from 'vue'
import draggable from 'vuedraggable'
import Components from './packages'

// 组件安装函数
function setupMedusa(app: App): void {
  // 注册所有组件
  app.use(Components)

  // 注册第三方拖拽组件
  app.component('MDraggable', draggable)
}

// 默认导出安装函数
export default {
  install: setupMedusa,
}
