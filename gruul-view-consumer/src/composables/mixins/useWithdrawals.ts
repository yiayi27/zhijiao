import { ref, reactive } from 'vue'
import { getWithdrawalList as apiGetWithdrawalList } from '@/api/modules/finance'
import { groupBy } from 'lodash-es'

export interface WithdrawalItem {
  createTime: string
  groupTime?: string
  time?: string
  channel?: string
  amount?: Long
  auditStatus?: Long
  [key: string]: any
}

interface WithdrawalResponse {
  current: number
  pages: number
  list: WithdrawalItem[]
}

export function useWithdrawals() {
  // 状态
  const query = reactive({
    // 0->团长; 1->区域团长;2->提货点
    accountType: 'USER',
    current: 1,
    size: 10,
    keyword: '',
    // 对应用户id
    userId: '',
    // 0->全部记录；1->到账记录
    type: '0',
  })

  const isLast = ref(false)
  const group = ref<Record<string, WithdrawalItem[]>>({})
  const list = ref<WithdrawalItem[]>([])

  // 方法
  /** 触底加载更多 */
  function onReachBottom() {
    query.current++
    getWithdrawalList()
  }

  async function getWithdrawalList(queryParams = {}, refresh = false) {
    /** 如果已为最后一条记录，跳过请求 */
    if (isLast.value && !refresh) {
      return
    }

    try {
      const res = (await apiGetWithdrawalList({
        ...query,
        ...queryParams,
      })) as WithdrawalResponse

      const { current, pages, list: resultList } = res
      // 年-月分组
      const groupResult = groupBy(
        (current > 1 && !refresh ? [...list.value, ...resultList] : resultList).map((item) => {
          const [date, time] = item.createTime.split(' ')
          const [hour, min] = time.split(':')
          const [year, month, day] = date.split('-')
          item.groupTime = `${year}-${month}`
          item.time = `${month}-${day} ${hour}:${min}`
          return item
        }),
        (item) => {
          return [item.groupTime]
        },
      )

      /** 如果为最后一页设置到底状态 */
      if (current === pages) {
        isLast.value = true
      }

      group.value = groupResult
      list.value = resultList
    } catch (err) {
      uni.showToast({
        title: err instanceof Error ? err.message : '佣金数据获取失败',
        icon: 'none',
      })
    }
  }

  return {
    // 状态
    query,
    isLast,
    group,
    list,

    // 方法
    onReachBottom,
    getWithdrawalList,
  }
}
