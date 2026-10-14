// 店铺设置模块
import { defineStore } from 'pinia'
import { isPrimeNum } from '@/utils'
import { getSetting, getGuide, getGuideVisible, getRecyclingSetting } from '@/api/modules/global'
import { getAggregate } from '@/api/modules/userCenter'
import { getIntegralRule } from '@/api/modules/integral'
import { getAllRegionList, getDecorationAll } from '@/api/modules/decoration'
import storage from '@/utils/storage'
import { isEqual } from 'lodash-es'

export type ITabName = 'appmodel' | 'home' | 'mall' | 'shopCar' | 'spike' | 'group' | 'me' | ''

interface IOrderSetting {
  ceiling: number
  customFrom: string
  id: number
  kdAppId: string
  kdAppKey: string
  orderNotify: true
  paymentModel: string
  ratio: number
  shopId: string
  supportAllowedDay: number
  supportAuditDay: number
  type: string
}

interface IShopSetting {
  /** 是否开启评价 */
  hasOpenEvaluate: boolean
  /** 返利设置 */
  rebateSet: { [key: string]: Long }
  /** 是否开启会员功能 */
  openMember: boolean
  /** 当前店铺信息 */
  curShopInfoDto: { shopName?: string }
  appMode?: string
}

type TypeNumber = 1 | 0

