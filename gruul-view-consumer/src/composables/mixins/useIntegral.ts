import { ref, reactive } from 'vue'
import {
  getIntegralGoods,
  signSetDay,
  userSignMsg,
  todaySign,
  userWalk,
  getUserIntegral,
  integralShopType,
  getIntegralCoupon,
  changeIntegralCoupon,
} from '@/api/modules/integral'
import { useUserStore } from '@/store/modules/user'

interface SignNeedDay {
  [key: string]: string
}

interface SignArrItem {
  type: boolean
  signInState: boolean
  value: number
  isAdd: boolean
  addCode: number
}

interface WeRunData {
  encryptedData: string
  iv: string
}

interface IntegralItem {
  guidePrice: number
  status?: number
  hasUse?: boolean
  index?: number
  checkType?: boolean
  pic?: string
  inventory?: number
  integralGoodName?: string
  integralPrice?: number
  sales?: number
  id?: Long
}

interface CouponItem extends IntegralItem {
  id: Long
  totalAvailable: number
  couponType?: number
  discount?: number
  discountAmount?: number
  conditionAmount?: number
  integral?: number
}

export function useIntegral() {
  // 状态
  const couponList = ref<CouponItem[]>([])
  const todayStep = ref(0)
  const walkAuth = ref(true)
  const goodsList = ref<IntegralItem[]>([])
  const hasMore = ref(false)
  const signNeedDay = ref<SignNeedDay>({})
  const query = reactive({
    current: 1,
    size: 10,
    launchArea: '',
  })
  const runningDays = ref(1)
  const signInState = ref(false)
  const signArr = ref<SignArrItem[]>([])
  const integral = ref(0)
  const avatarUrl = ref('')
  const nickName = ref('')
  const shop = ref({})
  const singIn = ref({})
  const stepNumber = ref(0)

  // 方法
  function getShopType() {
    integralShopType({}).then((res) => {
      shop.value = res.shop
      singIn.value = res.singIn
      stepNumber.value = res.stepNumber
      if (res.shop) {
        if (res.singIn) {
          getDays()
        }
        if (res.stepNumber) {
          walkType()
        }
        console.log('1111111111111')
        getGoods()
      }
    })
  }

  /**
   * 获取商家设置签到天数
   */
  async function getDays() {
    const res = await signSetDay({})
    signNeedDay.value = res
    getUserSign()
  }

  /**
   * 获取用户签到信息
   */
  async function getUserSign() {
    const signNeedDayValue = signNeedDay.value
    const signNeedDayList: string[] = []
    for (const key in signNeedDayValue) {
      if (key) {
        signNeedDayList.push(key)
      }
    }
    const res = await userSignMsg({})
    const signArrValue = dealSignArr(res, signNeedDayValue, signNeedDayList)
    const numList: number[] = []
    signArrValue.forEach((item) => {
      numList.push(item.value)
    })
    const maxName = findMost(numList)
    signArrValue.forEach((item) => {
      if (item.value > maxName!) {
        item.isAdd = true
        item.addCode = item.value - maxName!
      }
    })
    runningDays.value = Number(res.runningDays)
    signArr.value = signArrValue
    signInState.value = res.signInState
  }

  /**
   * 获取签到最大值
   */
  function findMost(arr: number[]): number | undefined {
    const res: { [key: string]: number } = {}
    let maxName: number | undefined,
      maxNum = 0
    arr.forEach((item) => {
      res[item] = (res[item] || 0) + 1
    })
    for (const r in res) {
      if (res[r] > maxNum) {
        maxNum = res[r]
        maxName = Number(r)
      }
    }
    return maxName
  }

  /**
   * 处理签到信息
   */
  function dealSignArr(res: { runningDays: Long; signInState: boolean }, signNeedDay: SignNeedDay, signNeedDayList: string[]): SignArrItem[] {
    const signArr: SignArrItem[] = []
    const runningDaysValue = Number(res.runningDays)
    for (let i = 0; i < signNeedDayList.length; i++) {
      signArr.push({
        type: runningDaysValue - 1 >= i + 1,
        signInState: runningDaysValue === i + 1 && !res.signInState,
        value: Number(signNeedDay[i + 1].split('.')[0]),
        isAdd: false,
        addCode: 0,
      })
    }
    return signArr
  }

  /**
   * 是否开启步数兑换
   */
  function walkType() {
    if (!walkAuth.value) {
      return
    }
    // #ifdef MP-WEIXIN
    uni.authorize({
      scope: 'scope.werun',
      success: () => {
        uni.getWeRunData({
          success: (res) => {
            getUserWalk(res)
            walkAuth.value = true
          },
          fail: () => {
            uni.showToast({
              title: '未授权获取步数',
              icon: 'none',
              mask: true,
            })
            walkAuth.value = false
          },
        })
      },
      fail: () => {
        uni.showToast({
          title: '未授权获取步数',
          icon: 'none',
          mask: true,
        })
        walkAuth.value = false
      },
    })
    // #endif
  }

  /**
   * 获取用户步数
   */
  function getUserWalk(options: WeRunData) {
    const userInfo = useUserStore().userInfo
    const decryptRunData = {
      encryptedData: options.encryptedData,
      ivStr: options.iv,
      sessionKey: userInfo.sessionKey,
    }
    userWalk(decryptRunData).then((res) => {
      todayStep.value = res
    })
  }

  /**
   * 获取积分商品
   */
  async function getGoods(queryParams = {}) {
    // this.query.launchArea = (infoExtends && infoExtends.prohibitStatus === "0") ? infoExtends.pointId : -1;
    if (hasMore.value) {
      return
    }
    // Toast.loading({ duration: 10000 });
    const res = await getIntegralGoods(Object.assign({}, query, queryParams))
    res.list.forEach((item: IntegralItem) => {
      item.guidePrice = item.guidePrice && item.guidePrice > 0 ? Number(item.guidePrice.toFixed(2)) : 0
    })

    const { current, pages, list } = res
    goodsList.value = current > 1 ? [...goodsList.value, ...list] : list
    /** 如果为最后一页设置到底状态 */
    if (current === pages) {
      hasMore.value = true
    }
  }

  /**
   * 用户签到操作
   */
  function todaySignOpt() {
    todaySign({})
      .then((res) => {
        uni.showToast({
          title: `签到成功,获得${res}积分`,
          icon: 'none',
        })
      })
      .then(() => {
        getUserSign()
        getUser()
      })
  }

  /**
   * 获取用户积分信息
   */
  function getUser() {
    getUserIntegral({}).then((res) => {
      integral.value = res.integral
      avatarUrl.value = res.avatarUrl
      nickName.value = res.nickName
    })
  }

  /**
   * 获取积分优惠券
   */
  async function getCoupon() {
    const list: CouponItem[] = []
    const res = await getIntegralCoupon({})
    res.forEach((item: CouponItem, index: number) => {
      item.hasUse = false
      item.index = index
      item.checkType = true
      if (item.status === 1) {
        list.push(item)
      }
    })
    couponList.value = list
  }

  /**
   * 兑换积分优惠券
   */
  function changeCoupon(e: { currentTarget: { dataset: { coupon: { id: Long; totalAvailable: number } } } }) {
    const id = e.currentTarget.dataset.coupon.id
    const avaliable = e.currentTarget.dataset.coupon.totalAvailable
    if (avaliable === 0) {
      return
    }
    uni.showLoading({
      mask: true,
    })
    changeIntegralCoupon(id, {})
      .then(() => {
        uni.showToast({ title: '兑换成功', icon: 'none' })
        getUser()
        getCoupon()
        uni.hideLoading()
      })
      .catch((err) => {
        uni.hideLoading()
        uni.showToast({ title: err, icon: 'none' })
      })
  }

  /**
   * 查看积分商品详情
   */
  function gotoDetail(e: { currentTarget: { dataset: { good: { id: Long } } } }) {
    const id = e.currentTarget.dataset.good.id
    uni.navigateTo({
      url: '/subcontract/pages/detail/integralGoods?id=' + id,
    })
  }

  /**
   * 步数兑换
   */
  function gotoChange() {
    if (!walkAuth.value) {
      uni.openSetting({})
      walkType()
    } else {
      uni.navigateTo({
        url: '/subcontract/pages/integralShop/integralChange/integralChange?todayStep=' + todayStep.value,
      })
    }
  }

  /**
   * 商品详情
   */
  function gotoBalance() {
    uni.navigateTo({
      url: '/subcontract/pages/balanceDetail/integralDetail',
    })
  }

  /**
   * 商品规则
   */
  function gotoRule() {
    uni.navigateTo({
      url: '/subcontract/pages/integralShop/integralRule/integralRule',
    })
  }

  return {
    // 状态
    couponList,
    todayStep,
    walkAuth,
    goodsList,
    hasMore,
    signNeedDay,
    query,
    runningDays,
    signInState,
    signArr,
    integral,
    avatarUrl,
    nickName,
    shop,
    singIn,
    stepNumber,

    // 方法
    getShopType,
    getDays,
    getUserSign,
    findMost,
    dealSignArr,
    walkType,
    getUserWalk,
    getGoods,
    todaySignOpt,
    getUser,
    getCoupon,
    changeCoupon,
    gotoDetail,
    gotoChange,
    gotoBalance,
    gotoRule,
  }
}
