<script setup lang="ts">
import type { AppViewProps } from '@/components/LinkSelect/type'
import { LinkType } from '@/components/LinkSelect/enums'

const props = defineProps<AppViewProps>()
const emit = defineEmits(['update:link'])

const appmodel = ref('')

onMounted(() => {
  if (props.link?.type === LinkType.MiniProgram) {
    appmodel.value = props.link.url || ''
  }
})

watch(appmodel, () => {
  const currentItem = {
    id: 999,
    type: LinkType.MiniProgram,
    name: '小程序',
    url: appmodel.value,
    append: 'appmodel',
  }

  emit('update:link', { ...props.link, ...currentItem })
})
</script>

<template>
  <!-- 小程序 -->
  <div>
    <span style="color: #9797a1">AppId</span>
    <el-input v-model="appmodel" placeholder="请输入小程序appId" class="input-with-select" style="width: 180px; margin-left: 20px" />
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
