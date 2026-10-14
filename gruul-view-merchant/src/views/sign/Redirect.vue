<script lang="ts" setup>
import { toPostAccountVerifyBind, unionAccountLogin } from '@/api/sign'
import { modifySignRefresh, modifySignStatus } from '@/libs'
import { LoginType, RedirectType } from '@/views/sign/type'

import { useRoute, useRouter } from 'vue-router'

const router = useRouter()
const route = useRoute()

// 扫码登录接口
async function onLogin() {
  try {
    const response = await unionAccountLogin({ code: route.query.code }, { grant_type: LoginType.WECHAT_OPEN_SCAN })
    const { data } = response
    modifySignStatus(data.value)
    modifySignRefresh(data.refreshToken.value)
    await router.replace('/console')
  } catch (err) {
    console.error(err)
    await router.replace({ path: '/sign', query: { type: 'SignInByQrCode' } })
  }
}

// 二维码绑定微信
async function onBindingWx() {
  try {
    await toPostAccountVerifyBind({ code: route.query.code })
    await router.replace('/console')
    ElMessage.success('绑定成功')
  } catch (err) {
    console.error(err)
    await router.replace({ path: '/console' })
  }
}

onMounted(() => {
  const fnMap: any = {
    [RedirectType.LOGIN]: onLogin,
    [RedirectType.BINDING_WX]: onBindingWx,
  }

  const state = route.query.state as RedirectType
  const code = route.query.code as string

  if (code && state) {
    if (fnMap[state]) {
      fnMap[state]()
    } else {
      ElMessage.error('未找到该扫码模式！')
      router.back()
    }
  }
})
</script>
