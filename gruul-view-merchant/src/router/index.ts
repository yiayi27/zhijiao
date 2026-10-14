import type { App } from 'vue'
import type { Router, RouteRecordRaw } from 'vue-router'
import { consoleRouters } from '@/router/console'
import { createRouterGuards } from '@/router/tool'
import routes from '@/router/routes'
import { createRouter, createWebHashHistory } from 'vue-router'

const constantRouterMap: Array<RouteRecordRaw> = [
  {
    path: '/sign',
    name: 'SignIndex',
    component: () => import('@/views/sign/Index.vue'),
    meta: {
      title: '登录',
      isShow: 1,
    },
  },
  {
    path: '/changepass',
    name: 'ChangePass',
    component: () => import('@/views/businessCenter/Account/ChangePassword.vue'),
    meta: {
      title: '修改密码',
      isShow: 1,
    },
  },
  {
    path: '/redirect',
    name: 'Redirect',
    component: () => import('@/views/sign/Redirect.vue'),
    meta: {
      title: '认证信息',
      isShow: 1,
    },
  },
  ...consoleRouters,
  ...routes,
]

const router: Router = createRouter({
  history: createWebHashHistory(import.meta.env.VITE_APP_BASEPATH),
  routes: constantRouterMap,
  scrollBehavior(to) {
    if (to.hash) {
      return {
        el: to.hash,
        behavior: 'smooth',
      }
    }
  },
})

export function setupRouter(app: App) {
  app.use(router)
  // 创建路由守卫
  createRouterGuards(router)
}

export default router
