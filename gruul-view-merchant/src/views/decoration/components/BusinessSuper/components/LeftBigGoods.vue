<script setup lang="ts">
import BusinessSuper from './../BusinessSuper'

import TopBigGoods from './TopBigGoods.vue'

const props = defineProps({
  formData: {
    type: Object as () => typeof BusinessSuper,
    required: true,
  },
})

const getCategoryVo = computed(() => {
  const { category = {} as any } = props.formData.currentFirstCategory
  return category.showCategoryVos || []
})
</script>

<template>
  <div class="left__biggoods-page">
    <div v-for="item in getCategoryVo" :key="item.title" class="con__box">
      <div class="i__title">
        {{ item.name }}
      </div>
      <TopBigGoods :form-data="formData" />
    </div>
  </div>
</template>

<style lang="scss">
.left__biggoods-page {
  .i__title {
    height: 30px;
    box-sizing: border-box;
    line-height: 30px;
    border-bottom: 1px solid rgba(242, 242, 242, 0.5);
    color: #999999;
  }

  .spellpre__goodsB--icon {
    height: 130px;

    .ipic {
      height: 130px;
      border-radius: 8px;
    }
  }

  .spellpre__goodsL--icon {
    height: 90px;
    width: 90px;

    .ipic {
      height: 90px;
      width: 90px;
      border-radius: 8px;
    }
  }

  .spellpre__goodsL--info {
    overflow-x: hidden;
  }

  .spellpre__goodsL--name {
    width: 100%;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}
</style>
