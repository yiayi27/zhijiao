import type { RouteRecordRaw } from 'vue-router'
import { MManageMain } from '@/router/base'

export const consoleRouters: Array<RouteRecordRaw> = [
  {
    path: '/console',
    component: MManageMain,
    meta: {
      title: '控制台',
      isShow: 1,
    },
    children: [
      {
        path: '',
        name: 'ConsoleHome',
        meta: {
          title: '控制台',
        },
        component: () => import('@/views/console/home/Index.vue'),
      },
      {
        path: 'create',
        name: 'ConsoleCreate',
        meta: {
          title: '创建店铺',
        },
        component: () => import('@/views/console/create/Index.vue'),
      },
    ],
  },
  {
    path: '/console/protocol',
    name: 'ConsoleProtocol',
    component: () => import('@/views/console/static/Protocol.vue'),
    meta: {
      title: '注册协议',
      isShow: 1,
    },
  },
  {
    path: `/console/privacy`,
    name: 'ConsolePrivacy',
    component: () => import('@/views/console/static/Privacy.vue'),
    meta: {
      title: '隐私政策',
      isShow: 1,
    },
  },
  {
    path: `/console/order`,
    name: 'ConsoleOrder',
    component: () => import('@/views/console/static/Order.vue'),
    meta: {
      title: '订购及服务协议',
      isShow: 1,
    },
  },
  {
    path: `/console/register`,
    name: 'ConsoleRegister',
    component: () => import('@/views/console/static/Register.vue'),
    meta: {
      title: '开户及服务协议',
      isShow: 1,
    },
  },
]
