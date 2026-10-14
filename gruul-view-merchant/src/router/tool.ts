import type { Router, RouteRecordRaw } from 'vue-router'
import config from '@/config'
import { logout, modifySignStatus } from '@/libs'
import { ACCESS_TOKEN } from '@/libs/enum/storage'
import { onProgressClose, onProgressStart } from '@/libs/nprogress'
import { storage } from '@/libs/storage'
import { ErrorPageRoute } from '@/router/base'
import { useMenuStoreWithOut } from '@/store/modules/menu'
import { useUserStoreWithOut } from '@/store/modules/user'
import { RedirectType } from '@/views/sign/type'

// 使用Set提高查询效率
const WHITE_LIST = new Set(['/sign', '/redirect/sign', '/changepass', '/redirect'])
const NO_NEED_GET_INFO = new Set([RedirectType.LOGIN, RedirectType.BINDING_WX])
const userStore = useUserStoreWithOut()

export function createRouterGuards(router: Router) {
  const menuStore = useMenuStoreWithOut()

  // 统一处理注销逻辑
  const handleLogout = () => {
    modifySignStatus('')
    logout()
  }

  router.beforeEach((to, _from, next) => {
    onProgressStart()
    try {
      const token = storage.get(ACCESS_TOKEN)

      // 未认证处理
      if (!token) {
        handleLogout()

        if (WHITE_LIST.has(to.path)) {
          return next()
        }

        return next(`/sign?redirect=${encodeURIComponent(to.path)}`)
      }

      // 处理根路径重定向
      if (to.path === '/sign') {
        return next('/console')
      }

      if (!to.path.includes('console') && !NO_NEED_GET_INFO.has(to.query?.state as RedirectType)) {
        return userStore
          .getUserInfo()
          .then(async () => {
            // 动态路由处理
            if (!menuStore.routers.length) {
              await menuStore.getInfo().then(async (roles) => {
                await menuStore.generateRoutes({ roles })
                menuStore.routers.forEach((route) => {
                  router.addRoute(route as unknown as RouteRecordRaw)
                })

                // 添加404
                const isErrorPage = router.getRoutes().findIndex((item) => item.name === ErrorPageRoute.name)
                if (isErrorPage === -1) {
                  router.addRoute(ErrorPageRoute as unknown as RouteRecordRaw)
                }
                // 处理可能的新路由
                return next({ ...to, replace: true })
              })
            } else {
              return next()
            }
          })
          .catch((error) => {
            console.error('路由生成失败:', error)
            handleLogout()
            return next(`/sign?redirect=${encodeURIComponent(to.path)}`)
          })
      }

      return next()
    } catch (error) {
      console.error('路由守卫异常:', error)
      return next(_from)
    } finally {
      onProgressClose()
    }
  })

  router.afterEach((to, _from, failure) => {
    document.title = (to.meta.title || config.title) as string
    onProgressClose()
  })
}
