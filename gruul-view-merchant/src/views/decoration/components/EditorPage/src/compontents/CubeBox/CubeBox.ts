import type { LinkSelectItem } from '@/components/LinkSelect/type'

interface IBanners {
  x: number
  y: number
  width: number
  height: number
  img: string
  link: LinkSelectItem
  linkName: string
}

export default {
  borderWidth: 0, // 图片间隙

  layoutWidth: 2, // 列数

  layoutHeight: 1, // 行数

  showMethod: 0, // 行数index

  pageMargin: 0, // 页面边距离

  width: 2, // 展示个数

  subEntry: [
    {
      x: 0,
      y: 0,
      width: 1,
      height: 1,
      img: '',
      link: {
        id: null,
        type: null,
        name: '',
        url: '',
        append: '',
      },
      linkName: '',
    },
    {
      x: 1,
      y: 0,
      width: 1,
      height: 1,
      img: '',
      link: {
        id: null,
        type: null,
        name: '',
        url: '',
        append: '',
      },
      linkName: '',
    },
  ] as IBanners[], // 图片数组
}
