import { defineStore } from 'pinia'

export const useRequestStore = defineStore('request', {
  state: () => {
    return {
      // 是否正在无感刷新中
      isRefresh: false,
    }
  },

  actions: {
    setRefreshState(val: boolean) {
      this.isRefresh = val
    },
  },

  getters: {
    isRefreshing(state) {
      return !!state.isRefresh
    },
  },
})
