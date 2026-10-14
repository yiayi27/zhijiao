import type { LinkSelectItem } from '@/components/LinkSelect/type'
import { getDefaultLinkData } from '@/components/LinkSelect/constants'

export interface SwiperListItem {
  title: string
  img: string
  link: LinkSelectItem
  linkName: string
}

/**
 * 轮播图
 */
export default {
  type: 'HomeSwiper',

  swiperList: [] as SwiperListItem[],

  padding: 14,

  /** 图片样式 1常规 2投影 */
  imageStyle: 1,

  /** 图片倒角 1直角 2圆角 */
  imageAngle: 1,

  /** 指示器 1样式一 2样式二 3样式三 */
  indicator: 1,

  /** 图片高度 */
  height: 185,
}

export function getDefualtSwiperItem(defaultData: object = {}): SwiperListItem {
  return {
    title: '',
    img: '',
    link: getDefaultLinkData(),
    linkName: '',
    ...defaultData,
  }
}
