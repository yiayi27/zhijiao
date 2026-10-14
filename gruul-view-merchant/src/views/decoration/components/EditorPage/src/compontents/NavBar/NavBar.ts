/**
 * 底部导航
 */
import type MenuList from './NavBarItem'

export default {
  /** 选择样式,1普通样式 2中间大图样式 */
  codeStyle: 1,

  /** 底部导航列表 */
  menuList: [] as MenuList[],

  /** 字体选中颜色  */
  selectColor: '#F64E3F',

  /** 字体未选择颜色 */
  defaultColor: '#7A7E83',
}

/* 获取默认icon列表 */
export function getDefaultIconList(): { def: string; act: string }[] {
  return [
    {
      def: 'https://oss-tencent.bgniao.cn/api/home_page.png',
      act: 'https://oss-tencent.bgniao.cn/api/home_page1.png',
    },
    {
      def: 'https://oss-tencent.bgniao.cn/api/shopping_mall1.png',
      act: 'https://oss-tencent.bgniao.cn/api/shopping_mall.png',
    },
    {
      def: 'https://oss-tencent.bgniao.cn/api/shopping_cart1.png',
      act: 'https://oss-tencent.bgniao.cn/api/shopping_cart.png',
    },
    {
      def: 'https://oss-tencent.bgniao.cn/api/my1.png',
      act: 'https://oss-tencent.bgniao.cn/api/my.png',
    },
    {
      def: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20210112/b2ef1106af8b4a6c9b22ee0afac6195c.png',
      act: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20210112/613afce069ca4cfdb980cd446e0195f1.png',
    },
  ]
}
