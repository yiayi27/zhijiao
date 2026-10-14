<script setup lang="ts">
import type { RouteRecordRaw } from 'vue-router'
import { upDateAsyncRouterMap } from '@/api/sign'
import { useMenuStore } from '@/store/modules/menu'
import { pick } from 'lodash-es'
import { storeToRefs } from 'pinia'

import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

// 定义组件名称
defineOptions({
  name: 'MMenu',
})

// 定义属性
defineProps({
  userInfo: {
    type: Object,
    default: () => ({}),
  },
})

// 自定义路由项接口
interface CustomRoute {
  path: string
  meta: {
    title?: string
    icon?: string
    isShow?: number
    show?: boolean
    islink?: boolean
    [key: string]: any
  }
  children?: CustomRoute[]
  name?: string
}

const routeInfo = useRoute()

// 响应式状态
const localRoutes = ref<CustomRoute[]>([])
const itemActiveIndex = ref<number | null>(null)
const isEditMode = ref(false)
const itemSubTop = ref(-1)
const isScrolling = ref(false)
const currentRouteShow = ref<CustomRoute | null>(null)
const floatmenuBoxRef = ref<HTMLElement | null>(null)

// 用于存储 DOM 引用的 Map
const menuItemRefs = new Map()
const itemSubRefs = new Map()

// 设置引用函数
function setMenuItemRef(el: any | null, index: number) {
  if (el) menuItemRefs.set(index, el)
}

function setItemSubRef(el: any | null, index: number) {
  if (el) itemSubRefs.set(index, el)
}

// 计算属性
const currentRoute = computed(() => routeInfo.path)
const { routersId, routers } = storeToRefs(useMenuStore())

// 滚动相关变量
let scrollVal: any = null
let timer: ReturnType<typeof setTimeout> | null = null

// 方法
function getRouteData() {
  localRoutes.value = handleRouteData(routers.value as unknown as CustomRoute[])
}

function handleRouteData(routes: CustomRoute[]) {
  return routes.map((item) => {
    const newItem = { ...item }
    if (newItem.children) {
      newItem.children = handleRouteData(newItem.children)
    }
    return pick(newItem, ['path', 'meta', 'name', 'children']) as CustomRoute
  })
}

function isCurrent(path: string, item: CustomRoute) {
  const { matched } = routeInfo
  if (currentRoute.value === path || (matched[1] && matched[1].path === path)) {
    currentRouteShow.value = item
    return true
  }
  return false
}

function isCurrent2(path: string) {
  const { matched } = routeInfo

  // 检查匹配路由
  if (matched[1] && matched[1].path === path) return true

  // 检查当前路由
  if (currentRoute.value === path) return true

  // 检查特定路径
  return path.includes('/copartner/order/') && path.includes('/copartner/setting/') && currentRoute.value.includes(path)
}

function toggleEditMode() {
  isEditMode.value = !isEditMode.value
  if (!isEditMode.value) {
    const routesData = JSON.stringify(localRoutes.value)
    upDateAsyncRouterMap({
      id: routersId.value,
      properties: routesData,
    }).then(() => {
      ElMessage.success('保存成功')
    })
  }
}

function checkItem() {
  for (const route of localRoutes.value) {
    if (route.children && route.children.length) {
      for (const child of route.children) {
        if (child.meta?.show) {
          return true
        }
      }
    }
  }
  return false
}

function onSelect(item: CustomRoute, index: number) {
  if (item.meta) {
    item.meta.show = !item.meta.show
  }
  if (itemActiveIndex.value !== null && localRoutes.value[itemActiveIndex.value]?.children) {
    showFloatBox(index, localRoutes.value[itemActiveIndex.value].children || [])
  }
}

function hideLength(items: CustomRoute[] = []) {
  return items.filter((item) => !item.meta?.show && !item.meta?.isShow).length
}

function parentIsActive(items: CustomRoute[], path: string) {
  return (
    items.filter((item) => {
      return isCurrent(`${path}/${item.path}`, item)
    }).length > 0
  )
}

function showFloatBox(index: number, arr: CustomRoute[] = []) {
  if (isScrolling.value) {
    return
  }

  if (arr && hideLength(arr)) {
    itemActiveIndex.value = index
    const menuItem = menuItemRefs.get(index)

    if (menuItem) {
      const clientHeight = document.documentElement.clientHeight
      const wrapHeight = clientHeight - 88
      const itemHeight = 38 + 40 * hideLength(arr)
      const sideNavWrap = document.querySelector('.side-nav-wrap-main') as HTMLElement
      const scrollTop = sideNavWrap ? sideNavWrap.scrollTop : 0
      const domToclientHeight = menuItem.offsetTop - scrollTop
      const dValue = wrapHeight - (domToclientHeight + itemHeight)

      if (dValue < 0) {
        itemSubTop.value = wrapHeight - itemHeight
      } else {
        itemSubTop.value = domToclientHeight
      }
    }
  } else {
    hideFloatBox()
  }
}

function hideFloatBox() {
  itemActiveIndex.value = null
}

function onScroll(val: any) {
  if (timer) clearTimeout(timer)
  if (!isScrolling.value) {
    isScrolling.value = true
  }
  hideFloatBox()
  timer = setTimeout(() => {
    if (val === scrollVal) {
      isScrolling.value = false
    }
  }, 500)
  scrollVal = val
}

function onMouseLeave() {
  if (!isEditMode.value) {
    hideFloatBox()
  }
}

// 监听编辑模式变化
watch(
  () => isEditMode.value,
  (newVal) => {
    if (newVal) {
      if (!checkItem()) {
        showFloatBox(1, localRoutes.value[1]?.children as CustomRoute[])
      }
    } else {
      hideFloatBox()
    }
  },
)

