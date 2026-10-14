import { defineStore } from 'pinia'
import { userMsgUpdate, userMsgGet, login, accountUserLogin, getLoginId, uniformLogin, getTenantId } from '@/api/modules/user'
import { getCarsList } from '@/api/modules/shopCar'
import storage from '@/utils/storage'
import { GrantType, type LoginForm } from '@/api/modules/sign/type'
import { doSignByUser } from '@/api/modules/sign'
import { LOGIN_OPEN, LOGIN_SUCCESS } from '@/utils/emitNameConfig'
import { useRequestStore } from './request'
import { setSignRule } from '@/api/modules/integral'

interface InfoExtends {
  clockNum?: objectdata
  communityId?: string
  consumeNum?: number
  integral?: number
  integralTotal?: objectdata
  isBlacklist?: number
  lastDealTime?: objectdata
  lastLoginTime?: string
  oneselfActivityAssociation?: objectdata
  assembleActivityAreaAssociation?: objectdata
  shippingPointInfoVo?: objectdata
  shopId?: string
  memberInfo?: any[]
}

interface IUser {
  tenantId: string
  token: string
  couponType: boolean
  infoExtends: InfoExtends
  shopUserId: string
  transData: any
  code?: string
}

type objectdata = {
  [key: string]: Long | boolean | objectdata | any[]
}

const initUser = () => {
  return (
    storage.get('getLoginData') ?? {
      tenantId: '',
      token: '',
      refreshToken: '',
      couponType: true,
      nickname: '',
      phone: '',
      shopUserId: '',
      infoExtends: {
        memberInfo: [
          {
            id: null,
            memberCardInfoId: null,
            memberExpireTime: null,
            rankCode: null,
            rankName: null,
            rebateBonus: 0,
            state: 0,
            status: 2,
            supplyAndRebateBonus: 0,
            supplyBonus: 0,
            userId: '-1',
          },
        ],
      },
      transData: {
        goodData: {},
        shopUserId: '',
      },
    }
  )
}

