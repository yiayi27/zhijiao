// 通知消息模块

import { defineStore } from 'pinia'

interface IMessage {
  id: string
  mark: string
  miniTemplateId: string
  miniOpen: number
  miniMsg: string
}

interface Map {
  [porp: string]: Long
}

export const useMessageStore = defineStore('message', {
  state: () => ({
    /** 模板列表 */
    templates: [] as IMessage[],

    type: {
      /** 消息通知 */
      sendMsg: 'sendMsg',
      /** 审核通知 */
      auditMsg: 'auditMsg',
      /** 审核通知 */
      memberCartOpen: 'memberCartOpen',
      /** 订单退款 */
      orderRefund: 'orderRefund',
      /** 订单退款 */
      orderRefundNotify: 'orderRefundNotify',
    },
  }),

  getters: {
    /** 模板列表ID */
    templateMap(): Map {
      const map: Map = {}
      this.templates.forEach(({ mark, miniTemplateId }) => {
        map[mark] = miniTemplateId
      })
      return map
    },

    /** 模板可用状态 */
    templateSatusMap(): Map {
      const map: Map = {}
      this.templates.forEach(({ mark, miniOpen }) => {
        map[mark] = miniOpen
      })
      return map
    },
  },

  actions: {
    /** 获取模板列表 */
    getTemplates(templates: IMessage[]) {
      this.templates = templates
    },

    /**
     * 订阅消息
     * @param ids 模板id数组
     * 只会返回resolve true 表示成功 false 表示失败
     */
    async subscribe(ids: string[]): Promise<boolean> {
      // return Promise.resolve(false);
      if (!this.templates || !this.templates.length) {
        return false
      }

      const tmplIds = ids.filter((item) => this.templateSatusMap[item]).map((key) => this.templateMap[key])

      if (!tmplIds.length) return false

      return new Promise((resolve) => {
        uni.requestSubscribeMessage({
          tmplIds,
          success: () => resolve(true),
          fail: (err) => {
            console.log('message.ts line 101 err', err)
            resolve(false)
          },
          complete: () => resolve(false),
        })
      })
    },

    /**
     * 获取templatid
     * @param mark
     */
    getTemplateId(mark: string) {
      return this.templateMap[mark]
    },
  },
})
