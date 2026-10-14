<script setup lang="ts">
import Information from './Information'

const props = defineProps<{
  formData: typeof Information
}>()
</script>

<template>
  <!-- 资讯 -->
  <div class="information-preview">
    <div class="information-preview__top">
      <div>资讯标题</div>
      <div>更多密探></div>
    </div>

    <div class="information-preview__list" :class="props.formData.showStyle">
      <div v-for="(item, index) in props.formData.informationList" :key="index" class="information-preview__item">
        <div class="information-preview__img" />
        <div class="information-preview__foot">
          <div class="information-preview__title">
            {{ item.title }}
          </div>
          <div class="information-preview__record">
            <div>2.5</div>
            <div>2</div>
            <div>评价0</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
