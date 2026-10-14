import api from '@/libs/request'

/**
 * 分页查询当前店铺通知
 */
export function toPostGetAdminPage(params: Record<string, any>): Promise<any> {
  return api.post('/platform/shop/admin/page', params)
}

/**
 * 新增店铺自定义管理员
 */
export function toPostShopAdminAdd(data: Record<string, any>) {
  return api.post('/platform/shop/admin/add', data)
}

/**
 * 编辑店铺自定义管理员
 */
export function toPostShopAdminEdit(data: Record<string, any>) {
  return api.post(`/platform/shop/admin/update`, data)
}

/**
 * 移除店铺管理员
 */
export function toPostShopAdminDelete(data: Record<string, any>) {
  return api.post(`/platform/shop/admin/delete`, data)
}

/**
 * 查询该手机号是否可绑定本店管理员
 */
export function toPostShopAdminAvailable(data: Record<string, any>) {
  return api.post(`/platform/shop/admin/available`, data)
}
