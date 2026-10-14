<script setup lang="ts">
import { getAccountInfo } from '@/api/sign'
import { useUserStore } from '@/store/modules/user'
import ChangeInfo from '@/views/businessCenter/Component/dialog/ChangeInfo.vue'
import ChangePhone from '@/views/businessCenter/Component/dialog/ChangePhone.vue'

import { useRouter } from 'vue-router'

/* 路由与状态管理 */
const router = useRouter()
const useStore = useUserStore()

/* 用户信息 */
const userInfo = reactive<any>({})

/* 换绑微信弹窗 */
const accountVisible = ref(false)

/* 换绑手机弹窗 */
const phoneVisible = ref(false)

onMounted(() => {
  getRecentAccountInfo()
})

async function getRecentAccountInfo() {
  const response = await getAccountInfo()
  const { code, data } = response
  if (code === 200) {
    Object.assign(userInfo, data)
    useStore.setIphone(data.phone)
  }
}

function changeInfoHandle() {
  accountVisible.value = true
}

function changePhoneHandle() {
  phoneVisible.value = true
}

function routerLinkHandle(path: string) {
  router.push(path)
}
</script>

<template>
  <div class="account">
    <table class="account__manage">
      <tbody>
        <tr>
          <td class="ptd" style="width: 120px">头像</td>
          <td style="width: 280px">
            <img :src="userInfo.avatarUrl" class="avatarUrl" />
          </td>
          <td style="width: 70px">
            <el-button type="primary" plain @click="changeInfoHandle"> 换绑 </el-button>
          </td>
        </tr>
        <tr>
          <td class="ptd">昵称</td>
          <td>{{ userInfo.nikeName }}</td>
        </tr>
        <tr>
          <td class="ptd">注册账号</td>
          <td>{{ userInfo.phone }}</td>
          <td>
            <el-button type="primary" plain @click="changePhoneHandle"> 换绑 </el-button>
          </td>
        </tr>
        <tr>
          <td class="ptd">密码</td>
          <td>******</td>
          <td>
            <el-button type="primary" plain @click="routerLinkHandle('/changepass')"> 修改 </el-button>
          </td>
        </tr>
      </tbody>
    </table>

    <ChangeInfo v-model:visible="accountVisible" :user-info="userInfo" @refresh-data-list="getRecentAccountInfo" />
    <ChangePhone v-model:visible="phoneVisible" :user-info="userInfo" @refresh-data-list="getRecentAccountInfo" />
  </div>
</template>

<style lang="scss">
.account {
  width: 100%;
  height: 100%;
  color: #515a6e;
  font-size: 13px;
  background-color: #ffffff;

  .account__manage {
    padding-top: 50px;
  }

  .avatarUrl {
    width: 60px;
    border-radius: 100%;
  }
}

.ptd {
  padding: 20px 10px;
}
</style>
