import { getShopManage } from '@/api/modules/user' // login,
import { useSettingStore } from '@/store/modules/setting'
import storage from '@/utils/storage'
import { defineStore } from 'pinia'

export interface ShopInfo {
  businessHours?: string
  status?: number
  [key: string]: any
}
export const useShopsetStore = defineStore('shopset', {
  state: () => ({
    /** 自定义店铺转发状态 */
    shopInfo: {} as ShopInfo,

    /** 自定义商品转发状态 */
    goodInfo: {},

    isTrans: {},
  }),

  getters: {},

  actions: {
    /**
     * @description 设置店铺信息
     * @param data 店铺信息数据
     */
    setShopInfo(data: any) {
      this.shopInfo = data
    },

    /**
     * @description 设置自定义商品转发信息
     * @param data 自定义商品转发信息
     */
    setGoodInfo(data: any) {
      this.goodInfo = data
    },

    setIsTrans(data: any) {
      this.isTrans = data
    },

    /**
     * @description 获取店铺信息
     */
    async getshopset() {
      const res = await getShopManage({})
      this.setShopInfo(res)
      storage.set('getshopsetData', res)
      useSettingStore().setShopName(res.shopName)
      useSettingStore().setIndexTitle(res.shopName)
    },
  },
})
