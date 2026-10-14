<script setup lang="ts">
import type { WebViewProps } from '@/components/LinkSelect/type'
import { LinkType } from '@/components/LinkSelect/enums'

const props = defineProps<WebViewProps>()
const emit = defineEmits(['update:link'])

const webview = ref('')

onMounted(() => {
  if (props.link?.type === LinkType.CustomLink) {
    webview.value = props.link.url.replace(/^https:\/\//, '') || ''
  }
})

watch(webview, () => {
  const webviewValue = webview.value.replace(/^https:\/\//, '')
  webview.value = webviewValue

  const currentItem = {
    id: 999,
    type: LinkType.CustomLink,
    name: '自定义链接',
    url: `https://${webviewValue}`,
    append: '',
  }

  emit('update:link', { ...props.link, ...currentItem })
})
</script>

<template>
  <!-- 自定义链接 -->
  <div>
    <el-input v-model="webview" placeholder="请输入内容" class="input-with-select">
      <template #prepend> https:// </template>
    </el-input>
    <div class="link-tips">
      注意事项 : 选择此项需要https协议的链接，其它网页需登录
      <a href="https://mp.weixin.qq.com/" target="_tartget" rel="noopener noreferrer"> 小程序管理后台 </a>配置业务域名。
    </div>
  </div>
</template>

<style scoped lang="scss">
.link-tips {
  color: #999999;
  text-align: left;
  padding: 10px;
  font-size: 12px;
  line-height: 20px;
  padding-left: 0;
  box-sizing: border-box;
  word-break: normal;
  a {
    color: #576b95;
  }
}
</style>
