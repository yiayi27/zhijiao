import { getActivityList } from '@/api/modules/chain'
import { useSettingStore, type ITabName } from '@/store/modules/setting'
import { useUserStore } from '@/store/modules/user'
import { LOGIN_OPEN } from './emitNameConfig'
import { storeToRefs } from 'pinia'

const userStore = useUserStore()
const { isLogin } = storeToRefs(userStore)

interface ItemLink {
  [x: string]: any
  type: number // 链接选择器分类id 1.功能页面 2.商超 3.分类 4.活动 5.自定义页面 6.外链
  url: string //  链接地址
  append: string // 链接后缀
  id: number // 链接相关id
  name?: string // 链接名称
}

export const navtofun = (itemLink: ItemLink, formType?: any) => {
  if (itemLink.url === 'subcontract/pages/integralShop/integralShop') {
    if (!isLogin.value) {
      uni.$emit(LOGIN_OPEN, true)
      return
    }
  }
  if (itemLink.type === 7 && itemLink.append === 'appmodel') {
    //点击跳转小程序
    uni.navigateToMiniProgram({
      appId: itemLink.url,
      envVersion: 'release',
    })
    return
  }
  if (itemLink.type === 0 && itemLink.id === 8) {
    //点击客服
    console.log('点击客服')
    return
  }
  if (itemLink.url) {
    const pageList = useSettingStore().pageList
    const newPages = pageList.filter((item: { isDef: string }) => {
      return item.isDef === '1'
    })

    const buttomList = useSettingStore().tabBar.list
    const jumpToButtom = buttomList.filter((item: { linkName?: string }) => {
      return item.linkName === itemLink.name
    })
    if (jumpToButtom.length > 0) {
      // 涉及店铺导航跳转时的路径是否存在于首页路径
      if (itemLink.type === 5) {
        useSettingStore().setCurrentPageId(jumpToButtom[0].id)
        useSettingStore().setNewCurrentPageId(jumpToButtom[0].id)
        useSettingStore().setCurrentTab(jumpToButtom[0].name)
        useSettingStore().setShowTabBar(jumpToButtom[0])
        useSettingStore().setNewJump({
          nid: jumpToButtom[0].id,
          name: jumpToButtom[0].text,
        })
      } else {
        useSettingStore().setCurrentPageId(jumpToButtom[0].id)
        useSettingStore().setCurrentTab(jumpToButtom[0].name)
        useSettingStore().setIndexTitle(jumpToButtom[0].text)
      }
      return
    }
    if (itemLink.type === 0 || itemLink.type === 4 || itemLink.type === 5 || itemLink.type === 2) {
      // 0功能页面  4.活动营销 2展示分类
      if (itemLink.append || itemLink.type === 5) {
        if (itemLink.type === 5 && itemLink.append !== 'home') {
          useSettingStore().setHasInCustom(true)
          useSettingStore().setNewCurrentPageId(itemLink.id)
          uni.navigateTo({
            url: `/pages/index/custom/custom`,
          })
          return
        } else {
          if (itemLink.url.indexOf('pages/index/') === -1) {
            // 如果跳转路径不存在首页路径，判断append是否有值，有跳转到对应路径, 需要与底部导航的路径做区分
            uni.navigateTo({
              url: `${itemLink.url}`,
            })
            return
          }
          if (itemLink.append === 'mall' && itemLink.type === 2) {
            if (itemLink.selectedIconPath) {
              // 选择商超某个分类后进入分类页面
              const selectItem = buttomList.filter((item: { name: string }) => {
                return item.name === itemLink.append
              })
              useSettingStore().setCurrentPageId(selectItem[0].id)
              useSettingStore().setCurrentTab(itemLink.append)
              useSettingStore().setIndexTitle(selectItem[0].text)
              return
            } else {
              uni.navigateTo({
                url: `/subcontract/pages/spellPage/index?titleName=${itemLink.name}&id=${itemLink.id}`,
              })
              return
            }
          }
          // 有append 说明路径都为/pages/index/index
          if (Number(itemLink.id) === 471) {
            useSettingStore().setNewCurrentPageId(newPages[0].id)
          } else {
            useSettingStore().setNewCurrentPageId(itemLink.id)
          }
          useSettingStore().setCurrentPageId(itemLink.id)
          useSettingStore().setCurrentTab(itemLink.append as ITabName)
        }
        useSettingStore().setHistoryPage(itemLink)
        useSettingStore().setShowTabBar(itemLink as any)

        // 分类页里的海报点击
        if (formType === 'poster') {
          const selectItem = buttomList.filter((item: { name: string }) => {
            return item.name === itemLink.append
          })
          useSettingStore().setIndexTitle(selectItem[0].text)
        }
      } else if (itemLink.id === 13) {
        getActivityList({}).then((res) => {
          itemLink.url = `subcontract/pages/chainDetail/chainDetail?id=${res.id}`
          uni.navigateTo({
            url: `${itemLink.url}`,
          })
          return
        })
      } else {
        if (itemLink.id === 11) {
          const openMember = useSettingStore().shopSetting.openMember
          if (!openMember) {
            uni.showToast({
              icon: 'none',
              title: '功能暂未开启',
            })
            return
          }
        }
        if (itemLink.id === 14 && itemLink.type === 4) {
          const openrecyling = useSettingStore().recyclingsetting.recyclingState
          if (!openrecyling) {
            uni.showToast({
              icon: 'none',
              title: '功能暂未开启',
            })
            return
          }
        }
        uni.navigateTo({
          url: `${itemLink.url}`,
        })
      }
    } else if (itemLink.type === 1) {
      // 1商超页面
      uni.navigateTo({
        url: `${itemLink.url}?id=${itemLink.id}`,
      })
    } else if (itemLink.type === 6) {
      // 外部链接
      uni.navigateTo({
        url: `/subcontract/pages/linkPage/linkPage?src=${itemLink.url}`,
      })
    }
    if (itemLink.type !== 6) {
      useSettingStore().setNewCurrentPage(itemLink)
      uni.$emit('onTap')
    }
  } else {
    console.error('页面路径为空')
  }
}
