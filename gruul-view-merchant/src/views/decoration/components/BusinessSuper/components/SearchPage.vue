<script setup lang="ts">
import BusinessSuper from './../BusinessSuper'
import { Search } from '@element-plus/icons-vue'

defineProps({
  formData: {
    type: Object as () => typeof BusinessSuper,
    required: true,
  },
})
</script>

<template>
  <div class="sear__page-page">
    <div class="search__box">
      <div class="search-icon">
        <el-icon><Search /></el-icon>
      </div>
    </div>
  </div>
</template>

<style lang="scss">
.sear__page-page {
  .search__box {
    height: 60px;
    box-sizing: border-box;
    padding: 12px 20px;

    .search-icon {
      height: 35px;
      background-color: rgba(242, 242, 242, 1);
      border-radius: 6px;

      i {
        float: right;
        margin-top: 10px;
        margin-right: 10px;
      }
    }
  }
}
</style>
