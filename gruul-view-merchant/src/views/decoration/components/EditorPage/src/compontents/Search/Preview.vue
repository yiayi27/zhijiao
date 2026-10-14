<script setup lang="ts">
import { Search } from '@element-plus/icons-vue'

defineProps({
  formData: {
    type: Object,
    default: () => null,
  },
})
</script>

<template>
  <!-- 搜索 -->
  <div class="search" :class="formData.showStyle">
    <el-icon class="search__iconfont">
      <Search />
    </el-icon>
    <span>{{ formData.keyWord }}</span>
  </div>
</template>
