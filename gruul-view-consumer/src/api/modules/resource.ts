import api from '@/utils/request'

// 供应商申请
export const supApply = (data: any) => {
  return api.post('/goods/api/supplier/save', data)
}
