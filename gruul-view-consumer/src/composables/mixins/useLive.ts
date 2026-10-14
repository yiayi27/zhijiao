import { ref, onUnmounted } from 'vue'
import { getLiveList, like, view } from '@/api/modules/live'
import { cloneDeep } from 'lodash-es'

declare function requirePlugin(name: string): any

interface LiveItem {
  roomId: Long
  id: Long
  clickLike: boolean
  likeCount: number
  status: number
  viewCount?: number
  shareImg?: string
  liveName?: string
  anchor?: string
}

interface LivePlayer {
  getLiveStatus: (params: { room_id: Long }) => Promise<{ liveStatus: number }>
}

export function useLive() {
  // 初始化 livePlayer 插件
  const livePlayer = requirePlugin('live-player-plugin') as LivePlayer

  // 状态
  const liveList = ref<LiveItem[]>([])
  const current = ref(1)
  const total = ref(0)
  const isFetch = ref(false)
  const loading = ref(false)

  // 定时器句柄
  let timer: NodeJS.Timeout | null = null

  // 方法
  // 跳转直播间
  function onGotoLive(e: { currentTarget: { dataset: { room_id: Long; id: Long; count: Long } } }) {
    const roomId = e.currentTarget.dataset.room_id
    const id = e.currentTarget.dataset.id
    let viewCount = e.currentTarget.dataset.count
    try {
      viewCount = parseInt(String(viewCount), 10) + 1
    } catch (err) {
      viewCount = 1
      console.log(err)
    }
    view({ id, viewCount }).then(() => fetchLiveList())
    uni.navigateTo({
      url: `plugin-private://wx2b03c6e691cd7370/pages/live-player-plugin?room_id=${roomId}`,
    })
  }

  // 点赞
  function onClickLike(e: { currentTarget: { dataset: { room_id: Long; id: Long; count: Long } } }) {
    const roomId = e.currentTarget.dataset.room_id
    const id = e.currentTarget.dataset.id
    let likeCount = e.currentTarget.dataset.count
    try {
      likeCount = parseInt(String(likeCount), 10) + 1
    } catch (err) {
      console.log(err)
      likeCount = 1
    }
    const liveListValue = liveList.value
    liveListValue.forEach((v) => {
      if (String(v.roomId) === String(roomId)) {
        v.clickLike = true
      }
    })
    liveList.value = liveListValue
    setTimeout(() => {
      liveListValue.forEach((v) => {
        if (String(v.roomId) === String(roomId)) {
          v.clickLike = false
          v.likeCount = likeCount
        }
      })
      liveList.value = liveListValue
      like({ id, likeCount })
    }, 400)
  }

  /* 获取直播列表 */
  async function fetchLiveList(currentPage = 1, isMore = false) {
    isFetch.value = isMore ? false : true
    loading.value = isMore ? true : false
    const res = await getLiveList({
      current: currentPage,
      size: 20,
    })
    liveList.value = isMore ? [...liveList.value, ...(res.list || [])] : res.list
    current.value = res.current
    total.value = res.total
    isFetch.value = false
    loading.value = false
    setTimeout(() => {
      updateStatus()
      if (timer) {
        clearInterval(timer)
      }
      timer = setInterval(updateStatus, 1000 * 60)
    }, 100)
  }

  // 更新直播状态
  async function updateStatus() {
    const liveListValue = cloneDeep(liveList.value)
    const asyncArrs = liveListValue.map((v) => {
      return livePlayer.getLiveStatus({ room_id: v.roomId })
    })
    const arrRes = await Promise.all(asyncArrs)
    liveList.value = cloneDeep(
      liveListValue.map((v, i) => {
        v.status = arrRes[i].liveStatus
        return v
      }),
    )
  }

  // 组件销毁时清除定时器
  onUnmounted(() => {
    if (timer) {
      clearInterval(timer)
      timer = null
    }
  })

  return {
    // 状态
    liveList,
    current,
    total,
    isFetch,
    loading,
    timer,

    // 方法
    onGotoLive,
    onClickLike,
    fetchLiveList,
    updateStatus,
  }
}
