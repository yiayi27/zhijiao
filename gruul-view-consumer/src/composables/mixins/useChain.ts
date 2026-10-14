import { ref, reactive } from 'vue'
import { getActivityListDetail, getListDetail as apiGetListDetail, getUserRecord, getChainRecord, transFormActivityById } from '@/api/modules/chain'
import DateUtil from '@/mixins/date'
import { useUserStore } from '@/store/modules/user'
import { storeToRefs } from 'pinia'

const { isLogin, userInfo } = storeToRefs(useUserStore())

interface SkuStockItem {
  id: Long
  isCheck: boolean
  price: Long
  originalPrice: Long
  stock: number
  goodNum?: number
  perLimit: number
  specs?: string
}

interface ActStockItem {
  id: Long
  goodNum: number
  price: Long
  stock: number
  perLimit: number
  specs?: string
  sale?: number
  originalPrice?: Long
  pic?: string
}

interface ActivityItem {
  id: Long
  pic?: string
  name?: string
  actStocks: ActStockItem
  skuStocks: SkuStockItem[]
  activityProductId?: Long
  dominoState?: Long
  createTime?: string
  receiverPhone?: string
  payTime?: string
  allStock?: number
  userAvatarUrl?: string
  userName?: string
  itemVoList?: {
    productName: string
    productQuantity?: number
  }[]
  receiverName?: string
  receiverDetailAddress?: string
  payAmount?: Long
}

interface ActivityDetail {
  apiSolitaireActivityProductVos: { productId: Long }[]
  productType: Long
  endTime: string
  startTime: string
  stopTime?: string
  name?: string
  content?: string
  coverPoster?: string
  views?: number
  userRecords?: number
}

interface CountDownResult {
  status: string
  remainTime: number
  toBeginTime: number
  toEndTime: number
  timeData?: {
    hours: string
    minutes: string
    seconds: string
  }
}

