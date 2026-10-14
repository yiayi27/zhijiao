export const MManageMain = () => import('@/layout/manage/MManageMain.vue')

export const MMain = () => import('@/layout/MMain.vue')

// 404 on a page
export const ErrorPageRoute = {
  path: '/:path(.*)*',
  name: 'ErrorPageParent',
  component: MMain,
  meta: {
    title: 'ErrorPage',
    hideBreadcrumb: true,
  },
  children: [
    {
      path: '/:path(.*)*',
      name: 'ErrorPage',
      component: import('@/views/sign/404.vue'),
      meta: {
        title: 'ErrorPage',
        hideBreadcrumb: true,
      },
    },
  ],
}
