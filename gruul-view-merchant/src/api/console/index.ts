import api from '@/libs/request'

/**
 * 控制台拥有的店铺
 */
export function getShopList(data: any) {
  return api.get('/platform/shop/console/list', data)
}

/**
 * 控制台创建店铺
 * @query: Dto -->  (必须)
 */
export function createShop(data: any) {
  return api.post('/platform/shop/console/create', data, {
    headers: { 'Content-Type': 'application/json' },
  })
}

/**
 * 控制台删除店铺
 * @query: Dto -->  (必须)
 * @param id
 */
export function deleteShop(id: number) {
  return api.delete(`/platform/shop/console/del/${id}`, {})
}