export const useUserStore = defineStore('user', {
  state: () => ({
    /** 用户信息 */
    userInfo: initUser(),

    /** 自定义用户信息 */
    userInfoTem: null,

    /** 自定义用户信息 */
    goodsNumber: 0,
  }),

  actions: {
    /**
     * 初始化用户信息
     */
    initUserInfo() {
      this.setUserInfo(initUser())
    },

    /**
     * 设置用户信息
     * @param data 用户信息
     */
    setUserInfo(data: IUser) {
      this.userInfo = data
    },

    /**
     * 设置全局购物车数量
     * @param data 购物车数量
     */
    setGoodsNumber(data: number) {
      this.goodsNumber = data
    },

    //获取code
    getCode() {
      return new Promise<string>((resolve, reject) => {
        //获取微信小程序 OPENID (loginCode)
        // #ifdef MP-WEIXIN
        uni.login({
          success: (login_res) => {
            if (login_res.errMsg === 'login:ok') {
              resolve(login_res.code)
            }
          },
          fail: (err) => {
            console.log(err)
            reject(err)
          },
        })
        // #endif
      })
    },

    /**
     * 设置token
     */
    setToken(token: string, refreshToken: string) {
      this.userInfo.token = token
      this.userInfo.refreshToken = refreshToken
      storage.set('getLoginData', this.userInfo)
    },

    /**
     * 全局登陆方法
     * @param info.type 是否清除登录缓存，1是，0否
     * @param info.GrantType 授权类型
     * @param info.form 登录表单
     */
    async login(info: { GrantType: GrantType; form?: LoginForm; type?: number }) {
      const form = info.form || {}
      if (info.type === 1) {
        storage.remove('getLoginData')
      }
      // #ifdef MP-WEIXIN
      if (!form.mobileCode) {
        return uni.showToast({
          icon: 'none',
          title: '请先获取手机号',
        })
      }
      const code = await this.getCode()
      form.loginCode = code
      // #endif

      // #ifndef MP-WEIXIN
      if (info.GrantType === GrantType.SMS_CODE && (!info.form?.mobile || !info.form?.code)) {
        return uni.showToast({
          icon: 'none',
          title: '请传入手机号和验证码',
        })
      }
      // #endif

      if (info.GrantType === GrantType.REFRESH_TOKEN && !info.form?.value) {
        return uni.showToast({
          icon: 'none',
          title: '请传入刷新token',
        })
      }

      try {
        const data = await doSignByUser(info.GrantType, form)
        useRequestStore().setRefreshState(false)
        console.log('data', data)
        accountUserLogin({})
        setSignRule({})
        // token 加入 store
        this.setToken(data.value, data.refreshToken.value)
        // 关闭授权弹窗
        uni.$emit(LOGIN_OPEN, false)
        // 登录成功返回的页面中可以监听此事件 用于刷新用户信息
        uni.$emit(LOGIN_SUCCESS)

        // TODOS: 积分
        // ...

        // TODOS: 获取会员信息
        // ...
        // 获取购物车数量
        this.getShopCarNum()
        const user = await userMsgGet({ infoLevel: 2 })
        user.transData = {
          goodData: {},
          shopUserId: user.shopUserId,
        }
        Object.assign(this.userInfo, user)
        storage.set('getLoginData', this.userInfo)
        return data
      } catch (error) {
        console.log(error)
        throw error
      }

      // try {
      //   let userInfo = this.userInfo
      //   // const {code} = await this.getCode()
      //   const code = this.userInfo.code
      //   const { sessionKey } = await login(code)
      //   const { iv, encryptedData } = info.detail
      //   const loginId = await getLoginId({ sessionKey, iv, encryptedData })
      //   const result = await uniformLogin(loginId)
      //   userInfo.token = result.token.token
      //   const user = await userMsgGet({ infoLevel: 2 })
      //   userInfo = Object.assign({}, userInfo, user, {
      //     couponType: true,
      //     sessionKey,
      //   })
      //   const transData = {
      //     goodData: {},
      //     shopUserId: userInfo.shopUserId,
      //     communityId: userInfo.infoExtends.communityId,
      //   } as objectdata
      //   // if (user.whetherAuthorization) {
      //   //   userMsgUpdate(userInfo);
      //   // }
      //   userInfo.transData = transData
      //   userInfo.infoExtends = user.infoExtends
      //   const res = userInfo
      //   storage.set('getLoginData', res)
      //   this.setUserInfo(userInfo)
      //   accountUserLogin({})
      // }
    },

    /**
     * 获取购物车数量
     */
    async getShopCarNum() {
      if (!this.isLogin) {
        return
      }
      const res = await getCarsList()
      const useList: any[] = []
      const noList: any[] = []
      let goodsNumber = 0
      if (!res) {
        goodsNumber = 0
      } else {
        const list = res
        list.forEach(
          (item: {
            skuList: number[]
            skuStocks: { id: number; stock: number }[]
            status: number
            deleted: number
            skuId: number
            activityEndTime: string
            activityStatus: string
            goodsNumber: number
          }) => {
            item.skuList = []
            item.skuStocks.forEach((v) => {
              item.skuList.push(v.id)
            })
            const type = item.skuStocks.every((v) => {
              return v.stock === 0
            })
            item.status = type ? 2 : item.status
            if (item.deleted === 1) {
              item.status = 2
            }
            item.status = item.skuList.indexOf(item.skuId) === -1 ? 0 : item.status
            if (item.activityEndTime) {
              item.status = item.activityStatus === '1' || item.activityStatus === '0' ? 1 : 0
            }
            if (item.status === 1) {
              useList.push(item)
            } else {
              noList.push(item)
            }
          },
        )
        goodsNumber = useList.reduce((preVal: number, item: { goodsNumber: number }) => {
          return preVal + item.goodsNumber
        }, 0)
      }
      this.setGoodsNumber(goodsNumber)
    },

    /**
     * 获取第三方tenantId
     */
    async getTenId() {
      // const accountInfo = uni.getAccountInfoSync().miniProgram.appId;
      // console.log('accountInfo',accountInfo); // 小程序 appId
      // const extConfig = uni.getExtConfigSync ? uni.getExtConfigSync() : {}
      // console.log('extConfig',extConfig)
      const tenantId = await getTenantId()
      console.log('tenantId', tenantId)

      // const tenantId = uni.getExtConfigSync ? uni.getExtConfigSync().tenantId : import.meta.env.VITE_APP_TENAN_ID
      const userInfo = {
        ...this.userInfo,
        tenantId: tenantId,
      }
      this.setUserInfo(userInfo)
    },
  },

  getters: {
    isLogin(state) {
      return !!state.userInfo.token
    },
  },
})
