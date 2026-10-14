<script setup lang="ts">
import { toPostLogout } from '@/api/sign'
import config from '@/config'
import MLayout from '@/layout/manage/MManageLayout.vue'
import { logout, modifySignStatus } from '@/libs'
import { MoreFilled, Picture } from '@element-plus/icons-vue'

import { useRouter } from 'vue-router'

const router = useRouter()

// 数据
const logo = ref(config.logo)

// 方法
function linkTo() {
  router.replace('/console')
}

function goPolice() {
  window.open(`http://www.beian.gov.cn/portal/registerSystemInfo?recordcode=33020502000496`)
}

function commandHandle(command: string) {
  // 退出登录
  if (command === 'logout') {
    toPostLogout()
    modifySignStatus('')
    logout()
    router.replace('/sign')
  }
}
</script>

<template>
  <div>
    <MLayout>
      <template #logo>
        <el-image class="admin__header--logo cursor-pointer" :src="logo" @click="linkTo">
          <template #error>
            <div class="image-slot">
              <el-icon>
                <Picture />
              </el-icon>
            </div>
          </template>
        </el-image>
      </template>
      <template #user>
        <div class="admin__header--user">
          <el-dropdown trigger="click" @command="commandHandle">
            <el-icon>
              <MoreFilled />
            </el-icon>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout"> 退出 </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </template>
      <template #content>
        <router-view class="admin__main--view" />
      </template>
      <template #footer>
        <footer class="admin__footer">
          <p class="medusa__rights">Copyright © 2020 启山智软. All Rights Reserved 宁波启山科技有限公司版权所有</p>
          <p class="flex items-center justify-center">
            <img style="width: 14px; height: 14px" src="https://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/gruul/20200321/gongan.jpg" />
            <span class="pointer" @click="goPolice">浙公网安备33020502000496号</span>
          </p>
        </footer>
      </template>
    </MLayout>
  </div>
</template>

<style lang="scss" scoped>
@import '@/assets/styles/console/m-layout.scss';
</style>