export const useSettingStore = defineStore('setting', {
  state: () => ({
    /** 店铺设置 */
    shopSetting: {
      hasOpenEvaluate: false,
      rebateSet: {},
      openMember: false,
      curShopInfoDto: {},
    } as IShopSetting,

    /** 是否准备完毕 */
    isReady: false,

    /** 引导页是否显示 */
    guideVisible: false,

    /** 当前自定义页面 */
    linkPageId: '' as Long,

    /** 当前选中tab */
    currentTab: '',

    /** 搜索页面定义搜索词 */
    searchName: '',

    /** 当前自定义页面id */
    currentPageId: '' as Long,

    /** homeTab的信息obj（跳转页面前判断type==5为自定义页面，需要将store中currentPageId置为homeTab的id,然后跳转;非type==5,跳转linkUrl的地址） */
    homeTab: {} as Obj,

    /** 使用中的模板 */
    templateId: '' as Long,

    /** 重新请求自定义装修数据的id */
    newCurrentPageId: '' as Long,

    /** 页面列表 */
    pageList: [] as any[],

    /** 是否转发进入 */
    iftransFrom: false,

    /** 是否进入过自定义页面 */
    hasInCustom: false,

    /** 自定义页面名称 */
    customName: '',

    /** 获取需要进入的自定义页面的新id */
    customPageId: '' as Long,

    /** 首页标题 */
    indexTitle: '首页',

    /** 重新设置底部选中对象 */
    isReSet: false,

    /** 购物车规格弹窗息屏问题 */
    formShopCar: false,

    /** 订单信息 */
    order: {} as IOrderSetting,

    /** 顶部导航栏高度 */
    navigationBarHeight: 0,

    /** tabBar数据 */
    tabBar: {
      mode: 'default',
      color: '#7A7E83',
      selectedColor: '#F64E3F',
      background: '#ffffff',
      list: [] as any[],
      selectColor: '#F64E3F',
      defaultColor: '#7A7E83',
    },

    /** 积分规则 */
    integral: {
      load: false,
    },

    /** 自定义页面控件数据 */
    components: [] as any[],

    /** 装修数据-拼团分类页 */
    spellPage: {},

    /** 装修数据-商超分类页 */
    businessSuper: {},

    /** 个人中心配置数据 */
    userCenter: {},

    /** 跳转自定义和首页的历史纪录*/
    historyPage: [] as any[],

    /** 引导页数据 */
    guideList: [] as any[],

    newCurrentPage: null,

    /** 专区id */
    saleMode: '' as Long,

    /** 分类页所在专区 */
    modeType: 1 as TypeNumber, // 拼团专区0 商超专区1

    /** 自定义分类页标题 */
    customPageName: '',

    centerTabbarItem: {},

    /** 当前店铺回收、基金相关配置 */
    recyclingsetting: {} as Obj,
  }),

  getters: {},

  actions: {
    /**
     * 设置是否开启转发进入状态
     * @param res true开启 false关闭
     */
    setIftransFrom(res: boolean) {
      this.iftransFrom = res
    },

    /**
     * 设置当前店铺回收、基金相关配置
     * @param res 回收、基金相关配置
     */
    setRecyclingsetting(res: any): void {
      this.recyclingsetting = res
    },

    /**
     * 设置是否开启引导页
     * @param res true开启 false关闭
     */
    setGuideVisible(res: boolean): void {
      this.guideVisible = res
    },

    /**
     * 设置自定义分类页标题
     * @param res 标题名
     */
    setCustomPageName(res: string): void {
      this.customPageName = res
    },

    /**
     * 设置自定义页面名称
     * @param res 名称
     */
    setcustomName(res: string) {
      this.customName = res
    },

    /**
     * 设置当前tab
     * @param tab "" | "home" | "mall" | "shopCar" | "spike" | "group" | "me"
     */
    setCurrentTab(tab: ITabName) {
      this.currentTab = tab
    },

    /**
     * 设置准备完毕
     */
    setIsReady() {
      this.isReady = true
    },

    /**
     * 设置当前自定义页面id
     * @param id
     */
    setCurrentPageId(id: Long = '') {
      this.currentPageId = id
    },

    /**
     * 设置新的id
     * @param id
     */
    setNewCurrentPageId(id: Long = '') {
      this.newCurrentPageId = id
    },

    /**
     * 设置引导页配置
     * @param res 引导页列表
     */
    setGuideList(res: any[]) {
      this.guideList = res
    },

    /**
     * 设置订单设置
     * @param res 订单设置数据
     */
    setOrder(res: IOrderSetting) {
      this.order = res
    },

    /**
     * 设置店铺基本信息
     * @param data 店铺基本信息数据
     */
    setShopSetting(data: IShopSetting) {
      this.shopSetting = data
    },

    /**
     * 设置首页标题
     * @param name 标题名
     */
    setIndexTitle(name: string) {
      this.indexTitle = name
    },

    /**
     * 设置个人中心数据
     * @param data 用户中心数据
     */
    setUserCenter(data: any) {
      this.userCenter = data
      storage.set('getAllUserCenterData', this.userCenter)
    },

    /**
     * 设置页面装修数据
     * @param nid 页面ID
     */
    setComponents(components: any) {
      this.components = components
    },

    /**
     * 设置页面装修模板ID
     * @param data 页面装修模板ID
     */
    setTemplateId(data: Long) {
      this.templateId = data
    },

    /**
     * 设置页面数据
     * @param data 页面数据
     */
    setPageList(data: any) {
      this.pageList = data
    },

    /**
     * 设置tabBar数据
     * @param data
     */
    setTabBar(data: {
      mode: string
      color: string
      selectedColor: string
      background: string
      list: any[]
      selectColor: string
      defaultColor: string
    }) {
      this.tabBar = data
    },

    /**
     * 设置homeTab的信息obj
     */
    setHomeTab(data: any) {
      this.homeTab = data
    },

    /**
     * 设置
     */
    setCenterTabbarItem(data: Record<string, unknown>) {
      this.centerTabbarItem = data
    },

    /**
     * 设置积分开关
     * @param data 开关状态true|false
     */
    setIntegral(data: boolean) {
      this.integral.load = data
    },

    /**
     * 设置新进入的自定义页面的ID
     * @param data 自定义页面ID
     */
    setCustomPageId(data: Long) {
      this.customPageId = data
    },

    /**
     * 设置是否进入过自定义页面
     * @param data true|false
     */
    setHasInCustom(data: boolean) {
      this.hasInCustom = data
    },

    /**
     * 重新设置底部选中对象
     */
    setIsReSet(data: boolean) {
      this.isReSet = data
    },

    /**
     * 设置导航栏高度
     */
    setNavigationBarHeight(data: number) {
      this.navigationBarHeight = data
    },

    setSellPage(data: { spellPage: any; businessSuper: any }) {
      this.spellPage = data.spellPage
      this.businessSuper = data.businessSuper
    },

    setShowTabBar(itemLink: { type: number; url: string; append: string; id: number; sortIndex: number }) {
      const tabBar = this.tabBar
      const arr = tabBar.list.filter(
        (item: { type: number; linkUrl: string; name: string; id: number }) =>
          !!item && item.type === itemLink.type && item.linkUrl === itemLink.url && item.name === itemLink.append && item.id === itemLink.id,
      )
    },

    setHistoryPage(data: any) {
      this.historyPage.push(data)
    },

    setNewCurrentPage(data: any) {
      this.newCurrentPage = data
    },

    setFormShopCar(data: boolean) {
      this.formShopCar = data
    },

    setSaleMode(data: Long) {
      this.saleMode = data
    },

    /**
     * 当前店铺回收、基金相关配置
     */
    async getRecyclingSetting() {
      const res = await getRecyclingSetting()
      this.setRecyclingsetting(res)
    },

    /**
     *  获取是否开启引导页
     */
    getGuideVisible() {
      return new Promise((resolve) => {
        // 从缓存中获取今日是否展示过引导页
        const todayShowed = storage.get(`todayShowedGuide`)
        if (todayShowed) {
          this.setGuideVisible(false)
          resolve(false)
          return
        }
        getGuideVisible()
          .then((res) => {
            if (!!res && !!this.guideList.length) {
              storage.set(`todayShowedGuide`, true)
              this.setGuideVisible(true)
            }
            resolve(res)
          })
          .catch((error) => {
            console.log(error)
            resolve(false)
          })
      })
    },

    /**
     * 判断当前分类页是拼团OR商超
     * @param id 当前分类页ID
     */
    async getPageType(id: Long) {
      let selectItem: Obj[] = []
      const res = await getAllRegionList({})
      selectItem = res.filter((item: { id: Long }) => Number(item.id) === Number(id))
      if (selectItem.length > 0) {
        this.setCustomPageName(String(selectItem[0].modeName))
      }
    },

    /**
     * 获取引导页设置
     */
    getGuideList() {
      return new Promise((resolve) => {
        getGuide()
          .then((res) => {
            const list = res.filter((item: { url: string }) => !!item.url)
            this.setGuideList(list)
            resolve(list)
          })
          .catch((error) => {
            console.log(error)
            resolve([])
          })
      })
    },
    setShopName(name: string) {
      this.shopSetting.curShopInfoDto.shopName = name ?? ''
    },
    /**
     * 获取店铺设置
     * @returns miniMsgVoList
     */
    async getSetting() {
      const { openEvaluate, setting, miniMsgVoList, rebateSet, openMember, mergePoint, curShopInfoDto } = await getSetting()
      const data = {
        hasOpenEvaluate: !!openEvaluate,
        rebateSet,
        openMember: openMember,
        curShopInfoDto: {
          ...this.shopSetting.curShopInfoDto,
          ...curShopInfoDto,
        },
        mergePoint: mergePoint === '0',
      }
      this.setShopSetting(data)
      this.setOrder(setting)
      return miniMsgVoList
    },

    /**
     * 获取个人中心数据并缓存
     */
    async getAllUserCenterData() {
      const res = await getAggregate()
      this.setUserCenter(res)
    },

    setNewJump({ nid, name }: { nid: Long; name: string }) {
      const showPage = this.pageList.filter((item: any) => {
        return Number(item.id) === Number(nid)
      })
      const components: any[] = []
      const assemblyVos = showPage[0]?.assemblyVos || []
      if (assemblyVos.length > 0) {
        assemblyVos.forEach((item: { properties: string }) => {
          const properties = JSON.parse(item.properties)
          components.push(properties)
        })
      }
      this.setComponents(components)
      this.setIndexTitle(name)
    },

    /**
     * 获取聚合装修接口数据
     */
    async getDecorationAllList() {
      const res = await getDecorationAll()
      if (res) {
        storage.set('getDecorationAllListData', res)
      }
      const storageData = storage.get('getDecorationAllListData')
      if (!storageData) {
        uni.showToast({
          title: '接口无数据',
          icon: 'none',
        })
        return
      }
      const data = storageData.data || storageData
      const plugins = data.plugins
      const pages = data.pages
      const id = data.id
      if (plugins.length === 0) {
        uni.showModal({
          title: '无底部导航数据',
          content: '请前往后台装修',
          showCancel: false,
          confirmText: '我知道了',
        })
        return Promise.reject('装修未完成')
      }
      this.parseControlList(plugins)
      this.setTemplateId(id)
      const newPages = data.pages.filter((item: { isDef: string }) => {
        return item.isDef === '1'
      })
      if (!newPages.length) {
        uni.showModal({
          title: '无首页数据',
          content: '请前往后台装修',
          showCancel: false,
          confirmText: '我知道了',
        })
        return Promise.reject('无首页数据')
      }
      this.parseAssembleList(newPages)
      this.setNewCurrentPageId(newPages[0].id)
      this.setPageList(pages)
    },

    /**
     * 解析配置控件数据
     * @param plugins 配置控件数据
     */
    parseControlList(plugins: any[]) {
      let formdata: Obj | null = null
      const navbar = plugins.filter((item: { pluginNameEn: string }) => {
        return item.pluginNameEn === 'navBar'
      })
      if (navbar.length === 0) {
        uni.showModal({
          title: '无底部导航数据',
          content: '请前往后台装修',
          showCancel: false,
          confirmText: '我知道了',
        })
        return
      }
      formdata = JSON.parse(navbar[0].pluginProperties)[0].formData || null

      const navBarform = formdata ? formdata : null
      const handleIcon = this.handleIcon(formdata?.menuList || [])
      const navBarPlugin = navBarform ? handleIcon : null
      const tabBar = {
        ...this.tabBar,
        selectColor: formdata ? formdata.selectColor : '#F64E3F',
        defaultColor: formdata ? formdata.defaultColor : '#7A7E83',
      }
      this.setTabBar(tabBar)
      this.getPlugins(navBarPlugin)
      this.settingBigPic(navBarform)
    },

    /** 解析自定义页面组件数据 */
    parseAssembleList(pages: any[]) {
      if (!pages.length) {
        uni.showToast({
          title: '无首页数据',
          icon: 'none',
        })
        return
      }
      const components: any[] = []
      let assembleList: any[] = pages
      const assemblyVos = assembleList[0].assemblyVos
      if (assemblyVos.length > 0) {
        assemblyVos.forEach((item: { properties: string }) => {
          const properties = JSON.parse(item.properties)
          components.push(properties)
        })
      }
      this.setComponents(components)
      // 处理分类页页面数据
      const spell = pages.find((i: { type: string }) => i.type === '1')
      const businessSuper = pages.find((i: { type: string }) => i.type === '2')
      const spellPage = spell ? (spell.assemblyVos[0] ? JSON.parse(spell.assemblyVos[0].properties) : {}) : {}
      const businessSuperdata = businessSuper ? (businessSuper.assemblyVos[0] ? JSON.parse(businessSuper.assemblyVos[0].properties) : {}) : {}
      this.setSellPage({ spellPage, businessSuper: businessSuperdata })
    },

    /**
     * 处理系统图标
     * @param menuList
     * @returns 菜单列表数据
     */
    handleIcon(menuList: any[]) {
      const icons = [
        {
          def: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/api/home_page.png',
          act: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/api/home_page1.png',
        },
        {
          def: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/api/shopping_mall1.png',
          act: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/api/shopping_mall.png',
        },
        {
          def: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/api/shopping_cart1.png',
          act: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/api/shopping_cart.png',
        },
        {
          def: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/api/my1.png',
          act: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/api/my.png',
        },
        {
          def: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20210112/b2ef1106af8b4a6c9b22ee0afac6195c.png',
          act: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20210112/613afce069ca4cfdb980cd446e0195f1.png',
        },
      ]
      if (menuList.length < 5) {
        menuList.forEach((menu, index) => {
          if (menu.iconType === 1) {
            menu.iconPath = icons[index].def
            menu.selectedIconPath = icons[index].act
          }
        })
      } else {
        if (menuList[0].iconType === 1) {
          menuList[0].iconPath = icons[0].def
          menuList[0].selectedIconPath = icons[0].act
        }
        if (menuList[1].iconType === 1) {
          menuList[1].iconPath = icons[1].def
          menuList[1].selectedIconPath = icons[1].act
        }
        if (menuList[2].iconType === 1) {
          menuList[2].iconPath = icons[2].def
          menuList[2].selectedIconPath = icons[2].act
        }
        if (menuList[3].iconType === 1) {
          menuList[3].iconPath = icons[3].def
          menuList[3].selectedIconPath = icons[3].act
        }
        if (menuList[4].iconType === 1) {
          menuList[4].iconPath = icons[4].def
          menuList[4].selectedIconPath = icons[4].act
        }
      }
      return menuList
    },
    /**
     * 获取并处理页面控件
     * @param navBarPlugin 控件数据
     */
    getPlugins(navBarPlugin: any) {
      if (!navBarPlugin || !navBarPlugin.length) {
        return
      }
      this.setHomeTab(navBarPlugin[0])
      navBarPlugin.forEach((item: { type: number }, index: number) => {
        if (item.type === 5) {
          navBarPlugin[index].name = 'home'
        }
      })
      const tabBar = {
        ...this.tabBar,
        list: navBarPlugin,
      }
      this.setTabBar(tabBar)
      const navbarHome = navBarPlugin.filter((item: { isHome: boolean }) => {
        return item.isHome
      })
      if (navbarHome.length > 0) {
        if (navbarHome[0].type === 5) {
          this.setCurrentPageId(navbarHome[0].id)
          this.setCurrentTab('home')
        } else {
          this.setCurrentPageId('')
          this.setCurrentTab(navbarHome[0].name)
        }
      }
    },
    /**
     * 设置大图模式
     * @param navBarform
     */
    settingBigPic(navBarform: any) {
      const listLen = this.tabBar.list.length
      const centerNum = Math.floor(listLen / 2)

      const centerTabbarItem = isPrimeNum(listLen) ? Object.assign({}, this.tabBar.list[centerNum], { centerNum }) : null
      const tabBar = {
        ...this.tabBar,
      }
      if (navBarform && navBarform.codeStyle === 2) {
        tabBar.mode = 'center'
      } else {
        tabBar.mode = 'default'
      }
      this.setCenterTabbarItem(centerTabbarItem as any)
      this.setTabBar(tabBar)
    },

    /**
     * 获取积分规则
     */
    async getRule() {
      const res = await getIntegralRule({})
      if (res && !isEqual(res, storage.get('getRuleData'))) {
        storage.set('getRuleData', res)
      }
      const storageData = storage.get('getRuleData')
      this.setIntegral(storageData?.length ? storageData[1].open : false)
    },

    /**
     * 返回首页公共方法
     */
    backHome() {
      const { linkUrl, name, type, id } = this.homeTab
      console.log('homeTab', this.homeTab)
      if (['pages/index/index', '/pages/index/index'].includes(linkUrl)) {
        this.setIndexTitle(String(this.shopSetting.curShopInfoDto?.shopName ?? ''))
        this.setCurrentTab(name as ITabName)
        this.setCurrentPageId('')
        this.setCustomPageId('')
        this.setHasInCustom(true)
        this.setIsReSet(false)
        if (type === 5) {
          this.setCurrentPageId(id as Long)
          const newPages = this.pageList.filter((item: any) => item.isDef === '1')
          this.setNewCurrentPageId(newPages[0]?.id)
        }
        uni.reLaunch({
          url: `/pages/index/index`,
        })
      }
    },
  },
})
