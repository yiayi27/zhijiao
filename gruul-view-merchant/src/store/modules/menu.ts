import type { RouteRecordRaw } from 'vue-router'
import { getAsyncRouterMap } from '@/api/sign'
import { PERMISSION } from '@/libs/enum/storage'
import { storage } from '@/libs/storage'
import routerMap from '@/router/routes'
import { store } from '@/store'
import { cloneDeep } from 'lodash-es'
import { defineStore } from 'pinia'

// 接口定义
interface RouterMeta {
  title?: string
  permission?: string
  show?: boolean
  ishow?: boolean
  isShow?: boolean
  islink?: number
  [key: string]: any
}

// 扩展的路由记录接口
interface ExtendedRouteRecord {
  path: string
  redirect?: string
  name?: string | symbol
  meta?: RouterMeta
  children?: ExtendedRouteRecord[]
  components?: Record<string, any>
  component?: any
  [key: string]: any
}

interface RoleInfo {
  id: string
  properties: string
  [key: string]: any
}

interface PermissionData {
  roles: RoleInfo
}

// 定义状态接口
interface MenuState {
  routersId: string
  routers: RouteRecordRaw[]
  permission: { perms: string[]; role: string }
}

function getDefaultPermissionInfo(): MenuState['permission'] {
  return {
    perms: [],
    role: '',
  }
}

// 创建 Pinia store
export const useMenuStore = defineStore('menu', {
  // 初始化状态
  state: (): MenuState => ({
    routersId: '',
    routers: [],
    permission: storage.get(PERMISSION) || getDefaultPermissionInfo(),
  }),

  // 定义操作（actions）
  actions: {
    // 设置路由
    setRouters(routers: RouteRecordRaw[]) {
      this.routers = routers
    },

    // 设置路由 ID
    setRoutersId(id: string) {
      this.routersId = id
    },

    // 設置權限
    setPermission(data: MenuState['permission']) {
      this.permission = data
      storage.set(PERMISSION, data)
    },

    // 获取用户目录
    async getInfo() {
      const res = await getAsyncRouterMap()
      return res.data
    },

    // 生成路由
    async generateRoutes(data: PermissionData) {
      const { roles } = data

      let cacheMenuKeysMap: Record<string, ExtendedRouteRecord> = {}
      if (roles) {
        const asyncRouterMap = JSON.parse(roles.properties) as ExtendedRouteRecord[]
        cacheMenuKeysMap = getRouterCacheMenuMap(asyncRouterMap)
      }

      // 先将routerMap转换为ExtendedRouteRecord[]类型进行处理
      const extendedRouterMap = cloneDeep(routerMap) as unknown as ExtendedRouteRecord[]
      const routes = getPermissionRoutes(extendedRouterMap, this.permission, cacheMenuKeysMap)

      // 再将结果转换回RouteRecordRaw[]类型
      this.setRouters(routes as unknown as RouteRecordRaw[])
      this.setRoutersId(roles ? roles.id : '')
    },
  },
})

// Need to be used outside the setup
export function useMenuStoreWithOut() {
  return useMenuStore(store)
}

// 获取缓存路由 {[permission]: 数据}
function getRouterCacheMenuMap(routes: ExtendedRouteRecord[]): Record<string, ExtendedRouteRecord> {
  const showMap: Record<string, ExtendedRouteRecord> = {}

  routes.forEach((route) => {
    if (!route.meta?.ishow && route.children && route.children.length) {
      route.children.forEach((child) => {
        if (Object.hasOwn(child.meta || {}, 'show') && Object.hasOwn(child.meta || {}, 'permission') && child.meta?.permission) {
          showMap[child.meta.permission] = child
        }
      })
    }
  })

  return showMap
}

// 获取权限路由
function getPermissionRoutes(
  routes: ExtendedRouteRecord[],
  permission: MenuState['permission'],
  cacheMeneKeysMap: Record<string, ExtendedRouteRecord>,
): ExtendedRouteRecord[] {
  if (permission?.role === 'ADMIN') {
    return routes
  }

  const permissions = new Set(permission.perms)
  return genRoutes(routes, permissions, cacheMeneKeysMap)
}

// 递归生成权限路由
function genRoutes(
  routes: ExtendedRouteRecord[],
  permissions: Set<string>,
  cacheMeneKeysMap: Record<string, ExtendedRouteRecord>,
): ExtendedRouteRecord[] {
  return routes.filter((item) => {
    if (Object.hasOwn(item?.meta || {}, 'permission') && item.meta?.permission && cacheMeneKeysMap[item.meta.permission]) {
      if (item.meta && cacheMeneKeysMap[item.meta.permission]?.meta?.show !== undefined) {
        const cachedMeta = cacheMeneKeysMap[item.meta.permission]?.meta
        if (cachedMeta && typeof cachedMeta.show !== 'undefined') {
          item.meta.show = cachedMeta.show
        }
      }
    }

    if (item.children) {
      item.children = genRoutes(item.children, permissions, cacheMeneKeysMap)
      if (item.children.length === 0) {
        return false // 替换为返回 false 而不是将 item 设为 null
      }
    }

    return item && !(item.meta?.permission && !permissions.has(item.meta.permission))
  })
}

interface MenuItem {
  label: string
  value?: string
  children?: MenuItem[]
}

export function getAllMenu(routes: ExtendedRouteRecord[]): MenuItem[] {
  const menus: MenuItem[] = []

  routes
    .filter((item) => !item?.meta?.isShow)
    .forEach((item) => {
      if (item.meta?.permission && item.meta?.islink === 1) {
        menus.push({
          label: item.meta?.title || '',
          value: item.meta?.permission,
        })
      } else if (item?.children?.length) {
        menus.push({
          label: item.meta?.title || '',
          children: item.children
            .filter((child) => child.meta?.permission && !item?.meta?.isShow)
            .map((child) => ({
              label: child.meta?.title || '',
              value: child.meta?.permission || '',
            })),
        })
      }
    })

  return menus
}
