import api from '@/utils/request'

/**
 * 获取用户地址
 * @param data 1-获取所有 2-获取默认地址,默认type=1
 */
export const getAddressList = (data: { type: number } = { type: 1 }) => {
  return api.get('/account/mini-account-address', data)
}

/**
 * 更新用户地址|设置为默认
 * @param data 用户地址数据 + id
 */
export const updateAddress = (data: string) => {
  return api.put('/account/mini-account-address', data, {
    'Content-Type': 'application/json',
  })
}

/**
 * 删除用户地址
 * @param adderssId adderssId: 地址ID, Long
 */
export const deleteAddress = (adderssId: Long) => {
  return api.delete(`/account/mini-account-address/${adderssId}`, {})
}

/**
 * 添加用户地址
 * @param data 用户地址数据
 */
export const addAddress = (data: string) => {
  return api.post('/account/mini-account-address', data, {
    'Content-Type': 'application/json',
  })
}

/**
 * 获取省市区
 * @param data id:父级id,string;type:获取类型 1-省 2-市 3-区,1|2|3;
 */
export const getArea = (data: { id: string; type: 1 | 2 | 3 }) => {
  return api.get('/account/mini-account-address/area', data, {
    'Content-Type': 'application/json',
  })
}

/**
 * 获取经纬度
 * @param data site:详细地址,string
 */
export const getLatAndLon = (data: { site: string }) => {
  return api.get('/account/mini-account-address/latitude-longitude', data)
}
