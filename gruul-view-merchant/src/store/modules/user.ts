import type { IShopInfo, IUserInfo } from '@/api/type'
import { getShopInfo } from '@/api/businessCenter/setting'
import { getAccountInfo } from '@/api/sign'
import { setGlobalShopId } from '@/libs'
import { ACCESS_TOKEN, REFRESH_TOKEN, SHOP_ID } from '@/libs/enum/storage'
import { storage } from '@/libs/storage'
import { store } from '@/store'
import { defineStore } from 'pinia'

// 定义状态接口
interface UserState {
  userInfo: IUserInfo | null
  token: string
  refreshToken: string
  shopInfo: IShopInfo | null
  iphone: string
  currentShopId: string
}

// 创建 Pinia store
export const useUserStore = defineStore('user', {
  // 初始化状态
  state: (): UserState => ({
    token: storage.get(ACCESS_TOKEN),
    refreshToken: storage.get(REFRESH_TOKEN),
    currentShopId: storage.get(SHOP_ID),
    userInfo: null,
    shopInfo: null,
    iphone: '',
  }),

  // 定义操作（actions）
  actions: {
    // 设置用户信息
    setUserInfo(data: IUserInfo | null) {
      this.userInfo = data
    },

    // 设置店铺信息
    setShopInfo(data: IShopInfo) {
      this.shopInfo = data
    },

    // 设置 token
    setToken(data: string) {
      this.token = data
      storage.set(ACCESS_TOKEN, data)
    },

    // 设置刷新 token
    setRefreshToken(data: string) {
      this.refreshToken = data
      storage.set(REFRESH_TOKEN, data)
    },

    // 设置 iphone（手机号）
    setIphone(data: string) {
      this.iphone = data
    },

    // 设置当前的shopId
    setCurrentShopId(data: string) {
      this.currentShopId = data
      storage.set(SHOP_ID, data)
    },

    // 获取用户信息
    async getUserInfo(skip = true) {
      if (this.shopInfo && this.userInfo && skip) {
        return
      }

      const [shopInfo, userInfo]: [any, any] = await Promise.all([getShopInfo(), getAccountInfo()])
      this.setShopInfo(shopInfo)
      this.setUserInfo(userInfo)
      setGlobalShopId(shopInfo.platformShopId)
      return true
    },
  },
})

// Need to be used outside the setup
export function useUserStoreWithOut() {
  return useUserStore(store)
}
