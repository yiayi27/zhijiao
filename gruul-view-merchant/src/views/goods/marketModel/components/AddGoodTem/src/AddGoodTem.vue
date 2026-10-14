<script setup lang="ts">
import GoodForm from './GoodForm/index'

// 组件引用
const goodRef = ref<HTMLFormElement | null>(null)

// 标签页类型
const tagType = ref('0')
</script>

<template>
  <div>
    <el-tabs v-model="tagType">
      <el-tab-pane label="商品信息" name="0">
        <keep-alive>
          <GoodForm ref="goodRef" :tag-type="tagType" />
        </keep-alive>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
