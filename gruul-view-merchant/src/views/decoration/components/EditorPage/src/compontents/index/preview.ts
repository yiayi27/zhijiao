import { defineAsyncComponent } from 'vue'

export default {
  HomeSwiper: defineAsyncComponent(() => import('../HomeSwiper/Preview.vue')),
  GoodSwiper: defineAsyncComponent(() => import('../GoodSwiper/Preview.vue')),
  Search: defineAsyncComponent(() => import('../Search/Preview.vue')),
  Goods: defineAsyncComponent(() => import('../Goods/Preview.vue')),
  TitleBar: defineAsyncComponent(() => import('../TitleBar/Preview.vue')),
  BlankPlaceholder: defineAsyncComponent(() => import('../BlankPlaceholder/Preview.vue')),
  Information: defineAsyncComponent(() => import('../Information/Preview.vue')),
  Separator: defineAsyncComponent(() => import('../Separator/Preview.vue')),
  StoreNavigation: defineAsyncComponent(() => import('../StoreNavigation/Preview.vue')),
  NavBar: defineAsyncComponent(() => import('../NavBar/Preview.vue')),
  Mall: defineAsyncComponent(() => import('../Mall/Preview.vue')),
  Live: defineAsyncComponent(() => import('../Live/Preview.vue')),
  CubeBox: defineAsyncComponent(() => import('../CubeBox/Preview.vue')),
  RichText: defineAsyncComponent(() => import('../RichText/Preview.vue')),
  BusinessSuper: defineAsyncComponent(() => import('../../../../BusinessSuper/Preview.vue')),
  ImageCom: defineAsyncComponent(() => import('../ImageCom/Preview.vue')),
  VideoCom: defineAsyncComponent(() => import('../VideoCom/Preview.vue')),

  // --营销组件--
  Coupon: defineAsyncComponent(() => import('../Coupon/Preview.vue')),
  Chain: defineAsyncComponent(() => import('../Chain/Preview.vue')),
  HomeLive: defineAsyncComponent(() => import('../HomeLive/Preview.vue')),
}