// 生命周期钩子
onMounted(() => {
  getRouteData()

  document.addEventListener('click', handleDocumentClick)
  document.addEventListener('mouseover', handleDocumentMouseover)
})

onBeforeUnmount(() => {
  document.removeEventListener('click', handleDocumentClick)
  document.removeEventListener('mouseover', handleDocumentMouseover)
  if (timer) clearTimeout(timer)
})

// 文档事件处理
function handleDocumentClick(e: MouseEvent) {
  const asideWrap = document.querySelector('.aside-wrap')
  if (asideWrap && !asideWrap.contains(e.target as Node) && isEditMode.value) {
    toggleEditMode()
  }
}

function handleDocumentMouseover(e: MouseEvent) {
  if (!isEditMode.value && document.querySelector('.aside-wrap') && !document.querySelector('.aside-wrap')?.contains(e.target as Node)) {
    hideFloatBox()
  }
}
</script>

<template>
  <div class="aside-wrap">
    <div class="admin__aside--shop">
      <slot name="dropmenu" />
      <div class="iconfont iconweixiubeijing cursor" @click="toggleEditMode" />
    </div>
    <div class="side-nav-wrap">
      <div class="side-nav-wrap-main">
        <div class="admin__menu" @scroll="onScroll">
          <template v-for="(route, i) in localRoutes" :key="route.path + i">
            <template v-if="route.meta.isShow !== 1">
              <template v-if="!route.meta.islink && route.children">
                <div
                  :ref="(el) => setMenuItemRef(el, i)"
                  class="admin__menu--item"
                  :class="[
                    {
                      active: parentIsActive(route.children, route.path) && !isEditMode,
                      bgf8: itemActiveIndex === i,
                    },
                  ]"
                >
                  <div
                    :ref="(el) => setItemSubRef(el, i)"
                    class="item--title"
                    :class="currentRouteShow && currentRouteShow.meta.show ? 'noActive' : ''"
                    @mouseover="showFloatBox(i, route.children)"
                  >
                    <span>
                      <i :class="`iconfont ${route.meta.icon}`" />
                      {{ route.meta.title }}
                    </span>
                    <i
                      v-show="hideLength(route.children)"
                      class="iconfont iconicon-arrow-right2 f14"
                      :class="currentRouteShow && currentRouteShow.meta.show ? 'black' : ''"
                    />
                  </div>
                  <div class="item__sub--menu" @mouseover="onMouseLeave">
                    <template v-for="sRoute in route.children" :key="`${route.path}/${sRoute.path}`">
                      <div v-if="!sRoute.meta.isShow && sRoute.meta.show" class="sub--item pt20" :index="`${route.path}/${sRoute.path}`">
                        <router-link
                          v-if="!isEditMode"
                          :path="sRoute.path"
                          :target="sRoute.path === 'editorPage' ? '_blank' : '_self'"
                          :to="`${route.path}${sRoute.path ? `/${sRoute.path}` : ''}`"
                          :class="{
                            active: isCurrent2(`${route.path}/${sRoute.path}`),
                          }"
                        >
                          {{ sRoute.meta.title }}
                        </router-link>
                        <a v-else href="javascript:;" @click.stop="onSelect(sRoute, i)">
                          {{ sRoute.meta.title }}
                          <i class="iconfont iconyoujiantou" />
                        </a>
                      </div>
                    </template>
                  </div>
                  <div v-show="!!itemActiveIndex" class="item--mask" />
                </div>
              </template>
              <template v-else>
                <div class="admin__menu--item" :class="{ active: route.path === $route.path && !isEditMode }">
                  <div class="item--title">
                    <router-link v-if="!isEditMode" :to="route.path">
                      <span>
                        <i :class="`iconfont ${route.meta.icon}`" />
                        {{ route.meta.title }}
                      </span>
                    </router-link>
                    <a v-else href="javascript:;">
                      <span>
                        <i :class="`iconfont ${route.meta.icon}`" />
                        {{ route.meta.title }}
                      </span>
                    </a>
                  </div>
                </div>
              </template>
            </template>
          </template>
        </div>
      </div>
      <div
        ref="floatmenuBoxRef"
        class="floatmenuBox item--modal"
        :class="[itemActiveIndex && hideLength(localRoutes[itemActiveIndex]?.children) ? 'active_v' : '']"
        :style="{ top: `${itemSubTop}px` }"
        @mouseleave="onMouseLeave"
      >
        <div v-if="itemActiveIndex !== null && localRoutes[itemActiveIndex]?.children">
          <template v-for="(sRoute, sIndex) in localRoutes[itemActiveIndex].children" :key="sIndex">
            <div v-if="!sRoute.meta.isShow && !sRoute.meta.show" class="modal--item" :index="`${localRoutes[itemActiveIndex].path}/${sRoute.path}`">
              <router-link
                v-if="!isEditMode"
                :path="sRoute.path"
                :target="sRoute.path === 'editorPage' ? '_blank' : '_self'"
                :to="`${localRoutes[itemActiveIndex].path}/${sRoute.path}`"
                :class="{
                  active: isCurrent2(`${localRoutes[itemActiveIndex].path}/${sRoute.path}`),
                }"
              >
                {{ sRoute.meta.title }}
              </router-link>
              <a v-else href="javascript:;" @click.stop="onSelect(sRoute, itemActiveIndex)">
                <i class="iconfont iconzuojiantou" />
                {{ sRoute.meta.title }}
              </a>
            </div>
          </template>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@import 'styles/m-layout.scss';
@import 'styles/new_mmenu.scss';

.black {
  color: #676767 !important;
}
</style>
