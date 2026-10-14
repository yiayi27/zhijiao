<script setup lang="ts">
import type { IShopInfo, IUserInfo } from '@/api/type'
import { toPostLogout } from '@/api/sign'
import MLayout from '@/layout/MLayout.vue'
import MMenu from '@/layout/MMenu.vue'
import { logout, modifySignStatus } from '@/libs'
import { useUserStore } from '@/store/modules/user'
import { ArrowRight, SwitchButton } from '@element-plus/icons-vue'

import { useRoute, useRouter } from 'vue-router'

// 路由和状态初始化
const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const breadcrumb = computed(() => {
  return route.matched
    .map((route) => ({
      title: route.meta.title,
      path: route.path,
    }))
    .filter((item) => !!item.title)
})

// 用户信息计算属性
const userInfo = computed<IUserInfo>(() => {
  return userStore.userInfo as IUserInfo
})

// 店铺信息计算属性
const shopInfo = computed<IShopInfo>(() => {
  return userStore.shopInfo as IShopInfo
})

// 命令处理映射表
const commandActions: Record<string, () => Promise<void> | void> = {
  // 退出登录
  logout() {
    toPostLogout()
    modifySignStatus('')
    logout()
    router.replace('/sign')
  },
  // 账号信息
  async setting() {
    await router.push('/business')
  },
  // 控制台
  async console() {
    await router.replace('/console')
  },
  // 官网
  index() {
    const baseUrl = import.meta.env.VITE_APP_BASEURL || ''
    const url = baseUrl.replace(/\/api/, '')
    window.open(url, '_top')
    return Promise.resolve()
  },
  // 订购
  async order() {
    await router.push('/meal')
  },
  // 升级/续费
  async update() {
    await router.push('/meal/update')
  },
}

// 处理下拉菜单命令 - 使用策略模式代替多个if
async function commandHandle(command: string) {
  if (command in commandActions) {
    await commandActions[command]()
  }
}

// 跳转到商店设置
function goChange() {
  router.push({
    path: '/setting',
    query: { tabName: 'Store' },
  })
}
</script>

<template>
  <MLayout :breadcrumb="breadcrumb" :user-info="userInfo">
    <template #breadcrumb>
      <el-breadcrumb separator="/" class="m__breadcrumb">
        <el-breadcrumb-item
          v-for="(item, i) of breadcrumb"
          :key="item.path"
          class="m__breadcrumb--item"
          :class="[{ 'm__breadcrumb--last': i !== breadcrumb.length - 1 }]"
          :to="i !== breadcrumb.length - 1 && i !== 0 ? { path: item.path } : { path: '/' }"
        >
          {{ item.title }}
        </el-breadcrumb-item>
      </el-breadcrumb>
    </template>

    <template #aside>
      <div class="side-nav-main">
        <div class="side-nav">
          <MMenu :user-info="userInfo">
            <template #dropmenu>
              <el-dropdown placement="bottom-start" @command="commandHandle">
                <div class="lineFlex">
                  <img :src="shopInfo.logoUrl" class="shop--logo" alt="商店logo" @click.stop="goChange" />
                  <div class="shop--name">
                    <span class="el-dropdown-link">
                      <span>{{ shopInfo.shopName }}</span>
                    </span>
                  </div>
                </div>

                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="console">
                      <div class="dorp-cell">
                        <span>控制台</span>
                      </div>
                    </el-dropdown-item>
                    <el-dropdown-item command="setting">
                      <div class="dorp-cell">
                        <span>商家中心</span>
                        <span class="c6">{{ userInfo.nikeName }}</span>
                        <el-icon>
                          <ArrowRight />
                        </el-icon>
                      </div>
                    </el-dropdown-item>
                    <el-dropdown-item command="logout">
                      <div class="dorp-cell noborder">
                        <span>退出登录</span>
                        <el-icon>
                          <SwitchButton />
                        </el-icon>
                      </div>
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
          </MMenu>
        </div>
      </div>
    </template>

    <template #view>
      <router-view>
        <template #default="{ Component, route: routeProps }">
          <transition name="fade" appear mode="out-in">
            <component :is="Component" :key="routeProps.fullPath" />
          </transition>
        </template>
      </router-view>
    </template>
  </MLayout>
</template>

<style lang="scss" scoped>
@import 'styles/m-layout.scss';
@import 'styles/new_mmenu.scss';

.lineFlex {
  display: flex;
  align-items: center;
  height: 60px;
  cursor: pointer;
}

.m__breadcrumb {
  &--item {
    .is-link {
      font-weight: 400 !important;
      color: #606266 !important;
      cursor: pointer;
    }

    &:last-child .el-breadcrumb__inner {
      font-weight: 700 !important;
      text-decoration: none;
      transition: color 0.2s cubic-bezier(0.645, 0.045, 0.355, 1);
      color: #303133;
    }
  }
}

.el-dropdown-link {
  display: flex;
  height: 50px;
  align-items: center;
}
</style>