export function useChain() {
  // 状态
  const activityDetail = ref<ActivityDetail>({} as ActivityDetail)
  const activityList = ref<ActivityItem[]>([])
  const countDetail = reactive({
    status: '',
    toBeginTime: 0,
    toEndTime: 0,
  })
  const timeData = ref({
    hours: ['0', '0'],
    minutes: ['0', '0'],
    seconds: ['0', '0'],
  })
  const state = ref('')
  const showVisible = ref(false)
  const currentItem = ref<ActivityItem>({} as ActivityItem)
  const totalPrice = ref('0')
  const goodNum = ref(0)
  const buyQuery = ref<ActivityItem[]>([])
  const joinList = ref<ActivityItem[]>([])
  const page = ref(1)
  const size = ref(10)
  const hasMore = ref(true)
  const hasListMore = ref(true)
  const goodQuery = reactive({
    page: 1,
    size: 10,
  })
  const authType = ref(false)
  const options = ref({})
  const actId = ref('')
  const hasChainAct = ref(false)
  const bottomType = ref(true)
  const bottomHeight = ref(46)
  const showCountDown = ref(false)
  const remainTime = ref(0)
  const getActivitytype = ref(false)

  // 方法
  function getHeight() {
    let bottomHeightValue = bottomHeight.value
    uni.getSystemInfo({
      success: (res) => {
        if (res.model.indexOf('iPhone X') > -1) {
          bottomHeightValue = 80
          bottomHeight.value = bottomHeightValue
        }
      },
    })
  }

  function getPosition(type: any) {
    bottomType.value = Boolean(type)
  }

  function getActivity(id: Long) {
    getActivityListDetail(id, {})
      .then((res: ActivityDetail) => {
        const countDownInfo = getCountDownInfo(res)
        dealTime(countDownInfo)
        const now = Date.now()
        const end = Date.parse(res.endTime)
        if (now > end) {
          const culTime = now - end
          const h = culTime / 3600000
          const m = (culTime % (1000 * 60 * 60)) / (1000 * 60)
          if (h > 24) {
            const now = new Date().getTime()
            const stop = new Date(res.endTime.replace(/-/g, '/')).getTime()
            let day = (now - stop) / 1000 / 24 / 60 / 60
            day = +day.toFixed(0)
            res.stopTime = `${day}天前`
          } else if (h > 1) {
            res.stopTime = `${h.toFixed(0)}小时前`
          } else if (m > 0) {
            res.stopTime = `${m.toFixed(0)}分钟前`
          }
        }
        activityDetail.value = res
        totalPrice.value = '0.00'
        goodNum.value = 0
        getActivitytype.value = false
      })
      .catch(() => {
        getActivitytype.value = false
      })
      .finally(() => {
        getBuyerRecord()
        getAllChain()
        getListDetail()
      })
  }

  /**
   * 获取商品数组详情
   */
  function getListDetail() {
    const goodQueryValue = goodQuery
    const activityDetailValue = activityDetail.value
    const idList = activityDetailValue.apiSolitaireActivityProductVos || []
    const ids = idList.map((item) => item.productId).join(',')
    const param = {
      current: goodQueryValue.page,
      distributionMode: 1, // (0–商家配送，1–快递配送)
      size: goodQueryValue.size,
      productType: activityDetailValue.productType,
      productIds: ids,
    }
    apiGetListDetail(param)
      .then((res) => {
        if (res.list.length === 0) {
          hasListMore.value = false
          return
        }
        res.list.forEach((item: ActivityItem) => {
          item.skuStocks.forEach((cItem) => {
            cItem.isCheck = false
            cItem.price = cItem.price.toString()
            cItem.originalPrice = cItem.originalPrice.toString()
            cItem.perLimit = item.actStocks?.perLimit ?? cItem.perLimit
          })
          item.allStock = item.skuStocks.reduce((per: number, tItem) => {
            return per + tItem.stock
          }, 0)
          item.skuStocks[0].isCheck = true
          item.actStocks = { ...item.skuStocks[0], goodNum: 0 }
        })
        const list = activityList.value
        hasListMore.value = true
        activityList.value = list.concat(res.list)
      })
      .catch((err) => {
        console.log('err', err)
      })
  }

  /**
   * 点击复制接龙链接
   */
  function clickCopy() {
    const tenantId = userInfo.value.tenantId
    const url = `https://test.superprism.cn/h5?tenanId=${tenantId}`
    const param = {
      path: url,
    }
    transFormActivityById(actId.value, param).then((res) => {
      uni.setClipboardData({
        data: res,
        success: () => {
          uni.showToast({
            title: '复制成功',
          })
        },
      })
    })
  }

  /**
   * 获取用户购买记录
   */
  function getBuyerRecord() {
    const param = {
      solitaireActivityId: actId.value,
    }
    getUserRecord(param)
      .then((res: ActivityItem[]) => {
        res.forEach((item: ActivityItem) => {
          if (item.receiverPhone) {
            item.receiverPhone = item.receiverPhone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
          }
          if (item.payTime) {
            item.payTime =
              Number(new DateUtil('').getD(new Date())) - Number(new DateUtil('').getD(item.payTime)) > 0
                ? `${Number(new DateUtil('').getD(new Date())) - Number(new DateUtil('').getD(item.payTime))}天前`
                : new DateUtil('').getHM(item.payTime)
          }
        })
        buyQuery.value = res
      })
      .catch((err) => {
        console.log(err)
      })
  }

  /**
   * 获取接龙记录
   */
  function getAllChain() {
    const param = {
      solitaireActivityId: actId.value,
      current: page.value,
      size: size.value,
    }
    getChainRecord(param).then((res) => {
      if (res.list.length === 0) {
        hasMore.value = false
        return
      }
      let list = joinList.value
      list = list.concat(res.list)
      joinList.value = list
    })
  }

  /**
   * 查看更多接龙记录
   */
  function showMore() {
    if (!hasMore.value) {
      return
    }
    page.value = page.value + 1
    getAllChain()
  }

  /**
   * 点击选择规格
   */
  function clickSku(e: { currentTarget: { dataset: { item: ActivityItem } } }) {
    const currentItemData = e.currentTarget.dataset.item
    currentItemData.skuStocks.forEach((cItem) => {
      cItem.isCheck = false
      if (cItem.id === currentItemData.actStocks.id) {
        cItem.isCheck = true
      }
    })
    showVisible.value = true
    currentItem.value = currentItemData
  }

  /**
   * 弹窗修改规格
   */
  function selectSku(skuItem: SkuStockItem) {
    if (skuItem.stock === 0) {
      uni.showToast({
        title: '此规格暂无库存',
        icon: 'none',
      })
      return
    }
    const currentItemData = currentItem.value
    currentItemData.skuStocks.forEach((cItem) => {
      cItem.isCheck = false
      if (skuItem.id === cItem.id) {
        cItem.isCheck = !cItem.isCheck
      }
      if (cItem.isCheck) {
        currentItemData.actStocks = { ...skuItem, goodNum: 0 }
      }
    })
    const activityListValue = activityList.value
    activityListValue.forEach((aItem) => {
      if (aItem.id === currentItemData.id) {
        aItem.actStocks = currentItemData.actStocks
      }
    })
    activityList.value = activityListValue
    currentItem.value = currentItemData
  }

  /**
   * 关闭规格
   */
  function onClose() {
    showVisible.value = false
  }

  /**
   * 增加购物车数量
   */
  function increase(e: { currentTarget: { dataset: { item: ActivityItem } } }) {
    const item = e.currentTarget.dataset.item
    const list = activityList.value
    if (item.actStocks.perLimit > 0 && item.actStocks.goodNum + 1 > item.actStocks.perLimit) {
      uni.showToast({
        title: '商品已达限购',
        icon: 'none',
      })
      return
    }
    if (item.actStocks.goodNum + 1 > item.actStocks.stock) {
      uni.showToast({
        title: '商品库存不足',
        icon: 'none',
      })
      return
    }
    list.forEach((aItem) => {
      if (aItem.id === item.id) {
        aItem.actStocks.goodNum = aItem.actStocks.goodNum + 1
      }
    })
    activityList.value = list
    getTotalPrice()
  }

  /**
   * 减少商品数量
   */
  function decrease(e: { currentTarget: { dataset: { item: ActivityItem } } }) {
    const item = e.currentTarget.dataset.item
    const list = activityList.value
    if (item.actStocks.goodNum - 1 < 0) {
      return
    }
    list.forEach((aItem) => {
      if (aItem.id === item.id) {
        aItem.actStocks.goodNum -= 1
      }
    })
    activityList.value = list
    getTotalPrice()
  }

  /**
   * 算取总价
   */
  function getTotalPrice() {
    if (countDetail.status === 'ended') {
      return
    }
    let total = 0
    let goodNumValue = 0
    const list = activityList.value
    list.forEach((item) => {
      if (item.actStocks.goodNum < 0) {
        item.actStocks.goodNum = 0
      }
    })
    list.reduce((preVal, item) => {
      return (total = preVal + item.actStocks.goodNum * Number(item.actStocks.price))
    }, total)

    list.reduce((preVal, item) => {
      return (goodNumValue = preVal + item.actStocks.goodNum)
    }, goodNumValue)

    totalPrice.value = total.toFixed(2)
    goodNum.value = goodNumValue
  }

  /**
   * 点击提交订单
   */
  function submit() {
    if (countDetail.status !== 'beginning') {
      return
    }
    if (!isLogin.value) {
      authType.value = true
      return
    }
    const apiProductVos = activityList.value
    const items = apiProductVos
      .filter((aItem) => aItem.actStocks.goodNum > 0)
      .map((inItem) => {
        return {
          solitaireActivityId: actId.value,
          activityProductId: inItem.activityProductId,
          dominoState: inItem.dominoState,
          number: inItem.actStocks.goodNum,
          skuId: inItem.actStocks.id,
        }
      })
    if (!items.length) {
      uni.showToast({
        title: '请先选择商品',
        icon: 'none',
      })
      return
    }
    uni.navigateTo({
      url: `/modules/pages/submit/submit?items=${encodeURIComponent(JSON.stringify(items))}`,
    })
  }

  /**
   * 获取倒计时
   */
  function getCountDownInfo(item: { startTime: string; endTime: string }): CountDownResult {
    const dateNow = Date.now()
    const activityBeginTime = new Date(item.startTime.replace(/-/g, '/')).getTime()
    const activityEndTime = new Date(item.endTime.replace(/-/g, '/')).getTime()
    const aboutToBegin = dateNow < activityBeginTime // 将要开始
    const isBegin = dateNow >= activityBeginTime && dateNow < activityEndTime // 已开始
    const isEnd = dateNow >= activityEndTime // 已结束
    let toBeginTime = activityBeginTime - dateNow
    let toEndTime = activityEndTime - dateNow
    let remainTimeValue = 0 //
    let status = ''

    if (aboutToBegin) {
      status = 'aboutToBegin'
      remainTimeValue = toBeginTime
    }
    if (isBegin) {
      status = 'beginning'
      toBeginTime = 0
      remainTimeValue = toEndTime
    }
    if (isEnd) {
      status = 'ended'
      toEndTime = 0
      remainTimeValue = 0
      timeData.value = {
        hours: ['0', '0'],
        minutes: ['0', '0'],
        seconds: ['0', '0'],
      }
    }

    return {
      status,
      remainTime: remainTimeValue,
      toBeginTime,
      toEndTime,
    }
  }

  /**
   * 格式化时间
   * @param n
   */
  function formatNumber(n: Long): string {
    n = n.toString()
    return n[1] ? n : '0' + n
  }

  /**
   * 获取时间信息
   */
  function dealTime(options: CountDownResult) {
    if (options.status === 'aboutToBegin') {
      countDetail.status = 'aboutToBegin'
      countDetail.toBeginTime = 0
      countDetail.toEndTime = options.toEndTime - options.remainTime
    }

    if (options.status === 'beginning') {
      countDetail.status = 'beginning'
      countDetail.toBeginTime = 0
      countDetail.toEndTime = 0
    }

    if (options.status === 'ended') {
      countDetail.status = 'ended'
      countDetail.toBeginTime = 0
      countDetail.toEndTime = 0
    }

    if (options.timeData) {
      timeData.value = {
        hours: options.timeData.hours ? options.timeData.hours.split('') : ['0', '0'],
        minutes: options.timeData.minutes ? options.timeData.minutes.split('') : ['0', '0'],
        seconds: options.timeData.seconds ? options.timeData.seconds.split('') : ['0', '0'],
      }
    }
    remainTime.value = options.remainTime
    // showCountDown: options.remainTime ? true : false,
    showCountDown.value = true
  }

  /**
   * 倒计时变化
   * @param e
   */
  function handleChange(e: { days: number; hours: number; minutes: number; seconds: number }) {
    const { days, hours, minutes, seconds } = e
    const h = hours + days * 24
    // h = h > 99 ? 90 : h;
    const hoursStr = formatNumber(h)
    const minutesStr = formatNumber(minutes)
    const secondsStr = formatNumber(seconds)

    timeData.value = {
      hours: hoursStr.split(''),
      minutes: minutesStr.split(''),
      seconds: secondsStr.split(''),
    }

    if (h === 0 && minutes === 0 && seconds === 0) {
      activityList.value = []
      hasListMore.value = false
      if (actId.value) {
        getActivity(actId.value)
      }
    }
  }

  /**
   * 点击进入商品详情
   */
  function jumpDetail(e: { currentTarget: { dataset: { item: { id: Long } } } }) {
    return
    // 原函数中有return，表示此功能被禁用
    const id = e.currentTarget.dataset.item.id
    uni.navigateTo({
      url: `/pages/detail/detail?id=${id}`,
    })
  }

  return {
    // 返回状态
    activityDetail,
    activityList,
    countDetail,
    timeData,
    state,
    showVisible,
    currentItem,
    totalPrice,
    goodNum,
    buyQuery,
    joinList,
    page,
    size,
    hasMore,
    hasListMore,
    goodQuery,
    authType,
    options,
    actId,
    hasChainAct,
    bottomType,
    bottomHeight,
    showCountDown,
    remainTime,
    getActivitytype,

    // 返回方法
    getHeight,
    getPosition,
    getActivity,
    getListDetail,
    clickCopy,
    getBuyerRecord,
    getAllChain,
    showMore,
    clickSku,
    selectSku,
    onClose,
    increase,
    decrease,
    getTotalPrice,
    submit,
    getCountDownInfo,
    formatNumber,
    dealTime,
    handleChange,
    jumpDetail,
  }
}
