import type { RouteRecordRaw } from 'vue-router'
import { MMain } from '@/router/base'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/overview',
    component: MMain,
    meta: {
      icon: 'iconjiankonggaikuang_huaban',
      title: '经营概况',
      islink: 1,
      permission: 'overview',
    },
    redirect: '/overview/index',
    children: [
      {
        path: 'index',
        name: 'OverViewIndex',
        meta: {
          title: '经营概况',
          noPadding: true,
          isShow: 1,
        },
        component: () => import('@/views/manage/Index.vue'),
      },
    ],
  },
  {
    path: '',
    component: MMain,
    meta: {
      icon: 'iconshangpinguanli',
      title: '商品管理',
    },
    redirect: '/overview/index',
    children: [
      {
        path: 'goods',
        meta: {
          title: '商品列表',
          show: true,
          permission: 'good:list',
        },
        children: [
          {
            path: '',
            name: 'GoodsList',
            component: () => import('@/views/goods/marketModel/Goods.vue'),
          },
          {
            path: 'addGood',
            name: 'AddGood',
            component: () => import('@/views/goods/marketModel/AddGood.vue'),
            meta: {
              title: '发布商品',
              show: true,
            },
          },
        ],
      },
      {
        path: 'goodRegion',
        meta: {
          title: '设置专区',
          show: true,
          permission: 'good:set:area',
        },
        children: [
          {
            path: '',
            name: 'GoodRegion',
            component: () => import('@/views/goods/goodManage/GoodRegion.vue'),
          },
          {
            path: 'class',
            name: 'GoodClass',
            component: () => import('@/views/goods/goodManage/GoodClass.vue'),
            meta: {
              title: '商品分类',
              show: true,
            },
          },
        ],
      },
      {
        path: 'attributeTemple',
        name: 'AttributeTemple',
        component: () => import('@/views/goods/goodManage/AttributeTemple.vue'),
        meta: {
          title: '属性模板',
          show: true,
          permission: 'good:attr:template',
        },
      },
      {
        path: 'supplier',
        name: 'SupplierManage',
        component: () => import('@/views/goods/goodManage/SupplierManage.vue'),
        meta: {
          title: '供货商',
          show: true,
          permission: 'good:supplier',
        },
      },
    ],
  },
  {
    path: '/order',
    component: MMain,
    meta: {
      title: '订单管理',
      icon: 'icondingdan',
    },
    children: [
      {
        path: 'delivery',
        meta: {
          title: '快递订单',
          show: true,
          permission: 'order:express:order',
        },
        children: [
          {
            path: '',
            name: 'OrderDelivery',
            component: () => import('@/views/order/DeliveryOrder.vue'),
          },
          {
            path: 'send',
            name: 'OrderDeliverySend',
            meta: {
              title: '批量发货',
              noPadding: true,
            },
            component: () => import('@/views/order/DeliverySend.vue'),
          },
        ],
      },
      {
        path: 'evaluation',
        name: 'OrderEvaluation',
        component: () => import('@/views/order/Evaluation.vue'),
        meta: {
          title: '评价管理',
          show: true,
          permission: 'order:comment:manager',
        },
      },
      {
        path: 'afterSale',
        name: 'OrderAfterSale',
        component: () => import('@/views/order/AfterSaleOrder.vue'),
        meta: {
          title: '售后工单',
          show: true,
          permission: 'order:afs:order',
        },
      },
      {
        path: 'store',
        name: 'OrderStore',
        component: () => import('@/views/order/Store.vue'),
        meta: {
          title: '储值订单',
          show: true,
          permission: 'order:value:order',
        },
      },
    ],
  },
  {
    path: '',
    component: MMain,
    meta: {
      title: '营销应用',
      icon: 'iconcangchuyingyong',
    },
    children: [
      {
        path: 'couponActivity',
        meta: {
          title: '优惠券',
          show: true,
          permission: 'marketing:coupon',
        },
        children: [
          {
            path: '',
            name: 'CouponIndex',
            component: () => import('@/views/marketing/couponActivity/CouponIndex.vue'),
          },
          {
            path: 'addCouponActivity',
            name: 'AddCouponActivity',
            component: () => import('@/views/marketing/couponActivity/addCouponActivity/AddActivity.vue'),
            meta: {
              title: '添加优惠券',
            },
          },
        ],
      },
      {
        path: 'fullReduction',
        meta: {
          title: '满减',
          show: true,
          permission: 'marketing:full:reduce',
        },
        children: [
          {
            path: '',
            name: 'FullIndex',
            component: () => import('@/views/marketing/fullReduction/FullIndex.vue'),
          },
          {
            path: 'addFull',
            name: 'FullRedAdd',
            component: () => import('@/views/marketing/fullReduction/fullReduceAdd/FullReduceAdd.vue'),
            meta: {
              title: '添加满减活动',
            },
          },
        ],
      },
      // {
      //   path: 'spike',
      //   meta: {
      //     title: '限时秒杀',
      //     isShow: 1,
      //   },
      //   children: [
      //     {
      //       path: '',
      //       name: 'SpikeIndex',
      //       component: () => import('@/views/marketing/spike/Index.vue'),
      //     },
      //     {
      //       path: 'add',
      //       name: 'SpikeAdd',
      //       component: () => import('@/views/marketing/spike/EditActivityAdd.vue'),
      //       meta: {
      //         title: '添加秒杀',
      //       },
      //     },
      //     {
      //       path: 'update',
      //       name: 'SpikeUpdate',
      //       component: () => import('@/views/marketing/spike/EditActivityUpdate.vue'),
      //       meta: {
      //         title: '编辑秒杀',
      //       },
      //     },
      //   ],
      // },
      {
        path: 'integralMall',
        meta: {
          title: '积分商城',
          show: true,
          permission: 'marketing:integral',
        },
        children: [
          {
            path: '',
            name: 'IntegralMallIndex',
            component: () => import('@/views/marketing/integralMall/Index.vue'),
          },
          {
            path: 'addCoupom',
            name: 'IssueCouponAdd',
            component: () => import('@/views/marketing/integralMall/integralPro/IssueCoupon.vue'),
            meta: {
              title: '添加积分优惠券',
            },
          },
          {
            path: 'addGood',
            name: 'IssueGoodAdd',
            component: () => import('@/views/marketing/integralMall/integralPro/IssueGood.vue'),
            meta: {
              title: '添加积分商品',
            },
          },
          {
            path: 'deliver',
            name: 'DeliverGoods',
            component: () => import('@/views/marketing/integralMall/integralOrder/DeliverGoods.vue'),
            meta: {
              title: '批量发货',
            },
          },
        ],
      },
      {
        path: 'live',
        meta: {
          title: '直播',
          show: true,
          permission: 'marketing:live',
        },
        children: [
          {
            path: '',
            name: 'LiveIndex',
            component: () => import('@/views/marketing/live/Index.vue'),
          },
        ],
      },
      {
        path: 'goodCommodity',
        meta: {
          title: '商品基金',
          show: true,
          permission: 'marketing:good:fund',
        },
        children: [
          {
            path: '',
            name: 'CommodityIndex',
            component: () => import('@/views/marketing/goodCommodity/Index.vue'),
          },
        ],
      },
      {
        path: 'recovery',
        meta: {
          title: '回收返券',
          show: true,
          permission: 'marketing:recycle:coupon',
        },
        children: [
          {
            path: '',
            name: 'RecoveryIndex',
            component: () => import('@/views/marketing/recovery/Index.vue'),
          },
        ],
      },
      {
        path: 'chainActivity',
        meta: {
          title: '社群接龙',
          show: true,
          permission: 'marketing:community',
        },
        children: [
          {
            path: '',
            name: 'ChainIndex',
            component: () => import('@/views/marketing/chainActivity/ChainIndex.vue'),
          },
          {
            path: 'addActivity',
            name: 'AddChainActivity',
            component: () => import('@/views/marketing/chainActivity/AddChainActivity.vue'),
            meta: {
              title: '接龙活动',
            },
          },
        ],
      },
    ],
  },
  {
    path: '/finance',
    component: MMain,
    meta: {
      title: '财务管理',
      icon: 'iconjinbi',
    },
    redirect: '/finance/index',
    children: [
      {
        path: 'index',
        name: 'FinanceIndex',
        component: () => import('@/views/finance/Index.vue'),
        meta: {
          title: '对账单',
          show: true,
          permission: 'finance:bill',
        },
      },
      {
        path: 'withdrawalTicket',
        name: 'FinanceWithdrawalTicket',
        component: () => import('@/views/finance/WithdrawalTicket.vue'),
        meta: {
          title: '提现工单',
          show: true,
          permission: 'finance:withdraw:order',
        },
      },
    ],
  },
  {
    path: '/customer',
    component: MMain,
    meta: {
      title: '客户管理',
      icon: 'iconkehu',
    },
    children: [
      {
        path: 'list',
        meta: {
          title: '普通会员',
          show: true,
          permission: 'customer:member',
        },
        children: [
          {
            path: '',
            name: 'CustomerList',
            component: () => import('@/views/customer/list/Index.vue'),
          },
          {
            path: 'setting',
            name: 'SettingMember',
            component: () => import('@/views/customer/list/components/SettingMember.vue'),
            meta: {
              title: '会员设置',
              isShow: 1,
              show: false,
            },
          },
        ],
      },
      {
        path: 'member',
        name: 'MemberList',
        component: () => import('@/views/customer/list/Member.vue'),
        meta: {
          title: '会员管理',
          show: true,
          permission: 'customer:member:manager',
        },
      },
      {
        path: 'blacklist',
        name: 'Blacklist',
        component: () => import('@/views/customer/blacklist/Index.vue'),
        meta: {
          title: '黑名单',
          show: true,
          permission: 'customer:blacklist',
        },
      },
    ],
  },
  {
    path: '/distribution',
    component: MMain,
    meta: {
      title: '配送方式',
      icon: 'icondaifahuo',
    },
    children: [
      {
        path: 'logistics',
        component: () => import('@/views/logistics/logistics/Index.vue'),
        meta: {
          title: '快递配送',
          show: true,
          permission: 'delivery:express',
        },
      },
    ],
  },
  {
    path: '/setting',
    component: MMain,
    meta: {
      title: '商城设置',
      icon: 'iconshangpinxiangqing-dianpu',
    },
    redirect: '/setting/index',
    children: [
      {
        path: 'editorPage',
        redirect: '/editorPage',
        meta: {
          title: '装修',
          show: true,
          permission: 'mall:decoration',
        },
      },
      {
        path: 'index',
        name: 'SettingIndex',
        component: () => import('@/views/setting/Index.vue'),
        meta: {
          title: '通用设置',
          show: true,
          permission: 'mall:common:setting',
        },
      },
      {
        path: 'perm',
        name: 'PermIndex',
        meta: {
          title: '权限管理',
          show: true,
          permission: 'mall:permission',
        },
        children: [
          {
            name: 'permPage',
            path: '',
            component: () => import('@/views/perm/Index.vue'),
          },
        ],
      },
    ],
  },
  {
    path: '/editorPage',
    name: 'EditorPage',
    component: () => import('@/views/decoration/components/EditorPage/src/Editor.vue'),
    meta: {
      title: '装修',
      isShow: 1,
      permission: 'mall:decoration',
    },
  },
  {
    path: '/business',
    component: MMain,
    meta: {
      title: '商家中心',
      isShow: 1,
    },
    children: [
      {
        path: '',
        name: 'BusinessCenter',
        component: () => import('@/views/businessCenter/Index.vue'),
        meta: {
          title: '',
        },
      },
    ],
  },
  {
    path: '/logistics',
    component: MMain,
    meta: {
      title: '物流管理',
      isShow: 1,
    },
    children: [
      {
        path: 'logistics',
        name: 'LogisticsIndex',
        component: () => import('@/views/logistics/logistics/Index.vue'),
        meta: {
          title: '快递配送',
          permission: 'delivery:express',
        },
      },
    ],
  },
  {
    path: '/CategoryTem',
    component: MMain,
    meta: {
      title: '商品属性',
      isShow: 1,
    },
    children: [
      {
        path: '',
        name: 'CategoryTem',
        component: () => import('@/views/goods/goodManage/components/CategoryTem.vue'),
        meta: {
          title: '商品属性',
        },
      },
    ],
  },
]

export default routes
