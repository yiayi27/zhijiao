export * from '@/api/businessCenter/type'
export * from '@/api/sign/type'

export interface IRegionSelectParams {
  /** 请求服务权限标识 */
  key: string

  /** 查询关键字 */
  keywords: string

  /**
   * 子级行政区
   * 规则：设置显示下级行政区级数（行政区级别包括：国家、省/直辖市、市、区/县、乡镇/街道多级数据）
   * 可选值：0、1、2、3等数字，并以此类推
   * 0：不返回下级行政区；
   * 1：返回下一级行政区；
   * 2：返回下两级行政区；
   * 3：返回下三级行政区；
   */
  subdistrict: number

  /** 需要第几页数据 */
  page: number

  /** 返回结果控制 可选值：base、all */
  extensions: string
}
