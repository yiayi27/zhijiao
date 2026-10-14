import { defineStore } from 'pinia'

// 定义状态的类型接口
interface DecorationState {
  pageName: string
  activePage: any
  activeComIndex: number | null
  homePageId: string | null
  activePageType: string
  copyLink: string
  isUsercenterCompontents: boolean
  userCenterType: string
  firstStatus: boolean
  activeTab: number
  getFrom: string
  saleMode: string
  isCustom: boolean
  liveItem: any[] | any
}

// 创建 Pinia store
export const useDecorationStore = defineStore('decoration', {
  // 初始化状态，使用类型注解
  state: (): DecorationState => ({
    pageName: '自定义页面',
    activePage: null,
    activeComIndex: null,
    homePageId: null,
    activePageType: 'customize',
    copyLink: '/pages/index/index?page=home',
    isUsercenterCompontents: false,
    userCenterType: '',
    firstStatus: false,
    activeTab: 0,
    getFrom: '',
    saleMode: '',
    isCustom: false,
    liveItem: [],
  }),

  // 定义操作（actions）
  actions: {
    // 更新首页 ID
    setHomePageId(pageId: string) {
      this.homePageId = pageId
    },

    // 更新链接来源
    setLinkForm(from: string) {
      this.getFrom = from
    },

    // 设置是否首次进入编辑页面
    setFirstStatus(data: boolean) {
      this.firstStatus = data
    },

    // 设置页面名称
    setPageName(pageName: string) {
      this.pageName = pageName
    },

    // 设置当前页面预览对象
    setActivePage(activePage: any = {}) {
      if (['usercenter', 'businessSuper'].includes(activePage)) {
        this.isUsercenterCompontents = true
        this.userCenterType = activePage
        this.isCustom = false
        this.liveItem = []
        this.activePage = []
      } else {
        this.activePage = activePage
        this.saleMode = activePage.modelId
        this.isUsercenterCompontents = false
        this.userCenterType = ''
        this.liveItem = []

        if (activePage.modelId) {
          this.isCustom = true
        }
        if (activePage.name === '直播页面') {
          this.isCustom = true
          this.liveItem = activePage
        }
      }
    },

    // 设置用户中心状态
    setIsUserCenter(data: number) {
      this.isUsercenterCompontents = false
      this.userCenterType = ''
      this.activeTab = data
    },

    // 设置页面类型和链接
    setActivePageType({ type, link }: { type: string; link: string }) {
      this.activePageType = type
      this.copyLink = link
    },

    // 设置当前组件索引
    setActiveComIndex(data: number | null) {
      this.activeComIndex = data
    },
  },
})
