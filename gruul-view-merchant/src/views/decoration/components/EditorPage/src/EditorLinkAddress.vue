<script setup lang="ts">
import { useDecorationStore } from '@/store/modules/decoration'
import { storeToRefs } from 'pinia'

const { copyLink } = storeToRefs(useDecorationStore())

/**
 * 吊起复制
 */
function handleCopy() {
  copy(copyLink.value)
}

/**
 * 复制内容
 */
function copy(data: string) {
  const url = data
  const oInput = document.createElement('input')
  oInput.value = url
  document.body.appendChild(oInput)
  oInput.select() // 选择对象;
  document.execCommand('Copy') // 执行浏览器复制命令
  ElMessage.success('复制成功')
  oInput.remove()
}
</script>

<template>
  <el-card class="editor__from" shadow="never">
    <template #header>
      <div class="clearfix">
        <span>分享链接</span>
      </div>
    </template>
    <transition name="el-fade-in-linear" mode="out-in" />
    <p class="content">
      <span class="label">页面路径:</span>
      <el-input :value="copyLink" disabled />
    </p>
    <p class="content">
      <span class="label" />
      <el-button @click="handleCopy"> 点击复制 </el-button>
    </p>
    <p>将获取的页面路径，添加至【微信公众平台】，或任何你想插入的地方</p>
  </el-card>
</template>

<style lang="scss" scoped>
@import '@/assets/styles/decoration/editorPage';

.content {
  font-size: 14px;
  display: flex;
  padding: 20px 0;

  .label {
    width: 200px;
  }
}
</style>
