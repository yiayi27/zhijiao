interface Lives {
  id: number
  /** 直播名称 */
  name: string
  /** 图片 */
  img: string
}

/**
 * 分隔符
 */
export default {
  /** 列表标题 */
  title: '直播',

  /** 直播数量 */
  count: 4,

  /** 列表样式 1大图模式 2一行两个 3横向滑动 */
  listStyle: 3,

  /** 页面边距 */
  pagePadding: 8,

  /** 上下间距 */
  goodsPadding: 8,

  /** 图片倒角 */
  doodsAngle: 2,

  Lives: {
    id: 1,
    name: '直播标题',
    img: 'https://dss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=2203762303,14815638&fm=26&gp=0.jpg',
  } as Lives,

  /** 直播列表 */
  gList: [
    {
      id: 1,
      name: '直播标题',
      img: 'https://dss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=2203762303,14815638&fm=26&gp=0.jpg',
    },
    {
      id: 2,
      name: '直播标题',
      img: 'https://dss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=2203762303,14815638&fm=26&gp=0.jpg',
    },
  ] as Lives[],
}
