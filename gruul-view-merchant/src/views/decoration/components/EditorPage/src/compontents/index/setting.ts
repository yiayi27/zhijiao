import { defineAsyncComponent } from 'vue'

export default {
  HomeSwiper: defineAsyncComponent(() => import('../HomeSwiper/Setting.vue')),
  GoodSwiper: defineAsyncComponent(() => import('../GoodSwiper/Setting.vue')),

  Goods: defineAsyncComponent(() => import('../Goods/Setting.vue')),
  Search: defineAsyncComponent(() => import('../Search/Setting.vue')),
  TitleBar: defineAsyncComponent(() => import('../TitleBar/Setting.vue')),
  BlankPlaceholder: defineAsyncComponent(() => import('../BlankPlaceholder/Setting.vue')),
  Separator: defineAsyncComponent(() => import('../Separator/Setting.vue')),
  StoreNavigation: defineAsyncComponent(() => import('../StoreNavigation/Setting.vue')),
  NavBar: defineAsyncComponent(() => import('../NavBar/Setting.vue')),
  CubeBox: defineAsyncComponent(() => import('../CubeBox/Setting.vue')),
  RichText: defineAsyncComponent(() => import('../RichText/Setting.vue')),
  BusinessSuper: defineAsyncComponent(() => import('../../../../BusinessSuper/Setting.vue')),
  ImageCom: defineAsyncComponent(() => import('../ImageCom/Setting.vue')),
  VideoCom: defineAsyncComponent(() => import('../VideoCom/Setting.vue')),

  // --营销组件--
  Coupon: defineAsyncComponent(() => import('../Coupon/Setting.vue')),
  Chain: defineAsyncComponent(() => import('../Chain/Setting.vue')),
  HomeLive: defineAsyncComponent(() => import('../HomeLive/Setting.vue')),
}
