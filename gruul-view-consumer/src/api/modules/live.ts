import api from '@/utils/request'

// 获取直播列表
export const getLiveList = (data: any) => {
  return api.get('/live/api/get/list', { ...data })
}

// 点赞
export const like = (data: any) => {
  return api.get('/live/api/update/likeCount', { ...data })
}

// 点击进入直播间观看量加一
export const view = (data: any) => {
  return api.get('/live/api/update/viewCount', { ...data })
}
