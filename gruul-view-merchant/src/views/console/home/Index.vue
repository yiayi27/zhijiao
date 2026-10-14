<script setup lang="ts">
import { getShopList } from '@/api/console'
import { toGetAccountVerifyEnabled, unionAccountLogin } from '@/api/sign'
import BaseTable from '@/components/BaseTable/index.vue'
import MContainer from '@/components/MContainer/MContainer.vue'
import PageManage from '@/components/PageManage.vue'
import ScanQrCode from '@/components/ScanQrCode/ScanQrCode.vue'
import { modifySignRefresh, modifySignStatus, setGlobalPermission, setGlobalShopId } from '@/libs/index'
import { useUserStore } from '@/store/modules/user'
import { LoginType, RedirectType } from '@/views/sign/type'
import { Picture } from '@element-plus/icons-vue'

import { useRouter } from 'vue-router'

defineOptions({
  name: 'ConsoleHome',
})

// 状态变量
const shopList = ref<Obj[]>([])
const dialogVisible = ref(false)
const orderBy = ref(2)
const pageSize = ref(10)
const pageNum = ref(1)
const total = ref(0)
const loading = ref(false)

const router = useRouter()

const userStore = useUserStore()
const userInfo = computed(() => {
  return {
    name: userStore.userInfo?.nikeName || '',
    avatar: userStore.userInfo?.avatarUrl || '',
  }
})

// 方法
async function getDataList() {
  const form = {
    page: pageNum.value,
    size: pageSize.value,
    orderBy: orderBy.value,
  }
  loading.value = true
  const response = await getShopList(form).finally(() => {
    loading.value = false
  })
  const { data } = response
  shopList.value = data.list || []
  total.value = data.total
}

// async function deleteShopById(shopId) {
//   try {
//     const response = await deleteShop(shopId)
//     const { code } = response.data
//     if (code === 200) {
//       ElMessage({
//         type: 'success',
//         message: '删除成功!',
//       })
//       await getDataList()
//     }
//   }
//   catch (e) {
//     ElMessage({
//       message: e as string,
//       type: 'warning',
//     })
//   }
// }
function routerLinkHandle() {
  router.push(`/console/create`)
}

async function itemClick(row: Obj) {
  try {
    loading.value = true
    const response = await unionAccountLogin({ shopId: row.id }, { grant_type: LoginType.SWITCH_SHOP })
    const { data } = response
    modifySignStatus(data.value)
    modifySignRefresh(data.refreshToken.value)
    setGlobalShopId(row.id)
    setGlobalPermission(data.open)
    loading.value = false
    await userStore.getUserInfo(false)
    router.replace('/overview/index')
  } finally {
    loading.value = false
  }
}

function handleSizeChange(val: number) {
  pageSize.value = val
  getDataList()
}

function handleCurrentChange(val: number) {
  pageNum.value = val
  getDataList()
}

const scanEnabled = ref(false)
async function getScanVerifyEnabled() {
  const res = await toGetAccountVerifyEnabled()
  scanEnabled.value = res.data
}

// 生命周期钩子
onMounted(() => {
  getScanVerifyEnabled()
  getDataList()
})
</script>

<template>
  <div v-loading="loading" class="shop">
    <div class="shop__header">
      <div class="shop__header--user">
        <span>欢迎 {{ userInfo.name }}</span>
      </div>
      <div class="shop__header--banner">
        <el-image
          class="banner"
          src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20210517/e5d9b381b6764afba4cc2e5b06012673.jpg"
          fit="cover"
        />
      </div>
    </div>
    <div v-if="shopList.length > 0" class="shop__list">
      <MContainer v-model:size="pageSize" v-model:current="pageNum" v-model:total="total">
        <template #form>
          <div class="shop__list--form flex justify-between">
            <span>我的店铺</span>

            <el-space>
              <el-button v-if="scanEnabled" @click="dialogVisible = true"> 绑定微信 </el-button>
              <el-button type="primary" class="shop__list--create" @click="routerLinkHandle"> 创建店铺 </el-button>
            </el-space>
          </div>
        </template>
        <template #content>
          <BaseTable :data="shopList" class="shop__list--content" height="calc(100vh - 575px)" style="width: 100%">
            <el-table-column prop="info" align="center" label="基本信息">
              <template #default="scope">
                <div class="info">
                  <el-image class="info__img" :src="scope.row.logoUrl" fit="cover">
                    <template #error>
                      <div class="image-slot">
                        <el-icon><Picture /></el-icon>
                      </div>
                    </template>
                  </el-image>
                  <div class="info__msg">
                    <div class="info__msg--name">
                      <el-button link>
                        {{ scope.row.shopName }}
                      </el-button>
                    </div>
                    <div class="info__msg--phone">
                      {{ scope.row.templateName }}
                    </div>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" align="center" label="创建时间" />
            <el-table-column align="center" label="操作">
              <template #default="scope">
                <el-button type="primary" @click="itemClick(scope.row)"> 进入后台 </el-button>
              </template>
            </el-table-column>
          </BaseTable>
        </template>
      </MContainer>
      <PageManage
        :page-size="pageSize"
        :page-num="pageNum"
        :total="total"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
      />
    </div>
    <div v-if="shopList.length === 0" class="shop__list">
      <div class="myShop">我的店铺</div>
      <div class="shop__list--empty">
        <div class="mt15">你还没有运行中的店铺，现在就去<span class="primary cursor-pointer" @click="routerLinkHandle">新建</span>一个吧！</div>
      </div>
    </div>
    <el-dialog v-model="dialogVisible" width="30%">
      <ScanQrCode redirect-url="/manage#/redirect" :scenes="RedirectType.BINDING_WX" />
    </el-dialog>
  </div>
</template>

<style lang="scss">
@import '@/assets/styles/console/shops.scss';
</style>
