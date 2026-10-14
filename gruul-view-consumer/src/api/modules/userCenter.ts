import api from '@/utils/request'

// 获取用户信息 聚合接口
export const getAggregate = () => {
  return api.get('/account/aggregate/more', {}, { cache: true })
}
