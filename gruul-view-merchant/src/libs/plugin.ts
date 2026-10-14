import type { App } from 'vue'
import MContainer from '@/components/MContainer/MContainer.vue'
import MContent from '@/components/MContainer/MContent.vue'
// 使用SCSS自定义主题时，不需要导入element-plus的CSS文件
// import 'element-plus/dist/index.css'
import MedusaUI from '@/components/medusa-core'
import { Icon, Search, Swipe, SwipeItem, Image as VanImage } from 'vant'
import VueDraggableResizable from 'vue3-draggable-resizable'
import VueDOMPurifyHTML from 'vue-dompurify-html'
import 'vant/lib/index.css'
import 'element-plus/theme-chalk/el-overlay.css'
import 'element-plus/theme-chalk/el-message-box.css'

export function setupPlugin(app: App) {
  // app.use(ElementPlus, { locale: zhCn })

  /** 自定义UI库 */
  app.use(MedusaUI)

  /** 安全HTML */
  app.use(VueDOMPurifyHTML)

  /** admin容器 */
  app.component('MContainer', MContainer)
  app.component('MContent', MContent)

  /* 圖片拖拽 */
  app.component('VueDraggableResizable', VueDraggableResizable)

  app.use(Search).use(Swipe).use(SwipeItem).use(Icon).use(VanImage)
}
