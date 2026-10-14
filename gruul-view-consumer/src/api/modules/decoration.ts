import api from '@/utils/request'

// // 获取自定义数据
// export const getProperties = (data: any) => {
//   return api.post("/shops/renovation/template/page/assembly/list", {
//     ...data,
//   });
// };
/** 获取页面装修数据 */
export const getAssembly = (data: any) => {
  return api.post('/shops/renovation/template/page/assembly/list', data)
}

/** 装修聚合接口 */
export const getDecorationAll = () => {
  return api.get('/shops/renovation/template/def/one', null, {
    cache: true,
    duration: 1000,
  })
}

/**
 * 获取所有专区
 */
export const getAllRegionList = (data: any) => {
  return api.get(`/goods/manager/sale/mode/get/all`, data)
}
