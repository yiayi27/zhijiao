import type { QrCodeConfig } from '@/libs/types'
import { ACCESS_TOKEN, PERMISSION, REFRESH_TOKEN, SHOP_ID } from '@/libs/enum/storage'
import { storage } from '@/libs/storage'
import { useMenuStore } from '@/store/modules/menu'
import { useUserStore } from '@/store/modules/user'
import { unionAccountLogout } from '@/api/sign'

export const isDev = import.meta.env.MODE === 'development'

/**
 * 创建微信扫码iframe窗口
 */
export function createQrCode(config: QrCodeConfig): void {
  const iframe = document.createElement('iframe')
  iframe.src = config.url

  // 使用 RAF 替代 setTimeout 获得更好性能
  requestAnimationFrame(() => {
    // 类型安全的属性设置
    iframe.setAttribute('frameborder', '0')
    iframe.style.overflow = 'hidden'
    iframe.style.width = '100%'
    iframe.style.height = typeof config.height === 'number' ? `${config.height}px` : config.height

    iframe.id = 'iframeId'

    // 安全获取容器元素
    const container = document.getElementById(config.id)
    if (!container) {
      console.error(`找不到 ID 为 ${config.id} 的容器元素`)
      return
    }

    // 清空容器并添加 iframe
    container.innerHTML = ''
    container.appendChild(iframe)
  })
}

/** 修复el Dropdown下拉 */
export function dropdownHack() {
  const admin = document.querySelector('.admin')

  if (admin) {
    admin.addEventListener('scroll', () => {
      const els = document.querySelectorAll('.el-dropdown-menu')
      Array.from(els).forEach((el) => {
        if (el instanceof HTMLElement) {
          el.style.display = 'none'
        }
      })
    })
  }
}

/**
 * 切换为登录状态
 */
export function modifySignStatus(token: string) {
  const userStore = useUserStore()
  userStore.setToken(token)
}

/**
 * 设置刷新token
 * @param refreshToken
 */
export function modifySignRefresh(refreshToken: string) {
  const userStore = useUserStore()
  userStore.setRefreshToken(refreshToken)
}

/**
 * 清除localStorage登录信息
 */
export function logout() {
  const menuStore = useMenuStore()
  storage.delete(ACCESS_TOKEN)
  storage.delete(SHOP_ID)
  storage.delete(PERMISSION)
  storage.delete(REFRESH_TOKEN)
  menuStore.setRouters([])
  unionAccountLogout()
}

// 设置全局ShopId
export function setGlobalShopId(shopId: string) {
  const userStore = useUserStore()
  userStore.setCurrentShopId(shopId)
}

// 设置权限参数
export function setGlobalPermission(data: { perms: string[]; role: string }) {
  const menuStore = useMenuStore()
  menuStore.setPermission(data)
}
