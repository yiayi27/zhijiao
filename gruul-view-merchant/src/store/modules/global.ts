import { getCommoditySet } from '@/api/commodity/commodity'
import { getOrderManageSetting } from '@/api/order'
import { useUserStore } from '@/store/modules/user'
import { Howl } from 'howler'
import { defineStore } from 'pinia'

// 定义 ISound 接口
interface ISound {
  createTime: string
  updateTime: string
  deleted: boolean
  id: string
  shopId: string
  times: number
  startTime: string
  endTime: string
  orderId: string
  templateUrl: string
  isOpen: number
}

// 定义状态接口
interface GlobalState {
  sounds: ISound[]
  timer: any
  setting: any
  recover: any
  isOpend: boolean
  mode?: string
}

// 创建 Pinia store
export const useGlobalStore = defineStore('global', {
  // 初始化状态
  state: (): GlobalState => ({
    sounds: [],
    timer: null,
    setting: {},
    recover: {},
    isOpend: false,
    mode: '',
  }),

  // 定义操作（actions）
  actions: {
    // 初始化 WebSocket
    initSocket() {
      const userStore = useUserStore() // 获取 userStore
      const shopInfo = userStore.shopInfo

      if (this.isOpend || !shopInfo || !shopInfo?.platformShopId) return

      this.getShopSetting()
      this.getShopInfo()

      // 打开 WebSocket
      const ws = new WebSocket(import.meta.env.VITE_APP_WS + shopInfo.platformShopId)

      ws.onopen = () => {
        this.isOpend = true
        setInterval(() => {
          ws.send('1')
        }, 9000)
      }

      ws.onmessage = (evt) => {
        const receivedMsg = JSON.parse(evt.data)
        if (receivedMsg && receivedMsg.mp3) {
          if (this.setting.orderNotify) {
            this.handlePlay()
          }
        }
      }

      ws.onclose = () => {
        console.warn('websocket 断开连接')
      }
    },

    // 获取店铺设置
    async getShopSetting() {
      const res = await getOrderManageSetting()
      this.setting = res.data
      return res.data
    },

    // 获取回收设置
    async getShopInfo() {
      const res = await getCommoditySet({})
      this.recover = res.data
      return res.data
    },

    // 处理播放
    async handlePlay() {
      try {
        const orderReminder = (await import('@/assets/order-reminder.mp3')).default
        await this.playSound({
          url: orderReminder,
          id: '', // id 未在原始代码中使用，添加默认值
        })
        return true
      } catch (err) {
        console.error('播放失败', err)
        return Promise.reject(false)
      }
    },

    // 播报声音
    playSound(data: { url: string; id: string }) {
      return new Promise((resolve, reject) => {
        const howler = new Howl({
          src: [data.url],
        })

        let timer: any = null

        howler.play()

        howler.on('play', () => {
          setTimeout(resolve, 2000)
          clearTimeout(timer)
        })

        timer = setTimeout(() => {
          reject(new Error('time out'))
        }, 3000)

        howler.on('playerror', (err) => {
          reject(err)
        })

        howler.on('loaderror', (err) => {
          reject(err)
        })
      })
    },
  },
})
